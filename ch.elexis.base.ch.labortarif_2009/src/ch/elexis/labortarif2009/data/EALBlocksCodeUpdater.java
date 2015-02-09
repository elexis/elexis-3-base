/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     T. Huster - initial API and implementation
 ******************************************************************************/
package ch.elexis.labortarif2009.data;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.exceptions.PersistenceException;
import ch.elexis.data.Leistungsblock;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.compress.CompEx;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;

public class EALBlocksCodeUpdater {
	private static final Logger logger = LoggerFactory.getLogger(EALBlocksCodeUpdater.class);
	
	private JdbcLink pj;
	
	protected static abstract class AbstractTarifResolver {
		abstract protected PersistentObject getTarif(String code, TimeTool date);
	}
	
	protected static class DefaultTarifResolver extends AbstractTarifResolver {
		@Override
		protected PersistentObject getTarif(String code, TimeTool date){
			return Labor2009Tarif.getFromCode(code, date);
		}
	}

	protected static class AnalysenUpdateTarifResolver extends AbstractTarifResolver {
		@Override
		protected PersistentObject getTarif(String code, TimeTool date){
			// look for .01 updated Tarif first
			if (code.endsWith(".00")) {
				String updateCode = code.substring(0, code.indexOf('.'));
				updateCode = updateCode + ".01";
				Labor2009Tarif updateTarif = Labor2009Tarif.getFromCode(updateCode, date);
				if (updateTarif != null) {
					return updateTarif;
				}
			}
			return Labor2009Tarif.getFromCode(code, date);
		}
	}

	protected static class AnalysenOnlyTarifResolver extends AbstractTarifResolver {
		@Override
		protected PersistentObject getTarif(String code, TimeTool date){
			// look for .01 updated Tarif first
			if (code.endsWith(".00")) {
				String updateCode = code.substring(0, code.indexOf('.'));
				updateCode = updateCode + ".01";
				Labor2009Tarif updateTarif = Labor2009Tarif.getFromCode(updateCode, date);
				return updateTarif;
			}
			return null;
		}
	}

	public String updateBlockCodesAnalysen() {
		return updateBlocks(Labor2009Tarif.class.getName(), new AnalysenUpdateTarifResolver());
	}
	
	/**
	 * Update the codes in Blocks to valid Labor2009Tarif for today.
	 * 
	 * @return a String containing a description of what was done
	 */
	public String updateBlockCodes(){
		return updateBlocks(Labor2009Tarif.class.getName(), new DefaultTarifResolver());
	}
	
	private String updateBlocks(String clazzName, AbstractTarifResolver resolver){
		int absoluteCnt = 0;
		HashSet<String> problems = new HashSet<String>();
		TimeTool today = new TimeTool();
		pj = PersistentObject.getConnection();
		try {
			Query<Leistungsblock> lQuery = new Query<Leistungsblock>(Leistungsblock.class);
			List<Leistungsblock> blocks = lQuery.execute();
			for (Leistungsblock block : blocks) {
				StringBuilder newCodes = new StringBuilder();
				// get blob
				byte[] compressed =
					getBinaryRaw(Leistungsblock.FLD_LEISTUNGEN, Leistungsblock.TABLENAME, block.getId());
				if (compressed != null) {
					// get String representing all contained leistungen
					String storable = new String(CompEx.expand(compressed), "UTF-8"); //$NON-NLS-1$
					// rebuild a String containing all updated leistungen
					for (String p : storable.split(",")) {
						if (p != null && !p.isEmpty()) {
							String[] parts = p.split("::");
							if (parts[0].equals(clazzName)) {
								String code = getCodeFromId(parts[1]);
								PersistentObject leistung = resolver.getTarif(code, today);
								if (leistung != null) {
									absoluteCnt++;
									// add new string
									if (newCodes.length() > 0)
										newCodes.append(",");
									newCodes.append(leistung.storeToString());
								} else {
									problems.add(block.getName() + " -> " + code);
									// set string old string
									if (newCodes.length() > 0)
										newCodes.append(",");
									newCodes.append(p);
								}
							} else {
								if (newCodes.length() > 0)
									newCodes.append(",");
								newCodes.append(p);
							}
						}
					}
					// write the updated String back
					setBinaryRaw(Leistungsblock.FLD_LEISTUNGEN, Leistungsblock.TABLENAME,
						block.getId(), CompEx.Compress(newCodes.toString(), CompEx.ZIP));
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Could not update " + clazzName + " in blocks." + e);
		}
		ArrayList<String> problemsList = new ArrayList<String>(problems);
		Collections.sort(problemsList);
		StringBuilder problemsString = new StringBuilder();
		for (String string : problemsList) {
			problemsString.append("- ").append(string).append("\n");
		}
		return absoluteCnt
			+ " EAL codes angepasst.\nIn folgenden Blöcken sind noch fehlerhafte Leistungen\n"
			+ problemsString.toString();
	}

	/**
	 * Get code by removing date
	 * 
	 * @param string
	 * @return
	 */
	private String getCodeFromId(String string){
		Labor2009Tarif tarif = Labor2009Tarif.load(string);
		if (tarif != null && tarif.exists()) {
			return tarif.getCode();
		}
		return "";
	}
	
	/**
	 * Copy of method from PersistentObject to get access to a binary field
	 * 
	 * @param field
	 * @return
	 */
	private byte[] getBinaryRaw(final String field, String tablename, String id){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ").append(field).append(" FROM ").append(tablename)
			.append(" WHERE ID='").append(id).append("'");
		
		Stm stm = null;
		try {
			stm = pj.getStatement();
			ResultSet res = stm.query(sql.toString());
			if ((res != null) && (res.next() == true)) {
				return res.getBytes(field);
			}
		} catch (Exception ex) {
			logger.error("Could not update Tarmed in blocks." + ex);
		} finally {
			if (stm != null) {
				pj.releaseStatement(stm);
			}
		}
		return null;
	}
	
	/**
	 * Copy of method from PersistentObject to get access to a binary field
	 * 
	 * @param field
	 * @return
	 */
	private void setBinaryRaw(final String field, String tablename, String id, final byte[] value){
		StringBuilder sql = new StringBuilder(1000);
		sql.append("UPDATE ").append(tablename).append(" SET ").append((field)).append("=?")
			.append(" WHERE ID='").append(id).append("'");
		String cmd = sql.toString();
		
		PreparedStatement stm = pj.prepareStatement(cmd);
		try {
			stm.setBytes(1, value);
			stm.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
			ExHandler.handle(ex);
		} finally {
			try {
				stm.close();
			} catch (SQLException e) {
				logger.error("Could not update Tarmed in blocks." + e);
				throw new PersistenceException("Could not close statement " + e.getMessage());
			}
		}
	}
}
