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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import ch.elexis.core.services.IConfigService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.framsteg.elexis.labor.teamw.beans.LabOrder;

public class MessageBuilder {

	private final static String RAW_MESSAGE = "props.app.message.parametrizeable";
	private final static String CLIENT_IP = "props.teamw.message.property.client.ip";
	private final static String CLIENT_TYPE = "props.teamw.message.property.client.type";
	private final static String SERIAL_NUME = "props.teamw.message.property.serial.num";
	private final static String LOG_UNSIGNED_HASHED_SIGNATURE = "props.app.log.unsigned.hashed.signature";
	private final static String LOG_UNSIGNED_UNHASHED_SIGNATURE = "props.app.log.unsigned.unhashed.signature";

	private final static String MESSAGE_LANGUAGE = "props.teamw.message.language";
	private final static String MESSAGE_PATIENT_DATA_FORMAT = "props.teamw.message.patient.data.format";

	private static final String USERNAME_KEY = "key.teamw.username";
	private static final String PASSWORD_KEY = "key.teamw.password";


	private Properties applicationProperties;
	private Properties teamwProperties;
	
	Logger logger = LoggerFactory.getLogger(MessageBuilder.class);

	@Inject
	private IConfigService configService;

	public MessageBuilder(Properties applicationProperties, Properties teamwProperties) {
		setApplicationProperties(applicationProperties);
		setTeamwProperties(teamwProperties);
		CoreUiUtil.injectServices(this);
	}

	public String build(LabOrder labOrder)
			throws SAXException, IOException, ParserConfigurationException, TransformerException, InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException, SignatureException {

		// Step 1 Load Bare Bone Message
		String rawMessage = getApplicationProperties().getProperty(RAW_MESSAGE);
		Printer printer = new Printer();
		printer.print(rawMessage);

		// Step 2 Create/set timestamp
		TimeStampCreator timeStampCreator = new TimeStampCreator(getApplicationProperties());
		// timeStampCreator.test();
		String timeStamp = timeStampCreator.getUTCTimeStamp();

		// Step 3 Create Client Hash (login+clientIP)
		String login = configService.get(USERNAME_KEY, "");
		String clientIP = getTeamwProperties().getProperty(CLIENT_IP);
		String clientHashRaw = login + clientIP;
		String clientHash = DigestUtils.sha1Hex(clientHashRaw);

		// Step 4 Signture building
		String signatureUnsigned = timeStampCreator.getSimpleTimeStamp() + login + clientHash;
		logger.info(getApplicationProperties().getProperty(LOG_UNSIGNED_UNHASHED_SIGNATURE));
		logger.info(signatureUnsigned);
		String signatureUnsignedHashed = DigestUtils.sha1Hex(signatureUnsigned);
		logger.info(LOG_UNSIGNED_HASHED_SIGNATURE);
		logger.info(signatureUnsignedHashed);
		
		// Step 5 Signing
		Signer signer = new Signer(getApplicationProperties(), getTeamwProperties());
		String signatureSigned = signer.sign(signatureUnsigned);

		String gdt = labOrder.getGdtInstanceBase64();

		// Step 6 Fill in tags into message bare bone
		String language = getTeamwProperties().getProperty(MESSAGE_LANGUAGE);
		String patientInfoFormat = getTeamwProperties().getProperty(MESSAGE_PATIENT_DATA_FORMAT);

		String message = new String();
		message = MessageFormat.format(rawMessage, timeStamp, getTeamwProperties().getProperty(CLIENT_TYPE),
				getTeamwProperties().getProperty(SERIAL_NUME), configService.get(USERNAME_KEY, ""),
				configService.get(PASSWORD_KEY, ""),
				getTeamwProperties().getProperty(CLIENT_IP), clientHash, signatureSigned, language, patientInfoFormat,
				gdt);

		// printer.print(message);
		return message;
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
