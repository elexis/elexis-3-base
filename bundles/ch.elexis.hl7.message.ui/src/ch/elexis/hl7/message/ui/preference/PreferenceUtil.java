package ch.elexis.hl7.message.ui.preference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class PreferenceUtil {
	
	public static final String PREF_RECEIVERS = "ch.elexis.hl7.message.ui/receivers";
	
	public static final String PREF_FILESYSTEM_OUTPUTDIR =
		"ch.elexis.hl7.message.ui/output/directory";
	
	public static List<Receiver> getReceivers(){
		List<Receiver> ret = new ArrayList<>();
		String receiversString = ConfigServiceHolder.getGlobal(PREF_RECEIVERS, null);
		if (receiversString != null && !receiversString.isEmpty()) {
			String[] receiversParts = receiversString.split("\\|\\|");
			if (receiversParts != null) {
				for (String receiverString : receiversParts) {
					ret.add(Receiver.of(receiverString));
				}
			}
		}
		return ret;
	}
	
	public static void addReceiver(Receiver receiver){
		List<Receiver> receivers = getReceivers();
		receivers.add(receiver);
		setReceivers(receivers);
	}
	
	public static void removeReceiver(Receiver receiver){
		List<Receiver> receivers = getReceivers();
		List<Receiver> filtered = receivers.stream().filter(existing -> !existing.equals(receiver))
			.collect(Collectors.toList());
		setReceivers(filtered);
	}
	
	public static void setReceivers(List<Receiver> receivers){
		StringJoiner sj = new StringJoiner("||");
		for (Receiver receiver : receivers) {
			sj.add(receiver.toString());
		}
		ConfigServiceHolder.setGlobal(PREF_RECEIVERS, sj.toString());
	}
	
	public static Optional<File> getOutputDirectory(){
		String outputDir = CoreHub.localCfg.get(PREF_FILESYSTEM_OUTPUTDIR, null);
		if (outputDir != null) {
			return Optional.of(new File(outputDir));
		}
		return Optional.empty();
	}
}
