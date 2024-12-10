package ch.elexis.mednet.webapi.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class UIStyleTableHelper {

	private static Font tableFont = null;

	public static Color getBackgroundColor(Display display) {
		return new Color(display, 248, 248, 248);
	}

	public static Color getTextColor(Display display) {
		return new Color(display, 42, 108, 155);
	}

	public static Color getRow1Color(Display display) {
		return new Color(display, 248, 248, 248);
	}

	public static Color getRow2Color(Display display) {
		return new Color(display, 255, 255, 255);
	}

	public static Color getLineColor(Display display) {
		return new Color(display, 200, 200, 200);
	}

	public static Font getTableFont(Display display) {
		if (tableFont == null || tableFont.isDisposed()) {
			tableFont = new Font(display, new FontData("Arial", 10, SWT.NONE)); //$NON-NLS-1$
		}
		return tableFont;
	}

	public static Font getHeaderFont(Display display) {
		return new Font(display, new FontData("Arial", 12, SWT.BOLD)); //$NON-NLS-1$
	}

	public static GridLayout createStyledGridLayout() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		layout.verticalSpacing = 10;
		return layout;
	}

	public static CCombo createStyledCCombo(Composite parent) {
		Display display = parent.getDisplay();
		CCombo combo = new CCombo(parent, SWT.BORDER | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		combo.setFont(getTableFont(display));
		combo.setForeground(getTextColor(display));
		combo.setBackground(getBackgroundColor(display));
		parent.addDisposeListener(e -> {
			if (!combo.isDisposed()) {
				combo.dispose();
			}
		});

		return combo;
	}

	public static Table createStyledTable(Composite parent) {
		Display display = parent.getDisplay();
		Table table = new Table(parent, SWT.NO_BACKGROUND | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		table.setFont(getTableFont(display));
		table.setLinesVisible(false);

		parent.addDisposeListener(e -> {
			if (!table.isDisposed()) {
				table.dispose();
			}
		});

		return table;
	}

	public static void addTableColumns(Table table, String[] columnHeaders, int[] columnWidths) {
		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnHeaders[i]);
			column.setWidth(columnWidths[i]);
			column.setResizable(true);
		}
	}

	public static void styleTableRows(TableItem item, int rowIndex, Display display) {
		if (rowIndex % 2 == 0) {
			item.setBackground(getRow1Color(display));
		} else {
			item.setBackground(getRow2Color(display));
		}
		item.setForeground(getTextColor(display));
	}

	public static void addTableLines(Table table) {
		Display display = table.getDisplay();
		Color lineColor = getLineColor(display);
		table.addListener(SWT.PaintItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				GC gc = event.gc;
				Rectangle bounds = item.getBounds(event.index);
				gc.setForeground(lineColor);
				gc.drawLine(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y);
				gc.drawLine(bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y + bounds.height);
			}
		});
	}
}
