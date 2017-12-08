/*******************************************************************************
 * Copyright (c) 2010, G. Weirich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *
 *    
 *******************************************************************************/
package elexis_db_shaker.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import ch.elexis.core.data.util.PlatformHelper;
import ch.elexis.core.ui.util.SWTHelper;

public class Namen {
	
	List<String> vornamen;
	List<String> nachnamen;
	
	public Namen(){
		try {
			vornamen = new LinkedList<String>();
			String base = PlatformHelper.getBasePath("ch.elexis.support.dbshaker");
			File rsc = new File(base, "rsc");
			File fFirstnames = new File(rsc, "vornamen.txt");
			Reader infilevn = new FileReader(fFirstnames);
			BufferedReader vn = new BufferedReader(infilevn);
			String line;
			while ((line = vn.readLine()) != null) {
				vornamen.add(line);
			}
			vn.close();
			
			nachnamen = new LinkedList<String>();
			File fLastNames = new File(rsc, "nachnamen.txt");
			Reader infilenn = new FileReader(fLastNames);
			BufferedReader nn = new BufferedReader(infilenn);
			while ((line = nn.readLine()) != null) {
				nachnamen.add(line);
			}
			nn.close();
			
		} catch (FileNotFoundException e) {
			SWTHelper.alert("File not found", e.getMessage());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getRandomVorname(){
		return vornamen.get((int) Math.round(Math.random() * (vornamen.size() - 1))).trim();
	}
	
	public String getRandomNachname(){
		return nachnamen.get((int) Math.round(Math.random() * (nachnamen.size() - 1))).trim();
	}
}
