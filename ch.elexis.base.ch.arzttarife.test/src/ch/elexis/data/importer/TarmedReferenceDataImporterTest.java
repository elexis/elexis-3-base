package ch.elexis.data.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.data.PersistentObject;
import ch.elexis.data.TarmedExclusive;
import ch.elexis.data.TarmedGroup;
import ch.elexis.data.TarmedKumulation;
import ch.elexis.data.TarmedKumulation.TarmedKumulationType;
import ch.elexis.data.TarmedLeistung;
import ch.rgw.tools.JdbcLink;
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
	public void performImport() throws FileNotFoundException, SQLException{
		File tarmedFile =
			new File(System.getProperty("user.dir") + File.separator + "rsc" + File.separator
				+ "tarmed.mdb");
		InputStream tarmedInStream = new FileInputStream(tarmedFile);
		
		TarmedReferenceDataImporter importer = new TarmedReferenceDataImporter();
		importer.suppressRestartDialog();
		Status retStatus =
			(Status) importer.performImport(new NullProgressMonitor(), tarmedInStream, null);
		assertEquals(IStatus.OK, retStatus.getCode());
		
		JdbcLink cacheDb = new JdbcLink("org.h2.Driver", "jdbc:h2:mem:tarmed_import", "hsql");
		cacheDb.connect("", "");
		ResultSet res =
			cacheDb.getStatement().query(
				String.format("SELECT * FROM %sLEISTUNG", "TARMED_IMPORT_"));
		
		List<String> codes = new ArrayList<String>();
		while (res != null && res.next()) {
			String code = res.getString("LNR");
			codes.add(code);
			
			String knrCode = res.getString("KNR");
			codes.add(knrCode);
		}
		
		assertTrue(codes.contains(codeAerztlGutachten));
		assertTrue(codes.contains(codeGutachtenKatA));
		assertTrue(codes.contains(codeTapingKat1));
		assertTrue(codes.contains(codeBesuchErste5Min));
	}
	
	@Test
	public void performImportAndCheck() throws FileNotFoundException, SQLException{
		File tarmedFile =
			new File(System.getProperty("user.dir") + File.separator + "rsc" + File.separator
				+ "tarmed.mdb");
		InputStream tarmedInStream = new FileInputStream(tarmedFile);
		
		TarmedReferenceDataImporter importer = new TarmedReferenceDataImporter();
		importer.suppressRestartDialog();
		Status retStatus =
			(Status) importer.performImport(new NullProgressMonitor(), tarmedInStream, null);
		assertEquals(IStatus.OK, retStatus.getCode());
		
		// exclusion
		TarmedLeistung echoKardiografie =
			(TarmedLeistung) TarmedLeistung.getFromCode(codeEchokardiografie, new TimeTool(), null);
		assertTrue(echoKardiografie.exists());
		assertEquals(codeEchokardiografie, echoKardiografie.getCode());
		
		TimeTool time = new TimeTool("31.12.2000");
		String exclusions = echoKardiografie.getExclusion(time);
		assertEquals("", exclusions);
		
		time.set("01.01.2006");
		exclusions = echoKardiografie.getExclusion(time);
		assertNotNull(exclusions);
		
		assertTrue(exclusions.contains(codeEchokardiografieIncompatible));
		assertTrue(exclusions.contains(codeEchokardiografieExpired1));
		assertTrue(exclusions.contains(codeEchokardiografieExpired2));
		
		String validExclusion = echoKardiografie.getExclusion();
		assertEquals(codeEchokardiografieIncompatible, validExclusion);
		
		TimeTool now = new TimeTool();
		
		// hierarchy
		TarmedLeistung hierarchyMaster =
			(TarmedLeistung) TarmedLeistung.getFromCode(codeHierarchyMaster, new TimeTool(), null);
		List<String> slaves = hierarchyMaster.getHierarchy(now);
		assertNotNull(slaves);
		assertTrue(slaves.contains(codeHierarchySlave));
		TarmedLeistung hierarchyNotMaster =
			(TarmedLeistung) TarmedLeistung.getFromCode(codeHierarchyNotMaster, new TimeTool(),
				null);
		slaves = hierarchyNotMaster.getHierarchy(now);
		assertNotNull(slaves);
		assertFalse(slaves.contains(codeHierarchySlave));
		
		// groups
		TarmedLeistung inGroup =
			(TarmedLeistung) TarmedLeistung.getFromCode(codeInGroup, new TimeTool(), null);
		List<String> groups = inGroup.getServiceGroups(now);
		assertNotNull(groups);
		assertTrue(groups.contains(codeGroup));
		TarmedLeistung notInGroup =
			(TarmedLeistung) TarmedLeistung.getFromCode(codeNotInGroup, new TimeTool(), null);
		groups = notInGroup.getServiceGroups(now);
		assertFalse(groups.contains(codeGroup));
		
		// group limits
		TarmedLeistung limitInGroup =
			(TarmedLeistung) TarmedLeistung.getFromCode(codeLimitInGroup, new TimeTool(), null);
		groups = limitInGroup.getServiceGroups(now);
		assertTrue(groups.contains("31"));
		Optional<TarmedGroup> tarmedGroup =
			TarmedGroup.find("31", inGroup.get(TarmedLeistung.FLD_LAW), inGroup.getGueltigVon());
		assertTrue(tarmedGroup.isPresent());
		String limits = tarmedGroup.get().loadExtension().get("limits");
		assertTrue(limits != null && !limits.isEmpty());
		
		// blocks
		TarmedLeistung inBlock =
			(TarmedLeistung) TarmedLeistung.getFromCode(codeInBlock, new TimeTool(), null);
		List<String> blocks = inBlock.getServiceBlocks(now);
		assertNotNull(blocks);
		assertTrue(blocks.contains(codeBlock));
		TarmedLeistung notInBlock =
			(TarmedLeistung) TarmedLeistung.getFromCode(codeNotInBlock, new TimeTool(), null);
		blocks = notInBlock.getServiceBlocks(now);
		assertFalse(blocks.contains(codeBlock));
		
		// block kumulation, exclusives
		List<TarmedExclusive> exclusives =
			TarmedKumulation.getExclusives("01", TarmedKumulationType.BLOCK, new TimeTool(), null);
		assertFalse(exclusives.isEmpty());
		exclusives.get(0).isMatching(
			(TarmedLeistung) TarmedLeistung.getFromCode("00.1345", new TimeTool(), null),
			new TimeTool());
		
		// parents #9212
		PreparedStatement stm = PersistentObject.getDefaultConnection().getPreparedStatement(testParentsSql);
		try {
			ResultSet result = stm.executeQuery();
			assertFalse(result.next());
		} catch (SQLException se) {
			throw se;
		} finally {
			if (stm != null) {
				PersistentObject.getDefaultConnection().releasePreparedStatement(stm);
			}
		}
	}
	
	private static String testParentsSql =
		"SELECT * FROM TARMED WHERE PARENT NOT IN (SELECT ID FROM TARMED) AND PARENT NOT LIKE 'NIL'";
	
	@Test
	public void currentVersion(){
		TarmedLeistung.setVersion("1");
		assertEquals(1, TarmedLeistung.getCurrentVersion());
		assertEquals(1, TarmedLeistung.getCurrentVersion(""));
		assertEquals(1, TarmedLeistung.getCurrentVersion(null));
		
		TarmedLeistung.setVersion("2", "kvg");
		assertEquals(2, TarmedLeistung.getCurrentVersion("kvg"));
		assertEquals(1, TarmedLeistung.getCurrentVersion(""));
		
		TarmedLeistung.setVersion("3", "uvg");
		assertEquals(3, TarmedLeistung.getCurrentVersion("uvg"));
		assertEquals(2, TarmedLeistung.getCurrentVersion("kvg"));
		assertEquals(1, TarmedLeistung.getCurrentVersion(""));
		
		TarmedLeistung.setVersion("3", "kvg");
		assertEquals(3, TarmedLeistung.getCurrentVersion("uvg"));
		assertEquals(3, TarmedLeistung.getCurrentVersion("kvg"));
		assertEquals(1, TarmedLeistung.getCurrentVersion(""));
	}
}
