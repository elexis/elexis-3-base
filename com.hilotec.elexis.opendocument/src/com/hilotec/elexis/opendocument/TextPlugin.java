package com.hilotec.elexis.opendocument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.data.Patient;
import ch.elexis.text.ITextPlugin;
import ch.elexis.text.ReplaceCallback;
import ch.elexis.util.PlatformHelper;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.StringTool;

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
	
	private Process editor_process;
	
	private File file;
	
	private OdfTextDocument odt;
	private Style curStyle;
	
	private Composite comp;
	private Label filename_label;
	private Button open_button;
	private Button import_button;
	private static final String pluginID = "com.hilotec.elexis.opendocument";
	private static final String NoFileOpen = "Dateiname: Keine Datei geöffnet";
	
	private Logger logger = LoggerFactory.getLogger(pluginID);
	static int cnt = 0;
	
	private String getTempPrefix(){
		cnt += 1;
		StringBuffer sb = new StringBuffer();
		Patient actPatient = ElexisEventDispatcher.getSelectedPatient();
		sb.append(cnt + "_" + actPatient.getName() + "_");
		sb.append(actPatient.getVorname() + "_");
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_S");
		Date date = new Date();
		sb.append(dateFormat.format(date));
		return sb.toString();
	}
	
	private boolean editorRunning(){
		if (editor_process == null)
			return false;
		try {
			int exitValue = editor_process.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}
	
	private synchronized void odtSync(){
		if (file == null || odt == null || editorRunning()) {
			return;
		}
		
		try {
			odt.save(file);
			logger.info("odtSync: completed " + file.length() + " saved");
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		}
	}
	
	/**
	 * Sicherstellen dass kein Editor geoeffnet ist. Falls einer geoeffnet ist, wird eine
	 * Fehlermeldung mit einem entsprechenden Hinweis angezeigt.
	 * 
	 * @return True wenn keine Instanz mehr geoeffnet ist.
	 */
	private boolean ensureClosed(){
		Patient actPatient = ElexisEventDispatcher.getSelectedPatient();
		logger.info("ensureClosed: " + actPatient.getVorname() + " "
			+ actPatient.getName().toString());
		
		while (editorRunning()) {
			logger.info("Editor already opened file " + file.getAbsolutePath());
			SWTHelper
				.showError(
					"Editor bereits geöffnet",
					"Es scheint bereits ein Editor geöffnet zu sein für "
						+ file.getAbsolutePath()
						+ " geöffnet zu sein.\n\n"
						+ "Falls Sie sicher sind, dass kein Editor diese Datei mehr offen hat, müssen Sie Elexis neu starten.\n\n"
						+ "Falls Sie diese Warnung nicht beachten werden die in der Datei gemachten Änderungen nicht in der Elexis Datenbank gespeichert!");
			return false;
		}
		return true;
	}
	
	private void openEditor(){
		if (file == null || !ensureClosed()) {
			return;
		}
		
		odtSync();
		
		String editor = Hub.localCfg.get(Preferences.P_EDITOR, "");
		String argstr = Hub.localCfg.get(Preferences.P_EDITARGS, "");
		String baseName = "open_odf.sh";
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
			baseName = "open_odf.bat";
		String scriptFile =
			PlatformHelper.getBasePath(pluginID) + File.separator + "rsc" + File.separator
				+ baseName;
		logger.info(scriptFile);
		if (editor.length() == 0) {
			SWTHelper.showError("Kein Editor gesetzt",
				"In den Einstellungen wurde kein Editor konfiguriert.");
			return;
		}
		
		File scriptShell = new File(scriptFile);
		if (!scriptShell.canExecute())
			scriptShell.setExecutable(true);
		String args = (scriptFile + "\n" + editor + "\n" + argstr + "\n" + file.getAbsolutePath());
		Patient actPatient = ElexisEventDispatcher.getSelectedPatient();
		logger.info("openEditor: " + actPatient.getPersonalia() + "\n" + args);
		ProcessBuilder pb = new ProcessBuilder(args.split("\n"));
		filename_label.setText(file.getAbsolutePath());
		
		try {
			editor_process = pb.start();
			odt = null;
			(new Thread() {
				public void run(){
					try {
						editor_process.waitFor();
						odt = (OdfTextDocument) OdfTextDocument.loadDocument(file);
						logger.info("openEditor: exitValue " + editor_process.exitValue()
							+ " done " + file.getAbsolutePath());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						editor_process = null;
						Display.getDefault().asyncExec(new Runnable() {
							public void run(){
								filename_label.setText(NoFileOpen);
								logger.info("openEditor: updated filename_label");
							}
						});
					}
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void importFile(){
		if (file == null || !ensureClosed()) {
			return;
		}
		
		odtSync();
		
		FileDialog fd = new FileDialog(Desk.getTopShell(), SWT.OPEN);
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
					fileValid();
				}
				
				odtSync();
				
			} catch (Exception e) {
				SWTHelper.showError("Fehler beim Import", e.getMessage());
			}
		}
	}
	
	public boolean print(String toPrinter, String toTray, boolean wait){
		if (file == null || !ensureClosed()) {
			return false;
		}
		
		odtSync();
		String editor = Hub.localCfg.get(Preferences.P_EDITOR, "oowriter");
		String argstr = Hub.localCfg.get(Preferences.P_PRINTARGS, "");
		String args[] = (editor + "\n" + argstr + "\n" + file.getAbsolutePath()).split("\n");
		ProcessBuilder pb = new ProcessBuilder(args);
		
		try {
			logger.info("print: " + args);
			editor_process = pb.start();
			editor_process.waitFor();
			logger.info("print waitFor done: " + args);
			filename_label.setText(NoFileOpen);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			editor_process = null;
		}
		
		return true;
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
			filename_label = new Label(comp, SWT.PUSH);
			filename_label.setText(NoFileOpen);
			filename_label.setLayoutData(data);
			data.width = 400;
			open_button = new Button(comp, SWT.PUSH);
			open_button.setText("Editor öffnen");
			open_button.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event){
					openEditor();
				}
			});
			data = new RowData();
			open_button.setLayoutData(data);
			import_button = new Button(comp, SWT.PUSH);
			import_button.setText("Datei importieren");
			import_button.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event){
					importFile();
				}
			});
			import_button.setLayoutData(data);
			
			comp.pack();
			/* open_button.setEnabled(false); */
		}
		
		return comp;
	}
	
	private void fileValid(){
		open_button.setEnabled(true);
		curStyle = new Style();
	}
	
	@Override
	public void dispose(){
		logger.info("dispose: ");
		
	}
	
	private void closeFile(){
		// System.out.println("closeFile()");
		logger.info("closeFile: " + file.toString());
		odtSync();
		file.delete();
		file = null;
	}
	
	@Override
	public boolean clear(){
		logger.info("clear: ");
		SWTHelper.showError("TODO", "TODO: clear()");
		return false;
	}
	
	@Override
	public boolean createEmptyDocument(){
		logger.info("createEmptyDocument: ");
		if (!ensureClosed()) {
			return false;
		}
		
		if (file != null) {
			closeFile();
		}
		
		try {
			file = File.createTempFile(getTempPrefix(), ".odt");
			file.deleteOnExit();
			
			logger.info("createEmptyDocument: " + file.toString());
			odt = OdfTextDocument.newTextDocument();
			odt.save(file);
			fileValid();
			logger.info("createEmptyDocument: save done: " + file.toString());
		} catch (Exception e) {
			file = null;
			return false;
		}
		return true;
	}
	
	@Override
	public byte[] storeToByteArray(){
		logger.info("storeToByteArray: editorRunning() " + editorRunning() + " file: " + file
			+ " odt null " + (odt == null));
		if (file == null || odt == null || editorRunning()) {
			return null;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		if (stream == null)
			return null;
		try {
			odt.save(stream);
			logger.info("storeToByteArray: completed " + file.length() + " bytes will open editor");
			openEditor();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return stream.toByteArray();
	}
	
	@Override
	public boolean loadFromByteArray(byte[] bs, boolean asTemplate){
		logger.info("loadFromByteArray: asTemplate " + asTemplate);
		ByteArrayInputStream stream = new ByteArrayInputStream(bs);
		return loadFromStream(stream, asTemplate);
	}
	
	@Override
	public boolean loadFromStream(InputStream is, boolean asTemplate){
		logger.info("loadFromStream: " + (file != null));
		if (!ensureClosed()) {
			return false;
		}
		
		if (file != null) {
			closeFile();
		}
		
		try {
			file = File.createTempFile(getTempPrefix(), ".odt");
			logger.info("loadFromStream: " + file.toString());
			file.deleteOnExit();
			
			odt = (OdfTextDocument) OdfDocument.loadDocument(is);
			odt.save(file);
			fileValid();
			logger.info("loadFromStream: saved (but not yet converted) " + file.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("loadFromStream: loading document failed ");
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
		if (editorRunning() || file == null) {
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
		// System.out.println("insertTable()" + this.hashCode());
		if (!ensureClosed() || file == null) {
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
		if (!ensureClosed() || file == null) {
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
		if (!ensureClosed() || file == null || pos == null) {
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
		if (!ensureClosed() || file == null) {
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
		if (!ensureClosed() || file == null) {
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
		// System.out.println("setSaveOnFocusLost");
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
	
}
