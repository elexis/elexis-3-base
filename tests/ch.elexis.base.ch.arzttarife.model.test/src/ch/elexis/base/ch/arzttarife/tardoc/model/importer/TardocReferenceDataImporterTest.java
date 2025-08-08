package ch.elexis.base.ch.arzttarife.tardoc.model.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup;
import ch.elexis.base.ch.arzttarife.tardoc.model.TardocGroup;
import ch.elexis.base.ch.arzttarife.tardoc.model.TardocLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.model.VersionUtil;

public class TardocReferenceDataImporterTest {

	private String codeEchokardiografie = "TK.05.0010";
	private String codeEchokardiografieIncompatible = "TK.05.0050";

	private String codeGynaekologie = "VG.00.0010";

	private String codeHierarchySlave = "GP.00.0020";
	private String codeHierarchyMaster = "GP.00.0010";
	private String codeHierarchyNotMaster = "GP.10.0010";

	private String codeGroup = "LG-202";
	private String codeInGroup = "AR.05.0070";
	private String codeNotInGroup = "VG.00.0010";

	private String codeLimitGroup = "LG-303";
	private String codeLimitInGroup = "MP.05.0070";

	@Test
	public void performImport() throws FileNotFoundException, SQLException {
		// IMPORTANT download the database from
		// (https://oaat-otma.ch/)
		// and add to the rsc folder of test bundle
		InputStream tarmedInStream = TardocReferenceDataImporterTest.class
				.getResourceAsStream("/rsc/250410_TARDOC_1.4b_ohne_001_4.mdb");

		TardocReferenceDataImporter importer = new TardocReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(), tarmedInStream, null);
		assertEquals(IStatus.OK, retStatus.getCode());
	}

	@Test
	public void performImportAndCheck() throws FileNotFoundException, SQLException {
		// IMPORTANT download the database from
		// (https://oaat-otma.ch/)
		// and add to the rsc folder of test bundle
		InputStream tarmedInStream = TardocReferenceDataImporterTest.class
				.getResourceAsStream("/rsc/250410_TARDOC_1.4b_ohne_001_4.mdb");

		TardocReferenceDataImporter importer = new TardocReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(), tarmedInStream, null);
		assertEquals(IStatus.OK, retStatus.getCode());

		LocalDate start = LocalDate.of(2026, 1, 1);

		// gynaekologie
		TardocLeistung gynaekologie = TardocLeistung.getFromCode(codeGynaekologie, start, null);
		assertNotNull(gynaekologie);
		assertEquals(codeGynaekologie, gynaekologie.getCode());
		assertNotNull(gynaekologie.getText());
		assertTrue(StringUtils.isNotBlank(gynaekologie.getText()));

		// exclusion
		TardocLeistung echoKardiografie = TardocLeistung.getFromCode(codeEchokardiografie, start, null);
		assertNotNull(echoKardiografie);
		assertEquals(codeEchokardiografie, echoKardiografie.getCode());

		String exclusions = echoKardiografie.getExclusion(start);
		assertNotNull(exclusions);
		assertTrue(exclusions.contains(codeEchokardiografieIncompatible));

		// hierarchy
		TardocLeistung hierarchyMaster = TardocLeistung.getFromCode(codeHierarchyMaster, start, null);
		List<String> slaves = hierarchyMaster.getHierarchy(start);
		assertNotNull(slaves);
		assertTrue(slaves.contains(codeHierarchySlave));
		TardocLeistung hierarchyNotMaster = TardocLeistung.getFromCode(codeHierarchyNotMaster, start, null);
		slaves = hierarchyNotMaster.getHierarchy(start);
		assertNotNull(slaves);
		assertFalse(slaves.contains(codeHierarchySlave));

		// groups
		TardocLeistung inGroup = TardocLeistung.getFromCode(codeInGroup, start, null);
		List<String> groups = inGroup.getServiceGroups(start);
		assertNotNull(groups);
		assertTrue(groups.contains(codeGroup));
		TardocLeistung notInGroup = TardocLeistung.getFromCode(codeNotInGroup, start, null);
		groups = notInGroup.getServiceGroups(start);
		assertFalse(groups.contains(codeGroup));

		// group limits
		TardocLeistung limitInGroup = TardocLeistung.getFromCode(codeLimitInGroup, start, null);
		groups = limitInGroup.getServiceGroups(start);
		assertTrue(groups.contains("LG-303"));
		Optional<ITardocGroup> tarmedGroup = TardocGroup.find("LG-303", inGroup.getLaw(), inGroup.getValidFrom());
		assertTrue(tarmedGroup.isPresent());
		String limits = tarmedGroup.get().getExtension().getLimits().get("limits");
		assertTrue(limits != null && !limits.isEmpty());

		// parents #9212
		Stream<?> found = ArzttarifeModelServiceHolder.get().executeNativeQuery(testParentsSql);
		assertFalse(found.findFirst().isPresent());
	}

	private static String testParentsSql = "SELECT * FROM TARDOC WHERE PARENT NOT IN (SELECT ID FROM TARDOC) AND PARENT NOT LIKE 'NIL'";

	@Test
	public void currentVersion() {
		VersionUtil.setCurrentVersion("1", "");
		assertEquals(1, VersionUtil.getCurrentVersion());
		assertEquals(1, VersionUtil.getCurrentVersion(""));
		assertEquals(1, VersionUtil.getCurrentVersion(null));

		VersionUtil.setCurrentVersion("2", "kvg");
		assertEquals(2, VersionUtil.getCurrentVersion("kvg"));
		assertEquals(1, VersionUtil.getCurrentVersion(""));

		VersionUtil.setCurrentVersion("3", "uvg");
		assertEquals(3, VersionUtil.getCurrentVersion("uvg"));
		assertEquals(2, VersionUtil.getCurrentVersion("kvg"));
		assertEquals(1, VersionUtil.getCurrentVersion(""));

		VersionUtil.setCurrentVersion("3", "kvg");
		assertEquals(3, VersionUtil.getCurrentVersion("uvg"));
		assertEquals(3, VersionUtil.getCurrentVersion("kvg"));
		assertEquals(1, VersionUtil.getCurrentVersion(""));
	}
}
