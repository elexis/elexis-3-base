/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.base.ch.labortarif_2009.ui;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;

import ch.elexis.base.ch.labortarif.ILaborLeistung;
import ch.elexis.base.ch.labortarif_2009.Messages;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.selectors.DisplayPanel;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IDetailDisplay;
import jakarta.inject.Inject;
import jakarta.inject.Named;

public class DetailDisplay implements IDetailDisplay {
	Form form;
	DisplayPanel panel;
	FieldDescriptor<?>[] fields = {
			new FieldDescriptor<ILaborLeistung>(Messages.DetailDisplay_chapter, "chapter", Typ.STRING, null), //$NON-NLS-1$
			new FieldDescriptor<ILaborLeistung>(Messages.DetailDisplay_code, "code", Typ.STRING, null), //$NON-NLS-1$
			new FieldDescriptor<ILaborLeistung>(Messages.DetailDisplay_fachbereich, "speciality", Typ.STRING, null), //$NON-NLS-1$
			new FieldDescriptor<ILaborLeistung>(Messages.DetailDisplay_name, "text", Typ.STRING, null), //$NON-NLS-1$
			new FieldDescriptor<ILaborLeistung>(Messages.DetailDisplay_limitation, "limitation", Typ.STRING, null), //$NON-NLS-1$
			new FieldDescriptor<ILaborLeistung>(Messages.DetailDisplay_taxpoints, "points", Typ.STRING, null) }; //$NON-NLS-1$

	@Inject
	public void selection(@Optional @Named("ch.elexis.base.ch.labortarif_2009.ui.selection") ILaborLeistung item) {
		if (item != null && !panel.isDisposed()) {
			display(item);
		}
	}

	public void display(Object obj) {
		if (obj instanceof ILaborLeistung) {
			form.setText(((ILaborLeistung) obj).getLabel());
			panel.setObject((ILaborLeistung) obj);
		}
	}

	public String getTitle() {
		return "EAL 2009"; //$NON-NLS-1$
	}

	public Composite createDisplay(Composite parent, IViewSite site) {
		form = UiDesk.getToolkit().createForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		form.getBody().setLayout(new GridLayout());
		panel = new DisplayPanel(form.getBody(), fields, 1, 1);
		panel.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return panel;
	}

	@Override
	public Class<?> getElementClass() {
		return ILaborLeistung.class;
	}
}
