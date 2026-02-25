package ch.elexis.importer.aeskulap.core.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.data.Xid;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile.Type;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;
import ch.elexis.importer.aeskulap.core.service.DocumentStoreServiceHolder;
import ch.elexis.omnivore.model.IDocumentHandle;

@Component
public class AeskulapImporter implements IAeskulapImporter {

	private Map<Type, IAeskulapImportFile> transientFiles;

	private LetterDirectories letterDirectories;
	private DocumentDirectories documentDirectories;
	private FileDirectories fileDirectories;
	private DiagDirectory diagDirectory;

	@Override
	public List<IAeskulapImportFile> setImportDirectory(File directory) {
		registerXids();
		List<IAeskulapImportFile> ret = new ArrayList<>();
		if (directory != null && directory.exists() && directory.isDirectory()) {
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

	private void addLetterDirectory(File file) {
		if (letterDirectories == null) {
			letterDirectories = new LetterDirectories();
		}
		letterDirectories.add(file);
	}

	private void addDocumentDirectory(File file) {
		if (documentDirectories == null) {
			documentDirectories = new DocumentDirectories();
		}
		documentDirectories.add(file);
	}

	private void addFileDirectory(File file) {
		if (fileDirectories == null) {
			fileDirectories = new FileDirectories();
		}
		fileDirectories.add(file);
	}

	private void addDiagDirectory(File file) {
		if (diagDirectory == null) {
			diagDirectory = new DiagDirectory();
		}
		diagDirectory.add(file);
	}

	private boolean isLetterDirectory(File directory) {
		if (directory.isDirectory()) {
			File[] docFiles = directory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith("!") && name.contains("_");
				}
			});
			return docFiles.length > 0;
		}
		return false;
	}

	private boolean isDocumentDirectory(File directory) {
		if (directory.isDirectory()) {
			File[] docFiles = directory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith("Doc_") && name.contains("_");
				}
			});
			return docFiles.length > 0;
		}
		return false;
	}

	private boolean isFileDirectory(File directory) {
		if (directory.isDirectory()) {
			File[] docFiles = directory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith("PF_") && name.contains("_");
				}
			});
			return docFiles.length > 0;
		}
		return false;
	}

	private boolean isDiagDirectory(File directory) {
		if (directory.isDirectory()) {
			if (directory.getName().equalsIgnoreCase("diag")) {
				File[] diagFiles = directory.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.matches("[0-9]+.txt");
					}
				});
				return diagFiles.length > 0;
			}
		}
		return false;
	}

	private List<IAeskulapImportFile> getAeskulapFilesFromDirectory(File directory) {
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

	public void registerXids() {
		// make sure Xids are available
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_ADDRESS, "Alte Adress-ID", XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_LABCONTACT, "Alte Labor Kontakt-ID",
				XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_LABITEM, "Alte Labor Typ-ID", XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_LABRESULT, "Alte Labor Resultat-ID",
				XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_PATIENT, "Alte KG-ID", XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_GARANT, "Alte Garant-ID", XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_LETTER, "Alte Brief-ID", XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_DOCUMENT, "Alte Dokument-ID", XidConstants.ASSIGNMENT_LOCAL);
		Xid.localRegisterXIDDomainIfNotExists(XID_IMPORT_FILE, "Alte Datei-ID", XidConstants.ASSIGNMENT_LOCAL);
	}

	@Override
	public List<IAeskulapImportFile> importFiles(List<IAeskulapImportFile> files, boolean overwrite,
			SubMonitor monitor) {
		// deactivate all events
		ElexisEventDispatcher.getInstance().setBlockEventTypes(
				Arrays.asList(ElexisEvent.EVENT_CREATE, ElexisEvent.EVENT_DELETE, ElexisEvent.EVENT_SELECTED,
						ElexisEvent.EVENT_DESELECTED, ElexisEvent.EVENT_RELOAD, ElexisEvent.EVENT_UPDATE));
		// create a new map
		transientFiles = new HashMap<>();

		List<IAeskulapImportFile> ret = new ArrayList<>();
		Map<Type, List<IAeskulapImportFile>> typedMap = getTypedMap(files);
		// import files ordered by Type sequence
		for (Type type : Type.getSequenced()) {
			files = typedMap.get(type);
			if (files != null) {
				for (IAeskulapImportFile iAeskulapImportFile : files) {
					boolean success = iAeskulapImportFile.doImport(transientFiles, overwrite, monitor.newChild(1));
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

	private Map<Type, List<IAeskulapImportFile>> getTypedMap(List<IAeskulapImportFile> files) {
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

	private IModelService omnivoreModelService;

	@Override
	public void removePatientDuplicates(IProgressMonitor monitor) {
		try (CSVWriter csvWriter = initCsvWriter()) {
			IQuery<IXid> xidQuery = CoreModelServiceHolder.get().getQuery(IXid.class);
			xidQuery.and(ModelPackage.Literals.IXID__DOMAIN, COMPARATOR.EQUALS, XID_IMPORT_PATIENT);
			List<IXid> importedXids = xidQuery.execute();
			monitor.beginTask("Patienten Duplikate entfernen", importedXids.size());
			for (IXid importedXid : importedXids) {
				IPatient importedPatient = importedXid.getObject(IPatient.class);
				if (importedPatient != null) {
					IPatient existingPatient = findExistingPatient(importedPatient);
					if (existingPatient != null) {
						if (importedPatient.getCoverages().isEmpty()) {
							transferImportedData(importedPatient, existingPatient);
							existingPatient.addXid(importedXid.getDomain(), importedXid.getDomainId(), true);
							CoreModelServiceHolder.get().delete(importedPatient);
							csvWriter.writeNext(new String[] { importedPatient.getPatientNr(),
									existingPatient.getPatientNr(), "OK" });
						} else {
							csvWriter.writeNext(new String[] { importedPatient.getPatientNr(),
									existingPatient.getPatientNr(), "HASCOVERAGE" });
						}
					} else {
						csvWriter.writeNext(new String[] { importedPatient.getPatientNr(), "", "NOEXISTING" });
					}
				}
				monitor.worked(1);
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error creating csv writer", e);
		}
	}

	private CSVWriter initCsvWriter() throws IOException {
		File file = new File(CoreUtil.getWritableUserDir(), "RemoveDuplicatePatients.csv");
		FileWriter fw = new FileWriter(file);
		CSVWriter csv = new CSVWriter(fw);
		String[] header = new String[] { "ImportPatNr", "ExistingPatNr", "Action" };
		csv.writeNext(header);
		return csv;
	}

	private void transferImportedData(IPatient importedPatient, IPatient existingPatient) {
		// transfer omnivore
		if (omnivoreModelService == null) {
			omnivoreModelService = OsgiServiceUtil.getService(IModelService.class,
					"(" + IModelService.SERVICEMODELNAME + "=ch.elexis.omnivore.data.model)").get();
		}
		IQuery<IDocumentHandle> documentQuery = omnivoreModelService.getQuery(IDocumentHandle.class);
		documentQuery.and("kontakt", COMPARATOR.EQUALS, importedPatient);
		List<IDocumentHandle> importedDocuments = documentQuery.execute();
		for (IDocumentHandle importedDocument : importedDocuments) {
			// no need to transfer categories, just the document
			// create new docment for transfer of content
			IDocument document = DocumentStoreServiceHolder.get().createDocument(existingPatient.getId(),
					importedDocument.getTitle(), importedDocument.getCategory().getName());
			document.setCreated(importedDocument.getCreated());
			document.setLastchanged(importedDocument.getLastchanged());
			document.setExtension(importedDocument.getExtension());
			document.setMimeType(importedDocument.getMimeType());
			try (InputStream fin = importedDocument.getContent()) {
				DocumentStoreServiceHolder.get().saveDocument(document, fin);
			} catch (IOException | ElexisException e) {
				LoggerFactory.getLogger(getClass())
						.error("Error transfer of document [" + importedDocument.getId() + "]", e);
			}
			importedDocument.setDeleted(true);
		}
		omnivoreModelService.save(importedDocuments);
		// transfer Laboratory
		IQuery<ILabResult> query = CoreModelServiceHolder.get().getQuery(ILabResult.class);
		query.and(ModelPackage.Literals.ILAB_RESULT__PATIENT, COMPARATOR.EQUALS, importedPatient);
		List<ILabResult> importedLaboratory = query.execute();
		for (ILabResult importedResult : importedLaboratory) {
			importedResult.setPatient(existingPatient);
		}
		CoreModelServiceHolder.get().save(importedLaboratory);
	}

	private IPatient findExistingPatient(IPatient importedPatient) {
		IXid importedAhv = importedPatient.getXid(XidConstants.DOMAIN_AHV);
		if (importedAhv != null && StringUtils.isNotEmpty(importedAhv.getDomainId())) {
			List<IPatient> patientsWithAhv = XidServiceHolder.get().findObjects(XidConstants.DOMAIN_AHV,
					importedAhv.getDomainId(), IPatient.class);
			if (patientsWithAhv.size() > 1) {
				return patientsWithAhv.stream().filter(p -> !p.getId().equals(importedPatient.getId())).findFirst()
						.get();
			}
		}
		IQuery<IPatient> query = CoreModelServiceHolder.get().getQuery(IPatient.class);
		query.and(ModelPackage.Literals.IPERSON__FIRST_NAME, COMPARATOR.EQUALS, importedPatient.getFirstName());
		query.and(ModelPackage.Literals.IPERSON__LAST_NAME, COMPARATOR.EQUALS, importedPatient.getLastName());
		query.and(ModelPackage.Literals.IPERSON__DATE_OF_BIRTH, COMPARATOR.EQUALS,
				importedPatient.getDateOfBirth().toLocalDate());
		query.and(ModelPackage.Literals.IPERSON__GENDER, COMPARATOR.EQUALS, importedPatient.getGender());
		List<IPatient> patientsMatching = query.execute();
		if (patientsMatching.size() > 1) {
			return patientsMatching.stream().filter(p -> !p.getId().equals(importedPatient.getId())).findFirst().get();
		}
		return null;
	}
}
