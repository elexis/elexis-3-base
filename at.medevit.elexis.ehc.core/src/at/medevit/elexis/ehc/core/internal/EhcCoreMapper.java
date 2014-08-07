package at.medevit.elexis.ehc.core.internal;

import java.util.Date;

import ch.elexis.data.Person;
import ch.rgw.tools.TimeTool;
import ehealthconnector.cda.documents.ch.ConvenienceUtilsEnums.AdministrativeGenderCode;
import ehealthconnector.cda.documents.ch.Name;
import ehealthconnector.cda.documents.ch.Patient;

public class EhcCoreMapper {
	
	private static TimeTool timeTool = new TimeTool();
	
	public static Patient getEhcPatient(ch.elexis.data.Patient elexisPatient){
		Patient ret =
			new Patient(getEhcPersonName(elexisPatient), getEhcGenderCode(elexisPatient),
				getDate(elexisPatient.getGeburtsdatum()));
		
		return ret;
	}
	
	public static Name getEhcPersonName(Person elexisPerson){
		Name ret =
			new Name(elexisPerson.getName(), elexisPerson.getVorname(),
				elexisPerson.get(Person.TITLE));
		
		return ret;
	}
	
	public static AdministrativeGenderCode getEhcGenderCode(Person elexisPerson){
		if (elexisPerson.getGeschlecht().equals(Person.FEMALE)) {
			return AdministrativeGenderCode.Female;
		} else if (elexisPerson.getGeschlecht().equals(Person.MALE)) {
			return AdministrativeGenderCode.Male;
		}
		return AdministrativeGenderCode.Undifferentiated;
	}
	
	public static Date getDate(String elexisDate){
		timeTool.set(elexisDate);
		return timeTool.getTime();
	}
}
