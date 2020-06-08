/*******************************************************************************
 * Copyright (c) 2010, Medelexis AG, Baden, Switzerland
 * All rights reserved.
 *******************************************************************************/
package ch.elexis.openoffice;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaTray;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.awt.Size;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.drawing.XShape;
import com.sun.star.text.HoriOrientation;
import com.sun.star.text.RelOrientation;
import com.sun.star.text.TextContentAnchorType;
import com.sun.star.text.VertOrientation;
import com.sun.star.text.XText;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextFrame;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.PrintableState;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.desktop.DesktopException;
import ag.ion.bion.officelayer.desktop.GlobalCommands;
import ag.ion.bion.officelayer.desktop.IDesktopService;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.ITextTableCell;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.bion.officelayer.text.IViewCursorService;
import ag.ion.bion.officelayer.text.table.ITextTablePropertyStore;
import ag.ion.noa.NOAException;
import ag.ion.noa.filter.OpenOfficeFilter;
import ag.ion.noa.frame.IDispatchDelegate;
import ag.ion.noa.frame.ILayoutManager;
import ag.ion.noa.printing.IPrinter;
import ag.ion.noa.search.ISearchResult;
import ag.ion.noa.search.SearchDescriptor;
import ag.ion.noa4e.ui.NOAUIPlugin;
import ch.elexis.core.data.interfaces.text.ReplaceCallback;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.MimeTypeUtil;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.textsystem.TextTemplatePrintSettings;
import ch.elexis.openoffice.noa.UnoUtil;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;

public class OOTextPlugin implements ITextPlugin {
	private static Logger log = LoggerFactory.getLogger(OOTextPlugin.class);
	
	private final static String TMP_PATH = System.getProperty("user.home") //$NON-NLS-1$
		+ File.separator + "elexis" + File.separator + "ootemp" //$NON-NLS-1$ //$NON-NLS-2$
		+ File.separator;
	private static final String TMP_FILENAME = "elexisdoc";
	
	// Inital Variables
	protected ITextDocument document = null;
	protected ICallback callbackHandler = null;
	
	// Frames
	private Composite officeParent = null;
	protected IFrame officeFrame = null;
	protected Frame officeAWTFrame = null;
	protected Panel officePanel = null;
	protected IOfficeApplication officeApplication = null;
	
	// Variables
	protected PageFormat format = ITextPlugin.PageFormat.USER;
	protected boolean saveOnFocusLost = false;
	private String fontName = null;
	private float fontSize = 0;
	private int fontStyle = -1;
	private boolean showMenubar = true;
	private boolean showToolbar = true;
	
	private IDesktopService service;
	
	private volatile boolean initDone = false;
	
	private List<String> hidenToolbarUrls = new ArrayList<String>();
	
	private TextTemplatePrintSettings printSettings;
	
	/**
	 * Thread opens a new document or loads an existing document from an url. <br>
	 * Loading/Creating must be in a thread. If not, floating toolbar wont work.
	 */
	private class OpenDocumentThread implements IRunnableWithProgress {
		private boolean loaded = false;
		private final String url;
		
		public OpenDocumentThread(final String url){
			this.url = url;
		}
		
		public boolean isLoaded(){
			return this.loaded;
		}
		
		public void run(final IProgressMonitor monitor){
			while (!initDone) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					log.error("Interrupted in waiting for initDone", e);
				}
			}
			
			log.debug("OpenDocumentThread.run(monitor)");
			try {
				// Creating new/ loading document
				if (this.url == null) { // Empty
					monitor.setTaskName("Neues Dokument wird erstellt..");
					document =
						(ITextDocument) officeApplication.getDocumentService()
							.constructNewDocument(officeFrame, IDocument.WRITER,
								DocumentDescriptor.DEFAULT);
				} else {
					monitor.setTaskName("Dokument wird geladen..");
					document =
						(ITextDocument) officeApplication.getDocumentService().loadDocument(
							officeFrame, url, DocumentDescriptor.DEFAULT);
				}
				
				loaded = true;
			} catch (Exception e) {
				log.error(e.getLocalizedMessage(), e);
				loaded = false;
			} finally {
				log.debug("Exit OpenDocumentThread.run(monitor). Loaded = " + loaded);
			}
		}
	}
	
	private void dump(){
		if (this.officeApplication != null) {
			try {
				this.officeApplication.getApplicationInfo().dumpInfo();
			} catch (Exception e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}
	}
	
	/**
	 * Error handler for OpenOffice Plugin
	 * 
	 * @param t
	 */
	private void handleError(String message){
		log.error(message);
	}
	
	/**
	 * Error handler for OpenOffice Plugin
	 * 
	 * @param t
	 */
	private void handleError(Throwable t, boolean dump){
		log.error(t.getLocalizedMessage(), t);
		ExHandler.handle(t);
		if (dump) {
			dump();
		}
	}
	
	/**
	 * Sets visibility of menubar
	 * 
	 * @param frame
	 */
	private void showMenuBarChanged() throws NOAException{
		if (this.officeFrame != null) {
			ILayoutManager layoutManager = this.officeFrame.getLayoutManager();
			if (this.showMenubar) {
				layoutManager.showElement(ILayoutManager.URL_MENUBAR);
			} else {
				layoutManager.hideElement(ILayoutManager.URL_MENUBAR);
			}
		}
	}
	
	/**
	 * Sets visibility of toolbar
	 * 
	 * @param frame
	 */
	private void showToolBarChanged() throws NOAException{
		if (this.officeFrame != null) {
			ILayoutManager layoutManager = this.officeFrame.getLayoutManager();
			if (this.showToolbar) {
				for (String url : hidenToolbarUrls) {
					layoutManager.showElement(url);
				}
			} else {
				for (String url : ILayoutManager.ALL_BARS_URLS) {
					if (layoutManager.getXLayoutManager().isElementVisible(url)) {
						if (url.startsWith(ILayoutManager.URL_PREFIX_TOOLBAR)) {
							layoutManager.hideElement(url);
							hidenToolbarUrls.add(url);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Configures the office frame.
	 * 
	 * @param frame
	 *            office frame to be configured
	 * @param handler
	 *            Handler for saving the document
	 */
	private void configureOfficeFrame(IFrame frame, final ICallback handler){
		frame.addDispatchDelegate(GlobalCommands.SAVE_AS, new IDispatchDelegate() {
			public void dispatch(Object[] arg0){
				officeParent.getDisplay().asyncExec(new Runnable() {
					public void run(){
						handler.saveAs();
					}
				});
			}
		});

		frame.addDispatchDelegate(GlobalCommands.SAVE, new IDispatchDelegate() {
			public void dispatch(Object[] arg0){
				officeParent.getDisplay().asyncExec(new Runnable() {
					public void run(){
						handler.save();
					}
				});
			}
		});
		

		frame.disableDispatch(GlobalCommands.CLOSE_DOCUMENT);
		frame.disableDispatch(GlobalCommands.CLOSE_WINDOW);
		frame.disableDispatch(GlobalCommands.QUIT_APPLICATION);
		
		// Show/Hide Menu and Toolbar
		try {
			showMenuBarChanged();
			showToolBarChanged();
		} catch (NOAException e) {
			handleError(e, false);
		}
		
		frame.updateDispatches();
		
		// Drop support
		new DropTarget(officePanel, new DropTargetAdapter() {
			public void drop(final DropTargetDropEvent e){
				String dropText = null;
				if (e.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					Transferable tr = e.getTransferable();
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					try {
						dropText = (String) tr.getTransferData(DataFlavor.stringFlavor);
					} catch (UnsupportedFlavorException ufe) {
						log.error(ufe.getLocalizedMessage(), ufe);
					} catch (IOException ioe) {
						log.error(ioe.getLocalizedMessage(), ioe);
					}
				}
				if (document != null && dropText != null) {
					IViewCursorService viewCursorService = document.getViewCursorService();
					IViewCursor viewCursor = viewCursorService.getViewCursor();
					ITextRange textRange = viewCursor.getStartTextRange();
					textRange.setText(dropText);
				}
			}
		});
		
		log.debug("exit configureOfficeFrame");
	}
	
	/**
	 * Create and return an SWT Composite that holds the editor
	 * 
	 * @param parent
	 *            parent component
	 * @param handler
	 *            Handler for saving the document
	 */
	public synchronized Composite createContainer(Composite parent, ICallback handler){
		log.debug(MessageFormat.format("createContainer({0}, {1})", parent, handler));
		
		callbackHandler = handler;
		officeParent = parent;
		
		// Content in the clipboard can cause a deadlock during
		// opening an OpenOffice.org document.
		Clipboard clipboard = new Clipboard(Display.getCurrent());
		if (clipboard.getAvailableTypeNames().length != 0) {
			clipboard.setContents(new Object[] {
				" "}, new Transfer[] { TextTransfer.getInstance()}); //$NON-NLS-1$
			clipboard.clearContents();
		}
		clipboard.dispose();
		
		Composite composite = new Composite(parent, SWT.EMBEDDED);
		composite.setVisible(true);
		
		log.debug("new SWT_AWT frame");
		officeAWTFrame = SWT_AWT.new_Frame(composite);
		officeAWTFrame.setVisible(true);
		officePanel = new Panel(new BorderLayout());
		officeAWTFrame.add(officePanel);
		officeAWTFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e){
				dispose();
			}
		});
		
		log.debug("get local office application");
		try {
			officeApplication =
				OOActivator.getDefault().getLocalOfficeApplication(parent.getShell());
		} catch (Exception ex) {
			handleError(ex, true);
			dispose();
		} catch (Error err) {
			handleError(err, true);
			dispose();
			throw err;
		} finally {
			log.debug("exit createContainer");
		}
		
		return composite;
	}
	
	/**
	 * Activates local office application
	 */
	public void activateLocalOfficeApplication() throws CoreException, OfficeApplicationException{
		System.out.println(OOTextPlugin.class.getName() + "#activateLocalOfficeApplication()");
		log.debug("activateLocalOfficeApplication()");
		
		if (this.officeApplication == null) {
			throw new CoreException(new Status(IStatus.ERROR, "openoffice.prototyp", IStatus.ERROR,
				"Local OpenOffice.org application is not available.", null));
		}
		
		synchronized (this.officeApplication) {
			if (!this.officeApplication.isActive()) {
				log.debug("startLocalOfficeApplication..");
				IStatus status =
					NOAUIPlugin.startLocalOfficeApplication(officeParent.getShell(),
						this.officeApplication);
				if (status.getSeverity() == IStatus.ERROR) {
					throw new CoreException(new Status(IStatus.ERROR, "openoffice.prototyp",
						IStatus.ERROR, "Local OpenOffice.org application cannot be activated.",
						null));
				}
			}
			
			try {
				if (!this.officeApplication.isActive() && this.officeApplication.isConfigured()) {
					log.debug("activate officeApplication..");
					this.officeApplication.activate();
				}
				
				
				if (officeFrame == null && this.officeApplication.isActive()) {
					log.debug("get desktop service..");
					service = this.officeApplication.getDesktopService();
					log.debug("Service=" + service);
					String libPathFromProps =
						System.getProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH);
					log.debug("Load library from: " + libPathFromProps);
					
					Display.getCurrent().asyncExec(new Runnable() {
						
						@Override
						public void run(){
							try {
								officeFrame = service.constructNewOfficeFrame(officePanel);
								configureOfficeFrame(officeFrame, callbackHandler);
								officePanel.doLayout();
							} catch (DesktopException e) {
								handleError(e, true);
								dispose();
							} finally {
								initDone = true;
							}
						}
					});
				}
			} catch (OfficeApplicationException officeEx) {
				handleError(officeEx, true);
				dispose();
				throw officeEx;
			} catch (Exception ex) {
				handleError(ex, true);
				dispose();
			} catch (Error err) {
				handleError(err, true);
				dispose();
				throw err;
			}
			
		}
	}
	
	/**
	 * Creates an empty document
	 */
	public synchronized boolean createEmptyDocument(){
		log.debug("createEmptyDocument()");
		try {
			activateLocalOfficeApplication();
			OpenDocumentThread openNewDocumentThread = new OpenDocumentThread(null);
			
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(UiDesk.getTopShell());
			dialog.run(true, false, openNewDocumentThread);
			return openNewDocumentThread.isLoaded();
		} catch (Exception ex) {
			handleError(ex, false);
			closeDocument();
		}
		
		return false;
	}
	
	/**
	 * Close document if available
	 */
	private synchronized void closeDocument(){
		if (this.document != null) {
			this.document.close();
		}
		this.document = null;
	}
	
	/**
	 * Dispose plugin and releases the open office bridge
	 */
	public synchronized void dispose(){
		
		// Document and callbackhandler
		closeDocument();
		this.callbackHandler = null;
		
		// Widgets
		if (officeAWTFrame != null) {
			this.officeAWTFrame.dispose();
			this.officeAWTFrame = null;
		}
		this.officeFrame = null;
	}
	
	/**
	 * Saves current document
	 */
	public synchronized boolean clear(){
		if (callbackHandler != null) {
			try {
				callbackHandler.save();
				document.setModified(false);
				return true;
			} catch (DocumentException ex) {
				handleError(ex, false);
			}
		}
		return false;
	}
	
	/**
	 * Find a pattern (regular expression) in the document, and call ReplaceCallback with each
	 * match. Replace the found pattern with the replacment String received from ReplaceCallback.
	 * 
	 * @param pattern
	 *            a regular expression
	 * @param cb
	 *            a ReplaceCallback or null if no Replacement should be performed
	 * @return true if pattern was found at least once
	 */
	public synchronized boolean findOrReplace(String pattern, ReplaceCallback cb){
		SearchDescriptor search = new SearchDescriptor(pattern);
		search.setUseRegularExpression(true);
		if (this.document == null) {
			SWTHelper.showError("No doc in bill", "Fehler:",
				"Es ist keine Rechnungsvorlage definiert");
			return false;
		}
		ISearchResult searchResult = this.document.getSearchService().findAll(search);
		if (!searchResult.isEmpty()) {
			ITextRange[] textRanges = searchResult.getTextRanges();
			if (cb != null) {
				for (ITextRange r : textRanges) {
					String orig = r.getXTextRange().getString();
					Object replace = cb.replace(orig);
					if (replace == null) {
						r.setText("??Auswahl??");
					} else if (replace instanceof String) {
						// String
						// repl=((String)replace).replaceAll("\\r\\n[\\r\\n]*",
						// "\n")
						String repl = ((String) replace).replaceAll("\\r", "\n");
						repl = repl.replaceAll("\\n\\n+", "\n");
						r.setText(repl);
					} else if (replace instanceof String[][]) {
						String[][] contents = (String[][]) replace;
						try {
							ITextTable textTable =
								this.document.getTextTableService().constructTextTable(
									contents.length, contents[0].length);
							this.document.getTextService().getTextContentService()
								.insertTextContent(r, textTable);
							r.setText("");
							for (int row = 0; row < contents.length; row++) {
								String[] zeile = contents[row];
								for (int col = 0; col < zeile.length; col++) {
									textTable.getCell(col, row).getTextService().getText()
										.setText(zeile[col]);
								}
							}
							textTable.spreadColumnsEvenly();
						} catch (Exception ex) {
							handleError(ex, false);
							r.setText("Fehler beim Ersetzen");
						}
					} else {
						r.setText("Not a String");
					}
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Insert a table.
	 * 
	 * @param place
	 *            A string to search for and replace with the table
	 * @param properties
	 *            Properties for the table
	 * @param contents
	 *            An Array of String[]s describing each line of the table
	 * @param columnsizes
	 *            Int-array describing the relative width of each column (all columns together are
	 *            taken as 100%). May be null, in that case the columns will be spread evenly
	 */
	public synchronized boolean insertTable(String place, int properties, String[][] contents,
		int[] columnSizes){
		
		int header = 0;
		if ((properties & ITextPlugin.FIRST_ROW_IS_HEADER) != 0) {
			header = 1;
		}
		
		SearchDescriptor search = new SearchDescriptor(place);
		search.setIsCaseSensitive(true);
		ISearchResult searchResult = this.document.getSearchService().findFirst(search);
		if (!searchResult.isEmpty()) {
			ITextRange r = searchResult.getTextRanges()[0];
			
			try {
				ITextTable textTable =
					this.document.getTextTableService().constructTextTable(contents.length /*
																							 * +
																							 * offset
																							 */,
						contents[0].length);
				this.document.getTextService().getTextContentService()
					.insertTextContent(r, textTable);
				r.setText("");
				ITextTablePropertyStore props = textTable.getPropertyStore();
				long w = props.getWidth();
				long percent = w / 100;
				for (int row = 0; row < contents.length; row++) {
					String[] zeile = contents[row];
					for (int col = 0; col < zeile.length; col++) {
						textTable.getCell(col, row /* + offset */).getTextService().getText()
							.setText(zeile[col]);
					}
				}
				if (columnSizes == null) {
					textTable.spreadColumnsEvenly();
				} else {
					for (int col = 0; col < contents[0].length; col++) {
						textTable.getColumn(col).setWidth((short) (columnSizes[col] * percent));
					}
					
				}
				for (int i = 0; i < contents[0].length; i++) {
					textTable.getCell(i, 0).setCellParagraphStyle(
						ITextTableCell.STYLE_TABLE_CONTENT);
				}
				return true;
			} catch (Exception ex) {
				handleError(ex, false);
			}
		}
		return false;
	}
	
	/**
	 * Insert Text and return a cursor describing the position. <br>
	 * We can not avoid using UNO here, because NOA does not give us <br>
	 * enough control over the text cursor
	 */
	public synchronized Object insertText(String marke, String text, int adjust){
		SearchDescriptor search = new SearchDescriptor(marke);
		search.setIsCaseSensitive(true);
		ISearchResult searchResult = this.document.getSearchService().findFirst(search);
		XText myText = this.document.getXTextDocument().getText();
		XTextCursor cur = myText.createTextCursor();
		if (!searchResult.isEmpty()) {
			ITextRange r = searchResult.getTextRanges()[0];
			cur = myText.createTextCursorByRange(r.getXTextRange());
			cur.setString(text);
			try {
				UnoUtil.setFormat(cur, this.fontName, this.fontSize, this.fontStyle);
			} catch (Exception ex) {
				handleError(ex, false);
			}
			
			cur.collapseToEnd();
		}
		return cur;
	}
	
	/**
	 * Insert text at a position returned by insertText(String,text,adjust)
	 */
	public synchronized Object insertText(Object pos, String text, int adjust){
		XTextCursor cur = (XTextCursor) pos;
		if (cur != null) {
			cur.setString(text);
			try {
				UnoUtil.setFormat(cur, this.fontName, this.fontSize, this.fontStyle);
			} catch (Exception e) {
				handleError(e, false);
			}
			cur.collapseToEnd();
		} else {
			if (this.document != null) {
				IViewCursorService viewCursorService = this.document.getViewCursorService();
				IViewCursor viewCursor = viewCursorService.getViewCursor();
				ITextRange textRange = viewCursor.getStartTextRange();
				textRange.setText(text);
				cur = this.document.getXTextDocument().getText().createTextCursor();
			}
		}
		return cur;
	}
	
	/**
	 * Insert Text inside a rectangular area. <br>
	 * We need UNO to get access to a Text frame.
	 */
	public synchronized Object insertTextAt(int x, int y, int w, int h, String text, int adjust){
		if (this.document == null) {
			log.warn("Kein Dokument vorhanden!");
			return null;
		}
		try {
			XTextDocument myDoc = this.document.getXTextDocument();
			com.sun.star.lang.XMultiServiceFactory documentFactory =
				(com.sun.star.lang.XMultiServiceFactory) UnoRuntime.queryInterface(
					com.sun.star.lang.XMultiServiceFactory.class, myDoc);
			
			Object frame = documentFactory.createInstance("com.sun.star.text.TextFrame");
			
			XText docText = myDoc.getText();
			XTextFrame xFrame = (XTextFrame) UnoRuntime.queryInterface(XTextFrame.class, frame);
			
			XShape xWriterShape = (XShape) UnoRuntime.queryInterface(XShape.class, xFrame);
			
			xWriterShape.setSize(new Size(w * 100, h * 100));
			
			XPropertySet xFrameProps =
				(XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xFrame);
			
			// Setting the vertical position
			xFrameProps.setPropertyValue("AnchorPageNo", new Short((short) 1));
			xFrameProps.setPropertyValue("VertOrientRelation", RelOrientation.PAGE_FRAME);
			xFrameProps.setPropertyValue("AnchorType", TextContentAnchorType.AT_PAGE);
			xFrameProps.setPropertyValue("HoriOrient", HoriOrientation.NONE);
			xFrameProps.setPropertyValue("VertOrient", VertOrientation.NONE);
			xFrameProps.setPropertyValue("HoriOrientPosition", x * 100);
			xFrameProps.setPropertyValue("VertOrientPosition", y * 100);
			
			XTextCursor docCursor = docText.createTextCursor();
			docCursor.gotoStart(false);
			// docText.insertControlCharacter(docCursor,ControlCharacter.PARAGRAPH_BREAK,false);
			docText.insertTextContent(docCursor, xFrame, false);
			
			// get the XText from the shape
			
			// XText xShapeText = ( XText ) UnoRuntime.queryInterface(
			// XText.class, writerShape );
			
			XText xFrameText = xFrame.getText();
			XTextCursor xtc = xFrameText.createTextCursor();
			UnoUtil.setFormat(xtc, this.fontName, this.fontSize, this.fontStyle, adjust);
			
			xFrameText.insertString(xtc, text, false);
			
			return xtc;
		} catch (Exception ex) {
			handleError(ex, false);
		}
		return null;
	}
	
	/**
	 * Delete all files in the ootemp directory
	 */
	private void deleteTempDirectory(){
		File tempDirectory = new File(TMP_PATH);
		File[] files = tempDirectory.listFiles();
		if (files != null) {
			for (File file : files) {
				file.delete();
			}
		}
	}
	
	/**
	 * Get next filename dummy0.odt, dummy1.odt, .., dummyx.odt
	 * 
	 * @return
	 */
	private String getNextFilename(){
		File tempDirectory = new File(TMP_PATH);
		String newFilename = TMP_FILENAME;
		int counter = 0;
		String[] fileNames = tempDirectory.list();
		if (fileNames != null) {
			List<String> existingFilenameList = Arrays.asList(fileNames);
			while (existingFilenameList.contains(newFilename + ".odt")) {
				newFilename = TMP_FILENAME + counter;
				counter++;
			}
		}
		return newFilename + ".odt";
	}
	
	/**
	 * Load a file from a byte array.
	 */
	public synchronized boolean loadFromByteArray(byte[] bs, boolean asTemplate){
		if (bs == null) {
			log.error("Null-Array zum speichern!");
			return false;
		}
		
		deleteTempDirectory();
		String url = TMP_PATH + getNextFilename();
		FileTool.checkCreatePath(TMP_PATH);
		try {
			activateLocalOfficeApplication();
			saveDocument(null);
			
			FileTool.writeFile(new File(url), bs);
			OpenDocumentThread openDocument = new OpenDocumentThread(url);
			
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(UiDesk.getTopShell());
			dialog.run(true, false, openDocument);
			return openDocument.isLoaded();
		} catch (Exception ex) {
			handleError(ex, false);
			closeDocument();
			return false;
		} finally {
			FileTool.deleteFile(url);
		}
	}
	
	/**
	 * Load a file from an input stream.
	 */
	public synchronized boolean loadFromStream(InputStream is, boolean asTemplate){
		try {
			byte[] daten = null;
			try {
				daten = new byte[is.available()];
				is.read(daten);
			} finally {
				if (is != null) {
					is.close();
				}
			}
			
			return loadFromByteArray(daten, asTemplate);
		} catch (Exception ex) {
			handleError(ex, false);
			closeDocument();
		}
		return false;
	}
	
	/**
	 * Stores current document. If asFilenamePath is null, the document is stored in the appropiate
	 * file (if exists).
	 */
	private void saveDocument(final String filenamePath){
		if (this.document == null) {
			// Nothing to save
			return;
		}
		try {
			if (filenamePath == null) {
				if (this.document.getPersistenceService().getLocation() != null) {
					if (this.document.isModified()) {
						this.document.getPersistenceService().store();
					}
				}
			} else {
				this.document.getPersistenceService().store(filenamePath);
			}
		} catch (DocumentException e) {
			handleError(e, false);
		}
	}
	
	/**
	 * Store the document and returns content as byte array. <br>
	 * First the document is saved to a temporary file. <br>
	 * Then the content is returned as byte array
	 */
	public synchronized byte[] storeToByteArray(){
		if (this.document == null) {
			return null;
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] byteArray = null;
		try {
			this.document.getPersistenceService().export(outputStream, new OpenOfficeFilter());
			byteArray = outputStream.toByteArray();
		} catch (NOAException e) {
			handleError(e, false);
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}
		
		return byteArray;
	}
	
	/**
	 * Print the contents of the panel. <br>
	 * NOA does no allow us to select printer and tray, so we do it directly with UNO.
	 */
	public synchronized boolean print(String toPrinter, String toTray, boolean waitUntilFinished){
		if (this.document == null) {
			log.warn("Kein Dokument zum Drucken!");
			return false;
		}
		
		// Querying for the interface XPrintable on the loaded document
		com.sun.star.view.XPrintable xPrintable =
			(com.sun.star.view.XPrintable) UnoRuntime.queryInterface(
				com.sun.star.view.XPrintable.class, this.document.getXComponent());
		// check print settings for this template
		toPrinter = printSettings.getPrinter() == null ? toPrinter : printSettings.getPrinter();
		toTray = printSettings.getTray() == null ? toTray : printSettings.getTray();
		
		if (!StringTool.isNothing(toTray)) {
			XTextDocument myDoc = this.document.getXTextDocument();
			try {
				UnoUtil.setPrinterTray(myDoc, toTray);
			} catch (Exception e) {
				String printer =
					manuallyHookIntoPrintJob(printSettings.getPrinter(), printSettings.getTray());
				if (printer == null) {
					handleError(MessageFormat.format("Fehler beim Auswählen des Druckerfaches {0}",
						toTray));
					return false;
				}
				toPrinter = printer;
			}
		}
		
		try {
			// Set printer and pages parameters
			PropertyValue[] pprops;
			if (StringTool.isNothing(toPrinter)) {
				pprops = new PropertyValue[1];
				pprops[0] = new PropertyValue();
				pprops[0].Name = "Pages";
				pprops[0].Value = "1-";
				xPrintable.print(pprops);
			} else {
				//Setting the property "Name" for the favored printer (name of IP address)
				pprops = new PropertyValue[2];
				pprops[0] = new PropertyValue();
				pprops[0].Name = "Name";
				pprops[0].Value = toPrinter;
				xPrintable.setPrinter(pprops);
				
				// Setting the property "Pages" to print all pages
				pprops[1] = new PropertyValue();
				pprops[1].Name = "Pages";
				pprops[1].Value = "1-";
				xPrintable.print(pprops);
			}
		} catch (Exception e) {
			log.error("Setting printing properties for OpenOffice document failed", e);
		}
		
		com.sun.star.view.XPrintJobBroadcaster selection =
			(com.sun.star.view.XPrintJobBroadcaster) UnoRuntime.queryInterface(
				com.sun.star.view.XPrintJobBroadcaster.class, xPrintable);
		
		OOPrintJobListener myXPrintJobListener = new OOPrintJobListener();
		selection.addPrintJobListener(myXPrintJobListener);
		
		// 0.1 Sekunde warten.
		// Ohne diesem Delay kann es passieren, dass das Dokument mit falschen
		// Einstellungen gedruckt wird.
		// Die bessere Lösung wäre natürlich, wenn uns XPrintable sagen würde,
		// wann es gedruckt werden kann.
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		
		try {
			long timeout = System.currentTimeMillis();
			// Warten bis fertig oder 10 Sekunden
			while ((myXPrintJobListener.getStatus() == null)
				|| (myXPrintJobListener.getStatus() == PrintableState.JOB_STARTED)) {
				Thread.sleep(100);
				long to = System.currentTimeMillis();
				if ((to - timeout) > 10000) {
					break;
				}
			}
			
			return true;
		} catch (Exception ex) {
			handleError(ex, false);
		}
		
		return false;
	}
	
	private String manuallyHookIntoPrintJob(String printer, String tray){
		List<PrintService> printers =
			Arrays.asList(PrintServiceLookup.lookupPrintServices(null, null));
		PrintService printSrv = null;
		for (PrintService ps : printers) {
			// is it the PrintService we're looking for
			if (ps.getName().equals(printer)) {
				printSrv = ps;
				
				// get printable attributes
				Object attributes = ps.getSupportedAttributeValues(Media.class,
					DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);
				if (attributes != null && attributes.getClass().isArray()) {
					MediaTray mediaTray = findMediaTray(tray, (Media[]) attributes);
					
					// pass PrintService and MediaTray (if available) to print job
					if (printSrv != null) {
						try {
							PrinterJob pjob = PrinterJob.getPrinterJob();
							// specify MediaTray if set
							if (mediaTray != null) {
								PrintRequestAttributeSet pReqAS =
									new HashPrintRequestAttributeSet();
								pReqAS.add(mediaTray);
								pjob.print(pReqAS);
							}
							pjob.setPrintService(printSrv);
							return printSrv.getName();
						} catch (PrinterException e) {
							log.warn("Could neither modify print job", e);
							return null;
						}
					}
				}
			}
		}
		return null;
	}
	
	private MediaTray findMediaTray(String searchedTray, Media[] mediaAttributes){
		for (Media media : mediaAttributes) {
			if (media instanceof MediaTray) {
				// matches our searchedTray
				if (media.toString().equals(searchedTray)) {
					return (MediaTray) media;
				}
			}
		}
		return null;
	}
	
	/**
	 * The text component receives the focus
	 */
	public synchronized void setFocus(){
		if (this.officeParent != null) {
			this.officeParent.setFocus();
		}
	}
	
	/**
	 * Set font for all following operations (until the next call to setFont)
	 * 
	 * @param name
	 *            name of the font
	 * @param style
	 *            SWT.MIN, SWT.NORMAL, SWT.BOLD (thin, normal or bold)
	 * @param size
	 *            font height in Pt
	 * @return false on error. True on success, what might mean however, that not the specified font
	 *         but a similar font was set.
	 */
	public boolean setFont(String name, int style, float size){
		this.fontName = name;
		this.fontSize = size;
		boolean styleOk = setStyle(style);
		return styleOk;
	}
	
	/**
	 * Set format of the page
	 */
	public void setFormat(PageFormat f){
		this.format = f;
	}
	
	/**
	 * Get format of the page
	 */
	public PageFormat getFormat(){
		return this.format;
	}
	
	/**
	 * Default Mimettype of the documents that this implementation creates
	 */
	public String getMimeType(){
		return MimeTypeUtil.MIME_TYPE_OPENOFFICE;
	}
	
	/**
	 * Save contents on focus lost
	 * 
	 * @param bSave
	 *            true: yes, else no.
	 */
	public void setSaveOnFocusLost(boolean save){
		this.saveOnFocusLost = save;
	}
	
	/**
	 * Set style for all following operations (until the next call to setFont or setStyle)
	 * 
	 * @param style
	 *            SWT.MIN, SWT.NORMAL, SWT.BOLD (thin, normal or bold)
	 * @return false on error. True on success
	 */
	public boolean setStyle(int style){
		this.fontStyle = style;
		return true;
	}
	
	/**
	 * Show or hide the component specific menu bar
	 * 
	 * @param visible
	 *            if true then show
	 */
	public void showMenu(boolean visible){
		this.showMenubar = visible;
		
		try {
			showMenuBarChanged();
		} catch (NOAException ex) {
			handleError(ex, false);
		}
	}
	
	/**
	 * Show or hide the component specific toolbar
	 * 
	 * @param visible
	 *            if true then show
	 */
	public void showToolbar(boolean visible){
		this.showToolbar = visible;
		
		try {
			showToolBarChanged();
		} catch (NOAException ex) {
			handleError(ex, false);
		}
	}
	
	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
		throws CoreException{
		// Do nothing
	}
	
	@Override
	public boolean isDirectOutput(){
		return false;
	}

	@Override
	public void setParameter(Parameter parameter){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void initTemplatePrintSettings(String template){
		printSettings = new TextTemplatePrintSettings(template, getMimeType());
		
		// only relevant if document and print settings are not null
		if (document != null && printSettings.getPrinter() != null) {
			try {
				IPrinter printer =
					document.getPrintService().createPrinter(printSettings.getPrinter());
				document.getPrintService().setActivePrinter(printer);
				
				if (printSettings.getTray() != null) {
					UnoUtil.setPrinterTray(document.getXTextDocument(), printSettings.getTray());
				}
			} catch (Exception e) {
				log.error("Failed configuring print settings for '" + template + "'", e);
			}
		}
	}
}
