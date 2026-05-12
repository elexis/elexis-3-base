package ch.elexis.global_inbox.preferencepage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IDocumentStore;

public class CategoryEditingSupport extends EditingSupport {

	private final TableViewer viewer;
	private final IDocumentStore omnivoreDocumentStore;

	private String[] _selectOptions;
	private List<String> categories;

	private final String NO_SELECTION = "Keine Kategorie";

	public CategoryEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		omnivoreDocumentStore = OsgiServiceUtil
				.getService(IDocumentStore.class, "(storeid=ch.elexis.data.store.omnivore)").orElse(null); //$NON-NLS-1$
		if (omnivoreDocumentStore != null) {
			categories = omnivoreDocumentStore.getCategories().stream().map(category -> category.getName())
					.sorted(Comparator.naturalOrder()).collect(Collectors.toList());
		}
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		List<String> selectOptions = new ArrayList<>();
		selectOptions.add(NO_SELECTION);
		selectOptions.addAll(categories);
		_selectOptions = selectOptions.toArray(new String[] {});
		return new ComboBoxCellEditor(viewer.getTable(), _selectOptions);
	}

	@Override
	protected boolean canEdit(Object element) {
		return omnivoreDocumentStore != null;
	}

	@Override
	protected Object getValue(Object element) {
		String categoryName = ((TitleEntry) element).getCategoryName();
		if (categoryName == null) {
			return 0;
		}
		return Arrays.binarySearch(_selectOptions, ((TitleEntry) element).getCategoryName());
	}

	@Override
	protected void setValue(Object element, Object value) {
		int index = ((Integer) value).intValue();
		String _value = _selectOptions[index];
		if (_value == NO_SELECTION) {
			((TitleEntry) element).setCategoryName(null);
		} else {
			((TitleEntry) element).setCategoryName(_value.toString());
		}
		viewer.refresh(element);
	}

}
