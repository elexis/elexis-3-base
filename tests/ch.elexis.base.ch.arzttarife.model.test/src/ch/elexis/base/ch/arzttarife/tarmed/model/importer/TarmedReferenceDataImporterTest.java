package ch.elexis.base.ch.arzttarife.tarmed.model.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExclusive;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedGroup;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedKumulation;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.model.VersionUtil;
import ch.rgw.tools.TimeTool;

public class TarmedReferenceDataImporterTest {
	private String codeGutachtenKatA = "00.2310";
	private String codeAerztlGutachten = "00.07";
	private String codeTapingKat1 = "01.0110";
	private String codeBesuchErste5Min = "00.0060";

	private String codeEchokardiografie = "17.0210";
	private String codeEchokardiografieIncompatible = "17.0230";
	private String codeEchokardiografieExpired1 = "17.0280";
	private String codeEchokardiografieExpired2 = "17.0290";

	private String codeGynaekologie = "22.0035";

	private String codeHierarchySlave = "39.5010";
	private String codeHierarchyMaster = "39.5060";
	private String codeHierarchyNotMaster = "39.5120";

	private String codeGroup = "33";
	private String codeInGroup = "39.0370";
	private String codeNotInGroup = "39.3005";

	private String codeLimitGroup = "31";
	private String codeLimitInGroup = "02.0310";

	private String codeBlock = "04";
	private String codeInBlock = "15.0720";
	private String codeNotInBlock = "15.0750";

	@Test
	public void performImport() throws FileNotFoundException, SQLException {
		// IMPORTANT download the database from
		// (https://www.fmh.ch/themen/ambulante-tarife/tarmed-tarifbrowser-datenbank.cfm)
		// and add to the rsc folder of test bundle
		InputStream tarmedInStream = TarmedReferenceDataImporterTest.class
				.getResourceAsStream("/rsc/TARMED_Datenbank_01.08.00_BR_UVG_IVG_MVG.mdb");

		TarmedReferenceDataImporter importer = new TarmedReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(), tarmedInStream, null);
		assertEquals(IStatus.OK, retStatus.getCode());
	}

	@Test
	public void performImportAndCheck() throws FileNotFoundException, SQLException {
		// IMPORTANT download the database from
		// (https://www.fmh.ch/themen/ambulante-tarife/tarmed-tarifbrowser-datenbank.cfm)
		// and add to the rsc folder of test bundle
		InputStream tarmedInStream = TarmedReferenceDataImporterTest.class
				.getResourceAsStream("/rsc/TARMED__Datenbank_01.09.00_BR_KVG-27.12.2017.mdb");

		TarmedReferenceDataImporter importer = new KVGTarmedReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(), tarmedInStream, null);
		assertEquals(IStatus.OK, retStatus.getCode());

		// gynaekologie
		TarmedLeistung gynaekologie = TarmedLeistung.getFromCode(codeGynaekologie, LocalDate.now(), null);
		assertNotNull(gynaekologie);
		assertEquals(codeGynaekologie, gynaekologie.getCode());
		assertNotNull(gynaekologie.getText());
		assertTrue(StringUtils.isNotBlank(gynaekologie.getText()));
		String ageLimit = gynaekologie.getExtension().getLimits()
				.get(ch.elexis.core.jpa.entities.TarmedLeistung.EXT_FLD_SERVICE_AGE);
		assertNotNull(ageLimit);
		List<TarmedLeistungAge> tarmedAgeLimits = TarmedLeistungAge.of(ageLimit,
				LocalDateTime.of(2000, 12, 31, 13, 13));
		assertFalse(tarmedAgeLimits.isEmpty());
		assertTrue(tarmedAgeLimits.get(0).getFromDays() < tarmedAgeLimits.get(0).getToDays());

		// exclusion
		TarmedLeistung echoKardiografie = TarmedLeistung.getFromCode(codeEchokardiografie, LocalDate.now(), null);
		assertNotNull(echoKardiografie);
		assertEquals(codeEchokardiografie, echoKardiografie.getCode());

		LocalDate time = LocalDate.of(2000, 12, 31);
		String exclusions = echoKardiografie.getExclusion(time);
		assertEquals("", exclusions);

		time = LocalDate.of(2006, 1, 1);
		exclusions = echoKardiografie.getExclusion(time);
		assertNotNull(exclusions);

		assertTrue(exclusions.contains(codeEchokardiografieIncompatible));
		assertTrue(exclusions.contains(codeEchokardiografieExpired1));
		assertTrue(exclusions.contains(codeEchokardiografieExpired2));

		String validExclusion = echoKardiografie.getExclusion();
		assertEquals(codeEchokardiografieIncompatible, validExclusion);

		LocalDate now = LocalDate.now();

		// hierarchy
		TarmedLeistung hierarchyMaster = TarmedLeistung.getFromCode(codeHierarchyMaster, LocalDate.now(), null);
		List<String> slaves = hierarchyMaster.getHierarchy(now);
		assertNotNull(slaves);
		assertTrue(slaves.contains(codeHierarchySlave));
		TarmedLeistung hierarchyNotMaster = TarmedLeistung.getFromCode(codeHierarchyNotMaster, LocalDate.now(), null);
		slaves = hierarchyNotMaster.getHierarchy(now);
		assertNotNull(slaves);
		assertFalse(slaves.contains(codeHierarchySlave));

		// groups
		TarmedLeistung inGroup = TarmedLeistung.getFromCode(codeInGroup, LocalDate.now(), null);
		List<String> groups = inGroup.getServiceGroups(now);
		assertNotNull(groups);
		assertTrue(groups.contains(codeGroup));
		TarmedLeistung notInGroup = TarmedLeistung.getFromCode(codeNotInGroup, LocalDate.now(), null);
		groups = notInGroup.getServiceGroups(now);
		assertFalse(groups.contains(codeGroup));

		// group limits
		TarmedLeistung limitInGroup = TarmedLeistung.getFromCode(codeLimitInGroup, LocalDate.now(), null);
		groups = limitInGroup.getServiceGroups(now);
		assertTrue(groups.contains("31"));
		Optional<ITarmedGroup> tarmedGroup = TarmedGroup.find("31", inGroup.getLaw(), inGroup.getValidFrom());
		assertTrue(tarmedGroup.isPresent());
		String limits = tarmedGroup.get().getExtension().getLimits().get("limits");
		assertTrue(limits != null && !limits.isEmpty());

		// blocks
		TarmedLeistung inBlock = (TarmedLeistung) TarmedLeistung.getFromCode(codeInBlock, LocalDate.now(), null);
		List<String> blocks = inBlock.getServiceBlocks(now);
		assertNotNull(blocks);
		assertTrue(blocks.contains(codeBlock));
		TarmedLeistung notInBlock = (TarmedLeistung) TarmedLeistung.getFromCode(codeNotInBlock, LocalDate.now(), null);
		blocks = notInBlock.getServiceBlocks(now);
		assertFalse(blocks.contains(codeBlock));

		// block kumulation, exclusives
		List<TarmedExclusive> exclusives = TarmedKumulation.getExclusives("01", TarmedKumulationArt.BLOCK,
				LocalDate.now(), null);
		assertFalse(exclusives.isEmpty());
		exclusives.get(0).isMatching((TarmedLeistung) TarmedLeistung.getFromCode("00.1345", LocalDate.now(), null),
				new TimeTool());

		// parents #9212
		Stream<?> found = ArzttarifeModelServiceHolder.get().executeNativeQuery(testParentsSql);
		assertFalse(found.findFirst().isPresent());

		// long text
		TarmedLeistung tl = TarmedLeistung.getFromCode("22.0045", LocalDate.now(), null);
		assertNotNull(tl);
		assertTrue(StringUtils.isNotBlank(tl.getText()));
		tl = TarmedLeistung.getFromCode("22.0035", LocalDate.now(), null);
		assertTrue(StringUtils.isNotBlank(tl.getText()));

		// parent of 09.0010-20010101 has different valid from #23345
		ITarmedLeistung tl2001 = ArzttarifeModelServiceHolder.get().load("09.0010-20010101", ITarmedLeistung.class)
				.get();
		assertNotNull(tl2001.getParent());
		ITarmedLeistung tl2001Parent = ArzttarifeModelServiceHolder.get()
				.load("09.01.01-20080101", ITarmedLeistung.class).get();
		assertTrue(tl2001Parent.isChapter());
		assertEquals(tl2001.getParent(), tl2001Parent);
	}

	private static String testParentsSql = "SELECT * FROM TARMED WHERE PARENT NOT IN (SELECT ID FROM TARMED) AND PARENT NOT LIKE 'NIL'";

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
