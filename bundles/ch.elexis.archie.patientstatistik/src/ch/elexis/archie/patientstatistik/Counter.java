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

import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class Counter extends Job {
	private static final int tasksum = 1000000;
	private int perCase = tasksum;
	private int perKons = 1;
	private HashMap<IBillable, List<IBilled>> result;
	private Patient p;
	private TimeTool von;
	private TimeTool bis;
	
	public interface IJobFinishedListener {
		public void jobFinished(Counter counter);
	}
	
	public HashMap<IBillable, List<IBilled>> getValues(){
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
		result = new HashMap<IBillable, List<IBilled>>();
		IPatient patient = NoPoUtil.loadAsIdentifiable(p, IPatient.class).get();
		List<ICoverage> coverages = patient.getCoverages();
		if (!coverages.isEmpty()) {
			perCase = tasksum / coverages.size();
			
			IQuery<IEncounter> query = CoreModelServiceHolder.get().getQuery(IEncounter.class);
			query.startGroup();
			for (ICoverage coverage : coverages) {
				query.or(ModelPackage.Literals.IENCOUNTER__COVERAGE, COMPARATOR.EQUALS, coverage);
			}
			query.andJoinGroups();
			if (von != null) {
				query.and(ModelPackage.Literals.IENCOUNTER__DATE, COMPARATOR.GREATER_OR_EQUAL,
					von.toLocalDate());
			}
			if (bis != null) {
				query.and(ModelPackage.Literals.IENCOUNTER__DATE, COMPARATOR.LESS_OR_EQUAL,
					bis.toLocalDate());
			}
			List<IEncounter> kk = query.execute();
			perKons = perCase / kk.size();
			for (IEncounter encounter : kk) {
				for (IBilled v : encounter.getBilled()) {
					IBillable iv = v.getBillable();
					List<IBilled> liv = result.get(iv);
					if (liv == null) {
						liv = new LinkedList<IBilled>();
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
