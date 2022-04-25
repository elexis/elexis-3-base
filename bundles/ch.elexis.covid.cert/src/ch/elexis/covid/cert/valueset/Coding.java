package ch.elexis.covid.cert.valueset;

import java.util.Map;
import java.util.Map.Entry;

import ch.elexis.core.findings.ICoding;

public class Coding implements ICoding {

	private String code;
	private String display;
	private String system;

	public Coding(Map<String, String> map) {
		if (map.containsKey("manufacturer")) {
			this.display = map.get("manufacturer") + " - ";
		}
		if (map.containsKey("short")) {
			this.code = map.get("short");
		}
		if (map.containsKey("display")) {
			if (this.display != null) {
				this.display += map.get("display");
			} else {
				this.display = map.get("display");
			}
		}
		if (map.containsKey("system")) {
			this.system = map.get("system");
		}
		if (map.containsKey("manufacturer_code_eu")) {
			this.code = map.get("manufacturer_code_eu");
		}
		if (map.containsKey("name")) {
			if (this.display != null) {
				this.display += map.get("name");
			} else {
				this.display = map.get("name");
			}
		}
	}

	public Coding(Entry<String, Map<String, String>> entry) {
		this.code = entry.getKey();
		if (entry.getValue().containsKey("display")) {
			this.display = entry.getValue().get("display");
		}
		if (entry.getValue().containsKey("system")) {
			this.system = entry.getValue().get("system");
		}

	}

	@Override
	public String getSystem() {
		return system;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getDisplay() {
		return display;
	}

}
