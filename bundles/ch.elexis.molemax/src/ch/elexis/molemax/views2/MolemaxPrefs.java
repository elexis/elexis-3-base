package ch.elexis.molemax.views2;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.molemax.Messages;

public class MolemaxPrefs extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private List<Control> controlledWidgets = new ArrayList<>();

	public static final String CUSTOM_BASEDIR = "molemax/custom_imagebase";
	public static final String BASEDIR = "molemax/imagebase";

	private StringFieldEditor customBaseDirEditor;
	private Text dateFormatText;
	private Text timeFormatText;

	private String selectedSeparator = "_"; // Default separator
	private String selectedDateFormat = "yyyyMMdd"; // Default date format
	private String selectedTimeFormat = "hhmmss";
	private Spinner nameSpinner;
	private Spinner firstNameSpinner;
	Button slashButton;
	Button underLineButton;
	Button clearButton;
	Button patNumButton;
	Button nameButton;
	Button firstNameButton;
	Button timeButton;
	Button slotButton;
	Button dateButton;

	public MolemaxPrefs() {
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}

	@Override
	protected void createFieldEditors() {

		addField(new DirectoryFieldEditor(BASEDIR, Messages.MolemaxPrefs_basedir, getFieldEditorParent()));

		customBaseDirEditor = new StringFieldEditor(CUSTOM_BASEDIR, "Patienten Ordner Struktur",
				getFieldEditorParent());
		addField(customBaseDirEditor);

		// Fügen Sie den generierten Pfad hier ein
		customBaseDirEditor.setStringValue(generateCustomBaseDir());

		Composite separatorButtonContainer = new Composite(getFieldEditorParent(), SWT.NONE);
		separatorButtonContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		separatorButtonContainer.setLayout(new GridLayout(3, false));

		slashButton = new Button(separatorButtonContainer, SWT.PUSH);
		slashButton.setText("/");
		slashButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedSeparator = "/";
			}
		});
		underLineButton = new Button(separatorButtonContainer, SWT.PUSH);
		underLineButton.setText("_");
		underLineButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedSeparator = "_";
			}
		});

		clearButton = new Button(separatorButtonContainer, SWT.PUSH);
		clearButton.setText("Clear");
		clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				customBaseDirEditor.setStringValue("");
			}
		});
		Composite buttonContainer = new Composite(getFieldEditorParent(), SWT.NONE);
		buttonContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		buttonContainer.setLayout(new GridLayout(8, false));

		patNumButton = new Button(buttonContainer, SWT.PUSH);
		patNumButton.setText("Patienten Num.");
		patNumButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				appendToGeneratedStructure("PatNum");
			}
		});

		Composite nameComposite = new Composite(buttonContainer, SWT.NONE);
		nameComposite.setLayout(new GridLayout(2, false));

		nameButton = new Button(nameComposite, SWT.PUSH);
		nameButton.setText("Name");
		nameButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				appendToGeneratedStructure("Name", nameSpinner.getSelection());
			}
		});

		nameSpinner = new Spinner(nameComposite, SWT.BORDER);
		nameSpinner.setMinimum(0);
		nameSpinner.setMaximum(10);
		nameSpinner.setSelection(0);

		Composite firstNameComposite = new Composite(buttonContainer, SWT.NONE);
		firstNameComposite.setLayout(new GridLayout(2, false));

		firstNameButton = new Button(firstNameComposite, SWT.PUSH);
		firstNameButton.setText("Vorname");
		firstNameButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				appendToGeneratedStructure("Vorname", firstNameSpinner.getSelection());
			}
		});

		firstNameSpinner = new Spinner(firstNameComposite, SWT.BORDER);
		firstNameSpinner.setMinimum(0); // 0 means use the full name
		firstNameSpinner.setMaximum(10); // Assume a reasonable maximum value
		firstNameSpinner.setSelection(0);
		timeButton = new Button(buttonContainer, SWT.PUSH);
		timeButton.setText("Uhrzeit");
		controlledWidgets.add(timeButton);
		timeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				appendToGeneratedStructure("Uhrzeit-" + selectedTimeFormat);
			}
		});

		timeFormatText = new Text(buttonContainer, SWT.BORDER);
		timeFormatText.setText(selectedDateFormat);
		timeFormatText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		timeFormatText.addModifyListener(e -> selectedTimeFormat = timeFormatText.getText());

		dateButton = new Button(buttonContainer, SWT.PUSH);
		dateButton.setText("Datum");
		dateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				appendToGeneratedStructure("Datum-" + selectedDateFormat);
			}
		});

		dateFormatText = new Text(buttonContainer, SWT.BORDER);
		dateFormatText.setText(selectedDateFormat);
		dateFormatText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		dateFormatText.addModifyListener(e -> selectedDateFormat = dateFormatText.getText());
		slotButton = new Button(buttonContainer, SWT.PUSH);
		slotButton.setText("Slot");
		slotButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				appendToGeneratedStructure("Slot");
			}
		});

	}

	private void appendToGeneratedStructure(String component) {
		appendToGeneratedStructure(component, 0); // Default behavior is to use the full string
	}

	private void appendToGeneratedStructure(String component, int charCount) {
		String currentText = customBaseDirEditor.getStringValue();
		if (charCount > 0 && (component.equals("Name") || component.equals("Vorname"))) {
			component = component + "-" + charCount;
		}
		if (!currentText.isEmpty()) {
			currentText += selectedSeparator;
		}
		customBaseDirEditor.setStringValue(currentText + component);
	}

	private String generateCustomBaseDir() {
		String customPath = getPreferenceStore().getString(CUSTOM_BASEDIR);

		return customPath;
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}
}