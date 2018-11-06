/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.omnivore.dialog;

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

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.omnivore.data.DocHandle;
import ch.elexis.omnivore.data.Preferences;
import ch.rgw.tools.TimeTool;

import com.tiff.common.ui.datepicker.DatePickerCombo;

public class FileImportDialog extends TitleAreaDialog {
	String file;
	DocHandle dh;
	DatePickerCombo saveDatePicker;
	DatePickerCombo originDatePicker;
	Text tTitle;
	Text tKeywords;
	
	Combo cbCategories;
	public Date saveDate;
	public Date originDate;
	public String title;
	public String keywords;
	public String category;
	public String preSelectedCategory;
	
	public FileImportDialog(DocHandle dh){
		super(Hub.plugin.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.dh = dh;
		file = dh.get("Titel");
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
			Composite dateComposite = new Composite(ret, SWT.NONE);
			dateComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
			dateComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
			
			new Label(dateComposite, SWT.None).setText(Messages.OmnivoreView_dateSavedColumn);
			saveDatePicker = new DatePickerCombo(dateComposite, SWT.NONE);
			if (dh == null) {
				saveDatePicker.setDate(new Date());
			} else {
				saveDatePicker.setDate(new TimeTool(dh.getDate()).getTime());
			}
			
			new Label(dateComposite, SWT.None).setText(Messages.OmnivoreView_dateOriginColumn);
			originDatePicker = new DatePickerCombo(dateComposite, SWT.NONE);
			if (dh == null) {
				originDatePicker.setDate(new Date());
			} else {
				originDatePicker.setDate(new TimeTool(dh.getCreationDate()).getTime());
			}
		}
		new Label(ret, SWT.None).setText("Kategorie");
		Composite cCats = new Composite(ret, SWT.NONE);
		cCats.setFocus();
		cCats.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cCats.setLayout(new RowLayout(SWT.HORIZONTAL));
		cbCategories = new Combo(cCats, SWT.SINGLE | SWT.DROP_DOWN | SWT.READ_ONLY);
		RowData rd = new RowData(200, SWT.DEFAULT);
		cbCategories.setLayoutData(rd);
		Button bNewCat = new Button(cCats, SWT.PUSH);
		bNewCat.setImage(Images.IMG_NEW.getImage());
		bNewCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				InputDialog id = new InputDialog(getShell(), "Neue Kategorie",
					"Geben Sie bitte einen Namen für die neue Kategorie ein", null, null);
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
					new InputDialog(getShell(), MessageFormat.format("Kategorie {0} löschen", old),
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
			cbCategories.select(0);
		}
		new Label(ret, SWT.NONE).setText("Titel");
		tTitle = SWTHelper.createText(ret, 1, SWT.NONE);
		new Label(ret, SWT.NONE).setText("Stichwörter");
		tKeywords = SWTHelper.createText(ret, 4, SWT.NONE);
		tTitle.setText(file);
		if (dh != null) {
			tKeywords.setText(dh.get("Keywords"));
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
		getShell().setText("Datei importieren");
		setMessage(
			"Geben Sie bitte einen Titel und ggf. einige Stichwörter für dieses Dokument ein");
	}
	
	@Override
	protected void okPressed(){
		setDateValues();
		
		keywords = tKeywords.getText();
		title = tTitle.getText();
		category = cbCategories.getText();
		if (dh != null) {
			dh.setDate(saveDate);
			dh.setCreationDate(originDate);
			dh.set(new String[] {
				DocHandle.FLD_CAT, DocHandle.FLD_TITLE, DocHandle.FLD_KEYWORDS
			}, category, title, keywords);
		}
		super.okPressed();
	}
	
	private void setDateValues(){
		if (saveDatePicker != null) {
			saveDate = saveDatePicker.getDate();
		}
		if (originDatePicker != null) {
			originDate = originDatePicker.getDate();
		}
		
		if (saveDate == null && originDate != null) {
			saveDate = originDate;
		} else if (originDate == null && saveDate != null) {
			originDate = saveDate;
		} else if (saveDate == null && originDate == null) {
			saveDate = new Date();
			originDate = new Date();
		}
	}
	
}
