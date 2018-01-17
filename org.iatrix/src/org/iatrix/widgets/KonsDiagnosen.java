/*******************************************************************************
 * Copyright (c) 2007-2015, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     Gerry Weirich - adapted for 2.1
 *     Niklaus Giger - small improvements, split into 20 classes
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.iatrix.util.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IDiagnose;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.StringTool;

public class KonsDiagnosen implements IJournalArea {

	private Konsultation actKons = null;
	private CLabel lDiagnosis;
	private static Logger log = LoggerFactory.getLogger(KonsDiagnosen.class);

	public KonsDiagnosen(Composite konsultationComposite){
		lDiagnosis = new CLabel(konsultationComposite, SWT.LEFT);
		lDiagnosis.setText("");
		FormToolkit tk = UiDesk.getToolkit();
		tk.adapt(lDiagnosis);
		lDiagnosis.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
	}

	private void setDiagnosenText(Konsultation konsultation){
		String text = "";
		Image image = null;
		actKons = konsultation;

		if (konsultation != null) {
			List<IDiagnose> diagnosen = konsultation.getDiagnosen();
			if (diagnosen != null && diagnosen.size() > 0) {
				List<String> dxList = new ArrayList<>();
				for (IDiagnose diagnose : diagnosen) {
					dxList.add(diagnose.getLabel());
				}
				text = "Diagnosen: " + StringTool.join(dxList, ", ");
			} else {
				// no diagnosis, warn error
				text = "Keine Diagnosen";
				image = Images.IMG_ACHTUNG.getImage();
			}
		}

		lDiagnosis.setText(PersistentObject.checkNull(text));
		lDiagnosis.setImage(image);
		logEvent("setDiagnosenText");
	}

	private void updateKonsultation(boolean updateText){
		if (actKons != null) {
			setDiagnosenText(actKons);
		} else {
			setDiagnosenText(null);
		}
		logEvent("updateKonsultation");
	}
	private void logEvent(String msg){
		StringBuilder sb = new StringBuilder(msg + ": ");
		sb.append(lDiagnosis.getText()+ " ");
		if (actKons == null) {
			sb.append("actKons null ");
		} else {
			sb.append(actKons.getId());
			Patient pat = actKons.getFall().getPatient();
			sb.append(" kons vom " + actKons.getDatum());
			sb.append(" " + pat.getId() + ": " + pat.getPersonalia());
		}
		log.debug(sb.toString());
	}

	@Override
	public void setKons(Patient newPatient, Konsultation newKons, KonsActions op){
		if (newPatient == null || newKons == null) {
			actKons = null;
			updateKonsultation(true);
		}
		boolean konsChanged = !Helpers.twoKonsSamePatient(actKons,  newKons);
		logEvent("setKons " + (newKons != null ? newKons.getId() +
				" vom " + newKons.getDatum() : "null") +
				" konsChanged: " + konsChanged);
		if (op == KonsActions.ACTIVATE_KONS || konsChanged) {
			updateKonsultation(true);
		}
		actKons = newKons;

	}

	@Override
	public void visible(boolean mode){
		// nothing todo
	}

	@Override
	public void activation(boolean mode, Patient selectedPat, Konsultation selectedKons){
		if (mode == false) {
			setDiagnosenText(null);
		}

	}
}
