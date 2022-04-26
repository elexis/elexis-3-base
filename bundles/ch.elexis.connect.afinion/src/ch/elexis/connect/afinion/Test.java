package ch.elexis.connect.afinion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Test {
	private static final int NUL = 0x00;
	private static final int STX = 0x02;
	private static final int ETX = 0x03;
	private static final int ACK = 0x06;
	private static final int DLE = 0x10;
	private static final int NAK = 0x15;
	private static final int ETB = 0x17;
	private static final int LF = 0x0D;

	private static ByteArrayOutputStream read(InputStream inputStream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// <DLE><ETB> suchen
		int data = inputStream.read();
		while (data != -1 && data != ETB) {
			while (data != -1 && data != DLE) {
				baos.write(data);
				data = inputStream.read();
			}
			if (data == DLE) { // <DLE><DLE> wird zweites DLE nicht beachtet
				baos.write(data);
				data = inputStream.read();
				if (data != DLE) {
					baos.write(data);
				}
				data = inputStream.read();
			}
		}
		return baos;
	}

	private static void write(OutputStream outputStream, byte[] bytes) throws IOException {
		for (byte b : bytes) {
			write(outputStream, b);
		}
	}

	private static void write(OutputStream outputStream, byte b) throws IOException {
		outputStream.write(b);
		if (b == DLE) { // Aus <DLE> wird <DLE><DLE>
			outputStream.write(DLE);
		}
	}

	private static void printlnHex(final byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			String hex = Long.toHexString((long) bytes[i]);
			while (hex.length() < 2) {
				hex = "0" + hex;
			}
			System.out.print("0x" + hex + ", ");
		}
		System.out.println();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		byte[] bArray = new byte[] { 0x09, DLE, STX, 0x01, 0x02, 0x03, 0x10, 0x10, 0x04, 0x05, DLE, ETX };

		printlnHex(bArray);

		byte[] retBytes = null;

		try {
			ByteArrayOutputStream baos = read(new ByteArrayInputStream(bArray));
			retBytes = baos.toByteArray();
			printlnHex(retBytes);

		} catch (Throwable t) {
			t.printStackTrace();
		}

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			write(baos, retBytes);
			byte[] bytes = baos.toByteArray();
			printlnHex(bytes);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
