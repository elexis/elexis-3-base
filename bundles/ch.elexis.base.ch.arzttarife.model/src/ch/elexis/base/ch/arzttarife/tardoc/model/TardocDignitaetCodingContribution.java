package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import ch.elexis.core.findings.ICoding;
import ch.elexis.core.findings.codes.ICodingContribution;
import ch.elexis.core.findings.util.model.TransientCoding;

@Component
public class TardocDignitaetCodingContribution implements ICodingContribution {

	private List<ICoding> codes;

	@Activate
	public void activate() {
		codes = new ArrayList<ICoding>();

		try (CSVReader reader = new CSVReaderBuilder(
				new InputStreamReader(getClass().getResourceAsStream("/rsc/tardoc_dignitaet_uniq.csv")))
				.withCSVParser(new CSVParserBuilder().withSeparator(',').withQuoteChar('"').build())
				.withKeepCarriageReturn(false).build()) {
			for (String[] line : reader.readAll()) {
				codes.add(new TransientCoding("tardoc_dignitaet", StringUtils.leftPad(line[0], 4, '0'), line[1]));
			}
			codes.sort((l, r) -> {
				return l.getCode().compareTo(r.getCode());
			});
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Exception on activation", e);
		}
	}

	@Override
	public String getCodeSystem() {
		return "tardoc_dignitaet";
	}

	@Override
	public List<ICoding> getCodes() {
		return codes;
	}

	@Override
	public Optional<ICoding> getCode(String code) {
		return codes.stream().filter(c -> c.getCode().equals(code)).findAny();
	}
}
