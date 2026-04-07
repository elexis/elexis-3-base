/*******************************************************************************
 * Copyright (c) 2026, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - initial implementation
 *
 *******************************************************************************/

package at.medevit.ch.artikelstamm.ui.internal;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.ui.ArtikelstammLabelProvider;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class ArticleDetailDialog extends Dialog {
	private IBilled billed;
	private IArtikelstammItem article;

	private ArtikelstammLabelProvider labelProvider;
	private Button bFranchiseFree;
	private Text tIndication;

	public ArticleDetailDialog(Shell shell, IBilled tl) {
		super(shell);
		billed = tl;

		labelProvider = new ArtikelstammLabelProvider();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		article = (IArtikelstammItem) billed.getBillable();
		Composite ret = (Composite) super.createDialogArea(parent);
		ret.setLayout(new GridLayout(2, false));

		Composite title = new Composite(ret, SWT.NONE);
		title.setLayout(new RowLayout());
		title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label lIcon = new Label(title, SWT.NONE);
		lIcon.setImage(labelProvider.getImage(article));
		Label lTitle = new Label(title, SWT.NONE);
		lTitle.setText(labelProvider.getText(article));

		if (article.isVaccination()) {
			Label lFranchiseFree = new Label(ret, SWT.NONE);
			lFranchiseFree.setText("Impfung nicht franchise befreit");

			bFranchiseFree = new Button(ret, SWT.CHECK);
			bFranchiseFree.setSelection(
					StringUtils.isNotBlank((String) billed.getExtInfo(Constants.FLD_EXT_NOFRANCHISEFREE)));
		}

		Label lIndication = new Label(ret, SWT.NONE);
		lIndication.setText("Inkationscode");

		tIndication = new Text(ret, SWT.BORDER);
		tIndication.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		tIndication.setText(StringUtils.defaultString((String) billed.getExtInfo(Constants.FLD_EXT_INDICATIONCODE)));
		tIndication.setEnabled(article.isInSLList());
		tIndication.setTextLimit(8);
		ControlDecoration deco = new ControlDecoration(tIndication, SWT.TOP | SWT.LEFT);
		Image infoImage = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
				.getImage();
		Image errorImage = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
				.getImage();
		// set description and image
		deco.setDescriptionText("Indikaitonscode xxxxx.xx");
		deco.setImage(infoImage);
		deco.setShowOnlyOnFocus(true);
		if (!article.isInSLList()) {
			tIndication.setMessage("Indikaitonscode nur bei SL");
		}

		tIndication.addModifyListener(e -> {
			Text source = (Text) e.getSource();
			String prevText = (String) source.getData("prevText");
			if (!source.getText().isEmpty()) {
				if (isValidIndicationCode(source.getText())) {
					deco.hide();
				} else {
					deco.setDescriptionText("Indikaitonscode nich im Format xxxxx.xx");
					deco.setImage(errorImage);
					deco.show();
				}
			} else {
				deco.setDescriptionText("Indikaitonscode Format xxxxx.xx");
				deco.setImage(infoImage);
				deco.show();
			}
			if (source.getText().matches("[0-9]{5}") && prevText.matches("[0-9]{4}")) {
				source.append(".");
			}
			source.setData("prevText", source.getText());
		});

		ret.pack();
		return ret;
	}

	private boolean isValidIndicationCode(String text) {
		return text != null && text.matches("[0-9]{5}.[0-9]{2}");
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(billed.getBillable().getCodeSystemName() + "-Details: " + billed.getCode());
	}

	@Override
	protected void okPressed() {
		if (article.isVaccination()) {
			if (bFranchiseFree.getSelection()) {
				billed.setExtInfo(Constants.FLD_EXT_NOFRANCHISEFREE, Boolean.TRUE.toString());
			} else {
				billed.setExtInfo(Constants.FLD_EXT_NOFRANCHISEFREE, null);
			}
			CoreModelServiceHolder.get().save(billed);
		}
		if (article.isInSLList()) {
			if (isValidIndicationCode(tIndication.getText())) {
				billed.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, tIndication.getText());
			} else if (StringUtils.isEmpty(tIndication.getText())) {
				billed.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, null);
			}
			CoreModelServiceHolder.get().save(billed);
		}
		super.okPressed();
	}
}
