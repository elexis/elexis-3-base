package at.medevit.elexis.ehc.ui.vacdoc.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.ehealth_connector.cda.Immunization;
import org.ehealth_connector.cda.ch.CdaChVacd;

import at.medevit.elexis.ehc.core.EhcCoreMapper;
import at.medevit.elexis.ehc.vacdoc.service.VacdocService;
import ch.elexis.data.Patient;

public class ImportVaccinationsWizardPage1 extends WizardPage {
	private TableViewer contentViewer;
	private CdaChVacd ehcDocument;
	
	protected ImportVaccinationsWizardPage1(String pageName, CdaChVacd ehcDocument){
		super(pageName);
		setTitle(pageName);
		this.ehcDocument = ehcDocument;
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
				if (element instanceof Immunization) {
					Immunization immunization = (Immunization) element;
					return "(" + immunization.getApplyDate() + ") "
						+ immunization.getConsumable().getTradeName() + " - "
						+ immunization.getAuthor().getCompleteName();
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
		
		setControl(composite);
	}
	
	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
		if (visible) {
			if (ehcDocument != null) {
				ArrayList<Immunization> immunizations = ehcDocument.getImmunizations();
				contentViewer.setInput(immunizations);
				contentViewer.setSelection(new StructuredSelection(immunizations), true);
			}
		}
	}
	
	@Override
	public boolean isPageComplete(){
		IStructuredSelection contentSelection = (IStructuredSelection) contentViewer.getSelection();
		
		if (!contentSelection.isEmpty()) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private List<Immunization> getSelectedImmunizations(){
		IStructuredSelection contentSelection = (IStructuredSelection) contentViewer.getSelection();
		
		if (!contentSelection.isEmpty()) {
			return contentSelection.toList();
		}
		return Collections.emptyList();
	}

	public boolean finish(){
		try {
			Patient elexisPatient = EhcCoreMapper.getElexisPatient(ehcDocument.getPatient());
			VacdocService service = ExportVaccinationsWizard.getVacdocService();

			service.importImmunizations(elexisPatient, getSelectedImmunizations());
		} catch (Exception e) {
			ImportVaccinationsWizard.logger.error("Import failed.", e);
			MessageDialog.openError(getShell(), "Error",
				"Es ist ein Fehler beim Impfungen importieren aufgetreten.");
			return false;
		}
		return true;
	}
}
