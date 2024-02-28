package ch.elexis.omnivore.ui.views;

import java.util.Optional;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.StyleRange;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.text.IRichTextDisplay;
import ch.elexis.core.ui.util.IKonsExtension;
import ch.elexis.data.Konsultation;
import ch.elexis.omnivore.Constants;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.ui.service.OmnivoreModelServiceHolder;
import ch.elexis.omnivore.ui.util.UiUtils;

public class KonsExtension implements IKonsExtension {
	IRichTextDisplay mine;

	static final String DOCHANDLE_TITLE = "Dokument: ";

	@Override
	public String connect(IRichTextDisplay tf) {
		mine = tf;
		mine.addDropReceiver(IDocumentHandle.class, this);
		return Constants.PLUGIN_ID;
	}

	@Override
	public boolean doLayout(StyleRange styleRange, String provider, String id) {
		styleRange.background = UiDesk.getColor(UiDesk.COL_LIGHTBLUE);
		return true;
	}

	@Override
	public boolean doXRef(String refProvider, String refID) {
		Optional<IDocumentHandle> handle = OmnivoreModelServiceHolder.get().load(refID, IDocumentHandle.class);
		if (handle.isPresent()) {
			UiUtils.open(handle.get());
			ContextServiceHolder.get().getRootContext().setNamed(IContextService.SELECTIONFALLBACK, handle.get());
		}
		return true;
	}

	@Override
	public void insert(Object o, int pos) {
		if (o instanceof IDocumentHandle) {
			IDocumentHandle handle = (IDocumentHandle) o;
			final Konsultation k = (Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);

			mine.insertXRef(pos, DOCHANDLE_TITLE + handle.getLabel(), Constants.PLUGIN_ID, handle.getId());
			k.updateEintrag(mine.getContentsAsXML(), false);
			ElexisEventDispatcher.update(k);
		}

	}

	@Override
	public IAction[] getActions() {
		return null;
	}

	@Override
	public void removeXRef(String refProvider, String refID) {
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
	}

}
