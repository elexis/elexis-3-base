package ch.elexis.global_inbox.ui.parts;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.e4.events.ElexisUiEventTopics;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.global_inbox.model.GlobalInboxEntry;
import ch.elexis.global_inbox.ui.Constants;
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
		ESelectionService selectionService, IEventBroker eventBroker){
		
		Table table = new Table(parent, SWT.FULL_SELECTION);
		tv = new TableViewer(table);
		tc = new TableColumn[columnHeaders.length];
		for (int i = 0; i < tc.length; i++) {
			tc[i] = new TableColumn(table, SWT.NONE);
			tc[i].setText(columnHeaders[i]);
			if (i == 0) {
				tc[i].setWidth(100);
			} else {
				tc[i].setWidth(250);
			}
			
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
				return f1.getMainFile().getName().toLowerCase()
					.compareTo(f2.getMainFile().getName().toLowerCase());
			}
		});
		
		tv.addSelectionChangedListener(event -> {
			GlobalInboxEntry globalInboxEntry =
				(GlobalInboxEntry) tv.getStructuredSelection().getFirstElement();
			selectionService.setSelection(globalInboxEntry);
			
			if (globalInboxEntry != null) {
				File mainFile = globalInboxEntry.getPdfPreviewFile();
				if (globalInboxEntry.getMimetype().toLowerCase().contains("pdf")) {
					try (ByteArrayInputStream byteArrayInputStream =
						new ByteArrayInputStream(FileUtils.readFileToByteArray(mainFile))) {
						eventBroker.post(ElexisUiEventTopics.EVENT_PREVIEW_MIMETYPE_PDF,
							byteArrayInputStream);
					} catch (IOException e) {
						LoggerFactory.getLogger(getClass()).warn("Exception", e);
					}
				}
			}
		});
		
		inboxConfigStat = cp.reload();
		
		menuService.registerContextMenu(table,
			"ch.elexis.global_inbox.popupmenu.globalinboxentries");
		
		tv.setInput(this);
	}
	
	@Inject
	@Optional
	void handleRemoveAndSelectNext(
		@UIEventTopic(Constants.EVENT_UI_REMOVE_AND_SELECT_NEXT) GlobalInboxEntry gie){
		
		int selectionIndex = tv.getTable().getSelectionIndex();
		tv.remove(gie);
		tv.getTable().setSelection(selectionIndex);
	}
	
	@PreDestroy
	public void destroy(){
		cp.destroy();
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
