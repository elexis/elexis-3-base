/*******************************************************************************
 * Copyright (c) 2009-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.base.ch.artikel.model;

import ch.elexis.artikel_ch.data.Medikament;
import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.data.Query;

public class MedikamentLoader extends FlatDataLoader {
	
	public MedikamentLoader(CommonViewer cv){
		super(cv, new Query<Medikament>(Medikament.class));
		setOrderFields(Medikament.FLD_NAME);
	}
}
