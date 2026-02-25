package ch.elexis.tasks.integration.test.hl7import;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.Test;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.importer.div.tasks.BillLabResultOnCreationIdentifiedRunnable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ILabMapping;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.IIdentifiedRunnable.ReturnParameter;
import ch.elexis.core.model.tasks.IIdentifiedRunnable.RunContextParameter;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.rcp.utils.PlatformHelper;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.LabServiceHolder;
import ch.elexis.core.tasks.model.ITask;
import ch.elexis.core.tasks.model.ITaskDescriptor;
import ch.elexis.core.tasks.model.OwnerTaskNotification;
import ch.elexis.core.tasks.model.TaskState;
import ch.elexis.core.tasks.model.TaskTriggerType;
import ch.elexis.core.tasks.model.TaskTriggerTypeParameter;
import ch.elexis.tasks.integration.test.AllTests;
import ch.elexis.tasks.integration.test.internal.TaskServiceHolder;

public class Hl7ImporterTaskIntegrationTest {

	private IVirtualFilesystemService vfs = OsgiServiceUtil.getService(IVirtualFilesystemService.class).get();

	IVirtualFilesystemHandle hl7Target;
	IVirtualFilesystemHandle hl7Archived;

	@Test
	public void executionOnLocalFilesystem() throws Exception {

		Path tempDirectory = Files.createTempDirectory("hl7ImporterTest");
		tempDirectory.toFile().deleteOnExit();
		IVirtualFilesystemHandle tempDirectoryVfs = vfs.of(tempDirectory.toFile());
		//

		tempDirectoryVfs.mkdir();

		final IVirtualFilesystemHandle hl7 = vfs
				.of(new File(PlatformHelper.getBasePath("ch.elexis.tasks.integration.test"),
						"rsc/5083_LabCube_ABXMicrosEmi_20160217143956_198647.hl7"));
		final IVirtualFilesystemHandle pdf = vfs
				.of(new File(PlatformHelper.getBasePath("ch.elexis.tasks.integration.test"),
						"rsc/5083_LabCube_ABXMicrosEmi_20160217143956_198647.pdf"));
		hl7Target = tempDirectoryVfs.subFile(hl7.getName());
		final IVirtualFilesystemHandle pdfTarget = tempDirectoryVfs.subFile(pdf.getName());
		final IVirtualFilesystemHandle archiveDir = tempDirectoryVfs.subDir("archive").mkdir();
		hl7Archived = archiveDir.subFile(hl7.getName());
		final IVirtualFilesystemHandle pdfArchived = archiveDir.subFile(pdf.getName());

		Callable<Void> pushFiles = () -> {
			pdf.copyTo(pdfTarget);
			hl7.copyTo(hl7Target);
			return null;
		};

		performLocalFilesystemImport(AllTests.getUser(), tempDirectoryVfs.toString(), pushFiles);

		// import was successful, files was moved to archive
		System.out.println(tempDirectoryVfs.getAbsolutePath());
//		assertTrue(hl7Archived.exists());
//		assertTrue(pdfArchived.exists());
		archiveDir.delete();
		tempDirectoryVfs.delete();
		// new File(tempDirectory.toFile() + "/archive").delete();
	}

	/**
	 * Test the automatic import of hl7 files. This is realized by chaining tasks.
	 * The first task watches a given directory for changes, and on every file found
	 * it starts a subsequent task - the importer. A third task, bills created
	 * LabResults
	 *
	 * @param owner
	 *
	 * @param pushFiles
	 * @param url
	 * @throws Exception
	 */

	private void performLocalFilesystemImport(IUser owner, String urlString, Callable<Void> pushFiles)
			throws Exception {

		ITaskDescriptor hl7ImporterTaskDescriptor = initHl7ImporterTask(owner, urlString);
		ITaskDescriptor billLabResultsTaskDescriptor = initBillLabResultTask(owner);

		ITask hl7ImporterTask = pushFilesAndWait(pushFiles, hl7ImporterTaskDescriptor);
		String resultData = hl7ImporterTask.getResultEntryTyped(ReturnParameter.RESULT_DATA, String.class);
		assertTrue(resultData.startsWith("Result (OK)"));
		String url = hl7ImporterTask.getResultEntryTyped(ReturnParameter.STRING_URL, String.class);
//		assertEquals(hl7Archived.getAbsolutePath(), url);

		// 18 labResults + 1 pdf
		List<ILabResult> labResults = CoreModelServiceHolder.get().getQuery(ILabResult.class).execute();
		assertEquals(19, labResults.size());
		for (ILabResult iLabResult : labResults) {
			assertEquals(AllTests.getLaboratory().getId(), iLabResult.getOrigin().getId());
			assertNotNull(iLabResult.getItem());
			Optional<ILabMapping> mapping = LabServiceHolder.get().getLabMappingByContactAndItem(iLabResult.getOrigin(),
					iLabResult.getItem());
			assertTrue(mapping.isPresent());
		}

		// check that each was billed
		List<IBilled> billedList = CoreModelServiceHolder.get().getQuery(IBilled.class).execute();
		assertEquals(1, billedList.size());
		// 1371.00 fails, as no eal code is available
		for (IBilled billed : billedList) {
			assertEquals(1, billed.getAmount(), 0.01d);
		}

//		CoreModelServiceHolder.get().getQuery(ILabResult.class).execute().forEach(e->CoreModelServiceHolder.get().remove(e));
		String sqlScript = "UPDATE LABORWERTE SET DELETED = '1' WHERE DELETED = '0'";
		assertTrue(
				OsgiServiceUtil.getService(IElexisEntityManager.class).get().executeSQLScript("clearAll", sqlScript));

		pushFilesAndWait(pushFiles, hl7ImporterTaskDescriptor);

		billedList = CoreModelServiceHolder.get().getQuery(IBilled.class).execute();
		assertEquals(1, billedList.size());
		// 1371.00 fails, as no eal code is available
		for (IBilled billed : billedList) {
			assertEquals(2, billed.getAmount(), 0.01d);
			System.out.println(billed);
		}

		// assertEquals(true,
		// TaskServiceHolder.get().findLatestExecution(billLabResultsTaskDescriptor).isPresent());
		// // It was tried to bill EAL 1371.00 - but this will not succeed as the
		// required
		// // code is available in elexis-3-base only, the task was however executed and
		// // failed correctly
		// ITask billingTask =
		// TaskServiceHolder.get().findLatestExecution(billLabResultsTaskDescriptor).get();
		// assertEquals(TaskState.FAILED, billingTask.getState());
		// String result =
		// (String)
		// billingTask.getResult().get(ReturnParameter.FAILED_TASK_EXCEPTION_MESSAGE);
		// assertTrue(result.contains("EAL tarif [1371.00] does not exist"));

		// TODO test fail message
		// TODO partial result?

		TaskServiceHolder.get().removeTaskDescriptor(billLabResultsTaskDescriptor);
		TaskServiceHolder.get().removeTaskDescriptor(hl7ImporterTaskDescriptor);
	}

	/**
	 *
	 * @param pushFiles
	 * @param watcherTaskDescriptor
	 * @param hl7ImporterTaskDescriptor
	 * @return the latest execution of the hl7importer task
	 * @throws Exception
	 */
	private ITask pushFilesAndWait(Callable<Void> pushFiles, ITaskDescriptor hl7ImporterTaskDescriptor)
			throws Exception {

		// cleanup previous entry for multiple runs
		TaskServiceHolder.get().findLatestExecution(hl7ImporterTaskDescriptor)
				.ifPresent(t -> CoreModelServiceHolder.get().remove(t));

		pushFiles.call();

		// add a hl7 file with accompanying pdf to the directory
		Awaitility.await().atMost(10, TimeUnit.SECONDS)
				.until(() -> TaskServiceHolder.get().findLatestExecution(hl7ImporterTaskDescriptor).isPresent());
		Awaitility.await().atMost(10, TimeUnit.SECONDS)
				.until(() -> TaskServiceHolder.get().findLatestExecution(hl7ImporterTaskDescriptor).get().isFinished());
		assertEquals(TaskState.COMPLETED,
				TaskServiceHolder.get().findLatestExecution(hl7ImporterTaskDescriptor).get().getState());

		return TaskServiceHolder.get().findLatestExecution(hl7ImporterTaskDescriptor).orElse(null);
	}

	private ITaskDescriptor initBillLabResultTask(IUser activeUser) throws TaskException {
		IIdentifiedRunnable runnable = TaskServiceHolder.get()
				.instantiateRunnableById(BillLabResultOnCreationIdentifiedRunnable.RUNNABLE_ID);
		assertNotNull(runnable);

		ITaskDescriptor taskDescriptor = TaskServiceHolder.get().createTaskDescriptor(runnable);
		taskDescriptor.setOwner(activeUser);
		taskDescriptor.setTriggerType(TaskTriggerType.SYSTEM_EVENT);
		taskDescriptor.setTriggerParameter("topic", ElexisEventTopics.PERSISTENCE_EVENT_CREATE);
		taskDescriptor.setTriggerParameter(ElexisEventTopics.PROPKEY_CLASS, "ch.elexis.data.LabResult");
		taskDescriptor.setTriggerParameter("origin", "self");
		TaskServiceHolder.get().setActive(taskDescriptor, true);

		return taskDescriptor;
	}

	private ITaskDescriptor initHl7ImporterTask(IUser activeUser, String url) throws TaskException {
		// create hl7importer taskdescriptor
		IIdentifiedRunnable hl7ImporterRunnable = TaskServiceHolder.get().instantiateRunnableById("hl7importer");
		assertNotNull(hl7ImporterRunnable);

		ITaskDescriptor hl7ImporterTaskDescriptor = TaskServiceHolder.get().createTaskDescriptor(hl7ImporterRunnable);
		hl7ImporterTaskDescriptor.setOwner(activeUser);
		hl7ImporterTaskDescriptor.setSingleton(true);
		hl7ImporterTaskDescriptor.setTriggerType(TaskTriggerType.FILESYSTEM_CHANGE);
		hl7ImporterTaskDescriptor.setTriggerParameter(RunContextParameter.STRING_URL, url);
		hl7ImporterTaskDescriptor.setTriggerParameter(TaskTriggerTypeParameter.FILESYSTEM_CHANGE.FILE_EXTENSION_FILTER,
				"hl7");
		hl7ImporterTaskDescriptor.setReferenceId("hl7Importer_a");
		hl7ImporterTaskDescriptor.setOwnerNotification(OwnerTaskNotification.WHEN_FINISHED_FAILED);
		TaskServiceHolder.get().setActive(hl7ImporterTaskDescriptor, true);
		return hl7ImporterTaskDescriptor;
	}

}
