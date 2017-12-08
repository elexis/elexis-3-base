/*******************************************************************************
 * Copyright (c) 2006-2017, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    T. Huster - copied from ch.elexis.base.ch.artikel
 *    
 *******************************************************************************/
package ch.elexis.base.ch.migel.ui;

import ch.elexis.artikel_ch.data.MiGelArtikel;
import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.data.Query;

public class MiGelLoader extends FlatDataLoader {
	public MiGelLoader(CommonViewer cv){
		super(cv, new Query<MiGelArtikel>(MiGelArtikel.class));
		setOrderFields(MiGelArtikel.FLD_NAME);
	}
}
