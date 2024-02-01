package at.medevit.ch.artikelstamm.ui;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.State;

import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.holder.ContextServiceHolder;

public class OriginalNoSubstituteState extends State {

	public OriginalNoSubstituteState() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getValue() {
		Optional<IBilled> selection = ContextServiceHolder.get().getTyped(IBilled.class);
		if (selection.isPresent()) {
			String value = (String) selection.get().getExtInfo(Constants.FLD_EXT_ORIGINALNOSUBSTITUTE);
			return Boolean.valueOf(StringUtils.isNotBlank(value) && "true".equals(value));
		}
		return Boolean.FALSE;
	}
}
