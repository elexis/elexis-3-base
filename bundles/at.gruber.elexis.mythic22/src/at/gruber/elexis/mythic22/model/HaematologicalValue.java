/*******************************************************************************
 * Copyright (c) 2011, Christian Gruber and MEDEVIT OG
 * All rights reserved.
 *******************************************************************************/
package at.gruber.elexis.mythic22.model;

import org.apache.commons.lang3.StringUtils;

/**
 * This class represents one haematological value These values are delivered
 * from mythic22
 *
 * @author Christian
 *
 */
public class HaematologicalValue {

	private String m_identifier = new String();
	private String m_value = new String();
	private String m_flagA = new String();
	private String m_flagB = new String();
	private String m_lowPanicValue = new String();
	private String m_lowNormalValue = new String();
	private String m_highNormalValue = new String();
	private String m_highPanicValue = new String();

	public HaematologicalValue(String identifier, String value, String flagA, String flagB, String lowPanicValue,
			String lowNormalValue, String highNormalValue, String highPanicValue) {
		super();
		m_identifier = identifier;
		m_value = value;
		m_flagA = flagA;
		m_flagB = flagB;
		m_lowPanicValue = lowPanicValue;
		m_lowNormalValue = lowNormalValue;
		m_highNormalValue = highNormalValue;
		m_highPanicValue = highPanicValue;
	}

	public HaematologicalValue(String identifier, String csvValues) {
		super();
		m_identifier = identifier;

		// seperate the values and remove spaces
		String[] str = csvValues.split(";");
		for (int i = 0; i < str.length; i++) {
			str[i] = str[i].trim();
		}

		// fill the Attributes using the csvValues
		if (str.length == 7) {
			int i = 0;
			m_value = str[i++];
			m_flagA = str[i++];
			m_flagB = str[i++];
			m_lowPanicValue = str[i++];
			m_lowNormalValue = str[i++];
			m_highNormalValue = str[i++];
			m_highPanicValue = str[i++];

			// else: field is empty because no values have been sent from mythic22 ->
			// Attributes are an empty String StringUtils.EMPTY
		}
	}

	public HaematologicalValue(String identifier) {
		super();
		m_identifier = identifier;
	}

	public String getIdentifier() {
		return m_identifier;
	}

	public void setIdentifier(String identifier) {
		m_identifier = identifier;
	}

	public String getValue() {
		return m_value;
	}

	public void setValue(String value) {
		m_value = value;
	}

	public String getFlagA() {
		return m_flagA;
	}

	public void setFlagA(String flagA) {
		m_flagA = flagA;
	}

	public String getFlagB() {
		return m_flagB;
	}

	public void setFlagB(String flagB) {
		m_flagB = flagB;
	}

	public String getLowPanicValue() {
		return m_lowPanicValue;
	}

	public void setLowPanicValue(String lowPanicValue) {
		m_lowPanicValue = lowPanicValue;
	}

	public String getLowNormalValue() {
		return m_lowNormalValue;
	}

	public void setLowNormalValue(String lowNormalValue) {
		m_lowNormalValue = lowNormalValue;
	}

	public String getHighNormalValue() {
		return m_highNormalValue;
	}

	public void setHighNormalValue(String highNormalValue) {
		m_highNormalValue = highNormalValue;
	}

	public String getHighPanicValue() {
		return m_highPanicValue;
	}

	public void setHighPanicValue(String highPanicValue) {
		m_highPanicValue = highPanicValue;
	}

}
