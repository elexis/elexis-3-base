/*******************************************************************************
 * Copyright (c) 2007-2015, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     Gerry Weirich - adapted for 2.1
 *     Niklaus Giger - small improvements, split into 20 classes
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.widgets;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.iatrix.Iatrix;
import org.iatrix.data.Problem;
import org.iatrix.views.JournalView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.CodeSelectorHandler;
import ch.elexis.core.ui.actions.ICodeSelectorTarget;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.codesystems.LeistungenView;
import ch.elexis.data.Artikel;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;
import ch.elexis.data.Query;
import ch.elexis.data.Verrechnet;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.StringTool;

public class KonsVerrechnung implements IJournalArea {

	private Konsultation actKons = null;
	private FormToolkit tk;
	private static Logger log = LoggerFactory.getLogger(KonsVerrechnung.class);
	public IAction delVerrechnetAction;
	public IAction changeVerrechnetPreisAction;
	public IAction changeVerrechnetZahlAction;
	private TableViewer verrechnungViewer;
	private Color verrechnungViewerColor; // original color of verrechnungViewer
	private Hyperlink hVerrechnung;
	private Text tVerrechnungKuerzel;
	private ICodeSelectorTarget konsultationVerrechnungCodeSelectorTarget;
	private Color highlightColor;

	public KonsVerrechnung(Composite verrechnungComposite, Form form, String partName,
		Composite assignmentComposite){
		tk = UiDesk.getToolkit();

		// highlighting colors for ICodeSelectorTarget
		highlightColor = form.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
		verrechnungComposite.setLayout(new GridLayout(1, true));

		Composite verrechnungHeader = tk.createComposite(verrechnungComposite);
		verrechnungHeader.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		verrechnungHeader.setLayout(new GridLayout(2, false));

		hVerrechnung = tk.createHyperlink(verrechnungHeader, "Verrechnung", SWT.NONE);
		hVerrechnung.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		hVerrechnung.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e){
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView(LeistungenView.ID);
					CodeSelectorHandler.getInstance()
						.setCodeSelectorTarget(konsultationVerrechnungCodeSelectorTarget);
				} catch (Exception ex) {
					ExHandler.handle(ex);
					log.error("Fehler beim Starten des Leistungscodes " + ex.getMessage());
				}
			}
		});
		hVerrechnung.setEnabled(false);

		tVerrechnungKuerzel = tk.createText(verrechnungHeader, "", SWT.BORDER);
		GridData gd;
		gd = new GridData(SWT.END);
		gd.widthHint = 50;
		tVerrechnungKuerzel.setLayoutData(gd);
		tVerrechnungKuerzel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				String mnemonic = tVerrechnungKuerzel.getText();
				if (!StringTool.isNothing(mnemonic)) {
					// TODO evaluate return value, visualize errors
					addLeistungByMnemonic(mnemonic, false, false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e){
				widgetSelected(e);
			}
		});
		tVerrechnungKuerzel.setEnabled(false);

		Table verrechnungTable = tk.createTable(verrechnungComposite, SWT.MULTI);
		verrechnungViewer = new TableViewer(verrechnungTable);
		verrechnungViewer.getControl().setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		// used by hightlightVerrechnung()
		verrechnungHeader.setBackground(verrechnungHeader.getBackground());
		verrechnungTable.setBackground(verrechnungHeader.getBackground());
		verrechnungViewerColor = verrechnungTable.getBackground();
		hVerrechnung.setBackground(verrechnungHeader.getBackground());

		verrechnungViewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(Object inputElement){
				if (actKons != null) {
					List<Verrechnet> lgl = actKons.getLeistungen();
					return lgl.toArray();
				}
				return new Object[0];
			}

			@Override
			public void dispose(){
				// nothing to do
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
				// nothing to do
			}
		});
		verrechnungViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element){
				if (!(element instanceof Verrechnet)) {
					return "";
				}

				Verrechnet verrechnet = (Verrechnet) element;
				StringBuilder sb = new StringBuilder();
				int z = verrechnet.getZahl();
				Money preis = new Money(verrechnet.getEffPreis()).multiply(z);
				// double preis = (z * verrechnet.getEffPreisInRappen()) / 100.0;
				sb.append(z).append(" ").append(verrechnet.getCode()).append(" ")
					.append(verrechnet.getText()).append(" (").append(preis.getAmountAsString())
					.append(")");
				return sb.toString();
			}

		});
		verrechnungViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				boolean enableDel = false;
				boolean enableChange = false;

				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if (sel != null) {
					if (sel.size() >= 1) {
						enableDel = true;
					}
					if (sel.size() == 1) {
						enableChange = true;
					}
				}

				delVerrechnetAction.setEnabled(enableDel);
				changeVerrechnetZahlAction.setEnabled(enableChange);
				changeVerrechnetPreisAction.setEnabled(enableChange);
			}
		});
		verrechnungViewer.setInput(this);

		makeActions();

		/* Implementation Drag&Drop */

		final TextTransfer textTransfer = TextTransfer.getInstance();
		Transfer[] types = new Transfer[] {
			textTransfer
		};
		verrechnungViewer.getControl().addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e){
				if (e.keyCode == SWT.DEL) {
					int j = verrechnungViewer.getTable().getSelectionIndex();
					deleleSelectedItem();
					// Allow pressing delete several times in a row
					if (j >= 1) { // Correct by one, as we removed one item
						verrechnungViewer.getTable().setFocus();
						verrechnungViewer.getTable().select(j-1);
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e){}
		});
		// assignmentComposite
		DropTarget dtarget = new DropTarget(assignmentComposite, DND.DROP_COPY);
		dtarget.setTransfer(types);
		dtarget.addDropListener(new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetEvent event){
				/* Wir machen nur Copy-Operationen */
				event.detail = DND.DROP_COPY;
			}

			/* Mausbewegungen mit gedrückter Taste sind uns egal */
			@Override
			public void dragLeave(DropTargetEvent event){
				/* leer */
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event){
				/* leer */
			}

			@Override
			public void dragOver(DropTargetEvent event){
				/* leer */
			}

			/* Erst das Loslassen interessiert uns wieder */
			@Override
			public void drop(DropTargetEvent event){
				String drp = (String) event.data;
				String[] dl = drp.split(",");
				for (String obj : dl) {
					PersistentObject dropped = CoreHub.poFactory.createFromString(obj);
					if (dropped instanceof Problem) {
						Problem problem = (Problem) dropped;
						problem.addToKonsultation(actKons);

						// TODO: updateProblemAssignmentViewer();
						// TODO: setDiagnosenText(actKons);
					}
				}
			}

			@Override
			public void dropAccept(DropTargetEvent event){
				/* leer */
			}
		});

		// verrechnungComposite
		dtarget = new DropTarget(verrechnungComposite, DND.DROP_COPY);
		dtarget.setTransfer(types);
		dtarget.addDropListener(new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetEvent event){
				/* Wir machen nur Copy-Operationen */
				event.detail = DND.DROP_COPY;
			}

			/* Mausbewegungen mit gedrückter Taste sind uns egal */
			@Override
			public void dragLeave(DropTargetEvent event){
				/* leer */
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event){
				/* leer */
			}

			@Override
			public void dragOver(DropTargetEvent event){
				/* leer */
			}

			/* Erst das Loslassen interessiert uns wieder */
			@Override
			public void drop(DropTargetEvent event){
				String drp = (String) event.data;
				String[] dl = drp.split(",");
				for (String obj : dl) {
					PersistentObject dropped = CoreHub.poFactory.createFromString(obj);
					if (dropped instanceof IVerrechenbar) {
						if (CoreHub.acl.request(AccessControlDefaults.LSTG_VERRECHNEN) == false) {
							SWTHelper.alert("Fehlende Rechte",
								"Sie haben nicht die Berechtigung, Leistungen zu verrechnen");
						} else {
							Result<IVerrechenbar> result =
								actKons.addLeistung((IVerrechenbar) dropped);
							if (!result.isOK()) {
								SWTHelper.alert("Diese Verrechnung it ungültig", result.toString());
							}
							verrechnungViewer.refresh();
							updateVerrechnungSum();
						}
					}
				}
			}

			@Override
			public void dropAccept(DropTargetEvent event){
				/* leer */
			}

		});

		// ICodeSelectorTarget for Verrechnung in consultation area
		konsultationVerrechnungCodeSelectorTarget = new ICodeSelectorTarget() {
			@Override
			public String getName(){
				return partName;
			}

			@Override
			public void codeSelected(PersistentObject po){
				if (po instanceof IVerrechenbar) {
					if (CoreHub.acl.request(AccessControlDefaults.LSTG_VERRECHNEN) == false) {
						SWTHelper.alert("Fehlende Rechte",
							"Sie haben nicht die Berechtigung, Leistungen zu verrechnen");
					} else {
						IVerrechenbar verrechenbar = (IVerrechenbar) po;

						if (actKons != null) {
							Result<IVerrechenbar> result = actKons.addLeistung(verrechenbar);
							if (!result.isOK()) {
								SWTHelper.alert("Diese Verrechnung ist ungültig",
									result.toString());
							} else {
								if (CoreHub.userCfg.get(Iatrix.CFG_CODE_SELECTION_AUTOCLOSE,
									Iatrix.CFG_CODE_SELECTION_AUTOCLOSE_DEFAULT)) {
									// re-activate this view
									try {
										PlatformUI.getWorkbench().getActiveWorkbenchWindow()
											.getActivePage().showView(JournalView.ID);
									} catch (Exception ex) {
										ExHandler.handle(ex);
										log.error("Fehler beim Öffnen von JournalView: "
											+ ex.getMessage());
									}
								}
							}
							verrechnungViewer.refresh();
							updateVerrechnungSum();
						}
					}
				}
			}

			@Override
			public void registered(boolean registered){
				highlightVerrechnung(registered);
			}
		};
	}

	public void updateVerrechnungSum(){
		StringBuilder sb = new StringBuilder();
		sb.append("Verrechnung");

		if (actKons != null) {
			List<Verrechnet> leistungen = actKons.getLeistungen();
			Money sum = new Money(0);
			for (Verrechnet leistung : leistungen) {
				int z = leistung.getZahl();
				Money preis = leistung.getEffPreis().multiply(z);
				sum.addMoney(preis);
			}
			sb.append(" (");
			sb.append(sum.getAmountAsString());
			sb.append(")");
		}

		hVerrechnung.setText(sb.toString());
	}

	/**
	 * Leistung anhand des Kuerzels hinzufuegen
	 */
	private boolean addLeistungByMnemonic(String mnemonic, boolean approximation, boolean multi){
		boolean success = false;

		if (actKons != null && !StringTool.isNothing(mnemonic)) {
			Query<Artikel> query = new Query<Artikel>(Artikel.class);
			if (approximation) {
				query.add("Eigenname", "LIKE", mnemonic + "%");
			} else {
				query.add("Eigenname", "=", mnemonic);
			}
			List<Artikel> artikels = query.execute();

			if (artikels != null && !artikels.isEmpty()) {
				List<Artikel> selection = new ArrayList<Artikel>();
				if (multi) {
					selection.addAll(artikels);
				} else {
					selection.add(artikels.get(0));
				}

				List<Result<IVerrechenbar>> results = new ArrayList<Result<IVerrechenbar>>();
				PersistentObjectFactory factory = new PersistentObjectFactory();
				for (Artikel artikel : artikels) {
					String typ = artikel.get("Typ");
					String id = artikel.getId();

					// work-around for articles without class information (plugin
					// elexis-artikel-schweiz)
					if (typ.equals("Medikament") || typ.equals("Medical") || typ.equals("MiGeL")) {
						typ = "ch.elexis.artikel_ch.data." + typ;
					}

					PersistentObject po = factory.createFromString(typ + "::" + id);
					if (po instanceof IVerrechenbar) {
						Result<IVerrechenbar> result = actKons.addLeistung((IVerrechenbar) po);
						if (!result.isOK()) {
							results.add(result);
						}
					}
				}

				verrechnungViewer.refresh();
				updateVerrechnungSum();

				if (results.isEmpty()) {
					success = true;
				} else {
					StringBuffer sb = new StringBuffer();
					boolean first = true;
					for (Result<IVerrechenbar> result : results) {
						if (first) {
							first = false;
						} else {
							sb.append("; ");
						}
						sb.append(result.toString());
						SWTHelper.alert("Diese Verrechnung ist ungültig", sb.toString());
					}
				}
			}
		}

		return success;
	}

	public void updateKonsultation(boolean updateText){
		if (actKons != null) {
			hVerrechnung.setEnabled(true);
			tVerrechnungKuerzel.setEnabled(true);
			log.debug("Konsultation: " + actKons.getId());
		} else {
			hVerrechnung.setEnabled(false);
			tVerrechnungKuerzel.setEnabled(false);
			log.debug("Konsultation: null");
		}
	}

	private void highlightVerrechnung(boolean highlight){
		Table table = verrechnungViewer.getTable();

		if (highlight) {
			// set highlighting color
			table.setBackground(highlightColor);
		} else {
			// set default color
			table.setBackground(verrechnungViewerColor);
		}
	}

	/*
	 * Return the index of the selected item. -1 if it could not be found
	 */
	private void deleleSelectedItem(){
		IStructuredSelection sel = (IStructuredSelection) verrechnungViewer.getSelection();
		if (sel != null) {
			for (Object obj : sel.toArray()) {
				if (obj instanceof Verrechnet) {
					Verrechnet verrechnet = (Verrechnet) obj;
					Result<Verrechnet> result = actKons.removeLeistung(verrechnet);
					if (!result.isOK()) {
						SWTHelper.alert("Leistungsposition kann nicht entfernt werden",
							result.toString());
					}
					verrechnungViewer.refresh();
					updateVerrechnungSum();
				}
			}
		}
	}

	private void makeActions(){
		delVerrechnetAction = new Action("Leistungsposition entfernen") {
			@Override
			public void run(){
				deleleSelectedItem();
			}
		};
		changeVerrechnetPreisAction = new Action("Preis ändern") {
			@Override
			public void run(){
				Object sel =
					((IStructuredSelection) verrechnungViewer.getSelection()).getFirstElement();
				if (sel != null) {
					Verrechnet verrechnet = (Verrechnet) sel;
					// String p=Rechnung.geldFormat.format(verrechnet.getEffPreisInRappen()/100.0);
					String p = verrechnet.getEffPreis().getAmountAsString();
					InputDialog dlg = new InputDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"Preis für Leistung ändern",
						"Geben Sie bitte den neuen Preis für die Leistung ein (x.xx)", p, null);
					if (dlg.open() == Dialog.OK) {
						Money newPrice;
						try {
							newPrice = new Money(dlg.getValue());
							verrechnet.setPreis(newPrice);
							verrechnungViewer.refresh();
							updateVerrechnungSum();
						} catch (ParseException e) {
							ExHandler.handle(e);
							SWTHelper.showError("Falsche Eingabe",
								"Konnte Angabe nicht interpretieren");
						}

					}
				}
			}
		};
		changeVerrechnetZahlAction = new Action("Zahl ändern") {
			@Override
			public void run(){
				Object sel =
					((IStructuredSelection) verrechnungViewer.getSelection()).getFirstElement();
				if (sel != null) {
					Verrechnet verrechnet = (Verrechnet) sel;
					String p = Integer.toString(verrechnet.getZahl());
					InputDialog dlg = new InputDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"Zahl der Leistung ändern",
						"Geben Sie bitte die neue Anwendungszahl für die Leistung bzw. den Artikel ein",
						p, null);
					if (dlg.open() == Dialog.OK) {
						int vorher = verrechnet.getZahl();
						int neu = Integer.parseInt(dlg.getValue());
						verrechnet.setZahl(neu);
						IVerrechenbar verrechenbar = verrechnet.getVerrechenbar();
						if (verrechenbar instanceof Artikel) {
							Artikel art = (Artikel) verrechenbar;
							art.einzelRuecknahme(vorher);
							art.einzelAbgabe(neu);
						}

						verrechnungViewer.refresh();
						updateVerrechnungSum();
					}
				}
			}
		};

	}

	private void logEvent(String msg){
		StringBuilder sb = new StringBuilder(msg + ": ");
		if (actKons == null) {
			sb.append("actKons null");
		} else {
			sb.append("kons vom " + actKons.getDatum());
			sb.append(" " + actKons.getFall().getPatient().getPersonalia());
		}
		sb.append(" sum: " + hVerrechnung.getText());
		log.debug(sb.toString());
	}

	@Override
	public void setPatient(Patient newPatient){
		// nothing todo
	}

	@Override
	public void setKons(Konsultation newKons, KonsActions op){
		if (KonsActions.ACTIVATE_KONS == op) {
			actKons = newKons;
			updateKonsultation(false);
			verrechnungViewer.refresh();
			updateVerrechnungSum();
		}
	}

	@Override
	public void visible(boolean mode){
		// nothing todo
	}

	@Override
	public void activation(boolean mode){
		// nothing todo
	}

	public TableViewer getVerrechnungViewer(){
		return verrechnungViewer;
	}

}
