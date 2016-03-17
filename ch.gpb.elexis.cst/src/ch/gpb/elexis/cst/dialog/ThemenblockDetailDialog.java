/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.dialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.statushandlers.StatusManager;

import ch.gpb.elexis.cst.preferences.Messages;

/**
 * @author daniel ludin ludin@swissonline.ch
 * 27.06.2015
 * 
 */

public class ThemenblockDetailDialog extends TitleAreaDialog {

	protected Text txtName;
	protected Text txtDescription;

	protected String name;
	private String validFrom;
	private String validTo;
	protected String description;
	private CDateTime cdtValidFrom;
	private CDateTime cdtValidTo;

	public ThemenblockDetailDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.Cst_Text_Gruppen_details_editieren);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		createFirstName(container);
		createLastName(container);

		return area;
	}

	private void createFirstName(Composite container) {
		Label lbtFirstName = new Label(container, SWT.NONE);
		lbtFirstName.setText(Messages.CstLaborPrefs_name);

		GridData dataFirstName = new GridData();
		dataFirstName.grabExcessHorizontalSpace = true;
		dataFirstName.horizontalAlignment = GridData.FILL;

		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(dataFirstName);
	}

	private void createLastName(Composite container) {
		Label lbtLastName = new Label(container, SWT.NONE);
		lbtLastName.setText(Messages.CstCategory_description);

		GridData dataLastName = new GridData();
		dataLastName.grabExcessHorizontalSpace = true;
		dataLastName.horizontalAlignment = GridData.FILL;
		
		txtDescription = new Text(container, SWT.BORDER);
		txtDescription.setLayoutData(dataLastName);
	}
	
	private Date getDateFromCompact(String sDate){
		DateFormat df = new SimpleDateFormat("yyyyMMdd");

		try {
			return df.parse(sDate);
		} catch (ParseException e) {
			Status status = new Status(IStatus.WARNING, "ch.gbp.elexis.cst", e.getLocalizedMessage());
			StatusManager.getManager().handle(status, StatusManager.LOG);
		}
		return null;
	}

	
	@Override
	protected boolean isResizable() {
		return true;
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	protected void saveInput() {
		name = txtName.getText();
		description = txtDescription.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setName(String name) {
		this.name = name;
		this.txtName.setText(name);
	}

	public void setDescription(String description) {
		this.description = description;
		this.txtDescription.setText(description);
	}

	public String getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
		cdtValidFrom.setSelection(getDateFromCompact(validFrom));
	}

	public String getValidTo() {
		return validTo;
	}

	public void setValidTo(String validTo) {
		this.validTo = validTo;
		cdtValidTo.setSelection(getDateFromCompact(validTo));
	}
}