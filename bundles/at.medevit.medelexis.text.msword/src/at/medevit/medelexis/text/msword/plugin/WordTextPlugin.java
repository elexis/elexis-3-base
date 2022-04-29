/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin;

import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.medelexis.text.msword.Activator;
import at.medevit.medelexis.text.msword.Messages;
import at.medevit.medelexis.text.msword.plugin.util.CommunicationFile;
import at.medevit.medelexis.text.msword.plugin.util.DocxWordDocument;
import at.medevit.medelexis.text.msword.plugin.util.ExternalFile;
import at.medevit.medelexis.text.msword.plugin.util.GlobalOleWordWrapperManager;
import at.medevit.medelexis.text.msword.plugin.util.OleWordApplication;
import at.medevit.medelexis.text.msword.plugin.util.OleWordBorder;
import at.medevit.medelexis.text.msword.plugin.util.OleWordCell;
import at.medevit.medelexis.text.msword.plugin.util.OleWordCells;
import at.medevit.medelexis.text.msword.plugin.util.OleWordColumn;
import at.medevit.medelexis.text.msword.plugin.util.OleWordColumns;
import at.medevit.medelexis.text.msword.plugin.util.OleWordConstants;
import at.medevit.medelexis.text.msword.plugin.util.OleWordDialog;
import at.medevit.medelexis.text.msword.plugin.util.OleWordDialogs;
import at.medevit.medelexis.text.msword.plugin.util.OleWordDocument;
import at.medevit.medelexis.text.msword.plugin.util.OleWordFind;
import at.medevit.medelexis.text.msword.plugin.util.OleWordFont;
import at.medevit.medelexis.text.msword.plugin.util.OleWordHeaderFooter;
import at.medevit.medelexis.text.msword.plugin.util.OleWordHeadersFooters;
import at.medevit.medelexis.text.msword.plugin.util.OleWordPageSetup;
import at.medevit.medelexis.text.msword.plugin.util.OleWordParagraphFormat;
import at.medevit.medelexis.text.msword.plugin.util.OleWordRange;
import at.medevit.medelexis.text.msword.plugin.util.OleWordRow;
import at.medevit.medelexis.text.msword.plugin.util.OleWordRows;
import at.medevit.medelexis.text.msword.plugin.util.OleWordSection;
import at.medevit.medelexis.text.msword.plugin.util.OleWordSections;
import at.medevit.medelexis.text.msword.plugin.util.OleWordShape;
import at.medevit.medelexis.text.msword.plugin.util.OleWordShapes;
import at.medevit.medelexis.text.msword.plugin.util.OleWordSite;
import at.medevit.medelexis.text.msword.plugin.util.OleWordTable;
import at.medevit.medelexis.text.msword.plugin.util.OleWordTextFrame;
import at.medevit.medelexis.text.msword.plugin.util.OleWrapperManager;
import at.medevit.medelexis.text.msword.ui.MSWordPreferencePage;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.text.ReplaceCallback;
import ch.elexis.core.data.status.ElexisStatus;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.MimeTypeUtil;
import ch.elexis.core.ui.views.textsystem.TextTemplatePrintSettings;

/**
 * ITextPlugin implementation for MS Word.
 *
 * Loading and saving has to be done via files as the Word COM object does not
 * support these functions when embedded.
 *
 * @author thomashu
 *
 */
public class WordTextPlugin implements ITextPlugin {
	private static Logger logger = LoggerFactory.getLogger(WordTextPlugin.class);

	private static ArrayList<WordTextPlugin> instances = new ArrayList<WordTextPlugin>();

	private Composite wordComposite;
	private OleWordSite word;
	private OleWordDocument openDocument;

	private ExternalFile externalOpenDocument;

	protected boolean saveOnFocusLost = true;

	protected PageFormat format = ITextPlugin.PageFormat.USER;

	// font settings
	private String fontName = null;
	private int fontStyle = -1;
	private int fontSize = -1;

	private boolean dirty;
	private DocxWordDocument dirtyFile;

	private Parameter parameter;

	private static TextTemplatePrintSettings printSettings;

	public WordTextPlugin() {
		synchronized (instances) {
			instances.add(this);
			dirty = false;
		}
	}

	private void reloadDirtyFile() {
		synchronized (this) {
			if (dirtyFile != null) {
				CommunicationFile file = word.getCommunicationFile();
				file.write(dirtyFile);
				reload(file.getFile(), true);
				dirtyFile = null;
				dirty = false;
			}
		}
	}

	private void setDirtyFile(DocxWordDocument doc) {
		synchronized (this) {
			dirtyFile = doc;
			dirty = true;
			ReloadRunnable.createInstance(this);
		}
	}

	private static class ReloadRunnable implements Runnable {
		private static Map<WordTextPlugin, ReloadRunnable> instances = new HashMap<WordTextPlugin, ReloadRunnable>();

		private WordTextPlugin wtp;

		public static void createInstance(WordTextPlugin plugin) {
			synchronized (plugin) {
				if (instances.get(plugin) == null) {
					ReloadRunnable runnable = new ReloadRunnable(plugin);
					Display.getCurrent().timerExec(1000, runnable);
					instances.put(plugin, runnable);
				}
			}
		}

		private ReloadRunnable(WordTextPlugin plugin) {
			wtp = plugin;
		}

		@Override
		public void run() {
			synchronized (wtp) {
				if (wtp.dirty) {
					wtp.reloadDirtyFile();
				}
				instances.remove(wtp);
			}
		}
	}

	private DocxWordDocument getDirtyFile() {
		synchronized (this) {
			if (dirtyFile == null) {
				OleWrapperManager manager = new OleWrapperManager();
				dirtyFile = word.getApplication(manager).getActiveDocument(manager).getDocxWordDocument();
				manager.dispose();
			}
			return dirtyFile;
		}
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		// Do nothing
	}

	@Override
	public PageFormat getFormat() {
		return format;
	}

	@Override
	public void setFormat(PageFormat f) {
		format = f;
	}

	@Override
	public Composite createContainer(Composite parent, ICallback handler) {
		synchronized (this) {
			wordComposite = new Composite(parent, SWT.NONE);
			GridLayout gridLayout = new GridLayout(1, true);
			gridLayout.numColumns = 1;
			gridLayout.marginHeight = 0;
			gridLayout.marginWidth = 0;
			wordComposite.setLayout(gridLayout);

			ToolBar toolBar = new ToolBar(wordComposite, SWT.FLAT);
			GridData gridData = new GridData(GridData.FILL, SWT.TOP, true, false);
			toolBar.setLayoutData(gridData);
			ToolBarManager toolBarManager = new ToolBarManager(toolBar);

			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("at.medevit.medelexis.text.msword.WordTextPluginHash", //$NON-NLS-1$
					Integer.toString(this.hashCode()));

			CommandContributionItemParameter parameter = new CommandContributionItemParameter(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow(), null,
					"at.medevit.medelexis.text.msword.printDialog", //$NON-NLS-1$
					CommandContributionItem.STYLE_PUSH);
			parameter.icon = Images.IMG_PRINTER.getImageDescriptor();
			parameter.parameters = parameters;

			toolBarManager.add(new CommandContributionItem(parameter));

			toolBarManager.update(true);

			word = new OleWordSite(wordComposite);
			gridData = new GridData(GridData.FILL, SWT.FILL, true, true);
			word.getFrame().setLayoutData(gridData);

			logger.info("Created Word Site " + word + " for instance " + this);

			return wordComposite;
		}
	}

	@Override
	public void setFocus() {
		if (word != null) {
			word.setFocus();
		}
	}

	@Override
	public void dispose() {
		logger.info("Disposing Word Site " + word + " with instance " + this);
		synchronized (instances) {
			instances.remove(this);
			if (word != null && !word.isDisposed())
				word.dispose();
		}
	}

	@Override
	public void showMenu(boolean b) {
		// Do nothing
	}

	@Override
	public void showToolbar(boolean b) {
		// Do nothing
	}

	@Override
	public void setSaveOnFocusLost(boolean bSave) {
		saveOnFocusLost = bSave;
	}

	@Override
	public boolean createEmptyDocument() {
		synchronized (this) {
			try {
				// Reload the OleWordSite without a file results in creating a new document.
				reload(null, false);
			} catch (IllegalStateException e) {
				handleException(e);
				return false;
			}
			return true;
		}
	}

	@Override
	public boolean loadFromByteArray(byte[] bs, boolean asTemplate) {
		synchronized (this) {
			try {
				if (!asTemplate && CoreHub.userCfg.get(MSWordPreferencePage.MSWORD_OPEN_EXTERN, false)) {
					externalOpenDocument = new ExternalFile();
					externalOpenDocument.write(bs);
					externalOpenDocument.open();
				} else {
					if (externalOpenDocument != null) {
						externalOpenDocument.dispose();
						externalOpenDocument = null;
					}
					// Write the byte array to the CommunicationFile and reload the OleWordSite with
					// that CommunicationFile.
					CommunicationFile file = word.getCommunicationFile();
					file.write(bs);
					reload(file.getFile(), false);
				}
			} catch (IllegalStateException e) {
				handleException(e);
				return false;
			}
			return true;
		}
	}

	@Override
	public boolean loadFromStream(InputStream is, boolean asTemplate) {
		synchronized (this) {
			try {
				if (!asTemplate && CoreHub.userCfg.get(MSWordPreferencePage.MSWORD_OPEN_EXTERN, false)) {
					externalOpenDocument = new ExternalFile();
					externalOpenDocument.write(is);
					externalOpenDocument.open();
				} else {
					if (externalOpenDocument != null) {
						externalOpenDocument.dispose();
						externalOpenDocument = null;
					}
					// Write the InputStream to the CommunicationFile and reload the OleWordSite
					// with
					// that CommunicationFile.
					CommunicationFile file = word.getCommunicationFile();
					file.write(is);
					reload(file.getFile(), false);
				}
			} catch (IllegalStateException e) {
				handleException(e);
				return false;
			}
			return true;
		}
	}

	@Override
	public byte[] storeToByteArray() {
		if (parameter == Parameter.READ_ONLY) {
			return null;
		}
		synchronized (this) {
			byte[] ret = null;
			try {
				// Get a CommunicationFile with the ActiveDocument of the OleWordSite as content
				// and
				// read that file into a byte array.
				FileInputStream fis = null;
				try {
					// read external if exists, else from open document
					if (externalOpenDocument != null) {
						fis = new FileInputStream(externalOpenDocument.getFile());
						ret = new byte[(int) externalOpenDocument.getFile().length()];
						fis.read(ret);
					} else {
						if (dirty) {
							reloadDirtyFile();
						}

						CommunicationFile file = word.getCommunicationFile();
						if (file.isError()) {
							// try to cancel the save operation if the loaded file is in error state
							return null;
						}
						// update with open document
						if (openDocument != null) {
							// if the open document is no longer valid ... update with active document
							if (!openDocument.isValid()) {
								logger.error("Saving not valid Word Document of instance " + this);
								return null;
							}
							file.write(openDocument);
						}

						fis = new FileInputStream(file.getFile());
						ret = new byte[(int) file.getFile().length()];
						fis.read(ret);
					}
				} catch (IOException e) {
					throw new IllegalStateException(e);
				} finally {
					try {
						if (fis != null)
							fis.close();
					} catch (IOException e) {
						// ignore exception on close
					}
				}
			} catch (IllegalStateException e) {
				handleException(e);
				return null;
			}
			// dont write empty files, Brief will not overwrite if return value is null
			if (ret.length == 0) {
				return null;
			}
			return ret;
		}
	}

	@Override
	public boolean findOrReplace(String pattern, ReplaceCallback cb) {
		return findOrReplaceFile(pattern, cb);
	}

	protected boolean findOrReplaceFile(String pattern, ReplaceCallback cb) {
		synchronized (this) {
			// As Word does not support regular expressions, the search and replace for the
			// matches
			// is done directly on the xml content (DocxWordDocument)
			try {
				DocxWordDocument docxWordDocument = getDirtyFile();
				boolean found = docxWordDocument.findAndReplaceWithCallback(pattern, cb);

				setDirtyFile(docxWordDocument);

				return found;
			} catch (IllegalStateException e) {
				handleException(e);
				return false;
			}
		}
	}

	// Currently not used ... OLE is very slow
	protected boolean findOrReplaceOle(String pattern, ReplaceCallback cb) {
		synchronized (this) {
			OleWrapperManager manager = new OleWrapperManager();
			// As Word does not support regular expressions, the search for the matches is
			// done
			// directly on the xml content (DocxWordDocument), and then the matches are used
			// to
			// search and replace using Word.
			try {
				DocxWordDocument docxWordDocument = word.getApplication(manager).getActiveDocument(manager)
						.getDocxWordDocument();

				Iterator<String> found = docxWordDocument.getMatchesIterator(pattern);
				while (found.hasNext()) {
					String foundStr = found.next();
					String replace = null;
					OleWordRange range = null;

					Object obj = cb.replace(foundStr);
					if (obj instanceof String) {
						replace = (String) obj;
						range = findAndReplaceFirst(foundStr, getWindowsString(replace));
					}
					if (range != null) {
						applyFont(range);
						manager.add(range);
					}
				}
			} catch (IllegalStateException e) {
				handleException(e);
				manager.dispose();
				return false;
			}
			manager.dispose();
			return true;
		}
	}

	private String getWindowsString(String string) {
		String ret = string;
		// try to fix line endings
		int idx = ret.indexOf("\r\n"); //$NON-NLS-1$
		if (idx == -1) {
			idx = ret.indexOf(StringUtils.LF);
			if (idx != -1) {
				ret = ret.replaceAll(StringUtils.LF, StringUtils.CR); // $NON-NLS-1$
			}
		}
		return ret;
	}

	@Override
	public Object insertText(Object pos, String text, int adjust) {
		synchronized (this) {
			if (dirty)
				reloadDirtyFile();

			OleWrapperManager manager = new OleWrapperManager();
			OleWordRange range = null;
			// append the text to the OleWordRange defining the position in the document
			// the alignment is applied to the paragraph before setting the text of the
			// returned
			// range
			if (pos == null)
				range = word.getApplication(manager).getActiveDocument(manager).getContent(manager);
			else
				range = (OleWordRange) pos;

			range.collapse();
			range.setText(getWindowsString(text));
			applyFont(range);
			// OleWordParagraphFormat pFormat = range.getParagraphFormat(manager);
			// pFormat.setAlignment(adjust);

			manager.remove(range);
			manager.dispose();
			return range;
		}
	}

	@Override
	public Object insertTextAt(int x, int y, int w, int h, String text, int adjust) {
		synchronized (this) {
			if (dirty)
				reloadDirtyFile();

			OleWrapperManager manager = new OleWrapperManager();
			OleWordRange textRange = null;

			// Create a new OleWordShape (Textbox) to position the text in the document
			// the alignment is applied to the paragraph before setting the text of the
			// returned
			// range
			OleWordApplication app = word.getApplication(manager);
			OleWordDocument doc = app.getActiveDocument(manager);
			OleWordPageSetup pageSetup = doc.getPageSetup(manager);
			// add left margin as x = 0 would result in an not printable area, handle y the
			// same
			// way!? OO seems to handle y without margin?
			int ptX = app.getPoints(x) + pageSetup.getLeftMargin();
			int ptY = app.getPoints(y);

			int ptW = app.getPoints(w);
			int ptH = app.getPoints(h);

			OleWordShape textbox = doc.getShapes(manager).addTextbox(OleWordShapes.msoTextOrientationHorizontal, ptX,
					ptY, ptW, ptH, manager);
			textbox.getLine(manager).setVisible(false);
			OleWordTextFrame textFrame = textbox.getTextFrame(manager);
			textFrame.setMarginLeft(0);
			textFrame.setMarginTop(0);
			textFrame.setMarginRight(0);
			textFrame.setMarginBottom(0);
			textFrame.setAutoSize(true);

			textRange = textbox.getTextFrame(manager).getTextRange(manager);
			textRange.setText(getWindowsString(text));

			OleWordParagraphFormat paragraph = textRange.getParagraphFormat(manager);
			paragraph.setAlignment(adjust);
			paragraph.setSpaceAfter(0);
			paragraph.setSpaceBefore(0);

			applyFont(textRange);

			manager.remove(textRange);
			manager.dispose();
			return textRange;
		}
	}

	@Override
	public Object insertText(String marke, String text, int adjust) {
		// Use the DcoxWordDocument to search for the first occurrence of the pattern in
		// the text.
		// Then use the returned matching string with OleFind to locate and replace it
		// in the
		// document.
		// TODO if marke is used as regex strings like [xyz] are not interpreted as
		// string but as character class that is currently not the desired behavior so
		// skip regex
		// handling
		// search in the text
		synchronized (this) {
			if (dirty)
				reloadDirtyFile();

			OleWordRange range = null;
			range = findAndReplaceFirst(marke, getWindowsString(text));
			if (range != null) {
				OleWrapperManager manager = new OleWrapperManager();

				applyFont(range);
				OleWordParagraphFormat paragraph = range.getParagraphFormat(manager);
				paragraph.setAlignment(adjust);

				manager.dispose();
			}
			return range;
		}
	}

	/**
	 * Find and replace the first occurrence of findStr with replaceStr in the
	 * active document. Occurrences in text and textboxes (OleWordShapes) have to be
	 * searched separately. This implementation searches first text then textboxes.
	 *
	 * @param findStr
	 * @param replaceStr
	 * @return OleWordRange object containing the replaced text or null if not found
	 */
	protected OleWordRange findAndReplaceFirst(String findStr, String replaceStr) {
		OleWrapperManager manager = new OleWrapperManager();
		// sections
		OleWordDocument doc = word.getApplication(manager).getActiveDocument(manager);
		OleWordSections sections = doc.getSections(manager);
		int sectionsCnt = sections.getCount();
		for (int sectionsIdx = 0; sectionsIdx < sectionsCnt; sectionsIdx++) {
			OleWordRange range;
			OleWordFind find;

			OleWordSection section = sections.getItem(sectionsIdx + 1, manager);

			// headers
			OleWordHeadersFooters headers = section.getHeaders(manager);
			OleWordHeaderFooter header = headers.getItem(OleWordConstants.wdHeaderFooterPrimary, manager);
			range = header.getRange(manager);
			if (range != null) {
				find = range.getFind(manager);
				if (find.execute(findStr, replaceStr)) {
					manager.remove(range);
					manager.dispose();
					return range;
				}
			}

			// content
			range = section.getRange(manager);
			if (range != null) {
				find = range.getFind(manager);
				if (find.execute(findStr, replaceStr)) {
					manager.remove(range);
					manager.dispose();
					return range;
				}
			}

			// footers
			OleWordHeadersFooters footers = section.getFooters(manager);
			OleWordHeaderFooter footer = footers.getItem(OleWordConstants.wdHeaderFooterPrimary, manager);
			range = footer.getRange(manager);
			if (range != null) {
				find = range.getFind(manager);
				if (find.execute(findStr, replaceStr)) {
					manager.remove(range);
					manager.dispose();
					return range;
				}
			}

		}

		// search in the textboxes
		OleWordShapes shapes = doc.getShapes(manager);
		int cnt = shapes.getCount();
		for (int i = 0; i < cnt; i++) {
			OleWordShape shape = shapes.getItem(i + 1, manager);
			OleWordTextFrame textFrame = shape.getTextFrame(manager);
			if (textFrame.getHasText()) {
				OleWordRange range = textFrame.getTextRange(manager);
				OleWordFind find = range.getFind(manager);
				if (find.execute(findStr, replaceStr)) {
					manager.remove(range);
					manager.dispose();
					return range;
				}
			}
		}
		manager.dispose();
		return null;
	}

	@Override
	public boolean insertTable(String place, int properties, String[][] contents, int[] columnSizes) {
		synchronized (this) {
			return insertTableFile(place, properties | ITextPlugin.GRID_VISIBLE, contents, columnSizes);
		}
	}

	protected boolean insertTableFile(String place, int properties, String[][] contents, int[] columnSizes) {

		try {
			DocxWordDocument docxWordDocument = getDirtyFile();
			docxWordDocument.findAndInsertTable(place, properties, contents, columnSizes);

			setDirtyFile(docxWordDocument);

		} catch (IllegalStateException e) {
			handleException(e);
			return false;
		}
		return true;
	}

	protected boolean insertTableOle(String place, int properties, String[][] contents, int[] columnSizes) {
		synchronized (this) {
			OleWrapperManager manager = new OleWrapperManager();

			OleWordRange range = findAndReplaceFirst(place, StringUtils.EMPTY);
			manager.add(range);
			if (range != null && contents != null) {
				OleWordTable table = range.getTables(manager).add(range, contents.length, contents[0].length, manager);
				// all rows
				OleWordRows rows = table.getRows(manager);
				long rowCnt = rows.getCount();
				for (long rowIndex = 0; rowIndex < rowCnt; rowIndex++) {
					OleWordRow row = rows.getItem(rowIndex + 1, manager);
					// all cells
					OleWordCells cells = row.getCells(manager);
					long cellCnt = cells.getCount();
					for (long cellIndex = 0; cellIndex < cellCnt; cellIndex++) {
						OleWordCell cell = cells.getItem(cellIndex + 1, manager);
						cell.getRange(manager).insertAfter(contents[(int) rowIndex][(int) cellIndex]);
					}
				}
				// apply properties
				if ((properties & GRID_VISIBLE) > 0) {
					table.getBorders(manager).setOutsideLineStyle(OleWordConstants.wdLineStyleSingle);
					table.getBorders(manager).setInsideLineStyle(OleWordConstants.wdLineStyleSingle);
				} else {
					table.getBorders(manager).setOutsideLineStyle(OleWordConstants.wdLineStyleNone);
					table.getBorders(manager).setInsideLineStyle(OleWordConstants.wdLineStyleNone);
				}
				if ((properties & FIRST_ROW_IS_HEADER) > 0) {
					rows = table.getRows(manager);
					rowCnt = rows.getCount();
					if (rowCnt > 0) {
						OleWordRow row = rows.getItem(1, manager);
						row.getRange(manager).getFont(manager).setItalic(true);
						OleWordBorder border = row.getBorders(manager).getItem(OleWordConstants.wdBorderBottom,
								manager);
						border.setLineStyle(OleWordConstants.wdLineStyleSingle);
						border.setLineWidth(OleWordConstants.wdLineWidth150pt);
					}
				}
				// apply column sizes if specified
				if (columnSizes != null) {
					int percent = word.getApplication(manager).getActiveDocument(manager).getPageSetup(manager)
							.getPageWidth() / 100;
					OleWordColumns columns = table.getColumns(manager);
					long columnCnt = columns.getCount();
					for (long columnIndex = 0; columnIndex < columnCnt
							&& columnIndex < columnSizes.length; columnIndex++) {
						OleWordColumn column = columns.getItem(columnIndex + 1, manager);
						column.setWidth(columnSizes[(int) columnIndex] * percent);
					}
				}
			}
			manager.dispose();
			return true;
		}
	}

	@Override
	public boolean print(String toPrinter, String toTray, boolean waitUntilFinished) {
		synchronized (this) {
			if (dirty)
				reloadDirtyFile();

			// trigger save of word document -> have last printed document in temp folder
			OleWrapperManager tmpmanager = new OleWrapperManager();
			word.getApplication(tmpmanager).getActiveDocument(tmpmanager).getDocxWordDocument();
			tmpmanager.dispose();

			OleWrapperManager manager = new OleWrapperManager();

			OleWordApplication app = word.getApplication(manager);
			// remember default settings
			String defaultPrinter = app.getActivePrinter();
			String defaultTray = app.getOptions(manager).getDefaultTray();
			if (printSettings != null) {
				toPrinter = printSettings.getPrinter() == null ? toPrinter : printSettings.getPrinter();
				toTray = printSettings.getTray() == null ? toTray : printSettings.getTray();
			}

			if (toPrinter != null && !toPrinter.isEmpty()) {
				try {
					app.setActivePrinter(toPrinter);
				} catch (Exception e) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Messages.WordTextPlugin_PrintError,
							Messages.WordTextPlugin_PrintConnectionIssue + ": " + toPrinter + "\n[" + e.getMessage()
									+ "]\n\n" + Messages.WordTextPlugin_SelectAnotherPrinter);
					PrintDialog pd = new PrintDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
					PrinterData pdata = pd.open();
					if (pdata != null && pdata.name != null) {
						app.setActivePrinter(pdata.name);
					} else {
						return false;
					}
				}
			}
			if (toTray != null && !toTray.isEmpty())
				app.getOptions(manager).setDefaultTray(toTray);

			app.getActiveDocument(manager).printOut(!waitUntilFinished);

			// set default settings
			app.setActivePrinter(defaultPrinter);
			app.getOptions(manager).setDefaultTray(defaultTray);

			manager.dispose();
			return true;
		}
	}

	private void applyFont(OleWordRange range) {
		OleWrapperManager manager = new OleWrapperManager();
		OleWordFont font = range.getFont(manager);
		if (fontName != null)
			font.setName(fontName);
		if (fontSize != -1)
			font.setSize(fontSize);
		if (fontStyle != -1) {
			if (fontStyle == SWT.NORMAL) {
				font.setBold(false);
				font.setItalic(false);
			} else if (fontStyle == SWT.BOLD) {
				font.setBold(true);
				font.setItalic(false);
			} else if (fontStyle == SWT.ITALIC) {
				font.setBold(false);
				font.setItalic(true);
			}
		}
		manager.dispose();
	}

	@Override
	public boolean setFont(String name, int style, float size) {
		synchronized (this) {
			fontName = name;
			fontStyle = style;
			fontSize = (int) size;
			return true;
		}
	}

	protected void resetFont() {
		fontName = null;
		fontStyle = -1;
		fontSize = -1;
	}

	@Override
	public boolean setStyle(int style) {
		synchronized (this) {
			fontStyle = style;
			return true;
		}
	}

	protected void resetStyle() {
		fontStyle = -1;
	}

	protected void reload(File file, boolean dirty) {
		synchronized (this) {
			if (!dirty) {
				resetFont();
				resetStyle();
			}
			if (openDocument != null) {
				// remove all documents of other active instances ...
				for (WordTextPlugin instance : instances) {
					if (instance != this) {
						logger.debug("Dont Dispose Word Document of instance " + instance);
						if (instance.openDocument != null) {
							GlobalOleWordWrapperManager.remove(instance.openDocument.getOleObj());
						}
					}
				}
				openDocument.dispose();
			}
			if (file != null && parameter == Parameter.READ_ONLY) {
				// rewrite tmp file as read only
				DocxWordDocument document = new DocxWordDocument(file);
				document.setReadOnly(true);
				try {
					document.writeTo(new FileOutputStream(file));
				} catch (IOException e) {
					logger.error("Could not set read only mode of document. " + file.getAbsolutePath(), e);
				}
			}
			openDocument = word.reload(file, parameter);
		}
	}

	@Override
	public boolean clear() {
		return false;
	}

	@Override
	public String getMimeType() {
		return MimeTypeUtil.MIME_TYPE_MSWORD;
	}

	@Override
	public boolean isDirectOutput() {
		return false;
	}

	protected void handleException(Exception e) {
		if (e instanceof IllegalStateException) {
			StatusManager.getManager().handle(new ElexisStatus(ElexisStatus.ERROR, Activator.PLUGIN_ID,
					ElexisStatus.CODE_NOFEEDBACK, e.getMessage(), e, ElexisStatus.LOG_ERRORS), StatusManager.BLOCK);
		}
	}

	private static WordTextPlugin getInstanceByHash(String hashCode) {
		Integer hash = Integer.decode(hashCode);
		for (WordTextPlugin instance : instances) {
			if (hash.intValue() == instance.hashCode()) {
				return instance;
			}
		}
		return null;
	}

	private static WordTextPlugin getActiveInstance() {
		WordTextPlugin activeInstance = null;
		for (WordTextPlugin instance : instances) {
			if (instance.wordComposite != null && !instance.wordComposite.isDisposed()
					&& instance.wordComposite.isVisible()) {
				if (activeInstance == null) {
					activeInstance = instance;
				} else if (instance.wordComposite.isFocusControl()) {
					activeInstance = instance;
				}
			}
		}
		return activeInstance;
	}

	public static void openPrintDialog(String wordTextPluginHash) {
		synchronized (instances) {
			WordTextPlugin activeInstance = null;
			if (wordTextPluginHash != null)
				activeInstance = getInstanceByHash(wordTextPluginHash);
			else
				activeInstance = getActiveInstance();

			if (activeInstance != null) {
				OleWrapperManager manager = new OleWrapperManager();

				activeInstance.setFocus();
				OleWordApplication app = activeInstance.word.getApplication(manager);
				// remember default settings
				String defaultPrinter = app.getActivePrinter();
				String defaultTray = app.getOptions(manager).getDefaultTray();
				if (printSettings != null && printSettings.getPrinter() != null) {
					String toPrinter = printSettings.getPrinter();
					String toTray = printSettings.getTray();
					if (toPrinter != null && !toPrinter.isEmpty())
						app.setActivePrinter(toPrinter);
					if (toTray != null && !toTray.isEmpty())
						app.getOptions(manager).setDefaultTray(toTray);
				}
				OleWordDialogs dialogs = app.getDialogs(manager);
				OleWordDialog dialog = dialogs.getItem(OleWordConstants.wdDialogFilePrint, manager);
				// read events so dialog does not block
				while (Display.getCurrent().readAndDispatch()) {
					;
				}
				dialog.show();

				// set default settings
				app.setActivePrinter(defaultPrinter);
				app.getOptions(manager).setDefaultTray(defaultTray);

				manager.dispose();
				return;
			} else {
				StatusManager.getManager().handle(
						new Status(Status.WARNING, Activator.PLUGIN_ID, Messages.WordTextPlugin_NoActiveWordView),
						StatusManager.SHOW);
			}
		}
	}

	@Override
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public void initTemplatePrintSettings(String template) {
		printSettings = new TextTemplatePrintSettings(template, getMimeType());
	}
}
