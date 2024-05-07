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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import com.itextpdf.text.DocumentException;

import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.controller.ProviderManager;
import ch.unibe.iam.scg.archie.export.PDFWriter;
import ch.unibe.iam.scg.archie.ui.views.StatisticsView;
import ch.unibe.iam.scg.archie.utils.StringHelper;

/**
 * <p>
 * TODO: DOCUMENT ME!
 * </p>
 * 
 * $Id: org.eclipse.jdt.ui.prefs 663 2008-12-12 23:53:24Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @author Dalibor Aksic
 * @version $Rev: 663 $
 */
public class ExportPdfAction extends Action {

	private StatisticsView view;
	private static String DEFAULT_EXTENSION = "pdf";

	public ExportPdfAction(StatisticsView view) {
		super();
		this.view = view;
		this.setText("Export to PDF");
		this.setToolTipText("Export data to a PDF file");
		this.setImageDescriptor(ArchieActivator.getImageDescriptor("icons/page_pdf.png"));
		this.setEnabled(true);
	}

	@Override
	public void run() {
		FileDialog chooser = new FileDialog(view.getSite().getShell(), SWT.SAVE);
		chooser.setFilterExtensions(new String[] { "*." + DEFAULT_EXTENSION, "*.*" });
		chooser.setFilterNames(new String[] { "PDF Files", "All Files" });
		String name = getNameSuggestion().toLowerCase() + "." + DEFAULT_EXTENSION;
		chooser.setFileName(name);
		String fileName = chooser.open();
		if (fileName != null) {
			try {
				PDFWriter.saveFile(fileName, ProviderManager.getInstance().getProvider());
			} catch (DocumentException | IOException e) {
				ArchieActivator.LOG.log("Could not save the PDF file." + StringUtils.LF + e.getLocalizedMessage(),
						Log.ERRORS);
				SWTHelper.showError("Fehler beim Schreiben von PDF",
						"Beim Schreiben der PDF-Datei ist ein Fehler aufgetreten.");
			}
		}
	}
	private String getNameSuggestion() {
		String name = StringHelper.removeIllegalCharacters(ProviderManager.getInstance().getProvider().getName(),
				false);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		name += "_" + format.format(Calendar.getInstance().getTime());
		return name;
	}
}