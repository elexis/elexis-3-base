package ch.elexis.icpc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import ch.elexis.data.PersistentObject;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;

public class IcpcReferenceDataImporter
		extends ch.elexis.core.interfaces.AbstractReferenceDataImporter {
	
	@Override
	public Class<?> getReferenceDataTypeResponsibleFor(){
		return IcpcCode.class;
	}
	
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
				
				monitor.worked(1);
				JdbcLink pj = PersistentObject.getConnection();
				
				monitor.subTask("Lösche alte Daten");
				pj.exec("DELETE FROM " + IcpcCode.TABLENAME + " where ID != 'ver'");
				monitor.worked(1);
				monitor.subTask("Lese Daten ein");
				PreparedStatement ps = pj.prepareStatement("INSERT INTO " + IcpcCode.TABLENAME
					+ " ("
					+ "ID,component,txt,synonyms,short,icd10,criteria,inclusion,exclusion,consider,note)"
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?);");
				monitor.worked(1);
				
				Table table = db.getTable("ICPC2eGM");
				Iterator<Row> it = table.iterator();
				while (it.hasNext()) {
					Row row = it.next();
					ps.setString(1, (String) row.get("CODE")); // id
					ps.setObject(2, row.get("COMPONENT")); // component
					ps.setString(3, (String) row.get("TEXT"));// txt
					ps.setString(4, (String) row.get("SYNONYMS"));// synonyms
					ps.setString(5, (String) row.get("SHORT"));// short
					ps.setString(6, (String) row.get("ICD-10"));// icd10
					ps.setString(7, (String) row.get("CRIT"));// criteria
					ps.setString(8, (String) row.get("INCL"));
					ps.setString(9, (String) row.get("EXCL"));
					ps.setString(10, (String) row.get("CONS"));
					ps.setString(11, (String) row.get("NOTE"));
					ps.execute();
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					monitor.worked(1);
				}
				
				monitor.done();
			}
		} catch (Exception e) {
			ExHandler.handle(e);
			return new Status(Status.ERROR, "ICPC", 3, e.getMessage(), null);
		} finally {
			IOUtils.closeQuietly(db);
		}
		return Status.OK_STATUS;
	}
	
	@Override
	public int getCurrentVersion(){
		// currently the dataset is not versioned
		return 0;
	}
	
}
