/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.ui.dialog;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.databinding.conversion.IConverter;


public class DateTimeTargetToModelUVS implements IConverter {
	
	@Override
	public Object getFromType(){
		return Date.class;
	}
	
	@Override
	public Object getToType(){
		return String.class;
	}
	
	@Override
	public Object convert(Object fromObject){
		Date dt = (Date) fromObject;
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		return sdf.format(dt);
	}
	
}
