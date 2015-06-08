package at.medevit.elexis.cobasmira.ui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import at.medevit.elexis.cobasmira.connection.CobasMiraConnection;
import at.medevit.elexis.cobasmira.model.CobasMiraLog;
import at.medevit.elexis.cobasmira.model.CobasMiraMapping;
import at.medevit.elexis.cobasmira.model.CobasMiraMessage;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Anwender;

public class DeviceView extends ViewPart {
	private CobasMiraLog log = CobasMiraLog.getInstance();
	
	private DataBindingContext m_bindingContext;
	private CobasMiraMessage message;
	
	private Table tableCobasMiraLog;
	private Table tableCobasMiraMapping;
	private Label lblStatus;
	private TableViewer tableViewer;
	private TableColumn tableZeit;
	private CobasMiraConnection conn = CobasMiraConnection.getInstance();
	private Button buttonActivate;
	private Button buttonDeactivate;
	private Combo anwenderErrMsgCombo;
	private StructuredSelection selectedErrMsgRcvr;
	
	public DeviceView(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout(2, true));
		
		Label lblCobasMiraLog = new Label(parent, SWT.NONE);
		lblCobasMiraLog.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 9, 1));
		lblCobasMiraLog.setText("Nachrichten Log");
		
		/// --- Log Table
		Composite logTableComposite = new Composite(parent, SWT.NONE);
		logTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 9, 1));
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		logTableComposite.setLayout(tableColumnLayout);
		
		tableViewer = new TableViewer(logTableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		tableCobasMiraLog = tableViewer.getTable();
		tableCobasMiraLog.setLinesVisible(true);
		tableCobasMiraLog.setHeaderVisible(true);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableZeit = tableViewerColumn.getColumn();
		tableZeit.setText(Messages.getString("UI.dateTime"));
		tableColumnLayout.setColumnData(tableZeit, new ColumnPixelData(120));
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnTyp = tableViewerColumn_1.getColumn();
		tblclmnTyp.setText(Messages.getString("UI.type"));
		tableColumnLayout.setColumnData(tblclmnTyp, new ColumnPixelData(100));
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnLogeintrag = tableViewerColumn_2.getColumn();
		tblclmnLogeintrag.setText(Messages.getString("UI.description"));
		tableColumnLayout.setColumnData(tblclmnLogeintrag, new ColumnWeightData(50, 200));
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnElexisstatus = tableViewerColumn_3.getColumn();
		tblclmnElexisstatus.setText(Messages.getString("UI.elexis-state"));
		tableColumnLayout.setColumnData(tblclmnElexisstatus, new ColumnWeightData(10, 100));
		
		tableViewer.setComparator(new CobasMiraLogTableSorter());
		tableViewer.setContentProvider(new CobasMiraLogContentProvider(tableViewer));
		tableViewer.setLabelProvider(new CobasMiraLogLabelProvider());
		tableViewer.setInput(log.getMessageList());
		/// --- End of Log Table
		
		Label lblMappingEintrge = new Label(parent, SWT.NONE);
		lblMappingEintrge.setText("Mapping Einträge");
		
		Group grpFehlermeldungen = new Group(parent, SWT.NONE);
		grpFehlermeldungen.setText("Fehlermeldungen");
		grpFehlermeldungen.setLayout(new GridLayout(3, false));
		grpFehlermeldungen.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 5));
		
		Label lblEmpfnger = new Label(grpFehlermeldungen, SWT.NONE);
		lblEmpfnger.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEmpfnger.setText("Empfänger");
		
		ComboViewer comboViewer = new ComboViewer(grpFehlermeldungen, SWT.NONE);
		anwenderErrMsgCombo = comboViewer.getCombo();
		anwenderErrMsgCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element){
				Anwender anw = (Anwender) element;
				return anw.getVorname() + " " + anw.getName();
			}
			
		});
		
		// NEEDED in 2.1
		//TODO: Remove afterwards
		ch.elexis.data.Query<Anwender> qbe = new ch.elexis.data.Query<Anwender>(Anwender.class);
		comboViewer.setInput(qbe.execute().toArray());
		//comboViewer.setInput(Anwender.getAll().toArray());
		//
		
		Anwender selected = getSelectedAnwender();
		if (selected != null)
			comboViewer.setSelection(new StructuredSelection(selected));
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				selectedErrMsgRcvr = (StructuredSelection) event.getSelection();
				Anwender selected = (Anwender) selectedErrMsgRcvr.getFirstElement();
				CoreHub.localCfg.set(Preferences.ERRORMSGRECEIVER, selected.getId());
				CoreHub.localCfg.flush();
			}
		});
		new Label(grpFehlermeldungen, SWT.NONE);
		
		Button button = new Button(grpFehlermeldungen, SWT.CHECK);
		button.setText("Check Button");
		
		Button button_1 = new Button(grpFehlermeldungen, SWT.CHECK);
		button_1.setText("Check Button");
		new Label(grpFehlermeldungen, SWT.NONE);
		
		TableViewer tableViewerCMM = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		tableCobasMiraMapping = tableViewerCMM.getTable();
		tableCobasMiraMapping.setLinesVisible(true);
		tableCobasMiraMapping.setHeaderVisible(true);
		tableCobasMiraMapping.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 4));
		
		TableViewerColumn tableViewerColumnTestkrzel =
			new TableViewerColumn(tableViewerCMM, SWT.NONE);
		TableColumn tblclmnCobasMiraTestkrzel = tableViewerColumnTestkrzel.getColumn();
		tblclmnCobasMiraTestkrzel.setWidth(100);
		tblclmnCobasMiraTestkrzel.setText("CM Testname");
		
		TableViewerColumn tableViewerColumnRefLaboritem =
			new TableViewerColumn(tableViewerCMM, SWT.NONE);
		TableColumn tblclmnReferenziertesLaboritem = tableViewerColumnRefLaboritem.getColumn();
		tblclmnReferenziertesLaboritem.setWidth(111);
		tblclmnReferenziertesLaboritem.setText("Ref. Laborwert");
		
		TableViewerColumn tableViewerColumnNoKommastellen =
			new TableViewerColumn(tableViewerCMM, SWT.NONE);
		TableColumn tblclmnNoKommastellen = tableViewerColumnNoKommastellen.getColumn();
		tblclmnNoKommastellen.setToolTipText(Messages
			.getString("DeviceView.tblclmnNoKommastellen.toolTipText")); //$NON-NLS-1$
		tblclmnNoKommastellen.setWidth(70);
		tblclmnNoKommastellen.setText(Messages.getString("DeviceView.tblclmnNoKommastellen.text")); //$NON-NLS-1$
		
		TableViewerColumn tableViewerColumnReferenzM =
			new TableViewerColumn(tableViewerCMM, SWT.NONE);
		TableColumn tblclmnReferenzM = tableViewerColumnReferenzM.getColumn();
		tblclmnReferenzM.setWidth(100);
		tblclmnReferenzM.setText("Referenz M");
		
		TableViewerColumn tableViewerColumnReferenzW =
			new TableViewerColumn(tableViewerCMM, SWT.NONE);
		TableColumn tblclmnReferenzW = tableViewerColumnReferenzW.getColumn();
		tblclmnReferenzW.setWidth(100);
		tblclmnReferenzW.setText("Referenz W");
		tableViewerCMM.setContentProvider(new CobasMiraMappingContentProvider());
		tableViewerCMM.setLabelProvider(new CobasMiraMappingLabelProvider());
		tableViewerCMM.setInput(CobasMiraMapping.getCmmappings());
		
		lblStatus = new Label(parent, SWT.NONE);
		lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite compositeButtonContainer = new Composite(parent, SWT.NONE);
		compositeButtonContainer.setLayout(new GridLayout(2, true));
		compositeButtonContainer
			.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		buttonActivate = new Button(compositeButtonContainer, SWT.NONE);
		buttonActivate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				conn.startReadingSerialInput();
				if (conn.isActivated()) {
					buttonActivate.setEnabled(false);
					buttonDeactivate.setEnabled(true);
				}
			}
		});
		buttonActivate.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		buttonActivate.setText("Aktivieren");
		if (conn.isActivated())
			buttonActivate.setEnabled(false);
		
		buttonDeactivate = new Button(compositeButtonContainer, SWT.NONE);
		buttonDeactivate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				conn.stopReadingSerialInput();
				buttonActivate.setEnabled(true);
				buttonDeactivate.setEnabled(false);
			}
		});
		buttonDeactivate.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		buttonDeactivate.setText("Deaktivieren");
		if (conn.isDeactivated())
			buttonDeactivate.setEnabled(false);
		
		m_bindingContext = initDataBindings();
	}
	
	private Anwender getSelectedAnwender(){
		String id = CoreHub.localCfg.get(Preferences.ERRORMSGRECEIVER, "1");
		return Anwender.load(id);
	}
	
	@Override
	public void setFocus(){
		
	}
	
	public Label getLblStatus(){
		return lblStatus;
	}
	
	protected DataBindingContext initDataBindings(){
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue lblStatusObserveTextObserveWidget = SWTObservables.observeText(lblStatus);
		IObservableValue conngetStatusBytesObserveValue =
			BeanProperties.value("status").observe(conn);
		bindingContext.bindValue(lblStatusObserveTextObserveWidget, conngetStatusBytesObserveValue,
			null, null);
		//
		return bindingContext;
	}
}
