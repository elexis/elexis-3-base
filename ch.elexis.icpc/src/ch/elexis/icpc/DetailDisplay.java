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

public class DetailDisplay implements IDetailDisplay {
	Text tLang, tICD, tCriteria, tInclude, tExclude, tConsider, tNote;
	FormToolkit tk = UiDesk.getToolkit();
	Form form;
	
	public Composite createDisplay(Composite parent, IViewSite site){
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
	
	public void display(Object obj){
		if (obj instanceof IcpcCode) {
			IcpcCode ipc = (IcpcCode) obj;
			form.setText(IcpcCode.checkNull(ipc.get("short")));
			tLang.setText(IcpcCode.checkNull(ipc.get("text")));
			tICD.setText(IcpcCode.checkNull(ipc.get("icd10")));
			tCriteria.setText(IcpcCode.checkNull(ipc.get("criteria")));
			tInclude.setText(IcpcCode.checkNull(ipc.get("inclusion")));
			tExclude.setText(IcpcCode.checkNull(ipc.get("exclusion")));
			tNote.setText(IcpcCode.checkNull(ipc.get("note")));
			tConsider.setText(IcpcCode.checkNull(ipc.get("consider")));
			// form.reflow(true);
		}
		
	}
	
	public Class getElementClass(){
		return IcpcCode.class;
	}
	
	public String getTitle(){
		return "ICPC";
	}
	
}
