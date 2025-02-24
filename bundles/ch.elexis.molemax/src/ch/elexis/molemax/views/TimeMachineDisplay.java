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

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.molemax.Messages;
import ch.elexis.molemax.data.Tracker;
import ch.rgw.tools.TimeTool;

public class TimeMachineDisplay extends ScrolledComposite {
	Overview parent;
	// List<Composite> images;
	Composite inlay;
	List<Tracker> myTracker;

	TimeMachineDisplay(final Overview parent, final Composite c) {
		super(c, SWT.V_SCROLL | SWT.H_SCROLL);
		this.parent = parent;
	}

	void setTracker(final List<Tracker> t) {
		myTracker = t;
		if (inlay != null) {
			inlay.dispose();
		}
		inlay = new Composite(this, SWT.NONE);
		inlay.setLayout(new GridLayout(2, false));
		setContent(inlay);
		int w = 300;
		int h = 0;
		Label lb = SWTHelper.createHyperlink(inlay, Messages.TimeMachineDisplay_back, new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				parent.setTopControl(parent.dispRow);
			}
		});
		lb.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		for (Tracker tracker : myTracker) {
			Composite cAnn = new Composite(inlay, SWT.BORDER);
			Composite cImg = new Composite(inlay, SWT.BORDER);
			cAnn.setLayout(new GridLayout());
			Image img = tracker.createImage();
			if (img != null) {
				ImageData imd = img.getImageData();
				cImg.setBounds(imd.x, imd.y, imd.width + 20, imd.height + 20);
				cImg.setBackgroundImage(img);
				TimeTool date = new TimeTool(tracker.get(Messages.TimeMachineDisplay_date));
				new Label(cAnn, SWT.NONE).setText(date.toString(TimeTool.DATE_GER));
				Text tAnn = SWTHelper.createText(cAnn, 8, SWT.BORDER);
				tAnn.setData(tracker);
				tAnn.setText(tracker.getInfoString("annotation"));
				tAnn.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(final FocusEvent e) {
						Text text = (Text) e.getSource();
						Tracker t = (Tracker) text.getData();
						String newMsg = text.getText();
						t.setInfoString("annotation", newMsg);
					}

				});

				GridData ldImg = new GridData(cImg.getBounds().width, cImg.getBounds().height);
				GridData ldAnn = new GridData(100, ldImg.heightHint);
				cAnn.setLayoutData(ldAnn);
				cImg.setLayoutData(ldImg);
				h = h + cImg.getBounds().height;
				int wTest = cImg.getBounds().width + 100;
				if (wTest > w) {
					w = wTest;
				}
			}
		}
		if (h == 0) {
			h = 20;
		}
		inlay.setBounds(0, 0, w, h);
		layout();
	}
}
