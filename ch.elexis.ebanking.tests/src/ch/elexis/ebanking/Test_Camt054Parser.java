package ch.elexis.ebanking;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.ebanking.parser.Camet054Exception;
import ch.elexis.ebanking.parser.Camt054Parser;
import ch.elexis.ebanking.parser.Camt054Record;
import junit.framework.Assert;

public class Test_Camt054Parser {
	
	private Logger logger = LoggerFactory.getLogger(Test_Camt054Parser.class);
	
	@Test
	public void testReadRecords() throws Camet054Exception, IOException{
		
		InputStream in = getInputStream();
		
		List<Camt054Record> camt054Records = new Camt054Parser().parseRecords(in);
		Assert.assertTrue(!camt054Records.isEmpty());
		for (Camt054Record l : camt054Records) {
			logger.debug(l.toString());
		}
		
		in.close();
	}
	
	@Test
	public void testInvalidRecords() {
		testInvalidRecord(null, null, null, null, null);
		testInvalidRecord("-1000", null, null, null, null);
		testInvalidRecord("1000", null, null, null, null);
		testInvalidRecord("1000", "12345678901234567890123456", null, null, null);
		testInvalidRecord("1000", "123456789012345678901234567", null, new Date(-1), null);
		testInvalidRecord("1000", "123456789012345678901234567", null, new Date(), new Date(-1));
		
		try {
			Camt054Record valid = new Camt054Record("1000", "123456789012345678901234567", null,
				new Date(), new Date());
			Assert.assertNotNull(valid);
		} catch (Camet054Exception e) {
			fail("not valid");
			
		}
		
	}
	
	private void testInvalidRecord(String amount, String reference, String tn, Date bookingDate,
		Date valueDate){
		try {
			Camt054Record camt054Record = new Camt054Record(amount, reference, tn, bookingDate, valueDate);
			fail("not valid");
		} catch (Camet054Exception e) {
			/*ignore*/
		}
	}

	private InputStream getInputStream(){
		return Test_Camt054Parser.class.getResourceAsStream(
			"/rsc/camt.054-ESR-ASR_P_CH1.xml");
	}
}
