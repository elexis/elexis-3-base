package ch.itmed.fop.printing.data;

import ch.elexis.core.l10n.Messages;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IOrganization;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.Gender;
import ch.rgw.tools.StringTool;

public class Salutation {

	/**
	 * extracted from ch.elexis.data.Kontakt#getSalutation
	 * 
	 * @param contact
	 * @return
	 */
	static String getSalutation(IContact contact) {
		StringBuilder sb = new StringBuilder();
		if (contact.isPerson()) {
			IPerson p = CoreModelServiceHolder.get().load(contact.getId(), IPerson.class).orElse(null);

			// TODO default salutation should be configurable
			String salutation;
			if (p.getGender().equals(Gender.MALE)) {
				salutation = Messages.Contact_SalutationM;
			} else {
				salutation = Messages.Contact_SalutationF;
			}
			sb.append(salutation);
			sb.append(StringTool.lf);

			String titel = p.getTitel();
			if (!StringTool.isNothing(titel)) {
				sb.append(titel).append(StringTool.space);
			}
			sb.append(p.getFirstName()).append(StringTool.space).append(p.getLastName()).append(StringTool.lf);

		} else {
			IOrganization o = CoreModelServiceHolder.get().load(contact.getId(), IOrganization.class).orElse(null);
			sb.append(o.getDescription1()).append(StringTool.space).append(o.getDescription2()).append(StringTool.lf);
		}
		return sb.toString();
	}

}
