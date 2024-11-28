package ch.elexis.mednet.webapi.core.fhir.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Enumerations.DocumentReferenceStatus;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IPatient;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;
import ch.elexis.omnivore.model.IDocumentHandle;

public class DocumentResource {

	private static final Logger logger = LoggerFactory.getLogger(DocumentResource.class);

	public static List<DocumentReference> createDocuments(IPatient sourcePatient, String patientFullUrl,
			List<IDocument> selectedDocuments, boolean isEpdSelected) {
        List<DocumentReference> documentReferences = new ArrayList<>();

            for (IDocument document : selectedDocuments) {
                DocumentReference fhirDocument = new DocumentReference();

				fhirDocument.setId(UUID.nameUUIDFromBytes(document.getId().getBytes()).toString());

                Meta meta = new Meta();
				meta.addProfile(FHIRConstants.PROFILE_DOCUMENT_REFERENCE);
                fhirDocument.setMeta(meta);

                fhirDocument.setStatus(DocumentReferenceStatus.CURRENT);

                CodeableConcept type = new CodeableConcept();
                Coding typeCoding = new Coding();
				typeCoding.setSystem(FHIRConstants.SNOMED_SYSTEM);
				typeCoding.setCode("419891008");
				typeCoding.setDisplay("Record artifact");
                type.addCoding(typeCoding);
                fhirDocument.setType(type);

                CodeableConcept category = new CodeableConcept();
                Coding snomedCoding = new Coding();
				snomedCoding.setSystem(FHIRConstants.SNOMED_SYSTEM);
				snomedCoding.setCode("371531000");
                snomedCoding.setDisplay("Report of clinical encounter (record artifact)");
                category.addCoding(snomedCoding);

				if (isEpdSelected) {
					Coding esanitaCoding = new Coding();
					esanitaCoding.setSystem(FHIRConstants.ESANITA_SYSTEM);
					esanitaCoding.setCode("epd");
					esanitaCoding.setDisplay("EPD");
					category.addCoding(esanitaCoding);
				}

                fhirDocument.addCategory(category);

                fhirDocument.setSubject(new Reference(patientFullUrl));
				String authorReference = FHIRConstants.UUID_PREFIX
						+ UUID.nameUUIDFromBytes(sourcePatient.getId().getBytes());
				fhirDocument.addAuthor(new Reference(authorReference));

                Attachment attachment = new Attachment();
				attachment.setContentType(FHIRConstants.FHIRKeys.APPLIKATION_TYP);
				String originalTitle = document.getTitle();

				if (originalTitle != null && originalTitle.contains(".")) {
					originalTitle = originalTitle.substring(0, originalTitle.lastIndexOf('.'));
				}

				attachment.setTitle(originalTitle);

                Date createdDate = document.getCreated();
                if (createdDate != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String creationDate = dateFormat.format(createdDate);
                    attachment.setCreationElement(new DateTimeType(creationDate));
                }

				IDocumentHandle docHandle = (IDocumentHandle) document;
				String documentPath = docHandle.getHandle().getAbsolutePath();

				if (documentPath != null && !documentPath.isEmpty()) {
					File file;
					if (documentPath.startsWith("file:/")) {
						String path = documentPath.substring(6);
						if (path.startsWith("/")) {
							path = path.substring(1);
						}
						file = new File(path);
					} else {
						file = new File(documentPath);
					}
					try (FileInputStream fis = new FileInputStream(file)) {
						byte[] fileContent = new byte[(int) file.length()];
						int bytesRead = fis.read(fileContent);
						if (bytesRead != fileContent.length) {
							logger.error("Konnte die gesamte Datei nicht lesen: " + documentPath);
							continue;
						}
						attachment.setData(fileContent);
						
					} catch (IOException e) {
						logger.error("Fehler beim Lesen der Datei: " + file.getAbsolutePath());
						e.printStackTrace();
						continue;
					}
				} else {
					logger.error("Dokumentpfad ist nicht verfügbar für Dokument: " + document.getTitle());
					continue;
				}
                fhirDocument.addContent().setAttachment(attachment);
                documentReferences.add(fhirDocument);
            }

        return documentReferences;
    }

}

