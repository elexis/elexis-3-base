/*******************************************************************************
 * Copyright (c) 2019 Medbits GmbH.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Thomas Huster - initial API and implementation
 *******************************************************************************/
package at.medbits.elexis.labbit.discovery;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.ws.rs.ProcessingException;

import org.slf4j.LoggerFactory;

import com.eclipsesource.jaxrs.consumer.RequestException;

public class DiscoveryInfo {

	public static final String DISCOVER_SERVICE_NAME = "at.medbits.emr.discovery.name";
	public static final String DISCOVER_SERVICE_VERSION = "at.medbits.emr.discovery.version";
	
	private URI location;

	private String name;

	private Map<String, String> info;


	public URI getLocation() {
		return location;
	}


	public String getName() {
		return name;
	}


	public String getProperty(String key) {
		return info.get(key);
	}

	public static DiscoveryInfo of(String host, String port, RestDiscoveryClient discoveryClient) {
		try {
			Map<String, String> info = discoveryClient.getInfo();
			if (info != null) {
				DiscoveryInfo ret = new DiscoveryInfo();
				ret.info = info;
				ret.name = info.get(DiscoveryInfo.DISCOVER_SERVICE_NAME);
				if (ret.name.toLowerCase().contains("lab")) {
					ret.location = new URI("http://" + host + ":" + port + "/webapp");
				} else {
					ret.location = new URI("http://" + host + ":" + port + "/swaggerui/index.html");
				}
				return ret;
			}
		} catch (ProcessingException | RequestException | URISyntaxException e) {
			LoggerFactory.getLogger(DiscoveryInfo.class).warn(
					"Error getting discovery info from [" + host + ":" + port + "] error [" + e.getMessage() + "]");
		}
		return null;
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiscoveryInfo other = (DiscoveryInfo) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
