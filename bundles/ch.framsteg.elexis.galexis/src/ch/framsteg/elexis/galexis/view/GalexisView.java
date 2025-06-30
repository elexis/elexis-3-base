package ch.framsteg.elexis.galexis.view;

import java.math.BigInteger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import ch.framsteg.elexis.galexis.xml.Client;
import ch.framsteg.elexis.galexis.xml.ProductAvailability;
import jakarta.xml.ws.Service;

public class GalexisView extends ViewPart {

	private Button test;

	@Override
	public void createPartControl(Composite parent) {

		// System.setProperty("javax.xml.ws.spi.Provider",
		// "com.sun.xml.ws.spi.ProviderImpl");
		// ClassLoader tccl = Thread.currentThread().getContextClassLoader();

		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;

		GridData buttonsGridData = new GridData();
		buttonsGridData.horizontalAlignment = SWT.FILL;
		buttonsGridData.heightHint = 50;

		composite.setLayout(gridLayout);
		composite.setLayoutData(gridLayout);

		Label label = new Label(composite, SWT.NONE);
		label.setText("PIPAPO");
		test = new Button(composite, SWT.PUSH);
		test.setText("Test");
		test.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * try { Helper helper = new Helper(); ProductPortType productPort =
				 * helper.getProductPortType();
				 * 
				 * Client client = new Client(); client.setNumber("052027");
				 * client.setPassword("Winter2025");
				 * 
				 * ProductAvailabilityRequest productAvailabilityRequest = new
				 * ProductAvailabilityRequest(); productAvailabilityRequest.setClient(client);
				 * 
				 * ProductAvailabilityLines productAvailabilityLines = new
				 * ProductAvailabilityLines(); ProductAvailabilityLine productAvailabilityLine =
				 * new ProductAvailabilityLine();
				 * productAvailabilityLine.setQuantity(BigInteger.valueOf(3));
				 * productAvailabilityLine.setRoundUpForCondition(false); Product product = new
				 * Product(); EAN ean = new EAN(); BigInteger bi = new
				 * BigInteger("7680516821578"); ean.setId(bi); product.setEAN(ean);
				 * 
				 * productAvailabilityLines.getProductAvailabilityLine().add(
				 * productAvailabilityLine);
				 * productAvailabilityRequest.setProductAvailabilityLines(
				 * productAvailabilityLines);
				 * 
				 * ProductAvailabilityResponse productAvailabilityResponse = new
				 * ProductAvailabilityResponse();
				 * 
				 * productAvailabilityResponse =
				 * productPort.getAvailability(productAvailabilityRequest); } catch
				 * (ErrorResponse | OfflineResponse_Exception e1) { e1.printStackTrace(); }
				 */

				ProductAvailability productAvailability = new ProductAvailability();

				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Test",
						Client.send(productAvailability.checkAvailability("7680516821578"))
						);
			}
		});
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
