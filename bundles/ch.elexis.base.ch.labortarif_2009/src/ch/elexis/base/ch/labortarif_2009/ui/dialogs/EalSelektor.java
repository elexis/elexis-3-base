/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     G. Weirich - initial API and implementation
 ******************************************************************************/
package ch.elexis.base.ch.labortarif_2009.ui.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

import ch.elexis.base.ch.labortarif.ILaborLeistung;
import ch.elexis.labortarif2009.data.ModelServiceHolder;

public class EalSelektor extends FilteredItemsSelectionDialog {

	private static boolean initialized = false;
	private static HashMap<ILaborLeistung, String> labelCache;
	private static List<ILaborLeistung> allCodes;

	public EalSelektor(Shell shell) {
		super(shell);
		setTitle("EAL Code Selektion");

		setListLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element == null) {
					return StringUtils.EMPTY;
				}
				return labelCache.get((ILaborLeistung) element);
			}
		});
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
	protected ItemsFilter createFilter() {
		return new ItemsFilter() {
			@Override
			public boolean isConsistentItem(Object item) {
				return true;
			}

			@Override
			public boolean matchItem(Object item) {
				ILaborLeistung code = (ILaborLeistung) item;

				return matches(labelCache.get(code));
			}
		};
	}

	@Override
	protected Comparator<ILaborLeistung> getItemsComparator() {
		return new Comparator<ILaborLeistung>() {

			public int compare(ILaborLeistung o1, ILaborLeistung o2) {
				return labelCache.get(o1).compareTo(labelCache.get(o2));
			}
		};
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter,
			IProgressMonitor progressMonitor) throws CoreException {

		if (!initialized) {
			labelCache = new HashMap<ILaborLeistung, String>();
			allCodes = new ArrayList<ILaborLeistung>();
			allCodes.addAll(ModelServiceHolder.get().getQuery(ILaborLeistung.class).execute());
			progressMonitor.beginTask(StringUtils.EMPTY, allCodes.size());
			for (ILaborLeistung labor2009Tarif : allCodes) {
				if (progressMonitor.isCanceled()) {
					return;
				}
				labelCache.put(labor2009Tarif, labor2009Tarif.getLabel());
				progressMonitor.worked(1);
			}
			initialized = true;
		}
		for (ILaborLeistung code : allCodes) {
			if (progressMonitor.isCanceled()) {
				return;
			}
			contentProvider.add(code, itemsFilter);
		}
	}

	@Override
	public String getElementName(Object item) {
		ILaborLeistung code = (ILaborLeistung) item;
		return labelCache.get(code);
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		// TODO Auto-generated method stub
		return null;
	}
}
