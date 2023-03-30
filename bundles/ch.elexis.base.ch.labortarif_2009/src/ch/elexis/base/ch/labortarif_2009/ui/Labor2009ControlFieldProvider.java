/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.base.ch.labortarif_2009.ui;

import java.time.LocalDate;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;

import ch.elexis.base.ch.labortarif.LabortarifPackage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;

public class Labor2009ControlFieldProvider extends DefaultControlFieldProvider {

	private LocalDate filterDate;

	public Labor2009ControlFieldProvider(CommonViewer viewer) {
		super(viewer, new String[] { "filter=Filter" }); //$NON-NLS-1$
		CoreUiUtil.injectServicesWithContext(this);
	}

	@Inject
	public void selectedEncounter(@Optional IEncounter encounter) {
		if (encounter != null) {
			this.filterDate = encounter.getDate();
			fireChangedEvent();
		} else {
			this.filterDate = null;
			fireChangedEvent();
		}
	}

	@Override
	public void setQuery(IQuery<?> query) {
		String[] values = getValues();
		if (values != null && values.length == 1) {
			String filterValue = values[0];
			String[] filterParts = filterValue.split(StringUtils.SPACE);
			if (filterParts != null) {
				for (String string : filterParts) {
					if (StringUtils.isNotBlank(string)) {
						if (Character.isDigit(string.charAt(0))) {
							query.and(ModelPackage.Literals.ICODE_ELEMENT__CODE, COMPARATOR.LIKE, string + "%", true); //$NON-NLS-1$
						} else {
							query.and("name", COMPARATOR.LIKE, string + "%", true); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}
			if (filterDate != null) {
				query.and(LabortarifPackage.Literals.ILABOR_LEISTUNG__VALID_FROM, COMPARATOR.LESS_OR_EQUAL, filterDate);
				query.startGroup();
				query.or(LabortarifPackage.Literals.ILABOR_LEISTUNG__VALID_TO, COMPARATOR.EQUALS, null);
				query.or(LabortarifPackage.Literals.ILABOR_LEISTUNG__VALID_TO, COMPARATOR.GREATER_OR_EQUAL, filterDate);
				query.andJoinGroups();
			}
		}
	}
}
