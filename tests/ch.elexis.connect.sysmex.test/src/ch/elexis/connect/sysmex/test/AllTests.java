package ch.elexis.connect.sysmex.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.connect.sysmex.packages.SysmexKx21nTest;
import ch.elexis.connect.sysmex.packages.SysmexUc1000Test;

@RunWith(Suite.class)
@SuiteClasses({
	SysmexProbeTest.class, SysmexUc1000Test.class, SysmexKx21nTest.class
})
public class AllTests {

	public static final int STX = 0x02;
	public static final int ETX = 0x03;
	
	public static String getTextBetween(int start, int end, InputStream input) throws IOException{
		byte[] bytes = getBytesBetween(start, end, input);
		return new String(bytes);
	}
	
	public static byte[] getBytesBetween(int start, int end, InputStream input) throws IOException{
		try (ByteArrayOutputStream output = new ByteArrayOutputStream();
				ByteArrayOutputStream partOutput = new ByteArrayOutputStream()) {
			IOUtils.copy(input, output);
			byte[] bytes = output.toByteArray();
			boolean started = false;
			for (byte bite : bytes) {
				if (started) {
					if (bite == end) {
						return partOutput.toByteArray();
					}
					partOutput.write(bite);
				} else {
					if (bite == start) {
						started = true;
					}
				}
			}
		}
		return null;
	}
}
