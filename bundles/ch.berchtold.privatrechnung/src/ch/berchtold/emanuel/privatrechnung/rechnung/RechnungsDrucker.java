/*******************************************************************************
 * Copyright (c) 2007-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.berchtold.emanuel.privatrechnung.rechnung;

import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Result;

public class RechnungsDrucker implements IRnOutputter {
	private Button bFirst, bSecond, bThird;
	private boolean bSummary, bDetail, bReclaim;

	/**
	 * We'll take all sorts of bills
	 */
	@Override
	public boolean canBill(final Fall fall) {
		return true;
	}

	/**
	 * We never storno
	 */
	@Override
	public boolean canStorno(final Rechnung rn) {
		return false;
	}

	/**
	 * Create the Control that will be presented to the user before selecting the
	 * bill output target. Here we simply chose a template to use for the bill. In
	 * fact we need two templates: a template for the page with summary and giro and
	 * a template for the other pages
	 */
	@Override
	public Object createSettingsControl(Object parent) {
		Composite compParent = (Composite) parent;
		Composite ret = new Composite(compParent, SWT.NONE);
		ret.setLayout(new GridLayout());
		bFirst = new Button(ret, SWT.CHECK);
		bFirst.setText("Zusammenfassung");
		bSecond = new Button(ret, SWT.CHECK);
		bSecond.setText("Detail");
		bThird = new Button(ret, SWT.CHECK);
		bThird.setText("Rückforderungsbeleg");
		bFirst.setSelection(true);
		bSecond.setSelection(true);
		bThird.setSelection(true);
		return ret;
	}

	/**
	 * Print the bill(s)
	 */
	@Override
	public Result<Rechnung> doOutput(final TYPE type, final Collection<Rechnung> rnn, final Properties props) {
		IWorkbenchPage rnPage;
		final Result<Rechnung> result = new Result<Rechnung>(); // =new
		// Result<Rechnung>(Log.ERRORS,99,"Not
		// yet implemented",null,true);
		rnPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		final Result<Rechnung> res = new Result<Rechnung>();
		props.setProperty("Summary", Boolean.toString(bSummary));
		props.setProperty("Detail", Boolean.toString(bDetail));
		props.setProperty(IRnOutputter.PROP_OUTPUT_WITH_RECLAIM, Boolean.toString(bReclaim));
		try {
			final RnPrintView rnp = (RnPrintView) rnPage.showView(RnPrintView.ID);
			progressService.runInUI(PlatformUI.getWorkbench().getProgressService(), new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor) {
					monitor.beginTask("Drucke Rechnungen", rnn.size() * 10);
					int errors = 0;
					for (Rechnung rn : rnn) {
						try {
							result.add(rnp.doPrint(rn, props));
							monitor.worked(10);
							if (!result.isOK()) {
								String errms = "Rechnung " + rn.getNr() + "konnte nicht gedruckt werden";
								res.add(Result.SEVERITY.ERROR, 1, errms, rn, true);
								errors++;
								continue;
							}
							InvoiceState status_vorher = rn.getInvoiceState();
							if ((status_vorher == InvoiceState.OPEN) || (status_vorher == InvoiceState.DEMAND_NOTE_1)
									|| (status_vorher == InvoiceState.DEMAND_NOTE_2)
									|| (status_vorher == InvoiceState.DEMAND_NOTE_3)) {
								rn.setStatus(InvoiceState.fromState(status_vorher.getState() + 1));
							}
							rn.addTrace(Rechnung.OUTPUT,
									getDescription() + ": " + rn.getInvoiceState().getLocaleText());
						} catch (Exception ex) {
							LoggerFactory.getLogger(getClass()).error("Error printing", ex);
							SWTHelper.showError("Fehler beim Drucken der Rechnung " + rn.getRnId(), ex.getMessage());
							errors++;
						}
					}
					monitor.done();
					if (errors == 0) {
						SWTHelper.showInfo("OK", "OK");
					} else {
						SWTHelper.showError("Fehler", "Fehler");
					}
				}
			}, null);

			rnPage.hideView(rnp);

		} catch (Exception ex) {
			ExHandler.handle(ex);
			res.add(Result.SEVERITY.ERROR, 2, ex.getMessage(), null, true);
			ErrorDialog.openError(null, "Exception", "Exception", ResultAdapter.getResultAsStatus(res));
			return res;
		}
		if (!result.isOK()) {
			ResultAdapter.displayResult(result, "Fehler beim Rechnungsdruck");
		}
		return result;
	}

	@Override
	public String getDescription() {
		return "Privatrechnung B. auf Drucker";
	}

	@Override
	public void saveComposite() {
		bSummary = bFirst.getSelection();
		bDetail = bSecond.getSelection();
		bReclaim = bThird.getSelection();
	}
}
