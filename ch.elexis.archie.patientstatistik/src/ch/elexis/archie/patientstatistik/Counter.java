/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.archie.patientstatistik;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;

import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Verrechnet;
import ch.rgw.tools.TimeTool;

public class Counter extends Job {
	private static final int tasksum = 1000000;
	private int perCase = tasksum;
	private int perKons = 1;
	private HashMap<IVerrechenbar, List<Verrechnet>> result;
	private Patient p;
	private TimeTool von;
	private TimeTool bis;
	
	public interface IJobFinishedListener {
		public void jobFinished(Counter counter);
	}
	
	public HashMap<IVerrechenbar, List<Verrechnet>> getValues(){
		return result;
	}
	
	public Counter(final Patient p, final TimeTool von, final TimeTool bis,
		final IJobFinishedListener lis){
		super("Verrechnungszähler");
		setUser(true);
		setSystem(false);
		setPriority(Job.LONG);
		this.p = p;
		this.von = von;
		this.bis = bis;
		if (lis != null) {
			addJobChangeListener(new IJobChangeListener() {
				
				public void sleeping(IJobChangeEvent event){}
				
				public void scheduled(IJobChangeEvent event){}
				
				public void running(IJobChangeEvent event){}
				
				public void done(IJobChangeEvent event){
					UiDesk.getDisplay().asyncExec(new Runnable() {
						public void run(){
							lis.jobFinished(Counter.this);
							
						}
					});
				}
				
				public void awake(IJobChangeEvent event){}
				
				public void aboutToRun(IJobChangeEvent event){
					
				}
			});
		}
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor){
		
		monitor.beginTask("Zähle Verrechnungen", tasksum);
		result = new HashMap<IVerrechenbar, List<Verrechnet>>();
		Fall[] faelle = p.getFaelle();
		if (faelle.length > 0) {
			perCase = tasksum / faelle.length;
			
			Query<Konsultation> qbe = new Query<Konsultation>(Konsultation.class);
			qbe.startGroup();
			for (Fall fall : faelle) {
				qbe.add(Konsultation.FLD_CASE_ID, Query.EQUALS, fall.getId());
				qbe.or();
			}
			qbe.endGroup();
			qbe.and();
			if (von != null) {
				qbe.add(Konsultation.DATE, Query.GREATER_OR_EQUAL,
					von.toString(TimeTool.DATE_COMPACT));
			}
			if (bis != null) {
				qbe.add(Konsultation.DATE, Query.LESS_OR_EQUAL, bis.toString(TimeTool.DATE_COMPACT));
			}
			List<Konsultation> kk = qbe.execute();
			perKons = perCase / kk.size();
			for (Konsultation k : kk) {
				List<Verrechnet> lv = k.getLeistungen();
				for (Verrechnet v : lv) {
					IVerrechenbar iv = v.getVerrechenbar();
					List<Verrechnet> liv = result.get(iv);
					if (liv == null) {
						liv = new LinkedList<Verrechnet>();
						result.put(iv, liv);
					}
					liv.add(v);
				}
				monitor.worked(perKons);
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
			}
		}
		monitor.done();
		return Status.OK_STATUS;
	}
}
