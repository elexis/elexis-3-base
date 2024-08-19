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
package ch.unibe.iam.scg.archie.handlers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.util.SWTHelper;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.controller.ProviderManager;
import ch.unibe.iam.scg.archie.export.CSVWriter;
import ch.unibe.iam.scg.archie.export.PDFWriter;
import ch.unibe.iam.scg.archie.model.AbstractDataProvider;
import ch.unibe.iam.scg.archie.ui.views.StatisticsView;
import ch.unibe.iam.scg.archie.utils.StringHelper;

/**
 * <p>
 * Handler for the Exportmenu. <br>
 * Handles what happens when the export menu itself or its subitems are clicked
 * on.
 * </p>
 * 
 * $Id: org.eclipse.jdt.ui.prefs 663 2008-12-12 23:53:24Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @author Marcel Dedic
 * @version $Rev: 663 $
 */
public class ExportHandler extends AbstractHandler {

	private StatisticsView view;
	private static boolean SINGLE_SPACED_FILES = false;
	private static String EXTENSION;
	private static String viewURI = "ch.unibe.iam.scg.archie.ui.views.StatisticsView";
	private static Logger log = LoggerFactory.getLogger(ExportHandler.class);

	// TODO: find a way to check if data is available, only export when data is
	// on the output view.
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = activePage.findView(viewURI);
		if (viewPart instanceof StatisticsView) {
			this.view = (StatisticsView) viewPart;
		} else {
			log.error("ExportHandler - StatisticsView not found.");
			return null;
		}
		if (ProviderManager.getInstance().getProvider() == null
				|| ProviderManager.getInstance().getProvider().getResult() == null) {
			SWTHelper.showError(Messages.ERROR_WRITING_FILE_TITLE,
					Messages.ERROR_EXPORT_DATA_NOT_EXIST + Messages.ERROR_SELECT_DATA);
		} else {
			String exportType = event.getParameter("ch.unibe.iam.scg.archie.commandParameter.export.format");
			if ("pdf".equals(exportType)) {
				exportAction(exportType);
			} else if ("csv".equals(exportType)) {
				exportAction(exportType);
			}
		}
		return null;
	}

	public void exportAction(String format) {
		EXTENSION = format;
		final FileDialog chooser = new FileDialog(this.view.getSite().getShell(), SWT.SAVE);
		chooser.setFilterExtensions(new String[] { "*." + EXTENSION, "*.*" });
		chooser.setFilterNames(new String[] { format.toUpperCase() + " Files", "All Files" });
		final String name = getNameSuggestion().toLowerCase() + "." + EXTENSION;
		chooser.setFileName(name);
		final String fileName = chooser.open();
		if (fileName != null) {
			try {
				if (format.equals("pdf")) {
					PDFWriter.saveFile(fileName, ProviderManager.getInstance().getProvider());
					SWTHelper.showInfo(format.toUpperCase() + " " + Messages.ACTION_EXPORT_TITLE,
							Messages.ACTION_EXPORT_SUCCESS);
				} else if (format.equals("csv")) {
					CSVWriter.writeFile(ProviderManager.getInstance().getProvider(), fileName);
					SWTHelper.showInfo(format.toUpperCase() + " " + Messages.ACTION_EXPORT_TITLE,
							Messages.ACTION_EXPORT_SUCCESS);
				}
			} catch (IOException e) {
				log.error("Could not save the " + format.toUpperCase() + " file.");
				SWTHelper.showError(format.toUpperCase() + " " + Messages.ERROR_WRITING_FILE_TITLE,
						Messages.ERROR_WRITING_FILE);
			}
		}
	}

	private String getNameSuggestion() {
		AbstractDataProvider provider = ProviderManager.getInstance().getProvider();
		String name = StringHelper.removeIllegalCharacters(provider.getName(), SINGLE_SPACED_FILES);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		name += "_" + format.format(Calendar.getInstance().getTime());
		return name;
	}

}

