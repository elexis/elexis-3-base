
package ch.elexis.global_inbox.ui.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.model.IInboxElementService;
import ch.elexis.core.documents.DocumentStore;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.findings.IDocumentReference;
import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.global_inbox.Preferences;
import ch.elexis.global_inbox.model.GlobalInboxEntry;
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
	public void execute(
		@Named(IServiceConstants.ACTIVE_SELECTION) GlobalInboxEntry globalInboxEntry){
		
		String title = globalInboxEntry.getTitle();
		IPatient patient = globalInboxEntry.getPatient();
		if (patient == null) {
			patient = contextService.getActivePatient().orElse(null);
		}
		String category = globalInboxEntry.getCategory();
		String senderId = globalInboxEntry.getSenderId();
		
		File mainFile = globalInboxEntry.getMainFile();
		IDocument document =
			documentStore.createDocument(null, patient.getId(), mainFile.getName(), category);
		document.setTitle(title);
		try (InputStream fin = new FileInputStream(mainFile)) {
			document = documentStore.saveDocument(document, fin);
		} catch (IOException | ElexisException e) {
			LoggerFactory.getLogger(getClass()).warn("Import error", e);
			SWTHelper.showError("Import error", e.getMessage());
		}
		
		IDocumentReference documentReference = findingService.create(IDocumentReference.class);
		documentReference.setPatientId(patient.getId());
		documentReference.setAuthorId(senderId);
		documentReference.setDocument(document);
		findingService.saveFinding(documentReference);
		
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
		
	}
	
	@CanExecute
	public boolean canExecute(
		@Named(IServiceConstants.ACTIVE_SELECTION) GlobalInboxEntry globalInboxEntry){
		
		if (globalInboxEntry == null) {
			return false;
		}
		
		if (globalInboxEntry.getPatient() == null
			&& !contextService.getActivePatient().isPresent()) {
			return false;
		}
		
		if (globalInboxEntry.getCategory() == null) {
			return false;
		}
		
		return true;
	}
	
}