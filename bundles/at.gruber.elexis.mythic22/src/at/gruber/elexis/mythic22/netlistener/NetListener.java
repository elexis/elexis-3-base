/*******************************************************************************
 * Copyright (c) 2011, Christian Gruber and MEDEVIT OG
 * All rights reserved.
 *******************************************************************************/
package at.gruber.elexis.mythic22.netlistener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gruber.elexis.mythic22.Activator;
import at.gruber.elexis.mythic22.inputhandler.InputHandler;
import at.gruber.elexis.mythic22.model.Mythic22Result;
import at.gruber.elexis.mythic22.persistency.PersistencyHandler;

public class NetListener implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(NetListener.class);
	
	private static final String START = "MYTHIC";
	private static final String END = "END_RESULT";
	
	private int m_serverPort;
	private static boolean m_running = false;
	private Thread m_thread;
	private Socket m_socket;
	private ServerSocket m_serverSocket;
	
	public NetListener(int serverPort){
		super();
		m_serverPort = serverPort;
	}
	
	/**
	 * Invokes a new Thread and starts it. This thread will process all incoming results from
	 * mythic22
	 */
	public void startContinousRead(){
		// TODO IF we are still in Thread state RUNNABLE we face an IllegalThreadException here
		if (m_thread == null || (m_thread.getState() == Thread.State.TERMINATED)) {
			m_thread = new Thread(this);
		}
		//		System.out.println(m_thread.getState());
		m_serverSocket = null;
		m_socket = null;
		m_running = true;
		m_thread.start();
	}
	
	/**
	 * tries to read one Mythic22 output from start to end and returns the output on success if it
	 * fails to read the whole output or an output at all null is returned
	 * 
	 * @return null or the mythic22 output as a String
	 */
	private String readFromServer(){
		
		boolean mythicHeaderFound = false;
		boolean mythicEndReached = false;
		
		StringBuilder strBuilder = new StringBuilder();
		try {
			if (m_serverSocket == null || m_serverSocket.isClosed()) {
				m_serverSocket = new ServerSocket(m_serverPort);
				m_serverSocket.setSoTimeout(5000);
			}
			
			m_socket = m_serverSocket.accept();
			
			BufferedReader in =
				new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
			
			String temp;
			while (!mythicEndReached && (temp = in.readLine()) != null) {
				if (temp.startsWith(START)) {
					mythicHeaderFound = true;
				} else if (temp.startsWith(END)) {
					mythicEndReached = true;
				}
				if (mythicHeaderFound) {
					strBuilder.append(temp);
					strBuilder.append('\n');
				}
			}
			
			in.close();
			m_socket.close();
			
			if (mythicHeaderFound == true && mythicEndReached == true) {
				return strBuilder.toString();
			} else {
				return null;
			}
			
		} catch (IOException e) {
			if(e instanceof BindException) {
				requestThreadToStop();
			}
			if (e instanceof SocketTimeoutException)
				return null;
			String message = "Error on creation of server socket on port " + m_serverPort;
			Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message, e);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			logger.error(message, e);
			return null;
		}
		
	}
	
	/**
	 * reads incoming mythic22 results, processes them using the InputHandler and saves them into
	 * the database using the PersistencyHandler
	 */
	@Override
	public void run(){
		logger.debug("Starting mythic 22 listener");
		while (m_running) {
			String temp = readFromServer();
			if (temp != null) {
				logger.debug("Got mythic 22 input of length " + temp.length());
				Mythic22Result result = InputHandler.getInstance().processInput(temp);
				if (!PersistencyHandler.getInstance().persistMythicResult(result)) {
					String message = "Error persisting data!";
					Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message);
					StatusManager.getManager().handle(status, StatusManager.SHOW);
				}
			}
		}
		logger.debug("Stopping mythic 22 listener");
		try {
			if (m_serverSocket != null) {
				m_serverSocket.close();
				m_serverSocket = null;
			}
		} catch (IOException e) {
			String message = "Error closing server socket on " + m_serverSocket.getLocalPort();
			logger.warn(message, e);
		}
	}
	
	/**
	 * request the continuous read Thread to stop
	 */
	public void requestThreadToStop(){
		m_running = false;
	}
	
	public int getServerPort(){
		return m_serverPort;
	}
	
	public static boolean isRunning(){
		return m_running;
	}
	
}
