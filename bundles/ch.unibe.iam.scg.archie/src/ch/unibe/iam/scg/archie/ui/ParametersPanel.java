/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.ui;

import org.apache.commons.lang3.StringUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import ch.elexis.core.ui.util.Log;
import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractDataProvider;
import ch.unibe.iam.scg.archie.model.RegexValidation;
import ch.unibe.iam.scg.archie.ui.widgets.AbstractWidget;
import ch.unibe.iam.scg.archie.ui.widgets.CheckboxWidget;
import ch.unibe.iam.scg.archie.ui.widgets.ComboWidget;
import ch.unibe.iam.scg.archie.ui.widgets.DateWidget;
import ch.unibe.iam.scg.archie.ui.widgets.NumericWidget;
import ch.unibe.iam.scg.archie.ui.widgets.TextWidget;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;
import ch.unibe.iam.scg.archie.utils.ProviderHelper;

/**
 * <p>
 * A composite panel which contains all the parameter fields for a data
 * provider. The parameters of a provider are set accordingly. Parameter fields
 * and their content are determined at runtime through annotations.
 * </p>
 *
 * $Id: ParametersPanel.java 764 2009-07-24 11:20:03Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 764 $
 */
public class ParametersPanel extends Composite {

	/**
	 * Map containing all text fields an their name. Used to feed the query with the
	 * user input.
	 */
	private Map<String, AbstractWidget> widgetMap;

	/**
	 * Map containing the getter method names and their default values. We need this
	 * because of the control decorations that have to be set after the text fields
	 * have been created.
	 */
	private Map<String, Object> defaultValuesMap;

	/**
	 * The query which is selected at the moment and will be configured according to
	 * the user input in this panel.
	 */
	private AbstractDataProvider provider;

	/**
	 * @param parent
	 * @param style
	 */
	public ParametersPanel(final Composite parent, final int style) {
		super(parent, style);

		// define layout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;

		this.setLayout(layout);
	}

	/**
	 * @param provider
	 */
	public void updateParameterList(AbstractDataProvider provider) {
		this.provider = provider; // the query which was selected.

		// clear this composite
		for (Control child : this.getChildren()) {
			child.dispose();
		}

		// initialize an empty field map
		this.widgetMap = new TreeMap<String, AbstractWidget>();
		this.defaultValuesMap = new HashMap<String, Object>();

		// populate again
		this.createWidgets();

		// re-show everything
		this.layout();

		// adjust label widths
		// NOTE: Probably not so efficient, maybe refactor the abstract
		// composites to only hold the text input fields and put all labels and
		// abstract fields into one layout?
		this.adjustLabelWidths();

		// set default widget values
		this.setDefaultValues();
	}

	/**
	 * Adjusts the width of the labels left to the text fields in widgets.
	 */
	private void adjustLabelWidths() {
		// calculate max label width
		int maxWidth = 0;
		for (AbstractWidget field : this.widgetMap.values()) {
			Label label = field.getLabel();
			String labelText = label.getText();
			if (!labelText.equals("Leistungen")) {
				int width = label.getBounds().width;
				maxWidth = Math.max(width, maxWidth);
			}
		}

		for (AbstractWidget field : this.widgetMap.values()) {
			Label label = field.getLabel();
			GridData data = new GridData();

			if (label.getText().equals("Leistungen")) {
				data.widthHint = 320;
			} else {
				data.widthHint = maxWidth;
			}

			label.setLayoutData(data);
		}
		this.layout();
	}

	/**
	 * Sets the default values of the widgets.
	 */
	private void setDefaultValues() {
		for (Entry<String, Object> name : this.defaultValuesMap.entrySet()) {
			this.widgetMap.get(name.getKey()).setValue(name.getValue());
		}
	}

	/**
	 * Create Parameter Fields. This method creates all the widgets based on the
	 * annotations of the currently selected provider.
	 */
	private void createWidgets() {
		// get all getters
		for (Method method : ProviderHelper.getGetterMethods(this.provider, true)) {
			GetProperty getter = method.getAnnotation(GetProperty.class);

			RegexValidation regex = null; // can be null
			if (!getter.validationRegex().equals(StringUtils.EMPTY)
					&& !getter.validationMessage().equals(StringUtils.EMPTY)) {
				regex = new RegexValidation(getter.validationRegex(), getter.validationMessage());
			}

			// create the appropriate text field
			AbstractWidget widget = this.createWidget(this, getter.name(), getter.widgetType(), regex,
					getter.vendorClass());

			// set a description if not empty
			if (!getter.description().equals(StringUtils.EMPTY)) {
				widget.setDescription(getter.description());
			}

			/*
			 * **************************************************************** Get string
			 * array and set the items if we have a Combo, that is:
			 * ***************************************************************
			 *
			 * - if there are any items - if the widget is our combo widget - if the widget
			 * is not a custom vendor widget
			 */
			if (getter.items().length > 0 && widget instanceof ComboWidget
					&& getter.widgetType() != WidgetTypes.VENDOR) {
				((ComboWidget) widget).setItems(getter.items());
			}

			// put field and label title in the map
			this.widgetMap.put(getter.name(), widget);

			// store widget default values in a map
			this.defaultValuesMap.put(getter.name(), ProviderHelper.getValue(method, this.provider));
		}
	}

	/**
	 * Updates the provider parameters according to the user input in the fields in
	 * this panel.
	 *
	 * @throws Exception
	 */
	public void updateProviderParameters() throws Exception {
		this.setProviderData();
	}

	/**
	 * Checks the validity of all input fields in this composite.
	 *
	 * @return true if all fields are valid, false else.
	 */
	public boolean allFieldsValid() {
		for (Map.Entry<String, AbstractWidget> entry : this.widgetMap.entrySet()) {
			if (!entry.getValue().isValid()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Enables or disables all controls in the <code>fieldMap</code> belonging to
	 * this <code>ParameterPanel</code>
	 *
	 * @param enabled true to enable, false to disable
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (this.widgetMap != null) {
			for (Map.Entry<String, AbstractWidget> entry : this.widgetMap.entrySet()) {
				entry.getValue().setEnabled(enabled);
			}
		}
	}

	/**
	 * Creates a single widget based on the <code>fieldType</code> parameter with
	 * given labels and values as well as a validation regex.
	 *
	 * @param parent      Composite container for the widget.
	 * @param label       Label for the widget.
	 * @param widgetType  Type of the widget to create.
	 * @param regex       A regex validation object.
	 * @param vendorClass Class of a vendor specific widget implementation.
	 * @return An <code>AbstractWidget</code> object.
	 */
	private AbstractWidget createWidget(final Composite parent, final String label, WidgetTypes widgetType,
			final RegexValidation regex, Class<?> vendorClass) {
		switch (widgetType) {
		case TEXT_DATE:
			return new DateWidget(parent, SWT.NONE, label, regex);
		case TEXT_NUMERIC:
			return new NumericWidget(parent, SWT.NONE, label, regex);
		case BUTTON_CHECKBOX:
			return new CheckboxWidget(parent, SWT.NONE, label, regex);
		case COMBO:
			return new ComboWidget(parent, SWT.NONE, label, regex);
		case VENDOR: // Vendor specific / custom widgets
			return this.createVendorWidget(parent, label, widgetType, regex, vendorClass);
		case TEXT:
		default: // Text widget returned by default.
			return new TextWidget(parent, SWT.NONE, label, regex);
		}
	}

	/** Sets all fields via the meta model in the given query. */
	private void setProviderData() throws Exception {
		assert (this.provider != null);
		this.setData(ProviderHelper.getSetterMethods(this.provider, true));
	}

	/**
	 * @param setterList
	 * @throws Exception
	 */
	private void setData(ArrayList<Method> setterList) throws Exception {
		for (Method method : setterList) {
			SetProperty setter = method.getAnnotation(SetProperty.class);
			AbstractWidget field = this.widgetMap.get(setter.name());
			Object value = field.getValue();
			ProviderHelper.setValue(this.provider, method, value);
		}
	}

	/**
	 * Creates a vendor widget object based on the given class. This method is used
	 * to instantiate custom widgets.
	 *
	 * @param parent      Composite container for the widget.
	 * @param label       Label for the widget.
	 * @param widgetType  Type of the widget to create.
	 * @param regex       A regex validation object.
	 * @param vendorClass Class of a vendor specific widget implementation.
	 * @return An <code>AbstractWidget</code> object.
	 * @return A vendor widget object, null else.
	 */
	@SuppressWarnings("unchecked")
	private AbstractWidget createVendorWidget(final Composite parent, final String label, WidgetTypes widgetType,
			final RegexValidation regex, Class<?> vendorClass) {
		AbstractWidget widget = null;
		Class<AbstractWidget> abstractWidgetClass = (Class<AbstractWidget>) vendorClass;
		try {
			widget = abstractWidgetClass
					.getConstructor(new Class[] { Composite.class, int.class, String.class, RegexValidation.class })
					.newInstance(parent, SWT.NONE, label, regex);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		// Log error as FATAL
		if (widget == null) {
			ArchieActivator.LOG.log(
					"Could not create custom vendor widget. Widget class was: [" + vendorClass.getName() + "]", //$NON-NLS-1$ //$NON-NLS-2$
					Log.FATALS);
		}

		return widget;
	}
}