package ch.elexis.mednet.webapi.ui.navigation;

import ch.elexis.mednet.webapi.core.constants.StateConstants;

public class NavigationState {
	public String state;
	public Integer customerId;
	public Integer providerId;
	public Integer formId;
	public String formName;
	public String customerName;
	public String providerName;

	public NavigationState(String state, Integer customerId, Integer providerId, Integer formId, String formName,
			String customerName, String providerName) {
		this.state = state;
		this.customerId = customerId;
		this.providerId = providerId;
		this.formId = formId;
		this.formName = formName;
		this.customerName = customerName;
		this.providerName = providerName;
	}

	public String getDisplayName() {
		switch (state) {
		case StateConstants.CUSTOMER:
			return customerName != null ? customerName : "Kunden";
		case StateConstants.PROVIDER:
			return providerName != null ? providerName : "Anbieter";
		case StateConstants.FORM:
			return formName != null ? formName : "Formulare";
		default:
			return "";
		}
	}
}
