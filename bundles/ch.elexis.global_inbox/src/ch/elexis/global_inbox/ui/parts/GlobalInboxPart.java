package ch.elexis.global_inbox.ui.parts;

import javax.inject.Inject;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.global_inbox.model.GlobalInboxEntry;
import ch.elexis.global_inbox.ui.Messages;

public class GlobalInboxPart {
	
	private TableViewer tv;
	private String[] columnHeaders = new String[] {
		Messages.InboxView_category, Messages.InboxView_title
	};
	private TableColumn[] tc;
	private IStatus inboxConfigStat;
	private boolean configErrorShown = false;
	private GlobalInboxContentProvider cp;
	
	@Inject
	public GlobalInboxPart(Composite parent, EMenuService menuService,
		ESelectionService selectionService){
		
		Table table = new Table(parent, SWT.FULL_SELECTION);
		tv = new TableViewer(table);
		tc = new TableColumn[columnHeaders.length];
		for (int i = 0; i < tc.length; i++) {
			tc[i] = new TableColumn(table, SWT.NONE);
			tc[i].setText(columnHeaders[i]);
			tc[i].setWidth(100);
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		cp = new GlobalInboxContentProvider(this);
		tv.setContentProvider(cp);
		tv.setLabelProvider(new GlobalInboxLabelProvider());
		tv.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2){
				GlobalInboxEntry f1 = (GlobalInboxEntry) e1;
				GlobalInboxEntry f2 = (GlobalInboxEntry) e2;
				return f1.getMainFile().getAbsolutePath()
					.compareTo(f2.getMainFile().getAbsolutePath());
			}
		});
		
		tv.addSelectionChangedListener(event -> {
			GlobalInboxEntry globalInboxEntry =  (GlobalInboxEntry) tv.getStructuredSelection().getFirstElement();
			selectionService.setSelection(globalInboxEntry);
		});
		
		//		cp.setView(this);
		inboxConfigStat = cp.reload();
		
		menuService.registerContextMenu(table,
			"ch.elexis.global_inbox.popupmenu.globalinboxentries");
		
		tv.setInput(this);
	}
	
	@Focus
	public void setFocus(){
		if (!inboxConfigStat.isOK() && !configErrorShown) {
			SWTHelper.alert(Messages.Activator_noInbox,
				Messages.InboxContentProvider_noInboxDefined);
			configErrorShown = true;
		}
	}
	
	public void reload(){
		UiDesk.asyncExec(new Runnable() {
			@Override
			public void run(){
				tv.refresh();
			}
		});
	}
	
	public void reloadInbox(){
		IStatus status = cp.reload();
		if (status == Status.CANCEL_STATUS) {
			SWTHelper.showError(Messages.InboxView_error, Messages.InvoxView_errorCantDetectInbox);
		}
	}
	
}
