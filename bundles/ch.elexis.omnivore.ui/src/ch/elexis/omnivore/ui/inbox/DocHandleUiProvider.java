package ch.elexis.omnivore.ui.inbox;

import java.time.LocalDate;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import ch.elexis.core.time.TimeUtil;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.ui.util.UiUtils;

public class DocHandleUiProvider implements IInboxElementUiProvider {
	private DocHandleLabelProvider labelProvider;
	private DocHandleViewerFilter filter;

	public DocHandleUiProvider() {
		labelProvider = new DocHandleLabelProvider();
		filter = new DocHandleViewerFilter();
	}

	@Override
	public ImageDescriptor getFilterImage() {
		return AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.omnivore.ui", "icons/fressen.gif"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public ViewerFilter getFilter() {
		return filter;
	}

	@Override
	public LabelProvider getLabelProvider() {
		return labelProvider;
	}

	@Override
	public IColorProvider getColorProvider() {
		return null;
	}

	@Override
	public LocalDate getObjectDate(IInboxElement element) {
		if (element.getObject() instanceof IDocumentHandle) {
			return TimeUtil.toLocalDate(((IDocumentHandle) element.getObject()).getCreated());
		}
		return null;
	}

	@Override
	public boolean isProviderFor(IInboxElement element) {
		Object obj = element.getObject();
		if (obj instanceof IDocumentHandle) {
			return true;
		}
		return false;
	}

	@Override
	public void doubleClicked(IInboxElement element) {
		Object obj = element.getObject();
		if (isProviderFor(element)) {
			IDocumentHandle document = (IDocumentHandle) obj;
			UiUtils.open(document);
		}
	}
}
