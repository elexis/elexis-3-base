package ch.novcom.elexis.mednet.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MedNetConfigFormItem implements Comparable<MedNetConfigFormItem> {

	//"accountID";"accountTitle";"accountLastname";"accountFirstname";"institutionID";"institutionName";"formID";"formName"
	private final static Pattern csvLinePattern = Pattern.compile("^\"(?<accountID>[^\"]*)\";\"(?<accountTitle>[^\"]*)\";\"(?<accountLastname>[^\"]*)\";\"(?<accountFirstname>[^\"]*)\";\"(?<institutionID>[^\"]*)\";\"(?<institutionName>[^\"]*)\";\"(?<formID>[^\"]*)\";\"(?<formName>[^\"]*)\"$");//$NON-NLS-1$
	
	private String accountID="";
	private String accountTitle="";
	private String accountLastname="";
	private String accountFirstname="";
	private String institutionID="";
	private String institutionName="";
	private String formID="";
	private String formName="";
	

	public MedNetConfigFormItem(String line) {
		
		Matcher matcher = csvLinePattern.matcher(line);
		if(matcher.matches()){
			this.accountID = matcher.group("accountID");//$NON-NLS-1$
			this.accountTitle = matcher.group("accountTitle");//$NON-NLS-1$
			this.accountLastname = matcher.group("accountLastname");//$NON-NLS-1$
			this.accountFirstname = matcher.group("accountFirstname");//$NON-NLS-1$
			this.institutionID = matcher.group("institutionID");//$NON-NLS-1$
			this.institutionName = matcher.group("institutionName");//$NON-NLS-1$
			this.formID = matcher.group("formID");//$NON-NLS-1$
			this.formName = matcher.group("formName");//$NON-NLS-1$
		}
		
	}
	

	public String getKey() {
		return this.accountID+"_"+this.institutionID+"_"+this.formID;
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
	public String getFormID() {
		return formID;
	}
	public String getFormName() {
		return formName;
	}
	

	public int compareTo(MedNetConfigFormItem other){
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
		comparator = this.getInstitutionID().compareTo(other.getInstitutionID());
		if(comparator != 0){
			return comparator;
		}
		

		if(this.getFormID() != null && other.getFormID() == null){
			return -1;
		}
		if(this.getFormID() == null && other.getFormID() != null){
			return 1;
		}
		return this.getFormID().compareTo(other.getFormID());
		
	}
	
	
}
