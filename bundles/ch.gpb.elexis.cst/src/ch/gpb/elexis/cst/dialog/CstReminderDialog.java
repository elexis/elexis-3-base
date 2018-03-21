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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Mandant;
import ch.gpb.elexis.cst.Activator;
import ch.gpb.elexis.cst.data.CstProfile;
import ch.gpb.elexis.cst.data.CstStateItem.StateType;
import ch.gpb.elexis.cst.widget.ImageCombo;

/**
 * @author daniel ludin ludin@swissonline.ch
 * 27.06.2015
 * 
 */

public class CstReminderDialog extends TitleAreaDialog {

    private Text txtFirstName;
    private Text lastNameText;

    StateType selectedType;

    private String groupName;
    ImageCombo combo;
    private String groupDescription;
    Mandant mandant;
    CDateTime dateTime;
    Date selDate;

    ArrayList<CstProfile> options = new ArrayList<CstProfile>();

    Map<Integer, Image> imageMap = new HashMap<Integer, Image>();

    public CstReminderDialog(Shell parentShell, Mandant mandant) {
	super(parentShell);
	this.mandant = mandant;

	imageMap.put(StateType.ACTION.ordinal(), UiDesk.getImage(Activator.IMG_REMINDER_ACTION_NAME));
	imageMap.put(StateType.DECISION.ordinal(), UiDesk.getImage(Activator.IMG_REMINDER_DECISION_NAME));
	imageMap.put(StateType.REMINDER.ordinal(), UiDesk.getImage(Activator.IMG_REMINDER_REMINDER_NAME));
	imageMap.put(StateType.TRIGGER.ordinal(), UiDesk.getImage(Activator.IMG_REMINDER_TRIGGER_NAME));

    }

    @Override
    public void create() {
	super.create();
	setTitle("Erzeugt neuen Reminder");
	setMessage("Bitte wählen Sie Namen und Typ des Reminders", IMessageProvider.INFORMATION);
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

	Label lblNewLabel_1 = new Label(container, SWT.NONE);
	lblNewLabel_1.setText("Datum:");

	dateTime = new CDateTime(container, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);

	Label lblNewLabel = new Label(container, SWT.NONE);
	lblNewLabel.setText("Typ: ");

	combo = new ImageCombo(container, SWT.NONE);
	combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	combo.setBackground(UiDesk.getColorFromRGB("FFFFFF"));

	StateType[] stateTypes = StateType.values();
	for (StateType stateType : stateTypes) {
	    combo.add(imageMap.get(stateType.ordinal()), stateType.name());
	}
	combo.select(0);
	return area;
    }

    private void createFirstName(Composite container) {
	Label lbtFirstName = new Label(container, SWT.NONE);
	lbtFirstName.setText("Name: ");

	GridData dataFirstName = new GridData();
	dataFirstName.grabExcessHorizontalSpace = true;
	dataFirstName.horizontalAlignment = GridData.FILL;

	txtFirstName = new Text(container, SWT.BORDER);
	txtFirstName.setLayoutData(dataFirstName);
    }

    private void createLastName(Composite container) {
	Label lbtLastName = new Label(container, SWT.NONE);

	lbtLastName.setText("Beschreibung: ");

	GridData dataLastName = new GridData();
	dataLastName.grabExcessHorizontalSpace = true;
	dataLastName.horizontalAlignment = GridData.FILL;
	lastNameText = new Text(container, SWT.BORDER);
	lastNameText.setLayoutData(dataLastName);
    }

    @Override
    protected boolean isResizable() {
	return true;
    }

    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    // this method seems to be a hook and is called on close by okPressed
    private void saveInput() {
	groupName = txtFirstName.getText();
	groupDescription = lastNameText.getText();
	if (combo.getSelectionIndex() > -1) {
	    selectedType = StateType.values()[combo.getSelectionIndex()];
	}
	selDate = dateTime.getSelection();
    }

    @Override
    protected void okPressed() {
	saveInput();
	super.okPressed();
    }

    public String getGroupName() {
	return groupName;
    }

    public String getGroupDescription() {
	return groupDescription;
    }

    public StateType getItemType() {
	return selectedType;
    }

    public void setName(String name) {
	txtFirstName.setText(name);
    }

    public void setDescription(String name) {
	lastNameText.setText(name);
    }

    public void setType(StateType type) {
	combo.select(type.ordinal());
    }

    public void setDate(Date date) {
	dateTime.setSelection(date);

    }

    public Date getDate() {
	return selDate;

    }
}