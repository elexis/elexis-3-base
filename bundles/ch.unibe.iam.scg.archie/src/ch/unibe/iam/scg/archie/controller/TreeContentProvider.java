package ch.unibe.iam.scg.archie.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;

import ch.unibe.iam.scg.archie.model.DataSet;

public class TreeContentProvider implements ITreeContentProvider {

	private Map<String, List<Comparable<?>[]>> groupedRows = new LinkedHashMap<>();
	private DataSet cachedDataSet;
	private List<Comparable<?>[]> flatRows = new ArrayList<>();
	private boolean groupBy;

	public TreeContentProvider(boolean groupBy) {
		this.groupBy = groupBy;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (cachedDataSet == null && inputElement instanceof DataSet) {
			cachedDataSet = (DataSet) inputElement;

		}
		if (groupBy) {
			buildGroups();
			return groupedRows.keySet().toArray();
		} else {
			buildRows();
			return flatRows.toArray();
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof String) {
			List<Comparable<?>[]> rows = groupedRows.get(parentElement);
			if (rows != null) {
				return rows.toArray();
			}
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof String) {
			List<Comparable<?>[]> rows = groupedRows.get(element);
			return rows != null && !rows.isEmpty();
		}
		return false;
	}

	private void buildGroups() {
		groupedRows.clear();
		String currentGroup = null;
		List<Comparable<?>[]> rows = cachedDataSet.getContent();

		for (Comparable<?>[] row : rows) {
			if (row[0] != null && !row[0].toString().isEmpty()) {
				currentGroup = row[0].toString();
				groupedRows.put(currentGroup, new ArrayList<>());
			} else if (currentGroup != null) {
				groupedRows.get(currentGroup).add(row);
			}
		}
	}

	private void buildRows() {
		groupedRows.clear();
		flatRows.clear();
		List<Comparable<?>[]> rows = cachedDataSet.getContent();

		for (Comparable<?>[] row : rows) {
			flatRows.add(row);
		}
	}

	public void refreshDataSet(DataSet newDataSet) {
		this.cachedDataSet = newDataSet;
		if (groupBy) {
			buildGroups();
		} else {
			buildRows();
		}

	}

}
