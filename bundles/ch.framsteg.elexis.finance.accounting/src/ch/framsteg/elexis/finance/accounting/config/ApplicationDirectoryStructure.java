/*******************************************************************************
 * Copyright (c) 2020-2022,  Olivier Debenath
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olivier Debenath <olivier@debenath.ch> - initial implementation
 *    
 *******************************************************************************/
package ch.framsteg.elexis.finance.accounting.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ApplicationDirectoryStructure {
			
	public void create(String path) {		
		try {
			Files.createDirectories(Paths.get(path));
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
