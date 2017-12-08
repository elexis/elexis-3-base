/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.ui.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.ui.Messages;
import at.medevit.elexis.ehc.ui.util.CDALoader;
import ch.elexis.core.ui.UiDesk;

public class EHealthConnectorView extends ViewPart {
	public final static String ID = "at.medevit.elexis.eHealthConnectorView";
	
	private static Logger log = LoggerFactory.getLogger(EHealthConnectorView.class);
	private static Image circle = ResourceManager.getPluginImage("at.medevit.elexis.ehc.ui",
		"icons/arrow-circle.png");
	
	private Text txtUrl;
	private Browser browser;
	
	private CDALoader cdaLoader;
	private File displayedReport;
	
	public EHealthConnectorView(){
		cdaLoader = new CDALoader();
	}
	
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout());
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(2, false));
		
		txtUrl = new Text(composite, SWT.BORDER);
		txtUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtUrl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e){
				if (e.keyCode == SWT.CR) {
					displayReport(txtUrl.getText());
				}
			}
		});
		Button btnShow = new Button(composite, SWT.PUSH);
		btnShow.setImage(circle);
		btnShow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				displayReport(txtUrl.getText());
			}
		});
		
		browser = new Browser(composite, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
	}
	
	/**
	 * display the given CDA report
	 * 
	 * @param path
	 *            of the CDA report file
	 */
	public void displayReport(String path){
		InputStream inputStream;
		try {
			// try file first, next try resolving via url
			try {
				inputStream = new FileInputStream(path);
				displayReport(inputStream, path);
			} catch (FileNotFoundException fne) {
				inputStream = new URL(path).openStream();
				displayReport(inputStream, "");
			}
			
		} catch (IOException e) {
			log.warn("Could not resolve CDA report on path [" + path + "]", e);
			MessageDialog.openError(UiDesk.getTopShell(), Messages.Dlg_ResolveError,
				Messages.Dlg_ResolveErrorMsg + "[" + path + "]");
		}
	}
	
	/**
	 * loads cda file an displays in in the browser
	 * 
	 * @param inputStream
	 *            stream
	 * @param path
	 *            if available file path (can be Null or empty)
	 */
	public void displayReport(InputStream inputStream, String path){
		if (path == null) {
			path = "";
		}
		displayedReport = cdaLoader.buildXmlDocument(inputStream, path);
		browser.setUrl(displayedReport.getAbsolutePath());
	}
	
	public InputStream getDisplayedReport(){
		if (displayedReport != null && displayedReport.exists()) {
			try {
				return new FileInputStream(displayedReport);
			} catch (FileNotFoundException e) {
				log.error("Could not open displayed report.", e);
			}
		}
		return null;
	}
	
	@Override
	public void setFocus(){
		txtUrl.setFocus();
	}
}
