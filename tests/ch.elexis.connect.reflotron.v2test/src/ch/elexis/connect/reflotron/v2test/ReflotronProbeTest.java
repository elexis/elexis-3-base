package ch.elexis.connect.reflotron.v2test;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.elexis.connect.reflotron.packages.PackageException;
import ch.elexis.connect.reflotron.packages.Probe;
import ch.elexis.connect.reflotron.packages.Value;

public class ReflotronProbeTest {
	
	public static String p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11;
	private static String SEPARATOR = ";";
	private static int NAME = 0;
	private static int SHORT = 1;
	private static int VALUE = 2;
	private static int UNIT = 3;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		p1 = "GGT < 2.80 U/l 25°C";
		p2 = "CHOL >600 mg/dl";
		p3 = "CHOL 240 mg/dl";
		p4 = "K 105.00 mmol/l";
		p5 = "GGT <5.00 U/l    37øC   ";
		p6 = "CHOL<15.26mmol/l 20°C";
		p7 = "I'll fail";
		p8 = "CREA  88.3    umol/l";
		p9 = "GOT  25.3 U/l    37øC ";
		p10 = "GPT  17.9 U/l    37øC   ";
		p11 = "GLU   9.28    mmol/l   ";
	}
	
	@Test
	public void testWrite() throws PackageException{
		//		Patient pat = new Patient("reflotest", "reflotest", "", "");
		String[] dummyArray =
			{
				"", "15.12.14 C1 08:57:57", "0,m", "GGT <5.00 U/l    37øC",
				"ElexisReflotronUnitTestRunning"
			};
		
		Probe probe = new Probe(dummyArray);
		probe.setResult(p1);
		String[] fields = probe.write(null).split(SEPARATOR);
		assertEquals("GGT", fields[NAME]);
		assertEquals("GGT", fields[SHORT]);
		assertEquals("<2.80", fields[VALUE]);
		assertEquals("U/l", fields[UNIT]);
		
		probe.setResult(p2);
		fields = probe.write(null).split(SEPARATOR);
		assertEquals("Cholesterol", fields[NAME]);
		assertEquals("CHOL", fields[SHORT]);
		assertEquals(">600", fields[VALUE]);
		assertEquals("mg/dl", fields[UNIT]);
		
		probe.setResult(p3);
		fields = probe.write(null).split(SEPARATOR);
		assertEquals("Cholesterol", fields[NAME]);
		assertEquals("CHOL", fields[SHORT]);
		assertEquals("240", fields[VALUE]);
		assertEquals("mg/dl", fields[UNIT]);
		
		probe.setResult(p4);
		fields = probe.write(null).split(SEPARATOR);
		assertEquals("Kalium", fields[NAME]);
		assertEquals("K", fields[SHORT]);
		assertEquals("105.00", fields[VALUE]);
		assertEquals("mmol/l", fields[UNIT]);
		
		probe.setResult(p5);
		fields = probe.write(null).split(SEPARATOR);
		assertEquals("GGT", fields[NAME]);
		assertEquals("GGT", fields[SHORT]);
		assertEquals("<5.00", fields[VALUE]);
		assertEquals("U/l", fields[UNIT]);
		
		probe.setResult(p6);
		fields = probe.write(null).split(SEPARATOR);
		assertEquals("Cholesterol", fields[NAME]);
		assertEquals("CHOL", fields[SHORT]);
		assertEquals("<15.26", fields[VALUE]);
		assertEquals("mmol/l", fields[UNIT]);
		
		probe.setResult(p7);
		try {
			probe.write(null).split(SEPARATOR);
		} catch (PackageException e) {
			String msg =  e.getMessage();
	        Locale locale = new Locale(System.getProperty("user.language"), System.getProperty("user.country"));
	        if (locale.getLanguage().equals("de"))
	        {
	        	// Our most common case
	    		assertEquals("Resultat der Probe zu klein!",msg );
	        } else if (locale.getLanguage().equals("en"))
	        {
	        	// This is the case when running under CI via gitlab/travis
	    		assertEquals("Result of the sample too small!",msg );
	        }  else {
	        	System.out.println(String.format("Skipping test for language {} produced {}",
	        		locale.getLanguage(), msg));
	        }
		}
		
		probe.setResult(p8);
		fields = probe.write(null).split(SEPARATOR);
		assertEquals("Creatinin", fields[NAME]);
		assertEquals("CREA", fields[SHORT]);
		assertEquals("88.3", fields[VALUE]);
		assertEquals("umol/l", fields[UNIT]);
		
		probe.setResult(p9);
		fields = probe.write(null).split(SEPARATOR);
		assertEquals("GOT", fields[NAME]);
		assertEquals("GOT", fields[SHORT]);
		assertEquals("25.3", fields[VALUE]);
		assertEquals("U/l", fields[UNIT]);
		
		probe.setResult(p10);
		fields = probe.write(null).split(SEPARATOR);
		assertEquals("GPT", fields[NAME]);
		assertEquals("GPT", fields[SHORT]);
		assertEquals("17.9", fields[VALUE]);
		assertEquals("U/l", fields[UNIT]);
		
		probe.setResult(p11);
		fields = probe.write(null).split(SEPARATOR);
		assertEquals("Glukose", fields[NAME]);
		assertEquals("GLU", fields[SHORT]);
		assertEquals("9.28", fields[VALUE]);
		assertEquals("mmol/l", fields[UNIT]);
	}

	@Test
	public void testLocalizedValue() throws PackageException{
		Value val = Value.getValue("AMYL", "U/l");
		assertEquals("Gesamt-Amylase", val.get_longName() );
		assertEquals("AMYL", val.get_shortName() );
	}

	@Test
	public void testLocalizedValueError() throws PackageException{
		String msg = ch.elexis.connect.reflotron.Messages.Reflotron_Value_Error;
        Locale locale = new Locale(System.getProperty("user.language"), System.getProperty("user.country"));
        if (locale.getLanguage().equals("de"))
        {
        	// Our most common case
    		assertEquals("Fehler bei der Messung",msg );
        } else if (locale.getLanguage().equals("en"))
        {
        	// This is the case when running under CI via gitlab/travis
    		assertEquals("Error in the measurement",msg );
        }  else {
        	System.out.println(String.format("Skipping test for language {} produced {}",
        		locale.getLanguage(), msg));
        }
    }

	@Test
	public void testLocalizedConnectionName() throws PackageException{
		String msg = ch.elexis.connect.reflotron.Messages.ReflotronSprintAction_ConnectionName;
		assertEquals("Elexis-Roche Reflotron", msg);
	}
	
}
