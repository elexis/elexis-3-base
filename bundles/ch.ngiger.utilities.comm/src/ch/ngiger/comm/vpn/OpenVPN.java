/*******************************************************************************
 * Copyright (c) 2010, Niklaus Giger and Medelexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Niklaus Giger - initial implementation
 *
 *******************************************************************************/
package ch.ngiger.comm.vpn;

import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

import org.eclipse.swt.program.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.events.MessageEvent;
import ch.rgw.tools.ExHandler;

public class OpenVPN {
	Logger logger = LoggerFactory.getLogger(OpenVPN.class);

	/*
	 * Ping another host
	 *
	 * @hostname hostname/IP-address of host to ping
	 */
	public boolean ping(String hostname) {
		boolean result = false;
		try {
			result = InetAddress.getByName(hostname).isReachable(3000);
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return result;
	}

	/*
	 * Open the connection and keeps it open for a specified time The connection
	 * will be tested using a ping.
	 *
	 * @hostName string, e.g. 172.25.144 or ftp.example.com
	 *
	 * @ovpnIp IP of the server we will try to ping to verify that the connection is
	 * okay.
	 *
	 * @timeout how long we will wait till the connection is okay 20 seconds is a
	 * reasonable value
	 *
	 * @return OpenVPN connection is okay
	 */
	public boolean openConnection(String ovpnConfig, String ovpnIp, int timeout) {
		try {

			String ext = ".ovpn";
			File temp = File.createTempFile("start_ovpn", ".cmd");
			temp.deleteOnExit();
			File myFile = new File(ovpnConfig);
			File parent = new File(myFile.getParent());
			String parentDir = parent.getParent();
			String cmd = StringUtils.EMPTY;
			String exe = StringUtils.EMPTY;
			String os = System.getProperty("os.name").toLowerCase();
			// Under Windows we start OpenVPN here
			if (os.indexOf("win") >= 0) {
				cmd = "cd " + parentDir + File.separator + "config && ";
				cmd += " start /min ";
				File ovpnExe = new File(parentDir + File.separator + "bin" + File.separator, "openvpn");
				exe = ovpnExe.getAbsolutePath();
				cmd += StringUtils.SPACE + ovpnExe + " --config " + myFile.getName();
				FileWriter fos = new FileWriter(temp);
				logger.info(cmd);
				fos.write(cmd);
				fos.close();
				boolean res = Program.launch(temp.getAbsolutePath());
			}
			// else we assume that it was launched by daemons
			int j = 0, maxWait = 20;
			long startMs = Calendar.getInstance().getTimeInMillis();

			while (true) {
				if (ping(ovpnIp))
					break;
				long actualMs = Calendar.getInstance().getTimeInMillis();

				if ((actualMs - startMs) >= timeout * 1000) {
					logger.error("Could not ping to server: " + ovpnIp);
					return false;
				}
				System.out.println("Pinging");
			}
			System.out.println("Ping was okay to server: " + ovpnIp);

		} catch (Exception ex) {
			logger.error("Could not start program");
			ExHandler.handle(ex);
			MessageEvent.fireError("OpenVPN", String.format("Could not start program %s", ex.getMessage()));
		}
		return true;
	}

	/*
	 * Closes the OpenVPN connection
	 */
	public void closeConnection() {
		logger.info("Close connection");
	}
}
