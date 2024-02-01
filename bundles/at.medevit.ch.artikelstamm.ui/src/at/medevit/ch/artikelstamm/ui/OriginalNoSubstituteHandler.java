package at.medevit.ch.artikelstamm.ui;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class OriginalNoSubstituteHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Optional<IBilled> selection = ContextServiceHolder.get().getTyped(IBilled.class);
		if (selection.isPresent()) {
			String value = (String) selection.get().getExtInfo(Constants.FLD_EXT_ORIGINALNOSUBSTITUTE);
			if (StringUtils.isNotBlank(value) && "true".equals(value)) {
				selection.get().setExtInfo(Constants.FLD_EXT_ORIGINALNOSUBSTITUTE, "false"); //$NON-NLS-1$
			} else {
				selection.get().setExtInfo(Constants.FLD_EXT_ORIGINALNOSUBSTITUTE, "true"); //$NON-NLS-1$
			}
			CoreModelServiceHolder.get().save(selection.get());
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		Optional<IBilled> selection = ContextServiceHolder.get().getTyped(IBilled.class);
		if (selection.isPresent()) {
			IBillable billable = selection.get().getBillable();
			return billable instanceof IArtikelstammItem && "O".equals(((IArtikelstammItem) billable).getGenericType());
		}
		return false;
	}
}
