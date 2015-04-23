/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.base.ch.diagnosecodes.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.TICode;

public class TICodeSelectorFactory extends CodeSelectorFactory {
	
	public TICodeSelectorFactory(){}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		return new ViewerConfigurer(new TICodeContentProvider(cv), new TICodeLabelProvider(),
			new DefaultControlFieldProvider(cv, new String[] {
				"Text"}), //$NON-NLS-1$
			new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));
	}
	
	static class TICodeContentProvider implements ITreeContentProvider,
			ICommonViewerContentProvider {
		private CommonViewer viewer;
		private String value;
		private String TICKey = "Text";
		
		private List<TICode> nodes;
		private List<TICode> childs;
		private List<TICode> allNodes;
		
		public TICodeContentProvider(CommonViewer viewer){
			this.viewer = viewer;
			allNodes = new ArrayList<TICode>(Arrays.asList(TICode.getRootNodes()));
			nodes = allNodes;
			value = "";
		}
		
		public Object[] getChildren(Object parentElement){
			TICode c = (TICode) parentElement;
			// get all children if no search value is set
			if (value == null || value.isEmpty()) {
				return c.getChildren();
			}
			
			// only show children that match the search query
			List<TICode> availableChildren = new ArrayList<TICode>();
			for (TICode tic : c.getChildren()) {
				if (childs.contains(tic)) {
					availableChildren.add(tic);
				}
			}
			return availableChildren.toArray(new TICode[availableChildren.size()]);
		}
		
		public Object getParent(Object element){
			TICode c = (TICode) element;
			return c.getParent();
		}
		
		public boolean hasChildren(Object element){
			TICode c = (TICode) element;
			return c.hasChildren();
		}
		
		public Object[] getElements(Object inputElement){
			return nodes.toArray(new TICode[nodes.size()]);
		}
		
		public void dispose(){
			// TODO Auto-generated method stub
			
		}
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput){}
		
		public void startListening(){
			viewer.getConfigurer().getControlFieldProvider().addChangeListener(this);
		}
		
		public void stopListening(){
			viewer.getConfigurer().getControlFieldProvider().removeChangeListener(this);
		}
		
		public void changed(HashMap<String, String> values){
			String filterText = values.get(TICKey).toLowerCase();
			if (filterText == null || filterText.isEmpty() || filterText.equals("%")) {
				nodes = allNodes;
				setFilterValue("");
			} else {
				List<TICode> filteredTICodes = new ArrayList<TICode>();
				
				// search for any match of query
				if (filterText.startsWith("%")) {
					filterText = filterText.replace("%", "");
					setFilterValue(filterText);
					filteredTICodes = findAnyMatches(filterText);
				} else {
					// search for starts with query
					setFilterValue(filterText);
					filteredTICodes = findStartMatches(filterText);
				}
				nodes = filteredTICodes;
			}
			// update view
			viewer.notify(CommonViewer.Message.update);
		}
		
		/**
		 * looks for TICode texts that start with the searched query
		 * 
		 * @param search
		 * @return list of search term matching TICodes
		 */
		private List<TICode> findStartMatches(String search){
			childs = new ArrayList<TICode>();
			List<TICode> matches = new ArrayList<TICode>();
			
			for (TICode tiCode : allNodes) {
				String text = tiCode.getText().toLowerCase();
				if (text.startsWith(search)) {
					matches.add(tiCode);
				} else {
					TICode[] ticChildren = tiCode.getChildren();
					for (TICode ticChild : ticChildren) {
						text = ticChild.getText().toLowerCase();
						if (text.startsWith(search)) {
							childs.add(ticChild);
							if (!matches.contains(tiCode)) {
								matches.add(tiCode);
							}
						}
					}
				}
				
			}
			return matches;
		}
		
		/**
		 * looks for matching text everywhere in the text
		 * 
		 * @param search
		 * @return list of all matching TICodes
		 */
		private List<TICode> findAnyMatches(String search){
			childs = new ArrayList<TICode>();
			List<TICode> matches = new ArrayList<TICode>();
			
			for (TICode tiCode : allNodes) {
				String text = tiCode.getText().toLowerCase();
				
				if (text.contains(search)) {
					matches.add(tiCode);
				} else {
					TICode[] ticChildren = tiCode.getChildren();
					for (TICode ticChild : ticChildren) {
						text = ticChild.getText().toLowerCase();
						if (text.contains(search)) {
							childs.add(ticChild);
							if (!matches.contains(tiCode)) {
								matches.add(tiCode);
							}
						}
					}
				}
				
			}
			return matches;
		}
		
		public void reorder(String field){}
		
		public void selected(){}
		
		@Override
		public void init(){
			// TODO Auto-generated method stub
			
		}
		
		private void setFilterValue(String value){
			this.value = value;
		}
	}
	
	static class TICodeLabelProvider extends LabelProvider {
		public String getText(Object element){
			TICode c = (TICode) element;
			return c.getCode() + " " + c.getText(); //$NON-NLS-1$
		}
		
		@Override
		public Image getImage(Object element){
			return null;
		}
		
	}
	
	@Override
	public Class getElementClass(){
		return TICode.class;
	}
	
	@Override
	public void dispose(){}
	
	@Override
	public String getCodeSystemName(){
		return "TI-Code"; //$NON-NLS-1$
	}
}
