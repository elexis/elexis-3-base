package at.medevit.elexis.agenda.ui.function;

import org.apache.commons.lang3.StringUtils;

import com.equo.chromium.swt.Browser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class LoadContactInfoFunction extends AbstractBrowserFunction {

	private Gson gson;

	public LoadContactInfoFunction(Browser browser, String name) {
		super(browser, name);
		gson = new GsonBuilder().create();
	}

	@Override
	public Object function(Object[] arguments) {
		if (arguments.length == 1) {
			IAppointment termin = CoreModelServiceHolder.get().load((String) arguments[0], IAppointment.class)
					.orElse(null);
			if (termin != null) {
				IContact contact = termin.getContact();
				if (contact != null) {
					ContactInfo ret = new ContactInfo();
					if (StringUtils.isNotBlank(contact.getEmail())) {
						ret.setMail(contact.getEmail());
					}
					if (ret.getMail() == null && StringUtils.isNotBlank(contact.getEmail2())) {
						ret.setMail(contact.getEmail2());
					}
					if (StringUtils.isNotBlank(contact.getMobile())) {
						ret.setTel(contact.getMobile());
					}
					if (ret.getTel() == null && StringUtils.isNotBlank(contact.getPhone1())) {
						ret.setTel(contact.getPhone1());
					}
					return gson.toJson(ret);
				}
			}
		}
		return null;
	}

	private class ContactInfo {

		private String tel;
		private String mail;

		public String getMail() {
			return mail;
		}

		public String getTel() {
			return tel;
		}

		public void setMail(String mail) {
			this.mail = mail;
		}

		public void setTel(String tel) {
			this.tel = tel;
		}
	}
}
