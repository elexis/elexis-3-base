package ch.elexis.covid.cert.ui.dialogs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.LoggerFactory;

import ch.elexis.core.findings.ICoding;
import ch.elexis.core.findings.codes.IValueSetService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.covid.cert.service.CertificatesService;
import ch.elexis.covid.cert.service.rest.model.VaccinationModel;

public class VaccinationModelDialog extends Dialog {

	@Inject
	private IValueSetService valueSetService;

	private VaccinationModel model;

	private ComboViewer languageCombo;

	private ComboViewer productCombo;

	private Text dosage;

	private Text dosages;

	private CDateTime dateTime;

	private ComboViewer countryCombo;

	private Text transferCode;

	public VaccinationModelDialog(VaccinationModel model, Shell shell) {
		super(shell);
		this.model = model;

		CoreUiUtil.injectServices(this);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Daten der Impfung");
		parent = (Composite) super.createDialogArea(parent);

		languageCombo = new ComboViewer(parent, SWT.BORDER);
		languageCombo.setContentProvider(ArrayContentProvider.getInstance());
		languageCombo.setInput(new String[] { "DE", "FR", "IT", "RM" });
		languageCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				model.setLanguage(((String) event.getStructuredSelection().getFirstElement()).toLowerCase());
			}
		});
		languageCombo.setSelection(new StructuredSelection(model.getLanguage().toUpperCase()));
		languageCombo.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		languageCombo.getControl().setToolTipText("Sprache");

		productCombo = new ComboViewer(parent, SWT.BORDER);
		productCombo.setContentProvider(ArrayContentProvider.getInstance());
		List<ICoding> vaccinationValueSet = valueSetService.getValueSet("vaccines-covid-19-names");
		productCombo.setInput(vaccinationValueSet);
		productCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ICoding) {
					return ((ICoding) element).getDisplay();
				}
				return super.getText(element);
			}
		});
		productCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				model.getVaccinationInfo()[0].setMedicinalProductCode(
						((ICoding) event.getStructuredSelection().getFirstElement()).getCode());
				if (productCombo.getControl().getData("deco") != null) {
					removeErrorDecoration(productCombo.getControl());
				}
			}
		});
		productCombo.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		productCombo.getControl().setToolTipText("Produkt");
		productCombo.getCombo().setText("Produkt");

		Composite dosageComp = new Composite(parent, SWT.NONE);
		dosageComp.setLayout(new GridLayout(4, false));
		Label lbl = new Label(dosageComp, SWT.NONE);
		lbl.setText("Impfung ");
		dosage = new Text(dosageComp, SWT.BORDER);
		dosage.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (StringUtils.isNotBlank(dosage.getText())) {
					try {
						model.getVaccinationInfo()[0].setNumberOfDoses(Integer.parseInt(dosage.getText()));
						if (dosage.getData("deco") != null) {
							removeErrorDecoration(dosage);
						}
					} catch (NumberFormatException ex) {
						addErrorDecoration(dosage);
					}
				}
			}
		});
		lbl = new Label(dosageComp, SWT.NONE);
		lbl.setText(" von ");
		dosages = new Text(dosageComp, SWT.BORDER);
		dosages.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (StringUtils.isNotBlank(dosages.getText())) {
					try {
						model.getVaccinationInfo()[0].setTotalNumberOfDoses(Integer.parseInt(dosages.getText()));
						if (dosages.getData("deco") != null) {
							removeErrorDecoration(dosages);
						}
					} catch (NumberFormatException ex) {
						addErrorDecoration(dosages);
					}
				}
			}
		});

		dateTime = new CDateTime(parent, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);
		dateTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Date now = new Date();
				if (dateTime.getSelection() != null) {
					Date selection = dateTime.getSelection();
					if (selection.before(now)) {
						model.getVaccinationInfo()[0]
								.setVaccinationDate(new SimpleDateFormat("yyyy-MM-dd").format(selection));
						removeErrorDecoration(dateTime);
					} else {
						addErrorDecoration(dateTime);
					}
				}
			}
		});
		try {
			dateTime.setSelection(
					new SimpleDateFormat("yyyy-MM-dd").parse(model.getVaccinationInfo()[0].getVaccinationDate()));
		} catch (ParseException e1) {
			LoggerFactory.getLogger(getClass())
					.warn("Could not parse date [" + model.getVaccinationInfo()[0].getVaccinationDate() + "]");
		}
		dateTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		dateTime.setToolTipText("Datum der Impfung");

		countryCombo = new ComboViewer(parent, SWT.BORDER);
		countryCombo.setContentProvider(ArrayContentProvider.getInstance());
		countryCombo.setInput(valueSetService.getValueSet("country-alpha-2-de").stream().map(c -> c.getCode())
				.collect(Collectors.toList()));
		countryCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				model.getVaccinationInfo()[0]
						.setCountryOfVaccination(((String) event.getStructuredSelection().getFirstElement()));
			}
		});
		countryCombo.setSelection(new StructuredSelection(model.getVaccinationInfo()[0].getCountryOfVaccination()));
		countryCombo.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		countryCombo.getControl().setToolTipText("Land der Impfung");

		String defaultVaccCode = ConfigServiceHolder.get().get(CertificatesService.CFG_DEFAULT_VACCPRODUCT, null);
		if (defaultVaccCode != null) {
			vaccinationValueSet.stream().filter(c -> c.getCode().equals(defaultVaccCode)).findFirst()
					.ifPresent(c -> productCombo.setSelection(new StructuredSelection(c)));
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

		dosage.setFocus();
		return parent;
	}

	@Override
	protected void okPressed() {
		if (StringUtils.isEmpty(model.getVaccinationInfo()[0].getMedicinalProductCode())) {
			addErrorDecoration(productCombo.getControl());
			return;
		}
		if (model.getVaccinationInfo()[0].getNumberOfDoses() == null) {
			addErrorDecoration(dosage);
			return;
		}
		if (model.getVaccinationInfo()[0].getTotalNumberOfDoses() == null) {
			addErrorDecoration(dosages);
			return;
		}
		try {
			if (model.getVaccinationInfo()[0].getVaccinationDate() == null || new SimpleDateFormat("yyyy-MM-dd")
					.parse(model.getVaccinationInfo()[0].getVaccinationDate()).after(new Date())) {
				addErrorDecoration(dateTime);
				return;
			}
		} catch (ParseException e) {
			addErrorDecoration(dateTime);
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
