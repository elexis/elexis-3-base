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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.molemax.Messages;

public class RowDisplay extends Composite {
	SashForm sash;
	Overview parent;
	ScrolledComposite right;
	DetailDisplay rightContents;
	Composite left;

	List<ImageSlot> slots = new ArrayList<ImageSlot>(3);
	int actSlot;

	RowDisplay(final Overview parent, final Composite c) {
		super(c, SWT.NONE);
		this.parent = parent;
		setLayout(new FillLayout());
		sash = new SashForm(this, SWT.HORIZONTAL);
		left = new Composite(sash, SWT.NONE);
		left.setLayout(new GridLayout(1, false));
		SWTHelper.createHyperlink(left, Messages.RowDisplay_overview, new HyperlinkAdapter() {

			@Override
			public void linkActivated(final HyperlinkEvent e) {
				parent.setTopControl(parent.dispAll);
			}

		});
		right = new ScrolledComposite(sash, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		right.setAlwaysShowScrollBars(true);
		actSlot = 0;
		rightContents = new DetailDisplay(right, parent);
		right.setContent(rightContents);
		sash.setWeights(new int[] { 20, 80 });
	}

	public void setUser() {
		rightContents.setUser();
	}

	void setRow(final int start) {
		for (ImageSlot slot : slots) {
			slot.dispose();
		}
		slots.clear();
		for (int i = start; i < 12; i += 4) {
			ImageSlot slot = new ImageSlot(parent, left, i);
			slots.add(slot);

		}
		left.layout();
	}
}
