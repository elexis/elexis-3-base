package at.medevit.elexis.impfplan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.medevit.elexis.impfplan.model.vaccplans.AbstractVaccinationPlan;
import at.medevit.elexis.impfplan.model.vaccplans.ImpfplanSchweiz2013;

public class VaccinationPlanModel {
	
	private static List<AbstractVaccinationPlan> vaccinationPlans = null;
	
	public static List<AbstractVaccinationPlan> getVaccinationPlans(){
		if (vaccinationPlans == null)
			init();
		return vaccinationPlans;
	}
	
	private static void init(){
		vaccinationPlans = new ArrayList<>();
		vaccinationPlans.add(new ImpfplanSchweiz2013());
		// add in the future
		vaccinationPlans = Collections.unmodifiableList(vaccinationPlans);
	}
	
}
