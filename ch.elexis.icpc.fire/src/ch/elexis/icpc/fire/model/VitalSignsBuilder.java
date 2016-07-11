package ch.elexis.icpc.fire.model;

import java.util.HashMap;
import java.util.Optional;

import ch.elexis.base.befunde.xchange.XChangeContributor;
import ch.elexis.data.Konsultation;

public class VitalSignsBuilder {

	private enum BDIdentifier {
			DIAST, SYST
	}
	
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
			bauchumfang.ifPresent(value -> ret.setBauchumfang(value));
			Optional<Integer> bpDiast = getBpDiast();
			bpDiast.ifPresent(value -> ret.setBpDiast(value));
			Optional<Integer> bpSyst = getBpSyst();
			bpSyst.ifPresent(value -> ret.setBpSyst(value));
			Optional<Float> gewicht = getGewicht();
			gewicht.ifPresent(value -> ret.setGewicht(value));
			Optional<Float> groesse = getGroesse();
			groesse.ifPresent(value -> ret.setGroesse(value));
			Optional<Integer> puls = getPuls();
			puls.ifPresent(value -> ret.setPuls(value));
			return Optional.of(ret);
		}
		return Optional.empty();
	}
	
	private Optional<Integer> getBpDiast(){
		Optional<String> value = getBdVitalParm(BDIdentifier.DIAST);
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
		Optional<String> value = getBdVitalParm(BDIdentifier.SYST);
		if (value.isPresent()) {
			try {
				return Optional.of(Integer.parseInt(value.get()));
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
	
	private Optional<String> getBdVitalParm(BDIdentifier identifier){
		String[] split = config.getBdSystTab().split("\\s*\\:\\s*");
		String bdsyst = null, bddiast = null;
		if (split.length > 1) {
			HashMap<String, String> vals = xc.getResult(split[0].trim(), consultation.getDatum());
			
			if (config.getBdSystTab().equals(config.getBdDiastTab())) {
				String bd = vals.get(split[1].trim());
				if (bd != null) {
					String[] bds = bd.split("\\s*\\/\\s*");
					if (bds.length > 1) {
						bdsyst = bds[0].trim();
						bddiast = bds[1].trim();
					}
				}
			} else {
				bdsyst = vals.get(split[1]).trim();
				split = config.getBdDiastTab().split("\\s:\\s");
				if (split.length > 1) {
					vals = xc.getResult(split[0].trim(), consultation.getDatum());
					bddiast = vals.get(split[1]).trim();
				}
			}
			if (identifier == BDIdentifier.DIAST) {
				return Optional.ofNullable(bddiast);
			} else if (identifier == BDIdentifier.SYST) {
				return Optional.ofNullable(bdsyst);
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
