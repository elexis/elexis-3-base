package at.medevit.elexis.cobasmira.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import at.medevit.elexis.cobasmira.model.CobasMiraMessage;
import ch.rgw.tools.TimeTool;

public class CobasMiraLogTableSorter extends ViewerComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof CobasMiraMessage && e2 instanceof CobasMiraMessage) {
			TimeTool d1 = ((CobasMiraMessage) e1).getEntryDate();
			TimeTool d2 = ((CobasMiraMessage) e2).getEntryDate();
			if (d1.after(d2))
				return -1;
			if (d2.after(d1))
				return 1;
		}
		return 0;
	}
}
