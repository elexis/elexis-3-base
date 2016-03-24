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
 * Last changes made by $Author: markus $, $Date: 2008-11-18 14:08:07 +0100 (Di, 18 Nov 2008) $
 */
package ag.ion.noa4e.ui.operations;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import ag.ion.bion.officelayer.application.IApplicationAssistant;
import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.workbench.office.editor.core.EditorCorePlugin;

/**
 * Operation in order to find OpenOffice.org application infos.
 * 
 * @author Andreas Bröker
 * @version $Revision: 11686 $
 */
public class FindApplicationInfosOperation implements IRunnableWithProgress {

  private ILazyApplicationInfo[] applicationInfos = ILazyApplicationInfo.EMPTY_LAZY_APPLICATION_INFOS_ARRAY;

  //----------------------------------------------------------------------------
  /**
   * Returns constructed application info objects.
   * 
   * @return constructed application info objects
   * 
   * @author Andreas Bröker
   */
  public ILazyApplicationInfo[] getApplicationsInfos() {
    return applicationInfos;
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
   *    it should wrap it inside an <code>InvocationTargetException</code>; runtime exceptions are automatically
   *  wrapped in an <code>InvocationTargetException</code> by the calling context
   * @exception InterruptedException if the operation detects a request to cancel, 
   *  using <code>IProgressMonitor.isCanceled()</code>, it should exit by throwing 
   *  <code>InterruptedException</code>
   *
   * @author Andreas Bröker
   */
  public void run(IProgressMonitor progressMonitor) throws InvocationTargetException,
      InterruptedException {
    try {
      IApplicationAssistant applicationAssistant = OfficeApplicationRuntime.getApplicationAssistant(EditorCorePlugin.getDefault().getLibrariesLocation());
      OfficeProgressMonitor officeProgressMonitor = new OfficeProgressMonitor(progressMonitor);
      applicationInfos = applicationAssistant.getLocalApplications(officeProgressMonitor);
      progressMonitor.done();
    }
    catch (Throwable throwable) {
      progressMonitor.done();
    }
  }
  //----------------------------------------------------------------------------

}