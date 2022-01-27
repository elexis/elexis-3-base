package ch.elexis.tarmed.printer;

import ch.fd.invoice450.request.BalanceTGType;
import ch.fd.invoice450.request.BalanceTPType;
import ch.fd.invoice450.request.VatType;

public class Balance45Adapter {
	
	private BalanceTGType tgType;
	private BalanceTPType tpType;
	
	public Balance45Adapter(BalanceTGType balance){
		this.tgType = balance;
	}
	
	public Balance45Adapter(BalanceTPType balance){
		this.tpType = balance;
	}
	
	public double getAmountDue(){
		if (tgType != null) {
			return tgType.getAmountDue();
		}
		return tpType.getAmountDue();
	}
	
	public double getAmountReminder(){
		if (tgType != null) {
			return tgType.getAmountReminder();
		}
		return tpType.getAmountReminder();
	}
	
	public double getAmountPrepaid(){
		if (tgType != null) {
			return tgType.getAmountPrepaid();
		}
		return 0.0;
	}
	
	public VatType getVat(){
		if (tgType != null) {
			return tgType.getVat();
		}
		return tpType.getVat();
	}
	
	public double getAmount(){
		if (tgType != null) {
			return tgType.getAmount();
		}
		return tpType.getAmount();
	}
	
	public double getAmountObligations(){
		if (tgType != null) {
			return tgType.getAmountObligations();
		}
		return tpType.getAmountObligations();
	}
}
