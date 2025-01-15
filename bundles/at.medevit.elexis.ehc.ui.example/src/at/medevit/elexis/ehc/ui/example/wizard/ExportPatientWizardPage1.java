/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.ui.example.wizard;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.projecthusky.common.hl7cdar2.POCDMT000040ClinicalDocument;

import at.medevit.elexis.ehc.ui.example.service.ServiceComponent;
import at.medevit.elexis.ehc.ui.preference.PreferencePage;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.Query;

public class ExportPatientWizardPage1 extends WizardPage {

	private TableViewer contentViewer;

	protected ExportPatientWizardPage1(String pageName) {
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
				if (element instanceof Patient) {
					return ((Patient) element).getLabel();
				}
				return super.getText(element);
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof Patient) {
					if (((Patient) element).getGeschlecht().equals(Person.FEMALE)) {
						return Images.IMG_FRAU.getImage();
					} else {
						return Images.IMG_MANN.getImage();
					}
				}
				return super.getImage(element);
			}
		});
		Query<Patient> qp = new Query(Patient.class);
		contentViewer.setInput(qp.execute());

		Patient selectedPatient = ElexisEventDispatcher.getSelectedPatient();
		if (selectedPatient != null) {
			contentViewer.setSelection(new StructuredSelection(selectedPatient));
			contentViewer.getTable().showSelection();
		}

		setControl(composite);
	}

	public boolean finish() {
		IStructuredSelection contentSelection = (IStructuredSelection) contentViewer.getSelection();

		if (!contentSelection.isEmpty()) {
			Patient selectedPatient = (Patient) contentSelection.getFirstElement();
			POCDMT000040ClinicalDocument document = ServiceComponent.getService().createDocument(selectedPatient,
					(Mandant) ElexisEventDispatcher.getSelected(Mandant.class));
			try {
				String outputDir = ConfigServiceHolder.getUser(PreferencePage.EHC_OUTPUTDIR,
						PreferencePage.getDefaultOutputDir());
				File file = new File(
						outputDir + File.separator + selectedPatient.get(Patient.FLD_PATID) + "_patientdata.xml"); // $NON-NLS-1$
				try (FileOutputStream fo = new FileOutputStream(file)) {
					ServiceComponent.getService().saveDocument(document, fo);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
	}
}
