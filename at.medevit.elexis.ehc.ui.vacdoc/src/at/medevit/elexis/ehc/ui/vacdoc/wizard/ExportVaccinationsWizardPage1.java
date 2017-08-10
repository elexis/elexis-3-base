package at.medevit.elexis.ehc.ui.vacdoc.wizard;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.ehealth_connector.cda.ch.vacd.CdaChVacd;

import at.medevit.elexis.ehc.ui.preference.PreferencePage;
import at.medevit.elexis.ehc.ui.vacdoc.service.OutboxElementServiceHolder;
import at.medevit.elexis.ehc.ui.vacdoc.service.VacdocServiceComponent;
import at.medevit.elexis.ehc.vacdoc.service.VacdocService;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.outbox.model.OutboxElementType;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

public class ExportVaccinationsWizardPage1 extends WizardPage {
	
	private TableViewer contentViewer;
	
	private Patient selectedPatient;
	
	private Button [] btnTypes = new Button[2];
	
	protected ExportVaccinationsWizardPage1(String pageName){
		super(pageName);
		setTitle(pageName);
	}
	
	@Override
	public void createControl(Composite parent){
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		contentViewer =
			new TableViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
		Control control = contentViewer.getControl();
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.heightHint = 300;
		control.setLayoutData(gd);
		
		contentViewer.setContentProvider(new ArrayContentProvider());
		contentViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element){
				if (element instanceof Vaccination) {
					return ((Vaccination) element).getLabel();
				}
				return super.getText(element);
			}
			
			@Override
			public Image getImage(Object element){
				return super.getImage(element);
			}
		});
		
		contentViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				getWizard().getContainer().updateButtons();
			}
		});
		
		Composite c = new Composite(composite, SWT.RIGHT_TO_LEFT);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	
	
		btnTypes[0] = new Button(c, SWT.RADIO);
		btnTypes[0].setText("CDA");
		btnTypes[1] = new Button(c, SWT.RADIO);
		btnTypes[1].setText("XDM");
		
		// defaults
		btnTypes[1].setSelection(true);
		btnTypes[0].setSelection(false);
		setControl(composite);
	}
	
	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
		if (visible) {
			setErrorMessage(null);
			Query<Vaccination> qbe = new Query<Vaccination>(Vaccination.class);
			selectedPatient = ElexisEventDispatcher.getSelectedPatient();
			if (selectedPatient != null) {
				qbe.add(Vaccination.FLD_PATIENT_ID, Query.EQUALS, selectedPatient.getId());
				qbe.orderBy(true, new String[] {
					Vaccination.FLD_DOA, PersistentObject.FLD_LASTUPDATE
				});
				List<Vaccination> vaccinations = qbe.execute();
				contentViewer.setInput(vaccinations);
				contentViewer.setSelection(new StructuredSelection(vaccinations), true);
				
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
	public boolean isPageComplete(){
		IStructuredSelection contentSelection = (IStructuredSelection) contentViewer.getSelection();
		String ahvNr = selectedPatient.getXid(DOMAIN_AHV);
		if (!contentSelection.isEmpty() && ahvNr != null && !ahvNr.isEmpty()) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private List<Vaccination> getSelectedVaccinations(){
		IStructuredSelection contentSelection = (IStructuredSelection) contentViewer.getSelection();
		
		if (!contentSelection.isEmpty()) {
			return contentSelection.toList();
		}
		return Collections.emptyList();
	}
	
	public boolean finish(){
		String outputFile = "";
		try {
			Patient elexisPatient = ElexisEventDispatcher.getSelectedPatient();
			Mandant elexisMandant = ElexisEventDispatcher.getSelectedMandator();
			String outputDir = CoreHub.userCfg.get(PreferencePage.EHC_OUTPUTDIR,
				PreferencePage.getDefaultOutputDir());
			VacdocService service = VacdocServiceComponent.getService();
			
			CdaChVacd document = service.getVacdocDocument(elexisPatient, elexisMandant);
			
			service.addVaccinations(document, getSelectedVaccinations());
			if (btnTypes[0] != null && btnTypes[0].getSelection()) {
				// TODO write as cda
			} else {
				outputFile = writeAsXDM(elexisPatient, outputDir, service, document);
			}
			createOutboxElement(elexisPatient, elexisMandant, outputFile);
		} catch (Exception e) {
			ExportVaccinationsWizard.logger.error("Export failed.", e);
			MessageDialog.openError(getShell(), "Error",
				"Es ist ein Fehler beim Impfungen exportiern nach [" + outputFile
					+ "] aufgetreten.");
			return false;
		}
		return true;
	}
	
	private void createOutboxElement(Patient patient, Mandant mandant, String outputFile){
		OutboxElementServiceHolder.getService().createOutboxElement(patient, mandant, OutboxElementType.FILE.getPrefix() +  outputFile);
	}

	private String writeAsXDM(Patient elexisPatient, String outputDir, VacdocService service,
		CdaChVacd document) throws Exception, FileNotFoundException, IOException{
		String outputFile;
		// write a XDM document for exchange
		InputStream xdmDocumentStream = service.getXdmAsStream(document);
		outputFile =
			outputDir + File.separator + getVaccinationsFileName(elexisPatient) + ".xdm";
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		IOUtils.copy(xdmDocumentStream, outputStream);
		xdmDocumentStream.close();
		outputStream.close();
		return outputFile;
	}
	
	private String getVaccinationsFileName(Patient patient){
		return "vacc_" + patient.getPatCode();
	}
}
