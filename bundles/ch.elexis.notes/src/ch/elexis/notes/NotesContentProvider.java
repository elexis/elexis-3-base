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
package ch.elexis.notes;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.elexis.data.Query;

/**
 * ContentProvider to fill the Notes View
 *
 * @author gerry
 *
 */
public class NotesContentProvider implements ITreeContentProvider {
	Query<Note> qbe = new Query<Note>(Note.class);
	NoteComparator nc = new NoteComparator();

	public Object[] getChildren(Object element) {
		qbe.clear();
		qbe.add("Parent", "=", ((Note) element).getId()); //$NON-NLS-1$ //$NON-NLS-2$
		List<Note> res = qbe.execute();
		Collections.sort(res, nc);
		return res.toArray();
	}

	public Object getParent(Object element) {
		Note note = (Note) element;
		return Note.load(note.get("Parent")); //$NON-NLS-1$
	}

	public boolean hasChildren(Object element) {
		qbe.clear();
		qbe.add("Parent", "=", ((Note) element).getId()); //$NON-NLS-1$ //$NON-NLS-2$
		List<Note> res = qbe.execute();
		return res.size() > 0;
	}

	public Object[] getElements(Object inputElement) {
		qbe.clear();
		qbe.add("Parent", StringUtils.EMPTY, null); //$NON-NLS-1$
		List<Note> res = qbe.execute();
		Collections.sort(res, nc);

		return res.toArray();
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	public Query<Note> getQuery() {
		return qbe;
	}
}
