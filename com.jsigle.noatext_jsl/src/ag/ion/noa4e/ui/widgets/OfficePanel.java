/****************************************************************************
 *                                                                          *
 * NOAText_jsl based upon NOA (Nice Office Access) / noa-libre              *
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU General Public License Version 2.1                      *
 *                                                                          * 
 * GNU General Public License Version 2.1                                   *
 * ======================================================================== *
 * Portions Copyright 2011-2012 by Joerg Sigle                              *
 * Copyright 2003-2005 by IOn AG                                            *
 *                                                                          *
 * This program is free software: you can redistribute it and/or modify     *
 * it under the terms of the GNU General Public License as published by     *
 * the Free Software Foundation, either version 2.1 of the License.         *
 *                                                                          *
 *  This program is distributed in the hope that it will be useful,         *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of          *
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
 * Possibly, this version of the library should get a new revision number, currently used is 11724.
 ****************************************************************************/

/*
 * Last changes made by $Author: jsigle $, $Date: 2012-06-23 14:38:00 +0100 (Su, 23 Jun 2012) $
 */
package ag.ion.noa4e.ui.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;

import ag.ion.bion.officelayer.OSHelper;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.workbench.office.editor.core.EditorCorePlugin;
import ag.ion.noa4e.ui.NOAUIPlugin;
import ag.ion.noa4e.ui.operations.AsyncProgressMonitorWrapper;
import ag.ion.noa4e.ui.operations.LoadDocumentOperation;

/**
 * The office panel can be used in order to integrate the OpenOffice.org User
 * Interface into the SWT environment.
 * 
 * @author Andreas Br�ker
 * @version $Revision: 11742 $
 * @date 28.06.2006
 */
public class OfficePanel extends Composite {

	private IOfficeApplication officeApplication = null;

	private IFrame officeFrame = null;
	private StackLayout stackLayout = null;
	private Frame officeAWTFrame = null;
	private ProgressMonitorPart progressMonitorPart = null;

	private Composite baseComposite = null;
	private Composite progressComposite = null;
	private Composite officeComposite = null;

	private IDocument document = null;
	private IStatus lastLoadingStatus = null;

	private Thread loadingThread = null;

	private String currentDocumentPath = null;

	private boolean buildAlwaysNewFrames = false;
	private boolean showProgressIndicator = true;

	// ----------------------------------------------------------------------------
	/**
	 * Constructs new OfficePanel.
	 * 
	 * @param parent
	 *            parent to be used
	 * @param style
	 *            style to be used
	 * 
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	public OfficePanel(Composite parent, int style) {
		this(parent, style, null);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Constructs new OfficePanel.
	 * 
	 * @param parent
	 *            parent to be used
	 * @param style
	 *            style to be used
	 * @param officeApplication
	 *            the office application to be used, or null to use default
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Markus Kr�ger
	 * @date 08.04.2009
	 */
	public OfficePanel(Composite parent, int style,
			IOfficeApplication officeApplication) {
		super(parent, style);
		System.out.println("OfficePanel: OfficePanel(3): just returned from super(parent, style)");
		
		
		System.out.println("OfficePanel: OfficePanel(3): about to buildControls()");
		buildControls();
		System.out.println("OfficePanel: OfficePanel(3): just returned from buildControls()");
		
		if (officeApplication == null)	System.out.println("OfficePanel: OfficePanel(3): officeApplication==null");
		else							System.out.println("OfficePanel: OfficePanel(3): officeApplication=" + officeApplication.toString());

		if (officeApplication == null) {
			System.out.println("OfficePanel: OfficePanel(3): about to EditorCorePlugin.getDefault().getManagedLocalOfficeApplication()");
			officeApplication = EditorCorePlugin.getDefault()
					.getManagedLocalOfficeApplication();
		}

		if (officeApplication == null)	System.out.println("OfficePanel: OfficePanel(3): WARNING: about to return this.officeApplication==null");
		else							System.out.println("OfficePanel: OfficePanel(3): returning this.officeApplication=" + officeApplication.toString());
		this.officeApplication = officeApplication;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Returns current office frame. Returns null if an office frame is not
	 * available.
	 * 
	 * @return current office frame or null if an office frame is not available
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	public IFrame getFrame() {
		System.out.println("OfficePanel: getFrame()");
		if (officeFrame == null)	System.out.println("OfficePanel: getFrame(): WARNING: about to return officeFrame==null");
		else						System.out.println("OfficePanel: getFrame(): returning officeFrame=" + officeFrame.toString());
		
		return officeFrame;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Returns current document. Returns null if a document is not available.
	 * 
	 * @return current document. Returns null if a document is not available.
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Markus Kr�ger
	 * @date 19.03.2007
	 */
	public IDocument getDocument() {
		System.out.println("OfficePanel: getDocument()");

		if (document == null)	System.out.println("OfficePanel: getDocument(): WARNING: about to return document==null");
		else					System.out.println("OfficePanel: getDocument(): returning document=" + document.toString());

		return document;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Sets focus to the office panel.
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	public boolean setFocus() {
		System.out.println("OfficePanel: setFocus()");

		if (officeFrame == null)	System.out.println("OfficePanel: setFocus(): WARNING: officeFrame==null; will return super.setFocus() instead");
		else						System.out.println("OfficePanel: setFocus(): about to officeFrame.setFocus() for officeFrame=" + officeFrame.toString());
		
		if (officeFrame != null) {
			officeFrame.setFocus();
			return true;
		}
		return super.setFocus();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Sets information whether a new frame should be builded for new loaded
	 * documents. The default value is <code>false</code>.
	 * 
	 * @param buildAlwaysNewFrames
	 *            information whether a new frame should be builded for new
	 *            loaded documents
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	public void setBuildAlwaysNewFrames(boolean buildAlwaysNewFrames) {
		System.out.println("OfficePanel: setBuildAlwaysNewFrames(buildAlwaysNewFrames)");
		System.out.println("OfficePanel: setBuildAlwaysNewFrames(): about to set this.buildAlwaysNewFrames="+buildAlwaysNewFrames);
		
		this.buildAlwaysNewFrames = buildAlwaysNewFrames;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Sets information whether a progress indicator should be shown during
	 * document loading. The default value is <code>true</code>.
	 * 
	 * @param showProgressIndicator
	 *            information whether a progress indicator should be shown
	 *            during document loading
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	public void showProgressIndicator(boolean showProgressIndicator) {
		System.out.println("OfficePanel: showProgressIndicator(showProgressIndicator)");
		System.out.println("OfficePanel: showProgressIndicator(): about to set this.showProgressIndicator="+showProgressIndicator);
		this.showProgressIndicator = showProgressIndicator;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Loads document into the office panel.
	 * 
	 * @param fork
	 *            information whether the loading should be done in an own
	 *            thread
	 * @param documentPath
	 *            path of the document to be loaded
	 * @param documentDescriptor
	 *            document descriptor to be used
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	public final void loadDocument(boolean fork, final String documentPath,
			final IDocumentDescriptor documentDescriptor) {
		System.out.println("OfficePanel: loadDocument(fork, documentPath, documentDescriptor) begin");

		if (isDisposed()) {
			System.out.println("OfficePanel: loadDocument(3): WARNING: isDisposed==true; will return immediately.");
			return;
		}

		System.out.println("OfficePanel: loadDocument(3): Status before doing the work:");
		if (documentPath == null)			System.out.println("OfficePanel: loadDocument(3): documentPath==null");
		else								System.out.println("OfficePanel: loadDocument(3): documentPath=" + documentPath);
		if (currentDocumentPath == null)	System.out.println("OfficePanel: loadDocument(3): currentDocumentPath==null");
		else								System.out.println("OfficePanel: loadDocument(3): currentDocumentPath="+ currentDocumentPath);
		if (document == null)				System.out.println("OfficePanel: loadDocument(3): document==null");
		else								System.out.println("OfficePanel: loadDocument(3): document=" + document.toString());
		if (officeFrame == null)			System.out.println("OfficePanel: loadDocument(3): officeFrame==null");
		else								System.out.println("OfficePanel: loadDocument(3): officeFrame=" + officeFrame.toString());
		if (lastLoadingStatus == null)		System.out.println("OfficePanel: loadDocument(3): lastLoadingStatus==null");
		else								System.out.println("OfficePanel: loadDocument(3): lastLoadingStatus=" + lastLoadingStatus.toString());
		
		if (documentPath != null && (currentDocumentPath == null || !currentDocumentPath.equals(documentPath))) {
			try {

				System.out.println("OfficePanel: loadDocument(3): Setting currentDocumentPath=documentPath;");

				currentDocumentPath = documentPath;
				if (document != null && buildAlwaysNewFrames) {
					System.out.println("OfficePanel: loadDocument(3): closing currently open document...");
					document.close();
				}

				if (officeFrame == null || buildAlwaysNewFrames) {
					System.out.println("OfficePanel: loadDocument(3): activating new officeFrame...");
					officeFrame = activateNewFrame();
					if (officeFrame == null)	System.out.println("OfficePanel: loadDocument(3): WARNING: FAILED: still, officeFrame==null");
					else						System.out.println("OfficePanel: loadDocument(3): SUCCESS: officeFrame="+ officeFrame.toString());
				}

				if (!fork) {
					System.out.println("OfficePanel: loadDocument(3): fork=false");

					IProgressMonitor progressMonitor = getProgressMonitor();
					if (progressMonitor == null)	System.out.println("OfficePanel: loadDocument(3): progressMonitor==null");
					else							System.out.println("OfficePanel: loadDocument(3): progressMonitor="+ progressMonitor.toString());

					if (showProgressIndicator)
						showProgressIndicator();

					System.out.println("OfficePanel: loadDocument(3): loading document...");
					if (documentPath == null)		System.out.println("OfficePanel: loadDocument(3): documentPath==null");
					else							System.out.println("OfficePanel: loadDocument(3): documentPath="	+ documentPath.toString());
					if (documentDescriptor == null)	System.out.println("OfficePanel: loadDocument(3): documentDescriptor==null");
					else							System.out.println("OfficePanel: loadDocument(3): documentDescriptor=" + documentDescriptor.toString());
					if (progressMonitor == null)	System.out.println("OfficePanel: loadDocument(3): progressMonitor==null");
					else							System.out.println("OfficePanel: loadDocument(3): progressMonitor=" + progressMonitor.toString());

					loadDocument(documentPath, documentDescriptor,
							progressMonitor);

					if (document != null)
						lastLoadingStatus = Status.OK_STATUS;

					if (document == null)			System.out.println("OfficePanel: loadDocument(3): WARNING: FAILED: document==null");
					else							System.out.println("OfficePanel: loadDocument(3): SUCCESS: document=" + document.toString());

					if (showProgressIndicator) {
						hideProgressIndicator();
						showOfficeFrame();
					}
					
				} else {
					System.out.println("OfficePanel: loadDocument(3): fork=true");

					final Display display = Display.getCurrent();
					loadingThread = new Thread() {
						AsyncProgressMonitorWrapper asyncProgressMonitorWrapper = null;

						public void run() {
							display.asyncExec(new Runnable() {
								public void run() {
									if (!isDisposed())
										if (showProgressIndicator)
											showProgressIndicator();
								}
							});

							asyncProgressMonitorWrapper = new AsyncProgressMonitorWrapper(
									getProgressMonitor(), getDisplay());

							try {
								loadDocument(documentPath, documentDescriptor,
										asyncProgressMonitorWrapper);
								if (document != null)
									lastLoadingStatus = Status.OK_STATUS;
								display.asyncExec(new Runnable() {
									public void run() {
										if (showProgressIndicator) {
											hideProgressIndicator();
											showOfficeFrame();
										}
									}
								});
							} catch (CoreException coreException) {
								if (showProgressIndicator) {
									hideProgressIndicator();
									showOfficeFrame();
								}
								lastLoadingStatus = coreException.getStatus();
							}
						}
					};
					loadingThread.start();
				}
			} catch (Throwable throwable) {
				if (showProgressIndicator) {
					hideProgressIndicator();
					showOfficeFrame();
				}
				lastLoadingStatus = new Status(IStatus.ERROR,
						NOAUIPlugin.PLUGIN_ID, IStatus.ERROR,
						throwable.getMessage(), throwable);
			}
		}

		System.out.println("OfficePanel: loadDocument(3): Status after doing the work:");
		if (documentPath == null)			System.out.println("OfficePanel: loadDocument(3): documentPath==null");
		else								System.out.println("OfficePanel: loadDocument(3): documentPath=" + documentPath);
		if (currentDocumentPath == null)	System.out.println("OfficePanel: loadDocument(3): currentDocumentPath==null");
		else								System.out.println("OfficePanel: loadDocument(3): currentDocumentPath=" + currentDocumentPath);
		if (document == null)				System.out.println("OfficePanel: loadDocument(3): document==null");
		else								System.out.println("OfficePanel: loadDocument(3): document=" + document.toString());
		if (officeFrame == null)			System.out.println("OfficePanel: loadDocument(3): officeFrame==null");
		else								System.out.println("OfficePanel: loadDocument(3): officeFrame=" + officeFrame.toString());
		if (lastLoadingStatus == null)		System.out.println("OfficePanel: loadDocument(3): lastLoadingStatus==null");
		else								System.out.println("OfficePanel: loadDocument(3): lastLoadingStatus=" + lastLoadingStatus.toString());

	}

	// ----------------------------------------------------------------------------
	/**
	 * Disposes the office panel.
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	public void dispose() {
		System.out.println("OfficePanel: dispose");
		if (officeFrame != null) {
			try {
				officeFrame.close();
			} catch (Throwable throwable) {
				// do not consume
			}
		}
		super.dispose();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Returns status of the last document loading. Returns null if a status is
	 * not available.
	 * 
	 * @return status of the last document loading or null if a status is not
	 *         available
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	public IStatus getLastLoadingStatus() {
		System.out.println("OfficePanel: getLastLoadingStatus");
		return lastLoadingStatus;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Sets the layout which is associated with the receiver to be the argument
	 * which may be null.
	 * 
	 * @param layout
	 *            the receiver's new layout or null
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	public void setLayout(Layout layout) {
		System.out.println("OfficePanel: setLayout(layout) - default action is to do nothing");
		// default is to do nothing
	}

	// ----------------------------------------------------------------------------
	/**
	 * Is called after a document loading operation was done. This method can be
	 * overwriten by subclasses in order to do some work after a document
	 * loading operation was done.
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	protected void documentLoadingOperationDone() {
		System.out.println("OfficePanel: documentLoadingOperationDone() - default action is to do nothing");
		// default is to do nothing
	}

	// ----------------------------------------------------------------------------
	/**
	 * Returns progress monitor. Subclasses can overwrite this method in order
	 * to provide their own progress monitor.
	 * 
	 * @return progress monitor
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	protected IProgressMonitor getProgressMonitor() {
		System.out.println("OfficePanel: getProgressMonitor()");
		if (progressMonitorPart != null)
			return progressMonitorPart;
		return new NullProgressMonitor();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Starts office application.
	 * 
	 * @param officeApplication
	 *            office application to be started
	 * 
	 * @return information whether the office application was started
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	protected IStatus startOfficeApplication(
			IOfficeApplication officeApplication) {
		
		System.out.println("OfficePanel: StartOfficeApplication(officeApplication)");
		
		if (officeApplication == null)	System.out.println("OfficePanel: StartOfficeApplication(1): WARNING: officeApplication==null");
		else 							System.out.println("OfficePanel: StartOfficeApplication(1): officeApplication="+officeApplication.toString());
		
		if (getShell() == null)			System.out.println("OfficePanel: StartOfficeApplication(1): WARNING: getShell()==null");
		else 							System.out.println("OfficePanel: StartOfficeApplication(1): getShell()="+getShell().toString());
		
		return NOAUIPlugin.startLocalOfficeApplication(getShell(),officeApplication);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Builds progress indicator. Subclasses can overwrite this method in order
	 * to provide their own progress indicator.
	 * 
	 * @param parent
	 *            parent to be used
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	protected void buildProgressIndicator(Composite parent) {
		System.out.println("OfficePanel: buildProgressIndicator()");
		progressComposite = new Composite(parent, SWT.EMBEDDED);
		progressComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_DARK_GRAY));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginLeft = -5;
		gridLayout.marginBottom = -5;
		gridLayout.marginRight = -5;
		progressComposite.setLayout(gridLayout);

		Composite composite = new Composite(progressComposite, SWT.EMBEDDED);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_DARK_GRAY));

		Composite progressIndicator = new Composite(progressComposite,
				SWT.EMBEDDED);
		GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
		gridData.verticalAlignment = SWT.CENTER;
		progressIndicator.setLayoutData(gridData);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		progressIndicator.setLayout(gridLayout);
		progressMonitorPart = new ProgressMonitorPart(progressIndicator, null);
		gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
		gridData.verticalAlignment = SWT.CENTER;
		progressMonitorPart.setLayoutData(gridData);

		Link linkCancel = new Link(progressIndicator, SWT.FLAT);
		linkCancel
				.setText("<a>" + Messages.OfficePanel_link_text_cancel + "</a>"); //$NON-NLS-1$ //$NON-NLS-3$
		progressMonitorPart.attachToCancelComponent(linkCancel);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Shows progress indicator. Subclasses can overwrite this method in order
	 * to show their own progress indicator.
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	protected void showProgressIndicator() {
		System.out.println("OfficePanel: showProgressIndicator()");
		if (progressComposite == null)
			buildProgressIndicator(baseComposite);
		stackLayout.topControl = progressComposite;
		baseComposite.layout();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Hides the progress indicator. Subclasses can overwrite this method in
	 * order to hide their own progress indicator.
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	protected void hideProgressIndicator() {
		System.out.println("OfficePanel: hideProgressIndicator() - default action is to do nothing");
		// default is to do nothing
	}

	// ----------------------------------------------------------------------------
	/**
	 * Returns office application.
	 * 
	 * @return office application
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @author Markus Kr�ger
	 * @date 28.06.2006
	 */
	protected final IOfficeApplication getOfficeApplication() {
		System.out.println("OfficePanel: getOfficeApplication()");
		return officeApplication;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Loads document.
	 * 
	 * @param documentPath
	 *            document path to be used
	 * @param documentDescriptor
	 *            document descriptor to be used
	 * @param progressMonitor
	 *            progress monitor to be used
	 * 
	 * @throws CoreException
	 *             if the document can not be loaded
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	private void loadDocument(String documentPath,
			IDocumentDescriptor documentDescriptor,
			IProgressMonitor progressMonitor) throws CoreException {
		
		System.out.println("OfficePanel: loadDocument(documentPath, documentDescriptor, progressMonitor) begin");
		if (documentPath == null)	System.out.println("OfficePanel: loadDocument(3): WARNING: documentPath==null");
		else						System.out.println("OfficePanel: loadDocument(3): documentPath=" + documentPath);

		URL url = convertToURL(documentPath);
		System.out.println("OfficePanel: loadDocument(3): url=" + url.toString());
		try {
			System.out.println("OfficePanel: loadDocumen(3)t: trying to allocate new LoadDocumentOperation()...");
			LoadDocumentOperation loadDocumentOperation = new LoadDocumentOperation(
					null, getOfficeApplication(), officeFrame, url,
					documentDescriptor);
			if (loadDocumentOperation==null)	System.out.println("OfficePanel: loadDocument(3): WARNING: loadDocumentOperation==null");
			else								System.out.println("OfficePanel: loadDocument(3): SUCCESS: loadDocumentOperation= "+loadDocumentOperation.toString());
			
			System.out.println("OfficePanel: loadDocument(3): trying loadDocumentOperation.run(progressMonitor)...");
			loadDocumentOperation.run(progressMonitor);
			System.out.println("OfficePanel: loadDocument(3): trying document=loadDocumentOperation.getDocument()...");
			document = loadDocumentOperation.getDocument();
		} catch (InvocationTargetException invocationTargetException) {
			System.out.println("OfficePanel: loadDocument(3): FAILURE: caught InvocationTargetException during loadDocumentOperation");
			documentLoadingOperationDone();
			throw new CoreException(new Status(IStatus.ERROR,
					NOAUIPlugin.PLUGIN_ID, IStatus.ERROR,
					invocationTargetException.getCause().getMessage(),
					invocationTargetException.getCause()));
		} catch (InterruptedException interruptedException) {
			// the operation was aborted
			System.out.println("OfficePanel: loadDocument(3): FAILURE: caught InterruptedException during loadDocumentOperation");
		}
		
		System.out.println("OfficePanel: loadDocument(3): loadDocumentOperationDone()...");
		documentLoadingOperationDone();
		System.out.println("OfficePanel: loadDocument(3) end");
	}

	// ----------------------------------------------------------------------------
	/**
	 * Shows office frame.
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	private void showOfficeFrame() {
		System.out.println("OfficePanel: showOfficeFrame(): begin");
		
		System.out.println("OfficePanel: showOfficeFrame(): baseComposite.isDisposed()="+baseComposite.isDisposed());
		if (officeComposite==null)	System.out.println("OfficePanel: showOfficeFrame(): WARNING: officeComposite==null");
		else						System.out.println("OfficePanel: showOfficeFrame(): officeComposite="+officeComposite.toString());
		
		if (!baseComposite.isDisposed()) {
			stackLayout.topControl = officeComposite;
			baseComposite.layout();
			officeComposite.layout();
			
		System.out.println("OfficePanel: showOfficeFrame(): end");
		}
	}

	// ----------------------------------------------------------------------------
	/**
	 * Builds controls of the office panel.
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	private void buildControls() {
		System.out.println("OfficePanel: buildControls()");
		super.setLayout(new GridLayout());
		baseComposite = new Composite(this, SWT.EMBEDDED);
		baseComposite
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		stackLayout = new StackLayout();
		stackLayout.marginHeight = -5;
		stackLayout.marginWidth = -5;
		baseComposite.setLayout(stackLayout);
		baseComposite.setBackground(this.getParent().getBackground());

		if (!showProgressIndicator)
			buildProgressIndicator(this);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Activates a new office frame.
	 * 
	 * @return new builded office frame
	 * 
	 * @throws CoreException
	 *             if a new office frame can not be activated
	 * 
     * @author Joerg Sigle - added progress monitoring code
     * @date 22.05.2012
     *
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	private IFrame activateNewFrame() throws CoreException {
		System.out.println("OfficePanel: activateNewFrame()");

		Control oldOfficeComposite = stackLayout.topControl;
		Frame oldOfficeAWTFrame = officeAWTFrame;

		officeComposite = new Composite(baseComposite, SWT.EMBEDDED);
		officeComposite.setBackground(this.getBackground());
		officeComposite.setLayout(new GridLayout());

		try {
			System.out.println("OfficePanel: activateNewFrame(): Trying...");

			officeAWTFrame = SWT_AWT.new_Frame(officeComposite);
			officeAWTFrame.setVisible(true);
			officeAWTFrame.setBackground(Color.GRAY);
			Panel officeAWTPanel = new Panel();
			officeAWTPanel.setLayout(new BorderLayout());
			officeAWTPanel.setVisible(true);
			officeAWTFrame.add(officeAWTPanel);

			if (!getOfficeApplication().isActive()) {
				System.out.println("OfficePanel: activateNewFrame(): !getOfficeApplication().isActive()...");
				System.out.println("OfficePanel: activateNewFrame(): startOfficeApplication(getOfficeApplication)...");
								
				IStatus status = startOfficeApplication(getOfficeApplication());

				if (status==null)	System.out.println("OfficePanel: activateNewFrame(): resulting status==null");
				else				System.out.println("OfficePanel: activateNewFrame(): resulting status="+status.toString());

				if (status.getSeverity() == IStatus.ERROR) {
					System.out.println("OfficePanel: activateNewFrame(): WARNING: status.getSeverity()==IStatus.ERROR");
					throw new CoreException(status);
				}
			}

			if (isDisposed()) {
				System.out.println("OfficePanel: activateNewFrame(): isDisposed() ... throwing");
				
				throw new CoreException(new Status(IStatus.ERROR,
						NOAUIPlugin.PLUGIN_ID, IStatus.ERROR,
						"Widget disposed", null)); //$NON-NLS-1$
			}

			IFrame newOfficeFrame = getOfficeApplication().getDesktopService()
					.constructNewOfficeFrame(officeAWTFrame);

			if (oldOfficeAWTFrame != null)
				oldOfficeAWTFrame.dispose();
			if (oldOfficeComposite != null)
				oldOfficeComposite.dispose();

			stackLayout.topControl = officeComposite;
			baseComposite.layout();

			if (newOfficeFrame == null)		System.out.println("OfficePanel: activateNewFrame(): WARNING: Returning newOfficeFrame==null");
			else							System.out.println("OfficePanel: activateNewFrame(): Returning newOfficeFrame=" + newOfficeFrame.toString());

			return newOfficeFrame;
		} catch (Throwable throwable) {
			System.out
					.println("OfficePanel: activateNewFrame(): CATCHING - SORRY...");
			throw new CoreException(new Status(IStatus.ERROR,
					NOAUIPlugin.PLUGIN_ID, IStatus.ERROR,
					throwable.getMessage(), throwable));
		}
	}

	// ----------------------------------------------------------------------------
	/**
	 * Converts the submitted document path an URL.
	 * 
	 * @param documentPath
	 *            document path to be used
	 * 
	 * @return converted document path
	 * 
	 * @throws CoreException
	 * 
	 * @author Joerg Sigle
	 * @date 201202192307
	 * @date 201108222324 Modified the code got with noa 2.2.3 from ag.ion:
	 *       Changed the number of slashes for the Windows OS similar to
	 *       previouly discovered modification requirement in the
	 *       OfficePanel.java as downloaded with multiple Elexis code bases for
	 *       building in Windows Eclipse. Please note: Elexis code original has
	 *       5 slashes in Windows Portion; Working js modification to that keeps
	 *       3 slashes in Windows Portion; The original ag.ion version noa
	 *       2.2.3, noa4e 2.0.14 now has only ONE slash in Windows Portion?!?!
	 * 
	 *       See more comments below.
	 * 
	 *       Also added progress monitoring code.
	 * 
	 * @author Andreas Br�ker
	 * @date 28.06.2006
	 */
	private URL convertToURL(String documentPath) throws CoreException {
		System.out
				.println("OfficePanel: convertToURL(documentPath) - modified by js re. Windows part");
		System.out.println("OfficePanel: convertToURL(1): TO DO: Please note that the correction-mod may not be necessary any more in noa4e 2.0.14 (js)");
		System.out.println("OfficePanel: convertToURL(1): Now trying conversion; if it succeeds, will return immediately thereafter.");

		try {

			/*
			 * if (Debug.DEBUG) { //$NON-NLS-1$ //$NON-NLS-2$ return new
			 * URL("file:/" + documentPath); //$NON-NLS-1$ }
			 */
			// 201108222324 Joerg Sigle http://www.jsigle.com
			// For revision 4960..4974, on win32, Eclipse 3.6.2 Mercurial 1.8
			// JavaSE 1.6 or so.
			// Line +2 after this comment had: "file://///" with 5 slashes,
			// When an OpenOffice document was opened in Elexis,
			// this would cause an error message:
			// ************************************************************
			// Error:
			// URL seems to be an unsupported one.
			// file://///c:/documen~/username/local~/temp/noa1234567890.odt
			// ************************************************************
			// (with similar URL/filename), rendering NOAText unusable.
			// It must have "file:///" with 3 slashes instead.
			// I have not tested the other line (maybe for Linux/Mac systems?)
			// The unchanged code in Elexis probably was:
			//if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) { //$NON-NLS-1$ //$NON-NLS-2$
			//     return new URL("file://///" + documentPath); //$NON-NLS-1$
			// And the corrected, working with outdated noa and OO 2.0.3 version
			// in Elexis was:
			//if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) { //$NON-NLS-1$ //$NON-NLS-2$
			//       return new URL("file:///" + documentPath); //$NON-NLS-1$
			//
			// 201202192311 Joerg Sigle http://www.jsigle.com
			// NOW, with the updated noa 2.2.3, noa4e 2.0.14,
			// freshly obtained from ag.ion, I see NO colon, and only ONE slash
			// in the Windows portion?
			// N.B.: The Linux Portion has 4 slashes in all versions.
			//return new URL("file", "/", documentPath); //$NON-NLS-1$ //$NON-NLS-2$
			// I'm changing it now to:
			//return new URL("file:///" + documentPath); //$NON-NLS-1$ //$NON-NLS-2$
			// PLEASE NOTE, that URL() may have at least two different
			// constructors,
			// so the code found in noa4e 2.0.14 may indeed be ok,
			// because "file", "/", documentPath are supplied as 3 strings
			// so a different constructor may handle them properly.
			// That could mean that ag.ion have corrected the problem I found in
			// the meantime as well.

			System.out.println("OfficePanel: convertToURL(1): FRAGE: ****************************************************************************************");
			System.out.println("OfficePanel: convertToURL(1): FRAGE: Müssen eigentlich wirklich mehrfach im Verlauf mehrere / vorne eingefügt werden?");
			System.out.println("OfficePanel: convertToURL(1): FRAGE: Die Zahl der führenden (Back)Slashes scheint nämlich hier und später nochmal zu wachsen.");
			System.out.println("OfficePanel: convertToURL(1): FRAGE: Das mag ohne Auswirkungen bleiben - aber ist es notwendig?");
			System.out.println("OfficePanel: convertToURL(1): FRAGE: ****************************************************************************************");

			if (OSHelper.IS_WINDOWS) {
				System.out.println("OfficePanel: convertToURL(1): For "+OSHelper.OS_NAME+": adding file:/// in front of documentPath");
				
				return new URL("file:///" + documentPath); //$NON-NLS-1$ //$NON-NLS-2$
			}
			System.out.println("OfficePanel: convertToURL(1): For "+OSHelper.OS_NAME+": adding file:// in front of documentPath");
			//System.out.println("OfficePanel: convertToURL: For "+OSHelper.OS_NAME+": ************************************");
			//System.out.println("OfficePanel: convertToURL: For "+OSHelper.OS_NAME+": DELIBERATELY ADDING NONSENSE AS WELL");
			//System.out.println("OfficePanel: convertToURL: For "+OSHelper.OS_NAME+": ************************************");
			//return new URL("file:////NONSENSE_TO_MAKE_THIS_FAIL" + documentPath); //$NON-NLS-1$

			System.out.println("OfficePanel: convertToURL(1): In Linux scheint das zu funktionieren mit vorangestelltem:   file:////, file:////, file:///, file://");
			System.out.println("OfficePanel: convertToURL(1): In Linux scheint es aber NICHT zu gehen mit vorangestelltem: file:/");
			System.out.println("OfficePanel: convertToURL(1): Ursprünglich im Code war für Linux:                          file:////");
			System.out.println("OfficePanel: convertToURL(1): Dabei bedeutet funktionieren: Nacher gibt's ein document!=null.");
			
			return new URL("file://" + documentPath); //$NON-NLS-1$
		} catch (Throwable throwable) {
			System.out.println("OfficePanel: convertToURL(1): FAILURE - catching throwable.");

			throw new CoreException(new Status(IStatus.ERROR,
					NOAUIPlugin.PLUGIN_ID, IStatus.ERROR,
					throwable.getMessage(), throwable));
		}
	}

	// ----------------------------------------------------------------------------

}