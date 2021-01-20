package ch.elexis.global_inbox.preferencepage;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class TitleEditingSupport extends EditingSupport {
	
	private final TableViewer viewer;
	
	public TitleEditingSupport(TableViewer viewer){
		super(viewer);
		this.viewer = viewer;
	}
	
	@Override
	protected CellEditor getCellEditor(Object element){
		return new TextCellEditor(viewer.getTable());
	}
	
	@Override
	protected boolean canEdit(Object element){
		return true;
	}
	
	@Override
	protected Object getValue(Object element){
		return ((TitleEntry) element).getTitle();
	}
	
	@Override
	protected void setValue(Object element, Object value){
		((TitleEntry) element).setTitle(value.toString());
		viewer.refresh(element);
	}
	
}
