package ch.elexis.icpc.fire.model;

import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.IPersistentObject;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Xid;
import ch.elexis.icpc.fire.ui.Preferences;

public class FireConfig {
	
	public static String XID_FIRE_PATID = "http://elexis.ch/icpc/fire/patid";
	
	private String bdSystTab, bdDiastTab, pulseTab, heightTab, weightTab, waistTab;
	
	private ObjectFactory factory;
	
	protected Logger log = LoggerFactory.getLogger(FireConfig.class);
	
	
	public FireConfig(){
		factory = new ObjectFactory();
		
		Xid.localRegisterXIDDomainIfNotExists(XID_FIRE_PATID, "IcpcFirePatId",
			Xid.ASSIGNMENT_LOCAL);
	}
	
	public BigInteger getPatId(Patient patient){

		String patientNr = patient.getPatCode();
		try {
			long parseLong = Long.parseLong(patientNr);
			return BigInteger.valueOf(parseLong);
		} catch (NumberFormatException nfe) {
			log.warn("Error parsing patientNr [{}], falling back to XID_FIRE_PATID", patientNr);
		}
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// ignore
		}
		String existingPatId = patient.getXid(FireConfig.XID_FIRE_PATID);
		if (existingPatId == null || existingPatId.isEmpty()) {
			// create a new pat id
			long currentMs = System.currentTimeMillis();
			BigInteger patId = new BigInteger(Long.toString(currentMs));
			// make sure no collision happens
			IPersistentObject existingPat =
				Xid.findObject(FireConfig.XID_FIRE_PATID, patId.toString());
			while (existingPat != null) {
				patId.add(BigInteger.ONE);
				existingPat = Xid.findObject(FireConfig.XID_FIRE_PATID, patId.toString());
			}
			// set the new id
			patient.addXid(FireConfig.XID_FIRE_PATID, patId.toString(), true);
			return patId;
		} else {
			return new BigInteger(existingPatId);
		}
	}
	
	public BigInteger getDocId(Mandant mandant) throws IllegalStateException{
		String ean = mandant.getXid(DOMAIN_EAN);
		if (ean != null && !ean.isEmpty()) {
			return new BigInteger(ean);
		}
		throw new IllegalStateException("Mandant " + mandant.getLabel() + " has no EAN specified.");
	}
	
	private boolean readVitalSignsConfig(){
		bdSystTab = getOrFail(Preferences.CFG_BD_SYST);
		if (bdSystTab != null) {
			bdDiastTab = getOrFail(Preferences.CFG_BD_DIAST);
			if (bdDiastTab != null) {
				pulseTab = getOrFail(Preferences.CFG_PULS);
				if (pulseTab != null) {
					heightTab = getOrFail(Preferences.CFG_HEIGHT);
					if (heightTab != null) {
						weightTab = getOrFail(Preferences.CFG_WEIGHT);
						if (weightTab != null) {
							waistTab = getOrFail(Preferences.CFG_BU);
							if (waistTab != null) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private String getOrFail(String prefs){
		String ret = CoreHub.globalCfg.get(prefs, null);
		return ret;
	}
	
	public ObjectFactory getFactory(){
		return factory;
	}
	
	public boolean isValid(){
		return readVitalSignsConfig();
	}
	
	public String getWaistTab(){
		return waistTab;
	}
	
	public String getBdSystTab(){
		return bdSystTab;
	}
	
	public String getBdDiastTab(){
		return bdDiastTab;
	}
	
	public String getWeightTab(){
		return weightTab;
	}
	
	public String getHeightTab(){
		return heightTab;
	}
	
	public String getPulseTab(){
		return pulseTab;
	}
}
