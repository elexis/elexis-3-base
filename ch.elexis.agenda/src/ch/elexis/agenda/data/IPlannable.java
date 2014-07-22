/*******************************************************************************
 * Copyright (c) 2006, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.agenda.data;

/**
 * Ein Plannable ist ein planbarer Zeitraum. Also entweder ein Termin oder eine Sperrzeit oder ein
 * Freiraum
 * 
 * @author Gerry
 * 
 */
public interface IPlannable {
	public String getDay();
	
	public int getStartMinute();
	
	public int getDurationInMinutes();
	
	public String getType();
	
	public String getStatus();
	
	public String getTitle();
	
	public String getText();
	
	public void setStartMinute(int min);
	
	public void setDurationInMinutes(int min);
	
	public boolean isRecurringDate();
	
	public String getReason();
	
}
