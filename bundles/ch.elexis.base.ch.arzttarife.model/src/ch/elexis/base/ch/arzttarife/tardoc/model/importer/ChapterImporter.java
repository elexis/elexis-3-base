package ch.elexis.base.ch.arzttarife.tardoc.model.importer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.ImporterUtil;
import ch.elexis.core.jpa.entities.TardocLeistung;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;

public class ChapterImporter {

	private static final Logger logger = LoggerFactory.getLogger(ChapterImporter.class);

	private JdbcLink cacheDb;
	private String lang;
	private String law;

	private int chapterCount;

	private HashMap<String, List<TardocLeistung>> importedChapters;

	private TimeTool validFrom;
	private TimeTool validTo;

	public ChapterImporter(JdbcLink cacheDb, String lang, String law) {
		this.cacheDb = cacheDb;
		this.lang = lang;
		this.law = law;
		importedChapters = new HashMap<>();
		validFrom = new TimeTool();
		validTo = new TimeTool();
	}

	public void setChapterCount(int count) {
		this.chapterCount = count;
	}

	public IStatus doImport(IProgressMonitor ipm) throws SQLException, IOException {
		Stm source = null;
		try {
			ipm.subTask(Messages.TarmedImporter_chapter);

			List<Object> imported = new ArrayList<>();
			source = cacheDb.getStatement();
			try (ResultSet res = source.query(String.format("SELECT * FROM %sKAPITEL_TEXT WHERE SPRACHE='%s'", //$NON-NLS-1$
					TardocReferenceDataImporter.ImportPrefix, lang))) {
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
					TardocLeistung tl = new TardocLeistung();
					tl.setId(id);
					tl.setCode_(code);
					tl.setParent(parentId);
					tl.setDigniQuali(StringUtils.EMPTY);
					tl.setDigniQuanti(StringUtils.EMPTY);
					tl.setSparte(StringUtils.EMPTY);
					tl.setChapter(true);

					String text = ImporterUtil.getAsString(res, "BEZ_255"); //$NON-NLS-1$
					tl.setTx255(text);
					tl.setGueltigVon(validFrom.toLocalDate());
					tl.setGueltigBis(validTo.toLocalDate());
					tl.setLaw(law);

					ipm.subTask(Messages.TarmedImporter_chapter + " (" + count++ + "/" + chapterCount + ")");

					imported.add(tl);
					addToImportedChapters(tl);
					ipm.worked(1);
					if (ipm.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}
				EntityUtil.save(imported);
			}
		} finally {
			if (source != null) {
				cacheDb.releaseStatement(source);
			}
		}
		return Status.OK_STATUS;
	}

	private void addToImportedChapters(TardocLeistung tl) {
		String code = tl.getCode();
		List<TardocLeistung> list = importedChapters.get(code);
		if (list == null) {
			list = new ArrayList<>();
		}
		list.add(tl);
		list.sort((l, r) -> l.getGueltigVon().compareTo(r.getGueltigVon()));
		importedChapters.put(code, list);
		logger.debug("Imported " + tl.getCode());
	}

	private String getParentId(String parentCode) throws SQLException {
		Stm source = null;
		List<TimeTool> parentValidFroms = new ArrayList<>();
		try {
			source = cacheDb.getStatement();
			try (ResultSet res = source
					.query(String.format("SELECT * FROM %sKAPITEL_TEXT WHERE SPRACHE='%s' AND KNR='%s'", //$NON-NLS-1$
							TardocReferenceDataImporter.ImportPrefix, lang, parentCode))) {
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
			if (parentValidFrom.isBeforeOrEqual(validTo) && parentValidFrom.isAfter(latestParentValidFrom)) {
				latestParentValidFrom = parentValidFrom;
			}
		}
		if (latestParentValidFrom == null) {
			throw new IllegalStateException(
					"No parent valid from found for " + parentCode + " in " + parentValidFroms.size() + " values");
		}

		return parentCode + "-" + latestParentValidFrom.toString(TimeTool.DATE_COMPACT) //$NON-NLS-1$
				+ getLawIdExtension();
	}

	private String getId(ResultSet res) throws SQLException {
		return res.getString("KNR") + "-" + validFrom.toString(TimeTool.DATE_COMPACT) //$NON-NLS-1$
				+ getLawIdExtension();
	}

	private String getLawIdExtension() {
		if (law != null && !law.isEmpty()) {
			return "-" + law;
		}
		return StringUtils.EMPTY;
	}

	public String getIdForCode(String lookupCode, LocalDate lookupValidFrom, String lookupLaw) {
		List<TardocLeistung> list = importedChapters.get(lookupCode);
		if (!list.isEmpty()) {
			if (list.size() == 1) {
				return list.get(0).getId();
			} else {
				// lookup matching by law and valid from
				for (TardocLeistung tarmedLeistung : list) {
					String currLaw = tarmedLeistung.getLaw();
					LocalDate currValidFrom = tarmedLeistung.getGueltigVon();
					if ((currValidFrom.isAfter(lookupValidFrom) || currValidFrom.isEqual(lookupValidFrom))
							&& currLaw.equals(lookupLaw)) {
						return tarmedLeistung.getId();
					}
				}
				// fallback return latest
				TardocLeistung latestLeistung = list.get(list.size() - 1);
				String latestLaw = latestLeistung.getLaw();
				if (latestLaw.equals(lookupLaw)) {
					return latestLeistung.getId();
				}
			}
		}
		return null;
	}
}
