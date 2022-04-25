package ch.elexis.tarmed.printer;

import java.util.List;

import ch.fd.invoice440.request.RecordDRGType;
import ch.fd.invoice440.request.RecordDrugType;
import ch.fd.invoice440.request.RecordLabType;
import ch.fd.invoice440.request.RecordMigelType;
import ch.fd.invoice440.request.RecordOtherType;
import ch.fd.invoice440.request.RecordParamedType;
import ch.fd.invoice440.request.RecordTarmedType;
import ch.fd.invoice440.request.ServicesType;
import ch.rgw.tools.Money;

public class XML44Services {
	private ServicesType services;

	private Money tarmedMoney;
	private Money drugMoney;
	private Money drgMoney;
	private Money migelMoney;
	private Money labMoney;
	private Money paramedMoney;
	private Money otherMoney;
	private Money complementaryMoney;

	public XML44Services(ServicesType services) {
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
		List<Object> serviceRecords = services.getRecordTarmedOrRecordDrgOrRecordLab();
		for (Object rec : serviceRecords) {
			if (rec instanceof RecordTarmedType) {
				RecordTarmedType tarmed = (RecordTarmedType) rec;
				tarmedMoney.addAmount(tarmed.getAmount());
			} else if (rec instanceof RecordDrugType) {
				RecordDrugType drug = (RecordDrugType) rec;
				drugMoney.addAmount(drug.getAmount());
			} else if (rec instanceof RecordDRGType) {
				RecordDRGType drg = (RecordDRGType) rec;
				drgMoney.addAmount(drg.getAmount());
			} else if (rec instanceof RecordMigelType) {
				RecordMigelType migel = (RecordMigelType) rec;
				migelMoney.addAmount(migel.getAmount());
			} else if (rec instanceof RecordLabType) {
				RecordLabType lab = (RecordLabType) rec;
				labMoney.addAmount(lab.getAmount());
			} else if (rec instanceof RecordParamedType) {
				RecordParamedType param = (RecordParamedType) rec;
				paramedMoney.addAmount(param.getAmount());
			} else if (rec instanceof RecordOtherType) {
				RecordOtherType other = (RecordOtherType) rec;
				String type = other.getTariffType();
				if (type.equals("590")) {
					complementaryMoney.addAmount(other.getAmount());
				} else {
					otherMoney.addAmount(other.getAmount());
				}
			}
		}
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
