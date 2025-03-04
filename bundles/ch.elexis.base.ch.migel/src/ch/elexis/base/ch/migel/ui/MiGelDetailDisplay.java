/*******************************************************************************
 * Copyright (c) 2006-2017, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    T. Huster - copied from ch.elexis.base.ch.artikel
 *
 *******************************************************************************/
package ch.elexis.base.ch.migel.ui;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import ch.elexis.artikel_ch.data.service.MiGelCodeElementService;
import ch.elexis.base.ch.migel.Messages;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.rgw.tools.Money;
import jakarta.inject.Inject;
import jakarta.inject.Named;

public class MiGelDetailDisplay implements IDetailDisplay {

	FormToolkit tk = UiDesk.getToolkit();
	ScrolledForm form;
	LabeledInputField ifName, ifPreis, ifAmount, ifEinheit;
	Text tName, tLong;
	IArticle act;

	@Inject
	public void selection(@Optional @Named("ch.elexis.base.ch.migel.ui.selection") IArticle typedArticle) {
		if (typedArticle != null && !form.isDisposed()) {
			display(typedArticle);
		}
	}

	public Composite createDisplay(Composite parent, IViewSite site) {
		parent.setLayout(new GridLayout());
		form = tk.createScrolledForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Composite ret = form.getBody();
		ret.setLayout(new GridLayout());

		ifName = new LabeledInputField(ret, "Name");
		ifName.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tName = (Text) ifName.getControl();
		tName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (act != null) {
					act.setName(tName.getText());
				}
				super.focusLost(e);
			}

		});
		ifPreis = new LabeledInputField(ret, Messages.MiGelDetailDisplay_Price);
		ifPreis.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		ifAmount = new LabeledInputField(ret, Messages.MiGelDetailDisplay_Amount);
		ifAmount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		ifEinheit = new LabeledInputField(ret, Messages.MiGelDetailDisplay_Unit);
		ifEinheit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		tLong = SWTHelper.createText(tk, ret, 4, SWT.READ_ONLY);
		tLong.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		return ret;
	}

	public Class getElementClass() {
		return IArticle.class;
	}

	public void display(Object obj) {
		if (obj instanceof IArticle) {
			act = (IArticle) obj;
			form.setText(act.getLabel());
			ifName.setText(act.getName());
			ifPreis.setText(new Money(act.getSellingPrice()).getAmountAsString());
			ifAmount.setText(Integer.toString(act.getPackageSize()));
			ifEinheit.setText(act.getPackageUnit());
			tLong.setText((String) act.getExtInfo("FullText")); //$NON-NLS-1$
		}
	}

	public String getTitle() {
		return MiGelCodeElementService.MIGEL_NAME; // $NON-NLS-1$
	}

}
