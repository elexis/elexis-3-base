package ch.elexis.base.ch.migel;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.artikel_ch.data.service.MiGelCodeElementService;
import ch.elexis.base.ch.migel.ui.MiGelImporter;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.builder.IArticleBuilder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.ArticleTyp;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=migel_xlsx")
public class MigelXlsxDataImporter extends AbstractReferenceDataImporter implements IReferenceDataImporter {

	private static final String REFERENCEDATA_MIGEL_VERSION = "referencedata/migel/version";

	private static Logger log = LoggerFactory.getLogger(MiGelImporter.class);

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
		monitor.beginTask("MiGeL Import", (int) lastRow); //$NON-NLS-1$
		int invalidAmounts = 0;
		int nrItems = 0;
		String category = "unknown"; //$NON-NLS-1$
		for (int i = firstRow; i <= lastRow; i++) {
			List<String> row = xl.getRow(i);
			// POSNUMER(0), NAME(1), UNIT(2), PRICE(3), CATEGORY(4), SUBCATEGORY(5),
			// AMOUNT(6);
			if (row == null) {
				break;
			}
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
			String shortname = MigelCsvDataImporter.getShortname(text.toString());
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
	public IStatus performImport(@Nullable IProgressMonitor monitor, InputStream input, @Nullable Integer newVersion) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
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
		if (xl.load(input, worksheet_id)) {
			xl.setFieldTypes(new Class[] { String.class, String.class, String.class, String.class, String.class,
					String.class, String.class, String.class, String.class, String.class, String.class, String.class,
					String.class, String.class, String.class });
			readExcelLines(xl, monitor);
		}
		if (newVersion != null) {
			ConfigServiceHolder.get().set(REFERENCEDATA_MIGEL_VERSION, newVersion);
		}
		return Status.OK_STATUS;
	}

	@Override
	public int getCurrentVersion() {
		return ConfigServiceHolder.get().get(REFERENCEDATA_MIGEL_VERSION, 0);
	}
}
