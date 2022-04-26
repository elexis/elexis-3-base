package ch.elexis.ebanking.qr.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.elexis.ebanking.qr.QRBillDataBuilderTest;
import ch.elexis.ebanking.qr.QRBillImageTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ QRBillDataBuilderTest.class, QRBillImageTest.class })
public class AllTests {

}
