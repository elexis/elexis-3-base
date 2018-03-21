package ch.elexis.agenda.ui.provider;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import ch.elexis.core.ui.util.viewers.ViewerConfigurer.WidgetProvider;

public class TerminListWidgetProvider implements WidgetProvider {
	
	@Override
	public StructuredViewer createViewer(Composite parent){
		Table table = new Table(parent, SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE);
		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setWidth(1000);
		tc.setData(0);
		
		table.setHeaderVisible(false);
		table.setLinesVisible(false);
		TableViewer tableViewer = new TableViewer(table);
		tableViewer.setSorter(new TerminListSorter());
		return tableViewer;
	}
	
}
