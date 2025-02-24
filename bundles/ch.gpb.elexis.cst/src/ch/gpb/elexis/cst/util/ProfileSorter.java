/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.util;

import java.text.Collator;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.gpb.elexis.cst.data.CstProfile;

public class ProfileSorter extends ViewerSorter {
	private int sortColumn = 0;
	private boolean sortReverse = false;

	public ProfileSorter() {
	}

	public ProfileSorter(Collator collator) {
		super(collator);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if ((e1 instanceof CstProfile) && (e2 instanceof CstProfile)) {
			CstProfile d1 = (CstProfile) e1;
			CstProfile d2 = (CstProfile) e2;
			String c1 = StringUtils.EMPTY;
			String c2 = StringUtils.EMPTY;
			switch (sortColumn) {
			case 0:
				c1 = d1.getName();
				c2 = d2.getName();
				break;
			case 1:
				c1 = d1.getDescription();
				c2 = d2.getDescription();
				break;
			case 2:
				c1 = d1.getValidFrom();
				c2 = d2.getValidFrom();
				break;
			}
			if (sortReverse) {
				return c1.compareTo(c2);
			} else {
				return c2.compareTo(c1);
			}
		}
		return 0;
	}

}
