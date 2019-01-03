package ch.elexis.importer.aeskulap.core.internal;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.LoggerFactory;

import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;

public class LetterDirectories implements IAeskulapImportFile {
	
	private Map<String, File> letterMap;
	
	public LetterDirectories(){
		letterMap = new HashMap<>();
	}
	
	@Override
	public File getFile(){
		return null;
	}
	
	@Override
	public Type getType(){
		return Type.LETTERDIRECTORY;
	}
	
	@Override
	public boolean doImport(Map<Type, IAeskulapImportFile> transientFiles, boolean overwrite,
		SubMonitor monitor){
		// this is a transient mapping for the letter import, does not import anything here
		return true;
	}
	
	@Override
	public boolean isTransient(){
		return true;
	}
	
	@Override
	public Object getTransient(String filename){
		return letterMap.get(filename);
	}
	
	public void add(File directory){
		File[] docFiles = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name){
				return name.startsWith("!") && name.contains("_");
			}
		});
		Arrays.asList(docFiles).stream().forEach(f -> {
			if (f != null) {
				File existing = letterMap.put(FilenameUtils.getBaseName(f.getName()), f);
				if (existing != null) {
					LoggerFactory.getLogger(getClass()).warn("Duplicate File ["
						+ existing.getAbsolutePath() + "] [" + f.getAbsolutePath() + "]");
				}
			}
		});
	}
}
