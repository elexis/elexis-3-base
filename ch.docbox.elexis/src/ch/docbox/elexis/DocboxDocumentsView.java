/*******************************************************************************
 * Copyright (c) 2010, Oliver Egger, visionary ag
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *    
 *******************************************************************************/
package ch.docbox.elexis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ch.docbox.cdach.CdaChXPath;
import ch.docbox.model.CdaMessage;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.exchange.KontaktMatcher;
import ch.elexis.core.ui.exchange.KontaktMatcher.CreateMode;
import ch.elexis.core.ui.util.Log;
import ch.elexis.data.Anwender;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.elexis.data.Xid;

/**
 * Displays the documents downloaded from docbox (doctrans)
 */
public class DocboxDocumentsView extends ViewPart implements ISaveablePart2, IActivationListener,
		HeartListener {
	
	public static final String ID = "chdocbox.elexis.DocboxDocumentsView";
	
	private TableViewer tableViewer;
	private Table table;
	private Font boldFont;
	
	private int sortColumn = 0;
	private boolean sortReverse = false;
	
	public CdaMessage selectedCdaMessage;
	
	private final ElexisEventListenerImpl reloadListener = new ElexisUiEventListenerImpl(
		CdaMessage.class, ElexisEvent.EVENT_RELOAD) {
		public void runInUi(ElexisEvent ev){
			if (!tableViewer.getControl().isDisposed()) {
				log.log("reloadListener refresh", Log.DEBUGMSG);
				tableViewer.refresh(true);
			}
		}
	};
	
	private final ElexisEventListenerImpl updateListener =
		new ElexisUiEventListenerImpl(
		CdaMessage.class, ElexisEvent.EVENT_UPDATE) {
		public void runInUi(ElexisEvent ev){
			if (!tableViewer.getControl().isDisposed()) {
				log.log("updateListener refresh", Log.DEBUGMSG);
				tableViewer.refresh(true);
			}
		}
	};
	
	private final ElexisUiEventListenerImpl patientSelectionListener =
		new ElexisUiEventListenerImpl(Patient.class, ElexisEvent.EVENT_SELECTED) {
			
			@Override
			public void runInUi(ElexisEvent ev){
				
				ISelection selection = tableViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj != null) {
					CdaMessage cdaMessage = (CdaMessage) obj;
					if (!cdaMessage.isEqualsPatient((Patient) ev.getObject())) {
						tableViewer.setSelection(null);
					} else {
						
					}
					
				}
				
			}
			
		};
	
	ElexisEventListenerImpl eeli_user =
		new ElexisUiEventListenerImpl(Anwender.class,
		ElexisEvent.EVENT_USER_CHANGED) {
		
		public void runInUi(ElexisEvent ev){
			userChanged();
		}
	};
	
	private Action actionOpenAttachments;
	private Action actionDeleteDocument;
	private Action actionShowCdaDocument;
	private Action actionCreatePatient;
	
	protected static Log log = Log.get("DocboxDocumentsView"); //$NON-NLS-1$
	
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput){}
		
		public void dispose(){}
		
		public Object[] getElements(Object parent){
			return CdaMessage.getCdaMessages();
		}
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider,
			ITableFontProvider {
		public String getColumnText(Object obj, int index){
			CdaMessage cdaMessage = (CdaMessage) obj;
			switch (index) {
			case 0:
				return cdaMessage.getDate();
			case 1:
				return cdaMessage.getTitle();
			case 2:
				return cdaMessage.getSender();
			case 3:
				return cdaMessage.getPatient();
			case 4:
				return cdaMessage.getFilesListing();
			default:
				return "?";
			}
		}
		
		public Image getColumnImage(Object obj, int index){
			return null;
		}
		
		public Image getImage(Object obj){
			return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
		
		public Font getFont(Object element, int columnIndex){
			Font font = null;
			if (element instanceof CdaMessage) {
				CdaMessage cdaMessage = (CdaMessage) element;
				if (cdaMessage.isUnread()) {
					font = boldFont;
				}
			}
			return font;
		}
	}
	
	class Sorter extends ViewerSorter {
		
		@Override
		public int compare(Viewer viewer, Object e1, Object e2){
			if ((e1 instanceof CdaMessage) && (e2 instanceof CdaMessage)) {
				CdaMessage d1 = (CdaMessage) e1;
				CdaMessage d2 = (CdaMessage) e2;
				String c1 = "";
				String c2 = "";
				switch (sortColumn) {
				case 0:
					c1 = d1.getCreationDate();
					c2 = d2.getCreationDate();
					break;
				case 1:
					c1 = d1.getTitle();
					c2 = d2.getTitle();
					break;
				case 2:
					c1 = d1.getSender();
					c2 = d2.getSender();
					break;
				case 3:
					c1 = d1.getPatient();
					c2 = d2.getPatient();
					break;
				case 4:
					c1 = d1.getFilesListing();
					c2 = d2.getFilesListing();
					break;
				}
				if (sortReverse) {
					return c1.compareTo(c2);
				} else {
					return c2.compareTo(c1);
				}
			}
			return 0;
		}
		
	}
	
	class SortListener extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent e){
			TableColumn col = (TableColumn) e.getSource();
			
			Integer colNo = (Integer) col.getData();
			
			if (colNo != null) {
				if (colNo == sortColumn) {
					sortReverse = !sortReverse;
				} else {
					sortReverse = false;
					sortColumn = colNo;
				}
				tableViewer.refresh();
			}
		}
		
	}
	
	public DocboxDocumentsView(){
		super();
		CdaMessage.load("1");
	}
	
	private String[] getColumnLabels(){
		String columnLabels[] =
			{
				Messages.DocboxDocumentsView_DateSent, Messages.DocboxDocumentsView_Title,
				Messages.DocboxDocumentsView_Sender, Messages.DocboxDocumentsView_Patient,
				Messages.DocboxDocumentsView_Attachments
			};
		return columnLabels;
	}
	
	private int[] getColumnWidth(){
		int columnWidth[] = {
			80, 150, 200, 200, 500
		};
		return columnWidth;
	}
	
	private Font createBoldFont(Font baseFont){
		FontData fd = baseFont.getFontData()[0];
		Font font =
			new Font(baseFont.getDevice(), fd.getName(), fd.getHeight(), fd.getStyle() | SWT.BOLD);
		return font;
	}
	
	@Override
	public void createPartControl(Composite parent){
		table = new Table(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		
		String[] colLabels = getColumnLabels();
		int columnWidth[] = getColumnWidth();
		SortListener sortListener = new SortListener();
		TableColumn[] cols = new TableColumn[colLabels.length];
		for (int i = 0; i < colLabels.length; i++) {
			cols[i] = new TableColumn(table, SWT.NONE);
			cols[i].setWidth(columnWidth[i]);
			cols[i].setText(colLabels[i]);
			cols[i].setData(new Integer(i));
			cols[i].addSelectionListener(sortListener);
		}
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		boldFont = createBoldFont(table.getFont());
		
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new ViewContentProvider());
		tableViewer.setLabelProvider(new ViewLabelProvider());
		tableViewer.setSorter(new Sorter());
		tableViewer.setUseHashlookup(true);
		
		Transfer[] transferTypes = new Transfer[] {
			FileTransfer.getInstance()
		};
		
		tableViewer.addDragSupport(DND.DROP_COPY, transferTypes, new DragSourceListener() {
			
			private CdaMessage cdaMessage;
			
			@Override
			public void dragStart(DragSourceEvent event){
				event.doit = true;
				event.detail = DND.DROP_MOVE;
				log.log("dragStart", Log.DEBUGMSG);
			}
			
			@Override
			public void dragSetData(DragSourceEvent event){
				
				ISelection selection = tableViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				
				if (obj != null) {
					cdaMessage = (CdaMessage) obj;
					String files[] = cdaMessage.getFiles();
					for (int i = 0; i < files.length; ++i) {
						files[i] = cdaMessage.getPath(files[i]);
						log.log("dragSetData " + files[i], Log.DEBUGMSG);
					}
					event.data = files;
				}
				
			}
			
			@Override
			public void dragFinished(DragSourceEvent event){
				log.log("dragFinished", Log.DEBUGMSG);
				if (event.detail == 1) {
					cdaMessage.setAssignedToOmnivore();
				}
			}
			
		});
		
		selectionEvent(CoreHub.actUser);
		tableViewer.setInput(getViewSite());
		
		actionOpenAttachments = new Action(Messages.DocboxDocumentsView_Action_AttachmentsOpen) {
			public void run(){
				ISelection selection = tableViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				CdaMessage cdaMessage = (CdaMessage) obj;
				cdaMessage.execute();
			}
		};
		
		actionShowCdaDocument = new Action(Messages.DocboxDocumentsView_Action_ShowCdaDocument) {
			public void run(){
				ISelection selection = tableViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				CdaMessage cdaMessage = (CdaMessage) obj;
				TextTransfer textTransfer = TextTransfer.getInstance();
				final Clipboard cb = new Clipboard(UiDesk.getDisplay());
				cb.setContents(new Object[] {
					cdaMessage.getCda()
				}, new Transfer[] {
					textTransfer
				});
			}
		};
		
		actionDeleteDocument = new Action(Messages.DocboxDocumentsView_Action_Delete) {
			public void run(){
				ISelection selection = tableViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj != null) {
					CdaMessage cdaMessage = (CdaMessage) obj;
					MessageBox messageBox =
						new MessageBox(UiDesk.getDisplay().getActiveShell(), SWT.ICON_WARNING
							| SWT.OK | SWT.CANCEL);
					messageBox.setText(Messages.DocboxDocumentsView_Action_Delete);
					messageBox
						.setMessage(String.format(
							Messages.DocboxDocumentsView_Action_DeleteConfirmMsg,
							cdaMessage.getTitle()));
					if (messageBox.open() == SWT.OK) {
						cdaMessage.deleteDocs();
						tableViewer.refresh();
					}
				}
			}
		};
		
		actionCreatePatient = new Action(Messages.DocboxDocumentsView_Action_CreatePatient) {
			public void run(){
				ISelection selection = tableViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				
				if (obj != null) {
					CdaMessage cdaMessage = (CdaMessage) obj;
					
					CdaChXPath xpath = new CdaChXPath();
					String name = cdaMessage.getCda();
					if (name != null) {
						xpath.setPatientDocument(cdaMessage.getCda());
						
						String family = xpath.getPatientLastName();
						String given = xpath.getPatientFirstName();
						String streetAdressLine = xpath.getPatientStreet();
						String plz = xpath.getPatientPlz();
						String city = xpath.getPatientCity();
						
						Patient p = new Patient(family, given, "", "");
						p.set(Person.NAME, family);
						p.set(Person.FIRSTNAME, given);
						
						p.set(Kontakt.FLD_STREET, streetAdressLine);
						p.set(Kontakt.FLD_ZIP, plz);
						p.set(Kontakt.FLD_PLACE, city);
						
						p.set(Patient.FLD_E_MAIL, xpath.getPatientEmail());
						if ("M".equals(xpath.getPatientGender())) {
							p.set(Patient.FLD_SEX, "m");
						}
						if ("F".equals(xpath.getPatientGender())) {
							p.set(Patient.FLD_SEX, "w");
						}
						p.set(Patient.FLD_PHONE1, xpath.getPatientHomePhone());
						p.set(Patient.FLD_MOBILEPHONE, xpath.getPatientMobile());
						p.set(Patient.FLD_PHONE2, xpath.getPatientOfficePhone());
						p.set(Patient.BIRTHDATE, xpath.getPatientDateOfBirth());
						
						String ahv = xpath.getPatientAhv13();
						if (ahv != null && !"".equals(ahv)) {
							p.addXid(Xid.DOMAIN_AHV, xpath.getPatientAhv13(), true);
						}
						
						ElexisEventDispatcher.fireSelectionEvent((PersistentObject) p);
						ElexisEventDispatcher.reload(Patient.class);
					}
				}
			}
		};
		
		MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);
		mgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager){
				manager.add(actionOpenAttachments);
				manager.add(actionDeleteDocument);
				manager.add(actionShowCdaDocument);
				manager.add(actionCreatePatient);
			}
		});
		tableViewer.getControl().setMenu(mgr.createContextMenu(tableViewer.getControl()));
		
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event){
				actionOpenAttachments.run();
			}
		});
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event){
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if ((sel != null) && !sel.isEmpty()) {
					Object object = sel.getFirstElement();
					if (object instanceof CdaMessage) {
						CdaMessage cdaMessage = (CdaMessage) object;
						cdaMessage.setRead();
						// try to find the matchting person
						
						CdaChXPath cdaChXPath = new CdaChXPath();
						String cda = cdaMessage.getCda();
						if (cda != null) {
							cdaChXPath.setPatientDocument(cda);
							String lastName = cdaChXPath.getPatientLastName();
							String firstName = cdaChXPath.getPatientFirstName();
							String geburtsdatum = cdaChXPath.getPatientDateOfBirth();
							if (cdaChXPath.getPatientNumber() != null) {
								String patId =
									new Query<Patient>(Patient.class).findSingle(
										"PatientNr", "=", cdaChXPath.getPatientNumber());//$NON-NLS-1$ //$NON-NLS-2$
								if (patId != null) {
									
									Patient elexisPatient = Patient.load(patId);
									if (elexisPatient != null
										&& elexisPatient.getName().equals(lastName)
										&& elexisPatient.getVorname().equals(firstName)) {
										log.log("selecting patient by id with " + lastName + ", "
											+ firstName, Log.DEBUGMSG);
										ElexisEventDispatcher.fireSelectionEvent(elexisPatient);
										return;
									}
								}
							}
							if (KontaktMatcher.findPerson(lastName, firstName, geburtsdatum, null,
								null, null, null, null, CreateMode.FAIL) != null) {
								log.log("selecting patient by demographics " + lastName + ", "
									+ firstName, Log.DEBUGMSG);
								Patient elexisPatient =
									KontaktMatcher.findPatient(lastName, firstName, geburtsdatum,
										null, null, null, null, null, CreateMode.FAIL);
								if (elexisPatient != null) {
									ElexisEventDispatcher.fireSelectionEvent(elexisPatient);
								}
							}
						}
						tableViewer.refresh();
					}
				}
				
			}
			
		});
		
		GlobalEventDispatcher.addActivationListener(this, getViewSite().getPart());
		
	}
	
	void userChanged(){
		tableViewer.refresh();
	}
	
	public int promptToSaveOnClose(){
		return 0;
	}
	
	public void doSave(IProgressMonitor monitor){}
	
	public boolean isDirty(){
		return false;
	}
	
	public boolean isSaveAsAllowed(){
		return false;
	}
	
	public boolean isSaveOnCloseNeeded(){
		return false;
	}
	
	public void doSaveAs(){}
	
	public void selectionEvent(PersistentObject obj){
		if (obj instanceof CdaMessage) {
			CdaMessage cdaMessage = (CdaMessage) obj;
			cdaMessage.setRead();
			tableViewer.refresh();
		} else if (obj instanceof Anwender) {
			tableViewer.refresh();
		}
	}
	
	public void activation(boolean mode){}
	
	public void visible(boolean mode){
		if (mode == true) {
			CoreHub.heart.addListener(this);
			ElexisEventDispatcher.getInstance().addListeners(reloadListener, updateListener,
				patientSelectionListener);
			heartbeat();
		} else {
			CoreHub.heart.removeListener(this);
			ElexisEventDispatcher.getInstance().removeListeners(reloadListener, updateListener,
				patientSelectionListener);
		}
		
	};
	
	public void clearEvent(Class<? extends PersistentObject> template){}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus(){
		tableViewer.getControl().setFocus();
	}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, getViewSite().getPart());
		super.dispose();
	}
	
	public void refresh(){
		tableViewer.refresh();
	}
	
	public void objectChanged(PersistentObject obj){
		if (obj instanceof CdaMessage) {
			tableViewer.refresh();
		}
	}
	
	public void objectCreated(PersistentObject obj){
		if (obj instanceof CdaMessage) {
			tableViewer.refresh();
		}
	}
	
	public void objectDeleted(PersistentObject obj){
		if (obj instanceof CdaMessage) {
			tableViewer.refresh();
		}
	}
	
	public void heartbeat(){
		reloadListener.catchElexisEvent(new ElexisEvent(null, CdaMessage.class,
			ElexisEvent.EVENT_RELOAD));
		updateListener.catchElexisEvent(new ElexisEvent(null, CdaMessage.class,
			ElexisEvent.EVENT_UPDATE));
	}
	
}
