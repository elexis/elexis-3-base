package ch.medshare.connect.abacusjunior.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import ch.medshare.connect.abacusjunior.packages.Value;

public class AbacusJunionTest {
	

	@Test
	public void testLocalizedValue() {
		Value val = Value.getValue("P05");
		assertEquals("Mittleres korpuskuläres Volumen", val.get_longName() );
		assertEquals("MCV", val.get_shortName() );
	}

	@Test
	public void testLocalizedValueError() {
		String msg =ch.medshare.connect.abacusjunior.Messages.AbacusJunior_Value_Error;
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
	public void testLocalizedConnectionName() {
		String msg = ch.medshare.connect.abacusjunior.Messages.AbacusJuniorAction_ConnectionName;
		assertEquals("Elexis-AbacusJunior", msg);
	}
	
}
