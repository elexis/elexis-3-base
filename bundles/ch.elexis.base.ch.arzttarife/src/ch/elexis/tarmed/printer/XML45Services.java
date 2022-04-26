package ch.elexis.tarmed.printer;

import java.util.List;

import ch.fd.invoice450.request.ServiceExType;
import ch.fd.invoice450.request.ServiceType;
import ch.fd.invoice450.request.ServicesType;
import ch.rgw.tools.Money;

public class XML45Services {
	private ServicesType services;

	private Money tarmedMoney;
	private Money drugMoney;
	private Money drgMoney;
	private Money migelMoney;
	private Money labMoney;
	private Money paramedMoney;
	private Money otherMoney;
	private Money complementaryMoney;

	public XML45Services(ServicesType services) {
		this.services = services;
		tarmedMoney = new Money();
		drugMoney = new Money();
		drgMoney = new Money();
		migelMoney = new Money();
		labMoney = new Money();
		paramedMoney = new Money();
		otherMoney = new Money();
		complementaryMoney = new Money();
		initMoneyAmounts();
	}

	private void initMoneyAmounts() {
		List<Object> serviceRecords = services.getServiceExOrService();
		for (Object rec : serviceRecords) {
			if (rec instanceof ServiceExType) {
				ServiceExType tarmed = (ServiceExType) rec;
				tarmedMoney.addAmount(tarmed.getAmount());
			} else if (rec instanceof ServiceType && matchTarifType((ServiceType) rec, "400", "402")) {
				drugMoney.addAmount(((ServiceType) rec).getAmount());
			} else if (rec instanceof ServiceType && matchTarifType((ServiceType) rec, "452")) {
				migelMoney.addAmount(((ServiceType) rec).getAmount());
			} else if (rec instanceof ServiceType && matchTarifType((ServiceType) rec, "317")) {
				labMoney.addAmount(((ServiceType) rec).getAmount());
			} else if (rec instanceof ServiceType && matchTarifType((ServiceType) rec, "311", "312")) {
				paramedMoney.addAmount(((ServiceType) rec).getAmount());
			} else if (rec instanceof ServiceType) {
				String type = ((ServiceType) rec).getTariffType();
				if (type.equals("590")) {
					complementaryMoney.addAmount(((ServiceType) rec).getAmount());
				} else {
					otherMoney.addAmount(((ServiceType) rec).getAmount());
				}
			}
		}
	}

	private boolean matchTarifType(ServiceType rec, String... types) {
		for (String type : types) {
			if (type.equals(((ServiceType) rec).getTariffType())) {
				return true;
			}
		}
		return false;
	}

	public Money getTarmedMoney() {
		return tarmedMoney;
	}

	public Money getDrugMoney() {
		return drugMoney;
	}

	public Money getDrgMoney() {
		return drgMoney;
	}

	public Money getMigelMoney() {
		return migelMoney;
	}

	public Money getLabMoney() {
		return labMoney;
	}

	public Money getParamedMoney() {
		return paramedMoney;
	}

	public Money getOtherMoney() {
		return otherMoney;
	}

	public Money getComplementaryMoney() {
		return complementaryMoney;
	}
}
