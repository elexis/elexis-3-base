package ch.elexis.global_inbox.ui.parts;

import java.io.File;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.elexis.global_inbox.model.GlobalInboxEntry;
import ch.elexis.global_inbox.ui.GlobalInboxUtil;

public class GlobalInboxLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	@Override
	public Image getColumnImage(Object element, int columnIndex){
		return null;
	}
	
	@Override
	public String getColumnText(Object element, int columnIndex){
		if (element instanceof GlobalInboxEntry) {
			GlobalInboxEntry gie = (GlobalInboxEntry) element;
			File file = gie.getMainFile();
			if (columnIndex == 0) {
				return GlobalInboxUtil.getCategory(file);
			} else {
				return file.getName();
			}
		}
		return "?"; //$NON-NLS-1$
	}
	
}
