/*******************************************************************************
 * Copyright (c) 2010-2011, G. Weirich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *
 *
 *******************************************************************************/
package elexis_db_shaker.actions;

import org.apache.commons.lang3.StringUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.VersionedResource;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 *
 * @see IWorkbenchWindowActionDelegate
 */
public class Shake implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	boolean zufallsnamen;
	int TOTAL = Integer.MAX_VALUE;

	/**
	 * The constructor.
	 */
	public Shake() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 *
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		if (!AccessControlServiceHolder.get().request(DBShakerACL.EXEC_DBSHAKER)) {
			MessageDialog.openInformation(UiDesk.getTopShell(), "Insufficient rights",
					"Insufficient rights to execute.");
			return;
		}
		;

		final SettingsDialog sd = new SettingsDialog(window.getShell());
		if (sd.open() == Dialog.OK) {
			if (SWTHelper.askYesNo("Wirklich Datenbank anonymisieren",
					"Achtung! Diese Aktion macht die Datenbank unwiderruflich unbrauchbar! Wirklich anonymisieren?")) {
				zufallsnamen = sd.replaceNames;
				IWorkbench wb = PlatformUI.getWorkbench();
				IProgressService ps = wb.getProgressService();
				try {
					ps.busyCursorWhile(new IRunnableWithProgress() {
						public void run(IProgressMonitor pm) {
							pm.beginTask("Anonymisiere Datenbank", TOTAL);
							int jobs = 1;
							if (sd.replaceKons) {
								jobs++;
							}
							if (sd.deleteDocs) {
								jobs++;
							}
							if (sd.purgeDB) {
								jobs++;
							}
							doShakeNames(pm, TOTAL / jobs);
							if (sd.replaceKons) {
								doShakeKons(pm, TOTAL / jobs);
							}
							if (sd.deleteDocs) {
								new DocumentRemover().run(pm, TOTAL / jobs);
							}
							if (sd.purgeDB) {
								doPurgeDB(pm, TOTAL / jobs);
							}
							pm.done();
						}
					});
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void doPurgeDB(IProgressMonitor monitor, int workUnits) {
		monitor.subTask("Bereinige Datenbank");
		JdbcLink j = PersistentObject.getConnection();
		j.exec("DELETE FROM kontakt where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM briefe where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM faelle where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM behandlungen where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM artikel where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM leistungen where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM output_log"); //$NON-NLS-1$
		j.exec("DELETE FROM rechnungen where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM reminders where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM traces"); //$NON-NLS-1$
		j.exec("DELETE FROM laboritems where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM laborwerte where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM rezepte where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM heap where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM auf where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM heap2 where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM logs where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM xid where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM etiketten where deleted='1'"); //$NON-NLS-1$
		j.exec("DELETE FROM CH_ELEXIS_OMNIVORE_DATA where deleted='1'"); //$NON-NLS-1$
		monitor.worked(workUnits);
	}

	private void doShakeKons(IProgressMonitor monitor, int workUnits) {
		try {
			monitor.subTask("Anonymisiere Konsultationen");
			Query<Konsultation> qbe = new Query<Konsultation>(Konsultation.class);
			List<Konsultation> list = qbe.execute();
			int workPerKons = (Math.round(workUnits * .8f) / list.size());
			Lipsum lipsum = new Lipsum();
			monitor.worked(Math.round(workUnits * .2f));
			for (Konsultation k : list) {
				VersionedResource vr = k.getEintrag();
				StringBuilder par = new StringBuilder();
				int numPars = (int) Math.round(3 * Math.random() + 1);
				while (numPars-- > 0) {
					par.append(lipsum.getParagraph());
				}
				vr.update(par.toString(), "random contents"); //$NON-NLS-1$
				k.setEintrag(vr, true);
				k.purgeEintrag();
				if (monitor.isCanceled()) {
					break;
				}
				monitor.worked(workPerKons);
			}
		} catch (Throwable e) {
			SWTHelper.showError("Fehler", e.getMessage());
		}
	}

	private void doShakeNames(IProgressMonitor monitor, int workUnits) {
		monitor.subTask("Anonymisiere Patienten und Kontakte");
		Query<Kontakt> qbe = new Query<Kontakt>(Kontakt.class);
		List<Kontakt> list = qbe.execute();
		int workPerName = (Math.round(workUnits * .8f) / list.size());
		Namen n = new Namen();
		monitor.worked(Math.round(workUnits * .2f));
		for (Kontakt k : list) {
			String vorname = StringUtils.EMPTY;
			// Mandanten behalten
			// if(k.get(Kontakt.FLD_IS_MANDATOR).equalsIgnoreCase(StringConstants.ONE))
			// continue;

			if (zufallsnamen) {
				k.set("Bezeichnung1", n.getRandomNachname()); //$NON-NLS-1$
			} else {
				k.set("Bezeichnung1", getWord()); //$NON-NLS-1$
			}

			if (zufallsnamen) {
				vorname = n.getRandomVorname();
			} else {
				vorname = getWord();
			}
			k.set("Bezeichnung2", vorname); //$NON-NLS-1$

			if (k.istPerson()) {
				Person p = Person.load(k.getId());
				p.set(Person.SEX, StringTool.isFemale(vorname) ? Person.FEMALE : Person.MALE);
			}
			k.set(Kontakt.FLD_ANSCHRIFT, StringUtils.EMPTY);
			k.set(Kontakt.FLD_PHONE1, getPhone());
			k.set(Kontakt.FLD_PHONE2, Math.random() > 0.6 ? getPhone() : StringUtils.EMPTY);
			k.set(Kontakt.FLD_MOBILEPHONE, Math.random() > 0.5 ? getPhone() : StringUtils.EMPTY);
			k.set(Kontakt.FLD_E_MAIL, StringUtils.EMPTY);
			k.set(Kontakt.FLD_PLACE, StringUtils.EMPTY);
			k.set(Kontakt.FLD_STREET, StringUtils.EMPTY);
			k.set(Kontakt.FLD_ZIP, StringUtils.EMPTY);
			k.set(Kontakt.FLD_FAX, Math.random() > 0.8 ? getPhone() : StringUtils.EMPTY);
			if (monitor.isCanceled()) {
				break;
			}
			monitor.worked(workPerName);
		}
	}

	private String getPhone() {
		StringBuilder ret = new StringBuilder();
		ret.append("555-"); //$NON-NLS-1$
		for (int i = 0; i < 7; i++) {
			ret.append((char) Math.round(Math.random() * ('9' - '0') + '0'));
		}
		return ret.toString();
	}

	private String getWord() {
		int l = (int) Math.round(Math.random() * 5 + 5);
		StringBuilder ret = new StringBuilder();
		ret.append(Character.toUpperCase(getLetter()));
		for (int i = 0; i < l; i++) {
			ret.append(getLetter());
		}
		return ret.toString();
	}

	private char getLetter() {
		return (char) Math.round(Math.random() * ('z' - 'a') + 'a');
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of the
	 * 'real' action here if we want, but this can only happen after the delegate
	 * has been created.
	 *
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 *
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell for
	 * the message dialog.
	 *
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}