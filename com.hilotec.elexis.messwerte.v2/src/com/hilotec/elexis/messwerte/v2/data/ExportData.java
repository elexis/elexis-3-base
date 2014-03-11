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

package com.hilotec.elexis.messwerte.v2.data;

import ch.rgw.tools.TimeTool;

public class ExportData {
	
	private int patientNumberFrom;
	private int patientNumberTo;
	private TimeTool dateFrom;
	private TimeTool dateTo;
	boolean checkDate = false;
	
	public boolean isCheckDate(){
		return checkDate;
	}
	
	public void setCheckDate(boolean checkDate){
		this.checkDate = checkDate;
	}
	
	public int getPatientNumberFrom(){
		return patientNumberFrom;
	}
	
	public void setPatientNumberFrom(int patientNumberMin){
		this.patientNumberFrom = patientNumberMin;
	}
	
	public int getPatientNumberTo(){
		return patientNumberTo;
	}
	
	public void setPatientNumberTo(int patientNumberMax){
		this.patientNumberTo = patientNumberMax;
	}
	
	public TimeTool getDateFrom(){
		return dateFrom;
	}
	
	public void setDateFrom(TimeTool dateFrom){
		this.dateFrom = dateFrom;
	}
	
	public TimeTool getDateTo(){
		return dateTo;
	}
	
	public void setDateTo(TimeTool dateTo){
		this.dateTo = dateTo;
	}
	
}
