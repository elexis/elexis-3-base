package ch.elexis.base.ch.arzttarife.tardoc.model.importer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.LoggerFactory;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.core.jpa.entities.TardocDefinitionen;
import ch.elexis.core.jpa.entities.TarmedDefinitionen;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;

public class DefinitionImport {

	private JdbcLink cacheDb;
	private String lang;
	private String law;

	public DefinitionImport(JdbcLink cacheDb, String lang, String law) {
		this.cacheDb = cacheDb;
		this.lang = lang;
		this.law = law;
	}

	/**
	 * Import code and text of the various CT_ tables from the cacheDb, into
	 * {@link TarmedDefinitionen}. Only the newest (GUELTIG_VON) entry is imported.
	 *
	 * @param ipm
	 * @return
	 */
	public IStatus doImport(IProgressMonitor ipm) {
		String[] fields = new String[] { "ANAESTHESIE", "DIGNI_QUALI", "DIGNI_QUANTI", "LEISTUNG_BLOECKE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				"LEISTUNG_GRUPPEN", "LEISTUNG_TYP", "PFLICHT", "REGEL_EL_ABR", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				"SEITE", "SEX", "SPARTE", "ZR_EINHEIT" //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
		};

		Stm stmCached = null;
		try {
			ipm.subTask(Messages.TarmedImporter_definitions);

			stmCached = cacheDb.getStatement();
			for (String s : fields) {
				ResultSet res = stmCached.query(String.format("SELECT * FROM %sCT_" + s + " WHERE SPRACHE='%s'", //$NON-NLS-1$
						TardocReferenceDataImporter.ImportPrefix, lang));
				importNewest(s, res);
				res.close();
			}
		} catch (SQLException e) {
			LoggerFactory.getLogger(getClass()).error("Error importing definition", e);
		} finally {
			if (stmCached != null) {
				cacheDb.releaseStatement(stmCached);
			}
		}
		return Status.OK_STATUS;
	}

	protected void importNewest(String spalte, ResultSet res) throws SQLException {
		List<String[]> sorted = new ArrayList<String[]>();
		while (res.next()) {
			String[] values = new String[3];
			// kuerzel
			values[0] = res.getString(1);
			// titel
			values[1] = res.getString(3);
			// gueltig von (dd.MM.yy hh:mm)
			values[2] = res.getString("GUELTIG_VON");
			sorted.add(values);
		}
		// sort by code and GUELTIG_VON
		sorted.sort(new Comparator<String[]>() {
			private TimeTool leftTime = new TimeTool();
			private TimeTool rightTime = new TimeTool();

			@Override
			public int compare(String[] left, String[] right) {
				int ret = left[0].compareTo(right[0]);
				if (ret == 0) {
					leftTime.set(left[2]);
					rightTime.set(right[2]);
					ret = rightTime.compareTo(leftTime);
				}
				return ret;
			}
		});
		importNewest(spalte, sorted);
	}

	protected void importNewest(String spalte, List<String[]> sorted) {
		String lastCode = null;
		List<Object> newDefinitionen = new ArrayList<>();
		for (String[] strings : sorted) {
			if (!strings[0].equals(lastCode)) {
				TardocDefinitionen definitionen = new TardocDefinitionen();
				definitionen.setSpalte(spalte);
				definitionen.setKuerzel(strings[0]);
				definitionen.setTitel(strings[1]);
				definitionen.setLaw(law);
				newDefinitionen.add(definitionen);
				lastCode = strings[0];
			}
		}
		EntityUtil.save(newDefinitionen);
	}
}
