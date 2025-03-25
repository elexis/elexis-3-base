package ch.unibe.iam.scg.archie.controller;

import java.util.List;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import ch.unibe.iam.scg.archie.model.AbstractDataProvider;
import ch.unibe.iam.scg.archie.model.DataSet;
import ch.unibe.iam.scg.archie.model.TreeViewerComparator;

public class TreeFactory {

	private static TreeFactory INSTANCE = null;

	private TreeFactory() {
	}

	public static final TreeFactory getInstance() {
		if (TreeFactory.INSTANCE == null) {
			TreeFactory.INSTANCE = new TreeFactory();
		}
		return TreeFactory.INSTANCE;
	}

	/**
	 * Creates a TreeViewer from the given DataSet. The method supports two
	 * modes: grouped view (hierarchical) or flat view.
	 *
	 * @param parent
	 *            Composite that holds the TreeViewer.
	 * @param dataset
	 *            DataSet providing the headings and content.
	 * @param provider
	 *            Data provider (provides label/content providers).
	 * @param groupBy
	 *            Whether to group data hierarchically or show flat.
	 * @return Configured TreeViewer instance.
	 */
	public TreeViewer createTreeFromData(final Composite parent, final DataSet dataset,
			final AbstractDataProvider provider, boolean groupBy) {

		// Base Tree widget
		Tree tree = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		// Layout manager for resizing
		TreeColumnLayout treeLayout = new TreeColumnLayout();
		parent.setLayout(treeLayout);

		// Create columns dynamically
		createTreeColumns(tree, treeLayout, dataset.getHeadings(), groupBy);

		// TreeViewer setup
		TreeViewer treeViewer = new TreeViewer(tree);

		// Set ContentProvider
		if (provider.isTree() && provider.getTreeContentProvider() != null) {
			treeViewer.setContentProvider(provider.getTreeContentProvider());
		} else {
			TreeContentProvider defaultProvider = new TreeContentProvider(groupBy);
			defaultProvider.refreshDataSet(dataset);
			treeViewer.setContentProvider(defaultProvider);
		}

		// LabelProvider
		ILabelProvider labelProvider = provider.getLabelProvider();
		treeViewer.setLabelProvider(labelProvider != null ? labelProvider : new TreeLabelProvider(groupBy));

		// Comparator for sorting
		TreeViewerComparator comparator = new TreeViewerComparator();
		treeViewer.setComparator(comparator);
		treeViewer.setInput(dataset);

		// Column sorters
		TreeColumn[] cols = treeViewer.getTree().getColumns();
		int startIndex = groupBy ? 1 : 0;

		for (int i = startIndex; i < cols.length; i++) {
			TreeColumn tc = cols[i];
			final int index = i - startIndex;
			tc.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					TreePath[] expandedPaths = treeViewer.getExpandedTreePaths();

					comparator.setColumn(index);
					treeViewer.getTree().setSortDirection(comparator.getDirection());
					treeViewer.getTree().setSortColumn(tc);
					treeViewer.refresh();

					treeViewer.setExpandedTreePaths(expandedPaths);
				}
			});
		}

		return treeViewer;
	}

	/**
	 * Creates TreeColumns based on dataset headings + optional arrow column.
	 */
	private void createTreeColumns(Tree tree, TreeColumnLayout treeLayout, List<String> headings, boolean groupBy) {

		// Optional arrow column
		if (groupBy) {
			TreeColumn arrowColumn = new TreeColumn(tree, SWT.NONE);
			arrowColumn.setText("");
			arrowColumn.setWidth(30);
			arrowColumn.setResizable(false);
			treeLayout.setColumnData(arrowColumn, new ColumnPixelData(30, false));
		}

		// Data columns
		for (int i = 0; i < headings.size(); i++) {
			TreeColumn column = new TreeColumn(tree, SWT.NONE);
			column.setText(headings.get(i));
			column.setMoveable(true);

			if (i == 0) {
				treeLayout.setColumnData(column, new ColumnWeightData(0, 300, true));
			} else if (i == 1) {
				treeLayout.setColumnData(column, new ColumnWeightData(0, 100, true));
			} else {
				treeLayout.setColumnData(column, new ColumnWeightData(1));
			}
		}
	}
}
