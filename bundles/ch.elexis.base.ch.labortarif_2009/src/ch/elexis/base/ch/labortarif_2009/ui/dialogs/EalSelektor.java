/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     G. Weirich - initial API and implementation
 ******************************************************************************/
package ch.elexis.base.ch.labortarif_2009.ui.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.internal.WorkbenchMessages;

import ch.elexis.data.Query;
import ch.elexis.labortarif2009.data.Labor2009Tarif;

public class EalSelektor extends FilteredItemsSelectionDialog {
	
	private static boolean initialized = false;
	private static HashMap<Labor2009Tarif, String> labelCache;
	private static List<Labor2009Tarif> allCodes;
	
	public EalSelektor(Shell shell){
		super(shell);
		setTitle("EAL Code Selektion");
		
		setListLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element){
				if (element == null) {
					return "";
				}
				return labelCache.get((Labor2009Tarif) element);
			}
		});
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		String oldListLabel = WorkbenchMessages.FilteredItemsSelectionDialog_listLabel;
		
		setMessage("");
		WorkbenchMessages.FilteredItemsSelectionDialog_listLabel = ""; //$NON-NLS-1$
		Control ret = super.createDialogArea(parent);
		
		WorkbenchMessages.FilteredItemsSelectionDialog_listLabel = oldListLabel;
		return ret;
	}
	
	@Override
	protected IDialogSettings getDialogSettings(){
		return new DialogSettings("loincselector"); //$NON-NLS-1$
	}
	
	@Override
	protected IStatus validateItem(Object item){
		return Status.OK_STATUS;
	}
	
	@Override
	protected ItemsFilter createFilter(){
		return new ItemsFilter() {
			@Override
			public boolean isConsistentItem(Object item){
				return true;
			}
			
			@Override
			public boolean matchItem(Object item){
				Labor2009Tarif code = (Labor2009Tarif) item;
				
				return matches(labelCache.get(code));
			}
		};
	}
	
	@Override
	protected Comparator<Labor2009Tarif> getItemsComparator(){
		return new Comparator<Labor2009Tarif>() {
			
			public int compare(Labor2009Tarif o1, Labor2009Tarif o2){
				return labelCache.get(o1).compareTo(labelCache.get(o2));
			}
		};
	}
	
	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider,
		ItemsFilter itemsFilter, IProgressMonitor progressMonitor) throws CoreException{
		
		if (!initialized) {
			labelCache = new HashMap<Labor2009Tarif, String>();
			allCodes = new ArrayList<Labor2009Tarif>();
			Query<Labor2009Tarif> qlt = new Query<Labor2009Tarif>(Labor2009Tarif.class);
			allCodes.addAll(qlt.execute());
			progressMonitor.beginTask("", allCodes.size());
			for (Labor2009Tarif labor2009Tarif : allCodes) {
				if (progressMonitor.isCanceled()) {
					return;
				}
				labelCache.put(labor2009Tarif, labor2009Tarif.getLabel());
				progressMonitor.worked(1);
			}
			initialized = true;
		}
		for (Labor2009Tarif code : allCodes) {
			if (progressMonitor.isCanceled()) {
				return;
			}
			contentProvider.add(code, itemsFilter);
		}
	}
	
	@Override
	public String getElementName(Object item){
		Labor2009Tarif code = (Labor2009Tarif) item;
		return labelCache.get(code);
	}
	
	@Override
	protected Control createExtendedContentArea(Composite parent){
		// TODO Auto-generated method stub
		return null;
	}
}
