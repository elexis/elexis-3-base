package at.medevit.elexis.aerztekasse.ui.outputter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.jdom2.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.aerztekasse.core.IAerztekasseService;
import at.medevit.elexis.aerztekasse.ui.preferences.AerztekassePreferences;
import ch.elexis.TarmedRechnung.Messages;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.TarmedRechnung.XMLFileUtil;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.services.ICoverageService.Tiers;
import ch.elexis.core.services.LocalConfigService;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.rechnung.RnOutputDialog;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Result;

public class AerztekasseOutputter extends XMLExporter {
	private static Logger logger = LoggerFactory.getLogger(AerztekasseOutputter.class);

	private String outputDir;

	@Inject
	private IAerztekasseService service;

	public AerztekasseOutputter() {
		CoreUiUtil.injectServices(this);
	}

	@Override
	public String getDescription() {
		return ch.elexis.core.l10n.Messages.InvoiceOutputter_TransmisionAK;
	}

	@Override
	public Result<Rechnung> doOutput(final TYPE type, Collection<Rechnung> rnn, Properties props) {
		final Result<Rechnung> result = new Result<Rechnung>();

		if (!hasCredentialsSet()) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					ch.elexis.core.l10n.Messages.MissingSettingsDlg_Title,
					"Es sind nicht alle notwendgen Einstellungen gesetzt um Ärztekasse ausf\u00FChren zu k\u00F6nnen.\n"
							+ "Bitte unter Datei > Einstellungen > Abrechnungssystem > Ärztekasse einstellen.");

			result.add(Result.SEVERITY.ERROR, 1, ch.elexis.core.l10n.Messages.Outputter_NoCredentialsSet, null, true);
			return result;
		}

		try {
			IProgressService progService = PlatformUI.getWorkbench().getProgressService();
			progService.runInUI(PlatformUI.getWorkbench().getProgressService(), new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(ch.elexis.core.l10n.Messages.Invoices_export, rnn.size());
					for (Rechnung rn : rnn) {
						IInvoice invoice = CoreModelServiceHolder.get().load(rn.getId(), IInvoice.class).get();

						// if tiers TP do not print at intermediate
						if (CoverageServiceHolder.get().getTiersType(invoice.getCoverage()) == Tiers.PAYANT) {
							setPrintAtIntermediate(false);
						}
						Document doc = doExport(invoice, null, type, true);
						if (doc == null) {
							logger.error("Error exporting invoice #" + invoice.getNumber());
							result.add(Result.SEVERITY.ERROR, 1, "Fehler beim Export der Rechnung Nr.: " + rn.getNr(),
									rn, true);
						}

						// record invoice error
						if (invoice.getState() == InvoiceState.DEFECTIVE) {
							logger.error("Error in invoice #" + invoice.getNumber());
							result.add(Result.SEVERITY.ERROR, 1, "Fehler beim Export der Rechnung Nr.: " + rn.getNr(),
									rn, true);
						} else {
							try {
								Optional<Document> updatedDocument = TarmedXmlUtil.updateAerztekasseInfo(invoice, doc);
								if (updatedDocument.isPresent()) {
									doc = updatedDocument.get();
									setExistingXml(invoice, doc);
									writeToOutputDirectory(invoice, doc);
									updateInvoiceState(invoice);
								}
							} catch (Exception e) {
								logger.error("Error in invoice #" + invoice.getNumber(), e);
								result.add(Result.SEVERITY.ERROR, 1,
										"Fehler beim Export der Rechnung Nr.: " + rn.getNr(), rn, true);
							}
						}
						monitor.worked(1);

						if (monitor.isCanceled()) {
							break;
						}
					}
					monitor.done();
				}
			}, null);

			Result<Object> sendResult = service.sendFiles(new File(outputDir));
			if (!sendResult.isOK()) {
				logger.error("Error occured during aerztekasse transfer");
				Display.getDefault().syncExec(() -> {
					ErrorDialog.openError(Display.getDefault().getActiveShell(), "Fehler bei der Ausgabe",
							"Konnte Rechnungen nicht übermitteln", ResultAdapter.getResultAsStatus(sendResult));
				});
			}
		} catch (InvocationTargetException | InterruptedException e) {
			logger.error("aerztekasse invoice transfer aborted");
			ExHandler.handle(e);
			result.add(Result.SEVERITY.ERROR, 2, e.getMessage(), null, true);
		} catch (Exception ex) {
			logger.error("aerztekasse invoice delivery failed");
			ExHandler.handle(ex);
			result.add(Result.SEVERITY.ERROR, 2, ex.getMessage(), null, true);
		}

		return result;
	}

	private void writeToOutputDirectory(IInvoice invoice, Document document) {
		XMLFileUtil.getFileName(invoice, outputDir).ifPresent(filename -> {
			XMLFileUtil.writeToFile(filename, document);
		});
	}

	@Override
	public Control createSettingsControl(Object parent) {
		final Composite parentInc = (Composite) parent;
		return createSettingsControl(parentInc);
	}

	public Control createSettingsControl(Composite parent) {
		final Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(2, false));
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Label lblOutput = new Label(ret, SWT.NONE);
		lblOutput.setText(ch.elexis.core.l10n.Messages.Outputter_InvoiceOutputDirectory);
		lblOutput.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

		Button b = new Button(ret, SWT.PUSH);
		b.setText(Messages.XMLExporter_Change);
		GridData buttonData = new GridData();
		buttonData.widthHint = 75;
		b.setLayoutData(buttonData);
		final Text text = new Text(ret, SWT.READ_ONLY | SWT.BORDER);
		text.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				outputDir = new DirectoryDialog(parent.getShell(), SWT.OPEN).open();
				LocalConfigService.set(AerztekassePreferences.CFG_OUTPUTDIR, outputDir);
				text.setText(outputDir);
			}
		});
		outputDir = LocalConfigService.get(AerztekassePreferences.CFG_OUTPUTDIR, CoreUtil.getDefaultDBPath());
		text.setText(outputDir);

		return ret;
	}

	private boolean hasCredentialsSet() {
		return service.hasClientId() && service.hasCredentials();
	}

	@Override
	public void openOutput(IInvoice invoice, LocalDateTime timestamp, InvoiceState invoiceState) {
		String archiveDir = service.getGlobalArchiveDir().orElse(StringUtils.EMPTY);
		XMLFileUtil.lookupFile(archiveDir, invoice, timestamp, invoiceState).ifPresent(xmlFile -> {
			if (xmlFile.exists()) {
				Program.launch(xmlFile.getAbsolutePath());
			} else {
				LoggerFactory.getLogger(getClass()).info("File [" + xmlFile.getAbsolutePath() + "] does not exist"); //$NON-NLS-1$
			}
		});
	}

	private void updateInvoiceState(IInvoice invoice) {
		InvoiceState previousState = invoice.getState();
		if ((previousState == InvoiceState.OPEN) || (previousState == InvoiceState.DEMAND_NOTE_1)
				|| (previousState == InvoiceState.DEMAND_NOTE_2) || (previousState == InvoiceState.DEMAND_NOTE_3)) {
			InvoiceState newState = InvoiceState.fromState(previousState.numericValue() + 1);
			invoice.setState(newState);
		}
		invoice.addTrace(Rechnung.OUTPUT, getDescription() + ": " //$NON-NLS-1$
				+ invoice.getState().getLocaleText());
		CoreModelServiceHolder.get().save(invoice);
	}

	@Override
	public void customizeDialog(Object rnOutputDialog) {
		if (rnOutputDialog instanceof RnOutputDialog) {
			((RnOutputDialog) rnOutputDialog).setOkButtonText("Senden");
		}
	}
}
