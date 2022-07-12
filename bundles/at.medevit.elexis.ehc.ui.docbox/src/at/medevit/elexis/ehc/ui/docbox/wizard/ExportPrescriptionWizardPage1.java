package at.medevit.elexis.ehc.ui.docbox.wizard;

import org.apache.commons.lang3.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.ehealth_connector.cda.ch.AbstractCdaChV1;

import at.medevit.elexis.ehc.docbox.service.DocboxService;
import at.medevit.elexis.ehc.ui.preference.PreferencePage;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.Rezept;

public class ExportPrescriptionWizardPage1 extends WizardPage {
	private TableViewer contentViewer;

	protected ExportPrescriptionWizardPage1(String pageName) {
		super(pageName);
		setTitle(pageName);
	}

	@Override
	public void createControl(Composite parent) {
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
			public String getText(Object element) {
				if (element instanceof Rezept) {
					return ((Rezept) element).getLabel();
				}
				return super.getText(element);
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
			Query<Rezept> qbe = new Query<Rezept>(Rezept.class);
			Patient selectedPatient = ElexisEventDispatcher.getSelectedPatient();
			if (selectedPatient != null) {
				qbe.add(Rezept.PATIENT_ID, Query.EQUALS, selectedPatient.getId());
				qbe.orderBy(true, new String[] { Rezept.DATE, PersistentObject.FLD_LASTUPDATE });
			}
			contentViewer.setInput(qbe.execute());
		}
	}

	@Override
	public boolean isPageComplete() {
		IStructuredSelection contentSelection = (IStructuredSelection) contentViewer.getSelection();

		if (!contentSelection.isEmpty()) {
			Rezept selectedRezept = (Rezept) contentSelection.getFirstElement();

			AbstractCdaChV1<?> document = DocboxService.getPrescriptionDocument(selectedRezept);
			if (document != null) {
				ExportPrescriptionWizard.setRezept(selectedRezept);
				ExportPrescriptionWizard.setDocument(document);
				return true;
			}
		}
		return false;
	}

	private void writePdf(ByteArrayOutputStream pdf) throws FileNotFoundException, IOException {
		String outputDir = ConfigServiceHolder.getUser(PreferencePage.EHC_OUTPUTDIR,
				PreferencePage.getDefaultOutputDir());
		File pdfFile = new File(outputDir + File.separator + getRezeptFileName() + ".pdf"); //$NON-NLS-1$
		try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
			fos.write(pdf.toByteArray());
			fos.flush();
		}
	}

	public boolean finish() {
		try {
			String outputDir = ConfigServiceHolder.getUser(PreferencePage.EHC_OUTPUTDIR,
					PreferencePage.getDefaultOutputDir());
			ExportPrescriptionWizard.getDocument()
					.saveToFile(outputDir + File.separator + getRezeptFileName() + ".xml"); //$NON-NLS-1$
			ByteArrayOutputStream pdf = DocboxService.getPrescriptionPdf(ExportPrescriptionWizard.getDocument());
			writePdf(pdf);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String getRezeptFileName() {
		String ret = ExportPrescriptionWizard.getRezept().getLabel();
		return ret.replaceAll(StringUtils.SPACE, "_"); //$NON-NLS-1$
	}
}
