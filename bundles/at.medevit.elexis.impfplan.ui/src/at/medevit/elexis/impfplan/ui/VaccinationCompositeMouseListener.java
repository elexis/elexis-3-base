package at.medevit.elexis.impfplan.ui;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.program.Program;

public class VaccinationCompositeMouseListener implements MouseListener {

	private VaccinationCompositePaintListener vcpl;

	public VaccinationCompositeMouseListener(VaccinationCompositePaintListener vcpl) {
		this.vcpl = vcpl;
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		if (vcpl.isTitleArea()) {
			Program.launch(
					"https://www.bag.admin.ch/dam/bag/de/dokumente/mt/i-und-b/richtlinien-empfehlungen/neue-empfehlungen-2019/schweizerischer-impfplan-synopsis.pdf.download.pdf/schweizerischer-impfplan-synopsis-de.pdf");
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {

	}

	@Override
	public void mouseUp(MouseEvent e) {
	}
}
