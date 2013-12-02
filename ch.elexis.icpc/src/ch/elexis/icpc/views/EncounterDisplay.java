/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.icpc.views;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Form;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.actions.CodeSelectorHandler;
import ch.elexis.data.PersistentObject;
import ch.elexis.icpc.Encounter;
import ch.elexis.icpc.IcpcCode;
import ch.elexis.core.ui.util.PersistentObjectDropTarget;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.ExHandler;

/**
 * An ICPC-Encounter. Every encounter belongs to exactly one Episode, but an Episode can (and will
 * usually) contain several Encounters. An Encounter has an RFE (Reason for encounter, Problem), a
 * diagnosis and a plan. This display will allow the user to attach those Elements by drag&drop
 * 
 * @author Gerry
 * 
 */
public class EncounterDisplay extends Composite {
	Form form;
	Group gRfe, gDiag, gProc;
	Label lRfe, lDiag, lProc;
	Encounter actEncounter;
	PersistentObjectDropTarget podRfe, podDiag, podProc;
	
	public EncounterDisplay(Composite parent){
		super(parent, SWT.NONE);
		form = UiUiDesk.getToolkit().createForm(this);
		setLayout(new GridLayout());
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite body = form.getBody();
		body.setLayout(new GridLayout());
		gRfe = new Group(body, SWT.NONE);
		gRfe.setText("RFE / Problem");
		GridData gd = SWTHelper.getFillGridData(1, true, 1, true);
		gd.heightHint = 30;
		gRfe.setLayoutData(gd);
		podRfe = new PersistentObjectDropTarget(gRfe, new PersistentObjectDropTarget.IReceiver() {
			
			public boolean accept(PersistentObject o){
				if (o instanceof IcpcCode) {
					return true;
				}
				return false;
			}
			
			public void dropped(PersistentObject o, DropTargetEvent ev){
				if ((actEncounter) != null && (o instanceof IcpcCode)) {
					actEncounter.setRFE((IcpcCode) o);
					setEncounter(actEncounter);
				}
				CodeSelectorHandler.getInstance().removeCodeSelectorTarget();
			}
			
		});
		
		gRfe.setLayout(new FillLayout());
		gRfe.addMouseListener(new ClickReact(podRfe, "RFE"));
		lRfe = new Label(gRfe, SWT.WRAP);
		
		gDiag = new Group(body, SWT.NONE);
		gDiag.setText("Diagnose");
		gDiag.setLayoutData(GridDataFactory.copyData(gd));
		podDiag = new PersistentObjectDropTarget(gDiag, new PersistentObjectDropTarget.IReceiver() {
			
			public boolean accept(PersistentObject o){
				if (o instanceof IcpcCode) {
					return true;
				}
				return false;
			}
			
			public void dropped(PersistentObject o, DropTargetEvent ev){
				if ((actEncounter) != null && (o instanceof IcpcCode)) {
					actEncounter.setDiag((IcpcCode) o);
					setEncounter(actEncounter);
				}
				CodeSelectorHandler.getInstance().removeCodeSelectorTarget();
			}
			
		});
		gDiag.setLayout(new FillLayout());
		gDiag.addMouseListener(new ClickReact(podDiag, "DG"));
		lDiag = new Label(gDiag, SWT.WRAP);
		gProc = new Group(body, SWT.NONE);
		gProc.setText("Procedere");
		podProc = new PersistentObjectDropTarget(gProc, new PersistentObjectDropTarget.IReceiver() {
			
			public boolean accept(PersistentObject o){
				if (o instanceof IcpcCode) {
					return true;
				}
				return false;
			}
			
			public void dropped(PersistentObject o, DropTargetEvent ev){
				if ((actEncounter) != null && (o instanceof IcpcCode)) {
					actEncounter.setProc((IcpcCode) o);
					setEncounter(actEncounter);
				}
				CodeSelectorHandler.getInstance().removeCodeSelectorTarget();
			}
			
		});
		gProc.setLayoutData(GridDataFactory.copyData(gd));
		gProc.setLayout(new FillLayout());
		gProc.addMouseListener(new ClickReact(podProc, "PROC"));
		lProc = new Label(gProc, SWT.WRAP);
	}
	
	public void setEncounter(Encounter e){
		actEncounter = e;
		if (e == null) {
			form.setText("Keine Episode gewählt");
			lRfe.setText("");
			lDiag.setText("");
			lProc.setText("");
		} else {
			form.setText(e.getEpisode().getLabel());
			IcpcCode rfe = e.getRFE();
			lRfe.setText(rfe == null ? "" : rfe.getLabel());
			IcpcCode diag = e.getDiag();
			lDiag.setText(diag == null ? "" : diag.getLabel());
			IcpcCode proc = e.getProc();
			lProc.setText(proc == null ? "" : proc.getLabel());
		}
	}
	
	class ClickReact extends MouseAdapter {
		PersistentObjectDropTarget pod;
		String mode;
		
		ClickReact(PersistentObjectDropTarget pod, String mode){
			this.pod = pod;
			this.mode = mode;
		}
		
		@Override
		public void mouseUp(MouseEvent arg0){
			try {
				ICPCCodesView cov =
					(ICPCCodesView) Hub.plugin.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().showView(ICPCCodesView.ID);
				CodeSelectorHandler.getInstance().setCodeSelectorTarget(pod);
				cov.setComponent(mode);
			} catch (Exception ex) {
				ExHandler.handle(ex);
				
			}
			super.mouseUp(arg0);
		}
		
	}
}
