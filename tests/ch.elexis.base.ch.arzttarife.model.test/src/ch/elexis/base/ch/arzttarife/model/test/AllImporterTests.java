package ch.elexis.base.ch.arzttarife.model.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.base.ch.arzttarife.tardoc.model.importer.TardocReferenceDataImporterTest;

@RunWith(Suite.class)
//@SuiteClasses({ OccupationalImporterTest.class, PsychoImporterTest.class, NutritionImporterTest.class,
//		PhysioImporterTest.class, TarmedAllowanceImporterTest.class, TarmedReferenceDataImporterTest.class,
//		ComplementaryImporterTest.class, PandemieImporterTest.class })
@SuiteClasses({ TardocReferenceDataImporterTest.class })
public class AllImporterTests {

}
