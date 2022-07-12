package ch.elexis.base.solr.task;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.elexis.base.solr.internal.SolrConstants;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IDocumentLetter;
import ch.elexis.core.services.IModelService;

public class LetterIndexerIdentifiedRunnable extends AbstractIDocumentIndexerIdentifiedRunnable {

	public static final String RUNNABLE_ID = "solrLetterIndexer"; //$NON-NLS-1$
	public static final String DESCRIPTION = "Index letters into SOLR (in batches, newest first strategy)";

	private IModelService coreModelService;

	public LetterIndexerIdentifiedRunnable(IModelService coreModelService) {
		this.coreModelService = coreModelService;
	}

	@Override
	public String getId() {
		return RUNNABLE_ID;
	}

	@Override
	public String getLocalizedDescription() {
		return DESCRIPTION;
	}

	// we load either
	// letters that are NOT DELETED, do NOT have DocumentStatus#INDEXED and a
	// PATIENTID -> to add to solr
	// letters that are DocumentStatus#INDEXED and DELETED -> to remove from solr
	private final String QUERY = "SELECT ID FROM BRIEFE WHERE (!(DOCUMENT_STATUS & 2) AND DELETED = '0' AND PATIENTID IS NOT NULL AND PATIENTID <> '') OR ((DOCUMENT_STATUS & 2) AND DELETED='1') ORDER BY lastUpdate DESC LIMIT 1000"; //$NON-NLS-1$

	@Override
	protected List<?> getDocuments() {
		return coreModelService.getNativeQuery(QUERY).executeWithParameters(Collections.emptyMap())
				.collect(Collectors.toList());
	}

	@Override
	protected IDocument loadDocument(String id) {
		return coreModelService.load(id, IDocumentLetter.class, true, true).orElse(null);

	}

	@Override
	protected String getSolrCore() {
		return SolrConstants.CORE_LETTERS;
	}

	@Override
	protected IModelService getModelService() {
		return coreModelService;
	}

}
