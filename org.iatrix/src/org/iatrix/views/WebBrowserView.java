/*******************************************************************************
 * Copyright (c) 2007-2013, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     D. Lutz - initial API and implementation
 * 
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalActions;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.ViewMenus;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;

/**
 * General Web Browser View.
 * 
 * Multiple views with different content can be opened.
 * 
 * @author Daniel Lutz <danlutz@watz.ch>
 */

public class WebBrowserView extends ViewPart implements ISaveablePart2 {
	public static final String ID = "org.iatrix.views.WebBrowserView"; //$NON-NLS-1$
	
	private static final String PROP_TITLE = "title";
	private static final String PROP_URL = "url";
	
	private Browser browser;
	
	private Action newViewAction;
	private Action editLocationAction;
	private Action editTitleAction;
	
	@Override
	public void createPartControl(Composite parent){
		browser = new Browser(parent, SWT.NONE);
		
		makeActions();
		ViewMenus menu = new ViewMenus(getViewSite());
		menu.createToolbar(newViewAction, editLocationAction);
		menu.createMenu(newViewAction, editLocationAction, editTitleAction);
		
		if (getViewSite().getSecondaryId() == null) {
			// default view, disable "editTitleAction"
			editTitleAction.setEnabled(false);
		}
		
		initialize();
	}
	
	private void makeActions(){
		newViewAction = new Action("Neues Fenster") {
			{
				setImageDescriptor(Images.IMG_ADDITEM.getImageDescriptor());
				setToolTipText("Ein neues Fenster öffnen");
			}
			
			@Override
			public void run(){
				try {
					getViewSite().getPage().showView(ID, StringTool.unique(ID),
						IWorkbenchPage.VIEW_ACTIVATE);
				} catch (PartInitException e) {
					ExHandler.handle(e);
				}
			}
		};
		
		editLocationAction = new Action("Adresse ändern") {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setToolTipText("Die Adresse der gewünschten Website angeben");
			}
			
			public void run(){
				String current = browser.getUrl();
				InputDialog dialog =
					new InputDialog(getSite().getShell(), "Adresse ändern",
						"Bitte geben Sie die neue Adresse ein", current, null);
				if (dialog.open() == InputDialog.OK) {
					String location = dialog.getValue();
					setPartProperty(PROP_URL, location);
					setLocation(location);
				}
			}
		};
		
		editTitleAction = new Action("Titel ändern") {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setToolTipText("Den Titel dieser View angeben");
			}
			
			public void run(){
				String current = getPartName();
				InputDialog dialog =
					new InputDialog(getSite().getShell(), "Titel ändern",
						"Bitte geben Sie den neuen Titel ein", current, null);
				if (dialog.open() == InputDialog.OK) {
					String title = dialog.getValue();
					setPartProperty(PROP_TITLE, title);
					setPartName(title);
				}
			}
		};
	}
	
	/**
	 * Set the browser's new url. Does nothing if null is passed as location.
	 * 
	 * @param location
	 *            the new url
	 */
	public void setLocation(String location){
		if (location != null) {
			browser.setUrl(location); // ignore errors
		}
	}
	
	/**
	 * Sets the initial title and location (as stored in the workspace configuration)
	 */
	public void initialize(){
		String title = getPartProperty(PROP_TITLE);
		if (title != null) {
			setPartName(title);
		}
		
		String location = getPartProperty(PROP_URL);
		setLocation(location);
	}
	
	@Override
	public void setFocus(){
		browser.setFocus();
	}
	
	/*
	 * Die folgenden 6 Methoden implementieren das Interface ISaveablePart2 Wir benötigen das
	 * Interface nur, um das Schliessen einer View zu verhindern, wenn die Perspektive fixiert ist.
	 * Gibt es da keine einfachere Methode?
	 */
	public int promptToSaveOnClose(){
		return GlobalActions.fixLayoutAction.isChecked() ? ISaveablePart2.CANCEL
				: ISaveablePart2.NO;
	}
	
	public void doSave(IProgressMonitor monitor){ /* leer */}
	
	public void doSaveAs(){ /* leer */}
	
	public boolean isDirty(){
		return true;
	}
	
	public boolean isSaveAsAllowed(){
		return false;
	}
	
	public boolean isSaveOnCloseNeeded(){
		return true;
	}
}
