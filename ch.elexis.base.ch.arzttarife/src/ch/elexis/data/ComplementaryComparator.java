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
package ch.elexis.data;

import java.util.Comparator;

public class ComplementaryComparator implements Comparator<ComplementaryLeistung> {
	
	public int compare(ComplementaryLeistung left, ComplementaryLeistung right){
		
		int chapterComp = left.get(ComplementaryLeistung.FLD_CHAPTER)
			.compareTo(right.get(ComplementaryLeistung.FLD_CHAPTER));
		if (chapterComp == 0) {
			return left.getCode().compareTo(right.getCode());
		}
		return chapterComp;
	}
	
}
