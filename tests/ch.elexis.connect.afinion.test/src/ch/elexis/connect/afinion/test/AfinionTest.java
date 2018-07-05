package ch.elexis.connect.afinion.test;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import ch.elexis.connect.afinion.packages.PackageException;
import ch.elexis.connect.afinion.packages.Value;

public class AfinionTest {
	
		@Test
	public void testLocalizedValue() throws PackageException{
		Value val = Value.getValue("Chol", "mml/L");
		assertEquals("Chol", val.get_shortName() );
		assertEquals("Total Cholesterol", val.get_longName() );
	}

		@Test
	public void testLocalizedValueError() throws PackageException{
		String msg = ch.elexis.connect.afinion.Messages.Afinion_Value_Error;
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
		String msg = ch.elexis.connect.afinion.Messages.AfinionAS100Action_ConnectionName;
		assertEquals("Elexis-Afinion AS100", msg);
	}
	
}
