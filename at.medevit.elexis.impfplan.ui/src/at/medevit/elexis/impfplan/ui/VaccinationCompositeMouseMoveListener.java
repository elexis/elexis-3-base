package at.medevit.elexis.impfplan.ui;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

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
		Vaccination vac = vcpl.naviVacMap.floorEntry(e.y).getValue();
		if (vac != null && !vac.equals(selectedVacc)) {
			selectedVacc = vac;
			vcpl.setSelection(selectedVacc);
			updateVaccinationUi();
		}
		
		if (vac == null) {
			selectedVacc = null;
			vcpl.setSelection(selectedVacc);
			updateVaccinationUi();
		}
	}
	
	/**
	 * update so selection becomes visible
	 */
	private void updateVaccinationUi(){
		IViewPart viewPart =
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(VaccinationView.PART_ID);
		VaccinationView vaccView = (VaccinationView) viewPart;
		vaccView.updateUi(false);
	}
}
