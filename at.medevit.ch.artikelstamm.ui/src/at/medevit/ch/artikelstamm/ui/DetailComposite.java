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

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
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
import at.medevit.ch.artikelstamm.ui.internal.ATCCodeServiceConsumer;
import at.medevit.ch.artikelstamm.ui.internal.DatabindingTextResizeConverter;
import at.medevit.ch.artikelstamm.ui.internal.IntToStringConverterSelbstbehalt;

public class DetailComposite extends Composite {
	private DataBindingContext m_bindingContext;
	
	private WritableValue item = new WritableValue(null, IArtikelstammItem.class);
	
	private Label lblDSCR;
	private Label lblPHZNR;
	private Label lblGTIN;
	private Label lblHERSTELLER;
	private Label lblEXFACTORYPRICE;
	private Text txtPUBLICPRICE;
	private Tree treeATC;
	private Label lblAbgabekategorie;
	private Label lblABGABEKATEGORIE;
	private Label lblSelbstbehalt;
	private Label lblSELBSTBEHALT;
	private Button btnCheckIsNarcotic;
	private Button btnLPPVEntry;
	private Button btnlLimitation;
	private Label lblLimitationspunkte;
	private Label lblLIMITATIONPOINTS;
	private Label lblLimitationstext;
	private Text txtLIMITATIONTEXT;
	private ControlDecoration controlDecoIsCalculatedPPUB;
	private Button btnUserDefinedPrice;
	
	public DetailComposite(Composite parent, int style){
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Composite headerComposite = new Composite(this, SWT.NONE);
		headerComposite.setLayout(new GridLayout(4, false));
		headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblDSCR = new Label(headerComposite, SWT.NONE);
		lblDSCR.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDSCR.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		lblDSCR.setFont(SWTResourceManager.getFont("Lucida Grande", 16, SWT.BOLD));
		GridData gd_lblDSCR = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gd_lblDSCR.widthHint = 435;
		lblDSCR.setLayoutData(gd_lblDSCR);
		
		Label lblGtin = new Label(headerComposite, SWT.NONE);
		lblGtin.setToolTipText("European Article Number / Global Trade Index Number");
		lblGtin.setText("EAN/GTIN");
		
		lblGTIN = new Label(headerComposite, SWT.NONE);
		lblGTIN.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPhznr = new Label(headerComposite, SWT.NONE);
		lblPhznr.setToolTipText("Pharmacode");
		lblPhznr.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPhznr.setText("Pharmacode");
		
		lblPHZNR = new Label(headerComposite, SWT.NONE);
		lblPHZNR.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblAbgabekategorie = new Label(headerComposite, SWT.NONE);
		lblAbgabekategorie.setText("Abgabekategorie");
		
		lblABGABEKATEGORIE = new Label(headerComposite, SWT.NONE);
		lblABGABEKATEGORIE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(headerComposite, SWT.NONE);
		new Label(headerComposite, SWT.NONE);
		
		Group grpPackungsgroessenPreise = new Group(this, SWT.NONE);
		grpPackungsgroessenPreise.setText("Preis");
		grpPackungsgroessenPreise.setLayout(new GridLayout(7, false));
		grpPackungsgroessenPreise.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
			1));
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
		FieldDecoration fieldDecoration =
			FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_INFORMATION);
		controlDecoIsCalculatedPPUB.setImage(fieldDecoration.getImage());
		controlDecoIsCalculatedPPUB.hide();
		
		lblSelbstbehalt = new Label(grpPackungsgroessenPreise, SWT.NONE);
		lblSelbstbehalt.setText("Selbstbehalt (%)");
		
		lblSELBSTBEHALT = new Label(grpPackungsgroessenPreise, SWT.NONE);
		lblSELBSTBEHALT.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnUserDefinedPrice = new Button(grpPackungsgroessenPreise, SWT.FLAT | SWT.CHECK);
		btnUserDefinedPrice.setToolTipText("Benutzerdefinierter Preis");
		btnUserDefinedPrice.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnUserDefinedPrice.setImage(ResourceManager.getPluginImage(
			"at.medevit.ch.artikelstamm.ui", "rsc/icons/money--pencil.png"));
		btnUserDefinedPrice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				IArtikelstammItem ai = (IArtikelstammItem) item.getValue();
				ai.setPublicPrice(ai.getPublicPrice());
				m_bindingContext.updateTargets();
				txtPUBLICPRICE.setFocus();
			}
		});
		
		Group grepATCCode = new Group(this, SWT.NONE);
		grepATCCode.setLayout(new GridLayout(1, false));
		grepATCCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grepATCCode.setText("ATC-Code");
		
		treeATC = new Tree(grepATCCode, SWT.BORDER);
		GridData gd_treeATC = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_treeATC.heightHint = 80;
		treeATC.setLayoutData(gd_treeATC);
		treeATC.setBackground(parent.getBackground());
		
		Group grpMarker = new Group(this, SWT.None);
		grpMarker.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpMarker.setText("Marker");
		grpMarker.setLayout(new GridLayout(2, false));
		
		btnCheckIsNarcotic = new Button(grpMarker, SWT.CHECK);
		btnCheckIsNarcotic.setText("Betäubungsmittel");
		
		btnLPPVEntry = new Button(grpMarker, SWT.CHECK);
		btnLPPVEntry
			.setToolTipText("Artikel wird in Liste pharmazeutischer Präparate mit spezieller Verwendung (LPPV) geführt");
		btnLPPVEntry.setText("LPPV Eintrag");
		
		Group grpLimitations = new Group(this, SWT.None);
		grpLimitations.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpLimitations.setText("Einschränkungen");
		grpLimitations.setLayout(new GridLayout(2, false));
		
		btnlLimitation = new Button(grpLimitations, SWT.CHECK);
		btnlLimitation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnlLimitation.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
		btnlLimitation.setText("Limitation");
		
		lblLimitationspunkte = new Label(grpLimitations, SWT.NONE);
		lblLimitationspunkte.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
		lblLimitationspunkte.setText("Limitationspunkte");
		
		lblLIMITATIONPOINTS = new Label(grpLimitations, SWT.NONE);
		lblLIMITATIONPOINTS.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblLimitationstext = new Label(grpLimitations, SWT.NONE);
		lblLimitationstext.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblLimitationstext.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
		lblLimitationstext.setText("Limitationstext");
		
		txtLIMITATIONTEXT = new Text(grpLimitations, SWT.WRAP | SWT.MULTI);
		txtLIMITATIONTEXT.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtLIMITATIONTEXT.setBackground(grpLimitations.getBackground());
		
		Group grpHersteller = new Group(this, SWT.NONE);
		grpHersteller.setLayout(new GridLayout(1, false));
		grpHersteller.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpHersteller.setText("Hersteller");
		
		lblHERSTELLER = new Label(grpHersteller, SWT.NONE);
		lblHERSTELLER.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		m_bindingContext = initDataBindings();
	}
	
	@Override
	protected void checkSubclass(){
		// Disable the check that prevents subclassing of SWT components
	}
	
	public void setItem(IArtikelstammItem obj){
		item.setValue(obj);
		if (obj.isCalculatedPrice()) {
			controlDecoIsCalculatedPPUB.show();
		} else {
			controlDecoIsCalculatedPPUB.hide();
		}
		treeATC.removeAll();
		if (ATCCodeServiceConsumer.getATCCodeService() != null) {
			List<ATCCode> atcHierarchy =
				ATCCodeServiceConsumer.getATCCodeService().getHierarchyForATCCode(obj.getATCCode());
			if (atcHierarchy != null && atcHierarchy.size() > 0) {
				ATCCode rootCode = atcHierarchy.get(atcHierarchy.size() - 1);
				TreeItem root = new TreeItem(treeATC, SWT.None);
				root.setText(rootCode.atcCode + " " + rootCode.name);
				TreeItem parent = root;
				for (int i = atcHierarchy.size() - 2; i >= 0; i--) {
					ATCCode code = atcHierarchy.get(i);
					TreeItem newItem = new TreeItem(parent, SWT.None);
					newItem.setText(code.atcCode + " " + code.name);
					parent = newItem;
					if (i == 0)
						treeATC.setSelection(newItem);
				}
			}
		} else {
			TreeItem root = new TreeItem(treeATC, SWT.None);
			root.setText(obj.getATCCode());
		}
	}
	
	protected DataBindingContext initDataBindings(){
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextLblDSCRObserveWidget = WidgetProperties.text().observe(lblDSCR);
		IObservableValue itemDSCRObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "label", String.class)
				.observeDetail(item);
		bindingContext.bindValue(observeTextLblDSCRObserveWidget, itemDSCRObserveDetailValue,
			new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);
		//
		IObservableValue observeTextLblGTINObserveWidget = WidgetProperties.text().observe(lblGTIN);
		IObservableValue itemGTINObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "GTIN", String.class).observeDetail(item);
		bindingContext.bindValue(observeTextLblGTINObserveWidget, itemGTINObserveDetailValue,
			new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);
		//
		IObservableValue observeTextLblPHZNRObserveWidget =
			WidgetProperties.text().observe(lblPHZNR);
		IObservableValue itemPHARObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "PHAR", String.class).observeDetail(item);
		bindingContext.bindValue(observeTextLblPHZNRObserveWidget, itemPHARObserveDetailValue,
			new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);
		//
		IObservableValue observeTextLblHERSTELLERObserveWidget =
			WidgetProperties.text().observe(lblHERSTELLER);
		IObservableValue itemManufacturerLabelObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "manufacturerLabel", String.class)
				.observeDetail(item);
		bindingContext.bindValue(observeTextLblHERSTELLERObserveWidget,
			itemManufacturerLabelObserveDetailValue, null, null);
		//
		IObservableValue observeTextLblEXFACTORYPRICEObserveWidget =
			WidgetProperties.text().observe(lblEXFACTORYPRICE);
		IObservableValue itemExFactoryPriceObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "exFactoryPrice", double.class)
				.observeDetail(item);
		bindingContext.bindValue(observeTextLblEXFACTORYPRICEObserveWidget,
			itemExFactoryPriceObserveDetailValue, null, null);
		//
		IObservableValue observeTextLblPUBLICPRICEObserveWidget = WidgetProperties.text(new int[] {
			SWT.Modify, SWT.FocusOut
		}).observeDelayed(100, txtPUBLICPRICE);
		IObservableValue itemPublicPriceObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "publicPrice", Double.class)
				.observeDetail(item);
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		bindingContext.bindValue(observeTextLblPUBLICPRICEObserveWidget,
			itemPublicPriceObserveDetailValue, strategy_2, null);
		//
		IObservableValue observeTextLblABGABEKATEGORIEObserveWidget =
			WidgetProperties.text().observe(lblABGABEKATEGORIE);
		IObservableValue itemSwissmedicCategoryObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "swissmedicCategory", String.class)
				.observeDetail(item);
		bindingContext.bindValue(observeTextLblABGABEKATEGORIEObserveWidget,
			itemSwissmedicCategoryObserveDetailValue, null, null);
		//
		IObservableValue observeTextLblSELBSTBEHALTObserveWidget =
			WidgetProperties.text().observe(lblSELBSTBEHALT);
		IObservableValue itemDeductibleObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "deductible", Integer.class)
				.observeDetail(item);
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IntToStringConverterSelbstbehalt());
		bindingContext.bindValue(observeTextLblSELBSTBEHALTObserveWidget,
			itemDeductibleObserveDetailValue, null, strategy_1);
		//
		IObservableValue observeSelectionBtnCheckIsNarcoticObserveWidget =
			WidgetProperties.selection().observe(btnCheckIsNarcotic);
		IObservableValue itemNarcoticObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "narcotic", boolean.class).observeDetail(
				item);
		bindingContext.bindValue(observeSelectionBtnCheckIsNarcoticObserveWidget,
			itemNarcoticObserveDetailValue, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_NEVER), null);
		//
		IObservableValue observeTextLblLIMITATIONTEXTObserveWidget =
			WidgetProperties.text().observe(txtLIMITATIONTEXT);
		IObservableValue itemLimitationTextObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "limitationText", String.class)
				.observeDetail(item);
		bindingContext.bindValue(observeTextLblLIMITATIONTEXTObserveWidget,
			itemLimitationTextObserveDetailValue, null, null);
		//
		IObservableValue observeTextLblLIMITATIONPOINTSObserveWidget =
			WidgetProperties.text().observe(lblLIMITATIONPOINTS);
		IObservableValue itemLimitationPointsObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "limitationPoints", String.class)
				.observeDetail(item);
		bindingContext.bindValue(observeTextLblLIMITATIONPOINTSObserveWidget,
			itemLimitationPointsObserveDetailValue, null, null);
		//
		IObservableValue observeSelectionBtnLPPVEntryObserveWidget =
			WidgetProperties.selection().observe(btnLPPVEntry);
		IObservableValue itemInLPPVObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "inLPPV", boolean.class).observeDetail(
				item);
		bindingContext.bindValue(observeSelectionBtnLPPVEntryObserveWidget,
			itemInLPPVObserveDetailValue,
			new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);
		//
		IObservableValue observeSelectionBtnlLimitationObserveWidget =
			WidgetProperties.selection().observe(btnlLimitation);
		IObservableValue itemLimitedObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "limited", boolean.class).observeDetail(
				item);
		bindingContext.bindValue(observeSelectionBtnlLimitationObserveWidget,
			itemLimitedObserveDetailValue,
			new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);
		//
		IObservableValue observeSizeLblLIMITATIONTEXTObserveWidget =
			WidgetProperties.size().observe(txtLIMITATIONTEXT);
		IObservableValue observeTextLblLIMITATIONTEXTObserveWidget_1 =
			WidgetProperties.text(SWT.Modify).observe(txtLIMITATIONTEXT);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new DatabindingTextResizeConverter(txtLIMITATIONTEXT));
		bindingContext.bindValue(observeSizeLblLIMITATIONTEXTObserveWidget,
			observeTextLblLIMITATIONTEXTObserveWidget_1, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_NEVER), strategy);
		//
		IObservableValue observeSelectionBtnUserDefinedPriceObserveWidget =
			WidgetProperties.selection().observe(btnUserDefinedPrice);
		IObservableValue itemCalculatedPriceObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "userDefinedPrice", boolean.class)
				.observeDetail(item);
		bindingContext.bindValue(observeSelectionBtnUserDefinedPriceObserveWidget,
			itemCalculatedPriceObserveDetailValue, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_NEVER), null);
		//
		IObservableValue observeEditableTxtPUBLICPRICEObserveWidget =
			WidgetProperties.editable().observe(txtPUBLICPRICE);
		IObservableValue itemUserDefinedPriceObserveDetailValue =
			PojoProperties.value(IArtikelstammItem.class, "userDefinedPrice", boolean.class)
				.observeDetail(item);
		bindingContext.bindValue(observeEditableTxtPUBLICPRICEObserveWidget,
			itemUserDefinedPriceObserveDetailValue, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_NEVER), null);
		//
		return bindingContext;
	}
}
