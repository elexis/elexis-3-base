/*******************************************************************************
 * Copyright (c) 2007-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.berchtold.emanuel.privatrechnung.data;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import ch.berchtold.emanuel.privatrechnung.model.IPrivatLeistung;
import ch.berchtold.emanuel.privatrechnung.model.PrivatModelServiceHolder;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;
import com.opencsv.CSVReader;

/**
 * A class to import codes from an external source to this code system. the external source must be
 * an CSV or Excel(tm) file with the fields:
 * parentCode,codeID,codeName,cost,price,time,validFrom,validUntil,factor
 * <ul>
 * <li>parentCode: If this code system is organized as tree: codeID of the parent or 'NIL' if this
 * is a top level code. If this code system is a flat list, parentCode is always 'NIL'</li>
 * <li>codeID: The (within this code system unique) identification of the code. e.g. 10.00.01 for al
 * Tarmed-style system</li>
 * <li>codeName: the human understandable name of the code</li>
 * <li>cost: the internal cost of this service (what do we have to pay for it), in cents/Rp</li>
 * <li>price: the external cost of this service (what have cliebnts to pay us for it), in cents/Rp</li>
 * <li>time: the average time needed (in minutes) for this service
 * <li>validFrom: the date as YYYYMMDD when this code with this price starts being valid</li>
 * <li>validUntil: the date as YYYYMMDD when the validity of this entry expires</li>
 * <li>factor: a multiplicator to apply to the price before calculating the end-user-price.
 * Sometimes referred to as "Taxpunktwert"</li>
 * </ul>
 * 
 * Please note: the codeID needs not to be a unique key. instead we might have several entries with
 * the same codeID, but different validity-dates and prices/factors. At a given date however, only
 * one ellement of each code should be valid.
 * 
 * 
 * This Importer will be displayed, when the user selects "Import" from the Details-View of the
 * codes of this plugin
 */
public class Importer extends ImporterPage {
	
	/**
	 * Create the page that will let the user select a file to import. For simplicity, we use the
	 * default FileBasedImporter of our superclass.
	 */
	@Override
	public Composite createPage(final Composite parent){
		FileBasedImporter fbi = new FileBasedImporter(parent, this);
		fbi.setFilter(new String[] {
			"*.csv", "*.xls", "*"
		}, new String[] {
			"Character Separated Values", "Microsoft Excel 97", "All Files"
		});
		fbi.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return fbi;
	}
	
	/**
	 * The import process starts when the user has selected a file and clicked "OK". Warning: We can
	 * not read fields of the page created in createPage here! (The page is already disposed when
	 * doImport is called). If we have to transfer field values between createPage and doImport, we
	 * must override collect(). Our file based importer saves the user input in results[0]
	 */
	@Override
	public IStatus doImport(final IProgressMonitor monitor) throws Exception{
		// PersistentObject.getConnection().exec("DROP TABLE " + Leistung.TABLENAME + ";");
		// Leistung.createTable();
		File file = new File(results[0]);
		if (!file.canRead()) {
			log.log("Can't read " + results[0], Log.ERRORS);
			return new Status(Status.ERROR, PreferenceConstants.PLUGIN_ID, "Can't read "
				+ results[0]);
		}
		Result<String> res;
		if (results[0].endsWith(".xls")) {
			res = importExcel(file.getAbsolutePath(), monitor);
			;
		} else if (results[0].endsWith(".csv")) {
			res = importCSV(file.getAbsolutePath(), monitor);
		} else {
			return new Status(Status.ERROR, PreferenceConstants.PLUGIN_ID,
				"Unsupported file format");
		}
		if (res.isOK()) {
			
		}
		return ResultAdapter.getResultAsStatus(res);
	}
	
	/**
	 * return a description to display in the message area of the import dialog
	 */
	@Override
	public String getDescription(){
		return "Import aus CSV und Excel";
	}
	
	/**
	 * return a title to display in the title bar of the import dialog
	 */
	@Override
	public String getTitle(){
		return "Privatleistungen Berchtold";
	}
	
	private Result<String> importExcel(final String file, final IProgressMonitor mon){
		ExcelWrapper xl = new ExcelWrapper();
		if (!xl.load(file, 0)) {
			return new Result<String>(Result.SEVERITY.ERROR, 1, "Bad file format", file, true);
		}
		xl.setFieldTypes(new Class[] {
			String.class, String.class, String.class, Integer.class, Integer.class, Integer.class,
			TimeTool.class, TimeTool.class, Double.class
		});
		for (int i = xl.getFirstRow(); i <= xl.getLastRow(); i++) {
			List<String> row = xl.getRow(i);
			importLine(row.toArray(new String[0]));
		}
		return new Result<String>("OK");
	}
	
	private Result<String> importCSV(final String file, final IProgressMonitor mon){
		try {
			CSVReader cr = new CSVReader(new FileReader(file));
			String[] line;
			while ((line = cr.readNext()) != null) {
				importLine(line);
			}
			return new Result<String>("OK");
		} catch (Exception ex) {
			ExHandler.handle(ex);
			return new Result<String>(Result.SEVERITY.ERROR, 1, "Could not read " + file,
				ex.getMessage(), true);
		}
		
	}
	
	// parentCode 0,codeID 1,codeName 2,cost 3,price 4 ,time 5, validFrom
	// 6,validUntil 7,factor 8
	private void importLine(final String[] line){
		if (line.length == 8) {
			if (line[6].equals("")) {
				line[6] = TimeTool.BEGINNING_OF_UNIX_EPOCH;
			}
			if (line[7].equals("")) {
				line[7] = TimeTool.END_OF_UNIX_EPOCH;
			}
			IQuery<IPrivatLeistung> query =
				PrivatModelServiceHolder.get().getQuery(IPrivatLeistung.class);
			query.and("shortname", COMPARATOR.EQUALS, line[1]);
			List<IPrivatLeistung> res = query.execute();
			IPrivatLeistung lst;
			if (res.size() > 0) {
				lst = res.get(0);
				//				lst.set(new String[] {
				//					"parent", "Name", "Kosten", "Preis", "Zeit", "DatumVon", "DatumBis"
				//				}, line[0], line[2], line[3], line[4], line[5], line[6], line[7]);
			} else {
				lst = PrivatModelServiceHolder.get().create(IPrivatLeistung.class);
				//				new Leistung(null, line[0], line[2], line[1], line[3], line[4], line[5], line[6],
				//					line[7]);
			}
			lst.setParent(line[0]);
			lst.setCode(line[1]);
			lst.setText(line[2]);
			lst.setCost(line[3]);
			lst.setPrice(line[4]);
			lst.setTime(line[5]);
			lst.setValidFrom(line[6]);
			lst.setValidTo(line[7]);
			PrivatModelServiceHolder.get().save(lst);
		}
	}
}
