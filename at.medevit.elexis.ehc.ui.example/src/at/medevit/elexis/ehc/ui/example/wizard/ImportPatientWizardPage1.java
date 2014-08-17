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

import java.util.Collections;

import org.eclipse.jface.dialogs.MessageDialog;
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
import ch.elexis.core.ui.exchange.KontaktMatcher;
import ch.elexis.core.ui.exchange.KontaktMatcher.CreateMode;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ehealthconnector.cda.documents.ch.CdaCh;
import ehealthconnector.cda.documents.ch.ConvenienceUtilsEnums.AdministrativeGenderCode;

public class ImportPatientWizardPage1 extends WizardPage {
	
	private TableViewer contentViewer;
	private CdaCh ehcDocument;
	
	protected ImportPatientWizardPage1(String pageName, CdaCh ehcDocument){
		super(pageName);
		setTitle("Patienten für import auswählen.");
		this.ehcDocument = ehcDocument;
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
				if (element instanceof ehealthconnector.cda.documents.ch.Patient) {
					ehealthconnector.cda.documents.ch.Patient patient =
						((ehealthconnector.cda.documents.ch.Patient) element);
					return patient.cGetName().cGetCompleteName() + " - " + patient.cGetBirthDate();
				}
				return super.getText(element);
			}
			
			@Override
			public Image getImage(Object element){
				if (element instanceof ehealthconnector.cda.documents.ch.Patient) {
					if (((ehealthconnector.cda.documents.ch.Patient) element).cGetGender() == AdministrativeGenderCode.Female) {
						return Images.IMG_FRAU.getImage();
					} else {
						return Images.IMG_MANN.getImage();
					}
				}
				return super.getImage(element);
			}
		});
		
		if (ehcDocument != null) {
			contentViewer.setInput(Collections.singletonList(ehcDocument.cGetPatient()));
		}
		
		setControl(composite);
	}
	
	public boolean finish(){
		IStructuredSelection contentSelection = (IStructuredSelection) contentViewer.getSelection();
		
		if (!contentSelection.isEmpty()) {
			ehealthconnector.cda.documents.ch.Patient selectedPatient =
				(ehealthconnector.cda.documents.ch.Patient) contentSelection.getFirstElement();
			String gender =
				selectedPatient.cGetGender() == AdministrativeGenderCode.Female ? Person.FEMALE
						: Person.MALE;
			Patient existing =
				KontaktMatcher.findPatient(selectedPatient.cGetName().cGetName(), selectedPatient
					.cGetName().cGetFirstName(), selectedPatient.cGetBirthDate(), gender, null,
					null, null, null, CreateMode.FAIL);
			if (existing != null) {
				boolean result =
					MessageDialog.openConfirm(getShell(), "Patient existiert",
						"Der Patient existiert bereits sollen die Daten überschrieben werden?");
				if (!result) {
					return true;
				}
			}
			ServiceComponent.getService().importPatient(selectedPatient);
		}
		
		return true;
	}
	
	public void setDocument(CdaCh ehcDocument){
		this.ehcDocument = ehcDocument;
		if (contentViewer != null && !contentViewer.getControl().isDisposed()) {
			contentViewer.setInput(Collections.singletonList(ehcDocument.cGetPatient()));
			contentViewer.refresh();
		}
	}
}
