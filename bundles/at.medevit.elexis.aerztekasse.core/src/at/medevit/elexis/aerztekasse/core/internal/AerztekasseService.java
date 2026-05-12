package at.medevit.elexis.aerztekasse.core.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

import at.medevit.elexis.aerztekasse.core.IAerztekasseService;
import ch.elexis.TarmedRechnung.XMLFileUtil;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;

@Component
public class AerztekasseService implements IAerztekasseService {

	private AerztkasseSettings settings;

	private Optional<String> currentToken;
	private long currentTokenTimestamp;

	@Activate
	public void activate() {
		settings = new AerztkasseSettings();
	}

	@Override
	public List<File> getXmlFiles(File sendDirectory) {
		if (sendDirectory.exists() && sendDirectory.isDirectory() && sendDirectory.canRead()) {
			File[] xmlFiles = sendDirectory.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.isFile() && pathname.getName().toLowerCase().endsWith("xml");
				}
			});
			return new ArrayList<>(Arrays.asList(xmlFiles));
		}
		return Collections.emptyList();
	}

	@Override
	public Result<Object> sendFiles(File sendDirectory) {
		Result<Object> ret = new Result<>();
		validDirectories(ret);
		if (!ret.isOK()) {
			return ret;
		}
		if (sendDirectory.exists() && sendDirectory.isDirectory() && sendDirectory.canRead()) {
			List<File> xmlFiles = getXmlFiles(sendDirectory);
			for (File file : xmlFiles) {
				Result<Object> fileResult = sendFile(file);
				if (!fileResult.isOK()) {
					ret.add(fileResult);
					moveToError(file);
				} else {
					moveToArchive(file);
				}
			}
		} else {
			return ret.add(SEVERITY.ERROR, 0, "Send directory [" + sendDirectory.getAbsolutePath() + "] is not valid",
					null, true);
		}
		return ret;
	}

	@Override
	public void moveToArchive(File file) {
		XMLFileUtil.moveToArchive(file, new File(settings.getArchiveDirectory()));
	}

	@Override
	public void moveToError(File file) {
		XMLFileUtil.moveToArchive(file, new File(settings.getErrorDirectory()));
	}

	private void validDirectories(Result<Object> result) {
		if (StringUtils.isBlank(settings.getArchiveDirectory()) || !new File(settings.getArchiveDirectory()).exists()) {
			result.add(SEVERITY.ERROR, 0,
					"No archive directory [" + settings.getArchiveDirectory() + "] is not valid", null, true);
		}
		if (StringUtils.isBlank(settings.getErrorDirectory()) || !new File(settings.getErrorDirectory()).exists()) {
			result.add(SEVERITY.ERROR, 0,
					"No error directory [" + settings.getErrorDirectory() + "] is not valid",
					null, true);
		}
	}

	@Override
	public Result<Object> sendFile(File invoiceFile) {
		Result<Object> ret = new Result<>();
		validDirectories(ret);
		if (!ret.isOK()) {
			return ret;
		}
		try {
			Optional<IInvoice> invoice = loadInvoiceForFilename(invoiceFile.getName());
			if (invoice.isPresent()) {
				String account = settings.getAccount(invoice.get().getMandator().getBiller());
				if (StringUtils.isEmpty(account)) {
					ret.add(SEVERITY.ERROR, 0,
							"No account for mandator [" + invoice.get().getMandator().getLabel() + "]", null, true);
					return ret;
				}
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpPost uploadFile = new HttpPost(settings.getXmlImportUrl());
				uploadFile.addHeader("cdm_account_number", account);
				uploadFile.addHeader("Authorization",
						"Bearer " + getToken()
						.orElseThrow(() -> new IllegalStateException("Could not get bearer token")));
				uploadFile.addHeader("Accept-Language", Locale.getDefault().getLanguage());
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				// This attaches the file to the POST:
				builder.addBinaryBody("File", new FileInputStream(invoiceFile), ContentType.APPLICATION_OCTET_STREAM,
						invoiceFile.getName());
				HttpEntity multipart = builder.build();
				uploadFile.setEntity(multipart);
				CloseableHttpResponse response = httpClient.execute(uploadFile);
				HttpEntity responseEntity = response.getEntity();
				String responseString = IOUtils.toString(responseEntity.getContent(), "UTF-8");

				if (response.getCode() >= 200 && response.getCode() < 300) {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder xmlbuilder = factory.newDocumentBuilder();
					Document doc = xmlbuilder.parse(new InputSource(new StringReader(responseString)));
					XPathFactory xPathfactory = XPathFactory.newInstance();
					XPath xpath = xPathfactory.newXPath();
					XPathExpression xPathExpr = xpath.compile("/uploadResponse");
					NodeList nodes = (NodeList) xPathExpr.evaluate(doc, XPathConstants.NODESET);
					if (nodes.getLength() == 1) {
						Element node = (Element) nodes.item(0);
						String result = node.getAttribute("result");
						if ("failure".equals(result)) {
							LoggerFactory.getLogger(getClass()).error("Upload failure\n" + responseString);
							ret.add(SEVERITY.ERROR, 0, "Upload result failure\n\n" + responseString, null, true);
							setInvoiceFailure(invoice.get(), node);
						} else {
							setInvoiceSuccess(invoice.get());
						}
					}
				} else {
					LoggerFactory.getLogger(getClass())
							.warn("Uploading xml failed [" + response.getCode() + " " + responseString + "]");
					if (responseString.contains("SameMD5Found")) {
						ret.add(SEVERITY.ERROR, 0,
								"Die Rechnung " + invoice.get().getNumber() + " wurde bereits übermittelt.", null,
								true);
					} else {
						ret.add(SEVERITY.ERROR, 0, "Upload result failure\n\n" + responseString, null, true);
					}
				}
			} else {
				ret.add(SEVERITY.ERROR, 0, "No invoice found for file [" + invoiceFile.getName() + "]", null, true);
			}
		} catch (IOException | ParserConfigurationException | SAXException
				| XPathExpressionException e) {
			LoggerFactory.getLogger(getClass()).error("Error uploading xml", e);
		}
		return ret;
	}

	private void setInvoiceSuccess(IInvoice invoice) {
		invoice.setState(InvoiceState.PAID);
		CoreModelServiceHolder.get().save(invoice);
	}

	private void setInvoiceFailure(IInvoice invoice, Element node) {
		StringBuilder sb = new StringBuilder();
		NodeList invoiceElements = node.getElementsByTagName("invoice");
		if (invoiceElements.getLength() == 1) {
			Element invoiceelement = (Element) invoiceElements.item(0);
			sb.append(invoiceelement.getAttribute("filename")).append(" - ");
			NodeList errorElements = invoiceelement.getElementsByTagName("error");
			if (errorElements.getLength() == 1) {
				Element errorelement = (Element) errorElements.item(0);
				sb.append(errorelement.getAttribute("message")).append(" [").append(errorelement.getAttribute("code"))
						.append("]");
			}
		} else {
			NodeList generalerrorelements = node.getElementsByTagName("generalError");
			if (generalerrorelements.getLength() == 1) {
				Element generalerrorelement = (Element) generalerrorelements.item(0);
				sb.append(generalerrorelement.getAttribute("message")).append(" [")
						.append(generalerrorelement.getAttribute("code")).append("]");
			}
		}
		invoice.reject(InvoiceState.REJECTCODE.REJECTED_BY_PEER, sb.toString());
		CoreModelServiceHolder.get().save(invoice);
	}

	private Optional<String> getToken() {
		if (currentToken != null && !currentToken.isEmpty()) {
			// refresh after 8h
			if (System.currentTimeMillis() - currentTokenTimestamp > (1000 * 60 * 60 * 8)) {
				currentToken = null;
				currentTokenTimestamp = 0;
			}
		}
		if (currentToken == null || currentToken.isEmpty()) {
			Optional<String> newToken = getAccessToken(settings.getUsername(), settings.getPassword(),
					settings.getTokenUrl());
			if (newToken.isPresent()) {
				currentToken = newToken;
				currentTokenTimestamp = System.currentTimeMillis();
			}
		}
		if (currentToken != null && !currentToken.isEmpty()) {
			return currentToken;
		}
		return Optional.empty();
	}

	private Optional<String> getAccessToken(String username, String password, String tokenUrl) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("grant_type", "password");
		parameters.put("username", StringUtils.defaultString(username));
		parameters.put("password", StringUtils.defaultString(password));
		parameters.put("client_id", getClientId());
		parameters.put("client_secret", getClientSecret());

		String form = parameters.entrySet().stream()
				.map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
				.collect(Collectors.joining("&"));

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(tokenUrl))
				.headers("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(form)).build();

		try {
			HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				Gson gson = new Gson();
				@SuppressWarnings("rawtypes")
				Map map = gson.fromJson(response.body().toString(), Map.class);
				String token = (String) map.get("access_token");
				LoggerFactory.getLogger(getClass()).info("Got access token");
				return Optional.of(token);
			} else {
				LoggerFactory.getLogger(getClass()).error("Getting access token failed [" + response.statusCode() + " "
						+ response.body().toString() + "]");
			}
		} catch (IOException | InterruptedException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting access token", e);
		}
		return Optional.empty();
	}

	private String getClientId() {
		try (InputStream properties = getClass().getResourceAsStream("/rsc/id.properties")) {
			if (properties != null) {
				Properties idProps = new Properties();
				idProps.load(properties);
				if (settings.isPreprod()) {
					return idProps.getProperty("preprod_client_id");
				} else {
					return idProps.getProperty("client_id");
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error loading id properties", e);
		}
		return StringUtils.EMPTY;
	}

	private String getClientSecret() {
		try (InputStream properties = getClass().getResourceAsStream("/rsc/id.properties")) {
			if (properties != null) {
				Properties idProps = new Properties();
				idProps.load(properties);
				if (settings.isPreprod()) {
					return idProps.getProperty("preprod_client_secret");
				} else {
					return idProps.getProperty("client_secret");
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error loading id properties", e);
		}
		return StringUtils.EMPTY;
	}

	@Override
	public boolean hasClientId() {
		return StringUtils.isNotBlank(getClientId());
	}

	@Override
	public boolean hasCredentials() {
		return StringUtils.isNotBlank(settings.getUsername()) && StringUtils.isNotBlank(settings.getPassword());
	}

	@Override
	public void setGlobalArchiveDir(String archiveDir) {
		settings.setArchiveDirectory(archiveDir);
	}

	@Override
	public Optional<String> getGlobalArchiveDir() {
		return Optional.ofNullable(settings.getArchiveDirectory());
	}

	@Override
	public void setGlobalErrorDir(String errorDir) {
		settings.setErrorDirectory(errorDir);
	}

	@Override
	public void setAccount(IMandator mandator, String account) {
		settings.setAccount(mandator, account);
	}

	@Override
	public Optional<String> getAccount(IMandator mandator) {
		if (StringUtils.isNotBlank(settings.getAccount(mandator))) {
			return Optional.of(settings.getAccount(mandator));
		}
		return Optional.empty();
	}

	public Optional<IInvoice> loadInvoiceForFilename(String filename) {
		String invNr = filename.replace(".xml", "");
		invNr = invNr.replace("_m1", "");
		invNr = invNr.replace("_m2", "");
		invNr = invNr.replace("_m3", "");
		invNr = invNr.replace("_storno", "");
		invNr = invNr.replaceAll(".*_", "");
		INamedQuery<IInvoice> query = CoreModelServiceHolder.get().getNamedQuery(IInvoice.class, "number");
		List<IInvoice> found = query.executeWithParameters(query.getParameterMap("number", invNr));
		if (!found.isEmpty()) {
			return Optional.of(found.get(0));
		}
		return Optional.empty();
	}

	@Override
	public Optional<String> getUsername() {
		if (StringUtils.isNotBlank(settings.getUsername())) {
			return Optional.of(settings.getUsername());
		}
		return Optional.empty();
	}

	@Override
	public Optional<String> getPassword() {
		if (StringUtils.isNotBlank(settings.getPassword())) {
			return Optional.of(settings.getPassword());
		}
		return Optional.empty();
	}
}
