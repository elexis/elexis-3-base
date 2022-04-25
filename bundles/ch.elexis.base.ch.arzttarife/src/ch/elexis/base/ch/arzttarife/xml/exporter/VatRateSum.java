package ch.elexis.base.ch.arzttarife.xml.exporter;

import java.util.HashMap;

import ch.rgw.tools.Money;

public class VatRateSum {

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

	private HashMap<Double, VatRateElement> rates = new HashMap<Double, VatRateElement>();
	private double sumvat = 0.0;

	public void add(double scale, double amount) {
		VatRateElement element = rates.get(Double.valueOf(scale));
		if (element == null) {
			element = new VatRateElement(scale);
			rates.put(new Double(scale), element);
		}
		element.add(amount);
		sumvat += ((amount / (100.0 + scale)) * scale);
	}

	public Money getSumVat() {
		return new Money(sumvat);
	}

	public HashMap<Double, VatRateElement> getRates() {
		return rates;
	}
}
