/****************************************************************************
 *                                                                          *
 * NOA (Nice Office Access)                                     						*
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU Lesser General Public License Version 2.1.              *
 *                                                                          * 
 * GNU Lesser General Public License Version 2.1                            *
 * ======================================================================== *
 * Copyright 2003-2006 by IOn AG                                            *
 *                                                                          *
 * This library is free software; you can redistribute it and/or            *
 * modify it under the terms of the GNU Lesser General Public               *
 * License version 2.1, as published by the Free Software Foundation.       *
 *                                                                          *
 * This library is distributed in the hope that it will be useful,          *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
 * Lesser General Public License for more details.                          *
 *                                                                          *
 * You should have received a copy of the GNU Lesser General Public         *
 * License along with this library; if not, write to the Free Software      *
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,                    *
 * MA  02111-1307  USA                                                      *
 *                                                                          *
 * Contact us:                                                              *
 *  http://www.ion.ag																												*
 *  http://ubion.ion.ag                                                     *
 *  info@ion.ag                                                             *
 *                                                                          *
 ****************************************************************************/

/*
 * Last changes made by $Author: markus $, $Date: 2010-07-07 10:59:40 +0200 (Mi, 07 Jul 2010) $
 */
package ag.ion.noa4e.ui.operations;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.awt.SWT_AWT;

import ag.ion.bion.officelayer.OSHelper;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.runtime.IOfficeProgressMonitor;
import ag.ion.noa4e.ui.NOAUIPlugin;

/**
 * Operation in order to activate OpenOffice.org application.
 * 
 * @author Andreas Br�ker
 * @version $Revision: 11742 $
 */
public class ActivateOfficeApplicationOperation implements IRunnableWithProgress {

  private IOfficeApplication     officeApplication     = null;

  private InternalThread         internalThread        = null;

  private IOfficeProgressMonitor officeProgressMonitor = null;

  //----------------------------------------------------------------------------
  /**
   * Internal thread class in order start the office application.
   * 
   * @author Andreas Br�ker
   */
  private class InternalThread extends Thread {

    private OfficeApplicationException officeApplicationException = null;

    private boolean                    done                       = false;

    //----------------------------------------------------------------------------
    /**
     * Executes thread logic.
     * 
     * @author Andreas Br�ker
     */
    public void run() {
       

      System.out.println("ActivateOfficeApplication: trying to run inside ProgressMonitorDialog...");
  	  if (officeApplication==null)		System.out.println("ActivateOfficeApplication: officeApplication==null");
      else								System.out.println("ActivateOfficeApplication: officeApplication="+officeApplication.toString());
  	  if (officeProgressMonitor==null)	System.out.println("ActivateOfficeApplication: officeProgressMonitor==null");
      else								System.out.println("ActivateOfficeApplication: officeProgressMonitor="+officeProgressMonitor.toString());

        
      try {
        System.out.println("ActivateOfficeApplication: run inside ProgressMonitorDialog: officeApplication.activate() trying:");

        officeApplication.activate(officeProgressMonitor);
        done = true;

        System.out.println("ActivateOfficeApplication: run inside ProgressMonitorDialog: officeApplication.activate() done.");
      }
      catch (OfficeApplicationException officeApplicationException) {
        this.officeApplicationException = officeApplicationException;

        System.out.println("ActivateOfficeApplication: WARNING: Caught OfficeApplicationException:");
    	if (this.officeApplicationException==null)	System.out.println("this.officeApplicationException==null");
        else										System.out.println("this.officeApplicationException="+this.officeApplicationException.toString());

      }

      System.out.println("ActivateOfficeApplication: run inside ProgressMonitorDialog: end");
    }

    //----------------------------------------------------------------------------
    /**
     * Returns OfficeApplicationException exception. Returns null if
     * no exception was thrown.
     * 
     * @return OfficeApplicationException - returns null if
     * no exception was thrown
     * 
     * @author Andreas Br�ker
     */
    public OfficeApplicationException getOfficeApplicationException() {
      return officeApplicationException;
    }

    //----------------------------------------------------------------------------
    /**
     * Returns information whether the thread has finished his
     * work.
     * 
     * @return information whether the thread has finished his
     * work
     * 
     * @author Andreas Br�ker
     */
    public boolean done() {
      if (officeApplicationException != null)
        return true;
      return done;
    }
    //----------------------------------------------------------------------------
  }

  //----------------------------------------------------------------------------

  //----------------------------------------------------------------------------
  /**
   * Constructs new StartOfficeApplicationOperation.
   * 
   * @param officeApplication office application to be started
   * 
   * @author Andreas Br�ker
   */
  public ActivateOfficeApplicationOperation(IOfficeApplication officeApplication) {
    assert officeApplication != null;
    this.officeApplication = officeApplication;
  }

  //----------------------------------------------------------------------------
  /**
   * Returns OfficeApplicationException exception. Returns null if
   * no exception was thrown.
   * 
   * @return OfficeApplicationException - returns null if
   * no exception was thrown
   * 
   * @author Andreas Br�ker
   */
  public OfficeApplicationException getOfficeApplicationException() {
    return internalThread.getOfficeApplicationException();
  }

  //----------------------------------------------------------------------------
  /**
   * Runs this operation.  Progress should be reported to the given progress monitor.
   * This method is usually invoked by an <code>IRunnableContext</code>'s <code>run</code> method,
   * which supplies the progress monitor.
   * A request to cancel the operation should be honored and acknowledged 
   * by throwing <code>InterruptedException</code>.
   *
   * @param progressMonitor the progress monitor to use to display progress and receive
   *   requests for cancelation
   * 
   * @exception InvocationTargetException if the run method must propagate a checked exception,
   *  it should wrap it inside an <code>InvocationTargetException</code>; runtime exceptions are automatically
   *  wrapped in an <code>InvocationTargetException</code> by the calling context
   * @exception InterruptedException if the operation detects a request to cancel, 
   *  using <code>IProgressMonitor.isCanceled()</code>, it should exit by throwing 
   *  <code>InterruptedException</code>
   * 
   * @author Andreas Br�ker
   */
  public void run(IProgressMonitor progressMonitor) throws InvocationTargetException,
      InterruptedException {
    /**
     * Tricky workaround. The OpenOffice.org OfficeBean library can not find the JRE/ JDK library
     * jawt itself. The class SWT_AWT uses this native library too. Therefore code from SWT_AWT is used
     * to load the library.
     */

	  System.out.println("ActivateOfficeApplication.TrickyWorkaround(progressMonitor): begin");
      System.out.println("ActivateOfficeApplication: OSHelper.OS_NAME =    "+OSHelper.OS_NAME);
      System.out.println("ActivateOfficeApplication: OSHelper.IS_WINDOWS = "+OSHelper.IS_WINDOWS);
      System.out.println("ActivateOfficeApplication: OSHelper.IS_LINUX =   "+OSHelper.IS_LINUX);
      System.out.println("ActivateOfficeApplication: OSHelper.IS_MAC =     "+OSHelper.IS_MAC);
      
      //2013012190110js: Tested: Trying to use this workaround for Linux as well: No improvement.
      
      if (OSHelper.IS_WINDOWS) { //$NON-NLS-1$
      try {
        SWT_AWT.new_Shell(NOAUIPlugin.getDefault().getWorkbench().getDisplay(),
            new java.awt.Canvas());
      }
      catch (Throwable throwable) {
        //do nothing
      }
	
      System.out.println("ActivateOfficeApplication.TrickyWorkaround(progressMonitor): end");

    }

	System.out.println("ActivateOfficeApplication: about to start internal thread...");

	internalThread = new InternalThread();
    officeProgressMonitor = new OfficeProgressMonitor(progressMonitor);
    internalThread.start();
    while (!internalThread.done()) {
      Thread.sleep(150);
      if (progressMonitor.isCanceled())
        throw new InterruptedException(Messages.ActivateOfficeApplicationOperation_exception_message_interrupted);
    }

    if (progressMonitor.isCanceled())
      throw new InterruptedException(Messages.ActivateOfficeApplicationOperation_exception_message_interrupted);

    System.out.println("ActivateOfficeApplication: about to end");

    progressMonitor.done();
  }
  //----------------------------------------------------------------------------

}