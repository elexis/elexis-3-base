package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLimitation;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class TarmedLimitationTest extends AbstractTarmedTest {

	@Test
	public void findVerrechnetByPatientCodeDuringPeriod() {
		before();

		encounter.setDate(LocalDate.of(2025, 12, 31));
		CoreModelServiceHolder.get().save(encounter);

		TarmedLeistung tlGroupLimit1 = TarmedLeistung.getFromCode("02.0310", LocalDate.of(2025, 12, 31), null);
		result = billSingle(encounter, tlGroupLimit1);
		assertTrue(result.getMessages().toString(), result.isOK());
		result = billSingle(encounter, tlGroupLimit1);
		assertTrue(result.getMessages().toString(), result.isOK());

		List<IBilled> alreadyBilled = TarmedLimitation
				.findVerrechnetByPatientCodeDuringPeriod(encounter.getCoverage().getPatient(), tlGroupLimit1.getCode());
		assertEquals(1, alreadyBilled.size());
		assertEquals(2, alreadyBilled.get(0).getAmount(), 0.01);

		after();
	}
}
