package ch.pharmed.phmprescriber;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.data.Artikel;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.elexis.data.Rezept;



public class Test_PhMPrescriber {


	@Before
	public void setUp() throws Exception{}

	@After
	public void teardown() throws Exception{
		PlatformUI.getWorkbench().saveAllEditors(false); // do not confirm saving
		PlatformUI.getWorkbench().saveAll(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), PlatformUI.getWorkbench().getActiveWorkbenchWindow(), null, false);
		if (PlatformUI.getWorkbench() != null) // null if run from Eclipse-IDE
		{
			// needed if run as surefire test from using mvn install
			try {
				
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllPerspectives(false, true);
			} catch (Exception e) {
				
				System.out.println(e.getMessage());
			}
			
			
		}
	}

	@Test
	public void test(){

		System.out.println("JUnit testing PhMPrescriber-Plugin");
		
		//Create the test prescription
		Rezept rp = createTestPrescription();
		
		Physician ph = null;
		
		ph = getPhysicianInformationShouldReturnPhysicianAttributes();
		System.out.println("Physician testing done!");
		postPrescriptionShouldReturnID(rp,ph);
		System.out.println("Prescription sent!");
		getInteractionsShouldReturnInteractions(rp);
		System.out.println("Interactions checked!");
				
		System.out.println("Tests done!");
	}

	
	private void postPrescriptionShouldReturnID(Rezept rp, Physician ph) {
		// TODO Auto-generated method stub
		Sender sender = new Sender(rp, ph);
		
		assertEquals(true, sender.postPrescription());
		
	}

	private static Rezept createTestPrescription() {
		
		//Patient
		Patient pat = new Patient("Keller", "Max", "12.10.1969", "m");
						
		//Prescription
		Rezept rp = new Rezept(pat);
				
				
		//Articles
		Artikel art1 = Artikel.load("article1");
				
		art1.setATC_code("N02BA01");
		art1.setEAN("7680085370118");
		art1.setName("ASPIRIN Tabl 500mg Erw 20 Stk");
				
		Artikel art2 = Artikel.load("article2");
		art2.setATC_code("M01AB05");
		art2.setEAN("7680378670475");
		art2.setName("VOLTAREN Drag 50mg 20 Stk");

				
		Prescription p1 = new Prescription(art1, pat, StringConstants.EMPTY,
					StringConstants.EMPTY);
		Prescription p2 = new Prescription(art2, pat, StringConstants.EMPTY,
				StringConstants.EMPTY);
							
		rp.addPrescription(p1);
		rp.addPrescription(p2);	
		
	
		return rp;
				
		
	}
	
	private static Physician getPhysicianInformationShouldReturnPhysicianAttributes() {

		Physician phys = new Physician();
		
		//Check the WS for getting the physicians attributes
		phys.getAttributesFromWeb("U 0387.13ABC");
		assertEquals("", phys.getFirstname());
		assertEquals("", phys.getGlnid());
		
		phys.getAttributesFromWeb("U038713");
		assertEquals("U038713", phys.getZsrid());
		assertEquals("Strub", phys.getLastname());
		
			
		phys.getAttributesFromWeb("U 0387.13");
		assertEquals("Martin", phys.getFirstname());
		assertEquals("U 0387.13", phys.getZsrid());
		assertEquals("7601000490087", phys.getGlnid());
			
				
		//Check WS for checking available shops
		ConfigServiceHolder.setGlobal(Constants.CFG_PHM_LASTREQUEST,"");
				
		assertEquals(false, phys.hasShops());
		
		ConfigServiceHolder.setGlobal(Constants.CFG_PHM_LASTREQUEST,"");
				
		phys.setZsrid("W 0192.59");
		assertEquals(true, phys.hasShops());
		
		return phys;
		
	}

	
	private static void getInteractionsShouldReturnInteractions(Rezept rp) {

		Interaction IA = new Interaction();
					
		List<String> interactions = IA.checkPrescription(rp);
					
		assertEquals(2,interactions.size());
		
		rp.removePrescription(rp.getLines().get(0));
		
		assertEquals(null,IA.checkPrescription(rp));
		
	}
}
