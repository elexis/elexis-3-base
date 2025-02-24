package com.hilotec.elexis.kgview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
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

import com.hilotec.elexis.kgview.data.KonsData;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.util.PersistentObjectDragSource;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.rgw.tools.StringTool;
import jakarta.inject.Inject;

public class Therapieliste extends ViewPart implements IRefreshable {
	final public static String ID = "com.hilotec.elexis.kgview.Therapieliste";
	private TableViewer tv;
	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	@Override
	public void createPartControl(Composite parent) {
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
		tc.setText("Therapie");

		tv.setContentProvider(new IStructuredContentProvider() {
			private Patient pat;

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				this.pat = (Patient) newInput;
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				List<Konsultation> kl = ArchivKG.getKonsultationen(pat, false);
				ArrayList<KonsData> list = new ArrayList<KonsData>(kl.size());

				// Liste mit KonsDatas zusammenstellen
				for (Konsultation k : kl) {
					KonsData kd = KonsData.load(k);
					if (kd == null || StringTool.isNothing(kd.getTherapie()))
						continue;
					list.add(kd);
				}
				return list.toArray();
			}
		});

		// Label provider um mehrzeilige Zellen zu erlauben
		tv.setLabelProvider(new OwnerDrawLabelProvider() {
			private String getText(KonsData kd, Event event) {
				if (event.index == 0)
					return kd.getKonsultation().getDatum();
				else
					return StringTool.unNull(kd.getTherapie());
			}

			protected void paint(Event event, Object element) {
				KonsData kd = (KonsData) element;
				String text = getText(kd, event);
				event.gc.drawText(text, event.x, event.y, true);
			}

			protected void measure(Event event, Object element) {
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
			public void doubleClick(DoubleClickEvent event) {
				TableItem[] tis = tv.getTable().getSelection();
				if (tis.length != 1)
					return;
				KonsData kd = (KonsData) tis[0].getData();
				ElexisEventDispatcher.fireSelectionEvent(kd.getKonsultation());
			}
		});

		// Drag source um per D&D Therapien in die Therapieliste uebernehmen zu
		// koennen.
		new PersistentObjectDragSource(tv);

		tv.setInput(ElexisEventDispatcher.getSelectedPatient());
		getSite().getPage().addPartListener(udpateOnVisible);
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);
		super.dispose();
	}

	public void setFocus() {
	}

	@Optional
	@Inject
	void crudEncounter(@UIEventTopic(ElexisEventTopics.BASE_MODEL + "*") IEncounter encounter) {
		CoreUiUtil.runAsyncIfActive(() -> {
			tv.refresh();
		}, tv);
	}

	@Inject
	void activePatient(@Optional IPatient patient) {
		CoreUiUtil.runAsyncIfActive(() -> {
			Patient p = (Patient) NoPoUtil.loadAsPersistentObject(patient);
			tv.setInput(p);
		}, tv);
	}

	@Override
	public void refresh() {
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}
}
