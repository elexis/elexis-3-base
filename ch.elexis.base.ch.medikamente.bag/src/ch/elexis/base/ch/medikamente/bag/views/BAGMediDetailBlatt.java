/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.base.ch.medikamente.bag.views;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.settings.UserSettings;
import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.util.LabeledInputField.InputData;
import ch.elexis.core.ui.util.LabeledInputField.InputData.Typ;
import ch.elexis.core.ui.util.ListDisplay;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Artikel;
import ch.elexis.data.Kontakt;
import ch.elexis.data.PersistentObject;
import ch.elexis.medikamente.bag.data.BAGMedi;
import ch.elexis.medikamente.bag.data.Interaction;
import ch.elexis.medikamente.bag.data.Substance;
import ch.rgw.tools.StringTool;

public class BAGMediDetailBlatt extends Composite {
	private static final String BAGMEDI_DETAIL_BLATT_INTERACTIONS =
		"BAGMediDetailBlatt/interactions";
	private static final String BAGMEDI_DETAIL_BLATT_SUBSTANCES = "BAGMediDetailBlatt/substances";
	private static final String FACHINFORMATIONEN = "Fachinformationen";
	private static final String KEYWORDS = "Notizen, Schlüsselbegriffe";
	private static final String BAGMEDI_DETAIL_BLATT_KEYWORDS = "BAGMediDetailBlatt/keywords";
	private static final String BAGMEDI_DETAIL_BLATT_PROFINFOS = "BAGMediDetailBlatt/profinfos";
	private final LabeledInputField.AutoForm fld;
	private final Text tSubstances;
	private final Text tInfos, tKeywords;
	private final Composite parent;
	private final ScrolledForm form;
	ListDisplay<Interaction> ldInteraktionen;
	private BAGMedi actMedi;
	ExpandableComposite ecSubst, ecInterakt, ecFachinfo, ecKeywords;
	
	InputData[] fields = new InputData[] {
		new InputData("Hersteller", "ExtInfo", new LabeledInputField.IContentProvider() {
			public void displayContent(PersistentObject po, InputData ltf){
				Kontakt hersteller = ((BAGMedi) po).getHersteller();
				if (hersteller.isValid()) {
					String lbl = hersteller.getLabel();
					if (lbl.length() > 15) {
						lbl = lbl.substring(0, 12) + "...";
					}
					ltf.setText(lbl);
				} else {
					ltf.setText("?");
				}
			}
			
			public void reloadContent(PersistentObject po, InputData ltf){}
			
		}), new InputData("Therap. Gruppe", "Gruppe", InputData.Typ.STRING, null),
		new InputData("Generika", "ExtInfo", InputData.Typ.STRING, "Generika"),
		new InputData("Pharmacode", "ExtInfo", InputData.Typ.STRING, "Pharmacode"),
		new InputData("BAG-Dossier", "ExtInfo", InputData.Typ.STRING, "BAG-Dossier"),
		new InputData("Swissmedic-Nr", "ExtInfo", InputData.Typ.STRING, "Swissmedic-Nr"),
		new InputData("Swissmedic-Liste", "ExtInfo", InputData.Typ.STRING, "Swissmedic-Liste"),
		new InputData("ExFactory", "EK_Preis", InputData.Typ.CURRENCY, null),
		new InputData("Verkauf", "VK_Preis", InputData.Typ.CURRENCY, null),
		new InputData("Limitatio", "ExtInfo", InputData.Typ.STRING, "Limitatio"),
		new InputData("LimitatioPts", "ExtInfo", InputData.Typ.STRING, "LimitatioPts"),
		new InputData("Max. Pckg. an Lager", "Maxbestand", Typ.STRING, null),
		new InputData("Min. Pckg. an Lager", "Minbestand", Typ.STRING, null),
		new InputData("Aktuell Pckg. an Lager", "Istbestand", Typ.STRING, null),
		new InputData("Aktuell an Lager", "ExtInfo", Typ.INT, "Anbruch"),
		new InputData("Stück pro Packung", "ExtInfo", Typ.INT, "Verpackungseinheit"),
		new InputData("Stück pro Abgabe", "ExtInfo", Typ.INT, "Verkaufseinheit"),
		new InputData("Lieferant", "Lieferant", new LabeledInputField.IContentProvider() {
			public void displayContent(PersistentObject po, InputData ltf){
				String lbl = ((Artikel) po).getLieferant().getLabel();
				if (lbl.length() > 15) {
					lbl = lbl.substring(0, 12) + "...";
				}
				ltf.setText(lbl);
			}
			
			public void reloadContent(PersistentObject po, InputData ltf){
				KontaktSelektor ksl =
					new KontaktSelektor(Hub.getActiveShell(), Kontakt.class, "Lieferant",
						"Bitte wählen Sie, wer diesen Artikel liefert", Kontakt.DEFAULT_SORT);
				if (ksl.open() == Dialog.OK) {
					Kontakt k = (Kontakt) ksl.getSelection();
					((Artikel) po).setLieferant(k);
					String lbl = ((Artikel) po).getLieferant().getLabel();
					if (lbl.length() > 15) {
						lbl = lbl.substring(0, 12) + "...";
					}
					ltf.setText(lbl);
					ElexisEventDispatcher.reload(Artikel.class);
				}
			}
			
		})
	
	};
	
	public BAGMediDetailBlatt(final Composite pr){
		super(pr, SWT.NONE);
		FormToolkit tk = UiDesk.getToolkit();
		parent = pr;
		setLayout(new GridLayout());
		setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		form = tk.createScrolledForm(this);
		Composite ret = form.getBody();
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout());
		fld = new LabeledInputField.AutoForm(ret, fields);
		fld.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		// fld.setEnabled(false);
		tk.adapt(fld);
		ecSubst = tk.createExpandableComposite(ret, ExpandableComposite.TWISTIE);
		ecSubst.setText("Inhaltsstoffe");
		tSubstances = SWTHelper.createText(tk, ecSubst, 5, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		// tSubstances.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		ecSubst.addExpansionListener(new ExpansionAdapter() {
			
			@Override
			public void expansionStateChanged(final ExpansionEvent e){
				UserSettings.saveExpandedState(BAGMEDI_DETAIL_BLATT_SUBSTANCES, e.getState());
				form.reflow(true);
			}
			
		});
		ecSubst.setClient(tSubstances);
		// tk.createSeparator(ret, SWT.HORIZONTAL);
		ecInterakt = tk.createExpandableComposite(ret, ExpandableComposite.TWISTIE);
		ecInterakt.setText("Bisher eingetragene Interaktionen");
		
		ldInteraktionen =
			new ListDisplay<Interaction>(ecInterakt, SWT.BORDER, new ListDisplay.LDListener() {
				
				public String getLabel(final Object o){
					if (o instanceof Interaction) {
						Interaction inter = (Interaction) o;
						return inter.getLabel();
					}
					return "?";
				}
				
				public void hyperlinkActivated(final String l){
					InteraktionsDialog idlg = new InteraktionsDialog(pr.getShell(), actMedi);
					idlg.open();
					
				}
			});
		ldInteraktionen.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		ldInteraktionen.addHyperlinks("Bearbeiten...");
		// tk.adapt(ldInteraktionen);
		ecInterakt.setClient(ldInteraktionen);
		ecInterakt.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(final ExpansionEvent e){
				UserSettings.saveExpandedState(BAGMEDI_DETAIL_BLATT_INTERACTIONS, e.getState());
				form.reflow(true);
			}
		});
		ecKeywords = tk.createExpandableComposite(ret, ExpandableComposite.TWISTIE);
		tKeywords = SWTHelper.createText(ecKeywords, 3, SWT.NONE);
		tKeywords.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(final FocusEvent e){
				actMedi.set("keywords", tKeywords.getText());
			}
			
		});
		ecKeywords.setClient(tKeywords);
		ecKeywords.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(final ExpansionEvent e){
				UserSettings.saveExpandedState(BAGMEDI_DETAIL_BLATT_KEYWORDS, e.getState());
				form.reflow(true);
			}
		});
		ecKeywords.setText(KEYWORDS);
		
		ecFachinfo = tk.createExpandableComposite(ret, ExpandableComposite.TWISTIE);
		ecFachinfo.setText(FACHINFORMATIONEN);
		tInfos = SWTHelper.createText(ecFachinfo, 15, SWT.NONE);
		ecFachinfo.setClient(tInfos);
		ecFachinfo.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(final ExpansionEvent e){
				UserSettings.saveExpandedState(BAGMEDI_DETAIL_BLATT_PROFINFOS, e.getState());
				form.reflow(true);
			}
		});
		tInfos.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(final FocusEvent e){
				actMedi.set("KompendiumText", tInfos.getText());
			}
			
		});
		
	}
	
	public void display(final BAGMedi m){
		actMedi = m;
		form.setText(m.getLabel());
		fld.reload(m);
		List<Substance> list = m.getSubstances();
		StringBuilder sb = new StringBuilder();
		for (Substance s : list) {
			sb.append(s.getLabel()).append("\n");
		}
		tSubstances.setText(sb.toString());
		ldInteraktionen.clear();
		for (Interaction inter : m.getInteraktionen()) {
			ldInteraktionen.add(inter);
		}
		tInfos.setText(StringTool.unNull(m.get("KompendiumText")));
		tKeywords.setText(StringTool.unNull(m.get("keywords")));
		UserSettings.setExpandedState(ecSubst, BAGMEDI_DETAIL_BLATT_SUBSTANCES);
		UserSettings.setExpandedState(ecInterakt, BAGMEDI_DETAIL_BLATT_INTERACTIONS);
		UserSettings.setExpandedState(ecFachinfo, BAGMEDI_DETAIL_BLATT_PROFINFOS);
		UserSettings.setExpandedState(ecKeywords, BAGMEDI_DETAIL_BLATT_KEYWORDS);
		form.reflow(true);
	}
}
