package ch.medshare.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class UtilSecurity {
	public static Charset CHARSET = Charset.forName("UTF-8"); //$NON-NLS-1$
	
	private static String getProvider(){
		Provider prov = Security.getProvider("BC"); //$NON-NLS-1$
		if (prov == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		return "BC"; //$NON-NLS-1$
	}
	
	public static String toMD5(String input) throws NoSuchAlgorithmException,
		NoSuchProviderException, UnsupportedEncodingException{
		// Some stuff we will use later
		MessageDigest md = null;
		byte[] byteHash = null;
		StringBuffer resultString = new StringBuffer();
		
		md = MessageDigest.getInstance("MD5", getProvider()); // 128-Bit Hash (32 Zeichen) //$NON-NLS-1$
		
		md.reset();
		
		// We really need some conversion here
		md.update(input.getBytes(CHARSET.name()));
		
		// There goes the hash
		byteHash = md.digest();
		
		// Now here comes the best part
		for (int i = 0; i < byteHash.length; i++) {
			resultString.append(Integer.toHexString(0xFF & byteHash[i]));
		}
		
		// That's it!
		return resultString.toString();
	}
	
	/**
	 * Public/Private Key generator: Algorithm: RSA (DH, RSA, DSA )
	 */
	public static KeyPair getNewAsymmetricKey() throws NoSuchAlgorithmException,
		NoSuchProviderException{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", //$NON-NLS-1$
			getProvider());
		keyGen.initialize(2048);
		return keyGen.genKeyPair();
	}
	
	/**
	 * Symmetric Key generator: Algorithm: DES
	 */
	public static SecretKey getNewSymmetricKey() throws NoSuchAlgorithmException,
		NoSuchProviderException{
		return KeyGenerator.getInstance("DES", getProvider()).generateKey(); //$NON-NLS-1$
	}
	
	public static byte[] encryptAsym(String input, Key key) throws GeneralSecurityException,
		IOException, ClassNotFoundException{
		Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1PADDING", //$NON-NLS-1$
			getProvider());
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(input.getBytes(CHARSET.name()));
	}
	
	public static String decryptAsym(byte[] input, Key key) throws GeneralSecurityException,
		IOException, ClassNotFoundException{
		Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1PADDING", //$NON-NLS-1$
			getProvider());
		cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(input), CHARSET.name());
	}
	
	public static byte[] encryptSym(String input, SecretKey key) throws GeneralSecurityException,
		IOException, ClassNotFoundException{
		Cipher cipher = Cipher.getInstance("DES", getProvider()); //$NON-NLS-1$
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(input.getBytes(CHARSET.name()));
	}
	
	public static String decryptSym(byte[] input, SecretKey key) throws GeneralSecurityException,
		IOException, ClassNotFoundException{
		
		Cipher cipher = Cipher.getInstance("DES", getProvider()); //$NON-NLS-1$
		cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(input), CHARSET.name());
	}
}
