package at.medevit.elexis.gdt.interfaces;

import java.util.List;

public interface IGDTCommunicationPartnerProvider extends IGDTCommunicationPartner {
	public List<IGDTCommunicationPartner> getChildCommunicationPartners();
	
	public String getId();
}
