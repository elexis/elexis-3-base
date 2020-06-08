/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.elexis.core.data.interfaces.text.ReplaceCallback;
import ch.elexis.core.ui.text.ITextPlugin;

/**
 * Class for searching and manipulating the content of a docx MS Word document.
 * 
 * @author thomashu
 * 
 */
public class DocxWordDocument {
	private static Logger logger = LoggerFactory.getLogger(DocxWordDocument.class);
	
	private File unzipedDirectory;
	
	// document part as dom
	protected HashMap<File, Document> documentsMap;
	// header part as dom
	protected HashMap<File, Document> headersMap;
	// footer part as dom
	protected HashMap<File, Document> footersMap;
	
	protected File settings;
	
	/**
	 * Create a DocxWordDocument for manipulating a docx file. It will be unzipped to a new
	 * directory with the same name as the file, in the same directory as the file.
	 * 
	 * @param file
	 *            directory with unzipped docx content
	 */
	public DocxWordDocument(File file){
		// Verify the the Word document exists and can be read
		if (file.exists() || file.canRead()) {
			String unzipedDirName =
				file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('.'));
			unzipedDirectory = new File(unzipedDirName);
			// when used with CommunicationFile the unzipedDirectory will stay the same
			if (unzipedDirectory.exists())
				ZipUtil.deleteRecursive(unzipedDirectory);
			unzipedDirectory.mkdir();
			ZipUtil.unzipToDirectory(file, unzipedDirectory);
			
			initializeMaps();
			
			logger.debug("Unzipped docx to " + unzipedDirName);
		} else {
			throw new IllegalArgumentException("The file " + file.getAbsolutePath() //$NON-NLS-1$
				+ " is not accessible."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Write zipped content in MS Word docx format to the FileOutputStream.
	 * 
	 * @param os
	 * @throws IOException
	 */
	public void writeTo(FileOutputStream os) throws IOException{
		ZipUtil.zipDirectory(unzipedDirectory, os);
	}
	
	protected Document getDomFromFile(File file){
		Document doc = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			
			dBuilder = dbFactory.newDocumentBuilder();
			
			doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		} catch (SAXException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return doc;
	}
	
	protected void writeDomToFile(Document dom, File file){
		try {
			Source source = new DOMSource(dom);
			Result result = new StreamResult(file);
			
			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException(e);
		} catch (TransformerException e) {
			throw new IllegalStateException(e);
		}
	}
	
	protected void initializeMaps(){
		List<File> files = getDocumentFiles();
		// add settings
		if (settings != null) {
			if (documentsMap == null)
				documentsMap = new HashMap<File, Document>();
			documentsMap.put(settings, getDomFromFile(settings));
		}
		for (File file : files) {
			if (documentsMap == null)
				documentsMap = new HashMap<File, Document>();
			documentsMap.put(file, getDomFromFile(file));
		}
		files = getHeaderFiles();
		for (File file : files) {
			if (headersMap == null)
				headersMap = new HashMap<File, Document>();
			headersMap.put(file, getDomFromFile(file));
		}
		files = getFooterFiles();
		for (File file : files) {
			if (footersMap == null)
				footersMap = new HashMap<File, Document>();
			footersMap.put(file, getDomFromFile(file));
		}
	}
	
	protected List<File> getDocumentFiles(){
		ArrayList<File> ret = new ArrayList<File>();
		ret.add(new File(unzipedDirectory.getAbsolutePath() + File.separator + "word" //$NON-NLS-1$
			+ File.separator + "document.xml")); //$NON-NLS-1$
		
		settings = new File(unzipedDirectory.getAbsolutePath() + File.separator + "word" //$NON-NLS-1$
			+ File.separator + "settings.xml");
		
		return ret;
	}
	
	protected List<File> getHeaderFiles(){
		ArrayList<File> ret = new ArrayList<File>();
		File dir = new File(unzipedDirectory.getAbsolutePath() + File.separator + "word"); //$NON-NLS-1$
		String[] headerFiles = dir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename){
				if (filename.startsWith("header")) { //$NON-NLS-1$
					return true;
				}
				return false;
			}
		});
		if (headerFiles != null) {
			for (int i = 0; i < headerFiles.length; i++) {
				ret.add(new File(unzipedDirectory.getAbsolutePath() + File.separator + "word" //$NON-NLS-1$
					+ File.separator + headerFiles[i]));
			}
		}
		return ret;
	}
	
	protected List<File> getFooterFiles(){
		ArrayList<File> ret = new ArrayList<File>();
		File dir = new File(unzipedDirectory.getAbsolutePath() + File.separator + "word"); //$NON-NLS-1$
		String[] headerFiles = dir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename){
				if (filename.startsWith("footer")) { //$NON-NLS-1$
					return true;
				}
				return false;
			}
		});
		if (headerFiles != null) {
			for (int i = 0; i < headerFiles.length; i++) {
				ret.add(new File(unzipedDirectory.getAbsolutePath() + File.separator + "word" //$NON-NLS-1$
					+ File.separator + headerFiles[i]));
			}
		}
		return ret;
	}
	
	protected List<String> initFindNextMatch(String pattern){
		ArrayList<String> ret = new ArrayList<String>();
		// header
		if (headersMap != null) {
			Collection<Document> headers = headersMap.values();
			for (Document document : headers) {
				ret.addAll(getRegexMatches(pattern, getTextOfDom(document)));
			}
		}
		// document
		if (documentsMap != null) {
			Collection<Document> documents = documentsMap.values();
			for (Document document : documents) {
				ret.addAll(getRegexMatches(pattern, getTextOfDom(document)));
			}
		}
		// footer
		if (footersMap != null) {
			Collection<Document> footers = footersMap.values();
			for (Document document : footers) {
				ret.addAll(getRegexMatches(pattern, getTextOfDom(document)));
			}
		}
		
		return ret;
	}
	
	protected List<String> getRegexMatches(String regex, String text){
		List<String> matches = new ArrayList<String>();
		// prepare the pattern add ? for non greedy matching
		Pattern regexPattern = Pattern.compile(getNonGreedyRegex(regex));
		
		if (text != null && !text.isEmpty()) {
			Matcher matcher = regexPattern.matcher(text);
			while (matcher.find()) {
				matches.add(matcher.group());
			}
		}
		return matches;
	}
	
	private String getNonGreedyRegex(String pattern){
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < pattern.length(); i++) {
			char c = pattern.charAt(i);
			sb.append(c);
			if (i == 0 && (c == '+' || c == '*')) {
				sb.append('?');
			} else if ((c == '+' || c == '*') && (pattern.charAt(i - 1) != '\\')) {
				sb.append('?');
			}
		}
		
		return sb.toString();
	}
	
	protected String getTextOfDom(Document dom){
		StringBuilder sb = new StringBuilder();
		// get all text elements ignoring their parents
		NodeList nList = dom.getElementsByTagName("w:t"); //$NON-NLS-1$
		
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			
			String value = nNode.getTextContent();
			if (value != null && !value.isEmpty()) {
				sb.append(value);
			}
		}
		return sb.toString();
	}
	
	public synchronized boolean findAndReplaceWithCallback(String pattern, ReplaceCallback cb){
		boolean found = false;
		// header
		if (headersMap != null) {
			Collection<Document> headers = headersMap.values();
			for (Document document : headers) {
				if (findAndReplaceAllWithCallback(document, pattern, cb)) {
					found = true;
					writeDomToFile(document);
				}
			}
		}
		// document
		if (documentsMap != null) {
			Collection<Document> documents = documentsMap.values();
			for (Document document : documents) {
				if (findAndReplaceAllWithCallback(document, pattern, cb)) {
					found = true;
					writeDomToFile(document);
				}
			}
		}
		// footer
		if (footersMap != null) {
			Collection<Document> footers = footersMap.values();
			for (Document document : footers) {
				if (findAndReplaceAllWithCallback(document, pattern, cb)) {
					found = true;
					writeDomToFile(document);
				}
			}
		}
		return found;
	}
	
	private void writeDomToFile(Document dom){
		try {
			TransformerFactory tff = TransformerFactory.newInstance();
			Transformer transformer;
			
			transformer = tff.newTransformer();
			DOMSource xmlSource = new DOMSource(dom);
			
			File matchingOutput = null;
			// look in document map
			Set<File> keys = documentsMap.keySet();
			for (File file : keys) {
				if (documentsMap.get(file) == dom) {
					matchingOutput = file;
					break;
				}
			}
			// look in header map
			if (matchingOutput == null && headersMap != null) {
				keys = headersMap.keySet();
				for (File file : keys) {
					if (headersMap.get(file) == dom) {
						matchingOutput = file;
						break;
					}
				}
			}
			// look in footer map
			if (matchingOutput == null && footersMap != null) {
				keys = footersMap.keySet();
				for (File file : keys) {
					if (footersMap.get(file) == dom) {
						matchingOutput = file;
						break;
					}
				}
			}
			
			if (matchingOutput != null) {
				StreamResult outputTarget = new StreamResult(matchingOutput);
				transformer.transform(xmlSource, outputTarget);
			} else {
				throw new IllegalStateException("Could not find output file for DOM"); //$NON-NLS-1$
			}
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException("Error on writing dom.", e); //$NON-NLS-1$
		} catch (TransformerException e) {
			throw new IllegalStateException("Error on writing dom.", e); //$NON-NLS-1$
		}
	}
	
	private boolean findAndReplaceAllWithCallback(Document dom, String pattern, ReplaceCallback cb){
		boolean found = false;
		
		List<DocxWordParagraph> paragraphs = getAllParagraphs(dom);
		for (DocxWordParagraph paragraph : paragraphs) {
			if (paragraph.findAndReplaceAll(pattern, cb))
				found = true;
		}
		return found;
	}
	
	public List<DocxWordParagraph> getAllParagraphs(Document dom){
		ArrayList<DocxWordParagraph> ret = new ArrayList<DocxWordParagraph>();
		// get all paragraph elements ignoring their parents
		List<Node> paragraphs =
			XMLUtil.getAllChildElementsByTagName(dom.getDocumentElement(), "w:p"); //$NON-NLS-1$
		for (Node paragraph : paragraphs) {
			ret.add(new DocxWordParagraph(paragraph));
		}
		return ret;
	}
	
	/**
	 * Get an iterator for all matches of the provided regular expression pattern in the documents
	 * header, content and footer. The regular expression is always used non greedy.
	 * 
	 * @param pattern
	 * @return
	 */
	public synchronized Iterator<String> getMatchesIterator(String pattern){
		List<String> matches = initFindNextMatch(pattern);
		return matches.iterator();
	}
	
	public synchronized boolean findAndInsertTable(String place, int properties,
		String[][] contents, int[] columnSizes){
		boolean found = false;
		
		// document
		if (documentsMap != null) {
			Collection<Document> documents = documentsMap.values();
			for (Document document : documents) {
				if (findAndInsertTable(document, place, properties, contents, columnSizes)) {
					found = true;
					writeDomToFile(document);
				}
			}
		}
		
		return found;
	}
	
	public void setReadOnly(boolean value){
		Document dom = documentsMap.get(settings);
		if (dom != null) {
			DocxWordSettings settingsDom = new DocxWordSettings(dom.getDocumentElement());
			if (value) {
				if (!settingsDom.isReadOnly()) {
					settingsDom.setReadOnly(true);
				}
				
			} else {
				if (settingsDom.isReadOnly()) {
					settingsDom.setReadOnly(false);
				}
			}
			writeDomToFile(dom, settings);
		}
	}
	
	public boolean findAndInsertTable(Document document, String place, int properties,
		String[][] contents, int[] columnSizes){
		
		boolean found = false;
		
		List<DocxWordParagraph> paragraphs = getAllParagraphs(document);
		for (DocxWordParagraph paragraph : paragraphs) {
			if (paragraph.contains(place)) {
				found = true;
				// insert the table instead of the paragraph
				DocxWordRunProperties rProp = null;
				DocxWordParagraphProperties pProp = paragraph.getProperties();
				if (pProp != null) {
					rProp = pProp.getRunProperties();
				}
				DocxWordTable table = paragraph.replaceWithTable();
				DocxWordTableProperties tProp = table.createProperties();
				DocxWordTableRow firstRow = null;
				tProp.setWidth(100);
				// create the content
				for (int rowIdx = 0; rowIdx < contents.length; rowIdx++) {
					DocxWordTableRow row = table.createRow();
					if (rowIdx == 0)
						firstRow = row;
					String[] columns = contents[rowIdx];
					for (int columnIdx = 0; columnIdx < columns.length; columnIdx++) {
						// create column with parameters
						DocxWordTableColumn column = row.createTableColumn();
						DocxWordTableColumnProperties cProp = column.createProperties();
						if (columnSizes != null)
							cProp.setWidth(columnSizes[columnIdx]);
						// create run for the text
						DocxWordRun cRun = column.createParagraph().createRun();
						// user properties of the paragraph
						if (rProp != null && rProp.properties != null)
							cRun.setProperties(rProp.getClone(true));
						cRun.createText().setText(columns[columnIdx]);
					}
				}
				// apply properties
				if ((properties & ITextPlugin.GRID_VISIBLE) > 0) {
					tProp.setAllBorders(1);
				}
				if ((properties & ITextPlugin.FIRST_ROW_IS_HEADER) > 0) {
					if (firstRow != null) {
						List<DocxWordTableColumn> firstColumns = firstRow.getColumns();
						for (DocxWordTableColumn column : firstColumns) {
							List<DocxWordRun> runs = column.getParagraph().getDirectChildRuns();
							for (DocxWordRun run : runs) {
								rProp = run.getProperties();
								if (rProp == null)
									rProp = run.createProperties();
								rProp.setBold(true);
							}
						}
					}
				}
			}
		}
		return found;
	}
}
