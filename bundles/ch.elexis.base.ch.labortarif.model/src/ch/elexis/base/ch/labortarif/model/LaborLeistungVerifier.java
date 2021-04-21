package ch.elexis.base.ch.labortarif.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import ch.elexis.base.ch.labortarif.ILaborLeistung;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IEncounter;
import ch.rgw.tools.Result;

public class LaborLeistungVerifier implements IBillableVerifier {
	
	@Override
	public Result<IBillable> verifyAdd(IBillable billable, IEncounter encounter, double amount){
		if (billable instanceof ILaborLeistung) {
			ILaborLeistung laborLeistung = (ILaborLeistung) billable;
			LocalDate date = encounter.getDate().toLocalDate();
			if (laborLeistung.isValidOn(date)) {
				return Result.OK();
			} else {
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
				return new Result<IBillable>(Result.SEVERITY.ERROR, 2,
					laborLeistung.getCode() + " ("
						+ laborLeistung.getValidFrom().format(dateFormatter) + "-"
						+ laborLeistung.getValidTo().format(dateFormatter)
						+ ") Gültigkeit beinhaltet nicht das Konsultationsdatum "
						+ date.format(dateFormatter),
					billable, false);
				
			}
		}
		return new Result<IBillable>(Result.SEVERITY.ERROR, 2,
			"Verrechneter code [" + billable + "] ist keine Laborleistung", billable, true);
	}
}
