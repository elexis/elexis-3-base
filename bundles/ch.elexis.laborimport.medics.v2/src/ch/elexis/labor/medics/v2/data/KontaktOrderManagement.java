package ch.elexis.labor.medics.v2.data;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.util.PlatformHelper;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Kontakt;
import ch.elexis.data.PersistentObject;
import ch.elexis.labor.medics.v2.Messages;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;

public class KontaktOrderManagement extends PersistentObject {
	public static final String TABLENAME = "KONTAKT_ORDER_MANAGEMENT"; //$NON-NLS-1$

	public static final String FLD_KONTAKT_ID = "KONTAKT_ID"; //$NON-NLS-1$
	public static final String FLD_ORDER_NR = "ORDER_NR"; //$NON-NLS-1$

	public static long FIRST_ORDER_NR = 100000;

	private static final JdbcLink j = getConnection();
	protected static Logger log = LoggerFactory.getLogger(KontaktOrderManagement.class.getName());

	static {
		if (!tableExists(TABLENAME)) {
			String filepath = PlatformHelper.getBasePath("ch.elexis.laborimport.medics.v2") //$NON-NLS-1$
					+ File.separator + "createTable.script"; //$NON-NLS-1$
			Stm stm = j.getStatement();
			try {
				FileInputStream fis = new FileInputStream(filepath);
				stm.execScript(fis, true, true);
			} catch (Exception e) {
				ExHandler.handle(e);
				SWTHelper.showError(Messages.KontaktOrderManagement_titleErrorCreateDB,
						MessageFormat.format(Messages.KontaktOrderManagement_messageErrorCreateDB, filepath));
			} finally {
				j.releaseStatement(stm);
			}

		}
		addMapping(TABLENAME, FLD_KONTAKT_ID, FLD_ORDER_NR);
	}

	public KontaktOrderManagement() {
		super();
	}

	private KontaktOrderManagement(String id) {
		super(id);
	}

	public KontaktOrderManagement(final Kontakt kontakt) {
		create(null);
		setKontakt(kontakt);
		setOrderNr(FIRST_ORDER_NR);
	}

	/** Eine KontaktOrderManagement anhand der ID aus der Datenbank laden */
	public static KontaktOrderManagement load(String id) {
		return new KontaktOrderManagement(id);
	}

	public Kontakt getKontakt() {
		return Kontakt.load(get(FLD_KONTAKT_ID));
	}

	public void setKontakt(final Kontakt kontakt) {
		if (kontakt != null) {
			set(FLD_KONTAKT_ID, kontakt.getId());
		}
	}

	/**
	 * Transforms text into int value.
	 *
	 * @param doubleStr
	 * @return
	 */
	private static Long getLong(final String longStr) {
		Long value = null;
		if (longStr != null && longStr.length() > 0) {
			try {
				value = Long.parseLong(longStr);
			} catch (NumberFormatException e) {
				log.warn(MessageFormat.format("Could not parse long {0}!", longStr), e);
			}
		}
		return value;
	}

	public Long getOrderNr() {
		String intStr = get(FLD_ORDER_NR);
		return getLong(intStr);
	}

	public void setOrderNr(final Long orderNr) {
		if (orderNr != null) {
			set(FLD_ORDER_NR, orderNr.toString());
		}
	}

	@Override
	public String getLabel() {
		Kontakt kontakt = getKontakt();
		if (kontakt != null) {
			return kontakt.getLabel() + " (" + getOrderNr() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return "-"; //$NON-NLS-1$
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}
}
