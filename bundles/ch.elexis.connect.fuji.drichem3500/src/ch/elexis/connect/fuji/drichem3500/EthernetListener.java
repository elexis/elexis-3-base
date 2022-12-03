/*******************************************************************************
 * Copyright (c) 2009-2022, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - reworked for ethernet
 *
 *******************************************************************************/
package ch.elexis.connect.fuji.drichem3500;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EthernetListener implements Runnable {

	protected static Logger log = LoggerFactory.getLogger(EthernetListener.class);

	private int serverPort;
	private Thread thread;
	private ServerSocket serverSocket;

	private ConnectAction action;
	public static boolean active;

	/**
	 * construct ethernet listener for given port
	 *
	 * @param serverPort
	 */
	public EthernetListener(int serverPort, ConnectAction action) {
		super();
		this.serverPort = serverPort;
		this.action = action;
	}

	@Override
	public void run() {
		try {
			if (serverSocket == null || serverSocket.isClosed()) {
				serverSocket = new ServerSocket(serverPort);
				serverSocket.setSoTimeout(3000);

				log.debug("Fuji Drichem listening on PORT: " + serverPort + ", " + serverSocket);
			}

			while (active) {
				try {
					Socket clientSocket = serverSocket.accept();
					log.debug("serverSocket.accept()");

					// if still active read and handle input
					if (active) {
						EthernetClientThread cliThread = new EthernetClientThread(clientSocket, action);
						cliThread.start();
					}
				} catch (SocketTimeoutException ste) {
					// do not log
				}
			}
			serverSocket.close();
			serverSocket = null;

		} catch (IOException e) {
			log.error("", e);
			e.printStackTrace();
		}

	}

	public void startThread() {
		if (thread == null || (thread.getState() == Thread.State.TERMINATED)) {
			thread = new Thread(this);
			log.debug("Started thread for Fuji Drichem");
		}
		thread.start();
		active = true;
	}

	public void stopThread() {
		log.debug("Stopping Fuji Drichem listener");
		thread = null;
		active = false;
	}

}
