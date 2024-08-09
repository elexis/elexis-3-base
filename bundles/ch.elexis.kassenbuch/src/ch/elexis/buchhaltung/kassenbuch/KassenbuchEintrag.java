/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.buchhaltung.kassenbuch;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

/**
 * A single cash journal entry.
 *
 * @author Gerry
 *
 */
public class KassenbuchEintrag extends PersistentObject implements Comparable<KassenbuchEintrag> {
	private static final String TABLENAME = "CH_ELEXIS_KASSENBUCH"; //$NON-NLS-1$
	public static final String VERSION = "1.3.0"; //$NON-NLS-1$
	public static final String CATEGORIES = "ChElexisKassenbuchKategorien"; //$NON-NLS-1$
	public static final String GLOBAL_CFG_SEPARATOR = "##"; //$NON-NLS-1$
	public static final String PAYMENT_MODES = "ChElexisKassenbuchZahlungsarten"; //$NON-NLS-1$

	public static final String FLD_PAYMENT_MODE = "Zahlungsart"; //$NON-NLS-1$

	//@formatter:off
	private static final String createDB = "CREATE TABLE " + TABLENAME + "(" //$NON-NLS-1$ //$NON-NLS-2$
		+ "ID			VARCHAR(25) primary key," //$NON-NLS-1$
		+ "lastupdate BIGINT," //$NON-NLS-1$
		+ "deleted 	CHAR(1) default '0'," //$NON-NLS-1$
		+ "Nr	    	VARCHAR(25)," //$NON-NLS-1$
		+ "Category	VARCHAR(80)," //$NON-NLS-1$
		+ "Date   	CHAR(8)," //$NON-NLS-1$
		+ "Amount 	CHAR(8)," //$NON-NLS-1$
		+ "Total  	CHAR(8)," //$NON-NLS-1$
		+ "Entry  	VARCHAR(80)," //$NON-NLS-1$
		+ "PaymentMode VARCHAR(80)" //$NON-NLS-1$
		+ ");" //$NON-NLS-1$
		+ "INSERT INTO " //$NON-NLS-1$
		+ TABLENAME + " (ID,Nr,Date,Entry) VALUES ('1','-','" //$NON-NLS-1$
		+ new TimeTool().toString(TimeTool.DATE_COMPACT) + "','" + VERSION + "');";; //$NON-NLS-1$ //$NON-NLS-2$
	//@formatter:on

	static {
		addMapping(TABLENAME, "Betrag=Amount", "Text=Entry", "Datum=S:D:Date", "Saldo=Total", "BelegNr=Nr", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				"Kategorie=Category", "Zahlungsart=PaymentMode"); //$NON-NLS-1$ //$NON-NLS-2$
		KassenbuchEintrag version = KassenbuchEintrag.load("1"); //$NON-NLS-1$
		if (!version.exists()) {
			createOrModifyTable(createDB);
		} else {
			VersionInfo vi = new VersionInfo(version.getText());
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder("1.0.0")) { //$NON-NLS-1$
					getConnection().exec("ALTER TABLE " + TABLENAME + " ADD deleted CHAR(1) default '0';"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (vi.isOlder("1.1.0")) { //$NON-NLS-1$
					createOrModifyTable("ALTER TABLE " + TABLENAME + " ADD Category VARCHAR(80);"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (vi.isOlder("1.2.0")) { //$NON-NLS-1$
					createOrModifyTable("ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (vi.isOlder("1.3.0")) { //$NON-NLS-1$
					createOrModifyTable("ALTER TABLE " + TABLENAME + " ADD PaymentMode VARCHAR(80)"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				version.set("Text", VERSION); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Create a new entry and recalculate the whole cash journal
	 *
	 * @param beleg  identification of the new journal entry
	 * @param date   date of transaction
	 * @param amount amount of transaction
	 * @param text   title for the transaction
	 */
	public KassenbuchEintrag(String beleg, String date, Money amount, String text) {
		create(null);
		set(new String[] { "BelegNr", "Datum", "Betrag", "Text" }, beleg, date, amount.getCentsAsString(), text); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		recalc();
	}

	/**
	 * create an identifier for the next journal entry. If the given previous entry
	 * kbe has an identifier that begins with a numeric part, the next number of
	 * that numeric part will be generated. Otherwise the previous identifier,
	 * followed ba an " a" will be returned
	 *
	 * @param kbe the previous journal entry
	 * @return an identifier for the next journal entry.
	 */
	public static String nextNr(KassenbuchEintrag kbe) {
		String ret = "1"; //$NON-NLS-1$
		if (kbe != null) {
			String prev = kbe.getBelegNr().split("[^0-9]", 2)[0]; //$NON-NLS-1$

			if (prev.matches("[0-9]+")) { //$NON-NLS-1$
				int num = Integer.parseInt(prev);
				ret = Integer.toString(num + 1);
			} else {
				ret = kbe.getBelegNr() + " a"; //$NON-NLS-1$
			}
		}
		return ret;
	}

	/**
	 * create a new journal entry without recalculating the whole cash journal.
	 * Instead, take the balance out of the given last entry.
	 *
	 * @param beleg  Identifier for the entry. Can be null or StringUtils.EMPTY,
	 *               then it will be generated automatically
	 * @param date   date for the transaction. Can be null, then today will be
	 *               assumed
	 * @param amount sum of the transaction
	 * @param text   title for the transction
	 * @param last   previous transaction containing correct balance of the cash
	 *               book
	 */
	public KassenbuchEintrag(String beleg, String date, Money amount, String text, KassenbuchEintrag last) {
		create(null);
		Money sum = new Money(amount);
		if (last != null) {
			sum.addMoney(last.getSaldo());
		}
		if (date == null) {
			date = new TimeTool().toString(TimeTool.DATE_GER);
		}
		if (StringTool.isNothing(beleg) || (!beleg.matches("[0-9]+.*"))) { //$NON-NLS-1$
			beleg = nextNr(last) + beleg;
		}
		set(new String[] { "BelegNr", "Datum", "Betrag", "Text", "Saldo" }, beleg, date, amount.getCentsAsString(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				text, sum.getCentsAsString());
	}

	/** return the identifier for the entry */
	public String getBelegNr() {
		return checkNull(get("BelegNr")); //$NON-NLS-1$
	}

	/** return the date of the transaction */
	public String getDate() {
		return checkNull(get("Datum")); //$NON-NLS-1$
	}

	/** return the amount of the transaction (may be positive or negative) */
	public Money getAmount() {
		return new Money(checkZero(get("Betrag"))); //$NON-NLS-1$
	}

	/** return the balance */
	public Money getSaldo() {
		return new Money(checkZero(get("Saldo"))); //$NON-NLS-1$
	}

	/** return the text of the transaction */
	public String getText() {
		return get("Text"); //$NON-NLS-1$
	}

	/**
	 * recalculate the whole journal
	 *
	 * @return the last journal entry
	 */
	public static KassenbuchEintrag recalc() {
		KassenbuchEintrag ret = null;
		Money sum = new Money();
		for (KassenbuchEintrag kb : getBookings(null, null)) {
			sum.addMoney(kb.getAmount());
			kb.set("Saldo", sum.getCentsAsString()); //$NON-NLS-1$
			ret = kb;
		}
		return ret;
	}

	public static KassenbuchEintrag lastNr() {
		try {
			Query<KassenbuchEintrag> qbe = new Query<>(KassenbuchEintrag.class);
			qbe.add("BelegNr", "<>", "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			qbe.orderBy(true, "BelegNr");
			List<KassenbuchEintrag> result = qbe.execute();
			if (result.isEmpty()) {
				return null;
			} else {
				return result.get(0); // the first result will be the one with the highest BelegNr
			}
		} catch (Throwable t) {
			ExHandler.handle(t);
			return null;
		}
	}

	/**
	 * return a sorted set of all entries. The bookings are sorted by BelegNr
	 *
	 * @return a set that is guaranteed to be sorted by BelegNr
	 */
	public static SortedSet<KassenbuchEintrag> getBookings(TimeTool from, TimeTool until) {
		try {
			Query<KassenbuchEintrag> qbe = new Query<KassenbuchEintrag>(KassenbuchEintrag.class);
			qbe.add("BelegNr", "<>", "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (from != null) {
				qbe.add("Datum", ">=", from.toString(TimeTool.DATE_COMPACT)); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (until != null) {
				qbe.add("Datum", "<=", until.toString(TimeTool.DATE_COMPACT)); //$NON-NLS-1$ //$NON-NLS-2$
			}
			// qbe.orderBy(false, "BelegNr");
			TreeSet<KassenbuchEintrag> ts = new TreeSet<KassenbuchEintrag>();
			return (SortedSet<KassenbuchEintrag>) qbe.execute(ts);
		} catch (Throwable t) {
			ExHandler.handle(t);
			return null;
		}

	}

	@Override
	public String getLabel() {
		return getAmount().getAmountAsString() + StringUtils.SPACE + getText();
	}

	public String getKategorie() {
		return get("Kategorie"); //$NON-NLS-1$
	}

	public void setKategorie(String cat) {
		addKategorie(cat);
		set("Kategorie", cat); //$NON-NLS-1$
	}

	public String getPaymentMode() {
		return get(FLD_PAYMENT_MODE);
	}

	public void setPaymentMode(String payment) {
		addPaymentMode(payment);
		set(FLD_PAYMENT_MODE, payment);
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	public static KassenbuchEintrag load(String id) {
		return new KassenbuchEintrag(id);
	}

	protected KassenbuchEintrag() {

	}

	protected KassenbuchEintrag(String id) {
		super(id);

	}

	public static String[] getCategories() {
		String cats = ConfigServiceHolder.getGlobal(CATEGORIES, StringUtils.EMPTY);
		return cats.split(GLOBAL_CFG_SEPARATOR);
	}

	public static void addKategorie(String cat) {
		String updatedCats = append(cat, ConfigServiceHolder.getGlobal(CATEGORIES, StringUtils.EMPTY));

		if (updatedCats != null)
			ConfigServiceHolder.setGlobal(CATEGORIES, updatedCats);
	}

	public static String[] getPaymentModes() {
		String payments = ConfigServiceHolder.getGlobal(PAYMENT_MODES, StringUtils.EMPTY);
		return payments.split(GLOBAL_CFG_SEPARATOR);
	}

	public static void addPaymentMode(String payment) {
		String updatedPaymentModes = append(payment, ConfigServiceHolder.getGlobal(PAYMENT_MODES, StringUtils.EMPTY));

		if (updatedPaymentModes != null)
			ConfigServiceHolder.setGlobal(PAYMENT_MODES, updatedPaymentModes);

	}

	private static String append(String nEntry, String existing) {
		String[] current = existing.split(GLOBAL_CFG_SEPARATOR);

		if (StringTool.getIndex(current, nEntry) == -1) {
			return existing + GLOBAL_CFG_SEPARATOR + nEntry;
		}
		return null;
	}

	/**
	 * The comparator is used to create a sorted set of the bookings. It scans the
	 * identifier (BelegNr) for a numerical part and compares these numbers. If the
	 * numbers are identical or not found, a textual comparison is used.
	 */
	public int compareTo(KassenbuchEintrag k2) {
		// KassenbuchEintrag k2=(KassenbuchEintrag)o;
		String[] s1 = getBelegNr().split("[^0-9]", 2); //$NON-NLS-1$
		String[] s2 = k2.getBelegNr().split("[^0-9]", 2); //$NON-NLS-1$
		int res = 0;
		if (s1[0].matches("[0-9]+") && s2[0].matches("[0-9]+")) { //$NON-NLS-1$ //$NON-NLS-2$
			res = Integer.parseInt(s1[0]) - Integer.parseInt(s2[0]);
		} else {
			res = s1[0].compareTo(s2[0]);
		}
		if (res == 0) {
			if (s1.length > s2.length) {
				return 1;
			}
			if (s1.length < s2.length) {
				return -1;
			}
			if (s1.length < 2) {
				return 0;
			}
			return s1[1].compareTo(s2[1]);
		}
		return res;
	}
}
