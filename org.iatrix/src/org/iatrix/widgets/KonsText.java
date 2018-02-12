/*******************************************************************************
 * Copyright (c) 2007-2015, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     Gerry Weirich - adapted for 2.1
 *     Niklaus Giger - small improvements, split into 20 classes
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.widgets;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.iatrix.data.KonsTextLock;
import org.iatrix.dialogs.ChooseKonsRevisionDialog;
import org.iatrix.util.Heartbeat;
import org.iatrix.util.Heartbeat.IatrixHeartListener;
import org.iatrix.util.Helpers;
import org.iatrix.views.JournalView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.text.model.Samdas;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.constants.ExtensionPointConstantsUi;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.text.EnhancedTextField;
import ch.elexis.core.ui.util.IKonsExtension;
import ch.elexis.core.ui.util.IKonsMakro;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Anwender;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionedResource;
import ch.rgw.tools.VersionedResource.ResourceItem;

public class KonsText implements IJournalArea {

	private static Konsultation actKons = null;
	private static int konsTextSaverCount = 0;
	private static Logger log = LoggerFactory.getLogger(org.iatrix.widgets.KonsText.class);
	private static EnhancedTextField text;
	private static Label lVersion = null;
	private static Label lKonsLock = null;
	private static KonsTextLock konsTextLock = null;
	private static String unable_to_save_kons_id = "";
	int displayedVersion;
	private Action purgeAction;
	private IAction saveAction;
	private Action chooseVersionAction;
	private Action versionFwdAction;
	private Action versionBackAction;
	private final FormToolkit tk;
	private Composite parent;
	private Hashtable<String, IKonsExtension> hXrefs;

	public KonsText(Composite parentComposite){
		parent = parentComposite;
		tk = UiDesk.getToolkit();
		SashForm konsultationSash = new SashForm(parent, SWT.HORIZONTAL);
		konsultationSash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite konsultationTextComposite = tk.createComposite(konsultationSash);
		konsultationTextComposite.setLayout(new GridLayout(1, true));
		text = new EnhancedTextField(konsultationTextComposite);
		hXrefs = new Hashtable<>();
		@SuppressWarnings("unchecked")
		List<IKonsExtension> listKonsextensions = Extensions.getClasses(
			Extensions.getExtensions(ExtensionPointConstantsUi.KONSEXTENSION), "KonsExtension", //$NON-NLS-1$ //$NON-NLS-2$
			false);
		for (IKonsExtension x : listKonsextensions) {
			String provider = x.connect(text);
			hXrefs.put(provider, x);
		}
		text.setXrefHandlers(hXrefs);
		@SuppressWarnings("unchecked")
		List<IKonsMakro> makros = Extensions.getClasses(
			Extensions.getExtensions(ExtensionPointConstantsUi.KONSEXTENSION), "KonsMakro", false); //$NON-NLS-1$
		text.setExternalMakros(makros);

		text.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		makeActions();

		text.getControl().addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e){
				logEvent("widgetDisposed removeKonsTextLock");
				updateEintrag();
				removeKonsTextLock();
			}

		});
		tk.adapt(text);

		lVersion = tk.createLabel(konsultationTextComposite, "<aktuell>");
		lVersion.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		lKonsLock = tk.createLabel(konsultationTextComposite, "");
		lKonsLock.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		lKonsLock.setForeground(lKonsLock.getDisplay().getSystemColor(SWT.COLOR_RED));
		lKonsLock.setVisible(false);
		registerUpdateHeartbeat();
	}

	public Action getPurgeAction(){
		return purgeAction;
	}

	public IAction getSaveAction(){
		return saveAction;
	}

	public Action getChooseVersionAction(){
		return chooseVersionAction;
	}

	public Action getVersionForwardAction(){
		return versionFwdAction;
	}

	public Action getVersionBackAction(){
		return versionBackAction;
	}

	private void showUnableToSaveKons(String plain, String errMsg) {
		logEvent("showUnableToSaveKons errMsg: " + errMsg + " plain: " + plain);
		if (plain.length() == 0 ) {
			log.warn("showUnableToSaveKons Inhalt war leer");
			return;
		}
		boolean added = false;
		try {
			Clipboard clipboard = new Clipboard(UiDesk.getDisplay());
			TextTransfer textTransfer = TextTransfer.getInstance();
			Transfer[] transfers = new Transfer[] {	textTransfer };
			Object[] data = new Object[] { plain };
			clipboard.setContents(data, transfers);
			clipboard.dispose();
			added = true;
		} catch (Exception ex) {
			log.error("Fehlerhaftes clipboard " + plain);
		}
		StringBuilder sb = new StringBuilder();
		if (plain.length() > 0 && added) {
			sb.append("Inhalt wurde in die Zwischenablage aufgenommen\n");
		}
		sb.append("Patient: " + actKons.getFall().getPatient().getPersonalia()+ "\n");
		sb.append("\nInhalt ist:\n---------------------------------------------------\n");
		sb.append(plain);
		sb.append("\n----------------------------------------------------------------\n");
		SWTHelper.alert("Konnte Konsultationstext nicht abspeichern", sb.toString());
	}
	public synchronized void updateEintrag(){
		if (actKons != null) {
			if (actKons.getFall() == null) {
				return;
			}
			if (!Helpers.userMayEditKons(actKons)) {
				logEvent(String.format("skip updateEintrag as userMay not Edit dirty %s changed %s ", text.isDirty(), textChanged()));
			} else  if (text.isDirty() || textChanged()) {
				int old_version = actKons.getHeadVersion();
				String plain = text.getContentsPlaintext();
				logEvent("updateEintrag old_version " + old_version + " " +
				actKons.getId() + " dirty " + text.isDirty() + " changed " + textChanged());
				if (hasKonsTextLock()) {
					if (!actKons.isEditable(false)) {
						String notEditable = "Aktuelle Konsultation kannn nicht editiert werden";
						showUnableToSaveKons(plain, notEditable);
					} else  {
						actKons.updateEintrag(text.getContentsAsXML(), false);
						ElexisEventDispatcher.getInstance().fire(
                            new ElexisEvent(actKons, Konsultation.class, ElexisEvent.EVENT_UPDATE));
						int new_version = actKons.getHeadVersion();
						String samdasText = (new Samdas(actKons.getEintrag().getHead()).getRecordText());
						if (new_version <= old_version || !plain.equals(samdasText)) {
							if (!unable_to_save_kons_id.equals(actKons.getId())) {
								String errMsg = "Unable to update: old_version " +
										old_version + " " + plain +
										" new_version " + new_version + " " + samdasText ;
								logEvent("updateEintrag " + errMsg + plain);
								showUnableToSaveKons(plain, errMsg);
								unable_to_save_kons_id = actKons.getId();
							}
						} else {
							unable_to_save_kons_id = "";
						}
					}
				} else {
					// should never happen...
					String errMsg = "Konsultation gesperrt old_version " + old_version +
							"konsTextLock " + (konsTextLock == null ? "null" :
							"key " + konsTextLock.getKey() + " lock " + konsTextLock.getLockValue());
					showUnableToSaveKons(plain, errMsg);
				}
			}
		}
		text.setDirty(false);
		updateKonsVersionLabel();
	}

	/**
	 * Check whether the text in the text field has changed compared to the database entry.
	 *
	 * @return true, if the text changed, false else
	 */
	private boolean textChanged(){
		if (actKons == null || text == null || actKons.getEintrag() == null) {
			return false;
		}
		String dbEintrag = actKons.getEintrag().getHead();
		String textEintrag = text.getContentsAsXML();
		if (textEintrag != null) {
			if ( dbEintrag  != null && !textEintrag.equals(dbEintrag)) {
				// text differs from db entry
				log.debug("textChanged {}: saved text != db entry. Length {} == {}?. Is now '{}'", actKons.getId(),
					textEintrag.length(), dbEintrag.length(), getPlainBegining());
				return true;
			}
		}
		return false;
	}

	private void updateKonsLockLabel(){
		if (konsTextLock == null || hasKonsTextLock()) {
			lKonsLock.setVisible(false);
			lKonsLock.setText("");
		} else {
			Anwender user = konsTextLock.getLockValue().getUser();
			StringBuilder text = new StringBuilder();
			if (user != null && user.exists()) {
				text.append(
					"Konsultation wird von Benutzer \"" + user.getLabel() + "\" bearbeitet.");
			} else {
				text.append("Konsultation wird von anderem Benutzer bearbeitet.");
			}

			text.append(" Rechner \"" + konsTextLock.getLockValue().getHost() + "\".");
			log.debug("updateKonsLockLabel: " + text.toString());
			lKonsLock.setText(text.toString());
			lKonsLock.setVisible(true);
		}

		lKonsLock.getParent().layout();
	}

	// helper method to create a KonsTextLock object in a save way
	// should be called when a new konsultation is set
	private  synchronized void createKonsTextLock(){
		// remove old lock

		removeKonsTextLock();

		if (actKons != null && CoreHub.actUser != null) {
			konsTextLock = new KonsTextLock(actKons, CoreHub.actUser);
		} else {
			konsTextLock = null;
		}

		if (konsTextLock != null) {
			konsTextLock.lock();
			// boolean success = konsTextLock.lock();
			// logEvent(
			// "createKonsTextLock: konsText locked (" + success + ")" + konsTextLock.getKey());
		}
	}

	// helper method to release a KonsTextLock
	// should be called before a new konsultation is set
	// or the program/view exits
	private synchronized void removeKonsTextLock(){
		if (konsTextLock != null) {
			konsTextLock.unlock();
			// boolean success = konsTextLock.unlock();
			// logEvent(
			// "removeKonsTextLock: konsText unlocked (" + success + ") " + konsTextLock.getKey());
			konsTextLock = null;
		}
	}

	/**
	 * Check whether we own the lock
	 *
	 * @return true, if we own the lock, false else
	 */
	private synchronized boolean hasKonsTextLock(){
		return true;
		// return (konsTextLock != null && konsTextLock.isLocked());
	}

	@Override
	public synchronized void visible(boolean mode){
	}

	private void makeActions(){
		// Konsultationstext

		purgeAction = new Action("Alte Eintragsversionen entfernen") {
			@Override
			public void run(){
				actKons.purgeEintrag();
				ElexisEventDispatcher.getInstance().fire(
                    new ElexisEvent(actKons, Konsultation.class, ElexisEvent.EVENT_UPDATE));
			}
		};
		versionBackAction = new Action("Vorherige Version") {
			@Override
			public void run(){
				if (MessageDialog.openConfirm(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Konsultationstext ersetzen",
					"Wollen Sie wirklich den aktuellen Konsultationstext gegen eine frühere Version desselben Eintrags ersetzen?")) {
					setKonsText(actKons, displayedVersion - 1, false);
					ElexisEventDispatcher.getInstance().fire(
                        new ElexisEvent(actKons, Konsultation.class, ElexisEvent.EVENT_UPDATE));
				}
			}
		};
		versionFwdAction = new Action("nächste Version") {
			@Override
			public void run(){
				if (MessageDialog.openConfirm(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Konsultationstext ersetzen",
					"Wollen Sie wirklich den aktuellen Konsultationstext gegen eine spätere Version desselben Eintrags ersetzen?")) {
					setKonsText(actKons, displayedVersion + 1, false);
					ElexisEventDispatcher.getInstance().fire(
                        new ElexisEvent(actKons, Konsultation.class, ElexisEvent.EVENT_UPDATE));
				}
			}
		};
		chooseVersionAction = new Action("Version wählen...") {
			@Override
			public void run(){
				ChooseKonsRevisionDialog dlg = new ChooseKonsRevisionDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), actKons);
				if (dlg.open() == ChooseKonsRevisionDialog.OK) {
					int selectedVersion = dlg.getSelectedVersion();

					if (MessageDialog.openConfirm(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"Konsultationstext ersetzen",
						"Wollen Sie wirklich den aktuellen Konsultationstext gegen die Version "
							+ selectedVersion + " desselben Eintrags ersetzen?")) {
						setKonsText(actKons, selectedVersion, false);
						ElexisEventDispatcher.getInstance().fire(
                            new ElexisEvent(actKons, Konsultation.class, ElexisEvent.EVENT_UPDATE));
					}
				}

			}
		};

		saveAction = new Action("Konstext sichern") {
			{
				setImageDescriptor(Images.IMG_DISK.getImageDescriptor());
				setToolTipText("Konsultationstext explizit speichern");
			}

			@Override
			public void run(){
				logEvent("saveAction: " + actKons);
				if (actKons != null) {
					updateEintrag();
					JournalView.updateAllKonsAreas(actKons.getFall().getPatient(), actKons, KonsActions.SAVE_KONS);
				}
			}
		};
	};

	private void updateKonsultation(boolean updateText){
		if (actKons != null) {
			if (updateText) {
				setKonsText(actKons, actKons.getHeadVersion(), true);
			}
			logEvent("updateKonsultation: " + actKons.getId());
		} else {
			setKonsText(null, 0, true);
			logEvent("updateKonsultation: null");
		}
	}

	/**
	 * Aktuelle Konsultation setzen.
	 *
	 * Wenn eine Konsultation gesetzt wird stellen wir sicher, dass der gesetzte Patient zu dieser
	 * Konsultation gehoert. Falls nicht, wird ein neuer Patient gesetzt.
	 * @param putCaretToEnd
	 *            if true, activate text field ant put caret to the end
	 */
	@Override
	public synchronized void setKons(Patient newPatient, Konsultation k, KonsActions op){
		Helpers.checkActPatKons(newPatient, k);
		if (op == KonsActions.SAVE_KONS) {
			if (text.isDirty() || textChanged()) {
				logEvent("setKons.SAVE_KONS text.isDirty or changed saving Kons from "
					+ actKons.getDatum() + " is '" + text.getContentsPlaintext() + "' by " + actKons.getAuthor());
				updateEintrag();
			} else {
				if (actKons != null && text != null) {
					logEvent("setKons.SAVE_KONS nothing to save for Kons from " + actKons.getDatum()
						+ " is '" + text.getContentsPlaintext() + "' by"+ actKons.getAuthor());
				}
			}
			return;
		}
		boolean hasTextChanges = false;
		// make sure to unlock the kons edit field and release the lock
		if (text != null && actKons != null) {
			hasTextChanges = textChanged() ;
			logEvent(String.format("op %s same? %s text.isDirty %s hasTextChanges %s actKons vom:  %s",
				op, Helpers.twoKonsEqual(actKons, k), text.isDirty(), hasTextChanges, actKons.getDatum()));
			if (hasTextChanges) {
				updateEintrag();
			}
		}
		if (!Helpers.twoKonsEqual(actKons, k)) {
			removeKonsTextLock();
		}
		if (k == null) {
			actKons = k;
			logEvent("setKons null");
		} else {
			logEvent("setKons " + (actKons == null ? "null" : actKons.getId()) +
				" => " + k.getId());
			actKons = k;
			setKonsText(actKons, actKons.getHeadVersion(), true);
			boolean konsEditable = Helpers.hasRightToChangeConsultations(actKons, false);
			if (!konsEditable) {
				// isEditable(true) would give feedback to user why consultation
				// cannot be edited, but this often very shortlived as we create/switch
				// to a newly created kons of today
				logEvent("setKons actKons is not editable");
				updateKonsultation(true);
				updateKonsLockLabel();
				updateKonsVersionLabel();
				lVersion.setText(lVersion.getText() + " Nicht editierbar. (Keine Zugriffsrechte oder schon verrechnet)");
				return;
			} else if (actKons.getMandant().getId().contentEquals(CoreHub.actMandant.getId())) {
				createKonsTextLock();
			}
		}
		updateKonsultation(true);
		updateKonsLockLabel();
		updateKonsVersionLabel();
		saveAction.setEnabled(konsTextLock == null || hasKonsTextLock());
		if (op == KonsActions.EVENT_SELECTED) {
			text.setFocus();
		}
	}

	/**
	 * Set the version label to reflect the current kons' latest version Called by: updateEintrag()
	 */
	private void updateKonsVersionLabel(){
		text.setEnabled(false);
		if (actKons != null) {
			Mandant m = actKons.getMandant();
			int version = actKons.getHeadVersion();
			VersionedResource vr = actKons.getEintrag();
			ResourceItem entry = vr.getVersion(version);
			StringBuilder sb = new StringBuilder();
			if (entry  == null) {
				sb.append(actKons.getLabel() + " (neu)");
			} else {
				String revisionTime = new TimeTool(entry.timestamp).toString(TimeTool.FULL_GER);
				String revisionDate = new TimeTool(entry.timestamp).toString(TimeTool.DATE_GER);
				if (!actKons.getDatum().equals(revisionDate)) {
					sb.append("Kons vom " + actKons.getDatum() + ": ");
				}
				sb.append("rev. ").append(version).append(" vom ")
					.append(revisionTime).append(" (")
					.append(entry.remark).append(")");
				TimeTool konsDate = new TimeTool(actKons.getDatum());
				if (version == -1 && konsDate.isSameDay(new TimeTool())) {
					sb.append(" (NEU)");
				}
			}
			sb.append(Helpers.hasRightToChangeConsultations(actKons, false) ? "" : " Kein Recht ");
			// TODO: Allow administrators to change the konsText
			if (Helpers.userMayEditKons(actKons)) {
				sb.append(" editierbar");
				text.setEnabled(actKons.isEditable(false));
			} else {
				sb.append(" NICHT editierbar");
				text.setEnabled(false);
			}
			lVersion.setText(sb.toString());
			logEvent(String.format("UpdateVersionLabel: %s author <%s> >actUser <%s> editable? %s dirty? %s", sb.toString(), actKons.getAuthor(),CoreHub.actUser.getLabel(), actKons.isEditable(false), text.isDirty()));
		} else {
			lVersion.setText("");
		}
		if (text.isEnabled() && text.isDirty()) {
			text.getControl().setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		} else {
			text.getControl().setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		}
	}

	private synchronized void setKonsText(Konsultation aNewKons, int version, boolean putCaretToEnd){
		if (aNewKons != null) {
			String ntext = "";
			if ((version >= 0) && (version <= aNewKons.getHeadVersion())) {
				VersionedResource vr = aNewKons.getEintrag();
				ResourceItem entry = vr.getVersion(version);
				ntext = entry.data;
				StringBuilder sb = new StringBuilder();
				sb.append("rev. ").append(version).append(" vom ")
					.append(new TimeTool(entry.timestamp).toString(TimeTool.FULL_GER)).append(" (")
					.append(entry.remark).append(")");
				lVersion.setText(sb.toString());
			} else {
				lVersion.setText("");
			}
			String desc = String.format("setKonsText version %s => %s. toEnd %s. Len %s => %s. id: %s",
				aNewKons.getHeadVersion(), version,putCaretToEnd, getPlainText().length(),
				ntext.length(), aNewKons.getId());
			text.setText(PersistentObject.checkNull(ntext));
			text.setKons(aNewKons);
			displayedVersion = version;
			versionBackAction.setEnabled(version != 0);
			versionFwdAction.setEnabled(version != aNewKons.getHeadVersion());
			boolean locked =  hasKonsTextLock();
			if (locked) {
				logEvent("setKonsText hasKonsTextLock " + desc);
			} else {
				logEvent("setKonsText (locked) " + desc);
			}

			if (putCaretToEnd) {
				// set focus and put caret at end of text
				text.putCaretToEnd();
			}
		} else {
			lVersion.setText("");
			text.setText("");
			text.setKons(null);
			displayedVersion = -1;
			updateKonsVersionLabel();
			versionBackAction.setEnabled(false);
			versionFwdAction.setEnabled(false);
			logEvent("setKonsText null " + lVersion.getText() + " " + text.getContentsPlaintext());
		}
	}

	private void logEvent(String msg){
		StringBuilder sb = new StringBuilder(msg + ": ");
		if (actKons == null) {
			sb.append("actKons null");
		} else {
			sb.append(actKons.getId());
			sb.append(" kons rev. " + actKons.getHeadVersion());
			sb.append(" vom " + actKons.getDatum());
			if (actKons.getFall() != null) {
				sb.append(" " + actKons.getFall().getPatient().getPersonalia());
			}
		}
		log.debug(sb.toString());
	}

	@Override
	public synchronized void activation(boolean mode, Patient selectedPat, Konsultation selectedKons){
		logEvent("activation: " + mode);
		if (mode == false) {
			// save entry on deactivation if text was edited and changed or diry and user my edit it
			if (actKons != null && Helpers.userMayEditKons(actKons) && (text.isDirty()) || textChanged()) {
				actKons.updateEintrag(text.getContentsAsXML(), false);
				logEvent(String.format("updateEintrag activation vers %s dtext.isDirty ",
					actKons.getHeadVersion()));
				text.setDirty(false);
			} else {
				logEvent(String.format("skip updateEintrag activation as %s mayEdit %s dirty %s changed %s",
					actKons,  Helpers.userMayEditKons(actKons), text.isDirty(),  textChanged()));
			}
		} else {
			// load newest version on activation, if there are no local changes
			if (actKons != null && !text.isDirty()) {
				setKonsText(actKons, actKons.getHeadVersion(), true);
			}
		}
	}

	public synchronized void registerUpdateHeartbeat(){
		Heartbeat heat = Heartbeat.getInstance();
		heat.addListener(new IatrixHeartListener() {
			@Override
			public void heartbeat(){
				int konsTextSaverPeriod = Heartbeat.getKonsTextSaverPeriod();
				logEvent("Period: " + konsTextSaverPeriod + " dirty? " + text.isDirty());

				if (!(konsTextSaverPeriod > 0)) {
					// auto-save disabled
					return;
				}

				// inv: konsTextSaverPeriod > 0

				// increment konsTextSaverCount, but stay inside period
				konsTextSaverCount++;
				konsTextSaverCount %= konsTextSaverPeriod;

				logEvent("konsTextSaverCount = " + konsTextSaverCount);
				if (konsTextSaverCount == 0) {
					logEvent("Auto Save Kons Text");
					updateEintrag();
				}
			}
		});
	}

	public String getPlainText(){
		if (text == null ) {
			return "";
		}
		return text.getContentsPlaintext();
	}

	private String getPlainBegining() {
		String plain = getPlainText();
		int strlen = plain.length();
		return plain.substring(0, strlen < 120 ? strlen : 120);
	}
}