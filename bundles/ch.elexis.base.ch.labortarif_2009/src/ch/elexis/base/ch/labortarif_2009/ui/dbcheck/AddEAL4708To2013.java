package ch.elexis.base.ch.labortarif_2009.ui.dbcheck;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.statushandlers.StatusManager;

import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.data.status.ElexisStatus;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Query;
import ch.elexis.data.Verrechnet;
import ch.elexis.labortarif2009.data.Constants;
import ch.elexis.labortarif2009.data.Labor2009Tarif;
import ch.rgw.tools.TimeTool;

public class AddEAL4708To2013 extends ExternalMaintenance {
	
	TimeTool startDate = new TimeTool("1.1.2013");
	String errorString;
	
	@Override
	public String executeMaintenance(final IProgressMonitor pm, String DBVersion){
		IVerrechenbar EAL4708 = loadEAL4708();
		
		if (EAL4708 == null)
			return errorString;
		
		StringBuilder output = new StringBuilder();
		pm.beginTask("Bitte warten, EAL Zuschalg 4708.00 wird nachgetragen ...",
			IProgressMonitor.UNKNOWN);
		// get all Konsultationen of year 2013
		int addCount = 0;
		int konsultationCount = 0;
		
		Query<Konsultation> qbe = new Query<Konsultation>(Konsultation.class);
		TimeTool tt = new TimeTool(startDate.getTimeInMillis());
		qbe.add(Konsultation.FLD_DATE, Query.GREATER_OR_EQUAL, tt.toString(TimeTool.DATE_COMPACT));
		
		List<Konsultation> konsultationen = qbe.execute();
		for (Konsultation konsultation : konsultationen) {
			// only still open Konsultation
			if (konsultation.getRechnung() != null)
				continue;
			
			boolean Tarmed0010Found = false;
			boolean EAL4707Found = false;
			boolean EAL4708Found = false;
			
			List<Verrechnet> leistungen = konsultation.getLeistungen();
			for (Verrechnet leistung : leistungen) {
				IVerrechenbar verrechenbar = leistung.getVerrechenbar();
				if (verrechenbar.getCodeSystemName().equals("Tarmed")
					&& verrechenbar.getCode().equals("00.0010")) {
					Tarmed0010Found = true;
				}
				if (verrechenbar.getCodeSystemName().equals("EAL 2009")
					&& verrechenbar.getCode().equals("4707.00")) {
					EAL4707Found = true;
				}
				if (verrechenbar.getCodeSystemName().equals("EAL 2009")
					&& verrechenbar.getCode().equals("4708.00")) {
					EAL4708Found = true;
				}
			}
			
			if (Tarmed0010Found && EAL4707Found && !EAL4708Found) {
				System.out.println("Add to kons " + konsultation.getLabel());
				konsultation.addLeistung(EAL4708);
				addCount++;
			}
			konsultationCount++;
		}
		
		output.append(konsultationCount + " offene Konsultationen im Jahr 2013 gefunden.\n");
		output.append(addCount + " EAL Zuschlag 4708.00 nachgetragen.\n");
		
		pm.done();
		
		return output.toString();
	}
	
	public IVerrechenbar loadEAL4708(){
		try {
			Query<Labor2009Tarif> qEntries = new Query<Labor2009Tarif>(Labor2009Tarif.class);
			qEntries.add(Labor2009Tarif.FLD_CODE, "=", "4708.00");
			
			List<Labor2009Tarif> entries = qEntries.execute();
			for (Labor2009Tarif labor2009Tarif : entries) {
				if (labor2009Tarif.isValidOn(startDate))
					return labor2009Tarif;
			}
			errorString = "EAL Code 4708.00 nicht gefunden.";
			ElexisStatus status = new ElexisStatus(ElexisStatus.ERROR, Constants.pluginID,
				ElexisStatus.CODE_NOFEEDBACK, errorString, ElexisStatus.LOG_FATALS);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			return null;
		} catch (NoClassDefFoundError error) {
			errorString = "EAL Codesystem nicht gefunden.";
			ElexisStatus status = new ElexisStatus(ElexisStatus.ERROR, Constants.pluginID,
				ElexisStatus.CODE_NOFEEDBACK, errorString, ElexisStatus.LOG_FATALS);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			return null;
		}
	}
	
	@Override
	public String getMaintenanceDescription(){
		return "EAL Zuschlag 4708.00 in Konsultationen 2013 nachtragen";
	}
}
