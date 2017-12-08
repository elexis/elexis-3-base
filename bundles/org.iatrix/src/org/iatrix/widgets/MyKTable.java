/*******************************************************************************
 * Copyright (c) 2007-2015, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     Gerry Weirich - adapted for 2.1
 *     Niklaus Giger - small improvements, split into 20 classes
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.widgets;

import org.eclipse.swt.widgets.Composite;

import de.kupzog.ktable.KTable;

/*
 * Extension of KTable KTable doesn't update the scrollbar visibility if the model changes. We
 * would require to call setModel(). As a work-around, we implement refresh(), which calls
 * updateScrollbarVisibility() before redraw().
 */
public class MyKTable extends KTable {
	public MyKTable(Composite parent, int style){
		super(parent, style);
	}

	public void refresh(){
		updateScrollbarVisibility();
		redraw();
	}
}
