/*******************************************************************************
 * Copyright (c) 2006-2017, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    T. Huster - copied from ch.elexis.base.ch.artikel
 *    N. Giger - added support to import an XLSX file from the BAG
 *
 *******************************************************************************/
package ch.elexis.base.ch.migel.ui;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.migel.Messages;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IReferenceDataImporterService;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.ArticleTyp;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.Money;
import jakarta.inject.Inject;

public class MiGelImporter extends ImporterPage {
	boolean bDelete = false;
	Button bClear;
	String mode;
	private static Logger log = LoggerFactory.getLogger(MiGelImporter.class);
	@Inject
	private IReferenceDataImporterService importerService;

	private enum ImportFields {
		POSNUMER(0), NAME(1), UNIT(2), PRICE(3), CATEGORY(4), SUBCATEGORY(5), AMOUNT(6);

		private int index;

		ImportFields(int index) {
			this.index = index;
		}

		private boolean exists(String[] line) {
			return line.length > index;
		}

		public String getStringValue(String[] line) {
			if (exists(line)) {
				if (this == NAME && line[index].contains(StringUtils.LF)) {
					line[index] = getJoinedFirstLines(line[index]);
				}
				return line[index];
			} else {
				return StringUtils.EMPTY;
			}
		}

		private String getJoinedFirstLines(String string) {
			String[] parts = string.split(StringUtils.LF);
			if (parts.length > 1) {
				StringBuilder ret = new StringBuilder();
				if (!parts[1].isEmpty()) {
					if (parts[0].endsWith(",")) { //$NON-NLS-1$
						ret.append(parts[0] + StringUtils.SPACE + parts[1]);
					} else {
						ret.append(parts[0] + ", " + parts[1]); //$NON-NLS-1$
					}
				} else {
					ret.append(parts[0] + StringUtils.LF);
				}
				StringJoiner rest = new StringJoiner(StringUtils.LF);
				for (int i = 2; i < parts.length; i++) {
					rest.add(parts[i]);
				}
				if (rest.length() > 0) {
					ret.append(StringUtils.LF).append(rest);
				}
				return ret.toString();
			} else {
				return string;
			}
		}

		public Money getMoneyValue(String[] line) {
			if (exists(line)) {
				try {
					return new Money(getStringValue(line));
				} catch (ParseException e) {
					// ignore
				}
			}
			return new Money();
		}

	}

	public MiGelImporter() {
		CoreUiUtil.injectServicesWithContext(this);
	}

	@Override
	public String getTitle() {
		return "MiGeL"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return Messages.MiGelImporter_PleaseSelectFile;
	}

	@Override
	public List<String> getObjectClass() {
		return Collections.singletonList(IArticle.class.getName());
	}

	@Override
	public IStatus doImport(final IProgressMonitor monitor) throws Exception {
		mode = Messages.MiGelImporter_ModeUpdateAdd;
		if (bDelete == true) {
			IQuery<IArticle> query = CoreModelServiceHolder.get().getQuery(IArticle.class, true);
			query.and(ModelPackage.Literals.IARTICLE__TYP, COMPARATOR.EQUALS, ArticleTyp.MIGEL);
			List<IArticle> existing = query.execute();
			existing.forEach(a -> CoreModelServiceHolder.get().remove(a));
			mode = Messages.MiGelImporter_ModeCreateNew;
		}
		try {
			File file = new File(results[0]);
			log.info("Checking file " + file.getAbsolutePath() + " importerService " + importerService);
			if (file.getName().toLowerCase().endsWith("csv")) { //$NON-NLS-1$
				IReferenceDataImporter importer = importerService.getImporter("migel_csv")
						.orElseThrow(() -> new IllegalStateException("No IReferenceDataImporter available."));
				return importer.performImport(monitor, new FileInputStream(results[0]), null);
			} else if (file.getName().toLowerCase().endsWith("xlsx")) {
				IReferenceDataImporter importer = importerService.getImporter("migel_xlsx")
						.orElseThrow(() -> new IllegalStateException("No IReferenceDataImporter available."));
				return importer.performImport(monitor, new FileInputStream(results[0]), null);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			String msg = String.format("Unable to import file %s", ex.getMessage()); //$NON-NLS-1$
			log.info(ex.getStackTrace().toString());

			log.error(msg);
			SWTHelper.showError(ch.elexis.core.l10n.Messages.Core_Error_while_importing, msg);
		}
		return Status.CANCEL_STATUS;
	}

	@Override
	public void collect() {
		bDelete = bClear.getSelection();
	}

	@Override
	public Composite createPage(final Composite parent) {
		Composite ret = new ImporterPage.FileBasedImporter(parent, this);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		bClear = new Button(parent, SWT.CHECK | SWT.WRAP);
		bClear.setText(Messages.MiGelImporter_ClearAllData);
		bClear.setSelection(true);
		bClear.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		return ret;

	}
}
