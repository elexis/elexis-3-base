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
