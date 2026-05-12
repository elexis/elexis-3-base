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

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.core.findings.ICoding;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.ICodeElementService;

public class TardocValidDignitaetFilter extends ViewerFilter {

	private ICodeElementService codeElementService;

	private IMandator mandator;
	private List<ICoding> tardocSpecialist;

	private boolean doFilter = true;

	public boolean getDoFilter() {
		return doFilter;
	}

	public void setDoFilter(boolean value) {
		doFilter = value;
	}

	private synchronized ICodeElementService getCodeElementService() {
		if (codeElementService == null) {
			codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).orElse(null);
		}
		return codeElementService;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		ITardocLeistung leistung = (ITardocLeistung) element;

		if (doFilter) {
			String digni = leistung.getDigniQuali();
			if (StringUtils.isNotBlank(digni) && !digni.contains("9999")) {
				if (mandator != null) {
					if (!tardocSpecialist.stream().anyMatch(c -> digni.contains(c.getCode()))) {
						List<ICodeElement> acquiredRights = ArzttarifeUtil
								.getMandantTardocAcquiredRights(mandator, getCodeElementService());
						if (acquiredRights != null && !acquiredRights.isEmpty()) {
							Optional<ICodeElement> found = acquiredRights.stream()
									.filter(ce -> ce.getCodeSystemName().equals(leistung.getCodeSystemName())
											&& ce.getCode().equals(leistung.getCode()))
									.findAny();
							if (found.isPresent()) {
								return true;
							}
						}
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Set the {@link IMandator} that will be used to filter TARDOC elements by
	 * tardoc specialist code and acquired rights.
	 * 
	 * @param mandator
	 */
	public void setMandator(IMandator mandator) {
		this.mandator = mandator;
		if (mandator != null) {
			tardocSpecialist = ArzttarifeUtil.getMandantTardocSepcialist(mandator);
		}
	}

	/**
	 * Test if the current {@link IMandator} equals the provided {@link IMandator}.
	 * 
	 * @param mandator
	 * @return
	 */
	public boolean isEqualMandator(IMandator mandator) {
		if (this.mandator != null && mandator != null) {
			return this.mandator.getId().equals(mandator.getId());
		}
		return false;
	}
}
