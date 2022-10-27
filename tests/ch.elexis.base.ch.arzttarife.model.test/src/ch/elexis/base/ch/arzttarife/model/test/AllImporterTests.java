package ch.elexis.base.ch.arzttarife.model.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.base.ch.arzttarife.complementary.model.importer.ComplementaryImporterTest;
import ch.elexis.base.ch.arzttarife.nutrition.model.importer.NutritionImporterTest;
import ch.elexis.base.ch.arzttarife.pandemie.model.importer.PandemieImporterTest;
import ch.elexis.base.ch.arzttarife.physio.model.importer.PhysioImporterTest;
import ch.elexis.base.ch.arzttarife.psycho.model.importer.PsychoImporterTest;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.TarmedReferenceDataImporterTest;
import ch.elexis.base.ch.arzttarife.tarmedallowance.model.importer.TarmedAllowanceImporterTest;

@RunWith(Suite.class)
@SuiteClasses({ PsychoImporterTest.class, NutritionImporterTest.class, PhysioImporterTest.class,
		TarmedAllowanceImporterTest.class, TarmedReferenceDataImporterTest.class, ComplementaryImporterTest.class,
		PandemieImporterTest.class })
public class AllImporterTests {

}
