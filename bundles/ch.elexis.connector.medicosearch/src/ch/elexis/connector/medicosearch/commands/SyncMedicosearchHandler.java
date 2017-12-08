package ch.elexis.connector.medicosearch.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.connector.medicosearch.MedicosearchUtil;

public class SyncMedicosearchHandler extends AbstractHandler {
	private static final Logger log = LoggerFactory.getLogger(SyncMedicosearchHandler.class);
	
	private static final String JAVA_HOME = "java.home";
	private static final String BIN = "bin";
	private static final String JAVA = "java";
	private static final String RUN_JAR = "-jar";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		MedicosearchUtil medicosearchUtil = MedicosearchUtil.getInstance();
		
		// execute the jar
		String java = System.getProperty(JAVA_HOME) + File.separator + BIN + File.separator + JAVA;
		ProcessBuilder processCmd =
			new ProcessBuilder(java, RUN_JAR, medicosearchUtil.getMedicosearchJarPath());
		processCmd.directory(medicosearchUtil.getBundleDirectory());
		
		try {
			Process process = processCmd.start();
			InputStream is = process.getInputStream();
			InputStreamReader inStreamReader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(inStreamReader);
			
			log.debug(">>> Synchronize Medicosearch <<<");
			String line;
			while ((line = br.readLine()) != null) {
				log.debug(line);
			}
			log.debug(">>> Medicosearch synchronization finished <<<");
		} catch (IOException e) {
			log.error("Error executing medicosearch jar", e);
		}
		return null;
	}
}
