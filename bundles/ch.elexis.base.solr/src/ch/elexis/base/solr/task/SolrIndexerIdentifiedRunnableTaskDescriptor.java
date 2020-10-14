package ch.elexis.base.solr.task;

import static com.cronutils.model.field.expression.FieldExpressionFactory.always;
import static com.cronutils.model.field.expression.FieldExpressionFactory.on;
import static com.cronutils.model.field.expression.FieldExpressionFactory.questionMark;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;

import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.tasks.model.ITaskDescriptor;
import ch.elexis.core.tasks.model.ITaskService;
import ch.elexis.core.tasks.model.TaskTriggerType;

public class SolrIndexerIdentifiedRunnableTaskDescriptor {
	
	public static final String SOLR_POPULATE_TASK_DESCRIPTOR_REFERENCE_ID = "solrIndexer";
	
	public static ITaskDescriptor getOrCreate(ITaskService taskService) throws TaskException{
		ITaskDescriptor taskDescriptor = taskService
			.findTaskDescriptorByIdOrReferenceId(SOLR_POPULATE_TASK_DESCRIPTOR_REFERENCE_ID)
			.orElse(null);
		if (taskDescriptor == null) {
			taskDescriptor = taskService
				.createTaskDescriptor(new SolrIndexerIdentifiedRunnable(null, null, null));
			taskDescriptor.setReferenceId(SOLR_POPULATE_TASK_DESCRIPTOR_REFERENCE_ID);
			
			taskDescriptor.setTriggerType(TaskTriggerType.CRON);
			
			CronBuilder cron =
				CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
			String cronString =
				cron.withSecond(on(0)).withMinute(on(0)).withHour(on(9)).withDoM(questionMark())
					.withMonth(always()).withDoW(always()).withYear(always()).instance().asString();
			taskDescriptor.setTriggerParameter("cron", cronString);
			
			taskService.saveTaskDescriptor(taskDescriptor);
		}
		return taskDescriptor;
	}
	
}
