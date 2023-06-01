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
package at.medevit.elexis.inbox.core.ui;

import java.time.LocalDate;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.core.ui.filter.LabInboxFilter;
import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.ui.part.model.GroupedInboxElements;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.LabServiceHolder;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.data.LabResult;
import ch.rgw.tools.Result;

public class LabResultUiProvider implements IInboxElementUiProvider {

	private IViewDescriptor rocheView;
	private IViewDescriptor labView;

	private LabResultLabelProvider labelProvider;
	private LabInboxFilter filter;

	public LabResultUiProvider() {
		labelProvider = new LabResultLabelProvider();
	}

	@Override
	public ImageDescriptor getFilterImage() {
		return Images.IMG_VIEW_LABORATORY.getImageDescriptor();
	}

	@Override
	public ViewerFilter getFilter() {
		if (filter == null) {
			filter = new LabInboxFilter();
		}
		return filter;
	}

	@Override
	public LabelProvider getLabelProvider() {
		return labelProvider;
	}

	@Override
	public IColorProvider getColorProvider() {
		return labelProvider;
	}

	@Override
	public boolean isProviderFor(IInboxElement element) {
		if (element instanceof LabGroupedInboxElements) {
			return true;
		}
		Object obj = element.getObject();
		if (obj instanceof LabResult) {
			return true;
		} else if (obj instanceof ILabResult) {
			return true;
		}
		return false;
	}

	@Override
	public LocalDate getObjectDate(IInboxElement element) {
		if (element instanceof GroupedInboxElements) {
			IInboxElement firstElement = ((GroupedInboxElements) element).getFirstElement();
			if (firstElement.getObject() instanceof ILabResult) {
				if (((ILabResult) firstElement.getObject()).getObservationTime() != null) {
					return ((ILabResult) firstElement.getObject()).getObservationTime().toLocalDate();
				} else if (((ILabResult) firstElement.getObject()).getDate() != null) {
					return ((ILabResult) firstElement.getObject()).getDate();
				}
			}
		} else if (element.getObject() instanceof ILabResult) {
			return ((ILabResult) element.getObject()).getObservationTime().toLocalDate();
		}
		return null;
	}

	@Override
	public void doubleClicked(IInboxElement element) {
		if (element instanceof LabGroupedInboxElements) {
			if (rocheView == null && labView == null) {
				rocheView = PlatformUI.getWorkbench().getViewRegistry().find("at.medevit.elexis.roche.labor.view"); //$NON-NLS-1$
				labView = PlatformUI.getWorkbench().getViewRegistry().find("ch.elexis.Labor"); //$NON-NLS-1$
			}
			Display.getDefault().asyncExec(() -> {
				try {
					if (rocheView != null) {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.showView(rocheView.getId());
					} else if (labView != null) {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(labView.getId());
					}
				} catch (PartInitException e) {
					LoggerFactory.getLogger(getClass()).warn("Error showing lab view", e); //$NON-NLS-1$
				}
			});
		}
	}

	@Override
	public boolean isVisible(IInboxElement element) {
		if (element instanceof LabGroupedInboxElements) {
			return true;
		}
		Object obj = element.getObject();
		if (obj instanceof LabResult) {
			return StringUtils.isNotBlank(((LabResult) obj).getResult());
		} else if (obj instanceof ILabResult) {
			ILabResult labResult = (ILabResult) obj;
			if (StringUtils.isBlank(labResult.getResult())) {
				if (LabItemTyp.FORMULA == labResult.getItem().getTyp()) {
					Result<String> result = LabServiceHolder.get().evaluate(labResult);
					if (result.isOK() && StringUtils.isNotBlank(result.get())) {
						labResult.setResult(result.get());
						CoreModelServiceHolder.get().save(labResult);
					}
				}
				return StringUtils.isNotBlank(labResult.getResult());
			}
		}
		return true;
	}

	@Override
	public boolean isGrouped() {
		return true;
	}

	@Override
	public GroupedInboxElements getGrouped(PatientInboxElements patientInboxElements, IInboxElement element) {
		LabGroupedInboxElements ret = null;
		ILabResult labResult = (ILabResult) element.getObject();
		Optional<LabGroupedInboxElements> existing = patientInboxElements.getElements().stream()
				.filter(iie -> iie instanceof LabGroupedInboxElements).map(iie -> (LabGroupedInboxElements) iie)
				.filter(lge -> lge.isMatching(labResult)).findFirst();
		if (existing.isPresent()) {
			ret = existing.get();
		} else {
			ret = new LabGroupedInboxElements();
		}
		ret.addElement(element);
		return ret;
	}
}
