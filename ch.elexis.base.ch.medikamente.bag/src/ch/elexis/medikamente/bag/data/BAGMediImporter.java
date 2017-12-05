/*******************************************************************************
 * Copyright (c) 2007-2016, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    1/2017: G. Weirich added EAN and ATC
 * 
 *******************************************************************************/

package ch.elexis.medikamente.bag.data;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;

public class BAGMediImporter extends ImporterPage {
	static Query<Artikel> qbe = new Query<Artikel>(Artikel.class);
	static Logger log = Logger.getLogger(BAGMediImporter.class.getName());
	
	public BAGMediImporter(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Composite createPage(final Composite parent){
		FileBasedImporter fbi = new FileBasedImporter(parent, this);
		fbi.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return fbi;
	}
	
	@Override
	public IStatus doImport(final IProgressMonitor monitor) throws Exception{
		FileInputStream is = new FileInputStream(results[0]);
		return doImport(is, monitor);
	}
	
	public IStatus doImport(final InputStream inputStream, final IProgressMonitor monitor)
		throws ElexisException{
		ExcelWrapper ew = new ExcelWrapper();
		if (ew.load(inputStream, 0)) {
			int f = ew.getFirstRow() + 1;
			int l = ew.getLastRow();
			monitor.beginTask("Import BAG-Medikamente", l - f);
			int counter = 0;
			ew.setFieldTypes(new Class[] {
				String.class, Character.class, Integer.class, Integer.class, Integer.class,
				Character.class, String.class, String.class, Double.class, Double.class,
				String.class, Integer.class, Integer.class, String.class, Integer.class,
				Character.class, String.class, String.class
			});
			for (int i = f; i < l; i++) {
				List<String> row = ew.getRow(i);
				monitor.subTask(row.get(7));
				importUpdate(row.toArray(new String[0]));
				if (counter++ > 200) {
					PersistentObject.clearCache();
					counter = 0;
				}
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				monitor.worked(1);
				row = null;
			}
			monitor.done();
			return Status.OK_STATUS;
		}
		return Status.CANCEL_STATUS;
	}
	
	/**
	 * Import a medicament from one row of the BAG-Medi file
	 * 
	 * @param row
	 * 
	 *            <pre>
	 * 		row[0] = ID,bzw Name
	 * 		row[1] = Generikum
	 * 		row[2] = Pharmacode
	 * 	 	row[3] = BAG-Dossier
	 * 		row[4] = Swissmedic-Nr
	 * 		row[5] = Swissmedic-Liste
	 * 	 	row[6] = Einf. Datum
	 * 		row[7] = Bezeichnung
	 * 		row[8] = EK-Preis
	 *      row[9] = VK-Preis
	 *      row[10]= Limitatio (Y/N)
	 *      row[11]= LimitatioPts
	 *      row[12]= Gruppe (optional)
	 *      row[13]= Substance (optional)
	 *      row[14] = Rec.ID
	 *      row[15] = 20% Selbstbehalt Y/N
	 *      row[16] = GTIN / EAN
	 *      row[17] = ATC
	 *            </pre>
	 * 
	 * @return
	 */
	public static boolean importUpdate(final String[] row) throws ElexisException{
		String pharmacode = "0";
		String ean = "";
		String atc = "";
		BAGMedi imp = null;
		// Kein Pharmacode, dann nach Name suchen
		if (StringTool.isNothing(row[2].trim())) {
			String mid = qbe.findOne(Artikel.FLD_NAME, "=", row[7]);
			if (mid != null) {
				imp = BAGMedi.load(mid);
			}
		} else {
			try {
				// strip leading zeroes
				int pcode = Integer.parseInt(row[2].trim());
				pharmacode = Integer.toString(pcode);
				
			} catch (Exception ex) {
				ExHandler.handle(ex);
				log.log(Level.WARNING, "Pharmacode falsch: " + row[2]);
			}
			
			qbe.clear(true);
			qbe.add(Artikel.FLD_SUB_ID, "=", pharmacode);
			qbe.or();
			qbe.add(Artikel.FLD_SUB_ID, Query.EQUALS, row[2].trim());
			List<Artikel> lArt = qbe.execute();
			if (lArt == null) {
				throw new ElexisException(BAGMediImporter.class,
					"Article list was null while scanning for " + pharmacode,
					ElexisException.EE_UNEXPECTED_RESPONSE, true);
			}
			if (lArt.size() > 1) {
				// Duplikate entfernen, genau einen gültigen und existierenden Artikel behalten
				Iterator<Artikel> it = lArt.iterator();
				boolean hasValid = false;
				Artikel res = null;
				while (it.hasNext()) {
					Artikel ax = it.next();
					if (hasValid || (!ax.isValid())) {
						if (res == null) {
							res = ax;
						}
						it.remove();
					} else {
						hasValid = true;
					}
				}
				if (!hasValid) {
					if (res != null) {
						if (res.isDeleted()) {
							res.undelete();
							lArt.add(res);
						}
					}
				}
			}
			imp = lArt.size() > 0 ? BAGMedi.load(lArt.get(0).getId()) : null;
		}
		ean = row[16].replaceAll("'","");
		atc = row[17];
		if (imp == null || (!imp.isValid())) {
			imp = new BAGMedi(row[7], pharmacode, ean, atc);
			
			String sql = new StringBuilder().append("INSERT INTO ").append(BAGMedi.EXTTABLE)
				.append(" (ID) VALUES (").append(imp.getWrappedId()).append(");").toString();
			PersistentObject.getConnection().exec(sql);
			
		} else {
			
			String sql = new StringBuilder().append("SELECT ID FROM ").append(BAGMedi.EXTTABLE)
				.append(" WHERE ID=").append(imp.getWrappedId()).toString();
			String extid = PersistentObject.getConnection().queryString(sql);
			if (extid == null) {
				sql = new StringBuilder().append("INSERT INTO ").append(BAGMedi.EXTTABLE)
					.append(" (ID) VALUES (").append(imp.getWrappedId()).append(");").toString();
				PersistentObject.getConnection().exec(sql);
			}
			
		}
		imp.update(row);
		return true;
	}
	
	@Override
	public String getDescription(){
		return "Import Medikamentenliste BAG";
	}
	
	@Override
	public String getTitle(){
		return "Medi-BAG";
	}
	
}
