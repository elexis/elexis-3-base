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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.rgw.tools.TimeTool;
import jakarta.inject.Inject;
import jakarta.inject.Named;

public class AmbulatoryAllowanceDetailDisplay implements IDetailDisplay {

	public static final TimeTool INFINITE = new TimeTool("19991231");

	Form form;
	FormToolkit tk = UiDesk.getToolkit();
//	LabeledInputField.AutoForm tblLab;

//	InputData[] data = new InputData[] { new InputData("Taxpunkte / Preis in Rappen", "TP", InputData.Typ.STRING, null), //$NON-NLS-1$ //$NON-NLS-2$
//	};

	private FormText validity;

	@Inject
	public void selection(
			@Optional @Named("ch.elexis.views.codeselector.ambulatoryallowance.selection") IAmbulatoryAllowance allowance) {
		if (allowance != null && !form.isDisposed()) {
			display(allowance);
		}
	}

	@Override
	public Composite createDisplay(Composite parent, IViewSite site) {
		form = tk.createForm(parent);
		TableWrapLayout twl = new TableWrapLayout();
		form.getBody().setLayout(twl);

//		tblLab = new LabeledInputField.AutoForm(form.getBody(), data);
//		tblLab.setModelService(ArzttarifeModelServiceHolder.get());

		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
//		tblLab.setLayoutData(twd);

		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_Validity);
		validity = tk.createFormText(form.getBody(), false);
		return form.getBody();
	}

	@Override
	public void display(Object obj) {
		IAmbulatoryAllowance ll = (IAmbulatoryAllowance) obj;
		form.setText(ll.getLabel());
//		tblLab.reload(ll);
		// validity
		String text;
		TimeTool tGueltigVon = new TimeTool(ll.getValidFrom());
		TimeTool tGueltigBis = new TimeTool(ll.getValidTo());
		if (tGueltigVon != null && tGueltigBis != null) {
			String from = tGueltigVon.toString(TimeTool.DATE_GER);
			String to;
			if (tGueltigBis.isSameDay(INFINITE)) {
				to = StringUtils.EMPTY;
			} else {
				to = tGueltigBis.toString(TimeTool.DATE_GER);
			}
			text = from + "-" + to; //$NON-NLS-1$
		} else {
			text = StringUtils.EMPTY;
		}
		validity.setText(text, false, false);
		form.layout();
	}

	@Override
	public Class<?> getElementClass() {
		return IAmbulatoryAllowance.class;
	}

	@Override
	public String getTitle() {
		return "Ambulantepauschalen";
	}
}
