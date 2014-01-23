package at.medevit.ch.artikelstamm.elexis.common.ui.preferences;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.ch.artikelstamm.elexis.common.preference.MargePreference;
import at.medevit.ch.artikelstamm.marge.Marge;

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
	
	/**
	 * Create the preference page.
	 */
	public ArtikelstammPreferencePage(){
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
	public Control createContents(Composite parent){
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(5, false));
		
		Label lblMargeA = new Label(container, SWT.NONE);
		lblMargeA.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMargeA.setText("Marge");
		
		textMargeAstartIntervall = new Text(container, SWT.BORDER);
		textMargeAstartIntervall.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		textMargeAstartIntervall.setMessage("von CHF");
		
		textMargeAendIntervall = new Text(container, SWT.BORDER);
		textMargeAendIntervall.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeAendIntervall.setMessage("bis CHF");
		
		Label lblZuschlag = new Label(container, SWT.NONE);
		lblZuschlag.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblZuschlag.setText("Zuschlag in %");
		
		textMargeAAddition = new Text(container, SWT.BORDER);
		textMargeAAddition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeAAddition.setMessage("%");
		
		// --
		
		Label lblMargeB = new Label(container, SWT.NONE);
		lblMargeB.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMargeB.setText("Marge");
		
		textMargeBstartIntervall = new Text(container, SWT.BORDER);
		textMargeBstartIntervall.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		textMargeBstartIntervall.setMessage("von CHF");
		
		textMargeBendIntervall = new Text(container, SWT.BORDER);
		textMargeBendIntervall.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeBendIntervall.setMessage("bis CHF");
		
		Label lblBZuschlag = new Label(container, SWT.NONE);
		lblBZuschlag.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBZuschlag.setText("Zuschlag in %");
		
		textMargeBAddition = new Text(container, SWT.BORDER);
		textMargeBAddition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeBAddition.setMessage("%");
		
		// --
		
		Label lblMargeC = new Label(container, SWT.NONE);
		lblMargeC.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMargeC.setText("Marge");
		
		textMargeCstartIntervall = new Text(container, SWT.BORDER);
		textMargeCstartIntervall.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		textMargeCstartIntervall.setMessage("von CHF");
		
		textMargeCendIntervall = new Text(container, SWT.BORDER);
		textMargeCendIntervall.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeCendIntervall.setMessage("bis CHF");
		
		Label lblCZuschlag = new Label(container, SWT.NONE);
		lblCZuschlag.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCZuschlag.setText("Zuschlag in %");
		
		textMargeCAddition = new Text(container, SWT.BORDER);
		textMargeCAddition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMargeCAddition.setMessage("%");
		
		lblInfo = new Label(container, SWT.NONE);
		lblInfo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 1));
		lblInfo.setText("Ein 0 Eintrag bewirkt das Ignorieren einer Zeile.");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		m_bindingContext = initDataBindings();
		
		return container;
	}
	
	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench){
		// Initialize the preference page
	}
	
	protected DataBindingContext initDataBindings(){
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextMargeAstartIntervallObserveWidget =
			WidgetProperties.text(SWT.Modify).observe(textMargeAstartIntervall);
		IObservableValue startIntervalMargeAObserveValue =
			PojoProperties.value("startInterval").observe(margeA);
		bindingContext.bindValue(observeTextTextMargeAstartIntervallObserveWidget,
			startIntervalMargeAObserveValue, null, null);
		//
		IObservableValue observeTextTextMargeAendIntervallObserveWidget =
			WidgetProperties.text(SWT.Modify).observe(textMargeAendIntervall);
		IObservableValue endIntervalMargeAObserveValue =
			PojoProperties.value("endInterval").observe(margeA);
		bindingContext.bindValue(observeTextTextMargeAendIntervallObserveWidget,
			endIntervalMargeAObserveValue, null, null);
		//
		IObservableValue observeTextTextMargeAAdditionObserveWidget =
			WidgetProperties.text(SWT.Modify).observe(textMargeAAddition);
		IObservableValue additionMargeAObserveValue =
			PojoProperties.value("addition").observe(margeA);
		bindingContext.bindValue(observeTextTextMargeAAdditionObserveWidget,
			additionMargeAObserveValue, null, null);
		//
		IObservableValue observeTextTextMargeBstartIntervallObserveWidget =
			WidgetProperties.text(SWT.Modify).observe(textMargeBstartIntervall);
		IObservableValue startIntervalMargeBObserveValue =
			PojoProperties.value("startInterval").observe(margeB);
		bindingContext.bindValue(observeTextTextMargeBstartIntervallObserveWidget,
			startIntervalMargeBObserveValue, null, null);
		//
		IObservableValue observeTextTextMargeBendIntervallObserveWidget =
			WidgetProperties.text(SWT.Modify).observe(textMargeBendIntervall);
		IObservableValue endIntervalMargeBObserveValue =
			PojoProperties.value("endInterval").observe(margeB);
		bindingContext.bindValue(observeTextTextMargeBendIntervallObserveWidget,
			endIntervalMargeBObserveValue, null, null);
		//
		IObservableValue observeTextTextMargeBAdditionObserveWidget =
			WidgetProperties.text(SWT.Modify).observe(textMargeBAddition);
		IObservableValue additionMargeBObserveValue =
			PojoProperties.value("addition").observe(margeB);
		bindingContext.bindValue(observeTextTextMargeBAdditionObserveWidget,
			additionMargeBObserveValue, null, null);
		//
		IObservableValue observeTextTextMargeCstartIntervallObserveWidget =
			WidgetProperties.text(SWT.Modify).observe(textMargeCstartIntervall);
		IObservableValue startIntervalMargeCObserveValue =
			PojoProperties.value("startInterval").observe(margeC);
		bindingContext.bindValue(observeTextTextMargeCstartIntervallObserveWidget,
			startIntervalMargeCObserveValue, null, null);
		//
		IObservableValue observeTextTextMargeCendIntervallObserveWidget =
			WidgetProperties.text(SWT.Modify).observe(textMargeCendIntervall);
		IObservableValue endIntervalMargeCObserveValue =
			PojoProperties.value("endInterval").observe(margeC);
		bindingContext.bindValue(observeTextTextMargeCendIntervallObserveWidget,
			endIntervalMargeCObserveValue, null, null);
		//
		IObservableValue observeTextTextMargeCAdditionObserveWidget =
			WidgetProperties.text(SWT.Modify).observe(textMargeCAddition);
		IObservableValue additionMargeCObserveValue =
			PojoProperties.value("addition").observe(margeC);
		bindingContext.bindValue(observeTextTextMargeCAdditionObserveWidget,
			additionMargeCObserveValue, null, null);
		//
		return bindingContext;
	}
	
	@Override
	protected void performApply(){
		MargePreference.storeMargeConfiguration();
	}
	
	@Override
	public boolean performOk(){
		performApply();
		return super.performOk();
	}
}
