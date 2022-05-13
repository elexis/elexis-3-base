package ch.elexis.base.ch.ebanking.command;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.ebanking.ESRView;
import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.base.ch.ebanking.esr.ESRFile;
import ch.elexis.base.ch.ebanking.esr.ESRRecord;
import ch.elexis.base.ch.ebanking.esr.Messages;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.AccountTransaction;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.elexis.data.Zahlung;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class LoadESRFileHandler extends AbstractHandler implements IElementUpdater {

	public static final String COMMAND_ID = "ch.elexis.ebanking_ch.command.loadESRFile";

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileDialog fld = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.OPEN);
		fld.setText(Messages.ESRView_selectESR);
		final String filename = fld.open();
		if (filename != null) {
			final ESRFile esrf = new ESRFile();
			final File file = new File(filename);
			try {
				PlatformUI.getWorkbench().getProgressService().busyCursorWhile(new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.beginTask(Messages.ESRView_reading_ESR, (int) (file.length() / 25));
						Result<List<ESRRecord>> result = esrf.read(file, monitor);
						if (result.isOK()) {
							boolean skipPaidAll = false;
							boolean bookPaidAll = false;
							for (ESRRecord rec : result.get()) {
								monitor.worked(1);
								if (rec.getRejectCode().equals(ESRRecord.REJECT.OK)) {
									if (rec.getTyp().equals(ESRRecord.MODE.Summenrecord)) {
										log.info(Messages.ESRView_ESR_finished + rec.getBetrag());
									} else if ((rec.getTyp().equals(ESRRecord.MODE.Storno_edv))
											|| (rec.getTyp().equals(ESRRecord.MODE.Storno_Schalter))) {
										Rechnung rn = rec.getRechnung();
										Money zahlung = rec.getBetrag().negate();
										Zahlung zahlungsObj = rn.addZahlung(zahlung,
												Messages.ESRView_storno_for + rn.getNr() + " / " //$NON-NLS-1$
														+ rec.getPatient().getPatCode(),
												new TimeTool(rec.getValuta()));
										if (zahlungsObj != null && ESR.getAccount() != null) {
											AccountTransaction transaction = zahlungsObj.getTransaction();
											transaction.setAccount(ESR.getAccount());
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
														Messages.ESRView_rechnung + rn.getNr()
																+ Messages.ESRView_ispaid,
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
											if (SWTHelper.askYesNo(Messages.ESRView_compulsoryExecution,
													Messages.ESRView_rechnung + rn.getNr()
															+ Messages.ESRView_isInCompulsoryExecution) == false) {
												continue;
											}
										}

										Money zahlung = rec.getBetrag();
										Money offen = rn.getOffenerBetrag();
										if (zahlung.isMoreThan(offen)
												&& (zahlung.doubleValue() - offen.doubleValue() > 0.03)) {
											if (SWTHelper.askYesNo(Messages.ESRView_toohigh, Messages.ESRView_paymentfor
													+ rn.getNr() + Messages.ESRView_morethan) == false) {
												continue;
											}
										}

										Zahlung zahlungsObj = rn.addZahlung(zahlung,
												Messages.ESRView_vesrfor + rn.getNr() + " / " //$NON-NLS-1$
														+ rec.getPatient().getPatCode(),
												new TimeTool(rec.getValuta()));
										if (zahlungsObj != null && ESR.getAccount() != null) {
											AccountTransaction transaction = zahlungsObj.getTransaction();
											transaction.setAccount(ESR.getAccount());
										}
										rec.setGebucht(null);
									}
								} else if (rec.getRejectCode().equals(ESRRecord.REJECT.RN_NUMMER)) {
									TimeTool valutaDate = new TimeTool(rec.getValuta());
									AccountTransaction transaction = new AccountTransaction(rec.getPatient(), null,
											rec.getBetrag(), valutaDate.toString(TimeTool.DATE_GER),
											Messages.LoadESRFileHandler_notAssignable);
									if (ESR.getAccount() != null) {
										transaction.setAccount(ESR.getAccount());
									}
								}
							}
							monitor.done();
							updateEsrView(event);
						} else {
							ResultAdapter.displayResult(result, Messages.ESRView_errorESR);
						}
					}

				});
			} catch (InvocationTargetException e) {
				ExHandler.handle(e);
				SWTHelper.showError(Messages.ESRView_errorESR2, Messages.ESRView_errrorESR2,
						Messages.ESRView_couldnotread + e.getMessage() + e.getCause().getMessage());
			} catch (InterruptedException e) {
				ExHandler.handle(e);
				SWTHelper.showError("ESR interrupted", Messages.ESRView_interrupted, e //$NON-NLS-1$
						.getMessage());
			}
		}

		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		element.setIcon(Images.IMG_IMPORT.getImageDescriptor());
		element.setTooltip(Messages.ESRView_read_ESR_explain);
	}

	private void updateEsrView(ExecutionEvent event) {
		UiDesk.asyncExec(new Runnable() {

			@Override
			public void run() {
				IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
				if (window != null) {
					IWorkbenchPage page = window.getActivePage();
					if (page != null) {
						ESRView view = (ESRView) page.findView(ESRView.ID);
						if (view != null) {
							view.updateView();
						}
					}
				}

			}
		});
	}

}
