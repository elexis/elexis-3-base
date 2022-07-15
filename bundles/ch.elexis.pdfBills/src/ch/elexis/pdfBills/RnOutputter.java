package ch.elexis.pdfBills;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.progress.IProgressService;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.LoggerFactory;

import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.TarmedRechnung.XMLExporterUtil;
import ch.elexis.base.ch.arzttarife.xml.exporter.Tarmed45Exporter.EsrType;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.data.util.PlatformHelper;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Person;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Result;

public class RnOutputter implements IRnOutputter {
	public static final String PDFDIR = "pdfdir";
	public static final String PLUGIN_ID = "ch.elexis.pdfBills";
	public static final String XMLDIR = "xmldir";
	public static final String CFG_ROOT = "pdf-output/";
	public static final String CFG_MARGINLEFT = "margin.left";
	public static final String CFG_MARGINRIGHT = "margin.right";
	public static final String CFG_MARGINTOP = "margin.top";
	public static final String CFG_MARGINBOTTOM = "margin.bottom";
	public static final String CFG_BESR_MARGIN_VERTICAL = "margin.besr.vertical";
	public static final String CFG_BESR_MARGIN_HORIZONTAL = "margin.besr.horizontal";

	public static final String CFG_ESR_HEADER_1 = CFG_ROOT + "esr.header.line1";
	public static final String CFG_ESR_HEADER_2 = CFG_ROOT + "esr.header.line2";

	public static final String CFG_ESR_REMINDERDAYS_M2 = CFG_ROOT + "esr.reminderdays.m2";
	public static final String CFG_ESR_REMINDERDAYS_M3 = CFG_ROOT + "esr.reminderdays.m3";

	public static final String CFG_PRINT_DIRECT = CFG_ROOT + "print.direct";

	public static final String CFG_PRINT_PRINTER = CFG_ROOT + "print.printer";
	public static final String CFG_PRINT_TRAY = CFG_ROOT + "print.tray";

	public static final String CFG_ESR_PRINT_PRINTER = CFG_ROOT + "esr.print.printer";
	public static final String CFG_ESR_PRINT_TRAY = CFG_ROOT + "esr.print.tray";

	public static final String CFG_PRINT_COMMAND = CFG_ROOT + "print.command";
	public static final String CFG_PRINT_USE_SCRIPT = CFG_ROOT + "print.usescript";

	protected static final String CFG_PRINT_BESR = "print.besr";
	protected static final String CFG_PRINT_RF = "print.rf";

	protected static final String CFG_MAIL_CPY = "mail.copy";
	protected static final String CFG_MAIL_MANDANT_ACCOUNT = "mail.mandant.account";

	public static final String CFG_PRINT_USEGUARANTORPOSTAL = CFG_ROOT + "guarantor.postaladdress";

	private Text tXml;
	private Text tPdf;

	private Button bWithEsr;
	private Button bWithRf;

	private Button bCopyMail;

	private boolean modifyInvoiceState;

	@Override
	public String getDescription() {
		return "PDF Output";
	}

	@Override
	public Result<Rechnung> doOutput(final TYPE type, final Collection<Rechnung> rnn, Properties props) {

		if (!props.isEmpty()) {
			initSelectedFromProperties(props);
		} else {
			modifyInvoiceState = true;
		}

		IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		final Result<Rechnung> res = new Result<Rechnung>();
		final File rsc = new File(PlatformHelper.getBasePath(PLUGIN_ID), "rsc");
		final StringJoiner mailErrors = new StringJoiner("\n- ", "- ", "");
		try {
			progressService.runInUI(PlatformUI.getWorkbench().getProgressService(), new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor) {
					monitor.beginTask("Exportiere Rechnungen...", rnn.size() * 10);
					int errors = 0;
					for (Rechnung rn : rnn) {
						XMLExporter ex = new XMLExporter();
						ex.setEsrType(EsrType.esr9);
						Document dRn = ex.doExport(rn, null, type, true);
						monitor.worked(1);
						if (rn.getStatus() == RnStatus.FEHLERHAFT) {
							errors++;
							continue;
						}
						String fname = CoreHub.localCfg.get(CFG_ROOT + XMLDIR, "") + File.separator + rn.getNr()
								+ ".xml";
						try {
							FileOutputStream fout = new FileOutputStream(fname);
							OutputStreamWriter cout = new OutputStreamWriter(fout, "UTF-8");
							XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
							xout.output(dRn, cout);
							cout.close();
							fout.close();
							// create an new generator for the bill
							ElexisPDFGenerator epdf = new ElexisPDFGenerator(fname, rn.getNr(), rn.getInvoiceState());
							epdf.printBill(rsc);
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
							if (CoreHub.localCfg.get(CFG_ROOT + CFG_MAIL_CPY, false) && shouldSendCopyMail(rn)) {
								Kontakt guarantor = getGuarantor(rn);
								if (guarantor != null && StringUtils.isNotBlank(guarantor.getMailAddress())) {
									List<File> printed = epdf.getPrintedBill();
									if (!printed.isEmpty()) {
										String resultString = sendAsMail(guarantor, rn, printed);
										if (StringUtils.isNoneBlank(resultString)) {
											mailErrors.add(resultString);
										}
									}
								} else if (guarantor != null) {
									mailErrors.add("Keine mail Addresse für " + guarantor.getLabel(false));
								} else {
									mailErrors.add("Keine Garant für Rechnung " + rn.getNr());
								}
							}
						} catch (Exception e1) {
							ExHandler.handle(e1);
							SWTHelper.showError("Fehler beim Rechnungsdruck",
									"Konnte Datei " + fname + " nicht schreiben");
							rn.reject(RnStatus.REJECTCODE.INTERNAL_ERROR, "write error: " + fname);
							continue;
						}
						monitor.worked(1);
					}
					monitor.done();
					if (errors > 0) {
						SWTHelper.alert("Fehler bei der Übermittlung", Integer.toString(errors)
								+ " Rechnungen waren fehlerhaft. Sie können diese unter Rechnungen mit dem Status fehlerhaft aufsuchen und korrigieren");
					} else {
						SWTHelper.showInfo("Übermittlung beendet", "Es sind keine Fehler aufgetreten");
					}
				}
			}, null);
		} catch (Exception ex) {
			ExHandler.handle(ex);
			res.add(Result.SEVERITY.ERROR, 2, ex.getMessage(), null, true);
			ErrorDialog.openError(null, "Fehler bei der Ausgabe", "Konnte Rechnungsdruck nicht starten",
					ResultAdapter.getResultAsStatus(res));
			return res;
		}
		if (mailErrors.length() > 2) {
			MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Fehler beim Mail-Versand",
					null, "Beim Mail-Versand sind folgende Fehler aufgetreten:\n" + mailErrors.toString(),
					MessageDialog.ERROR, 0, new String[] { IDialogConstants.OK_LABEL, "als Text öffnen" }) {

				@Override
				protected void buttonPressed(int buttonId) {
					if (buttonId == 1) {
						try {
							Path tmpFile = Files.createTempFile("error_", "rechnung.txt");
							try (FileWriter fo = new FileWriter(tmpFile.toFile())) {
								fo.write(mailErrors.toString());
							}
							Program.launch(tmpFile.toString());
						} catch (IOException e) {
							LoggerFactory.getLogger(getClass()).error("Error writing tmp file", e);
						}
					}
					super.buttonPressed(buttonId);
				}
			};
			dialog.open();
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
			CoreHub.localCfg.set(CFG_ROOT + CFG_PRINT_BESR, Boolean.parseBoolean(value));
		}
		if (props.get(IRnOutputter.PROP_OUTPUT_WITH_RECLAIM) instanceof String) {
			String value = (String) props.get(IRnOutputter.PROP_OUTPUT_WITH_RECLAIM);
			CoreHub.localCfg.set(CFG_ROOT + CFG_PRINT_RF, Boolean.parseBoolean(value));
		}
		if (props.get(IRnOutputter.PROP_OUTPUT_WITH_MAIL) instanceof String) {
			String value = (String) props.get(IRnOutputter.PROP_OUTPUT_WITH_MAIL);
			CoreHub.localCfg.set(CFG_ROOT + CFG_MAIL_CPY, Boolean.parseBoolean(value));
		}
	}

	private Kontakt getGuarantor(Rechnung rn) {
		ICoverage coverage = CoreModelServiceHolder.get().load(rn.getFall().getId(), ICoverage.class).orElse(null);
		if (coverage != null) {
			IPatient patient = CoreModelServiceHolder.get().load(rn.getFall().getPatient().getId(), IPatient.class)
					.orElse(null);
			if (patient != null) {
				IContact ret = XMLExporterUtil.getGuarantor(XMLExporter.TIERS_PAYANT, patient, coverage);
				if (ret != null) {
					return Kontakt.load(ret.getId());
				}
			}
		}
		return null;
	}

	private boolean shouldSendCopyMail(Rechnung rn) {
		Kontakt guarantor = getGuarantor(rn);
		if (guarantor != null) {
			Fall fall = rn.getFall();
			return !fall.getInvoiceRecipient().equals(guarantor);
		}
		return false;
	}

	private String sendAsMail(Kontakt receiver, Rechnung rechnung, List<File> printed) {
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		try {
			String attachmentsString = getAttachmentsString(printed);
			Command sendMailCommand = commandService.getCommand("ch.elexis.core.mail.ui.sendMailNoUi");

			HashMap<String, String> params = new HashMap<String, String>();
			String accountid = ConfigServiceHolder.getGlobal(
					RnOutputter.CFG_ROOT + RnOutputter.CFG_MAIL_MANDANT_ACCOUNT + "/" + rechnung.getMandant().getId(),
					null);
			if (accountid != null) {
				params.put("ch.elexis.core.mail.ui.sendMailNoUi.accountid", accountid);
			}
			params.put("ch.elexis.core.mail.ui.sendMailNoUi.mandant", rechnung.getMandant().getId());
			params.put("ch.elexis.core.mail.ui.sendMailNoUi.to", receiver.getMailAddress());
			params.put("ch.elexis.core.mail.ui.sendMailNoUi.attachments", attachmentsString);
			params.put("ch.elexis.core.mail.ui.sendMailNoUi.subject", "Rechnungskopie vom " + rechnung.getDatumRn());
			params.put("ch.elexis.core.mail.ui.sendMailNoUi.text",
					"Anbei finden Sie eine Kopie der Rechnung vom " + rechnung.getDatumRn()
							+ " für Ihre Unterlagen.\n\nBeste Grüsse\n" + rechnung.getMandant().get(Person.TITLE) + " "
							+ rechnung.getMandant().getVorname() + " " + rechnung.getMandant().getName());

			ParameterizedCommand parametrizedCommmand = ParameterizedCommand.generateCommand(sendMailCommand, params);
			return (String) PlatformUI.getWorkbench().getService(IHandlerService.class)
					.executeCommand(parametrizedCommmand, null);
		} catch (Exception me) {
			throw new RuntimeException("ch.elexis.core.mail.ui.sendMailNoUi not found", me);
		}
	}

	private String getAttachmentsString(List<File> attachments) {
		StringBuilder sb = new StringBuilder();
		for (File file : attachments) {
			if (sb.length() > 0) {
				sb.append(":::");
			}
			sb.append(file.getAbsolutePath());
		}
		return sb.toString();
	}

	@Override
	public boolean canStorno(Rechnung rn) {
		return false;
	}

	@Override
	public boolean canBill(Fall fall) {
		return true;
	}

	@Override
	public Object createSettingsControl(Object parent) {
		final Composite compParent = (Composite) parent;
		Composite ret = new Composite(compParent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData());
		ret.setLayout(new GridLayout(2, false));
		bWithEsr = new Button(ret, SWT.CHECK);
		bWithEsr.setText("Mit ESR");
		bWithEsr.setSelection(CoreHub.localCfg.get(CFG_ROOT + CFG_PRINT_BESR, true));
		bWithEsr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreHub.localCfg.set(CFG_ROOT + CFG_PRINT_BESR, bWithEsr.getSelection());
			}
		});
		bWithEsr.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

		bWithRf = new Button(ret, SWT.CHECK);
		bWithRf.setText("Mit Rechnungsformular");
		bWithRf.setSelection(CoreHub.localCfg.get(CFG_ROOT + CFG_PRINT_RF, true));
		bWithRf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreHub.localCfg.set(CFG_ROOT + CFG_PRINT_RF, bWithRf.getSelection());
			}
		});
		bWithRf.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

		bCopyMail = new Button(ret, SWT.CHECK);
		bCopyMail.setText("Kopie als mail");
		bCopyMail.setSelection(false);
		bCopyMail.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		bCopyMail.setSelection(CoreHub.localCfg.get(CFG_ROOT + CFG_MAIL_CPY, false));
		bCopyMail.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreHub.localCfg.set(CFG_ROOT + CFG_MAIL_CPY, bCopyMail.getSelection());
			}
		});

		Button bXML = new Button(ret, SWT.PUSH);
		bXML.setText("XML Verzeichnis");
		tXml = new Text(ret, SWT.BORDER | SWT.READ_ONLY);
		tXml.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		Button bPDF = new Button(ret, SWT.PUSH);
		bPDF.setText("PDF Verzeichnis");
		tPdf = new Text(ret, SWT.BORDER | SWT.READ_ONLY);
		tPdf.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		bXML.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(compParent.getShell());
				String dir = dd.open();
				if (dir != null) {
					tXml.setText(dir);
				}
			}

		});
		bPDF.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(compParent.getShell());
				String dir = dd.open();
				if (dir != null) {
					tPdf.setText(dir);
				}
			}

		});
		tXml.setText(CoreHub.localCfg.get(CFG_ROOT + XMLDIR, ""));
		tPdf.setText(CoreHub.localCfg.get(CFG_ROOT + PDFDIR, ""));
		return (Control) ret;
	}

	@Override
	public void saveComposite() {
		CoreHub.localCfg.set(CFG_ROOT + CFG_PRINT_BESR, bWithEsr.getSelection());
		CoreHub.localCfg.set(CFG_ROOT + CFG_PRINT_RF, bWithRf.getSelection());
		CoreHub.localCfg.set(CFG_ROOT + XMLDIR, tXml.getText());
		CoreHub.localCfg.set(CFG_ROOT + PDFDIR, tPdf.getText());
		CoreHub.localCfg.flush();
	}
}
