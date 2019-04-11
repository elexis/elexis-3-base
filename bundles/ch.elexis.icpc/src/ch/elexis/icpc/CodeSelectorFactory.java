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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
		ViewerConfigurer vc = new ViewerConfigurer(new IcpcCodeContentProvider(),
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
		
		public void startListening(){}
		
		public void stopListening(){}
		
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
		
		public void changed(HashMap<String, String> values){}
		
		public void reorder(String field){}
		
		public void selected(){
			// nothing to do
		}
		
		public Object[] getChildren(Object parentElement){
			if (parentElement instanceof IDiagnosisTree) {
				return ((IDiagnosisTree) parentElement).getChildren().toArray();
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
				List<IDiagnosisTree> children = ((IDiagnosisTree) element).getChildren();
				return children != null && !children.isEmpty();
			}
			return false;
		}
		
		@Override
		public void init(){
			// TODO Auto-generated method stub
			
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
