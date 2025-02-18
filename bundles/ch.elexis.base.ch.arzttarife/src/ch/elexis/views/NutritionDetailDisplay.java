/*******************************************************************************
 * Copyright (c) 2009, G. Weirich, medshare and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.views;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.elexis.base.ch.arzttarife.nutrition.INutritionLeistung;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.util.LabeledInputField.InputData;
import ch.elexis.core.ui.views.IDetailDisplay;
import jakarta.inject.Inject;
import jakarta.inject.Named;

public class NutritionDetailDisplay implements IDetailDisplay {
	ScrolledForm form;
	FormToolkit tk = UiDesk.getToolkit();
	LabeledInputField.AutoForm tblLab;

	InputData[] data = new InputData[] { new InputData("Ziffer", "code", InputData.Typ.STRING, null), //$NON-NLS-1$
			new InputData("Taxpunkte / Preis in Rappen", "TP", InputData.Typ.STRING, null), //$NON-NLS-1$ //$NON-NLS-2$
			new InputData("Gültig von", "validFrom", InputData.Typ.STRING, null), //$NON-NLS-1$ //$NON-NLS-2$
			new InputData("Gültig bis", "validTo", InputData.Typ.STRING, null) //$NON-NLS-1$ //$NON-NLS-2$
	};
	private FormText description;

	@Inject
	public void selection(
			@Optional @Named("ch.elexis.views.codeselector.nutrition.selection") INutritionLeistung physio) {
		if (physio != null && !form.isDisposed()) {
			display(physio);
		}
	}

	public Composite createDisplay(Composite parent, IViewSite site) {
		form = tk.createScrolledForm(parent);
		TableWrapLayout twl = new TableWrapLayout();
		form.getBody().setLayout(twl);

		tblLab = new LabeledInputField.AutoForm(form.getBody(), data);

		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		tblLab.setLayoutData(twd);

		tk.createLabel(form.getBody(), "Beschreibung");
		description = tk.createFormText(form.getBody(), false);

		return form.getBody();
	}

	public void display(Object obj) {
		INutritionLeistung ll = (INutritionLeistung) obj;
		form.setText(ll.getLabel());
		tblLab.reload(ll);

		String text = ll.getDescription() != null
				? "<form>" + ll.getDescription().replaceAll(StringUtils.LF, "<br/>") + "</form>"
				: "<form/>";
		description.setText(text, true, false);
		form.reflow(true);
	}

	public Class<?> getElementClass() {
		return INutritionLeistung.class;
	}

	public String getTitle() {
		return "Ernährungsberatung";
	}

}
