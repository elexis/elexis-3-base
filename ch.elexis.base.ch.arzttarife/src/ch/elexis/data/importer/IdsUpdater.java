package ch.elexis.data.importer;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.LoggerFactory;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Leistungsblock;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.TarmedLeistung;
import ch.elexis.data.Verrechnet;
import ch.rgw.compress.CompEx;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;

public class IdsUpdater {
	
	private String law;
	private JdbcLink jdbcLink;
	
	public IdsUpdater(String law){
		this.law = law;
		this.jdbcLink = PersistentObject.getDefaultConnection().getJdbcLink();
	}
	
	public IStatus updateVerrechnet(IProgressMonitor ipm){
		PreparedStatement ps = null;
		// update existing ids of Verrechnet
		try {
			ps = jdbcLink.getPreparedStatement(
				"UPDATE " + Verrechnet.TABLENAME + " SET leistg_code=? WHERE id=?"); //$NON-NLS-1$
			
			Query<Verrechnet> vQuery = new Query<Verrechnet>(Verrechnet.class);
			vQuery.add(Verrechnet.CLASS, "=", TarmedLeistung.class.getName());
			List<Verrechnet> verrechnete = vQuery.execute();
			for (Verrechnet verrechnet : verrechnete) {
				// make sure code and date of consultation are available
				String code = verrechnet.get(Verrechnet.LEISTG_CODE);
				TimeTool date = null;
				Konsultation kons = verrechnet.getKons();
				if (kons != null && kons.getDatum() != null)
					date = new TimeTool(kons.getDatum());
				if (code != null && date != null) {
					ipm.subTask(Messages.TarmedImporter_updateVerrechnet + " " + code + " "
						+ date.toString(TimeTool.DATE_COMPACT));
					TarmedLeistung leistung =
						(TarmedLeistung) TarmedLeistung.getFromCode(code, date);
					// update the id
					if (leistung != null) {
						ps.setString(1, leistung.getId());
						ps.setString(2, verrechnet.getId());
						ps.execute();
					}
				}
			}
		} catch (SQLException e) {
			LoggerFactory.getLogger(getClass()).error("Error updating ids", e);
			return Status.CANCEL_STATUS;
		} finally {
			if (ps != null) {
				jdbcLink.releasePreparedStatement(ps);
			}
		}
		return Status.OK_STATUS;
	}
	
	public IStatus updateStatistics(IProgressMonitor ipm){
		// update existing ids in statistics
		Query<Kontakt> kQuery = new Query<Kontakt>(Kontakt.class);
		List<Kontakt> kontakte = kQuery.execute();
		for (Kontakt kontakt : kontakte) {
			Map exi = kontakt.getMap(Kontakt.FLD_EXTINFO);
			String typ = TarmedLeistung.class.getName();
			// get list of type
			List l = (List) exi.get(typ);
			if (l != null) {
				// we dont have access to statL.v member so update is not possible
				// for (Kontakt.statL statL : l) {
				// String[] ci = statL.v.split("::");
				// if (ci.length == 2) {
				// TarmedLeistung leistung =
				// (TarmedLeistung) TarmedLeistung.getFromCode(ci[1]);
				// if (leistung != null)
				// statL.v = leistung.storeToString();
				// }
				// }
				// clear existing statistics
				l.clear();
				exi.put(typ, l);
				kontakt.setMap(Kontakt.FLD_EXTINFO, exi);
			}
		}
		return Status.OK_STATUS;
	}
	
	public IStatus udpateLeistungsBlock(IProgressMonitor ipm){
		IStatus ret = Status.OK_STATUS;
		try {
			Query<Leistungsblock> lQuery = new Query<Leistungsblock>(Leistungsblock.class);
			List<Leistungsblock> blocks = lQuery.execute();
			for (Leistungsblock block : blocks) {
				StringBuilder newCodes = new StringBuilder();
				// get blob
				byte[] compressed = getBinaryRaw(Leistungsblock.FLD_LEISTUNGEN,
					Leistungsblock.TABLENAME, block.getId());
				if (compressed != null) {
					// get String representing all contained leistungen
					String storable = new String(CompEx.expand(compressed), "UTF-8"); //$NON-NLS-1$
					// rebuild a String containing all leistungen but update TarmedLeistungen
					for (String p : storable.split(",")) {
						if (p != null && !p.isEmpty()) {
							String[] parts = p.split("::");
							if (parts[0].equals(TarmedLeistung.class.getName())) {
								ipm.subTask(Messages.TarmedImporter_updateBlock + " " + parts[1]);
								TarmedLeistung leistung =
									(TarmedLeistung) TarmedLeistung.getFromCode(parts[1]);
								if (leistung != null) {
									// add new string
									if (newCodes.length() > 0)
										newCodes.append(",");
									newCodes.append(leistung.storeToString());
								} else {
									ret = new Status(Status.WARNING, "", "Update block warning");
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
			return Status.CANCEL_STATUS;
		}
		return ret;
	}
	
	/**
	 * Copy of method from PersistentObject to get access to a binary field
	 * 
	 * @param field
	 * @return
	 */
	private byte[] getBinaryRaw(final String field, String tablename, String id){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ").append(field).append(" FROM ").append(tablename).append(" WHERE ID='")
			.append(id).append("'");
		
		Stm stm = null;
		try {
			stm = jdbcLink.getStatement();
			ResultSet res = stm.query(sql.toString());
			if ((res != null) && (res.next() == true)) {
				return res.getBytes(field);
			}
		} catch (Exception ex) {
			LoggerFactory.getLogger(getClass()).error("Error getting binary", ex);
		} finally {
			if (stm != null) {
				jdbcLink.releaseStatement(stm);
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
		
		PreparedStatement stm = jdbcLink.getPreparedStatement(cmd);
		try {
			stm.setBytes(1, value);
			stm.executeUpdate();
		} catch (Exception ex) {
			LoggerFactory.getLogger(getClass()).error("Error setting binary", ex);
		} finally {
			jdbcLink.releasePreparedStatement(stm);
		}
	}
}
