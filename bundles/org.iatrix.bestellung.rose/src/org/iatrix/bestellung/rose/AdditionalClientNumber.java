package org.iatrix.bestellung.rose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.services.holder.ConfigServiceHolder;

public class AdditionalClientNumber {

	private String clientIdent;
	private String clientNumber;

	private static String objDelimiter = "||";
	private static String fieldDelimiter = "|";

	public static String toString(List<AdditionalClientNumber> clientNumbers) {
		StringJoiner sj = new StringJoiner(objDelimiter);
		for (AdditionalClientNumber additionalClientNumber : clientNumbers) {
			sj.add(additionalClientNumber.toString());
		}
		return sj.toString();
	}

	public AdditionalClientNumber(String objectString) {
		String[] parts = objectString.split("\\|");
		if (parts.length == 2) {
			clientIdent = parts[0];
			clientNumber = parts[1];
		} else if (StringUtils.isBlank(objectString)) {
			clientIdent = "";
			clientNumber = "";
		} else {
			throw new IllegalStateException("Unknown objectString");
		}
	}

	@Override
	public String toString() {
		return clientIdent + fieldDelimiter + clientNumber;
	}

	public String getClientIdent() {
		return clientIdent;
	}

	public void setClientIdent(String clientIdent) {
		this.clientIdent = clientIdent;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public static boolean isConfigured() {
		String prefString = ConfigServiceHolder.getGlobal(Constants.CFG_ROSE_ADDITIONAL_CLIENT_NUMBERS, null);
		return StringUtils.isNotBlank(prefString);
	}

	public static List<AdditionalClientNumber> getConfigured() {
		String prefString = ConfigServiceHolder.getGlobal(Constants.CFG_ROSE_ADDITIONAL_CLIENT_NUMBERS, null);
		if (StringUtils.isNotEmpty(prefString)) {
			return of(prefString);
		}
		return Collections.emptyList();
	}

	private static List<AdditionalClientNumber> of(String string) {
		List<AdditionalClientNumber> ret = new ArrayList<>();
		String[] objects = string.split("\\|\\|");
		for (String objectString : objects) {
			ret.add(new AdditionalClientNumber(objectString));
		}
		return ret;
	}
}
