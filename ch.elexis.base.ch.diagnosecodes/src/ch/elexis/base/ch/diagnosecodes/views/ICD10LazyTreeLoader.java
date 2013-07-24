/*******************************************************************************
 * Copyright (c) 2005-2012, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    M. Descher / MEDEVIT - adaption
 *    
 *******************************************************************************/

package ch.elexis.base.ch.diagnosecodes.views;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.elexis.base.ch.diagnosecodes.ICD10;
import ch.elexis.core.data.PersistentObject;
import ch.elexis.core.data.Query;
import ch.elexis.core.ui.actions.AbstractDataLoaderJob;
import ch.elexis.core.ui.actions.LazyTreeLoader;
import ch.rgw.tools.IFilter;
import ch.rgw.tools.LazyTree;
import ch.rgw.tools.LazyTree.LazyTreeListener;
import ch.rgw.tools.Tree;

/**
 * Ein Job, der eine Baumstruktur "Lazy" aus der Datenbank lädt. D.h. es werden immer nur die gerade
 * benötigten Elemente geladen. Die Baumstruktur muss so in einer Tabelle abgelegt sein, dass eine
 * Spalte auf das Elternelement verweist.
 * 
 * @author gerry
 * @author MEDEVIT - copied from original {@link LazyTreeLoader}, adapted for ICD 10
 * 
 * @param <T>
 */
public class ICD10LazyTreeLoader<T> extends AbstractDataLoaderJob implements LazyTreeListener {
	String parentColumn;
	String parentField;
	IFilter filter;
	IProgressMonitor monitor;
	
	private HashMap<String, String> vals = null;
	
	public ICD10LazyTreeLoader(final String Jobname, final Query q, final String parent,
		final String[] orderBy){
		super(Jobname, q, orderBy);
		setReverseOrder(true);
		parentColumn = parent;
	}
	
	public void setFilter(final IFilter f){
		filter = f;
		if (isValid() == true) {
			((Tree) result).setFilter(f);
		}
	}
	
	public void setParentField(final String f){
		parentField = f;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IStatus execute(final IProgressMonitor moni){
		monitor = moni;
		if (monitor != null) {
			monitor.subTask(getJobname());
		}
		result = new LazyTree<T>(null, null, null, this);
		qbe.clear();
		
		if (vals != null && vals.get(ICD10.FLD_CODE).length() > 1) {
			qbe.add(ICD10.FLD_CODE, Query.LIKE, vals.get(ICD10.FLD_CODE) + "%");
		} else if (vals != null && vals.get(ICD10.FLD_TEXT).length() > 1) {
			qbe.add(ICD10.FLD_TEXT, Query.LIKE, "%" + vals.get(ICD10.FLD_TEXT) + "%");
		} else {
			qbe.add(parentColumn, "=", "NIL"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		List<T> list = load();
		for (T t : list) {
			((LazyTree) result).add(t, this);
			if (monitor != null) {
				monitor.worked(1);
			}
		}
		if (filter != null) {
			((Tree) result).setFilter(filter);
		}
		return Status.OK_STATUS;
	}
	
	@Override
	public int getSize(){
		return qbe.size();
	}
	
	@SuppressWarnings("unchecked")
	public boolean fetchChildren(final LazyTree l){
		qbe.clear();
		PersistentObject obj = (PersistentObject) l.contents;
		if (obj != null) {
			qbe.add(parentColumn, "=", parentField == null ? obj.getId() : obj.get(parentField)); //$NON-NLS-1$
			List ret = load();
			for (PersistentObject o : (List<PersistentObject>) ret) {
				l.add(o, this);
			}
			return ret.size() > 0;
		}
		return false;
	}
	
	public boolean hasChildren(final LazyTree l){
		fetchChildren(l);
		return (l.getFirstChild() != null);
	}
	
	public void setVals(HashMap<String, String> vals){
		this.vals = vals;
	}
	
}
