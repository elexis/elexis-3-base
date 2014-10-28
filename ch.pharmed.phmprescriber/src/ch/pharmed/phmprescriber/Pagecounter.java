/*******************************************************************************
 * Copyright (c) 2014, Pharmed Solutions GmbH
 * All rights reserved.
 *******************************************************************************/

package ch.pharmed.phmprescriber;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import ch.elexis.data.Artikel;
import ch.elexis.data.Patient;
import ch.elexis.data.Rezept;


class Pagecounter implements Printable  
{    
	
  //Fonts
  private static Font fnt = new Font("Helvetica",Font.PLAIN,8);
  private static Font fntBold = new Font("Helvetica",Font.BOLD,8);
  private static Font fntTitle = new Font("Helvetica",Font.BOLD,11);
  
  
  //Layout-Constants
  private final double LMARGINRATIO = 0.3; 
  private final double SPACERATIO = 1.5;

  
  int Code128Width = 185;
  int Code128Height = 36;
  int QRCodeBorder = 118;
  
  //Objects to print
  private Physician ph;
  private Rezept rp;
  private Patient pat;
  
  
  //Indices for correctly rendering the page
  List<Integer> indices = new ArrayList<Integer>();
 
  
  public List<Integer> getIndices() {
	  
	return indices;
}



public void setIndices(List<Integer> indices) {
	
	this.indices = indices;
}



public Pagecounter(Physician ph, Rezept rp, String presID,String QRCode){
	  
	  this.ph = ph;
	  this.rp = rp;
	  this.pat = rp.getPatient();
	  this.indices.add(-1);
	  
  }
  
    
  
  public int print(Graphics g, PageFormat pageFormat, int page)  throws PrinterException  
  {      

	  //======================================================================================================//
	  //This procedure will simply measure, how many we have to print and which products belong to which page
	  //Algorithm: (1) Measure the header and the footer to determine the area where to print the products
	  //	       (2) check, how many pages we need based on this area then productsnames with remarks
	  //Info: This procedure is necessary, as java.awt.print needs to know the page number before showing the
	  //	  print dialog...
	  //======================================================================================================//
	  
	    //Define the origin of the printArea
	    double printAreaX = pageFormat.getImageableX();
	    double printAreaY = pageFormat.getImageableY();
	    
	   //Measures the size of strings
	    FontMetrics metrics = g.getFontMetrics(fnt);
	  
	    //Parameters for the layout
	    //Dynamic variable to measure the y-position of each line
	    
	    int intMeasureY = 0;
	    //Others
	    int intMarginLeft = Integer.valueOf((int) Math.round(printAreaX*LMARGINRATIO));
	    int intSpace= Integer.valueOf((int) Math.round(metrics.getHeight()*SPACERATIO));
	    int intDefaultHeight = metrics.getHeight();
	    int intPageWidth = Integer.valueOf((int) Math.round(pageFormat.getImageableWidth()));
	    
	    int pageHeight = Integer.valueOf((int) Math.round(pageFormat.getImageableHeight()));
	    
	    metrics = g.getFontMetrics(fntTitle);
	    int intSpaceBig = metrics.getHeight();
	    	    
	    
	    //Graphics object to draw lines etc.
	    Graphics2D g2d;
	   
	    //Set colour to black
	    g.setColor(Color.black);
	    
	    //Validate the number of pages

	  	    
	      //Create a graphic2D object a set the default parameters
	      g2d = (Graphics2D) g;
	      g2d.setColor(Color.black);

	      //Translate the origin to be (0,0)
	      //Note: Imageable includes already margins for Headers and Footers
	      g2d.translate(printAreaX, printAreaY);

	      
	      //-- (2) Print the physicians attributes
	      
	      g.setFont(fntBold);
	      //Measure String height to start drawing at the right place
	      metrics = g.getFontMetrics(fntBold);
	      intMeasureY += metrics.getHeight();
	      
    
	      //Set font to default
	      g.setFont(fnt);
	      
	     //Measure the x-position (Page-Width - length of string)
	      metrics = g.getFontMetrics(fnt);
	
	      intMeasureY += metrics.getHeight();
    
	      if (ph.getSpecialty2().length() > 0) {
	    	  intMeasureY +=intDefaultHeight;
		      }
	      
	      intMeasureY +=intSpace;
	    	  
          intMeasureY +=intDefaultHeight;    
	   
	      intMeasureY += intSpace;
	      
	  	      
	      if (ph.getFax().length() > 0 ) {
	    	  intMeasureY +=intDefaultHeight;
	
	      }
	          
	      intMeasureY += intSpace;
	            
	      if(ph.getGlnid().length() > 0) {
	      
		      intMeasureY +=intDefaultHeight;

	      }
	      
	      intMeasureY += intSpaceBig;
	                
	      //-- (3) Print the line
	      
	      intMeasureY += intSpaceBig + intSpace;
	      
	      //-- (4) Title
	      g.setFont(fntTitle);     
	      
	      intMeasureY +=intSpaceBig + intDefaultHeight;
	      
	      //-- (5) Patient
	      g.setFont(fntBold);
	      
	      metrics = g.getFontMetrics(fntBold);
	      
	      g.setFont(fnt);
	      
	      intMeasureY +=intSpaceBig + intSpace;
	      	  
	      
	      //==================Area Height=============
	      //Upper limit
	      int upperlimit = intMeasureY;
		  //Define the lower limit of the area for the products
		  int lowerLimit = pageHeight - QRCodeBorder - intSpaceBig*2;
		    
		  //Area:
	      int areaHeight = lowerLimit - upperlimit;    
	
	      //Set measurer to 0
	      
	      intMeasureY = 0;
	      	      
	      
	      //-- (6) Products
	      LineBreakMeasurer lineBreakMeasurer;
	   	  int intstart, intend;
		 
		  Hashtable hash = new Hashtable();

		  //Print all the items
	      for(int i = 0; i < rp.getLines().size(); i = i+1) {
				 
	    	 	    	 	    	  
				ch.elexis.data.Prescription actualLine =  rp.getLines().get(i);
				Artikel article = actualLine.getArtikel();
				
										  			 
				 AttributedString attributedString = new AttributedString(
						 "1x " + article.getLabel(), hash);
				  
				  attributedString.addAttribute(TextAttribute.FONT, fntBold);
				  
				  g2d.setFont(fntBold);
				  FontRenderContext frc = g2d.getFontRenderContext();
				  
				  AttributedCharacterIterator attributedCharacterIterator =  attributedString.getIterator();
				  
				  intstart = attributedCharacterIterator.getBeginIndex();
				  intend = attributedCharacterIterator.getEndIndex();
				  
				  lineBreakMeasurer = new LineBreakMeasurer(attributedCharacterIterator,frc);
				  
				  float width = (float) intPageWidth-intMarginLeft;
				  
					
				  lineBreakMeasurer.setPosition(intstart);		  
				  
				  //Create TextLayout accordingly and draw it
				  while (lineBreakMeasurer.getPosition() < intend) {
				
				  TextLayout textLayout = lineBreakMeasurer.nextLayout(width);
				  
				  intMeasureY += textLayout.getAscent();
				
				  intMeasureY += textLayout.getDescent() + textLayout.getLeading();
						  
				  }
				    
			    //Draw the label
			    String label = actualLine.getBemerkung();
			    
			    if (actualLine.getDosis().length() > 0 ){
			    	
			    	label = actualLine.getDosis() + ", " + label;
			    	
			    }
			    
			    //If there is no label specified, go to the next iterations
			    if (label.length() == 0) {
			    	
			    	if (areaHeight < intMeasureY){
							
						  this.indices.add(i-1);
						  
						  //Do this run again and set the measurer to 0 again
						  i = i-1;
						  intMeasureY = 0; 
						  
					}	
					  
					else {intMeasureY += intSpaceBig*2;}
			    	 
			    	 continue;
			    	
			    }
			    
			    
			    attributedString = new AttributedString(label, hash);
			       
				attributedString.addAttribute(TextAttribute.FONT, fnt);
						  
				g2d.setFont(fnt);
				frc = g2d.getFontRenderContext();
				  
				attributedCharacterIterator =  attributedString.getIterator();
				  
						  intstart = attributedCharacterIterator.getBeginIndex();
						  intend = attributedCharacterIterator.getEndIndex();
						  			
						  lineBreakMeasurer = new LineBreakMeasurer(attributedCharacterIterator,frc);
						 
						  lineBreakMeasurer.setPosition(intstart);
						  
						  //Create TextLayout accordingly and draw it
						  while (lineBreakMeasurer.getPosition() < intend) {
						
							  //Extra code to determine line breaks in the string --> go on new line, if there is one
							  int next = lineBreakMeasurer.nextOffset(width);
							  int limit = next;
							  if (limit <= label.length()) {
							    for (int k = lineBreakMeasurer.getPosition(); k < next; ++k) {
							      char c = label.charAt(k);
							      if (c == '\n') {
							        limit = k + 1;
							        break;
							      }
							    }
							  }  
							  
							  
						  TextLayout textLayout = lineBreakMeasurer.nextLayout(width,limit, false);
						  
						  intMeasureY += textLayout.getAscent();
											  					
						  intMeasureY += textLayout.getDescent() + textLayout.getLeading();
						  
						  }	
			    					
						  
						//If the last item will touch the limit, assign the (real) last item to the indices, reset the measurer and run the current loop again  
						if (areaHeight < intMeasureY){
								
							  this.indices.add(i-1);
							  
							  //Do this run again and set the measurer to 0 again
							  i = i-1;
							  intMeasureY = 0; 
						}	
						  
						else {intMeasureY += intSpaceBig*2;}
									  
				   		    
	      }
	      
	      //Add the last item
	      this.indices.add(rp.getLines().size()-1);
	      
	      //(8) Return no such page to terminate
	      return (NO_SUCH_PAGE);
	      
  }  
} 

