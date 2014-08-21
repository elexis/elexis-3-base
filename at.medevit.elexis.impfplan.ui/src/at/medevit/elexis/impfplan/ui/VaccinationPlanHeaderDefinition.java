package at.medevit.elexis.impfplan.ui;

import java.util.List;

public class VaccinationPlanHeaderDefinition {
	public final String id;
	final String name;
	final List<String> base;
	final List<String> extended;

	public VaccinationPlanHeaderDefinition(String id, String name, List<String> base, List<String> extended){
		this.id = id;
		this.name = name;
		this.base = base;
		this.extended = extended;
	}
}
