package at.medevit.elexis.impflan.model;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import at.medevit.elexis.impfplan.model.DiseaseDefinitionModel;
import at.medevit.elexis.impfplan.model.DiseaseDefinitionModel.DiseaseDefinition;

public class DiseaseDefinitionModelTest {
	
	@Test
	public void testGetDiseaseDefinitions(){
		List<DiseaseDefinition> diseaseDefinitions = DiseaseDefinitionModel.getDiseaseDefinitions();
		Assert.assertTrue(diseaseDefinitions.size()>5);
	}
	
}
