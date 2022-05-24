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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.elexis.impfplan.model.DiseaseDefinitionModel;
import at.medevit.elexis.impfplan.model.DiseaseDefinitionModel.DiseaseDefinition;
import at.medevit.elexis.impfplan.service.ArtikelstammModelServiceHolder;
import at.medevit.elexis.impfplan.ui.VaccinationEffectCheckboxTreeViewer;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.service.StoreToStringServiceHolder;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.ui.proposals.IdentifiableContentProposal;
import ch.elexis.core.ui.proposals.IdentifiableProposalProvider;
import ch.elexis.core.ui.proposals.PersistentObjectContentProposal;
import ch.elexis.core.ui.proposals.PersistentObjectProposalProvider;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class SupplementVaccinationDialog extends TitleAreaDialog {
	private Text txtAdministrator;
	private Text txtArticleName;
	private Text txtLotNo;
	private Text txtAtcCode;
	private Text txtArticleEAN;
	private VaccinationEffectCheckboxTreeViewer vect;

	private boolean isSupplement = false;
	private String administratorString = null;
	private String articleString = null;
	private DateTime dateOfAdministration;
	private String articleEAN;
	private String vaccAgainst;

	private String articleAtcCode;
	private String lotNo;
	private GregorianCalendar doa;

	private Mandant mandant;
	private TimeTool patBDay;
	private TimeTool selDate;
	private Patient pat;

	/**
	 * Create the dialog.
	 *
	 * @param parentShell
	 * @param sp
	 * @param b
	 */
	public SupplementVaccinationDialog(Shell parentShell, Patient pat) {
		super(parentShell);
		this.pat = pat;
		mandant = (Mandant) ElexisEventDispatcher.getSelected(Mandant.class);
		patBDay = new TimeTool(pat.getGeburtsdatum());
		selDate = new TimeTool();
		isSupplement = true;
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Impfung nachtragen");
		setTitleImage(
				ResourceManager.getPluginImage("at.medevit.elexis.impfplan.ui", "rsc/icons/vaccination_logo.png"));

		Patient selectedPatient = ElexisEventDispatcher.getSelectedPatient();
		setMessage(pat.getLabel());

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Group mainGroup = new Group(container, SWT.NONE);
		mainGroup.setFont(SWTResourceManager.getFont("Noto Sans", 9, SWT.BOLD));
		mainGroup.setText("Pflicht Angaben");
		GridLayout gd_MainGroup = new GridLayout(2, false);
		mainGroup.setLayout(gd_MainGroup);
		mainGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblVerabreichungsdatum = new Label(mainGroup, SWT.NONE);
		lblVerabreichungsdatum.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblVerabreichungsdatum.setText("Datum");

		dateOfAdministration = new DateTime(mainGroup, SWT.BORDER | SWT.DROP_DOWN);
		dateOfAdministration.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
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

		{ // article name
			Label lblArtikelname = new Label(mainGroup, SWT.NONE);
			lblArtikelname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblArtikelname.setText("Artikelname");

			txtArticleName = new Text(mainGroup, SWT.BORDER);
			txtArticleName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			txtArticleName.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					articleString = txtArticleName.getText();
				}
			});

			IQuery<IArtikelstammItem> query = ArtikelstammModelServiceHolder.get().getQuery(IArtikelstammItem.class);
			query.and("atc", COMPARATOR.LIKE, "J07%");
			IdentifiableProposalProvider<IArtikelstammItem> aopp = new IdentifiableProposalProvider<>(query);
			ContentProposalAdapter articleProposalAdapter = new ContentProposalAdapter(txtArticleName,
					new TextContentAdapter(), aopp, null, null);
			articleProposalAdapter.addContentProposalListener(new IContentProposalListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void proposalAccepted(IContentProposal proposal) {
					IdentifiableContentProposal<IArtikelstammItem> prop = (IdentifiableContentProposal<IArtikelstammItem>) proposal;
					txtArticleName.setText(prop.getLabel());
					articleString = StoreToStringServiceHolder.getStoreToString(prop.getIdentifiable());
				}
			});
		}
		new Label(container, SWT.NONE);

		Group optionalGroup = new Group(container, SWT.NONE);
		optionalGroup.setFont(SWTResourceManager.getFont("Noto Sans", 9, SWT.BOLD));
		optionalGroup.setText("Optionale Angaben");
		optionalGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		optionalGroup.setLayout(new GridLayout(2, false));

		{ // administrating contact
			Label lblAdministratingContact = new Label(optionalGroup, SWT.NONE);
			lblAdministratingContact.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblAdministratingContact.setText("Nachtrag von");

			txtAdministrator = new Text(optionalGroup, SWT.BORDER);
			administratorString = mandant.storeToString();
			txtAdministrator.setText(mandant.getMandantLabel());
			txtAdministrator.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					administratorString = txtAdministrator.getText();
				}
			});
			txtAdministrator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			PersistentObjectProposalProvider<Mandant> mopp = new PersistentObjectProposalProvider<Mandant>(
					Mandant.class) {
				@Override
				public String getLabelForObject(Mandant a) {
					return a.getMandantLabel();
				}
			};

			ContentProposalAdapter mandatorProposalAdapter = new ContentProposalAdapter(txtAdministrator,
					new TextContentAdapter(), mopp, null, null);
			mandatorProposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
			mandatorProposalAdapter.addContentProposalListener(new IContentProposalListener() {

				@Override
				public void proposalAccepted(IContentProposal proposal) {
					PersistentObjectContentProposal<Mandant> prop = (PersistentObjectContentProposal<Mandant>) proposal;
					administratorString = prop.getPersistentObject().storeToString();
				}
			});

			Label lblLotNo = new Label(optionalGroup, SWT.NONE);
			lblLotNo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblLotNo.setText("Lot-Nr");

			txtLotNo = new Text(optionalGroup, SWT.BORDER);
			txtLotNo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		}

		/**
		 * could be useful to define vacc. against at some point, but not needed in the
		 * current version
		 */
		// Label lblArtikelEan = new Label(optionalGroup, SWT.NONE);
		// lblArtikelEan.setSize(60, 15);
		// lblArtikelEan.setText("Artikel EAN");
		//
		// txtArticleEAN = new Text(optionalGroup, SWT.BORDER);
		// txtArticleEAN.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
		// 1, 1));
		// txtArticleEAN.setSize(348, 21);
		//
		// Label lblAtccode = new Label(optionalGroup, SWT.NONE);
		// lblAtccode.setSize(56, 15);
		// lblAtccode.setText("ATC-Code");
		//
		// txtAtcCode = new Text(optionalGroup, SWT.BORDER);
		// txtAtcCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
		// 1));
		// txtAtcCode.setSize(314, 21);

		Group expiredGroup = new Group(container, SWT.NONE);
		expiredGroup.setFont(SWTResourceManager.getFont("Noto Sans", 9, SWT.BOLD));
		expiredGroup.setText("Bei nicht mehr erhältlichen Impfstoffen");
		expiredGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		expiredGroup.setLayout(new GridLayout(2, false));
		{
			Label lblVaccAgainst = new Label(expiredGroup, SWT.NONE);
			lblVaccAgainst.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
			lblVaccAgainst.setText("Impfung gegen Krankheit(en)");

			vect = new VaccinationEffectCheckboxTreeViewer(container, SWT.BORDER, vaccAgainst);
		}
		return area;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	/**
	 * Create contents of the button bar.
	 *
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		lotNo = txtLotNo.getText();
		doa = new GregorianCalendar(dateOfAdministration.getYear(), dateOfAdministration.getMonth(),
				dateOfAdministration.getDay());
		vaccAgainst = vect.getCheckedElementsAsCommaSeparatedString();

		super.okPressed();
	}

	public TimeTool getDateOfAdministration() {
		return new TimeTool(doa.getTime());
	}

	public String getAdministratorString() {
		return administratorString;
	}

	public String getLotNo() {
		return lotNo;
	}

	public String getAtcCode() {
		return articleAtcCode;
	}

	public String getArticleString() {
		return articleString;
	}

	public String getEAN() {
		return articleEAN;
	}

	public boolean isSupplement() {
		return isSupplement;
	}

	public String getVaccAgainst() {
		return vaccAgainst;
	}

	private class DiseaseTreeContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return DiseaseDefinitionModel.getDiseaseDefinitions().toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return (DiseaseDefinition) element;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}
	}

	private class DiseaseTreeLabelProvider implements ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getImage(Object element) {
			return null;
		}

		@Override
		public String getText(Object element) {
			return ((DiseaseDefinition) element).getDiseaseLabel();
		}
	}
}
