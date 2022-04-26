/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.base.ch.ebanking.esr;

import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

public class ESRLoader extends FlatDataLoader {

	public ESRLoader(CommonViewer cv, Query<? extends PersistentObject> qbe) {
		super(cv, qbe);

	}

}
