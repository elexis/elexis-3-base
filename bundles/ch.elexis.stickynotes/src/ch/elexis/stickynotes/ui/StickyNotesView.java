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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.text.EnhancedTextField;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Anwender;
import ch.elexis.data.Patient;
import ch.elexis.stickynotes.Messages;
import ch.elexis.stickynotes.data.StickyNote;

public class StickyNotesView extends ViewPart implements IActivationListener, HeartListener {
	private ScrolledForm form;
	EnhancedTextField etf;
	Patient actPatient;
	StickyNote actNote;
	ConfigServicePreferenceStore prefs;

	private final ElexisUiEventListenerImpl eeli_pat = new ElexisUiEventListenerImpl(Patient.class) {
		@Override
		public void runInUi(ElexisEvent ev) {
			if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
				doSelect((Patient) ev.getObject());
			} else if (ev.getType() == ElexisEvent.EVENT_DESELECTED) {
				deselect();
			}
		}
	};

	private final ElexisEventListenerImpl eeli_user = new ElexisEventListenerImpl(Anwender.class,
			ElexisEvent.EVENT_USER_CHANGED) {

		@Override
		public void catchElexisEvent(ElexisEvent ev) {
			prefs = new ConfigServicePreferenceStore(Scope.USER);
		}

	};

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
		GlobalEventDispatcher.addActivationListener(this, this);

	}

	@Override
	public void dispose() {
		GlobalEventDispatcher.removeActivationListener(this, this);
		super.dispose();
	}

	@Override
	public void setFocus() {
		etf.setFocus();
	}

	public void activation(boolean mode) {
		if ((mode == false) && etf.isDirty()) {
			if (actPatient != null) {
				if (actNote == null) {
					actNote = StickyNote.load(actPatient);
				}
				actNote.setText(etf.getContentsAsXML());
			}
		}

	}

	public void visible(boolean mode) {
		if (mode) {
			eeli_pat.catchElexisEvent(ElexisEvent.createPatientEvent());
			eeli_user.catchElexisEvent(ElexisEvent.createUserEvent());
			ElexisEventDispatcher.getInstance().addListeners(eeli_pat, eeli_user);
			CoreHub.heart.addListener(this);
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(eeli_pat, eeli_user);
			CoreHub.heart.removeListener(this);
		}

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
