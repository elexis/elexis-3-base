package de.fhdo.elexis.perspective.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

import ch.elexis.core.ui.icons.Images;
import de.fhdo.elexis.Messages;
import de.fhdo.elexis.perspective.model.FaultViewFix;

public class FixPerspectiveDialog extends TitleAreaDialog {
	private TableViewer tableViewer;
	private IViewRegistry viewRegistry;
	private List<FaultViewFix> faultViews;
	
	public FixPerspectiveDialog(Shell parentShell, List<FaultViewFix> faultViews,
		IViewRegistry viewRegistry){
		super(parentShell);
		this.faultViews = faultViews;
		this.viewRegistry = viewRegistry;
	}
	
	@Override
	public void create(){
		super.create();
		setTitle(Messages.FixPerspectiveDlg_Title);
		setMessage(Messages.FixPerspectiveDlg_Message);
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite area = new Composite(parent, SWT.NONE);
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableColumnLayout tcLayout = new TableColumnLayout();
		area.setLayout(tcLayout);
		
		tableViewer = new TableViewer(area, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableViewerColumn tvc = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tcMissing = tvc.getColumn();
		tcMissing.setText(Messages.FixPerspectiveDlg_MissingView);
		tcLayout.setColumnData(tcMissing, new ColumnPixelData(250, true, true));
		
		tvc = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tcReplacer = tvc.getColumn();
		tcReplacer.setText(Messages.FixPerspectiveDlg_ReplaceWith);
		tcLayout.setColumnData(tcReplacer, new ColumnPixelData(250, true, true));
		
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new FixPerspectiveLabelProvider());
		tableViewer.setInput(faultViews);
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event){
				IStructuredSelection structSelection =
					(IStructuredSelection) tableViewer.getSelection();
				FaultViewFix faultView = (FaultViewFix) structSelection.getFirstElement();
				
				SelectViewDialog svd = new SelectViewDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow(), viewRegistry);
				svd.open();
				IViewDescriptor selViewDesc = svd.getSelection();
				if (selViewDesc != null) {
					faultView.setReplacerId(selViewDesc.getId());
					faultView.setLabel(selViewDesc.getLabel());
				}
				tableViewer.refresh();
			}
		});
		
		return area;
	}
	
	public List<FaultViewFix> getFaultViewFixes(){
		return faultViews;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent){
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}
	
	@Override
	protected boolean canHandleShellCloseEvent(){
		return false;
	}
	
	class FixPerspectiveLabelProvider extends LabelProvider implements ITableLabelProvider {
		
		@Override
		public Image getColumnImage(Object element, int columnIndex){
			if (element instanceof FaultViewFix && columnIndex == 1) {
				FaultViewFix faultView = (FaultViewFix) element;
				if (faultView.getReplacerId().isEmpty()) {
					return Images.IMG_DELETE.getImage();
				}
			}
			return null;
		}
		
		@Override
		public String getColumnText(Object element, int columnIndex){
			if (element instanceof FaultViewFix) {
				FaultViewFix faultView = (FaultViewFix) element;
				switch (columnIndex) {
				case 0:
					return faultView.getMissingId();
				case 1:
					return faultView.getReplacerId();
				default:
					return "";
				}
			}
			return "";
		}
	}
}
