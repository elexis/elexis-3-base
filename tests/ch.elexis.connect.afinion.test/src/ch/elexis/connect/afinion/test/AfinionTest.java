package ch.elexis.connect.afinion.test;

import static org.junit.Assert.assertEquals;

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
		assertEquals("Fehler bei der Messung",msg );
	}

	@Test
	public void testLocalizedConnectionName() throws PackageException{
		String msg = ch.elexis.connect.afinion.Messages.AfinionAS100Action_ConnectionName;
		assertEquals("Elexis-Afinion AS100", msg);
	}
	
}
