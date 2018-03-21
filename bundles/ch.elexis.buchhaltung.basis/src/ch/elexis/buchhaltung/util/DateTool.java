/*******************************************************************************
 * Copyright (c) 2006-2010, Gerry Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gerry Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.buchhaltung.util;

import java.util.Calendar;

import ch.rgw.tools.TimeTool;

public class DateTool extends TimeTool {
	
	public DateTool(){
		super();
	}
	
	public DateTool(TimeTool other){
		super(other);
	}
	
	public DateTool(String other){
		super(other);
	}
	
	@Override
	public String toString(){
		return toString(TimeTool.DATE_SIMPLE);
	}
	
	@Override
	public int compareTo(Calendar c){
		long diff = (getTimeInMillis() - c.getTimeInMillis()) / 86400000L; // consider only
																			// day-differences
		return (int) diff;
	}
	
}
