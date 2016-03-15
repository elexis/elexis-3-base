package ch.elexis.laborimport.hl7.universal;

import ch.elexis.core.data.beans.ContactBean;
import ch.elexis.core.importer.div.importers.ILabContactResolver;
import ch.elexis.core.model.IContact;
import ch.elexis.core.ui.importer.div.importers.LabImportUtil;
import ch.elexis.core.ui.importer.div.importers.Messages;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Labor;

public class LinkLabContactResolver implements ILabContactResolver {
	
	@Override
	public IContact getLabContact(String identifier, String sendingFacility){
		Labor labor = LabImportUtil.getLinkLabor(sendingFacility);
		
		if (labor == null) {
			boolean askYesNo =
				SWTHelper.askYesNo(Messages.HL7Parser_NoLab, Messages.HL7Parser_AskUseOwnLab);
			if (askYesNo) {
				labor = LabImportUtil.getOrCreateLabor(identifier);
			}
		}
		return new ContactBean(labor);
	}
}
