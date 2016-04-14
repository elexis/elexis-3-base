/*******************************************************************************
 * Copyright (c) 2006-2008, G. Weirich and Elexis
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

import static ch.elexis.base.ch.ticode.TessinerCode.ticode;

import java.util.Hashtable;
import java.util.List;

import ch.elexis.base.ch.ticode.TessinerCode;
import ch.elexis.core.data.interfaces.IDiagnose;
/**
 * Die Tessinercodes werden nicht in der Datenbank vorgehalten, sondern sind aus Effizientgründen
 * hier in dieser Klasse fest verdrahtet. Die Klasse ist trotzdem im data-Package, um die
 * Programmierkonsistenz zu wahren.
 * 
 * @author Gerry
 * 
 */
public class TICode extends PersistentObject implements IDiagnose {
	private static Hashtable<String, TICode> hash = new Hashtable<String, TICode>();
	private String Text;
	
	TICode(String Code, String Text){
		super(Code);
		this.Text = Text;
	}
	
	public String getText(){
		return Text;
	}
	
	public String getCode(){
		return getId();
	}
	
	public String getLabel(){
		return getId() + " " + Text; //$NON-NLS-1$
	}
	
	public String getCodeSystemName(){
		return TessinerCode.CODESYSTEM_NAME;
	}
	
	public static TICode load(String code){
		return getFromCode(code);
	}
	
	public static TICode[] getRootNodes(){
		TICode[] ret = new TICode[ticode.length];
		int i;
		for (i = 0; i < ticode.length; i++) {
			String[] line = ticode[i];
			ret[i] = new TICode(line[0], line[1]);
		}
		return ret;
	}
	
	public static TICode getFromCode(String code){
		TICode ret = hash.get(code);
		if (ret == null && !code.isEmpty()) {
			String chapter = code.substring(0, 1);
			int subch = 0;
			if (code.length() == 2) {
				subch = Integer.parseInt(code.substring(1));
			}
			for (int i = 0; i < ticode.length; i++) {
				if (ticode[i][0].startsWith(chapter)) {
					if (subch == 9) {
						subch = ticode[i].length - 2;
						ret = new TICode(chapter + "9", ticode[i][subch + 1]); //$NON-NLS-1$
					} else {
						ret = new TICode(chapter + subch, ticode[i][subch + 1]);
					}
					hash.put(code, ret);
					return ret;
				}
			}
		}
		return ret;
	}
	
	public TICode getParent(){
		if (getId().length() == 1) {
			return null;
		}
		return getFromCode(getId().substring(0, 1));
	}
	
	public boolean hasChildren(){
		if (getId().length() == 1) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean exists(){
		return true;
	}
	
	public TICode[] getChildren(){
		if (getId().length() > 1) {
			return null;
		}
		String chapter = getId().substring(0, 1);
		for (int i = 0; i < ticode.length; i++) {
			if (ticode[i][0].equals(chapter)) {
				TICode[] ret = new TICode[ticode[i].length - 2];
				for (int j = 2; j < ticode[i].length; j++) {
					String x;
					if (j == ticode[i].length - 1) {
						x = "9"; //$NON-NLS-1$
					} else {
						x = Integer.toString(j - 1);
					}
					ret[j - 2] = new TICode(chapter + x, ticode[i][j]);
				}
				return ret;
			}
		}
		return null;
	}
	
	@Override
	protected String getTableName(){
		return "None"; //$NON-NLS-1$
	}
	
	@Override
	public boolean isDragOK(){
		return !hasChildren();
	}
	
	TICode(){}
	
	public String getCodeSystemCode(){
		return "999"; //$NON-NLS-1$
	}
	
	public List<Object> getActions(Object kontext){
		// TODO Auto-generated method stub
		return null;
	}
	
}
