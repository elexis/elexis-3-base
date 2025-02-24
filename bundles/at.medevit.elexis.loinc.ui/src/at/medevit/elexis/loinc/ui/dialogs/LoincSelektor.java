package at.medevit.elexis.loinc.ui.dialogs;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.internal.WorkbenchMessages;

import at.medevit.elexis.loinc.model.LoincCode;
import at.medevit.elexis.loinc.ui.LoincServiceComponent;

public class LoincSelektor extends FilteredItemsSelectionDialog {

	private boolean ignoreErrors;

	public LoincSelektor(Shell shell) {
		super(shell);
		setTitle("LOINC Code Selektion");

		setListLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element == null) {
					return StringUtils.EMPTY;
				}
				return ((LoincCode) element).getLabel();
			}
		});
	}

	public LoincSelektor(Shell shell, Object data) {
		this(shell);
		if (data instanceof String && data.equals("ignoreErrors")) {
			ignoreErrors = true;
		}

	}

	@Override
	protected void updateButtonsEnableState(IStatus status) {
		if (!ignoreErrors) {
			super.updateButtonsEnableState(status);
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		String oldListLabel = WorkbenchMessages.FilteredItemsSelectionDialog_listLabel;

		setMessage(StringUtils.EMPTY);
		WorkbenchMessages.FilteredItemsSelectionDialog_listLabel = StringUtils.EMPTY;
		Control ret = super.createDialogArea(parent);

		WorkbenchMessages.FilteredItemsSelectionDialog_listLabel = oldListLabel;
		return ret;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		return new DialogSettings("loincselector"); //$NON-NLS-1$
	}

	@Override
	protected IStatus validateItem(Object item) {
		return Status.OK_STATUS;
	}

	@Override
	protected void okPressed() {
		if (ignoreErrors) {
			updateStatus(Status.OK_STATUS);
		}
		super.okPressed();
	}

	@Override
	protected ItemsFilter createFilter() {
		return new ItemsFilter() {
			@Override
			public boolean isConsistentItem(Object item) {
				return true;
			}

			@Override
			public boolean matchItem(Object item) {
				LoincCode code = (LoincCode) item;

				return matches(code.getLabel());
			}
		};
	}

	@Override
	protected Comparator<LoincCode> getItemsComparator() {
		return new Comparator<LoincCode>() {

			public int compare(LoincCode o1, LoincCode o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		};
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter,
			IProgressMonitor progressMonitor) throws CoreException {

		List<LoincCode> allCodes = LoincServiceComponent.getService().getAllCodes();

		for (LoincCode code : allCodes) {
			if (progressMonitor.isCanceled()) {
				return;
			}
			contentProvider.add(code, itemsFilter);
		}
	}

	@Override
	public String getElementName(Object item) {
		LoincCode code = (LoincCode) item;
		return code.getLabel();
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		// TODO Auto-generated method stub
		return null;
	}
}
