/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.ole.win32.DbgOleControlSite;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.medelexis.text.msword.Activator;
import at.medevit.medelexis.text.msword.Messages;
import ch.elexis.core.data.status.ElexisStatus;
import ch.elexis.core.ui.text.ITextPlugin.Parameter;

/**
 * This class represents the Word UI inside the Workbench. It also provides access to the created
 * Word Document OLE/COM object.
 * 
 * @author thomashu
 * 
 */
public class OleWordSite {
	
	private Logger logger = LoggerFactory.getLogger(OleWordSite.class);

	private OleFrame oleFrame;
	private DbgOleControlSite oleClientSite;
	private OleAutomation oleClientSiteAuto;
	
	private CommunicationFile communicationFile;
	
	private boolean wordRedraw = true;
	
	public OleFrame getFrame(){
		return oleFrame;
	}
	
	public OleWordSite(Composite parent){
		try {
			communicationFile = new CommunicationFile();
			
			parent.getShell().setRedraw(false);
			Menu menuBar = parent.getShell().getMenuBar();
			parent.getShell().setMenuBar(null);
			
			oleFrame = new OleFrame(parent, SWT.CLIP_CHILDREN);
			oleFrame.setBackground(JFaceColors.getBannerBackground(oleFrame.getDisplay()));
			
			oleFrame.addDisposeListener(new DisposeListener() {
				
				@Override
				public void widgetDisposed(DisposeEvent arg0){
					setWordRedraw(true);
				}
			});
			
			parent.getShell().setMenuBar(menuBar);
			parent.getShell().setRedraw(true);
			
			reloadSite(null, null);
			
		} catch (SWTError e) {
			handleException(new IllegalStateException(Messages.OleWordSite_CouldNotStartWord));
		}
	}
	
	public CommunicationFile getCommunicationFile(){
		return communicationFile;
	}
	
	public void reloadSite(final File file, final Parameter parameter){
		synchronized (this) {
			OleRunnable runnable = new OleRunnable() {
				@Override
				public void run(){
					if (parameter == Parameter.NOUI) {
						oleFrame.setRedraw(false);
					}
					
					oleFrame.getShell().setRedraw(false);
					
					Menu menuBar = oleFrame.getShell().getMenuBar();
					oleFrame.getShell().setMenuBar(null);
					
					// If client site already opened dispose first
					if (oleClientSite != null && !oleClientSite.isDisposed()) {
						oleClientSiteAuto.dispose();
						oleClientSite.dispose();
					}
					try {
						if (file == null) {
							oleClientSite =
								new DbgOleControlSite(oleFrame, SWT.NONE, "Word.Document"); //$NON-NLS-1$
							oleClientSiteAuto = new OleAutomation(oleClientSite);
						} else {
							oleClientSite =
								new DbgOleControlSite(oleFrame, SWT.NONE, "Word.Document", file); //$NON-NLS-1$
							oleClientSiteAuto = new OleAutomation(oleClientSite);
						}
						setBackground(JFaceColors.getBannerBackground(oleFrame.getDisplay()));
						
						if (parameter == Parameter.NOUI) {
							setWordRedraw(false);
						} else {
							setWordRedraw(true);
						}
						
						show();
					} catch (SWTException e) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
							Messages.OleWordSite_CouldNotActivateCheck);
						logger.error("Word could not be created", e);
						oleFrame.setVisible(false);
					}
					
					oleFrame.getShell().setMenuBar(menuBar);
					oleFrame.getShell().setRedraw(true);
				}
			};
			run(runnable);
		}
	}
	
	private void setWordRedraw(boolean value){
		if (wordRedraw != value) {
			OleWrapperManager manager = new OleWrapperManager();
			OleWordApplication app = getApplication(manager);
			// refresh if turning redraw on
			if (value) {
				app.screenRefresh();
			}
			app.setScreenUpdating(value);
			
			manager.dispose();
			wordRedraw = value;
		}
	}
	
	public OleWordApplication getApplication(OleWrapperManager manager){
		synchronized (this) {
			OleAutomation returnAutomation =
				OleUtil.getOleAutomationProperty(oleClientSiteAuto, "Application"); //$NON-NLS-1$
			return (OleWordApplication) new OleWordApplication(this, returnAutomation,
				oleFrame.getDisplay(), manager);
		}
	}
	
	public OleWordDocument reload(File file, Parameter parameter){
		synchronized (this) {
			reloadSite(file, parameter);
			
			OleWrapperManager manager = new OleWrapperManager();
			OleWordApplication app = getApplication(manager);
			OleWordDocument doc = app.getActiveDocument(manager);
			manager.remove(doc);
			manager.dispose();
			return doc;
		}
	}
	
	public void deactivate(){
		oleClientSite.deactivateInPlaceClient();
	}
	
	private void show(){
		int ret = oleClientSite.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
		if (ret != OLE.S_OK) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
				Messages.OleWordSite_CouldNotActivate);
		}
	}
	
	public void setBackground(Color background){
		oleClientSite.setBackground(background);
	}
	
	public void setFocus(){
		oleClientSite.setFocus();
	}
	
	public void dispose(){
		synchronized (this) {
			OleRunnable runnable = new OleRunnable() {
				@Override
				public void run(){
					oleClientSite.dispose();
				}
			};
			run(runnable);
			
			communicationFile.dispose();
		}
	}
	
	public boolean isDisposed(){
		return oleClientSite.isDisposed();
	}
	
	protected void handleException(Exception e){
		if (e instanceof IllegalStateException) {
			StatusManager.getManager().handle(
				new ElexisStatus(ElexisStatus.ERROR, Activator.PLUGIN_ID,
					ElexisStatus.CODE_NOFEEDBACK, e.getMessage(), e, ElexisStatus.LOG_ERRORS),
				StatusManager.BLOCK);
		}
	}
	
	protected synchronized void run(OleRunnable runnable){
		try {
			// IMPORTANT: trigger event loop, else OLE/COM does not work correct (unpredictable
			// errors due to unhandled events of OLE/COM)
			while (Display.getCurrent().readAndDispatch())
				;
			runnable.run();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			if (ex instanceof RuntimeException)
				throw ((RuntimeException) ex);
		}
	}
}
