package ch.framsteg.elexis.galexis.view;

import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import ch.framsteg.elexis.galexis.xml.Client;
import ch.framsteg.elexis.galexis.xml.ProductAvailability;

public class ProductAvailabilityView {

	private Label lblResult;

	public Composite buildView(Composite parent) {
		Composite rootComposite = new Composite(parent, SWT.NONE);
		GridLayout rootLayout = new GridLayout();
		rootLayout.numColumns = 1;
		GridData rootData = new GridData(SWT.FILL, SWT.FILL, true, true);
		rootComposite.setLayout(rootLayout);
		rootComposite.setLayoutData(rootData);

		Composite titleComposite = new Composite(rootComposite, SWT.NONE);
		GridLayout titleLayout = new GridLayout();
		titleLayout.numColumns = 1;
		GridData titleData = new GridData(SWT.FILL, SWT.FILL, true, false);
		titleComposite.setLayout(titleLayout);
		titleComposite.setLayoutData(titleData);

		Label lblTitle = new Label(titleComposite, SWT.NONE);
		lblTitle.setText("Anhand der EAN kann die Verfügbarkeit von Produkten bei Galexis abgefragt werden");

		Group searchGroup = new Group(rootComposite, SWT.NONE);
		searchGroup.setText("Suchoptionen");

		GridLayout searchConfigurationLayout = new GridLayout();
		searchConfigurationLayout.numColumns = 1;
		GridData searchConfigurationData = new GridData(GridData.FILL_HORIZONTAL);
		searchConfigurationData.grabExcessHorizontalSpace = true;

		searchGroup.setLayout(searchConfigurationLayout);
		searchGroup.setLayoutData(searchConfigurationData);
		searchGroup.setText("Suchkonfiguration");

		Label lblEan = new Label(searchGroup, SWT.NONE);
		lblEan.setText("EAN eingeben*");
		Text txtEan = new Text(searchGroup, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).hint(150, 25).applyTo(txtEan);
		Label lblAmount = new Label(searchGroup, SWT.NONE);
		lblAmount.setText("Anzahl Packungen angeben*");
		Combo cmbAmount = new Combo(searchGroup, SWT.NULL);
		cmbAmount.setItems(
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" });

		Button btnRoundUpForCondition = new Button(searchGroup, SWT.CHECK);
		btnRoundUpForCondition.setText("Menge aufrunden");
		btnRoundUpForCondition.setToolTipText(
				"Falls dadurch günstigere Konditionen erzielt werden, kann Galexis die Menge bis maximal 20% erhöhen");

		Button btnInternalTransfer = new Button(searchGroup, SWT.CHECK);
		btnInternalTransfer.setText("Interne Überweisung");
		btnInternalTransfer.setToolTipText(
				"Falls die Ware nicht verfügbar ist, wird sie in Rückstand genommen bis sie durch internen Transfer wieder verfügbar ist");
		btnInternalTransfer.setSelection(true);

		Button btnExternalDelivery = new Button(searchGroup, SWT.CHECK);
		btnExternalDelivery.setText("Externe Lieferung");
		btnExternalDelivery.setToolTipText(
				"Falls die Ware nicht im Lager verfügbar ist, wird sie in Rückstand genommen, bis sie durch externe Lieferung wieder verfügbar ist");
		btnExternalDelivery.setSelection(true);

		Button btnExternalPurchase = new Button(searchGroup, SWT.CHECK);
		btnExternalPurchase.setText("Externe Beschaffung");
		btnExternalPurchase.setToolTipText(
				"Falls die Ware nicht im Lager verfügbar ist, wird sie zurückdurch externen Einkauf wieder verfügbar ist");
		btnExternalPurchase.setSelection(true);

		Button btnPartialDelivery = new Button(searchGroup, SWT.CHECK);
		btnPartialDelivery.setText("Teillierferung");
		btnPartialDelivery.setToolTipText(
				"Falls die gesamte Positionsmenge nicht im Lager verfügbar ist, wird die verfügbare Menge geliefert und die Restmenge in den Rückstand genommen");

		Button btnSearch = new Button(searchGroup, SWT.PUSH);
		btnSearch.setText("Suche");
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
				if (txtEan.getText().isBlank() || cmbAmount.getText().isBlank()) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Unvollständige Suchangaben",
							"Die EAN und die Menge müssen angegeben werden");
				} else {
					if (txtEan.getText().length() != 13 || !pattern.matcher(txtEan.getText()).matches()) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Ungültige Suchangaben",
								"Ungültige EAN");
					} else if (!pattern.matcher(cmbAmount.getText()).matches()) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Ungültige Suchangaben",
								"Falsche Mengenangabe");
					} else {
						ProductAvailability productAvailability = new ProductAvailability();
						/*
						 * Client.send(productAvailability.checkAvailability(txtEan.getText(),
						 * cmbAmount.getText(), btnRoundUpForCondition.getSelection() ? "true" :
						 * "false", btnInternalTransfer.getSelection() ? "true" : "false",
						 * btnExternalDelivery.getSelection() ? "true" : "false",
						 * btnExternalPurchase.getSelection() ? "true" : "false",
						 * btnPartialDelivery.getSelection() ? "true" : "false"));
						 */
						String result = productAvailability.checkAvailability(txtEan.getText(), cmbAmount.getText(),
								btnRoundUpForCondition.getSelection() ? "true" : "false",
								btnInternalTransfer.getSelection() ? "true" : "false",
								btnExternalDelivery.getSelection() ? "true" : "false",
								btnExternalPurchase.getSelection() ? "true" : "false",
								btnPartialDelivery.getSelection() ? "true" : "false");

						System.out.println(result);
						lblResult.setText(productAvailability.checkAvailability(txtEan.getText(), cmbAmount.getText(),
								btnRoundUpForCondition.getSelection() ? "true" : "false",
								btnInternalTransfer.getSelection() ? "true" : "false",
								btnExternalDelivery.getSelection() ? "true" : "false",
								btnExternalPurchase.getSelection() ? "true" : "false",
								btnPartialDelivery.getSelection() ? "true" : "false"));
					}
				}
			}
		});

		Composite resultComposite = new Composite(rootComposite, SWT.NONE);
		// resultComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		GridLayout resultLayout = new GridLayout();
		resultLayout.numColumns = 1;
		GridData resultData = new GridData(SWT.FILL, SWT.FILL, true, true);
		resultComposite.setLayout(resultLayout);
		resultComposite.setLayoutData(resultData);

		lblResult = new Label(resultComposite, SWT.NONE);

		return rootComposite;
	}
}
