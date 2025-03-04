package ch.elexis.covid.cert.ui.dialogs;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.LoggerFactory;

import ch.elexis.core.findings.ICoding;
import ch.elexis.core.findings.codes.IValueSetService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.covid.cert.service.CertificatesService;
import ch.elexis.covid.cert.service.rest.model.TestInfo;
import ch.elexis.covid.cert.service.rest.model.TestModel;
import jakarta.inject.Inject;

public class TestModelDialog extends Dialog {

	@Inject
	private IValueSetService valueSetService;

	private TestModel model;

	private ComboViewer typeCombo;

	private ComboViewer manufacturerCombo;

	private CDateTime sampleDateTime;

	private Text testingCenter;

	private ComboViewer countryCombo;

	private Text transferCode;

	public TestModelDialog(TestModel model, Shell shell) {
		super(shell);
		this.model = model;

		CoreUiUtil.injectServices(this);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Daten des Test");
		parent = (Composite) super.createDialogArea(parent);

		typeCombo = new ComboViewer(parent, SWT.BORDER);
		typeCombo.setContentProvider(ArrayContentProvider.getInstance());
		typeCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ICoding) {
					return ((ICoding) element).getDisplay();
				}
				return super.getText(element);
			}
		});
		List<ICoding> testsTypeValueSet = valueSetService.getValueSet("covid-19-test-type");
		typeCombo.setInput(testsTypeValueSet);
		typeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				String code = ((ICoding) typeCombo.getStructuredSelection().getFirstElement()).getCode();
				model.getTestInfo()[0].setTypeCode(code);
				if (typeCombo.getControl().getData("deco") != null) {
					removeErrorDecoration(typeCombo.getControl());
				}
				// disable manufacturer if pcr test
				if ("LP6464-4".equals(code)) {
					manufacturerCombo.getControl().setEnabled(false);
					model.getTestInfo()[0].setManufacturerCode(null);
				} else {
					model.getTestInfo()[0].setManufacturerCode(null);
					manufacturerCombo.setSelection(new StructuredSelection());
					manufacturerCombo.getControl().setEnabled(true);
				}
			}
		});
		typeCombo.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		typeCombo.getControl().setToolTipText("Test Typ");
		typeCombo.getCombo().setText("Test Typ");

		manufacturerCombo = new ComboViewer(parent, SWT.BORDER);
		manufacturerCombo.setContentProvider(ArrayContentProvider.getInstance());
		List<ICoding> testsValueSet = valueSetService.getValueSet("covid-19-lab-test-manufacturer");
		manufacturerCombo.setInput(testsValueSet);
		manufacturerCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ICoding) {
					return ((ICoding) element).getDisplay();
				}
				return super.getText(element);
			}
		});
		manufacturerCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getStructuredSelection() != null
						&& event.getStructuredSelection().getFirstElement() != null) {
					model.getTestInfo()[0].setManufacturerCode(
							((ICoding) event.getStructuredSelection().getFirstElement()).getCode());
					if (manufacturerCombo.getControl().getData("deco") != null) {
						removeErrorDecoration(manufacturerCombo.getControl());
					}
				}
			}
		});
		manufacturerCombo.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		manufacturerCombo.getControl().setToolTipText("Produkt");
		manufacturerCombo.getCombo().setText("Produkt");

		sampleDateTime = new CDateTime(parent, CDT.BORDER | CDT.DATE_MEDIUM | CDT.TIME_SHORT | CDT.DROP_DOWN);
		sampleDateTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Date now = new Date();
				if (sampleDateTime.getSelection() != null) {
					Date selection = sampleDateTime.getSelection();
					if (selection.before(now)) {
						ZonedDateTime zonedSelected = ZonedDateTime.ofInstant(selection.toInstant(),
								ZoneId.systemDefault());
						ZonedDateTime utcDateTime = zonedSelected.withZoneSameInstant(ZoneId.of("Z"));
						model.getTestInfo()[0].setSampleDateTime(TestInfo.formatter.format(utcDateTime));
						removeErrorDecoration(sampleDateTime);
					} else {
						addErrorDecoration(sampleDateTime);
					}
				}
			}
		});
		try {
			ZonedDateTime utcDateTime = LocalDateTime
					.parse(model.getTestInfo()[0].getSampleDateTime(), TestInfo.formatter).atZone(ZoneId.of("Z"));
			ZonedDateTime localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault());
			sampleDateTime.setSelection(Date.from(localDateTime.toInstant()));
		} catch (DateTimeParseException e) {
			LoggerFactory.getLogger(getClass())
					.warn("Could not parse date [" + model.getTestInfo()[0].getSampleDateTime() + "]");
		}
		sampleDateTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		sampleDateTime.setToolTipText("Datum der Probe");

		testingCenter = new Text(parent, SWT.BORDER);
		testingCenter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		testingCenter.setMessage("Test Ort Name (z.B. Praxis Name) max. 50 Zeichen");
		testingCenter.setToolTipText("Test Ort Name (z.B. Praxis Name) max. 50 Zeichen");
		testingCenter.setText(model.getTestInfo()[0].getTestingCentreOrFacility());
		testingCenter.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (StringUtils.isNotBlank(testingCenter.getText())) {
					model.getTestInfo()[0].setTestingCentreOrFacility(testingCenter.getText());
					if (testingCenter.getData("deco") != null) {
						removeErrorDecoration(testingCenter);
					}
				}
			}
		});

		countryCombo = new ComboViewer(parent, SWT.BORDER);
		countryCombo.setContentProvider(ArrayContentProvider.getInstance());
		countryCombo.setInput(valueSetService.getValueSet("country-alpha-2-de").stream().map(c -> c.getCode())
				.collect(Collectors.toList()));
		countryCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				model.getTestInfo()[0]
						.setMemberStateOfTest(((String) event.getStructuredSelection().getFirstElement()));
			}
		});
		countryCombo.setSelection(new StructuredSelection(model.getTestInfo()[0].getMemberStateOfTest()));
		countryCombo.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		countryCombo.getControl().setToolTipText("Land des Test");

		if (StringUtils.isBlank(model.getTestInfo()[0].getTypeCode())) {
			// init default values
			initDefaultAntigen(testsTypeValueSet, testsValueSet);
		} else {
			// hide type selection
			((GridData) typeCombo.getCombo().getLayoutData()).exclude = true;
			typeCombo.getCombo().setVisible(false);
			typeCombo.getCombo().getParent().layout();

			if ("LP6464-4".equals(model.getTestInfo()[0].getTypeCode())) {
				manufacturerCombo.getControl().setEnabled(false);
				model.getTestInfo()[0].setManufacturerCode(null);
				initDefaultPcr(testsTypeValueSet, testsValueSet);
			} else {
				model.getTestInfo()[0].setManufacturerCode(null);
				manufacturerCombo.setSelection(new StructuredSelection());
				manufacturerCombo.getControl().setEnabled(true);
				initDefaultAntigen(testsTypeValueSet, testsValueSet);
			}
		}

		transferCode = new Text(parent, SWT.BORDER);
		transferCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		transferCode.setTextLimit(9);
		transferCode.setMessage("Transfer Code");
		transferCode.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (transferCode.getText() != null) {
					// update to upper case
					if (!transferCode.getText().equals(transferCode.getText().toUpperCase())) {
						transferCode.setText(transferCode.getText().toUpperCase());
						transferCode.setSelection(transferCode.getText().length());
					}
					if (transferCode.getText().length() == 9) {
						model.setAppCode(transferCode.getText());
						return;
					}
				}
				model.setAppCode(null);
			}
		});

		manufacturerCombo.getControl().setFocus();
		return parent;
	}

	private void initDefaultPcr(List<ICoding> testsTypeValueSet, List<ICoding> testsValueSet) {
		testsTypeValueSet.stream().filter(c -> c.getCode().equals("LP6464-4")).findFirst()
				.ifPresent(c -> typeCombo.setSelection(new StructuredSelection(c)));
	}

	private void initDefaultAntigen(List<ICoding> testsTypeValueSet, List<ICoding> testsValueSet) {
		testsTypeValueSet.stream().filter(c -> c.getCode().equals("LP217198-3")).findFirst()
				.ifPresent(c -> typeCombo.setSelection(new StructuredSelection(c)));
		String defaultTestCode = ConfigServiceHolder.get().get(CertificatesService.CFG_DEFAULT_TESTPRODUCT, null);
		if (defaultTestCode != null) {
			testsValueSet.stream().filter(c -> c.getCode().equals(defaultTestCode)).findFirst()
					.ifPresent(c -> manufacturerCombo.setSelection(new StructuredSelection(c)));
		}
	}

	@Override
	protected void okPressed() {
		if (StringUtils.isEmpty(model.getTestInfo()[0].getTypeCode())) {
			addErrorDecoration(typeCombo.getControl());
			return;
		}
		try {
			if (model.getTestInfo()[0].getSampleDateTime() == null
					|| LocalDateTime.parse(model.getTestInfo()[0].getSampleDateTime(), TestInfo.formatter)
							.isAfter(LocalDateTime.now())) {
				addErrorDecoration(sampleDateTime);
				return;
			}
		} catch (DateTimeParseException e) {
			addErrorDecoration(sampleDateTime);
			return;
		}

		super.okPressed();
	}

	private void removeErrorDecoration(Control control) {
		if (control.getData("deco") != null) {
			((ControlDecoration) control.getData("deco")).hide();
			((ControlDecoration) control.getData("deco")).dispose();
			control.setData("deco", null);
		}
	}

	private void addErrorDecoration(Control control) {
		if (control.getData("deco") == null) {
			ControlDecoration deco = new ControlDecoration(control, SWT.TOP | SWT.LEFT);

			// set description and image
			deco.setDescriptionText("Fehlende oder fehlerhafte Eingabe");
			deco.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
					.getImage());
			// hide deco if not in focus
			deco.setShowOnlyOnFocus(false);
			deco.show();
			control.setData("deco", deco);
		}
	}
}
