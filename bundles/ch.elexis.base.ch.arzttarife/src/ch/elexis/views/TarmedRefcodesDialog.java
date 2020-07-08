/*******************************************************************************
 * Copyright (c) 2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Query;
import ch.elexis.data.TarmedLeistung;
import ch.elexis.data.Verrechnet;

public class TarmedRefcodesDialog extends Dialog {
	
	private Verrechnet billed;
	private Composite contentComposite;
	
	private List<RefCodeEditComposite> refcodesComposites;
	
	public TarmedRefcodesDialog(Shell shell, Verrechnet tl) {
		super(shell);
		refcodesComposites = new ArrayList<>();
		billed = tl;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		contentComposite = (Composite) super.createDialogArea(parent);
		contentComposite.setLayout(new GridLayout(2, false));
		
		Label lbl = new Label(contentComposite, SWT.NONE);
		lbl.setText(billed.getZahl() + "x " + billed.getText());
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		lbl = new Label(contentComposite, SWT.NONE);
		lbl.setText("Aufteilen zu Bezugsleistungen (selber Fall und selber Tag)");
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		ToolBarManager mgr = new ToolBarManager(SWT.RIGHT | SWT.FLAT);
		mgr.add(new Action() {
			@Override
			public ImageDescriptor getImageDescriptor(){
				return Images.IMG_NEW.getImageDescriptor();
			}
			
			@Override
			public void run(){
				addRefcodeEdit();
			}
		});
		mgr.createControl(contentComposite);
		
		return contentComposite;
	}
	
	private void addRefcodeEdit(){
		RefCodeEditComposite add = new RefCodeEditComposite(contentComposite, SWT.NONE);
		add.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		refcodesComposites.add(add);
		TarmedRefcodesDialog.this.getShell().pack(true);
	}
	
	private void removeRefcodeEdit(RefCodeEditComposite refCodeEditComposite){
		((GridData) refCodeEditComposite.getLayoutData()).exclude = true;
		refCodeEditComposite.setVisible(false);
		refCodeEditComposite.dispose();
		refcodesComposites.remove(refCodeEditComposite);
		TarmedRefcodesDialog.this.getShell().pack(true);
	}
	
	@Override
	public void create(){
		super.create();
		getShell().setText("Tarmedbezüge herstellen: " + billed.getCode());
	}
	
	@Override
	protected void okPressed(){
		if (!refcodesComposites.isEmpty()) {
			if (isValid()) {
				refcodesComposites.forEach(rc -> rc.apply(billed));
				ElexisEventDispatcher.getInstance().fire(new ElexisEvent(billed.getKons(), Konsultation.class,
						ElexisEvent.EVENT_UPDATE));
				super.okPressed();
			} else {
				MessageDialog.openWarning(getShell(), "Warnung",
					"Summe der Bezüge ist grösser als die ursprüngliche Menge.");
			}
		}
	}
	
	private boolean isValid(){
		return refcodesComposites.stream().mapToInt(rc -> rc.amountSpinner.getSelection())
				.sum() <= billed.getZahl();
	}
	
	private class RefCodeEditComposite extends Composite {
		
		private ComboViewer refcodeCombo;
		private Spinner amountSpinner;
		
		public RefCodeEditComposite(Composite parent, int style){
			super(parent, style);
			setLayout(new GridLayout(3, false));
			createContent();
		}
		
		public void apply(Verrechnet billed) {
			if (amountSpinner.getSelection() > 0 && !refcodeCombo.getSelection().isEmpty()) {
				String bezug =
					(String) ((StructuredSelection) refcodeCombo.getSelection()).getFirstElement();
				int amount = amountSpinner.getSelection();
				if (amount == billed.getZahl()) {
					billed.setDetail("Bezug", bezug);
				} else {
					Verrechnet copy = new Verrechnet(billed.getVerrechenbar(), billed.getKons(), billed.getZahl());
					copyVerrechnet(billed, copy);
					copy.setZahl(amount);
					billed.setZahl(billed.getZahl() - amount);
					copy.setDetail("Bezug", bezug);
				}
			}
		}
		
		private void copyVerrechnet(Verrechnet from, Verrechnet to) {
			to.set(new String[] { Verrechnet.LEISTG_TXT,
					Verrechnet.LEISTG_CODE, Verrechnet.CLASS, Verrechnet.COUNT, Verrechnet.COST_BUYING,
					Verrechnet.SCALE_TP_SELLING, Verrechnet.SCALE_SELLING, Verrechnet.PRICE_SELLING, Verrechnet.SCALE,
					Verrechnet.SCALE2, Verrechnet.USERID },
					new String[] { from.get(Verrechnet.LEISTG_TXT), from.get(Verrechnet.LEISTG_CODE),
							from.get(Verrechnet.CLASS), from.get(Verrechnet.COUNT), from.get(Verrechnet.COST_BUYING),
							from.get(Verrechnet.SCALE_TP_SELLING), from.get(Verrechnet.SCALE_SELLING),
							from.get(Verrechnet.PRICE_SELLING), from.get(Verrechnet.SCALE), from.get(Verrechnet.SCALE2),
							from.get(Verrechnet.USERID) });
			// copy vat scale for reporting
			to.setDetail(Verrechnet.VATSCALE, from.getDetail(Verrechnet.VATSCALE));
		}

		private void createContent(){
			refcodeCombo = new ComboViewer(this, SWT.BORDER);
			refcodeCombo.setContentProvider(ArrayContentProvider.getInstance());
			refcodeCombo.setLabelProvider(new LabelProvider());
			refcodeCombo.setInput(getPossibleRefCodes());
			
			amountSpinner = new Spinner(this, SWT.BORDER);
			amountSpinner.setValues(0, 0, (int) TarmedRefcodesDialog.this.billed.getZahl(), 0, 1,
				1);
			
			ToolBarManager mgr = new ToolBarManager(SWT.RIGHT | SWT.FLAT);
			mgr.add(new Action() {
				@Override
				public ImageDescriptor getImageDescriptor(){
					return Images.IMG_DELETE.getImageDescriptor();
				}
				
				@Override
				public void run(){
					removeRefcodeEdit(RefCodeEditComposite.this);
				}
			});
			mgr.createControl(this);
		}
		
		private List<String> getPossibleRefCodes(){
			Konsultation encounter = billed.getKons();
			
			Query<Konsultation> query = new Query<>(Konsultation.class);
			query.add(Konsultation.FLD_CASE_ID, Query.EQUALS, encounter.getFall().getId());
			query.add(Konsultation.FLD_DATE, Query.EQUALS, encounter.get(Konsultation.FLD_DATE));
			List<Konsultation> encounters = query.execute();
			if (!encounters.isEmpty()) {
				List<String> ret = new ArrayList<String>();
				HashSet<String> uniqueCodes = new HashSet<String>();
				encounters.forEach(e -> {
					List<String> codes = e.getLeistungen().stream()
							.filter(b -> b.getVerrechenbar() instanceof TarmedLeistung)
						.map(b -> b.getCode()).collect(Collectors.toList());
					uniqueCodes.addAll(codes);
				});
				ret.addAll(uniqueCodes);
				Collections.sort(ret);
				return ret;
			}
			return Collections.emptyList();
		}
	}
}
