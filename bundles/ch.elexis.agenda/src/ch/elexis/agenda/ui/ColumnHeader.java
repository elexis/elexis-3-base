/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Sponsoring:
 * 	 mediX Notfallpaxis, diepraxen Stauffacher AG, Zürich
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.agenda.ui;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import ch.elexis.actions.Activator;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.StringTool;

/**
 * The header above the resource columns with the name of the resources (and
 * probably later more elements)
 *
 * @author gerry
 *
 */
public class ColumnHeader extends Composite {
	AgendaParallel view;
	static final String IMG_PERSONS_NAME = Activator.PLUGIN_ID + "/personen"; //$NON-NLS-1$
	static final String IMG_PERSONS_PATH = "icons/personen.png"; //$NON-NLS-1$
	ImageHyperlink ihRes;

	ColumnHeader(Composite parent, AgendaParallel v) {
		super(parent, SWT.NONE);
		view = v;

		if (UiDesk.getImage(IMG_PERSONS_NAME) == null) {
			UiDesk.getImageRegistry().put(IMG_PERSONS_NAME, Activator.getImageDescriptor(IMG_PERSONS_PATH));
		}
		ihRes = new ImageHyperlink(this, SWT.NONE);
		ihRes.setImage(UiDesk.getImage(IMG_PERSONS_NAME));
		ihRes.setToolTipText(Messages.ColumnHeader_selectMandatorToShow);
		ihRes.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				new SelectResourceDlg().open();
			}

		});

	}

	void recalc(double widthPerColumn, int left_offset, int padding, int textSize) {
		GridData gd = (GridData) getLayoutData();
		gd.heightHint = textSize + 2;
		for (Control c : getChildren()) {
			if (c instanceof Label) {
				c.dispose();
			}
		}
		Point bSize = ihRes.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		ihRes.setBounds(0, 0, bSize.x, bSize.y);
		String[] labels = view.getDisplayedResources();
		int count = labels.length;
		for (int i = 0; i < count; i++) {
			int lx = left_offset + (int) Math.round(i * (widthPerColumn + padding));
			Label l = new Label(this, SWT.NONE);
			l.setText(labels[i]);
			int outer = (int) Math.round(widthPerColumn);
			int inner = l.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			int off = (outer - inner) / 2;
			lx += off;
			l.setBounds(lx, 0, inner, textSize + 2);
		}
	}

	class SelectResourceDlg extends TitleAreaDialog {

		public SelectResourceDlg() {
			super(ColumnHeader.this.getShell());
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite ret = (Composite) super.createDialogArea(parent);
			String[] displayed = view.getDisplayedResources();
			for (String r : Activator.getDefault().getResources()) {
				Button b = new Button(ret, SWT.CHECK);
				b.setText(r);
				if (StringTool.getIndex(displayed, r) != -1) {
					b.setSelection(true);
				}
				b.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
			}
			return ret;
		}

		@Override
		public void create() {
			super.create();
			getShell().setText(Messages.ColumnHeader_Mandantors);
			setTitle(Messages.ColumnHeader_mandatorsForParallelView);
			setMessage(Messages.ColumnHeader_selectMandators);
		}

		@Override
		protected void okPressed() {
			Composite dlg = (Composite) getDialogArea();
			String[] res = Activator.getDefault().getResources();
			ArrayList<String> sel = new ArrayList<String>(res.length);
			for (Control c : dlg.getChildren()) {
				if (c instanceof Button) {
					if (((Button) c).getSelection()) {
						sel.add(((Button) c).getText());
					}
				}
			}
			view.clear();
			CoreHub.localCfg.set(PreferenceConstants.AG_RESOURCESTOSHOW, StringTool.join(sel, ",")); //$NON-NLS-1$
			view.refresh();

			super.okPressed();
		}

		@Override
		protected boolean isResizable() {
			return true;
		}
	}
}
