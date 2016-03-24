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

import org.eclipse.swt.widgets.Display;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Wrapper for a progress monitor. Every call to this progress monitor
 * will be delegated within an async operation to the wrapped progress monitor 
 * 
 * @author Andreas Bröker
 * @version $Revision: 9195 $
 * @date 28.06.2006
 */
public class AsyncProgressMonitorWrapper implements IProgressMonitor{

	private IProgressMonitor progressMonitor = null;
	
	private Display display = null;
	
  //----------------------------------------------------------------------------
	/**
	 * Constructs new AsyncProgressMonitorWrapper.
	 * 
	 * @param progressMonitor progress monitor to be wrapped
	 * @param display display to be used
	 * 
	 * @author Andreas Bröker
	 * @date 28.06.2006
	 */
	public AsyncProgressMonitorWrapper(IProgressMonitor progressMonitor, Display display) {
		assert progressMonitor != null;
		assert display != null;
		
		this.progressMonitor = progressMonitor;
		this.display = display;
	}
  //----------------------------------------------------------------------------
	/**
	 * Notifies that the main task is beginning.  This must only be called once
	 * on a given progress monitor instance.
	 * 
	 * @param name the name (or description) of the main task
	 * @param totalWork the total number of work units into which
	 *  the main task is been subdivided. If the value is <code>UNKNOWN</code> 
	 *  the implementation is free to indicate progress in a way which 
	 *  doesn't require the total number of work units in advance
	 * 
	 * @author Andreas Bröker
	 * @date 28.06.2006
	 */
	public void beginTask(final String name, final int totalWork) {
		display.asyncExec(new Runnable() {
			public void run() {
				progressMonitor.beginTask(name, totalWork);
			}			
		});
	}
  //----------------------------------------------------------------------------
	/**
	 * Notifies that the work is done; that is, either the main task is completed 
	 * or the user canceled it. This method may be called more than once 
	 * (implementations should be prepared to handle this case).
	 * 
	 * @author Andreas Bröker
	 * @date 28.06.2006
	 */
	public void done() {
		display.asyncExec(new Runnable() {
			public void run() {
				progressMonitor.done();
			}			
		});
	}
  //----------------------------------------------------------------------------
	/**
	 * Internal method to handle scaling correctly. This method
	 * must not be called by a client. Clients should 
	 * always use the method </code>worked(int)</code>.
	 * 
	 * @param work the amount of work done
	 * 
	 * @author Andreas Bröker
	 * @date 28.06.2006
	 */
	public void internalWorked(final double work) {
		display.asyncExec(new Runnable() {
			public void run() {
				progressMonitor.internalWorked(work);
			}			
		});
	}
  //----------------------------------------------------------------------------
	/**
	 * Returns whether cancelation of current operation has been requested.
	 * Long-running operations should poll to see if cancelation
	 * has been requested.
	 *
	 * @return <code>true</code> if cancellation has been requested,
	 *    and <code>false</code> otherwise
	 * 
	 * @author Andreas Bröker
	 * @date 28.06.2006
	 */
	public boolean isCanceled() {
		return progressMonitor.isCanceled();
	}
  //----------------------------------------------------------------------------
	/**
	 * Sets the cancel state to the given value.
	 * 
	 * @param value <code>true</code> indicates that cancelation has
	 *     been requested (but not necessarily acknowledged);
	 *     <code>false</code> clears this flag
	 * 
	 * @author Andreas Bröker
	 * @date 28.06.2006
	 */
	public void setCanceled(final boolean value) {
		display.asyncExec(new Runnable() {
			public void run() {
				progressMonitor.setCanceled(value);
			}			
		});
	}
  //----------------------------------------------------------------------------
	/**
	 * Sets the task name to the given value. This method is used to 
	 * restore the task label after a nested operation was executed. 
	 * Normally there is no need for clients to call this method.
	 *
	 * @param name the name (or description) of the main task
	 * 
	 * @author Andreas Bröker
	 * @date 28.06.2006
	 */
	public void setTaskName(final String name) {
		display.asyncExec(new Runnable() {
			public void run() {
				progressMonitor.setTaskName(name);
			}			
		});
	}
  //----------------------------------------------------------------------------
	/**
	 * Notifies that a subtask of the main task is beginning.
	 * Subtasks are optional; the main task might not have subtasks.
	 *
	 * @param name the name (or description) of the subtask
	 * 
	 * @author Andreas Bröker
	 * @date 28.06.2006
	 */
	public void subTask(final String name) {
		display.asyncExec(new Runnable() {
			public void run() {
				progressMonitor.subTask(name);
			}			
		});
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
	 * @date 28.06.2006
	 */
	public void worked(final int work) {
		display.asyncExec(new Runnable() {
			public void run() {
				progressMonitor.worked(work);
			}			
		});
	}
  //----------------------------------------------------------------------------

}