package ch.medshare.util;

import java.util.GregorianCalendar;
import java.util.Random;

public class UtilMisc {
	final static Random random = new Random(new GregorianCalendar().getTimeInMillis());

	public static String replaceWithForwardSlash(String path) {
		String[] parts = path.split("[\\\\]"); //$NON-NLS-1$
		String newPath = ""; //$NON-NLS-1$
		for (String part : parts) {
			newPath += part + "/"; //$NON-NLS-1$
		}
		return newPath.substring(0, newPath.length() - 1);
	}

	public static String getRandomStr() {
		int max = random.nextInt(40);
		char[] chars = new char[max];
		for (int i = 0; i < max; i++) {
			chars[i] = (char) (random.nextInt(88) + 32);
		}
		return new String(chars);
	}
}
