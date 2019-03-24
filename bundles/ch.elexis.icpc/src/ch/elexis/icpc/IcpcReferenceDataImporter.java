package ch.elexis.icpc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import ch.elexis.data.PersistentObject;
import ch.rgw.tools.ExHandler;

public class IcpcReferenceDataImporter
		extends ch.elexis.core.interfaces.AbstractReferenceDataImporter {
	
	private IcpcImporter icpcImporter = new IcpcImporter();
	
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
				return icpcImporter.doImport(monitor, db);
			}
		} catch (Exception e) {
			ExHandler.handle(e);
			return new Status(Status.ERROR, "ICPC", 3, e.getMessage(), null);
		} finally {
			IOUtils.closeQuietly(db);
		}
	}
	
	@Override
	public int getCurrentVersion(){
		// don't re-import again
		int count = PersistentObject.getDefaultConnection().getJdbcLink()
			.queryInt("SELECT COUNT(*) FROM " + IcpcCode.TABLENAME + " WHERE ID != 'ver'");
		return count > 0 ? 1 : 0;
	}
	
}
