/*******************************************************************************
 * Copyright (c) 2020, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - initial implementation
 *
 *******************************************************************************/
package ch.elexis.views;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.util.LabeledInputField.InputData;
import ch.elexis.core.ui.views.IDetailDisplay;
import jakarta.inject.Inject;
import jakarta.inject.Named;

public class PandemieDetailDisplay implements IDetailDisplay {
	Form form;
	FormToolkit tk = UiDesk.getToolkit();
	LabeledInputField.AutoForm tblLab;

	InputData[] data = new InputData[] { new InputData("Taxpunkte", "taxpoints", InputData.Typ.STRING, null), //$NON-NLS-1$
			new InputData("Preis in Rappen", "cents", InputData.Typ.STRING, null), //$NON-NLS-1$
	};

	@Inject
	public void selection(
			@Optional @Named("ch.elexis.views.codeselector.pandemie.selection") IPandemieLeistung pandemie) {
		if (pandemie != null && !form.isDisposed()) {
			display(pandemie);
		}
	}

	public Composite createDisplay(Composite parent, IViewSite site) {
		form = tk.createForm(parent);
		TableWrapLayout twl = new TableWrapLayout();
		form.getBody().setLayout(twl);

		tblLab = new LabeledInputField.AutoForm(form.getBody(), data);
		tblLab.setModelService(CoreModelServiceHolder.get());

		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		tblLab.setLayoutData(twd);
		return form.getBody();
	}

	public void display(Object obj) {
		IPandemieLeistung ll = (IPandemieLeistung) obj;
		form.setText(ll.getLabel());
		tblLab.reload(ll);
	}

	public Class<?> getElementClass() {
		return IPandemieLeistung.class;
	}

	public String getTitle() {
		return "Pandemie";
	}
}
