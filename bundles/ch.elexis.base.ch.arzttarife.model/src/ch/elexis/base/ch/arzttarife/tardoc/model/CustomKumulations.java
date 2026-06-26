package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;
import ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt;
import ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationTyp;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBlob;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class CustomKumulations {

	public static final String TARDOC_CUSTOM_KUMULATIONS_ID = "Tardoc_Custom_Kumulations";

	private static Map<String, List<ITardocKumulation>> customKumulations;

	/**
	 * Lookup custom {@link ITardocKumulation}s with provided {@link IBilled} code
	 * matching master and one of other {@link IBilled} of {@link IEncounter}
	 * matching slave code.
	 * 
	 * @param newBilled
	 * @return
	 */
	public static List<ITardocKumulation> of(IBilled newBilled) {
		init();
		List<ITardocKumulation> matchingMasterCode = customKumulations.get(newBilled.getCode());
		if (matchingMasterCode != null) {
			List<String> possibleSlaves = newBilled.getEncounter().getBilled().stream().map(b -> b.getCode()).toList();
			List<ITardocKumulation> matchingMasterAndSlaveCode = matchingMasterCode.stream()
					.filter(ck -> possibleSlaves.contains(ck.getSlaveCode())).toList();
			return matchingMasterAndSlaveCode;
		} else {
			List<ITardocKumulation> matchingMasterAndSlaveCode = getChapterKumulations(newBilled);
			if (!matchingMasterAndSlaveCode.isEmpty()) {
				return matchingMasterAndSlaveCode;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Lookup custom {@link ITardocKumulation}s with provided {@link IBilled}
	 * matching chapters of the {@link IBilled} as master, and other {@link IBilled}
	 * of the linked {@link IEncounter} as slave.
	 * 
	 * @param newBilled
	 * @return
	 */
	private static List<ITardocKumulation> getChapterKumulations(IBilled newBilled) {
		List<String> groups = getChapters((ITardocLeistung) newBilled.getBillable());

		Optional<List<ITardocKumulation>> foundGroupKumulation = groups.stream().map(g -> customKumulations.get(g))
				.filter(gk -> gk != null).findFirst();
		if (foundGroupKumulation.isPresent()) {
			Set<String> possibleTardocSlaveGroups = new HashSet<>();
			for (IBilled possibleSlave : newBilled.getEncounter().getBilled()) {
				if (possibleSlave.getBillable() instanceof ITardocLeistung possibleTardocSlave) {
					possibleTardocSlaveGroups.addAll(getChapters(possibleTardocSlave));
				}
			}
			List<ITardocKumulation> matchingMasterAndSlaveCode = foundGroupKumulation.get().stream()
					.filter(gk -> possibleTardocSlaveGroups.contains(gk.getSlaveCode())).toList();
			return matchingMasterAndSlaveCode;
		}
		return Collections.emptyList();
	}

	private static List<String> getChapters(ITardocLeistung tardoc) {
		List<String> ret = new ArrayList<>();
		ITardocLeistung parent = tardoc.getParent();
		while (parent != null) {
			ret.add(parent.getCode());
			parent = parent.getParent();
		}
		return ret;
	}

	private static synchronized void init() {
		if (customKumulations == null) {
			customKumulations = new ConcurrentHashMap<String, List<ITardocKumulation>>();

			Optional<IBlob> blob = CoreModelServiceHolder.get().load(TARDOC_CUSTOM_KUMULATIONS_ID, IBlob.class);
			if (blob.isPresent()) {

				StringReader stringReader = new StringReader(blob.get().getStringContent());
				CSVParser csvParser = new CSVParserBuilder().withSeparator(',').withQuoteChar('"').build();
				try (CSVReader reader = new CSVReaderBuilder(stringReader).withCSVParser(csvParser).build()) {
					List<String[]> lines = reader.readAll();
					for (String[] strings : lines) {
						if (strings.length == 3) {
							List<ITardocKumulation> list = customKumulations.get(strings[0]);
							if (list == null) {
								list = new ArrayList<>();
							}
							list.add(new CustomKumulation(strings[0], strings[1], strings[2]));
							customKumulations.put(strings[0], list);
						}
					}
				} catch (IOException | CsvException e) {
					LoggerFactory.getLogger(CustomKumulations.class).error("Exception reading custom Kumulations", e);
				}
			}
		}
	}

	public static synchronized void update() {
		customKumulations = null;
	}

	private static class CustomKumulation implements ITardocKumulation {

		private String masterCode;
		private String slaveCode;
		private String typ;

		public CustomKumulation(String masterCode, String slaveCode, String typ) {
			this.masterCode = masterCode;
			this.slaveCode = slaveCode;
			this.typ = typ;
		}

		@Override
		public String getSlaveCode() {
			return slaveCode;
		}

		@Override
		public TardocKumulationArt getSlaveArt() {
			return TardocKumulationArt.SERVICE;
		}

		@Override
		public String getValidSide() {
			return null;
		}

		@Override
		public LocalDate getValidFrom() {
			return null;
		}

		@Override
		public LocalDate getValidTo() {
			return null;
		}

		@Override
		public String getLaw() {
			return null;
		}

		@Override
		public String getMasterCode() {
			return masterCode;
		}

		@Override
		public TardocKumulationArt getMasterArt() {
			return TardocKumulationArt.SERVICE;
		}

		@Override
		public TardocKumulationTyp getTyp() {
			return TardocKumulationTyp.ofTyp(typ);
		}

		@Override
		public boolean isValidKumulation(LocalDate reference) {
			return true;
		}
	}
}
