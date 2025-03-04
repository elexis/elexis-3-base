/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.commands;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.unibe.iam.scg.archie.ui.views.StatisticsView;

/**
 * <p>
 * This handler copies selected items coming from a selection provider into the
 * system's clipboard. The selection has to be of type
 * <code>IStructuredSelection</code>, this way we can assume the data is coming
 * from a table, most likely one that has been built from a provider's dataset.
 * </p>
 *
 * $Id: CopySelected.java 672 2008-12-15 10:53:14Z hephster $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 672 $
 */
public class CopySelected extends AbstractHandler {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		IViewPart view = (IViewPart) page.findView(StatisticsView.ID);
		ISelection selection = view.getSite().getSelectionProvider().getSelection();
		if (selection == null)
			return null;

		// build selection string
		StringBuilder builder = new StringBuilder();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			for (Iterator<Object> iterator = strucSelection.iterator(); iterator.hasNext();) {
				Comparable<?>[] row = (Comparable<?>[]) iterator.next();
				builder.append(this.datasetRowToString(row));
			}
		}

		// create clipboard contents
		Clipboard clipboard = new Clipboard(Display.getDefault());
		TextTransfer transfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[] { builder.toString() }, new Transfer[] { transfer });

		return null;
	}

	/**
	 * Composes a string from a dataset row that resides in a table.
	 *
	 * @param row Array of <code>Comparable</code> objects.
	 * @return String composed from the array fields, separated by <code>\t</code>
	 */
	private String datasetRowToString(Comparable<?>[] row) {
		String rowString = StringUtils.EMPTY;
		for (int i = 0; i < row.length; i++) {
			rowString += row[i].toString();
			rowString += (i == row.length - 1) ? StringUtils.EMPTY : "\t"; //$NON-NLS-1$
		}
		rowString += System.getProperty("line.separator"); //$NON-NLS-1$
		return rowString;
	}
}