package at.medevit.elexis.loinc.ui.providers;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

import at.medevit.elexis.loinc.model.LoincCode;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldListener;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.IFilter;

public class LoincCodeControlFieldProvider implements ControlFieldProvider {
	
	private CommonViewer commonViewer;
	private StructuredViewer viewer;
	
	private Text txtFilter;
	
	private LoincCodeTextFilter filterCodeText;
	
	public LoincCodeControlFieldProvider(final CommonViewer viewer){
		commonViewer = viewer;
	}
	
	@Override
	public Composite createControl(Composite parent){
		final Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new FormLayout());
		
		Label lblFilter = new Label(ret, SWT.NONE);
		lblFilter.setText("Filter: ");
		
		txtFilter = new Text(ret, SWT.BORDER | SWT.SEARCH);
		txtFilter.setText(""); //$NON-NLS-1$
		
		ToolBarManager tbManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL | SWT.WRAP);
		tbManager.add(new Action("neu erstellen") {
			{
				setImageDescriptor(Images.IMG_NEW.getImageDescriptor());
				setToolTipText("Neuer Loinc Code erstellen");
			}
			
			@Override
			public void run(){
				EditLoincCodeDialog dialog = new EditLoincCodeDialog(ret.getShell(), null);
				if (dialog.open() == EditLoincCodeDialog.OK) {
					
				}
			}
		});
		ToolBar toolbar = tbManager.createControl(ret);
		
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(0, 5);
		lblFilter.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.right = new FormAttachment(100, -5);
		toolbar.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(lblFilter, 5);
		fd.right = new FormAttachment(toolbar, -5);
		txtFilter.setLayoutData(fd);
		
		return ret;
	}
	
	@Override
	public void addChangeListener(ControlFieldListener cl){
		// TODO Auto-generated method stub
	}
	
	@Override
	public void removeChangeListener(ControlFieldListener cl){
		// TODO Auto-generated method stub
	}
	
	@Override
	public String[] getValues(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void clearValues(){
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean isEmpty(){
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void setQuery(Query<? extends PersistentObject> q){
		// TODO Auto-generated method stub
	}
	
	@Override
	public IFilter createFilter(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void fireChangedEvent(){
		// TODO Auto-generated method stub
	}
	
	@Override
	public void fireSortEvent(String text){
		// TODO Auto-generated method stub
	}
	
	@Override
	public void setFocus(){
		// apply filter to viewer on focus as the creation in common viewer is done
		// first filter then viewer -> viewer not ready on createControl.
		if (viewer == null) {
			viewer = commonViewer.getViewerWidget();
			filterCodeText = new LoincCodeTextFilter();
			viewer.addFilter(filterCodeText);
			viewer.setComparator(new LoincCodeComparator());
			txtFilter.addKeyListener(new FilterKeyListener(txtFilter, viewer));
		}
	}
	
	private class LoincCodeComparator extends ViewerComparator {
		@Override
		public int compare(Viewer viewer, Object e1, Object e2){
			LoincCode lCode = (LoincCode) e1;
			LoincCode rCode = (LoincCode) e2;
			
			return lCode.getCode().compareTo(rCode.getCode());
		}
	}
	
	private class FilterKeyListener extends KeyAdapter {
		private Text text;
		private StructuredViewer viewer;
		
		FilterKeyListener(Text filterTxt, StructuredViewer viewer){
			text = filterTxt;
			this.viewer = viewer;
		}
		
		public void keyReleased(KeyEvent ke){
			String txt = text.getText();
			if (txt.length() > 1) {
				filterCodeText.setSearchText(txt);
				viewer.getControl().setRedraw(false);
				viewer.refresh();
				viewer.getControl().setRedraw(true);
			} else {
				filterCodeText.setSearchText(null);
				viewer.getControl().setRedraw(false);
				viewer.refresh();
				viewer.getControl().setRedraw(true);
			}
		}
	}
}
