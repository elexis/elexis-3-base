/*******************************************************************************
 * Copyright (c) 2014, Pharmed Solutions GmbH
 * All rights reserved.
 *******************************************************************************/

package ch.pharmed.phmprescriber;


import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.elexis.core.data.activator.CoreHub;
import ch.pharmedsolutions.www.apothekenservice.Apotheken;
import ch.pharmedsolutions.www.apothekenservice.ApothekenPortType;
import ch.pharmedsolutions.www.apothekenservice.ApothekenRequest;
import ch.pharmedsolutions.www.apothekenservice.ApothekenService;
import ch.pharmedsolutions.www.zsrservice.GetInformationParameters;
import ch.pharmedsolutions.www.zsrservice.Information;
import ch.pharmedsolutions.www.zsrservice.InformationPortType;
import ch.pharmedsolutions.www.zsrservice.InformationService;
import ch.rgw.tools.TimeTool;


public class Physician {
	
	
	//Define properties
	private String title = "";
	private String lastname = "";
	private String firstname = "";
	private String specialty1 = "";
	private String specialty2 = "";
	private String street = "";
	private String postbox = "";
	private String zip = "";
	private String city = "";
	
	private String phone = "";

	private String fax = "";
	
	private String zsrid = "";
	private String glnid = "";
	
	HashMap<String, String> shops;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getSpecialty1() {
		return specialty1;
	}
	public void setSpecialty1(String specialty1) {
		this.specialty1 = specialty1;
	}
	public String getSpecialty2() {
		return specialty2;
	}
	public void setSpecialty2(String specialty2) {
		this.specialty2 = specialty2;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getPostbox() {
		return postbox;
	}
	public void setPostbox(String postbox) {
		this.postbox = postbox;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
		
	}
	
	public String getZsrid() {
		return zsrid;
	}
	public void setZsrid(String zsrid) {
		this.zsrid = zsrid;
	}
	public String getGlnid() {
		return glnid;
	}
	public void setGlnid(String glnid) {
		this.glnid = glnid;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	
	
	public HashMap<String, String> getShops() {
		return shops;
	}


	public void setShops(HashMap<String, String> shops) {
		this.shops = shops;
	}
	
	//Constructor
	public Physician() {
		
		// TODO Auto-generated constructor stub
		this.getAttributesFromConfig();
						
	}

	public void getAttributesFromConfig() {
						
		String strCFG = CoreHub.globalCfg.get(Constants.CFG_PHM_PHY, "");
				
		//Split string and include also blanks
		String[] attributes = strCFG.split("\\;",-1);
		
		//Prevents error, if there are no settings saved
		if (attributes.length == 13) {
			
			this.zsrid  = attributes[0];
			this.glnid = attributes[1];
			
			this.title  = attributes[2];
			this.firstname = attributes[3];
			this.lastname  = attributes[4];
			
			this.street = attributes[5];
			this.postbox  = attributes[6];
			
			this.zip = attributes[7];
			this.city   = attributes[8];
			
			this.phone = attributes[9];
			this.fax  = attributes[10];
			
			this.specialty1 = attributes[11];
			this.specialty2  = attributes[12];
		
		}
				
	}
	
	//Get all the information required via SOAP-WebService
	public void getAttributesFromWeb(String zsr) {
		
		this.setZsrid(zsr);
		
	
		GetInformationParameters GetInfoParam = new GetInformationParameters();
	        
		GetInfoParam.setZsrId(this.getZsrid());
		        
		
		Information Info = new Information();
		
		 try {
	            
			 //Get the information
			 Info = getInfo(GetInfoParam);
             
			 if (!(Info == null)) {
				
				this.setGlnid(Info.getGlnId().toString());
				
				this.setTitle(Info.getTitle().toString());
				
				this.setFirstname(Info.getFirstName().toString());
				this.setLastname(Info.getLastName().toString());
				this.setStreet(Info.getStreet().toString());
				this.setPostbox(Info.getPobox().toString());
				this.setZip((Info.getZip().toString()));
				this.setCity(Info.getCity().toString());
				
				this.setPhone(Info.getPhone().toString());
				this.setFax(Info.getFax().toString());
						
			
			};
			 
			 
         } catch (Exception ex) {
             
             System.out.println( "Exception: " + ex);
       
        }
		 

	}
	
	
	  private static Information getInfo(GetInformationParameters parameters) {
	        
	        InformationService service = new InformationService();
	        InformationPortType port = service.getInformationPort();
	        return port.getInformation(parameters);
	        
	    }
	  
	public Boolean hasShops() {
			
			//(1) Check, if we need to update the shops from the WebService
			String strCFG = CoreHub.globalCfg.get(Constants.CFG_PHM_LASTREQUEST, "");
				
				
			TimeTool now = new TimeTool(new Date());
			
			if (strCFG.length() == 0) {
				
								
				this.shops = getShopsFromWS();	
				
			} else  {
				
				
				TimeTool before = new TimeTool(strCFG);
				
				int days = before.daysTo(now);
				
				if (days > 14) {
					
					this.shops = getShopsFromWS();		
					
				} else {
					
					this.shops = getShopsFromConfig();

				}

			}

			if (shops.size() == 0) {
				
				return false;
			}
			
			return true;
			
	
	}
	
	private HashMap<String, String> getShopsFromWS() {
		
		
		HashMap<String, String> hmShops = new HashMap<String, String>();
		
		
		ApothekenRequest request = new ApothekenRequest();
        
		request.setPassword(Constants.CFG_PHM_PASSWORD);
		request.setSoftware(BigInteger.valueOf(Constants.CFG_PHM_SOFTWARENR));
		
		request.setZsrId(this.zsrid);
		        
		
		Apotheken apotheken = new Apotheken();
		
		 try {
	            
			 //Get the information
			 ApothekenService service = new ApothekenService();
		     ApothekenPortType port = service.getApothekenPort();
				 
			 apotheken = port.getApotheken(request);
             
			 if (!(apotheken == null) && apotheken.getApotheken().getApotheke().size() > 0) {
				
			
				 for (int i = 0; i< apotheken.getApotheken().getApotheke().size(); i = i+1) {
						
					 String Name = apotheken.getApotheken().getApotheke().get(i).getName() + ", " + apotheken.getApotheken().getApotheke().get(i).getCity();
					 String GLN = apotheken.getApotheken().getApotheke().get(i).getGlnId();
					 
					 hmShops.put(Name,GLN);	
				
					}
				 									
			};
			 
			 
			//Store in Config, if successful consumption
			TimeTool now = new TimeTool(new Date());
			
			CoreHub.globalCfg.set(Constants.CFG_PHM_LASTREQUEST,now);
			CoreHub.globalCfg.set(Constants.CFG_PHM_SHOPS,this.createCFGStringShops(hmShops));
			CoreHub.globalCfg.flush();
			
         } catch (Exception ex) {
             
             System.out.println( "Exception: " + ex);
       
        }
		
		 
		
		return hmShops;
			
	}
	
	private String createCFGStringShops(HashMap<String, String> hm) {
		
		String strShops = "";
		
		//Iterate over the hash map
		if ( hm.size() > 0) {
			
		 for(Map.Entry<String, String> map : hm.entrySet()){
	           
	            
			 strShops = strShops + map.getKey() + ";" + map.getValue() + ";";
	            	       
	        }

		} 
		
		return strShops;

		
	}
	
	private HashMap<String, String> getShopsFromConfig() {
		
		HashMap<String, String> hmShops = new HashMap<String, String>();
		
		String strCFG = CoreHub.globalCfg.get(Constants.CFG_PHM_SHOPS, "");
				
		
		
		//Split string and include also blanks
		String[] attributes = strCFG.split("\\;",-1);
		 System.out.println(attributes.length);
		//Prevents error, if there are no settings saved
		if (attributes.length > 1) {
			
			 for (int i = 0; i< attributes.length-1; i = i+2) {
				 
				 hmShops.put(attributes[i], attributes[i+1]);
				 
			 }
					
		}
				
		return hmShops;
				
	}
}
