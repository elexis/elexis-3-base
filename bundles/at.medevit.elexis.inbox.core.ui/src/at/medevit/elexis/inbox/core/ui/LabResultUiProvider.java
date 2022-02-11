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

import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.ui.part.model.GroupedInboxElements;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.LabServiceHolder;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.data.LabResult;
import ch.rgw.tools.Result;

public class LabResultUiProvider implements IInboxElementUiProvider {
	//	private static DecorationOverlayIcon pathologicLabImage;
	
	@Inject
	private EPartService partService;
	
	private MPart labPart;
	
	private LabResultLabelProvider labelProvider;
	//	private PathologicInboxFilter filter;
	
	public LabResultUiProvider(){
		labelProvider = new LabResultLabelProvider();
	}
	
	@Override
	public ImageDescriptor getFilterImage(){
		//		if (pathologicLabImage == null) {
		//			initializeImages();
		//		}
		//		return pathologicLabImage;
		return null;
	}
	
	@Override
	public ViewerFilter getFilter(){
		//		if (filter == null) {
		//			filter = new PathologicInboxFilter();
		//		}
		//		return filter;
		return null;
	}
	
	@Override
	public LabelProvider getLabelProvider(){
		return labelProvider;
	}
	
	@Override
	public IColorProvider getColorProvider(){
		return labelProvider;
	}
	
	@Override
	public boolean isProviderFor(IInboxElement element){
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
	
	//	private static void initializeImages(){
	//		ImageDescriptor[] overlays = new ImageDescriptor[1];
	//		overlays[0] = AbstractUIPlugin.imageDescriptorFromPlugin("at.medevit.elexis.inbox.ui", //$NON-NLS-1$
	//			"/rsc/img/achtung_overlay.png"); //$NON-NLS-1$
	//		
	//		pathologicLabImage =
	//			new DecorationOverlayIcon(Images.IMG_VIEW_LABORATORY.getImage(), overlays);
	//	}
	
	@Override
	public void doubleClicked(IInboxElement element){
		if (partService == null) {
			CoreUiUtil.injectServicesWithContext(this);
			IViewDescriptor rocheView = PlatformUI.getWorkbench().getViewRegistry()
				.find("at.medevit.elexis.roche.labor.view");
			if (rocheView != null) {
				labPart = partService.findPart("at.medevit.elexis.roche.labor.view");
			} else {
				labPart = partService.findPart("ch.elexis.Labor");
			}
		}
		if (element instanceof LabGroupedInboxElements) {
			Display.getDefault().asyncExec(() -> {
				if (partService != null && labPart != null) {
					partService.showPart(labPart, PartState.ACTIVATE);
				}
			});
		}
	}
	
	@Override
	public boolean isVisible(IInboxElement element){
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
	public boolean isGrouped(){
		return true;
	}
	
	@Override
	public GroupedInboxElements getGrouped(PatientInboxElements patientInboxElements,
		IInboxElement element){
		LabGroupedInboxElements ret = null;
		ILabResult labResult = (ILabResult) element.getObject();
		Optional<LabGroupedInboxElements> existing = patientInboxElements.getElements().stream()
			.filter(iie -> iie instanceof LabGroupedInboxElements)
			.map(iie -> (LabGroupedInboxElements) iie).filter(lge -> lge.isMatching(labResult))
			.findFirst();
		if (existing.isPresent()) {
			ret = existing.get();
		} else {
			ret = new LabGroupedInboxElements();
		}
		ret.addElement(element);
		return ret;
	}
}
