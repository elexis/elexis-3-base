/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.ui;

import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.ATCCodeLanguageConstants;
import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.ui.internal.ATCCodeServiceConsumer;
import at.medevit.ch.artikelstamm.ui.internal.DatabindingTextResizeConverter;
import at.medevit.ch.artikelstamm.ui.internal.IntToStringConverterSelbstbehalt;
import at.medevit.ch.artikelstamm.ui.internal.ModelServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.databinding.SavingUpdateValueStrategy;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.controls.ArticleDefaultSignatureComposite;
import ch.rgw.tools.Money;

public class DetailComposite extends ScrolledComposite {

	private DataBindingContext m_bindingContext;

	private WritableValue<IArtikelstammItem> item = new WritableValue<IArtikelstammItem>(null, IArtikelstammItem.class);

	public static String prefAtcLanguage = null;

	private Label lblDSCR;
	private Text txtPHZNR;
	private Text txtGTIN;
	private Label lblHERSTELLER;
	private Label lblEXFACTORYPRICE;
	private Text txtPUBLICPRICE;
	private Tree treeATC;
	private Label lblAbgabekategorie;
	private Text txtABGABEKATEGORIE;
	private Label lblSelbstbehalt;
	private Text lblSELBSTBEHALT;
	private Button btnCheckIsNarcotic;
	private Button btnLPPVEntry;
	private Button btnK70Entry;
	private Button btnlLimitation;
	private Label lblLimitationspunkte;
	private Text txtLIMITATIONPOINTS;
	private Label lblLimitationstext;
	private Text txtLIMITATIONTEXT;
	private Text txtProductNr;
	private ControlDecoration controlDecoIsCalculatedPPUB;
	private Button btnUserDefinedPrice;
	private Group grpDefaultSignature;
	private ArticleDefaultSignatureComposite adsc;
	private Composite mainComposite;

	public DetailComposite(Composite parent, int style, String atcCodeLanguage) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		mainComposite = new Composite(this, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, false));

		DetailComposite.prefAtcLanguage = atcCodeLanguage;

		Composite headerComposite = new Composite(mainComposite, SWT.NONE);
		headerComposite.setLayout(new GridLayout(4, false));
		headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblDSCR = new Label(headerComposite, SWT.NONE);
		lblDSCR.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDSCR.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		lblDSCR.setFont(SWTResourceManager.getFont("Lucida Grande", 16, SWT.BOLD)); //$NON-NLS-1$
		GridData gd_lblDSCR = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gd_lblDSCR.widthHint = 435;
		lblDSCR.setLayoutData(gd_lblDSCR);

		Composite topComposite = new Composite(headerComposite, SWT.NONE);
		topComposite.setLayout(new GridLayout(6, false));
		topComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		GridData gdd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gdd.widthHint = 70;
		GridData gdd2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		Label lblGtin = new Label(topComposite, SWT.NONE);
		lblGtin.setLayoutData(gdd);
		lblGtin.setToolTipText("European Article Number / Global Trade Index Number");
		lblGtin.setText("EAN/GTIN");

		txtGTIN = new Text(topComposite, SWT.READ_ONLY);
		txtGTIN.setLayoutData(gdd2);
		txtGTIN.setBackground(topComposite.getBackground());

		Label lblPhznr = new Label(topComposite, SWT.NONE);
		lblPhznr.setToolTipText("Pharmacode");
		lblPhznr.setLayoutData(gdd);
		lblPhznr.setText("Pharmacode");

		txtPHZNR = new Text(topComposite, SWT.READ_ONLY);
		txtPHZNR.setLayoutData(gdd2);
		txtPHZNR.setBackground(topComposite.getBackground());

		final Label lblProductNr = new Label(topComposite, SWT.NONE);
		lblProductNr.setToolTipText("Produktnummer");
		lblProductNr.setLayoutData(gdd);
		lblProductNr.setText("Produkt Nr");

		txtProductNr = new Text(topComposite, SWT.READ_ONLY);
		txtProductNr.setLayoutData(gdd2);
		txtProductNr.setBackground(topComposite.getBackground());
		txtProductNr.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				lblProductNr.setVisible(!txtProductNr.getText().isEmpty());
			}
		});

		lblAbgabekategorie = new Label(headerComposite, SWT.NONE);
		lblAbgabekategorie.setText("Abgabekategorie");

		txtABGABEKATEGORIE = new Text(headerComposite, SWT.READ_ONLY);
		txtABGABEKATEGORIE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtABGABEKATEGORIE.setBackground(topComposite.getBackground());
		new Label(headerComposite, SWT.NONE);
		new Label(headerComposite, SWT.NONE);

		Group grpPackungsgroessenPreise = new Group(mainComposite, SWT.NONE);
		grpPackungsgroessenPreise.setText("Preis");
		grpPackungsgroessenPreise.setLayout(new GridLayout(7, false));
		grpPackungsgroessenPreise.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Label lblExFactoryPreis = new Label(grpPackungsgroessenPreise, SWT.NONE);
		lblExFactoryPreis.setText("Ex-Factory");

		lblEXFACTORYPRICE = new Label(grpPackungsgroessenPreise, SWT.BORDER);
		lblEXFACTORYPRICE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblPublicPrice = new Label(grpPackungsgroessenPreise, SWT.NONE);
		lblPublicPrice.setText("Publikumspreis");

		txtPUBLICPRICE = new Text(grpPackungsgroessenPreise, SWT.BORDER);
		txtPUBLICPRICE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		controlDecoIsCalculatedPPUB = new ControlDecoration(txtPUBLICPRICE, SWT.LEFT | SWT.TOP);
		controlDecoIsCalculatedPPUB.setDescriptionText("Preis wurde mittels Marge kalkuliert!");
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
		controlDecoIsCalculatedPPUB.setImage(fieldDecoration.getImage());
		controlDecoIsCalculatedPPUB.hide();

		lblSelbstbehalt = new Label(grpPackungsgroessenPreise, SWT.NONE);
		lblSelbstbehalt.setText("Selbstbehalt (%)");

		lblSELBSTBEHALT = new Text(grpPackungsgroessenPreise, SWT.READ_ONLY);
		lblSELBSTBEHALT.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnUserDefinedPrice = new Button(grpPackungsgroessenPreise, SWT.FLAT | SWT.CHECK);
		btnUserDefinedPrice.setToolTipText("Benutzerdefinierter Preis");
		btnUserDefinedPrice.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnUserDefinedPrice.setImage(
				ResourceManager.getPluginImage("at.medevit.ch.artikelstamm.ui", "rsc/icons/money--pencil.png")); //$NON-NLS-1$ //$NON-NLS-2$
		btnUserDefinedPrice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				item.getValue().setUserDefinedPrice(btnUserDefinedPrice.getSelection());
				m_bindingContext.updateTargets();
				txtPUBLICPRICE.setFocus();
				CoreModelServiceHolder.get().save(item.getValue());
			}
		});

		Group grepATCCode = new Group(mainComposite, SWT.NONE);
		grepATCCode.setLayout(new GridLayout(1, false));
		grepATCCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grepATCCode.setText("ATC-Code");

		treeATC = new Tree(grepATCCode, SWT.BORDER);
		GridData gd_treeATC = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_treeATC.heightHint = 80;
		treeATC.setLayoutData(gd_treeATC);
		treeATC.setBackground(parent.getBackground());

		Group grpMarker = new Group(mainComposite, SWT.None);
		grpMarker.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpMarker.setText("Marker");
		grpMarker.setLayout(new GridLayout(3, false));

		btnCheckIsNarcotic = new Button(grpMarker, SWT.CHECK);
		btnCheckIsNarcotic.setText("Betäubungsmittel");

		btnLPPVEntry = new Button(grpMarker, SWT.CHECK);
		btnLPPVEntry.setToolTipText(
				"Artikel wird in Liste pharmazeutischer Präparate mit spezieller Verwendung (LPPV) geführt");
		btnLPPVEntry.setText("LPPV Eintrag");

		btnK70Entry = new Button(grpMarker, SWT.CHECK);
		btnK70Entry.setToolTipText("Artikel wird in Kapitel 70 der Spezialitätenliste geführt");
		btnK70Entry.setText("Kapitel 70 Eintrag");

		Group grpLimitations = new Group(mainComposite, SWT.None);
		grpLimitations.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpLimitations.setText("Einschränkungen");
		grpLimitations.setLayout(new GridLayout(2, false));

		btnlLimitation = new Button(grpLimitations, SWT.CHECK);
		btnlLimitation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnlLimitation.setText("Limitation");

		lblLimitationspunkte = new Label(grpLimitations, SWT.NONE);
		lblLimitationspunkte.setText("Limitationspunkte");

		txtLIMITATIONPOINTS = new Text(grpLimitations, SWT.READ_ONLY);
		txtLIMITATIONPOINTS.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtLIMITATIONPOINTS.setBackground(grpLimitations.getBackground());

		lblLimitationstext = new Label(grpLimitations, SWT.NONE);
		lblLimitationstext.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblLimitationstext.setText("Limitationstext");

		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd.widthHint = 450;
		gd.minimumWidth = 450;
		txtLIMITATIONTEXT = new Text(grpLimitations, SWT.WRAP | SWT.MULTI);
		txtLIMITATIONTEXT.setLayoutData(gd);
		txtLIMITATIONTEXT.setBackground(grpLimitations.getBackground());

		Group grpHersteller = new Group(mainComposite, SWT.NONE);
		grpHersteller.setLayout(new GridLayout(1, false));
		grpHersteller.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpHersteller.setText("Hersteller");

		lblHERSTELLER = new Label(grpHersteller, SWT.NONE);
		lblHERSTELLER.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		grpDefaultSignature = new Group(mainComposite, SWT.NONE);
		grpDefaultSignature.setLayout(new GridLayout(1, false));
		grpDefaultSignature.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpDefaultSignature.setText("Standard Signatur");

		adsc = new ArticleDefaultSignatureComposite(grpDefaultSignature, SWT.NONE);
		adsc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		adsc.setOnLocationEnabled(false);

		m_bindingContext = initDataBindings();
		adsc.initDataBindings(m_bindingContext);
		adsc.setAutoSave(true);

		this.setContent(mainComposite);
		this.setExpandHorizontal(true);
		this.setExpandVertical(true);

		this.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.layout(true, true);
	}

	public Composite getMainComposite() {
		return mainComposite;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setItem(IArtikelstammItem obj) {
		if (isDisposed()) {
			return;
		}
		item.setValue(obj);

		String atcCode = obj.getAtcCode();
		adsc.setArticleToBind(obj);

		if (obj.isCalculatedPrice()) {
			controlDecoIsCalculatedPPUB.show();
		} else {
			controlDecoIsCalculatedPPUB.hide();
		}
		treeATC.removeAll();
		if (ATCCodeServiceConsumer.getATCCodeService() != null) {
			List<ATCCode> atcHierarchy = ATCCodeServiceConsumer.getATCCodeService().getHierarchyForATCCode(atcCode);
			if (atcHierarchy != null && atcHierarchy.size() > 0) {
				ATCCode rootCode = atcHierarchy.get(atcHierarchy.size() - 1);
				TreeItem root = new TreeItem(treeATC, SWT.None);
				if (prefAtcLanguage.equals(ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN)) {
					root.setText(rootCode.atcCode + StringUtils.SPACE + rootCode.name_german);
				} else {
					root.setText(rootCode.atcCode + StringUtils.SPACE + rootCode.name);
				}
				TreeItem parent = root;
				for (int i = atcHierarchy.size() - 2; i >= 0; i--) {
					ATCCode code = atcHierarchy.get(i);
					TreeItem newItem = new TreeItem(parent, SWT.None);
					if (prefAtcLanguage.equals(ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN)) {
						newItem.setText(code.atcCode + StringUtils.SPACE + code.name_german);
					} else {
						newItem.setText(code.atcCode + StringUtils.SPACE + code.name);
					}

					parent = newItem;
					if (i == 0)
						treeATC.setSelection(newItem);
				}
			}
		} else {
			TreeItem root = new TreeItem(treeATC, SWT.None);
			root.setText(obj.getAtcCode());
		}

		this.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.layout(true, true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextLblDSCRObserveWidget = WidgetProperties.text().observe(lblDSCR);
		IObservableValue itemDSCRObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "label", String.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeTextLblDSCRObserveWidget, itemDSCRObserveDetailValue,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);
		//
		IObservableValue observeTextLblGTINObserveWidget = WidgetProperties.text().observe(txtGTIN);
		IObservableValue itemGTINObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "gtin", String.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeTextLblGTINObserveWidget, itemGTINObserveDetailValue,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);
		//
		IObservableValue observeTextLblPHZNRObserveWidget = WidgetProperties.text().observe(txtPHZNR);
		IObservableValue itemPHARObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "PHAR", String.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeTextLblPHZNRObserveWidget, itemPHARObserveDetailValue,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);

		IObservableValue observeTextProductNrObserveWidget = WidgetProperties.text().observe(txtProductNr);
		IObservableValue itemProductNrObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "productId", String.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeTextProductNrObserveWidget, itemProductNrObserveDetailValue,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);

		IObservableValue observeTextLblHERSTELLERObserveWidget = WidgetProperties.text().observe(lblHERSTELLER);
		IObservableValue itemManufacturerLabelObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "manufacturerLabel", String.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeTextLblHERSTELLERObserveWidget, itemManufacturerLabelObserveDetailValue, null,
				null);
		//
		IObservableValue observeTextLblEXFACTORYPRICEObserveWidget = WidgetProperties.text().observe(lblEXFACTORYPRICE);
		IObservableValue itemExFactoryPriceObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "purchasePrice", Double.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeTextLblEXFACTORYPRICEObserveWidget, itemExFactoryPriceObserveDetailValue, null,
				null);
		//
		IObservableValue observeTextLblPUBLICPRICEObserveWidget = WidgetProperties
				.text(new int[] { SWT.Modify, SWT.FocusOut }).observeDelayed(100, txtPUBLICPRICE);
		IObservableValue itemPublicPriceObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "sellingPrice", Money.class).observeDetail(item); //$NON-NLS-1$
		UpdateValueStrategy strategy_2 = new SavingUpdateValueStrategy(ModelServiceHolder.get(), item);
		strategy_2.setConverter(new IConverter<String, Money>() {
			@Override
			public Money convert(String fromObject) {
				if (fromObject instanceof String) {
					try {
						return new Money(fromObject);
					} catch (ParseException e) {
						// ignore
					}
				}
				return null;
			}

			@Override
			public Object getFromType() {
				return String.class;
			}

			@Override
			public Object getToType() {
				return Money.class;
			}
		});
		bindingContext.bindValue(observeTextLblPUBLICPRICEObserveWidget, itemPublicPriceObserveDetailValue, strategy_2,
				null);
		//
		IObservableValue observeTextLblABGABEKATEGORIEObserveWidget = WidgetProperties.text()
				.observe(txtABGABEKATEGORIE);
		IObservableValue itemSwissmedicCategoryObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "swissmedicCategory", String.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeTextLblABGABEKATEGORIEObserveWidget, itemSwissmedicCategoryObserveDetailValue,
				null, null);

		IObservableValue observeTextLblSELBSTBEHALTObserveWidget = WidgetProperties.text().observe(lblSELBSTBEHALT);
		IObservableValue itemDeductibleObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "deductible", Integer.class).observeDetail(item); //$NON-NLS-1$
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IntToStringConverterSelbstbehalt());
		bindingContext.bindValue(observeTextLblSELBSTBEHALTObserveWidget, itemDeductibleObserveDetailValue, null,
				strategy_1);

		IObservableValue observeSelectionBtnCheckIsNarcoticObserveWidget = WidgetProperties.buttonSelection()
				.observe(btnCheckIsNarcotic);
		IObservableValue itemNarcoticObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "narcotic", Boolean.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeSelectionBtnCheckIsNarcoticObserveWidget, itemNarcoticObserveDetailValue,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);

		IObservableValue observeTextLblLIMITATIONTEXTObserveWidget = WidgetProperties.text().observe(txtLIMITATIONTEXT);
		IObservableValue itemLimitationTextObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "limitationText", String.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeTextLblLIMITATIONTEXTObserveWidget, itemLimitationTextObserveDetailValue, null,
				null);

		IObservableValue observeTextLblLIMITATIONPOINTSObserveWidget = WidgetProperties.text()
				.observe(txtLIMITATIONPOINTS);
		IObservableValue itemLimitationPointsObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "limitationPoints", String.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeTextLblLIMITATIONPOINTSObserveWidget, itemLimitationPointsObserveDetailValue,
				null, null);

		IObservableValue observeSelectionBtnLPPVEntryObserveWidget = WidgetProperties.buttonSelection()
				.observe(btnLPPVEntry);
		IObservableValue itemInLPPVObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "inLPPV", Boolean.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeSelectionBtnLPPVEntryObserveWidget, itemInLPPVObserveDetailValue,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);

		IObservableValue observeSelectionBtnK70EntryObserveWidget = WidgetProperties.buttonSelection()
				.observe(btnK70Entry);
		IObservableValue itemInK70ObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "inK70", Boolean.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeSelectionBtnK70EntryObserveWidget, itemInK70ObserveDetailValue,
				new SavingUpdateValueStrategy(ModelServiceHolder.get(), item), null);

		IObservableValue observeSelectionBtnlLimitationObserveWidget = WidgetProperties.buttonSelection()
				.observe(btnlLimitation);
		IObservableValue itemLimitedObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "limited", Boolean.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeSelectionBtnlLimitationObserveWidget, itemLimitedObserveDetailValue,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);

		IObservableValue observeSizeLblLIMITATIONTEXTObserveWidget = WidgetProperties.size().observe(txtLIMITATIONTEXT);
		IObservableValue observeTextLblLIMITATIONTEXTObserveWidget_1 = WidgetProperties.text(SWT.Modify)
				.observe(txtLIMITATIONTEXT);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new DatabindingTextResizeConverter(txtLIMITATIONTEXT));
		bindingContext.bindValue(observeSizeLblLIMITATIONTEXTObserveWidget, observeTextLblLIMITATIONTEXTObserveWidget_1,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), strategy);

		IObservableValue observeSelectionBtnUserDefinedPriceObserveWidget = WidgetProperties.buttonSelection()
				.observe(btnUserDefinedPrice);
		IObservableValue itemCalculatedPriceObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "userDefinedPrice", Boolean.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeSelectionBtnUserDefinedPriceObserveWidget,
				itemCalculatedPriceObserveDetailValue, new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);

		IObservableValue observeEditableTxtPUBLICPRICEObserveWidget = WidgetProperties.editable()
				.observe(txtPUBLICPRICE);
		IObservableValue itemUserDefinedPriceObserveDetailValue = PojoProperties
				.value(IArtikelstammItem.class, "userDefinedPrice", Boolean.class).observeDetail(item); //$NON-NLS-1$
		bindingContext.bindValue(observeEditableTxtPUBLICPRICEObserveWidget, itemUserDefinedPriceObserveDetailValue,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);

		return bindingContext;
	}

	public static void setPrefAtcLanguage(String prefAtcLanguage) {
		DetailComposite.prefAtcLanguage = prefAtcLanguage;
	}
}
