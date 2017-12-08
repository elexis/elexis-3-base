/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.ui.dialog;

import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

import com.eclipsesource.databinding.multivalidation.DateTimeObservableValue;

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartnerProvider;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6301;
import at.medevit.elexis.gdt.tools.GDTCommPartnerCollector;
import at.medevit.elexis.gdt.ui.dialog.provider.ComboViewerCommPartner;
import at.medevit.elexis.gdt.ui.dialog.provider.ComboViewerGeschlechtLabelProvider;
import at.medevit.elexis.gdt.ui.dialog.provider.ComboViewerVersichertenartLabelProvider;

public class StammdatenUebermittelnDialog extends TitleAreaDialog {
	private Text txtIDReceiver;
	private Text txtIDSender;
	private Text txtPatientenKennung;
	private Text txtPatientNachname;
	private Text txtPatientVorname;
	private Text txtTitel;
	private Text txtVersichertenNr;
	private Text txtStrasse;
	private Text txtOrt;
	private Text txtGroesse;
	private Text txtGewicht;
	private Text txtMuttersprache;

	private GDTSatzNachricht6301 gdt6301 = null;
	private ComboViewer comboViewerGeschlecht;
	private ComboViewer comboViewerVersichertenart;
	private ComboViewer comboViewerTarget;

	private IGDTCommunicationPartner commPartner;
	private DateTime dateTimeBirthday;
	
	private String preSelCommPartner;
	private String targetIdSelection;
	
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public StammdatenUebermittelnDialog(Shell parentShell, GDTSatzNachricht6301 gdtSatz,
		String preSelCommPartner){
		super(parentShell);
		this.gdt6301 = gdtSatz;
		this.preSelCommPartner = preSelCommPartner;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(ResourceManager.getPluginImage("at.medevit.elexis.gdt",
				"rsc/icons/TitleIcon6301.png"));
		setMessage("GDT Satznachricht 6301");
		setTitle("Stammdaten übermitteln");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(4, true));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblZiel = new Label(container, SWT.NONE);
		lblZiel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblZiel.setText("Ziel");

		comboViewerTarget = new ComboViewer(container, SWT.NONE);
		Combo combo = comboViewerTarget.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3,
				1));
		comboViewerTarget
				.setContentProvider(ArrayContentProvider.getInstance());
		comboViewerTarget.setLabelProvider(new ComboViewerCommPartner());
		List<IGDTCommunicationPartner> commPartners = GDTCommPartnerCollector
				.getRegisteredCommPartners();
		comboViewerTarget.setInput(commPartners);
		comboViewerTarget
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) comboViewerTarget
								.getSelection();
						IGDTCommunicationPartner cp = (IGDTCommunicationPartner) selection
								.getFirstElement();
						commPartner = cp;
						txtIDReceiver.setText(cp.getIDReceiver());
						if (cp.getOutgoingDefaultCharset() >= 0)
							gdt6301.setValue(
									GDTConstants.FELDKENNUNG_VERWENDETER_ZEICHENSATZ,
									cp.getOutgoingDefaultCharset() + "");
					}
				});

		Label lblGdtidEmpfnger = new Label(container, SWT.NONE);
		lblGdtidEmpfnger.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblGdtidEmpfnger.setText("ID Empfänger");

		txtIDReceiver = new Text(container, SWT.BORDER);
		txtIDReceiver.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		txtIDReceiver.setTextLimit(8);

		Label lblGdtidSender = new Label(container, SWT.NONE);
		lblGdtidSender.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblGdtidSender.setText("ID Sender");

		txtIDSender = new Text(container, SWT.BORDER);
		txtIDSender.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		txtIDSender.setTextLimit(8);

		Label lblNewLabel = new Label(container, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 4, 1);
		gd_lblNewLabel.heightHint = 5;
		lblNewLabel.setLayoutData(gd_lblNewLabel);

		Group groupPatient = new Group(container, SWT.NONE);
		groupPatient.setText("Notwendige Patienten-Daten");
		groupPatient.setLayout(new GridLayout(5, false));
		groupPatient.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 4, 1));

		Label lblPatientKennung = new Label(groupPatient, SWT.NONE);
		lblPatientKennung.setText("Nummer/Kennung");

		txtPatientenKennung = new Text(groupPatient, SWT.BORDER);
		txtPatientenKennung.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		txtPatientenKennung.setTextLimit(10);
		new Label(groupPatient, SWT.NONE);

		Label lblGeburtsdatum = new Label(groupPatient, SWT.NONE);
		lblGeburtsdatum.setText("Geburtsdatum");

		dateTimeBirthday = new DateTime(groupPatient, SWT.BORDER
				| SWT.DROP_DOWN);
		dateTimeBirthday.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblPatientNachname = new Label(groupPatient, SWT.NONE);
		lblPatientNachname.setText("Nachname");

		txtPatientNachname = new Text(groupPatient, SWT.BORDER);
		txtPatientNachname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		txtPatientNachname.setTextLimit(28);
		new Label(groupPatient, SWT.NONE);

		Label lblPatientVorname = new Label(groupPatient, SWT.NONE);
		lblPatientVorname.setText("Vorname");

		txtPatientVorname = new Text(groupPatient, SWT.BORDER);
		txtPatientVorname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		txtPatientVorname.setTextLimit(28);

		Group grpOptionaleDaten = new Group(container, SWT.NONE);
		grpOptionaleDaten.setText("Optionale Patienten-Daten");
		grpOptionaleDaten.setLayout(new GridLayout(5, false));
		grpOptionaleDaten.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 4, 1));

		Label lblTitel = new Label(grpOptionaleDaten, SWT.NONE);
		lblTitel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblTitel.setText("Titel");

		txtTitel = new Text(grpOptionaleDaten, SWT.BORDER);
		txtTitel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		new Label(grpOptionaleDaten, SWT.NONE);

		Label lblGeschlecht = new Label(grpOptionaleDaten, SWT.NONE);
		lblGeschlecht.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblGeschlecht.setText("Geschlecht");

		comboViewerGeschlecht = new ComboViewer(grpOptionaleDaten, SWT.NONE);
		Combo comboGeschlecht = comboViewerGeschlecht.getCombo();
		comboViewerGeschlecht.setContentProvider(ArrayContentProvider
				.getInstance());
		comboViewerGeschlecht.setInput(new String[] {
				GDTConstants.SEX_MALE + "", GDTConstants.SEX_FEMALE + "" });
		comboViewerGeschlecht
				.setLabelProvider(new ComboViewerGeschlechtLabelProvider());
		comboGeschlecht.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblVersichertennr = new Label(grpOptionaleDaten, SWT.NONE);
		lblVersichertennr.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblVersichertennr.setText("Versicherten-Nr.");

		txtVersichertenNr = new Text(grpOptionaleDaten, SWT.BORDER);
		txtVersichertenNr.setTextLimit(12);
		txtVersichertenNr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		new Label(grpOptionaleDaten, SWT.NONE);

		Label lblVersichertenart = new Label(grpOptionaleDaten, SWT.NONE);
		lblVersichertenart.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblVersichertenart.setText("Versichertenart");

		comboViewerVersichertenart = new ComboViewer(grpOptionaleDaten,
				SWT.NONE);
		Combo comboVersichertenart = comboViewerVersichertenart.getCombo();
		comboVersichertenart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		comboViewerVersichertenart.setContentProvider(ArrayContentProvider
				.getInstance());
		comboViewerVersichertenart.setInput(new String[] {
				GDTConstants.VERSICHERTENART_FAMILIENVERSICHERTER + "",
				GDTConstants.VERSICHERTENART_MITGLIED + "",
				GDTConstants.VERSICHERTENART_RENTNER + "" });
		comboViewerVersichertenart
				.setLabelProvider(new ComboViewerVersichertenartLabelProvider());

		Label lblStrasse = new Label(grpOptionaleDaten, SWT.NONE);
		lblStrasse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblStrasse.setText("Strasse");

		txtStrasse = new Text(grpOptionaleDaten, SWT.BORDER);
		txtStrasse.setTextLimit(28);
		txtStrasse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		new Label(grpOptionaleDaten, SWT.NONE);

		Label lblOrt = new Label(grpOptionaleDaten, SWT.NONE);
		lblOrt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblOrt.setText("Ort");

		txtOrt = new Text(grpOptionaleDaten, SWT.BORDER);
		txtOrt.setTextLimit(30);
		txtOrt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Label lblGroesse = new Label(grpOptionaleDaten, SWT.NONE);
		lblGroesse.setToolTipText("Größe in cm");
		lblGroesse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblGroesse.setText("Größe");

		txtGroesse = new Text(grpOptionaleDaten, SWT.BORDER);
		txtGroesse.setTextLimit(10);
		txtGroesse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		new Label(grpOptionaleDaten, SWT.NONE);

		Label lblGewicht = new Label(grpOptionaleDaten, SWT.NONE);
		lblGewicht.setToolTipText("Gewicht in kg");
		lblGewicht.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblGewicht.setText("Gewicht");

		txtGewicht = new Text(grpOptionaleDaten, SWT.BORDER);
		txtGewicht.setTextLimit(10);
		txtGewicht.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblMuttersprache = new Label(grpOptionaleDaten, SWT.NONE);
		lblMuttersprache.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblMuttersprache.setText("Muttersprache");

		txtMuttersprache = new Text(grpOptionaleDaten, SWT.BORDER);
		txtMuttersprache.setTextLimit(60);
		txtMuttersprache.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		new Label(grpOptionaleDaten, SWT.NONE);
		new Label(grpOptionaleDaten, SWT.NONE);
		new Label(grpOptionaleDaten, SWT.NONE);

		initDataBindings();
		if (commPartners != null && commPartners.size() > 0) {
			comboViewerTarget.setSelection(new StructuredSelection(commPartners.get(0)));
			if (preSelCommPartner != null) {
				for (IGDTCommunicationPartner gdtCommPartner : commPartners) {
					if (preSelCommPartner.equals(gdtCommPartner.getClass().getName())) {
						comboViewerTarget.setSelection(new StructuredSelection(gdtCommPartner));
					}
				}
			}
			else if (targetIdSelection != null) {
				int idx = 0;
				for (IGDTCommunicationPartner igdtCommunicationPartner : commPartners) {
					if (igdtCommunicationPartner instanceof IGDTCommunicationPartnerProvider) {
						String id = ((IGDTCommunicationPartnerProvider) igdtCommunicationPartner)
							.getId();
						if (id.equals(targetIdSelection))
						{
							comboViewerTarget
								.setSelection(new StructuredSelection(commPartners.get(idx)));
							break;
						}
					}
					idx++;
				}
			}
		}
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);

	}

	protected void initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		IObservableMap gdt6301ValuesObserveMap = PojoObservables.observeMap(
				gdt6301, "values", Integer.class, String.class);

		Text[] control = { txtPatientenKennung, txtPatientNachname,
				txtPatientVorname, txtIDReceiver, txtIDSender, txtGewicht,
				txtGroesse, txtMuttersprache, txtOrt, txtStrasse,
				txtTitel, txtVersichertenNr };
		int[] property = {
				GDTConstants.FELDKENNUNG_PATIENT_KENNUNG,
				GDTConstants.FELDKENNUNG_PATIENT_NAME,
				GDTConstants.FELDKENNUNG_PATIENT_VORNAME,
				GDTConstants.FELDKENNUNG_GDT_ID_EMPFAENGER,
				GDTConstants.FELDKENNUNG_GDT_ID_SENDER,
				GDTConstants.FELDKENNUNG_PATIENT_GEWICHT,
				GDTConstants.FELDKENNUNG_PATIENT_GROESSE,
				GDTConstants.FELDKENNUNG_PATIENT_MUTTERSPRACHE,
				GDTConstants.FELDKENNUNG_PATIENT_WOHNORT,
				GDTConstants.FELDKENNUNG_PATIENT_STRASSE,
				GDTConstants.FELDKENNUNG_PATIENT_TITEL,
				GDTConstants.FELDKENNUNG_PATIENT_VERSICHERTENNUMMER };

		for (int i = 0; i < control.length; i++) {
			bindMapValue(control[i], property[i], bindingContext,
					gdt6301ValuesObserveMap);
		}

		IObservableValue widgetValueGeschlecht = ViewerProperties
				.singleSelection().observe(comboViewerGeschlecht);
		IObservableValue observableMapValueGeschlecht = Observables
				.observeMapEntry(gdt6301ValuesObserveMap,
						GDTConstants.FELDKENNUNG_PATIENT_GESCHLECHT,
						String.class);
		bindingContext.bindValue(widgetValueGeschlecht,
				observableMapValueGeschlecht);

		IObservableValue widgetValueVersichertenart = ViewerProperties
				.singleSelection().observe(comboViewerVersichertenart);
		IObservableValue observableMapValueVersichertenart = Observables
				.observeMapEntry(gdt6301ValuesObserveMap,
						GDTConstants.FELDKENNUNG_PATIENT_VERSICHERTENART,
						String.class);
		bindingContext.bindValue(widgetValueVersichertenart,
				observableMapValueVersichertenart);

		IObservableValue widgetValueGeburtstag = new DateTimeObservableValue(
				dateTimeBirthday);
		IObservableValue observableValueGeburtstag = Observables
				.observeMapEntry(gdt6301ValuesObserveMap,
						GDTConstants.FELDKENNUNG_PATIENT_GEBURTSDATUM,
						String.class);
		UpdateValueStrategy geburtstagUvs = new UpdateValueStrategy();
		geburtstagUvs.setConverter(new DateTimeTargetToModelUVS());
		Binding bday = bindingContext.bindValue(widgetValueGeburtstag,
				observableValueGeburtstag, geburtstagUvs, null);
		bday.updateTargetToModel();
	}

	private void bindMapValue(Text text, int feldkennung,
			DataBindingContext bindingContext,
			IObservableMap gdt6302ValuesObserveMap) {
		IObservableValue textObserveWidget = SWTObservables.observeText(text,
				SWT.Modify);
		IObservableValue observableMapValue = Observables.observeMapEntry(
				gdt6302ValuesObserveMap, feldkennung, String.class);
		bindingContext.bindValue(textObserveWidget, observableMapValue);
	}

	public IGDTCommunicationPartner getGDTCommunicationPartner() {
		return commPartner;
	}
	
	public void setTargetIdSelection(String targetIdSelection){
		this.targetIdSelection = targetIdSelection;
	}
	
	public String getTargetIdSelection(){
		return targetIdSelection;
	}
}
