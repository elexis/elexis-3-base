/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.command;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;
import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.constants.GDTPreferenceConstants;
import at.medevit.elexis.gdt.handler.GDTOutputHandler;
import at.medevit.elexis.gdt.interfaces.HandlerProgramType;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6301;
import at.medevit.elexis.gdt.tools.GDTSatzNachrichtHelper;
import at.medevit.elexis.gdt.ui.dialog.StammdatenUebermittelnDialog;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IOrganization;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.EncounterServiceHolder;
import ch.elexis.data.Patient;

public class StammdatenUebermitteln extends AbstractHandler {

	public static final String ID = "at.medevit.elexis.gdt.command.StammdatenUebermitteln"; //$NON-NLS-1$
	public static final String PARAM_ID = "at.medevit.elexis.gdt.cmd.parameter.partnerClassname"; //$NON-NLS-1$
	private static final String PARAM_TARGET_ID = "at.medevit.elexis.gdt.cmd.parameter.targetId"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Patient pat = null;
		String commPartnerClass = event.getParameter(PARAM_ID);

		IWorkbenchWindow iWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		if (iWorkbenchWindow != null) {
			ISelection selection = iWorkbenchWindow.getActivePage().getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection strucSelection = (IStructuredSelection) selection;
				pat = getAsPatient(strucSelection);
			}
		}

		String configuredGDTId = CoreHub.localCfg.get(GDTPreferenceConstants.CFG_GDT_ID, StringUtils.EMPTY);
		if (pat == null) {
			pat = ElexisEventDispatcher.getSelectedPatient();
		}
		if (pat == null) {
			return null;
		}

		String ahvNummer = null;
		String socialSecurityNumber = pat.getXid(DOMAIN_AHV);
		if (socialSecurityNumber != null) {
			ahvNummer = socialSecurityNumber.trim();
		}

		GDTSatzNachricht6301 gdt6301 = new GDTSatzNachricht6301(pat.get(Patient.FLD_PATID), pat.getName(),
				pat.getVorname(), pat.getGeburtsdatum(), null, pat.get(Patient.TITLE), ahvNummer,
				pat.get(Patient.FLD_ZIP) + StringUtils.SPACE + pat.get(Patient.FLD_PLACE), pat.get(Patient.FLD_STREET),
				null, GDTSatzNachrichtHelper.bestimmeGeschlechtsWert(pat.get(Patient.FLD_SEX)), null, null, null, null,
				configuredGDTId, GDTConstants.ZEICHENSATZ_IBM_CP_437 + StringUtils.EMPTY, GDTConstants.GDT_VERSION);

		NoPoUtil.loadAsIdentifiable(pat, IPatient.class).ifPresent(patient -> {
			if (StringUtils.isNotBlank(patient.getEmail())) {
				gdt6301.setValue(GDTConstants.FELDKENNUNG_PATIENT_EMAIL, patient.getEmail());
			} else if (StringUtils.isNotBlank(patient.getEmail2())) {
				gdt6301.setValue(GDTConstants.FELDKENNUNG_PATIENT_EMAIL, patient.getEmail2());
			}

			Optional<ICoverage> coverage = getGdtFall(patient);
			if (coverage.isPresent()) {
				if (StringUtils.isNotBlank(coverage.get().getInsuranceNumber())) {
					gdt6301.setValue(GDTConstants.FELDKENNUNG_PATIENT_VERSICHERTENNUMMER,
							coverage.get().getInsuranceNumber());
				}
				getInsurance(coverage.get()).ifPresent(insurance -> {
					if (StringUtils.isNotBlank(insurance.getDescription1())) {
						gdt6301.setValue(GDTConstants.FELDKENNUNG_PATIENT_KRANKENKASSE, insurance.getDescription1());

					}
					IXid eanXid = insurance.getXid(DOMAIN_EAN);
					if (eanXid != null && StringUtils.isNoneBlank(eanXid.getDomainId())) {
						gdt6301.setValue(GDTConstants.FELDKENNUNG_PATIENT_KRANKENKASSEEAN, eanXid.getDomainId());
					}
				});
			}
		});

		StammdatenUebermittelnDialog sud = new StammdatenUebermittelnDialog(Display.getCurrent().getActiveShell(),
				gdt6301, commPartnerClass);
		sud.setTargetIdSelection(event.getParameter(PARAM_TARGET_ID));
		int retVal = sud.open();

		if (retVal == TitleAreaDialog.CANCEL)
			return null;

		IGDTCommunicationPartner cp = sud.getGDTCommunicationPartner();
		GDTOutputHandler.handleOutput(gdt6301, cp, HandlerProgramType.DEFAULT);

		return null;
	}

	private Optional<ICoverage> getGdtFall(IPatient patient) {
		Optional<ICoverage> activeCoverage = ContextServiceHolder.get().getActiveCoverage();
		if (activeCoverage.isPresent() && activeCoverage.get().isOpen()
				&& getInsurance(activeCoverage.get()).isPresent()) {
			return activeCoverage;
		}
		IEncounter latestEncounter = EncounterServiceHolder.get().getLatestEncounter(patient, false).orElse(null);

		if (latestEncounter != null && latestEncounter.getCoverage() != null
				&& getInsurance(latestEncounter.getCoverage()).isPresent()) {
			return Optional.of(latestEncounter.getCoverage());
		}
		return Optional.empty();
	}

	private Optional<IOrganization> getInsurance(ICoverage coverage) {
		if (coverage.getCostBearer() != null && coverage.getCostBearer().isOrganization()) {
			return Optional.of(coverage.getCostBearer().asIOrganization());
		}
		return Optional.empty();
	}

	private Patient getAsPatient(IStructuredSelection strucSelection) {
		Patient ret = null;
		if (strucSelection.getFirstElement() instanceof Identifiable) {
			ret = Patient.load(((Identifiable) strucSelection.getFirstElement()).getId());
		} else {
			ret = (Patient) strucSelection.getFirstElement();
		}
		return ret;
	}
}
