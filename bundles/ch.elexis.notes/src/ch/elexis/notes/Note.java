/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.notes;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

/**
 * A Note is an arbitrary Text or BLOB with a name and optional keywords. A Note
 * can consist of or contain external links to files in the file system or URLs.
 * Notes are stored hierarchically in a tree-like structure (which ist mapped to
 * a flat database table via a "parent"-field).
 *
 * @author gerry
 *
 */
public class Note extends PersistentObject {
	public static final String FLD_MIMETYPE = "mimetype"; //$NON-NLS-1$
	public static final String FLD_KEYWORDS = "keywords"; //$NON-NLS-1$
	public static final String FLD_REFS = "refs"; //$NON-NLS-1$
	public static final String FLD_CONTENTS = "Contents"; //$NON-NLS-1$
	public static final String FLD_TITLE = "Title"; //$NON-NLS-1$
	private static final String FLD_PARENT = "Parent"; //$NON-NLS-1$
	private static final String TABLENAME = "CH_ELEXIS_NOTES"; //$NON-NLS-1$

	/**
	 * Initaialization: Create the table mappings (@see PersistentObject), check the
	 * version and create or update the table if necessary
	 */
	static {
		addMapping(TABLENAME, FLD_PARENT, FLD_TITLE, FLD_CONTENTS, "Datum=S:D:Date", FLD_REFS, FLD_KEYWORDS, //$NON-NLS-1$
				FLD_MIMETYPE);
	}

	/**
	 * Create a new Note with text content
	 *
	 * @param parent the parent note or null if this is a top level note
	 * @param title  a Title for this note
	 * @param text   the text content of this note
	 */
	public Note(Note parent, String title, String text) {
		create(null);
		set(new String[] { FLD_TITLE, FLD_DATE, FLD_MIMETYPE }, title, new TimeTool().toString(TimeTool.DATE_GER),
				"text/plain"); //$NON-NLS-1$
		try {
			setContent(text.getBytes("utf-8")); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			ExHandler.handle(e);
			// should never happen
		}
		if (parent != null) {
			set(FLD_PARENT, parent.getId());
		}
	}

	/**
	 * Create a new Note with binary content
	 *
	 * @param parent    the parent note or null if this is a top level note
	 * @param title     a Title for this note
	 * @param contents  the contents of this note in
	 * @param mimettype the mimetype of the contents
	 */

	public Note(Note parent, String title, byte[] contents, String mimetype) {
		create(null);
		set(new String[] { FLD_TITLE, FLD_DATE, FLD_MIMETYPE }, title, new TimeTool().toString(TimeTool.DATE_GER),
				mimetype);
		setContent(contents);
		if (parent != null) {
			set(FLD_PARENT, parent.getId());
		}
	}

	/**
	 * find the parent note of this note
	 *
	 * @return the parent note or null if this is a top level note.
	 */
	public Note getParent() {
		String pid = get(FLD_PARENT);
		if (pid == null) {
			return null;
		}
		Note p = Note.load(pid);
		return p;
	}

	/**
	 * find the children of this note
	 *
	 * @return a list of all Notes that are children of the current note. The list
	 *         might me empty but is never null.
	 */
	public List<Note> getChildren() {
		Query<Note> qbe = new Query<Note>(Note.class);
		qbe.add(FLD_PARENT, Query.EQUALS, getId());
		return qbe.execute();
	}

	/**
	 * Set new binary content to the current note. Any old content will be
	 * overwritten.
	 *
	 * @param cnt the new content
	 */
	public void setContent(byte[] cnt) {
		setBinary(FLD_CONTENTS, cnt);
		set(FLD_DATE, new TimeTool().toString(TimeTool.DATE_GER));
	}

	/**
	 * retrieve the content of this note
	 *
	 * @return a byte[] containing the data for this note's content
	 */
	public byte[] getContent() {
		return getBinary(FLD_CONTENTS);
	}

	/**
	 * retrieve the keywords that are associated with this note.
	 *
	 * @return a String with a comma separated list of keywords that may be empty
	 *         but is never null
	 */
	public String getKeywords() {
		return checkNull(get(FLD_KEYWORDS));
	}

	/**
	 * Enter keywords for this note
	 *
	 * @param kw a string with a comma separated list of keywords (at most 250
	 *           chars)
	 */
	public void setKeywords(String kw) {
		set(FLD_KEYWORDS, StringTool.limitLength(kw.toLowerCase(), 250));
	}

	/**
	 * Return externals references associated with this Note
	 *
	 * @return a List with urls of external refs
	 */
	public List<String> getRefs() {
		String all = get(FLD_REFS);
		if (StringTool.isNothing(all)) {
			return new ArrayList<String>();
		}
		return StringTool.splitAL(all, StringConstants.COMMA);
	}

	/**
	 * Add a new external ref
	 *
	 * @param ref a string representing an URL
	 */
	public void addRef(String ref) {
		List<String> refs = getRefs();
		refs.add(ref);
		set(FLD_REFS, StringTool.join(refs, StringConstants.COMMA));
	}

	/**
	 * remove an external reference
	 *
	 * @param ref the reference to remove
	 */
	public void removeRef(String ref) {
		List<String> refs = getRefs();
		refs.remove(ref);
		set(FLD_REFS, StringTool.join(refs, StringConstants.COMMA));
	}

	@Override
	public String getLabel() {
		return get(FLD_TITLE);
	}

	@Override
	public boolean delete() {
		Query<Note> qbe = new Query<Note>(Note.class);
		qbe.add(FLD_PARENT, Query.EQUALS, getId());
		List<Note> list = qbe.execute();
		for (Note note : list) {
			note.delete();
		}
		return super.delete();
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	public static Note load(String id) {
		return new Note(id);
	}

	protected Note(String id) {
		super(id);
	}

	protected Note() {
	}

}
