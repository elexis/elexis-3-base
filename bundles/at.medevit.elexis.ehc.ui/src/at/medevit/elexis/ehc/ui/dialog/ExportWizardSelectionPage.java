/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import at.medevit.elexis.ehc.ui.extension.IWizardDescriptor;

public class ExportWizardSelectionPage extends WizardPage implements IWizardPage {

	/**
	 * List of wizard nodes that have cropped up in the past (element type:
	 * <code>IWizardNode</code> ).
	 */
	private static List<IWizardDescriptor> selectedWizardDescriptors = new ArrayList<IWizardDescriptor>();

	private IWizardDescriptor selectedWizardDescriptor;

	protected ExportWizardSelectionPage() {
		super("Export");
		setTitle("Exporter Selection");
		setDescription("Select an exporter for your data.");
		// Cannot finish from this page
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		ExportWizardsComposite composite = new ExportWizardsComposite(parent, SWT.NONE);

		composite.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
					Object o = ((IStructuredSelection) selection).getFirstElement();
					if (o instanceof IWizardDescriptor) {
						// Now we set our selected node, which toggles
						// the next button
						setSelectedDescriptor((IWizardDescriptor) o);
						setTitle(selectedWizardDescriptor.getLabel());
					}
				}
			}
		});
		setControl(composite);

	}

	/**
	 * Sets or clears the currently selected wizard node within this page.
	 *
	 * @param node the wizard node, or <code>null</code> to clear
	 */
	protected void setSelectedDescriptor(IWizardDescriptor descriptor) {
		selectedWizardDescriptor = descriptor;
		if (isCurrentPage()) {
			getContainer().updateButtons();
		}
	}

	/**
	 * Adds the given wizard node to the list of selected nodes if it is not already
	 * in the list.
	 *
	 * @param node the wizard node, or <code>null</code>
	 */
	private void addSelectedDescriptor(IWizardDescriptor descriptor) {
		if (descriptor == null) {
			return;
		}

		if (selectedWizardDescriptors.contains(descriptor)) {
			return;
		}

		selectedWizardDescriptors.add(descriptor);
	}

	/**
	 * The <code>WizardSelectionPage</code> implementation of this
	 * <code>IWizardPage</code> method returns <code>true</code> if there is a
	 * selected node.
	 */
	public boolean canFlipToNextPage() {
		return selectedWizardDescriptor != null;
	}

	/**
	 * The <code>WizardSelectionPage</code> implementation of this
	 * <code>IWizardPage</code> method returns the first page of the currently
	 * selected wizard if there is one.
	 */
	public IWizardPage getNextPage() {
		if (selectedWizardDescriptor == null) {
			return null;
		}

		IWizard wizard = null;
		try {
			wizard = selectedWizardDescriptor.createWizard();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		if (wizard == null) {
			setSelectedDescriptor(null);
			return null;
		}

		if (!selectedWizardDescriptors.contains(selectedWizardDescriptor)) {
			wizard.addPages();
			addSelectedDescriptor(selectedWizardDescriptor);
		}

		return wizard.getStartingPage();
	}
}
