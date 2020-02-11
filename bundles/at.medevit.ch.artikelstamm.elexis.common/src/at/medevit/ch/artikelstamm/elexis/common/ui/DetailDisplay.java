/*******************************************************************************
 * Copyright (c) 2016 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.ui;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.conversion.NumberToStringConverter;
import org.eclipse.core.databinding.conversion.StringToNumberConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;

import at.medevit.atc_codes.ATCCodeLanguageConstants;
import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import at.medevit.ch.artikelstamm.ArtikelstammHelper;
import at.medevit.ch.artikelstamm.DATASOURCEType;
import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.elexis.common.service.VersionUtil;
import at.medevit.ch.artikelstamm.model.common.preference.PreferenceConstants;
import at.medevit.ch.artikelstamm.ui.DetailComposite;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.databinding.SavingUpdateValueStrategy;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.elexis.core.ui.views.controls.StockDetailComposite;

public class DetailDisplay implements IDetailDisplay {
	
	protected WritableValue<IArtikelstammItem> item =
		new WritableValue<>(null, IArtikelstammItem.class);
	
	private DetailComposite dc = null;
	private StockDetailComposite sdc;
	private Text txtStkProPack, txtStkProAbgabe;
	
	@Override
	public Class<?> getElementClass(){
		return IArtikelstammItem.class;
	}
	
	@Inject
	public void selection(
		@Optional @Named("at.medevit.ch.artikelstamm.elexis.common.ui.selection") IArtikelstammItem item){
		if (item != null && dc != null && !dc.isDisposed()) {
			display(item);
		}
	}
	
	@Override
	public void display(Object obj){
		IArtikelstammItem ai = (IArtikelstammItem) obj;
		item.setValue(ai);
		if (dc != null) {
			dc.setItem(ai);
		}
		if (sdc != null && !sdc.isDisposed()) {
			sdc.setArticle(ai);
		}
		if (!txtStkProAbgabe.isDisposed()) {
			txtStkProAbgabe.setEnabled(!ai.isProduct());
		}
		if (!txtStkProPack.isDisposed()) {
			txtStkProPack.setEnabled(!ai.isProduct());
		}
		
	}
	
	@Override
	public String getTitle(){
		return ArtikelstammConstants.CODESYSTEM_NAME;
	}
	
	@Override
	public Composite createDisplay(Composite parent, IViewSite site){
		if (dc == null) {
			String atcLang =
				ConfigServiceHolder.get().get(PreferenceConstants.PREF_ATC_CODE_LANGUAGE,
					ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN);
			
			dc = new DetailComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL, atcLang);
			
			addLagerhaltungGroupToComposite(dc);
			addAdditionalInformation(dc);
			addDataSetStateLabelToComposite(dc);
		}
		return dc;
	}
	
	public void addAdditionalInformation(DetailComposite dc){
		// Overwritten by subclasses
	}
	
	private void addDataSetStateLabelToComposite(DetailComposite dc){
		Composite ret = new Composite(dc.getMainComposite(), SWT.None);
		ret.setLayout(new GridLayout(2, false));
		ret.setLayoutData(new GridData(SWT.FILL, SWT.VERTICAL, true, false));
		
		Label label = new Label(ret, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 1, 1));
		StringBuilder sb = new StringBuilder();
		int version = VersionUtil.getCurrentVersion();
		if (version != 99999) {
			sb.append(" v" + version);
		}
		Date creationDate = VersionUtil.getImportSetCreationDate();
		if (creationDate != null) {
			sb.append(" / " + ArtikelstammHelper.monthAndYearWritten.format(creationDate));
		}
		
		// the default datasource is oddb2xml
		DATASOURCEType datasourceType = DATASOURCEType.ODDB_2_XML;
		try {
			datasourceType = VersionUtil.getDatasourceType();
		} catch (IllegalArgumentException e) {
			/** ignore **/
		}
		sb.append(" / " + datasourceType.value());
		
		label.setText("Datensatz-Basis: " + sb.toString());
		
		addUpdateLabelToBottom(ret);
	}
	
	/**
	 * Hook to allow adding a label calling update
	 * 
	 * @param dc
	 */
	public void addUpdateLabelToBottom(Composite dc){
		// Overwritten by subclasses
	}
	
	/**
	 * This method adds the Elexis v2.1 aspect of stock to the detail display. As this functionality
	 * is specific to Elexis v2.1 it is not included in the base {@link DetailComposite}
	 * 
	 * @param dc
	 *            the {@link DetailComposite} of {@link ArtikelstammItem}
	 */
	@SuppressWarnings("unchecked")
	private void addLagerhaltungGroupToComposite(final DetailComposite dc){
		DataBindingContext bindingContext = new DataBindingContext();
		
		Group grpLagerhaltung = new Group(dc.getMainComposite(), SWT.NONE);
		grpLagerhaltung.setText("Lagerhaltung");
		grpLagerhaltung.setLayout(new GridLayout(4, false));
		grpLagerhaltung.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		sdc = new StockDetailComposite(grpLagerhaltung, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, true, 4, 1);
		gridData.heightHint = 100;
		sdc.setLayoutData(gridData);
		
		// Stk. pro Pkg.
		Label lblStkProPack = new Label(grpLagerhaltung, SWT.NONE);
		lblStkProPack.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStkProPack.setText("Stk. pro Pckg.");
		
		txtStkProPack = new Text(grpLagerhaltung, SWT.BORDER);
		txtStkProPack.setTextLimit(4);
		GridData gd_txtStkProPack = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtStkProPack.widthHint = 40;
		txtStkProPack.setLayoutData(gd_txtStkProPack);
		IObservableValue propertyStkProPack = PojoProperties
			.value(IArtikelstammItem.class, "packageSize", Integer.class)
			.observeDetail(item);
		IObservableValue targetStkProPack =
			WidgetProperties.text(SWT.Modify).observe(txtStkProPack);
		bindingContext.bindValue(targetStkProPack, propertyStkProPack,
			new SavingUpdateValueStrategy<String, Integer>(CoreModelServiceHolder.get(), item)
				.setAutoSave(true).setConverter(StringToNumberConverter.toInteger(false)),
			new UpdateValueStrategy<Integer, String>()
				.setConverter(NumberToStringConverter.fromInteger(false)));
		
		// Stk. pro Abgabe
		Label lblStkProAbgabe = new Label(grpLagerhaltung, SWT.NONE);
		lblStkProAbgabe.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStkProAbgabe.setText("Stk. pro Abgabe");
		final String tooltip = "Für Komplettpackung bitte 0 Stk. angeben.";
		lblStkProAbgabe.setToolTipText(tooltip);
		
		txtStkProAbgabe = new Text(grpLagerhaltung, SWT.BORDER);
		txtStkProAbgabe.setTextLimit(4);
		GridData gd_txtStkProAbgabe = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtStkProAbgabe.widthHint = 40;
		txtStkProAbgabe.setLayoutData(gd_txtStkProAbgabe);
		txtStkProAbgabe.setToolTipText(tooltip);
		IObservableValue propertyStkProAbgabe = PojoProperties
			.value(IArtikelstammItem.class, "sellingSize", Integer.class).observeDetail(item);
		IObservableValue targetStkProAbgabe =
			WidgetProperties.text(SWT.Modify).observe(txtStkProAbgabe);
		
		bindingContext.bindValue(targetStkProAbgabe, propertyStkProAbgabe,
			new SavingUpdateValueStrategy<String, Integer>(CoreModelServiceHolder.get(),
				item).setAutoSave(true).setConverter(StringToNumberConverter.toInteger(false)),
			new UpdateValueStrategy<Integer, String>()
				.setConverter(NumberToStringConverter.fromInteger(false)));
	}
}
