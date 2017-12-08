package at.medevit.elexis.impfplan.ui;

import java.util.Map.Entry;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Control;

import at.medevit.elexis.impfplan.model.po.Vaccination;

public class VaccinationCompositeMouseMoveListener implements MouseMoveListener {
	
	private VaccinationCompositePaintListener vcpl;
	private Vaccination selectedVacc;
	
	public VaccinationCompositeMouseMoveListener(VaccinationCompositePaintListener vcpl){
		this.vcpl = vcpl;
		selectedVacc = null;
	}
	
	@Override
	public void mouseMove(MouseEvent e){
		Entry<Integer, Vaccination> entry = vcpl.naviVacMap.floorEntry(e.y);
		if (entry == null)
			return;
		
		Vaccination vac = entry.getValue();
		
		if (vac != selectedVacc) {
			selectedVacc = vac;
			Control control = (Control) e.getSource();
			vcpl.setSelection(selectedVacc, control);
		}
	}
	
}
