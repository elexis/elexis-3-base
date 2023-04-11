package ch.elexis.TarmedRechnung;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.InvoiceState;

public class XMLFileUtil {

	/**
	 * Get a filename including outputDir according to following schema.
	 * <li>reminder n bill: <i>rnnr_mn.xml</i></li>
	 * <li>cancelled bill: <i>rnnr_storno.xml</i></li>
	 * <li>all other bills: <i>rnnr.xml</i></li> <br />
	 * If there is already a file with the same filename in the outputDir, the
	 * current millis is appended before .xml
	 * 
	 * @param invoice
	 * @param outputDir
	 * @return
	 */
	public static Optional<String> getFileName(IInvoice invoice, String outputDir) {
		if (invoice != null && StringUtils.isNotBlank(outputDir)) {
			return getFileName("", invoice, outputDir);
		}
		return Optional.empty();
	}

	private static Optional<String> getFileName(String timestamp, IInvoice invoice, String outputDir) {
		StringBuilder fname = new StringBuilder(outputDir + (outputDir.endsWith(File.separator) ? "" : File.separator)); //$NON-NLS-1$ ;
		fname.append(invoice.getNumber());
		fname.append(getFilenameAppendix(invoice.getState()));
		if (StringUtils.isNotBlank(timestamp)) {
			fname.append("_" + timestamp);
		}
		fname.append(".xml");
		File file = new File(fname.toString());
		if(file.exists()) {
			return getFileName(Long.toString(System.currentTimeMillis()), invoice, outputDir);
		}
		return Optional.of(fname.toString());
	}

	private static String getFilenameAppendix(InvoiceState state) {
		if (state == InvoiceState.DEMAND_NOTE_1 || state == InvoiceState.DEMAND_NOTE_1_PRINTED) {
			return "_m1";
		} else if (state == InvoiceState.DEMAND_NOTE_2 || state == InvoiceState.DEMAND_NOTE_2_PRINTED) {
			return "_m2";
		} else if (state == InvoiceState.DEMAND_NOTE_3 || state == InvoiceState.DEMAND_NOTE_3_PRINTED) {
			return "_m3";
		} else if (state == InvoiceState.CANCELLED) {
			return "_storno";
		}
		return "";
	}

	public static void writeToFile(String filename, Document document) {
		File xmlOutput = new File(filename);
		try (FileWriter writer = new FileWriter(xmlOutput)) {
			writer.write(getAsString(document));
		} catch (IOException e) {
			LoggerFactory.getLogger(XMLFileUtil.class).error("Could not write [" + xmlOutput.getAbsolutePath() + "]",
					e);
		}
		LoggerFactory.getLogger(XMLFileUtil.class)
				.info("Wrote [" + xmlOutput.getAbsolutePath() + "] with size [" + xmlOutput.length() + "]");
	}

	public static String getAsString(Document document) {
		try {
			StringWriter stringWriter = new StringWriter();
			XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
			xout.output(document, stringWriter);
			return stringWriter.toString();
		} catch (IOException e) {
			LoggerFactory.getLogger(XMLFileUtil.class).error("Error creating string from invoice", e);
		}
		return null;
	}

	/**
	 * Move the filte to the archiveDir. Add timestamp to the filename if file with
	 * same name exists in archiveDir. Returns the moved file.
	 * 
	 * @param file
	 * @param archiveDir
	 * @return
	 */
	public static File moveToArchive(File file, File archiveDir) {
		File destFile = new File(archiveDir, file.getName());
		if (destFile.exists()) {
			destFile = new File(archiveDir, addTimestamp(file.getName()));
		}

		Path src = file.toPath();
		Path dest = destFile.toPath();
		try {
			Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LoggerFactory.getLogger(XMLFileUtil.class).error(
					"Error moving [" + file.getAbsolutePath() + "] to archive [" + archiveDir.getAbsolutePath() + "]");
		}

		return destFile;
	}

	private static String addTimestamp(String filename) {
		return FilenameUtils.getBaseName(filename) + "_" + Long.toString(System.currentTimeMillis()) + "."
				+ FilenameUtils.getExtension(filename);
	}

	/**
	 * Lookup an xml file matching the provided information in the outputDir.
	 * 
	 * @param outputDir
	 * @param invoice
	 * @param timestamp
	 * @param invoiceState
	 * @return
	 */
	public static Optional<File> lookupFile(String outputDir, IInvoice invoice, LocalDateTime timestamp,
			InvoiceState invoiceState) {
		if (StringUtils.isNotBlank(outputDir)) {
			File directory = new File(outputDir);
			if (directory.exists() && directory.isDirectory()) {
				List<File> invoiceFiles = Arrays.asList(directory.listFiles()).stream()
						.filter(f -> f.getName().toLowerCase().startsWith(invoice.getNumber() + "_")
								|| f.getName().toLowerCase().startsWith(invoice.getNumber() + "."))
						.collect(Collectors.toList());
				if (!invoiceFiles.isEmpty()) {
					if (invoiceFiles.size() > 1) {
						List<File> filteredInvoiceFiles = new ArrayList<>(invoiceFiles);
						if (invoiceState != null && StringUtils.isNotBlank(getFilenameAppendix(invoiceState))) {
							filteredInvoiceFiles = filteredInvoiceFiles.stream()
									.filter(f -> f.getName().toLowerCase().contains("_" + getFilenameAppendix(invoiceState)))
									.collect(Collectors.toList());
						}
						if (filteredInvoiceFiles.size() > 1) {
							if (timestamp != null) {
								filteredInvoiceFiles = filteredInvoiceFiles.stream()
										.filter(f -> Math.abs(ChronoUnit.SECONDS
												.between(LocalDateTime.ofInstant(Instant.ofEpochMilli(f.lastModified()),
														TimeZone.getDefault().toZoneId()), timestamp)) < 5)
										.collect(Collectors.toList());
							}
						}
						if (filteredInvoiceFiles.size() == 1) {
							return Optional.of(filteredInvoiceFiles.get(0));
						}
					}
				}
				return Optional.of(invoiceFiles.get(0));
			}
		}
		return Optional.empty();
	}
}
