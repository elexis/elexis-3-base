package ch.elexis.base.ch.arzttarife.coding;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

@Component(property = { "system=forumdatenaustausch_sectioncode" })
public class SectionCodeCodingContribution implements ICodingContribution {

	private List<ICoding> codes;

	private Map<String, ICoding> dignitaetMap;

	private Map<String, Integer> dignitaetWeigthMap;

	@Activate
	public void activate() {
		codes = new ArrayList<ICoding>();
		dignitaetMap = new HashMap<String, ICoding>();
		dignitaetWeigthMap = new HashMap<String, Integer>();

		try (CSVReader reader = new CSVReaderBuilder(
				new InputStreamReader(
						getClass().getResourceAsStream("/rsc/section_code_tardoc_dignitaet_mapping_weighted.csv")))
				.withCSVParser(new CSVParserBuilder().withSeparator(',').withQuoteChar('"').build())
				.withKeepCarriageReturn(false).withSkipLines(1).build()) {
			for (String[] line : reader.readAll()) {
				if (StringUtils.isNotBlank(line[2])) {
					ICoding code = new TransientCoding("forumdatenaustausch_sectioncode", line[2], line[1]);
					codes.add(code);
					if (StringUtils.isNotBlank(line[0])) {
						dignitaetMap.put(line[0], code);
						if (StringUtils.isNotBlank(line[5])) {
							dignitaetWeigthMap.put(line[2], Integer.parseInt(line[5]));
						}
					}
				}
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
		return "forumdatenaustausch_sectioncode";
	}

	@Override
	public List<ICoding> getCodes() {
		return codes;
	}

	@Override
	public Optional<ICoding> getCode(String code) {
		return codes.stream().filter(c -> c.getCode().equals(code)).findAny();
	}

	public Optional<ICoding> getMappedBySpecialistCode(List<ICoding> specialistCodes) {
		List<ICoding> canidates = new ArrayList<ICoding>(
				specialistCodes.stream().map(c -> dignitaetMap.get(c.getCode())).filter(c -> c != null).toList());
		if (!canidates.isEmpty()) {
			canidates.sort((l, r) -> {
				Integer lw = dignitaetWeigthMap.getOrDefault(l.getCode(), 0);
				Integer rw = dignitaetWeigthMap.getOrDefault(r.getCode(), 0);
				return rw.compareTo(lw);
			});
			return Optional.of(canidates.get(0));
		}
		return Optional.empty();
	}
}
