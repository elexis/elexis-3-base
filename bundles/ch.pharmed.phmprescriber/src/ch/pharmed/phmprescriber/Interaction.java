package ch.pharmed.phmprescriber;




import java.util.ArrayList;
import java.util.List;

import ch.elexis.data.Rezept;
import ch.pharmedsolutions.www.interactionservice.InteractionPortType;
import ch.pharmedsolutions.www.interactionservice.InteractionRequest;
import ch.pharmedsolutions.www.interactionservice.InteractionService;
import ch.pharmedsolutions.www.interactionservice.Interactions;


public class Interaction {

	
		public Interaction() {
			
			// TODO Auto-generated constructor stub
						
							
		}
		
	//Check a prescription for interactions
		public List<String> checkPrescription(Rezept rp){
			
							
			//If only 1 product, don't run the check an proceed normally
			 if  (rp.getLines().size() < 2) {
				 
				 return null;
			 }
			
			 
			//Input parameter
			ch.pharmedsolutions.www.interactionservice.InteractionRequest IARequest = new ch.pharmedsolutions.www.interactionservice.InteractionRequest();
			
			
			//Assign all products
			IARequest.setKey("kdMie893Kaop");
					
			List<String> atc_codes = new ArrayList<String>();
			
		    for(int i = 0; i < rp.getLines().size(); i = i+1) {
		            	
		    	IARequest.getAtcCodes().add(rp.getLines().get(i).getArtikel().getATC_code());
		    	atc_codes.add(IARequest.getAtcCodes().get(i));
		    			    
		    }
				
		    Interactions interactions = new Interactions();
		    
			 try {
		            
				 //Get the information
				 interactions = consumService(IARequest);
	            
				 if (!(interactions == null) && interactions.getInteractions().getInteraction().size() > 0) {
					
					 return getDescription(rp,interactions, atc_codes);

				
				};
				 
				 
	        } catch (Exception ex) {
	            
	            System.out.println( "Exception: " + ex);
	      
	       }
		    
		    
			return null;
			
		}
		
		
		
		
		private Interactions consumService(InteractionRequest IAReqest) {
			
			InteractionService service = new InteractionService();
	        InteractionPortType port = service.getInteractionPort();
	        
	        return port.checkInteraction(IAReqest);
			
		}
	
	
		private List<String> getDescription(Rezept rp, Interactions interactions, List<String> atc_codes) {
			
			//Define the final list
			List<String> Descriptions = new ArrayList<String>();
			
			for (int i = 0; i< interactions.getInteractions().getInteraction().size(); i = i+1) {
				
				String atc1 = interactions.getInteractions().getInteraction().get(i).getAtcCode1();
				String atc2 = interactions.getInteractions().getInteraction().get(i).getAtcCode2();
				
				int indexLeft = atc_codes.indexOf(atc1);
				int indexRight = atc_codes.indexOf(atc2);
				
				Descriptions.add(rp.getLines().get(indexLeft).getArtikel().getName() + " <--> " + rp.getLines().get(indexRight).getArtikel().getName());
				Descriptions.add(interactions.getInteractions().getInteraction().get(i).getDescr());
												
				
			}
			
			return Descriptions;
			
			
		}
		
		
	
}
