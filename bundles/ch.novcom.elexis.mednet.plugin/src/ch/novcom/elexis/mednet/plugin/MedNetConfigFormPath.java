package ch.novcom.elexis.mednet.plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MedNetConfigFormPath implements Comparable<MedNetConfigFormPath> {

	//"accountID";"accountTitle";"accountLastname";"accountFirstname";"path"
	private final static Pattern csvLinePattern = Pattern.compile("^\"(?<accountID>[^\"]*)\";\"(?<accountTitle>[^\"]*)\";\"(?<accountLastname>[^\"]*)\";\"(?<accountFirstname>[^\"]*)\";\"(?<path>[^\"]*)\"$");//$NON-NLS-1$
	
	private String accountID;
	private String accountTitle;
	private String accountLastname;
	private String accountFirstname;
	private Path path;

	public MedNetConfigFormPath(String fileLine) {

		Matcher matcher = csvLinePattern.matcher(fileLine);
		if(matcher.matches()){
			this.accountID = matcher.group("accountID");//$NON-NLS-1$
			this.accountTitle = matcher.group("accountTitle");//$NON-NLS-1$
			this.accountLastname = matcher.group("accountLastname");//$NON-NLS-1$
			this.accountFirstname = matcher.group("accountFirstname");//$NON-NLS-1$
			this.path = Paths.get(matcher.group("path"));//$NON-NLS-1$
		}
	}
	
	public String getKey() {
		return this.accountID;
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
	public Path getPath() {
		return path;
	}

	public int compareTo(MedNetConfigFormPath other){
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
		return this.getAccountID().compareTo(other.getAccountID());
	}
	
}
