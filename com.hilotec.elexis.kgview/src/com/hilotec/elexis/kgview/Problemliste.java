package com.hilotec.elexis.kgview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.PersistentObjectDragSource;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.rgw.tools.StringTool;

import com.hilotec.elexis.kgview.data.KonsData;

public class Problemliste extends ViewPart implements ElexisEventListener {
	final public static String ID = "com.hilotec.elexis.kgview.Problemliste";
	private TableViewer tv;
	
	@Override
	public void createPartControl(Composite parent){
		tv = new TableViewer(parent);
		
		Table t = tv.getTable();
		t.setHeaderVisible(true);
		
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnPixelData(70));
		layout.addColumnData(new ColumnPixelData(70));
		t.setLayout(layout);
		
		TableColumn tc = new TableColumn(t, 0);
		tc.setText("Datum");
		
		tc = new TableColumn(t, 0);
		tc.setText("Diagnose");
		
		tv.setContentProvider(new IStructuredContentProvider() {
			private Patient pat;
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
				this.pat = (Patient) newInput;
			}
			
			public void dispose(){}
			
			public Object[] getElements(Object inputElement){
				List<Konsultation> kl = ArchivKG.getKonsultationen(pat, false);
				ArrayList<KonsData> list = new ArrayList<KonsData>(kl.size());
				
				// Liste mit KonsDatas zusammenstellen
				for (Konsultation k : kl) {
					KonsData kd = KonsData.load(k);
					if (kd == null || StringTool.isNothing(kd.getDiagnose()))
						continue;
					list.add(kd);
				}
				return list.toArray();
			}
		});
		
		// Label provider um mehrzeilige Zellen zu erlauben
		tv.setLabelProvider(new OwnerDrawLabelProvider() {
			private String getText(KonsData kd, Event event){
				if (event.index == 0)
					return kd.getKonsultation().getDatum();
				else
					return StringTool.unNull(kd.getDiagnose());
			}
			
			protected void paint(Event event, Object element){
				KonsData kd = (KonsData) element;
				String text = getText(kd, event);
				event.gc.drawText(text, event.x, event.y, true);
			}
			
			protected void measure(Event event, Object element){
				KonsData kd = (KonsData) element;
				String text = getText(kd, event);
				Point size = event.gc.textExtent(text);
				event.width = tv.getTable().getColumn(event.index).getWidth();
				if (event.width == 0)
					event.width = 1;
				int lines = size.x / event.width + 1;
				event.height = size.y * lines;
			}
		});
		
		// Doppelklick-Listener zum selektieren der Konsultation
		tv.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event){
				TableItem[] tis = tv.getTable().getSelection();
				if (tis.length != 1)
					return;
				KonsData kd = (KonsData) tis[0].getData();
				ElexisEventDispatcher.fireSelectionEvent(kd.getKonsultation());
			}
		});
		
		// Drag source um per D&D Diagnosen in die Diagnoseliste uebernehmen zu
		// koennen.
		new PersistentObjectDragSource(tv);
		
		tv.setInput(ElexisEventDispatcher.getSelectedPatient());
		new SelListener().init();
		ElexisEventDispatcher.getInstance().addListeners(this);
	}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(this);
		super.dispose();
	}
	
	public void setFocus(){}
	
	/** Selection Listener um bei Patientenwechsel zu aktualisieren */
	private class SelListener extends POSelectionListener<Patient> {
		protected void deselected(Patient p){
			if (tv.getControl() != null && !tv.getControl().isDisposed()) {
				tv.setInput(null);
			}
		}
		
		protected void selected(Patient p){
			if (tv.getControl() != null && !tv.getControl().isDisposed()) {
				tv.setInput(p);
			}
		}
	}
	
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
