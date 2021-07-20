package ch.elexis.covid.cert.valueset;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import ch.elexis.core.findings.ICoding;

public class JsonValueSet {
	
	public static Optional<JsonValueSet> load(String name){
		try {
			name = cummulatedMapName(name);
			
			InputStream jsonInput =
				JsonValueSet.class.getResourceAsStream("/rsc/" + name + ".json");
			if (jsonInput != null) {
				Gson gson = new GsonBuilder().create();
				if (isValueSetList(name)) {
					ValueSetConceptArray valueSet = gson
						.fromJson(IOUtils.toString(jsonInput, "UTF-8"), ValueSetConceptArray.class);
					return Optional.of(new JsonValueSet(valueSet));
				} else if (isCummulated(name)) {
					ValueSetCummulatedArray valueSet = gson.fromJson(
						IOUtils.toString(jsonInput, "UTF-8"), ValueSetCummulatedArray.class);
					return Optional.of(new JsonValueSet(valueSet, "covid-19-lab-test-manufacturer",
						"https://covid-19-diagnostics.jrc.ec.europa.eu/devices",
						"type_code=LP217198-3"));
				} else {
					ValueSetConceptMap valueSet = gson
						.fromJson(IOUtils.toString(jsonInput, "UTF-8"), ValueSetConceptMap.class);
					return Optional.of(new JsonValueSet(valueSet));
				}
			}
		} catch (JsonSyntaxException | IOException e) {
			LoggerFactory.getLogger(JsonValueSet.class)
				.error("Error parsing valueset [" + name + "]", e);
		}
		return Optional.empty();
	}
	
	private static String cummulatedMapName(String name){
		if("test-manufacturer".equals(name)) {
			return "cummulated_tests_1.0.0";
		}
		return name;
	}
	
	private static boolean isValueSetList(String name){
		return "country-alpha-2-de".equals(name);
	}
	
	private static boolean isCummulated(String name){
		return name.startsWith("cummulated");
	}
	
	private ValueSetConceptMap valueSetMap;
	
	private ValueSetConceptArray valueSetArray;
	
	private ValueSetCummulatedArray cummulatedValueSet;
	private String cummulatedValueSetId;
	private String cummulatedFilter;
	private String cummulatedSystem;
	
	public JsonValueSet(ValueSetConceptMap valueSet){
		this.valueSetMap = valueSet;
	}
	
	public JsonValueSet(ValueSetConceptArray valueSet){
		this.valueSetArray = valueSet;
	}
	
	public JsonValueSet(ValueSetCummulatedArray valueSet, String valueSetId, String system,
		String filterString){
		this.cummulatedValueSet = valueSet;
		this.cummulatedValueSetId = valueSetId;
		this.cummulatedSystem = system;
		this.cummulatedFilter = filterString;
	}
	
	public String getId(){
		if (valueSetArray != null) {
			return valueSetArray.valueSetId;
		}
		if (cummulatedValueSet != null) {
			return cummulatedValueSetId;
		}
		return valueSetMap.valueSetId;
	}
	
	public List<ICoding> getCoding(){
		if (valueSetArray != null) {
			if (valueSetArray.valueSetValues != null) {
				return Arrays.asList(valueSetArray.valueSetValues).stream()
					.filter(map -> isActive(map))
					.map(map -> (ICoding) new Coding(map)).collect(Collectors.toList());
			}
		} else if (valueSetMap != null) {
			if (valueSetMap.valueSetValues != null) {
				return valueSetMap.valueSetValues.entrySet().stream()
					.filter(entry -> isActive(entry.getValue()))
					.map(entry -> (ICoding) new Coding(entry)).collect(Collectors.toList());
			}
		} else if (cummulatedValueSet != null) {
			if (cummulatedValueSet.entries != null) {
				return Arrays.asList(cummulatedValueSet.entries).stream()
					.filter(entry -> isActive(entry) && isCummulatedFilter(entry, cummulatedFilter))
					.map(entry -> (ICoding) new Coding(entry)).collect(Collectors.toList());
			}
		}
		return Collections.emptyList();
	}
	
	private boolean isCummulatedFilter(Map<String, String> map, String cummulatedFilter){
		if (cummulatedFilter != null && cummulatedFilter.split("=").length == 2) {
			String[] filterParts = cummulatedFilter.split("=");
			if (StringUtils.isNotBlank(map.get(filterParts[0]))) {
				return filterParts[1].equals(map.get(filterParts[0]));
			}
		}
		return true;
	}
	
	private boolean isActive(Map<String, String> map){
		boolean activeValue = true;
		if (StringUtils.isNotBlank(map.get("active"))) {
			activeValue = Boolean.parseBoolean(map.get("active"));
		}
		boolean chAcceptedValue = true;
		if (StringUtils.isNotBlank(map.get("ch_accepted"))) {
			chAcceptedValue = Boolean.parseBoolean(map.get("ch_accepted"));
		}
		return activeValue && chAcceptedValue;
	}
	
	@Override
	public String toString(){
		return "JsonValueSet [valueSet=" + valueSetMap + ", valueList=" + valueSetArray + "]";
	}
	
	public static String getSystemLanguage(){
		String language = Locale.getDefault().getLanguage();
		if (language != null) {
			switch (language) {
			case "de":
				return "de-CH";
			case "fr":
				return "fr-CH";
			case "it":
				return "it-CH";
			case "en":
				return "en-US";
			}
		}
		return "de-CH";
	}
}
