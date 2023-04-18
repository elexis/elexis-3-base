package ch.elexis.base.ch.ebanking.command;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ch.elexis.base.ch.ebanking.model.service.holder.ModelServiceHolder;
import ch.elexis.base.ch.ebanking.print.ESRFileJournalLetter;
import ch.elexis.base.ch.ebanking.print.ESRLetter;

public class OpenESRFileJournalHandler extends AbstractHandler {

	private static String esrFileJournalSelect = "SELECT DATUM, EINGELESEN, count(*) as ANZAHL, sum(BETRAGINRP) as BETRAG, FILE FROM ESRRECORDS"
			+ " WHERE FILE is not null AND ID != '1' AND CODE != '6' GROUP BY FILE ORDER BY DATUM DESC LIMIT 140";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		List<?> resultList = ModelServiceHolder.get().executeNativeQuery(esrFileJournalSelect)
				.collect(Collectors.toList());
		
		ESRLetter.print(new ESRFileJournalLetter(resultList));

		return null;
	}

}
