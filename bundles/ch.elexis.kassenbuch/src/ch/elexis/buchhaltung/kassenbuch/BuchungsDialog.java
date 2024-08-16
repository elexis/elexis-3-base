/*******************************************************************************
 * Copyright (c) 2007-2019, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    N. Giger - Using Nebula CDateTime as DatePicker
 *
 *******************************************************************************/
package ch.elexis.buchhaltung.kassenbuch;


import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;

public class BuchungsDialog extends TitleAreaDialog {
	boolean bType;
	LabeledInputField liBeleg, liBetrag;
	CDateTime liDate;
	Label dDate;
	Text text;
	KassenbuchEintrag act, lastNr;
	Combo cbDate, cbCats, cbPayments;

	BuchungsDialog(Shell shell, boolean mode) {
		super(shell);
		bType = mode;
		act = null;
	}

	/**
	 * @wbp.parser.constructor
	 */
	BuchungsDialog(Shell shell, KassenbuchEintrag kbe) {
		super(shell);
		act = kbe;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout());
		Composite top = new Composite(ret, SWT.BORDER);
		top.setLayout(new FillLayout());
		top.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		liBeleg = new LabeledInputField(top, "Beleg");
		Composite cbDate_1 = new Composite(top, SWT.BORDER);
		cbDate_1.setLayout(new GridLayout(1, false));
		dDate = new Label(cbDate_1, SWT.NONE);
		dDate.setText("Datum");
		liDate = new CDateTime(cbDate_1, CDT.DATE_MEDIUM | CDT.DROP_DOWN | SWT.BORDER | CDT.COMPACT);
		liBetrag = new LabeledInputField(top, "Betrag", LabeledInputField.Typ.MONEY);
		Composite cCombos = new Composite(ret, SWT.NONE);
		cCombos.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cCombos.setLayout(new GridLayout(2, false));
		new Label(cCombos, SWT.NONE).setText("Kategorie");
		cbCats = new Combo(cCombos, SWT.SINGLE);
		cbCats.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cbCats.setItems(KassenbuchEintrag.getCategories());

		new Label(cCombos, SWT.NONE).setText("Zahlungsart");
		cbPayments = new Combo(cCombos, SWT.SINGLE);
		cbPayments.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cbPayments.setItems(KassenbuchEintrag.getPaymentModes());

		new Label(ret, SWT.NONE).setText("Buchungstext");
		text = new Text(ret, SWT.BORDER);
		text.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		if (act == null) {
			lastNr = KassenbuchEintrag.lastNr();
			liBeleg.setText(KassenbuchEintrag.nextNr(lastNr));
		} else {
			liBeleg.setText(act.getBelegNr());
			liDate.setSelection(new TimeTool(act.getDate()).getTime());
			liBetrag.setText(act.getAmount().getAmountAsString());
			cbCats.setText(act.getKategorie());
			cbPayments.setText(act.getPaymentMode());
			text.setText(act.getText());
		}
		return ret;
	}

	@Override
	public void create() {
		super.create();
		if (act == null) {
			if (bType) {
				setTitle("Einnahme verbuchen");
			} else {
				setTitle("Ausgabe verbuchen");
			}
		} else {
			setTitle("Buchung ändern");
		}
		setMessage("Bitte geben Sie den Betrag und einen Buchungstext ein");
		getShell().setText("Buchung für Kassenbuch");
		liBetrag.getControl().setFocus();
	}

	@Override
	protected void okPressed() {
		Money money = new Money();
		try {
			money.addAmount(liBetrag.getText());
		} catch (Exception ex) {
			ExHandler.handle(ex);
		}
		TimeTool tt = new TimeTool(liDate.getText());
		String bt = text.getText();

		if (act == null) {
			if (!bType) {
				money = money.negate();
			}
			act = new KassenbuchEintrag(liBeleg.getText(), tt.toString(TimeTool.DATE_GER), money, bt, lastNr);
		} else {
			act.set(new String[] { "BelegNr", "Datum", "Betrag", "Text" }, liBeleg.getText(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					tt.toString(TimeTool.DATE_GER), money.getCentsAsString(), text.getText());
			KassenbuchEintrag.recalc();
		}
		act.setKategorie(cbCats.getText());
		act.setPaymentMode(cbPayments.getText());
		super.okPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
