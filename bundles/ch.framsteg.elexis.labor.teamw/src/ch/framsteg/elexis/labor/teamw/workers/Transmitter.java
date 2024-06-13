package ch.framsteg.elexis.labor.teamw.workers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Transmitter {

	private final static String INTERNAL_BROWSER = "props.app.use.internal.browser";
	private final static String WRONG_BROWSER_CONFIGURATION = "props.msg.wrong.browser.configuration";
	private final static String HINT = "props.msg.lab.order.hint";
	private final static String LAB_ORDER_PERSONALIZED = "props.msg.lab.order.personalized";
	private final static String ERROR_TITLE = "props.msg.title.error";
	private final static String POST_URL = "props.teamw.post.url";
	private final static String HEADER_URL = "props.teamw.header.url";
	private final static String HEADER_ACTION = "props.teamw.header.action";

	private Properties applicationProperties;
	private Properties messagesProperties;
	private Properties teamwProperties;

	private String name;
	private String prename;

	public Transmitter(Properties applicationProperties, Properties teamwProperties, Properties messagesProperties,
			String name, String prename) {
		setApplicationProperties(applicationProperties);
		setTeamwProperties(teamwProperties);
		setMessagesProperties(messagesProperties);
		setName(name);
		setPrename(prename);
	}

	public int transmit(String message) throws ClientProtocolException, IOException, ParserConfigurationException,
			UnsupportedOperationException, SAXException, URISyntaxException {

		HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
		StringEntity strEntity = new StringEntity(message, ContentType.APPLICATION_SOAP_XML);

		HttpPost post = new HttpPost(getTeamwProperties().getProperty(POST_URL));
		post.setHeader(getTeamwProperties().getProperty(HEADER_ACTION), getTeamwProperties().getProperty(HEADER_URL));
		post.setEntity(strEntity);

		HttpResponse response;
		int httpStatus = 0;

		response = httpClient.execute(post);

		httpStatus = response.getStatusLine().getStatusCode();

		HttpEntity respEntity = response.getEntity();

		if (respEntity != null) {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(respEntity.getContent());
			NodeList nameNodesList = doc.getElementsByTagName("url");
			ArrayList<String> nameValues = new ArrayList<String>();
			for (int i = 0; i < nameNodesList.getLength(); i++) {
				nameValues.add(nameNodesList.item(i).getTextContent());
			}
			if (getApplicationProperties().getProperty(INTERNAL_BROWSER).equalsIgnoreCase("false")) {
				Desktop.getDesktop().browse(new URL(nameValues.get(0)).toURI());
			} else if (getApplicationProperties().getProperty(INTERNAL_BROWSER).equalsIgnoreCase("true")) {
				WorkbenchBrowserSupport service = (WorkbenchBrowserSupport) PlatformUI.getWorkbench()
						.getBrowserSupport();
				try {
					URL url = new URL(nameValues.get(0));
					service.createBrowser(IWorkbenchBrowserSupport.AS_VIEW, null,
							MessageFormat.format(getMessagesProperties().getProperty(LAB_ORDER_PERSONALIZED),
									getName() + ", " + getPrename()),
							getMessagesProperties().getProperty(HINT)).openURL(url);
				} catch (PartInitException e) {

					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			} else {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(),
						getMessagesProperties().getProperty(ERROR_TITLE),
						MessageFormat.format(getMessagesProperties().getProperty(WRONG_BROWSER_CONFIGURATION),
								getApplicationProperties().getProperty(INTERNAL_BROWSER)));
			}

		}
		return httpStatus;
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

	public Properties getMessagesProperties() {
		return messagesProperties;
	}

	public void setMessagesProperties(Properties messagesProperties) {
		this.messagesProperties = messagesProperties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrename() {
		return prename;
	}

	public void setPrename(String prename) {
		this.prename = prename;
	}
}
