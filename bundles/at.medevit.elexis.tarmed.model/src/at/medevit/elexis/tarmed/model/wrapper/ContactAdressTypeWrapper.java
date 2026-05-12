package at.medevit.elexis.tarmed.model.wrapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Wrapper Object providing transparent access for different
 * (http://www.forum-datenaustausch.ch/invoice) versions.
 *
 * @author thomas
 *
 */
public class ContactAdressTypeWrapper {

	private ch.fd.invoice400.response.ReplyAddressType replyAddressType40;
	private ch.fd.invoice440.response.ContactAddressType contactAddressType44;
	private ch.fd.invoice450.response.ContactAddressType contactAddressType45;
	private ch.fd.invoice500.response.ContactAddressType contactAddressType50;

	public ContactAdressTypeWrapper(ch.fd.invoice400.response.ReplyAddressType replyAddressType40) {
		this.replyAddressType40 = replyAddressType40;
	}

	public ContactAdressTypeWrapper(ch.fd.invoice440.response.ContactAddressType contactAddressType44) {
		this.contactAddressType44 = contactAddressType44;
	}

	public ContactAdressTypeWrapper(ch.fd.invoice450.response.ContactAddressType contactAddressType45) {
		this.contactAddressType45 = contactAddressType45;
	}

	public ContactAdressTypeWrapper(ch.fd.invoice500.response.ContactAddressType contactAddressType50) {
		this.contactAddressType50 = contactAddressType50;
	}

	public String getContactPhoneNumber() {
		if (replyAddressType40 != null) {
			ch.fd.invoice400.response.TelecomAddressType tk = getData40(
					ch.fd.invoice400.response.TelecomAddressType.class, FetchType.ADVISOR_TELECOM);
			if (tk != null) {
				return getFlatString(tk.getPhone(), ", ");
			}
		} else if (contactAddressType44 != null) {
			ch.fd.invoice440.response.TelecomAddressType tk = getData44(
					ch.fd.invoice440.response.TelecomAddressType.class, FetchType.ADVISOR_TELECOM);
			if (tk != null) {
				return getFlatString(tk.getPhone(), ", ");
			}
		} else if (contactAddressType45 != null) {
			ch.fd.invoice450.response.TelecomAddressType tk = getData45(
					ch.fd.invoice450.response.TelecomAddressType.class, FetchType.ADVISOR_TELECOM);
			if (tk != null) {
				return getFlatString(tk.getPhone(), ", ");
			}
		} else if (contactAddressType50 != null) {
			ch.fd.invoice500.response.TelecomAddressType tk = getData50(
					ch.fd.invoice500.response.TelecomAddressType.class, FetchType.ADVISOR_TELECOM);
			if (tk != null) {
				return getFlatString(tk.getPhone(), ", ");
			}
		}
		return null;
	}

	public String getContactName() {
		String name = null;
		if (replyAddressType40 != null) {
			name = getData40(String.class, FetchType.ADVISORNAME);
		} else if (contactAddressType44 != null) {
			name = getData44(String.class, FetchType.ADVISORNAME);
		} else if (contactAddressType45 != null) {
			name = getData45(String.class, FetchType.ADVISORNAME);
		} else if (contactAddressType50 != null) {
			name = getData50(String.class, FetchType.ADVISORNAME);
		}
		return name;
	}

	private <T> T getData50(Class<T> ret, FetchType fetchType) {
		if (contactAddressType50 != null) {
			if (contactAddressType50.getPerson() != null) {
				if (fetchType.equals(FetchType.ADVISOR_TELECOM)
						&& ret.isInstance(contactAddressType50.getPerson().getTelecom())) {
					return ret.cast(contactAddressType50.getPerson().getTelecom());
				} else if (fetchType.equals(FetchType.ADVISORNAME) && ret.isInstance("")) {
					return ret.cast(Objects.toString(contactAddressType50.getPerson().getGivenname(), "") + " "
							+ Objects.toString(contactAddressType50.getPerson().getFamilyname(), ""));
				}
			} else if (contactAddressType50.getEmployee() != null) {
				if (fetchType.equals(FetchType.ADVISOR_TELECOM)
						&& ret.isInstance(contactAddressType50.getEmployee().getTelecom())) {
					return ret.cast(contactAddressType50.getEmployee().getTelecom());
				} else if (fetchType.equals(FetchType.ADVISORNAME) && ret.isInstance("")) {
					return ret.cast(Objects.toString(contactAddressType50.getEmployee().getGivenname(), "") + " "
							+ Objects.toString(contactAddressType50.getEmployee().getFamilyname(), ""));
				}
			} else if (contactAddressType50.getCompany() != null) {
				if (fetchType.equals(FetchType.ADVISOR_TELECOM)
						&& ret.isInstance(contactAddressType50.getCompany().getTelecom())) {
					return ret.cast(contactAddressType50.getCompany().getTelecom());
				} else if (fetchType.equals(FetchType.ADVISORNAME) && ret.isInstance("")) {

					return ret.cast(Objects.toString(contactAddressType50.getCompany().getCompanyname(), "") + " "
							+ Objects.toString(contactAddressType50.getCompany().getDepartment(), ""));
				}
			}
		}
		return null;
	}

	private <T> T getData45(Class<T> ret, FetchType fetchType) {
		if (contactAddressType45 != null) {
			if (contactAddressType45.getPerson() != null) {
				if (fetchType.equals(FetchType.ADVISOR_TELECOM)
						&& ret.isInstance(contactAddressType45.getPerson().getTelecom())) {
					return ret.cast(contactAddressType45.getPerson().getTelecom());
				} else if (fetchType.equals(FetchType.ADVISORNAME) && ret.isInstance("")) {
					return ret.cast(Objects.toString(contactAddressType45.getPerson().getGivenname(), "") + " "
							+ Objects.toString(contactAddressType45.getPerson().getFamilyname(), ""));
				}
			} else if (contactAddressType45.getEmployee() != null) {
				if (fetchType.equals(FetchType.ADVISOR_TELECOM)
						&& ret.isInstance(contactAddressType45.getEmployee().getTelecom())) {
					return ret.cast(contactAddressType45.getEmployee().getTelecom());
				} else if (fetchType.equals(FetchType.ADVISORNAME) && ret.isInstance("")) {
					return ret.cast(Objects.toString(contactAddressType45.getEmployee().getGivenname(), "") + " "
							+ Objects.toString(contactAddressType45.getEmployee().getFamilyname(), ""));
				}
			} else if (contactAddressType45.getCompany() != null) {
				if (fetchType.equals(FetchType.ADVISOR_TELECOM)
						&& ret.isInstance(contactAddressType45.getCompany().getTelecom())) {
					return ret.cast(contactAddressType45.getCompany().getTelecom());
				} else if (fetchType.equals(FetchType.ADVISORNAME) && ret.isInstance("")) {

					return ret.cast(Objects.toString(contactAddressType45.getCompany().getCompanyname(), "") + " "
							+ Objects.toString(contactAddressType45.getCompany().getDepartment(), ""));
				}
			}
		}
		return null;
	}

	private <T> T getData44(Class<T> ret, FetchType fetchType) {
		if (contactAddressType44 != null) {
			if (contactAddressType44.getPerson() != null) {
				if (fetchType.equals(FetchType.ADVISOR_TELECOM)
						&& ret.isInstance(contactAddressType44.getPerson().getTelecom())) {
					return ret.cast(contactAddressType44.getPerson().getTelecom());
				} else if (fetchType.equals(FetchType.ADVISORNAME) && ret.isInstance("")) {
					return ret.cast(Objects.toString(contactAddressType44.getPerson().getGivenname(), "") + " "
							+ Objects.toString(contactAddressType44.getPerson().getFamilyname(), ""));
				}
			} else if (contactAddressType44.getEmployee() != null) {
				if (fetchType.equals(FetchType.ADVISOR_TELECOM)
						&& ret.isInstance(contactAddressType44.getEmployee().getTelecom())) {
					return ret.cast(contactAddressType44.getEmployee().getTelecom());
				} else if (fetchType.equals(FetchType.ADVISORNAME) && ret.isInstance("")) {
					return ret.cast(Objects.toString(contactAddressType44.getEmployee().getGivenname(), "") + " "
							+ Objects.toString(contactAddressType44.getEmployee().getFamilyname(), ""));
				}
			} else if (contactAddressType44.getCompany() != null) {
				if (fetchType.equals(FetchType.ADVISOR_TELECOM)
						&& ret.isInstance(contactAddressType44.getCompany().getTelecom())) {
					return ret.cast(contactAddressType44.getCompany().getTelecom());
				} else if (fetchType.equals(FetchType.ADVISORNAME) && ret.isInstance("")) {

					return ret.cast(Objects.toString(contactAddressType44.getCompany().getCompanyname(), "") + " "
							+ Objects.toString(contactAddressType44.getCompany().getDepartment(), ""));
				}
			}
		}
		return null;
	}

	private <T> T getData40(Class<T> ret, FetchType fetchType) {
		if (replyAddressType40 != null) {
			if (replyAddressType40.getContact() != null) {
				if (fetchType.equals(FetchType.ADVISOR_TELECOM)
						&& ret.isInstance(replyAddressType40.getContact().getTelecom())) {
					return ret.cast(replyAddressType40.getContact().getTelecom());
				} else if (fetchType.equals(FetchType.ADVISORNAME) && ret.isInstance("")) {
					return ret.cast(getFlatString(replyAddressType40.getContact().getGivenname(), " ") + " "
							+ Objects.toString(replyAddressType40.getContact().getFamilyname(), ""));
				}
			} else if (replyAddressType40.getCompany() != null) {
				if (fetchType.equals(FetchType.ADVISOR_TELECOM)
						&& ret.isInstance(replyAddressType40.getCompany().getTelecom())) {
					return ret.cast(replyAddressType40.getCompany().getTelecom());
				} else if (fetchType.equals(FetchType.ADVISORNAME) && ret.isInstance("")) {
					return ret.cast(Objects.toString(replyAddressType40.getCompany().getCompanyname(), "") + " "
							+ Objects.toString(replyAddressType40.getCompany().getDepartment(), ""));
				}
			}
		}
		return null;
	}

	private String getFlatString(List<String> list, String concat) {
		if (list != null) {
			return list.stream().filter(Objects::nonNull).collect(Collectors.joining(concat));
		}
		return "";
	}

	private enum FetchType {
		ADVISOR_TELECOM, ADVISORNAME;
	}
}
