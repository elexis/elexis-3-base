/*******************************************************************************
 * Copyright (c) 2018 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht - novcom AG
 *******************************************************************************/
package ch.novcom.elexis.mednet.plugin.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.core.types.PathologicDescription;
import ch.elexis.core.types.PathologicDescription.Description;
import ch.elexis.core.ui.text.GenericDocument;
import ch.elexis.data.Kontakt;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabResult;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.novcom.elexis.mednet.plugin.MedNet;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

/**
 * This class is used to import a Documents to a Patient It is used by the
 * DocumentImporter class
 *
 */
public class PatientDocumentManager {
	/**
	 * Logger used to log all activities of the module
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(DocumentImporter.class.getName());

	public static String DEFAULT_PRIO = "1";
	public static String DEFAULT_PRIO_DOCUMENT = "0";

	private static String FLD_ORGIN = "Quelle";

	private static int MAX_LEN_RESULT = 80; // Length of the column LABORWERTE.Result

	private final Patient patient;

	private IDocumentManager omnivoreDocManager;

	private boolean overwriteResults = true;

	private static SimpleDateFormat LABRESULT_TIMESTAMP_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat LABRESULT_TIME_FORMATTER = new SimpleDateFormat("HHmmss");

	/**
	 * Constructor
	 *
	 * @param patient
	 */
	public PatientDocumentManager(Patient patient) {
		super();
		this.patient = patient;
		this.initDocumentManager();
	}

	/**
	 * Try to initialize Omnivore
	 */
	private void initDocumentManager() {
		String logPrefix = "initDocumentManager() - ";//$NON-NLS-1$
		Object omnivore = Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
		if (omnivore != null) {
			this.omnivoreDocManager = (IDocumentManager) omnivore;
		} else {
			LOGGER.error(logPrefix + "Omnivore has not been loaded");//$NON-NLS-1$
		}
	}

	/**
	 * Check if the given Category already exists into Omnivore If not, create it
	 */
	private void checkCreateCategory(final String category) {
		String logPrefix = "checkCreateCategory() - ";//$NON-NLS-1$
		if (category != null) {
			boolean catExists = false;
			String[] categories = this.omnivoreDocManager.getCategories();
			if (categories != null) {
				for (String cat : categories) {
					if (category.equals(cat)) {
						catExists = true;
						break;
					}
				}
			}
			// If the category doesn't exist create it
			if (!catExists) {
				Boolean success = this.omnivoreDocManager.addCategorie(category);
				if (success) {
					LOGGER.info(logPrefix + "New Document category created in Omnivore: " + category);//$NON-NLS-1$
				} else {
					LOGGER.error(logPrefix + "Failed creating new document category in Omnivore: " + category);//$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Add a new document to Omnivore
	 *
	 * @param title
	 * @param category
	 * @param dateStr
	 * @param file
	 * @param keywords
	 * @return true if was successful
	 * @throws IOException
	 * @throws ElexisException
	 */
	private boolean addDocumentToOmnivore(final String title, final String category, final String dateStr,
			final Path file, final String keywords) throws IOException, ElexisException {
		String logPrefix = "addDocumentToOmnivore() - ";//$NON-NLS-1$

		this.checkCreateCategory(category);

		// First check if the document is not already in the database
		List<IOpaqueDocument> documentList = this.omnivoreDocManager.listDocuments(this.patient, category, title, null,
				new TimeSpan(dateStr + "-" + dateStr), null);

		// If no document has been found, we can add it to the database
		if (documentList == null || documentList.size() == 0) {

			// Get the document mimeType
			String mimeType = null;
			try {
				mimeType = Files.probeContentType(file);
				LOGGER.debug(logPrefix + "Mimetype for " + file.toString() + "  is " + mimeType);//$NON-NLS-1$
			} catch (IOException | SecurityException e) {
				// ignore exceptions
				LOGGER.warn(logPrefix + "Unable to find the mimetype of the following file " + file.toString());//$NON-NLS-1$
			}

			this.omnivoreDocManager.addDocument(
					new GenericDocument(this.patient, title, category, file.toFile(), dateStr, keywords, mimeType));
			// If the document has successfully been added
			LOGGER.debug(logPrefix + "This document has successfully been added to the omnivore database." //$NON-NLS-1$
					+ file.toString());

			return true;
		} else {
			// If the document already exists, log it
			LOGGER.warn(logPrefix + "This document already exists in the omnivore database. It will not be imported: " //$NON-NLS-1$
					+ title + StringUtils.SPACE + file.toString());
			return false;
		}
	}

	/**
	 * Search for a LabItem A LabItem is an AnalyzeCode that is linked to an
	 * institution For the documents we will define an AnalyzeCode (shortname) and
	 * name based on the category
	 *
	 * @param shortname
	 * @param name
	 * @param type
	 * @return the LabItem found or null
	 */
	private LabItem getLabItem(String institutionId, String shortname, LabItemTyp type) {
		Query<LabItem> qli = new Query<LabItem>(LabItem.class);
		qli.add(LabItem.SHORTNAME, "=", shortname);
		qli.and();
		qli.add(LabItem.LAB_ID, "=", institutionId);
		qli.and();
		qli.add(LabItem.TYPE, "=", new Integer(type.ordinal()).toString()); //$NON-NLS-1$

		LabItem labItem = null;
		List<LabItem> itemList = qli.execute();

		if (itemList.size() > 0) {
			// If we find multiple items, take the first one
			labItem = itemList.get(0);
		}
		return labItem;
	}

	/**
	 * Look for a result
	 *
	 * @param labItem      the corresponding labItem this result should be part of
	 * @param value        the value of the result
	 * @param samplingDate the date of the result
	 * @return The result we found or null
	 */
	private LabResult getLabResult(LabItem labItem, String value, Date samplingDate, Date transmissionDate) {
		Query<LabResult> qli = new Query<LabResult>(LabResult.class);
		qli.add(LabResult.ITEM_ID, "=", labItem.getId());//$NON-NLS-1$
		qli.and();
		qli.add(LabResult.OBSERVATIONTIME, "=", //$NON-NLS-1$
				PatientDocumentManager.LABRESULT_TIMESTAMP_FORMATTER.format(samplingDate));
		qli.and();
		qli.add(LabResult.TRANSMISSIONTIME, "=", //$NON-NLS-1$
				PatientDocumentManager.LABRESULT_TIMESTAMP_FORMATTER.format(transmissionDate));
		qli.and();
		qli.add(LabResult.PATIENT_ID, "=", patient.getId());//$NON-NLS-1$
		qli.and();
		qli.add(LabResult.RESULT, "=", value);//$NON-NLS-1$

		LabResult labResult = null;
		List<LabResult> resultList = qli.execute();
		if (resultList.size() > 0) {
			// If we find multiple items, take the first one
			labResult = resultList.get(0);
		}
		return labResult;
	}

	/**
	 *
	 * Save a LaborItem to the Database
	 *
	 * @param institutionId
	 * @param institutionName
	 * @param category
	 * @param orderId
	 * @param file
	 * @param samplingDateTime
	 * @param keyword
	 * @throws IOException
	 */

	public void addDocument(ContactLinkRecord contactLink, Kontakt institution, String orderId, Path file,
			Date samplingDateTime, Date transmissionDateTime, String keywords) throws IOException {
		String logPrefix = "addDocument() - ";//$NON-NLS-1$

		// First of all check if Omnivore exists
		if (this.omnivoreDocManager == null) {
			throw new IOException(MessageFormat.format(MedNetMessages.PatientDocumentManager_omnivoreNotInitialized,
					file.toString(), this.patient.getLabel()));
		}

		// Check if there is already a labitem of this category
		LabItem labItem = getLabItem(institution.getId(), MedNetMessages.PatientDocumentManager_documentId,
				LabItemTyp.DOCUMENT);

		// If no LabItem has been found, create one
		if (labItem == null) {
			labItem = new LabItem(MedNetMessages.PatientDocumentManager_documentId, // Test Code
					MedNetMessages.PatientDocumentManager_documentTitel, // Test Name
					institution, // If institution is empty, the labitem will be linked to the internal
									// laboratory
					StringUtils.EMPTY, StringUtils.EMPTY, "pdf", LabItemTyp.DOCUMENT, institution.getLabel(true),
					DEFAULT_PRIO_DOCUMENT);
		}

		// Finally create a new laboratory Result to save to the database

		// First of all define a title we will use for this result:
		// Set a title to the document
		SimpleDateFormat transmissionDateTimeFormatter = new SimpleDateFormat(
				MedNetMessages.PatientDocumentManager_LabResultTitleTransactionFormat);
		SimpleDateFormat samplingDateTimeFormatter = new SimpleDateFormat(
				MedNetMessages.PatientDocumentManager_LabResultTitleSamplingFormat);

		String title = StringUtils.EMPTY;
		if (samplingDateTime != null && transmissionDateTime != null) {
			title = MessageFormat.format(MedNetMessages.PatientDocumentManager_LabResultTitle, orderId,
					samplingDateTimeFormatter.format(samplingDateTime),
					transmissionDateTimeFormatter.format(transmissionDateTime), DocumentImporter.getBaseName(file),
					DocumentImporter.getExtension(file));
		} else if (transmissionDateTime != null) {
			title = MessageFormat.format(MedNetMessages.PatientDocumentManager_DocumentOmnivoreTitle, orderId,
					transmissionDateTimeFormatter.format(transmissionDateTime), DocumentImporter.getBaseName(file),
					DocumentImporter.getExtension(file));
		}

		// Limit the length of the title
		// If it is too long, cut it
		if (title.length() > MAX_LEN_RESULT)
			title = title.substring(0, title.length() - MAX_LEN_RESULT - 3) + "...";//$NON-NLS-1$

		// Since the labResult Object uses TimeTool,
		// We will convert the documentDateTime into a TimeTool

		TimeTool transmissionDate = new TimeTool();
		transmissionDate.setTime(transmissionDateTime);

		TimeTool samplingDate = new TimeTool();
		String samplingTime = null;
		if (samplingDateTime != null) {
			samplingDate.setTime(samplingDateTime);
			samplingTime = PatientDocumentManager.LABRESULT_TIME_FORMATTER.format(samplingDateTime);
		} else {
			samplingDateTime = transmissionDateTime;
			samplingDate.setTime(transmissionDateTime);
			samplingTime = PatientDocumentManager.LABRESULT_TIME_FORMATTER.format(transmissionDateTime);
		}

		boolean saved = false;

		// Check if a result already exists with this title, and this time
		LabResult labResult = this.getLabResult(labItem, title, samplingDateTime, transmissionDateTime);
		if (labResult == null) {
			// If no results exists, create one
			labResult = new LabResult(patient, samplingDate, labItem, title, null);
			labResult.set(FLD_ORGIN, orderId);
			labResult.set(LabResult.TIME, samplingTime);
			labResult.setObservationTime(samplingDate);
			labResult.setTransmissionTime(transmissionDate);
			labResult.setPathologicDescription(new PathologicDescription(Description.PATHO_IMPORT));
			saved = true;
		} else {
			// If there is already a labresult
			// We should check if we should overwrite it

			// If the option overwrite results is set to true, we overwrite the result
			if (overwriteResults) {
				LOGGER.warn(logPrefix + "An older version of this document will be overwritten:" + labItem.getKuerzel()
						+ "-" + labItem.getName() + StringUtils.SPACE + labResult.getObservationTime().toDBString(true)
						+ StringUtils.SPACE + labResult.getTransmissionTime().toDBString(true) + StringUtils.SPACE
						+ samplingDate.toDBString(true) + StringUtils.SPACE + labResult.getResult() + StringUtils.SPACE
						+ title + StringUtils.SPACE);
				labResult.setResult(title);
				labResult.set(LabResult.TIME, samplingTime);
				labResult.setObservationTime(samplingDate);
				labResult.setTransmissionTime(transmissionDate);
				labResult.setPathologicDescription(new PathologicDescription(Description.PATHO_IMPORT));
				saved = true;
			} else {

				LOGGER.warn(logPrefix + "Another version of this document is still in the database:"
						+ labItem.getKuerzel() + "-" + labItem.getName() + StringUtils.SPACE
						+ labResult.getObservationTime().toDBString(true) + StringUtils.SPACE
						+ labResult.getTransmissionTime().toDBString(true) + StringUtils.SPACE
						+ samplingDate.toDBString(true) + StringUtils.SPACE + labResult.getResult() + StringUtils.SPACE
						+ title + StringUtils.SPACE);
			}
		}

		// If we were able to create a labResult
		// We can archive the document into Omnivore
		if (saved) {
			try {
				String dateTimeDocumentString = samplingDate.toString(TimeTool.DATE_GER);

				// Construct the category were the documents will be stored:
				String categorylabel = null;
				if (contactLink != null) {
					Kontakt kontakt = Kontakt.load(contactLink.getContactID());
					String name = kontakt.getLabel(true);
					if (name == null || name.isEmpty()) {
						name = MedNet.getSettings().getInstitutions().get(contactLink.getMedNetID());
					}
					String category = contactLink.getCategoryDoc();
					if (category == null || category.isEmpty()) {
						categorylabel = name;
					} else {
						categorylabel = MessageFormat.format(MedNetMessages.Omnivore_category_formlabel, name,
								category);
					}
				} else {
					categorylabel = institution.getLabel(true);
				}

				this.addDocumentToOmnivore(title, categorylabel, dateTimeDocumentString, file, keywords);

				LOGGER.info(logPrefix + "Document successfully saved to omnivore:" + title);// $NON-NLS-1$
			} catch (ElexisException e) {
				throw new IOException(MessageFormat.format(MedNetMessages.PatientLabor_errorAddingDocumentToOmnivore,
						file.toString()), e);
			}
		}
	}

	/**
	 *
	 * Save a LaborItem to the Database
	 *
	 * @param institutionId
	 * @param institutionName
	 * @param category
	 * @param orderId
	 * @param file
	 * @param documentDateTime
	 * @param keyword
	 * @throws IOException
	 */
	public void addForm(ContactLinkRecord contactLink, String institutionName, String formularName, String orderId,
			Path file, Date documentDateTime, String keywords) throws IOException {
		String logPrefix = "addForm() - ";//$NON-NLS-1$

		// First of all check if Omnivore exists
		if (this.omnivoreDocManager == null) {
			throw new IOException(MessageFormat.format(MedNetMessages.PatientDocumentManager_omnivoreNotInitialized,
					file.toString(), this.patient.getLabel()));
		}

		// Construct the category were the documents will be stored:
		String categorylabel = null;
		if (contactLink != null) {
			Kontakt kontakt = Kontakt.load(contactLink.getContactID());

			String name = kontakt.getLabel(true);
			if (name == null || name.isEmpty()) {
				name = MedNet.getSettings().getInstitutions().get(contactLink.getMedNetID());
			}
			String category = contactLink.getCategoryForm();
			if (category == null || category.isEmpty()) {
				categorylabel = name;
			} else {
				categorylabel = MessageFormat.format(MedNetMessages.Omnivore_category_formlabel, name, category);
			}
		} else {
			categorylabel = institutionName;
		}

		// Finally create a new laboratory Result to save to the database

		// First of all define a title we will use for this result:
		// Set a title to the document
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
				MedNetMessages.PatientDocumentManager_LabResultTitleTransactionFormat);

		String title = MessageFormat.format(MedNetMessages.PatientDocumentManager_FormTitle, formularName, orderId,
				dateTimeFormatter.format(documentDateTime), DocumentImporter.getBaseName(file),
				DocumentImporter.getExtension(file));

		// Limit the length of the title
		// If it is too long, cut it
		if (title.length() > MAX_LEN_RESULT)
			title = title.substring(0, title.length() - MAX_LEN_RESULT - 3) + "...";//$NON-NLS-1$

		// Since the labResult Object uses TimeTool,
		// We will convert the documentDateTime into a TimeTool
		TimeTool documentDate = new TimeTool();
		documentDate.setTime(documentDateTime);

		// Archive the document into Omnivore
		try {
			String dateTimeDocumentString = documentDate.toString(TimeTool.DATE_GER);

			this.addDocumentToOmnivore(title, categorylabel, dateTimeDocumentString, file, keywords);

			LOGGER.info(logPrefix + "Document successfully saved to omnivore:" + title);// $NON-NLS-1$
		} catch (ElexisException e) {
			throw new IOException(
					MessageFormat.format(MedNetMessages.PatientLabor_errorAddingDocumentToOmnivore, file.toString()),
					e);
		}

	}

}
