package ch.unibe.iam.scg.archie.model;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

public class TreeViewerComparator extends ViewerComparator {

	private static final int DESCENDING = 1;
	private int columnIndex = 0;
	private int direction = DESCENDING;

	public void setColumn(int column) {
		if (column == this.columnIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.columnIndex = column;
			direction = DESCENDING;
		}
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	@Override
	public int category(Object element) {

		if (element instanceof String) {
			return 0;
		}
		return 1;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (category(e1) == 1 && category(e2) == 1 && e1 instanceof Comparable[] && e2 instanceof Comparable[]) {
			Comparable<?>[] row1 = (Comparable<?>[]) e1;
			Comparable<?>[] row2 = (Comparable<?>[]) e2;
			String val1 = (columnIndex >= 0 && columnIndex < row1.length && row1[columnIndex] != null)
					? row1[columnIndex].toString()
					: StringUtils.EMPTY;
			String val2 = (columnIndex >= 0 && columnIndex < row2.length && row2[columnIndex] != null)
					? row2[columnIndex].toString()
					: StringUtils.EMPTY;
			int result = val1.compareToIgnoreCase(val2);
			return (direction == DESCENDING) ? -result : result;
		}
		return category(e1) - category(e2);
	}
}
