package ch.elexis.importer.aeskulap.core;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.SubMonitor;

import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabResult;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Xid;

public interface IAeskulapImportFile {

	/**
	 * Type of import.
	 *
	 * @author thomas
	 *
	 */
	public enum Type {

		//@formatter:off
		ADDRESSES(1, Kontakt.class),
		MANDATOR(2, Mandant.class),
		PATIENT(100, Patient.class),
		COVERAGE(101, Fall.class),
		LABORCONTACT(200, Kontakt.class), 
		LABORITEM(210, LabItem.class), 
		LABORRESULT(250, LabResult.class),
		DIAGDIRECTORY(300, File.class),
		LETTER(1001, IOpaqueDocument.class), 
		LETTERDIRECTORY(1000, File.class),
		DOCUMENT(1101, IOpaqueDocument.class), 
		DOCUMENTDIRECTORY(1100, File.class), 
		FILE(1201, IOpaqueDocument.class),
		FILEDIRECTORY(1200, File.class);
		//@formatter:on

		private int sequence;
		private Class<?> elexisClass;

		private static List<Type> sequenced;

		private Type(int sequence, Class<?> clazz) {
			this.sequence = sequence;
			this.elexisClass = clazz;
		}

		public int getSequence() {
			return sequence;
		}

		public static List<Type> getSequenced() {
			if (sequenced == null) {
				List<Type> list = Arrays.asList(values());
				list.sort(new Comparator<Type>() {
					@Override
					public int compare(Type o1, Type o2) {
						return Integer.valueOf(o1.getSequence()).compareTo(o2.getSequence());
					}
				});
				sequenced = list;
			}
			return sequenced;
		}

		public boolean checkElexisClass(Object object) {
			return elexisClass.isAssignableFrom(object.getClass());
		}
	}

	/**
	 * Get the import file.
	 *
	 * @return
	 */
	public File getFile();

	/**
	 * Get the {@link Type} of import.
	 *
	 * @return
	 */
	public Type getType();

	/**
	 * Import the data from the file.
	 *
	 * @param overwrite
	 * @param monitor
	 * @return
	 */
	public default boolean doImport(boolean overwrite, SubMonitor monitor) {
		return doImport(null, overwrite, monitor);
	}

	/**
	 * Import the data from the file. The transientFiles map can be used to access
	 * imported transient data.
	 *
	 * @param transientFiles
	 * @param overwrite
	 * @param monitor
	 * @return
	 */
	public boolean doImport(Map<Type, IAeskulapImportFile> transientFiles, boolean overwrite, SubMonitor monitor);

	/**
	 * Test if this is a transient importer. Meaning that the imported data is not
	 * persisted.
	 *
	 * @return
	 */
	public default boolean isTransient() {
		return false;
	}

	/**
	 * If {@link IAeskulapImportFile#isTransient()} imported data can be accessed by
	 * id using this method.
	 *
	 * @param id
	 * @return
	 */
	public default Object getTransient(String id) {
		return null;
	}

	/**
	 * Get the object identified with the XID domain and id.
	 *
	 * @param domain
	 * @param id
	 * @return
	 */
	public default Object getWithXid(String domain, String id) {
		Xid existingXid = Xid.findXID(domain, id);
		if (existingXid != null) {
			return existingXid.getObject();
		}
		return null;
	}
}
