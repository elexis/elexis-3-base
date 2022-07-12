package ch.elexis.base.solr.task;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.elexis.base.solr.internal.SolrConstants;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.services.IModelService;
import ch.elexis.omnivore.model.IDocumentHandle;

public class DocumentIndexerIdentifiedRunnable extends AbstractIDocumentIndexerIdentifiedRunnable {

	public static final String RUNNABLE_ID = "solrDocumentIndexer"; //$NON-NLS-1$
	public static final String DESCRIPTION = "Index omnivore documents into SOLR"; //$NON-NLS-1$

	private IModelService omnivoreModelService;

	public DocumentIndexerIdentifiedRunnable(IModelService omnivoreModelService) {
		this.omnivoreModelService = omnivoreModelService;
	}

	@Override
	public String getId() {
		return RUNNABLE_ID;
	}

	@Override
	public String getLocalizedDescription() {
		return DESCRIPTION;
	}

	private final String QUERY = "SELECT ID FROM CH_ELEXIS_OMNIVORE_DATA WHERE !(DOCUMENT_STATUS & 2) AND (DOCUMENT_STATUS & 1) AND PatID IS NOT NULL ORDER BY lastUpdate DESC LIMIT 1000"; //$NON-NLS-1$

	@Override
	protected String getSolrCore() {
		return SolrConstants.CORE_DOCUMENTS;
	}

	@Override
	protected IModelService getModelService() {
		return omnivoreModelService;
	}

	@Override
	protected IDocument loadDocument(String id) {
		return omnivoreModelService.load(id, IDocumentHandle.class, true, true).orElse(null);
	}

	@Override
	protected List<?> getDocuments() {
		return omnivoreModelService.getNativeQuery(QUERY).executeWithParameters(Collections.emptyMap())
				.collect(Collectors.toList());
	}

}
