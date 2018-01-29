/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.base.ch.medikamente.bag.views;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TableViewer;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.service.StockService;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.medikamente.bag.data.BAGMedi;
import ch.elexis.medikamente.bag.data.Substance;
import ch.rgw.tools.IFilter;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;

public class BagMediContentProvider extends FlatDataLoader {
	PreparedStatement psSubst, psNotes, psMedi;
	private List<String> ids;
	private BAGMedi[] medis;
	private boolean bOnlyGenerics = false;
	private String sGroup = "";
	private boolean bOnlyStock;
	
	static final String FROM_SUBSTANCE = "SELECT j.product FROM " + BAGMedi.JOINTTABLE + " j, "
		+ Substance.TABLENAME + " s WHERE j.Substance=s.ID AND s.name LIKE ";
	
	public BagMediContentProvider(CommonViewer cv, Query<? extends PersistentObject> qbe){
		super(cv, qbe);
		qbe.addPostQueryFilter(new QueryFilter());
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m.product FROM ").append(BAGMedi.JOINTTABLE).append(" m, ")
			.append(Substance.TABLENAME).append(" s WHERE m.Substance=s.ID AND s.name LIKE ?;");
		psSubst = PersistentObject.getConnection().prepareStatement(sql.toString());
		sql.setLength(0);
		sql.append("SELECT id FROM ").append(BAGMedi.EXTTABLE).append(" WHERE Keywords LIKE ?;");
		psNotes = PersistentObject.getConnection().prepareStatement(sql.toString());
	}
	
	@Override
	public IStatus work(IProgressMonitor monitor, HashMap<String, Object> params){
		final TableViewer tv = (TableViewer) cv.getViewerWidget();
		// SortedSet<BAGMedi> coll=new TreeSet<BAGMedi>();
		qbe.clear();
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		if (sGroup.length() > 0) {
			qbe.add("Gruppe", "=", sGroup);
			medis = qbe.execute().toArray(new BAGMedi[0]);
			cv.getConfigurer().getControlFieldProvider().clearValues();
			sGroup = "";
		} else {
			// String[] values=cv.getConfigurer().getControlFieldProvider().getValues();
			HashMap<String, String> values = (HashMap<String, String>) params.get(PARAM_VALUES);
			if (values == null) {
				values = new HashMap<String, String>();
			}
			if (values.isEmpty()) {
				qbe.orderBy(false, new String[] {
					BAGMedi.FLD_NAME
				});
				medis = qbe.execute().toArray(new BAGMedi[0]);
			} else {
				String subst = values.get(BAGMediSelector.FIELD_SUBSTANCE);
				String notes = values.get(BAGMediSelector.FIELD_NOTES);
				String names = values.get(BAGMediSelector.FIELD_NAME);
				if (StringTool.isNothing(subst) && StringTool.isNothing(notes)) {
					qbe.add(BAGMedi.FLD_NAME, "Like", names + "%", true);
					if (bOnlyGenerics) {
						qbe.add("Generikum", "LIKE", "G%");
					}
					qbe.orderBy(false, new String[] {
						BAGMedi.FLD_NAME
					});
					List<? extends PersistentObject> result = qbe.execute();
					if (bOnlyStock) {
						result = result.stream()
							.filter(a -> (CoreHub.getStockService()
								.getCumulatedAvailabilityForArticle((Artikel) a) != null))
							.collect(Collectors.toList());
					}
					medis = result.toArray(new BAGMedi[0]);
				} else {
					if (!StringTool.isNothing(subst)) {
						String sql = FROM_SUBSTANCE + JdbcLink.wrap(subst + "%");
						Collection<BAGMedi> mediRaw =
							(Collection<BAGMedi>) qbe.queryExpression(sql, null);
						if (mediRaw == null) {
							medis = new BAGMedi[0];
						} else {
							medis = mediRaw.toArray(new BAGMedi[0]);
						}
						
					} else if (!StringTool.isNothing(notes)) {
						ids = qbe.execute(psNotes, new String[] {
							"%" + notes + "%"
						});
						medis = new BAGMedi[ids.size()];
					} else {
						medis = new BAGMedi[0];
					}
					
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}
			}
		}
		
		UiDesk.asyncExec(new Runnable() {
			public void run(){
				tv.setItemCount(0);
				// tv.remove(LOADMESSAGE);
				tv.setItemCount(medis.length);
			}
		});
		
		return Status.OK_STATUS;
		
	}
	
	@Override
	public void updateElement(int index){
		if (index < medis.length) {
			if (medis[index] == null) {
				medis[index] = BAGMedi.load(ids.get(index));
			}
			TableViewer tv = (TableViewer) cv.getViewerWidget();
			tv.replace(medis[index], index);
		}
		
	}
	
	public void setGroup(String group){
		sGroup = group;
	}
	
	public boolean toggleGenericsOnly(){
		bOnlyGenerics = !bOnlyGenerics;
		return bOnlyGenerics;
	}
	
	public boolean toggleStockOnly(){
		bOnlyStock = !bOnlyStock;
		return bOnlyStock;
	}
	
	class QueryFilter implements IFilter {
		public boolean select(Object element){
			BAGMedi medi = (BAGMedi) element;
			if (bOnlyGenerics && !medi.isGenericum()) {
				return false;
			}
			return true;
		}
		
	}
}
