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
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

import ch.elexis.artikel_ch.data.service.MiGelCodeElementService;
import ch.elexis.base.ch.migel.Messages;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.builder.IArticleBuilder;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.ArticleTyp;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;

public class MiGelImporter extends ImporterPage {
	private final String SRC_ENCODING = "iso-8859-1"; //$NON-NLS-1$
	boolean bDelete = false;
	Button bClear;
	String mode;
	private static Logger log = LoggerFactory.getLogger(MiGelImporter.class);

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
	}

	@Override
	public String getTitle() {
		return "MiGeL"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return Messages.MiGelImporter_PleaseSelectFile;
	}

	/**
	 * Reads an XLSX file as provided by the BAT, eg. downloaded from
	 * https://www.bag.admin.ch/dam/bag/de/dokumente/kuv-leistungen/Mittel-%20und%20Gegenst%C3%A4ndeliste/migel_gesamtliste_010722_excel.xlsx.download.xlsx/migel-gesamtliste-per010722.xlsx
	 *
	 * @param file    the file to be read
	 * @param monitor the monitor
	 * @return
	 */
	public IStatus importXLSX(final File file, @Nullable final IProgressMonitor monitor) {
		String path = file.getAbsolutePath();
		if (path != null && !path.isEmpty() && path.toLowerCase().endsWith("xlsx")) { //$NON-NLS-1$
			try (FileInputStream is = new FileInputStream(path)) {
				ExcelWrapper xl = new ExcelWrapper();
				int worksheet_id = 0;
				String lang = ch.elexis.core.data.activator.CoreHub.localCfg.get(Preferences.ABL_LANGUAGE, "d");
				if (lang.equalsIgnoreCase("d")) {
					worksheet_id = 0;
				} else if (lang.equalsIgnoreCase("f")) {
					worksheet_id = 1;
				} else if (lang.equalsIgnoreCase("i")) {
					worksheet_id = 2;
				} else {
					String msg = String.format("Preferences: System/general language selection for %s was %s",
							Preferences.ABL_LANGUAGE, lang);
					log.error(msg);
					SWTHelper.showError("Bevorzugte Sprache falsch definiert", msg);
					return Status.CANCEL_STATUS;
				}
				log.info("Using worksheet with id {} for lang {}", worksheet_id, lang);
				if (xl.load(is, worksheet_id)) {
					xl.setFieldTypes(new Class[] { String.class, String.class, String.class, String.class, String.class,
							String.class, String.class, String.class, String.class, String.class, String.class,
							String.class, String.class, String.class, String.class });
					readExcelLines(xl, monitor);
				}
			} catch (IOException e) {
				String msg = String.format("cannot import file at %s %s", path, e.getMessage()); //$NON-NLS-1$
				SWTHelper.showError(ch.elexis.core.l10n.Messages.GenericImporter_ErrorImporting, msg);
				return Status.CANCEL_STATUS;
			}
		} else {
			String msg = String.format("File is invalid %s", path); //$NON-NLS-1$
			SWTHelper.showError(ch.elexis.core.l10n.Messages.GenericImporter_ErrorImporting, msg);
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	private void readExcelLines(ExcelWrapper xl, final IProgressMonitor monitor) {
		int lastRow = xl.getLastRow();
		int firstRow = xl.getFirstRow() + 1; // header offset
		// @formatter:off
		/*
		 *  0 A Produktegruppe
		 *  1 B Kategorie
		 *  2 C 1. Unterkategorie
		 *  3 D 2. Unterkategorie
		 *  4 E 3. Unterkategorie
		 *  5 F 4. Unterkategorie
		 *  6 G 5. Unterkategorie
		 *  7 H Positions-Nr.
		 *  8 I L
		 *  9 H Bezeichnung
		 *  10 K Limitation
		 *  11 L Menge/ Einheit
		 *  12 M HVB Selbstanwendung
		 *  13 N HVB Pflege
		 *  14 O Gültig ab
		 *  14 P Rev.
		 *  15 Q Sortierung
		 */
		// @formatter:on
		monitor.beginTask("MiGeL Import " + mode, (int) lastRow); //$NON-NLS-1$
		int invalidAmounts = 0;
		int nrItems = 0;
		String category = "unknown"; //$NON-NLS-1$
		for (int i = firstRow; i <= lastRow; i++) {
			List<String> row = xl.getRow(i);
			// POSNUMER(0), NAME(1), UNIT(2), PRICE(3), CATEGORY(4), SUBCATEGORY(5),
			// AMOUNT(6);
			String code = row.get(7);
			String name = row.get(9);
			if (code.isBlank()) {
				int howMany = name.indexOf(StringUtils.LF);
				if (howMany > 0) {
					category = name.substring(0, howMany);
				} else {
					category = name;
				}
				continue;
			}
			nrItems++;
			String position = row.get(0);
			String amount = row.get(12).replace("’", "");

			StringBuilder text = new StringBuilder();
			if (!category.isEmpty()) {
				text.append(StringTool.getFirstLine(category, 80, "[\\n\\r]")).append(" - "); //$NON-NLS-1$ //$NON-NLS-2$
			}
			text.append(name);
			String unit_size = row.get(11);
			String size = null;
			String unit = null;
			Pattern p1 = Pattern.compile("(\\d+) (.+)");
			Matcher m1 = p1.matcher(unit_size);
			if (m1.find()) {
				size = m1.group(1);
				unit = m1.group(2);
			} else {
				Pattern p2 = Pattern.compile("(\\d+)");
				Matcher m2 = p2.matcher(unit_size);
				if (m1.find()) {
					size = m2.group(1);
				} else {
					unit = unit_size;
				}
			}
			String shortname = getShortname(text.toString());
			log.trace("xlsx: row {} /{} id {} pos {} code {} shortname {} unit {} size {} price {} text length {}",
					nrItems, i, MiGelCodeElementService.MIGEL_NAME + code, position, code, shortname, unit, size,
					amount, text.length());

			IArticle migelArticle = new IArticleBuilder(CoreModelServiceHolder.get(), shortname, code, ArticleTyp.MIGEL)
					.build();
			CoreModelServiceHolder.get().setEntityProperty("id", MiGelCodeElementService.MIGEL_NAME + code, //$NON-NLS-1$
					migelArticle);
			if (unit != null) {
				migelArticle.setPackageUnit(unit);
			}
			if (size != null) {
				migelArticle.setPackageSize((int) Double.parseDouble(size));
			}
			try {
				migelArticle.setSellingPrice(new Money(amount));
			} catch (ParseException e1) {
				invalidAmounts++;
				log.warn("Amount not a money {} for code {}. Raw value was {}", amount, code, row.get(12));
			}
			migelArticle.setExtInfo("FullText", text.toString()); //$NON-NLS-1$
			CoreModelServiceHolder.get().save(migelArticle);
			monitor.worked(1);
		}
		log.info("Finished reading {} xlsx lines. Read {} items with {} invalidAmounts", lastRow, nrItems,
				invalidAmounts);
	};

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
			long l = file.length();
			monitor.beginTask("MiGeL Import " + mode, (int) l / 100); //$NON-NLS-1$
			if (file.getName().toLowerCase().endsWith("csv")) { //$NON-NLS-1$
				return importCSV(file, monitor);
			} else if (file.getName().toLowerCase().endsWith("xlsx")) {
				return importXLSX(file, monitor);
			} else {
				String msg = String.format("Unable to open file %s", file.toPath()); //$NON-NLS-1$
				log.error(msg);
				SWTHelper.showError(ch.elexis.core.l10n.Messages.GenericImporter_ErrorImporting, msg);
			}
		} catch (Exception ex) {
			String msg = String.format("Unable to import file %s", ex.getMessage()); //$NON-NLS-1$
			log.error(msg);
			SWTHelper.showError(ch.elexis.core.l10n.Messages.GenericImporter_ErrorImporting, msg);
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

	static Pattern pattern = Pattern.compile("([a-z0-9A-Z])([A-Z][a-z])"); //$NON-NLS-1$

	/**
	 * Reads a CSV a published by Medelexis. e.g downloaded from
	 * https://medelexis.ch/wp-content/uploads/2022/04/MiGel_2022v2.csv
	 *
	 * @param file
	 * @param monitor
	 * @return
	 */
	public IStatus importCSV(final File file, @Nullable final IProgressMonitor monitor) {
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), SRC_ENCODING);
			CSVReader reader = new CSVReader(isr);
			String[] line;
			Integer nrLine = 0;
			monitor.subTask(Messages.MiGelImporter_ReadMigel);
			while ((line = reader.readNext()) != null) {
				if (isFieldsLine(line) && line.length >= 3) {
					StringBuilder text = new StringBuilder();
					String category = ImportFields.SUBCATEGORY.getStringValue(line);
					if (category.isEmpty()) {
						category = ImportFields.CATEGORY.getStringValue(line);
					}
					// category only 1 line and max 80 char
					if (!category.isEmpty()) {
						text.append(StringTool.getFirstLine(category, 80, "[\\n\\r]")).append(" - "); //$NON-NLS-1$ //$NON-NLS-2$
					}
					text.append(ImportFields.NAME.getStringValue(line));

					String amount = ImportFields.AMOUNT.getStringValue(line);
					String unit = ImportFields.UNIT.getStringValue(line);
					// try to parse amount from unit
					if (amount.isEmpty() && !unit.isEmpty() && Character.isDigit(unit.charAt(0))) {
						String[] parts = unit.split(StringUtils.SPACE);
						if (parts != null && parts.length > 1) {
							amount = parts[0];
							StringJoiner unitWithoutDigit = new StringJoiner(StringUtils.SPACE);
							for (int i = 1; i < parts.length; i++) {
								unitWithoutDigit.add(parts[i]);
							}
							unit = unitWithoutDigit.toString();
						}
					}

					String code = ImportFields.POSNUMER.getStringValue(line);
					String shortname = getShortname(text.toString());

					IArticle migelArticle = new IArticleBuilder(CoreModelServiceHolder.get(), shortname,
							ImportFields.POSNUMER.getStringValue(line), ArticleTyp.MIGEL).build();

					nrLine++;
					log.trace(
							"csv: row {} id {} pos {} code {} shortname {} unit <{}> size <{}> price {} text length {}",
							nrLine, MiGelCodeElementService.MIGEL_NAME + code,
							ImportFields.POSNUMER.getStringValue(line), code, shortname, unit, amount,
							ImportFields.PRICE.getMoneyValue(line), text.toString().length());
					CoreModelServiceHolder.get().setEntityProperty("id", MiGelCodeElementService.MIGEL_NAME + code, //$NON-NLS-1$
							migelArticle);
					migelArticle.setPackageUnit(unit);
					migelArticle.setSellingPrice(ImportFields.PRICE.getMoneyValue(line));
					migelArticle.setExtInfo("FullText", text.toString()); //$NON-NLS-1$

					if (!amount.isEmpty()) {
						try {
							double amountDbl = Double.parseDouble(amount);
							migelArticle.setPackageSize((int) amountDbl);
						} catch (NumberFormatException e) {
							// ignore
						}
					}
					CoreModelServiceHolder.get().save(migelArticle);
					monitor.worked(1);
				}
			}
			reader.close();
			monitor.done();
			return Status.OK_STATUS;
		} catch (IOException e) {
			String msg = String.format("Error importing file %s: %e", file.toPath(), e.getMessage()); //$NON-NLS-1$
			log.error(msg);
			SWTHelper.showError(ch.elexis.core.l10n.Messages.GenericImporter_ErrorImporting, msg);
			return Status.CANCEL_STATUS;
		}
	}

	private String getShortname(String text) {
		String shortname = StringTool.getFirstLine(text, 120, "[\\n\\r]"); //$NON-NLS-1$
		Matcher matcher = pattern.matcher(shortname);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1) + StringUtils.SPACE + matcher.group(2));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	private boolean isFieldsLine(String[] line) {
		// line[0] contains the code, which always contains digits, so if not its is
		// probably the description
		return containsDigits(line[0]);
	}

	private boolean containsDigits(String string) {
		for (char character : string.toCharArray()) {
			if (Character.isDigit(character)) {
				return true;
			}
		}
		return false;
	}
}
