package ch.elexis.archie.wzw;

import java.util.ArrayList;
import java.util.HashMap;
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
		
		List<IEncounter> conses = getConses(monitor);
		if (conses.size() > 0) {
			int clicksPerRound = HUGE_NUMBER / conses.size();
			HashMap<String, TarifStat> tstats = new HashMap<String, TarifStat>();
			for (IEncounter k : conses) {
				IMandator m = k.getMandator();
				if (m != null) {
					ICoverage fall = k.getCoverage();
					if (fall != null) {
						IPatient pat = fall.getPatient();
						if (pat != null) {
							List<IBilled> vr = k.getBilled();
							for (IBilled v : vr) {
								IBillable vv = v.getBillable();
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
									ts.count += v.getAmount();
									ts.umsatz += v.getTotal().doubleValue();
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
