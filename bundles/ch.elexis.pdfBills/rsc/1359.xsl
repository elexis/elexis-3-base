<?xml version="1.0" encoding="ISO-8859-1"?> 
<xsl:stylesheet
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
     xmlns:fo="http://www.w3.org/1999/XSL/Format"
     xmlns:invoice="http://www.xmlData.ch/xmlInvoice/XSD">
  <xsl:template match ="invoice:*">
  	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="OCRB10PitchBT-Regular">
  	  
  	  <fo:layout-master-set>
  	  	<!-- 
  	  	<fo:simple-page-master master-name="RF_Arzt_40_Code_First" page-height="29.7cm"
                    page-width="21cm"
                    margin-top="1.5cm"
                    margin-bottom="1.5cm"
                    margin-left="1.5cm"
                    margin-right="1.5cm">
 		  <fo:region-body margin-top="3cm" />
 		  <fo:region-before extent="5.5cm"/>
 		  <fo:region-after extent="1.8cm"/>
      	</fo:simple-page-master>
      	-->
  	  	<fo:simple-page-master master-name="RF_Arzt_40_Code" page-height="29.7cm" page-width="21cm" margin-top="1cm" margin-bottom="17mm" margin-left="1.5cm" margin-right="0.7cm">
 		  <fo:region-body margin-top="3.1cm" margin-bottom="5.6cm"/>
 		  <fo:region-before extent="3.5cm"/>
 		  <fo:region-after extent="5.6cm" />
      	</fo:simple-page-master>
      	
      	<!-- Specify the page sequence -->
      	<!-- 
		<fo:page-sequence-master master-name="master-sequence">
		  <fo:single-page-master-reference master-reference="RF_Arzt_40_Code_First" />
		  <fo:repeatable-page-master-reference master-reference="RF_Arzt_40_Code" />
		</fo:page-sequence-master>
		-->
      </fo:layout-master-set> 
      
      <!-- First -->
      <fo:page-sequence master-reference="RF_Arzt_40_Code">
        <fo:static-content flow-name="xsl-region-before">
          
          <fo:table table-layout="fixed" width="100%"  border-collapse="collapse" >
  		 	  <fo:table-column column-width="35%" text-align="left"/>
  		 	  <fo:table-column column-width="30%" text-align="center"/>
		      <fo:table-column column-width="25%" text-align="right"/>
		      <fo:table-column column-width="10%" text-align="right"/>
		      <fo:table-body>
  			   <fo:table-row>
	  		    <fo:table-cell>
	  		    	<fo:block  font-weight="bold" font-size="15">
	  		    		<xsl:choose>
               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
               					 
               					Rückforderungsbeleg
               				</xsl:when>
               				<xsl:otherwise>
               					TP-Rechnung
               				</xsl:otherwise>
               			</xsl:choose>	
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell>
	  		    	<fo:block text-align="center"  font-size="15">
	  		    		
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell>
	  		    	<fo:block text-align="right">
	  		    		Release 4.0
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell>
	  		    	<fo:block text-align="right" font-weight="bold">
	  		    		M
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		   </fo:table-row>
	  		  </fo:table-body> 	
  		  </fo:table>
  		  <fo:table table-layout="fixed" width="100%" >
  		  	  <fo:table-column column-width="100%" />
  		  	  <fo:table-body font-size="12px" font-family="tahoma,arial,helvetica,sans-serif">
  			   <fo:table-row>
	  		    <fo:table-cell border="0.5pt solid black">
		  		   	 <fo:table table-layout="fixed" width="100%" >
	  		  	  	  <fo:table-column column-width="15%" />
	  		  	  	  <fo:table-column column-width="15%" />
	  		  	  	  <fo:table-column column-width="20%" />
	  		  	  	  <fo:table-column column-width="15%" />
	  		  	  	  <fo:table-column column-width="35%" />
	  		  	  	  <fo:table-body>
	  			   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Dokument
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:value-of select="/invoice:request/invoice:invoice/@invoice_id" /> 
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif" text-align="center">
	  		    				Seite  <fo:page-number/>
	  		    			</fo:block>
	  		    		</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Rechnungssteller
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				EAN-Nr.
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
		  		        		<!-- 
	  		    				<xsl:value-of select="/invoice:request/invoice:header/invoice:sender/@ean_party" /> 
	  		    				-->
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@ean_party" /> 
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@ean_party" /> 
		               				</xsl:otherwise>
		               			</xsl:choose>	
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif" >
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="concat(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:givenname)" /> 
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="concat(/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:givenname)" />
		               				</xsl:otherwise>
		               			</xsl:choose>	
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif" white-space-treatment="preserve" white-space="pre">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="concat(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:street, '        ', /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:city)" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="concat(/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:street, '        ', /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:city)" /> 
		               				</xsl:otherwise>
		               			</xsl:choose>
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				ZSR-Nr. 
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif" >
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@zsr" /> 
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@zsr" /> 
		               				</xsl:otherwise>
		               			</xsl:choose>
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell >
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					Tel: <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:telecom/invoice:phone" /> 
			  		    			</xsl:when>
		               				<xsl:otherwise>
		               					Tel: <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:telecom/invoice:phone" /> 
			  		    			</xsl:otherwise>
		               			</xsl:choose>
		               		</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif" >
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					Fax: <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:telecom/invoice:fax" />
			  		    				Email: <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:online/invoice:email" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					Fax: <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:telecom/invoice:fax" />
			  		    				Email: <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:online/invoice:email" /> 
		               				</xsl:otherwise>
		               			</xsl:choose>
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Leistungserbringer
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				EAN-Nr.
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/@ean_party" /> 
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/@ean_party" /> 
		               				</xsl:otherwise>
		               			</xsl:choose>
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="concat(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/invoice:person/invoice:givenname)" /> 
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="concat(/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/invoice:person/invoice:givenname)" /> 
		               				</xsl:otherwise>
		               			</xsl:choose>
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif" white-space-treatment="preserve" white-space="pre">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="concat(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/invoice:person/invoice:postal/invoice:street, '        ', /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/invoice:person/invoice:postal/invoice:city)" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="concat(/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/invoice:person/invoice:postal/invoice:street, '        ', /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/invoice:person/invoice:postal/invoice:city)" />
		               				</xsl:otherwise>
		               			</xsl:choose> 
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				ZSR-Nr./ NIF-Nr.
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/@zsr" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/@zsr" />
		               				</xsl:otherwise>
		               			</xsl:choose> 
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					Tel: <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/invoice:person/invoice:telecom/invoice:phone" /> 
			  		    			</xsl:when>
		               				<xsl:otherwise>
		               					Tel: <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/invoice:person/invoice:telecom/invoice:phone" /> 
			  		    			</xsl:otherwise>
		               			</xsl:choose> 
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					Fax: <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/invoice:person/invoice:telecom/invoice:fax" />
			  		    				Email: <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/invoice:person/invoice:online/invoice:email" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					Fax: <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/invoice:person/invoice:telecom/invoice:fax" />
			  		    				Email: <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/invoice:person/invoice:online/invoice:email" />
		               				</xsl:otherwise>
		               			</xsl:choose> 
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   
		  		  	  </fo:table-body>  
	  		  		 </fo:table>
	  		    </fo:table-cell>
	  		   </fo:table-row>
	  		  </fo:table-body>  
  		  </fo:table>	
          
        </fo:static-content>
        
        <fo:static-content flow-name="xsl-region-after">
          <fo:table table-layout="fixed" width="85%"   >
  		    <fo:table-body>
  			  <fo:table-row>
	  		    <fo:table-cell >
	  		    	<fo:block   font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="center"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		TARMED AL
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_tarmed.mt" />(
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@unit_tarmed.mt" />)
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Physio
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_physio" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		MiGel
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_migel" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		übrige
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell>
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_unclassified" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    
	  		  </fo:table-row>
	  		  
	  		  <fo:table-row>
	  		    <fo:table-cell >
	  		    	<fo:block   font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="center"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		TARMED TL
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_tarmed.tt" />(
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@unit_tarmed.tt" />)
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Labor
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_lab" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Medi
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_drug" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Kantonal
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell>
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_cantonal" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    
	  		  </fo:table-row>
	  		  
	  		  <fo:table-row>
	  		  	<fo:table-cell number-columns-spanned="9">
	  		  		<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_cantonal" />
	  		    	</fo:block>
	  		  	</fo:table-cell>
	  		  </fo:table-row>
	  	  	</fo:table-body>
	  	  </fo:table>
	  	  
	  	  <fo:table table-layout="fixed" width="100%"   >
  		    <fo:table-body>
  			  <fo:table-row height="15pt">
	  		    <fo:table-cell >
	  		    	<fo:block text-align="left"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Gesamtbetrag
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@currency" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_obligations" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		davon PFL
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="(/invoice:request/invoice:invoice/invoice:balance/@amount_obligations - /invoice:request/invoice:invoice/invoice:balance/@amount_unclassified)" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Anzahlung
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_prepaid" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell>
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Fälliger Betrag
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_due" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		  </fo:table-row>
	  		  
	  		  <fo:table-row height="15pt">
	  		    <fo:table-cell >
	  		    	<fo:block text-align="left"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		MwSt.Nr.
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Keine
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell number-columns-spanned="7">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		   
	  		  </fo:table-row>
	  	  	</fo:table-body>
	  	  </fo:table>
	  	  
	  	  <fo:table table-layout="fixed" width="25%"   >
	  	  	
			<fo:table-column column-width="20%"/>
			<fo:table-column column-width="20%"/>
			<fo:table-column column-width="30%"/>
			<fo:table-column column-width="30%"/>
  		    <fo:table-body>
  			  <fo:table-row>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="left"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Code
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Satz
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Betrag
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		MwSt
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		  </fo:table-row>
	  		  
	  		  <fo:table-row>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="left"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		0
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		0
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/invoice:vat/invoice:vat_rate/@amount" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/invoice:vat/invoice:vat_rate/@vat" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		  </fo:table-row>
	  		  
	  		  <fo:table-row>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="left"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		1
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		7.6
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		0.0
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		0.0
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		  </fo:table-row>
	  		  
	  		  <fo:table-row>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="left"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		2
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		2.4
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		0.0
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		0.0
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		  </fo:table-row>
	  		  
	  		  <fo:table-row>
	  		    <fo:table-cell number-columns-spanned="2">
	  		    	<fo:block text-align="left"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Total
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_due" />
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell >
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		  </fo:table-row>
	  		  
	  		  <fo:table-row height="1.6cm">
	  		    <fo:table-cell number-columns-spanned="4">
	  		    	<fo:block text-align="left"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    	
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		  </fo:table-row>
	  		  
	  	  	</fo:table-body>
	  	  </fo:table>
	  	 
	  	  <fo:block font-family="OCRB" font-size="10px" text-align="right"  width="11.1cm">
	  		    <xsl:choose>
       				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:esr9) > 0">
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr9/@coding_line" /> 
       				</xsl:when>
       				<xsl:otherwise>
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr6/@coding_line" /> 
       				</xsl:otherwise>
       			</xsl:choose>				
	  	  </fo:block>		   
        </fo:static-content>
        
        <fo:flow flow-name="xsl-region-body" >
  	      
  	      <fo:table table-layout="fixed" width="100%" >
  		  	  <fo:table-column column-width="100%" />
  		  	  <fo:table-body font-size="12px" font-family="tahoma,arial,helvetica,sans-serif">
  			   <fo:table-row>
	  		    <fo:table-cell border="0.5pt solid black">
		  		   	 <fo:table table-layout="fixed" width="100%" >
	  		  	  	  <fo:table-column column-width="15%" />
	  		  	  	  <fo:table-column column-width="20%" />
	  		  	  	  <fo:table-column column-width="25%" />
	  		  	  	  <fo:table-column column-width="20%" />
	  		  	  	  <fo:table-column column-width="20%" />
	  		  	  	  <fo:table-body>
	  		  	  	  	<fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Patient
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Name
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:patient/invoice:person/invoice:familyname" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:patient/invoice:person/invoice:familyname" />
		               				</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				EAN-Nr.
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
		  		        		<xsl:choose>
		               				<xsl:when test="/invoice:request/invoice:header/invoice:recipient/@ean_party != ''">
		               					<xsl:value-of select="/invoice:request/invoice:header/invoice:recipient/@ean_party" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:choose>
				               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
				               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:insurance/@ean_party" />
				               				</xsl:when>
				               				<xsl:otherwise>
				               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:insurance/@ean_party" />
				               				</xsl:otherwise>
				               			</xsl:choose>  
		               				</xsl:otherwise>
		               			</xsl:choose>	
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Vorname
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:patient/invoice:person/invoice:givenname" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:patient/invoice:person/invoice:givenname" />
		               				</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Strasse
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:patient/invoice:person/invoice:postal/invoice:street" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:patient/invoice:person/invoice:postal/invoice:street" />
		               				</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				PLZ
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:patient/invoice:person/invoice:postal/invoice:zip" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:patient/invoice:person/invoice:postal/invoice:zip" />
		               				</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Ort
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:patient/invoice:person/invoice:postal/invoice:city" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:patient/invoice:person/invoice:postal/invoice:city" />
		               				</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell number-columns-spanned="2" >
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif" >
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:choose>
		               					 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
		               					 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:companyname" />
		               					 	</xsl:when>
				               				<xsl:otherwise>
				               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:givenname" />
		               							<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:familyname" />
				               				</xsl:otherwise>
				               			</xsl:choose>  
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:companyname" />
		               				</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Geburtsdatum
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif" >
		  		        		<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:call-template name="FormatDate">
		               						<xsl:with-param name="DateTime" select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:patient/@birthdate"/>
		               					</xsl:call-template>
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:call-template name="FormatDate">
		               						<xsl:with-param name="DateTime" select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:patient/@birthdate"/>
		               					</xsl:call-template>
		               				</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	
		  		   	 	<fo:table-cell number-columns-spanned="2" >
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif" >
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:choose>
		               					 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
		               					 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:street" />
		               					 	</xsl:when>
				               				<xsl:otherwise>
				               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:street" />
				               				</xsl:otherwise>
				               			</xsl:choose>  	
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:postal/invoice:street" />
		               					
		               				</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Geschlecht
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
		               			<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:choose>
				               				<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:patient/@gender = 'male'">
				               				m
				               				</xsl:when>
				               				<xsl:otherwise>
				               				w
				               				</xsl:otherwise>
				               			</xsl:choose>
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:choose>
				               				<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:patient/@gender = 'male'">
				               				m
				               				</xsl:when>
				               				<xsl:otherwise>
				               				w
				               				</xsl:otherwise>
				               			</xsl:choose>
		               				</xsl:otherwise>
		               			</xsl:choose>  	
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	
		  		   	 	<fo:table-cell number-columns-spanned="2" >
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif" >
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:choose>
		               					 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
		               					 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:zip" />
		               							<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:city" />
		               					 	</xsl:when>
				               				<xsl:otherwise>
				               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:zip" />
		               							<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:city" />
				               				</xsl:otherwise>
				               			</xsl:choose>  	
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:postal/invoice:zip" />
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:postal/invoice:city" />
		               				</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Unfalldatum
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
		  		        		<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:uvg) > 0">
		               					<xsl:call-template name="FormatDate">
								          <xsl:with-param name="DateTime" select="/invoice:request/invoice:invoice/invoice:detail/@date_begin"/>
								        </xsl:call-template>
		               				</xsl:when>
		               				<xsl:otherwise>
		               					
		               				</xsl:otherwise>
		               			</xsl:choose>  
	  		    				<!-- 
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:kvg) > 0">
		               					<xsl:call-template name="FormatDate">
								          <xsl:with-param name="DateTime" select="/invoice:request/invoice:invoice/invoice:detail/invoice:kvg/@case_date"/>
								        </xsl:call-template>
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:mvg) > 0">
		               					<xsl:call-template name="FormatDate">
								          <xsl:with-param name="DateTime" select="/invoice:request/invoice:invoice/invoice:detail/invoice:mvg/@case_date"/>
								        </xsl:call-template>
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:ivg) > 0">
		               					<xsl:call-template name="FormatDate">
								          <xsl:with-param name="DateTime" select="/invoice:request/invoice:invoice/invoice:detail/invoice:ivg/@case_date"/>
								        </xsl:call-template>
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:call-template name="FormatDate">
								          <xsl:with-param name="DateTime" select="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@case_date"/>
								        </xsl:call-template>
		               				</xsl:otherwise>
		               			</xsl:choose>  
		               			--> 
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	
		  		   	 	<fo:table-cell number-columns-spanned="2" >
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif" >
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Unfall-/Verfügungsnr
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:kvg) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:kvg/@case_id" />
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:mvg) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:mvg/@case_id" />
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:ivg) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:ivg/@case_id" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@case_id" />
		               				</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				AHV-Nr.
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:kvg) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:kvg/@ssn" />
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:mvg) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:mvg/@ssn" />
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:ivg) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:ivg/@ssn" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@ssn" />
		               				</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Versicherten-Nr.
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:kvg) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:kvg/@patient_id" />
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:mvg) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:mvg/@patient_id" />
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:ivg) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:ivg/@patient_id" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@patient_id" />
		               				</xsl:otherwise>
		               			</xsl:choose>
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Betriebs-Nr./Name
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:value-of select="/invoice:request/invoice:invoice/invoice:employer/@reg_number" />
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Kanton
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/@canton" />
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Rechnungskopie
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:choose>
		               				<xsl:when test="/invoice:request/invoice:invoice/@resend = 'false'">
		               				Nein
		               				</xsl:when>
		               				<xsl:otherwise>
		               				ja
		               				</xsl:otherwise>
		               			</xsl:choose>	
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Vergütungsart
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					TG
		               				</xsl:when>
		               				<xsl:otherwise>
		               					TP
		  		        			</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif" >
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Gesetz
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:kvg) > 0">
		               					KVG
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:mvg) > 0">
		               					MVG
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:ivg) > 0">
		               					IVG
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:vvg) > 0">
		               					VVG
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:uvg) > 0">
		               					UVG
		               				</xsl:when>
		               				<xsl:otherwise>
		               					privat
		               				</xsl:otherwise>
		               			</xsl:choose>
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Behandlungsgrund
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
		  		        		<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:kvg) > 0">
		               					<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:kvg/@reason = 'disease'">
				  		        			Krankheit
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:kvg/@reason = 'accident'">
			  		    					Unfall
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:kvg/@reason = 'maternity'">
			  		    					Schwangerschaft
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:kvg/@reason = 'prevention'">
			  		    					Vorsorge
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:kvg/@reason = 'birthdefect'">
			  		    					Geburtsgebrechen
			  		    				</xsl:if>
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:mvg) > 0">
		               					<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:mvg/@reason = 'disease'">
				  		        			Krankheit
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:mvg/@reason = 'accident'">
			  		    					Unfall
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:mvg/@reason = 'maternity'">
			  		    					Schwangerschaft
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:mvg/@reason = 'prevention'">
			  		    					Vorsorge
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:mvg/@reason = 'birthdefect'">
			  		    					Geburtsgebrechen
			  		    				</xsl:if>
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:ivg) > 0">
		               					<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:ivg/@reason = 'disease'">
				  		        			Krankheit
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:ivg/@reason = 'accident'">
			  		    					Unfall
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:ivg/@reason = 'maternity'">
			  		    					Schwangerschaft
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:ivg/@reason = 'prevention'">
			  		    					Vorsorge
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:ivg/@reason = 'birthdefect'">
			  		    					Geburtsgebrechen
			  		    				</xsl:if>
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:vvg) > 0">
		               					<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:vvg/@reason = 'disease'">
				  		        			Krankheit
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:vvg/@reason = 'accident'">
			  		    					Unfall
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:vvg/@reason = 'maternity'">
			  		    					Schwangerschaft
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:vvg/@reason = 'prevention'">
			  		    					Vorsorge
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:vvg/@reason = 'birthdefect'">
			  		    					Geburtsgebrechen
			  		    				</xsl:if>
		               				</xsl:when>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:uvg) > 0">
		               					<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@reason = 'disease'">
				  		        			Krankheit
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@reason = 'accident'">
			  		    					Unfall
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@reason = 'maternity'">
			  		    					Schwangerschaft
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@reason = 'prevention'">
			  		    					Vorsorge
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@reason = 'birthdefect'">
			  		    					Geburtsgebrechen
			  		    				</xsl:if>
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@reason = 'disease'">
				  		        			Krankheit
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@reason = 'accident'">
			  		    					Unfall
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@reason = 'maternity'">
			  		    					Schwangerschaft
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@reason = 'prevention'">
			  		    					Vorsorge
			  		    				</xsl:if>
			  		    				<xsl:if test="/invoice:request/invoice:invoice/invoice:detail/invoice:uvg/@reason = 'birthdefect'">
			  		    					Geburtsgebrechen
			  		    				</xsl:if>
		               				</xsl:otherwise>
		               			</xsl:choose>
		  		        		
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Behandlung
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:call-template name="FormatDate">
						          <xsl:with-param name="DateTime" select="/invoice:request/invoice:invoice/invoice:detail/@date_begin"/>
						        </xsl:call-template>
						        - 
						        <xsl:call-template name="FormatDate">
						          <xsl:with-param name="DateTime" select="/invoice:request/invoice:invoice/invoice:detail/@date_end"/>
						        </xsl:call-template>
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Rechnungsnr.
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:value-of select="number(substring(/invoice:request/invoice:invoice/@invoice_id,string-length(/invoice:request/invoice:invoice/@invoice_id) - 5))" />
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row>
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Erbringungsort
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Praxis
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Rechnungs-/Mahndatum
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:call-template name="FormatDate">
						          <xsl:with-param name="DateTime" select="/invoice:request/invoice:invoice/@invoice_date"/>
						        </xsl:call-template>
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row border-top-width="0.5pt" border-bottom-width="0.5pt" border-top-color="black" border-bottom-color="black" border-top-style="solid" border-bottom-style="solid">
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Auftraggeber
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				EAN-Nr. / ZSR-Nr.
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
		  		        		
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:referrer/@ean_party" />/
		  		        				<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:referrer/@zsr" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:referrer/@ean_party" />/
		  		        			<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:referrer/@zsr" />
		               				</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:referrer/invoice:person/@salutation" />
	  		    						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:referrer/invoice:person/invoice:familyname" />
	  		    						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:referrer/invoice:person/invoice:givenname" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:referrer/invoice:person/@salutation" />
	  		    						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:referrer/invoice:person/invoice:familyname" />
	  		    						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:referrer/invoice:person/invoice:givenname" />
		  		        			</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    				<xsl:choose>
		               				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:referrer/invoice:person/invoice:postal/invoice:street" />
		               				</xsl:when>
		               				<xsl:otherwise>
		               					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:referrer/invoice:person/invoice:postal/invoice:street" />
		  		        			</xsl:otherwise>
		               			</xsl:choose>  
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row border-top-width="0.5pt" border-bottom-width="0.5pt" border-top-color="black" border-bottom-color="black" border-top-style="solid" border-bottom-style="solid">
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Diagnose
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif" >
	  		    				TI-Code  <xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:diagnosis/@code" />
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				 <xsl:value-of select="/invoice:request/invoice:invoice/invoice:detail/invoice:diagnosis" />
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row border-top-width="0.5pt" border-bottom-width="0.5pt" border-top-color="black" border-bottom-color="black" border-top-style="solid" border-bottom-style="solid">
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				EAN-Liste
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				1/<xsl:value-of select="/invoice:request/invoice:header/invoice:sender/@ean_party" />
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
		  		   	   
		  		   	   <fo:table-row border-top-width="0.5pt" border-bottom-width="0.5pt" border-top-color="black" border-bottom-color="black" border-top-style="solid" border-bottom-style="solid">
		  		        <fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				Bemerkung
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				<xsl:value-of select="/invoice:request/invoice:invoice/invoice:remark" />
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	 	<fo:table-cell>
		  		        	<fo:block font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    				
	  		    			</fo:block>
		  		   	 	</fo:table-cell>
		  		   	   </fo:table-row>
  	      			  </fo:table-body>  
	  		  		 </fo:table>
	  		    </fo:table-cell>
	  		   </fo:table-row>
	  		  </fo:table-body>  
  		  </fo:table>
  		  	
  	      <fo:table table-layout="fixed" width="100%"   margin-top="10pt">
  		      <fo:table-body>
  			   <fo:table-row>
	  		    <fo:table-cell >
	  		    	<fo:block   font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Datum
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="1cm">
	  		    	<fo:block text-align="center"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Tarif
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="1.75cm">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Tarifziffer
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="1.5cm">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Bezugsziffer
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="0.4cm">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Si
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="1cm">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		St
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="1cm">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Anzahl
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="1.7cm">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		TP AL / Preis
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell>
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		fAL 
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="1.2cm">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		TPW AL
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="1.2cm">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		TP TL
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell>
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		fTL
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="1.2cm">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		TPW TL
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="0.4cm">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		A
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="0.4cm">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		V
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="0.4cm">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		P
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell width="0.4cm">
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		M
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		    <fo:table-cell>
	  		    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	  		    		Betrag
	  		    	</fo:block>
	  		    </fo:table-cell>
	  		   </fo:table-row>
	  		   
	  		   <xsl:for-each select="/invoice:request/invoice:invoice/invoice:detail/invoice:services/invoice:record_tarmed">
	  		   		<xsl:call-template name="recordsOfTarmed">
					</xsl:call-template>
	  		   </xsl:for-each>
	  		   <xsl:for-each select="/invoice:request/invoice:invoice/invoice:detail/invoice:services/invoice:record_lab">
	  		   		<xsl:call-template name="recordsOfLab">
					</xsl:call-template>
	  		   </xsl:for-each>
	  		   <xsl:for-each select="/invoice:request/invoice:invoice/invoice:detail/invoice:services/invoice:record_unclassified">
	  		   		<xsl:call-template name="recordsOfUnClassified">
					</xsl:call-template>
	  		   </xsl:for-each> 
	  		   <xsl:for-each select="/invoice:request/invoice:invoice/invoice:detail/invoice:services/invoice:record_migel">
	  		   		<xsl:call-template name="recordsOfMigel">
					</xsl:call-template>
	  		   </xsl:for-each> 
	  		   <xsl:for-each select="/invoice:request/invoice:invoice/invoice:detail/invoice:services/invoice:record_drug">
	  		   		<xsl:call-template name="recordsOfDrug">
					</xsl:call-template>
	  		   </xsl:for-each> 
	  		  </fo:table-body> 	
  		  </fo:table>
	    </fo:flow>
      </fo:page-sequence>
      
      <!-- Rest of pages -->
      <!-- 
      <fo:page-sequence master-reference="RF_Arzt_40_Code">
        <fo:static-content flow-name="xsl-region-before">
          <fo:block font-family="sans-serif">
        	Header2
          </fo:block>
        </fo:static-content>
        <fo:static-content flow-name="xsl-region-after">
          <fo:block font-family="sans-serif">
        	Footer2
          </fo:block>
        </fo:static-content>
        <fo:flow flow-name="xsl-region-body" >
  	      <fo:block font-family="sans-serif">
        	Testing the application2
          </fo:block>
	    </fo:flow>
      </fo:page-sequence>
       -->
  	</fo:root>   	
  </xsl:template>
  
  <xsl:template name="FormatDate">
    <xsl:param name="DateTime" />
   
    <!-- new date format 2006-01-14T08:55:22 -->
    <xsl:variable name="year">
      <xsl:value-of select="substring($DateTime,1,4)" />
    </xsl:variable>
    <xsl:variable name="month-temp">
      <xsl:value-of select="substring-after($DateTime,'-')" />
    </xsl:variable>
    <xsl:variable name="month">
      <xsl:value-of select="substring-before($month-temp,'-')" />
    </xsl:variable>
    <xsl:variable name="day-temp">
      <xsl:value-of select="substring-after($month-temp,'-')" />
    </xsl:variable>
    <xsl:variable name="day">
      <xsl:value-of select="substring($day-temp,1,2)" />
    </xsl:variable>
    
    <xsl:value-of select="$day"/>
    <xsl:value-of select="'.'"/>
    <xsl:value-of select="$month"/>
    <xsl:value-of select="'.'"/>
    <xsl:value-of select="$year"/>
    
    
  </xsl:template>
  
  <xsl:template name="recordsOfTarmed">
  	<fo:table-row height="10pt"  >
	    <fo:table-cell number-columns-spanned="18"  padding-top="5pt" padding-left="50pt">
	    	<fo:block   font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="." />
	    	</fo:block>
	    </fo:table-cell>
	</fo:table-row> 
	<fo:table-row>
	    <fo:table-cell>
	    	<fo:block   font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		
	    		<xsl:call-template name="FormatDate">
	         <xsl:with-param name="DateTime" select="@date_begin"/>
	       </xsl:call-template>
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="center"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@tariff_type" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@code" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@ref_code" />
	    		
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@number" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
         			<xsl:when test="@body_location = 'none'">
         				
         			</xsl:when>
         			<xsl:otherwise>
         				<xsl:value-of select="@body_location" />
      			    </xsl:otherwise>
         		</xsl:choose>  
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@quantity" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit.mt" />
	    		
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@scale_factor.mt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		
	    		<xsl:value-of select="@unit_factor.mt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit.tt" />
	    	</fo:block>
	    </fo:table-cell>
	     <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@scale_factor.tt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit_factor.tt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
        			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						1
        					</xsl:otherwise>
        				</xsl:choose>
        			</xsl:when>
        			<xsl:otherwise>
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						1
        					</xsl:otherwise>
        				</xsl:choose> 
        			</xsl:otherwise>
        		</xsl:choose>	
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
        			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						2
        					</xsl:otherwise>
        				</xsl:choose>
        			</xsl:when>
        			<xsl:otherwise>
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						2
        					</xsl:otherwise>
        				</xsl:choose> 
        			</xsl:otherwise>
        		</xsl:choose>	
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
	          				<xsl:when test="obligation = 'true'">
	          				0
	          				</xsl:when>
	          				<xsl:otherwise>
	          				1
	          				</xsl:otherwise>
	          			</xsl:choose>	
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@vat_rate" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@amount" />
	    	</fo:block>
	    </fo:table-cell>
	</fo:table-row>
  </xsl:template>
  
  <xsl:template name="recordsOfUnClassified">
  	<fo:table-row height="10pt"  >
	    <fo:table-cell number-columns-spanned="18"  padding-top="5pt" padding-left="50pt">
	    	<fo:block   font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="." />
	    	</fo:block>
	    </fo:table-cell>
	   </fo:table-row> 
	   <fo:table-row>
	    <fo:table-cell>
	    	<fo:block   font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		
	    		<xsl:call-template name="FormatDate">
	         <xsl:with-param name="DateTime" select="@date_begin"/>
	       </xsl:call-template>
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="center"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@tariff_type" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@code" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@ref_code" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@number" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@body_location" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@quantity" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit" />
	    		
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@scale_factor.mt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit_factor" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit.tt" />
	    	</fo:block>
	    </fo:table-cell>
	     <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@scale_factor.tt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit_factor.tt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
        			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						1
        					</xsl:otherwise>
        				</xsl:choose>
        			</xsl:when>
        			<xsl:otherwise>
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						1
        					</xsl:otherwise>
        				</xsl:choose> 
        			</xsl:otherwise>
        		</xsl:choose>
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
        			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						2
        					</xsl:otherwise>
        				</xsl:choose>
        			</xsl:when>
        			<xsl:otherwise>
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						2
        					</xsl:otherwise>
        				</xsl:choose> 
        			</xsl:otherwise>
        		</xsl:choose>
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
	          				<xsl:when test="obligation = 'true'">
	          				0
	          				</xsl:when>
	          				<xsl:otherwise>
	          				1
	          				</xsl:otherwise>
	          			</xsl:choose>	
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@vat_rate" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@amount" />
	    	</fo:block>
	    </fo:table-cell>
	</fo:table-row>
  </xsl:template>
  
  
  <xsl:template name="recordsOfLab">
  	<fo:table-row height="10pt"  >
	    <fo:table-cell number-columns-spanned="18"  padding-top="5pt" padding-left="50pt">
	    	<fo:block   font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="." />
	    	</fo:block>
	    </fo:table-cell>
	   </fo:table-row> 
	   <fo:table-row>
	    <fo:table-cell>
	    	<fo:block   font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		
	    		<xsl:call-template name="FormatDate">
	         <xsl:with-param name="DateTime" select="@date_begin"/>
	       </xsl:call-template>
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="center"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@tariff_type" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@code" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@ref_code" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@number" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@body_location" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@quantity" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit" />
	    		
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@scale_factor.mt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		
	    		<xsl:value-of select="@unit_factor" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit.tt" />
	    	</fo:block>
	    </fo:table-cell>
	     <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@scale_factor.tt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit_factor.tt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
        			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						1
        					</xsl:otherwise>
        				</xsl:choose>
        			</xsl:when>
        			<xsl:otherwise>
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						1
        					</xsl:otherwise>
        				</xsl:choose> 
        			</xsl:otherwise>
        		</xsl:choose>
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
        			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						2
        					</xsl:otherwise>
        				</xsl:choose>
        			</xsl:when>
        			<xsl:otherwise>
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						2
        					</xsl:otherwise>
        				</xsl:choose> 
        			</xsl:otherwise>
        		</xsl:choose>
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
	          				<xsl:when test="obligation = 'true'">
	          				0
	          				</xsl:when>
	          				<xsl:otherwise>
	          				1
	          				</xsl:otherwise>
	          			</xsl:choose>	
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@vat_rate" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@amount" />
	    	</fo:block>
	    </fo:table-cell>
	</fo:table-row>
  </xsl:template>
  
  
  <xsl:template name="recordsOfMigel">
  	<fo:table-row height="10pt"  >
	    <fo:table-cell number-columns-spanned="18"  padding-top="5pt" padding-left="50pt">
	    	<fo:block   font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="." />
	    	</fo:block>
	    </fo:table-cell>
	   </fo:table-row> 
	   <fo:table-row>
	    <fo:table-cell>
	    	<fo:block   font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		
	    		<xsl:call-template name="FormatDate">
	         <xsl:with-param name="DateTime" select="@date_begin"/>
	       </xsl:call-template>
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="center"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@tariff_type" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@code" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@ref_code" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@number" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@body_location" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@quantity" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit" />
	    		
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@scale_factor.mt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		
	    		<xsl:value-of select="@unit_factor" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit.tt" />
	    	</fo:block>
	    </fo:table-cell>
	     <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@scale_factor.tt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit_factor.tt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
        			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						1
        					</xsl:otherwise>
        				</xsl:choose>
        			</xsl:when>
        			<xsl:otherwise>
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						1
        					</xsl:otherwise>
        				</xsl:choose> 
        			</xsl:otherwise>
        		</xsl:choose>
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
        			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						2
        					</xsl:otherwise>
        				</xsl:choose>
        			</xsl:when>
        			<xsl:otherwise>
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						2
        					</xsl:otherwise>
        				</xsl:choose> 
        			</xsl:otherwise>
        		</xsl:choose>
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
	          				<xsl:when test="obligation = 'true'">
	          				0
	          				</xsl:when>
	          				<xsl:otherwise>
	          				1
	          				</xsl:otherwise>
	          			</xsl:choose>	
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@vat_rate" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@amount" />
	    	</fo:block>
	    </fo:table-cell>
	</fo:table-row>
  </xsl:template>
  
  <xsl:template name="recordsOfDrug">
  	<fo:table-row height="10pt"  >
	    <fo:table-cell number-columns-spanned="18"  padding-top="5pt" padding-left="50pt">
	    	<fo:block   font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="." />
	    	</fo:block>
	    </fo:table-cell>
	   </fo:table-row> 
	   <fo:table-row>
	    <fo:table-cell>
	    	<fo:block   font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		
	    		<xsl:call-template name="FormatDate">
	         <xsl:with-param name="DateTime" select="@date_begin"/>
	       </xsl:call-template>
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="center"  font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@tariff_type" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@code" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@ref_code" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@number" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@body_location" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@quantity" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit" />
	    		
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@scale_factor.mt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit_factor" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit.tt" />
	    	</fo:block>
	    </fo:table-cell>
	     <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@scale_factor.tt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@unit_factor.tt" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
        			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						1
        					</xsl:otherwise>
        				</xsl:choose>
        			</xsl:when>
        			<xsl:otherwise>
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						1
        					</xsl:otherwise>
        				</xsl:choose> 
        			</xsl:otherwise>
        		</xsl:choose>
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
        			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_garant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						2
        					</xsl:otherwise>
        				</xsl:choose>
        			</xsl:when>
        			<xsl:otherwise>
        				<xsl:choose> 
        					<xsl:when test="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@ean_party = /invoice:request/invoice:invoice/invoice:tiers_payant/invoice:provider/@ean_party">
        						1
        					</xsl:when>
        					<xsl:otherwise>
        						2
        					</xsl:otherwise>
        				</xsl:choose> 
        			</xsl:otherwise>
        		</xsl:choose>
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:choose>
	          				<xsl:when test="obligation = 'true'">
	          				0
	          				</xsl:when>
	          				<xsl:otherwise>
	          				1
	          				</xsl:otherwise>
	          			</xsl:choose>	
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@vat_rate" />
	    	</fo:block>
	    </fo:table-cell>
	    <fo:table-cell>
	    	<fo:block text-align="right" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
	    		<xsl:value-of select="@amount" />
	    	</fo:block>
	    </fo:table-cell>
	</fo:table-row>
  </xsl:template>
</xsl:stylesheet>