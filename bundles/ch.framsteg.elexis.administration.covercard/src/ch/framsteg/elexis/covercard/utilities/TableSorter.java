package ch.framsteg.elexis.covercard.utilities;

import java.text.Collator;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableSorter {

	public static boolean sort(Table table, int columnNumber, boolean asc) {
		TableItem[] items = table.getItems();
		Collator collator = Collator.getInstance(Locale.getDefault());
		// Sets the column1 as default sorted
		TableColumn column = table.getColumn(columnNumber);
		int index = columnNumber;
		if (asc) {
			asc = false;
			for (int i = 1; i < items.length; i++) {
				String value1 = items[i].getText(index);
				for (int j = 0; j < i; j++) {
					String value2 = items[j].getText(index);
					if (collator.compare(value1, value2) < 0) {
						String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
								items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
								items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
								items[i].getText(11), items[i].getText(12), items[i].getText(13), items[i].getText(14),
								items[i].getText(15), items[i].getText(16), items[i].getText(16) };
						items[i].dispose();
						TableItem item = new TableItem(table, SWT.NONE, j);
						item.setText(values);
						item.setBackground(8, new Color(248, 248, 248));
						item.setBackground(10, new Color(248, 248, 248));
						item.setBackground(11, new Color(248, 248, 248));
						item.setBackground(12, new Color(248, 248, 248));
						item.setBackground(16, new Color(248, 248, 248));
						items = table.getItems();
						break;
					}
				}
			}
		} else {
			asc = true;
			for (int i = 1; i < items.length; i++) {
				String value1 = items[i].getText(index);
				for (int j = 0; j < i; j++) {

					String value2 = items[j].getText(index);
					if (collator.compare(value1, value2) > 0) {
						String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
								items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
								items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
								items[i].getText(11), items[i].getText(12), items[i].getText(13), items[i].getText(14),
								items[i].getText(15), items[i].getText(16), items[i].getText(16) };
						items[i].dispose();
						TableItem item = new TableItem(table, SWT.NONE, j);
						item.setText(values);
						item.setBackground(8, new Color(248, 248, 248));
						item.setBackground(10, new Color(248, 248, 248));
						item.setBackground(11, new Color(248, 248, 248));
						item.setBackground(12, new Color(248, 248, 248));
						item.setBackground(16, new Color(248, 248, 248));
						items = table.getItems();
						break;
					}
				}
			}
		}
		table.setSortColumn(column);
		table.deselectAll();
		table.setTopIndex(0);
		table.setLinesVisible(true);
		return asc;

	}

}
