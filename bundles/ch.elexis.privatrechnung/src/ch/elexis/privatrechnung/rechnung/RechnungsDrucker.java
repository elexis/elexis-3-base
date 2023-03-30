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

package ch.elexis.privatrechnung.rechnung;

import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.elexis.privatrechnung.data.PreferenceConstants;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Result;

public class RechnungsDrucker implements IRnOutputter {
	String templateESR, templateBill;

	/**
	 * We'll take all sorts of bills
	 */
	public boolean canBill(final Fall fall) {
		return true;
	}

	/**
	 * We never storno
	 */
	public boolean canStorno(final Rechnung rn) {
		return false;
	}

	/**
	 * Create the Control that will be presented to the user before selecting the
	 * bill output target. Here we simply chose a template to use for the bill. In
	 * fact we need two templates: a template for the page with summary and giro and
	 * a template for the other pages
	 */
	public Control createSettingsControl(final Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		new Label(ret, SWT.NONE).setText("Formatvorlage für Rechnung (ESR-Seite)");
		final Text tVorlageESR = new Text(ret, SWT.BORDER);
		tVorlageESR.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tVorlageESR.setText(ConfigServiceHolder.getGlobal(PreferenceConstants.cfgTemplateESR,
				PreferenceConstants.DEFAULT_TEMPLATE_ESR));
		tVorlageESR.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent ev) {
				templateESR = tVorlageESR.getText();
				ConfigServiceHolder.setGlobal(PreferenceConstants.cfgTemplateESR, templateESR);
			}
		});
		new Label(ret, SWT.NONE).setText("Formatvorlage für Rechnung (Folgeseiten)");
		final Text tVorlageRn = new Text(ret, SWT.BORDER);
		tVorlageRn.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tVorlageRn.setText(ConfigServiceHolder.getGlobal(PreferenceConstants.cfgTemplateBill,
				PreferenceConstants.DEFAULT_TEMPLATE_BILL));
		tVorlageRn.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent ev) {
				templateBill = tVorlageRn.getText();
				ConfigServiceHolder.setGlobal(PreferenceConstants.cfgTemplateBill, templateBill);
			}
		});
		tVorlageESR.setText(ConfigServiceHolder.getGlobal(PreferenceConstants.cfgTemplateESR,
				PreferenceConstants.DEFAULT_TEMPLATE_ESR));
		tVorlageRn.setText(ConfigServiceHolder.getGlobal(PreferenceConstants.cfgTemplateBill,
				PreferenceConstants.DEFAULT_TEMPLATE_BILL));
		return ret;
	}

	/**
	 * Print the bill(s)
	 */
	public Result<Rechnung> doOutput(final TYPE type, final Collection<Rechnung> rnn, Properties props) {
		IWorkbenchPage rnPage;
		final Result<Rechnung> result = new Result<Rechnung>(); // =new
		// Result<Rechnung>(Log.ERRORS,99,"Not
		// yet implemented",null,true);
		rnPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		final Result<Rechnung> res = new Result<Rechnung>();

		try {
			final RnPrintView rnp = (RnPrintView) rnPage.showView(RnPrintView.ID);
			progressService.runInUI(PlatformUI.getWorkbench().getProgressService(), new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor) {
					monitor.beginTask("Drucke Rechnungen", rnn.size() * 10);
					int errors = 0;
					for (Rechnung rn : rnn) {
						try {
							// select Rechnung before printing for correct placeholder replacement
							ElexisEventDispatcher.fireSelectionEvent(rn);
							result.add(rnp.doPrint(rn));
							monitor.worked(10);
							if (!result.isOK()) {
								String errms = "Rechnung " + rn.getNr() + "konnte nicht gedruckt werden";
								res.add(Result.SEVERITY.ERROR, 1, errms, rn, true);
								errors++;
								continue;
							}
							int status_vorher = rn.getStatus();
							if ((status_vorher == RnStatus.OFFEN) || (status_vorher == RnStatus.MAHNUNG_1)
									|| (status_vorher == RnStatus.MAHNUNG_2) || (status_vorher == RnStatus.MAHNUNG_3)) {
								rn.setStatus(status_vorher + 1);
							}
							rn.addTrace(Rechnung.OUTPUT,
									getDescription() + ": " + RnStatus.getStatusText(rn.getStatus()));
						} catch (Exception ex) {
							SWTHelper.showError("Fehler beim Drucken der Rechnung " + rn.getRnId(), ex.getMessage()); //$NON-NLS-1$
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

	public String getDescription() {
		return "Privatrechnung auf Drucker";
	}

	public void saveComposite() {
		// Nothing
	}

	@Override
	public Object createSettingsControl(Object parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
