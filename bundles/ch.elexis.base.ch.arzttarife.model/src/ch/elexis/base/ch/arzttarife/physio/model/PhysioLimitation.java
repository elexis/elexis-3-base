package ch.elexis.base.ch.arzttarife.physio.model;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedOptifier;
import ch.elexis.core.model.IBlob;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.rgw.tools.Result;

/**
 * Limitations for {@link IPhysioLeistung} billing, loaded from the
 * physio_limit.csv reference data (stored as {@link IBlob} with id
 * {@link #LIMITATION_ID}).
 * <p>
 * Supported rules from the physio tariff structure (Anhang 1a, column
 * Anwendungsregel), all other rule texts are ignored:
 * <ul>
 * <li>"Max. n Min. pro Sitzung" -&gt; {@link TYPE#AMOUNT} limitation, the summed
 * amount of the position on one encounter (Sitzung) may not exceed n /
 * {@link #MINUTES_PER_UNIT} units, as the treatment positions are billed per 5
 * minutes</li>
 * <li>"Gilt als eine (1) Sitzung" -&gt; {@link TYPE#AUTO_BILL} limitation, the session
 * marker position (e.g. 27100) is automatically billed on the encounter if not
 * already present (done by the PhysioOptifier)</li>
 * </ul>
 */
public class PhysioLimitation {

	public static final String LIMITATION_ID = "Physio_Limitation";

	/** all physio treatment positions are billed per 5 minutes */
	private static final int MINUTES_PER_UNIT = 5;

	// csv columns of physio_limit.csv
	private static final int COL_POSNR = 0;
	private static final int COL_LIMIT_MIN = 1;
	private static final int COL_LIMIT_REF = 2;
	private static final int COL_AUTO_BILL = 6;

	private static Map<String, List<PhysioLimitation>> limitations;

	public synchronized static List<PhysioLimitation> get(IPhysioLeistung leistung) {
		if (limitations == null) {
			limitations = loadLimitations();
		}
		return limitations.getOrDefault(leistung.getCode(), Collections.emptyList());
	}

	private static Map<String, List<PhysioLimitation>> loadLimitations() {
		Map<String, List<PhysioLimitation>> ret = new HashMap<>();
		Optional<IBlob> blob = CoreModelServiceHolder.get().load(LIMITATION_ID, IBlob.class);
		if (blob.isPresent()) {
			StringReader stringReader = new StringReader(blob.get().getStringContent());
			try (CSVReader reader = new CSVReaderBuilder(stringReader)
					.withCSVParser(new CSVParserBuilder().withSeparator(',').withQuoteChar('"').build())
					.withKeepCarriageReturn(false).withSkipLines(1).build()) {
				for (String[] line : reader.readAll()) {
					String posnr = col(line, COL_POSNR);
					if (StringUtils.isNotBlank(posnr)) {
						List<PhysioLimitation> list = ret.computeIfAbsent(posnr, k -> new ArrayList<>());
						if (StringUtils.isNotBlank(col(line, COL_LIMIT_MIN))) {
							// "Max. n Min. pro Sitzung"
							PhysioLimitation limit = new PhysioLimitation(TYPE.AMOUNT);
							limit.posnr = posnr;
							limit.limitMinutes = Integer.parseInt(col(line, COL_LIMIT_MIN).trim());
							limit.limitReference = col(line, COL_LIMIT_REF);
							limit.limitationAmount = limit.limitMinutes / MINUTES_PER_UNIT;
							list.add(limit);
						}
						if (StringUtils.isNotBlank(col(line, COL_AUTO_BILL))) {
							// "Gilt als eine (1) Sitzung"
							PhysioLimitation autoBill = new PhysioLimitation(TYPE.AUTO_BILL);
							autoBill.posnr = posnr;
							autoBill.autoBill = col(line, COL_AUTO_BILL).trim();
							list.add(autoBill);
						}
					}
				}
			} catch (IOException | CsvException e) {
				LoggerFactory.getLogger(PhysioLimitation.class).error("Exception loading limitations", e);
			}
		}
		return ret;
	}

	private static String col(String[] line, int idx) {
		return idx < line.length ? line[idx] : StringUtils.EMPTY;
	}

	public enum TYPE {
		AMOUNT, AUTO_BILL
	}

	private TYPE type;

	private String posnr;

	private String limitReference;

	/** the minutes from the rule text "Max. n Min. pro Sitzung" */
	private Integer limitMinutes;

	/** the maximum amount of units (5 min.) per session */
	private Integer limitationAmount;

	/** the session marker position to auto bill, e.g. 27100 */
	private String autoBill;

	public PhysioLimitation(TYPE type) {
		this.type = type;
	}

	public TYPE getType() {
		return type;
	}

	public String getAutoBillCode() {
		return autoBill;
	}

	public Result<IBilled> test(IEncounter encounter, IBilled newBilled) {
		if (type == TYPE.AMOUNT && "sitzung".equalsIgnoreCase(limitReference)) {
			return testSession(encounter, newBilled);
		}
		// AUTO_BILL is not a violation, the optifier auto bills the marker position
		return new Result<IBilled>(null);
	}

	private Result<IBilled> testSession(IEncounter encounter, IBilled newBilled) {
		if (getBilledAmount(encounter, newBilled) > limitationAmount) {
			return new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION, toString(), newBilled,
					false);
		}
		return new Result<IBilled>(null);
	}

	/**
	 * Sum the amounts of all {@link IBilled} with the same code on the encounter,
	 * replacing the database version of the new billed with the current one.
	 */
	private int getBilledAmount(IEncounter encounter, IBilled newBilled) {
		List<IBilled> same = new ArrayList<>();
		String code = newBilled.getCode();
		Class<?> billableClass = newBilled.getBillable() != null ? newBilled.getBillable().getClass() : null;
		for (IBilled billed : encounter.getBilled()) {
			if (code != null && code.equals(billed.getCode()) && billableClass != null
					&& billableClass.equals(billed.getBillable() != null ? billed.getBillable().getClass() : null)) {
				same.add(billed);
			}
		}
		// replace value from database with current
		same.remove(newBilled);
		same.add(newBilled);
		int ret = 0;
		for (IBilled billed : same) {
			ret += billed.getAmount();
		}
		return ret;
	}

	@Override
	public String toString() {
		if (type == TYPE.AMOUNT && "sitzung".equalsIgnoreCase(limitReference)) {
			return "Max. " + limitMinutes + " Min. pro Sitzung (" + limitationAmount + " x " + MINUTES_PER_UNIT
					+ " Min.)";
		} else if (type == TYPE.AUTO_BILL) {
			return "Gilt als eine (1) Sitzung, " + autoBill + " wird automatisch verrechnet";
		}
		return super.toString();
	}

	public static void update() {
		limitations = null;
	}
}
