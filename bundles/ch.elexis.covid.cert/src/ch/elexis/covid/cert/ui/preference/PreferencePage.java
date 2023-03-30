package ch.elexis.covid.cert.ui.preference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.SelectionDialog;

import ch.elexis.core.findings.ICoding;
import ch.elexis.core.findings.codes.IValueSetService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.covid.cert.service.CertificatesService;
import ch.elexis.covid.cert.service.CertificatesService.Mode;
import ch.elexis.covid.cert.ui.handler.CovidHandlerUtil;
import ch.elexis.data.Leistungsblock;

public class PreferencePage extends org.eclipse.jface.preference.PreferencePage implements IWorkbenchPreferencePage {

	private ComboViewer modeCombo;

	private Text testingCenter;

	private Text otpText;

	private ComboViewer defaultVaccinationCombo;

	private ComboViewer defaultTestCombo;

	@Inject
	private CertificatesService service;

	@Inject
	private IValueSetService valueSetService;

	private Label textLabel;

	private Button bAutomaticBilling;

	public PreferencePage() {
		CoreUiUtil.injectServices(this);
	}

	public PreferencePage(String title) {
		super(title);
	}

	public PreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected Control createContents(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());

		modeCombo = new ComboViewer(ret, SWT.BORDER);
		modeCombo.setContentProvider(ArrayContentProvider.getInstance());
		modeCombo.setLabelProvider(new LabelProvider());
		modeCombo.setInput(CertificatesService.Mode.values());
		modeCombo.setSelection(new StructuredSelection(service.getMode()));
		modeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				service.setMode((Mode) event.getStructuredSelection().getFirstElement());
			}
		});

		defaultVaccinationCombo = new ComboViewer(ret, SWT.BORDER);
		defaultVaccinationCombo.setContentProvider(ArrayContentProvider.getInstance());
		List<ICoding> vaccinationValueSet = valueSetService.getValueSet("vaccines-covid-19-names");
		defaultVaccinationCombo.setInput(vaccinationValueSet);
		defaultVaccinationCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ICoding) {
					return ((ICoding) element).getDisplay();
				}
				return super.getText(element);
			}
		});
		defaultVaccinationCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getStructuredSelection() != null && !event.getStructuredSelection().isEmpty()) {
					ICoding selected = (ICoding) event.getStructuredSelection().getFirstElement();
					ConfigServiceHolder.get().set(CertificatesService.CFG_DEFAULT_VACCPRODUCT, selected.getCode());
				}
			}
		});
		defaultVaccinationCombo.getControl().setToolTipText("Vorauswahl Impf-Produkt");
		defaultVaccinationCombo.getCombo().setText("Vorauswahl Impf-Produkt");
		String defaultVaccCode = ConfigServiceHolder.get().get(CertificatesService.CFG_DEFAULT_VACCPRODUCT, null);
		if (defaultVaccCode != null) {
			vaccinationValueSet.stream().filter(c -> c.getCode().equals(defaultVaccCode)).findFirst()
					.ifPresent(c -> defaultVaccinationCombo.setSelection(new StructuredSelection(c)));
		}

		defaultTestCombo = new ComboViewer(ret, SWT.BORDER);
		defaultTestCombo.setContentProvider(ArrayContentProvider.getInstance());
		List<ICoding> testsValueSet = valueSetService.getValueSet("covid-19-lab-test-manufacturer");
		defaultTestCombo.setInput(testsValueSet);
		defaultTestCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ICoding) {
					return ((ICoding) element).getDisplay();
				}
				return super.getText(element);
			}
		});
		defaultTestCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getStructuredSelection() != null && !event.getStructuredSelection().isEmpty()) {
					ICoding selected = (ICoding) event.getStructuredSelection().getFirstElement();
					ConfigServiceHolder.get().set(CertificatesService.CFG_DEFAULT_TESTPRODUCT, selected.getCode());
				}
			}
		});
		defaultTestCombo.getControl().setToolTipText("Vorauswahl Test-Produkt");
		defaultTestCombo.getCombo().setText("Vorauswahl Test-Produkt");
		String defaultTestCode = ConfigServiceHolder.get().get(CertificatesService.CFG_DEFAULT_TESTPRODUCT, null);
		if (defaultTestCode != null) {
			testsValueSet.stream().filter(c -> c.getCode().equals(defaultTestCode)).findFirst()
					.ifPresent(c -> defaultTestCombo.setSelection(new StructuredSelection(c)));
		}

		testingCenter = new Text(ret, SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		testingCenter.setLayoutData(gd);
		testingCenter.setMessage("Test Ort Name (z.B. Praxis Name) max. 50 Zeichen");
		testingCenter.setToolTipText("Test Ort Name (z.B. Praxis Name) max. 50 Zeichen");
		testingCenter.setText(ConfigServiceHolder.get().get(CertificatesService.CFG_TESTCENTERNAME, StringUtils.EMPTY));
		testingCenter.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				ConfigServiceHolder.get().set(CertificatesService.CFG_TESTCENTERNAME, testingCenter.getText());

			}
		});

		Label lbl = new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		bAutomaticBilling = new Button(ret, SWT.CHECK);
		bAutomaticBilling.setText("Automatische Verrechnung von Tests");
		bAutomaticBilling.setSelection(ConfigServiceHolder.get().get(CovidHandlerUtil.CFG_AUTO_BILLING, false));
		bAutomaticBilling.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigServiceHolder.get().set(CovidHandlerUtil.CFG_AUTO_BILLING, bAutomaticBilling.getSelection());
			}
		});

		createBillingBlock(CovidHandlerUtil.CFG_KK_BLOCKID, ret);
		createBillingBlock(CovidHandlerUtil.CFG_KK_PCR_BLOCKID, ret);
		createBillingBlock(CovidHandlerUtil.CFG_SZ_BLOCKID, ret);
		createBillingBlock(CovidHandlerUtil.CFG_SZ_PCR_BLOCKID, ret);

		lbl = new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		textLabel = new Label(ret, SWT.NONE);

		otpText = new Text(ret, SWT.MULTI | SWT.BORDER | SWT.WRAP);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 400;
		otpText.setLayoutData(gd);
		otpText.setText(ConfigServiceHolder.get().getActiveMandator(CertificatesService.CFG_OTP, StringUtils.EMPTY));
		otpText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				ConfigServiceHolder.get().setActiveMandator(CertificatesService.CFG_OTP, otpText.getText());
				ConfigServiceHolder.get().setActiveMandator(CertificatesService.CFG_OTP_TIMESTAMP,
						LocalDateTime.now().toString());
			}
		});
		updateTextLabel();
		return ret;
	}

	private void createBillingBlock(String cfg, Composite ret) {
		Composite billingBlockComposite = new Composite(ret, SWT.NONE);
		billingBlockComposite.setLayout(new RowLayout());
		Text tAutomaticBillingBlock = new Text(billingBlockComposite, SWT.BORDER | SWT.READ_ONLY);
		tAutomaticBillingBlock.setLayoutData(new RowData(250, SWT.DEFAULT));
		tAutomaticBillingBlock.setTextLimit(80);
		String text = getText(cfg);
		tAutomaticBillingBlock.setMessage(text);
		tAutomaticBillingBlock.setToolTipText(text);
		Button blockCodeSelection = new Button(billingBlockComposite, SWT.PUSH);
		blockCodeSelection.setText("..."); //$NON-NLS-1$
		blockCodeSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectionDialog dialog = CodeSelectorFactory.getSelectionDialog("Block", getShell(), //$NON-NLS-1$
						"ignoreErrors");
				if (dialog.open() == SelectionDialog.OK) {
					if (dialog.getResult() != null && dialog.getResult().length > 0) {
						Leistungsblock block = (Leistungsblock) dialog.getResult()[0];
						tAutomaticBillingBlock.setText(block.getLabel());
						ConfigServiceHolder.get().set(cfg, block.getId());
					} else {
						ConfigServiceHolder.get().set(cfg, null);
						tAutomaticBillingBlock.setText(StringUtils.EMPTY);
					}
				}
			}
		});
		if (ConfigServiceHolder.get().get(cfg, null) != null) {
			tAutomaticBillingBlock.setText(Leistungsblock.load(ConfigServiceHolder.get().get(cfg, null)).getLabel());
		}
	}

	private String getText(String cfg) {
		if (cfg.equals(CovidHandlerUtil.CFG_KK_BLOCKID)) {
			return "Block für Krankenkasse Antigen";
		} else if (cfg.equals(CovidHandlerUtil.CFG_KK_PCR_BLOCKID)) {
			return "Block für Krankenkasse PCR";
		} else if (cfg.equals(CovidHandlerUtil.CFG_SZ_BLOCKID)) {
			return "Block für Selbstzahler Antigen";
		} else if (cfg.equals(CovidHandlerUtil.CFG_SZ_PCR_BLOCKID)) {
			return "Block für Selbstzahler PCR";
		}
		return "?";
	}

	private void updateTextLabel() {
		String timeStampString = ConfigServiceHolder.get().getActiveMandator(CertificatesService.CFG_OTP_TIMESTAMP,
				StringUtils.EMPTY);
		if (StringUtils.isNotBlank(timeStampString)) {
			LocalDateTime timeStamp = LocalDateTime.parse(timeStampString);
			textLabel.setText(
					"OTP des Mandanten von " + DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(timeStamp));
		} else {
			textLabel.setText("OTP des Mandanten");
		}
	}

}
