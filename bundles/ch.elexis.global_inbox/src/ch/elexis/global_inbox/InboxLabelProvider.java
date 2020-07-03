package ch.elexis.global_inbox;

import java.io.File;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class InboxLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	public InboxLabelProvider(){
		
	}
	
	@Override
	public Image getColumnImage(Object element, int columnIndex){
		return null;
	}
	
	@Override
	public String getColumnText(Object element, int columnIndex){
		if (element instanceof File) {
			File file = (File) element;
			if (columnIndex == 0) {
				return StartupComponent.getInstance().getCategory(file);
			} else {
				return file.getName();
			}
		}
		return "?"; //$NON-NLS-1$
	}
	
}
