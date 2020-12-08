package ch.elexis.base.solr.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.Builder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;

import ch.elexis.base.solr.internal.bean.IDocumentBean;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.SingleIdentifiableTaskResult;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.IModelService;
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
		try (HttpSolrClient solr = solrClientBuilder.build()) {
			// is the server alive, else early exit
			checkResponse(solr.ping(getSolrCore()));
			
			List<?> docHandles = getDocuments();
			SubMonitor subMonitor = SubMonitor.convert(progressMonitor, docHandles.size());
			for (Object id : docHandles) {
				subMonitor.worked(1);
				// assert document exists
				IDocument document = loadDocument(id.toString());
				if (document == null) {
					logger.warn("IDocument [{}] is not loadable , skipping", id.toString());
					failures.add(new SingleIdentifiableTaskResult(id.toString(),
						"IDocument is not loadable (null), skipping"));
					continue;
				}
				
				// is now deleted, but was indexed - remove from index, reset index flag
				if (new HashSet<DocumentStatus>(document.getStatus())
					.contains(DocumentStatus.INDEXED) && document.isDeleted()) {
					
					try {
						checkResponse(solr.deleteById(getSolrCore(), id.toString()));
						document.setStatus(DocumentStatus.INDEXED, false);
						getModelService().save(document);
						indexRemovedList.add(document);
					} catch (SolrServerException sse) {
						logger.warn("IDocument [{}] could not be deleted from solr index",
							id.toString(), sse);
						failures.add(new SingleIdentifiableTaskResult(id.toString(),
							"IDocument could not be deleted from solr index"));
					}
					continue;
				}
				
				// assert has content
				byte[] content;
				try (InputStream is = document.getContent()) {
					if (is == null) {
						logger.info("IDocument [{}] content is null, skipping", document.getId());
						failures.add(new SingleIdentifiableTaskResult(id.toString(),
							"IDocument content is null, skipping"));
						continue;
					}
					content = IOUtils.toByteArray(is);
				}
				
				// assert has patient
				String patientId =
					document.getPatient() != null ? document.getPatient().getId() : null;
				if (patientId == null) {
					logger.warn("IDocument [{}] has no assocatied patient, skipping",
						id.toString());
					failures.add(new SingleIdentifiableTaskResult(id.toString(),
						"IDocument has no assocatied patient, skipping"));
					continue;
				}
				
				String[] solrCellData =
					getUtil().performSolrCellRequest(solr, getSolrCore(), content);
				if (StringUtils.isEmpty(solrCellData[0])) {
					// TODO what to do with the metadata?
					logger.info("IDocument [{}] could not extract textual content, skipping",
						document.getId());
					failures.add(new SingleIdentifiableTaskResult(id.toString(),
						"IDocument could not extract textual content, skipping"));
					continue;
				}
				
				document.setStatus(DocumentStatus.INDEXED, true);
				boolean save = getModelService().save(document);
				if (!save) {
					logger.warn("IDocument [{}] could not be saved, see logs", id.toString());
					failures.add(new SingleIdentifiableTaskResult(id.toString(),
						"IDocument could not be saved, see logs"));
					continue;
				}
				
				IDocumentBean documentBean = new IDocumentBean();
				documentBean.setId(document.getId());
				documentBean.setLabel(document.getTitle());
				documentBean.setPatientId(patientId);
				documentBean.setLastUpdate(document.getLastupdate());
				documentBean.setCreationDate(document.getCreated());
				documentBean.setContent(solrCellData[0]);
				
				try {
					checkResponse(solr.addBean(getSolrCore(), documentBean));
					indexedList.add(document);
				} catch (SolrServerException sse) {
					logger.warn("IDocument [{}] could not add to solr index", id.toString(), sse);
					failures.add(new SingleIdentifiableTaskResult(id.toString(),
						"IDocument could not add to solr index"));
					// unset the indexed flag
					document.setStatus(DocumentStatus.INDEXED, false);
					getModelService().save(document);
					continue;
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
			throw new TaskException(TaskException.EXECUTION_ERROR, e);
		}
		
		resultMap.put(IIdentifiedRunnable.ReturnParameter.RESULT_DATA,
			indexedList.size() + " indexed / " + indexRemovedList.size() + " index removed");
		if (failures.size() > 0) {
			resultMap.put(IIdentifiedRunnable.ReturnParameter.MARKER_WARN, null);
			resultMap.put(IIdentifiedRunnable.ReturnParameter.RESULT_CLASS,
				SingleIdentifiableTaskResult.class.getName());
			resultMap.put(IIdentifiedRunnable.ReturnParameter.RESULT_DATA_LIST, failures);
		}
		
		return resultMap;
	}
	
	protected abstract String getSolrCore();
	
	protected abstract IModelService getModelService();
	
	protected abstract IDocument loadDocument(String string);
	
	protected abstract List<?> getDocuments();
	
}
