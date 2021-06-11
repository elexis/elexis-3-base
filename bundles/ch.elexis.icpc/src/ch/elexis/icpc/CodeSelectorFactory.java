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

package ch.elexis.icpc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;

import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IDiagnosisTree;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ContentType;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;
import ch.elexis.icpc.model.icpc.IcpcCode;
import ch.elexis.icpc.service.CodeElementServiceHolder;

public class CodeSelectorFactory extends ch.elexis.core.ui.views.codesystems.CodeSelectorFactory {
	
	public CodeSelectorFactory(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		ViewerConfigurer vc = new ViewerConfigurer(new IcpcCodeContentProvider(cv),
			new IcpcCodeLabelProvider(),
			new DefaultControlFieldProvider(cv, new String[] {
				"Text"
			}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));
		vc.setContentType(ContentType.GENERICOBJECT);
		cv.setNamedSelection("ch.elexis.icpc.selection");
		return vc;
	}
	
	@Override
	public void dispose(){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getCodeSystemName(){
		return "ICPC";
	}
	
	@Override
	public Class getElementClass(){
		return IcpcCode.class;
	}
	
	public class IcpcCodeContentProvider implements ICommonViewerContentProvider,
			ITreeContentProvider {
		private CommonViewer viewer;
		private String filterValue;
		
		public IcpcCodeContentProvider(CommonViewer cv){
			this.viewer = cv;
		}
		
		public void startListening(){
			viewer.getConfigurer().getControlFieldProvider().addChangeListener(this);
		}
		
		public void stopListening(){
			viewer.getConfigurer().getControlFieldProvider().removeChangeListener(this);
		}
		
		public Object[] getElements(Object inputElement){
			ICodeElementServiceContribution icpcCodeElementContribution = CodeElementServiceHolder
				.get().getContribution(CodeElementTyp.DIAGNOSE, "ICPC").orElseThrow(
					() -> new IllegalStateException("No ICPC CodeElementContribution available"));
				
			List<ICodeElement> roots = icpcCodeElementContribution.getElements(
				Collections
					.singletonMap(ICodeElementService.ContextKeys.TREE_ROOTS, Boolean.TRUE));
			
			return roots.toArray();
		}
		
		public void dispose(){}
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput){}
		
		public void changed(HashMap<String, String> values){
			String filterText = values.get("Text").toLowerCase();
			if (filterText == null || filterText.isEmpty() || filterText.equals("%")) {
				setFilterValue("");
			} else {
				setFilterValue(filterText);
			}
			// update view
			viewer.notify(CommonViewer.Message.update);
			if(StringUtils.isNotBlank(filterValue)) {
				((TreeViewer) viewer.getViewerWidget()).expandAll();
			}
		}
		
		public boolean matchFilter(IDiagnosisTree element){
			if (StringUtils.isNotBlank(filterValue)) {
				if (element.getChildren().isEmpty()) {
					return (element.getCode() + " " + element.getText()).toLowerCase()
						.contains(filterValue);
				} else {
					return getChildren(element).length > 0;
				}
			}
			return true;
		}
		
		public void reorder(String field){}
		
		public void selected(){
			// nothing to do
		}
		
		public Object[] getChildren(Object parentElement){
			if (parentElement instanceof IDiagnosisTree) {
				// get all children if no search value is set
				if (StringUtils.isBlank(filterValue)) {
					return ((IDiagnosisTree) parentElement).getChildren().toArray();
				} else {
					List<IDiagnosisTree> availableChildren =
						((IDiagnosisTree) parentElement).getChildren().parallelStream()
							.filter(ti -> matchFilter(ti)).collect(Collectors.toList());
					return availableChildren.toArray();
				}
			}
			return null;
		}
		
		public Object getParent(Object element){
			if (element instanceof IDiagnosisTree) {
				return ((IDiagnosisTree) element).getParent();
			}
			return null;
		}
		
		public boolean hasChildren(Object element){
			if (element instanceof IDiagnosisTree) {
				if (StringUtils.isBlank(filterValue)) {
					List<IDiagnosisTree> children = ((IDiagnosisTree) element).getChildren();
					return children != null && !children.isEmpty();
				} else {
					return getChildren(element).length > 0;
				}
			}
			return false;
		}
		
		@Override
		public void init(){
			// TODO Auto-generated method stub
			
		}
		
		private void setFilterValue(String value){
			this.filterValue = value;
		}
	}
	
	public class IcpcCodeLabelProvider extends LabelProvider {
		
		@Override
		public String getText(Object element){
			if (element instanceof IDiagnosisTree) {
				return ((IDiagnosisTree) element).getLabel();
			}
			return super.getText(element);
		}
		
	}
	
}
