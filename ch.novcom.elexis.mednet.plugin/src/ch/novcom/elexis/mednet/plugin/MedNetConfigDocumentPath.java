package ch.novcom.elexis.mednet.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MedNetConfigDocumentPath implements Comparable<MedNetConfigDocumentPath> {

	
	//"accountID";"accountTitle";"accountLastname";"accountFirstname";"institutionID";"institutionName";"type";"path"
	private final static Pattern csvLinePattern = Pattern.compile("^\"(?<accountID>[^\"]*)\";\"(?<accountTitle>[^\"]*)\";\"(?<accountLastname>[^\"]*)\";\"(?<accountFirstname>[^\"]*)\";\"(?<institutionID>[^\"]*)\";\"(?<institutionName>[^\"]*)\";\"(?<type>[^\"]*)\";\"(?<path>[^\"]*)\"$");//$NON-NLS-1$
	
	private String accountID;
	private String accountTitle;
	private String accountLastname;
	private String accountFirstname;
	private String institutionID;
	private String institutionName;
	private String path;
	
	public MedNetConfigDocumentPath(String fileLine) {
		
		Matcher matcher = csvLinePattern.matcher(fileLine);
		if(matcher.matches()){
			this.accountID = matcher.group("accountID");//$NON-NLS-1$
			this.accountTitle = matcher.group("accountTitle");//$NON-NLS-1$
			this.accountLastname = matcher.group("accountLastname");//$NON-NLS-1$
			this.accountFirstname = matcher.group("accountFirstname");//$NON-NLS-1$
			this.institutionID = matcher.group("institutionID");//$NON-NLS-1$
			this.institutionName = matcher.group("institutionName");//$NON-NLS-1$
			this.path = matcher.group("path");//$NON-NLS-1$
		}
		
	}
	

	public String getKey() {
		return this.accountID+"_"+this.institutionID;
	}
	
	public String getAccountID() {
		return accountID;
	}


	public String getAccountTitle() {
		return accountTitle;
	}


	public String getAccountLastname() {
		return accountLastname;
	}


	public String getAccountFirstname() {
		return accountFirstname;
	}

	public String getInstitutionID() {
		return institutionID;
	}

	public String getInstitutionName() {
		return institutionName;
	}


	public String getPath() {
		return path;
	}
	

	public int compareTo(MedNetConfigDocumentPath other){
		// check for null; put null values at the end
		if (other == null) {
			return -1;
		}
		if(this.getAccountID() != null && other.getAccountID() == null){
			return -1;
		}
		if(this.getAccountID() == null && other.getAccountID() != null){
			return 1;
		}
		int comparator = this.getAccountID().compareTo(other.getAccountID());
		if(comparator != 0){
			return comparator;
		}
		
		if(this.getInstitutionID() != null && other.getInstitutionID() == null){
			return -1;
		}
		if(this.getInstitutionID() == null && other.getInstitutionID() != null){
			return 1;
		}
		return this.getInstitutionID().compareTo(other.getInstitutionID());
	}
	
}
