/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.ui.dialogs;

import java.util.GregorianCalendar;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.proposals.PersistentObjectContentProposal;
import ch.elexis.core.ui.proposals.PersistentObjectProposalProvider;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class ApplyVaccinationDialog extends TitleAreaDialog {
	private Text txtAdministrator;
	private Text txtArticleName;
	private Text txtLotNo;
	private Text txtAtcCode;
	private Text txtArticleEAN;
	
	private boolean isSupplement = false;
	private String administratorString = null;
	private String articleString = null;
	private DateTime dateOfAdministration;
	private String articleEAN;
	
	private ArtikelstammItem ai;
	private String articleAtcCode;
	private String lotNo;
	private GregorianCalendar doa;
	private GregorianCalendar dob;
	
	private Mandant mandant;
	private TimeTool patBDay;
	private TimeTool selDate;
	
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param b
	 */
	public ApplyVaccinationDialog(Shell parentShell){
		super(parentShell);
		mandant = (Mandant) ElexisEventDispatcher.getSelected(Mandant.class);
		Patient pat = ElexisEventDispatcher.getSelectedPatient();
		patBDay = new TimeTool(pat.getGeburtsdatum());
		selDate = new TimeTool();
	}
	
	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent){
		setTitle("Impfung ein-/nachtragen");
		setTitleImage(ResourceManager.getPluginImage("at.medevit.elexis.impfplan.ui",
			"rsc/icons/vaccination_logo.png"));
		
		Patient selectedPatient = ElexisEventDispatcher.getSelectedPatient();
		setMessage(selectedPatient.getLabel());
		
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group radioGroup = new Group(container, SWT.NONE);
		RowLayout rl_radioGroup = new RowLayout(SWT.HORIZONTAL);
		rl_radioGroup.fill = true;
		radioGroup.setLayout(rl_radioGroup);
		radioGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Button btnNewVacc = new Button(radioGroup, SWT.RADIO);
		btnNewVacc.setText("eintragen");
		btnNewVacc.setSelection(true);
		btnNewVacc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				txtAdministrator.setText(mandant.getMandantLabel());
				administratorString = mandant.storeToString();
				isSupplement = false;
			}
		});
		
		Button btnSupplementVacc = new Button(radioGroup, SWT.RADIO);
		btnSupplementVacc.setText("nachtragen (wird nicht verrechnet)");
		btnSupplementVacc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				administratorString = "";
				txtAdministrator.setText("");
				isSupplement = true;
			}
		});
		
		Group mainGroup = new Group(container, SWT.NONE);
		mainGroup.setText("Pflicht Angaben");
		GridLayout gd_MainGroup = new GridLayout(2, false);
		mainGroup.setLayout(gd_MainGroup);
		mainGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblVerabreichungsdatum = new Label(mainGroup, SWT.NONE);
		lblVerabreichungsdatum
			.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblVerabreichungsdatum.setText("Datum");
		
		dateOfAdministration = new DateTime(mainGroup, SWT.BORDER | SWT.DROP_DOWN);
		dateOfAdministration.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				selDate.set(dateOfAdministration.getYear(), dateOfAdministration.getMonth(),
					dateOfAdministration.getDay());
				
				if (selDate.isBefore(patBDay)) {
					SWTHelper.showInfo("Patient noch nicht geboren",
						"Das von Ihnen gewählte Datum liegt vor der Geburt des Patienten.");
					dateOfAdministration.setYear(patBDay.get(TimeTool.YEAR));
					dateOfAdministration.setMonth(patBDay.get(TimeTool.MONTH));
					dateOfAdministration.setDay(patBDay.get(TimeTool.DAY_OF_MONTH));
				}
			}
		});
		{ // administrating contact
			Label lblAdministratingContact = new Label(mainGroup, SWT.NONE);
			lblAdministratingContact.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
			lblAdministratingContact.setText("Verabr. Arzt");
			
			txtAdministrator = new Text(mainGroup, SWT.BORDER);
			administratorString = mandant.storeToString();
			txtAdministrator.setText(mandant.getMandantLabel());
			txtAdministrator.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e){
					administratorString = txtAdministrator.getText();
				}
			});
			txtAdministrator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			PersistentObjectProposalProvider<Mandant> mopp =
				new PersistentObjectProposalProvider<Mandant>(Mandant.class) {
					@Override
					public String getLabelForObject(Mandant a){
						return a.getMandantLabel();
					}
				};
			
			ContentProposalAdapter mandatorProposalAdapter =
				new ContentProposalAdapter(txtAdministrator, new TextContentAdapter(), mopp, null,
					null);
			mandatorProposalAdapter
				.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
			mandatorProposalAdapter.addContentProposalListener(new IContentProposalListener() {
				
				@Override
				public void proposalAccepted(IContentProposal proposal){
					PersistentObjectContentProposal<Mandant> prop =
						(PersistentObjectContentProposal<Mandant>) proposal;
					administratorString = prop.getPersistentObject().storeToString();
				}
			});
		}
		
		{ // article name
			Label lblArtikelname = new Label(mainGroup, SWT.NONE);
			lblArtikelname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblArtikelname.setText("Artikelname");
			
			txtArticleName = new Text(mainGroup, SWT.BORDER);
			txtArticleName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			txtArticleName.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e){
					articleString = txtArticleName.getText();
				}
			});
			
			PersistentObjectProposalProvider<ArtikelstammItem> aopp =
				new PersistentObjectProposalProvider<>(ArtikelstammItem.class,
					ArtikelstammItem.FLD_ATC, Query.LIKE, "J07%");
			ContentProposalAdapter articleProposalAdapter =
				new ContentProposalAdapter(txtArticleName, new TextContentAdapter(), aopp, null,
					null);
			articleProposalAdapter.addContentProposalListener(new IContentProposalListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void proposalAccepted(IContentProposal proposal){
					PersistentObjectContentProposal<ArtikelstammItem> prop =
						(PersistentObjectContentProposal<ArtikelstammItem>) proposal;
					articleString = prop.getPersistentObject().storeToString();
					txtArticleEAN.setText(prop.getPersistentObject().getEAN());
					txtAtcCode.setText(prop.getPersistentObject().getATCCode());
				}
			});
		}
		
		Label lblLotNo = new Label(mainGroup, SWT.NONE);
		lblLotNo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLotNo.setText("Lot-Nr");
		
		txtLotNo = new Text(mainGroup, SWT.BORDER);
		txtLotNo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(container, SWT.NONE);
		
		Group optionalGroup = new Group(container, SWT.NONE);
		optionalGroup.setText("Optionale Angaben");
		optionalGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		optionalGroup.setLayout(new GridLayout(2, false));
		
		Label lblArtikelEan = new Label(optionalGroup, SWT.NONE);
		lblArtikelEan.setSize(60, 15);
		lblArtikelEan.setText("Artikel EAN");
		
		txtArticleEAN = new Text(optionalGroup, SWT.BORDER);
		txtArticleEAN.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtArticleEAN.setSize(348, 21);
		
		Label lblAtccode = new Label(optionalGroup, SWT.NONE);
		lblAtccode.setSize(56, 15);
		lblAtccode.setText("ATC-Code");
		
		txtAtcCode = new Text(optionalGroup, SWT.BORDER);
		txtAtcCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtAtcCode.setSize(314, 21);
		
		return area;
	}
	
	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent){
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	@Override
	protected void okPressed(){
		articleEAN = txtArticleEAN.getText();
		articleAtcCode = txtAtcCode.getText();
		lotNo = txtLotNo.getText();
		
		doa =
			new GregorianCalendar(dateOfAdministration.getYear(), dateOfAdministration.getMonth(),
				dateOfAdministration.getDay());
		super.okPressed();
	}
	
	public TimeTool getDateOfAdministration(){
		return new TimeTool(doa.getTime());
	}
	
	public String getAdministratorString(){
		return administratorString;
	}
	
	public String getLotNo(){
		return lotNo;
	}
	
	public String getAtcCode(){
		return articleAtcCode;
	}
	
	public String getArticleString(){
		return articleString;
	}
	
	public String getEAN(){
		return articleEAN;
	}
	
	public boolean isSupplement(){
		return isSupplement;
	}
}
