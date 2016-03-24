/****************************************************************************
 *                                                                          *
 * NOAText_jsl based upon NOA (Nice Office Access) / noa-libre              *
 * ubion.ORS - The Open Report Suite                                        *
 * Subproject: NOA (Nice Office Access)                                     *
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU General Public License Version 2.1                      *
 *                                                                          * 
 * GNU General Public License Version 2.1                                   *
 * ======================================================================== *
 * Portions Copyright 2012 by Joerg Sigle                                   *
 * Copyright 2003-2005 by IOn AG                                            *
 *                                                                          *
 * This program is free software: you can redistribute it and/or modify     *
 * it under the terms of the GNU General Public License as published by     *
 * the Free Software Foundation, either version 2.1 of the License.         *
 *                                                                          *
 *  This program is distributed in the hope that it will be useful,         *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 *  GNU General Public License for more details.                            *
 *                                                                          *
 *  You should have received a copy of the GNU General Public License       *
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.   *
 *                                                                          *
 * Contact us:                                                              *
 *  http://www.jsigle.com                                                   *
 *  http://www.ql-recorder.com                                              *
 *  http://code.google.com/p/noa-libre                                      *
 *  http://www.ion.ag                                                       *
 *  http://ubion.ion.ag                                                     *
 *  info@ion.ag                                                             *
 *                                                                          *
 * Please note: Previously, versions of the NOA library provided by         *
 * www.ion.ag and the noa-libre project carried a licensing remark          *
 * that made them available under the LGPL. However, they include portions  *
 * obtained from the YaBS project, licensed under GPL. Consequently, NOA    *
 * should have been licensed under the GPL, not LGPL, given that no special *
 * permission of the authors of YaBS for LGPL licensing had been obtained.  *
 * To point out the possible problem, I'm providing the files where I added *
 * contributions under the GPL for now. This move is always allowed for     *
 * LPGL licensed material. 20120623js                                       * 
 *                                                                          *
 ****************************************************************************/
 
/****************************************************************************
 * To Do:
 * See experiment / comments by Joerg Sigle below.
 * Review what's happening inside called code (i.e. in OpenOffice/LibreOffice
 * libraries); why does it all work when the office window is not generated
 * inside a frame, but on its own? Apply necessary corrections so that the
 * same grade of robustness is achieved in a frame.
 * Possibly, this version of the library should get a new revision number, currently used is 11724.
 ****************************************************************************/

/*
 * Last changes made by $Author: jsigle $, $Date: 2012-06-23 14:38:00 +0100 (Su, 23 Jun 2012) $
 */
package ag.ion.bion.officelayer.internal.document;

import java.io.IOException;

import ag.ion.bion.officelayer.document.AbstractDocument;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.internal.draw.DrawingDocument;
import ag.ion.bion.officelayer.internal.formula.FormulaDocument;
import ag.ion.bion.officelayer.internal.presentation.PresentationDocument;
import ag.ion.bion.officelayer.internal.spreadsheet.SpreadsheetDocument;
import ag.ion.bion.officelayer.internal.text.GlobalTextDocument;
import ag.ion.bion.officelayer.internal.text.TextDocument;
import ag.ion.bion.officelayer.internal.web.WebDocument;
import ag.ion.noa.internal.db.DatabaseDocument;
import ag.ion.noa.service.IServiceProvider;

import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.drawing.XDrawPagesSupplier;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XFrame;
import com.sun.star.io.XInputStream;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.presentation.XPresentationSupplier;
import com.sun.star.sdb.XOfficeDatabaseDocument;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.text.XTextDocument;
import com.sun.star.uno.UnoRuntime;

/**
 * Document loading helper class. 
 * 
 * @author Andreas Bröker
 * @version $Revision: 11724 $
 */
public class DocumentLoader {

  //----------------------------------------------------------------------------
  /**
   * Loads document from submitted URL.
   * 
   * @param serviceProvider the service provider to be used
   * @param URL URL of the document
   * 
   * @return loaded document
   * 
   * @throws Exception if an OpenOffice.org communication error occurs
   * @throws IOException if document can not be found
   */
  public static IDocument loadDocument(IServiceProvider serviceProvider, String URL)
      throws Exception, IOException {
	
	System.out.println("DocumentLoader: loadDocument(serviceProvider, URL) begin");
    return loadDocument(serviceProvider, URL, null);
  }

  //----------------------------------------------------------------------------
  /**
   * Loads document from submitted URL.
   * 
   * @param serviceProvider the service provider to be used
   * @param URL URL of the document
   * @param properties properties for OpenOffice.org
   * 
   * @return loaded document
   * 
   * @throws Exception if an OpenOffice.org communication error occurs
   * @throws IOException if document can not be found
   */
  public static IDocument loadDocument(IServiceProvider serviceProvider, String URL,
      PropertyValue[] properties) throws Exception, IOException {

	System.out.println("DocumentLoader: loadDocument(serviceProvider, URL, properties) begin");
    if (properties == null) {
      properties = new PropertyValue[0];
    }
    Object oDesktop = serviceProvider.createServiceWithContext("com.sun.star.frame.Desktop");
    XComponentLoader xComponentLoader = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class,
        oDesktop);
    return loadDocument(serviceProvider, xComponentLoader, URL, "_blank", 0, properties);
  }

  //----------------------------------------------------------------------------
  /**
   * Loads document on the basis of the submitted XInputStream implementation.
   * 
   * @param serviceProvider the service provider to be used
   * @param xInputStream OpenOffice.org XInputStream inplementation
   * 
   * @return loaded Document
   * 
   * @throws Exception if an OpenOffice.org communication error occurs
   * @throws IOException if document can not be found
   */
  public static IDocument loadDocument(IServiceProvider serviceProvider, XInputStream xInputStream)
      throws Exception, IOException {
	
	System.out.println("DocumentLoader: loadDocument(serviceProvider, xInputStream) begin");
    return loadDocument(serviceProvider, xInputStream, null);
  }

  //----------------------------------------------------------------------------
  /**
   * Loads document on the basis of the submitted XInputStream implementation.
   * 
   * @param serviceProvider the service provider to be used
   * @param xInputStream OpenOffice.org XInputStream inplementation
   * @param properties properties for OpenOffice.org
   * 
   * @return loaded Document
   * 
   * @throws Exception if an OpenOffice.org communication error occurs
   * @throws IOException if document can not be found
   */
  public static IDocument loadDocument(IServiceProvider serviceProvider, XInputStream xInputStream,
      PropertyValue[] properties) throws Exception, IOException {
	
	System.out.println("DocumentLoader: loadDocument(serviceProvider, xInputStream, properties) begin");

	if (properties == null) {
      properties = new PropertyValue[0];
    }
    PropertyValue[] newProperties = new PropertyValue[properties.length + 1];
    for (int i = 0; i < properties.length; i++) {
      newProperties[i] = properties[i];
    }
    newProperties[properties.length] = new PropertyValue();
    newProperties[properties.length].Name = "InputStream";
    newProperties[properties.length].Value = xInputStream;

    Object oDesktop = serviceProvider.createServiceWithContext("com.sun.star.frame.Desktop");
    XComponentLoader xComponentLoader = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class,
        oDesktop);
    return loadDocument(serviceProvider,
        xComponentLoader,
        "private:stream",
        "_blank",
        0,
        newProperties);
  }

  //----------------------------------------------------------------------------
  /**
   * Loads document from the submitted URL into the OpenOffice.org frame.
   * 
   * @param serviceProvider the service provider to be used
   * @param xFrame frame to used for document
   * @param URL URL of the document
   * @param searchFlags search flags for the target frame
   * @param properties properties for OpenOffice.org
   * 
   * @return loaded document
   * 
   * @throws Exception if an OpenOffice.org communication error occurs
   * @throws IOException if document can not be found
   * 
   * @author Joerg Sigle - added progress monitoring output and alternative code experiments.
   * @date 22.02.2012
   * 
   * @author Andreas Brueker
   */
  public static IDocument loadDocument(IServiceProvider serviceProvider, XFrame xFrame, String URL,
      int searchFlags, PropertyValue[] properties) throws Exception, IOException {
	  
	  System.out.println("DocumentLoader: loadDocument(serviceProvider, xFrame, URL, searchFlags, properties) begin");
	  
	  if (xFrame==null)		System.out.println("DocumentLoader: loadDocument(5): xFrame==null");
	  else {
		 System.out.println("DocumentLoader: loadDocument(5): xFrame="+xFrame.toString());
		 System.out.println("DocumentLoader: loadDocument(5): xFrame.getCreator()="+xFrame.getCreator());
		 System.out.println("DocumentLoader: loadDocument(5): xFrame.getName()="+xFrame.getName());
	  }
	  
	  if (properties==null)	System.out.println("DocumentLoader: loadDocument(5): properties==null");
	  else 	  				System.out.println("DocumentLoader: loadDocument(5): properties="+properties.toString());

    if (xFrame != null) {
      if (properties == null) {
        properties = new PropertyValue[0];
      }
      XComponentLoader xComponentLoader = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class,
          xFrame);
	  
      if (properties==null)	System.out.println("DocumentLoader: loadDocument(5): xComponentLoader==null");
	  else 	  				System.out.println("DocumentLoader: loadDocument(5): xComponentLoader="+xComponentLoader .toString());
      
      System.out.println("DocumentLoader: loadDocument(5): about to call and directly return loadDocument(6)...");

      return loadDocument(serviceProvider,
          xComponentLoader,
          URL,
          xFrame.getName(),
          searchFlags,
          properties);
    }

	  System.out.println("DocumentLoader: loadDocument(5): WARNING: about to return null");
    return null;
  }

  //----------------------------------------------------------------------------
  /**
   * Loads document into OpenOffice.org
   * 
   * @param serviceProvider the service provider to be used
   * @param xComponentLoader OpenOffice.org component loader
   * @param URL URL of the document
   * @param targetFrameName name of the OpenOffice.org target frame
   * @param searchFlags search flags for the target frame
   * @param properties properties for OpenOffice.org
   * 
   * @return loaded document
   * 
   * @throws Exception if an OpenOffice.org communication error occurs
   * @throws IOException if document can not be found
   *
   * @author Joerg Sigle - added progress monitoring output and alternative code experiments.
   * @date 22.02.2012
   * 
   * @author Andreas Brueker
   */
  private static IDocument loadDocument(IServiceProvider serviceProvider,
      XComponentLoader xComponentLoader, String URL, String targetFrameName, int searchFlags,
      PropertyValue[] properties) throws Exception, IOException {
	  
	 System.out.println("DocumentLoader: loadDocument(serviceProvider, xComponentLoader, URL, targetFramename, searchFlags, properties) begins");
	 System.out.println("DocumentLoader: loadDocument(6): checkMaxOpenDocuments()...");

    DocumentService.checkMaxOpenDocuments(serviceProvider);
	  
	  System.out.println("DocumentLoader: loadDocument(6): xComponentLoader.loadComponentFromURL(4)...");
	  if (URL==null)	    System.out.println("DocumentLoader: loadDocument(6): URL==null");
	  else 	  				System.out.println("DocumentLoader: loadDocument(6): URL="+URL.toString());
	  if (targetFrameName==null)	System.out.println("DocumentLoader: loadDocument(6): targetFrameName==null");
	  else 	  				System.out.println("DocumentLoader: loadDocument(6): targetFrameName="+targetFrameName.toString());
	  System.out.println("DocumentLoader: searchFlags="+searchFlags);
	  if (properties==null)	System.out.println("DocumentLoader: properties==null");
	  else 	  				System.out.println("DocumentLoader: properties="+properties.toString());
 
	  /**
	   * @author Joerg Sigle
	   * @date 22.02.2012
	   * Comment and alternative code to test behaviour if OpenOffice / LibreOffice
	   * shall appear in its own window, rather than inside a frame. 
	   */
	  //201202251835js: Für's Debugging mal den Aufruf durch Zuweisung von null ersetzt.
	  //Dann läuft es durch, auch wenn in OO/LO Dialoge geöffnet wären, aber natürlich wird das Dokument nicht in den Frame geladen.
	  //XComponent xComponent = null;	   
    XComponent xComponent = xComponentLoader.loadComponentFromURL(URL,
        targetFrameName,
        searchFlags,
        properties);
    if (xComponent != null) {
    	System.out.println("DocumentLoader: loadDocument(6): about call and directly return getDocument(3)...");
      return getDocument(xComponent, serviceProvider, properties);
    }
    
    System.out.println("DocumentLoader: loadDocument(6): about to throw IOException: Document not found");
    throw new IOException("Document not found.");
  }

  //----------------------------------------------------------------------------
  /**
   * Returns document on the basis of the submitted OpenOffice.org XComponent. Returns
   * null if the document can not be builded.
   * 
   * @param xComponent OpenOffice.org XComponent or null if the document can not be 
   * builded
   * @param serviceProvider the service provider to be used for the documents
   * @param intitialProperties the properties that were used loading the document
   * 
   * @return constructed document or null
   * 
   * @author Joerg Sigle - added progress monitoring output.
   * @date 22.02.2012
   * 
   * @author Andreas Brueker
   */
  public static IDocument getDocument(XComponent xComponent, IServiceProvider serviceProvider,
      PropertyValue[] intitialProperties) {
	System.out.println("DocumentLoader: getDocument(xComponent, serviceProvider, initialProperties) begin");
		
    if (intitialProperties == null) {
      intitialProperties = new PropertyValue[0];
    }
    IDocument document = null;
    
    System.out.println("DocumentLoader: getDocument(3): xServiceInfo=...UnoRuntime.queryInterface(2)...");
	
    XServiceInfo xServiceInfo = (XServiceInfo) UnoRuntime.queryInterface(XServiceInfo.class,
        xComponent);
	
	if (xServiceInfo==null)	System.out.println("DocumentLoader: getDocument(3): WARNING: xServiceInfo==null");
	else {
		System.out.println("DocumentLoader: getDocument(3): xServiceInfo="+xServiceInfo.toString());
		System.out.println("DocumentLoader: getDocument(3): xServiceInfo.getImplementationName()="+xServiceInfo.getImplementationName());
		System.out.println("DocumentLoader: getDocument(3): xServiceInfo.getSupportedServiceNames()="+xServiceInfo.getSupportedServiceNames());		
	}
	
    if (xServiceInfo.supportsService("com.sun.star.text.TextDocument")) {
    	System.out.println("DocumentLoader: getDocument(3): xServiceInfo supports com.sun.star.text.TextDocument");
    	System.out.println("DocumentLoader: getDocument(3): getting xTextDocument...");
    	
      XTextDocument xTextDocument = (XTextDocument) UnoRuntime.queryInterface(XTextDocument.class,
          xComponent);
    	
    	if (xTextDocument==null)	System.out.println("DocumentLoader: getDocument(3): WARNING: xTextDocument==null");
    	else 						System.out.println("DocumentLoader: getDocument(3): xTextDocument="+xTextDocument.toString());
    	
      if (xTextDocument != null) {
    		System.out.println("DocumentLoader: getDocument(3): allocating document from TextDocument(xTextDocument, initialProperties)...");
        document = new TextDocument(xTextDocument, intitialProperties);
      }
    }
    else if (xServiceInfo.supportsService("com.sun.star.sheet.SpreadsheetDocument")) {
      XSpreadsheetDocument xSpreadsheetDocument = (XSpreadsheetDocument) UnoRuntime.queryInterface(XSpreadsheetDocument.class,
          xComponent);
      if (xSpreadsheetDocument != null) {
        document = new SpreadsheetDocument(xSpreadsheetDocument, intitialProperties);
      }
    }
    else if (xServiceInfo.supportsService("com.sun.star.presentation.PresentationDocument")) {
      XPresentationSupplier presentationSupplier = (XPresentationSupplier) UnoRuntime.queryInterface(XPresentationSupplier.class,
          xComponent);
      if (presentationSupplier != null) {
        document = new PresentationDocument(presentationSupplier, intitialProperties);
      }
    }
    else if (xServiceInfo.supportsService("com.sun.star.drawing.DrawingDocument")) {
      XDrawPagesSupplier xDrawPagesSupplier = (XDrawPagesSupplier) UnoRuntime.queryInterface(XDrawPagesSupplier.class,
          xComponent);
      if (xDrawPagesSupplier != null) {
        document = new DrawingDocument(xDrawPagesSupplier, intitialProperties);
      }
    }
    else if (xServiceInfo.supportsService("com.sun.star.formula.FormulaProperties")) {
      XPropertySet xPropertySet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
          xComponent);
      if (xPropertySet != null) {
        document = new FormulaDocument(xPropertySet, intitialProperties);
      }
    }
    else if (xServiceInfo.supportsService("com.sun.star.text.WebDocument")) {
      XTextDocument xTextDocument = (XTextDocument) UnoRuntime.queryInterface(XTextDocument.class,
          xComponent);
      if (xTextDocument != null) {
        document = new WebDocument(xTextDocument, intitialProperties);
      }
    }
    else if (xServiceInfo.supportsService("com.sun.star.text.GlobalDocument")) {
      XTextDocument xTextDocument = (XTextDocument) UnoRuntime.queryInterface(XTextDocument.class,
          xComponent);
      if (xTextDocument != null) {
        document = new GlobalTextDocument(xTextDocument, intitialProperties);
      }
    }
    else if (xServiceInfo.supportsService("com.sun.star.sdb.OfficeDatabaseDocument")) {
      XOfficeDatabaseDocument xOfficeDatabaseDocument = (XOfficeDatabaseDocument) UnoRuntime.queryInterface(XOfficeDatabaseDocument.class,
          xComponent);
      if (xOfficeDatabaseDocument != null) {
        document = new DatabaseDocument(xOfficeDatabaseDocument, intitialProperties);
      }
    }
    
	if (document==null)	System.out.println("DocumentLoader: getDocument(3): WARNING: document==null");
	else				System.out.println("DocumentLoader: getDocument(3): document="+document.toString());

	System.out.println("DocumentLoader: getDocument(3): About to do some processing on hidden or non hidden frames...");
	
    if (document != null && document instanceof AbstractDocument) {
      ((AbstractDocument) document).setServiceProvider(serviceProvider);
      boolean isHidden = false;
      for (int i = 0; i < intitialProperties.length; i++) {
        if (intitialProperties[i].Name.equals("Hidden") && intitialProperties[i].Value.equals(Boolean.TRUE)) {
          isHidden = true;
          break;
        }
      }
      //XXX WORKAROUND: If you haven an app with more than one openoffice noa integrated frames, then the second frame and upwards is sometimes
      //not displayed under linux. The following lines of code works around this issue.
      if (!isHidden) {
        if (document.getServiceProvider() != null) {
          XWindow containerWindow = document.getFrame().getXFrame().getContainerWindow();
          containerWindow.setVisible(false);
          containerWindow.setVisible(true);
        }
      }
    }
    System.out.println("DocumentLoader: getDocument(3): About to return document...");
    return document;
  }

  //----------------------------------------------------------------------------

}