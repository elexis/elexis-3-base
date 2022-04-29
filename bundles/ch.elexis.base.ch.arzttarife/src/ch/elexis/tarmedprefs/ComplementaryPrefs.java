/*******************************************************************************
 * Copyright (c) 2006, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.tarmedprefs;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class ComplementaryPrefs extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Override
	protected void createFieldEditors() {
		HourlyWageFieldEditor hourlyWageEditpor = new HourlyWageFieldEditor(
				PreferenceConstants.COMPLEMENTARY_HOURLY_WAGE, "Stundensatz", getFieldEditorParent());
		addField(hourlyWageEditpor);

		BooleanFieldEditor fixToVVG = new BooleanFieldEditor(PreferenceConstants.COMPLEMENTARY_FIXTOVVG,
				"Nur bei VVG verrechenbar", getFieldEditorParent());
		addField(fixToVVG);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.MANDATOR));
		setTitle("Komplementärmedizin");
	}

	/**
	 * Hourly Wage {@link FieldEditor} implementation, editing as amount with
	 * decimal places, saving as cents.
	 *
	 * @author thomas
	 *
	 */
	private class HourlyWageFieldEditor extends StringFieldEditor {
		private float minValidValue = 0;

		private float maxValidValue = Float.MAX_VALUE;

		private static final int DEFAULT_TEXT_LIMIT = 10;

		public HourlyWageFieldEditor(String name, String labelText, Composite parent) {
			this(name, labelText, parent, DEFAULT_TEXT_LIMIT);
		}

		public HourlyWageFieldEditor(String name, String labelText, Composite parent, int textLimit) {
			init(name, labelText);
			setTextLimit(textLimit);
			setEmptyStringAllowed(true);
			setErrorMessage("Kein gültiger Wert");//$NON-NLS-1$
			createControl(parent);
		}

		@Override
		protected boolean checkState() {
			Text text = getTextControl();
			if (text == null) {
				return false;
			}

			String numberString = text.getText().replaceAll(",", ".");
			try {
				float number = Float.valueOf(numberString).floatValue();
				if (number >= minValidValue && number <= maxValidValue) {
					clearErrorMessage();
					return true;
				}
				showErrorMessage();
				return false;
			} catch (NumberFormatException e1) {
				showErrorMessage();
			}
			return false;
		}

		@Override
		protected void doLoad() {
			Text text = getTextControl();
			if (text != null) {
				int value = getPreferenceStore().getInt(getPreferenceName());
				text.setText(StringUtils.EMPTY + (float) value / 100);
				oldValue = StringUtils.EMPTY + value;
			}

		}

		@Override
		protected void doLoadDefault() {
			Text text = getTextControl();
			if (text != null) {
				int value = getPreferenceStore().getDefaultInt(getPreferenceName());
				text.setText(StringUtils.EMPTY + (float) value / 100);
			}
			valueChanged();
		}

		@Override
		protected void doStore() {
			Text text = getTextControl();
			if (text != null) {
				Float f = Float.valueOf(text.getText().replaceAll(",", "."));
				f *= 100;
				getPreferenceStore().setValue(getPreferenceName(), f.intValue());
			}
		}
	}
}
