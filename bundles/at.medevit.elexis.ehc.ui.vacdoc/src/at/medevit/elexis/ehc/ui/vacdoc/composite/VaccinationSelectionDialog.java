package at.medevit.elexis.ehc.ui.vacdoc.composite;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import at.medevit.elexis.impfplan.model.po.Vaccination;

public class VaccinationSelectionDialog extends Dialog {
	
	private Composite container;
	
	private VaccinationSelectionComposite composite;
	
	private List<Vaccination> vaccinations;
	
	private List<Vaccination> selectedVaccinations;
	
	public VaccinationSelectionDialog(Shell shell){
		super(shell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		getShell().setText("Impfungen Auswahl");
		container = (Composite) super.createDialogArea(parent);
		
		composite = new VaccinationSelectionComposite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		if (vaccinations != null) {
			composite.setInput(vaccinations);
			composite.setSelection(new StructuredSelection(vaccinations), true);
		}
		
		composite.addSelectionChangedListener(new ISelectionChangedListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				selectedVaccinations = composite.getSelection().toList();
			}
		});
		
		return container;
	}
	
	public void setVaccinations(List<Vaccination> vaccinations){
		if (composite != null && !composite.isDisposed()) {
			composite.setInput(vaccinations);
			composite.setSelection(new StructuredSelection(vaccinations), true);
		}
		this.vaccinations = vaccinations;
	}
	
	public List<Vaccination> getSelectedVaccinations(){
		return selectedVaccinations;
	}
}
