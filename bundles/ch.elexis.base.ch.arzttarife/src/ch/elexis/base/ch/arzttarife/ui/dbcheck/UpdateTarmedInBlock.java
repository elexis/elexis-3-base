package ch.elexis.base.ch.arzttarife.ui.dbcheck;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.exceptions.PersistenceException;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.data.Leistungsblock;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.TarmedLeistung;
import ch.rgw.compress.CompEx;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.JdbcLinkException;
import ch.rgw.tools.TimeTool;

public class UpdateTarmedInBlock extends ExternalMaintenance {
	
	private static Logger logger = LoggerFactory.getLogger(UpdateTarmedInBlock.class);

	private JdbcLink pj;

	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion){
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
					// rebuild a String containing all leistungen but update TarmedLeistungen
					for (String p : storable.split(",")) {
						if (p != null && !p.isEmpty()) {
							String[] parts = p.split("::");
							if (parts[0].equals(TarmedLeistung.class.getName())) {
								String code = getCodeFromId(parts[1]);
								TarmedLeistung leistung =
									(TarmedLeistung) TarmedLeistung.getFromCode(code, today);
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
			logger.error("Could not update Tarmed in blocks." + e);
		}
		ArrayList<String> problemsList = new ArrayList<String>(problems);
		Collections.sort(problemsList);
		StringBuilder problemsString = new StringBuilder();
		for (String string : problemsList) {
			problemsString.append("- ").append(string).append("\n");
		}
		return absoluteCnt
			+ " Tarmed Leistungen angepasst.\nIn folgenden Blöcken sind noch fehlerhafte Leistungen\n"
			+ problemsString.toString();
	}
	
	/**
	 * Get code by removing date
	 * 
	 * @param string
	 * @return
	 */
	private String getCodeFromId(String string){
		String[] parts = string.split("-");
		if (parts.length == 2) {
			return parts[0];
		}
		return string;
	}

	@Override
	public String getMaintenanceDescription(){
		return "Tarmed Leistungen in den Blöcken auf das heutige Datum anpassen";
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
	
	/**
	 * Execute the sql string and handle exceptions appropriately.
	 * <p>
	 * <b>ATTENTION:</b> JdbcLinkResourceException will trigger a restart of Elexis in
	 * at.medevit.medelexis.ui.statushandler.
	 * </p>
	 * 
	 * @param sql
	 * @return
	 */
	private ResultSet executeSqlQuery(String sql){
		JdbcLink conn = null;
		Stm stm = null;
		ResultSet res = null;
		try {
			conn = PersistentObject.getConnection();
			stm = conn.getStatement();
			res = stm.query(sql);
		} catch (JdbcLinkException je) {
			je.printStackTrace();
			ExHandler.handle(je);
		} finally {
			if (stm != null && conn != null)
				conn.releaseStatement(stm);
		}
		return res;
	}
}
