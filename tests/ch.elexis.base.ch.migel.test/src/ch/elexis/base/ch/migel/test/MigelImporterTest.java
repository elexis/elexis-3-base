package ch.elexis.base.ch.migel.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.migel.MigelCsvDataImporter;
import ch.elexis.base.ch.migel.MigelXlsxDataImporter;
import ch.elexis.base.ch.migel.ui.MiGelImporter;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.ArticleTyp;
import ch.rgw.tools.Money;

public class MigelImporterTest {
	private static Logger log = LoggerFactory.getLogger(MiGelImporter.class);

	private static String Example_id = "01.01.03.00.1";
	private static String Example_German = "Milchpumpen - Doppelmilchpumpe, elektrisch, inkl. Zubehörset, Kauf";
	private static String Example_French = "Tire-lait - Tire-lait double, électrique, set d’accessoires incl., achat";
	private static String Example_Italian = "Pompe tiralatte - Pompa tiralatte elettrica doppia, set d’accessori incluso, acquisto";
	private static Money Example_Price = new Money(340.0);
	private static String Unit_German = "Stück";
	private static String Unit_French = "pièce";
	private static String Unit_Italian = "pezzo";
	private static String Example_Size = "1";

	private void checkIdAndName(String id, String localizedName, String localizedUnit) {
		IQuery<IArticle> query = CoreModelServiceHolder.get().getQuery(IArticle.class, true);
		query.and(ModelPackage.Literals.IARTICLE__TYP, COMPARATOR.EQUALS, ArticleTyp.MIGEL);
		List<IArticle> existing = query.execute();
		existing.forEach(a -> {
			if (a.getCode().contains(id)) {
				String nameRead = a.getName();
				Money price = a.getSellingPrice();
				String full = a.getExtInfo("FullText").toString();
				log.info("price {} found {} ?\n{}\n{}", price, localizedName.contentEquals(a.getName()), localizedName,
						nameRead);
				assertTrue(localizedName.contentEquals(nameRead));
				assertEquals(Example_Price, a.getSellingPrice());
				assertEquals(localizedUnit, a.getPackageUnit());
				assertEquals(Example_Size, String.format("%d", a.getPackageSize()));
				assertTrue(full.contains(localizedName));
			}
		});
	}

	@Test
	public void performImport_CSV_German() throws FileNotFoundException, SQLException {
		MigelCsvDataImporter importer = new MigelCsvDataImporter();
		assertNotNull(importer);
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				MigelImporterTest.class.getResourceAsStream("/rsc/MiGel_2022v2.csv"), null);
		assertEquals(IStatus.OK, retStatus.getCode());
		checkIdAndName(Example_id, Example_German, Unit_German);
	}

	@Test
	public void performImport_XLSX_German() throws FileNotFoundException, SQLException {
		ch.elexis.core.data.activator.CoreHub.localCfg.set(Preferences.ABL_LANGUAGE, "d");
		MigelXlsxDataImporter importer = new MigelXlsxDataImporter();
		assertNotNull(importer);
		String resName = "/rsc/migel-gesamtliste-per010722.xlsx";
		log.info("Reading from {}", resName);
		InputStream input = MigelImporterTest.class.getResourceAsStream("/rsc/migel-gesamtliste-per010722.xlsx");
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(), input, null);
		assertEquals(IStatus.OK, retStatus.getCode());
		checkIdAndName(Example_id, Example_German, Unit_German);
	}

	@Test
	public void performImport_XLSX_French() throws FileNotFoundException, SQLException {
		ch.elexis.core.data.activator.CoreHub.localCfg.set(Preferences.ABL_LANGUAGE, "f");
		MigelXlsxDataImporter importer = new MigelXlsxDataImporter();
		assertNotNull(importer);
		String resName = "/rsc/migel-gesamtliste-per010722.xlsx";
		log.info("Reading from {}", resName);
		InputStream input = MigelImporterTest.class.getResourceAsStream("/rsc/migel-gesamtliste-per010722.xlsx");
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(), input, null);
		assertEquals(IStatus.OK, retStatus.getCode());
		checkIdAndName(Example_id, Example_French, Unit_French);
	}

	@Test
	public void performImport_XLSX_Italian() throws FileNotFoundException, SQLException {
		ch.elexis.core.data.activator.CoreHub.localCfg.set(Preferences.ABL_LANGUAGE, "i");
		MigelXlsxDataImporter importer = new MigelXlsxDataImporter();
		assertNotNull(importer);
		String resName = "/rsc/migel-gesamtliste-per010722.xlsx";
		log.info("Reading from {}", resName);
		InputStream input = MigelImporterTest.class.getResourceAsStream("/rsc/migel-gesamtliste-per010722.xlsx");
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(), input, null);
		assertEquals(IStatus.OK, retStatus.getCode());
		checkIdAndName(Example_id, Example_Italian, Unit_Italian);
	}
}
