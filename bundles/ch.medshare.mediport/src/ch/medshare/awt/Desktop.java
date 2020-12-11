package ch.medshare.awt;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.program.Program;

import ch.medshare.util.UtilFile;

public class Desktop {
	
	/**
	 * Opens a directory or file. JDK 1.6: Desktop.getDesktop().open(directory);
	 */
	public static void open(File dirOrFile) throws IOException{
		Program proggie = null;
		if (dirOrFile.isFile()) {
			String ext = UtilFile.getFileExtension(dirOrFile.getName());
			proggie = Program.findProgram(ext);
		}
		if (proggie != null) {
			proggie.execute(dirOrFile.getAbsolutePath());
		} else {
			if (Program.launch(dirOrFile.getAbsolutePath()) == false) {
				Runtime.getRuntime().exec(dirOrFile.getAbsolutePath());
			}
		}
	}
	
}
