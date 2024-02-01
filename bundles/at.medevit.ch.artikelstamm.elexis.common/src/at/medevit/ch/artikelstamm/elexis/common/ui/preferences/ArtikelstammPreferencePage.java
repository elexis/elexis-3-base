package at.medevit.ch.artikelstamm.elexis.common.ui.preferences;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
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
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class ArtikelstammPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	public Marge margeA, margeB, margeC;

	private DataBindingContext m_bindingContext;

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
		margeGroup.setText("Margen-Konfiguration");

		Label lblMargeA = new Label(margeGroup, SWT.NONE);
		lblMargeA.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMargeA.setText("Marge");

		textMargeAstartIntervall = new Text(margeGroup, SWT.BORDER);
		textMargeAstartIntervall.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		textMargeAstartIntervall.setMessage("von CHF");

		textMargeAendIntervall = new Text(margeGroup, SWT.BORDER);
		textMargeAendIntervall.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeAendIntervall.setMessage("bis CHF");

		Label lblZuschlag = new Label(margeGroup, SWT.NONE);
		lblZuschlag.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblZuschlag.setText("Zuschlag in %");

		textMargeAAddition = new Text(margeGroup, SWT.BORDER);
		textMargeAAddition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeAAddition.setMessage("%"); //$NON-NLS-1$

		// --

		Label lblMargeB = new Label(margeGroup, SWT.NONE);
		lblMargeB.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMargeB.setText("Marge");

		textMargeBstartIntervall = new Text(margeGroup, SWT.BORDER);
		textMargeBstartIntervall.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		textMargeBstartIntervall.setMessage("von CHF");

		textMargeBendIntervall = new Text(margeGroup, SWT.BORDER);
		textMargeBendIntervall.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeBendIntervall.setMessage("bis CHF");

		Label lblBZuschlag = new Label(margeGroup, SWT.NONE);
		lblBZuschlag.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBZuschlag.setText("Zuschlag in %");

		textMargeBAddition = new Text(margeGroup, SWT.BORDER);
		textMargeBAddition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeBAddition.setMessage("%"); //$NON-NLS-1$

		// --

		Label lblMargeC = new Label(margeGroup, SWT.NONE);
		lblMargeC.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMargeC.setText("Marge");

		textMargeCstartIntervall = new Text(margeGroup, SWT.BORDER);
		textMargeCstartIntervall.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		textMargeCstartIntervall.setMessage("von CHF");

		textMargeCendIntervall = new Text(margeGroup, SWT.BORDER);
		textMargeCendIntervall.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeCendIntervall.setMessage("bis CHF");

		Label lblCZuschlag = new Label(margeGroup, SWT.NONE);
		lblCZuschlag.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCZuschlag.setText("Zuschlag in %");

		textMargeCAddition = new Text(margeGroup, SWT.BORDER);
		textMargeCAddition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeCAddition.setMessage("%"); //$NON-NLS-1$

		lblInfo = new Label(margeGroup, SWT.NONE);
		lblInfo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 1));
		lblInfo.setText("Ein 0 Eintrag bewirkt das Ignorieren einer Zeile.");

		compAtcLang = new Composite(container, SWT.NONE);
		compAtcLang.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compAtcLang.setLayout(new GridLayout(3, false));

		lblShowAtcCodesIn = new Label(compAtcLang, SWT.NONE);
		lblShowAtcCodesIn.setText("ATC Codes darstellen in");

		SelectionListener radioSl = new LanguageRadioSelectionButtonListener();

		btnRadioGerman = new Button(compAtcLang, SWT.RADIO);
		btnRadioGerman.setData(ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN);
		btnRadioGerman.setText("deutsch");
		btnRadioGerman.addSelectionListener(radioSl);

		btnRadioEnglish = new Button(compAtcLang, SWT.RADIO);
		btnRadioEnglish.setData(ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_ENGLISH);
		btnRadioEnglish.setText("english");
		btnRadioEnglish.addSelectionListener(radioSl);

		btnShowArticlePrice = new Button(container, SWT.CHECK);
		btnShowArticlePrice
				.setSelection(ConfigServiceHolder.get().get(PreferenceConstants.PREF_SHOW_PRICE_IN_OVERVIEW, true));
		btnShowArticlePrice.setText("Artikelpreis in Übersicht anzeigen");
		btnShowArticlePrice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigServiceHolder.get().set(PreferenceConstants.PREF_SHOW_PRICE_IN_OVERVIEW,
						btnShowArticlePrice.getSelection());
			}
		});

		btnShowEmptyATCCodeGroups = new Button(container, SWT.CHECK);
		btnShowEmptyATCCodeGroups.setText("ATC Gruppen ohne verfügbare Artikel anzeigen");
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
		btnShowGenericWarning.setText("Warnung bei Abgabe von Orginalpräparaten anzeigen");
		btnShowGenericWarning.setSelection(
				ConfigServiceHolder.get().get(PreferenceConstants.PREF_SHOW_WARN_ORIGINAL_ARTICLES, false));
		btnShowGenericWarning.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigServiceHolder.get().set(PreferenceConstants.PREF_SHOW_WARN_ORIGINAL_ARTICLES,
						btnShowGenericWarning.getSelection());
			}
		});

		String language = ConfigServiceHolder.get().get(PreferenceConstants.PREF_ATC_CODE_LANGUAGE,
				ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN);
		if (language.equals(ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN)) {
			btnRadioGerman.setSelection(true);
		} else {
			btnRadioEnglish.setSelection(true);
		}

		m_bindingContext = initDataBindings();

		return container;
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
	public void init(IWorkbench workbench) {
		// Initialize the preference page
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextMargeAstartIntervallObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeAstartIntervall);
		IObservableValue startIntervalMargeAObserveValue = PojoProperties.value("startInterval").observe(margeA); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeAstartIntervallObserveWidget, startIntervalMargeAObserveValue,
				null, null);
		//
		IObservableValue observeTextTextMargeAendIntervallObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeAendIntervall);
		IObservableValue endIntervalMargeAObserveValue = PojoProperties.value("endInterval").observe(margeA); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeAendIntervallObserveWidget, endIntervalMargeAObserveValue, null,
				null);
		//
		IObservableValue observeTextTextMargeAAdditionObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeAAddition);
		IObservableValue additionMargeAObserveValue = PojoProperties.value("addition").observe(margeA); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeAAdditionObserveWidget, additionMargeAObserveValue, null, null);
		//
		IObservableValue observeTextTextMargeBstartIntervallObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeBstartIntervall);
		IObservableValue startIntervalMargeBObserveValue = PojoProperties.value("startInterval").observe(margeB); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeBstartIntervallObserveWidget, startIntervalMargeBObserveValue,
				null, null);
		//
		IObservableValue observeTextTextMargeBendIntervallObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeBendIntervall);
		IObservableValue endIntervalMargeBObserveValue = PojoProperties.value("endInterval").observe(margeB); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeBendIntervallObserveWidget, endIntervalMargeBObserveValue, null,
				null);
		//
		IObservableValue observeTextTextMargeBAdditionObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeBAddition);
		IObservableValue additionMargeBObserveValue = PojoProperties.value("addition").observe(margeB); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeBAdditionObserveWidget, additionMargeBObserveValue, null, null);
		//
		IObservableValue observeTextTextMargeCstartIntervallObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeCstartIntervall);
		IObservableValue startIntervalMargeCObserveValue = PojoProperties.value("startInterval").observe(margeC); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeCstartIntervallObserveWidget, startIntervalMargeCObserveValue,
				null, null);
		//
		IObservableValue observeTextTextMargeCendIntervallObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(textMargeCendIntervall);
		IObservableValue endIntervalMargeCObserveValue = PojoProperties.value("endInterval").observe(margeC); //$NON-NLS-1$
		bindingContext.bindValue(observeTextTextMargeCendIntervallObserveWidget, endIntervalMargeCObserveValue, null,
				null);
		//
		IObservableValue observeTextTextMargeCAdditionObserveWidget = WidgetProperties.text(SWT.Modify)
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
