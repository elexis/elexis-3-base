package at.medevit.elexis.cobasmira.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import at.medevit.elexis.cobasmira.model.CobasMiraMappingLabitem;

public class CobasMiraMappingLabelProvider extends ColumnLabelProvider implements
		ITableLabelProvider {
	
	@Override
	public Image getColumnImage(Object element, int columnIndex){
		return null;
	}
	
	@Override
	public String getColumnText(Object element, int columnIndex){
		if (element instanceof CobasMiraMappingLabitem) {
			CobasMiraMappingLabitem item = (CobasMiraMappingLabitem) element;
			switch (columnIndex) {
			case 0:
				return item.getTestNameCM();
			case 1:
				return item.getLaborwertID();
			case 2:
				return item.getNoDecPlaces();
			case 3:
				return item.getRefM().replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			case 4:
				return item.getRefW().replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			default:
				return "Invalid column";
			}
		} else {
			return "WrongElement";
		}
	}
}
