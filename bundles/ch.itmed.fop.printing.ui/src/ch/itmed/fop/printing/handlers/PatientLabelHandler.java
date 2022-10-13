/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.handlers;

import java.io.InputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.util.SWTHelper;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.SettingsProvider;
import ch.itmed.fop.printing.print.PrintProvider;
import ch.itmed.fop.printing.resources.Messages;
import ch.itmed.fop.printing.resources.ResourceProvider;
import ch.itmed.fop.printing.xml.documents.FoTransformer;
import ch.itmed.fop.printing.xml.documents.PatientLabel;

public final class PatientLabelHandler extends AbstractHandler {
	private static Logger logger = LoggerFactory.getLogger(PatientLabelHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			InputStream xmlDoc = PatientLabel.create();
			InputStream fo = FoTransformer.transformXmlToFo(xmlDoc,
					ResourceProvider.getXslTemplateFile(PreferenceConstants.PATIENT_LABEL_ID));

			String docName = PreferenceConstants.PATIENT_LABEL;
			IPreferenceStore settingsStore = SettingsProvider.getStore(docName);

			String printerName = settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 0));
			logger.info("Printing document PatientLabel on printer: " + printerName); //$NON-NLS-1$
			PrintProvider.print(fo, printerName);
		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg != null) {
				if (msg.equals("No patient selected")) { //$NON-NLS-1$
					// Make sure we don't show 2 error messages.
					return null;
				}
			}
			SWTHelper.showError(Messages.DefaultError_Title, Messages.DefaultError_Message);
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
