package ch.elexis.archie.wzw;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Verrechnet;

public class AlleLeistungenRoh extends BaseStats {
	static final String NAME = "Alle Leistungen roh";
	static final String DESC = "Listet sämtliche Leistungen im gegebenen Zeitraum";
	static final String[] HEADINGS = {
		"Mandant", "Patient-ID", "Patient-Name", "Patient Geschlecht", "Patient Alter", "Datum",
		"Gesetz", "Codesystem", "Code", "Text", "Anzahl", "Umsatz"
	};
	
	public AlleLeistungenRoh(){
		super(NAME, DESC, HEADINGS);
	}
	
	@Override
	protected IStatus createContent(IProgressMonitor monitor){
		List<Comparable<?>[]> lines = new ArrayList<Comparable<?>[]>(10000);
		List<Konsultation> conses = getConses(monitor);
		if (!conses.isEmpty()) {
			int clicksPerRound = HUGE_NUMBER / conses.size();
			for (Konsultation k : conses) {
				if (!k.isDeleted()) {
					Fall fall = k.getFall();
					if (fall != null) {
						Patient pat = fall.getPatient();
						Mandant m = k.getMandant();
						String md = m == null ? "?" : m.getLabel();
						String g = fall.getAbrechnungsSystem();
						if (pat != null) {
							for (Verrechnet v : k.getLeistungen()) {
								IVerrechenbar vv = v.getVerrechenbar();
								if (vv != null) {
									String[] line = new String[] {
										md, pat.getPatCode(), pat.getLabel(false),
										pat.getGeschlecht(), pat.getAlter(), k.getDatum(),
										g == null ? "?" : g, vv.getCodeSystemName(),
										vv.getCode() == null ? "?" : vv.getCode(), vv.getText(),
										Integer.toString(v.getZahl()),
										v.getNettoPreis().getAmountAsString()
									};
									lines.add(line);
								} else {
									System.out.println(v.getLabel());
								}
							}
						}
					}
				}
				monitor.worked(clicksPerRound);
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				
			}
		}
		// Und an Archie übermitteln
		this.dataSet.setContent(lines);
		return Status.OK_STATUS;
		
	}
	
}
