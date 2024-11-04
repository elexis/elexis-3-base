package ch.elexis.importer.aeskulap.core.internal;

import java.io.File;
import java.util.Optional;

import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.internal.csv.AddressesFile;
import ch.elexis.importer.aeskulap.core.internal.csv.CoverageFile;
import ch.elexis.importer.aeskulap.core.internal.csv.DocumentFile;
import ch.elexis.importer.aeskulap.core.internal.csv.FileFile;
import ch.elexis.importer.aeskulap.core.internal.csv.LabContactFile;
import ch.elexis.importer.aeskulap.core.internal.csv.LabItemFile;
import ch.elexis.importer.aeskulap.core.internal.csv.LabResultFile;
import ch.elexis.importer.aeskulap.core.internal.csv.LetterFile;
import ch.elexis.importer.aeskulap.core.internal.csv.MandatorFile;
import ch.elexis.importer.aeskulap.core.internal.csv.PatientFile;

public class AeskulapFileFactory {

	public static Optional<IAeskulapImportFile> getAeskulapFile(File file) {
		if (AddressesFile.canHandleFile(file)) {
			return Optional.of(new AddressesFile(file));
		} else if (LabContactFile.canHandleFile(file)) {
			return Optional.of(new LabContactFile(file));
		} else if (LabItemFile.canHandleFile(file)) {
			return Optional.of(new LabItemFile(file));
		} else if (LabResultFile.canHandleFile(file)) {
			return Optional.of(new LabResultFile(file));
		} else if (PatientFile.canHandleFile(file)) {
			return Optional.of(new PatientFile(file));
		} else if (LetterFile.canHandleFile(file)) {
			return Optional.of(new LetterFile(file));
		} else if (DocumentFile.canHandleFile(file)) {
			return Optional.of(new DocumentFile(file));
		} else if (FileFile.canHandleFile(file)) {
			return Optional.of(new FileFile(file));
		} else if (MandatorFile.canHandleFile(file)) {
			return Optional.of(new MandatorFile(file));
		} else if (CoverageFile.canHandleFile(file)) {
			return Optional.of(new CoverageFile(file));
		}
		return Optional.empty();
	}
}
