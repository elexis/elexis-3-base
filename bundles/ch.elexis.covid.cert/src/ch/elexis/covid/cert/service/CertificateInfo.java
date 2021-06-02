package ch.elexis.covid.cert.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class CertificateInfo {
	
	private static String EXTINFO_KEY = "ch.elexis.covid.certinfo";
	
	public enum Type {
			VACCINATION("Impfung"), RECOVERY("Genesen"), TEST("Getestet");
		
		private String label;
		
		Type(String label){
			this.label = label;
		}
		
		public String getLabel(){
			return label;
		}
	}
	
	private Type type;
	
	private LocalDateTime timestamp;
	
	private String documentId;
	
	private String uvci;
	
	public Type getType(){
		return type;
	}
	
	public LocalDateTime getTimestamp(){
		return timestamp;
	}
	
	public String getDocumentId(){
		return documentId;
	}
	
	public String getUvci(){
		return uvci;
	}
	
	private static CertificateInfo of(String infoStringPart) {
		String[] parts = infoStringPart.split("\\|");
		if (parts.length == 4) {
			CertificateInfo ret = new CertificateInfo();
			ret.timestamp = LocalDateTime.parse(parts[0]);
			ret.type = Type.valueOf(parts[1]);
			ret.documentId = parts[2];
			ret.uvci = parts[3];
			return ret;
		}
		LoggerFactory.getLogger(CertificateInfo.class).error("No valid input [" + infoStringPart + "]");
		return null;
	}
	
	/**
	 * Add a {@link CertificateInfo} with the provided properties to the {@link IPatient}s
	 * certificates.
	 * 
	 * @param type
	 * @param timestamp
	 * @param uvci
	 * @param patient
	 * @return
	 */
	public static CertificateInfo add(Type type, LocalDateTime timestamp, String documentId,
		String uvci, IPatient patient){
		List<CertificateInfo> certificates = of(patient);
		if (certificates == null || certificates.isEmpty()) {
			certificates = new ArrayList<CertificateInfo>();
		}
		CertificateInfo ret = new CertificateInfo();
		ret.timestamp = timestamp;
		ret.type = type;
		ret.documentId = documentId;
		ret.uvci = uvci;
		certificates.add(ret);

		StringJoiner sj = new StringJoiner("||");
		for (CertificateInfo certificateInfo : certificates) {
			sj.add(certificateInfo.toString());
		}
		patient.setExtInfo(EXTINFO_KEY, sj.toString());
		CoreModelServiceHolder.get().save(patient);
		return ret;
	}
	
	/**
	 * Remove the {@link CertificateInfo} with matching uvci from the patient;
	 * 
	 * @param info
	 * @param patient
	 */
	public static void remove(CertificateInfo info, IPatient patient){
		List<CertificateInfo> certificates = of(patient);
		certificates = certificates.stream().filter(c -> !c.getUvci().equals(info.getUvci()))
			.collect(Collectors.toList());
		
		StringJoiner sj = new StringJoiner("||");
		for (CertificateInfo certificateInfo : certificates) {
			sj.add(certificateInfo.toString());
		}
		patient.setExtInfo(EXTINFO_KEY, sj.toString());
		CoreModelServiceHolder.get().save(patient);
	}
	
	/**
	 * Get a {@link List} of all {@link CertificateInfo}s of the {@link IPatient}. The content of
	 * the {@link List} is sorted by timestamp desc.
	 * 
	 * @param patient
	 * @return
	 */
	public static List<CertificateInfo> of(IPatient patient){
		Object infoString = patient.getExtInfo(EXTINFO_KEY);
		if (infoString instanceof String) {
			List<CertificateInfo> ret = new ArrayList<CertificateInfo>();
			String[] infoStringParts = ((String) infoString).split("\\|\\|");
			for (String infoStringPart : infoStringParts) {
				CertificateInfo info = CertificateInfo.of(infoStringPart);
				if (info != null) {
					ret.add(CertificateInfo.of(infoStringPart));
				}
			}
			ret.sort((l, r) -> l.getTimestamp().compareTo(r.getTimestamp()));
			return ret;
		}
		return Collections.emptyList();
	}
	
	@Override
	public String toString(){
		return timestamp.toString() + "|" + type.name() + "|" + documentId + "|" + uvci;
	}
}
