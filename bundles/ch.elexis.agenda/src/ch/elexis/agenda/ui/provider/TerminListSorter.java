package ch.elexis.agenda.ui.provider;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;

import ch.elexis.agenda.data.Termin;

public class TerminListSorter extends ViewerSorter {
	private int direction = SWT.DOWN;
	
	/**
	 * set SWT.DOWN or SWT.UP as direction for the sorting
	 * 
	 * @param direction
	 */
	public void setDirection(int direction){
		this.direction = direction;
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2){
		int rc = 0;
		Termin t1 = (Termin) e1;
		Termin t2 = (Termin) e2;
		
		rc = t1.getStartTime().compareTo(t2.getStartTime());
		// If descending order, flip the direction
		if (direction == SWT.DOWN) {
			rc = -rc;
		}
		return rc;
	}
}
