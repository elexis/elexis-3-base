package ch.elexis.fop.service.test;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "contact")
public class TestJaxbContact {
	private String firstname;
	private String lastname;
	
	private List<TestJaxbAddress> addresses = new ArrayList<>();
	
	public String getFirstname(){
		return firstname;
	}
	
	public void setFirstname(String firstname){
		this.firstname = firstname;
	}
	
	public String getLastname(){
		return lastname;
	}
	
	public void setLastname(String lastname){
		this.lastname = lastname;
	}
	
	public List<TestJaxbAddress> getAddresses(){
		return addresses;
	}
	
	public void setAddresses(List<TestJaxbAddress> addresses){
		this.addresses = addresses;
	}
}
