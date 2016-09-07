/*******************************************************************************
 * Copyright (c) 2006-2016, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.omnivore.ui.views;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.tiff.common.ui.datepicker.DatePickerCombo;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.omnivore.data.DocHandle;
import ch.elexis.omnivore.data.Preferences;
import ch.rgw.tools.TimeTool;

public class FileImportDialog extends TitleAreaDialog {
	String file;
	DocHandle dh;
	DatePickerCombo dDate;
	Text tTitle;
	Text tKeywords;
	
	Combo cbCategories;
	public Date date;
	public String title;
	public String keywords;
	public String category;
	private String preSelectedCategory;
	
	public FileImportDialog(DocHandle dh){
		super(Hub.plugin.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.dh = dh;
		file = dh.get(DocHandle.FLD_TITLE);
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public FileImportDialog(String name){
		super(Hub.plugin.getWorkbench().getActiveWorkbenchWindow().getShell());
		file = name;
	}
	
	public FileImportDialog(String name, String preSelectedCategory){
		super(Hub.plugin.getWorkbench().getActiveWorkbenchWindow().getShell());
		file = name;
		this.preSelectedCategory = preSelectedCategory;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		if (Preferences.getDateModifiable()) {
			new Label(ret, SWT.None).setText(Messages.FileImportDialog_dateLabel);
			dDate = new DatePickerCombo(ret, SWT.NONE);
			if (dh == null) {
				dDate.setDate(new Date());
			} else {
				dDate.setDate(new TimeTool(dh.getDate()).getTime());
			}
		}
		new Label(ret, SWT.None).setText(Messages.FileImportDialog_categoryLabel);
		Composite cCats = new Composite(ret, SWT.NONE);
		cCats.setFocus();
		cCats.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cCats.setLayout(new RowLayout(SWT.HORIZONTAL));
		cbCategories = new Combo(cCats, SWT.SINGLE);
		RowData rd = new RowData(200, SWT.DEFAULT);
		cbCategories.setLayoutData(rd);
		Button bNewCat = new Button(cCats, SWT.PUSH);
		bNewCat.setImage(Images.IMG_NEW.getImage());
		bNewCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				InputDialog id =
					new InputDialog(getShell(), Messages.FileImportDialog_newCategoryCaption,
						Messages.FileImportDialog_newCategoryText, null, null);
				if (id.open() == Dialog.OK) {
					DocHandle.addMainCategory(id.getValue());
					cbCategories.add(id.getValue());
					cbCategories.setText(id.getValue());
				}
			}
		});
		Button bEditCat = new Button(cCats, SWT.PUSH);
		bEditCat.setImage(Images.IMG_EDIT.getImage());
		bEditCat.setToolTipText("Kategorie umbenennen");
		bEditCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				String old = cbCategories.getText();
				InputDialog id = new InputDialog(getShell(),
					MessageFormat.format("Kategorie '{0}' umbenennen.", old),
					"Geben Sie bitte einen neuen Namen für die Kategorie ein", old, null);
				if (id.open() == Dialog.OK) {
					String nn = id.getValue();
					DocHandle.renameCategory(old, nn);
					cbCategories.remove(old);
					cbCategories.add(nn);
				}
			}
		});
		
		Button bDeleteCat = new Button(cCats, SWT.PUSH);
		bDeleteCat.setImage(Images.IMG_DELETE.getImage());
		bDeleteCat.setToolTipText("Kategorie löschen");
		bDeleteCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent ev){
				String old = cbCategories.getText();
				InputDialog id =
					new InputDialog(getShell(), MessageFormat.format("Kategorie {0}löschen", old),
						"Geben Sie bitte an, in welche andere Kategorie die Dokumente dieser Kategorie verschoben werden sollen",
						"", null);
				if (id.open() == Dialog.OK) {
					DocHandle.removeCategory(old, id.getValue());
					cbCategories.remove(id.getValue());
				}
			}
		});
		List<String> cats = DocHandle.getMainCategoryNames();
		if (cats.size() > 0) {
			Collections.sort(cats);
			cbCategories.setItems(cats.toArray(new String[0]));
			
			if (preSelectedCategory == null) {
				cbCategories.select(0);
			} else {
				String[] items = cbCategories.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].equals(preSelectedCategory)) {
						cbCategories.select(i);
					}
				}
			}
		}
		new Label(ret, SWT.NONE).setText(Messages.FileImportDialog_titleLabel);
		tTitle = SWTHelper.createText(ret, 1, SWT.NONE);
		new Label(ret, SWT.NONE).setText(Messages.FileImportDialog_keywordsLabel);
		tKeywords = SWTHelper.createText(ret, 4, SWT.NONE);
		tTitle.setText(file);
		if (dh != null) {
			tKeywords.setText(dh.get(DocHandle.FLD_KEYWORDS));
			cbCategories.setText(dh.getCategoryName());
		}
		bEditCat.setEnabled(CoreHub.acl.request(AccessControlDefaults.DOCUMENT_CATDELETE));
		bDeleteCat.setEnabled(CoreHub.acl.request(AccessControlDefaults.DOCUMENT_CATDELETE));
		bNewCat.setEnabled(CoreHub.acl.request(AccessControlDefaults.DOCUMENT_CATCREATE));
		
		return ret;
	}
	
	@Override
	public void create(){
		super.create();
		setTitle(file);
		getShell().setText(Messages.FileImportDialog_importCaption);
		setMessage(Messages.FileImportDialog_importFileText);
	}
	
	@Override
	protected void okPressed(){
		if (dDate != null)
			date = dDate.getDate();
		
		// dDate was null or dDate.getDate() returned null
		if (date == null)
			date = new Date();
		
		keywords = tKeywords.getText();
		title = tTitle.getText();
		category = cbCategories.getText();
		if (dh != null) {
			dh.setDate(date);
			if (category.length() > 0)
				dh.set(DocHandle.FLD_CAT, category);
			dh.set(DocHandle.FLD_TITLE, title);
			dh.set(DocHandle.FLD_KEYWORDS, keywords);
		}
		super.okPressed();
	}
	
}
