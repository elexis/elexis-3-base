package ch.elexis.laborimport.hl7.universal;

import ch.elexis.core.importer.div.importers.ILabContactResolver;
import ch.elexis.core.importer.div.service.holder.LabImportUtilHolder;
import ch.elexis.core.model.ILaboratory;
import ch.elexis.core.ui.importer.div.importers.DefaultLinkLabContactResolver;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.ui.util.SWTHelper;

public class LinkLabContactResolver implements ILabContactResolver {

	@Override
	public ILaboratory getLabContact(String identifier, String sendingFacility) {
		ILaboratory labor = LabImportUtilHolder.get().getLinkLabor(sendingFacility,
				new DefaultLinkLabContactResolver().identifier(identifier));

		if (labor == null) {
			boolean askYesNo = SWTHelper.askYesNo(Messages.HL7Parser_NoLab, Messages.HL7Parser_AskUseOwnLab);
			if (askYesNo) {
				labor = LabImportUtilHolder.get().getOrCreateLabor(identifier);
			}
		}
		return labor;
	}
}
