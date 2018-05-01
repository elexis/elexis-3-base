package at.medevit.elexis.cobasmira.ui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import at.medevit.elexis.cobasmira.connection.CobasMiraConnection;
import at.medevit.elexis.cobasmira.model.CobasMiraLog;
import at.medevit.elexis.cobasmira.model.CobasMiraMapping;
import at.medevit.elexis.cobasmira.model.CobasMiraMessage;
import at.medevit.elexis.cobasmira.Messages;
import ch.elexis.data.Anwender;

public class DeviceView extends ViewPart {
	private CobasMiraLog log = CobasMiraLog.getInstance();
	
	private DataBindingContext m_bindingContext;
	private CobasMiraMessage message;
	
	private Table tableCobasMiraLog;
	private Table tableCobasMiraMapping;
	private TableViewer tableViewer;
	private TableColumn tableZeit;
	private CobasMiraConnection conn = CobasMiraConnection.getInstance();
	private Button btnActive;
	
	public DeviceView(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout(1, true));
		
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
		tableZeit.setText(Messages.UI_dateTime);
		tableColumnLayout.setColumnData(tableZeit, new ColumnPixelData(120));
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnTyp = tableViewerColumn_1.getColumn();
		tblclmnTyp.setText(Messages.UI_type);
		tableColumnLayout.setColumnData(tblclmnTyp, new ColumnPixelData(100));
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnLogeintrag = tableViewerColumn_2.getColumn();
		tblclmnLogeintrag.setText(Messages.UI_description);
		tableColumnLayout.setColumnData(tblclmnLogeintrag, new ColumnWeightData(50, 200));
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnElexisstatus = tableViewerColumn_3.getColumn();
		tblclmnElexisstatus.setText(Messages.UI_elexis_state);
		tableColumnLayout.setColumnData(tblclmnElexisstatus, new ColumnWeightData(10, 100));
		
		tableViewer.setComparator(new CobasMiraLogTableSorter());
		tableViewer.setContentProvider(new CobasMiraLogContentProvider(tableViewer));
		tableViewer.setLabelProvider(new CobasMiraLogLabelProvider());
		tableViewer.setInput(log.getMessageList());
		/// --- End of Log Table
		
		Label lblMappingEintrge = new Label(parent, SWT.NONE);
		lblMappingEintrge.setText("Mapping Einträge");
		
		// NEEDED in 2.1
		//TODO: Remove afterwards
		ch.elexis.data.Query<Anwender> qbe = new ch.elexis.data.Query<Anwender>(Anwender.class);
		//comboViewer.setInput(Anwender.getAll().toArray());
		//
		
		TableViewer tableViewerCMM = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		tableCobasMiraMapping = tableViewerCMM.getTable();
		tableCobasMiraMapping.setLinesVisible(true);
		tableCobasMiraMapping.setHeaderVisible(true);
		GridData gd_tableCobasMiraMapping = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 4);
		gd_tableCobasMiraMapping.heightHint = 100;
		tableCobasMiraMapping.setLayoutData(gd_tableCobasMiraMapping);
		
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
		tblclmnNoKommastellen
			.setToolTipText(Messages.DeviceView_tblclmnNoKommastellen_toolTipText); //$NON-NLS-1$
		tblclmnNoKommastellen.setWidth(70);
		tblclmnNoKommastellen.setText(Messages.DeviceView_tblclmnNoKommastellen_text); //$NON-NLS-1$
		
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
		
		Composite compositeButtonContainer = new Composite(parent, SWT.NONE);
		compositeButtonContainer.setLayout(new GridLayout(1, true));
		compositeButtonContainer
			.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
			
		btnActive = new Button(compositeButtonContainer, SWT.TOGGLE);
		btnActive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				boolean selection = btnActive.getSelection();
				if (selection) {
					conn.startReadingSerialInput();
				} else {
					conn.stopReadingSerialInput();
				}
			}
		});
		btnActive.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnActive.setText("Aktivieren");
		
		btnActive.setSelection(conn.isActivated());
		
		m_bindingContext = initDataBindings();
	}
	
	@Override
	public void setFocus(){
		tableViewer.getTable().setFocus();
	}
	
	protected DataBindingContext initDataBindings(){
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}
}
