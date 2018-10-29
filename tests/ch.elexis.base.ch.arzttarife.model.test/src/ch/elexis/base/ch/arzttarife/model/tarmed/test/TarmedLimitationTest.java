package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLimitation;
import ch.elexis.core.model.IBilled;

public class TarmedLimitationTest extends AbstractTarmedTest {
	
	@Test
	public void findVerrechnetByMandatorPatientCodeDuringPeriod(){
		before();
		
		TarmedLeistung tlGroupLimit1 = TarmedLeistung.getFromCode("02.0310", LocalDate.now(), null);
		result = billSingle(encounter, tlGroupLimit1);
		result = billSingle(encounter, tlGroupLimit1);
		
		List<IBilled> alreadyBilled = TarmedLimitation
			.findVerrechnetByMandatorPatientCodeDuringPeriod(encounter.getMandator(),
				encounter.getCoverage().getPatient(), tlGroupLimit1.getCode());
		assertEquals(1, alreadyBilled.size());
		assertEquals(2, alreadyBilled.get(0).getAmount(), 0.01);
		
		after();
	}
	
	@Test
	public void getVerrechnetByMandantAndCodeDuring(){
		
	}
	
}
