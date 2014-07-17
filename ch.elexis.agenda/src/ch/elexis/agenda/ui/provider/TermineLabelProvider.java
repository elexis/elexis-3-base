package ch.elexis.agenda.ui.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.elexis.agenda.data.Termin;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

public class TermineLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	@Override
	public Image getColumnImage(Object element, int columnIndex){
		return null;
	}
	
	@Override
	public String getColumnText(Object element, int columnIndex){
		if (element instanceof Termin) {
			Termin termin = (Termin) element;
			TimeSpan ts = termin.getTimeSpan();
			
			// get day, start and end time
			TimeTool tt = new TimeTool();
			tt.setDate(termin.getDay());
			String day = tt.toString(TimeTool.DATE_GER);
			
			tt.setTime(ts.from);
			String from = tt.toString(TimeTool.TIME_SMALL);
			
			tt.setTime(ts.until);
			String until = tt.toString(TimeTool.TIME_SMALL);
			
			String label =
				day + ", " + from + " - " + until + " (" + termin.getType() + ", "
					+ termin.getStatus() + ")";
			return label;
		}
		return element.toString();
	}
}
