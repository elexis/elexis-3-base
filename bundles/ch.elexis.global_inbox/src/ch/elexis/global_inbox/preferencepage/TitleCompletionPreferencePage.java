package ch.elexis.global_inbox.preferencepage;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.l10n.Messages;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.global_inbox.Preferences;

public class TitleCompletionPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	public TitleCompletionPreferencePage() {
	}

	private Table table;
	private TableViewer tableViewer;

	private java.util.List<TitleEntry> storedCompletions;

	static final String STORE_SEPARATOR = "|"; //$NON-NLS-1$

	@Override
	public void init(IWorkbench workbench) {
		storedCompletions = ConfigServiceHolder.get()
				.getAsList(Preferences.PREF_TITLE_COMPLETION, new ArrayList<String>()).stream()
				.map(val -> new TitleEntry(val)).collect(Collectors.toList());
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		TableColumnLayout tcl_composite = new TableColumnLayout();
		composite.setLayout(tcl_composite);

		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableViewerColumn tvcTitle = new TableViewerColumn(tableViewer, SWT.NONE);
		tvcTitle.setEditingSupport(new TitleEditingSupport(tableViewer));
		tvcTitle.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((TitleEntry) element).getTitle();
			}
		});
		TableColumn tbclmTitle = tvcTitle.getColumn();
		tcl_composite.setColumnData(tbclmTitle, new ColumnWeightData(80, 100, true));
		tbclmTitle.setText("Titel");

		TableViewerColumn tvcCategory = new TableViewerColumn(tableViewer, SWT.NONE);
		tvcCategory.setEditingSupport(new CategoryEditingSupport(tableViewer));
		tvcCategory.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((TitleEntry) element).getCategoryName();
			}
		});
		TableColumn tblclmCategory = tvcCategory.getColumn();
		tcl_composite.setColumnData(tblclmCategory, new ColumnPixelData(100, true, true));
		tblclmCategory.setText("Kategorie");

		Menu popupMenu = new Menu(table);
		table.setMenu(popupMenu);
		popupMenu.addMenuListener(new MenuAdapter() {

			@Override
			public void menuShown(MenuEvent e) {
				popupMenu.getItem(1).setEnabled(tableViewer.getStructuredSelection().getFirstElement() != null);
			}
		});

		MenuItem mntmAdd = new MenuItem(popupMenu, SWT.NONE);
		mntmAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TitleEntry titleEntry = new TitleEntry();
				storedCompletions.add(titleEntry);
				tableViewer.add(titleEntry);
			}
		});
		mntmAdd.setText(Messages.Bezugskontakt_Add);
		mntmAdd.setImage(Images.IMG_ADD.getImage());

		MenuItem mntmDelete = new MenuItem(popupMenu, SWT.NONE);
		mntmDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TitleEntry selection = (TitleEntry) tableViewer.getStructuredSelection().getFirstElement();
				if (selection != null) {
					storedCompletions.remove(selection);
				}
				tableViewer.remove(selection);
			}
		});
		mntmDelete.setText(Messages.Core_Delete);
		mntmDelete.setImage(Images.IMG_DELETE.getImage());

		tableViewer.setInput(storedCompletions);

		return composite;
	}

	@Override
	protected void performApply() {
		java.util.List<String> completions = storedCompletions.stream()
				.map(le -> le.getTitle() + STORE_SEPARATOR + le.getCategoryName()).collect(Collectors.toList());
		ConfigServiceHolder.setGlobalAsList(Preferences.PREF_TITLE_COMPLETION, completions);
		super.performApply();
	}

}
