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
 * Last changes made by $Author: andreas $, $Date: 2006-08-07 13:09:58 +0200 (Mo, 07 Aug 2006) $
 */
package ag.ion.noa4e.ui.operations;

import ag.ion.bion.officelayer.runtime.IOfficeProgressMonitor;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Progress monitor for the office layer. This office progress monitor 
 * can be used in order to wrap an Eclipse <code>IProgressMonitor</code>.
 * 
 * @author Andreas Bröker
 * @version $Revision: 9195 $
 */
public class OfficeProgressMonitor implements IOfficeProgressMonitor {

  private IProgressMonitor progressMonitor = null;
  
  //----------------------------------------------------------------------------
  /**
   * Constructs new OfficeProgressMonitor.
   * 
   * @param progressMonitor progress monitor to be used
   * 
   * @author Andreas Bröker
   */
  public OfficeProgressMonitor(IProgressMonitor progressMonitor) {
    assert progressMonitor != null;
    this.progressMonitor = progressMonitor;
  }  
  //----------------------------------------------------------------------------
  /**
   * Informs the monitor about a new task.
   * 
   * @param name name of the task
   * @param totalWork total work of the task
   * 
   * @author Andreas Bröker
   */
  public void beginTask(String name, int totalWork) {
    progressMonitor.beginTask(name, totalWork);
  }
  //----------------------------------------------------------------------------
  /**
   * Notifies that a given number of work unit of the main task
   * has been completed. Note that this amount represents an
   * installment, as opposed to a cumulative amount of work done
   * to date.
   *
   * @param work the number of work units just completed
   * 
   * @author Andreas Bröker
   */
  public void worked(int work) {
    progressMonitor.worked(work);
  }
  //----------------------------------------------------------------------------
  /**
   * Informs the monitor about a new subtask.
   * 
   * @param name name of the substask
   * 
   * @author Andreas Bröker
   */
  public void beginSubTask(String name) {
    progressMonitor.subTask(name);
  }
  //----------------------------------------------------------------------------
  /**
   * Returns information whether the progress monitor needs
   * to be notified about the end of the main task.
   * 
   * @return information whether the progress monitor needs
   * to be notified about the end of the main task
   * 
   * @author Andreas Bröker
   */
  public boolean needsDone() {
    return false;
  }
  //----------------------------------------------------------------------------
  /**
   * Informs the progress monitor that the work is done.
   * 
   * @author Andreas Bröker
   */
  public void done() {
    progressMonitor.done();    
  }
  //----------------------------------------------------------------------------
  /**
   * Sets information whether the work was canceled.
   * 
   * @param canceled information whether the work was canceled
   * 
   * @author Andreas Bröker
   */
  public void setCanceled(boolean canceled) {
    progressMonitor.setCanceled(canceled);
  }
  //----------------------------------------------------------------------------
  /**
   * Returns information whether the work was canceled.
   * 
   * @return information whether the work was canceled
   * 
   * @author Andreas Bröker
   */
  public boolean isCanceled() {
    return progressMonitor.isCanceled();
  }
  //----------------------------------------------------------------------------
  
}