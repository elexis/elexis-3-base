package ch.elexis.pdfBills;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.mail.MailAccount;
import ch.elexis.core.mail.MailAccount.TYPE;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;
import ch.elexis.core.ui.e4.dialog.VirtualFilesystemUriEditorDialog;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.data.Mandant;
import ch.elexis.data.Query;
import ch.elexis.pdfBills.print.PrintProcess;
import ch.elexis.pdfBills.print.PrinterSettingsDialog;
import ch.elexis.pdfBills.print.ScriptInitializer;

public class PreferencePage extends org.eclipse.jface.preference.PreferencePage implements IWorkbenchPreferencePage {

	private TabFolder tabFolder;

	private TabItem t40Settings;
	private TabItem t44Settings;
	private TabItem mailConfig;
	private TabItem headerConfig;
	private TabItem outputConfig;
	private TabItem messageConfig;

	private Text headerLine1Text;
	private Text headerLine2Text;

	private Text reminderDays2Text;
	private Text reminderDays3Text;

	private Text mandantHeaderLine1Text;
	private Text mandantHeaderLine2Text;
	private Text mandantReminderDays2Text;
	private Text mandantReminderDays3Text;

	private Text pdfRnTextTP;
	private Text pdfRnTextTG;
	private Text pdfRnTextM1TG;
	private Text pdfRnTextM2TG;
	private Text pdfRnTextM3TG;
	private Text pdfRnTextM1TP;
	private Text pdfRnTextM2TP;
	private Text pdfRnTextM3TP;

	private Text printCommandText;

	private Label printerConfigLabel;
	private Label esrPrinterConfigLabel;

	private Button openDialogBtn;

	private Button openEsrDialogBtn;

	@Override
	public void init(IWorkbench workbench) {
		setTitle("PDF Rechnungsdruck");
		setDescription("Hier können die Einstellungen für den PDF Rechnungsdruck vorgenommen werden.");
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());

		tabFolder = new TabFolder(ret, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		t44Settings = new TabItem(tabFolder, SWT.NONE);
		t44Settings.setText("Tarmed 4.4 / 4.5"); //$NON-NLS-1$
		createSettings(t44Settings, "4.4"); //$NON-NLS-1$
		getSettings(t44Settings);

		t40Settings = new TabItem(tabFolder, SWT.NONE);
		t40Settings.setText("Tarmed 4.0"); //$NON-NLS-1$
		createSettings(t40Settings, "4.0"); //$NON-NLS-1$
		getSettings(t40Settings);

		mailConfig = new TabItem(tabFolder, SWT.NONE);
		mailConfig.setText("Kopie per Mail");
		createMailConfig(mailConfig);

		headerConfig = new TabItem(tabFolder, SWT.NONE);
		headerConfig.setText("Rechnungskopf");
		createHeaderConfig(headerConfig);

		outputConfig = new TabItem(tabFolder, SWT.NONE);
		outputConfig.setText("Ausgabe");
		createOutputConfig(outputConfig);

		messageConfig = new TabItem(tabFolder, SWT.NONE);
		messageConfig.setText("Rechnungstexte");
		createMessageConfig(messageConfig);

		final Composite printerConfigComposite = new Composite(ret, SWT.NONE);
		printerConfigComposite.setLayout(new GridLayout(2, false));
		printerConfigComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Button couvertRight = new Button(printerConfigComposite, SWT.CHECK);
		couvertRight.setText("Couvert-Fenster links");
		couvertRight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		couvertRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreHub.localCfg.set(RnOutputter.CFG_ESR_COUVERT_LEFT, couvertRight.getSelection());
			}
		});
		couvertRight.setSelection(CoreHub.localCfg.get(RnOutputter.CFG_ESR_COUVERT_LEFT, false));

		final Button doPrint = new Button(printerConfigComposite, SWT.CHECK);
		doPrint.setText("Direkter Druck");
		doPrint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		doPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreHub.localCfg.set(RnOutputter.CFG_PRINT_DIRECT, doPrint.getSelection());
				updatePrintDirect();
			}
		});
		doPrint.setSelection(CoreHub.localCfg.get(RnOutputter.CFG_PRINT_DIRECT, false));

		Label label = new Label(printerConfigComposite, SWT.NONE);
		label.setText("Drucker Konfiguration");
		openDialogBtn = new Button(printerConfigComposite, SWT.PUSH);
		openDialogBtn.setText("konfigurieren");
		openDialogBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		openDialogBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PrinterSettingsDialog dialog = new PrinterSettingsDialog(Display.getDefault().getActiveShell(),
						CoreHub.localCfg.get(RnOutputter.CFG_PRINT_PRINTER, StringUtils.EMPTY),
						CoreHub.localCfg.get(RnOutputter.CFG_PRINT_TRAY, StringUtils.EMPTY));
				if (dialog.open() == Dialog.OK) {
					CoreHub.localCfg.set(RnOutputter.CFG_PRINT_PRINTER, dialog.getPrinter());
					CoreHub.localCfg.set(RnOutputter.CFG_PRINT_TRAY, dialog.getMediaTray());
					printerConfigLabel.setText(getPrinterConfigText());
					printerConfigComposite.layout();
				}
			}
		});

		printerConfigLabel = new Label(printerConfigComposite, SWT.NONE);
		printerConfigLabel.setText(getPrinterConfigText());
		printerConfigLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		label = new Label(printerConfigComposite, SWT.NONE);
		label.setText("ESR Drucker Konfiguration");
		openEsrDialogBtn = new Button(printerConfigComposite, SWT.PUSH);
		openEsrDialogBtn.setText("konfigurieren");
		openEsrDialogBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		openEsrDialogBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PrinterSettingsDialog dialog = new PrinterSettingsDialog(Display.getDefault().getActiveShell(),
						CoreHub.localCfg.get(RnOutputter.CFG_ESR_PRINT_PRINTER, StringUtils.EMPTY),
						CoreHub.localCfg.get(RnOutputter.CFG_ESR_PRINT_TRAY, StringUtils.EMPTY));
				if (dialog.open() == Dialog.OK) {
					CoreHub.localCfg.set(RnOutputter.CFG_ESR_PRINT_PRINTER, dialog.getPrinter());
					CoreHub.localCfg.set(RnOutputter.CFG_ESR_PRINT_TRAY, dialog.getMediaTray());
					esrPrinterConfigLabel.setText(getEsrPrinterConfigText());
					printerConfigComposite.layout();
				}
			}
		});

		esrPrinterConfigLabel = new Label(printerConfigComposite, SWT.NONE);
		esrPrinterConfigLabel.setText(getEsrPrinterConfigText());
		esrPrinterConfigLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		label = new Label(printerConfigComposite, SWT.NONE);
		label.setText("\nFolgende Variablen können in den Befehlen verwendet werden.\nVariablen: "
				+ PrintProcess.getVariablesAsString() + "\nZ.B.: befehl.exe -p [filename]\n");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		label = new Label(printerConfigComposite, SWT.NONE);
		label.setText("Befehl");
		printCommandText = new Text(printerConfigComposite, SWT.BORDER);
		printCommandText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		printCommandText.setText(CoreHub.localCfg.get(RnOutputter.CFG_PRINT_COMMAND, StringUtils.EMPTY));

		if (CoreUtil.isWindows()) {
			final Button useScript = new Button(printerConfigComposite, SWT.CHECK);
			useScript.setText("Vordefinierte Scripts und Befehle verwenden.");
			if (CoreHub.localCfg.get(RnOutputter.CFG_PRINT_USE_SCRIPT, false)) {
				useScript.setSelection(true);
				printCommandText.setEditable(false);
			}
			useScript.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					CoreHub.localCfg.set(RnOutputter.CFG_PRINT_USE_SCRIPT, useScript.getSelection());
					if (useScript.getSelection() && StringUtils.isBlank(printCommandText.getText())) {
						Properties commandsProperties = ScriptInitializer
								.getPrintCommands("/rsc/script/win/printcommands.properties"); //$NON-NLS-1$
						if (commandsProperties != null && commandsProperties.get("printer") != null) { //$NON-NLS-1$
							printCommandText.setText((String) commandsProperties.get("printer")); //$NON-NLS-1$
						}
						printCommandText.setEditable(false);
					} else {
						printCommandText.setEditable(true);
					}
				}
			});
		}
		updatePrintDirect();

		label = new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Button useGuarantorPostalAddress = new Button(ret, SWT.CHECK);
		useGuarantorPostalAddress
				.setText("Postanschrift des Garanten verwenden (Kann zu Abweichungen zwischen XML und PDF führen)");
		useGuarantorPostalAddress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		useGuarantorPostalAddress.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreHub.localCfg.set(RnOutputter.CFG_PRINT_USEGUARANTORPOSTAL,
						useGuarantorPostalAddress.getSelection());
				updatePrintDirect();
			}
		});
		useGuarantorPostalAddress.setSelection(CoreHub.localCfg.get(RnOutputter.CFG_PRINT_USEGUARANTORPOSTAL, false));

		return ret;
	}

	private void createOutputConfig(TabItem item) {
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		final Button useGlobalConfig = new Button(composite, SWT.CHECK);
		useGlobalConfig.setText("Globale Ausgabe Verzeichnisse");
		useGlobalConfig.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Button bXML = new Button(composite, SWT.PUSH);
		bXML.setText("XML Verzeichnis");
		Text tXml = new Text(composite, SWT.BORDER);
		tXml.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		Button bPDF = new Button(composite, SWT.PUSH);
		bPDF.setText("PDF Verzeichnis");
		Text tPdf = new Text(composite, SWT.BORDER);
		tPdf.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		bXML.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IVirtualFilesystemService virtualFilesystemService = VirtualFilesystemServiceHolder.get();
				URI inputUri = null;
				try {
					String stringValue = tXml.getText();
					if (StringUtils.isNotBlank(stringValue)) {
						IVirtualFilesystemHandle fileHandle = virtualFilesystemService.of(stringValue);
						inputUri = fileHandle.toURL().toURI();
					}
				} catch (URISyntaxException | IOException ex) {
				}
				VirtualFilesystemUriEditorDialog dialog = new VirtualFilesystemUriEditorDialog(getShell(),
						virtualFilesystemService, inputUri);
				if (IDialogConstants.OK_ID == dialog.open()) {
					tXml.setText(dialog.getValue().toString());
				}
			}

		});
		bPDF.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IVirtualFilesystemService virtualFilesystemService = VirtualFilesystemServiceHolder.get();
				URI inputUri = null;
				try {
					String stringValue = tPdf.getText();
					if (StringUtils.isNotBlank(stringValue)) {
						IVirtualFilesystemHandle fileHandle = virtualFilesystemService.of(stringValue);
						inputUri = fileHandle.toURL().toURI();
					}
				} catch (URISyntaxException | IOException ex) {
				}
				VirtualFilesystemUriEditorDialog dialog = new VirtualFilesystemUriEditorDialog(getShell(),
						virtualFilesystemService, inputUri);
				if (IDialogConstants.OK_ID == dialog.open()) {
					tPdf.setText(dialog.getValue().toString());
				}
			}
		});
		tXml.setText(CoreHub.globalCfg.get(OutputterUtil.CFG_PRINT_GLOBALXMLDIR, StringUtils.EMPTY));
		tPdf.setText(CoreHub.globalCfg.get(OutputterUtil.CFG_PRINT_GLOBALPDFDIR, StringUtils.EMPTY));

		tXml.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				CoreHub.globalCfg.set(OutputterUtil.CFG_PRINT_GLOBALXMLDIR, tXml.getText());
			}
		});
		tPdf.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				CoreHub.globalCfg.set(OutputterUtil.CFG_PRINT_GLOBALPDFDIR, tPdf.getText());
			}
		});

		useGlobalConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreHub.localCfg.set(OutputterUtil.CFG_PRINT_GLOBALOUTPUTDIRS, useGlobalConfig.getSelection());
				if (useGlobalConfig.getSelection()) {
					bPDF.setEnabled(true);
					bXML.setEnabled(true);
					tPdf.setEnabled(true);
					tXml.setEnabled(true);
				} else {
					bPDF.setEnabled(false);
					bXML.setEnabled(false);
					tPdf.setEnabled(false);
					tXml.setEnabled(false);
				}
			}
		});
		useGlobalConfig.setSelection(CoreHub.localCfg.get(OutputterUtil.CFG_PRINT_GLOBALOUTPUTDIRS, true));

		item.setControl(composite);
	}

	private void createHeaderConfig(TabItem item) {
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Standard-Briefkopf, falls kein Mandanten-spezifischer konfiguriert wurde");
		label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		label = new Label(composite, SWT.NONE);
		label.setText("ESR 1ste Zeile");
		headerLine1Text = new Text(composite, SWT.BORDER);
		headerLine1Text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		headerLine1Text.setText(getConfigTransferGlobal(RnOutputter.CFG_ESR_HEADER_1, StringUtils.EMPTY));
		label = new Label(composite, SWT.NONE);
		label.setText("ESR 2te Zeile");
		headerLine2Text = new Text(composite, SWT.BORDER);
		headerLine2Text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		headerLine2Text.setText(getConfigTransferGlobal(RnOutputter.CFG_ESR_HEADER_2, StringUtils.EMPTY));

		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		label.setText("Mandanten-spezifischer Briefkopf");
		label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		label = new Label(composite, SWT.NONE);
		label.setText("Mandant");
		final ComboViewer mandantsCombo = new ComboViewer(composite, SWT.BORDER);
		mandantsCombo.setContentProvider(ArrayContentProvider.getInstance());
		mandantsCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Mandant) {
					return ((Mandant) element).getLabel();
				}
				return super.getText(element);
			}
		});
		Query<Mandant> query = new Query<Mandant>(Mandant.class);
		List<Mandant> input = query.execute();
		Collections.sort(input, new MandantComparator());
		mandantsCombo.setInput(input);
		mandantsCombo.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Mandant mandant = (Mandant) event.getStructuredSelection().getFirstElement();
				mandantHeaderLine1Text.setText(ConfigServiceHolder
						.getGlobal(RnOutputter.CFG_ESR_HEADER_1 + "/" + mandant.getId(), StringUtils.EMPTY)); //$NON-NLS-1$
				mandantHeaderLine2Text.setText(ConfigServiceHolder
						.getGlobal(RnOutputter.CFG_ESR_HEADER_2 + "/" + mandant.getId(), StringUtils.EMPTY)); //$NON-NLS-1$
			}
		});

		label = new Label(composite, SWT.NONE);
		label.setText("ESR 1ste Zeile");
		mandantHeaderLine1Text = new Text(composite, SWT.BORDER);
		mandantHeaderLine1Text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		mandantHeaderLine1Text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Mandant mandant = (Mandant) ((IStructuredSelection) mandantsCombo.getSelection()).getFirstElement();
				if (mandant != null) {
					ConfigServiceHolder.setGlobal(RnOutputter.CFG_ESR_HEADER_1 + "/" + mandant.getId(), //$NON-NLS-1$
							mandantHeaderLine1Text.getText());
				}
			}
		});
		label = new Label(composite, SWT.NONE);
		label.setText("ESR 2te Zeile");
		mandantHeaderLine2Text = new Text(composite, SWT.BORDER);
		mandantHeaderLine2Text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		mandantHeaderLine2Text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Mandant mandant = (Mandant) ((IStructuredSelection) mandantsCombo.getSelection()).getFirstElement();
				if (mandant != null) {
					ConfigServiceHolder.setGlobal(RnOutputter.CFG_ESR_HEADER_2 + "/" + mandant.getId(), //$NON-NLS-1$
							mandantHeaderLine2Text.getText());
				}
			}
		});

		item.setControl(composite);
	}

	public static String getConfigTransferGlobal(String key, String defaultValue) {
		String globalValue = ConfigServiceHolder.get().get(key, null);
		if (globalValue == null) {
			String localValue = CoreHub.localCfg.get(key, defaultValue);
			if (StringUtils.isNotBlank(localValue)) {
				ConfigServiceHolder.get().set(key, localValue);
				globalValue = localValue;
			}
		}
		return globalValue != null ? globalValue : defaultValue;
	}

	private void createMailConfig(TabItem item) {
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Konfiguration des mail accounts für die Übermittlung der Rechnungskopie");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Query<Mandant> query = new Query<Mandant>(Mandant.class);
		List<Mandant> mandants = query.execute();
		Collections.sort(mandants, new MandantComparator());
		for (final Mandant mandant : mandants) {
			if (mandant.isInactive()) {
				continue;
			}
			label = new Label(composite, SWT.NONE);
			label.setText("Mandant " + mandant.getLabel());

			ComboViewer accountsViewer = new ComboViewer(composite, SWT.BORDER);
			accountsViewer.setContentProvider(ArrayContentProvider.getInstance());
			accountsViewer.setLabelProvider(new LabelProvider());
			final List<String> manadantAccounts = getSendMailAccounts(mandant);
			accountsViewer.setInput(manadantAccounts);
			accountsViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			accountsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					Object selected = event.getStructuredSelection().getFirstElement();
					if (selected instanceof String && StringUtils.isNotBlank((String) selected)) {
						ConfigServiceHolder.setGlobal(
								RnOutputter.CFG_ROOT + RnOutputter.CFG_MAIL_MANDANT_ACCOUNT + "/" + mandant.getId(), //$NON-NLS-1$
								(String) selected);
					}
				}
			});
			String selectedAccount = ConfigServiceHolder.getGlobal(
					RnOutputter.CFG_ROOT + RnOutputter.CFG_MAIL_MANDANT_ACCOUNT + "/" + mandant.getId(), null); //$NON-NLS-1$
			if (StringUtils.isNotBlank(selectedAccount)) {
				accountsViewer.setSelection(new StructuredSelection(selectedAccount));
			}
		}

		item.setControl(composite);
	}

	private List<String> getSendMailAccounts(Mandant mandant) {
		List<String> ret = new ArrayList<String>();
		List<String> accounts = MailClientHolder.get().getAccounts();
		for (String accountId : accounts) {
			Optional<MailAccount> accountOptional = MailClientHolder.get().getAccount(accountId);
			if (accountOptional.isPresent()) {
				if (accountOptional.get().getType() == TYPE.SMTP) {
					ret.add(accountId);
				}
			}
		}
		return ret;
	}

	public Text createMessageTextBox(Composite composite, String msgText) {
		Text textBox = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		textBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.END));
		textBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textBox.setText(msgText);
		return textBox;
	}

	private void createMessageConfig(TabItem item) {
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.Billing_Cfg_MsgTXT + "\nTP = Tiers Payant  TG = Tiers Garant");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.Billing_Cfg_Msg + " TP:");

		pdfRnTextTP = createMessageTextBox(composite, Messages.BillingDefaultMsg);
		pdfRnTextTP.setText(getValueOrDefault(
				ConfigServiceHolder.getGlobal(RnOutputter.CFG_MSGTEXT_TP_M0, Messages.BillingDefaultMsg),
				Messages.BillingDefaultMsg));

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.Billing_Cfg_Msg + " TG:");
		pdfRnTextTG = createMessageTextBox(composite, Messages.BillingDefaultMsg);
		pdfRnTextTG.setText(getValueOrDefault(
				ConfigServiceHolder.getGlobal(RnOutputter.CFG_MSGTEXT_TG_M0, Messages.BillingDefaultMsg),
				Messages.BillingDefaultMsg));

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.Billing_Cfg_Msg + " " + Messages.Core_Invoice_Reminder_1 + " TP:");// Messages.Billing_Cfg_Msg_Invoicelvl
		
		pdfRnTextM1TP = createMessageTextBox(composite, Messages.BillingDefaultMsg_M1);
		pdfRnTextM1TP.setText(getValueOrDefault(
						ConfigServiceHolder.getGlobal(RnOutputter.CFG_MSGTEXT_TP_M1, Messages.BillingDefaultMsg_M1),
						Messages.BillingDefaultMsg_M1));

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.Billing_Cfg_Msg + " " + Messages.Core_Invoice_Reminder_2 + " TP:");

		pdfRnTextM2TP = createMessageTextBox(composite, Messages.BillingDefaultMsg_M2);
		pdfRnTextM2TP.setText(getValueOrDefault(
						ConfigServiceHolder.getGlobal(RnOutputter.CFG_MSGTEXT_TP_M2, Messages.BillingDefaultMsg_M2),
						Messages.BillingDefaultMsg_M2));

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.Billing_Cfg_Msg + " " + Messages.Core_Invoice_Reminder_3 + " TP:");
		pdfRnTextM3TP = createMessageTextBox(composite, Messages.BillingDefaultMsg_M3);
		pdfRnTextM3TP.setText(getValueOrDefault(
				ConfigServiceHolder.getGlobal(RnOutputter.CFG_MSGTEXT_TP_M3, Messages.BillingDefaultMsg_M3),
				Messages.BillingDefaultMsg_M3));

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.Billing_Cfg_Msg + " " + Messages.Core_Invoice_Reminder_1 + " TG:");
		pdfRnTextM1TG = createMessageTextBox(composite, Messages.BillingDefaultMsg_M1);
		pdfRnTextM1TG.setText(getValueOrDefault(
						ConfigServiceHolder.getGlobal(RnOutputter.CFG_MSGTEXT_TG_M1, Messages.BillingDefaultMsg_M1),
						Messages.BillingDefaultMsg_M1));

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.Billing_Cfg_Msg + " " + Messages.Core_Invoice_Reminder_2 + " TG:");
		pdfRnTextM2TG = createMessageTextBox(composite, Messages.BillingDefaultMsg_M2);
		pdfRnTextM2TG.setText(getValueOrDefault(
						ConfigServiceHolder.getGlobal(RnOutputter.CFG_MSGTEXT_TG_M2, Messages.BillingDefaultMsg_M2),
						Messages.BillingDefaultMsg_M2));

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.Billing_Cfg_Msg + " " + Messages.Core_Invoice_Reminder_3 + " TG:");
		pdfRnTextM3TG = createMessageTextBox(composite, Messages.BillingDefaultMsg_M3);
		pdfRnTextM3TG.setText(getValueOrDefault(
						ConfigServiceHolder.getGlobal(RnOutputter.CFG_MSGTEXT_TG_M3, Messages.BillingDefaultMsg_M3),
						Messages.BillingDefaultMsg_M3));

		item.setControl(composite);
	}

	private String getValueOrDefault(String value, String defaultValue) {
		return StringUtils.isEmpty(value) ? defaultValue : value;
	}

	private void updatePrintDirect() {
		openDialogBtn.setEnabled(CoreHub.localCfg.get(RnOutputter.CFG_PRINT_DIRECT, false));
		openEsrDialogBtn.setEnabled(CoreHub.localCfg.get(RnOutputter.CFG_PRINT_DIRECT, false));
		printCommandText.setEnabled(CoreHub.localCfg.get(RnOutputter.CFG_PRINT_DIRECT, false));
	}

	private String getEsrPrinterConfigText() {
		StringBuilder sb = new StringBuilder();
		if (!CoreHub.localCfg.get(RnOutputter.CFG_ESR_PRINT_PRINTER, StringUtils.EMPTY).isEmpty()) {
			sb.append(CoreHub.localCfg.get(RnOutputter.CFG_ESR_PRINT_PRINTER, StringUtils.EMPTY));
		} else {
			sb.append("Kein Drucker");
		}
		if (!CoreHub.localCfg.get(RnOutputter.CFG_ESR_PRINT_TRAY, StringUtils.EMPTY).isEmpty()) {
			sb.append(", Fach ").append(CoreHub.localCfg.get(RnOutputter.CFG_ESR_PRINT_TRAY, StringUtils.EMPTY));
		} else {
			sb.append(", Kein Fach");
		}
		return sb.toString();
	}

	private String getPrinterConfigText() {
		StringBuilder sb = new StringBuilder();
		if (!CoreHub.localCfg.get(RnOutputter.CFG_PRINT_PRINTER, StringUtils.EMPTY).isEmpty()) {
			sb.append(CoreHub.localCfg.get(RnOutputter.CFG_PRINT_PRINTER, StringUtils.EMPTY));
		} else {
			sb.append("Kein Drucker");
		}
		if (!CoreHub.localCfg.get(RnOutputter.CFG_PRINT_TRAY, StringUtils.EMPTY).isEmpty()) {
			sb.append(", Fach ").append(CoreHub.localCfg.get(RnOutputter.CFG_PRINT_TRAY, StringUtils.EMPTY));
		} else {
			sb.append(", Kein Fach");
		}
		return sb.toString();
	}

	@Override
	public boolean performOk() {
		saveSettings(t40Settings);
		saveSettings(t44Settings);
		saveSettings(messageConfig);
		return super.performOk();
	}

	private void saveSettings(TabItem item) {
		Composite composite = (Composite) item.getControl();
		for (Control control : composite.getChildren()) {
			if (control instanceof Text && control.getData() instanceof String) {
				Text text = (Text) control;
				String cfgKey = (String) control.getData();
				String value = text.getText();
				if (value != null && !value.isEmpty() && checkValue(value)) {
					CoreHub.localCfg.set(cfgKey, value);
				}
			}
		}
		ConfigServiceHolder.setGlobal(RnOutputter.CFG_ESR_HEADER_1, headerLine1Text.getText());
		ConfigServiceHolder.setGlobal(RnOutputter.CFG_ESR_HEADER_2, headerLine2Text.getText());

		CoreHub.localCfg.set(RnOutputter.CFG_PRINT_COMMAND, printCommandText.getText());

		ConfigServiceHolder.setGlobal(RnOutputter.CFG_MSGTEXT_TG_M0, pdfRnTextTG.getText());
		ConfigServiceHolder.setGlobal(RnOutputter.CFG_MSGTEXT_TP_M0, pdfRnTextTP.getText());
		ConfigServiceHolder.setGlobal(RnOutputter.CFG_MSGTEXT_TG_M1, pdfRnTextM1TG.getText());
		ConfigServiceHolder.setGlobal(RnOutputter.CFG_MSGTEXT_TG_M2, pdfRnTextM2TG.getText());
		ConfigServiceHolder.setGlobal(RnOutputter.CFG_MSGTEXT_TG_M3, pdfRnTextM3TG.getText());
		ConfigServiceHolder.setGlobal(RnOutputter.CFG_MSGTEXT_TP_M1, pdfRnTextM1TP.getText());
		ConfigServiceHolder.setGlobal(RnOutputter.CFG_MSGTEXT_TP_M2, pdfRnTextM2TP.getText());
		ConfigServiceHolder.setGlobal(RnOutputter.CFG_MSGTEXT_TP_M3, pdfRnTextM3TP.getText());

		CoreHub.localCfg.flush();
	}

	public boolean checkValue(String value) {
		try {
			Float.parseFloat(value);
			return true;
		} catch (NumberFormatException e) {
			// s is not numeric
			return false;
		}
	}

	private void getSettings(TabItem item) {
		Composite composite = (Composite) item.getControl();
		for (Control control : composite.getChildren()) {
			if (control instanceof Text && control.getData() instanceof String) {
				Text text = (Text) control;
				String cfgKey = (String) control.getData();
				text.setText(getSetting(cfgKey));
			}
		}
	}

	public static String getSetting(String cfgKey) {
		return CoreHub.localCfg.get(cfgKey, getDefault(cfgKey));
	}

	private static String getDefault(String cfgKey) {
		// first try old settings ... then default
		String oldKey = cfgKey.replaceFirst("4.4/", StringUtils.EMPTY).replaceFirst("4.0/", StringUtils.EMPTY); //$NON-NLS-1$ //$NON-NLS-2$
		String oldSetting = CoreHub.localCfg.get(oldKey, null);
		if (oldSetting != null) {
			return oldSetting;
		} else {
			if (cfgKey.endsWith(RnOutputter.CFG_MARGINLEFT)) {
				return "1.5"; //$NON-NLS-1$
			} else if (cfgKey.endsWith(RnOutputter.CFG_MARGINRIGHT)) {
				return "0.7"; //$NON-NLS-1$
			} else if (cfgKey.endsWith(RnOutputter.CFG_MARGINTOP)) {
				return "1"; //$NON-NLS-1$
			} else if (cfgKey.endsWith(RnOutputter.CFG_MARGINBOTTOM)) {
				return "1.5"; //$NON-NLS-1$
			} else if (cfgKey.endsWith(RnOutputter.CFG_BESR_MARGIN_VERTICAL)) {
				return "0.75"; //$NON-NLS-1$
			} else if (cfgKey.endsWith(RnOutputter.CFG_BESR_MARGIN_HORIZONTAL)) {
				return "0.0"; //$NON-NLS-1$
			}
		}
		return StringUtils.EMPTY;
	}

	private void createSettings(TabItem item, String version) {
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout());
		new Label(composite, 256).setText("Rand links [cm]");
		Text pdfLeftMargin = new Text(composite, 128);
		pdfLeftMargin.setLayoutData(SWTHelper.getFillGridData(2, true, 2, false));
		pdfLeftMargin.setData(RnOutputter.CFG_ROOT + version + "/" + RnOutputter.CFG_MARGINLEFT); //$NON-NLS-1$
		new Label(composite, 512).setText("Rand rechts [cm]");
		Text pdfRightMargin = new Text(composite, 128);
		pdfRightMargin.setLayoutData(SWTHelper.getFillGridData(2, true, 2, false));
		pdfRightMargin.setData(RnOutputter.CFG_ROOT + version + "/" + RnOutputter.CFG_MARGINRIGHT); //$NON-NLS-1$
		new Label(composite, 1024).setText("Rand oben [cm]");
		Text pdfTopMargin = new Text(composite, 128);
		pdfTopMargin.setLayoutData(SWTHelper.getFillGridData(2, true, 2, false));
		pdfTopMargin.setData(RnOutputter.CFG_ROOT + version + "/" + RnOutputter.CFG_MARGINTOP); //$NON-NLS-1$
		new Label(composite, 128).setText("Rand unten [cm]");
		Text pdfBottumMagin = new Text(composite, 128);
		pdfBottumMagin.setLayoutData(SWTHelper.getFillGridData(2, true, 2, false));
		pdfBottumMagin.setData(RnOutputter.CFG_ROOT + version + "/" + RnOutputter.CFG_MARGINBOTTOM); //$NON-NLS-1$
		new Label(composite, 128).setText("BESR Abstand zu Rand unten [cm]");
		Text pdfBesrMarginVertical = new Text(composite, 128);
		pdfBesrMarginVertical.setLayoutData(SWTHelper.getFillGridData(2, true, 2, false));
		pdfBesrMarginVertical.setData(RnOutputter.CFG_ROOT + version + "/" + RnOutputter.CFG_BESR_MARGIN_VERTICAL); //$NON-NLS-1$
		new Label(composite, 128).setText("BESR Abstand zu Rand rechts [cm]");
		Text pdfBesrMarginHorizontal = new Text(composite, 128);
		pdfBesrMarginHorizontal.setLayoutData(SWTHelper.getFillGridData(2, true, 2, false));
		pdfBesrMarginHorizontal.setData(RnOutputter.CFG_ROOT + version + "/" + RnOutputter.CFG_BESR_MARGIN_HORIZONTAL); //$NON-NLS-1$
		item.setControl(composite);
	}

	private class MandantComparator implements Comparator<Mandant> {
		@Override
		public int compare(Mandant l, Mandant r) {
			return l.getLabel().compareTo(r.getLabel());
		}
	}
}
