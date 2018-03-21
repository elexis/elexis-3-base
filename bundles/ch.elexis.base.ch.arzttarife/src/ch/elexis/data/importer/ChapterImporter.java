package ch.elexis.data.importer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.data.TarmedLeistung;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;

public class ChapterImporter {
	
	private static final Logger logger = LoggerFactory.getLogger(ChapterImporter.class);
	
	private JdbcLink cacheDb;
	private String lang;
	private String law;
	
	private int chapterCount;
	
	private HashMap<String, List<TarmedLeistung>> importedChapters;
	
	private TimeTool validFrom;
	private TimeTool validTo;
	
	public ChapterImporter(JdbcLink cacheDb, String lang, String law){
		this.cacheDb = cacheDb;
		this.lang = lang;
		this.law = law;
		importedChapters = new HashMap<>();
		validFrom = new TimeTool();
		validTo = new TimeTool();
	}
	
	public void setChapterCount(int count){
		this.chapterCount = count;
	}
	
	public IStatus doImport(IProgressMonitor ipm) throws SQLException, IOException{
		Stm source = null;
		try {
			ipm.subTask(Messages.TarmedImporter_chapter);
			
			source = cacheDb.getStatement();
			try (ResultSet res = source.query(String
				.format("SELECT * FROM %sKAPITEL_TEXT WHERE SPRACHE=%s", //$NON-NLS-1$
					TarmedReferenceDataImporter.ImportPrefix, lang))) {
				int count = 0;
				while (res != null && res.next()) {
					String code = res.getString("KNR"); //$NON-NLS-1$
					if (code.trim().equals("I")) { //$NON-NLS-1$
						continue;
					}
					validFrom.set(res.getString("GUELTIG_VON"));
					validTo.set(res.getString("GUELTIG_BIS"));
					
					int subcap = code.lastIndexOf('.');
					String parentId = "NIL"; //$NON-NLS-1$
					if (subcap != -1) {
						String parentCode = code.substring(0, subcap);
						parentId = getParentId(parentCode);
					}
					String id = getId(res);
					TarmedLeistung tl = new TarmedLeistung(id, code, parentId, "", "", "", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					
					String text = ImporterUtil.getAsString(res, "BEZ_255"); //$NON-NLS-1$
					tl.set(new String[] {
						TarmedLeistung.FLD_TEXT, TarmedLeistung.FLD_GUELTIG_VON,
						TarmedLeistung.FLD_GUELTIG_BIS, TarmedLeistung.FLD_LAW //$NON-NLS-1$ //$NON-NLS-2$
					}, text, validFrom.toString(TimeTool.DATE_COMPACT),
						validTo.toString(TimeTool.DATE_COMPACT), law); //$NON-NLS-1$ //$NON-NLS-2$
					
					ipm.subTask(Messages.TarmedImporter_chapter + " (" + count++ + "/"
						+ chapterCount + ")");
					addToImportedChapters(tl);
					ipm.worked(1);
					if (ipm.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}
			}
		} finally {
			if (source != null) {
				cacheDb.releaseStatement(source);
			}
		}
		return Status.OK_STATUS;
	}
	
	private void addToImportedChapters(TarmedLeistung tl){
		String code = tl.getCode();
		List<TarmedLeistung> list = importedChapters.get(code);
		if (list == null) {
			list = new ArrayList<>();
		}
		list.add(tl);
		list.sort((l, r) -> r.getGueltigVon().compareTo(r.getGueltigVon()));
		importedChapters.put(code, list);
		logger.debug("Imported " + tl.getLabel());
	}
	
	private String getParentId(String parentCode) throws SQLException{
		Stm source = null;
		List<TimeTool> parentValidFroms = new ArrayList<>();
		try {
			source = cacheDb.getStatement();
			try (ResultSet res = source
				.query(String.format("SELECT * FROM %sKAPITEL_TEXT WHERE SPRACHE=%s AND KNR=%s", //$NON-NLS-1$
					TarmedReferenceDataImporter.ImportPrefix, lang, JdbcLink.wrap(parentCode)))) {
				while (res != null && res.next()) {
					String code = res.getString("KNR"); //$NON-NLS-1$
					if (code.trim().equals("I")) { //$NON-NLS-1$
						continue;
					}
					parentValidFroms.add(new TimeTool(res.getString("GUELTIG_VON")));
				}
			}
		} finally {
			if (source != null) {
				cacheDb.releaseStatement(source);
			}
		}
		if (parentValidFroms.isEmpty()) {
			throw new IllegalStateException("No parent valid from found for " + parentCode);
		}
		// determine the latest matching parent valid from
		TimeTool latestParentValidFrom = null;
		for (TimeTool parentValidFrom : parentValidFroms) {
			if (latestParentValidFrom == null && parentValidFrom.isBeforeOrEqual(validFrom)) {
				latestParentValidFrom = parentValidFrom;
				continue;
			}
			if (parentValidFrom.isBeforeOrEqual(validTo)
				&& parentValidFrom.isAfter(latestParentValidFrom)) {
				latestParentValidFrom = parentValidFrom;
			}
		}
		if (latestParentValidFrom == null) {
			throw new IllegalStateException("No parent valid from found for " + parentCode + " in "
				+ parentValidFroms.size() + " values");
		}
		
		return parentCode + "-" + latestParentValidFrom.toString(TimeTool.DATE_COMPACT) //$NON-NLS-1$
			+ getLawIdExtension();
	}
	
	private String getId(ResultSet res) throws SQLException{
		return res.getString("KNR") + "-" + validFrom.toString(TimeTool.DATE_COMPACT) //$NON-NLS-1$
			+ getLawIdExtension();
	}
	
	private String getLawIdExtension(){
		if (law != null && !law.isEmpty()) {
			return "-" + law;
		}
		return "";
	}
	
	public String getIdForCode(String lookupCode, TimeTool lookupValidFrom, String lookupLaw){
		List<TarmedLeistung> list = importedChapters.get(lookupCode);
		if (!list.isEmpty()) {
			if (list.size() == 1) {
				return list.get(0).getId();
			} else {
				// lookup matching by law and valid from
				for (TarmedLeistung tarmedLeistung : list) {
					String currLaw = tarmedLeistung.get(TarmedLeistung.FLD_LAW);
					TimeTool currValidFrom = tarmedLeistung.getGueltigVon();
					if (currValidFrom.isAfterOrEqual(lookupValidFrom)
						&& currLaw.equals(lookupLaw)) {
						return tarmedLeistung.getId();
					}
				}
				// fallback return latest
				TarmedLeistung latestLeistung = list.get(list.size() - 1);
				String latestLaw = latestLeistung.get(TarmedLeistung.FLD_LAW);
				if (latestLaw.equals(lookupLaw)) {
					return latestLeistung.getId();
				}
			}
		}
		return null;
	}
}
