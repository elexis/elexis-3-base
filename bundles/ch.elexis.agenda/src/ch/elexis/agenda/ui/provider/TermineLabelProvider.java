package ch.elexis.agenda.ui.provider;

import java.time.format.TextStyle;
import java.util.Locale;

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
			StringBuilder sbLabel = new StringBuilder();
			
			TimeSpan ts = termin.getTimeSpan();
			
			// day
			TimeTool tt = new TimeTool();
			tt.setDate(termin.getDay());
			sbLabel.append(tt.toString(TimeTool.DATE_GER));
			String dayShort = tt.toLocalDate().getDayOfWeek().getDisplayName(TextStyle.SHORT,
				Locale.getDefault());
			if (dayShort != null) {
				sbLabel.append(" (" + dayShort + ")");
			}
			sbLabel.append(", ");
			
			// start time
			tt.setTime(ts.from);
			sbLabel.append(tt.toString(TimeTool.TIME_SMALL));
			sbLabel.append(" - ");
			
			// end time
			tt.setTime(ts.until);
			sbLabel.append(tt.toString(TimeTool.TIME_SMALL));
			
			// type
			sbLabel.append(" (");
			sbLabel.append(termin.getType());
			sbLabel.append(", ");
			// status
			sbLabel.append(termin.getStatus());
			sbLabel.append("), ");
			
			// bereich
			sbLabel.append(termin.getBereich());
			
			// grund if set
			if (termin.getGrund() != null && !termin.getGrund().isEmpty()) {
				sbLabel.append(" (");
				sbLabel.append(termin.getGrund());
				sbLabel.append(")");
			}
			
			return sbLabel.toString();
		}
		return element.toString();
	}
}
