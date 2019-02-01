/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.ui.cv;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.elexis.common.service.ModelServiceHolder;
import ch.elexis.core.types.VatInfo;
import ch.elexis.core.ui.util.viewers.CommonViewer;

public class VATMenuContributionItem extends ContributionItem {
	
	CommonViewer cv;
	
	public VATMenuContributionItem(CommonViewer cv){
		this.cv = cv;
	}
	
	@Override
	public void fill(Menu menu, int index){
		StructuredSelection structuredSelection = new StructuredSelection(cv.getSelection());
		Object element = structuredSelection.getFirstElement();
		if (element instanceof IArtikelstammItem) {
			final IArtikelstammItem ai = (IArtikelstammItem) element;
			
			VatInfo vatInfo = ai.getVatInfo();
			
			MenuItem mi = new MenuItem(menu, SWT.None);
			mi.setText("MWSt. Satz für Artikel festlegen");
			mi.setEnabled(false);
			
			MenuItem vatNormal = new MenuItem(menu, SWT.RADIO);
			vatNormal.setText("Normal");
			vatNormal.setSelection(vatInfo.equals(VatInfo.VAT_CH_NOTMEDICAMENT));
			vatNormal.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					if (vatNormal.getSelection()) {
						ai.overrideVatInfo(VatInfo.VAT_CH_NOTMEDICAMENT);
						ModelServiceHolder.get().save(ai);
					}
				}
			});
			
			MenuItem vatReduced = new MenuItem(menu, SWT.RADIO);
			vatReduced.setText("Reduziert");
			vatReduced.setSelection(vatInfo.equals(VatInfo.VAT_CH_ISMEDICAMENT));
			vatReduced.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					if (vatReduced.getSelection()) {
						ai.overrideVatInfo(VatInfo.VAT_CH_ISMEDICAMENT);
						ModelServiceHolder.get().save(ai);
					}
				}
			});
			
			MenuItem vatNone = new MenuItem(menu, SWT.RADIO);
			vatNone.setText("Keine (0%)");
			vatNone.setSelection(vatInfo.equals(VatInfo.VAT_NONE));
			vatNone.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					if (vatNone.getSelection()) {
						ai.overrideVatInfo(VatInfo.VAT_NONE);
						ModelServiceHolder.get().save(ai);
					}
				}
			});
		}
	}
	
	@Override
	public boolean isDynamic(){
		return true;
	}
}
