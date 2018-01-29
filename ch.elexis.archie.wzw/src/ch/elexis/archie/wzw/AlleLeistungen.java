package ch.elexis.archie.wzw;

import java.util.ArrayList;
import java.util.HashMap;
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
import ch.rgw.tools.Money;

public class AlleLeistungen extends BaseStats {
	static final String NAME = "Leistungen-Hitliste";
	static final String DESC = "Listet sämtliche Leistungen im gegebenen Zeitraum";
	static final String[] HEADINGS = {
		"Codesystem", "Code", "Text", "Anzahl", "Umsatz"
	};
	
	public AlleLeistungen(){
		super(NAME, DESC, HEADINGS);
	}
	
	@Override
	protected IStatus createContent(IProgressMonitor monitor){
		final ArrayList<Comparable<?>[]> result = new ArrayList<Comparable<?>[]>();
		
		List<Konsultation> conses = getConses(monitor);
		if (conses.size() > 0) {
			int clicksPerRound = HUGE_NUMBER / conses.size();
			HashMap<String, TarifStat> tstats = new HashMap<String, TarifStat>();
			for (Konsultation k : conses) {
				Mandant m = k.getMandant();
				if (m != null) {
					Fall fall = k.getFall();
					if (fall != null) {
						Patient pat = fall.getPatient();
						if (pat != null) {
							List<Verrechnet> vr = k.getLeistungen();
							for (Verrechnet v : vr) {
								IVerrechenbar vv = v.getVerrechenbar();
								if (vv == null) {
									System.out.println(v.getLabel());
								} else {
									String sname = vv.getCodeSystemName();
									String scode = vv.getCode() == null ? "?" : vv.getCode();
									TarifStat ts = tstats.get(sname + scode);
									if (ts == null) {
										ts = new TarifStat();
										ts.tarif = vv.getCodeSystemName();
										ts.ziffer = vv.getCode() == null ? "?" : vv.getCode();
										ts.text = vv.getText();
										tstats.put(sname + scode, ts);
									}
									ts.count += v.getZahl();
									ts.umsatz += v.getNettoPreis().doubleValue() + v.getZahl();
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
			
			// Resultat-Array für Archie aufbauen
			for (TarifStat ts : tstats.values()) {
				Comparable<?>[] row = new Comparable<?>[this.dataSet.getHeadings().size()];
				row[0] = ts.tarif;
				row[1] = ts.ziffer;
				row[2] = ts.text;
				row[3] = new Integer(ts.count);
				row[4] = new Money(ts.umsatz);
				result.add(row);
			}
		}
		// Und an Archie übermitteln
		this.dataSet.setContent(result);
		return Status.OK_STATUS;
		
	}
	
	class TarifStat {
		
		String tarif;
		String ziffer;
		String text;
		int count;
		double umsatz;
	}
}
