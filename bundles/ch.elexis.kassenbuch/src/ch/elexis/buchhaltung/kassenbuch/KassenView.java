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
package ch.elexis.buchhaltung.kassenbuch;

import java.util.SortedSet;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.actions.RestrictedAction;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;

public class KassenView extends ViewPart implements IActivationListener, HeartListener {
	ScrolledForm form;
	FormToolkit tk;
	TableViewer tv;
	TableColumn[] tc;
	TimeTool ttVon, ttBis;
	String[] tableHeaders = new String[] {
		"Beleg", "Datum", "Soll", "Haben", "Saldo", "Kategorie", "Zahlungsart", "Text"
	};
	int[] tableCols = new int[] {
		50, 80, 60, 60, 60, 100, 100, 400
	};
	private IAction addAction, subtractAction, stornoAction, saldoAction, dateAction, printAction,
			editCatAction, editPaymentAction;
			
	public KassenView(){
		ttVon = new TimeTool();
		ttBis = new TimeTool();
		ttVon.addDays(-14);
	}
	
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout());
		tk = UiDesk.getToolkit();
		form = tk.createScrolledForm(parent);
		Composite body = form.getBody();
		body.setLayout(new FillLayout());
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tc = new TableColumn[tableHeaders.length];
		Table table = new Table(body, SWT.SINGLE | SWT.FULL_SELECTION);
		for (int i = 0; i < tc.length; i++) {
			tc[i] = new TableColumn(table, SWT.NONE);
			tc[i].setText(tableHeaders[i]);
			tc[i].setWidth(tableCols[i]);
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tv = new TableViewer(table);
		tv.setContentProvider(new IStructuredContentProvider() {
			public void dispose(){}
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput){}
			
			public Object[] getElements(Object inputElement){
				SortedSet<KassenbuchEintrag> set = KassenbuchEintrag.getBookings(ttVon, ttBis);
				if (set != null) {
					return set.toArray();
				} else {
					return new KassenbuchEintrag[0];
				}
			}
		});
		tv.setLabelProvider(new KBLabelProvider());
		tv.setUseHashlookup(true);
		makeActions();
		ViewMenus menu = new ViewMenus(getViewSite());
		menu.createToolbar(addAction, subtractAction, saldoAction);
		menu.createViewerContextMenu(tv, stornoAction);
		menu.createMenu(dateAction, printAction, null, editCatAction, editPaymentAction);
		tv.addDoubleClickListener(new IDoubleClickListener() {
			
			public void doubleClick(DoubleClickEvent event){
				IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
				if (!sel.isEmpty()) {
					KassenbuchEintrag kbe = (KassenbuchEintrag) sel.getFirstElement();
					if (new BuchungsDialog(getSite().getShell(), kbe).open() == Dialog.OK) {
						tv.refresh();
					}
				}
				
			}
		});
		tv.setInput(this);
		GlobalEventDispatcher.addActivationListener(this, this);
		setFormText();
	}
	
	private void setFormText(){
		if (ttVon == null) {
			form.setText("Anzeige: Alle Buchungen");
		} else {
			form.setText("Anzeige: Von " + ttVon.toString(TimeTool.DATE_GER) + " bis: "
				+ ttBis.toString(TimeTool.DATE_GER));
		}
	}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, this);
		super.dispose();
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
	class KBLabelProvider implements ITableLabelProvider, ITableColorProvider {
		
		public Image getColumnImage(Object element, int columnIndex){
			return null;
		}
		
		public String getColumnText(Object element, int columnIndex){
			KassenbuchEintrag kb = (KassenbuchEintrag) element;
			Money betrag = kb.getAmount();
			switch (columnIndex) {
			case 0:
				return kb.get("BelegNr");
			case 1:
				return kb.getDate();
			case 2:
				return betrag.isNegative() ? new Money(betrag).negate().getAmountAsString() : "";
			case 3:
				return betrag.isNegative() ? "" : betrag.getAmountAsString();
			case 4:
				return kb.getSaldo().getAmountAsString();
			case 5:
				return kb.getKategorie();
			case 6:
				return kb.getPaymentMode();
			case 7:
				return kb.getText();
			}
			return "?";
		}
		
		public void addListener(ILabelProviderListener listener){
			// TODO Auto-generated method stub
			
		}
		
		public void dispose(){
			// TODO Auto-generated method stub
			
		}
		
		public void removeListener(ILabelProviderListener listener){
			// TODO Auto-generated method stub
			
		}
		
		public Color getBackground(Object element, int columnIndex){
			// TODO Auto-generated method stub
			return null;
		}
		
		public Color getForeground(Object element, int columnIndex){
			if (columnIndex == 4) {
				KassenbuchEintrag kb = (KassenbuchEintrag) element;
				if (kb.getSaldo().isNegative()) {
					return UiDesk.getColor(UiDesk.COL_RED);
				} else {
					return UiDesk.getColor(UiDesk.COL_BLACK);
				}
			}
			return null;
			
		}
		
		public boolean isLabelProperty(Object element, String property){
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	private void makeActions(){
		addAction = new RestrictedAction(ACLContributor.BOOKING, "Einnahme") {
			{
				setImageDescriptor(Images.IMG_ADDITEM.getImageDescriptor());
				setToolTipText("Einnahme verbuchen");
			}
			
			public void doRun(){
				new BuchungsDialog(getSite().getShell(), true).open();
				tv.refresh();
			}
		};
		subtractAction = new RestrictedAction(ACLContributor.BOOKING, "Ausgabe") {
			{
				setImageDescriptor(Images.IMG_REMOVEITEM.getImageDescriptor());
				setToolTipText("Ausgabe verbuchen");
			}
			
			public void doRun(){
				new BuchungsDialog(getSite().getShell(), false).open();
				tv.refresh();
			}
		};
		stornoAction = new RestrictedAction(ACLContributor.STORNO, "Storno") {
			{
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
				setToolTipText("Buchung stornieren");
			}
			
			public void doRun(){
				IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
				if (!sel.isEmpty()) {
					KassenbuchEintrag kb = (KassenbuchEintrag) sel.getFirstElement();
					kb.delete();
					KassenbuchEintrag.recalc();
					tv.refresh();
				}
				
			}
		};
		saldoAction = new RestrictedAction(ACLContributor.BOOKING, "Saldo") {
			{
				setImageDescriptor(getPluginImageDescriptor("icons/sigma.ico"));
				setToolTipText("Zwischenbilanz erstellen");
			}
			
			public void doRun(){
				InputDialog inp =
					new InputDialog(getSite().getShell(), "Kassenbestand abgleichen",
						"Geben Sie bitte den abgezählten Betrag in der Kasse ein", "0.00", null);
				if (inp.open() == Dialog.OK) {
					try {
						Money money = new Money(inp.getValue());
						if (!money.isZero()) {
							KassenbuchEintrag last = KassenbuchEintrag.recalc();
							Money soll = last.getSaldo();
							Money diff = money.subtractMoney(soll);
							new KassenbuchEintrag(KassenbuchEintrag.nextNr(last) + " Kontrolle",
								new TimeTool().toString(TimeTool.DATE_GER), diff,
								diff.isNegative() ? "Fehlbetrag" : "Überschuss");
							tv.refresh();
						}
					} catch (Exception ex) {
						ExHandler.handle(ex);
						SWTHelper.alert("Fehler", "Die Eingabe im Betragsfeld war ungültig");
					}
				}
			}
		};
		
		dateAction = new RestrictedAction(ACLContributor.VIEW, "Zeitraum") {
			{
				setImageDescriptor(getPluginImageDescriptor("icons/calendar.png"));
				setToolTipText("Anzeigezeitraum einstellen");
			}
			
			public void doRun(){
				DatumEingabeDialog ded =
					new DatumEingabeDialog(getViewSite().getShell(), ttVon, ttBis);
				if (ded.open() == Dialog.OK) {
					ttVon = ded.ttVon;
					ttBis = ded.ttBis;
					
				} else {
					ttVon = null;
					ttBis = null;
				}
				setFormText();
				tv.refresh();
			}
		};
		printAction = new RestrictedAction(ACLContributor.VIEW, "Drucken") {
			{
				setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());
				setTitleToolTip("Angezeigte Buchungen ausdrucken");
			}
			
			public void doRun(){
				KassenbuchDruckDialog kbd =
					new KassenbuchDruckDialog(getSite().getShell(), ttVon, ttBis);
				kbd.open();
			}
		};
		editCatAction = new RestrictedAction(ACLContributor.BOOKING, "Kategorien...") {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setTitleToolTip("Kategorien editieren");
			}
			
			public void doRun(){
				new EditCatsDialog(getSite().getShell()).open();
			}
		};
		editPaymentAction = new RestrictedAction(ACLContributor.BOOKING, "Zahlungsart...") {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setTitleToolTip("Zahlungsart editieren");
			}
			
			@Override
			public void doRun(){
				new EditPaymentModesDialog(getSite().getShell()).open();
			}
		};
	}
	
	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getPluginImageDescriptor(String path){
		return AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.kassenbuch", path); //$NON-NLS-1$
	}
	
	public void activation(boolean mode){
		// Don't mind
		
	}
	
	public void visible(boolean mode){
		if (mode) {
			tv.refresh();
			CoreHub.heart.addListener(this);
		} else {
			CoreHub.heart.removeListener(this);
		}
		
	}
	
	public void heartbeat(){
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run(){
				tv.refresh();
			}
		});
	}
}
