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
import ch.elexis.util.SWTHelper;

public class ICPCCodesView extends ViewPart {
	public static final String ID = "ch.elexis.icpc.codesView";
	String mode;
	ChapterDisplay[] chapters;
	CTabFolder ctab;
	
	public ICPCCodesView(){}
	
	@Override
	public void createPartControl(Composite parent){
		ctab = new CTabFolder(parent, SWT.NONE);
		ctab.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		chapters = new ChapterDisplay[IcpcCode.classes.length];
		for (String chapter : IcpcCode.classes) {
			CTabItem item = new CTabItem(ctab, SWT.NONE);
			item.setText(chapter.substring(0, 1));
			item.setToolTipText(chapter.substring(3));
		}
		ctab.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e){
				int idx = ctab.getSelectionIndex();
				if (chapters[idx] == null) {
					chapters[idx] = new ChapterDisplay(ctab, IcpcCode.classes[idx]);
					ctab.getItem(idx).setControl(chapters[idx]);
				}
				chapters[idx].setComponent(mode);
			}
			
		});
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
	public void setComponent(String mode){
		this.mode = mode;
		int idx = ctab.getSelectionIndex();
		if (idx > -1 && idx < chapters.length) {
			chapters[ctab.getSelectionIndex()].setComponent(mode);
		}
	}
	
}
