/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.ui.cv;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.ATCCodeLanguageConstants;
import at.medevit.atc_codes.ATCCodeService;
import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.elexis.common.service.ATCCodeServiceHolder;
import at.medevit.ch.artikelstamm.model.common.preference.PreferenceConstants;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.CommonViewer.Message;
import ch.elexis.core.ui.util.viewers.CommonViewerContentProvider;

public class ATCMenuContributionItem extends ContributionItem {

	final private CommonViewer cov;
	final private String prefAtcLanguage;
	final private CommonViewerContentProvider commonViewerDataProvider;

	public ATCMenuContributionItem(CommonViewer cov, CommonViewerContentProvider commonViewerDataProvider) {
		this.cov = cov;
		this.commonViewerDataProvider = commonViewerDataProvider;

		prefAtcLanguage = ConfigServiceHolder.get().get(PreferenceConstants.PREF_ATC_CODE_LANGUAGE,
				ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN);
	}

	@Override
	public void fill(Menu menu, int index) {
		StructuredSelection structuredSelection = new StructuredSelection(cov.getSelection());
		Object element = structuredSelection.getFirstElement();

		ATCCodeService atcCodeService = ATCCodeServiceHolder.get().get();
		if (atcCodeService == null)
			return;

		if (element instanceof IArtikelstammItem) {
			final IArtikelstammItem ai = (IArtikelstammItem) element;
			List<ATCCode> atcHierarchy = atcCodeService.getHierarchyForATCCode(ai.getAtcCode());

			for (ATCCode atcCode : atcHierarchy) {
				MenuItem temp = new MenuItem(menu, SWT.PUSH);
				if (prefAtcLanguage.equals(ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN)) {
					temp.setText(atcCode.atcCode + StringUtils.SPACE + atcCode.name_german);
				} else {
					temp.setText(atcCode.atcCode + StringUtils.SPACE + atcCode.name);
				}
				final ATCCode tempC = atcCode;
				temp.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						commonViewerDataProvider.removeAllQueryFilterByType(AtcQueryFilter.class);
						AtcQueryFilter queryFilter = new AtcQueryFilter();
						queryFilter.setFilterValue(tempC.atcCode);
						commonViewerDataProvider.addQueryFilter(queryFilter);
						cov.notify(Message.update);
					}
				});
			}
		}

	}

	@Override
	public boolean isDynamic() {
		return true;
	}
}
