/*******************************************************************************
 * Copyright (c) 2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *****************************************************************************/

package ch.elexis.base.ch.medikamente.bag.views;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.ui.util.ListDisplay;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.medikamente.bag.data.BAGMedi;
import ch.elexis.medikamente.bag.data.Interaction;
import ch.elexis.medikamente.bag.data.Substance;

public class InteraktionsDialog extends TitleAreaDialog {
	BAGMedi medi;
	List<Substance> substances;
	Substance actSubstance;
	ListDisplay<Interaction> ldInter;
	List<Interaction> actInteractions;
	Combo cbTyp, cbSeverity;
	Text text;
	org.eclipse.swt.widgets.List lSubst;
	
	public InteraktionsDialog(final Shell shell, final BAGMedi medi){
		super(shell);
		this.medi = medi;
	}
	
	@Override
	protected Control createDialogArea(final Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout());
		substances = medi.getSubstances();
		new Label(ret, SWT.NONE).setText("Inhaltsstoffe");
		lSubst = new org.eclipse.swt.widgets.List(ret, SWT.SINGLE);
		lSubst.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		for (Substance s : substances) {
			lSubst.add(s.getLabel());
		}
		lSubst.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e){
				int idx = lSubst.getSelectionIndex();
				if (idx != -1) {
					setSubst(substances.get(idx));
				}
			}
			
		});
		new Label(ret, SWT.NONE).setText("Interaktion mit:");
		ldInter = new ListDisplay<Interaction>(ret, SWT.NONE, new ListDisplay.LDListener() {
			
			public String getLabel(final Object o){
				if (o instanceof Interaction) {
					Interaction subst = (Interaction) o;
					Substance[] s = subst.getSubstances();
					if (s[0].equals(actSubstance)) {
						return s[1].getLabel();
					} else {
						return s[2].getLabel();
					}
				}
				return "?";
			}
			
			public void hyperlinkActivated(final String l){
				SubstanzSelektor ssel = new SubstanzSelektor(getShell());
				if (ssel.open() == Dialog.OK) {
					Interaction iac =
						new Interaction(actSubstance, ssel.result, "", Interaction.TYPE_UNKNOWN,
							Interaction.RELEVANCE_UNKNOWN);
					ldInter.add(iac);
				}
				
			}
		});
		
		ldInter.addHyperlinks("Substanz Hinzufügen...");
		ldInter.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		ldInter.addListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e){
				setInter(ldInter.getSelection());
			}
			
		});
		new Label(ret, SWT.NONE).setText("Typ der Interaktion");
		cbTyp = new Combo(ret, SWT.SINGLE | SWT.READ_ONLY);
		cbTyp.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cbTyp.setItems(Interaction.INTERAKTIONSTYPEN);
		cbTyp.select(0);
		cbTyp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e){
				Interaction act = ldInter.getSelection();
				act.setType(cbTyp.getSelectionIndex());
			}
			
		});
		new Label(ret, SWT.NONE).setText("Klinische Relevanz");
		cbSeverity = new Combo(ret, SWT.SINGLE | SWT.READ_ONLY);
		cbSeverity.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cbSeverity.setItems(Interaction.RELEVANCE);
		cbSeverity.select(0);
		cbSeverity.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e){
				Interaction act = ldInter.getSelection();
				act.setRelevance(cbSeverity.getSelectionIndex());
			}
		});
		new Label(ret, SWT.NONE).setText("Beschreibung der Interaktion");
		text = SWTHelper.createText(ret, 4, SWT.BORDER);
		text.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(final FocusEvent e){
				Interaction act = ldInter.getSelection();
				if (act != null) {
					act.setDescription(text.getText());
				}
				super.focusLost(e);
			}
			
		});
		setSubst(null);
		// lSubst.select(0);
		return ret;
	}
	
	@Override
	public void create(){
		super.create();
		setTitle(medi.getLabel());
		// setMessage("Interaktionen für "+medi.getLabel());
		getShell().setText("Interaktionen");
	}
	
	void setSubst(final Substance s){
		actSubstance = s;
		ldInter.clear();
		if (s != null) {
			actInteractions = actSubstance.getInteractions();
			for (Interaction inter : actInteractions) {
				ldInter.add(inter);
			}
			if (actInteractions.size() > 0) {
				setInter(actInteractions.get(0));
			} else {
				setInter(null);
			}
		} else {
			setInter(null);
		}
	}
	
	void setInter(final Interaction i){
		boolean bEnable;
		if (i == null) {
			text.setText("");
			cbTyp.select(0);
			cbSeverity.select(0);
			bEnable = false;
		} else {
			text.setText(i.getDescription());
			cbTyp.select(i.getType());
			cbSeverity.select(i.getRelevance());
			bEnable = true;
		}
		text.setEnabled(bEnable);
		cbTyp.setEnabled(bEnable);
		cbSeverity.setEnabled(bEnable);
		ldInter.setSelection(i);
	}
}
