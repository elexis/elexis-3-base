package ch.elexis.hl7.message.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.hl7.message.core.IHL7MessageService;
import ch.elexis.hl7.message.core.message.ADT_A08Message;
import ch.elexis.hl7.message.core.message.IHL7Message;

@Component
public class HL7MessageService implements IHL7MessageService {
	
	private Map<String, List<IHL7Message>> messages;
	
	public HL7MessageService(){
		messages = new HashMap<>();
		// add message implementations here
		addMessage("ADT_A08", new ADT_A08Message());
	}
	
	private void addMessage(String messageTyp, IHL7Message message){
		List<IHL7Message> messagesList = messages.get(messageTyp);
		if (messagesList == null) {
			messagesList = new ArrayList<>();
		}
		messagesList.add(message);
		messages.put(messageTyp, messagesList);
	}
	
	@Override
	public String getMessage(String messageTyp, Map<String, Object> context) throws ElexisException{
		List<IHL7Message> messageVersions = messages.get(messageTyp);
		if (messageVersions != null && !messageVersions.isEmpty()) {
			String versionHint = (String) context.get(IHL7MessageService.CONTEXT_HL7VERSION_HINT);
			if (versionHint != null) {
				for (IHL7Message ihl7Message : messageVersions) {
					if (versionHint.equals(ihl7Message.getHL7Version())) {
						return ihl7Message.getMessage(context);
					}
				}
			}
			return messageVersions.get(0).getMessage(context);
		}
		throw new ElexisException("No message implementation for typ [" + messageTyp + "]");
	}
}
