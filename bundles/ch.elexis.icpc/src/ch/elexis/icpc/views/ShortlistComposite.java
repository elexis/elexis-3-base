package ch.elexis.icpc.views;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.data.service.StoreToStringServiceHolder;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.icpc.model.icpc.IcpcCode;
import ch.elexis.icpc.service.IcpcModelServiceHolder;

public class ShortlistComposite extends Composite {

	private String[] SHORTLIST_CODES = new String[] { "K86", "K74", "K75", "K77", "T90", "R95", "K78", "K90", "K92", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
			"P17", "R96", "P70", "A96" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public ShortlistComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		List<IcpcCode> shortList = Arrays.asList(SHORTLIST_CODES).stream()
				.map(s -> IcpcModelServiceHolder.get().load(s, IcpcCode.class).get()).collect(Collectors.toList());

		ListViewer listViewer = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL);
		org.eclipse.swt.widgets.List list = listViewer.getList();

		DragSource dragSource = new DragSource(list, DND.DROP_COPY);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new DragSourceListener() {

			@Override
			public void dragStart(DragSourceEvent event) {
				IStructuredSelection ss = listViewer.getStructuredSelection();
				if (ss.isEmpty()) {
					event.data = null;
					event.doit = false;
				} else {
					event.data = (IcpcCode) ss.getFirstElement();
				}
			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				IStructuredSelection ss = listViewer.getStructuredSelection();
				if (!ss.isEmpty()) {
					event.data = StoreToStringServiceHolder.get().storeToString((IcpcCode) ss.getFirstElement())
							.orElse(null);
				}
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
			}
		});

		listViewer.setContentProvider(ArrayContentProvider.getInstance());
		listViewer.setLabelProvider(new DefaultLabelProvider());
		listViewer.setInput(shortList);
	}

}
