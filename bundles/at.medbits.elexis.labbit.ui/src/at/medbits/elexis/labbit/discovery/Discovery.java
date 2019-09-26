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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.conn.util.InetAddressUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.jaxrs.consumer.ConsumerFactory;

@Component(service = Discovery.class)
public class Discovery {
	
	private static final Logger logger = LoggerFactory.getLogger(Discovery.class);
	
	private Map<String, DiscoveryInfo> discovered;
	
	@Activate
	public void activate(){
		discovered = new ConcurrentHashMap<>();
		refresh();
	}
	
	/**
	 * Refresh the discovery information by scanning the last 1-254 addresses of the network of the
	 * first non virtual network interface. Discovery is performed asynchronously.
	 */
	public void refresh(){
		List<String> networkAddresses = getNetworkAddresses();
		if (!networkAddresses.isEmpty()) {
			refresh(networkAddresses.get(0));
		} else {
			logger.warn("No network found.");
		}
	}
	
	/**
	 * Refresh the discovery information by scanning the last 1-254 addresses of the provided
	 * network. Discovery is performed asynchronously.
	 * 
	 * @param network
	 */
	public void refresh(String network){
		for (String host : discovered.keySet()) {
			String subnet = network.substring(0, network.lastIndexOf('.'));
			if (host.startsWith(subnet)) {
				discovered.remove(host);
			}
		}
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(() -> refreshScanSubNet(network));
		executorService.shutdown();
	}
	
	private void refreshScanSubNet(String network){
		String subnet = network.substring(0, network.lastIndexOf('.'));
		LoggerFactory.getLogger(getClass()).info("Scanning subnet [" + subnet + "]");
		for (int i = 1; i < 255; i++) {
			String host = subnet + "." + i;
			String port = "20141";
			if (isServiceAvailable(host, port, 100)) {
				Optional<DiscoveryInfo> info = getDiscoveryInfo(host, port, 100);
				if (info.isPresent()) {
					LoggerFactory.getLogger(getClass())
						.info("Discovered device [" + info.get().getName() + "@" + host + "]");
					discovered.put(host, info.get());
				}
			}
		}
	}
	
	private ClientConfig getClientConfig(){
		ClientConfig config = new ClientConfig();
		return config;
	}
	
	private Optional<DiscoveryInfo> getDiscoveryInfo(String host, String port, int i){
		String url = "http://" + host + ":" + port + "/";
		RestDiscoveryClient discoveryClient =
			ConsumerFactory.createConsumer(url, getClientConfig(), RestDiscoveryClient.class);
		return Optional.ofNullable(DiscoveryInfo.of(host, port, discoveryClient));
	}
	
	/**
	 * Test if we can connect to the serverHsot on the serverPort using a {@link Socket}. The method
	 * returns true immediately if connect was successful, or false after timeoutms if not
	 * successful.
	 * 
	 * @param serverHost
	 * @param serverPort
	 * @param timeoutms
	 * @return
	 */
	public static boolean isServiceAvailable(String serverHost, String serverPort,
		Integer timeoutms){
		if (serverHost != null) {
			try {
				SocketAddress endpoint =
					new InetSocketAddress(serverHost, Integer.parseInt(serverPort));
				Socket socket = new Socket();
				socket.connect(endpoint, timeoutms);
				socket.close();
				return true;
			} catch (NumberFormatException | IOException e) {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Get a list of all available network interface addresses.
	 * 
	 * @return
	 */
	public List<String> getNetworkAddresses(){
		List<String> ret = new ArrayList<>();
		Set<String> uniqueSet = new HashSet<>();
		try {
			Enumeration<NetworkInterface> networkInterfaces =
				NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				if (!networkInterface.isVirtual() && !networkInterface.isLoopback()
					&& networkInterface.isUp() && !networkInterface.getName().startsWith("tun")
					&& !networkInterface.getName().startsWith("docker")) {
					Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
					while (addresses.hasMoreElements()) {
						InetAddress address = addresses.nextElement();
						if (InetAddressUtils.isIPv4Address(address.getHostAddress())) {
							String subnet = address.getHostAddress().substring(0,
								address.getHostAddress().lastIndexOf('.'));
							uniqueSet.add(subnet + ".0");
						}
					}
				}
			}
		} catch (SocketException e) {
			logger.error("Error searching for network addresses", e);
		}
		ret.addAll(uniqueSet);
		return ret;
	}
	
	/**
	 * Get the {@link DiscoveryInfo} for all discovered devices.
	 * 
	 * @return
	 */
	public List<DiscoveryInfo> getDiscovered(){
		if (discovered != null && !discovered.isEmpty()) {
			return new ArrayList<>(discovered.values());
		}
		return Collections.emptyList();
	}
}
