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

package ch.unibe.iam.scg.archie.actions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.controller.ProviderManager;
import ch.unibe.iam.scg.archie.export.CSVWriter;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.ui.views.StatisticsView;
import ch.unibe.iam.scg.archie.utils.StringHelper;

/**
 * <p>
 * Action to export result data to a CSV file.
 * </p>
 *
 * $Id: ExportAction.java 683 2008-12-16 22:31:33Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 683 $
 */
public class ExportAction extends Action {

	private StatisticsView view;

	/**
	 * Default extensions for CSV exports.
	 */
	private static String DEFAULT_EXTENSION = "csv";

	/**
	 * Determines whether the files are allowed to have more than one _ spacers.
	 */
	private static boolean SINGLE_SPACED_FILES = false;

	/** constructor */
	public ExportAction() {
		super();

		this.setText(Messages.ACTION_EXPORT_TITLE);
		this.setToolTipText(Messages.ACTION_EXPORT_DESCRIPTION);
		this.setImageDescriptor(ArchieActivator.getImageDescriptor("icons/page_excel.png"));

		// disabled by default
		this.setEnabled(false);
	}

	/**
	 *
	 * @param view
	 */
	public ExportAction(StatisticsView view) {
		this();
		this.view = view;
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		// get a file chooser
		final FileDialog chooser = new FileDialog(this.view.getSite().getShell(), SWT.SAVE);

		// set default extension for the exported file
		chooser.setFilterExtensions(new String[] { "*." + ExportAction.DEFAULT_EXTENSION, "*.*" });
		chooser.setFilterNames(new String[] { "CSV Files", "All Files" });

		// get a default name based on the current date
		final String name = this.getNameSuggestion().toLowerCase();
		chooser.setFileName(name + "." + ExportAction.DEFAULT_EXTENSION);

		final String fileName = chooser.open();
		if (fileName != null) {
			this.saveFile(fileName);
		}
	}

	/**
	 * Saves a CSV list to the given filename.
	 *
	 * @param fileName Filename to save the CSV export to.
	 */
	private void saveFile(String fileName) {
		try {
			CSVWriter.writeFile(ProviderManager.getInstance().getProvider(), fileName);
		} catch (IOException e) {
			ArchieActivator.LOG.log("Could not save the given file." + "\n" + e.getLocalizedMessage(), Log.ERRORS);
			SWTHelper.showError(Messages.ERROR_WRITING_FILE_TITLE, Messages.ERROR_WRITING_FILE);
		}
	}

	/**
	 * Suggests a filename to the user based on the cleaned up name of the data
	 * provider we're looking at and the today's date being appened to that name.
	 *
	 * @return Cleaned up filename suggestion.
	 * @see StringHelper
	 */
	private String getNameSuggestion() {
		String name = StringHelper.removeIllegalCharacters(ProviderManager.getInstance().getProvider().getName(),
				ExportAction.SINGLE_SPACED_FILES);

		// append today's date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		name += "_" + format.format(Calendar.getInstance().getTime());
		return name;
	}
}
