/*******************************************************************************
 * Copyright (c) 2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.icpc.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.icpc.IcpcCode;

public class ICPCCodesView extends ViewPart {
	public static final String ID = "ch.elexis.icpc.codesView";
	String mode;
	ChapterDisplay[] chapters;
	ShortlistComposite shortlistComposite;
	CTabFolder ctab;
	
	public ICPCCodesView(){}
	
	@Override
	public void createPartControl(Composite parent){
		ctab = new CTabFolder(parent, SWT.NONE);
		chapters = new ChapterDisplay[IcpcCode.classes.length];
		
		for (String chapter : IcpcCode.classes) {
			CTabItem item = new CTabItem(ctab, SWT.NONE);
			item.setText(chapter.substring(0, 1));
			item.setToolTipText(chapter.substring(3));
		}
		
		CTabItem shortlistCTabItem = new CTabItem(ctab, SWT.NONE);
		shortlistCTabItem.setText("Shortlist");
		shortlistComposite = new ShortlistComposite(ctab, SWT.NONE);
		shortlistCTabItem.setControl(shortlistComposite);
		
		ctab.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e){
				int idx = ctab.getSelectionIndex();
				if (idx >= IcpcCode.classes.length) {
					// if shortlist
					return;
				}
				if (chapters[idx] == null) {
					chapters[idx] = new ChapterDisplay(ctab, IcpcCode.classes[idx]);
					ctab.getItem(idx).setControl(chapters[idx]);
				}
				chapters[idx].setComponent(mode);
			}
			
		});
		ctab.setSelection(IcpcCode.classes.length);
	}
	
	@Override
	public void setFocus(){
		ctab.setFocus();
	}
	
	public void setComponent(String mode){
		this.mode = mode;
		int idx = ctab.getSelectionIndex();
		if (idx > -1 && idx < chapters.length) {
			chapters[ctab.getSelectionIndex()].setComponent(mode);
		}
	}
	
}
