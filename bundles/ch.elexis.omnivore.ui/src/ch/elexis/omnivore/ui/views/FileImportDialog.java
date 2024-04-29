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
import org.eclipse.ui.PlatformUI;

import com.tiff.common.ui.datepicker.DatePickerCombo;

import ch.elexis.core.ac.EvACE;
import ch.elexis.core.ac.Right;
import ch.elexis.core.services.holder.AccessControlServiceHolder;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.omnivore.data.Preferences;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.model.TransientCategory;
import ch.elexis.omnivore.model.util.CategoryUtil;
import ch.elexis.omnivore.ui.Messages;
import ch.elexis.omnivore.ui.service.OmnivoreModelServiceHolder;
import ch.elexis.omnivore.ui.util.CategorySelectDialog;
import ch.rgw.tools.TimeTool;

public class FileImportDialog extends TitleAreaDialog {
	String file;
	IDocumentHandle dh;
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
	private String preSelectedCategory;

	public FileImportDialog(IDocumentHandle dh) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.dh = dh;
		file = dh.getTitle();
	}

	/**
	 * @wbp.parser.constructor
	 */
	public FileImportDialog(String name) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		file = name;
	}

	public FileImportDialog(String name, String preSelectedCategory) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		file = name;
		this.preSelectedCategory = preSelectedCategory;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		if (Preferences.getDateModifiable()) {
			Composite dateComposite = new Composite(ret, SWT.NONE);
			dateComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
			dateComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

			new Label(dateComposite, SWT.None).setText(Messages.FileImportDialog_dateLabel);
			saveDatePicker = new DatePickerCombo(dateComposite, SWT.NONE);
			if (dh == null) {
				saveDatePicker.setDate(new Date());
			} else {
				saveDatePicker.setDate(new TimeTool(dh.getLastchanged()).getTime());
			}

			new Label(dateComposite, SWT.None).setText(Messages.FileImportDialog_dateOriginLabel);
			originDatePicker = new DatePickerCombo(dateComposite, SWT.NONE);
			if (dh == null) {
				originDatePicker.setDate(new Date());
			} else {
				originDatePicker.setDate(new TimeTool(dh.getCreated()).getTime());
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
			public void widgetSelected(SelectionEvent e) {
				InputDialog id = new InputDialog(getShell(), Messages.FileImportDialog_newCategoryCaption,
						Messages.FileImportDialog_newCategoryText, null, null);
				if (id.open() == Dialog.OK) {
					CategoryUtil.addCategory(id.getValue());
					cbCategories.add(id.getValue());
					cbCategories.setText(id.getValue());
				}
			}
		});
		Button bEditCat = new Button(cCats, SWT.PUSH);
		bEditCat.setImage(Images.IMG_EDIT.getImage());
		bEditCat.setToolTipText(Messages.DocumentMetaDataDialog_renameCategory);
		bEditCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String old = cbCategories.getText();
				InputDialog id = new InputDialog(getShell(),
						MessageFormat.format(Messages.DocumentMetaDataDialog_renameCategoryConfirm, old),
						Messages.DocumentMetaDataDialog_renameCategoryText, old, null);
				if (id.open() == Dialog.OK) {
					String nn = id.getValue();
					CategoryUtil.renameCategory(old, nn);
					cbCategories.remove(old);
					cbCategories.add(nn);
					cbCategories.select(cbCategories.getItemCount() - 1);
				}
			}
		});

		Button bDeleteCat = new Button(cCats, SWT.PUSH);
		bDeleteCat.setImage(Images.IMG_DELETE.getImage());
		bDeleteCat.setToolTipText(Messages.Core_Delete_Document_Category);
		bDeleteCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent ev) {
				String old = cbCategories.getText();
				CategorySelectDialog catSelectDialog = new CategorySelectDialog(getShell(),
						MessageFormat.format(Messages.DocumentMetaDataDialog_deleteCategoryConfirm, old),
						Messages.DocumentMetaDataDialog_deleteCategoryConfirmText,
						CategoryUtil.getCategoriesNames());

				if (catSelectDialog.open() == Dialog.OK) {
					String newCategory = catSelectDialog.getSelectedCategory();
					CategoryUtil.removeCategory(old, newCategory);
					cbCategories.remove(old);
					cbCategories.add(newCategory);
					cbCategories.setText(newCategory);
				}
			}
		});
		List<String> cats = CategoryUtil.getCategoriesNames();
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
		tKeywords = SWTHelper.createText(ret, 4, SWT.V_SCROLL);
		tTitle.setText(file);
		if (dh != null) {
			tKeywords.setText(dh.getKeywords());
			cbCategories.setText(dh.getCategory().getName());
		}
		bEditCat.setEnabled(AccessControlServiceHolder.get().evaluate(EvACE.of(IDocumentHandle.class, Right.UPDATE).and(Right.EXECUTE)));
		bDeleteCat.setEnabled(AccessControlServiceHolder.get().evaluate(EvACE.of(IDocumentHandle.class, Right.DELETE).and(Right.EXECUTE)));
		bNewCat.setEnabled(AccessControlServiceHolder.get().evaluate(EvACE.of(IDocumentHandle.class, Right.CREATE).and(Right.EXECUTE)));

		return ret;
	}

	@Override
	public void create() {
		super.create();
		setTitle(file);
		getShell().setText(Messages.FileImportDialog_importCaption);
		setMessage(Messages.FileImportDialog_importFileText);
	}

	@Override
	protected void okPressed() {
		setDateValues();

		keywords = tKeywords.getText();
		title = tTitle.getText();
		category = cbCategories.getText();
		if (dh != null) {
			dh.setLastchanged(saveDate);
			dh.setCreated(originDate);

			if (category.length() > 0) {
				dh.setCategory(new TransientCategory(category));
			}
			dh.setTitle(title);
			dh.setKeywords(keywords);
			OmnivoreModelServiceHolder.get().save(dh);
		}
		super.okPressed();
	}

	private void setDateValues() {
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

	@Override
	protected boolean isResizable() {
		return true;
	}
}
