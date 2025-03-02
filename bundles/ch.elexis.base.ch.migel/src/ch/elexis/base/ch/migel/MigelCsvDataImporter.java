package ch.elexis.base.ch.migel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.StringJoiner;
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

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import ch.elexis.artikel_ch.data.service.MiGelCodeElementService;
import ch.elexis.base.ch.migel.ui.MiGelImporter;
import ch.elexis.core.exceptions.AccessControlException;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.builder.IArticleBuilder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.ArticleTyp;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=migel_csv")
public class MigelCsvDataImporter extends AbstractReferenceDataImporter implements IReferenceDataImporter {
	private static Logger log = LoggerFactory.getLogger(MiGelImporter.class);
	static Pattern pattern = Pattern.compile("([a-z0-9A-Z])([A-Z][a-z])"); //$NON-NLS-1$
	private final String SRC_ENCODING = "iso-8859-1"; //$NON-NLS-1$

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

	public static String getShortname(String text) {
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

	@Override
	/**
	 * Reads a CSV a published by Medelexis. e.g downloaded from
	 * https://medelexis.ch/wp-content/uploads/2022/04/MiGel_2022v2.csv
	 *
	 * @param file
	 * @param monitor
	 * @return
	 */
	public IStatus performImport(@Nullable IProgressMonitor monitor, InputStream input, @Nullable Integer newVersion) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			InputStreamReader isr;
			isr = new InputStreamReader(input, SRC_ENCODING);
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
		} catch (IOException e1) {
			log.error("Exception {} while running import", e1.getMessage());
		} catch (CsvValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessControlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Status.CANCEL_STATUS;
	}

	@Override
	public int getCurrentVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

}
