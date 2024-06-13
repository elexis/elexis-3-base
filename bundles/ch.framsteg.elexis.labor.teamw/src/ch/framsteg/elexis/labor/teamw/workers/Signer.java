package ch.framsteg.elexis.labor.teamw.workers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Properties;

public class Signer {

	private static final String KEY_PATH = "props.teamw.teamw.key.path";
	private final static String FACTORY_TYPE = "props.app.key.factory.type";
	private final static String SIGNATURE_TYPE = "props.app.signature.type";

	private Properties applicationProperties;
	private Properties teamwProperties;

	public Signer(Properties applicationProperties, Properties teamwProperties) {
		setApplicationProperties(applicationProperties);
		setTeamwProperties(teamwProperties);
	}

	public String sign(String toSign) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
			InvalidKeyException, SignatureException {
		String signed = new String();

		byte[] keyBytes = Files.readAllBytes(Paths.get(getTeamwProperties().getProperty(KEY_PATH)));

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

		KeyFactory kf = KeyFactory.getInstance(getApplicationProperties().getProperty(FACTORY_TYPE));
		PrivateKey privateKey = kf.generatePrivate(spec);

		Signature privateSignature = Signature.getInstance(getApplicationProperties().getProperty(SIGNATURE_TYPE));
		privateSignature.initSign(privateKey);
		privateSignature.update(toSign.getBytes());

		byte[] signature = privateSignature.sign();
		signed = Base64.getEncoder().encodeToString(signature);

		return signed;
	}

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public Properties getTeamwProperties() {
		return teamwProperties;
	}

	public void setTeamwProperties(Properties teamwProperties) {
		this.teamwProperties = teamwProperties;
	}
}
