/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
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
