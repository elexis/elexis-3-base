/*******************************************************************************
 * Copyright (c) 2006-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.icpc;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.elexis.icpc.model.icpc.IcpcCode;
import jakarta.inject.Inject;
import jakarta.inject.Named;

public class DetailDisplay implements IDetailDisplay {
	Text tLang, tICD, tCriteria, tInclude, tExclude, tConsider, tNote;
	FormToolkit tk = UiDesk.getToolkit();
	Form form;

	@Inject
	public void selection(@Optional @Named("ch.elexis.icpc.selection") IcpcCode code) {
		if (form != null && !form.isDisposed()) {
			display(code);
		}
	}

	public Composite createDisplay(Composite parent, IViewSite site) {
		// parent.setLayout(new FillLayout());
		Composite wrapper = new Composite(parent, SWT.None);
		wrapper.setLayoutData(new GridLayout());
		wrapper.setLayout(new GridLayout());
		form = tk.createForm(wrapper);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite ret = form.getBody();
		ret.setLayout(new GridLayout(1, true));

		tk.createLabel(ret, "Text:");
		tLang = SWTHelper.createText(tk, ret, 2, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
		tk.createSeparator(ret, SWT.SEPARATOR | SWT.HORIZONTAL);

		tk.createLabel(ret, "ICD-10 Entsprechungen:");
		tICD = SWTHelper.createText(tk, ret, 3, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
		tk.createSeparator(ret, SWT.HORIZONTAL | SWT.SEPARATOR);

		tk.createLabel(ret, "Kriterien:");
		tCriteria = SWTHelper.createText(tk, ret, 3, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
		tk.createSeparator(ret, SWT.HORIZONTAL | SWT.SEPARATOR);

		tk.createLabel(ret, "Einschliesslich:");
		tInclude = SWTHelper.createText(tk, ret, 3, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
		tk.createSeparator(ret, SWT.HORIZONTAL | SWT.SEPARATOR);
		;

		tk.createLabel(ret, "Ausgeschlossen:");
		tExclude = SWTHelper.createText(tk, ret, 3, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		tk.createSeparator(ret, SWT.HORIZONTAL | SWT.SEPARATOR);

		tk.createLabel(ret, "zu berücksichtigen:");
		tConsider = SWTHelper.createText(tk, ret, 3, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
		tk.createSeparator(ret, SWT.HORIZONTAL | SWT.SEPARATOR);

		tk.createLabel(ret, "Bemerkungen:");
		tNote = SWTHelper.createText(tk, ret, 3, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);

		// tk.adapt(ret);
		return ret;
	}

	public void display(Object obj) {
		if (obj instanceof IcpcCode) {
			IcpcCode ipc = (IcpcCode) obj;
			form.setText(checkNull(ipc.getText()));
			tLang.setText(checkNull(ipc.getDescription()));
			tICD.setText(checkNull(ipc.getIcd10()));
			tCriteria.setText(checkNull(ipc.getCriteria()));
			tInclude.setText(checkNull(ipc.getInclusion()));
			tExclude.setText(checkNull(ipc.getExclusion()));
			tNote.setText(checkNull(ipc.getNote()));
			tConsider.setText(checkNull(ipc.getConsider()));
			// form.reflow(true);
		}

	}

	private String checkNull(String string) {
		return string != null ? string : StringUtils.EMPTY;
	}

	public Class getElementClass() {
		return IcpcCode.class;
	}

	public String getTitle() {
		return "ICPC";
	}

}
