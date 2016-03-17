/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.data;


import java.util.ArrayList;
import java.util.List;

/**
 * @author daniel ludin ludin@swissonline.ch
 * 27.06.2015
 * 
 */

public class ValueSingleTimeline {

	private String sDate;
	private double dWeightKg;
	
		
	public ValueSingleTimeline(String sDate, double dWeightKg) {
		super();
		this.sDate = sDate;
		this.dWeightKg = dWeightKg;
	}
	
	public String getDate() {
		return sDate;
	}
	public void setDate(String sDate) {
		this.sDate = sDate;
	}
	public double getWeightKg() {
		return dWeightKg;
	}
	public void setWeightKg(double dWeightKg) {
		this.dWeightKg = dWeightKg;
	}
	
	
	
	public static List<ValueSingleTimeline> getTestdata(){
		
		List<ValueSingleTimeline> result = new ArrayList<ValueSingleTimeline>();
		
		ValueSingleTimeline value = new ValueSingleTimeline("20130125", 66.5);
		result.add(value);
		
		value = new ValueSingleTimeline("20130325", 71.5);
		result.add(value);
		
		value = new ValueSingleTimeline("20130525", 60.0);
		result.add(value);
		
		value = new ValueSingleTimeline("20130712", 72.0);
		result.add(value);
		
		value = new ValueSingleTimeline("20131201", 75.5);
		result.add(value);
		
		value = new ValueSingleTimeline("20140125", 66.0);
		result.add(value);
		
		value = new ValueSingleTimeline("20140225", 77.5);
		result.add(value);
		
		value = new ValueSingleTimeline("20140618", 80.0);
		result.add(value);
		
		value = new ValueSingleTimeline("20141101", 74.0);
		result.add(value);
		
		
	
		
		return result;
		
	}
	public static List<ValueSingleTimeline> getTestdata2(){
		
		List<ValueSingleTimeline> result = new ArrayList<ValueSingleTimeline>();
		
		ValueSingleTimeline value = new ValueSingleTimeline("20130125", 66.5);
		result.add(value);
		
		return result;
		
	}
	
}
