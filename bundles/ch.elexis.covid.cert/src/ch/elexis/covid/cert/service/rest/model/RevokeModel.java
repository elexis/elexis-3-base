package ch.elexis.covid.cert.service.rest.model;

import ch.elexis.covid.cert.service.CertificateInfo;

public class RevokeModel {
	private String uvci;
	
	private String otp;
	
	public String getUvci(){
		return uvci;
	}
	
	public void setUvci(String uvci){
		this.uvci = uvci;
	}
	
	public String getOtp(){
		return otp;
	}
	
	public void setOtp(String otp){
		this.otp = otp;
	}
	
	public RevokeModel initDefault(CertificateInfo info, String otp){
		setUvci(info.getUvci());
		setOtp(otp);
		return this;
	}
}
