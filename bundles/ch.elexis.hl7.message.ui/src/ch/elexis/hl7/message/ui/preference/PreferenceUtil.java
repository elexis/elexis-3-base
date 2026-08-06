package ch.elexis.hl7.message.ui.preference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.preferences.PreferencesUtil;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;

public class PreferenceUtil {

	public static final String PREF_RECEIVERS = "ch.elexis.hl7.message.ui/receivers"; //$NON-NLS-1$

	public static final String PREF_FILESYSTEM_OUTPUTDIR = "ch.elexis.hl7.message.ui/output/directory"; //$NON-NLS-1$

	public static final String PREF_FILESYSTEM_OUTPUTDIR_GLOBAL = "ch.elexis.hl7.message.ui/output/directory_global"; //$NON-NLS-1$

	public static List<Receiver> getReceivers() {
		List<Receiver> ret = new ArrayList<>();
		String receiversString = ConfigServiceHolder.getGlobal(PREF_RECEIVERS, null);
		if (receiversString != null && !receiversString.isEmpty()) {
			String[] receiversParts = receiversString.split("\\|\\|"); //$NON-NLS-1$
			if (receiversParts != null) {
				for (String receiverString : receiversParts) {
					ret.add(Receiver.of(receiverString));
				}
			}
		}
		return ret;
	}

	public static void addReceiver(Receiver receiver) {
		List<Receiver> receivers = getReceivers();
		receivers.add(receiver);
		setReceivers(receivers);
	}

	public static void removeReceiver(Receiver receiver) {
		List<Receiver> receivers = getReceivers();
		List<Receiver> filtered = receivers.stream().filter(existing -> !existing.equals(receiver))
				.collect(Collectors.toList());
		setReceivers(filtered);
	}

	public static void setReceivers(List<Receiver> receivers) {
		StringJoiner sj = new StringJoiner("||"); //$NON-NLS-1$
		for (Receiver receiver : receivers) {
			sj.add(receiver.toString());
		}
		ConfigServiceHolder.setGlobal(PREF_RECEIVERS, sj.toString());
	}

	public static boolean isStoreGlobal() {
		return isStoreGlobal(ConfigServiceHolder.get());
	}

	public static boolean isStoreGlobal(IConfigService configService) {
		return configService.get(PREF_FILESYSTEM_OUTPUTDIR_GLOBAL, false);
	}

	public static String getOutputDirectory() {
		return getOutputDirectory(ConfigServiceHolder.get());
	}

	public static String getOutputDirectory(IConfigService configService) {
		return PreferencesUtil.getOsSpecificPreference(PREF_FILESYSTEM_OUTPUTDIR, isStoreGlobal(configService),
				configService);
	}

	public static Optional<IVirtualFilesystemHandle> getOutputDirectoryHandle() {
		return getOutputDirectoryHandle(VirtualFilesystemServiceHolder.get(), getOutputDirectory());
	}

	public static Optional<IVirtualFilesystemHandle> getOutputDirectoryHandle(IVirtualFilesystemService vfsService,
			String directory) {
		if (StringUtils.isBlank(directory)) {
			return Optional.empty();
		}
		try {
			IVirtualFilesystemHandle handle = vfsService.of(directory);
			if (handle.exists() && handle.isDirectory()) {
				return Optional.of(handle);
			}
			LoggerFactory.getLogger(PreferenceUtil.class).warn("HL7 message output directory [{}] is not a directory",
					IVirtualFilesystemService.hidePasswordInUrlString(directory));
		} catch (IOException e) {
			LoggerFactory.getLogger(PreferenceUtil.class).warn("Could not access HL7 message output directory [{}]",
					IVirtualFilesystemService.hidePasswordInUrlString(directory), e);
		}
		return Optional.empty();
	}
}
