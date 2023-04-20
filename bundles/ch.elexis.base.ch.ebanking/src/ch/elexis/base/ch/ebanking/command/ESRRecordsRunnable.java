package ch.elexis.base.ch.ebanking.command;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.base.ch.ebanking.esr.ESRRecord;
import ch.elexis.base.ch.ebanking.esr.Messages;
import ch.elexis.core.model.IAccountTransaction;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.AccountTransaction;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.elexis.data.Zahlung;
import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;

public class ESRRecordsRunnable implements Runnable {

	private IProgressMonitor monitor;
	private List<ESRRecord> records;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public ESRRecordsRunnable(IProgressMonitor monitor, List<ESRRecord> records) {
		if (monitor == null) {
			this.monitor = new NullProgressMonitor();
		} else {
			this.monitor = monitor;
		}
		this.records = records;
	}

	@Override
	public void run() {
		boolean skipPaidAll = false;
		boolean bookPaidAll = false;
		for (ESRRecord rec : records) {
			monitor.worked(1);
			if (rec.getRejectCode().equals(ESRRecord.REJECT.OK)) {
				if (rec.getTyp().equals(ESRRecord.MODE.Summenrecord)) {
					log.info(Messages.ESRView_ESR_finished + rec.getBetrag());
				} else if ((rec.getTyp().equals(ESRRecord.MODE.Storno_edv))
						|| (rec.getTyp().equals(ESRRecord.MODE.Storno_Schalter))) {
					Rechnung rn = rec.getRechnung();
					Money zahlung = rec.getBetrag().negate();
					Zahlung zahlungsObj = rn.addZahlung(zahlung, Messages.ESRView_storno_for + rn.getNr() + " / " //$NON-NLS-1$
							+ rec.getPatient().getPatCode(), new TimeTool(rec.getValuta()));
					if (zahlungsObj != null && ESR.getAccount() != null) {
						IAccountTransaction transaction = zahlungsObj.getTransaction().toIAccountTransaction();
						transaction.setAccount(ESR.getAccount());
						CoreModelServiceHolder.get().save(transaction);
					}
					rec.setGebucht(null);
				} else {
					Rechnung rn = rec.getRechnung();
					if (rn.getStatus() == RnStatus.BEZAHLT) {
						if (skipPaidAll) {
							continue;
						}
						if (!bookPaidAll) {
							int ret = SWTHelper.ask(Messages.ESRView_paid,
									Messages.ESRView_rechnung + rn.getNr() + Messages.ESRView_ispaid,
									IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL,
									IDialogConstants.OK_LABEL + " für Alle",
									IDialogConstants.CANCEL_LABEL + " für Alle");
							if (ret == 3) {
								skipPaidAll = true;
							} else if (ret == 2) {
								bookPaidAll = true;
							}
							if (ret == 1 || skipPaidAll) {
								continue;
							}
						}
					}
					if (rn.getStatus() == RnStatus.IN_BETREIBUNG) {
						if (SWTHelper.askYesNo(Messages.ESRView_compulsoryExecution, Messages.ESRView_rechnung
								+ rn.getNr() + Messages.ESRView_isInCompulsoryExecution) == false) {
							continue;
						}
					}

					Money zahlung = rec.getBetrag();
					Money offen = rn.getOffenerBetrag();
					if (zahlung.isMoreThan(offen) && (zahlung.doubleValue() - offen.doubleValue() > 0.03)) {
						if (SWTHelper.askYesNo(Messages.ESRView_toohigh,
								Messages.ESRView_paymentfor + rn.getNr() + Messages.ESRView_morethan) == false) {
							continue;
						}
					}

					Zahlung zahlungsObj = rn.addZahlung(zahlung, Messages.ESRView_vesrfor + rn.getNr() + " / " //$NON-NLS-1$
							+ rec.getPatient().getPatCode(), new TimeTool(rec.getValuta()));
					if (zahlungsObj != null && ESR.getAccount() != null) {
						IAccountTransaction transaction = zahlungsObj.getTransaction().toIAccountTransaction();
						transaction.setAccount(ESR.getAccount());
						CoreModelServiceHolder.get().save(transaction);
					}
					rec.setGebucht(null);
				}
			} else if (rec.getRejectCode().equals(ESRRecord.REJECT.RN_NUMMER)) {
				TimeTool valutaDate = new TimeTool(rec.getValuta());
				IAccountTransaction transaction = new AccountTransaction(rec.getPatient(), null, rec.getBetrag(),
						valutaDate.toString(TimeTool.DATE_GER), Messages.LoadESRFileHandler_notAssignable)
						.toIAccountTransaction();
				if (ESR.getAccount() != null) {
					transaction.setAccount(ESR.getAccount());
					CoreModelServiceHolder.get().save(transaction);
				}
			}
		}
		monitor.done();
	}
}
