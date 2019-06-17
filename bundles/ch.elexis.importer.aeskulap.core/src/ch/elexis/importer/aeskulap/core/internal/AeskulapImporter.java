package ch.elexis.importer.aeskulap.core.internal;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Xid;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile.Type;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;

@Component
public class AeskulapImporter implements IAeskulapImporter {
	
	private Map<Type, IAeskulapImportFile> transientFiles;
	
	private LetterDirectories letterDirectories;
	private DocumentDirectories documentDirectories;
	private FileDirectories fileDirectories;
	private DiagDirectory diagDirectory;
	
	@Override
	public List<IAeskulapImportFile> setImportDirectory(File directory){
		List<IAeskulapImportFile> ret = new ArrayList<>();
		if (directory != null && directory.exists() && directory.isDirectory()) {
			File[] fileOrDirectory = directory.listFiles();
			for (File file : fileOrDirectory) {
				if(file.isDirectory()) {
					if (isLetterDirectory(file)) {
						addLetterDirectory(file);
					} else if (isDocumentDirectory(file)) {
						addDocumentDirectory(file);
					} else if (isFileDirectory(file)) {
						addFileDirectory(file);
					} else if (isDiagDirectory(file)) {
						addDiagDirectory(file);
					} else {
						ret.addAll(getAeskulapFilesFromDirectory(file));
					}
				} else {
					AeskulapFileFactory.getAeskulapFile(file).ifPresent(af -> ret.add(af));
				}
			}
		}
		if (letterDirectories != null) {
			ret.add(letterDirectories);
		}
		if (documentDirectories != null) {
			ret.add(documentDirectories);
		}
		if (fileDirectories != null) {
			ret.add(fileDirectories);
		}
		if (diagDirectory != null) {
			ret.add(diagDirectory);
		}
		return ret;
	}
	
	private void addLetterDirectory(File file){
		if (letterDirectories == null) {
			letterDirectories = new LetterDirectories();
		}
		letterDirectories.add(file);
	}
	
	private void addDocumentDirectory(File file){
		if (documentDirectories == null) {
			documentDirectories = new DocumentDirectories();
		}
		documentDirectories.add(file);
	}
	
	private void addFileDirectory(File file){
		if (fileDirectories == null) {
			fileDirectories = new FileDirectories();
		}
		fileDirectories.add(file);
	}
	
	private void addDiagDirectory(File file){
		if (diagDirectory == null) {
			diagDirectory = new DiagDirectory();
		}
		diagDirectory.add(file);
	}
	
	private boolean isLetterDirectory(File directory){
		if (directory.isDirectory()) {
			File[] docFiles = directory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name){
					return name.startsWith("!") && name.contains("_");
				}
			});
			return docFiles.length > 0;
		}
		return false;
	}
	
	private boolean isDocumentDirectory(File directory){
		if (directory.isDirectory()) {
			File[] docFiles = directory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name){
					return name.startsWith("Doc_") && name.contains("_");
				}
			});
			return docFiles.length > 0;
		}
		return false;
	}
	
	private boolean isFileDirectory(File directory){
		if (directory.isDirectory()) {
			File[] docFiles = directory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name){
					return name.startsWith("PF_") && name.contains("_");
				}
			});
			return docFiles.length > 0;
		}
		return false;
	}
	
	private boolean isDiagDirectory(File directory){
		if (directory.isDirectory()) {
			if (directory.getName().equalsIgnoreCase("diag")) {
				File[] diagFiles = directory.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name){
						return name.matches("[0-9]+.txt");
					}
				});
				return diagFiles.length > 0;
			}
		}
		return false;
	}
	
	private List<IAeskulapImportFile> getAeskulapFilesFromDirectory(File directory){
		List<IAeskulapImportFile> ret = new ArrayList<>();
		File[] fileOrDirectory = directory.listFiles();
		for (File file : fileOrDirectory) {
			if (file.isDirectory()) {
				if (isLetterDirectory(file)) {
					addLetterDirectory(file);
				} else if (isDocumentDirectory(file)) {
					addDocumentDirectory(file);
				} else if (isFileDirectory(file)) {
					addFileDirectory(file);
				} else if (isDiagDirectory(file)) {
					addDiagDirectory(file);
				} else {
					ret.addAll(getAeskulapFilesFromDirectory(file));
				}
			} else {
				AeskulapFileFactory.getAeskulapFile(file).ifPresent(af -> ret.add(af));
			}
		}
		return ret;
	}
	
	@Override
	public List<IAeskulapImportFile> importFiles(List<IAeskulapImportFile> files,
		boolean overwrite, SubMonitor monitor){
		// deactivate all events
		ElexisEventDispatcher.getInstance()
			.setBlockEventTypes(Arrays.asList(ElexisEvent.EVENT_CREATE, ElexisEvent.EVENT_DELETE,
				ElexisEvent.EVENT_SELECTED, ElexisEvent.EVENT_DESELECTED, ElexisEvent.EVENT_RELOAD,
				ElexisEvent.EVENT_UPDATE));
		// make sure Xids are available
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_ADDRESS, "Alte Adress-ID",
			XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_LABCONTACT, "Alte Labor Kontakt-ID",
			XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_LABITEM, "Alte Labor Typ-ID",
			XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_LABRESULT, "Alte Labor Resultat-ID",
			XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_PATIENT, "Alte KG-ID",
			XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_GARANT, "Alte Garant-ID",
			XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_LETTER, "Alte Brief-ID",
			XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_DOCUMENT, "Alte Dokument-ID",
			XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_FILE, "Alte Datei-ID",
			XidConstants.ASSIGNMENT_LOCAL);
		// create a new map
		transientFiles = new HashMap<>();
		
		List<IAeskulapImportFile> ret = new ArrayList<>();
		Map<Type, List<IAeskulapImportFile>> typedMap = getTypedMap(files);
		// import files ordered by Type sequence
		for (Type type : Type.getSequenced()) {
			files = typedMap.get(type);
			if (files != null) {
				for (IAeskulapImportFile iAeskulapImportFile : files) {
					boolean success =
						iAeskulapImportFile.doImport(transientFiles, overwrite,
							monitor.newChild(1));
					if (!success) {
						ret.add(iAeskulapImportFile);
					} else if (iAeskulapImportFile.isTransient()) {
						transientFiles.put(iAeskulapImportFile.getType(), iAeskulapImportFile);
					}
				}
			}
		}
		// reactivate inbox element creation
		ElexisEventDispatcher.getInstance().setBlockEventTypes(null);
		return ret;
	}
	
	private Map<Type, List<IAeskulapImportFile>> getTypedMap(List<IAeskulapImportFile> files){
		Map<Type, List<IAeskulapImportFile>> ret = new HashMap<>();
		for (IAeskulapImportFile iAeskulapImportFile : files) {
			List<IAeskulapImportFile> list = ret.get(iAeskulapImportFile.getType());
			if (list == null) {
				list = new ArrayList<>();
			}
			list.add(iAeskulapImportFile);
			ret.put(iAeskulapImportFile.getType(), list);
		}
		return ret;
	}
	
}
