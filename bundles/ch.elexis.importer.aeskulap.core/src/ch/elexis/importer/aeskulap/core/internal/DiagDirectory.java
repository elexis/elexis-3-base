package ch.elexis.importer.aeskulap.core.internal;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.LoggerFactory;

import ch.elexis.data.Patient;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;

public class DiagDirectory implements IAeskulapImportFile {
	
	private Map<String, File> diagMap;
	
	private File file;
	
	public DiagDirectory(){
		diagMap = new HashMap<>();
	}
	
	@Override
	public File getFile(){
		return file;
	}
	
	@Override
	public Type getType(){
		return Type.DIAGDIRECTORY;
	}
	
	@Override
	public boolean doImport(Map<Type, IAeskulapImportFile> transientFiles, boolean overwrite,
		SubMonitor monitor){
		monitor.beginTask("Aeskuplap Diagnosen Import", diagMap.size());
		for (String key : diagMap.keySet()) {
			File file = diagMap.get(key);
			if (file != null && file.isFile() && file.exists()) {
				readFile(file, Charset.forName("UTF-8")).ifPresent(content -> {
					Patient patient =
						(Patient) getWithXid(IAeskulapImporter.XID_IMPORT_PATIENT, key);
					if (patient != null) {
						patient.setDiagnosen(content);
					}
				});
			}
			monitor.worked(1);
		}
		monitor.done();
		return true;
	}
	
	private Optional<String> readFile(File file, Charset encoding){
		try {
			byte[] encoded = Files.readAllBytes(file.toPath());
			if (encoded != null && encoded.length > 0) {
				return Optional.of(new String(encoded, encoding));
			}
		} catch (IOException e) {
			
		}
		return Optional.empty();
	}
	
	@Override
	public boolean isTransient(){
		return false;
	}
	
	@Override
	public Object getTransient(String filename){
		return diagMap.get(filename);
	}
	
	public void add(File directory){
		file = directory;
		File[] diagFiles = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name){
				return name.matches("[0-9]+.txt");
			}
		});
		Arrays.asList(diagFiles).stream().forEach(f -> {
			if (f != null) {
				File existing = diagMap.put(FilenameUtils.getBaseName(f.getName()), f);
				if (existing != null) {
					LoggerFactory.getLogger(getClass()).warn("Duplicate File ["
						+ existing.getAbsolutePath() + "] [" + f.getAbsolutePath() + "]");
				}
			}
		});
	}
}
