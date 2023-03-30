/*******************************************************************************
 * Copyright (c) 2013-2017 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.importer;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import at.medevit.ch.artikelstamm.ArtikelstammHelper;
import at.medevit.ch.artikelstamm.elexis.common.service.VersionUtil;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.ICodeElementBlock;
import ch.elexis.core.services.IConfigService.ILocalLock;
import ch.elexis.core.services.IReferenceDataImporterService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.TimeTool;

public class ArtikelstammImporterPage extends ImporterPage {

	boolean userCanceled = false;

	@Inject
	private IReferenceDataImporterService importerService;

	public ArtikelstammImporterPage() {
		CoreUiUtil.injectServices(this);
	}

	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception {
		ILocalLock lock = ConfigServiceHolder.get().getLocalLock("ArtikelstammImporter"); //$NON-NLS-1$
		try {
			if (!lock.tryLock()) {
				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {
						if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), StringUtils.EMPTY,
								"Der Importer ist durch einen anderen Benutzer gestartet.\nDie Artikelstammeinträge werden bereits importiert.\n\n"
										+ "Startzeit: "
										+ new TimeTool(lock.getLockCurrentMillis()).toString(TimeTool.LARGE_GER)
										+ "\nGestartet durch: " + lock.getLockMessage()
										+ "\n\nWollen Sie den Importer trotzdem nochmal starten ?")) {
							lock.unlock();
							lock.tryLock();
							userCanceled = false;
						} else {
							userCanceled = true;
						}
					}
				});
			}
			if (userCanceled) {
				userCanceled = false;
				return Status.OK_STATUS;
			}

			LoggerFactory.getLogger(getClass()).info("ArtikelstammImporterPage.doImport " + results[0]); //$NON-NLS-1$
			IReferenceDataImporter refImporter = getImporter();
			IStatus status = refImporter.performImport(monitor, new FileInputStream(results[0]), null);
			if (!status.isOK()) {
				StatusManager.getManager().handle(status, StatusManager.SHOW);
			} else {
				validateLeistungsblockReferences(monitor);
			}
			return status;
		} finally {
			lock.unlock();
		}
	}

	private IReferenceDataImporter getImporter() {
		// default importer
		return importerService.getImporter("artikelstamm_v5") //$NON-NLS-1$
				.orElseThrow(() -> new IllegalStateException("No ReferenceDataImporter available")); //$NON-NLS-1$
	}

	@Override
	public String getTitle() {
		return "Artikelstamm CH Import";
	}

	@Override
	public String getDescription() {
		return "Importiere Artikelstamm";
	}

	@Override
	public Composite createPage(Composite parent) {
		Composite versionInfo = new Composite(parent, SWT.None);
		versionInfo.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		versionInfo.setLayout(new GridLayout(2, false));
		Label lblVersion = new Label(versionInfo, SWT.None);
		lblVersion.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		lblVersion.setText("Aktuelle Version:");
		Label lblVERSION = new Label(versionInfo, SWT.None);
		lblVERSION.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		int version = VersionUtil.getCurrentVersion();
		StringBuilder sb = new StringBuilder();
		sb.append(" v" + version); //$NON-NLS-1$
		Date importSetCreationDate = VersionUtil.getImportSetCreationDate();
		if (importSetCreationDate != null) {
			sb.append(" / " + ArtikelstammHelper.monthAndYearWritten.format(VersionUtil.getImportSetCreationDate())); //$NON-NLS-1$
		}
		lblVERSION.setText(sb.toString());

		Composite ret = new ImporterPage.FileBasedImporter(parent, this);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout(2, false));

		return ret;
	}

	private static void validateLeistungsblockReferences(IProgressMonitor monitor) {
		final Map<String, List<String>> nonResolvableArtikelstammItems = new HashMap<String, List<String>>();

		List<ICodeElementBlock> codeElementBlocks = CoreModelServiceHolder.get().getQuery(ICodeElementBlock.class)
				.execute();
		monitor.beginTask("Checking Artikelstamm-References in Leistungsblock", codeElementBlocks.size());
		for (ICodeElementBlock leistungsblock : codeElementBlocks) {
			List<String> entry = nonResolvableArtikelstammItems.get(leistungsblock.getId());
			List<ICodeElement> elements = leistungsblock.getElements();
			List<ICodeElement> diffToReferences = leistungsblock.getDiffToReferences(elements);
			if (diffToReferences.size() > 0) {
				for (ICodeElement iCodeElement : diffToReferences) {
					if (ArtikelstammConstants.CODESYSTEM_NAME.equals(iCodeElement.getCodeSystemName())) {
						// non-resolvable ArtikelstammItem
						if (entry == null) {
							entry = new ArrayList<String>();
						}
						entry.add(iCodeElement.getText() + " [" + iCodeElement.getCode() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
			if (entry != null) {
				nonResolvableArtikelstammItems.put(leistungsblock.getCode(), entry);
			}
			monitor.worked(1);
		}

		Set<Entry<String, List<String>>> entrySet = nonResolvableArtikelstammItems.entrySet();
		if (!entrySet.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append(
					"Die folgenden Artikelstamm-Referenzen in den genannten Leistungsblöcken sind nicht mehr auflösbar:\n\n");
			for (Entry<String, List<String>> entry : entrySet) {
				sb.append(entry.getKey() + ":\n"); //$NON-NLS-1$
				List<String> value = entry.getValue();
				for (String string : value) {
					sb.append("\t" + string + StringUtils.LF); //$NON-NLS-1$
				}
			}

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							"Leistungsblock Artikelstamm-Referenzen", sb.toString());
				}
			});
		}
	}
}
