package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationTyp;
import ch.elexis.core.model.IBlob;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class CustomExclusions {

	public static final String TARMED_CUSTOM_EXCLUSIONS_ID = "Tarmed_Custom_Exclusions";

	private static Map<String, List<ITarmedKumulation>> customExclusions;

	public static List<ITarmedKumulation> of(String mastercode) {
		init();
		return customExclusions.containsKey(mastercode) ? customExclusions.get(mastercode) : Collections.emptyList();
	}

	private static synchronized void init() {
		if (customExclusions == null) {
			customExclusions = new ConcurrentHashMap<String, List<ITarmedKumulation>>();

			Optional<IBlob> blob = CoreModelServiceHolder.get().load(TARMED_CUSTOM_EXCLUSIONS_ID, IBlob.class);
			if (blob.isPresent()) {

				StringReader stringReader = new StringReader(blob.get().getStringContent());
				CSVParser csvParser = new CSVParserBuilder().withSeparator(',').withQuoteChar('"').build();
				try (CSVReader reader = new CSVReaderBuilder(stringReader).withCSVParser(csvParser).build()) {
					List<String[]> lines = reader.readAll();
					for (String[] strings : lines) {
						List<ITarmedKumulation> list = customExclusions.get(strings[0]);
						if (list == null) {
							list = new ArrayList<>();
						}
						list.add(new CustomExclusion(strings[0], strings[1], strings[2]));
						customExclusions.put(strings[0], list);
					}
				} catch (IOException | CsvException e) {
					LoggerFactory.getLogger(CustomExclusions.class).error("Exception reading custom exclusions", e);
				}
			}
		}
	}

	public static synchronized void update() {
		customExclusions = null;
	}

	private static class CustomExclusion implements ITarmedKumulation {

		private String masterCode;
		private String slaveCode;
		private String typ;

		public CustomExclusion(String masterCode, String slaveCode, String typ) {
			this.masterCode = masterCode;
			this.slaveCode = slaveCode;
			this.typ = typ;
		}

		@Override
		public String getSlaveCode() {
			return slaveCode;
		}

		@Override
		public TarmedKumulationArt getSlaveArt() {
			return TarmedKumulationArt.SERVICE;
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
		public TarmedKumulationArt getMasterArt() {
			return TarmedKumulationArt.SERVICE;
		}

		@Override
		public TarmedKumulationTyp getTyp() {
			return TarmedKumulationTyp.ofTyp(typ);
		}

		@Override
		public boolean isValidKumulation(LocalDate reference) {
			return true;
		}
	}
}
