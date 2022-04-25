package ch.medshare.licence;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.StringTokenizer;

import javax.crypto.NoSuchPaddingException;

import ch.medshare.util.SystemProperties;
import ch.medshare.util.UtilMisc;
import ch.medshare.util.UtilSecurity;

public class MedshareLicence {
	private final String vorname;
	private final String name;
	private final String ean;
	private String module;
	private boolean isValid = false;

	public MedshareLicence(String vorname, String name, String ean, String module) {
		this.vorname = vorname;
		this.module = module;
		this.name = name;
		this.ean = ean;
	}

	public MedshareLicence(String vorname, String name, String ean, byte[] bytes, PublicKey publicKey)
			throws GeneralSecurityException, IOException, ClassNotFoundException, NoSuchPaddingException {
		this.vorname = vorname;
		this.name = name;
		this.ean = ean;
		readContents(bytes, publicKey);
	}

	private void readContents(byte[] bytes, PublicKey publicKey)
			throws GeneralSecurityException, IOException, ClassNotFoundException {
		boolean isValid1 = false;
		boolean isValid2 = false;
		String content = UtilSecurity.decryptAsym(bytes, publicKey);

		StringTokenizer tokenizer = new StringTokenizer(content, ";"); //$NON-NLS-1$

		if (tokenizer.hasMoreElements()) {
			isValid1 = tokenizer.nextElement().equals(getHash1());
		}

		if (tokenizer.hasMoreElements()) {
			isValid2 = tokenizer.nextElement().equals(getHash2());
		}
		isValid = isValid1 || isValid2;

		if (tokenizer.hasMoreElements()) {
			this.module = tokenizer.nextToken();
		}
	}

	public byte[] getEncrypted(PrivateKey privateKey)
			throws GeneralSecurityException, IOException, ClassNotFoundException, NoSuchPaddingException {
		String content = getHash1() + ";" + getHash2() + ";" + this.module; //$NON-NLS-1$
		return UtilSecurity.encryptAsym(content, privateKey);
	}

	public String getHash1() throws NoSuchProviderException, NoSuchAlgorithmException, UnsupportedEncodingException {
		String vorname = this.vorname;
		String name = this.name;
		if (vorname == null) {
			vorname = UtilMisc.getRandomStr();
		}
		if (name == null) {
			name = UtilMisc.getRandomStr();
		}
		String hashStr = name.trim() + vorname.trim();
		return UtilSecurity.toMD5(hashStr);
	}

	public String getHash2() throws NoSuchProviderException, NoSuchAlgorithmException, UnsupportedEncodingException {
		String ean = this.ean;
		if (ean == null || ean.length() == 0) {
			ean = UtilMisc.getRandomStr();
		}
		return UtilSecurity.toMD5(ean.trim());
	}

	public String getVorname() {
		return vorname;
	}

	public String getModule() {
		return module;
	}

	public String getName() {
		return name;
	}

	public String getEAN() {
		return ean;
	}

	public boolean isValid() {
		return isValid;
	}

	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(getModule());
		strBuf.append(SystemProperties.LINE_SEPARATOR);

		return strBuf.toString();
	}
}
