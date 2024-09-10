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
package ch.framsteg.elexis.covercard.control;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

import ch.elexis.core.services.IConfigService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.framsteg.elexis.covercard.dao.CardInfoData;
import ch.framsteg.elexis.covercard.dao.JDOMCardInfoDocument;
import ch.framsteg.elexis.covercard.exceptions.BlockedCardException;
import ch.framsteg.elexis.covercard.exceptions.InvalidCardException;
import ch.framsteg.elexis.covercard.exceptions.UnsupportedCardException;

public class CardInfoRetriever {

	private static final String APACHE_NON_VALIDATING = "apache.non.validating.url";
	private static final String XXX_PLACEHOLDER = "xxx.placeholder";
	private static final String YYY_PLACEHOLDER = "yyy.placeholder";

	private static final String KEY_URL = "key.url";
	private static final String KEY_XML_PARAMETER = "key.parameter.xml";

	private static final String KEY_PROXY_SERVER = "key.proxy.server";
	private static final String KEY_PROXY_PORT = "key.proxy.port";

	private Properties applicationProperties;

	@Inject
	private IConfigService configService;

	public CardInfoRetriever(Properties applicationProperties, Properties messagesProperties) {
		this.applicationProperties = applicationProperties;
		CoreUiUtil.injectServices(this);
	}

	public CardInfoRetriever(String id) {
		CoreUiUtil.injectServices(this);
	}

	public CardInfoData getCardInfo(String id)
			throws ParserConfigurationException, ClientProtocolException, IOException, UnsupportedOperationException,
			SAXException, JDOMException, HttpHostConnectException, NullPointerException, InvalidCardException, UnsupportedCardException, BlockedCardException {

		String rawUrl = configService.get(applicationProperties.getProperty(KEY_URL), "");
		String typedUrl = rawUrl.replace(applicationProperties.getProperty(XXX_PLACEHOLDER),
				configService.get(applicationProperties.getProperty(KEY_XML_PARAMETER), ""));
		String personalizedUrl = typedUrl.replace(applicationProperties.getProperty(YYY_PLACEHOLDER), id);
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpHost proxy = new HttpHost(configService.get(applicationProperties.getProperty(KEY_PROXY_SERVER), ""),
				Integer.valueOf(configService.get(applicationProperties.getProperty(KEY_PROXY_PORT), ""))
						.intValue());
		HttpGet httpGet = new HttpGet(personalizedUrl);
		RequestConfig defaultRequestConfig = RequestConfig.custom().setProxy(proxy).build();

		httpGet.setConfig(defaultRequestConfig);
		CloseableHttpResponse response = httpClient.execute(httpGet);
		DocumentBuilderFactory.newInstance();

		SAXBuilder sbuilder = new SAXBuilder();
		sbuilder.setFeature(applicationProperties.getProperty(APACHE_NON_VALIDATING), false);
		
		Document doc = sbuilder.build(response.getEntity().getContent());

		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		xmlOutputter.output(doc, System.out);
		
		JDOMCardInfoDocument cardInfoDocument = new JDOMCardInfoDocument(doc, applicationProperties);
		return cardInfoDocument.unmarshall();
	}
}
