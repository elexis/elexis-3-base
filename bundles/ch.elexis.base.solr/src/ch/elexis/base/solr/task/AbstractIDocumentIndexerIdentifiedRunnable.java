package ch.elexis.base.solr.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.Builder;
import org.apache.solr.common.SolrException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;

import ch.elexis.base.solr.internal.bean.IDocumentBean;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.SingleIdentifiableTaskResult;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.time.TimeUtil;
import ch.elexis.core.types.DocumentStatus;

public abstract class AbstractIDocumentIndexerIdentifiedRunnable
		extends AbstractIndexerIdentifiedRunnable {
	
	@Override
	public Map<String, Serializable> run(Map<String, Serializable> runContext,
		IProgressMonitor progressMonitor, Logger logger) throws TaskException{
		
		super.init(runContext);
		
		List<IDocument> indexedList = new ArrayList<IDocument>();
		List<IDocument> indexRemovedList = new ArrayList<IDocument>();
		
		Builder solrClientBuilder = super.getSolrClientBuilder();
		String currentDocumentId = null;
		try (HttpSolrClient solr = solrClientBuilder.build()) {
			// is the server alive, else early exit
			checkResponse(solr.ping(getSolrCore()));
			
			List<?> docHandles = getDocuments();
			SubMonitor subMonitor = SubMonitor.convert(progressMonitor, docHandles.size());
			for (Object id : docHandles) {
				currentDocumentId = id.toString();
				subMonitor.worked(1);
				// assert document exists
				IDocument document = loadDocument(currentDocumentId);
				if (document == null) {
					logger.warn("[{}] not loadable , skipping", currentDocumentId);
					failures.add(new SingleIdentifiableTaskResult(currentDocumentId,
						"not loadable (null), skipping"));
					continue;
				}
				
				// is now deleted, but was indexed - remove from index, reset index flag
				if (new HashSet<DocumentStatus>(document.getStatus())
					.contains(DocumentStatus.INDEXED) && document.isDeleted()) {
					
					try {
						checkResponse(solr.deleteById(getSolrCore(), currentDocumentId));
						document.setStatus(DocumentStatus.INDEXED, false);
						getModelService().save(document);
						indexRemovedList.add(document);
					} catch (SolrServerException sse) {
						logger.warn("[{}] could not be deleted from solr index", currentDocumentId,
							sse);
						failures.add(new SingleIdentifiableTaskResult(currentDocumentId,
							"could not be deleted from solr index"));
					}
					continue;
				}
				
				// assert has patient
				String patientId =
					document.getPatient() != null ? document.getPatient().getId() : null;
				if (patientId == null) {
					logger.warn("[{}] no assocatied patient, skipping", currentDocumentId);
					failures.add(new SingleIdentifiableTaskResult(currentDocumentId,
						"no assocatied patient, skipping"));
					continue;
				}
				
				// assert has content
				byte[] content;
				try (InputStream is = document.getContent()) {
					if (is == null) {
						logger.info("[{}] content is null, skipping", document.getId());
						failures.add(new SingleIdentifiableTaskResult(currentDocumentId,
							"content is null, skipping"));
						continue;
					}
					content = IOUtils.toByteArray(is);
				}
				
				String[] solrCellData = null;
				if (content.length == 0) {
					logger.info("[{}] content length is 0, marking indexed", document.getId());
					failures.add(new SingleIdentifiableTaskResult(currentDocumentId,
						"content length is 0, marking indexed "));
				} else {
					try {
						solrCellData =
							getUtil().performSolrCellRequest(solr, getSolrCore(), content);
					} catch (SolrException e) {
						if (e.getMessage().contains("EncryptedDocumentException")) {
							logger.warn("[{}] " + e.getMessage() + ", marking indexed",
								document.getId());
							failures.add(new SingleIdentifiableTaskResult(document.getId(),
								e.getMessage() + ", marking indexed"));
						} else {
							logger.error("[{}] " + e.getMessage(), id);
							throw e;
						}
					}
				}
				
				document.setStatus(DocumentStatus.INDEXED, true);
				boolean save = getModelService().save(document);
				if (!save) {
					logger.warn("[{}] could not be saved, see logs", currentDocumentId);
					failures.add(new SingleIdentifiableTaskResult(currentDocumentId,
						"could not be saved, see logs"));
					continue;
				}
				
				if (solrCellData != null) {
					IDocumentBean documentBean = new IDocumentBean();
					documentBean.setId(document.getId());
					LocalDate localDate = TimeUtil.toLocalDate(document.getCreated());
					String date =
						(localDate != null) ? TimeUtil.formatSafe(localDate) : "??.??.????";
					String title =
						StringUtils.isNotBlank(document.getTitle()) ? document.getTitle().trim()
								: document.getKeywords();
					documentBean.setLabel(date + " - " + title);
					documentBean.setPatientId(patientId);
					documentBean.setLastUpdate(document.getLastupdate());
					documentBean.setCreationDate(document.getCreated());
					documentBean.setContent(solrCellData[0]);
					
					try {
						checkResponse(solr.addBean(getSolrCore(), documentBean));
						indexedList.add(document);
					} catch (SolrServerException sse) {
						logger.warn("[{}] could not add to solr index, unsetting index status",
							currentDocumentId, sse);
						failures.add(new SingleIdentifiableTaskResult(currentDocumentId,
							"could not add to solr index, unsetting index status"));
						document.setStatus(DocumentStatus.INDEXED, false);
						getModelService().save(document);
						continue;
					}
				}
				
				if (subMonitor.isCanceled()) {
					logger.info("Task is cancelled");
					break;
				}
				
				boolean isOverMaxRunTime = super.checkIsOverMaxRunTime();
				if (isOverMaxRunTime) {
					break;
				}
			}
			
			try {
				checkResponse(solr.commit(getSolrCore()));
			} catch (SolrServerException sse) {
				// all changes lost - reset the flags!
				indexedList.stream().forEach(letter -> {
					letter.setStatus(DocumentStatus.INDEXED, false);
					getModelService().save(letter);
				});
				indexRemovedList.stream().forEach(letter -> {
					letter.setStatus(DocumentStatus.INDEXED, true);
					getModelService().save(letter);
				});
				throw (sse);
			}
			
		} catch (IOException | SolrServerException e) {
			throw new TaskException(TaskException.EXECUTION_ERROR, currentDocumentId, e);
		}
		
		resultMap.put(IIdentifiedRunnable.ReturnParameter.RESULT_DATA,
			indexedList.size() + " indexed / " + indexRemovedList.size() + " index removed");
		if (failures.size() > 0) {
			resultMap.put(IIdentifiedRunnable.ReturnParameter.MARKER_WARN, null);
			resultMap.put(IIdentifiedRunnable.ReturnParameter.RESULT_CLASS,
				SingleIdentifiableTaskResult.class.getName());
			resultMap.put(IIdentifiedRunnable.ReturnParameter.RESULT_DATA_LIST, failures);
		}
		if (indexedList.size() == 0 && indexRemovedList.size() == 0 && failures.size() == 0) {
			resultMap.put(IIdentifiedRunnable.ReturnParameter.MARKER_DO_NOT_PERSIST, true);
		}
		
		return resultMap;
	}
	
	protected abstract String getSolrCore();
	
	protected abstract IModelService getModelService();
	
	protected abstract IDocument loadDocument(String string);
	
	protected abstract List<?> getDocuments();
	
}
