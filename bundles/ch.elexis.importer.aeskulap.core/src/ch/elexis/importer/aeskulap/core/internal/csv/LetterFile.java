package ch.elexis.importer.aeskulap.core.internal.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.LoggerFactory;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IPersistentObject;
import ch.elexis.data.Patient;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;
import ch.elexis.importer.aeskulap.core.service.DocumentStoreServiceHolder;
import ch.rgw.tools.TimeTool;

public class LetterFile extends AbstractCsvImportFile<IDocument> implements IAeskulapImportFile {
	
	public static final String CATEGORY_AESKULAP_BRIEFE = "Aeskulap-Briefe";
	
	private File file;
	
	private ICategory importCategory;
	
	private IAeskulapImportFile letterDirectory;
	
	public LetterFile(File file){
		super(file);
		this.file = file;
	}
	
	@Override
	public File getFile(){
		return file;
	}
	
	public static boolean canHandleFile(File file){
		// can only handle letter if store is available
		return FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("csv")
			&& FilenameUtils.getBaseName(file.getName()).equalsIgnoreCase("Briefe");
	}
	
	@Override
	public Type getType(){
		return Type.LETTER;
	}
	
	@Override
	public boolean doImport(Map<Type, IAeskulapImportFile> transientFiles, boolean overwrite,
		SubMonitor monitor){
		monitor.beginTask("Aeskuplap Briefe Import", getLineCount());
		importCategory = DocumentStoreServiceHolder.get().createCategory(CATEGORY_AESKULAP_BRIEFE);
		letterDirectory = transientFiles.get(Type.LETTERDIRECTORY);
		if (letterDirectory != null) {
			try {
				String[] line = null;
				while ((line = getNextLine()) != null) {
					String filename = getFilename(line);
					file = (File) letterDirectory.getTransient(filename);
					if (file != null) {
						IDocument letter = getExisting(line[1]);
						if (letter == null) {
							letter = create(line);
						} else if (!overwrite) {
							// skip if overwrite is not set
							continue;
						}
						setProperties(letter, line);
						letter.setExtension(FilenameUtils.getExtension(file.getName()));
						letter.setMimeType(FilenameUtils.getExtension(file.getName()));
						DocumentStoreServiceHolder.get().saveDocument(letter,
							new FileInputStream(file));
						String xid = line[1];
						Optional<IPersistentObject> po =
							DocumentStoreServiceHolder.get().getPersistenceObject(letter);
						po.ifPresent(o -> o.addXid(getXidDomain(), xid, true));
					}
					monitor.worked(1);
				}
				return true;
			} catch (IOException e) {
				LoggerFactory.getLogger(getClass()).error("Error importing file", e);
			} catch (ElexisException e) {
				LoggerFactory.getLogger(getClass()).error("Error saving file", e);
			} finally {
				close();
				monitor.done();
			}
		} else {
			LoggerFactory.getLogger(getClass()).error("No letter directories found");
		}
		return false;
	}
	
	private String getFilename(String[] line){
		return new StringBuilder("!").append(line[0]).append("_").append(line[1]).toString();
	}
	
	@Override
	public boolean isHeaderLine(String[] line){
		return line[0].equalsIgnoreCase("pat_no");
	}
	
	@Override
	public String getXidDomain(){
		return IAeskulapImporter.XID_IMPORT_LETTER;
	}
	
	@Override
	public IDocument create(String[] line){
		Patient patient = (Patient) getWithXid(IAeskulapImporter.XID_IMPORT_PATIENT, line[0]);
		IDocument document = DocumentStoreServiceHolder.get().createDocument(patient.getId(),
			line[3],
			importCategory.getName());
		return document;
	}
	
	@Override
	public void setProperties(IDocument document, String[] line){
		TimeTool letterDate = new TimeTool(line[2]);
		document.setCreated(letterDate.getTime());
		document.setLastchanged(letterDate.getTime());
	}
}
