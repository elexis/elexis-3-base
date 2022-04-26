/*******************************************************************************
 * Copyright (c) 2012 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 ******************************************************************************/
package at.medevit.elexis.epha.interactions.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.epha.interactions.api.EphaInteractionsApi;
import at.medevit.elexis.epha.interactions.api.model.AdviceResponse;
import at.medevit.elexis.epha.interactions.api.model.Substance;
import at.medevit.elexis.epha.interactions.preference.EphaConstants;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.holder.CodeElementServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.text.IRichTextDisplay;
import ch.elexis.core.ui.util.IKonsExtension;

public class EphaApiSearchAction extends Action implements IKonsExtension, IHandler {

	public static final Logger logger = LoggerFactory.getLogger(EphaApiSearchAction.class);

	public static final String ID = "at.medevit.elexis.epha.interactions.EphaSearchAction"; //$NON-NLS-1$
	private static EphaApiSearchAction instance;

	private EphaInteractionsApi interactionsApi;

	public String connect(IRichTextDisplay tf) {
		return "at.medevit.elexis.epha.interactions.EphaSearchAction"; //$NON-NLS-1$
	}

	public EphaApiSearchAction() {
		super("Medikamenteninteraktion prüfen ..."); //$NON-NLS-1$

		interactionsApi = new EphaInteractionsApi();
	}

	public boolean doLayout(StyleRange n, String provider, String id) {
		return false;
	}

	public boolean doXRef(String refProvider, String refID) {
		return false;
	}

	@Override
	public void run() {
		Optional<IPatient> patient = ContextServiceHolder.get().getActivePatient();
		if (patient.isPresent()) {
			List<IPrescription> medication = patient.get()
					.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION, EntryType.SYMPTOMATIC_MEDICATION));
			if (medication != null && !medication.isEmpty()) {
				Object ret = interactionsApi.advice(medication.stream().filter(p -> p.getArticle() != null)
						.map(p -> Substance.of(p.getArticle())).collect(Collectors.toList()));
				if (ret instanceof AdviceResponse && ((AdviceResponse) ret).getData() != null) {
					if (((AdviceResponse) ret).getData().getSafety() > EphaConstants.SAFTEY_INFO) {
						if (MessageDialog.open(MessageDialog.INFORMATION, Display.getDefault().getActiveShell(), "Epha",
								getMessage((AdviceResponse) ret), SWT.SHEET, "Epha Interaktionen öffnen",
								IDialogConstants.OK_LABEL) == 0) {
							Program.launch(((AdviceResponse) ret).getData().getLink());
						}
					} else if (((AdviceResponse) ret).getData().getSafety() > EphaConstants.SAFTEY_WARN) {
						if (MessageDialog.open(MessageDialog.WARNING, Display.getDefault().getActiveShell(), "Epha",
								getMessage((AdviceResponse) ret), SWT.SHEET, "Epha Interaktionen öffnen") == 0) {
							Program.launch(((AdviceResponse) ret).getData().getLink());
						}
					} else {
						if (MessageDialog.open(MessageDialog.ERROR, Display.getDefault().getActiveShell(), "Epha",
								getMessage((AdviceResponse) ret), SWT.SHEET, "Epha Interaktionen öffnen") == 0) {
							Program.launch(((AdviceResponse) ret).getData().getLink());
						}
					}
				} else if (ret instanceof AdviceResponse && ((AdviceResponse) ret).getMeta() != null) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
							"Es ist folgender Fehler aufgetreten.\n\n" + ((AdviceResponse) ret).getMeta().getStatus()
									+ " - " + ((AdviceResponse) ret).getMeta().getMessage());
				} else {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
							"Es ist folgender Fehler aufgetreten.\n\n" + ret);
				}
			} else {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Info",
						"Der Patient hat keine fix Medikation");
			}
		} else {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Info",
					"Es ist kein Patient selektiert");
		}
	}

	private String getMessage(AdviceResponse response) {
		int safety = response.getData().getSafety();
		StringBuilder ret = new StringBuilder();
		if (safety > EphaConstants.SAFTEY_INFO) {
			ret.append("Keine relevanten Einschränkung der Medikamentensicherheit");
		} else if (safety > EphaConstants.SAFTEY_WARN) {
			ret.append("Erhöhtes Risiko");
		} else {
			ret.append("Stark erhöhtes Risiko");
		}
		if (response.getData().getValid() != null) {
			ret.append("\n\nEs wurden " + response.getData().getValid().size()
					+ " Medikamente erfolgreich an epha übertragen.");
		}
		if (response.getData().getFails() != null && !response.getData().getFails().isEmpty()) {
			List<Map<String, String>> fails = response.getData().getFails();
			ret.append("\n\nFolgende Medikamente konnten bei epha nicht gefunden werden:");
			for (Map<String, String> failMap : fails) {
				if (StringUtils.isNoneBlank(failMap.get("name"))) {
					ret.append("\n- " + failMap.get("name"));
				} else if (StringUtils.isNoneBlank(failMap.get("gtin"))) {
					ICodeElementServiceContribution artikelstammContribution = CodeElementServiceHolder.get()
							.getContribution(CodeElementTyp.ARTICLE, "Artikelstamm").orElse(null);
					if (artikelstammContribution != null) {
						ICodeElement loaded = artikelstammContribution.loadFromCode(failMap.get("gtin")).orElse(null);
						if (loaded != null) {
							ret.append("\n- " + loaded.getText());
							continue;
						}
					}
					ret.append("\n- GTIN: " + failMap.get("gtin"));
				}
			}
		}
		return ret.toString();
	}

	public IAction[] getActions() {
		return new IAction[] { this };
	}

	public void insert(Object o, int pos) {
		// TODO Auto-generated method stub

	}

	public void removeXRef(String refProvider, String refID) {
		// TODO Auto-generated method stub

	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		// TODO Auto-generated method stub

	}

	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (instance != null)
			instance.run();
		return null;
	}

	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub
	}
}
