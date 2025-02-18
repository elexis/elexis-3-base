/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.notes;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;

/**
 * The left side of the notes View: Listing of all Notes and Search field
 *
 * @author gerry
 *
 */
public class NotesList extends Composite {
	TreeViewer tv;
	Composite parent;
	Text tFilter;
	String filterExpr;
	NotesFilter notesFilter = new NotesFilter();
	HashMap<Note, String> matches = new HashMap<Note, String>();

	NotesList(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout());
		this.parent = parent;
		Composite cFilter = new Composite(this, SWT.NONE);
		cFilter.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cFilter.setLayout(new GridLayout(3, false));
		ImageHyperlink clearSearchFieldHyperlink = new ImageHyperlink(cFilter, SWT.NONE);
		clearSearchFieldHyperlink.setImage(Images.IMG_CLEAR.getImage());
		clearSearchFieldHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				tFilter.setText(StringUtils.EMPTY);
				filterExpr = StringUtils.EMPTY;
				matches.clear();
				tv.collapseAll();
				tv.removeFilter(notesFilter);
			}
		});
		new Label(cFilter, SWT.NONE).setText(Messages.NotesList_searchLabel);
		tFilter = new Text(cFilter, SWT.SINGLE);
		tFilter.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				filterExpr = tFilter.getText().toLowerCase();
				matches.clear();
				if (filterExpr.length() == 0) {
					tv.removeFilter(notesFilter);
					tv.collapseAll();
				} else {
					tv.addFilter(notesFilter);
					tv.expandAll();
				}

			}
		});
		tv = new TreeViewer(this, SWT.NONE);
		tv.getControl().setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tv.setContentProvider(new NotesContentProvider());
		tv.setLabelProvider(new DefaultLabelProvider());
		tv.setUseHashlookup(true);
		tv.setInput(parent);
		tv.addSelectionChangedListener(GlobalEventDispatcher.getInstance().getDefaultListener());
	}

	public void dispose() {
		tv.removeSelectionChangedListener(GlobalEventDispatcher.getInstance().getDefaultListener());
	}

	class NotesFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {

			if (filterExpr.length() == 0) {
				return true;
			}
			boolean bMatch = isMatch((Note) element, filterExpr);
			if (bMatch) {
				Note parent = (Note) element;
				while ((parent = parent.getParent()) != null) {
					matches.put(parent, filterExpr);
				}
			}
			return bMatch;
		}

		private boolean isMatch(Note n, String t) {
			if (matches.get(n) != null) {
				return true;
			}

			String lbl = n.getLabel().toLowerCase();
			if (lbl.startsWith(t) || n.getKeywords().contains(t)) {
				matches.put(n, t);

				return true;
			}

			List<Note> l = n.getChildren();
			for (Note note : l) {
				if (isMatch(note, t)) {
					matches.put(n, t);
					return true;
				}
			}
			return false;
		}

	}

}
