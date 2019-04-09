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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import ch.elexis.base.ch.diagnosecodes.service.CodeElementServiceHolder;
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
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class TICodeSelectorFactory extends CodeSelectorFactory {
	
	public TICodeSelectorFactory(){}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		ViewerConfigurer vc =
			new ViewerConfigurer(new TICodeContentProvider(cv), new TICodeLabelProvider(),
			new DefaultControlFieldProvider(cv, new String[] {
				"Text"}), //$NON-NLS-1$
			new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));
				
		cv.setNamedSelection("ch.elexis.base.ch.diagnosecodes.ti.selection");
		vc.setContentType(ContentType.GENERICOBJECT);
		return vc;
	}
	
	static class TICodeContentProvider implements ITreeContentProvider,
			ICommonViewerContentProvider {
		private CommonViewer viewer;
		private String value;
		private String TICKey = "Text";
		
		private ICodeElementServiceContribution tiCodeElementContribution;
		private List<ICodeElement> roots;
		
		public TICodeContentProvider(CommonViewer viewer){
			this.viewer = viewer;
			tiCodeElementContribution = CodeElementServiceHolder.get()
				.getContribution(CodeElementTyp.DIAGNOSE, "TI-Code").orElseThrow(
					() -> new IllegalStateException("No TI CodeElementContribution available"));
			
			roots = tiCodeElementContribution.getElements(Collections
				.singletonMap(ICodeElementService.ContextKeys.TREE_ROOTS, Boolean.TRUE));
			value = "";
		}
		
		public Object[] getChildren(Object parentElement){
			IDiagnosisTree c = (IDiagnosisTree) parentElement;
			// get all children if no search value is set
			if (value == null || value.isEmpty()) {
				return c.getChildren().toArray();
			}
			
			// only show children that match the search query
			List<IDiagnosisTree> availableChildren =  c.getChildren().parallelStream().filter(ti -> matchFilter(ti)).collect(Collectors.toList());
			return availableChildren.toArray();
		}
		
		public Object getParent(Object element){
			IDiagnosisTree c = (IDiagnosisTree) element;
			return c.getParent();
		}
		
		public boolean hasChildren(Object element){
			IDiagnosisTree c = (IDiagnosisTree) element;
			if (c.getChildren() == null) {
				return false;
			}
			return !c.getChildren().isEmpty();
		}
		
		public Object[] getElements(Object inputElement){
			return roots.toArray();
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
				setFilterValue("");
			} else {
				setFilterValue(filterText);
			}
			// update view
			viewer.notify(CommonViewer.Message.update);
		}

		public boolean matchFilter(IDiagnosisTree element) {
			if(StringUtils.isNotBlank(value)) {
				return (element.getCode() + " " + element.getText()).contains(value);
			}
			return true;
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
			IDiagnosisTree c = (IDiagnosisTree) element;
			return c.getCode() + " " + c.getText(); //$NON-NLS-1$
		}
		
		@Override
		public Image getImage(Object element){
			return null;
		}
		
	}
	
	@Override
	public Class getElementClass(){
		return IDiagnosisTree.class;
	}
	
	@Override
	public void dispose(){}
	
	@Override
	public String getCodeSystemName(){
		return "TI-Code"; //$NON-NLS-1$
	}
}
