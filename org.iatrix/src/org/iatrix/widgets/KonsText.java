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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.iatrix.data.KonsTextLock;
import org.iatrix.dialogs.ChooseKonsRevisionDialog;
import org.iatrix.util.Heartbeat;
import org.iatrix.util.Heartbeat.IatrixHeartListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.constants.ExtensionPointConstantsUi;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.text.EnhancedTextField;
import ch.elexis.core.ui.util.IKonsExtension;
import ch.elexis.core.ui.util.IKonsMakro;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Anwender;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionedResource;
import ch.rgw.tools.VersionedResource.ResourceItem;

public class KonsText implements IJournalArea {

	private Patient actPatient = null;
	private Konsultation actKons = null;
	private static int konsTextSaverCount = 0;
	private static Logger log = LoggerFactory.getLogger(org.iatrix.widgets.KonsText.class);
	private EnhancedTextField text;
	private Label lVersion = null;
	private Label lKonsLock = null;
	private KonsTextLock konsTextLock = null;
	int displayedVersion;
	private Action purgeAction;
	private Action saveAction;
	private Action chooseVersionAction;
	private Action versionFwdAction;
	private Action versionBackAction;
	private static final String PATIENT_KEY = "org.iatrix.patient";
	private boolean konsEditorHasFocus = false;
	private static boolean creatingKons = false;
	private static String savedInitialKonsText = null;
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
		hXrefs = new Hashtable<String, IKonsExtension>();
		@SuppressWarnings("unchecked")
		List<IKonsExtension> listKonsextensions = Extensions.getClasses(
			Extensions.getExtensions(ExtensionPointConstantsUi.KONSEXTENSION), "KonsExtension", //$NON-NLS-1$ //$NON-NLS-2$
			false);
		for (IKonsExtension x : listKonsextensions) {
			String provider = x.connect(text);
			hXrefs.put(provider, x);
		}
		@SuppressWarnings("unchecked")
		List<IKonsMakro> makros = Extensions.getClasses(
			Extensions.getExtensions(ExtensionPointConstantsUi.KONSEXTENSION), "KonsMakro", false); //$NON-NLS-1$
		text.setExternalMakros(makros);

		text.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		makeActions();

		text.getControl().addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e){
				logEvent("widgetDisposed");
				updateEintrag();
				konsEditorHasFocus = false;
			}

		});
		text.getControl().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e){
				logEvent("focusGained");
				konsEditorHasFocus = true;
			}

			@Override
			public void focusLost(FocusEvent e){
				logEvent("focusLost");
				updateEintrag();
				konsEditorHasFocus = false;
			}
		});
		Control control = text.getControl();
		if (control instanceof StyledText) {
			StyledText styledText = (StyledText) control;

			styledText.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e){
					// create new consultation if required
					handleInitialKonsText();
				}
			});
		}

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

	public Action getSaveAction(){
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

	public synchronized void updateEintrag(){
		if (actKons != null) {
			if (actKons.getFall() == null) {
				return;
			}
			Patient konsPatient = actKons.getFall().getPatient();
			if (text.isDirty() || textChanged()) {
				if (actPatient != null && konsPatient.getId().equals(actPatient.getId())) {
					logEvent("updateEintrag same");
				} else {
					logEvent("updateEintrag not same. not skipping. Length is "
						+ text.getContentsPlaintext().length());
					//					return;
				}
				if (hasKonsTextLock()) {
					actKons.updateEintrag(text.getContentsAsXML(), false);
					logEvent(
						"saved rev. " + actKons.getHeadVersion() + " "+ text.getContentsPlaintext());
					text.setDirty(false);

					// update kons version label
					// (we would get an objectChanged event, but this event isn't processed
					// in case the kons text field has the focus.)
					updateKonsVersionLabel();
				} else {
					// should never happen...
					if (konsTextLock == null) {
						logEvent("updateEintrag Konsultation gesperrt. konsTextLock null.");
					} else {
						logEvent("updateEintrag Konsultation gesperrt. " + " key "
							+ konsTextLock.getKey() + " lock " + konsTextLock.getLockValue());
						SWTHelper.alert("Konsultation gesperrt",
							"Der Text kann nicht gespeichert werden, weil die Konsultation durch einen anderen Benutzer gesperrt ist."
								+ "(info: " + konsTextLock.getKey()
								+ ". Dieses Problem ist ein Programmfehler. Bitte informieren Sie die Entwickler.)");
					}
				}
			} else {
				boolean check = (savedInitialKonsText == null) || (text == null) || savedInitialKonsText.equals(text.getContentsPlaintext());
				if (check)
				{
					log.debug("skipping check vis " + text.isVisible() + " enabled " + text.isEnabled()+  " disposed " + text.isDisposed() + " " + text.getContentsPlaintext() + " initial " + savedInitialKonsText);
				} else
				{
					log.debug("skipping check " + text.getContentsPlaintext() + " != initial " + savedInitialKonsText);
					actKons.updateEintrag(text.getContentsAsXML(), false);
				}
			}
		}
	}

	/**
	 * Check whether the text in the text field has changed compared to the database entry.
	 *
	 * @return true, if the text changed, false else
	 */
	private boolean textChanged(){
		String dbEintrag = actKons.getEintrag().getHead();
		String textEintrag = text.getContentsAsXML();

		if (textEintrag != null) {
			if (!textEintrag.equals(dbEintrag)) {
				// text differs from db entry
				logEvent("saved text != db entry");
				return true;
			}
		}

		return false;

	}

	/**
	 * Creates a new consultation if text has been entered, but no consultation is selected.
	 */
	private void handleInitialKonsText(){
		if (actPatient != null && actKons == null && creatingKons == false) {
			creatingKons = true;
			logEvent("handleInitialKonsText: creatingKons" + text.getContentsPlaintext());
			String initialText = text.getContentsAsXML();
			Konsultation.neueKons(initialText);
		} else {
			logEvent("handleInitialKonsText: set " + text.getContentsPlaintext());
			text.setData(PATIENT_KEY, actPatient.getId());
			savedInitialKonsText = text.getContentsAsXML();
		}
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
	private void createKonsTextLock(){
		// remove old lock
		removeKonsTextLock();

		if (actKons != null && CoreHub.actUser != null) {
			konsTextLock = new KonsTextLock(actKons, CoreHub.actUser);
		} else {
			konsTextLock = null;
		}

		if (konsTextLock != null) {
			boolean success = konsTextLock.lock();
			logEvent(
				"createKonsTextLock: konsText locked (" + success + ")" + konsTextLock.getKey());
			// System.err.println("DEBUG: createKonsTextLock: konsText locked (" + success + ")");
		}
	}

	// helper method to release a KonsTextLock
	// should be called before a new konsultation is set
	// or the program/view exits
	private void removeKonsTextLock(){
		if (konsTextLock != null) {
			boolean success = konsTextLock.unlock();
			logEvent(
				"removeKonsTextLock: konsText unlocked (" + success + ") " + konsTextLock.getKey());
			konsTextLock = null;
		}
	}

	/**
	 * Check whether we own the lock
	 *
	 * @return true, if we own the lock, false else
	 */
	private boolean hasKonsTextLock(){
		return (konsTextLock != null && konsTextLock.isLocked());
	}

	@Override
	public synchronized void visible(boolean mode){
		log.debug("visible mode " + mode);
	}

	private void makeActions(){
		// Konsultationstext

		purgeAction = new Action("Alte Eintragsversionen entfernen") {
			@Override
			public void run(){
				actKons.purgeEintrag();
				ElexisEventDispatcher.fireSelectionEvent(actKons);
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
					text.setDirty(true);
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
					text.setDirty(true);
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
						text.setDirty(true);
					}
				}

			}
		};

		saveAction = new Action("Eintrag sichern") {
			{
				setImageDescriptor(Images.IMG_DISK.getImageDescriptor());
				setToolTipText("Text explizit speichern");
			}

			@Override
			public void run(){
				updateEintrag();
			}
		};
	};

	private void updateKonsultation(boolean updateText, boolean putCaretToEnd){
		if (actKons != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(actKons.getDatum());
			if (updateText) {
				setKonsText(actKons, actKons.getHeadVersion(), putCaretToEnd);
			}
			logEvent("updateKonsultation: " + actKons.getId());
		} else {
			setKonsText(null, 0, putCaretToEnd);
			logEvent("updateKonsultation: null");
		}
	}

	/*
	 * Aktuellen Patienten setzen
	 */
	@Override
	public void setPatient(Patient newPatient){
		// We only change to a selected konsultation
		// this avoid locking problems between actKons and actPatient
		return;
	}

	/**
	 * Aktuelle Konsultation setzen.
	 *
	 * Wenn eine Konsultation gesetzt wird stellen wir sicher, dass der gesetzte Patient zu dieser
	 * Konsultation gehoert. Falls nicht, wird ein neuer Patient gesetzt.
	 *
	 * @param putCaretToEnd
	 *            if true, activate text field ant put caret to the end
	 */
	@Override
	public synchronized void setKons(Konsultation k, boolean putCaretToEnd){
		// make sure to unlock the kons edit field and release the lock
		removeKonsTextLock();
		actKons = k;
		if (actKons == null) {
			actPatient = null;
			logEvent("setKons null");
		} else {
			boolean different = actPatient != null && k != null && !actPatient.getId().equals(k.getFall().getPatient().getId());
			if (different) {
				Patient newPat =  k.getFall().getPatient();
				logEvent("changed actPatient " +  actPatient.getId() + "  != patient "+ newPat.getId() + " for kon. Skipping ??  "+  newPat.getPersonalia());
				creatingKons = false;
				return;
			}
			actPatient = actKons.getFall().getPatient();
		}
		if (savedInitialKonsText != null && actKons != null) {
			logEvent("set kons patient key " + text.getData(PATIENT_KEY) + " len "
					+ savedInitialKonsText.length());
			if (savedInitialKonsText.length() > 0
				&& !actKons.getEintrag().toString().equalsIgnoreCase(text.getContentsAsXML())) {
				logEvent("in Text:" + text.getContentsAsXML());
				if (actKons != null && actKons.getEintrag() != null && actKons.getEintrag().getHead() != null) {
					logEvent("in DB:" + actKons.getEintrag().getHead().toString());
				}
				actKons.updateEintrag(savedInitialKonsText, false);
			}
			savedInitialKonsText = null;
			text.setData(PATIENT_KEY, null);
		}
		creatingKons = false;

		if (actKons != null) {
			// create new konsTextLock
			createKonsTextLock();
		}
		updateKonsultation(true, putCaretToEnd);

		// update konslock label and enable/disable save action
		updateKonsLockLabel();
		saveAction.setEnabled(konsTextLock == null || hasKonsTextLock());
	}

	/**
	 * Set the version label to reflect the current kons' latest version Called by: updateEintrag()
	 */
	private void updateKonsVersionLabel(){
		if (actKons != null) {
			int version = actKons.getHeadVersion();
			logEvent("Update Version Label: " + version);

			VersionedResource vr = actKons.getEintrag();
			ResourceItem entry = vr.getVersion(version);
			StringBuilder sb = new StringBuilder();
			sb.append("rev. ").append(version).append(" vom ")
				.append(new TimeTool(entry.timestamp).toString(TimeTool.FULL_GER)).append(" (")
				.append(entry.remark).append(")");
			lVersion.setText(sb.toString());
		} else {
			lVersion.setText("");
		}
	}

	private void setKonsText(Konsultation b, int version, boolean putCaretToEnd){
		if (b != null) {
			String ntext = "";
			if ((version >= 0) && (version <= b.getHeadVersion())) {
				VersionedResource vr = b.getEintrag();
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
			logEvent("setKonsText");
			text.setText(PersistentObject.checkNull(ntext));
			text.setKons(b);
			text.setEnabled(hasKonsTextLock());
			displayedVersion = version;
			versionBackAction.setEnabled(version != 0);
			versionFwdAction.setEnabled(version != b.getHeadVersion());

			if (putCaretToEnd) {
				// set focus and put caret at end of text
				text.putCaretToEnd();
			}
		} else {
			lVersion.setText("");
			text.setText("");
			text.setKons(null);

			if (actPatient == null) {
				text.setEnabled(false);
			} else {
				// enable text, in case user wants to create a new kons by
				// typing in the empty text field
				text.setEnabled(true);
			}

			displayedVersion = -1;
			versionBackAction.setEnabled(false);
			versionFwdAction.setEnabled(false);
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
	public synchronized void activation(boolean mode){
		logEvent("activation mode " + mode);
		if (mode == false) {
			// text is neither dirty nor changed.
			// If it es empty and nothing has been billed, we just delete this kons.
			if (actKons == null) {
				return;
			}
			boolean noLeistungen =
				actKons.getLeistungen() == null || actKons.getLeistungen().isEmpty();
			log.debug(actPatient.getPersonalia() + " delete3 the kons? "
				+ text.getContentsPlaintext().length() + " noLeistungen " + noLeistungen);
			if (text.getContentsPlaintext().length() == 0 && (noLeistungen)) {
				Fall f = actKons.getFall();
				Konsultation[] ret = f.getBehandlungen(false);
				actKons.delete(true);
				if (ret.length == 1) {
					/* Trying to remove the associated case got me into problems.
					 * Peter Schoenbucher argued on September, 2, 2015, that we should never
					 * delete a case, because the case holds the information which Krankenkasse is
					 * attached to this client. Therefore often the assistant opens a case before
					 * the consultation starts
					 */
					// f.delete(true);
				}
			}
			updateEintrag();
			setKons(null, false);
			text.setData(PATIENT_KEY, null);
			savedInitialKonsText = "";
			text.setText("");
			actKons = null;
			actPatient = null;
		}
	}

	public synchronized void registerUpdateHeartbeat(){
		Heartbeat heat = Heartbeat.getInstance();
		heat.addListener(new IatrixHeartListener() {
			private int konsTextSaverPeriod;

			@Override
			public void heartbeat(){
				logEvent("Period: " + konsTextSaverPeriod);
				if (!(konsTextSaverPeriod > 0)) {
					// auto-save disabled
					return;
				}

				// inv: konsTextSaverPeriod > 0

				// increment konsTextSaverCount, but stay inside period
				konsTextSaverCount++;
				konsTextSaverCount %= konsTextSaverPeriod;

				logEvent("konsTextSaverCount = " + konsTextSaverCount + " konsEditorHasFocus: "
					+ konsEditorHasFocus);
				if (konsTextSaverCount == 0) {
					if (konsEditorHasFocus) {
						logEvent("Auto Save Kons Text");
						updateEintrag();
					}
				}
			}
		});
	}
}