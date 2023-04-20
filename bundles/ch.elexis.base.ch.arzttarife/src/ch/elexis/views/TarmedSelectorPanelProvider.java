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

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;

public class TarmedSelectorPanelProvider extends SelectorPanelProvider {
	private static FieldDescriptor<?>[] fields = {
			new FieldDescriptor<ITarmedLeistung>("Ziffer", "code_", Typ.STRING, null),
			new FieldDescriptor<ITarmedLeistung>("Text", "tx255", Typ.STRING, null) };

	private CommonViewer commonViewer;
	private StructuredViewer viewer;

	private TarmedLawFilter lawFilter = new TarmedLawFilter();
	private TarmedValidDateFilter validDateFilter = new TarmedValidDateFilter();

	private IEncounter previousKons;
	private ICoverage previousFall;
	private boolean dirty;

	public TarmedSelectorPanelProvider(CommonViewer viewer) {
		super(fields, true);
		commonViewer = viewer;
		CoreUiUtil.injectServicesWithContext(this);
	}

	@Inject
	public void selectedEncounter(@Optional IEncounter encounter) {
		if (encounter != null) {
			updateLawFilter(encounter);
			updateValidFilter(encounter);
			updateDirty(encounter);
		} else {
			// clear filters
			lawFilter.setLaw(null);
			validDateFilter.setValidDate(null);
			updateDirty(null);
		}
	}

	@Optional
	@Inject
	void udpateEncounter(@UIEventTopic(ElexisEventTopics.EVENT_UPDATE) IEncounter encounter) {
		if (encounter != null) {
			updateLawFilter(encounter);
			updateValidFilter(encounter);
			updateDirty(encounter);
		}
	}

	@Override
	public void setFocus() {
		super.setFocus();
		if (viewer == null) {
			java.util.Optional<IEncounter> selectedEncounter = ContextServiceHolder.get().getTyped(IEncounter.class);
			viewer = commonViewer.getViewerWidget();
			selectedEncounter.ifPresent(encounter -> updateLawFilter(encounter));
			viewer.addFilter(lawFilter);
			selectedEncounter.ifPresent(encounter -> updateValidFilter(encounter));
			viewer.addFilter(validDateFilter);
		}
		refreshViewer();
	}

	private void refreshViewer() {
		if (viewer != null && dirty) {
			dirty = false;
			viewer.getControl().setRedraw(false);
			viewer.setSelection(new StructuredSelection());
			viewer.refresh();
			viewer.getControl().setRedraw(true);
		}
	}

	private void updateValidFilter(IEncounter encounter) {
		validDateFilter.setValidDate(encounter.getDate());
	}

	private void updateLawFilter(IEncounter encounter) {
		ICoverage coverage = encounter.getCoverage();
		String law = StringUtils.EMPTY;
		if (coverage != null) {
			String konsLaw = coverage.getBillingSystem().getLaw().name();
			if (ArzttarifeUtil.isAvailableLaw(konsLaw)) {
				law = konsLaw;
			}
		}
		lawFilter.setLaw(law);
	}

	private void updateDirty(IEncounter encounter) {
		if (encounter != previousKons) {
			dirty = true;
			previousKons = encounter;
		}
		if (encounter != null && encounter.getCoverage() != previousFall) {
			dirty = true;
			previousFall = encounter.getCoverage();
		}
	}

	public void toggleFilters() {
		validDateFilter.setDoFilter(!validDateFilter.getDoFilter());
		lawFilter.setDoFilter(!lawFilter.getDoFilter());
		dirty = true;
		refreshViewer();
	}
}
