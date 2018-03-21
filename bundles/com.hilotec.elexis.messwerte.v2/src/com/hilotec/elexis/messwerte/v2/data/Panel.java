/*******************************************************************************
 * Copyright (c) 2011, and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    P. Chaubert - adapted to Messwerte V2
 *    medshare GmbH - adapted to Messwerte V2.1 in February 2012
 *    
 *******************************************************************************/
package com.hilotec.elexis.messwerte.v2.data;

public class Panel {
	private final String type;
	private Panel[] panels;
	private String[] fields;
	private String[] attributes;
	
	public Panel[] getPanels(){
		return panels;
	}
	
	public void setPanels(Panel[] panels){
		this.panels = panels;
	}
	
	public String[] getFields(){
		return fields;
	}
	
	public void setFields(String[] fields){
		this.fields = fields;
	}
	
	public String[] getAttributes(){
		return attributes;
	}
	
	public void setAttributes(String[] attributes){
		this.attributes = attributes;
	}
	
	public Panel(String type){
		this.type = type;
	}
	
	public String getAttribute(String name){
		for (String a : attributes) {
			if (a.startsWith(name + "=")) { //$NON-NLS-1$
				return a.substring(name.length() + 1);
			}
		}
		return null;
	}
	
	public String getType(){
		return type;
	}
	
}
