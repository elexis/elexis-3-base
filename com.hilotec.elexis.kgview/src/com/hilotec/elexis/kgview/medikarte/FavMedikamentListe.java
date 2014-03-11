package com.hilotec.elexis.kgview.medikarte;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.PersistentObjectDragSource;
import ch.elexis.core.ui.util.PersistentObjectDropTarget;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

import com.hilotec.elexis.kgview.Preferences;
import com.hilotec.elexis.kgview.data.FavMedikament;

/**
 * Liste der Favorisierten Medikamente anzeigen. Bietet die Möglichkeit zu suchen, neue Eintraege zu
 * erstellen indem ein neues Medikament per Drag&Drop auf die Tabelle gezogen wird, Eintraege zu
 * veraendern und zu loeschen.
 * 
 * @author Antoine Kaufmann
 */
public class FavMedikamentListe extends ViewPart implements ElexisEventListener {
	public static final String ID = "com.hilotec.elexis.kgview.medikarte.FavMedikamentListe";
	
	private Text suche;
	private Table table;
	
	private Action actEdit;
	private Action actDelete;
	private Action actCheckList;
	
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout(2, false));
		
		Label l = new Label(parent, SWT.NONE);
		l.setText("Suche");
		
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		suche = new Text(parent, SWT.BORDER);
		suche.setLayoutData(gd);
		suche.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e){
				refresh();
			}
		});
		
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		table = new Table(parent, SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setLayoutData(gd);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tc;
		
		tc = new TableColumn(table, 0);
		tc.setText("Bezeichnung");
		tc.setWidth(180);
		
		tc = new TableColumn(table, 0);
		tc.setText("Zweck");
		tc.setWidth(180);
		
		tc = new TableColumn(table, 0);
		tc.setText("Einheit");
		tc.setWidth(40);
		
		if (Preferences.getOrdnungszahlInFML()) {
			tc = new TableColumn(table, 0);
			tc.setText("Ordnungszahl");
			tc.setWidth(40);
		}
		
		// Drop Target um neue Eintraege zu erstellen
		new PersistentObjectDropTarget(table, new PersistentObjectDropTarget.IReceiver() {
			@Override
			public void dropped(PersistentObject o, DropTargetEvent e){
				Artikel a = (Artikel) o;
				new FavMedikamentDialog(getSite().getShell(), a).open();
			}
			
			@Override
			public boolean accept(PersistentObject o){
				if (o instanceof Artikel) {
					Artikel a = (Artikel) o;
					// Droppen nur erlauben wenn Medikament noch nicht in Liste
					FavMedikament fm = FavMedikament.load(a.getId());
					return (fm == null);
				}
				return false;
			}
		});
		
		// Drag Source um die Eintraege zu benutzen
		// XXX: Dabei wird kein FavMedikament mitgeben sondern direkt der
		// Artikel
		new PersistentObjectDragSource(table, new PersistentObjectDragSource.ISelectionRenderer() {
			public List<PersistentObject> getSelection(){
				TableItem[] tis = table.getSelection();
				if (table.getSelection() == null)
					return null;
				
				ArrayList<PersistentObject> res = new ArrayList<PersistentObject>(tis.length);
				for (TableItem ti : tis) {
					FavMedikament fm = (FavMedikament) ti.getData();
					res.add(fm.getArtikel());
				}
				return res;
			}
		});
		
		makeActions();
		
		// Mouse Listener zum aendern von Eintraegen
		table.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e){}
			
			public void mouseDown(MouseEvent e){}
			
			public void mouseDoubleClick(MouseEvent e){
				actEdit.run();
			}
		});
		
		// Key Listener zum Loeschen von Eintraegen
		table.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e){}
			
			public void keyPressed(KeyEvent e){
				if (e.keyCode != SWT.DEL)
					return;
				actDelete.run();
			}
		});
		
		// Kontextmenü für Liste
		ViewMenus menus = new ViewMenus(getViewSite());
		menus.createMenu(actCheckList);
		menus.createControlContextMenu(table, actEdit, actDelete);
		refresh();
		ElexisEventDispatcher.getInstance().addListeners(this);
	}
	
	/** Formatiere Volltext (mit \n) fuer Darstellung in Tabelle. */
	private String fmtVolltext(String text){
		return text.replaceAll("[\\n\\r]+", ", ");
	}
	
	/**
	 * Liste neu laden.
	 */
	private void refresh(){
		Query<FavMedikament> qMeds = new Query<FavMedikament>(FavMedikament.class);
		// XXX: Irgendwie unschoen
		qMeds.add("ID", Query.NOT_EQUAL, "VERSION");
		String suchstring = suche.getText();
		if (!suchstring.isEmpty()) {
			qMeds.add(FavMedikament.FLD_BEZEICHNUNG, Query.LIKE, "%" + suchstring + "%");
		}
		qMeds.orderBy(false, FavMedikament.FLD_BEZEICHNUNG);
		
		List<FavMedikament> meds = qMeds.execute();
		table.removeAll();
		for (FavMedikament med : meds) {
			TableItem ti = new TableItem(table, 0);
			ti.setData(med);
			ti.setText(0, med.getBezeichnung());
			ti.setText(1, fmtVolltext(med.getZweck()));
			ti.setText(2, med.getEinheit());
			if (Preferences.getOrdnungszahlInFML()) {
				ti.setText(3, Integer.toString(med.getOrdnungszahl()));
			}
		}
	}
	
	/**
	 * FavMedis auf tote Medikamente pruefen, und ggf. versuchen neu zu verlinken.
	 */
	private static void checkFavMediList(){
		StringBuilder sb = new StringBuilder();
		for (FavMedikament fm : FavMedikament.getAll()) {
			Artikel art = fm.getArtikel();
			// Wir interessieren uns nur fuer geloeschte Artikel
			if (art.exists())
				continue;
			
			String pk = art.get(Artikel.FLD_SUB_ID);
			if (pk == null || pk.isEmpty() || pk.equals("0")) {
				sb.append("Kann nicht verknüpft werden: ");
				sb.append(fm.getBezeichnung());
				sb.append("\n");
				continue;
			}
			
			Query<Artikel> aq = new Query<Artikel>(Artikel.class);
			aq.clear();
			aq.add(Artikel.FLD_SUB_ID, Query.EQUALS, pk);
			List<Artikel> al = aq.execute();
			if (al.isEmpty()) {
				sb.append("Keine Alternative gefunden: ");
				sb.append(fm.getBezeichnung());
				sb.append("\n");
				continue;
			}
			
			boolean rl = false;
			for (Artikel na : al) {
				if (SWTHelper.askYesNo("Fav. Medikamente", "Soll '" + fm.getBezeichnung()
					+ "' neu mit '" + na.getName() + "' verknüpft werden?")) {
					fm.relinkTo(na);
					rl = true;
					break;
				}
			}
			
			if (!rl) {
				sb.append("Medikament nicht neu verknuepft: ");
				sb.append(fm.getBezeichnung());
				sb.append("\n");
			}
		}
		
		if (sb.length() != 0) {
			SWTHelper.showInfo("Favoriten Medikamenten-Listen check", sb.toString());
		}
	}
	
	private void makeActions(){
		actEdit = new Action("Bearbeiten", Action.AS_PUSH_BUTTON) {
			@Override
			public void run(){
				TableItem[] tis = table.getSelection();
				if (tis == null || tis.length != 1)
					return;
				new FavMedikamentDialog(getSite().getShell(), (FavMedikament) tis[0].getData())
					.open();
				refresh();
			}
		};
		
		actDelete = new Action("Löschen", Action.AS_PUSH_BUTTON) {
			@Override
			public void run(){
				TableItem[] tis = table.getSelection();
				if (tis == null || tis.length == 0)
					return;
				
				// Nachfragen
				if (!SWTHelper.askYesNo("Medikament(e) aus Liste entfernen",
					"Sollen das/die ausgewählte(n) Medikament(e) aus der"
						+ " Liste entfernt werden?"))
					return;
				
				for (TableItem ti : tis) {
					FavMedikament fm = (FavMedikament) ti.getData();
					fm.delete();
				}
				refresh();
			}
		};
		
		actCheckList = new Action("Liste prüfen", Action.AS_PUSH_BUTTON) {
			@Override
			public void run(){
				checkFavMediList();
			}
		};
	}
	
	@Override
	public void setFocus(){}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(this);
		super.dispose();
	}
	
	@Override
	public void catchElexisEvent(ElexisEvent ev){
		UiDesk.syncExec(new Runnable() {
			public void run(){
				refresh();
			}
		});
	}
	
	private final ElexisEvent eetmpl = new ElexisEvent(null, FavMedikament.class,
		ElexisEvent.EVENT_CREATE | ElexisEvent.EVENT_DELETE | ElexisEvent.EVENT_UPDATE
			| ElexisEvent.EVENT_RELOAD);
	
	@Override
	public ElexisEvent getElexisEventFilter(){
		return eetmpl;
	}
	
}
