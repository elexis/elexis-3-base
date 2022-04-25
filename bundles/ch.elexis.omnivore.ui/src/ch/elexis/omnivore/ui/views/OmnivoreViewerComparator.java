package ch.elexis.omnivore.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import ch.elexis.omnivore.model.IDocumentHandle;

/**
 * OmnivorViewerComparator. In Non-Flat view categories are handled with
 * priority - meaning category sorting will be kept and only elements inside the
 * same category are sorted ascending/descending
 *
 * @author lucia
 *
 */
public class OmnivoreViewerComparator extends ViewerComparator {
	private static final int DESCENDING = 1;

	private int propertyIndex;
	private int direction = DESCENDING;
	private int catDirection;
	private boolean isFlat;

	public OmnivoreViewerComparator() {
		this.propertyIndex = 0;
		direction = DESCENDING;
		catDirection = -1;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}

		if (column == 1) {
			catDirection = direction;
		}
	}

	public void setFlat(boolean isFlat) {
		this.isFlat = isFlat;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		boolean compareCategories = false;
		IDocumentHandle dh1 = (IDocumentHandle) e1;
		IDocumentHandle dh2 = (IDocumentHandle) e2;
		String cat1 = dh1.getCategory().getName().toLowerCase();
		String cat2 = dh2.getCategory().getName().toLowerCase();

		int rc = 0;

		switch (propertyIndex) {
		case 1:
			rc = cat1.compareTo(cat2);
			break;
		case 2:
			if (cat1.equals(cat2) || isFlat) {
				rc = dh1.getLastchanged().compareTo(dh2.getLastchanged());
			} else {
				compareCategories = true;
			}
			break;
		case 3:
			if (cat1.equals(cat2) || isFlat) {
				rc = dh1.getCreated().compareTo(dh2.getCreated());
			} else {
				compareCategories = true;
			}
			break;
		case 4:
			if (cat1.equals(cat2) || isFlat) {
				String t1 = dh1.getTitle().toLowerCase();
				String t2 = dh2.getTitle().toLowerCase();
				rc = t1.compareTo(t2);
			} else {
				compareCategories = true;
			}
			break;
		case 5:
			if (cat1.equals(cat2) || isFlat) {
				String k1 = dh1.getKeywords().toLowerCase();
				String k2 = dh2.getKeywords().toLowerCase();
				rc = k1.compareTo(k2);
			} else {
				compareCategories = true;
			}
			break;
		default:
			rc = 0;
		}

		// If not in category column and values were not from same category
		if (compareCategories) {
			rc = cat1.compareTo(cat2);
			if (catDirection == DESCENDING) {
				rc = -rc;
			}
			return rc;
		}

		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public int getDirectionDigit() {
		return direction;
	}

	public int getCategoryDirection() {
		return catDirection;
	}

	public void setCategoryDirection(int catDirection) {
		this.catDirection = catDirection;
	}

	public int getPropertyIndex() {
		return propertyIndex;
	}
}
