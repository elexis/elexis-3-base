package at.medevit.elexis.ehc.ui.vacdoc.wizard;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.ehealth_connector.cda.Consumable;
import org.ehealth_connector.cda.ch.vacd.CdaChVacd;
import org.ehealth_connector.cda.ch.vacd.Immunization;
import org.ehealth_connector.common.mdht.Code;

import at.medevit.elexis.ehc.core.EhcCoreMapper;
import at.medevit.elexis.ehc.ui.vacdoc.service.VacdocServiceComponent;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class ImportVaccinationsWizardPage1 extends WizardPage {
	private TableViewer contentViewer;
	private CdaChVacd ehcDocument;

	private List<Vaccination> vaccinations;

	protected ImportVaccinationsWizardPage1(String pageName, CdaChVacd ehcDocument) {
		super(pageName);
		setTitle(pageName);
		this.ehcDocument = ehcDocument;
		Patient elexisPatient = EhcCoreMapper.getElexisPatient(ehcDocument.getPatient());
		vaccinations = getVaccinations(elexisPatient);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		contentViewer = new TableViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
		Control control = contentViewer.getControl();
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.heightHint = 300;
		control.setLayoutData(gd);

		contentViewer.setContentProvider(new ArrayContentProvider());
		contentViewer.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Immunization) {
					Immunization immunization = (Immunization) element;
					return "(" + immunization.getApplyDate() + ") " + immunization.getConsumable().getTradeName() //$NON-NLS-1$ //$NON-NLS-2$
							+ " - " + immunization.getAuthor().getCompleteName(); //$NON-NLS-1$
				}
				return super.getText(element);
			}

			@Override
			public Color getBackground(Object element) {
				if (element instanceof Immunization) {
					Immunization immunization = (Immunization) element;
					if (isExisting(immunization, vaccinations)) {
						return UiDesk.getColorFromRGB("D3D3D3"); //$NON-NLS-1$
					}
				}
				return super.getBackground(element);
			}

			@Override
			public Image getImage(Object element) {
				return super.getImage(element);
			}
		});

		contentViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getWizard().getContainer().updateButtons();
			}
		});

		setControl(composite);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			if (ehcDocument != null) {
				List<Immunization> immunizations = ehcDocument.getImmunizations();
				contentViewer.setInput(immunizations);
			}
		}
	}

	private List<Vaccination> getVaccinations(Patient elexisPatient) {
		Query<Vaccination> qbe = new Query<>(Vaccination.class);
		qbe.add("ID", Query.NOT_EQUAL, StringConstants.VERSION_LITERAL); //$NON-NLS-1$
		qbe.add(Vaccination.FLD_PATIENT_ID, Query.EQUALS, elexisPatient.getId());
		qbe.orderBy(false, Vaccination.FLD_DOA);
		return qbe.execute();
	}

	private boolean isExisting(Immunization immunization, List<Vaccination> vaccinations) {
		for (Vaccination vaccination : vaccinations) {
			Date applyDate = immunization.getApplyDate();
			TimeTool vaccDate = vaccination.getDateOfAdministration();
			if (applyDate.equals(vaccDate.getTime())) {
				Consumable consumable = immunization.getConsumable();
				Code atcCode = consumable.getWhoAtcCode();
				if (atcCode.getCode().equalsIgnoreCase(vaccination.getAtcCode())) {
					return consumable.getLotNr().equalsIgnoreCase(vaccination.getLotNo());
				}
			}
		}
		return false;
	}

	@Override
	public boolean isPageComplete() {
		IStructuredSelection contentSelection = (IStructuredSelection) contentViewer.getSelection();

		if (!contentSelection.isEmpty()) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<Immunization> getSelectedImmunizations() {
		IStructuredSelection contentSelection = (IStructuredSelection) contentViewer.getSelection();
		if (!contentSelection.isEmpty()) {
			List<Immunization> immunizations = contentSelection.toList();
			return immunizations.stream().filter(i -> !isExisting(i, vaccinations)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	public boolean finish() {
		try {
			Patient elexisPatient = EhcCoreMapper.getElexisPatient(ehcDocument.getPatient());

			VacdocServiceComponent.getService().importImmunizations(elexisPatient, getSelectedImmunizations());
		} catch (Exception e) {
			ImportVaccinationsWizard.logger.error("Import failed.", e); //$NON-NLS-1$
			MessageDialog.openError(getShell(), "Error", "Es ist ein Fehler beim Impfungen importieren aufgetreten.");
			return false;
		}
		return true;
	}
}
