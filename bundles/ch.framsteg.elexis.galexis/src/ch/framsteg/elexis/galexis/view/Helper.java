package ch.framsteg.elexis.galexis.view;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import javax.xml.namespace.QName;

import com.e_galexis.xml.v2.schemas.ProductPortType;

import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceFeature;

@WebServiceClient(name = "ProductService", targetNamespace = "http://xml.e-galexis.com/V2/schemas/", wsdlLocation = "https://xml.e-galexis.com/V2/wsdl/productAvailability.wsdl")
public class Helper extends Service {

	public final static URL PRODUCT_AVAILABILTY_WSDL_LOCATION;
	private final static WebServiceException PRODUCTSERVICE_EXCEPTION;

	static {
		URL url = null;
		WebServiceException e = null;
		try {
			url = new URI("https://xml.e-galexis.com/V2/wsdl/productAvailability.wsdl").toURL();
		} catch (MalformedURLException | URISyntaxException e1) {
			e = new WebServiceException(e1);
		}

		PRODUCT_AVAILABILTY_WSDL_LOCATION = url;
		PRODUCTSERVICE_EXCEPTION = e;
		// PRODUCT_AVAILABILTY_WSDL_LOCATION = Helper.class
		// .getResource("https://xml.e-galexis.com/V2/wsdl/productAvailability.wsdl");

	}

	private Helper(URL wsdlDocumentLocation, QName serviceName) {
		super(wsdlDocumentLocation, serviceName);
	}

	public Helper() {
		super(PRODUCT_AVAILABILTY_WSDL_LOCATION, new QName("http://xml.e-galexis.com/V2/schemas/", "ProductService"));
	}

	@WebEndpoint(name = "ProductPort")
	public ProductPortType getProductPortType() {
		ClassLoader tccl = Thread.currentThread().getContextClassLoader();

		Thread.currentThread().setContextClassLoader(Service.class.getClassLoader());
		ProductPortType port = super.getPort(new QName("http://xml.e-galexis.com/V2/schemas/", "ProductPort"),
				ProductPortType.class);
		// Client client = ClientProxy.getClient(port);
		// client.getInInterceptors().add(new
		// AbstractPhaseInterceptor<org.apache.cxf.message.Message>(Phase.RECEIVE) {
		// public void handleMessage(org.apache.cxf.message.Message message) {
		// message.put(org.apache.cxf.message.Message.CONTENT_TYPE, "text/xml");
		// }
		// });
		Thread.currentThread().setContextClassLoader(tccl);

		return port;
	}

	@WebEndpoint(name = "ProductPort")
	public ProductPortType getProductPortType(WebServiceFeature... features) {
		return super.getPort(new QName("http://xml.e-galexis.com/V2/schemas/", "ProductPort"), ProductPortType.class,
				features);
	}

	private static URL __getWsdlLocation() {
		if (PRODUCTSERVICE_EXCEPTION != null) {
			throw PRODUCTSERVICE_EXCEPTION;
		}
		return PRODUCT_AVAILABILTY_WSDL_LOCATION;
	}

}
