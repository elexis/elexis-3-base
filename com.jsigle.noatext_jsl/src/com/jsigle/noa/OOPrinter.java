/*******************************************************************************
 * Copyright (c) 2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id$
 *******************************************************************************/

package com.jsigle.noa;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import com.sun.star.beans.XPropertySet;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.style.XStyle;
import com.sun.star.style.XStyleFamiliesSupplier;
import com.sun.star.text.XText;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.view.PrintJobEvent;
import com.sun.star.view.PrintableState;
import com.sun.star.view.XPrintJobListener;

import ch.elexis.core.ui.util.Log;
import ch.rgw.tools.ExHandler;

/**
 * Some helpers for printing
 * @author Gerry
 *
 */
public class OOPrinter {
	private com.sun.star.frame.XComponentLoader xCompLoader = null;
	XComponentContext xContext;
	XMultiComponentFactory xMCF;
	static Log log=Log.get("OOPrinter");
	
	public boolean init(){
		try{
			xContext=Bootstrap.bootstrap();
			xMCF=xContext.getServiceManager();
			if(xMCF!=null){
				xCompLoader = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class, 
						xMCF.createInstanceWithContext("com.sun.star.frame.Desktop",xContext));
				return (xCompLoader!=null);
			}
			return false;
		}catch(Exception ex){
			ExHandler.handle(ex);
			return false;
		}
	}
	
	
	public static boolean setPrinterTray(XTextDocument doc, String tray) throws Exception{
		XText xText = doc.getText();
		XTextCursor cr = xText.createTextCursor();
	
		XPropertySet xTextCursorProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, cr);
		
		String pageStyleName = xTextCursorProps.getPropertyValue("PageStyleName").toString();
		
//		Get the StyleFamiliesSupplier interface of the document
		XStyleFamiliesSupplier xSupplier = (XStyleFamiliesSupplier) UnoRuntime.queryInterface(XStyleFamiliesSupplier.class, doc);
//		Use the StyleFamiliesSupplier interface to get the XNameAccess interface of the
//		actual style families
		XNameAccess xFamilies = (XNameAccess) UnoRuntime.queryInterface(XNameAccess.class, xSupplier.getStyleFamilies());
//		Access the 'PageStyles' Family
		XNameContainer xFamily = (XNameContainer) UnoRuntime.queryInterface(XNameContainer.class, xFamilies.getByName("PageStyles"));
		
		XStyle xStyle = (XStyle) UnoRuntime.queryInterface(XStyle.class, xFamily.getByName(pageStyleName));
//		Get the property set of the cell's TextCursor
		XPropertySet xStyleProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xStyle);
//		my PageStyleSetting ...
		try{
			xStyleProps.setPropertyValue("PrinterPaperTray", tray);
			return true;
		}catch(Exception ex){
			String possible=(String)xStyleProps.getPropertyValue("PrinterPaperTray");
			log.log("Could not set Tray to "+tray+" try "+possible, Log.ERRORS);
			return false;
		}
	}
	
	 public static boolean checkExistsPrinter(String printer) {
		boolean exists = true;
		
//		Look up all services
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		for (int i = 0; i < services.length; i++) {
			if (services[i].getName().trim().equals(printer.trim()))
				return exists;
		}
		return !exists;
	}
	
	
	static class MyXPrintJobListener implements XPrintJobListener {
		private PrintableState status = null;
		public PrintableState getStatus() {
			return status;
		}
		
		public void setStatus(PrintableState status) {
			this.status = status;
		}
		
		/**
		 * The print job event: has to be called when the action is triggered.
		 */
		public void printJobEvent(PrintJobEvent printJobEvent) {
			if(printJobEvent.State == PrintableState.JOB_COMPLETED)
			{
				System.out.println("JOB_COMPLETED");
				this.setStatus(PrintableState.JOB_COMPLETED);
			}
			if(printJobEvent.State == PrintableState.JOB_ABORTED)
			{
				System.out.println("JOB_ABORTED");
				this.setStatus(PrintableState.JOB_ABORTED);
			}
			if(printJobEvent.State == PrintableState.JOB_FAILED)
			{
				System.out.println("JOB_FAILED");
				this.setStatus(PrintableState.JOB_FAILED);
				return;
			}
			if(printJobEvent.State == PrintableState.JOB_SPOOLED)
			{
				System.out.println("JOB_SPOOLED");
				this.setStatus(PrintableState.JOB_SPOOLED);
			}
			if(printJobEvent.State == PrintableState.JOB_SPOOLING_FAILED)
			{
				System.out.println("JOB_SPOOLING_FAILED");
				this.setStatus(PrintableState.JOB_SPOOLING_FAILED);
				return;
			}
			if(printJobEvent.State == PrintableState.JOB_STARTED)
			{
				System.out.println("JOB_STARTED");
				this.setStatus(PrintableState.JOB_STARTED);
				return;
			}
		}
		
		/**
		 * Disposing event: ignore.
		 */
		public void disposing(com.sun.star.lang.EventObject eventObject) {
			System.out.println("disposing");
		}
	}
	
}

