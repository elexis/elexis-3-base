/*******************************************************************************
 * Copyright (c) 2010-2011, Medelexis AG
 * All rights reserved.
  *******************************************************************************/

package org.iatrix.bestellung.rose;

import org.apache.commons.lang3.StringUtils;

public interface Constants {
	public static final String CFG_BASE = "org.iatrix.bestellung.rose";

	public static final String CFG_ROSE_CLIENT_NUMBER = CFG_BASE + "/global_client_number";
	public static final String CFG_ROSE_ADDITIONAL_CLIENT_NUMBERS = CFG_BASE + "/additional_client_numbers";
	public static final String CFG_ROSE_USERNAME = CFG_BASE + "/global_username";
	public static final String CFG_ROSE_PASSWORD = CFG_BASE + "/global_password";
	public static final String CFG_ASAS_PROXY_HOST = CFG_BASE + "/asas_host";
	public static final String CFG_ASAS_PROXY_PORT = CFG_BASE + "/asas_port";
	public static final String CFG_ROSE_SUPPLIER = CFG_BASE + "/supplier";

	public static final String DEFAULT_ROSE_CLIENT_NUMBER = StringUtils.EMPTY;

	/* actually not significant... */
	public static final String DEFAULT_ROSE_USERNAME = "elexis";
	public static final String DEFAULT_ROSE_PASSWORD = "elexis";

}
