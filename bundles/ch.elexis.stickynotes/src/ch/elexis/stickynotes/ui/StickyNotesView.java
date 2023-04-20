/*******************************************************************************
 * Copyright (c) 2009-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    J, Kurath - Sponsoring
 *
 *******************************************************************************/

package ch.elexis.stickynotes.ui;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IUser;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.text.EnhancedTextField;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Patient;
import ch.elexis.stickynotes.Messages;
import ch.elexis.stickynotes.data.StickyNote;

public class StickyNotesView extends ViewPart implements IRefreshable, HeartListener {
	private ScrolledForm form;
	EnhancedTextField etf;
	Patient actPatient;
	StickyNote actNote;
	ConfigServicePreferenceStore prefs;

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this) {

		public void partDeactivated(IWorkbenchPartReference partRef) {
			if (actPatient != null) {
				if (actNote == null) {
					actNote = StickyNote.load(actPatient);
				}
				actNote.setText(etf.getContentsAsXML());
			}
		};

		public void partVisible(org.eclipse.ui.IWorkbenchPartReference partRef) {
			CoreHub.heart.addListener(StickyNotesView.this);
			super.partVisible(partRef);
		};

		public void partHidden(org.eclipse.ui.IWorkbenchPartReference partRef) {
			CoreHub.heart.removeListener(StickyNotesView.this);
		};
	};

	@Inject
	void activePatient(@Optional IPatient patient) {
		CoreUiUtil.runAsyncIfActive(() -> {
			if (patient != null) {
				doSelect((Patient) NoPoUtil.loadAsPersistentObject(patient));
			} else {
				deselect();
			}
		}, form);
	}

	@Inject
	void activeUser(@Optional IUser user) {
		Display.getDefault().asyncExec(() -> {
			if (user != null) {
				prefs = new ConfigServicePreferenceStore(Scope.USER);
			}
		});
	}

	@Override
	public void createPartControl(Composite parent) {
		prefs = new ConfigServicePreferenceStore(Scope.USER);
		form = UiDesk.getToolkit().createScrolledForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite body = form.getBody();
		body.setLayout(new GridLayout());
		etf = new EnhancedTextField(body);
		etf.connectGlobalActions(getViewSite());
		etf.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		getSite().getPage().addPartListener(udpateOnVisible);
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);
		super.dispose();
	}

	@Override
	public void setFocus() {
		etf.setFocus();
	}

	@Override
	public void refresh() {
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}

	private void deselect() {
		actNote = null;
		actPatient = null;
		etf.setText(StringUtils.EMPTY);
		// form.setText(Messages.StickyNotesView_NoPatientSelected);
		setPartName(Messages.StickyNotesView_StickyNotesName);
	}

	private void doSelect(Patient pat) {
		if (pat == null) {
			deselect();

		} else {

			actPatient = pat;
			actNote = StickyNote.load(actPatient);
			etf.setText(actNote.getText());
			// form.setText(actPatient.getLabel());
			setPartName(Messages.StickyNotesView_StickyNotesNameDash + actPatient.getLabel());
			RGB rgb = PreferenceConverter.getColor(prefs, Preferences.COLBACKGROUND);
			UiDesk.getColorRegistry().put(Preferences.COLBACKGROUND, rgb);
			Color back = UiDesk.getColorRegistry().get(Preferences.COLBACKGROUND);
			rgb = PreferenceConverter.getColor(prefs, Preferences.COLFOREGROUND);
			UiDesk.getColorRegistry().put(Preferences.COLFOREGROUND, rgb);
			Color fore = UiDesk.getColorRegistry().get(Preferences.COLFOREGROUND);
			etf.getControl().setBackground(back);
			etf.getControl().setForeground(fore);
		}
	}

	public void heartbeat() {
		if (actPatient == null) {
			actPatient = ElexisEventDispatcher.getSelectedPatient();
		}
		if (actPatient != null) {
			if (actNote == null) {
				actNote = StickyNote.load(actPatient);
			}
			if (actNote != null) {
				// TODO handle conflicts

			}
		}
	}

}
