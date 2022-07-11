package ch.elexis.base.ch.diagnosecodes.util;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.diagnosecodes.service.CodeElementServiceHolder;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IDiagnosis;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.util.IKonsMakro;

public class TIMakro implements IKonsMakro {

	private static Logger logger = LoggerFactory.getLogger(TIMakro.class);

	public TIMakro() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String executeMakro(String makro) {
		Optional<IEncounter> encounter = ContextServiceHolder.get().getRootContext().getTyped(IEncounter.class);
		if (encounter.isPresent()) {
			try {
				Optional<ICodeElement> tiCode = CodeElementServiceHolder.get().loadFromString("TI-Code", makro, null); //$NON-NLS-1$
				if (tiCode.isPresent()) {
					encounter.get().addDiagnosis((IDiagnosis) tiCode.get());
					CoreModelServiceHolder.get().save(encounter.get());
					return StringConstants.EMPTY;
				}
			} catch (Exception e) {
				logger.debug("Could not resolve TI Code [" + makro + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return null;
	}

}
