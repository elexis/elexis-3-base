/*******************************************************************************
 * Copyright (c) 2006, G. Weirich and Elexis
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

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;

import ch.elexis.base.ch.diagnosecodes.ICD10;
import ch.elexis.core.data.Query;
import ch.elexis.core.ui.actions.JobPool;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class ICDCodeSelectorFactory extends CodeSelectorFactory {
	ICD10LazyTreeLoader dataloader;
	
	public ICDCodeSelectorFactory(){
		dataloader = (ICD10LazyTreeLoader) JobPool.getJobPool().getJob("ICD"); //$NON-NLS-1$
		if (dataloader == null) {
			
			Query<ICD10> check = new Query<ICD10>(ICD10.class);
			/*
			 * check.add("Code","=","xyz"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ List
			 * l=check.execute(); if(l==null){ if(ICD10.createTable()==false){
			 * MessageDialog.openError
			 * (Desk.theDisplay.getActiveShell(),Messages.ICDCodeSelectorFactory_errorLoading
			 * ,Messages.ICDCodeSelectorFactory_couldntCreate); } check.clear(); }
			 */
			dataloader =
				new ICD10LazyTreeLoader<ICD10>(
					"ICD", check, "parent", new String[] { ICD10.FLD_CODE, ICD10.FLD_TEXT}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			JobPool.getJobPool().addJob(dataloader);
		}
		JobPool.getJobPool().activate("ICD", Job.SHORT); //$NON-NLS-1$
	}
	
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		return new ViewerConfigurer(new ICD10TreeContentProvider(cv, dataloader),
			new ViewerConfigurer.TreeLabelProvider(), new DefaultControlFieldProvider(cv,
				new String[] {
					"Code", "Text"}), //$NON-NLS-1$ //$NON-NLS-2$
			new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));
	}
	
	@Override
	public Class getElementClass(){
		return ICD10.class;
	}
	
	@Override
	public void dispose(){}
	
	@Override
	public String getCodeSystemName(){
		return "ICD-10"; //$NON-NLS-1$
	}
	
}
