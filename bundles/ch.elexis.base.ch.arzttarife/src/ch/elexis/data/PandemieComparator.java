/*******************************************************************************
 * Copyright (c) 2009-2020, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.data;

import java.util.Comparator;

public class PandemieComparator implements Comparator<PandemieLeistung> {
	
	public int compare(PandemieLeistung pl0, PandemieLeistung pl1){
		return pl0.getCode().compareTo(pl1.getCode());
	}
	
}
