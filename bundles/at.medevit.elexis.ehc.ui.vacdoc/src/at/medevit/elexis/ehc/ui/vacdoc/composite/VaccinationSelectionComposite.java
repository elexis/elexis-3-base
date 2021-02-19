package at.medevit.elexis.ehc.ui.vacdoc.composite;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import at.medevit.elexis.impfplan.model.po.Vaccination;
import ch.rgw.tools.TimeTool;

public class VaccinationSelectionComposite extends Composite {
	
	private TableViewer contentViewer;
	
	public VaccinationSelectionComposite(Composite parent, int style){
		super(parent, style);
		
		createContent();
	}
	
	private void createContent(){
		setLayout(new GridLayout());
		setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label label = new Label(this, SWT.NONE);
		label.setText("Auswahl der Impfungen mit gedrückter Strg Taste verändern.");
		
		contentViewer = new TableViewer(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
		Control control = contentViewer.getControl();
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.heightHint = 300;
		control.setLayoutData(gd);
		
		contentViewer.setContentProvider(new ArrayContentProvider());
		contentViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element){
				if (element instanceof Vaccination) {
					Vaccination vaccination = ((Vaccination) element);
					return vaccination.getDateOfAdministration().toString(TimeTool.DATE_GER) + " "
						+ vaccination.getBusinessName() + " (" + vaccination.getLotNo() + ") - "
						+ vaccination.getAdministratorLabel();
				}
				return super.getText(element);
			}
			
			@Override
			public Image getImage(Object element){
				return super.getImage(element);
			}
		});
	}
	
	public void addSelectionChangedListener(ISelectionChangedListener listener){
		contentViewer.addSelectionChangedListener(listener);
	}
	
	public void setInput(List<Vaccination> vaccinations){
		contentViewer.setInput(vaccinations);
	}
	
	public void setSelection(StructuredSelection structuredSelection, boolean reveal){
		contentViewer.setSelection(structuredSelection, reveal);
	}
	
	public IStructuredSelection getSelection(){
		return contentViewer.getStructuredSelection();
	}
}
