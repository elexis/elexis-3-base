package ch.elexis.covid.cert.ui.dialogs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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

import ch.elexis.core.findings.codes.IValueSetService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.covid.cert.service.rest.model.RecoveryModel;
import jakarta.inject.Inject;

public class RecoveryModelDialog extends Dialog {

	@Inject
	private IValueSetService valueSetService;

	private RecoveryModel model;

	private ComboViewer languageCombo;

	private CDateTime dateTime;

	private ComboViewer countryCombo;

	private Text transferCode;

	public RecoveryModelDialog(RecoveryModel model, Shell shell) {
		super(shell);
		this.model = model;

		CoreUiUtil.injectServices(this);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Daten der Genesung / pos. Test");
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

		dateTime = new CDateTime(parent, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);
		dateTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Date now = new Date();
				if (dateTime.getSelection() != null) {
					Date selection = dateTime.getSelection();
					if (selection.before(now)) {
						model.getRecoveryInfo()[0]
								.setDateOfFirstPositiveTestResult(new SimpleDateFormat("yyyy-MM-dd").format(selection));
						removeErrorDecoration(dateTime);
					} else {
						addErrorDecoration(dateTime);
					}
				}
			}
		});
		try {
			dateTime.setSelection(new SimpleDateFormat("yyyy-MM-dd")
					.parse(model.getRecoveryInfo()[0].getDateOfFirstPositiveTestResult()));
		} catch (ParseException e1) {
			LoggerFactory.getLogger(getClass()).warn(
					"Could not parse date [" + model.getRecoveryInfo()[0].getDateOfFirstPositiveTestResult() + "]");
		}
		dateTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		dateTime.setToolTipText("Datum des positiven Test");

		countryCombo = new ComboViewer(parent, SWT.BORDER);
		countryCombo.setContentProvider(ArrayContentProvider.getInstance());
		countryCombo.setInput(valueSetService.getValueSet("country-alpha-2-de").stream().map(c -> c.getCode())
				.collect(Collectors.toList()));
		countryCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				model.getRecoveryInfo()[0]
						.setCountryOfTest(((String) event.getStructuredSelection().getFirstElement()));
			}
		});
		countryCombo.setSelection(new StructuredSelection(model.getRecoveryInfo()[0].getCountryOfTest()));
		countryCombo.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		countryCombo.getControl().setToolTipText("Land der Testung");

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

		dateTime.setFocus();
		return parent;
	}

	@Override
	protected void okPressed() {
		try {
			if (model.getRecoveryInfo()[0].getDateOfFirstPositiveTestResult() == null
					|| new SimpleDateFormat("yyyy-MM-dd")
							.parse(model.getRecoveryInfo()[0].getDateOfFirstPositiveTestResult()).after(new Date())) {
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
