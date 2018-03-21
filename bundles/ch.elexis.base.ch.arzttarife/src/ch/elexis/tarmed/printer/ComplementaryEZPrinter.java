package ch.elexis.tarmed.printer;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;

import ch.elexis.TarmedRechnung.TarmedACL;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.text.TextContainer;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Brief;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Rechnung;
import ch.elexis.data.Rechnungssteller;
import ch.elexis.data.Zahlung;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;

public class ComplementaryEZPrinter {
	
	public static class EZPrinterData {
		Money amountComplementary;
		Money amountUnclassified;
		
		Money due;
		Money paid;
		
		String paymentMode;
		
		public EZPrinterData(){
			
			due = new Money();
			paid = new Money();
			
			paymentMode = XMLExporter.TIERS_GARANT;
		}
	}
	
	private Kontakt getAddressee(String paymentMode, Fall fall, Patient patient){
		Kontakt addressee;
		if (paymentMode.equals(XMLExporter.TIERS_PAYANT)) {
			// TP
			addressee = fall.getRequiredContact(TarmedRequirements.INSURANCE);
		} else if (paymentMode.equals(XMLExporter.TIERS_GARANT)) {
			// TG
			Kontakt invoiceReceiver = fall.getGarant();
			if (invoiceReceiver.equals(patient)) {
				Kontakt legalGuardian = patient.getLegalGuardian();
				if (legalGuardian != null) {
					addressee = legalGuardian;
				} else {
					addressee = patient;
				}
			} else {
				addressee = invoiceReceiver;
			}
		} else {
			addressee = fall.getGarant();
		}
		addressee.getPostAnschrift(true); // damit sicher eine existiert
		return addressee;
	}
	
	private String printer;
	
	public Brief doPrint(Rechnung rn, EZPrinterData ezData, TextContainer text, ESR esr,
		IProgressMonitor monitor){
		
		Money mEZDue = new Money(ezData.due);
		mEZDue.addMoney(ezData.paid);
		
		Brief actBrief;
		Fall fall = rn.getFall();
		Patient pat = fall.getPatient();
		Mandant mnd = rn.getMandant();
		Rechnungssteller rs = mnd.getRechnungssteller();
		Kontakt addressee = getAddressee(ezData.paymentMode, fall, pat);
		
		String tmpl = ComplementaryTemplateRequirement.TT_COMPLEMENTARY_EZ; //$NON-NLS-1$
		actBrief =
			XMLPrinterUtil.createBrief(tmpl, addressee, text);
		
		List<Zahlung> extra = rn.getZahlungen();
		Kontakt bank = Kontakt.load(rs.getInfoString(TarmedACL.getInstance().RNBANK));
		final StringBuilder sb = new StringBuilder();
		sb.append("Komplementär-Leistungen:\t")
			.append(ezData.amountComplementary.getAmountAsString()).append(StringConstants.LF);
		sb.append(Messages.RnPrintView_otherpoints)
			.append(ezData.amountUnclassified.getAmountAsString()).append(StringConstants.LF);
		
		for (Zahlung z : extra) {
			Money betrag = new Money(z.getBetrag()).multiply(-1.0);
			if (!betrag.isNegative()) {
				sb.append(z.getBemerkung())
					.append(":\t").append(betrag.getAmountAsString()).append(StringConstants.LF); //$NON-NLS-1$ 
				mEZDue.addMoney(betrag);
			}
		}
		sb.append("--------------------------------------").append(StringConstants.LF); //$NON-NLS-1$ 
		
		sb.append(Messages.RnPrintView_sum).append(mEZDue);
		
		if (!ezData.paid.isZero()) {
			sb.append(Messages.RnPrintView_prepaid).append(ezData.paid.getAmountAsString())
				.append(StringConstants.LF);
			// sb.append("Noch zu zahlen:\t").append(xmlex.mDue.getAmountAsString()).append("\n");
			sb.append(Messages.RnPrintView_topay)
				.append(mEZDue.subtractMoney(ezData.paid).roundTo5().getAmountAsString())
				.append(StringConstants.LF);
		}
		
		text.getPlugin().setFont("Serif", SWT.NORMAL, 9); //$NON-NLS-1$
		text.replace("\\[Leistungen\\]", sb.toString());
		
		if (esr.printBESR(bank, addressee, rs,
			mEZDue.roundTo5().getCentsAsString(), text) == false) {
			return actBrief;
		}
		printer = CoreHub.localCfg.get("Drucker/A4ESR/Name", null); //$NON-NLS-1$
		String esrTray = CoreHub.localCfg.get("Drucker/A4ESR/Schacht", null); //$NON-NLS-1$
		if (StringTool.isNothing(esrTray)) {
			esrTray = null;
		}
		// Das mit der Tray- Einstellung funktioniert sowieso nicht richtig.
		// OOo nimmt den Tray aus der Druckformatvorlage. Besser wir setzen
		// ihn hier auf
		// null vorläufig.
		// Alternative: Wir verwenden ihn, falls er eingestellt ist, sonst
		// nicht.
		// Dies scheint je nach Druckertreiber unterschiedlich zu
		// funktionieren.
		if (text.getPlugin().print(printer, esrTray, false) == false) {
			SWTHelper.showError("Fehler beim Drucken", "Konnte den Drucker nicht starten");
			rn.addTrace(Rechnung.REJECTED, "Druckerfehler");
			return actBrief;
		}
		
		monitor.worked(2);
		
		return actBrief;
	}
	
}
