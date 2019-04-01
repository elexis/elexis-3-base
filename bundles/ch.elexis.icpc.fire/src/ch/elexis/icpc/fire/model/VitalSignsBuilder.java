package ch.elexis.icpc.fire.model;

import java.util.HashMap;
import java.util.Optional;

import ch.elexis.base.befunde.xchange.XChangeContributor;
import ch.elexis.data.Konsultation;

public class VitalSignsBuilder {
	
	private FireConfig config;
	
	private XChangeContributor xc;
	
	private Konsultation consultation;
	
	public VitalSignsBuilder(FireConfig config){
		this.config = config;
		this.xc = new XChangeContributor();
	}
	
	public Optional<TVital> build(){
		if (consultation != null) {
			xc.setPatient(consultation.getFall().getPatient());
			TVital ret = config.getFactory().createTVital();
			Optional<Float> bauchumfang = getBauchumfang();
			Optional<Integer> bpDiast = getBpDiast();
			Optional<Integer> bpSyst = getBpSyst();
			Optional<Float> gewicht = getGewicht();
			Optional<Float> groesse = getGroesse();
			Optional<Integer> puls = getPuls();
			
			if (bauchumfang.isPresent() || bpDiast.isPresent() || bpSyst.isPresent()
				|| gewicht.isPresent() || groesse.isPresent() || puls.isPresent()) {
				bauchumfang.ifPresent(value -> ret.setBauchumfang(value));
				bpDiast.ifPresent(value -> ret.setBpDiast(value));
				bpSyst.ifPresent(value -> ret.setBpSyst(value));
				gewicht.ifPresent(value -> ret.setGewicht(value));
				groesse.ifPresent(value -> ret.setGroesse(value));
				puls.ifPresent(value -> ret.setPuls(value));
				return Optional.of(ret);
			}
			
		}
		return Optional.empty();
	}
	
	private Optional<Integer> getBpDiast(){
		Optional<String> value = getVitalParm(config.getBdDiastTab());
		if (value.isPresent()) {
			try {
				return Optional.of(Integer.parseInt(value.get()));
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
	
	private Optional<Integer> getBpSyst(){
		Optional<String> value = getVitalParm(config.getBdSystTab());
		if (value.isPresent()) {
			try {
				return Optional.of(Integer.parseInt(value.get()));
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
	
	private Optional<Float> getBauchumfang(){
		Optional<String> value = getVitalParm(config.getWaistTab());
		if (value.isPresent()) {
			try {
				return Optional.of(Float.parseFloat(value.get()));
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
	
	private Optional<Float> getGewicht(){
		Optional<String> value = getVitalParm(config.getWeightTab());
		if (value.isPresent()) {
			try {
				return Optional.of(Float.parseFloat(value.get()));
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
	
	private Optional<Float> getGroesse(){
		Optional<String> value = getVitalParm(config.getHeightTab());
		if (value.isPresent()) {
			try {
				return Optional.of(Float.parseFloat(value.get()));
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
	
	private Optional<Integer> getPuls(){
		Optional<String> value = getVitalParm(config.getPulseTab());
		if (value.isPresent()) {
			try {
				return Optional.of(Integer.parseInt(value.get()));
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
	
	private Optional<String> getVitalParm(String parameter){
		String[] split = parameter.split("\\s*\\:\\s*");
		if (split.length > 1) {
			HashMap<String, String> vals = xc.getResult(split[0].trim(), consultation.getDatum());
			if (vals != null && !vals.isEmpty()) {
				return Optional.ofNullable(vals.get(split[1].trim()));
			}
		}
		return Optional.empty();
	}
	
	public VitalSignsBuilder consultation(Konsultation consultation){
		this.consultation = consultation;
		return this;
	}
	
}
