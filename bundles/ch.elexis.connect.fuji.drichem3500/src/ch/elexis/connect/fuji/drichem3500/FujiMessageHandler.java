/*******************************************************************************
 * Copyright (c) 2009-2022, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - reworked for ethernet
 *
 *******************************************************************************/
package ch.elexis.connect.fuji.drichem3500;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.interfaces.events.MessageEvent;
import ch.elexis.core.importer.div.importers.TransientLabResult;
import ch.elexis.core.importer.div.service.holder.LabImportUtilHolder;
import ch.elexis.core.model.ILabItem;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.core.ui.importer.div.importers.DefaultLabImportUiHandler;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class FujiMessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(FujiMessageHandler.class);

	private ConnectAction action;

	public FujiMessageHandler(ConnectAction action) {
		this.action = action;
	}

	public boolean handle(String message) {
		logger.debug("Got message: " + message);
		if (isValidMessage(message)) {
			String prolog = message.substring(0, 51);
			String data = message.substring(51);
			String testmode = message.substring(1, 1 + 7);
			String date = message.substring(8, 8 + 10);
			String time = message.substring(18, 18 + 5);
			String sequence = message.substring(23, 23 + 13).trim();
			String sampleID = message.substring(36, 36 + 13).trim();
			String position = message.substring(49, 50);
			String patid = new Query<Patient>(Patient.class).findSingle(Patient.FLD_PATID, Query.EQUALS, sampleID);
			Patient pat = Patient.load(patid);
			if (!pat.exists()) {
				patid = new Query<Patient>(Patient.class).findSingle(Patient.FLD_PATID, Query.EQUALS, sequence);
				pat = Patient.load(patid);
				if (!pat.exists()) {
					logger.warn("Eingabefehler, kein Patient mit Nummer " + sequence + " vorhanden!");
					MessageEvent.fireError("Eingabefehler",
							"Es ist kein Patient mit der Nummer " + sequence + " vorhanden.");
					return false;
				}
			}
			logger.debug("Resultatzuordnung zu Patient: " + pat.getName() + " " + pat.getVorname());

			int dp = 51;
			TimeTool testDate = new TimeTool(date);
			testDate.set(time);
			List<TransientLabResult> results = new ArrayList<TransientLabResult>();
			while (message.length() > dp + 35) {
				String testname = message.substring(dp, dp + 7).trim();
				String sign = message.substring(dp + 7, dp + 8);
				String result = message.substring(dp + 8, dp + 17).trim();
				String unit = message.substring(dp + 17, dp + 23).trim();
				String dilution = message.substring(dp + 23, dp + 25);
				String warning = message.substring(dp + 25, dp + 35).trim();

				ILabItem li = LabImportUtilHolder.get().getLabItem(testname, action.getMyLab());
				if (li == null) {
					li = LabImportUtilHolder.get().createLabItem(testname, testname, action.getMyLab(), "0", "0", unit,
							LabItemTyp.NUMERIC, "Dri-Chem", "20");
					li.setUnit(unit);
					logger.warn(
							"Kein passendes Laboritem gefunden. Lege neues Laboritem mit Name " + testname + " an.");
				}

				results.add(new TransientLabResult.Builder(pat.toIPatient(), action.getMyLab(), li, result)
						.date(testDate).observationTime(testDate).comment(warning).unit(unit)
						.build(LabImportUtilHolder.get()));

				logger.debug("Laborresultat hinzugefuegt: " + result);
				dp += 36;
			}
			LabImportUtilHolder.get().importLabResults(results, new DefaultLabImportUiHandler());
			return true;
		}
		logger.debug("Received not valid fuji message of size [" + message.length() + "]");
		return false;
	}

	private boolean isValidMessage(String message) {
		if (message != null && message.length() >= 52) {
			return true;
		}
		return false;
	}
}
