package ch.docbox.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Organisation;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.VersionInfo;

public class DocboxContact extends PersistentObject {
	public static final String TABLENAME = "CH_DOCBOX_ELEXIS_CONTACT_JOINT";

	public static final String FLD_DOCBOX_ID = "DocboxId";
	public static final String FLD_CONTACT_ID = "ContactId";
	public static final String VERSION = "1.0.0"; //$NON-NLS-1$

	// @formatter:off
	/** Definition of the database table */
	static final String createDB =
		"CREATE TABLE "	+ TABLENAME	+ "("
		+ "ID VARCHAR(25) primary key,"
		+ "lastupdate BIGINT,"
		+ "deleted CHAR(1) default '0',"

		+ FLD_DOCBOX_ID + " VARCHAR(255),"
		+ FLD_CONTACT_ID + " VARCHAR(255));"

		+ "INSERT INTO " + TABLENAME + " (ID," + FLD_DOCBOX_ID + ") VALUES ("
		+ JdbcLink.wrap(StringConstants.VERSION_LITERAL) + ","
		+ JdbcLink.wrap(VERSION) + ");";
	//@formatter:on

	static {
		addMapping(TABLENAME, FLD_DOCBOX_ID, FLD_CONTACT_ID);

		DocboxContact version = load(StringConstants.VERSION_LITERAL);
		if (!version.exists()) {
			createOrModifyTable(createDB);
		} else {
			VersionInfo vi = new VersionInfo(version.get(FLD_DOCBOX_ID));
			if (vi.isOlder(VERSION)) {
				// future update script
			}
		}
	}

	protected DocboxContact() {
	}

	protected DocboxContact(String id) {
		super(id);
	}

	/**
	 * Create a DocboxContact entry. Does not add same docboxId twice -> if entry
	 * for docboxId should already exist contact reference for this docboxId will be
	 * replaced
	 *
	 * @param docboxId
	 * @param contact
	 */
	public DocboxContact(String docboxId, Kontakt contact) {
		DocboxContact docboxContact = loadByDocboxId(docboxId);
		// update if entry for docboxId already exists
		if (docboxContact != null) {
			docboxContact.setContact(contact);
		} else {
			create(null);
			String[] fields = new String[] { FLD_DOCBOX_ID, FLD_CONTACT_ID };

			String[] values = new String[] { docboxId, contact.getId() };
			set(fields, values);
		}
	}

	public static DocboxContact load(String id) {
		return new DocboxContact(id);
	}

	/**
	 * finds DocboxContact if entry exists for this id
	 *
	 * @param docboxId
	 * @return matching DocboxContact or null if docboxId is unknown
	 */
	public static DocboxContact loadByDocboxId(String docboxId) {
		Query<DocboxContact> qbe = new Query<DocboxContact>(DocboxContact.class);
		qbe.add(FLD_DOCBOX_ID, Query.EQUALS, docboxId);
		List<DocboxContact> docboxContact = qbe.execute();
		if (docboxContact == null || docboxContact.isEmpty()) {
			return null;
		}
		return docboxContact.get(0);
	}

	/**
	 * loads a contacts docboxId if there is an existing entry for it
	 *
	 * @param contact {@link Kontakt} which's docboxId you are looking for
	 * @return the docboxId or StringUtils.EMPTY if none was found
	 */
	public static String getDocboxIdFor(Kontakt contact) {
		Query<DocboxContact> qbe = new Query<DocboxContact>(DocboxContact.class);
		qbe.add(FLD_CONTACT_ID, Query.EQUALS, contact.getId());
		List<DocboxContact> list = qbe.execute();
		if (list != null && !list.isEmpty()) {
			return list.get(0).getDocboxId();
		}
		return StringUtils.EMPTY;
	}

	/**
	 * find a contact for this docboxId
	 *
	 * @param docboxId
	 * @return an {@link Kontakt} (or inheriting type {@link Person} or
	 *         {@link Organisation}). Null if none was found.
	 */
	public static Kontakt findContactForDocboxId(String docboxId) {
		DocboxContact docboxContact = loadByDocboxId(docboxId);
		if (docboxContact == null) {
			return null;
		}

		Kontakt c = docboxContact.getContact();
		if (c.istPerson()) {
			return Person.load(c.getId());
		} else if (c.istOrganisation()) {
			return Organisation.load(c.getId());
		} else {
			return c;
		}
	}

	/**
	 * imports docboxId and contactId form Kontakts ExtInfo into the DB-Table
	 * CH_DOCBOX_ELEXIS_CONTACT_JOINT
	 *
	 * @param monitor
	 */
	public static void importDocboxIdsFromKontaktExtinfo(IProgressMonitor monitor) {
		Query<Kontakt> qbe = new Query<Kontakt>(Kontakt.class);
		List<Kontakt> contacts = qbe.execute();
		monitor.beginTask("DocboxIds <-> Kontakt Zuordnung wird in eigene DB-Tabelle \u00fcbertragen", contacts.size());
		for (Kontakt c : contacts) {
			monitor.subTask(c.getLabel());
			String docboxId = c.getInfoString("docboxId");
			if (docboxId != null && !docboxId.isEmpty()) {
				new DocboxContact(docboxId, c);
			}
			monitor.worked(1);
		}
		monitor.done();
	}

	@Override
	public String getLabel() {
		return "DocboxId: " + getDocboxId() + " Kontakt: " + getContact().getLabel();
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	public String getDocboxId() {
		return get(FLD_DOCBOX_ID);
	}

	public void setDocboxId(String docboxId) {
		set(FLD_DOCBOX_ID, docboxId);
	}

	public Kontakt getContact() {
		String contactId = get(FLD_CONTACT_ID);
		return Kontakt.load(contactId);
	}

	public void setContactId(String contactId) {
		set(FLD_CONTACT_ID, contactId);
	}

	public void setContact(Kontakt contact) {
		String contactId = null;
		if (contact != null) {
			contactId = contact.getId();
		}
		setContactId(contactId);
	}

}
