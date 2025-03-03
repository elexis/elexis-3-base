package ch.elexis.fop.service.test;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "contacts")
public class TestJaxbContacts {
	private List<TestJaxbContact> contact = new ArrayList<>();
	
	public List<TestJaxbContact> getContact(){
		return contact;
	}
	
	public void setContact(List<TestJaxbContact> contacts){
		this.contact = contacts;
	}
}
