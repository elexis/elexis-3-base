package ch.medshare.connect.abacusjunior.test;

import static org.junit.Assert.assertEquals;

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
		assertEquals("Fehler bei der Messung",msg );
	}

	@Test
	public void testLocalizedConnectionName() {
		String msg = ch.medshare.connect.abacusjunior.Messages.AbacusJuniorAction_ConnectionName;
		assertEquals("Elexis-AbacusJunior", msg);
	}
	
}
