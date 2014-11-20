/*******************************************************************************
 * Copyright (c) 2009-2014, A. Kaufmann, Niklaus Giger and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation
 *    Niklaus Giger - support for having several documents open at once
 *
 *******************************************************************************/

package com.hilotec.elexis.opendocument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.OdfXMLFactory;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.doc.draw.OdfDrawTextBox;
import org.odftoolkit.odfdom.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.doc.office.OdfOfficeFontFaceDecls;
import org.odftoolkit.odfdom.doc.office.OdfOfficeText;
import org.odftoolkit.odfdom.doc.style.OdfStyle;
import org.odftoolkit.odfdom.doc.style.OdfStyleBackgroundImage;
import org.odftoolkit.odfdom.doc.style.OdfStyleColumns;
import org.odftoolkit.odfdom.doc.style.OdfStyleFontFace;
import org.odftoolkit.odfdom.doc.style.OdfStyleGraphicProperties;
import org.odftoolkit.odfdom.doc.style.OdfStyleParagraphProperties;
import org.odftoolkit.odfdom.doc.style.OdfStyleTableColumnProperties;
import org.odftoolkit.odfdom.doc.style.OdfStyleTextProperties;
import org.odftoolkit.odfdom.doc.text.OdfTextLineBreak;
import org.odftoolkit.odfdom.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.doc.text.OdfTextTab;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawTextBoxElement;
import org.odftoolkit.odfdom.dom.element.style.StyleBackgroundImageElement;
import org.odftoolkit.odfdom.dom.element.style.StyleColumnsElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFontFaceElement;
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableColumnPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.text.ReplaceCallback;
import ch.elexis.core.data.util.PlatformHelper;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Brief;
import ch.elexis.data.Patient;
import ch.rgw.tools.StringTool;

/**
 * This text-plugin can work under Linux/MacoSX/Windows with any editor which is able to modify in
 * the OpenDocument Format. It's primary use is for LibreOffice under Windows and Linux.
 * <p>
 * The user documentation can be found at
 * <p>
 * http://wiki.elexis.info/Com.hilotec.elexis.opendocument.feature.feature.group
 * <p>
 * <p>
 * The following features are implemented
 * <p>
 * * all normally supported formats for text (bold, italic)
 * <p>
 * * Tables in templates allow [] which contains a place holder {} for the formatting of each line.
 * This allows to configure the width of each column
 * <p>
 * * Create an underlined line for lines starting and ending with '_'
 *
 * <p>
 * * Since version 3.1 it is possible to open several documents concurrently.
 * <p>
 * * ODT documents are removed after saving their contents as extinfo in the corresponding Brief.
 * <p>
 * * A list of all currently opened documents is show in the view of the plugin.
 * <p>
 * * A helper script (open_odf.bat/sh) is used to launch the editor application. It may not return
 * before the editor application closed the document. It must return soon after the document is
 * closed or Elexis will wait forever.
 * <p>
 * * Care was given to make adding/removing files to the list of open files as resilient/atomic as
 * possible to avoid false error. (See {@link updateOpenFiles})
 * <p>
 * * Known working templates can be found under https://github.com/hilotec/elexis-vorlagen
 * <p>
 * Known deficiencies:
 * <p>
 * * Some combinations of unreadable documents/double clicking an already open document may result
 * in an unspecified behaviour.
 * <p>
 * * Separate instances of Elexis can modify the same document. (This problem should probably be
 * fixed in elexis core).
 * <p>
 * * A user can save the document under another name. In this case Elexis is unaware of the changes
 * in that document and will ignore any modifications made there.
 *
 * @author Antoine Kaufmann & Niklaus Giger
 *
 */
public class TextPlugin implements ITextPlugin {

	/** Internal Representation of current style */
	private class Style {
		final static int ALIGN = SWT.LEFT | SWT.CENTER | SWT.RIGHT;

		String font = null;
		public int flags;
		Float size = null;

		public void setStyle(int s){
			flags = s;
		}

		public void clearAlign(){
			flags &= (~ALIGN);
		}

		public void setAlign(int a){
			clearAlign();
			flags |= a;
		}

		public void setFont(String n, int f, float s){
			font = n;
			flags = f;
			size = s;
		}

		private String label(){
			MessageDigest m;
			try {
				String pass = "" + flags;
				if (font != null)
					pass += "_" + font;
				if (size != null)
					pass += "_" + size;
				m = MessageDigest.getInstance("MD5");
				byte[] data = pass.getBytes();
				m.update(data, 0, data.length);
				BigInteger i = new BigInteger(1, m.digest());
				return String.format("%1$032X", i);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return null;
		}

		private void declareFont(String name) throws Exception{
			OdfFileDom contentDom = odt.getContentDom();
			XPath xpath = odt.getXPath();

			// Declare font face
			OdfOfficeFontFaceDecls ffdec =
				(OdfOfficeFontFaceDecls) xpath.evaluate("//office:font-face-decls", contentDom,
					XPathConstants.NODE);

			OdfStyleFontFace fface = (OdfStyleFontFace) ffdec.getFirstChild();
			NodeList nl =
				(NodeList) xpath.evaluate("//office:font-face-decls/style:font-face[@style:name='"
					+ font + "']", contentDom, XPathConstants.NODESET);
			if (nl.getLength() == 0) {
				fface =
					(OdfStyleFontFace) OdfXMLFactory.newOdfElement(contentDom,
						StyleFontFaceElement.ELEMENT_NAME);
				fface.setStyleNameAttribute(font);
				fface.setSvgFontFamilyAttribute("'" + font + "'");
				ffdec.appendChild(fface);
			}
		}

		private void createStyle(String sname, String family){
			try {
				OdfFileDom contentDom = odt.getContentDom();
				XPath xpath = odt.getXPath();

				// Create Style
				NodeList nl =
					(NodeList) xpath.evaluate("//style:style[@style:name='" + sname + "']",
						contentDom, XPathConstants.NODESET);
				if (nl.getLength() == 0) {
					OdfOfficeAutomaticStyles autost =
						(OdfOfficeAutomaticStyles) xpath.evaluate("//office:automatic-styles",
							contentDom, XPathConstants.NODE);

					OdfStyle frst =
						(OdfStyle) OdfXMLFactory.newOdfElement(contentDom,
							StyleStyleElement.ELEMENT_NAME);
					frst.setStyleNameAttribute(sname);
					frst.setStyleFamilyAttribute(family);
					frst.setStyleParentStyleNameAttribute("Standard");
					autost.appendChild(frst);

					OdfStyleTextProperties stp =
						(OdfStyleTextProperties) OdfXMLFactory.newOdfElement(contentDom,
							StyleTextPropertiesElement.ELEMENT_NAME);

					if (font != null) {
						declareFont(font);
						stp.setStyleFontNameAttribute(font);
					}

					if (size != null) {
						stp.setFoFontSizeAttribute(size + "pt");
						stp.setStyleFontSizeAsianAttribute(size + "pt");
						stp.setStyleFontSizeComplexAttribute(size + "pt");
					}

					if ((flags & SWT.BOLD) != 0) {
						stp.setFoFontWeightAttribute("bold");
					}
					if ((flags & SWT.ITALIC) != 0) {
						stp.setFoFontStyleAttribute("italic");
					}

					// If we have a paragraph style we might need to apply
					// alignment settings.
					if ((flags & ALIGN) != 0 && family.compareTo("paragraph") == 0) {
						OdfStyleParagraphProperties pp =
							(OdfStyleParagraphProperties) OdfXMLFactory.newOdfElement(contentDom,
								StyleParagraphPropertiesElement.ELEMENT_NAME);
						if ((flags & SWT.LEFT) != 0) {
							pp.setFoTextAlignAttribute("left");
						} else if ((flags & SWT.RIGHT) != 0) {
							pp.setFoTextAlignAttribute("right");
						} else if ((flags & SWT.CENTER) != 0) {
							pp.setFoTextAlignAttribute("center");
						}
						frst.appendChild(pp);
					}

					frst.appendChild(stp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/** @return Label for paragraph style */
		public String getParagraphLbl(){
			String lbl = label() + "_pg";
			createStyle(lbl, "paragraph");
			return lbl;
		}

		/** @return Label for text style */
		public String getTextLbl(){
			String lbl = label() + "_txt";
			createStyle(lbl, "text");
			return lbl;
		}

	}

	private File file;
	private static ArrayList<Path> openFiles = new ArrayList<Path>(5);

	private OdfTextDocument odt;
	private Style curStyle;

	private Composite comp;
	private org.eclipse.swt.widgets.Table filenames;
	private Label filenames_label;
	private Button open_button;
	private Button import_button;
	private static final String pluginID = "com.hilotec.elexis.opendocument";
	private static final String NoFileOpen = "Keine Datei geöffnet";
	private static Path tempPath = null;
	private final Logger logger = LoggerFactory.getLogger(pluginID);
	private Boolean only_one_doc_open = true;
	private static Integer file_counter = 0;

	/**
	 * Creates a human readable filename inside our temp directory, containing the name of the
	 * patient, the date of the letter and its title. All non word characters are replaced by an
	 * underscore.
	 *
	 * @return full path name
	 */
	private String fileNameFromBrief(){
		StringBuffer sb = new StringBuffer();
		Patient actPatient = ElexisEventDispatcher.getSelectedPatient();
		sb.append(actPatient.getVorname() + "_");
		sb.append(actPatient.getName() + "_");
		Brief actBrief = (Brief) ElexisEventDispatcher.getSelected(Brief.class);
		if (actBrief != null)
			sb.append(actBrief.getLabel()); // contains datum
		file_counter += 1; // append a unique part
		sb.append("_" + file_counter);
		return sb.toString().replaceAll("[^0-9A-Za-z]", "_");
	}

	private synchronized void odtSync(){
		if (file == null || odt == null) {
			return;
		}

		try {
			logger.info("odtSync: saving in " + file.getAbsolutePath());
			odt.save(file);
			logger.info("odtSync: completed " + file.length() + " saved in "
				+ file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Atomic handling for the list of open files. This is needed, as for each open ODF file we have
	 * a corresponding thread and must avoid race-conditions. Updates also the table with the list
	 * of files.
	 *
	 * @param add
	 *            if true add it to the openFiles, else remove it
	 * @param path
	 *            if null, don't change the openFiles else add or remove it from the openFiles
	 * @return false, if add and the path is already open
	 */
	private synchronized boolean updateOpenFiles(boolean add, Path path){
		if (path != null) {
			if (add) {
				if (openFiles.contains(path)) {
					return false; // this should never occur!
				}
				openFiles.add(path);
			} else {
				openFiles.remove(path);
			}
			logger.info("updateOpenFiles: updated " + openFiles.size() + " filenames add " + add
				+ ": " + path.toString());
		}
		if (openFiles.size() == 0) {
			filenames_label.setText(NoFileOpen);
		} else if (openFiles.size() == 1) {
			filenames_label.setText(openFiles.size() + " offene Datei");
		} else {
			filenames_label.setText(openFiles.size() + " offene Dateien");
		}
		Font font = filenames_label.getFont();
		FontData fontData = font.getFontData()[0];
		font = new Font(Display.getCurrent(), fontData.getName(), fontData.getHeight(), SWT.BOLD);
		filenames_label.setFont(font);
		int j;
		for (j = 0; j < openFiles.size() + 2; j++) {
			if (openFiles.size() > j) {
				if (filenames.getItemCount() <= j) {
					new TableItem(filenames, 0);
				}
				filenames.getItem(j).setText(openFiles.get(j).getFileName().toString());
			} else if (filenames.getItemCount() > j)
				filenames.remove(j);
		}
		return true;
	}

	/**
	 * Handles editing and printing OpenDocument files. For each file a new thread is started and
	 * after the editing finished we save the generated file in the extinfo of the corresponding
	 * letter (Brief).
	 *
	 * @param file
	 *            the ODF file to be edited or printed
	 * @param printed
	 *            if true the file will be printed, else edited
	 * @param toPrinter
	 *            TODO: used to select the printer if printed == true at the moment ignored
	 * @param wait
	 *            at the moment ignored
	 * @return false if add and the file is already open or setup not correct
	 */
	private boolean runOpenoffice(final File file, final boolean printIt, final String toPrinter,
		final String toTray, final boolean wait){
		if (file == null || odt == null) {
			return false;
		}
		final String editor = CoreHub.localCfg.get(Preferences.P_EDITOR, "oowriter");
		if (editor.length() == 0) {
			SWTHelper.showError("Kein Editor gesetzt",
				"In den Einstellungen wurde kein Editor konfiguriert.");
			return false;
		}
		final Path path = Paths.get(file.getAbsolutePath());
		if (updateOpenFiles(true, path) == false) {
			logger.info("Hilotec-ODF: alreadyOpened: " + path);
			SWTHelper.showError("Hilotec-ODF: Datei schon offen", "Die Datei " + path
				+ " ist schon geöffnet!");
			return false;
		}

		if (printIt)
			file.setWritable(false);
		file.deleteOnExit(); // TODO: can this lead to problems when the document is still open in LibreOffice?
		try {
			logger.error("runOpenoffice: file is " + path + " " + Files.size(path) + " bytes.");
		} catch (IOException e1) {}
		String argstr = "";
		odtSync();
		odt.close();
		odt = null;
		if (printIt) {
			argstr = CoreHub.localCfg.get(Preferences.P_PRINTARGS, "");
			argstr = editor + "\n" + argstr + "\n" + path;
		} else {
			argstr = CoreHub.localCfg.get(Preferences.P_EDITARGS, "");
			String baseName = "open_odf.sh";
			if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
				baseName = "open_odf.bat";
			String scriptFile =
				PlatformHelper.getBasePath(pluginID) + File.separator + "rsc" + File.separator
					+ baseName;
			File scriptShell = new File(scriptFile);
			if (!scriptShell.canExecute())
				scriptShell.setExecutable(true);
			argstr = scriptFile + "\n" + editor + "\n" + argstr + "\n" + path.toString();
		}
		logger.info("runOpenoffice: run " + argstr);
		final String process_args[] = argstr.split("\n");
		UiDesk.asyncExec(new Runnable() {
			@Override
			public void run(){
				ProcessBuilder pb = new ProcessBuilder(process_args);
				try {
					final Process editor_process;
					editor_process = pb.start();
					odt = null;
					(new Thread() {
						@Override
						public void run(){
							try {
								editor_process.waitFor();
								if (printIt) {
									logger.info("runOpenoffice: printing done. exitValue "
										+ editor_process.exitValue() + " done " + path);
								} else {
									logger.info("runOpenoffice: exitValue "
										+ editor_process.exitValue() + " file is " + path + " "
										+ Files.size(path) + " bytes.");
									odt =
										(OdfTextDocument) OdfTextDocument.loadDocument(path
											.toString());
									odtToByteArray(odt);
								}
								File f = new File(path.toString());
								f.delete();
								logger.info("runOpenoffice: completed successfully");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run(){
										updateOpenFiles(false, path);
									}
								});
							}
						}
					}).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		return true;
	}

	@Override
	public boolean print(String toPrinter, String toTray, boolean wait){
		return runOpenoffice(file, true, toPrinter, toTray, wait);
	}

	private void openEditor(){
		runOpenoffice(file, false, null, null, false);
	}

	private void importFile(){
		if (file == null) {
			return;
		}

		odtSync();

		FileDialog fd = new FileDialog(UiDesk.getTopShell(), SWT.OPEN);
		fd.setFilterExtensions(new String[] {
			"*.odt"
		});
		String path = fd.open();

		if (path != null) {
			try {
				logger.info("importFile: " + path);
				OdfTextDocument ndoc = (OdfTextDocument) OdfTextDocument.loadDocument(path);
				if (ndoc != null) {
					odt = ndoc;
				}
				odtSync();

			} catch (Exception e) {
				SWTHelper.showError("Fehler beim Import", e.getMessage());
			}
		}
	}

	@Override
	public Composite createContainer(Composite parent, ICallback handler){
		logger.info("createContainer: ");
		if (comp == null) {
			comp = new Composite(parent, SWT.NONE);
			RowLayout layout = new RowLayout(SWT.VERTICAL);
			layout.wrap = true;
			layout.fill = false;
			layout.justify = false;
			comp.setLayout(layout);

			RowData data = new RowData();
			data.width = 400;
			Label header = new Label(comp, SWT.PUSH);
			header.setText("Hilotec OpenDocumentFormat Dateien");
			Font font = header.getFont();
			FontData fontData = font.getFontData()[0];
			font =
				new Font(Display.getCurrent(), fontData.getName(), fontData.getHeight() + 2,
					SWT.BOLD);
			header.setFont(font);
			data = new RowData();

			open_button = new Button(comp, SWT.PUSH);
			open_button.setText("Editor öffnen");
			open_button.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event){
					openEditor();
				}
			});
			data = new RowData();
			open_button.setLayoutData(data);
			import_button = new Button(comp, SWT.PUSH);
			import_button.setText("Datei importieren");
			import_button.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event){
					importFile();
				}
			});
			import_button.setLayoutData(data);

			data = new RowData();
			filenames_label = new Label(comp, SWT.PUSH);
			filenames_label.setLayoutData(data);

			data = new RowData();
			data.width = 800;
			filenames = new org.eclipse.swt.widgets.Table(comp, SWT.PUSH);
			filenames.setLayoutData(data);
			filenames.setBackground(filenames_label.getBackground());
			updateOpenFiles(true, null);
			comp.pack();
		}

		return comp;
	}

	@Override
	public void dispose(){
		logger.info("dispose: ");

	}

	@Override
	public boolean clear(){
		logger.info("clear: ");
		SWTHelper.showError("TODO", "TODO: clear()");
		return false;
	}

	@Override
	public boolean createEmptyDocument(){
		try {
			ensureTempDirectory();
			file = new File(tempPath.toString(), fileNameFromBrief() + ".odt");
			logger.info("createEmptyDocument: " + file.toString());
			file.deleteOnExit(); // TODO: can this lead to problems when the document is still open in LibreOffice?
			odt = OdfTextDocument.newTextDocument();
			odt.save(file);
			logger.info("createEmptyDocument: save done: " + file.toString());
		} catch (Exception e) {
			file = null;
			return false;
		}
		return true;
	}

	/**
	 * Convert a ODT file into a byte array suitable to be stored in an extinfo (Briefe)
	 *
	 * @param odt
	 * @return converted byte array
	 */
	private byte[] odtToByteArray(OdfTextDocument odt){
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			odt.save(stream);
			logger.info("odtToByteArray: completed " + stream.size() + " bytes");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return stream.toByteArray();
	}

	@Override
	public byte[] storeToByteArray(){
		if (file == null || odt == null) {
			return null;
		}
		if (openFiles.contains(Paths.get(file.getAbsolutePath()))) {
			// We ignore save commands which result by the activation of the view
			return null;
		}
		only_one_doc_open = CoreHub.localCfg.get(Preferences.P_ONLY_ONE_OPEN, true);
		if (only_one_doc_open && openFiles.size() > 0) {
			logger.info("storeToByteArray: only_one_doc_open, and already " + openFiles.size()
				+ " open file(s)");
			showOnlyOneOpenFileAllowed();
			return null;
		}

		byte[] bytes = odtToByteArray(odt);
		if (bytes != null) {
			openEditor();
			logger.info("storeToByteArray: " + file.getAbsolutePath() + " returns " + bytes.length
				+ " bytes");
		}
		return bytes;
	}

	@Override
	public boolean loadFromByteArray(byte[] bs, boolean asTemplate){
		logger.info("loadFromByteArray: asTemplate " + asTemplate);
		ByteArrayInputStream stream = new ByteArrayInputStream(bs);
		return loadFromStream(stream, asTemplate);
	}

	private void ensureTempDirectory(){
		if (tempPath == null || !Files.isWritable(tempPath))
			try {
				tempPath = java.nio.file.Files.createTempDirectory("hilotec_odf");
				File file = new File(tempPath.toString());
				file.deleteOnExit(); // TODO: can this lead to problems when the document is still open in LibreOffice?
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void showOnlyOneOpenFileAllowed(){
		SWTHelper.showError("Nur eine offene Datei erlaubt",
			"Gemäss Einstellungen darf nur eine Datei offen sein!");
	}

	@Override
	public boolean loadFromStream(InputStream is, boolean asTemplate){
		logger.info("loadFromStream: " + (file != null));
		try {
			ensureTempDirectory();
			file = new File(tempPath.toString(), fileNameFromBrief() + ".odt");
			logger.info("loadFromStream: " + file.toString() + " already open "
				+ openFiles.contains(Paths.get(file.getAbsolutePath())));
			if (openFiles.contains(Paths.get(file.getAbsolutePath()))) {
				// We ignore save commands which result by the activation of the view
				return false;
			}
			only_one_doc_open = CoreHub.localCfg.get(Preferences.P_ONLY_ONE_OPEN, true);
			if (only_one_doc_open && openFiles.size() > 0) {
				logger.info("loadFromStream: only_one_doc_open, and already " + openFiles.size()
					+ " open file(s)");
				showOnlyOneOpenFileAllowed();
				return false;
			}
			file.deleteOnExit(); // TODO: can this lead to problems when the document is still open in LibreOffice?
			odt = (OdfTextDocument) OdfDocument.loadDocument(is);
			odt.save(file);
			logger.info("loadFromStream: saved (but not yet converted) " + file.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("loadFromStream: loading document failed ");
			SWTHelper.alert("Fehler beim Laden",
				"Das Dokument konnte nicht geladen werden. Meldung war: " + e.getMessage());
			file = null;
			return false;
		}

		return true;
	}

	/**
	 * Ersetzt Tabs in einem Text-Node
	 *
	 * @see formatText
	 *
	 * @return Letzter enstandener Knoten
	 */
	private Text replaceTabs(OdfFileDom dom, Text text){
		Node parent = text.getParentNode();
		Text cur = text;
		int i;

		while ((i = cur.getTextContent().indexOf('\t')) >= 0) {
			Text next = cur.splitText(i);
			next.setTextContent(next.getTextContent().substring(1));

			OdfTextTab tab = (OdfTextTab) OdfXMLFactory.newOdfElement(dom, OdfTextTab.ELEMENT_NAME);
			parent.insertBefore(tab, next);
			cur = next;
		}

		return cur;
	}

	/**
	 * Text-Node formatieren. Dabei werden Newlines und Tabs ersetzt. Der Knoten kann unter
	 * Umstaenden aufgespalten werden. {text} entspricht in dem Fall dem ersten Stueck.
	 *
	 * @return Letzter Knoten, der beim aufsplitten entstanden ist
	 * @throws Exception
	 */
	private Text formatText(OdfFileDom dom, Text text) throws Exception{
		Node parent = text.getParentNode();
		Text cur = text;
		int i;

		// XXX: Hack fuer Text unterstrichen darzustellen
		String textContent = text.getTextContent();
		if (textContent.startsWith("_") && textContent.endsWith("_")) {
			// _ Pre/Suffix entfernen
			text.setTextContent(textContent.substring(1, textContent.length() - 1));

			if (parent instanceof OdfStylableElement) {
				OdfStylableElement stel = (OdfStylableElement) parent;
				OdfFileDom contentDom = odt.getContentDom();

				// Neuen Stil erstellen mit unterstrichen aktiviert
				OdfStyle newst = createNewStyle("ul_", contentDom, odt.getStylesDom());
				newst.setStyleFamilyAttribute("paragraph");
				OdfStyleTextProperties stp =
					(OdfStyleTextProperties) OdfXMLFactory.newOdfElement(contentDom,
						StyleTextPropertiesElement.ELEMENT_NAME);
				stp.setStyleTextUnderlineStyleAttribute("solid");
				stp.setStyleTextUnderlineWidthAttribute("auto");
				stp.setStyleTextUnderlineColorAttribute("font-color");
				newst.appendChild(stp);

				// Originalstil als parent-Stil
				// FIXME: Funktioniert so nicht wie gewuenscht, die
				// style:text-properties muessten aus dem Elternstil kopiert
				// werden.
				String oldst = stel.getStyleName();
				if (oldst != null && !oldst.isEmpty())
					newst.setStyleParentStyleNameAttribute(oldst);

				stel.setStyleName(newst.getStyleNameAttribute());
			}
		}

		while ((i = cur.getTextContent().indexOf('\n')) >= 0) {
			Text next = cur.splitText(i);
			next.setTextContent(next.getTextContent().substring(1));

			OdfTextLineBreak lbrk =
				(OdfTextLineBreak) OdfXMLFactory.newOdfElement(dom, OdfTextLineBreak.ELEMENT_NAME);
			parent.insertBefore(lbrk, next);

			replaceTabs(dom, cur);
			cur = next;
		}

		return replaceTabs(dom, cur);
	}

	private boolean searchNode(Node n, Pattern pat, List<Text> matches, boolean onlyFirst){
		boolean result = false;
		Node child = n.getFirstChild();
		while (child != null) {
			if (child instanceof Text) {
				Text bit = (Text) child;
				String content = bit.getTextContent();
				Matcher m = pat.matcher(content);

				if (m.find()) {
					int start = m.start();
					int end = m.end();

					// Wenn noetig fuehrendes Stueck abschneiden
					if (start != 0) {
						bit = bit.splitText(start);
						end -= start;
					}

					// Wenn noetig nachfolgendes Stueck abschneiden
					if (end != bit.getTextContent().length()) {
						bit.splitText(end);
					}

					result = true;
					matches.add(bit);
					child = bit;

					if (onlyFirst) {
						return true;
					}
				}
			}
			child = child.getNextSibling();
		}

		return result;
	}

	/**
	 * Text-Nodes finden deren Inhalt das uebergebene Pattern matcht. Dabei werden die Folgenden
	 * Elementtypen durchsucht: - text:p - text:span Es koennen vorhandene Text-Knoten aufgespalten
	 * werden.
	 */
	private List<Text> findTextNode(OdfFileDom dom, XPath xpath, Pattern pat, boolean onlyFirst)
		throws Exception{
		List<Text> result = new ArrayList<Text>();

		String types[] = {
			"//text:p", "//text:span"
		};
		for (String t : types) {
			NodeList bits = (NodeList) xpath.evaluate(t, dom, XPathConstants.NODESET);
			for (int i = 0; i < bits.getLength(); i++) {
				Node n = bits.item(i);
				if (searchNode(n, pat, result, onlyFirst) && onlyFirst) {
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * Tabelle erstellen, an der stelle an der match steht. match wird aus dem Dokument entfernt.
	 *
	 * Bei den Breiten werden Prozentwerte erwartet, oder null, falls die Breite auf alle Spalten
	 * gleichmaessig verteilt werden soll.
	 */
	private void makeTableAt(OdfFileDom dom, Text match, String[][] content, int[] widths)
		throws Exception{
		Node tableParent = match.getParentNode();

		// Find Parent-Node for table
		while (tableParent instanceof TextSpanElement) {
			tableParent = tableParent.getParentNode();
		}
		Node before = tableParent;
		tableParent = tableParent.getParentNode();

		// Create table
		TableTableElement table =
			(TableTableElement) OdfXMLFactory.newOdfElement(dom, TableTableElement.ELEMENT_NAME);
		tableParent.insertBefore(table, before);

		// Remove reference node
		// FIXME: There is probably a better solution
		before.getParentNode().removeChild(before);

		// Initialize columns
		if (content.length == 0) {
			return;
		}
		int colcount = 0;
		for (String[] row : content) {
			colcount = Math.max(colcount, row.length);
		}
		if (widths == null) {
			// Create a column declaration for all columns
			TableTableColumnElement ttc =
				(TableTableColumnElement) OdfXMLFactory.newOdfElement(dom,
					TableTableColumnElement.ELEMENT_NAME);
			ttc.setTableNumberColumnsRepeatedAttribute(colcount);
			table.appendChild(ttc);
		} else {
			float percentval = 65535f / 100f;

			for (int i = 0; i < widths.length; i++) {
				// Create Style for this column
				OdfStyle cst = createNewStyle("col", odt.getContentDom(), odt.getStylesDom());
				String stname = cst.getStyleNameAttribute();
				cst.setStyleFamilyAttribute("table-column");

				OdfStyleTableColumnProperties stcp =
					(OdfStyleTableColumnProperties) OdfXMLFactory.newOdfElement(dom,
						StyleTableColumnPropertiesElement.ELEMENT_NAME);
				stcp.setStyleRelColumnWidthAttribute(Integer
					.toString((int) (widths[i] * percentval)));
				cst.appendChild(stcp);

				// Create Column declaration for this column
				TableTableColumnElement ttc =
					(TableTableColumnElement) OdfXMLFactory.newOdfElement(dom,
						TableTableColumnElement.ELEMENT_NAME);
				ttc.setStyleName(stname);
				table.appendChild(ttc);
			}
		}

		for (String[] row : content) {
			// Create row
			TableTableRowElement ttre = table.newTableTableRowElement();
			table.appendChild(ttre);
			for (int i = 0; i < row.length; i++) {
				String col = row[i];
				boolean last = (i == row.length - 1);

				if (col == null) {
					col = "";
				}

				// Create cell
				TableTableCellElement ttce = ttre.newTableTableCellElement();
				ttce.setOfficeValueTypeAttribute("string");
				ttre.appendChild(ttce);

				// If this is the last column, and we don't have values for all
				// columns, we need to set colspan.
				if (last && row.length < colcount) {
					ttce.setTableNumberColumnsSpannedAttribute(colcount - i);
				}

				TextPElement tp =
					(TextPElement) OdfXMLFactory.newOdfElement(dom, TextPElement.ELEMENT_NAME);
				tp.setStyleName(curStyle + "_pg");
				tp.setTextContent(col);
				ttce.appendChild(tp);

				// Format cell content
				Text t = (Text) tp.getFirstChild();
				if (t != null) {
					formatText(dom, t);
				}
			}
		}

	}

	/**
	 * Tabelle in der sich der angegebene Platzhalter befindet befuellen. Dafuer wird die
	 * Vorlagezeile 1:1 kopiert (insbesondere werden styles und breiten uebernommen) und die spalten
	 * vom Plathalter an werden alle mit dem Inhalt aus content ueberschrieben. Spalten vor dem
	 * angegebenen Platzhalter und ueberschuessige danach werden 1:1 kopiert.
	 */
	private void fillTableAt(OdfFileDom dom, Text match, String[][] content) throws Exception{
		Node cellNode = match.getParentNode();
		TableTableRowElement row;
		TableTableCellElement cell;
		int cellIndex = 0;

		// Find row-node
		while (!(cellNode instanceof TableTableCellElement)) {
			cellNode = cellNode.getParentNode();
		}
		cell = (TableTableCellElement) cellNode;
		row = (TableTableRowElement) cell.getParentNode();
		Node parent = row.getParentNode();

		// Zellenindex ausfindig machen
		NodeList cellList = row.getChildNodes();
		for (int i = 0; i < cellList.getLength(); i++) {
			Node n = cellList.item(i);
			if (!(n instanceof TableTableCellElement))
				continue;
			if (n == cell)
				break;
			cellIndex++;
		}

		for (String[] rData : content) {
			if (rData == null)
				continue;
			TableTableRowElement r = (TableTableRowElement) row.cloneNode(true);

			// Durch spalten und anderen Inhalt iterieren und entsprechende
			// Zellen befuellen.
			int i = 0;
			NodeList nl = r.getChildNodes();
			for (int j = 0; j < nl.getLength(); j++) {
				Node n = nl.item(j);
				if (!(n instanceof TableTableCellElement))
					continue;
				TableTableCellElement c = (TableTableCellElement) n;
				if (i >= cellIndex && (i - cellIndex + 1 <= rData.length)) {
					// FIXME
					TextPElement pe = (TextPElement) c.getChildNodes().item(0);

					pe.setTextContent(StringTool.unNull(rData[i - cellIndex]));
					Text t = (Text) pe.getFirstChild();
					if (t != null) {
						formatText(dom, t);
					}
				}
				i++;
			}
			parent.insertBefore(r, row);
		}

		parent.removeChild(row);
	}

	private void replaceTableFills(OdfFileDom dom, XPath xpath, Pattern pat, boolean onlyFirst,
		ReplaceCallback cb) throws Exception{
		String spat = pat.pattern();
		spat = spat.replaceAll("\\\\\\[", "\\\\{");
		spat = spat.replaceAll("\\\\\\]", "\\\\}");
		Pattern npat = Pattern.compile(spat);

		List<Text> matches = findTextNode(dom, xpath, npat, onlyFirst);
		for (Text match : matches) {
			String text = match.getTextContent().replaceAll("\\{", "[").replaceAll("\\}", "]");
			Object replacement = cb.replace(text);
			if (replacement instanceof String[][]) {
				try {
					fillTableAt(dom, match, (String[][]) replacement);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private int findOrReplaceIn(OdfFileDom dom, Pattern pat, ReplaceCallback cb, XPath xpath)
		throws Exception{

		replaceTableFills(dom, xpath, pat, false, cb);
		List<Text> matches = findTextNode(dom, xpath, pat, false);

		for (Text match : matches) {
			String text = match.getTextContent();
			Object replacement = cb.replace(text);
			String replstr;

			if (replacement == null) {} else if (replacement instanceof String) {
				replstr = (String) replacement;
				if (replstr.compareTo(text) != 0) {
					match.setTextContent(replstr);
					formatText(dom, match);
				}
			} else if (replacement instanceof String[][]) {
				try {
					makeTableAt(dom, match, (String[][]) replacement, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				replstr = "???Unbekannter Typ???";
				if (replstr.compareTo(text) != 0) {
					match.setTextContent(replstr);
				}
			}

		}

		return matches.size();
	}

	@Override
	public boolean findOrReplace(String pattern, ReplaceCallback cb){
		if (file == null) {
			return false;
		}

		int count = 0;
		try {
			Pattern pat = Pattern.compile(pattern);
			OdfFileDom contentDom = odt.getContentDom();
			OdfFileDom styleDom = odt.getStylesDom();
			XPath xpath = odt.getXPath();

			count += findOrReplaceIn(contentDom, pat, cb, xpath);
			count += findOrReplaceIn(styleDom, pat, cb, xpath);

			odtSync();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return count > 0;
	}

	@Override
	public boolean insertTable(String place, int properties, String[][] contents, int[] columnSizes){
		if (file == null) {
			return false;
		}

		try {
			OdfFileDom contentDom = odt.getContentDom();
			XPath xpath = odt.getXPath();

			List<Text> texts =
				findTextNode(contentDom, xpath, Pattern.compile(Pattern.quote(place)), true);

			if (texts.size() == 0) {
				return false;
			}

			Text txt = texts.get(0);
			makeTableAt(contentDom, txt, contents, columnSizes);

			// TODO: Style
			odtSync();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Object insertText(String marke, String text, int adjust){
		if (file == null) {
			return null;
		}

		// System.out.println("insertText('" + marke + "', '" + text + "')");
		try {
			OdfFileDom contentDom = odt.getContentDom();
			XPath xpath = odt.getXPath();

			List<Text> texts =
				findTextNode(contentDom, xpath, Pattern.compile(Pattern.quote(marke)), true);

			if (texts.size() == 0) {
				return null;
			}

			Text txt = texts.get(0);
			txt.setTextContent(text);
			txt = formatText(contentDom, txt);

			// TODO: Style
			odtSync();
			return txt;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object insertText(Object pos, String text, int adjust){
		if (file == null || pos == null) {
			return null;
		}

		// System.out.println("insertText2('" + text + "')");
		try {
			OdfFileDom contentDom = odt.getContentDom();
			Text prev = (Text) pos;

			curStyle.setAlign(adjust);

			TextSpanElement span =
				(TextSpanElement) OdfXMLFactory.newOdfElement(contentDom,
					TextSpanElement.ELEMENT_NAME);
			span.setTextContent(text);
			span.setStyleName(curStyle.getTextLbl());

			int i;
			Text txt = prev;
			for (i = 0; i < span.getChildNodes().getLength(); i++) {
				Node n = span.getChildNodes().item(i);
				if (n instanceof Text) {
					txt = (Text) n;
					formatText(contentDom, txt);
				}
			}
			prev.getParentNode().insertBefore(span, prev.getNextSibling());
			curStyle.clearAlign();
			return txt;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Finde einen nicht benutzten Style-Namen der mit prefix beginnt, und mit einer beliebigen Zahl
	 * endet.
	 */
	private String generateStyleName(String prefix, OdfFileDom contentDom, OdfFileDom styleDom,
		XPath xpath){
		NodeList nl;

		// TODO: Muesste sich doch in konstanter Zeit machen lassen. ;-)

		for (int i = 0;; i++) {
			String cur = prefix + i;

			String xp = "//*[@style:name='" + cur + "']";
			try {
				nl = (NodeList) xpath.evaluate(xp, contentDom, XPathConstants.NODESET);
				if (nl.getLength() > 0) {
					continue;
				}

				nl = (NodeList) xpath.evaluate(xp, styleDom, XPathConstants.NODESET);
				if (nl.getLength() > 0) {
					continue;
				}

				return cur;
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private OdfStyle createNewStyle(String prefix, OdfFileDom contentDom, OdfFileDom styleDom)
		throws Exception{
		XPath xpath = odt.getXPath();

		String name = generateStyleName(prefix, contentDom, styleDom, xpath);
		OdfOfficeAutomaticStyles autost =
			(OdfOfficeAutomaticStyles) xpath.evaluate("//office:automatic-styles", contentDom,
				XPathConstants.NODE);

		OdfStyle style =
			(OdfStyle) OdfXMLFactory.newOdfElement(contentDom, StyleStyleElement.ELEMENT_NAME);
		style.setStyleNameAttribute(name);
		autost.appendChild(style);

		return style;
	}

	@Override
	public Object insertTextAt(int x, int y, int w, int h, String text, int adjust){
		if (file == null) {
			return null;
		}

		try {
			OdfFileDom contentDom = odt.getContentDom();
			OdfFileDom styleDom = odt.getStylesDom();
			XPath xpath = odt.getXPath();

			curStyle.setAlign(adjust);

			// Generate Styles
			OdfStyle frst = createNewStyle("fr", contentDom, styleDom);
			String frstyle = frst.getStyleNameAttribute();
			frst.setStyleFamilyAttribute("graphic");
			frst.setStyleParentStyleNameAttribute("Frame");

			OdfStyleGraphicProperties gsp =
				(OdfStyleGraphicProperties) OdfXMLFactory.newOdfElement(contentDom,
					StyleGraphicPropertiesElement.ELEMENT_NAME);
			gsp.setStyleRunThroughAttribute("foreground");
			gsp.setStyleWrapAttribute("dynamic");
			gsp.setStyleNumberWrappedParagraphsAttribute("no-limit");
			gsp.setStyleVerticalPosAttribute("from-top");
			gsp.setStyleVerticalRelAttribute("page-content");
			gsp.setStyleHorizontalPosAttribute("from-left");
			gsp.setStyleHorizontalRelAttribute("page-content");
			gsp.setStyleBackgroundTransparencyAttribute("100%");
			gsp.setStyleShadowAttribute("none");
			// Strange, if transparent is chosen OO3 doesent display it
			// transparently
			gsp.setFoBackgroundColorAttribute("#ffffff");
			gsp.setFoPaddingAttribute("0cm");
			gsp.setFoBorderAttribute("none");
			frst.appendChild(gsp);

			OdfStyleBackgroundImage bgimg =
				(OdfStyleBackgroundImage) OdfXMLFactory.newOdfElement(contentDom,
					StyleBackgroundImageElement.ELEMENT_NAME);
			gsp.appendChild(bgimg);

			OdfStyleColumns scols =
				(OdfStyleColumns) OdfXMLFactory.newOdfElement(contentDom,
					StyleColumnsElement.ELEMENT_NAME);
			scols.setFoColumnCountAttribute(1);
			scols.setFoColumnGapAttribute("0cm");
			gsp.appendChild(scols);

			// Generate Content
			OdfOfficeText officeText =
				(OdfOfficeText) xpath.evaluate("//office:text", contentDom, XPathConstants.NODE);

			OdfDrawFrame frame =
				(OdfDrawFrame) OdfXMLFactory.newOdfElement(contentDom,
					DrawFrameElement.ELEMENT_NAME);
			frame.setSvgXAttribute(x + "mm");
			frame.setSvgYAttribute(y + "mm");
			frame.setSvgWidthAttribute(w + "mm");
			// FIXME: Unschoener Workaround fuer platzproblem bei
			// Einzahlungsschein
			frame.setSvgHeightAttribute((h + 1) + "mm");
			frame.setTextAnchorTypeAttribute("page");
			frame.setTextAnchorPageNumberAttribute(1);
			frame.setDrawZIndexAttribute(0);
			frame.setDrawStyleNameAttribute(frstyle);
			frame.setDrawNameAttribute("Frame" + frstyle);
			officeText.insertBefore(frame, officeText.getFirstChild());

			OdfDrawTextBox textbox =
				(OdfDrawTextBox) OdfXMLFactory.newOdfElement(contentDom,
					DrawTextBoxElement.ELEMENT_NAME);
			frame.appendChild(textbox);

			OdfTextParagraph para =
				(OdfTextParagraph) OdfXMLFactory.newOdfElement(contentDom,
					TextPElement.ELEMENT_NAME);
			para.setTextContent(text);
			para.setStyleName(curStyle.getParagraphLbl());
			textbox.appendChild(para);

			// TODO: Sauber?
			Text txt = (Text) para.getChildNodes().item(0);
			formatText(contentDom, txt);

			curStyle.clearAlign();

			odtSync();
			return txt;
		} catch (Exception e) {
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PageFormat getFormat(){
		// System.out.println("getFormat()");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMimeType(){
		return "application/vnd.oasis.opendocument.text";
	}

	@Override
	public void setFocus(){
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setFont(String name, int style, float size){
		if (file == null) {
			return false;
		}

		curStyle.setFont(name, style, size);
		return true;
	}

	@Override
	public void setFormat(PageFormat f){
		// System.out.println("setFormat");
		// TODO Auto-generated method stub

	}

	@Override
	public void setSaveOnFocusLost(boolean bSave){
		System.out.println("setSaveOnFocusLost");
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setStyle(int style){
		curStyle.setStyle(style);
		return true;
	}

	@Override
	public void showMenu(boolean b){
		// TODO Auto-generated method stub

	}

	@Override
	public void showToolbar(boolean b){
		// TODO Auto-generated method stub

	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
		throws CoreException{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDirectOutput(){
		// TODO: Make sure that false is what we want here...
		return false;
	}

	@Override
	public void setParameter(Parameter parameter){
		// TODO Auto-generated method stub

	}

}
