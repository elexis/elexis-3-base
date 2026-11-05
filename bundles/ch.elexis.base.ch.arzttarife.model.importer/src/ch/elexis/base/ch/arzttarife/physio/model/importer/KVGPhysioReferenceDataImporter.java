package ch.elexis.base.ch.arzttarife.physio.model.importer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import ch.elexis.core.interfaces.IReferenceDataImporter;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=physio_kvg")
public class KVGPhysioReferenceDataImporter extends PhysioReferenceDataImporter implements IReferenceDataImporter {

	// first column of the header row of the new csv format (Tarif 312)
	private static final String NEW_FORMAT_HEADER_PREFIX = "type;";

	private final PhysioReferenceDataImporterV2 newFormatImporter = new KVGPhysioReferenceDataImporterV2();

	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer newVersion) {
		try {
			byte[] firstLine = readFirstLine(input);
			// re-inject the first line, both formats contain a header row that is skipped by the importers
			InputStream fullStream = new SequenceInputStream(new ByteArrayInputStream(firstLine), input);
			if (isNewFormat(firstLine)) {
				return newFormatImporter.performImport(monitor, fullStream, newVersion);
			}
			return super.performImport(monitor, fullStream, newVersion);
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Could not detect physio tarif format", e);
			return Status.CANCEL_STATUS;
		}
	}

	private byte[] readFirstLine(InputStream input) throws IOException {
		ByteArrayOutputStream firstLine = new ByteArrayOutputStream();
		int b;
		while ((b = input.read()) != -1) {
			firstLine.write(b);
			if (b == '\n') {
				break;
			}
		}
		return firstLine.toByteArray();
	}

	private boolean isNewFormat(byte[] firstLine) {
		return new String(firstLine, StandardCharsets.US_ASCII).startsWith(NEW_FORMAT_HEADER_PREFIX);
	}

	@Override
	protected String getLaw() {
		return "KVG";
	}
}
