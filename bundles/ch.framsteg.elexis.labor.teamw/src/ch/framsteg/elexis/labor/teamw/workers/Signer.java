/*******************************************************************************
 * Copyright 2024 Framsteg GmbH / olivier.debenath@framsteg.ch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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

import javax.inject.Inject;

import ch.elexis.core.services.IConfigService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;

public class Signer {

	private final static String FACTORY_TYPE = "props.app.key.factory.type";
	private final static String SIGNATURE_TYPE = "props.app.signature.type";

	private static final String PATH_KEY = "key.teamw.path";

	private Properties applicationProperties;
	private Properties teamwProperties;

	@Inject
	private IConfigService configService;

	public Signer(Properties applicationProperties, Properties teamwProperties) {
		setApplicationProperties(applicationProperties);
		setTeamwProperties(teamwProperties);
		CoreUiUtil.injectServices(this);
	}

	public String sign(String toSign) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
			InvalidKeyException, SignatureException {
		String signed = new String();

		byte[] keyBytes = Files.readAllBytes(Paths.get(configService.get(PATH_KEY, "")));

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
