package com.hilotec.elexis.kgview.medikarte;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.PersistentObjectDropTarget;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.views.TextView;
import ch.elexis.data.Artikel;
import ch.elexis.data.Brief;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;

import com.hilotec.elexis.kgview.data.FavMedikament;
import com.hilotec.elexis.kgview.medikarte.MedikarteEintragComparator.Sortierung;

public class MedikarteView extends ViewPart implements ElexisEventListener {
	public static final String ID = "com.hilotec.elexis.kgview.MedikarteView";
	
	private static final String TEMPLATE_MEDIKARTE = "Medikarte";
	
	private Table table;
	// Alle Verschreibungen anzeigen? Oder nur die aktiven.
	private boolean alle = false;
	// Geloeschte auch anzeigen?
	private boolean geloescht = false;
	// Alphabetisch sortieren
	private boolean sortAlph = false;
	private Patient patient;
	
	private Action actEdit;
	private Action actStop;
	private Action actDelete;
	private Action actFilter;
	private Action actShowDel;
	private Action actDrucken;
	private Action actSortAlph;
	
	@Override
	public void createPartControl(Composite parent){
		table = new Table(parent, SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		// Ein haufen Tabellenueberschriften
		String[] ueberschriften =
			{
				"Ordnungszahl", "Medikament", "Mo", "Mi", "Ab", "Na", "Einh", "Zweck",
				"Einnahmevorschrift", "Von", "Bis"
			};
		int[] breiten = {
			30, 140, 40, 40, 40, 40, 40, 200, 70, 70, 70
		};
		for (int i = 0; i < breiten.length; i++) {
			TableColumn tc = new TableColumn(table, SWT.NONE);
			tc.setText(ueberschriften[i]);
			tc.setWidth(breiten[i]);
		}
		
		makeActions();
		
		// Drop target um neue Medikamente auf die Liste zu nehmen
		new PersistentObjectDropTarget(table, new PersistentObjectDropTarget.IReceiver() {
			public void dropped(PersistentObject o, DropTargetEvent e){
				if (patient == null)
					return;
				FavMedikament fm = FavMedikament.load((Artikel) o);
				new MedikarteEintragDialog(getSite().getShell(), patient, fm).open();
				refresh();
			}
			
			@Override
			public boolean accept(PersistentObject o){
				if (!(o instanceof Artikel) || patient == null)
					return false;
				return (FavMedikament.load((Artikel) o) != null);
			}
		});
		
		// Listener fuer Doppelklick um eintraege zu editieren
		table.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e){}
			
			public void mouseDown(MouseEvent e){}
			
			public void mouseDoubleClick(MouseEvent e){
				actEdit.run();
			}
		});
		
		// Key-Listener zum stoppen von Eintraegen
		table.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e){}
			
			public void keyPressed(KeyEvent e){
				if (e.keyCode != SWT.DEL)
					return;
				actStop.run();
			}
		});
		
		// Menus oben rechts in der View
		ViewMenus menus = new ViewMenus(getViewSite());
		menus.createToolbar(actFilter, actShowDel, actDrucken, actSortAlph);
		
		// Contextmenu fuer Tabelle
		menus.createControlContextMenu(table, actEdit, actStop, actDelete);
		
		ElexisEventDispatcher.getInstance().addListeners(this);
		patient = (Patient) ElexisEventDispatcher.getSelected(Patient.class);
		refresh();
	}
	
	private void makeActions(){
		// Aktion zum Bearbeiten einer Verschreibung
		actEdit = new Action("Bearbeiten", Action.AS_PUSH_BUTTON) {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
			}
			
			@Override
			public void run(){
				TableItem[] sel = table.getSelection();
				if (sel == null || sel.length != 1)
					return;
				Prescription presc = (Prescription) sel[0].getData();
				new MedikarteEintragDialog(getSite().getShell(), patient, presc).open();
				refresh();
			}
		};
		
		// Aktion zum Stoppen einer Verschreibung
		actStop = new Action("Stoppen", Action.AS_PUSH_BUTTON) {
			{
				setImageDescriptor(Images.IMG_AUSRUFEZ_ROT.getImageDescriptor());
			}
			
			@Override
			public void run(){
				TableItem[] tis = table.getSelection();
				if (tis == null || tis.length != 1)
					return;
				Prescription presc = (Prescription) tis[0].getData();
				if (presc.isDeleted() || !presc.getEndDate().equals(""))
					return;
				new MedikarteStopDialog(getSite().getShell(), presc).open();
				refresh();
			}
		};
		
		// Aktion zum Loeschen von Verschreibungen
		actDelete = new Action("Löschen", Action.AS_PUSH_BUTTON) {
			{
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
			}
			
			@Override
			public void run(){
				TableItem[] tis = table.getSelection();
				if (tis == null || tis.length != 1)
					return;
				Prescription presc = (Prescription) tis[0].getData();
				if (presc.isDeleted())
					return;
				
				if (!SWTHelper.askYesNo("Verschreibung loeschen", "Soll der "
					+ "markierte Eintrag wirklich permanent gelöscht werden?"))
					return;
				presc.remove();
				refresh();
			}
		};
		
		// Aktion fuer den Filter-Button
		actFilter = new Action("Alle anzeigen", Action.AS_CHECK_BOX) {
			{
				setImageDescriptor(Images.IMG_FILTER.getImageDescriptor());
				setChecked(true);
			}
			
			@Override
			public void run(){
				alle = !isChecked();
				refresh();
			}
		};
		
		// Aktion fuer den Geloeschte anzeigen Button
		actShowDel = new Action("Gelöschte anzeigen", Action.AS_CHECK_BOX) {
			{
				setImageDescriptor(Images.IMG_BOOK.getImageDescriptor());
			}
			
			@Override
			public void run(){
				geloescht = isChecked();
				refresh();
			}
		};
		
		// Aktion fuer den Drucken-Button
		actDrucken = new Action("Drucken", Action.AS_PUSH_BUTTON) {
			{
				setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());
			}
			
			@Override
			public void run(){
				Konsultation kons =
					(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
				Patient patient = ElexisEventDispatcher.getSelectedPatient();
				
				if (patient == null || kons == null) {
					SWTHelper.alert("Keine Konsultation ausgewählt",
						"Eine Konsutlation muss ausgewählt sein in der "
							+ "die Medikamentenkarte erstellt werden soll.");
					return;
				}
				
				TextView tv;
				try {
					tv = (TextView) getSite().getPage().showView(TextView.ID);
				} catch (PartInitException e) {
					e.printStackTrace();
					return;
				}
				
				// Medikarte aus Vorlage erstellen
				Brief doc =
					tv.getTextContainer().createFromTemplateName(kons, TEMPLATE_MEDIKARTE,
						Brief.UNKNOWN, patient, "Medikamentenkarte");
				tv.openDocument(doc);
			}
		};
		
		// Aktion fuer die alphabetische Sortierung der Eintraege
		actSortAlph = new Action("Alphabetisch sortieren", Action.AS_CHECK_BOX) {
			{
				setImageDescriptor(Images.IMG_ARROWDOWN.getImageDescriptor());
			}
			
			@Override
			public void run(){
				sortAlph = isChecked();
				refresh();
			}
		};
	}
	
	/** Formatiere Volltext (mit \n) fuer Darstellung in Tabelle. */
	private String fmtVolltext(String text){
		return text.replaceAll("[\\n\\r]+", ", ");
	}
	
	private void refresh(){
		table.removeAll();
		if (patient == null)
			return;
		
		// Medikation zu Patient zusammensuchen.
		List<Prescription> l = MedikarteHelpers.medikarteMedikation(patient, alle, geloescht);
		Sortierung s = Sortierung.CHRONOLOGISCH;
		if (sortAlph)
			s = Sortierung.ALPHABETISCH;
		Collections.sort(l, new MedikarteEintragComparator(s));
		
		// Tabelle neu befuellen
		for (Prescription p : l) {
			FavMedikament fm = FavMedikament.load(p.getArtikel());
			String[] dosierung = p.getDosis().split("-");
			if (dosierung.length != 4 || fm == null)
				continue;
			
			TableItem ti = new TableItem(table, 0);
			ti.setData(p);
			
			if (p.isDeleted()) {
				Color red = new Color(Display.getCurrent(), 255, 0, 0);
				ti.setForeground(red);
			}
			
			int i = 0;
			int ord = MedikarteHelpers.getOrdnungszahl(p);
			ti.setText(i++, Integer.toString(ord));
			ti.setText(i++, fm.getBezeichnung());
			ti.setText(i++, dosierung[0]);
			ti.setText(i++, dosierung[1]);
			ti.setText(i++, dosierung[2]);
			ti.setText(i++, dosierung[3]);
			
			ti.setText(i++, fm.getEinheit());
			String z = MedikarteHelpers.getPZweck(p);
			ti.setText(i++, fmtVolltext(z));
			ti.setText(i++, p.getBemerkung());
			
			ti.setText(i++, p.getBeginDate());
			ti.setText(i++, p.getEndDate());
		}
	}
	
	@Override
	public void setFocus(){}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(this);
		super.dispose();
	}
	
	public void catchElexisEvent(final ElexisEvent ev){
		UiDesk.syncExec(new Runnable() {
			public void run(){
				if (ev.getObjectClass().equals(Patient.class)) {
					Patient p = (Patient) ev.getObject();
					if (ev.getType() == ElexisEvent.EVENT_SELECTED)
						patient = p;
					else if (ev.getType() == ElexisEvent.EVENT_DESELECTED)
						patient = null;
					refresh();
				} else if (ev.getObjectClass().equals(Prescription.class)) {
					refresh();
				}
			}
		});
	}
	
	private final ElexisEvent eetmpl = new ElexisEvent(null, null, ElexisEvent.EVENT_SELECTED
		| ElexisEvent.EVENT_DESELECTED | ElexisEvent.EVENT_CREATE | ElexisEvent.EVENT_DELETE);
	
	public ElexisEvent getElexisEventFilter(){
		return eetmpl;
	}
}
