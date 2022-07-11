package ch.elexis.barcode.scanner;

import ch.elexis.core.data.events.ElexisEventDispatcher;

/**
 * User the {@link BarcodeScannerMessage} with {@link ElexisEventDispatcher}
 * register it as follows: <code>elexisEventListenerImpl =
			new ElexisEventListenerImpl(BarcodeScannerMessage.class, ElexisEvent.EVENT_UPDATE) {
				public void run(ElexisEvent ev){
					BarcodeScannerMessage b = (BarcodeScannerMessage) ev.getGenericObject();
					BarcodeInputHandler.execute(b.getChunk());
				}
			};
		ElexisEventDispatcher.getInstance().addListeners(elexisEventListenerImpl);</code>
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
