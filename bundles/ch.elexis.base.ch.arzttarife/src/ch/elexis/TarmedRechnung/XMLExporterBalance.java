package ch.elexis.TarmedRechnung;

import java.util.Arrays;

import org.jdom2.Element;

import ch.elexis.TarmedRechnung.XMLExporter.VatRateSum;
import ch.elexis.TarmedRechnung.XMLExporter.VatRateSum.VatRateElement;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IMandator;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.XMLTool;

public class XMLExporterBalance {

	private static final String ATTR_AMOUNT_OBLIGATIONS = "amount_obligations"; //$NON-NLS-1$

	private Element balanceElement;

	private Money mDue;
	private Money mAmount = new Money();

	public XMLExporterBalance(Element balance) {
		this.balanceElement = balance;
	}

	public Money getDue() {
		if (mDue == null) {
			String attrValue = balanceElement.getAttributeValue(XMLExporter.ATTR_AMOUNT_DUE);
			if (attrValue != null && !attrValue.isEmpty()) {
				mDue = XMLTool.xmlDoubleToMoney(attrValue);
			} else {
				mDue = new Money();
			}
		}
		return mDue;
	}

	public void setDue(Money money) {
		mDue = money;
		balanceElement.setAttribute(XMLExporter.ATTR_AMOUNT_DUE, XMLTool.moneyToXmlDouble(mDue));
	}

	public Money getAmount() {
		String attrValue = balanceElement.getAttributeValue(XMLExporter.ATTR_AMOUNT);
		if (attrValue != null && !attrValue.isEmpty()) {
			mAmount = XMLTool.xmlDoubleToMoney(attrValue);
		} else {
			mAmount = new Money();
		}
		return mAmount;
	}

	public void setAmount(Money money) {
		balanceElement.setAttribute(XMLExporter.ATTR_AMOUNT, XMLTool.moneyToXmlDouble(money));
	}

	public Money getAmountObligations() {
		String attrValue = balanceElement.getAttributeValue(ATTR_AMOUNT_OBLIGATIONS);
		if (attrValue != null && !attrValue.isEmpty()) {
			return XMLTool.xmlDoubleToMoney(attrValue);
		}
		return new Money();
	}

	public Money getReminder() {
		String attrValue = balanceElement.getAttributeValue(XMLExporter.ATTR_AMOUNT_REMINDER);
		if (attrValue != null && !attrValue.isEmpty()) {
			return XMLTool.xmlDoubleToMoney(attrValue);
		}
		return new Money();
	}

	public Money getPrepaid() {
		String attrValue = balanceElement.getAttributeValue(XMLExporter.ATTR_AMOUNT_PREPAID);
		if (attrValue != null && !attrValue.isEmpty()) {
			return XMLTool.xmlDoubleToMoney(attrValue);
		}
		return new Money();
	}

	public void setPrepaid(Money money) {
		balanceElement.setAttribute(XMLExporter.ATTR_AMOUNT_PREPAID, XMLTool.moneyToXmlDouble(money));
	}

	public boolean hasPrepaid() {
		String attrValue = balanceElement.getAttributeValue(XMLExporter.ATTR_AMOUNT_PREPAID);
		return attrValue != null && !attrValue.isEmpty();
	}

	public Element getElement() {
		return balanceElement;
	}

	public void negateAmount() {
		XMLExporterUtil.negate(balanceElement, XMLExporter.ATTR_AMOUNT);
	}

	public void negateAmountObligations() {
		XMLExporterUtil.negate(balanceElement, ATTR_AMOUNT_OBLIGATIONS);
	}

	public static XMLExporterBalance buildBalance(IInvoice invoice, XMLExporterServices services, VatRateSum vatSummer,
			XMLExporter xmlExporter) {

		IMandator actMandant = invoice.getMandator();
		Money reminders = invoice.getDemandAmount();

		Element element = new Element(XMLExporter.ELEMENT_BALANCE, XMLExporter.nsinvoice);
		XMLExporterBalance balance = new XMLExporterBalance(element);

		String curr = (String) actMandant.getBiller().getExtInfo(Messages.XMLExporter_Currency);
		if (StringTool.isNothing(curr)) {
			curr = "CHF"; //$NON-NLS-1$
		}
		element.setAttribute("currency", curr);

		balance.mAmount.addMoney(services.getTarmedMoney()).addMoney(services.getAnalysenMoney())
				.addMoney(services.getMedikamentMoney()).addMoney(services.getUebrigeMoney())
				.addMoney(services.getKantMoney()).addMoney(services.getPhysioMoney())
				.addMoney(services.getMigelMoney());

		element.setAttribute(XMLExporter.ATTR_AMOUNT_PREPAID,
				XMLTool.moneyToXmlDouble(new Money(invoice.getPayedAmount())));

		if (!reminders.isZero()) {
			element.setAttribute(XMLExporter.ATTR_AMOUNT_REMINDER, XMLTool.moneyToXmlDouble(reminders));
		}

		element.setAttribute(XMLExporter.ATTR_AMOUNT, XMLTool.moneyToXmlDouble(balance.mAmount));
		balance.mDue = new Money(balance.mAmount);
		if (!reminders.isZero()) {
			balance.mDue.addMoney(reminders);
		}
		balance.mDue.subtractMoney(invoice.getPayedAmount());
		balance.mDue.roundTo5();
		element.setAttribute(XMLExporter.ATTR_AMOUNT_DUE, XMLTool.moneyToXmlDouble(balance.mDue));

		element.setAttribute(ATTR_AMOUNT_OBLIGATIONS, XMLTool.moneyToXmlDouble(services.getObligationsMoney()));

		Element vat = new Element(XMLExporter.ELEMENT_VAT, XMLExporter.nsinvoice);

		String vatNumber = (String) actMandant.getBiller().getExtInfo(XMLExporter.VAT_MANDANTVATNUMBER);
		if (vatNumber != null && vatNumber.length() > 0)
			vat.setAttribute(XMLExporter.ELEMENT_VAT_NUMBER, vatNumber);

		vat.setAttribute(XMLExporter.ELEMENT_VAT, XMLTool.doubleToXmlDouble(vatSummer.sumvat, 2));

		VatRateElement[] vatValues = vatSummer.rates.values().toArray(new VatRateElement[0]);
		Arrays.sort(vatValues);
		for (VatRateElement rate : vatValues) {
			Element vatrate = new Element(XMLExporter.ATTR_VAT_RATE, XMLExporter.nsinvoice);
			vatrate.setAttribute(XMLExporter.ATTR_VAT_RATE, XMLTool.doubleToXmlDouble(rate.scale, 2));
			vatrate.setAttribute(XMLExporter.ATTR_AMOUNT, XMLTool.doubleToXmlDouble(rate.sumamount, 2));
			vatrate.setAttribute(XMLExporter.ELEMENT_VAT, XMLTool.doubleToXmlDouble(rate.sumvat, 2));
			vat.addContent(vatrate);
		}

		element.addContent(vat);

		return balance;
	}
}
