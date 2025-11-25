package at.medevit.ch.artikelstamm.elexis.common.ui.preferences;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.atc_codes.ATCCodeLanguageConstants;
import at.medevit.ch.artikelstamm.marge.Marge;
import at.medevit.ch.artikelstamm.model.common.preference.MargePreference;
import at.medevit.ch.artikelstamm.model.common.preference.PreferenceConstants;
import at.medevit.ch.artikelstamm.ui.DetailComposite;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.tasks.model.ITaskDescriptor;
import ch.elexis.core.tasks.model.ITaskService;
import ch.elexis.core.tasks.model.TaskTriggerType;
import ch.elexis.core.utils.OsgiServiceUtil;

public class ArtikelstammPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	public Marge margeA, margeB, margeC;

	private Text textMargeAstartIntervall;
	private Text textMargeAendIntervall;
	private Text textMargeAAddition;
	private Text textMargeBstartIntervall;
	private Text textMargeBendIntervall;
	private Text textMargeBAddition;
	private Text textMargeCstartIntervall;
	private Text textMargeCendIntervall;
	private Text textMargeCAddition;
	private Label lblInfo;
	private Composite compAtcLang;
	private Label lblShowAtcCodesIn;
	private Button btnRadioGerman;
	private Button btnRadioEnglish;
	private Button btnShowArticlePrice;
	private Button btnShowEmptyATCCodeGroups;
	private Button btnShowGenericWarning;

	private Button btnShowGenericWarningMediList;
	private Button btnShowGenericWarningRecipe;

	public static final String PREFERENCE_BASE = "rdus/"; //$NON-NLS-1$
	public static final String PREFERENCE_UPDATE_INTERVAL = PREFERENCE_BASE + "updateCheckInterval"; //$NON-NLS-1$
	public static final String PREFERENCE_AUTO_UPDATE_ENABLED = "rdus.autoUpdate.enabled"; //$NON-NLS-1$
	public static final String PREFERENCE_AUTO_ADJUST_OPEN_ENCOUNTERS = "rdus.autoAdjustOpenEncounters"; //$NON-NLS-1$
	public static final String RUNNABLE_ID = "rdusReferenceDataUpdate"; //$NON-NLS-1$
	final boolean autoEnabledInit = ConfigServiceHolder.getGlobal(PREFERENCE_AUTO_UPDATE_ENABLED, false);
	final int intervalInit = ConfigServiceHolder.getGlobal(PREFERENCE_UPDATE_INTERVAL, 1);

	/**
	 * Create the preference page.
	 */
	public ArtikelstammPreferencePage() {
		Marge[] marges = MargePreference.getMarges();
		// currently hard-coded as set to 3, quick and dirty solution
		margeA = marges[0];
		margeB = marges[1];
		margeC = marges[2];
	}

	/**
	 * Create contents of the preference page.
	 *
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, false));

		Group margeGroup = new Group(container, SWT.None);
		margeGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		margeGroup.setLayout(new GridLayout(5, false));
		margeGroup.setText(Messages.ArtikelstammPref_MargeGroup_Title);

		Label lblMargeA = new Label(margeGroup, SWT.NONE);
		lblMargeA.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMargeA.setText(Messages.ArtikelstammPref_Marge_Label);

		textMargeAstartIntervall = new Text(margeGroup, SWT.BORDER);
		textMargeAstartIntervall.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		textMargeAstartIntervall.setMessage(Messages.ArtikelstammPref_Marge_FromCHF);

		textMargeAendIntervall = new Text(margeGroup, SWT.BORDER);
		textMargeAendIntervall.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeAendIntervall.setMessage(Messages.ArtikelstammPref_Marge_ToCHF);

		Label lblZuschlag = new Label(margeGroup, SWT.NONE);
		lblZuschlag.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblZuschlag.setText(Messages.ArtikelstammPref_Marge_AdditionPercent);

		textMargeAAddition = new Text(margeGroup, SWT.BORDER);
		textMargeAAddition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeAAddition.setMessage("%"); //$NON-NLS-1$

		// --

		Label lblMargeB = new Label(margeGroup, SWT.NONE);
		lblMargeB.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMargeB.setText(Messages.ArtikelstammPref_Marge_Label);

		textMargeBstartIntervall = new Text(margeGroup, SWT.BORDER);
		textMargeBstartIntervall.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		textMargeBstartIntervall.setMessage(Messages.ArtikelstammPref_Marge_FromCHF);

		textMargeBendIntervall = new Text(margeGroup, SWT.BORDER);
		textMargeBendIntervall.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeBendIntervall.setMessage(Messages.ArtikelstammPref_Marge_ToCHF);

		Label lblBZuschlag = new Label(margeGroup, SWT.NONE);
		lblBZuschlag.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBZuschlag.setText(Messages.ArtikelstammPref_Marge_AdditionPercent);

		textMargeBAddition = new Text(margeGroup, SWT.BORDER);
		textMargeBAddition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeBAddition.setMessage("%"); //$NON-NLS-1$

		// --

		Label lblMargeC = new Label(margeGroup, SWT.NONE);
		lblMargeC.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMargeC.setText(Messages.ArtikelstammPref_Marge_Label);

		textMargeCstartIntervall = new Text(margeGroup, SWT.BORDER);
		textMargeCstartIntervall.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		textMargeCstartIntervall.setMessage(Messages.ArtikelstammPref_Marge_FromCHF);

		textMargeCendIntervall = new Text(margeGroup, SWT.BORDER);
		textMargeCendIntervall.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeCendIntervall.setMessage(Messages.ArtikelstammPref_Marge_ToCHF);

		Label lblCZuschlag = new Label(margeGroup, SWT.NONE);
		lblCZuschlag.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCZuschlag.setText(Messages.ArtikelstammPref_Marge_AdditionPercent);

		textMargeCAddition = new Text(margeGroup, SWT.BORDER);
		textMargeCAddition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeCAddition.setMessage("%"); //$NON-NLS-1$

		lblInfo = new Label(margeGroup, SWT.NONE);
		lblInfo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 1));
		lblInfo.setText(Messages.ArtikelstammPref_Marge_IgnoreInfo);

		compAtcLang = new Composite(container, SWT.NONE);
		compAtcLang.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compAtcLang.setLayout(new GridLayout(3, false));

		lblShowAtcCodesIn = new Label(compAtcLang, SWT.NONE);
		lblShowAtcCodesIn.setText(Messages.ArtikelstammPref_ATCLang_Label);

		SelectionListener radioSl = new LanguageRadioSelectionButtonListener();

		btnRadioGerman = new Button(compAtcLang, SWT.RADIO);
		btnRadioGerman.setData(ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN);
		btnRadioGerman.setText(Messages.ArtikelstammPref_ATCLang_de);
		btnRadioGerman.addSelectionListener(radioSl);

		btnRadioEnglish = new Button(compAtcLang, SWT.RADIO);
		btnRadioEnglish.setData(ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_ENGLISH);
		btnRadioEnglish.setText(Messages.ArtikelstammPref_ATCLang_en);
		btnRadioEnglish.addSelectionListener(radioSl);

		btnShowArticlePrice = new Button(container, SWT.CHECK);
		btnShowArticlePrice
				.setSelection(ConfigServiceHolder.get().get(PreferenceConstants.PREF_SHOW_PRICE_IN_OVERVIEW, true));
		btnShowArticlePrice.setText(Messages.ArtikelstammPref_ShowPriceInOverview);
		btnShowArticlePrice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigServiceHolder.get().set(PreferenceConstants.PREF_SHOW_PRICE_IN_OVERVIEW,
						btnShowArticlePrice.getSelection());
			}
		});

		btnShowEmptyATCCodeGroups = new Button(container, SWT.CHECK);
		btnShowEmptyATCCodeGroups.setText(Messages.ArtikelstammPref_ShowEmptyATCGroups);
		btnShowEmptyATCCodeGroups.setSelection(
				ConfigServiceHolder.get().get(PreferenceConstants.PREF_SHOW_ATC_GROUPS_WITHOUT_ARTICLES, true));
		btnShowEmptyATCCodeGroups.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigServiceHolder.get().set(PreferenceConstants.PREF_SHOW_ATC_GROUPS_WITHOUT_ARTICLES,
						btnShowEmptyATCCodeGroups.getSelection());
			}
		});

		btnShowGenericWarning = new Button(container, SWT.CHECK);
		btnShowGenericWarning.setText(Messages.ArtikelstammPref_ShowGenericWarning);
		btnShowGenericWarning.setSelection(
				ConfigServiceHolder.get().get(PreferenceConstants.PREF_SHOW_WARN_ORIGINAL_ARTICLES, false));
		btnShowGenericWarning.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigServiceHolder.get().set(PreferenceConstants.PREF_SHOW_WARN_ORIGINAL_ARTICLES,
						btnShowGenericWarning.getSelection());
			}
		});

		btnShowGenericWarningMediList = new Button(container, SWT.CHECK);
		btnShowGenericWarningMediList.setText(Messages.ArtikelstammPref_ShowGenericWarning_MediList);
		btnShowGenericWarningMediList.setSelection(
				ConfigServiceHolder.get().get(PreferenceConstants.PREF_SHOW_WARN_ORIGINAL_ARTICLES_MEDILIST, false));
		btnShowGenericWarningMediList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigServiceHolder.get().set(PreferenceConstants.PREF_SHOW_WARN_ORIGINAL_ARTICLES_MEDILIST,
						btnShowGenericWarningMediList.getSelection());
			}
		});

		btnShowGenericWarningRecipe = new Button(container, SWT.CHECK);
		btnShowGenericWarningRecipe.setText(Messages.ArtikelstammPref_ShowGenericWarning_Recipe);
		btnShowGenericWarningRecipe.setSelection(
				ConfigServiceHolder.get().get(PreferenceConstants.PREF_SHOW_WARN_ORIGINAL_ARTICLES_RECIPE, false));
		btnShowGenericWarningRecipe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigServiceHolder.get().set(PreferenceConstants.PREF_SHOW_WARN_ORIGINAL_ARTICLES_RECIPE,
						btnShowGenericWarningRecipe.getSelection());
			}
		});

		String language = ConfigServiceHolder.get().get(PreferenceConstants.PREF_ATC_CODE_LANGUAGE,
				ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN);
		if (language.equals(ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN)) {
			btnRadioGerman.setSelection(true);
		} else {
			btnRadioEnglish.setSelection(true);
		}

		Group rdus = new Group(container, SWT.None);
		rdus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rdus.setLayout(new GridLayout(5, false));
		rdus.setText(Messages.ArtikelstammPref_RDUS_Group);

		Button chkAutoUpdate = new Button(rdus, SWT.CHECK);
		chkAutoUpdate.setText(Messages.ArtikelstammPref_RDUS_EnableAutoImport);
		chkAutoUpdate.setSelection(autoEnabledInit);
		GridData gdChk = new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 1);
		chkAutoUpdate.setLayoutData(gdChk);

		Label lblInterval = new Label(rdus, SWT.NONE);
		lblInterval.setText(Messages.ArtikelstammPref_RDUS_CheckInterval_Label);
		lblInterval.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		org.eclipse.swt.widgets.Spinner spnInterval = new org.eclipse.swt.widgets.Spinner(rdus, SWT.BORDER);
		spnInterval.setMinimum(1);
		spnInterval.setMaximum(168);
		spnInterval.setIncrement(1);
		spnInterval.setPageIncrement(1);
		spnInterval.setSelection(Math.max(1, Math.min(168, intervalInit)));
		GridData gdSpin = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gdSpin.widthHint = 80;
		spnInterval.setLayoutData(gdSpin);
		new Label(rdus, SWT.NONE);
		new Label(rdus, SWT.NONE);

		lblInterval.setEnabled(chkAutoUpdate.getSelection());
		spnInterval.setEnabled(chkAutoUpdate.getSelection());

		chkAutoUpdate.addListener(SWT.Selection, e -> {
			boolean enabled = chkAutoUpdate.getSelection();
			lblInterval.setEnabled(enabled);
			spnInterval.setEnabled(enabled);
			ConfigServiceHolder.get().set(PREFERENCE_AUTO_UPDATE_ENABLED, enabled);
			int val = spnInterval.getSelection();
			if (val < 1)
				val = 1;
			if (val > 168)
				val = 168;
			toggleRdusTask(enabled, val);
		});

		spnInterval.addListener(SWT.Modify, e -> {
			int val = spnInterval.getSelection();
			if (val < 1)
				val = 1;
			if (val > 168)
				val = 168;

			ConfigServiceHolder.get().set(PREFERENCE_UPDATE_INTERVAL, val);
			boolean enabled = chkAutoUpdate.getSelection();
			toggleRdusTask(enabled, val);
		});

		Button chkAutoAdjust = new Button(rdus, SWT.CHECK);
		chkAutoAdjust.setText(Messages.ArtikelstammPref_RDUS_AutoAdjust);
		chkAutoAdjust.setSelection(ConfigServiceHolder.getGlobal(PREFERENCE_AUTO_ADJUST_OPEN_ENCOUNTERS, true));
		GridData gdAdj = new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1);
		chkAutoAdjust.setLayoutData(gdAdj);

		chkAutoAdjust.addListener(SWT.Selection, e -> {
			boolean enabled = chkAutoAdjust.getSelection();
			ConfigServiceHolder.get().set(PREFERENCE_AUTO_ADJUST_OPEN_ENCOUNTERS, enabled);
		});

		initDataBindings();

		return container;
	}

	private void toggleRdusTask(boolean enabled, int intervalHours) {
		try {
			ITaskService taskService = OsgiServiceUtil.getService(ITaskService.class).orElse(null);
			if (taskService == null) {
				return;
			}

			java.util.List<ITaskDescriptor> descriptors = taskService
					.findTaskDescriptorByIIdentifiedRunnableId(RUNNABLE_ID);
			if (descriptors == null || descriptors.isEmpty()) {
				return;
			}
			String cronExpr = "0 0 */" + intervalHours + " * * ?"; //$NON-NLS-1$ //$NON-NLS-2$
			for (ITaskDescriptor td : descriptors) {
				try {
					taskService.setActive(td, enabled);

					if (td.getTriggerType() == TaskTriggerType.CRON) {
						java.util.Map<String, String> tp = td.getTriggerParameters();
						if (tp == null) {
							tp = new java.util.HashMap<>();
						} else {
							tp = new java.util.HashMap<>(tp);
						}
						tp.put("cron", cronExpr); //$NON-NLS-1$
						td.setTriggerParameters(tp);
					}

					taskService.saveTaskDescriptor(td);
					taskService.refresh(td);
				} catch (TaskException ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private class LanguageRadioSelectionButtonListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (!((Button) e.widget).getSelection())
				return;
			ConfigServiceHolder.get().set(PreferenceConstants.PREF_ATC_CODE_LANGUAGE, (String) e.widget.getData());
			DetailComposite.setPrefAtcLanguage((String) e.widget.getData());
		}
	}

	/**
	 * Initialize the preference page.
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Initialize the preference page
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue<String> observeTextTextMargeAstartIntervallObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeAstartIntervall);
		IObservableValue startIntervalMargeAObserveValue = PojoProperties.value("startInterval").observe(margeA); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeAstartIntervallObserveWidget, startIntervalMargeAObserveValue,
				null, null);
		//
		IObservableValue<String> observeTextTextMargeAendIntervallObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeAendIntervall);
		IObservableValue endIntervalMargeAObserveValue = PojoProperties.value("endInterval").observe(margeA); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeAendIntervallObserveWidget, endIntervalMargeAObserveValue, null,
				null);
		//
		IObservableValue<String> observeTextTextMargeAAdditionObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeAAddition);
		IObservableValue additionMargeAObserveValue = PojoProperties.value("addition").observe(margeA); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeAAdditionObserveWidget, additionMargeAObserveValue, null, null);
		//
		IObservableValue<String> observeTextTextMargeBstartIntervallObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeBstartIntervall);
		IObservableValue startIntervalMargeBObserveValue = PojoProperties.value("startInterval").observe(margeB); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeBstartIntervallObserveWidget, startIntervalMargeBObserveValue,
				null, null);
		//
		IObservableValue<String> observeTextTextMargeBendIntervallObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeBendIntervall);
		IObservableValue endIntervalMargeBObserveValue = PojoProperties.value("endInterval").observe(margeB); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeBendIntervallObserveWidget, endIntervalMargeBObserveValue, null,
				null);
		//
		IObservableValue<String> observeTextTextMargeBAdditionObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeBAddition);
		IObservableValue additionMargeBObserveValue = PojoProperties.value("addition").observe(margeB); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeBAdditionObserveWidget, additionMargeBObserveValue, null, null);
		//
		IObservableValue<String> observeTextTextMargeCstartIntervallObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeCstartIntervall);
		IObservableValue startIntervalMargeCObserveValue = PojoProperties.value("startInterval").observe(margeC); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeCstartIntervallObserveWidget, startIntervalMargeCObserveValue,
				null, null);
		//
		IObservableValue<String> observeTextTextMargeCendIntervallObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeCendIntervall);
		IObservableValue endIntervalMargeCObserveValue = PojoProperties.value("endInterval").observe(margeC); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeCendIntervallObserveWidget, endIntervalMargeCObserveValue, null,
				null);
		//
		IObservableValue<String> observeTextTextMargeCAdditionObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeCAddition);
		IObservableValue additionMargeCObserveValue = PojoProperties.value("addition").observe(margeC); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeCAdditionObserveWidget, additionMargeCObserveValue, null, null);
		//
		return bindingContext;
	}

	@Override
	protected void performApply() {
		MargePreference.storeMargeConfiguration();
	}

	@Override
	public boolean performOk() {
		performApply();
		return super.performOk();
	}
}
