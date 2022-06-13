package ch.elexis.base.solr.task;

import ch.elexis.core.eenv.IElexisEnvironmentService;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.tasks.model.ITaskDescriptor;
import ch.elexis.core.tasks.model.ITaskService;
import ch.elexis.core.tasks.model.TaskTriggerType;

public class SolrIndexerIdentifiedRunnableTaskDescriptor {

	public static final String SOLR_ENCOUNTER_INDEXER_TASK_DESCRIPTOR_REFERENCE_ID = "solrIndexerEncounters";
	public static final String SOLR_LETTER_INDEXER_TASK_DESCRIPTOR_REFERENCE_ID = "solrIndexerLetters";
	public static final String SOLR_DOCUMENT_INDEXER_TASK_DESCRIPTOR_REFERENCE_ID = "solrIndexerDocuments";

	public static ITaskDescriptor getOrCreateForEncounter(ITaskService taskService) throws TaskException {
		ITaskDescriptor taskDescriptor = taskService
				.findTaskDescriptorByIdOrReferenceId(SOLR_ENCOUNTER_INDEXER_TASK_DESCRIPTOR_REFERENCE_ID).orElse(null);
		if (taskDescriptor == null) {
			taskDescriptor = taskService.createTaskDescriptor(new EncounterIndexerIdentifiedRunnable(null, null));
			taskDescriptor.setReferenceId(SOLR_ENCOUNTER_INDEXER_TASK_DESCRIPTOR_REFERENCE_ID);
			taskDescriptor.setTriggerType(TaskTriggerType.CRON);
			taskDescriptor.setRunner(IElexisEnvironmentService.ES_STATION_ID_DEFAULT);
			// At second :7, every 10 minutes starting at minute :00, of every hour
			taskDescriptor.setTriggerParameter("cron", "7 0/10 * * * ?");

			taskService.saveTaskDescriptor(taskDescriptor);
		}
		return taskDescriptor;
	}

	public static ITaskDescriptor getOrCreateForLetter(ITaskService taskService) throws TaskException {
		ITaskDescriptor taskDescriptor = taskService
				.findTaskDescriptorByIdOrReferenceId(SOLR_LETTER_INDEXER_TASK_DESCRIPTOR_REFERENCE_ID).orElse(null);
		if (taskDescriptor == null) {
			taskDescriptor = taskService.createTaskDescriptor(new LetterIndexerIdentifiedRunnable(null));
			taskDescriptor.setReferenceId(SOLR_LETTER_INDEXER_TASK_DESCRIPTOR_REFERENCE_ID);
			taskDescriptor.setTriggerType(TaskTriggerType.CRON);
			taskDescriptor.setRunner(IElexisEnvironmentService.ES_STATION_ID_DEFAULT);
			// At second :17, every 10 minutes starting at minute :00, of every hour
			taskDescriptor.setTriggerParameter("cron", "17 0/10 * * * ?");

			taskService.saveTaskDescriptor(taskDescriptor);
		}
		return taskDescriptor;
	}

	public static ITaskDescriptor getOrCreateForDocument(ITaskService taskService) throws TaskException {
		ITaskDescriptor taskDescriptor = taskService
				.findTaskDescriptorByIdOrReferenceId(SOLR_DOCUMENT_INDEXER_TASK_DESCRIPTOR_REFERENCE_ID).orElse(null);
		if (taskDescriptor == null) {
			taskDescriptor = taskService.createTaskDescriptor(new DocumentIndexerIdentifiedRunnable(null));
			taskDescriptor.setReferenceId(SOLR_DOCUMENT_INDEXER_TASK_DESCRIPTOR_REFERENCE_ID);
			taskDescriptor.setTriggerType(TaskTriggerType.CRON);
			taskDescriptor.setRunner(IElexisEnvironmentService.ES_STATION_ID_DEFAULT);
			// At second :27, every 10 minutes starting at minute :00, of every hour
			taskDescriptor.setTriggerParameter("cron", "27 0/10 * * * ?");

			taskService.saveTaskDescriptor(taskDescriptor);
		}
		return taskDescriptor;
	}

}
