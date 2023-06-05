package at.medevit.elexis.emediplan.inbox;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.elexis.emediplan.StartupHandler;
import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import ch.elexis.core.model.IBlob;
import ch.rgw.tools.TimeTool;

public class EMediplanUiProvider implements IInboxElementUiProvider {
	private EMediplanLabelProvider labelProvider;
	private EMediplanViewerFilter filter;

	public EMediplanUiProvider() {
		labelProvider = new EMediplanLabelProvider();
		filter = new EMediplanViewerFilter();
	}

	@Override
	public ImageDescriptor getFilterImage(ViewerFilter filter) {
		return ResourceManager.getPluginImageDescriptor("at.medevit.elexis.emediplan.ui", "rsc/logo.png"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public List<ViewerFilter> getFilters() {
		return Collections.singletonList(filter);
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
	public IToolTipProvider getToolTipProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDate getObjectDate(IInboxElement element) {
		IBlob document = (IBlob) ((IInboxElement) element).getObject();
		return new TimeTool(document.getLastupdate()).toLocalDate();
	}

	@Override
	public boolean isProviderFor(IInboxElement element) {
		Object obj = element.getObject();
		if (obj instanceof IBlob && ((IBlob) obj).getId().startsWith("Med_")) { //$NON-NLS-1$

			return true;
		}
		return false;
	}

	@Override
	public void doubleClicked(IInboxElement element) {
		Object obj = element.getObject();
		if (isProviderFor(element)) {
			IBlob document = (IBlob) obj;
			StartupHandler.openEMediplanImportDialog(document.getStringContent(), element.getPatient().getId());
		}
	}

}
