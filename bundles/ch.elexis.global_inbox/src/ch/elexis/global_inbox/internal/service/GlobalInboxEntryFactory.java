package ch.elexis.global_inbox.internal.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.preferences.PreferencesUtil;
import ch.elexis.core.services.IAccessControlService;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IStoreToStringService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.AccessControlServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.core.utils.FileUtil;
import ch.elexis.global_inbox.Preferences;
import ch.elexis.global_inbox.model.GlobalInboxEntry;
import ch.elexis.global_inbox.ui.GlobalInboxUtil;

@SuppressWarnings("rawtypes")
@Component(service = GlobalInboxEntryFactory.class, immediate = true)
public class GlobalInboxEntryFactory {

	@Reference
	private IStoreToStringService storeToStringService;
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	private IModelService modelService;
	@Reference
	private IConfigService configService;

	@Reference
	private IAccessControlService accessControl;

	private static List<Function> extensionFileHandlers = new ArrayList<Function>();

	@Activate
	public void activate() {
		accessControl.doPrivileged(() -> {
			String giDirSetting = GlobalInboxUtil.getDirectory("NOTSET", configService); //$NON-NLS-1$
			if ("NOTSET".equals(giDirSetting)) { //$NON-NLS-1$
				File giDir = new File(CoreHub.getWritableUserDir(), "GlobalInbox"); //$NON-NLS-1$
				boolean created = giDir.mkdir();
				if (created) {
					ConfigServiceHolder.get().setLocal(PreferencesUtil.getOsSpecificPreferenceName(
							CoreUtil.getOperatingSystemType(), Preferences.PREF_DIR), giDir.getAbsolutePath());
				}
			}
		});
	}

	@Reference(target = "(service.name=ch.elexis.global_inbox.extensionfilehandler)", cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void setExtensionFileHandler(Function extensionFileHandler) {
		GlobalInboxEntryFactory.extensionFileHandlers.add(extensionFileHandler);
	}

	public void unsetExtensionFileHandler(Function extensionFileHandler) {
		GlobalInboxEntryFactory.extensionFileHandlers.remove(extensionFileHandler);
	}

	public GlobalInboxEntry createEntry(IVirtualFilesystemHandle mainFile, IVirtualFilesystemHandle[] extensionFiles,
			String category) {
		GlobalInboxEntry globalInboxEntry = new GlobalInboxEntry(mainFile, extensionFiles);
		String mimeType = null;
		Optional<File> localFile = mainFile.toFile();
		if (localFile.isPresent()) {
			try {
				mimeType = Files.probeContentType(localFile.get().toPath());
			} catch (IOException e) {
			}
		}
		if (mimeType == null) {
			mimeType = FilenameUtils.getExtension(mainFile.getName());
		}
		globalInboxEntry.setMimetype(mimeType);
		globalInboxEntry.setCategory(category);
		globalInboxEntry.setSendInfoTo(configService.getLocal(Preferences.PREF_INFO_IN_INBOX, false));
		return globalInboxEntry;

	}

	public GlobalInboxEntry populateExtensionInformation(GlobalInboxEntry globalInboxEntry) {
		IVirtualFilesystemHandle[] extensionFiles = globalInboxEntry.getExtensionFiles();
		for (IVirtualFilesystemHandle file : extensionFiles) {
			String absolutePath = toLocalPath(file);
			if (absolutePath == null) {
				continue;
			}
			for (Function handler : extensionFileHandlers) {
				@SuppressWarnings("unchecked")
				Map<String, Object> result = (Map<String, Object>) handler.apply(absolutePath);
				if (result != null) {
					integrateAdditionalInformation(result, globalInboxEntry);
				}
			}
		}
		return globalInboxEntry;
	}

	/**
	 * The registered extension file handlers read their input through
	 * {@link File}, so a handle not residing on the local filesystem has to be
	 * fetched into a temporary copy first. The copy keeps the original file name as
	 * its suffix, the handlers dispatch on it.
	 *
	 * @return a local path, or <code>null</code> if the file could not be fetched
	 */
	private String toLocalPath(IVirtualFilesystemHandle handle) {
		Optional<File> localFile = handle.toFile();
		if (localFile.isPresent()) {
			return localFile.get().getAbsolutePath();
		}
		try {
			File tempFile = File.createTempFile("globalinbox_", //$NON-NLS-1$
					"_" + FileUtil.sanitizeFilename(handle.getName(), LoggerFactory.getLogger(getClass()))); //$NON-NLS-1$
			tempFile.deleteOnExit();
			try (InputStream in = handle.openInputStream()) {
				Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			return tempFile.getAbsolutePath();
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).warn("Could not fetch extension file [{}]", //$NON-NLS-1$
					GlobalInboxUtil.toLogString(handle), e);
			return null;
		}
	}

	private void integrateAdditionalInformation(Map<String, Object> result, GlobalInboxEntry gie) {

		Object dateTokens = result.get("dateTokens"); //$NON-NLS-1$
		if (dateTokens instanceof List) {
			@SuppressWarnings("unchecked")
			List<LocalDate> _dateTokens = (List<LocalDate>) dateTokens;
			if (_dateTokens != null && !_dateTokens.isEmpty()) {
				gie.setDateTokens(_dateTokens);
			}
		}

		Object object = result.get("creationDateCandidate"); //$NON-NLS-1$
		if (object instanceof LocalDate) {
			gie.setCreationDateCandidate((LocalDate) object);
		}

		Object patientCandidates = result.get("patientCandidates"); //$NON-NLS-1$
		if (patientCandidates instanceof List) {
			@SuppressWarnings("unchecked")
			List<String> patientCandidatesSts = (List<String>) patientCandidates;
			if (patientCandidatesSts != null && !patientCandidatesSts.isEmpty()) {
				List<Identifiable> _patients = patientCandidatesSts.stream()
						.map(storeToString -> storeToStringService.loadFromString(storeToString).orElse(null))
						.filter(Objects::nonNull).collect(Collectors.toList());
				List<IPatient> patients = _patients.stream()
						.map(i -> modelService.load(i.getId(), IPatient.class).orElse(null))
						.collect(Collectors.toList());
				gie.setPatientCandidates(patients);
				if (patients.size() == 1) {
					gie.setPatient(patients.get(0));
				}
			}
		}

		Object senderCandidates = result.get("senderCandidates"); //$NON-NLS-1$
		if (senderCandidates instanceof List) {
			@SuppressWarnings("unchecked")
			List<String> senderCandidatesSts = (List<String>) senderCandidates;
			if (senderCandidatesSts != null && !senderCandidatesSts.isEmpty()) {
				List<Identifiable> _senders = senderCandidatesSts.stream()
						.map(storeToString -> storeToStringService.loadFromString(storeToString).orElse(null))
						.filter(Objects::nonNull).collect(Collectors.toList());
				List<IContact> senders = _senders.stream()
						.map(i -> modelService.load(i.getId(), IContact.class).orElse(null))
						.collect(Collectors.toList());
				gie.setSenderCandidates(senders);
				if(senders.size() == 1) {
					gie.setSender(senders.get(0));
				}
			}
		}
	}
}
