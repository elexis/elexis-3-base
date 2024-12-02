package ch.elexis.mednet.webapi.core.fhir.resources.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonManipulator {

	private ObjectMapper mapper;

	public JsonManipulator() {
		this.mapper = new ObjectMapper();
	}

	/**
	 * Passt das JSON an, indem die Reihenfolge der Felder in allen
	 * DocumentReference-Ressourcen und deren Attachment-Objekten geändert wird.
	 *
	 * @param jsonString Das ursprüngliche JSON als String
	 * @return Das angepasste JSON als String
	 * @throws IOException Wenn ein Fehler beim Parsen auftritt oder die erwartete
	 *                     Struktur fehlt
	 */
	public String adjustDocumentReference(String jsonString) throws IOException {
		JsonNode rootNode = mapper.readTree(jsonString);
		if (rootNode.has("resourceType") && "Bundle".equals(rootNode.get("resourceType").asText())) {
			JsonNode entryArray = rootNode.path("entry");
			if (entryArray.isArray()) {
				for (JsonNode entry : entryArray) {
					processDocumentReference(entry);
				}
	        }
		} else {
			processDocumentReference(rootNode);
	    }
		String adjustedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

		return adjustedJson;
	}

	

	private void processDocumentReference(JsonNode node) {
		JsonNode resource = node.path("resourceType").asText().equals("DocumentReference") ? node
				: node.path("resource");
		if (resource.isMissingNode() || !"DocumentReference".equals(resource.path("resourceType").asText())) {
			return;
		}

		String id = resource.has("id") ? resource.get("id").asText() : null;
		JsonNode meta = resource.get("meta");
		String status = resource.has("status") ? resource.get("status").asText() : null;
		JsonNode type = resource.get("type");
		JsonNode category = resource.get("category");
		JsonNode subject = resource.get("subject");
		JsonNode author = resource.get("author");
		JsonNode indexed = resource.get("indexed");
		JsonNode content = resource.get("content");

		ObjectNode newDocumentReference = mapper.createObjectNode();
		newDocumentReference.put("resourceType", "DocumentReference");
		if (id != null) {
			newDocumentReference.put("id", id);
		}
		if (meta != null && !meta.isMissingNode()) {
			newDocumentReference.set("meta", meta);
		}
		if (status != null) {
			newDocumentReference.put("status", status);
		}
		if (type != null && !type.isMissingNode()) {
			newDocumentReference.set("type", type);
		}
		if (category != null && !category.isMissingNode()) {
			newDocumentReference.set("category", category);
		}
		if (subject != null && !subject.isMissingNode()) {
			newDocumentReference.set("subject", subject);
		}
		if (author != null && !author.isMissingNode()) {
			newDocumentReference.set("author", author);
		}
		if (indexed != null && !indexed.isMissingNode()) {
			newDocumentReference.set("indexed", indexed);
		}

		if (content != null && content.isArray()) {
			ArrayNode newContentArray = mapper.createArrayNode();
			for (JsonNode contentItem : content) {
				JsonNode attachment = contentItem.get("attachment");
				if (attachment != null && !attachment.isMissingNode()) {
					String contentType = attachment.has("contentType") ? attachment.get("contentType").asText() : null;
					String data = attachment.has("data") ? attachment.get("data").asText() : null;
					String url = attachment.has("url") ? attachment.get("url").asText() : null;
					String titleAtt = attachment.has("title") ? attachment.get("title").asText() : null;
					String creation = attachment.has("creation") ? attachment.get("creation").asText() : null;
					ObjectNode newAttachment = mapper.createObjectNode();
					if (contentType != null) {
						newAttachment.put("contentType", contentType);
					}
					if (data != null) {
						newAttachment.put("data", data);
					}
					if (url != null) {
						newAttachment.put("url", url);
					}
					if (titleAtt != null) {
						newAttachment.put("title", titleAtt);
					}
					if (creation != null) {
						newAttachment.put("creation", creation);
					}
					ObjectNode newContentItem = mapper.createObjectNode();
					newContentItem.set("attachment", newAttachment);
					newContentArray.add(newContentItem);
				} else {
					newContentArray.add(contentItem);
				}
			}
			newDocumentReference.set("content", newContentArray);
		}
		if (node.has("resource")) {
			((ObjectNode) node).set("resource", newDocumentReference);
		} else {
			((ObjectNode) node).removeAll();
			((ObjectNode) node).setAll(newDocumentReference);
		}
	}
}
