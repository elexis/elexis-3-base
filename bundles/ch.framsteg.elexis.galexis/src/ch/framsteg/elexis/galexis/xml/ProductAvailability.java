package ch.framsteg.elexis.galexis.xml;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

public class ProductAvailability {

	private Properties productAvailabilityRequestProperties;

	public ProductAvailability() {
		setProductAvailabilityRequestProperties(new Properties());
		try {
			getProductAvailabilityRequestProperties().load(ProductAvailability.class.getClassLoader()
					.getResourceAsStream("/resources/skeletons.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String checkAvailability(String ean) {
		String skeleton = getProductAvailabilityRequestProperties()
				.getProperty("productAvailability.request.message.skeleton");
		String parametrizedSkeleton = MessageFormat.format(skeleton, "052027", "Winter2025", "true", "true", "false",
				"true", "true", "3",
				"false", ean);
		System.out.println(parametrizedSkeleton);
		return parametrizedSkeleton;
	}

	public Properties getProductAvailabilityRequestProperties() {
		return productAvailabilityRequestProperties;
	}

	public void setProductAvailabilityRequestProperties(Properties productAvailabilityRequestProperties) {
		this.productAvailabilityRequestProperties = productAvailabilityRequestProperties;
	}

}
