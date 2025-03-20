package ch.unibe.iam.scg.archie.controller;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class LeistungenTreeLabelProvider extends LabelProvider implements ITableLabelProvider {

	private boolean groupBy;

	public LeistungenTreeLabelProvider(boolean groupBy) {
		this.groupBy = groupBy;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof Comparable<?>[]) {
			Comparable<?>[] row = (Comparable<?>[]) element;
			int rowIndex = columnIndex - (groupBy ? 1 : 0);
			if (rowIndex >= 0 && rowIndex < row.length) {
				Object value = row[rowIndex];
				return value != null ? value.toString() : StringUtils.EMPTY;
			}
		} else if (element instanceof String) {
			if (columnIndex == 1) {
				return element.toString();
			}
		}
		return StringUtils.EMPTY;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}
}
