package ch.elexis.barcode.scanner;

import org.osgi.service.event.EventHandler;

/**
 * Use the {@link BarcodeScannerMessage} with {@link EventHandler}.
 * 
 * <pre>
&#64;Component(property = { EventConstants.EVENT_TOPIC + "=" + ElexisEventTopics.EVENT_UPDATE })
public class BarcodeScannerMessageHandler implements EventHandler {

	&#64;Override
	public void handleEvent(Event event) {
		if (event.getTopic().equals(ElexisEventTopics.EVENT_UPDATE)) {
			if (event.getProperty("org.eclipse.e4.data") instanceof BarcodeScannerMessage) {
				BarcodeScannerMessage b = (BarcodeScannerMessage) event.getProperty("org.eclipse.e4.data"); *
 * </pre>
 * 
 * @author med1
 *
 */

public class BarcodeScannerMessage {
	private String name;
	private String port;
	private String chunk;

	public BarcodeScannerMessage(String name, String port, String chunk) {
		super();
		this.name = name;
		this.port = port;
		this.chunk = chunk;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getChunk() {
		return chunk;
	}

	public void setChunk(String chunk) {
		this.chunk = chunk;
	}

	@Override
	public String toString() {
		return "BarcodeScannerMessage [name=" + name + ", port=" + port + ", chunk=" + chunk + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

}
