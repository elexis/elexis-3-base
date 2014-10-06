package ch.elexis.global_inbox;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.text.GenericDocument;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.data.Patient;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;

public class InboxView extends ViewPart {
	private TableViewer tv;
	private IAction addAction, deleteAction, execAction, reloadAction;
	String[] columnHeaders = new String[] {
		Messages.InboxView_category, Messages.InboxView_title
	};
	TableColumn[] tc;
	
	public InboxView(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void createPartControl(Composite parent){
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
		InboxContentProvider cp = Activator.getDefault().getContentProvider();
		makeActions();
		tv.setContentProvider(cp);
		tv.setLabelProvider(new InboxLabelProvider());
		tv.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2){
				File f1 = (File) e1;
				File f2 = (File) e2;
				return f1.getAbsolutePath().compareTo(f2.getAbsolutePath());
			}
		});
		tv.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
				addAction.setEnabled(!sel.isEmpty());
				deleteAction.setEnabled(!sel.isEmpty());
				execAction.setEnabled(!sel.isEmpty());
			}
		});
		cp.setView(this);
		cp.reload();
		tv.setInput(this);
		final MenuManager mgr = new MenuManager();
		mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager){
				mgr.add(addAction);
				mgr.add(execAction);
				mgr.add(new Separator());
				mgr.add(deleteAction);
			}
		});
		mgr.setRemoveAllWhenShown(true);
		table.setMenu(mgr.createContextMenu(table));
		ViewMenus menus = new ViewMenus(getViewSite());
		menus.createToolbar(addAction, execAction, reloadAction, null, deleteAction);
		addAction.setEnabled(false);
		deleteAction.setEnabled(false);
		execAction.setEnabled(false);
	}
	
	@Override
	public void dispose(){
		Activator.getDefault().getContentProvider().setView(null);
		super.dispose();
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
	public void reload(){
		UiDesk.asyncExec(new Runnable() {
			@Override
			public void run(){
				tv.refresh();
			}
		});
	}
	
	public File getSelection(){
		IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
		if (sel.isEmpty()) {
			return null;
		}
		return (File) sel.getFirstElement();
	}
	
	private void makeActions(){
		addAction = new Action(Messages.InboxView_assign) {
			{
				setToolTipText(Messages.InboxView_assignThisDocument);
				setImageDescriptor(Images.IMG_OK.getImageDescriptor());
			}
			
			@Override
			public void run(){
				File sel = getSelection();
				Patient pat = ElexisEventDispatcher.getSelectedPatient();
				if (sel != null && pat != null) {
					if (SWTHelper.askYesNo(
						Messages.InboxView_inbox,
						MessageFormat.format(Messages.InboxView_assignxtoy, sel.getName(),
							pat.getLabel()))) {
						IDocumentManager dm =
							(IDocumentManager) Extensions
								.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
						try {
							String cat = Activator.getDefault().getCategory(sel);
							if (cat.equals("-")) {
								cat = "";
							}
							tv.remove(sel);
							GenericDocument fd =
								new GenericDocument(pat, sel.getName(), cat, sel,
									new TimeTool().toString(TimeTool.DATE_GER), "", null);
							boolean bSucc = sel.delete();
							sel = null;
							dm.addDocument(fd);
							fd = null;
							Activator.getDefault().getContentProvider().reload();
						} catch (Exception ex) {
							ExHandler.handle(ex);
							SWTHelper.alert(Messages.InboxView_error, ex.getMessage());
						}
					}
				}
			}
		};
		deleteAction = new Action(Messages.InboxView_delete) {
			{
				setToolTipText(Messages.InboxView_reallydelete);
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
			}
			
			@Override
			public void run(){
				File sel = getSelection();
				if (SWTHelper.askYesNo(Messages.InboxView_inbox,
					MessageFormat.format(Messages.InboxView_thisreallydelete, sel.getName()))) {
					sel.delete();
					Activator.getDefault().getContentProvider().reload();
				}
			}
		};
		
		execAction = new Action(Messages.InboxView_view) {
			{
				setToolTipText(Messages.InboxView_viewThisDocument);
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
			}
			
			@Override
			public void run(){
				try {
					File sel = getSelection();
					String ext = FileTool.getExtension(sel.getName());
					Program proggie = Program.findProgram(ext);
					String arg = sel.getAbsolutePath();
					if (proggie != null) {
						proggie.execute(arg);
					} else {
						if (Program.launch(sel.getAbsolutePath()) == false) {
							Runtime.getRuntime().exec(arg);
						}
						
					}
					
				} catch (Exception ex) {
					ExHandler.handle(ex);
					SWTHelper.showError(Messages.InboxView_couldNotStart, ex.getMessage());
				}
			}
		};
		reloadAction = new Action(Messages.InboxView_reload) {
			{
				setToolTipText(Messages.InboxView_reloadNow);
				setImageDescriptor(Images.IMG_REFRESH.getImageDescriptor());
			}
			
			@Override
			public void run(){
				Activator.getDefault().getContentProvider().reload();
			}
		};
	}
}
