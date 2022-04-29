/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.TarmedRechnung;

import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.elexis.tarmedprefs.PreferenceConstants;
import ch.elexis.views.RnPrintView2;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Result;

public class RechnungsDrucker implements IRnOutputter {
	// Mandant actMandant;
	TarmedACL ta = TarmedACL.getInstance();
	RnPrintView2 rnp;
	IWorkbenchPage rnPage;
	// IProgressMonitor monitor;
	private Button bESR, bForms, bIgnoreFaults, bSaveFileAs;
	String dirname = CoreHub.localCfg.get(PreferenceConstants.RNN_EXPORTDIR, null);
	Text tName;

	private boolean bESRSelected, bFormsSelected, bIgnoreFaultsSelected, bSaveFileAsSelected;

	private boolean modifyInvoiceState;

	public Result<Rechnung> doOutput(final IRnOutputter.TYPE type, final Collection<Rechnung> rechnungen,
			Properties props) {

		if (!props.isEmpty()) {
			initSelectedFromProperties(props);
		} else {
			modifyInvoiceState = true;
		}

		rnPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		final Result<Rechnung> res = new Result<Rechnung>();
		// ElexisEventCascade.getInstance().stop();
		try {
			rnp = (RnPrintView2) rnPage.showView(RnPrintView2.ID);
			progressService.runInUI(PlatformUI.getWorkbench().getProgressService(), new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor) {
					monitor.beginTask(Messages.RechnungsDrucker_PrintingBills, rechnungen.size() * 10);
					int errors = 0;
					for (Rechnung rn : rechnungen) {
						try {
							if (rnp.doPrint(rn, type,
									bSaveFileAsSelected ? dirname + File.separator + rn.getNr() + ".xml" : null, //$NON-NLS-1$
									bESRSelected, bFormsSelected, !bIgnoreFaultsSelected, monitor) == false) {
								String errms = Messages.RechnungsDrucker_TheBill + rn.getNr()
										+ Messages.RechnungsDrucker_Couldntbeprintef;
								res.add(Result.SEVERITY.ERROR, 1, errms, rn, true);
								errors++;
								continue;
							}
							if (modifyInvoiceState) {
								int status_vorher = rn.getStatus();
								if ((status_vorher == RnStatus.OFFEN) || (status_vorher == RnStatus.MAHNUNG_1)
										|| (status_vorher == RnStatus.MAHNUNG_2)
										|| (status_vorher == RnStatus.MAHNUNG_3)) {
									rn.setStatus(status_vorher + 1);
								}
								rn.addTrace(Rechnung.OUTPUT, getDescription() + ": " //$NON-NLS-1$
										+ RnStatus.getStatusText(rn.getStatus()));
							}
						} catch (Exception ex) {
							ExHandler.handle(ex);
							String msg = ex.getMessage();
							if (msg == null) {
								msg = Messages.RechnungsDrucker_MessageErrorInternal;
							}
							SWTHelper.showError(Messages.RechnungsDrucker_MessageErrorWhilePrinting + rn.getNr(), msg);
							errors++;
						}
					}
					monitor.done();
					if (errors == 0) {
						SWTHelper.showInfo(Messages.RechnungsDrucker_PrintingFinished,
								Messages.RechnungsDrucker_AllFinishedNoErrors);
					} else {
						SWTHelper.showError(Messages.RechnungsDrucker_ErrorsWhilePrinting,
								Integer.toString(errors) + Messages.RechnungsDrucker_ErrorsWhiilePrintingAdvice);
					}
				}
			}, null);

			rnPage.hideView(rnp);

		} catch (Exception ex) {
			ExHandler.handle(ex);
			res.add(Result.SEVERITY.ERROR, 2, ex.getMessage(), null, true);
			ErrorDialog.openError(null, Messages.RechnungsDrucker_ErrorsWhilePrinting,
					Messages.RechnungsDrucker_CouldntOpenPrintView, ResultAdapter.getResultAsStatus(res));
			return res;
		} finally {
			// ElexisEventCascade.getInstance().start();
		}
		return res;
	}

	private void initSelectedFromProperties(Properties props) {
		LoggerFactory.getLogger(getClass()).warn("Initializing with properties " + props.toString());
		modifyInvoiceState = true;
		if (props.get(IRnOutputter.PROP_OUTPUT_MODIFY_INVOICESTATE) instanceof String) {
			String value = (String) props.get(IRnOutputter.PROP_OUTPUT_MODIFY_INVOICESTATE);
			modifyInvoiceState = Boolean.parseBoolean(value);
		}
		if (props.get(IRnOutputter.PROP_OUTPUT_WITH_ESR) instanceof String) {
			String value = (String) props.get(IRnOutputter.PROP_OUTPUT_WITH_ESR);
			bESRSelected = Boolean.parseBoolean(value);
		}
		if (props.get(IRnOutputter.PROP_OUTPUT_WITH_RECLAIM) instanceof String) {
			String value = (String) props.get(IRnOutputter.PROP_OUTPUT_WITH_RECLAIM);
			bFormsSelected = Boolean.parseBoolean(value);
		}
	}

	public String getDescription() {
		return Messages.RechnungsDrucker_PrintAsTarmed;
	}

	@Override
	public Control createSettingsControl(final Object parent) {
		final Composite parentInc = (Composite) parent;
		Composite ret = new Composite(parentInc, SWT.NONE);
		ret.setLayout(new GridLayout());
		bESR = new Button(ret, SWT.CHECK);
		bForms = new Button(ret, SWT.CHECK);
		bESR.setText(Messages.RechnungsDrucker_WithESR);
		bESR.setSelection(true);
		bForms.setText(Messages.RechnungsDrucker_WithForm);
		bForms.setSelection(true);
		bIgnoreFaults = new Button(ret, SWT.CHECK);
		bIgnoreFaults.setText(Messages.RechnungsDrucker_IgnoreFaults);
		bIgnoreFaults.setSelection(CoreHub.localCfg.get(PreferenceConstants.RNN_RELAXED, true));
		bIgnoreFaults.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreHub.localCfg.set(PreferenceConstants.RNN_RELAXED, bIgnoreFaults.getSelection());
			}

		});
		Group cSaveCopy = new Group(ret, SWT.NONE);
		cSaveCopy.setText(Messages.RechnungsDrucker_FileForTrustCenter);
		cSaveCopy.setLayout(new GridLayout(2, false));
		bSaveFileAs = new Button(cSaveCopy, SWT.CHECK);
		bSaveFileAs.setText(Messages.RechnungsDrucker_AskSaveForTrustCenter);
		bSaveFileAs.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		bSaveFileAs.setSelection(CoreHub.localCfg.get(PreferenceConstants.RNN_SAVECOPY, false));
		bSaveFileAs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreHub.localCfg.set(PreferenceConstants.RNN_SAVECOPY, bSaveFileAs.getSelection());
			}

		});

		Button bSelectFile = new Button(cSaveCopy, SWT.PUSH);
		bSelectFile.setText(Messages.RechnungsDrucker_Directory);
		bSelectFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog ddlg = new DirectoryDialog(parentInc.getShell());
				dirname = ddlg.open();
				if (dirname == null) {
					SWTHelper.alert(Messages.RechnungsDrucker_DirNameMissingCaption,
							Messages.RechnungsDrucker_DirnameMissingText);
				} else {
					CoreHub.localCfg.set(PreferenceConstants.RNN_EXPORTDIR, dirname);
					tName.setText(dirname);
				}
			}
		});
		tName = new Text(cSaveCopy, SWT.BORDER | SWT.READ_ONLY);
		tName.setText(CoreHub.localCfg.get(PreferenceConstants.RNN_EXPORTDIR, StringUtils.EMPTY));
		return ret;
	}

	public boolean canStorno(final Rechnung rn) {
		// We do not need to react on cancel messages
		return false;
	}

	public boolean canBill(final Fall fall) {
		return true;
	}

	public void saveComposite() {
		bESRSelected = bESR.getSelection();
		bFormsSelected = bForms.getSelection();
		bIgnoreFaultsSelected = bIgnoreFaults.getSelection();
		bSaveFileAsSelected = bSaveFileAs.getSelection();
	}

}
