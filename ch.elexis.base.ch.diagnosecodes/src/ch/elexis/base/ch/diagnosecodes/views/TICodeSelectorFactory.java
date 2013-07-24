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

import java.util.HashMap;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import ch.elexis.base.ch.diagnosecodes.TICode;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class TICodeSelectorFactory extends CodeSelectorFactory {
	
	public TICodeSelectorFactory(){
		System.out.println("hier"); //$NON-NLS-1$
	}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		return new ViewerConfigurer(new TICodeContentProvider(), new TICodeLabelProvider(),
			new DefaultControlFieldProvider(cv, new String[] {
				"Text"}), //$NON-NLS-1$
			new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));
	}
	
	static class TICodeContentProvider implements ITreeContentProvider,
			ICommonViewerContentProvider {
		public Object[] getChildren(Object parentElement){
			TICode c = (TICode) parentElement;
			return c.getChildren();
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
			return TICode.getRootNodes();
		}
		
		public void dispose(){
			// TODO Auto-generated method stub
			
		}
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput){}
		
		public void startListening(){}
		
		public void stopListening(){}
		
		public void changed(HashMap<String, String> values){}
		
		public void reorder(String field){}
		
		public void selected(){}
		
		@Override
		public void init(){
			// TODO Auto-generated method stub
			
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
