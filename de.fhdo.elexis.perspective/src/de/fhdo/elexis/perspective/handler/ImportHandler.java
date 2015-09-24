/*******************************************************************************
 * Copyright (c) 2011, fhdo and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bernhard Rimatzki, Thorsten Wagner, Pascal Proksch, Sven Lüttmann
		- initial implementation
 *    
 *******************************************************************************/
package de.fhdo.elexis.perspective.handler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.internal.registry.PerspectiveRegistry;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.views.IViewRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ch.elexis.core.ui.UiDesk;
import de.fhdo.elexis.Messages;
import de.fhdo.elexis.perspective.dialog.FixPerspectiveDialog;
import de.fhdo.elexis.perspective.model.FaultViewFix;

/**
 * Imports selected perspectives from given .xml files
 * 
 * This class pops up a FileDialog to select one or more stored perspectives to be restored An error
 * correction routine is provided if perspectives with the same name are tried to restore
 * 
 * @author Bernhard Rimatzki, Thorsten Wagner, Pascal Proksch, Sven Lüttmann
 * @version 1.0
 * 
 */

public class ImportHandler extends AbstractHandler implements IHandler {
	private static final String TAG_SHOW_VIEW_ACTION = "show_view_action";
	private static final String TAG_FAST_VIEWS = "fastViews";
	private static final String TAG_VIEW = "view";
	private static final String TAG_PAGE = "page";
	private static final String TAG_PRESENTATION = "presentation";
	private static final String TAG_PART = "part";
	private static final String TAG_DESCRIPTOR = "descriptor";
	private static final String ATTR_ID = "id";
	private static final String ATTR_CONTENT = "content";
	private static final String ATTR_ACTIVE_PAGE_ID = "activePageID";
	private static final String ATTR_LABEL = "label";
	
	@Override
	@SuppressWarnings("all")
	public Object execute(ExecutionEvent event) throws ExecutionException{
		
		IWorkbenchWindow mainWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		PerspectiveRegistry perspRegistry =
			(PerspectiveRegistry) WorkbenchPlugin.getDefault().getPerspectiveRegistry();
		String importMessage = ""; //$NON-NLS-1$
		
		//
		// Open a FileDialog to select the .xml files with stored perspectives
		// Only display .xml Files to select
		//
		FileDialog diag = new FileDialog(mainWindow.getShell(), SWT.MULTI);
		
		String[] filterNames = {
			"XML"};//$NON-NLS-1$
		String[] filterExtensions = {
			"*.xml"};//$NON-NLS-1$
		
		diag.setFilterNames(filterNames);
		diag.setFilterExtensions(filterExtensions);
		
		if (diag.open() == null)
			return null;
			
		// Since it is possible to select multiple perspectives to be restored we have to iterate
		// over the selected files
		for (String file : diag.getFileNames()) {
			String filename = diag.getFilterPath() + File.separator + file;
			FileReader reader;
			XMLMemento memento = null;
			
			try {
				validateAndFixXMLIfNeeded(new File(filename));
				reader = new FileReader(new File(filename));
				memento = XMLMemento.createReadRoot(reader);
				reader.close();
				PerspectiveDescriptor newPersp = new PerspectiveDescriptor(null, null, null);
				
				// Get the label and the ID of the stored perspective
				String label = memento.getChild(TAG_DESCRIPTOR).getString(ATTR_LABEL); //$NON-NLS-1$ //$NON-NLS-2$
				String id = memento.getChild(TAG_DESCRIPTOR).getString(ATTR_ID); //$NON-NLS-1$ //$NON-NLS-2$
				
				// Find the perspective by label within the preference store
				PerspectiveDescriptor pd =
					(PerspectiveDescriptor) perspRegistry.findPerspectiveWithLabel(label);
					
				String[] buttonLabels = {
					Messages.ImportHandler_Abort, Messages.ImportHandler_Overwrite,
					Messages.ImportHandler_Rename
				};
				
				while (pd != null) {
					// If pd != null the perspective is already present in the preference store
					// though we have to store it with a different name
					String notDeleted = "";//$NON-NLS-1$
					String dialogMessage =
						String.format(Messages.ImportHandler_Name_Import_Already_Exists, label);
					MessageDialog mesDiag = new MessageDialog(mainWindow.getShell(),
						Messages.ImportHandler_OverWrite_Perspective, null, dialogMessage, 0,
						buttonLabels, 0);
					int ergMesDiag = mesDiag.open();
					
					if (ergMesDiag == 0) {// Cancel was pressed
						return null;
					} else if (ergMesDiag == 1) { // Overwrite was pressed
						perspRegistry.deletePerspective(pd);
						PerspectiveDescriptor pd2 =
							(PerspectiveDescriptor) perspRegistry.findPerspectiveWithLabel(label);
							
						// If the perspective could not be deleted, the user have to choose another
						// name
						if (pd2 != null) {
							notDeleted = Messages.ImportHandler_Cannot_Overwrite_Perspective;
							ergMesDiag = 2;
						}
						
						// After the Perspective has been deleted the descriptor has to be null
						pd = null;
					}
					
					if (ergMesDiag == 2) { // Rename was pressed
						String dialogMessageOverride =
							notDeleted + Messages.ImportHandler_Choose_new_name_for_Perspective;
						InputDialog inputDiag = new InputDialog(mainWindow.getShell(),
							Messages.ImportHandler_Rename_Perspective, dialogMessageOverride, null,
							null);
							
						inputDiag.open();
						
						String[] idsplit = id.split("\\.");//$NON-NLS-1$
						System.out.println("ID: " + idsplit.length);//$NON-NLS-1$
						id = "";//$NON-NLS-1$
						label = inputDiag.getValue();
						
						for (int i = 0; i < idsplit.length - 1; i++) {
							id += idsplit[i] + ".";//$NON-NLS-1$
						}
						id += label;
						
						// Create a new perspective with the new name
						newPersp = new PerspectiveDescriptor(id, label, pd);
						pd = (PerspectiveDescriptor) perspRegistry.findPerspectiveWithLabel(label);
					}
				}
				
				memento.getChild(TAG_DESCRIPTOR).putString(ATTR_LABEL, label); //$NON-NLS-1$ //$NON-NLS-2$
				memento.getChild(TAG_DESCRIPTOR).putString(ATTR_ID, id);//$NON-NLS-1$ //$NON-NLS-2$
				
				newPersp.restoreState(memento);
				
				// Save the new generated perspective in the preference store
				perspRegistry.saveCustomPersp(newPersp, memento);
				
				importMessage +=
					String.format(Messages.ImportHandler_Saved_As, file, newPersp.getLabel());
				
			} catch (WorkbenchException e) {
				unableToLoadPerspective(e.getStatus());
			} catch (IOException e) {
				unableToLoadPerspective(null);
			}
		}
		
		MessageDialog.openInformation(mainWindow.getShell(),
			Messages.ImportHandler_Successfully_Imported,
			Messages.ImportHandler_Imported_perspectives_successfully + importMessage);
		
		return null;
	}
	
	private void unableToLoadPerspective(IStatus status){
		String msg = Messages.ImportHandler_Unable_to_load_Perspective;
		
		if (status == null) {
			IStatus errStatus = new Status(IStatus.ERROR, WorkbenchPlugin.PI_WORKBENCH, msg);
			StatusManager.getManager().handle(errStatus, StatusManager.SHOW | StatusManager.LOG);
		} else {
			StatusAdapter adapter = new StatusAdapter(status);
			adapter.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, msg);
			StatusManager.getManager().handle(adapter, StatusManager.SHOW | StatusManager.LOG);
		}
	}
	
	private void validateAndFixXMLIfNeeded(File xmlFile){
		IViewRegistry viewRegistry = PlatformUI.getWorkbench().getViewRegistry();
		List<FaultViewFix> faultyViews = new ArrayList<FaultViewFix>();
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			
			// load all views of perspective and check viewRegistry if view can be resolved
			NodeList nList = doc.getElementsByTagName(TAG_VIEW);
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node viewNode = nList.item(temp);
				
				if (viewNode.getNodeType() == Node.ELEMENT_NODE) {
					Element vElement = (Element) viewNode;
					String viewId = vElement.getAttribute(ATTR_ID);
					
					// if view can't be found in registry add it to faulty views
					if (viewRegistry.find(viewId) == null) {
						faultyViews.add(new FaultViewFix(viewId));
					}
				}
			}
			
			// ask with which to replace
			FixPerspectiveDialog fixDialog =
				new FixPerspectiveDialog(UiDesk.getTopShell(), faultyViews, viewRegistry);
			fixDialog.open();
			doc = fixViewRefrences(doc, fixDialog.getFaultViewFixes());
			
			// write the content into xml file
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException | SAXException | IOException
				| TransformerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * fixes all view references. previously invalid view references will either be deleted or
	 * replaced with new, valid view id's. depnding on the users choice.
	 * 
	 * @param doc
	 * @param faultViewFixes
	 * @return
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private Document fixViewRefrences(Document doc, List<FaultViewFix> faultViewFixes)
		throws TransformerException, SAXException, IOException, ParserConfigurationException{
		List<FaultViewFix> replaceFixViews = new ArrayList<FaultViewFix>();
		String xmlString = "";
		
		for (FaultViewFix viewFix : faultViewFixes) {
			// REMOVE views with no replacement id
			if (viewFix.getReplacerId().isEmpty()) {
				String missingId = viewFix.getMissingId();
				
				// remove show_view_action and view node
				removeNode(doc, TAG_SHOW_VIEW_ACTION, ATTR_ID, missingId);
				removeNode(doc, TAG_VIEW, ATTR_ID, missingId);
				
				//fast view
				Node fastViews = doc.getElementsByTagName(TAG_FAST_VIEWS).item(0);
				NodeList fvChildren = fastViews.getChildNodes();
				Node viewToDelete = findNode(fvChildren, ATTR_ID, missingId);
				
				if (viewToDelete != null) {
					fastViews.removeChild(viewToDelete);
				}
				
				//delete page entries
				NodeList nodeList = doc.getElementsByTagName(TAG_PAGE);
				Node page = findNode(nodeList, ATTR_CONTENT, missingId);
				if (page != null) {
					Node folder = page.getParentNode();
					List<Element> folderPages =
						getChildElementsByTag(folder.getChildNodes(), TAG_PAGE);
						
					// more than one page in info folder
					if (folderPages.size() > 1) {
						String newPageId = folderPages.get(0).getAttribute(ATTR_CONTENT);
						if (page.equals(newPageId)) {
							newPageId = folderPages.get(1).getAttribute(ATTR_CONTENT);
						}
						replaceActivePageIdIfNeeded(folder, missingId, newPageId);
						
						//remove page entry
						folder.removeChild(page);
						
						List<Element> folderParts = getChildSubElementsByTag(folder.getChildNodes(),
							TAG_PRESENTATION, TAG_PART);
						if (!folderParts.isEmpty()) {
							Element part = folderParts.get(0);
							Node presentation = part.getParentNode();
							presentation.removeChild(part);
						}
						
					} else {
						// only page in info folder -> delete complete node
						Node mainWindow = folder.getParentNode().getParentNode();
						mainWindow.removeChild(folder.getParentNode());
					}
				}
			} else { // REPLACE page elements references with new viewId and label
				fixPageNode(doc.getElementsByTagName(TAG_PAGE), viewFix);
				replaceFixViews.add(viewFix);
			}
		}
		
		// replace all left references with the new value
		DOMSource domSrc = new DOMSource(doc);
		StreamResult result = new StreamResult(new StringWriter());
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(domSrc, result);
		xmlString = result.getWriter().toString();
		
		for (FaultViewFix replaceView : replaceFixViews) {
			xmlString =
				xmlString.replaceAll(replaceView.getMissingId(), replaceView.getReplacerId());
		}
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document fixedDoc = docBuilder.parse(new InputSource(new StringReader(xmlString)));
		return fixedDoc;
	}
	
	/**
	 * rebuld the page node withnew view id and label
	 * 
	 * @param pageList
	 * @param viewFix
	 */
	private void fixPageNode(NodeList pageList, FaultViewFix viewFix){
		Node fixPage = findNode(pageList, ATTR_CONTENT, viewFix.getMissingId());
		if (fixPage != null) {
			// fix content and label attributes of page
			NamedNodeMap pageAttributes = fixPage.getAttributes();
			// content id
			Node fixContent = pageAttributes.getNamedItem(ATTR_CONTENT);
			fixContent.setTextContent(viewFix.getReplacerId());
			// view label
			Node fixLabel = pageAttributes.getNamedItem(ATTR_LABEL);
			fixLabel.setTextContent(viewFix.getLabel());
		}
	}
	
	/**
	 * replaces activePageID of folder with the alternativeId if it matches an invalidViewId
	 * 
	 * @param folder
	 *            to check
	 * @param invalidView
	 *            id of the invalid view that might be set as activepage
	 * @param alternativeId
	 *            alternative to insert
	 */
	private void replaceActivePageIdIfNeeded(Node folder, String invalidView, String alternativeId){
		NamedNodeMap folderAttributes = folder.getAttributes();
		Node activePage = folderAttributes.getNamedItem(ATTR_ACTIVE_PAGE_ID);
		
		// replace if active page is invalid view
		if (invalidView.equals(activePage.getNodeValue())) {
			activePage.setTextContent(alternativeId);
		}
	}
	
	/**
	 * get child element by a tag
	 * 
	 * @param nodeList
	 *            list to search through
	 * @param tag
	 *            to look for
	 * @return list with all matches or empty list
	 */
	private List<Element> getChildElementsByTag(NodeList nodeList, String tag){
		List<Element> elements = new ArrayList<Element>();
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && tag.equals(node.getNodeName())) {
				elements.add((Element) node);
			}
		}
		return elements;
	}
	
	/**
	 * find all elements with the 'subTag' within a 'tag' of the given nodeList
	 * 
	 * @param nodeList
	 *            to search through
	 * @param tag
	 *            parent of the tag (see subTag) we're looking for
	 * @param subTag
	 *            the acutall tag we want the elements from
	 * @return a list of all elements that are children of TAG and match SUB-TAG, or empty list if
	 *         no matches occured
	 */
	private List<Element> getChildSubElementsByTag(NodeList nodeList, String tag, String subTag){
		List<Element> subElements = new ArrayList<Element>();
		
		List<Element> elements = getChildElementsByTag(nodeList, tag);
		for (Element element : elements) {
			NodeList subChilds = element.getChildNodes();
			subElements.addAll(getChildElementsByTag(subChilds, subTag));
		}
		return subElements;
	}
	
	/**
	 * Removes a node matching the given parameters if existing.<br>
	 * i.e. tag=view attribute=id value=my.sample.view<br>
	 * removes <view id="my.sample.view/> if no such entry exists nothing changes
	 * 
	 * @param doc
	 *            document to examine
	 * @param tag
	 *            of the searched node
	 * @param attribute
	 *            that should be compared
	 * @param value
	 *            of the attribute -> used to find a match
	 */
	private void removeNode(Document doc, String tag, String attribute, String value){
		Node nodeToRemove = findNode(doc.getElementsByTagName(tag), attribute, value);
		
		if (nodeToRemove != null) {
			Node parentNode = nodeToRemove.getParentNode();
			parentNode.removeChild(nodeToRemove);
		}
	}
	
	/**
	 * find a node from the given node list
	 * 
	 * @param nodeList
	 *            to search through
	 * @param attribute
	 *            we're looking for
	 * @param value
	 *            that should match the attributes content
	 * @return found node or null
	 */
	private Node findNode(NodeList nodeList, String attribute, String value){
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (value.equals(element.getAttribute(attribute))) {
					return node;
				}
			}
		}
		return null;
	}
}
