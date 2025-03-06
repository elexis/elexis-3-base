/*******************************************************************************
 * Copyright (c) 2010-2011, Medelexis AG
 * All rights reserved.
  *******************************************************************************/

package org.iatrix.bestellung.rose;

import org.apache.commons.lang3.StringUtils;

public interface Constants {
	public static final String TOKEN_URL_TEST = "https://test.api.clustertec.com/auth/realms/GHP/protocol/openid-connect/token";
	public static final String ORDER_URL_TEST = "https://test.api.clustertec.com/services/orders";
	public static final String TOKEN_URL = "https://api.clustertec.com/auth/realms/GHP/protocol/openid-connect/token";
	public static final String ORDER_URL = "https://api.clustertec.com/services/orders";
	public static final String PLUGIN_ID = "org.iatrix.bestellung.rose";
	public static final String XSD_RELATIVE_PATH = "rsc/gs1/ecom/Order.xsd";
	public static final String CFG_BASE = "org.iatrix.bestellung.rose";

	public static final String CFG_ROSE_CLIENT_NUMBER = CFG_BASE + "/global_client_number";
	public static final String CFG_ROSE_ADDITIONAL_CLIENT_NUMBERS = CFG_BASE + "/additional_client_numbers";
	public static final String CFG_ROSE_USERNAME = CFG_BASE + "/global_username";
	public static final String CFG_ROSE_PASSWORD = CFG_BASE + "/global_password";
	public static final String CFG_ASAS_PROXY_HOST = CFG_BASE + "/asas_host";
	public static final String CFG_ASAS_PROXY_PORT = CFG_BASE + "/asas_port";
	public static final String CFG_ROSE_SUPPLIER = CFG_BASE + "/supplier";

	public static final String CFG_ROSE_CLIENT_SECRET_NAME = CFG_BASE + "/client_id_prod";
	public static final String CFG_ROSE_CLIENT_SECRET_APIKEY = CFG_BASE + "/client_secret_prod";

	public static final String DEFAULT_ROSE_CLIENT_NUMBER = StringUtils.EMPTY;

	/* actually not significant... */
	public static final String DEFAULT_ROSE_USERNAME = "elexis";
	public static final String DEFAULT_ROSE_PASSWORD = "elexis";

	// Zusätzliche Konstanten für XML-Erstellung
	public static final String HEADER_VERSION = "1.0";
	public static final String SENDER_AUTHORITY = "PartnerName";
	public static final String SENDER_NAME = "Elexis";
	public static final String RECEIVER_AUTHORITY = "ZurRose";
	public static final String RECEIVER_NAME = "ZurRose";
	public static final String STANDARD_GS1 = "GS1";
	public static final String DOCUMENT_TYPE = "Order";
	public static final String DOCUMENT_VERSION = "3.4.1";
	public static final String DOCUMENT_ELEMENT = "origin";
	public static final String ORDER_TYPE_CODE = "220";
	public static final String ADDITIONAL_ORDER_INSTRUCTION = "ROWA";
	public static final String LANGUAGE_CODE = "en";
	public static final String SELLER_NUMBER = "54183";
	public static final String BUYER_ASSIGNED_ID = "BUYER_ASSIGNED_IDENTIFIER_FOR_A_PARTY";
	public static final String SELLER_ASSIGNED_ID = "SELLER_ASSIGNED_IDENTIFIER_FOR_A_PARTY";
	public static final String PHARMACODE_CH = "PHARMACODE_CH";

}
