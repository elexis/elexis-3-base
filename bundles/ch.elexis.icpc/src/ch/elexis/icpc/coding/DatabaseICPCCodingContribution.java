package ch.elexis.icpc.coding;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.core.findings.ICoding;
import ch.elexis.core.findings.codes.CodingSystem;
import ch.elexis.core.findings.codes.ICodingContribution;
import ch.elexis.core.findings.codes.TransientCoding;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink.Stm;

@Component
public class DatabaseICPCCodingContribution implements ICodingContribution {

	private HashMap<String, ICoding> codesMap;

	@Override
	public String getCodeSystem() {
		return CodingSystem.ICPC2_CODESYSTEM.getSystem();
	}

	@Override
	public Optional<ICoding> getCode(String code) {
		if (codesMap == null && isPresent()) {
			initialize();
		}
		return Optional.ofNullable(codesMap.get(code));
	}

	@Override
	public synchronized List<ICoding> getCodes() {
		if (isPresent()) {
			if (codesMap == null) {
				initialize();
			}
			return new ArrayList<>(codesMap.values());
		}
		return Collections.emptyList();
	}

	private boolean isPresent() {
		if (PersistentObject.getDefaultConnection() != null) {
			return PersistentObject.tableExists("ICD10"); //$NON-NLS-1$
		}
		return true;
	}

	private void initialize() {
		codesMap = new HashMap<>();
		Stm statement = PersistentObject.getDefaultConnection().getStatement();
		try {
			ResultSet result = statement
					.query("SELECT ID, short FROM ch_elexis_icpc WHERE ID <> 1 AND short IS NOT NULL"); //$NON-NLS-1$
			while (result.next()) {
				String code = result.getString(1);
				String text = result.getString(2);
				codesMap.put(code, new TransientCoding(CodingSystem.ICPC2_CODESYSTEM.getSystem(), code, text));
			}
			result.close();
		} catch (SQLException e) {
			// ignore
		} finally {
			PersistentObject.getDefaultConnection().releaseStatement(statement);
		}
	}
}
