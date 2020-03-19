package ch.elexis.connect.sysmex.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import ch.elexis.connect.sysmex.test.AllTests;

public class SysmexKx21nTest {
	
	@Test
	public void kx21n_1() throws IOException, PackageException{
		InputStream in = AllTests.class.getResourceAsStream("/rsc/KX-21N_1.txt");
		String content = AllTests.getTextBetween(AllTests.STX, AllTests.ETX, in);
		assertNotNull(content);
		KX21NData data = new KX21NData();
		assertEquals(data.getSize(), content.length());
		data.parse(content);
		Value value = data.getValue("WBC");
		assertNotNull(value);
		assertEquals("WBC", value.get_shortName());
	}
}
