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
package at.medevit.elexis.ehc.ui.inbox;

import java.io.IOException;
import java.time.LocalDate;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.ui.model.EhcDocument;
import at.medevit.elexis.ehc.ui.views.EHealthConnectorView;
import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import ch.rgw.tools.TimeTool;

public class InboxElementUiProvider implements IInboxElementUiProvider {
	private static Logger logger = LoggerFactory.getLogger(InboxElementUiProvider.class);

	private EhcDocumentLabelProvider labelProvider;
	// private EhcDocumentViewerFilter filter;

	public InboxElementUiProvider() {
		labelProvider = new EhcDocumentLabelProvider();
		// filter = new EhcDocumentViewerFilter();
	}

	@Override
	public ImageDescriptor getFilterImage() {
		// return ResourceManager
		// .getPluginImageDescriptor("at.medevit.elexis.ehc.ui", "icons/ehc.jpg");
		return null;
	}

	@Override
	public ViewerFilter getFilter() {
		// return filter;
		return null;
	}

	@Override
	public LabelProvider getLabelProvider() {
		return labelProvider;
	}

	@Override
	public IColorProvider getColorProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDate getObjectDate(IInboxElement element) {
		EhcDocument document = (EhcDocument) ((IInboxElement) element).getObject();
		return new TimeTool(document.get(EhcDocument.FLD_TIMESTAMP)).toLocalDate();
	}

	@Override
	public boolean isProviderFor(IInboxElement element) {
		Object obj = element.getObject();
		if (obj instanceof EhcDocument) {
			return true;
		}
		return false;
	}

	@Override
	public void doubleClicked(IInboxElement element) {
		// open the ehc view and display the ehc document
		Object obj = element.getObject();
		if (obj instanceof EhcDocument) {
			EhcDocument document = (EhcDocument) obj;
			try {
				EHealthConnectorView view = (EHealthConnectorView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().showView(EHealthConnectorView.ID);
				view.displayReport(document.getLocation().openStream(), null);
			} catch (PartInitException | IOException e) {
				logger.error("Could not open ehc document", e); //$NON-NLS-1$
			}
		}
	}
}
