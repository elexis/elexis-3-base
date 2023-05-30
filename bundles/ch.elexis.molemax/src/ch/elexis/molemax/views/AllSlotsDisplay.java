/*******************************************************************************
 * Copyright (c) 2007-2014 G. Weirich, A. Brögli and A. Häffner.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    rgw - initial API and implementation
 *    rgw - 2014: Changes for Elexis 2.x
 ******************************************************************************/
package ch.elexis.molemax.views;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.molemax.Messages;
import ch.elexis.molemax.data.MolemaxACL;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.TimeTool;

public class AllSlotsDisplay extends Composite {
	FormToolkit tk = UiDesk.getToolkit();
	Overview parent;
	ImageSlot[] slots = new ImageSlot[12];
	Composite self;
	RowSelector hl;

	AllSlotsDisplay(final Overview parent, final Composite c) {
		super(c, SWT.NONE);
		self = this;
		this.parent = parent;
		setLayout(new GridLayout(4, true));
		hl = new RowSelector();
		Hyperlink hlLinks = tk.createHyperlink(this, Messages.AllSlotsDisplay_left, SWT.CENTER);
		hlLinks.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		hlLinks.addHyperlinkListener(hl);
		Hyperlink hlVorne = tk.createHyperlink(this, Messages.AllSlotsDisplay_front, SWT.CENTER);
		hlVorne.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		hlVorne.addHyperlinkListener(hl);
		Hyperlink hlRechts = tk.createHyperlink(this, Messages.AllSlotsDisplay_right, SWT.CENTER);
		hlRechts.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		hlRechts.addHyperlinkListener(hl);
		Hyperlink hlHinten = tk.createHyperlink(this, Messages.AllSlotsDisplay_back, SWT.CENTER);
		hlHinten.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		hlHinten.addHyperlinkListener(hl);
	}

	@Override
	public void dispose() {
		if (slots != null) {
			for (ImageSlot is : slots) {
				if (!is.isDisposed()) {
					is.dispose();
				}
			}
		}
		super.dispose();
	}

	void reload() {

		UiDesk.asyncExec(new Runnable() {
			public void run() {
				for (int i = 0; i < 12; i++) {
					if (slots[i] != null) {
						slots[i].dispose();
						slots[i] = null;
					}
				}
				for (int i = 0; i < 12; i++) {
					slots[i] = new ImageSlot(parent, self, i);
				}
				layout();
			}

		});

	}

	public void setUser() {
		for (ImageSlot slot : slots) {
			if (slot != null) {
				slot.setUser();
			}
		}
	}

	public void addImageFromSequence(final int slot, final File file) {
		slots[slot].setImage(file);
	}

	public void addImage(final String date, final int slot, final File file) {
		if (AccessControlServiceHolder.get().request(MolemaxACL.CHANGE_IMAGES)) {
			TimeTool ttDate = new TimeTool(date);
			parent.setPatient(parent.pat, ttDate.toString(TimeTool.DATE_GER));
			slots[slot].setImage(file);
		}
	}

	class RowSelector extends HyperlinkAdapter {
		@Override
		public void linkActivated(final HyperlinkEvent e) {
			String text = e.getLabel();
			if (text.equalsIgnoreCase(Messages.AllSlotsDisplay_left)) {
				parent.dispRow.setRow(0);
			} else if (text.equalsIgnoreCase(Messages.AllSlotsDisplay_front)) {
				parent.dispRow.setRow(1);
			} else if (text.equalsIgnoreCase(Messages.AllSlotsDisplay_right)) {
				parent.dispRow.setRow(2);
			} else {
				parent.dispRow.setRow(3);
			}
			parent.setTopControl(parent.dispRow);
		}

	}
}
