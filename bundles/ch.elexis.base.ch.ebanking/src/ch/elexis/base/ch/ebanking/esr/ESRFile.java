/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.base.ch.ebanking.esr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import ch.elexis.data.Query;
import ch.elexis.ebanking.parser.Camt054Parser;
import ch.elexis.ebanking.parser.Camt054Record;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Result;
import ch.rgw.tools.StringTool;

/**
 * Ein ESRFile ist eine Datei, wie sie von der Bank heruntergeladen werden kann,
 * um VESR-Records zu verbuchen
 *
 * @author gerry
 *
 */
public class ESRFile {
	List<ESRRecord> list = new ArrayList<ESRRecord>();
	String name;
	String hash;

	/**
	 * ein ESR-File einlesen
	 *
	 * @param filename vollständiger Pfadname der Datei
	 * @return true wenn die Datei erfolgreich gelesen werden konnte
	 */
	public Result<List<ESRRecord>> read(File file, final IProgressMonitor monitor) {

		if (!file.exists()) {
			return new Result<List<ESRRecord>>(Result.SEVERITY.ERROR, 1, Messages.ESRFile_esrfile_not_founde, null,
					true);
		}
		if (!file.canRead()) {
			return new Result<List<ESRRecord>>(Result.SEVERITY.ERROR, 2, Messages.ESRFile_cannot_read_esr, null, true);
		}
		byte[] md5 = FileTool.checksum(file);
		name = file.getName();
		if (md5 != null) {
			hash = StringTool.enPrintableStrict(md5);
		} else {
			hash = name;
		}
		Query<ESRRecord> qesr = new Query<ESRRecord>(ESRRecord.class);
		qesr.add("File", "=", hash); //$NON-NLS-1$ //$NON-NLS-2$
		List<ESRRecord> list = qesr.execute();
		if (list.size() > 0) {
			return new Result<List<ESRRecord>>(Result.SEVERITY.ERROR, 4, Messages.ESRFile_file_already_read, null,
					true);
		}
		String fileName = file.getName();
		if ("xml".equalsIgnoreCase(FilenameUtils.getExtension(fileName))) { //$NON-NLS-1$

			try (InputStream inputStream = new FileInputStream(file)) {
				Camt054Parser camt054Parser = new Camt054Parser();
				List<Camt054Record> inputs = camt054Parser.parseRecords(inputStream);

				for (Camt054Record camt054Record : inputs) {
					ESRRecord esr = new ESRRecord(hash, camt054Record);
					list.add(esr);
					monitor.worked(1);

				}
				return new Result<List<ESRRecord>>(Result.SEVERITY.OK, 0, "OK", list, false); //$NON-NLS-1$
			} catch (Exception ex) {
				ExHandler.handle(ex);
				return new Result<List<ESRRecord>>(Result.SEVERITY.ERROR, 3, Messages.ESRFile_ExceptionParsing, list,
						true);
			}
		} else {
			try (InputStreamReader ir = new InputStreamReader(new FileInputStream(file));
					BufferedReader br = new BufferedReader(ir)) {
				String in;
				// String date=new TimeTool().toString(TimeTool.DATE_COMPACT);
				LinkedList<String> records = new LinkedList<String>();
				while ((in = br.readLine()) != null) {
					for (int i = 0; i < in.length(); i += 128) {
						int eidx = i + 125;
						if (eidx >= in.length()) {
							eidx = in.length() - 1;
						}
						records.add(in.substring(i, eidx));
					}
				}
				for (String s : records) {
					ESRRecord esr = new ESRRecord(hash, s);
					list.add(esr);
					monitor.worked(1);
				}

				return new Result<List<ESRRecord>>(Result.SEVERITY.OK, 0, "OK", list, false); //$NON-NLS-1$
			} catch (Exception ex) {
				ExHandler.handle(ex);
				return new Result<List<ESRRecord>>(Result.SEVERITY.ERROR, 3, Messages.ESRFile_ExceptionParsing, list,
						true);
			}
		}
	}

	public List<ESRRecord> getLastResult() {
		return list;
	}

	public String getFilename() {
		return name;
	}
}
