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

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

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
	private Button bIndication;

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
			lFranchiseFree.setText("Impfung franchise befreit");

			bFranchiseFree = new Button(ret, SWT.CHECK);
			bFranchiseFree.setSelection(
					StringUtils.isNotBlank((String) billed.getExtInfo(Constants.FLD_EXT_FRANCHISEFREE)));
		}

		Label lIndication = new Label(ret, SWT.NONE);
		lIndication.setText("Inkationscode");

		bIndication = new Button(ret, SWT.PUSH);
		bIndication.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		bIndication.setText(Objects.toString(billed.getExtInfo(Constants.FLD_EXT_INDICATIONCODE), "..."));
		if (!article.isInSLList()) {
			bIndication.setText("Indikaitonscode nur bei SL");
			bIndication.setEnabled(false);
		} else if (!article.isPm()) {
			bIndication.setText("Indikaitonscode nur bei Preismodell");
			bIndication.setEnabled(false);
		} else {
			bIndication.setEnabled(true);
		}
		bIndication.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IndicationCodeSelectionDialog dialog = new IndicationCodeSelectionDialog(article, getShell());
				dialog.setSelectedCode(bIndication.getText());
				if (dialog.open() == Window.OK) {
					if (dialog.getSelectedCode() instanceof String selectedCode) {
						bIndication.setText(selectedCode);
					}
				}
			}
		});

		ret.pack();
		return ret;
	}

	private boolean isValidIndicationCode(String text) {
		return text != null && text.matches("[0-9]{5}.[0-9X]{2}");
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
				billed.setExtInfo(Constants.FLD_EXT_FRANCHISEFREE, Boolean.TRUE.toString());
			} else {
				billed.setExtInfo(Constants.FLD_EXT_FRANCHISEFREE, null);
			}
			CoreModelServiceHolder.get().save(billed);
		}
		if (bIndication.isEnabled()) {
			if (isValidIndicationCode(bIndication.getText())) {
				billed.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, bIndication.getText());
			} else if (StringUtils.isEmpty(bIndication.getText())) {
				billed.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, null);
			}
			CoreModelServiceHolder.get().save(billed);
		}
		super.okPressed();
	}
}
