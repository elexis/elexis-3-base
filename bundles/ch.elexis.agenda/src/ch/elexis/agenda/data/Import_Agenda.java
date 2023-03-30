/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.agenda.data;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.elexis.actions.Synchronizer;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.Result;

@Deprecated
public class Import_Agenda extends ImporterPage {
	public static final String drop = "DROP INDEX it ON AGNTERMINE;" + //$NON-NLS-1$
			"DROP INDEX pattern ON AGNTERMINE;" + //$NON-NLS-1$
			"DROP INDEX mandterm ON AGNTERMINE;" + //$NON-NLS-1$
			"DROP TABLE AGNTERMINE;"; //$NON-NLS-1$
	ImporterPage.DBBasedImporter importer = null;
	Combo cbBereich;
	Button bSyncEnable, bDoDelete;
	Text tMandant;
	String orig_mandant;
	String dest_bereich;
	boolean bDelete = false;
	Hashtable<String, String> map;

	public Import_Agenda() {
		map = Synchronizer.getBereichMapping();
	}

	@Override
	public String getTitle() {
		return "JavaAgenda"; //$NON-NLS-1$
	}

	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception {
		Result<JdbcLink> res = importer.getConnection();
		if (!res.isOK()) {
			return ResultAdapter.getResultAsStatus(res);
		}
		JdbcLink j = res.get();
		int size = j.queryInt("SELECT COUNT(0) FROM agnTermine WHERE BEIWEM='" + orig_mandant + "' AND deleted<>'1'"); //$NON-NLS-1$ //$NON-NLS-2$
		monitor.beginTask(Messages.Import_Agenda_importingAgenda, size + 100);
		Stm stm = j.getStatement();
		// Activator.getDefault().pinger.pause(true);
		Synchronizer.pause(true);
		if (bDelete) {
			monitor.subTask(Messages.Import_Agenda_creatingTables);
			Termin.getConnection().execScript(new ByteArrayInputStream(drop.getBytes("UTF-8")), true, false); //$NON-NLS-1$
			Termin.init();
		}
		monitor.worked(10);

		try {
			monitor.subTask(Messages.Import_Agenda_importingApps);
			ResultSet rs = stm.query("SELECT * FROM agnTermine WHERE BEIWEM='" + orig_mandant + "' AND deleted<>'1'"); //$NON-NLS-1$ //$NON-NLS-2$
			Query<Patient> qPat = new Query<Patient>(Patient.class);
			int loop = 0;
			while (rs.next()) {
				if (++loop > 100) {
					loop = 0;
					PersistentObject.clearCache();
					Thread.sleep(10);
				}
				int von = rs.getInt("Beginn"); //$NON-NLS-1$
				int dauer = rs.getInt("Dauer"); //$NON-NLS-1$
				int bis = von + dauer;
				Termin t = new Termin(rs.getString("ID"), dest_bereich, rs.getString("Tag"), von, bis, //$NON-NLS-1$ //$NON-NLS-2$
						rs.getString("TerminTyp"), rs.getString("TerminStatus")); //$NON-NLS-1$ //$NON-NLS-2$
				t.set(new String[] { "Grund", "ErstelltWann", Termin.FLD_CREATOR }, //$NON-NLS-1$ //$NON-NLS-2$
						rs.getString("Grund"), rs.getString("Angelegt"), //$NON-NLS-1$ //$NON-NLS-2$
						rs.getString(Termin.FLD_CREATOR));

				String pers = rs.getString("Personalien"); //$NON-NLS-1$
				String[] px = Termin.findID(pers);
				List<Patient> list = qPat.queryFields(new String[] { "Name", "Vorname", "Geburtsdatum" }, px, true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if ((list == null) || (list.size() != 1)) {
					t.set("Wer", pers); //$NON-NLS-1$
				} else {
					t.set("Wer", ((PersistentObject) list.get(0)).getId()); //$NON-NLS-1$
				}
				if (monitor.isCanceled()) {
					monitor.done();
					Termin.getConnection().execScript(new ByteArrayInputStream(drop.getBytes("UTF-8")), true, false); //$NON-NLS-1$
					SWTHelper.showError(Messages.Import_Agenda_cancelled, Messages.Import_Agenda_importWasCancelled);
					return Status.CANCEL_STATUS;
				}
				monitor.worked(1);
			}
			return Status.OK_STATUS;
		} catch (Exception ex) {
			SWTHelper.showError(Messages.Import_Agenda_errorsDuringImport, ex.getMessage());
			ExHandler.handle(ex);
		} finally {
			j.releaseStatement(stm);
			monitor.done();
			// Activator.getDefault().pinger.pause(false);
			Synchronizer.pause(false);
		}
		return Status.CANCEL_STATUS;
	}

	@Override
	public String getDescription() {
		return Messages.Import_Agenda_importFromJavaAgenda;
	}

	@Override
	public void collect() {
		orig_mandant = tMandant.getText();
		dest_bereich = cbBereich.getText();
		map.put(dest_bereich, orig_mandant);
		Synchronizer.setBereichMapping(map);
		ConfigServiceHolder.setGlobal(PreferenceConstants.AG_SYNC_ENABLED, bSyncEnable.getSelection());
		ConfigServiceHolder.setGlobal(PreferenceConstants.AG_SYNC_TYPE, results[0]);
		ConfigServiceHolder.setGlobal(PreferenceConstants.AG_SYNC_HOST, results[1]);
		ConfigServiceHolder.setGlobal(PreferenceConstants.AG_SYNC_CONNECTOR, results[2]);
		ConfigServiceHolder.setGlobal(PreferenceConstants.AG_SYNC_DBUSER, results[3]);
		ConfigServiceHolder.setGlobal(PreferenceConstants.AG_SYNC_DBPWD, results[4]);
		bDelete = bDoDelete.getSelection();
	}

	@Override
	public Composite createPage(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout());
		importer = new ImporterPage.DBBasedImporter(ret, this);
		importer.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		Composite cMandant = new Composite(ret, SWT.BORDER);
		cMandant.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		cMandant.setLayout(new GridLayout(3, false));
		Composite cl = new Composite(cMandant, SWT.NONE);
		cl.setLayout(new GridLayout());
		new Label(cl, SWT.NONE).setText("Bereich in Elexis"); //$NON-NLS-1$
		cbBereich = new Combo(cl, SWT.SINGLE | SWT.READ_ONLY);
		cbBereich.setItems(ConfigServiceHolder.getGlobal(PreferenceConstants.AG_BEREICHE, "Praxis").split(",")); //$NON-NLS-1$ //$NON-NLS-2$
		dest_bereich = cbBereich.getItem(0);
		cbBereich.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				map.put(dest_bereich, tMandant.getText());
				dest_bereich = cbBereich.getText();
				orig_mandant = map.get(dest_bereich);
				tMandant.setText(orig_mandant == null ? StringUtils.EMPTY : orig_mandant);
			}

		});
		cbBereich.select(0);
		orig_mandant = map.get(dest_bereich);
		if (orig_mandant == null) {
			orig_mandant = StringUtils.EMPTY;
		}
		new Label(cMandant, SWT.NONE).setText("Entspricht"); //$NON-NLS-1$
		Composite cr = new Composite(cMandant, SWT.NONE);
		cr.setLayout(new GridLayout());
		new Label(cr, SWT.NONE).setText("Name in Agenda"); //$NON-NLS-1$
		tMandant = new Text(cr, SWT.BORDER);
		tMandant.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tMandant.setText(orig_mandant);
		bDoDelete = new Button(ret, SWT.CHECK);
		bDoDelete.setText("Agenda-Daten löschen und neu anlegen"); //$NON-NLS-1$
		bSyncEnable = new Button(ret, SWT.CHECK);
		bSyncEnable.setText("Kontinuierlich synchronisieren"); //$NON-NLS-1$
		bSyncEnable.setSelection(ConfigServiceHolder.getGlobal(PreferenceConstants.AG_SYNC_ENABLED, false));
		return ret;
	}

}
