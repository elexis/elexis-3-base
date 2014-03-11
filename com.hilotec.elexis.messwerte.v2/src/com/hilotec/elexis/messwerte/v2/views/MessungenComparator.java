/*******************************************************************************
 * 
 * The authorship of this code and the accompanying materials is held by 
 * medshare GmbH, Switzerland. All rights reserved. 
 * http://medshare.net
 * 
 * This code and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0
 * 
 * Year of publication: 2012
 * 
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import ch.rgw.tools.TimeTool;

import com.hilotec.elexis.messwerte.v2.data.Messung;

public class MessungenComparator extends ViewerComparator {
	
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;
	
	public MessungenComparator(){
		this.propertyIndex = 0;
		direction = DESCENDING;
	}
	
	public int getDirection(){
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}
	
	public void setColumn(int column){
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2){
		Messung m1 = (Messung) e1;
		Messung m2 = (Messung) e2;
		int rc = 0;
		switch (propertyIndex) {
		case 0:
			TimeTool t1 = new TimeTool(m1.getDatum());
			TimeTool t2 = new TimeTool(m2.getDatum());
			rc = t1.compareTo(t2);
			break;
		default:
			rc = 0;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}
	
}
