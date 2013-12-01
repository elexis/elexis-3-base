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

import java.util.HashMap;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;

import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.DefaultControlFieldProvider;
import ch.elexis.util.viewers.SimpleWidgetProvider;
import ch.elexis.util.viewers.ViewerConfigurer;
import ch.elexis.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;
import ch.rgw.tools.Tree;

public class CodeSelectorFactory extends ch.elexis.views.codesystems.CodeSelectorFactory {
	
	public CodeSelectorFactory(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		return new ViewerConfigurer(new IcpcCodeContentProvider(), new IcpcCodeLabelProvider(),
			new DefaultControlFieldProvider(cv, new String[] {
				"Text"
			}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));
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
			return IcpcCode.getRoot().getChildren().toArray();
		}
		
		public void dispose(){}
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput){}
		
		public void changed(HashMap<String, String> values){}
		
		public void reorder(String field){}
		
		public void selected(){
			// nothing to do
		}
		
		public Object[] getChildren(Object parentElement){
			if (parentElement instanceof Tree) {
				Tree t = (Tree) parentElement;
				return t.getChildren().toArray();
			}
			return null;
		}
		
		public Object getParent(Object element){
			if (element instanceof Tree) {
				Tree t = (Tree) element;
				return t.getParent();
			}
			return null;
		}
		
		public boolean hasChildren(Object element){
			if (element instanceof Tree) {
				Tree t = (Tree) element;
				return t.hasChildren();
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
			if (element instanceof Tree) {
				Tree t = (Tree) element;
				if (t.contents instanceof String) {
					return (String) t.contents;
				} else if (t.contents instanceof IcpcCode) {
					IcpcCode c = (IcpcCode) t.contents;
					return c.getLabel();
				}
			}
			return super.getText(element);
		}
		
	}
	
}
