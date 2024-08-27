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

import java.time.LocalDate;
import java.util.SortedSet;

import org.apache.commons.lang3.StringUtils;
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
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

import ch.elexis.core.ac.EvACE;
import ch.elexis.core.ac.Right;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.time.TimeUtil;
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
	String[] tableHeaders = new String[] { "Beleg", "Datum", "Soll", "Haben", "Saldo", "Kategorie", "Zahlungsart",
			"Text" };
	int[] tableCols = new int[] { 50, 80, 60, 60, 60, 100, 100, 400 };
	private IAction addAction, subtractAction, stornoAction, saldoAction, dateAction, printAction, editCatAction,
			editPaymentAction;

	public KassenView() {
		ttVon = new TimeTool();
		ttBis = new TimeTool();
		ttVon.addDays(-14);
	}

	public static KassenbuchViewerComparator comparator;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		tk = UiDesk.getToolkit();
		form = tk.createScrolledForm(parent);
		Composite body = form.getBody();
		body.setLayout(new FillLayout());
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tc = new TableColumn[tableHeaders.length];
		Table table = new Table(body, SWT.SINGLE | SWT.FULL_SELECTION);
		tv = new TableViewer(table);
		tv.setContentProvider(new IStructuredContentProvider() {
			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public Object[] getElements(Object inputElement) {
				SortedSet<KassenbuchEintrag> set = KassenbuchEintrag.getBookings(ttVon, ttBis);
				if (set != null) {
					return set.toArray();
				} else {
					return new KassenbuchEintrag[0];
				}
			}
		});

		comparator = new KassenbuchViewerComparator();
		tv.setComparator(comparator);

		for (int i = 0; i < tc.length; i++) {
			tc[i] = new TableColumn(table, SWT.NONE);
			tc[i].setText(tableHeaders[i]);
			tc[i].setWidth(tableCols[i]);
			tc[i].addSelectionListener(getSelectionAdapter(tc[i], i));
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tv.setLabelProvider(new KBLabelProvider());
		tv.setUseHashlookup(true);
		makeActions();
		ViewMenus menu = new ViewMenus(getViewSite());
		menu.createToolbar(addAction, subtractAction, saldoAction);
		menu.createViewerContextMenu(tv, stornoAction);
		menu.createMenu(dateAction, printAction, null, editCatAction, editPaymentAction);
		tv.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
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

	private void setFormText() {
		if (ttVon == null) {
			form.setText("Anzeige: Alle Buchungen");
		} else {
			form.setText(
					"Anzeige: Von " + ttVon.toString(TimeTool.DATE_GER) + " bis: " + ttBis.toString(TimeTool.DATE_GER));
		}
	}

	@Override
	public void dispose() {
		GlobalEventDispatcher.removeActivationListener(this, this);
		super.dispose();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	class KBLabelProvider implements ITableLabelProvider, ITableColorProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			KassenbuchEintrag kb = (KassenbuchEintrag) element;
			Money betrag = kb.getAmount();
			switch (columnIndex) {
			case 0:
				return kb.get("BelegNr");
			case 1:
				return kb.getDate();
			case 2:
				return betrag.isNegative() ? StringUtils.EMPTY : betrag.getAmountAsString();
			case 3:
				return betrag.isNegative() ? new Money(betrag).negate().getAmountAsString() : StringUtils.EMPTY;
			case 4:
				return kb.getSaldo().getAmountAsString();
			case 5:
				return kb.getKategorie();
			case 6:
				return kb.getPaymentMode();
			case 7:
				return kb.getText();
			}
			return "?"; //$NON-NLS-1$
		}

		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		public void dispose() {
			// TODO Auto-generated method stub

		}

		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		public Color getBackground(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		public Color getForeground(Object element, int columnIndex) {
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

		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	private void makeActions() {
		addAction = new RestrictedAction(EvACE.of(KassenbuchEintrag.class, Right.CREATE), "Einnahme") {
			{
				setImageDescriptor(Images.IMG_ADD.getImageDescriptor());
				setToolTipText("Einnahme verbuchen");
			}

			public void doRun() {
				new BuchungsDialog(getSite().getShell(), true).open();
				tv.refresh();
			}
		};
		subtractAction = new RestrictedAction(EvACE.of(KassenbuchEintrag.class, Right.CREATE), "Ausgabe") {
			{
				setImageDescriptor(Images.IMG_REMOVEITEM.getImageDescriptor());
				setToolTipText("Ausgabe verbuchen");
			}

			public void doRun() {
				new BuchungsDialog(getSite().getShell(), false).open();
				tv.refresh();
			}
		};
		stornoAction = new RestrictedAction(EvACE.of(KassenbuchEintrag.class, Right.UPDATE), "Storno") {
			{
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
				setToolTipText("Buchung stornieren");
			}

			public void doRun() {
				IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
				if (!sel.isEmpty()) {
					KassenbuchEintrag kb = (KassenbuchEintrag) sel.getFirstElement();
					kb.delete();
					KassenbuchEintrag.recalc();
					tv.refresh();
				}

			}
		};
		saldoAction = new RestrictedAction(EvACE.of(KassenbuchEintrag.class, Right.CREATE), "Saldo") {
			{
				setImageDescriptor(getPluginImageDescriptor("icons/sigma.ico")); //$NON-NLS-1$
				setToolTipText("Zwischenbilanz erstellen");
			}

			public void doRun() {
				InputDialog inp = new InputDialog(getSite().getShell(), "Kassenbestand abgleichen",
						"Geben Sie bitte den abgezählten Betrag in der Kasse ein", "0.00", null); //$NON-NLS-2$
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

		dateAction = new RestrictedAction(EvACE.of(KassenbuchEintrag.class, Right.VIEW), "Zeitraum") {
			{
				setImageDescriptor(getPluginImageDescriptor("icons/calendar.png")); //$NON-NLS-1$
				setToolTipText("Anzeigezeitraum einstellen");
			}

			public void doRun() {
				DatumEingabeDialog ded = new DatumEingabeDialog(getViewSite().getShell(), ttVon, ttBis);
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
		printAction = new RestrictedAction(EvACE.of(KassenbuchEintrag.class, Right.VIEW), "Drucken") {
			{
				setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());
				setTitleToolTip("Angezeigte Buchungen ausdrucken");
			}

			public void doRun() {
				KassenbuchDruckDialog kbd = new KassenbuchDruckDialog(getSite().getShell(), ttVon, ttBis);
				kbd.open();
			}
		};
		editCatAction = new RestrictedAction(EvACE.of(KassenbuchEintrag.class, Right.CREATE), "Kategorien...") {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setTitleToolTip("Kategorien editieren");
			}

			public void doRun() {
				new EditCatsDialog(getSite().getShell()).open();
			}
		};
		editPaymentAction = new RestrictedAction(EvACE.of(KassenbuchEintrag.class, Right.CREATE), "Zahlungsart...") {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setTitleToolTip("Zahlungsart editieren");
			}

			@Override
			public void doRun() {
				new EditPaymentModesDialog(getSite().getShell()).open();
			}
		};
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative
	 * path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getPluginImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.kassenbuch", path); //$NON-NLS-1$
	}

	public void activation(boolean mode) {
		// Don't mind

	}

	public void visible(boolean mode) {
		if (mode) {
			tv.refresh();
			CoreHub.heart.addListener(this);
		} else {
			CoreHub.heart.removeListener(this);
		}

	}

	public void heartbeat() {
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				tv.refresh();
			}
		});
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				tv.getTable().setSortDirection(comparator.getDirection());
				tv.getTable().setSortColumn(column);
				tv.refresh();
			}
		};
		return selectionAdapter;
	}
}

class KassenbuchViewerComparator extends ViewerComparator {

	private int propertyIndex;
	private boolean direction = true;

	public KassenbuchViewerComparator() {
		this.propertyIndex = 0;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof KassenbuchEintrag && e2 instanceof KassenbuchEintrag) {
			KassenbuchEintrag kb1 = (KassenbuchEintrag) e1;
			KassenbuchEintrag kb2 = (KassenbuchEintrag) e2;
			Money betrag1 = kb1.getAmount();
			Money betrag2 = kb2.getAmount();

			int rc = 0;
			switch (propertyIndex) {
			case 0:
				try {
					int belegNr1 = Integer.parseInt(kb1.getBelegNr());
					int belegNr2 = Integer.parseInt(kb2.getBelegNr());
					rc = Integer.compare(belegNr1, belegNr2);
				} catch (NumberFormatException e) {
					rc = kb1.getBelegNr().compareTo(kb2.getBelegNr());
				}
				break;
			case 1:
				LocalDate date1 = LocalDate.parse(kb1.getDate(), TimeUtil.DATE_GER);
				LocalDate date2 = LocalDate.parse(kb2.getDate(), TimeUtil.DATE_GER);
				rc = date1.compareTo(date2);
				break;
			case 2:
			case 3:
				rc = Double.compare(betrag1.getAmount(), betrag2.getAmount());
				break;
			case 4:
				rc = kb1.getSaldo().compareTo(kb2.getSaldo());
				break;
			case 5:
				rc = kb1.getKategorie().compareToIgnoreCase(kb2.getKategorie());
				break;
			case 6:
				rc = kb1.getPaymentMode().compareToIgnoreCase(kb2.getPaymentMode());
				break;
			case 7:
				rc = kb1.getText().compareToIgnoreCase(kb2.getText());
				break;
			default:
				break;
			}

			if (direction) {
				rc = -rc;
			}
			return rc;
		}
		return 0;
	}

	/**
	 * for sort direction
	 *
	 * @return SWT.DOWN or SWT.UP
	 */
	public int getDirection() {
		return direction ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			direction = !direction;
		} else {
			this.propertyIndex = column;
			direction = true;
		}
	}

}
