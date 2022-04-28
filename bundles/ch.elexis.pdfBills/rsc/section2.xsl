<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:invoice="http://www.xmlData.ch/xmlInvoice/XSD">
	<xsl:param name="printerType" select="''"></xsl:param>
	<xsl:param name="leftMargin" select="''" />
	<xsl:param name="rightMargin" select="''" />
	<xsl:param name="topMargin" select="''" />
	<xsl:param name="bottomMargin" select="''" />
	<xsl:param name="besrMarginVertical" select="''" />
	<xsl:param name="besrMarginHorizontal" select="''" />
	<xsl:param name="amountReminders" select="''" />
	<xsl:param name="amountTotal" select="''" />
	<xsl:param name="amountDue" select="''" />
	<xsl:template match="invoice:*">
		<xsl:variable name="pType" select="$printerType" />
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format"
			font-family="OCRB">
			<fo:layout-master-set>
				<!-- margin-right für Abstand Codierzeile-rechter Papierrand. margin-bottom 
					für Abstand Codierzeile-Papierunterrand. margin-left = globaler Abstand von 
					links für Text und EZ -->
				<fo:simple-page-master master-name="LD2-S"
					page-height="29.7cm" page-width="21cm" margin-top="{$topMargin}"
					margin-bottom="{$bottomMargin}" margin-left="{$leftMargin}"
					margin-right="{$rightMargin}">
					<fo:region-body margin-top="1.5cm" margin-bottom="0.9cm" />
					<fo:region-before extent="2.5cm" />
					<fo:region-after extent="{$besrMarginVertical}" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="LD2-S">
				<fo:static-content flow-name="xsl-region-before">
					<!-- ganzer Block mit margin-left=0.5cm eingerückt von links her -->
					<fo:table table-layout="fixed" width="100%" margin-left="0.5cm">
						<fo:table-column column-width="100%" text-align="center" />
						<fo:table-body font-size="12px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left"
										font-family="tahoma,arial,helvetica,sans-serif" font-size="10px"
										color="#000000">
										Dr. med.
										<xsl:choose>
											<xsl:when
												test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:givenname" />
												<fo:inline> &#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:familyname" />
												<fo:inline> &#160;</fo:inline>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:givenname" />
												<fo:inline> &#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:familyname" />
												<fo:inline> &#160;</fo:inline>
											</xsl:otherwise>
										</xsl:choose>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left"
										font-family="tahoma,arial,helvetica,sans-serif" font-size="10px"
										color="#000000">
										<xsl:choose>
											<xsl:when
												test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@specialty"></xsl:value-of>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@specialty"></xsl:value-of>
											</xsl:otherwise>
										</xsl:choose>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left"
										font-family="tahoma,arial,helvetica,sans-serif" font-size="8px"
										color="#000000">
										<xsl:choose>
											<xsl:when
												test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:street" />
												,
												<fo:inline> &#160;</fo:inline>
												CH-
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:zip" />
												<fo:inline> &#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:city" />
												<fo:inline> &#160;</fo:inline>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:street" />
												,
												<fo:inline> &#160;</fo:inline>
												CH-
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:zip" />
												<fo:inline> &#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:city" />
												<fo:inline> &#160;</fo:inline>
											</xsl:otherwise>
										</xsl:choose>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left"
										font-family="tahoma,arial,helvetica,sans-serif" font-size="8px"
										color="#000000">
										<xsl:choose>
											<xsl:when
												test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
												Tel.
												<fo:inline> &#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:telecom/invoice:phone" />
												,
												<fo:inline> &#160;</fo:inline>
												Fax
												<fo:inline> &#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:telecom/invoice:fax" />
											</xsl:when>
											<xsl:otherwise>
												Tel.
												<fo:inline> &#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:telecom/invoice:phone" />
												,
												<fo:inline> &#160;</fo:inline>
												Fax
												<fo:inline> &#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:telecom/invoice:fax" />
											</xsl:otherwise>
										</xsl:choose>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left"
										font-family="tahoma,arial,helvetica,sans-serif" font-size="8px"
										color="#000000">
										<xsl:choose>
											<xsl:when
												test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
												E-Mail
												<fo:inline> &#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:online/invoice:email" />
												,
												<fo:inline> &#160;</fo:inline>
												Web
												<fo:inline> &#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:online/invoice:url" />
											</xsl:when>
											<xsl:otherwise>
												E-Mail
												<fo:inline> &#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:online/invoice:email" />
												,
												<fo:inline> &#160;</fo:inline>
												Web
												<fo:inline> &#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:online/invoice:url" />
											</xsl:otherwise>
										</xsl:choose>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left"
										font-family="tahoma,arial,helvetica,sans-serif" font-size="8px"
										color="#000000">
										<xsl:choose>
											<xsl:when
												test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
												GLN:
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@ean_party" />
												,
												<fo:inline> &#160;</fo:inline>
												ZSR:
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@zsr" />
											</xsl:when>
											<xsl:otherwise>
												GLN:
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@ean_party" />
												,
												<fo:inline> &#160;</fo:inline>
												ZSR:
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@zsr" />
											</xsl:otherwise>
										</xsl:choose>
										<xsl:choose>
											<xsl:when
												test="count(/invoice:request/invoice:invoice/invoice:detail/invoice:ivg) > 0">
												,
												<fo:inline> &#160;</fo:inline>
												NIF:
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:detail/invoice:ivg/@nif" />
											</xsl:when>
											<xsl:otherwise>

											</xsl:otherwise>
										</xsl:choose>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

						</fo:table-body>
					</fo:table>
				</fo:static-content>

				<fo:static-content flow-name="xsl-region-after">
					<fo:block font-family="OCRB" font-size="10px" text-align="right"
						margin-right="{$besrMarginHorizontal}">
						<xsl:choose>
							<xsl:when
								test="count(/invoice:request/invoice:invoice/invoice:esr9) > 0">
								<xsl:value-of
									select="/invoice:request/invoice:invoice/invoice:esr9/@coding_line" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of
									select="/invoice:request/invoice:invoice/invoice:esr6/@coding_line" />
							</xsl:otherwise>
						</xsl:choose>
					</fo:block>
				</fo:static-content>

				<fo:flow flow-name="xsl-region-body">
					<fo:table table-layout="fixed" width="100%"
						border-collapse="collapse">
						<fo:table-column column-width="55%" />
						<fo:table-column column-width="45%" />
						<fo:table-body font-size="9px"
							font-family="tahoma,arial,helvetica,sans-serif">



							<fo:table-row>
								<fo:table-cell padding-right="100pt">
									<fo:block linefeed-treatment="preserve">
										<fo:inline> &#160;</fo:inline>
									</fo:block>
									<fo:block linefeed-treatment="preserve">
										<fo:inline> &#160;</fo:inline>
									</fo:block>
									<fo:block linefeed-treatment="preserve">
										<fo:inline> &#160;</fo:inline>
									</fo:block>
									<fo:block linefeed-treatment="preserve">
										<fo:inline> &#160;</fo:inline>
									</fo:block>
									<!-- padding-left = Abstand zu linkem, grauen Rand des Kästchen 
										ganzer Block mit margin-left=1.0cm eingerückt von links her linefeed-treatment="preserve" -->
									<fo:block linefeed-treatment="preserve"
										background-color="#D3D3D3" margin-left="1.0cm" padding-left="2pt">
										Für: <xsl:choose>
											<xsl:when
												test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:patient/invoice:person/invoice:familyname" />
												<fo:inline>&#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:patient/invoice:person/invoice:givenname" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:patient/invoice:person/invoice:familyname" />
												<fo:inline>&#160;</fo:inline>
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:patient/invoice:person/invoice:givenname" />
											</xsl:otherwise>
										</xsl:choose>, <xsl:choose>
											<xsl:when
												test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
												<xsl:call-template name="FormatDate">
													<xsl:with-param name="DateTime"
														select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:patient/@birthdate" />
												</xsl:call-template>
											</xsl:when>
											<xsl:otherwise>
												<xsl:call-template name="FormatDate">
													<xsl:with-param name="DateTime"
														select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:patient/@birthdate" />
												</xsl:call-template>
											</xsl:otherwise>
										</xsl:choose>
										Rechnungs-Nummer <xsl:value-of select="substring(/invoice:request/invoice:invoice/@invoice_id,string-length(/invoice:request/invoice:invoice/@invoice_id) - 5)" />
										Rechnungs-Datum: <xsl:call-template name="FormatDate">
											<xsl:with-param name="DateTime"
												select="/invoice:request/invoice:invoice/@invoice_date" />
										</xsl:call-template>
										Behandlungen von: <xsl:call-template name="FormatDate">
											<xsl:with-param name="DateTime"
												select="/invoice:request/invoice:invoice/invoice:detail/@date_begin" />
										</xsl:call-template>
										Behandlungen bis: <xsl:call-template name="FormatDate">
											<xsl:with-param name="DateTime"
												select="/invoice:request/invoice:invoice/invoice:detail/@date_end" />
										</xsl:call-template>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding-right="50pt">
									<fo:block linefeed-treatment="preserve" margin-left="1.0cm">
										<fo:inline> &#160;</fo:inline>
									</fo:block>
									<!-- Adressat für Fenstercouvert -->
									<fo:block linefeed-treatment="preserve" padding-top="5pt"
										padding-left="5pt" border-top-width="0.5pt" border-left-width="0.5pt"
										border-right-width="0.5pt" border-bottom-width="0.5pt"
										border-left-color="#D3D3D3" border-right-color="#D3D3D3"
										border-top-color="#D3D3D3" border-bottom-color="#D3D3D3"
										border-top-style="solid" border-bottom-style="solid"
										border-left-style="solid" border-right-style="solid">
										<fo:inline font-style="italic" font-size="8px">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:familyname" />, <fo:inline> &#160;</fo:inline>
													<xsl:value-of
														select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:street" />, <fo:inline> &#160;</fo:inline>
													<xsl:value-of
														select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:zip" />
													<fo:inline> &#160;</fo:inline>
													<xsl:value-of
														select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:city" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:familyname" />, <fo:inline> &#160;</fo:inline>
													<xsl:value-of
														select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:street" />, <fo:inline> &#160;</fo:inline>
													<xsl:value-of
														select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:zip" />
													<fo:inline> &#160;</fo:inline>
													<xsl:value-of
														select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:city" />
												</xsl:otherwise>
											</xsl:choose>
										</fo:inline>

										<fo:inline> &#160;&#10;&#10;</fo:inline>

										<!-- Adressat Anrede eingefügt mit salutation -->
										<xsl:choose>
											<xsl:when
												test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
												<xsl:value-of
													select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/@salutation" />
												<fo:inline> &#10;</fo:inline>
          				</xsl:when>
          				<xsl:otherwise>
           				 	  <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/@salutation" /><fo:inline> &#10;</fo:inline>
          				</xsl:otherwise>
            	 </xsl:choose>
   		
              		<fo:inline> &#160;&#10;</fo:inline>
              		  		
		    		<xsl:choose>
           				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
            				<xsl:choose>
            				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
            				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:companyname" /><fo:inline> &#10;</fo:inline>
            				 	</xsl:when>
              					<xsl:otherwise>
              						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:givenname" /><fo:inline> &#160;</fo:inline>
            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:familyname" /><fo:inline> &#10;&#13;</fo:inline>
            					</xsl:otherwise>
              				</xsl:choose>  
            			</xsl:when>
            			<xsl:otherwise>
            				<xsl:choose>
            				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company) > 0">
            				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company/invoice:companyname" /><fo:inline> &#10;</fo:inline>
            				 	</xsl:when>
              					<xsl:otherwise>
              						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:givenname" /><fo:inline> &#160;</fo:inline>
            				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:familyname" /><fo:inline> &#10;</fo:inline>
            				 	</xsl:otherwise>
              				</xsl:choose>  
            			</xsl:otherwise>
             		</xsl:choose>
             		
             		<fo:inline> &#160;&#10;</fo:inline>
             		
             		<xsl:choose>
           				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
            				<xsl:choose>
            				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
            				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:street" /><fo:inline> &#10;</fo:inline>
            				 	</xsl:when>
              					<xsl:otherwise>
              						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:street" /><fo:inline> &#10;</fo:inline>
            					</xsl:otherwise>
              				</xsl:choose>  
            			</xsl:when>
            			<xsl:otherwise>
            				<xsl:choose>
            				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company) > 0">
            				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company/invoice:postal/invoice:street" /><fo:inline> &#10;</fo:inline>
            				 	</xsl:when>
              					<xsl:otherwise>
              						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:street" /><fo:inline> &#10;</fo:inline>
            					</xsl:otherwise>
              				</xsl:choose>  
            			</xsl:otherwise>
             		</xsl:choose>
             		
             		<fo:inline> &#160;&#10;</fo:inline>
             		
             		<xsl:choose>
           				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
            				<xsl:choose>
            				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
            				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
              						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:city" />
            				 	</xsl:when>
              					<xsl:otherwise>
              						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
              						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:city" />
              					</xsl:otherwise>
              				</xsl:choose>  
            			</xsl:when>
            			<xsl:otherwise>
            				<xsl:choose>
            				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company) > 0">
            				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
            				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company/invoice:postal/invoice:city" />
            				 	</xsl:when>
              					<xsl:otherwise>
              				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
            				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:city" />
              					</xsl:otherwise>
              				</xsl:choose>  
            			</xsl:otherwise>
             		</xsl:choose>
             		
             	</fo:block>
	  		  </fo:table-cell>
  		     </fo:table-row>
		    
	  	    </fo:table-body> 	
	  <!-- ganzer Block mit margin-left=0.5cm eingerückt von links her
		-->
		</fo:table>
  	    <fo:table table-layout="fixed" width="100%"  border-collapse="collapse" margin-left="0.5cm" >
		    <fo:table-column column-width="100%" />
		    <fo:table-body font-size="9px" font-family="tahoma,arial,helvetica,sans-serif">
		     <fo:table-row>
  		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	
                <fo:block font-weight="bold">
		    		Honorar-Rechnung
		    	</fo:block>
		    	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
	  		  </fo:table-cell>
  		     </fo:table-row>
		     <fo:table-row>
  		      <fo:table-cell>
                <fo:block >
		    		Diese Seite ist für Ihre Unterlagen bestimmt. Den beiliegenden Rückforderungsbeleg senden Sie an Ihre Krankenkasse. 
		    	</fo:block>
		    	<fo:block >
		    		Bitte bezahlen Sie diese Rechnung mit dem orangen Einzahlungsschein innert 30 Tagen.
		    	</fo:block>
	  		  </fo:table-cell>
  		     </fo:table-row>
  		     <fo:table-row>
  		      <fo:table-cell>
                <fo:block >
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
	  		  </fo:table-cell>
  		     </fo:table-row>
  		      <fo:table-row>
  		      <fo:table-cell>
                <fo:block >
		    		Mit freundlichen Grüssen,  Dr. med. <xsl:choose>
             				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
             					 <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:givenname" /><fo:inline> &#160;</fo:inline>	
             					 <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:familyname" /><fo:inline> &#160;</fo:inline>
             				</xsl:when>
             				<xsl:otherwise>
             					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:givenname" /><fo:inline> &#160;</fo:inline>	
	             				<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:familyname" /><fo:inline> &#160;</fo:inline>
             				</xsl:otherwise>
             			</xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
  		     </fo:table-row>
  		     <fo:table-row>
  		      <fo:table-cell>
                <fo:block >
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	<fo:block >
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>

	  		  </fo:table-cell>
  		     </fo:table-row>
  		    </fo:table-body>
  		</fo:table>    
  		 <!-- ganzer Block mit margin-left=0.5cm eingerückt von links her
  		 Original erster table-column 35%, zweiter table-column 65%
		-->
  		<fo:table table-layout="fixed" width="100%"  border-collapse="collapse" margin-left="0.5cm" >
  			<fo:table-column column-width="25%" />
  			<fo:table-column column-width="75%" />
		    <fo:table-body font-size="9px" font-family="tahoma,arial,helvetica,sans-serif">    
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<!--  
  		        <fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	-->
                <fo:block >
		    		Leistungen nach Tarmed:
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	
  		        <fo:block linefeed-treatment="preserve">
		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_tarmed" /><fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	
		      </fo:table-cell>	
  		     </fo:table-row>
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<!--  
  		        <fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	-->
                <fo:block >
		    		Medikamente:
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	
  		        <fo:block linefeed-treatment="preserve">
		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_drug" /><fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	
		      </fo:table-cell>	
  		     </fo:table-row>
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<!--  
  		        <fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	-->
                <fo:block >
		    		Leistungen nach Labortarif:
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	
  		        <fo:block linefeed-treatment="preserve">
		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_lab" /><fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	
		      </fo:table-cell>	
  		     </fo:table-row>
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<!--  
  		        <fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	-->
                <fo:block >
		    		Artikel aus MiGeL:
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	
  		        <fo:block linefeed-treatment="preserve">
		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_migel" /><fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	
		      </fo:table-cell>	
  		     </fo:table-row>
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<!--  
  		        <fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	-->
                <fo:block >
		    		Physiotherapie-Leistungen:
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	
  		        <fo:block linefeed-treatment="preserve">
		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_physio" /><fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	
		      </fo:table-cell>	
  		     </fo:table-row>
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<!--  
  		        <fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	-->
                <fo:block >
		    		Sonstige Leistungen:
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	
  		        <fo:block linefeed-treatment="preserve">
		    		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:balance/@amount_unclassified" /><fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	
		      </fo:table-cell>	
  		     </fo:table-row>
  		     <xsl:if test="string-length($amountReminders) > 0">
			<fo:table-row>
  		      <fo:table-cell>
  		      	<!--  
  		        <fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	-->
                <fo:block >
		    		Mahngebühr:
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	
  		        <fo:block linefeed-treatment="preserve">
		    		<xsl:value-of select="$amountReminders" /><fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	
		      </fo:table-cell>	
  		     </fo:table-row>
			</xsl:if>
  		     
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<!--  
  		        <fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	-->
                <fo:block >
		    		-----------------------------------------
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	
  		        <fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	
		      </fo:table-cell>	
  		     </fo:table-row>
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<!--  
  		        <fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	-->
                <fo:block >
		    		Summe
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	
  		        <fo:block linefeed-treatment="preserve">
		    		<xsl:value-of select="$amountTotal" /><fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	
		      </fo:table-cell>	
  		     </fo:table-row>
	  	    </fo:table-body> 	
		</fo:table>
		
		<fo:table table-layout="fixed" width="100%"  border-collapse="collapse" >
		    <fo:table-column column-width="100%" />
		    <fo:table-body font-size="9px" font-family="tahoma,arial,helvetica,sans-serif">
		     <fo:table-row >
  		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
			  </fo:table-cell>	
  		     </fo:table-row>
  		     <fo:table-row height="28mm">
  		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		      </fo:table-cell>	
  		     </fo:table-row>
	  	    </fo:table-body> 	
		</fo:table>    
		
		<fo:table table-layout="fixed" width="100%"  border-collapse="collapse" >
			<fo:table-column column-width="30%" />
			<fo:table-column column-width="30%" />
			<fo:table-column column-width="20%" />
			<fo:table-column column-width="20%" />
		    <fo:table-body font-size="9px" font-family="tahoma,arial,helvetica,sans-serif">    
  		    
  		     
  		     <fo:table-row>
  		      <fo:table-cell>
  		        <fo:block >
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    		
		    	</fo:block>
	  		  </fo:table-cell>
  		     </fo:table-row>
  		     
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<fo:block >
		    	 <xsl:choose>
       				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:esr9) > 0">
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr9/invoice:bank/invoice:company/invoice:companyname" /> 
       				</xsl:when>
       				<xsl:otherwise>
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr6/invoice:bank/invoice:company/invoice:companyname" /> 
       				</xsl:otherwise>
       			 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    	 <xsl:choose>
       				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:esr9) > 0">
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr9/invoice:bank/invoice:company/invoice:companyname" /> 
       				</xsl:when>
       				<xsl:otherwise>
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr6/invoice:bank/invoice:company/invoice:companyname" /> 
       				</xsl:otherwise>
       			 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    	</fo:block>
	  		  </fo:table-cell>
  		     </fo:table-row>
  		     
  		      <fo:table-row>
  		      <fo:table-cell>
  		      	<fo:block >
		    	 <xsl:choose>
       				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:esr9) > 0">
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr9/invoice:bank/invoice:company/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr9/invoice:bank/invoice:company/invoice:postal/invoice:city" /> 
       				</xsl:when>
       				<xsl:otherwise>
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr6/invoice:bank/invoice:company/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr6/invoice:bank/invoice:company/invoice:postal/invoice:city" /> 
       				</xsl:otherwise>
       			 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    	 <xsl:choose>
       				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:esr9) > 0">
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr9/invoice:bank/invoice:company/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr9/invoice:bank/invoice:company/invoice:postal/invoice:city" /> 
       				</xsl:when>
       				<xsl:otherwise>
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr6/invoice:bank/invoice:company/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr6/invoice:bank/invoice:company/invoice:postal/invoice:city" /> 
       				</xsl:otherwise>
       			 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
	  		  </fo:table-cell>
  		     </fo:table-row>
  		     
  		     
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		      </fo:table-cell>
		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		      </fo:table-cell>
		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		      </fo:table-cell>
		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		      </fo:table-cell>	
  		     </fo:table-row>
  		     
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<fo:block >
		    	 <xsl:choose>
          				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
          					 <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/@salutation" /><fo:inline> &#160;</fo:inline>
          				</xsl:when>
          				<xsl:otherwise>
           				 	  <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/@salutation" /><fo:inline> &#160;</fo:inline>
          				</xsl:otherwise>
            	 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    	 <xsl:choose>
          				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
          					  <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/@salutation" /><fo:inline> &#160;</fo:inline>
          				</xsl:when>
          				<xsl:otherwise>
           				 	 <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/@salutation" /><fo:inline> &#160;</fo:inline>
          				</xsl:otherwise>
            	 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    		
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    		
		    	</fo:block>
	  		  </fo:table-cell>
  		     </fo:table-row>
  		     
  		     
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<fo:block >
		    	 <xsl:choose>
          				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
          					 Dr. med. <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:givenname" /><fo:inline> &#160;</fo:inline>
          					 <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:familyname" /><fo:inline> &#160;</fo:inline>
          				</xsl:when>
          				<xsl:otherwise>
           				 	  Dr. med. <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:givenname" /><fo:inline> &#160;</fo:inline>
           				 	  <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:familyname" /><fo:inline> &#160;</fo:inline>
          				</xsl:otherwise>
            	 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    	 <xsl:choose>
          				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
          					  Dr. med. <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:givenname" /><fo:inline> &#160;</fo:inline>
          					  <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:familyname" /><fo:inline> &#160;</fo:inline>
          				</xsl:when>
          				<xsl:otherwise>
           				 	  Dr. med. <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:givenname" /><fo:inline> &#160;</fo:inline>
           				 	  <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:familyname" /><fo:inline> &#160;</fo:inline>
          				</xsl:otherwise>
            	 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    		
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    		
		    	</fo:block>
	  		  </fo:table-cell>
  		     </fo:table-row>
  		     
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<fo:block >
                <xsl:choose>
             				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
             					 <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@specialty"></xsl:value-of>
             				</xsl:when>
             				<xsl:otherwise>
	             				  <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@specialty"></xsl:value-of>
             				</xsl:otherwise>
             	</xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
                	<xsl:choose>
             				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
             					 <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/@specialty"></xsl:value-of>
             				</xsl:when>
             				<xsl:otherwise>
	             				  <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/@specialty"></xsl:value-of>
             				</xsl:otherwise>
              </xsl:choose>		    	
              </fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    		
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    		
		    	</fo:block>
	  		  </fo:table-cell>
  		     </fo:table-row>
  		     
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<fo:block >
		    	 <xsl:choose>
        				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:street" />
        				</xsl:when>
        				<xsl:otherwise>
         					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:street" />
         				</xsl:otherwise>
          		 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    	 <xsl:choose>
        				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:street" />
        				</xsl:when>
        				<xsl:otherwise>
         					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:street" />
         				</xsl:otherwise>
          		 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell number-columns-spanned="2">
  		      	<fo:block text-align="right" margin-right="0.8cm">
  		      		<xsl:if test="$pType != 'P1'">
			    		<xsl:choose>
		       				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:esr9) > 0">
		       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr9/@reference_number" /> 
		       				</xsl:when>
		       				<xsl:otherwise>
		       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr6/@reference_number" /> 
		       				</xsl:otherwise>
		       			</xsl:choose>
	       			</xsl:if>
		    	</fo:block>
	  		  </fo:table-cell>
	  		 </fo:table-row>
  		     
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<fo:block >
		    	 <xsl:choose>
        				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
        					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:city" /><fo:inline> &#160;</fo:inline>
        				</xsl:when>
        				<xsl:otherwise>
         					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
         					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:city" /><fo:inline> &#160;</fo:inline>
         				</xsl:otherwise>
          		 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block >
		    	 <xsl:choose>
        				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
        					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
        					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:city" /><fo:inline> &#160;</fo:inline>
        				</xsl:when>
        				<xsl:otherwise>
         					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
         					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:city" /><fo:inline> &#160;</fo:inline>
         				</xsl:otherwise>
          		 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell number-columns-spanned="2">
  		      	<fo:block text-align="right" margin-right="0.8cm">
  		      		<xsl:if test="$pType = 'P1'">
			    		<xsl:choose>
		       				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:esr9) > 0">
		       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr9/@reference_number" /> 
		       				</xsl:when>
		       				<xsl:otherwise>
		       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr6/@reference_number" /> 
		       				</xsl:otherwise>
		       			</xsl:choose>
	       			</xsl:if>
		    	</fo:block>
	  		  </fo:table-cell>
  		     </fo:table-row>
  		     
  		     
  		     
  		     
  		     
  		     <fo:table-row>
  		      <fo:table-cell>
  		      	<fo:block margin-left="1.8cm">
		    	 <xsl:choose >
       				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:esr9) > 0">
       					<fo:inline> &#160;</fo:inline><fo:inline> &#160;</fo:inline><fo:inline> &#160;</fo:inline>
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr9/@participant_number" /> 
       				</xsl:when>
       				<xsl:otherwise>
       					<fo:inline> &#160;</fo:inline><fo:inline> &#160;</fo:inline><fo:inline> &#160;</fo:inline>
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr6/@participant_number" /> 
       				</xsl:otherwise>
       			 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell>
	  		  <fo:table-cell>
  		      	<fo:block margin-left="2.0cm">
		    	 <xsl:choose>
       				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:esr9) > 0">
       					<fo:inline> &#160;</fo:inline><fo:inline> &#160;</fo:inline><fo:inline> &#160;</fo:inline>
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr9/@participant_number" /> 
       				</xsl:when>
       				<xsl:otherwise>
       					<fo:inline> &#160;</fo:inline><fo:inline> &#160;</fo:inline><fo:inline> &#160;</fo:inline>
       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr6/@participant_number" /> 
       				</xsl:otherwise>
       			 </xsl:choose>
		    	</fo:block>
	  		  </fo:table-cell >
	  		  <fo:table-cell number-columns-spanned="2">
	  		  	<fo:block text-align="right" margin-right="0.8cm">
		    		
		    	</fo:block>
  		      	
	  		  </fo:table-cell>
	  		  
  		     </fo:table-row>
  		     
  		     <fo:table-row height="0.5cm">
  		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		      </fo:table-cell>
		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		      </fo:table-cell>
		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		      </fo:table-cell>
		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		      </fo:table-cell>	
  		     </fo:table-row>
  		     
  		     	<fo:table-row height="0.5cm">
	  		      <fo:table-cell>
	 		   	    <fo:block linefeed-treatment="preserve" text-align="right" margin-right="1.1cm">
		    		  <xsl:value-of select="substring-before($amountDue, '.')" /><fo:inline> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:inline>
		    		  <xsl:value-of select="substring-after($amountDue, '.')" />
		    	    </fo:block>
			      </fo:table-cell>
			      <fo:table-cell>
	  		      	<fo:block linefeed-treatment="preserve" text-align="right" margin-right="0.9cm">
		    		  <xsl:value-of select="substring-before($amountDue, '.')" /><fo:inline> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:inline>
		    		  <xsl:value-of select="substring-after($amountDue, '.')" />
		    	    </fo:block>
			      </fo:table-cell>
			      <fo:table-cell>
	  		      	<fo:block linefeed-treatment="preserve">
			    		<fo:inline> &#160;</fo:inline>
			    	</fo:block>
			      </fo:table-cell>
			      <fo:table-cell>
	  		      	<fo:block linefeed-treatment="preserve">
			    		<fo:inline> &#160;</fo:inline>
			    	</fo:block>
			      </fo:table-cell>	
	  		    </fo:table-row>	
  		     <!-- amount_due entspricht den Beträgen in den Kästchen
  		     erster Block kleiner Abschnitt EZ links; margin-right = Abstand von rechts
  		     zweiter Block grosser Abschnitt EZ rechts; margin-right = Abstand von rechts	    		
			  -->
  		     <fo:table-row margin-top="0.3cm">
	 		  <fo:table-cell>
	  		     <fo:block linefeed-treatment="preserve">
			       <fo:inline> &#160;</fo:inline>
			     </fo:block>
		      </fo:table-cell>
		      <fo:table-cell>
	  		    <fo:block linefeed-treatment="preserve">
			      <fo:inline> &#160;</fo:inline>
			    </fo:block>
		      </fo:table-cell>
		      <fo:table-cell number-columns-spanned="2">
	 		    <fo:block >
		    		
		    	</fo:block>
		      </fo:table-cell>
		      	
	 		 </fo:table-row>
	 		 
	 		 <!--  
	 		 <fo:table-row>
  		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		      </fo:table-cell>
		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		      </fo:table-cell>
		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		      </fo:table-cell>
		      <fo:table-cell>
  		      	<fo:block linefeed-treatment="preserve">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		      </fo:table-cell>	
  		     </fo:table-row>
  		     -->
  		     
	 		 <fo:table-row margin-top="0.5cm">
	 		  <fo:table-cell number-columns-spanned="2">
	 		  	<!--  
	 		   	<fo:block linefeed-treatment="preserve" text-align="right" margin-right="2.3cm">
		    		<fo:inline> &#160;</fo:inline>
		    	</fo:block>
		    	-->
		    	
		    	
		    	<!--  Dieser code-Block mit reference_number entspricht dem kleinen linken Abschnitt des EZ
		    	-->
		    	<fo:block linefeed-treatment="preserve" padding-top="5pt" padding-left="5pt"  margin-right="2.0cm">
		    		
		    		<fo:inline> &#160;&#10;</fo:inline>
					<xsl:choose>
		       				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:esr9) > 0">
		       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr9/@reference_number" /> 
		       				</xsl:when>
		       				<xsl:otherwise>
		       					<xsl:value-of select="/invoice:request/invoice:invoice/invoice:esr6/@reference_number" /> 
		       				</xsl:otherwise>
		       		</xsl:choose>
		       		<fo:inline> &#160;&#10;</fo:inline>

		    		 <xsl:choose>
          				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
          					 <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/@salutation" /><fo:inline> &#10;</fo:inline>
          				</xsl:when>
          				<xsl:otherwise>
           				 	  <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/@salutation" /><fo:inline> &#10;</fo:inline>
          				</xsl:otherwise>
            	 	 </xsl:choose>  		
		    		
		    		<fo:inline> &#160;&#10;</fo:inline>
			    	<xsl:choose>
		         			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		          				<xsl:choose>
		          				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:companyname" /><fo:inline> &#10;</fo:inline>
		          				 	</xsl:when>
		            					<xsl:otherwise>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:givenname" /><fo:inline> &#160;</fo:inline>
		          						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:familyname" />
		          					</xsl:otherwise>
		            				</xsl:choose>  
		          			</xsl:when>
		          			<xsl:otherwise>
		          				<xsl:choose>
		          				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company) > 0">
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company/invoice:companyname" /><fo:inline> &#10;</fo:inline>
		          				 	</xsl:when>
		            					<xsl:otherwise>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:givenname" /><fo:inline> &#160;</fo:inline>
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:familyname" />
		          				 	</xsl:otherwise>
		            				</xsl:choose>  
		          			</xsl:otherwise>
		           	</xsl:choose>
		           	<fo:inline> &#160;&#10;</fo:inline>
		            <xsl:choose>
		         				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		          				<xsl:choose>
		          				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:street" /><fo:inline> &#10;</fo:inline>
		          				 	</xsl:when>
		            					<xsl:otherwise>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:street" /><fo:inline> &#10;</fo:inline>
		          					</xsl:otherwise>
		            				</xsl:choose>  
		          			</xsl:when>
		          			<xsl:otherwise>
		          				<xsl:choose>
		          				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company) > 0">
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company/invoice:postal/invoice:street" /><fo:inline> &#10;</fo:inline>
		          				 	</xsl:when>
		            					<xsl:otherwise>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:street" /><fo:inline> &#10;</fo:inline>
		          					</xsl:otherwise>
		            				</xsl:choose>  
		          			</xsl:otherwise>
		            </xsl:choose>
		            <fo:inline> &#160;&#10;</fo:inline>
		            <xsl:choose>
		         			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		          				<xsl:choose>
		          				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:city" />
		          				 	</xsl:when>
		            					<xsl:otherwise>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:city" />
		            					</xsl:otherwise>
		            				</xsl:choose>  
		          			</xsl:when>
		          			<xsl:otherwise>
		          				<xsl:choose>
		          				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company) > 0">
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company/invoice:postal/invoice:city" />
		          				 	</xsl:when>
		            					<xsl:otherwise>
		            				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:city" />
		            					</xsl:otherwise>
		            				</xsl:choose>  
		          			</xsl:otherwise>
		            </xsl:choose>
		        </fo:block>    
		      </fo:table-cell>
		      <!--  Dieser code-Block mit reference_number entspricht dem grossen rechten Abschnitt des EZ
		    	-->
		      <fo:table-cell number-columns-spanned="2">
	 		    <fo:block linefeed-treatment="preserve" padding-top="5pt" padding-left="5pt"  margin-right="2cm">
	 		     <xsl:choose>
          				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
          					 <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/@salutation" /><fo:inline> &#10;</fo:inline>
          				</xsl:when>
          				<xsl:otherwise>
           				 	  <xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/@salutation" /><fo:inline> &#10;</fo:inline>
          				</xsl:otherwise>
            	 	 </xsl:choose>  
		    		<fo:inline> &#160;&#10;</fo:inline>
			    	<xsl:choose>
		         			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		          				<xsl:choose>
		          				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:companyname" /><fo:inline> &#10;</fo:inline>
		          				 	</xsl:when>
		            					<xsl:otherwise>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:givenname" /><fo:inline> &#160;</fo:inline>
		          						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:familyname" />
		          					</xsl:otherwise>
		            				</xsl:choose>  
		          			</xsl:when>
		          			<xsl:otherwise>
		          				<xsl:choose>
		          				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company) > 0">
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company/invoice:companyname" /><fo:inline> &#10;</fo:inline>
		          				 	</xsl:when>
		            					<xsl:otherwise>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:givenname" /><fo:inline> &#160;</fo:inline>
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:familyname" />
		          				 	</xsl:otherwise>
		            				</xsl:choose>  
		          			</xsl:otherwise>
		           	</xsl:choose>
		           	<fo:inline> &#160;&#10;</fo:inline>
		            <xsl:choose>
		         				<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		          				<xsl:choose>
		          				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:street" /><fo:inline> &#10;</fo:inline>
		          				 	</xsl:when>
		            					<xsl:otherwise>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:street" /><fo:inline> &#10;</fo:inline>
		          					</xsl:otherwise>
		            				</xsl:choose>  
		          			</xsl:when>
		          			<xsl:otherwise>
		          				<xsl:choose>
		          				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company) > 0">
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company/invoice:postal/invoice:street" /><fo:inline> &#10;</fo:inline>
		          				 	</xsl:when>
		            					<xsl:otherwise>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:street" /><fo:inline> &#10;</fo:inline>
		          					</xsl:otherwise>
		            				</xsl:choose>  
		          			</xsl:otherwise>
		            </xsl:choose>
		            <fo:inline> &#160;&#10;</fo:inline>
		            <xsl:choose>
		         			<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant) > 0">
		          				<xsl:choose>
		          				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:city" />
		          				 	</xsl:when>
		            					<xsl:otherwise>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
		            						<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:city" />
		            					</xsl:otherwise>
		            				</xsl:choose>  
		          			</xsl:when>
		          			<xsl:otherwise>
		          				<xsl:choose>
		          				 	<xsl:when test="count(/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company) > 0">
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:company/invoice:postal/invoice:city" />
		          				 	</xsl:when>
		            					<xsl:otherwise>
		            				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:zip" /><fo:inline> &#160;</fo:inline>
		          				 		<xsl:value-of select="/invoice:request/invoice:invoice/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:city" />
		            					</xsl:otherwise>
		            				</xsl:choose>  
		          			</xsl:otherwise>
		            </xsl:choose>
		            		
		        </fo:block>
		      </fo:table-cell>
		      
		      
		      	
	 		 </fo:table-row>
	 		 
	  	    </fo:table-body>
	  	</fo:table>
		
		
  		     
		
		
	   </fo:flow>
	   
     </fo:page-sequence>
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
</xsl:stylesheet>
