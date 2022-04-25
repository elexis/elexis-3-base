package at.medevit.elexis.cobasmira.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import at.medevit.elexis.cobasmira.model.CobasMiraLog;
import at.medevit.elexis.cobasmira.model.CobasMiraMessage;

public class CobasMiraLogContentProvider implements IStructuredContentProvider, PropertyChangeListener {
	protected TableViewer tableViewer;

	public CobasMiraLogContentProvider(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
		CobasMiraLog.getInstance().addPropertyChangeListener(this);
	}

	@Override
	public void dispose() {
		// empty
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<CobasMiraMessage> list = (List<CobasMiraMessage>) inputElement;
		return list.toArray();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		tableViewer.getTable().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				tableViewer.setInput(CobasMiraLog.getInstance().getMessageList());
				tableViewer.refresh();
			}
		});

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
