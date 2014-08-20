package at.medevit.elexis.ehc.ui.example.wizard;

import java.io.File;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import at.medevit.elexis.ehc.ui.example.service.ServiceComponent;
import at.medevit.elexis.ehc.ui.preference.PreferencePage;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.Rezept;
import ehealthconnector.cda.documents.ch.CdaCh;

public class ExportPrescriptionWizardPage1 extends WizardPage {
	private TableViewer contentViewer;
	
	protected ExportPrescriptionWizardPage1(String pageName){
		super(pageName);
		setTitle(pageName);
	}
	
	@Override
	public void createControl(Composite parent){
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		contentViewer = new TableViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		Control control = contentViewer.getControl();
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.heightHint = 300;
		control.setLayoutData(gd);
		
		contentViewer.setContentProvider(new ArrayContentProvider());
		contentViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element){
				if (element instanceof Rezept) {
					return ((Rezept) element).getLabel();
				}
				return super.getText(element);
			}
			
			@Override
			public Image getImage(Object element){
				return super.getImage(element);
			}
		});
		
		Query<Rezept> qbe = new Query<Rezept>(Rezept.class);
		Patient selectedPatient = ElexisEventDispatcher.getSelectedPatient();
		if (selectedPatient != null) {
			qbe.add(Rezept.PATIENT_ID, Query.EQUALS, selectedPatient.getId());
			qbe.orderBy(true, new String[] {
				Rezept.DATE, PersistentObject.FLD_LASTUPDATE
			});
		}
		contentViewer.setInput(qbe.execute());
		setControl(composite);
	}
	
	public boolean finish(){
		IStructuredSelection contentSelection = (IStructuredSelection) contentViewer.getSelection();
		
		if (!contentSelection.isEmpty()) {
			Rezept selectedRezept = (Rezept) contentSelection.getFirstElement();
			
			CdaCh document = ServiceComponent.getService().getPrescriptionDocument(selectedRezept);
			try {
				String outputDir =
					CoreHub.userCfg.get(PreferencePage.EHC_OUTPUTDIR,
						PreferencePage.getDefaultOutputDir());
				document.cSaveToFile(outputDir + File.separator + selectedRezept.getId()
					+ "_rezept.xml");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
}
