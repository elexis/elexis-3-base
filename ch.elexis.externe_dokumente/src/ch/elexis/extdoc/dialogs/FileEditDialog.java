/*******************************************************************************
 * Copyright (c) 2005-2008, Daniel Lutz and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Lutz - initial implementation
 *    G. Weirich - small changes to follow API changes
 *    
 *******************************************************************************/

package ch.elexis.extdoc.dialogs;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import ch.elexis.Desk;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.elexis.extdoc.Messages;
import ch.elexis.extdoc.util.*;

import com.tiff.common.ui.datepicker.DatePickerCombo;

public class FileEditDialog extends TitleAreaDialog {
	private static final int WIDGET_SPACE = 20;
	
	private Text tDateiname;
	private Text tExtension;
	
	private DatePickerCombo dp;
	
	private File file;
	
	public FileEditDialog(Shell parent, File file){
		super(parent);
		
		this.file = file;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		String fileName = file.getName();
		String fileExtension = ""; //$NON-NLS-1$
		
		// extract name and extension
		Pattern p = Pattern.compile("^(.+)\\.([^.]+)$"); //$NON-NLS-1$
		Matcher m = p.matcher(fileName);
		if (m.matches()) {
			// replace fileName with prefix and fileExtension with suffix
			
			fileName = m.group(1);
			fileExtension = m.group(2);
		}
		
		Composite area = new Composite(parent, SWT.NONE);
		area.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		area.setLayout(new GridLayout(2, true));
		
		Label label;
		GridData gd;
		
		// filename text (without extension)
		
		label = new Label(area, SWT.NONE);
		label.setText(Messages.FileEditDialog_file_name + file.getParent());
		label.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		
		tDateiname = new Text(area, SWT.BORDER);
		tDateiname.setText(fileName);
		tDateiname.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		
		SWTHelper.setSelectOnFocus(tDateiname);
		
		// date label
		label = new Label(area, SWT.NONE);
		label.setText(String.format(Messages.FileEditDialog_file_date_and_explanation,
			new TimeTool(file.lastModified()).toString(TimeTool.DATE_GER)));
		gd = SWTHelper.getFillGridData(1, true, 1, false);
		gd.verticalIndent = WIDGET_SPACE;
		label.setLayoutData(gd);
		
		// extension label
		label = new Label(area, SWT.NONE);
		label.setText(Messages.FileEditDialog_extension);
		gd = SWTHelper.getFillGridData(1, true, 1, false);
		gd.verticalIndent = WIDGET_SPACE;
		label.setLayoutData(gd);
		
		// date text / datepickercombo
		
		dp = new DatePickerCombo(area, SWT.NONE);
		// current date of file
		/*
		 * doesn't work, because you can't decide if user has changed date or just only set cursor
		 * into date edit field
		 */
		// dp.setDate(new Date(file.lastModified()));
		// TODO DEBUG
		// System.out.println("old time: " + file.lastModified());
		
		// extension text
		tExtension = new Text(area, SWT.BORDER);
		tExtension.setText(fileExtension);
		tExtension.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		SWTHelper.setSelectOnFocus(tExtension);
		
		return area;
	}
	
	@Override
	public void create(){
		super.create();
		setMessage(Messages.ExterneDokumente_rename_or_change_date);
		setTitle(Messages.FileEditDialog_file_properties);
		getShell().setText(Messages.FileEditDialog_file_properties);
		setTitleImage(Desk.getImage(Desk.IMG_LOGO48));
	}
	
	@Override
	protected void okPressed(){
		String fileName = tDateiname.getText();
		String fileExtension = tExtension.getText();
		
		String dateiname;
		if (StringTool.isNothing(fileExtension)) {
			dateiname = fileName;
		} else {
			// re-assemble prefix and suffix
			dateiname = fileName + "." + fileExtension; //$NON-NLS-1$
		}
		
		Date datum = dp.getDate();
		
		if (datum != null && datum.getTime() != file.lastModified()) {
			// set time to 00:00
			Calendar cal = Calendar.getInstance();
			cal.setTime(datum);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			Long newTime = cal.getTimeInMillis();
			
			System.out.println("new time: " + newTime + " (" + file.lastModified() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			file.setLastModified(newTime);
		}
		
		if (!file.getName().equals(dateiname)) {
			File newFile = new File(file.getParent(), dateiname);
			
			System.out.format("rename %1s ->%2s (%3s)", file.getAbsolutePath(), //$NON-NLS-1$
				newFile.getAbsolutePath(), file.getName());
			String oldShort = MatchPatientToPath.firstToken(file.getName());
			String newShort = MatchPatientToPath.firstToken(newFile.getName());
			if (!oldShort.equals(newShort)) {
				if (SWTHelper.askYesNo(Messages.FileEditDialog_attribute_to_new_patient, String
					.format(Messages.FileEditDialog_really_attribute_to_new_patient, oldShort,
						newShort))) {
					System.out.format("okPressed move %1s -> %2s", oldShort, newShort); //$NON-NLS-1$
				} else {
					System.out.format("cancelPressed move %1s -> %2s", oldShort, newShort); //$NON-NLS-1$
					super.cancelPressed();
					return;
				}
			}
			if (file.renameTo(newFile)) {
				// seems to be required on Windows
				// newFile.setLastModified(file.lastModified());
			} else {
				MessageDialog.openError(getShell(), Messages.FileEditDialog_17,
					Messages.FileEditDialog_18);
			}
		}
		
		super.okPressed();
	}
	
}
