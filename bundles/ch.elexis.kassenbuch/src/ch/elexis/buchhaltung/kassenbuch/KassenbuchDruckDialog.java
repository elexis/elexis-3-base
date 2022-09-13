/*******************************************************************************
 * Copyright (c) 2008-2019, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    N. Giger - Warn on printing error
 *
 *******************************************************************************/
package ch.elexis.buchhaltung.kassenbuch;

import static ch.elexis.buchhaltung.kassenbuch.KassenbuchTextTemplateRequirement.TT_LIST;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.SortedSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.ITextPlugin.ICallback;
import ch.elexis.core.ui.text.TextContainer;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Brief;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class KassenbuchDruckDialog extends Dialog implements ICallback {
	TimeTool ttVon, ttBis;
	Hashtable<String, Money> mCategories = new Hashtable<String, Money>();

	public KassenbuchDruckDialog(Shell shell, TimeTool von, TimeTool bis) {
		super(shell);
		ttVon = von;
		ttBis = bis;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new FillLayout());
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		TextContainer text = new TextContainer(getShell());
		text.getPlugin().createContainer(ret, this);
		text.getPlugin().showMenu(false);
		text.getPlugin().showToolbar(false);
		Brief brief = text.createFromTemplateName(null, TT_LIST, Brief.UNKNOWN, CoreHub.getLoggedInContact(),
				"Kassenbuch");
		if (brief == null) {
			String title = "Probleme beim Drucken";
			String msg = String.format(
					"Konnte kein TextDokument erstellen. Fehlt die Vorlage '%s'? Oder ist sie fehlerhaft?", TT_LIST);
			SWTHelper.alert(title, msg);
			return ret;
		}
		SortedSet<KassenbuchEintrag> set = KassenbuchEintrag.getBookings(ttVon, ttBis);
		if (set == null) {
			return ret;
		}
		KassenbuchEintrag[] lines = set.toArray(new KassenbuchEintrag[0]);
		String[][] table = new String[lines.length + 1][];
		table[0] = new String[] { "Nr", "Datum", "Soll", "Haben", "Betrag", "Text" };
		for (int i = 1; i <= lines.length; i++) {
			table[i] = new String[6];
			KassenbuchEintrag kb = lines[i - 1];
			String kategorie = kb.getKategorie();
			if (StringTool.isNothing(kategorie)) {
				kategorie = "Sonstiges";
			}
			Money mKat = mCategories.get(kategorie);
			if (mKat == null) {
				mKat = new Money();
				mCategories.put(kategorie, mKat);
			}
			Money betrag = kb.getAmount();
			mKat.addMoney(betrag);
			table[i][0] = kb.get("BelegNr");
			table[i][1] = kb.getDate();
			table[i][2] = betrag.isNegative() ? "" : betrag.getAmountAsString();
			table[i][3] = betrag.isNegative() ? new Money(betrag).negate().getAmountAsString() : "";
			table[i][4] = kb.getSaldo().getAmountAsString();
			table[i][5] = kb.getText();
		}
		text.getPlugin().setFont("Helvetica", SWT.NORMAL, 9);
		text.getPlugin().insertTable("[Liste]", ITextPlugin.FIRST_ROW_IS_HEADER, table,
				new int[] { 5, 15, 15, 15, 20, 30 });
		Enumeration<String> keys = mCategories.keys();
		Object cursor = text.getPlugin().insertText("##end##", "", SWT.LEFT);
		if (cursor != null) {
			while (keys.hasMoreElements()) {
				String cat = keys.nextElement();
				Money betrag = mCategories.get(cat);
				StringBuilder sb = new StringBuilder();
				sb.append("\n").append(cat).append("\t\t\t").append(betrag.getAmountAsString());
				cursor = text.getPlugin().insertText(cursor, sb.toString(), SWT.LEFT);
			}
		}
		return ret;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Kassenbuch");
		getShell().setSize(800, 700);

	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	public void save() {
	}

	public boolean saveAs() {
		return false;
	}

}
