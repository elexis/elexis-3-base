package ch.elexis.base.ch.arzttarife.xml.exporter;

import java.time.LocalDate;
import java.util.HashMap;

import ch.rgw.tools.Money;

public class VatRateSum {

	private HashMap<Double, VatRateElement> rates;
	private double sumvat = 0.0;

	private LocalDate invoiceStartDate;

	public VatRateSum(LocalDate localDate) {
		rates = new HashMap<Double, VatRateElement>();
		invoiceStartDate = localDate;
	}

	public void add(double scale, double amount) {
		VatRateElement element = rates.get(Double.valueOf(scale));
		if (element == null) {
			element = new VatRateElement(scale);
			rates.put(Double.valueOf(scale), element);
		}
		element.add(amount);
		sumvat += ((amount / (100.0 + scale)) * scale);
	}

	public Money getSumVat() {
		return new Money(sumvat);
	}

	public HashMap<Double, VatRateElement> getRates() {
		// add missing rates to show correct values on bill
		if (VatUtil.isVatAvailable()) {
			if (isNormalRateMissing()) {
				String normalRate = VatUtil.getNormalRateFromConfig(invoiceStartDate);
				if(normalRate != null) {
					try {
						Double scale = Double.valueOf(normalRate);
						rates.put(scale, new VatRateElement(scale));
					} catch (Exception e) {
						// ignore
					}
				}
			}
			if (isReducedRateMissing()) {
				String reducedRate = VatUtil.getReducedRateFromConfig(invoiceStartDate);
				if (reducedRate != null) {
					try {
						Double scale = Double.valueOf(reducedRate);
						rates.put(scale, new VatRateElement(scale));
					} catch (Exception e) {
						// ignore
					}
				}
			}
		}
		return rates;
	}

	private boolean isReducedRateMissing() {
		return rates.keySet().stream().filter(vatRate -> VatUtil.guessVatCode(vatRate) == 2).findAny().isEmpty();
	}

	private boolean isNormalRateMissing() {
		return rates.keySet().stream().filter(vatRate -> VatUtil.guessVatCode(vatRate) == 1).findAny().isEmpty();
	}

	public static class VatRateElement implements Comparable<VatRateElement> {
		private double scale;
		private double sumamount;
		private double sumvat;

		VatRateElement(double scale) {
			this.scale = scale;
			sumamount = 0.0;
			sumvat = 0.0;
		}

		void add(double amount) {
			sumamount += amount;
			sumvat += ((amount / (100.0 + scale)) * scale);
		}

		@Override
		public int compareTo(VatRateElement other) {
			if (scale < other.scale)
				return -1;
			else if (scale > other.scale)
				return 1;
			else
				return 0;
		}

		public double getScale() {
			return scale;
		}

		public Money getAmount() {
			return new Money(sumamount);
		}

		public Money getVat() {
			return new Money(sumvat);
		}
	}
}
