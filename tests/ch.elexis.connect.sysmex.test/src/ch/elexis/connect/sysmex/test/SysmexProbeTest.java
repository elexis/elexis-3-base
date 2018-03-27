package ch.elexis.connect.sysmex.test;

import static org.junit.Assert.assertEquals;

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
		String msg = ch.elexis.connect.sysmex.packages.Messages.getString("Value.Error");
		assertEquals("Fehler bei der Messung",msg );
	}

	@Test
	public void testLocalizedConnectionName() throws PackageException{
		String msg = ch.elexis.connect.sysmex.Messages.SysmexAction_ConnectionName;
		assertEquals("Elexis-Sysmex", msg);
	}
	
}
