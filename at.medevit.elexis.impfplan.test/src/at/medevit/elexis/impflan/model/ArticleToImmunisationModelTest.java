package at.medevit.elexis.impflan.model;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import at.medevit.elexis.impfplan.model.ArticleToImmunisationModel;

public class ArticleToImmunisationModelTest {
	
	@Test
	public void testGetImmunisationForAtcCode(){
		List<String> immunisationForAtcCode = ArticleToImmunisationModel.getImmunisationForAtcCode("J07CA02");
		Assert.assertEquals(4, immunisationForAtcCode.size());
	}
	
}
