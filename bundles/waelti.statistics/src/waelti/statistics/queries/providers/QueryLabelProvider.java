package waelti.statistics.queries.providers;

import java.util.Currency;
import java.util.Locale;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.rgw.tools.Money;

/**
 * Standard label provider for the queries. If no special labels or model is required, this label
 * provider will do nicely. It provides just the labels given at the specific row/columns.
 */
public class QueryLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	public Image getColumnImage(Object element, int columnIndex){
		return null;
	}
	
	public String getColumnText(Object element, int columnIndex){
		Object[] row = (Object[]) element;
		if (row[columnIndex].getClass() == Money.class) {
			Currency cur = Currency.getInstance(Locale.getDefault());
			return cur + " " + row[columnIndex].toString();
		} else {
			return row[columnIndex].toString();
		}
	}
	
}
