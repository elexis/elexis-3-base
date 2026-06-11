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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.SettingsProvider;
import ch.itmed.fop.printing.print.PrintProvider;
import ch.itmed.fop.printing.resources.Messages;
import ch.itmed.fop.printing.resources.ResourceProvider;
import ch.itmed.fop.printing.xml.documents.AppointmentCard;
import ch.itmed.fop.printing.xml.documents.PdfTransformer;

public final class SelectedAppointmentCardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection currentSelection = HandlerUtil.getCurrentStructuredSelection(event);
		if (currentSelection == null || currentSelection.isEmpty()) {
			return null;
		}
		List<IAppointment> appointments = new ArrayList<>();
		for (Object obj : currentSelection.toList()) {
			if (obj instanceof IAppointment) {
				appointments.add((IAppointment) obj);
			}
		}

		if (appointments.isEmpty()) {
			return null;
		}

		IPatient patient = ContextServiceHolder.get().getActivePatient().orElse(null);
		IMandator mandator = ContextServiceHolder.get().getActiveMandator().orElse(null);

		try {
			InputStream xmlDoc = AppointmentCard.create(appointments, patient, mandator);
			InputStream pdf = PdfTransformer.transformXmlToPdf(xmlDoc,
					ResourceProvider.getXslTemplateFile(PreferenceConstants.APPOINTMENT_CARD_ID));

			String docName = PreferenceConstants.APPOINTMENT_CARD;
			IPreferenceStore settingsStore = SettingsProvider.getStore(docName);
			String printerName = settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 0));
			PrintProvider.printPdf(pdf, printerName);
		} catch (Exception e) {
			SWTHelper.showError(Messages.DefaultError_Title, Messages.DefaultError_Message);
			LoggerFactory.getLogger(getClass()).error("Failed to generate or print the appointment card.", e);
		}
		return null;
	}
}