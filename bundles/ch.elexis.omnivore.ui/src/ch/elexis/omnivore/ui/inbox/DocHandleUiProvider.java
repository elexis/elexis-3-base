package ch.elexis.omnivore.ui.inbox;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.time.TimeUtil;
import ch.elexis.core.ui.e4.events.ElexisUiEventTopics;
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
	public ImageDescriptor getFilterImage(ViewerFilter filter) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.omnivore.ui", "icons/fressen.gif"); //$NON-NLS-1$ //$NON-NLS-2$
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
		return null;
	}

	@Override
	public IToolTipProvider getToolTipProvider() {
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

	@Override
	public void singleClicked(IInboxElement element) {
		Object obj = element.getObject();
		if (isProviderFor(element)) {
			IDocumentHandle document = (IDocumentHandle) obj;
			if (document != null && !document.isCategory()) {
				if (StringUtils.containsIgnoreCase(document.getMimeType(), "pdf")) { //$NON-NLS-1$
					ContextServiceHolder.get().postEvent(ElexisUiEventTopics.EVENT_PREVIEW_MIMETYPE_PDF, document);
				}
				else if (StringUtils.containsIgnoreCase(document.getMimeType(), "docx")) { //$NON-NLS-1$
					ContextServiceHolder.get().postEvent(ElexisUiEventTopics.EVENT_PREVIEW_MIMETYPE_PDF, document);
				}
			}
		}
	}
}
