package ch.elexis.icpc.model.internal.importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jpa.entities.ICPCCode;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.icpc.model.icpc.IcpcCode;
import ch.elexis.icpc.model.internal.service.IcpcModelServiceHolder;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=icpc2")
public class IcpcReferenceDataImporter extends AbstractReferenceDataImporter
		implements IReferenceDataImporter {
	
	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer newVersion){
		monitor.beginTask("Importiere ICPC-2", 727);
		Database db = null;
		try {
			File tempFile = File.createTempFile("tmp_icpc-2", ".mdb");
			tempFile.deleteOnExit();
			
			try (OutputStream outputStream = new FileOutputStream(tempFile)) {
				IOUtils.copy(input, outputStream);
				db = new DatabaseBuilder().setReadOnly(true).open(tempFile);
				return doImport(monitor, db);
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Import error", e);
			return new Status(Status.ERROR, "ICPC", 3, e.getMessage(), null);
		} finally {
			IOUtils.closeQuietly(db);
		}
	}
	
	@Override
	public int getCurrentVersion(){
		long count = IcpcModelServiceHolder.get().executeNativeQuery("SELECT ID FROM CH_ELEXIS_ICPC WHERE ID != 'ver'").count();
		return count > 0 ? 1 : 0;
	}
	
	private IStatus doImport(IProgressMonitor monitor, Database db) throws IOException{
		monitor.worked(1);
		
		monitor.subTask("Lösche alte Daten");
		IQuery<IcpcCode> query = IcpcModelServiceHolder.get().getQuery(IcpcCode.class);
		query.and("id", COMPARATOR.NOT_EQUALS, "ver");
		List<IcpcCode> existing = query.execute();
		existing.forEach(ec -> IcpcModelServiceHolder.get().remove(ec));
		monitor.worked(1);
		monitor.subTask("Lese Daten ein");
		List<Object> codeEntities = new ArrayList<>();
		Table table = db.getTable("ICPC2eGM");
		Iterator<Row> it = table.iterator();
		while (it.hasNext()) {
			Row row = it.next();
			ICPCCode entity = new ICPCCode();
			entity.setId((String) row.get("CODE"));
			Object component = row.get("COMPONENT");
			entity.setComponent(component != null ? String.valueOf(component) : null);
			entity.setText((String) row.get("TEXT"));
			entity.setSynonyms((String) row.get("SYNONYMS"));
			entity.setShortName((String) row.get("SHORT"));
			entity.setIcd10((String) row.get("ICD-10"));
			entity.setCriteria((String) row.get("CRIT"));
			entity.setInclusion((String) row.get("INCL"));
			entity.setExclusion((String) row.get("EXCL"));
			entity.setConsider((String) row.get("CONS"));
			entity.setNote((String) row.get("NOTE"));
			codeEntities.add(entity);
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			monitor.worked(1);
		}
		monitor.done();
		EntityUtil.save(codeEntities);
		return Status.OK_STATUS;
	}
}
