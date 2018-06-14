package ch.elexis.connect.sysmex.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import ch.elexis.connect.sysmex.packages.PackageException;
import ch.elexis.connect.sysmex.packages.Value;

public class  SysmexProbeTest{
	
	
	@Test
	public void testLocalizedValue() throws PackageException{
		Value val = Value.getValuePOCH("RDW-SD");
		assertEquals("RDW-SD", val.get_longName() );
		assertEquals("RDW-SD", val.get_shortName() );
	}

	@Test
	public void testLocalizedValueError() throws PackageException{
		String msg = ch.elexis.connect.sysmex.Messages.Sysmex_Value_Error;
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
		String msg = ch.elexis.connect.sysmex.Messages.SysmexAction_ConnectionName;
		assertTrue(msg.matches("Elexis.Sysmex"));// is either Elexis Sysmex or Elexis-Sysmex
	}
	
}
