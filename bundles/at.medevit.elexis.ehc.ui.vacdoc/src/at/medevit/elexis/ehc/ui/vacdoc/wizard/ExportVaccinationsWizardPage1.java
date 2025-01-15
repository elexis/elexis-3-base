package at.medevit.elexis.ehc.ui.vacdoc.wizard;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.hl7.fhir.r4.model.Bundle;

import at.medevit.elexis.ehc.ui.preference.PreferencePage;
import at.medevit.elexis.ehc.ui.vacdoc.composite.VaccinationSelectionComposite;
import at.medevit.elexis.ehc.ui.vacdoc.service.OutboxElementServiceHolder;
import at.medevit.elexis.ehc.ui.vacdoc.service.VacdocServiceComponent;
import at.medevit.elexis.ehc.ui.vacdoc.wizard.ExportVaccinationsWizard.ExportType;
import at.medevit.elexis.ehc.vacdoc.service.VacdocService;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.outbox.model.OutboxElementType;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.findings.util.ModelUtil;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

public class ExportVaccinationsWizardPage1 extends WizardPage {

	private Patient selectedPatient;

	private final ExportType exportType;

	private VaccinationSelectionComposite composite;

	protected ExportVaccinationsWizardPage1(String pageName, ExportType exportType) {
		super(pageName);
		setTitle(pageName);
		this.exportType = exportType;
	}

	@Override
	public void createControl(Composite parent) {
		composite = new VaccinationSelectionComposite(parent, SWT.NULL);

		composite.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getWizard().getContainer().updateButtons();
			}
		});

		Composite c = new Composite(composite, SWT.RIGHT_TO_LEFT);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setControl(composite);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			setErrorMessage(null);
			Query<Vaccination> qbe = new Query<Vaccination>(Vaccination.class);
			selectedPatient = ElexisEventDispatcher.getSelectedPatient();
			if (selectedPatient != null) {
				qbe.add(Vaccination.FLD_PATIENT_ID, Query.EQUALS, selectedPatient.getId());
				qbe.orderBy(true, new String[] { Vaccination.FLD_DOA, PersistentObject.FLD_LASTUPDATE });
				List<Vaccination> vaccinations = qbe.execute();
				composite.setInput(vaccinations);
				composite.setSelection(new StructuredSelection(vaccinations), true);

				String ahvNr = selectedPatient.getXid(DOMAIN_AHV);
				if (ahvNr == null || ahvNr.isEmpty()) {
					setErrorMessage("Patient hat keine AHV Nummer.");
				}
			} else {
				setErrorMessage("Es ist kein Patient ausgewählt.");
			}
		}
	}

	@Override
	public boolean isPageComplete() {
		IStructuredSelection contentSelection = composite.getSelection();
		String ahvNr = selectedPatient.getXid(DOMAIN_AHV);
		if (!contentSelection.isEmpty() && ahvNr != null && !ahvNr.isEmpty()) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<Vaccination> getSelectedVaccinations() {
		IStructuredSelection contentSelection = composite.getSelection();

		if (!contentSelection.isEmpty()) {
			return contentSelection.toList();
		}
		return Collections.emptyList();
	}

	public boolean finish() {
		String outputFile = StringUtils.EMPTY;
		try {
			Patient elexisPatient = ElexisEventDispatcher.getSelectedPatient();
			Mandant elexisMandant = ElexisEventDispatcher.getSelectedMandator();
			String outputDir = ConfigServiceHolder.getUser(PreferencePage.EHC_OUTPUTDIR,
					PreferencePage.getDefaultOutputDir());
			VacdocService service = VacdocServiceComponent.getService();

			Bundle document = service.getVacdocDocument(elexisPatient, elexisMandant);

			service.addVaccinations(document, getSelectedVaccinations());
			switch (exportType) {
			case FHIR:
				outputFile = writeAsFHIR(elexisPatient, outputDir, service, document);
				createOutboxElement(elexisPatient, elexisMandant, outputFile);
				break;
			case XDM:
				outputFile = writeAsXDM(elexisPatient, outputDir, service, document);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			ExportVaccinationsWizard.logger.error("Export failed.", e);
			MessageDialog.openError(getShell(), "Error",
					"Es ist ein Fehler beim Impfungen exportiern nach [" + outputFile + "] aufgetreten.");
			return false;
		}
		return true;
	}

	private void createOutboxElement(Patient patient, Mandant mandant, String outputFile) {
		OutboxElementServiceHolder.getService().createOutboxElement(
				CoreModelServiceHolder.get().load(patient.getId(), IPatient.class).orElse(null),
				CoreModelServiceHolder.get().load(mandant.getId(), IMandator.class).orElse(null),
				OutboxElementType.FILE.getPrefix() + outputFile);
	}

	private String writeAsXDM(Patient elexisPatient, String outputDir, VacdocService service, Bundle document)
			throws Exception, FileNotFoundException, IOException {
		// write a XDM document for exchange
		InputStream xdmDocumentStream = service.getXdmAsStream(document);
		String outputFile = outputDir + File.separator + getVaccinationsFileName(elexisPatient) + ".xdm"; //$NON-NLS-1$
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		IOUtils.copy(xdmDocumentStream, outputStream);
		xdmDocumentStream.close();
		outputStream.close();
		return outputFile;
	}

	private String writeAsFHIR(Patient elexisPatient, String outputDir, VacdocService service, Bundle bundle)
			throws Exception, FileNotFoundException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String fhirJson = ModelUtil.getFhirJson(bundle);
		out.write(fhirJson.getBytes());
		InputStream in = new ByteArrayInputStream(out.toByteArray());
		String outputFile = outputDir + File.separator + getVaccinationsFileName(elexisPatient) + "_" //$NON-NLS-1$
				+ System.currentTimeMillis() + ".json"; //$NON-NLS-1$
		try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
			IOUtils.copy(in, outputStream);
			in.close();
		}
		return outputFile;
	}

	private String getVaccinationsFileName(Patient patient) {
		return "vacc_" + patient.getPatCode(); //$NON-NLS-1$
	}
}
