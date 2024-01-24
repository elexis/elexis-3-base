
package ch.elexis.global_inbox.ui.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.model.IInboxElementService;
import ch.elexis.core.documents.DocumentStore;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.findings.IDocumentReference;
import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.ui.e4.events.ElexisUiEventTopics;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.global_inbox.Preferences;
import ch.elexis.global_inbox.model.GlobalInboxEntry;
import ch.elexis.global_inbox.ui.Constants;
import ch.elexis.global_inbox.ui.GlobalInboxUtil;
import ch.elexis.omnivore.data.AutomaticBilling;

public class GlobalInboxEntryImportHandler {

	@Inject
	private IConfigService configService;
	@Inject
	private IContextService contextService;
	@Inject
	private DocumentStore documentStore;
	@Inject
	private IFindingsService findingService;
	@Inject
	private IInboxElementService inboxElementService;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) GlobalInboxEntry globalInboxEntry,
			IEventBroker eventBroker) {

		String title = globalInboxEntry.getTitle();
		IPatient patient = globalInboxEntry.getPatient();
		if (patient == null) {
			patient = contextService.getActivePatient().orElse(null);
		}
		IContact sender = globalInboxEntry.getSender();

		ICategory category = getCategoryOrDefault(globalInboxEntry.getCategory());

		File mainFile = globalInboxEntry.getMainFile();
		IDocument document = documentStore.createDocument(null, patient.getId(), mainFile.getName(),
				category.getName());
		document.setTitle(title);
		document.setMimeType(globalInboxEntry.getMimetype());
		document.setKeywords(globalInboxEntry.getKeywords());
		if (globalInboxEntry.getCreationDate() != null) {
			document.setCreated(globalInboxEntry.getCreationDate());
		} else {
			document.setCreated(new Date());
		}
		try (InputStream fin = new FileInputStream(mainFile)) {
			document = documentStore.saveDocument(document, fin);
		} catch (IOException | ElexisException e) {
			LoggerFactory.getLogger(getClass()).warn("Import error", e); //$NON-NLS-1$
			SWTHelper.showError("Import error", e.getMessage());
			documentStore.removeDocument(document);
			return;
		}

		IDocumentReference documentReference = findingService.create(IDocumentReference.class);
		documentReference.setPatientId(patient.getId());
		documentReference.setAuthorId(sender != null ? sender.getId() : null);
		documentReference.setDocument(document);
		try {
			findingService.saveFinding(documentReference);
		} catch (IllegalStateException e) {
			LoggerFactory.getLogger(getClass()).warn("Import error - could not save documentReference"); //$NON-NLS-1$
			SWTHelper.showError("Import error", "Could not save documentReference");
			documentStore.removeDocument(document);
			return;
		}

		// unload the document in preview, s.t. it can be deleted by the OS (Win)
		eventBroker.send(ElexisUiEventTopics.EVENT_PREVIEW_MIMETYPE_PDF, null);

		new GlobalInboxUtil().removeFiles(globalInboxEntry);

		// update document preview with imported document
		if (StringUtils.containsIgnoreCase(document.getMimeType(), "pdf")) { //$NON-NLS-1$
			eventBroker.send(ElexisUiEventTopics.EVENT_PREVIEW_MIMETYPE_PDF, document);
		}

		boolean automaticBilling = configService.getLocal(Preferences.PREF_AUTOBILLING, false);
		if (automaticBilling && AutomaticBilling.isEnabled()) {
			AutomaticBilling billing = new AutomaticBilling(document);
			billing.bill();
		}

		if (globalInboxEntry.isSendInfoTo()) {
			List<IMandator> notificationTo = globalInboxEntry.getInfoTo();
			for (IMandator mandator : notificationTo) {
				inboxElementService.createInboxElement(patient, mandator, document);
			}
		}

		eventBroker.send(Constants.EVENT_UI_REMOVE_AND_SELECT_NEXT, globalInboxEntry);
	}

	private ICategory getCategoryOrDefault(String category) {
		IDocumentStore defaultStore = documentStore.getDefaultDocumentStore();
		ICategory ret = defaultStore.getCategoryDefault();

		List<ICategory> categories = defaultStore.getCategories();
		for (ICategory iCategory : categories) {
			if (iCategory.getName().equals(category)) {
				ret = iCategory;
				break;
			}
		}
		return ret;
	}

	@CanExecute
	public boolean canExecute(@Named(IServiceConstants.ACTIVE_SELECTION) GlobalInboxEntry globalInboxEntry) {

		if (globalInboxEntry == null) {
			return false;
		}

		if (globalInboxEntry.getPatient() == null && !contextService.getActivePatient().isPresent()) {
			return false;
		}

		if (globalInboxEntry.getCategory() == null) {
			return false;
		}

		return true;
	}

}