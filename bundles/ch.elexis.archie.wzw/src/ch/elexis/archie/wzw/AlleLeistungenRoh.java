package ch.elexis.archie.wzw;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;

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
		List<IEncounter> conses = getConses(monitor);
		if (!conses.isEmpty()) {
			int clicksPerRound = HUGE_NUMBER / conses.size();
			for (IEncounter k : conses) {
				if (!k.isDeleted()) {
					ICoverage fall = k.getCoverage();
					if (fall != null) {
						IPatient pat = fall.getPatient();
						IMandator m = k.getMandator();
						String md = m == null ? "?" : m.getLabel();
						String g = fall.getBillingSystem().getName();
						if (pat != null) {
							for (IBilled v : k.getBilled()) {
								IBillable vv = v.getBillable();
								if (vv != null) {
									String[] line = new String[] {
										md, pat.getPatientNr(), pat.getLabel(),
										pat.getGender().toString(),
										Integer.toString(pat.getAgeInYears()),
										k.getDate().toString(),
										g == null ? "?" : g, vv.getCodeSystemName(),
										vv.getCode() == null ? "?" : vv.getCode(), vv.getText(),
										Double.toString(v.getAmount()),
										v.getTotal().getAmountAsString()
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
