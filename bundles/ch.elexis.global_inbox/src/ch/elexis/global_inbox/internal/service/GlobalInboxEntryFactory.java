package ch.elexis.global_inbox.internal.service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IStoreToStringService;
import ch.elexis.global_inbox.Preferences;
import ch.elexis.global_inbox.model.GlobalInboxEntry;
import ch.elexis.global_inbox.ui.GlobalInboxUtil;

@SuppressWarnings("rawtypes")
@Component(immediate = true)
public class GlobalInboxEntryFactory {
	
	private static IStoreToStringService storeToStringService;
	private static IModelService modelService;
	private static IConfigService configService;
	
	private static List<Function> extensionFileHandlers = new ArrayList<Function>();
	
	@Activate
	public void activate(){
		String giDirSetting = configService.getLocal(Preferences.PREF_DIR, "NOTSET");
		if ("NOTSET".equals(giDirSetting)) {
			File giDir = new File(CoreHub.getWritableUserDir(), "GlobalInbox");
			boolean created = giDir.mkdir();
			if (created) {
				CoreHub.localCfg.set(Preferences.PREF_DIR, giDir.getAbsolutePath());
			}
		}
	}
	
	@Reference
	public void setStoreToStringService(IStoreToStringService storeToStringService){
		GlobalInboxEntryFactory.storeToStringService = storeToStringService;
	}
	
	@Reference
	public void setConfigService(IConfigService configService){
		GlobalInboxEntryFactory.configService = configService;
	}
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	public void setModelService(IModelService modelService){
		GlobalInboxEntryFactory.modelService = modelService;
	}
	
	@Reference(target = "(service.name=ch.elexis.global_inbox.extensionfilehandler)", cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void setExtensionFileHandler(Function extensionFileHandler){
		GlobalInboxEntryFactory.extensionFileHandlers.add(extensionFileHandler);
	}
	
	public void unsetExtensionFileHandler(Function extensionFileHandler){
		GlobalInboxEntryFactory.extensionFileHandlers.remove(extensionFileHandler);
	}
	
	@SuppressWarnings("unchecked")
	public static GlobalInboxEntry createEntry(File mainFile, File[] extensionFiles){
		
		GlobalInboxEntry globalInboxEntry = new GlobalInboxEntry(mainFile, extensionFiles);
		String category = GlobalInboxUtil.getCategory(mainFile);
		globalInboxEntry.setCategory(category);
		globalInboxEntry
			.setSendInfoTo(configService.getLocal(Preferences.PREF_INFO_IN_INBOX, false));
		return globalInboxEntry;
		
	}
	
	public static GlobalInboxEntry populateExtensionInformation(GlobalInboxEntry globalInboxEntry){
		File[] extensionFiles = globalInboxEntry.getExtensionFiles();
		for (File file : extensionFiles) {
			String absolutePath = file.getAbsolutePath();
			for (Function handler : extensionFileHandlers) {
				Map<String, Object> result = (Map<String, Object>) handler.apply(absolutePath);
				if (result != null) {
					integrateAdditionalInformation(result, globalInboxEntry);
				}
			}
		}
		return globalInboxEntry;
	}
	
	private static void integrateAdditionalInformation(Map<String, Object> result,
		GlobalInboxEntry gie){
		
		Object creationDateCandidates = result.get("creationDateCandidates");
		if (creationDateCandidates instanceof List) {
			@SuppressWarnings("unchecked")
			List<LocalDate> candidates = (List<LocalDate>) creationDateCandidates;
			if (candidates != null && !candidates.isEmpty()) {
				gie.setCreationDateCandidates(candidates);
			}
		}
		
		Object patientCandidates = result.get("patientCandidates");
		if (patientCandidates instanceof List) {
			@SuppressWarnings("unchecked")
			List<String> patientCandidatesSts = (List<String>) patientCandidates;
			if (patientCandidatesSts != null && !patientCandidatesSts.isEmpty()) {
				List<Identifiable> _patients = patientCandidatesSts
					.stream().map(storeToString -> storeToStringService
						.loadFromString(storeToString).orElse(null))
					.filter(Objects::nonNull).collect(Collectors.toList());
				List<IPatient> patients = _patients.stream()
					.map(i -> modelService.load(i.getId(), IPatient.class).orElse(null))
					.collect(Collectors.toList());
				gie.setPatientCandidates(patients);
			}
		}
		
		Object senderCandidates = result.get("senderCandidates");
		if (senderCandidates instanceof List) {
			@SuppressWarnings("unchecked")
			List<String> senderCandidatesSts = (List<String>) senderCandidates;
			if (senderCandidatesSts != null && !senderCandidatesSts.isEmpty()) {
				List<Identifiable> _senders = senderCandidatesSts
					.stream().map(storeToString -> storeToStringService
						.loadFromString(storeToString).orElse(null))
					.filter(Objects::nonNull).collect(Collectors.toList());
				List<IContact> senders = _senders.stream()
					.map(i -> modelService.load(i.getId(), IContact.class).orElse(null))
					.collect(Collectors.toList());
				gie.setSenderCandidates(senders);
			}
		}
		
	}
	
}
