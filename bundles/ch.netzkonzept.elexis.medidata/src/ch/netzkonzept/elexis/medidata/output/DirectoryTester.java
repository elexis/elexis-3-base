/*******************************************************************************
 * Copyright (c) 2020-2022,  Fabian Schmid and Olivier Debenath
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Fabian <f.schmid@netzkonzept.ch> - initial implementation
 *    Olivier Debenath <olivier@debenath.ch>
 *
 *******************************************************************************/
package ch.netzkonzept.elexis.medidata.output;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryTester {
	public static boolean testExistence(String path) {
		return testExistence(Paths.get(path));
	}
	public static boolean isDirectory(String path) {
		return isDirectory(Paths.get(path));
	}
	public static boolean testReadAccess(String path) {
		return testReadAccess(Paths.get(path));
	}
	public static boolean testWriteAccess(String path) {
		return testWriteAccess(Paths.get(path));
	}
	public static boolean testExistence(Path path) {
		return Files.exists(path);
	}
	public static boolean isDirectory(Path path) {
		return Files.isDirectory(path);
	}
	public static boolean testReadAccess(Path path) {
		return Files.isReadable(path);
	}
	public static boolean testWriteAccess(Path path) {
		return Files.isWritable(path);
	}
}
