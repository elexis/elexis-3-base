/*******************************************************************************
 * Copyright (c) 2012 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - reworked for Tarmed version 1.08
 ******************************************************************************/
package ch.elexis.views;

import java.time.LocalDate;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;

public class PhysioValidDateFilter extends ViewerFilter {

	private LocalDate validDate;
	private boolean doFilter = true;

	/**
	 * Show only positions valid at date. Set date to null to show all.
	 *
	 * @param date
	 */
	public void setValidDate(LocalDate date) {
		validDate = date;
	}

	public boolean getDoFilter() {
		return doFilter;
	}

	public void setDoFilter(boolean value) {
		doFilter = value;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		IPhysioLeistung leistung = (IPhysioLeistung) element;

		if (doFilter && validDate != null) {
			LocalDate validFrom = leistung.getValidFrom();
			LocalDate validTo = leistung.getValidTo();
			if (validFrom != null) {
				if (!(validDate.isAfter(validFrom) || validDate.equals(validFrom))) {
					return false;
				}
			}
			if (validTo != null) {
				if (!(validDate.isBefore(validTo) || validDate.equals(validTo))) {
					return false;
				}
			}
		}

		return true;
	}
}
