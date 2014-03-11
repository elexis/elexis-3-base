package com.hilotec.elexis.kgview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalActions;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

import com.hilotec.elexis.kgview.data.KonsData;

/**
 * Einfache Konsultationsliste die aber auch auf Konsultationszeiten Ruecksicht nimmt beim
 * Sortieren.
 */
public class Konsliste extends ViewPart implements ElexisEventListener {
	protected TableViewer tv;
	protected SelListener sl;
	
	/**
	 * Wrapper um globale delKonsAction da diese keine Bestaetigung verlangt.
	 */
	class KonsLoeschenAct extends Action {
		public KonsLoeschenAct(){
			setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
			setText("Konsultation löschen");
			setToolTipText("Aktuell ausgewählte Konsultation löschen");
		}
		
		@Override
		public void run(){
			Konsultation k = (Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
			if (k != null
				&& SWTHelper.askYesNo("Konsultation löschen",
					"Soll die aktuell ausgewählte Konsultation wirklich "
						+ "gelöscht werden?\nDieser Vorgang kann nicht Rückgängig"
						+ "gemacht werden."))
				GlobalActions.delKonsAction.run();
		}
	}
	
	@Override
	public void createPartControl(Composite parent){
		tv = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
		
		Table t = tv.getTable();
		t.setHeaderVisible(true);
		
		// Konfiguriere Tabellenspalten
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnPixelData(20));
		layout.addColumnData(new ColumnPixelData(70));
		layout.addColumnData(new ColumnPixelData(50));
		layout.addColumnData(new ColumnWeightData(10));
		t.setLayout(layout);
		
		TableColumn tc = new TableColumn(t, 0);
		
		tc = new TableColumn(t, 0);
		tc.setText("Datum");
		
		tc = new TableColumn(t, 0);
		tc.setText("Zeit");
		
		tc = new TableColumn(t, 0);
		tc.setText("Fall");
		
		// Inhalt
		tv.setContentProvider(new IStructuredContentProvider() {
			private Patient pat;
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
				this.pat = (Patient) newInput;
			}
			
			public void dispose(){}
			
			public Object[] getElements(Object inputElement){
				return ArchivKG.getKonsultationen(pat, false).toArray();
			}
		});
		
		// Label provider um mehrzeilige Zellen zu erlauben
		tv.setLabelProvider(new CellLabelProvider() {
			private Color mandantColor(Mandant m){
				return UiDesk.getColorFromRGB(CoreHub.globalCfg.get(
					ch.elexis.core.constants.Preferences.USR_MANDATOR_COLORS_PREFIX + m.getLabel(),
					"ffffff"));
			}
			
			@Override
			public void update(ViewerCell cell){
				Konsultation k = (Konsultation) cell.getElement();
				KonsData kd = KonsData.load(k);
				String s = "";
				switch (cell.getColumnIndex()) {
				case 0:
					int typ = kd.getKonsTyp();
					ImageDescriptor desc = null;
					if (typ == KonsData.KONSTYP_TELEFON)
						desc =
							AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
								"rsc/phone.png");
					else if (typ == KonsData.KONSTYP_HAUSBESUCH)
						desc = Images.IMG_HOME.getImageDescriptor();
					
					if (desc != null)
						cell.setImage(desc.createImage());
					break;
				case 1:
					s = k.getDatum();
					break;
				case 2:
					s = (kd == null ? "" : kd.getKonsBeginn());
					break;
				case 3:
					s = k.getFall().getLabel();
					break;
				}
				// Zum Mandanten passende Hintergrundfarbe
				cell.setBackground(mandantColor(k.getMandant()));
				cell.setText(s);
			}
		});
		
		// Doppelklick-Listener zum selektieren der Konsultation
		tv.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event){
				TableItem[] tis = tv.getTable().getSelection();
				if (tis.length != 1)
					return;
				Konsultation k = (Konsultation) tis[0].getData();
				ElexisEventDispatcher.fireSelectionEvent(k);
			}
		});
		
		// Menuleiste
		ViewMenus menus = new ViewMenus(getViewSite());
		menus.createToolbar(new ArchivKG.NeueKonsAct(KonsData.KONSTYP_NORMAL),
			new ArchivKG.NeueKonsAct(KonsData.KONSTYP_TELEFON), new ArchivKG.NeueKonsAct(
				KonsData.KONSTYP_HAUSBESUCH), null, new ArchivKG.KonsAendernAct(),
			new KonsLoeschenAct());
		
		tv.setInput(ElexisEventDispatcher.getSelectedPatient());
		sl = new SelListener();
		sl.init();
		ElexisEventDispatcher.getInstance().addListeners(this);
	}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(this);
		sl.destroy();
		super.dispose();
	}
	
	/** Selection Listener um bei Patientenwechsel zu aktualisieren */
	private class SelListener extends POSelectionListener<Patient> {
		protected void deselected(Patient p){
			tv.setInput(null);
		}
		
		protected void selected(Patient p){
			tv.setInput(p);
		}
	}
	
	@Override
	public void setFocus(){}
	
	public void catchElexisEvent(ElexisEvent ev){
		UiDesk.syncExec(new Runnable() {
			public void run(){
				tv.refresh();
			}
		});
	}
	
	private ElexisEvent eetmpl = new ElexisEvent(null, Konsultation.class, ElexisEvent.EVENT_CREATE
		| ElexisEvent.EVENT_UPDATE | ElexisEvent.EVENT_DELETE);
	
	public ElexisEvent getElexisEventFilter(){
		return eetmpl;
	}
}
