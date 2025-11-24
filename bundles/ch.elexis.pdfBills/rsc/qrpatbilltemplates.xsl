<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:invoice="http://www.forum-datenaustausch.ch/invoice">

	<xsl:template name="patbill_garant_salutation">
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
							Sehr geehrte Damen und Herren
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when
									test="string-length($guarantorPostal) > 1">
									Sehr geehrte Damen und Herren
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="PersonSalutation">
										<xsl:with-param name="Person"
											select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person" />
									</xsl:call-template>
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:familyname" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company) > 0">
							Sehr geehrte Damen und Herren
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when
									test="string-length($guarantorPostal) > 1">
									Sehr geehrte Damen und Herren
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="PersonSalutation">
										<xsl:with-param name="Person"
											select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:guarantor/invoice:person" />
									</xsl:call-template>
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:familyname" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
	</xsl:template>

	<xsl:template name="patbill_garant_address">
		<xsl:choose>
			<xsl:when
				test="string-length($guarantorPostal) > 1">
				<fo:block linefeed-treatment="preserve">
					<xsl:value-of select="$guarantorPostal" />			
				</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<!-- Adressat Anrede eingefügt mit salutation -->
				<fo:block>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/@salutation" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company/@salutation" />
						</xsl:otherwise>
					</xsl:choose>
				</fo:block>
				<xsl:choose>
					<xsl:when
						test="(string-length($guarantorLine) > 1 and count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0) or 
						(string-length($insuranceLine) > 1 and count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant) > 0)">
						<xsl:choose>
							<xsl:when
							test="(string-length($guarantorLine) > 1 and count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0)">
								<fo:block linefeed-treatment="preserve">
									<xsl:value-of select="$guarantorLine" />			
								</fo:block>
							</xsl:when>
							<xsl:otherwise>
								<fo:block linefeed-treatment="preserve">
									<xsl:value-of select="$insuranceLine" />			
								</fo:block>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<fo:block>
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
									<xsl:choose>
										<xsl:when
											test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
											<xsl:value-of
												select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:companyname" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of
												select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:familyname)" />
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when
											test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company) > 0">
											<xsl:value-of
												select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:companyname" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of
												select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:familyname)" />
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</xsl:otherwise>
				</xsl:choose>
				<fo:block>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:street" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:street" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company) > 0">
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:postal/invoice:street" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:street" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</fo:block>
				<fo:block>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:city)" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:city)" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company) > 0">
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:postal/invoice:city)" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:city)" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="patbill_45_services_overview">
		<fo:table table-layout="auto" width="8cm"
			border-collapse="collapse" font-size="8px"
			font-family="tahoma,arial,helvetica,sans-serif">
			<fo:table-column width="80%" />
			<fo:table-column width="20%" />
			<fo:table-body>
				<fo:table-row border-bottom-width="0.5pt"
					border-bottom-color="black" border-bottom-style="solid">
					<fo:table-cell>
						<fo:block font-weight="bold">
							Bereich
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-weight="bold">
							Total/CHF
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>
							Medizinisch
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service_ex[@tariff_type='001']/@amount)" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>
							Paramedizinisch
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service[@tariff_type='311']/@amount)" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>
							Medikamente
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service[@tariff_type='402']/@amount)
									+ sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service[@tariff_type='400']/@amount)
									+ sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service[@tariff_type='406']/@amount)" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>
							Labor, Migel
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service[@tariff_type='317']/@amount)
									+ sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service[@tariff_type='452']/@amount)" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>
							Übrige
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service[@tariff_type!='311' and @tariff_type!='317' and @tariff_type!='452' and @tariff_type!='400' and @tariff_type!='402' and @tariff_type!='406']/@amount)
									+ sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service_ex[@tariff_type!='001']/@amount)" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>

				<xsl:choose>
					<xsl:when
						test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
						<xsl:if
							test="string-length(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_reminder) > 0">
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										Gebühren
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right">
										<xsl:call-template name="FormatNumber">
											<xsl:with-param name="Number"
												select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_reminder" />
										</xsl:call-template>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</xsl:if>
		
						<fo:table-row border-top-width="0.5pt"
							border-top-color="black" border-top-style="solid">
							<fo:table-cell>
								<fo:block font-weight="bold">
									Gesamt-Total
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-weight="bold">
									<xsl:choose>
										<xsl:when
											test="string-length(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_reminder) > 0">
											<xsl:call-template name="FormatNumber">
												<xsl:with-param name="Number"
													select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount + /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_reminder" />
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="FormatNumber">
												<xsl:with-param name="Number"
													select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount" />
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-weight="bold">
									Anzahlung
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-weight="bold">
									<xsl:call-template name="FormatNumber">
										<xsl:with-param name="Number"
											select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_prepaid" />
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-weight="bold">
									Rechnungs-Total
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-weight="bold">
									<xsl:call-template name="FormatNumber">
										<xsl:with-param name="Number"
											select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_due" />
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>						
					</xsl:when>
					<xsl:otherwise>
						<xsl:if
							test="string-length(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount_reminder) > 0">
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										Gebühren
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right">
										<xsl:call-template name="FormatNumber">
											<xsl:with-param name="Number"
												select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount_reminder" />
										</xsl:call-template>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</xsl:if>
		
						<fo:table-row border-top-width="0.5pt"
							border-top-color="black" border-top-style="solid">
							<fo:table-cell>
								<fo:block font-weight="bold">
									Gesamt-Total
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-weight="bold">
									<xsl:choose>
										<xsl:when
											test="string-length(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount_reminder) > 0">
											<xsl:call-template name="FormatNumber">
												<xsl:with-param name="Number"
													select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount + /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount_reminder" />
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="FormatNumber">
												<xsl:with-param name="Number"
													select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount" />
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-weight="bold">
									Anzahlung
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-weight="bold">
									<xsl:call-template name="FormatNumber">
										<xsl:with-param name="Number" select="$amountPrepaid" />
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-weight="bold">
									Rechnungs-Total
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-weight="bold">
									<xsl:call-template name="FormatNumber">
										<xsl:with-param name="Number"
											select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount_due" />
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>	
					</xsl:otherwise>
				</xsl:choose>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template name="patbill_patient_info">
		<xsl:choose>
			<xsl:when
				test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
				<xsl:value-of
					select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:givenname, ', ')" />
				<xsl:call-template name="FormatDate">
					<xsl:with-param name="DateTime"
						select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/@birthdate" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of
					select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:givenname, ', ')" />
				<xsl:call-template name="FormatDate">
					<xsl:with-param name="DateTime"
						select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/@birthdate" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="patbill_bill_info">
		<fo:block>
			Rechnungs-Nummer
			<xsl:value-of
				select="number(substring(/invoice:request/invoice:payload/invoice:invoice/@request_id,string-length(/invoice:request/invoice:payload/invoice:invoice/@request_id) - 5))" />
		</fo:block>
		<fo:block>
			Rechnungs-Datum:
			<xsl:call-template name="FormatDate">
				<xsl:with-param name="DateTime"
					select="/invoice:request/invoice:payload/invoice:invoice/@request_date" />
			</xsl:call-template>
		</fo:block>
		<fo:block>
			Behandlungszeitraum:
			<xsl:call-template name="FormatDate">
				<xsl:with-param name="DateTime"
					select="/invoice:request/invoice:payload/invoice:body/invoice:treatment/@date_begin" />
			</xsl:call-template>
			-
			<xsl:call-template name="FormatDate">
				<xsl:with-param name="DateTime"
					select="/invoice:request/invoice:payload/invoice:body/invoice:treatment/@date_end" />
			</xsl:call-template>
		</fo:block>
	</xsl:template>

	<xsl:template name="patbill_biller_address">
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="string-length($billerLine) > 1">
					<fo:block linefeed-treatment="preserve">
						<xsl:value-of select="$billerLine" />			
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company) > 0">
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:companyname" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/@title, ' ')" />
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:familyname)" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company) > 0">
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:companyname" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/@title, ' ')" />
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:familyname)" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:value-of
						select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/@specialty"></xsl:value-of>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of
						select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/@specialty"></xsl:value-of>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:postal/invoice:street" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:street" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:postal/invoice:street" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:street" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company) > 0">
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:postal/invoice:city)" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:city)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company) > 0">
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:postal/invoice:city)" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:city)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company) > 0">
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:telecom/invoice:phone) > 0">
								Tel.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:telecom/invoice:phone" />
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:telecom/invoice:fax) > 0">
								Fax.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:telecom/invoice:fax" />
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:telecom/invoice:phone) > 0">
								Tel.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:telecom/invoice:phone" />
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:telecom/invoice:fax) > 0">
								Fax.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:telecom/invoice:fax" />
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company) > 0">
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:telecom/invoice:phone) > 0">
								Tel.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:telecom/invoice:phone" />
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:telecom/invoice:fax) > 0">
								Fax.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:telecom/invoice:fax" />
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:telecom/invoice:phone) > 0">
								Tel.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:telecom/invoice:phone" />
								<fo:inline> &#160;</fo:inline>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:telecom/invoice:fax) > 0">
								Fax.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:telecom/invoice:fax" />
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					GLN:
					<xsl:value-of
						select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/@ean_party, ', ')" />
					ZSR:
					<xsl:value-of
						select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/@zsr" />
				</xsl:when>
				<xsl:otherwise>
					GLN:
					<xsl:value-of
						select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/@ean_party, ', ')" />
					ZSR:
					<xsl:value-of
						select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/@zsr" />
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
	</xsl:template>

	<xsl:template name="patbill_header_line">
		<fo:block>
			<xsl:if test="$headerLine1">
				<xsl:value-of select="$headerLine1" />
				,
				<xsl:value-of select="$headerLine2" />
			</xsl:if>
		</fo:block>
	</xsl:template>

	<xsl:template name="patbill_esr_bank">
		<fo:block margin-top="10mm">
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:esrQR) > 0">
					<xsl:value-of
						select="concat(/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:bank/invoice:company/invoice:companyname, ' ',  /invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:bank/invoice:company/invoice:department)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of
						select="concat(/invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:bank/invoice:company/invoice:companyname, ' ',  /invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:bank/invoice:company/invoice:department)" />
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:esrQR) > 0">
					<xsl:value-of
						select="concat(/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:bank/invoice:company/invoice:postal/invoice:zip, ' ',  /invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:bank/invoice:company/invoice:postal/invoice:city)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of
						select="concat(/invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:bank/invoice:company/invoice:postal/invoice:zip, ' ',  /invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:bank/invoice:company/invoice:postal/invoice:city)" />
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
	</xsl:template>

	<xsl:template name="patbill_esr_creditor">
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="string-length($creditorLine) > 1">
					<fo:block linefeed-treatment="preserve">
						<xsl:value-of select="$creditorLine" />			
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:esrQR) > 0">
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:creditor/invoice:company) > 0">
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:creditor/invoice:company/invoice:companyname" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:creditor/invoice:person/@title, ' ')" />
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:creditor/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:creditor/invoice:person/invoice:familyname)" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:creditor/invoice:company) > 0">
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:creditor/invoice:company/invoice:companyname" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:creditor/invoice:person/@title, ' ')" />
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:creditor/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:creditor/invoice:person/invoice:familyname)" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>				
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<xsl:choose>
				<xsl:when
					test="string-length($creditorLine) > 36">
				</xsl:when>
				<xsl:otherwise>
					<fo:block>
						<xsl:choose>
							<xsl:when
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:esrQR) > 0">
								<xsl:choose>
									<xsl:when
										test="count(/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:creditor/invoice:company) > 0">
										<xsl:value-of
											select="/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:creditor/invoice:company/invoice:postal/invoice:street" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of
											select="/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:creditor/invoice:person/invoice:postal/invoice:street" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when
										test="count(/invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:creditor/invoice:company) > 0">
										<xsl:value-of
											select="/invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:creditor/invoice:company/invoice:postal/invoice:street" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of
											select="/invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:creditor/invoice:person/invoice:postal/invoice:street" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</fo:block>
				</xsl:otherwise>
		</xsl:choose>
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:esrQR) > 0">
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:creditor/invoice:company) > 0">
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:creditor/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:creditor/invoice:company/invoice:postal/invoice:city)" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:creditor/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:creditor/invoice:person/invoice:postal/invoice:city)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:creditor/invoice:company) > 0">
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:creditor/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:creditor/invoice:company/invoice:postal/invoice:city)" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:creditor/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:esr9/invoice:creditor/invoice:person/invoice:postal/invoice:city)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
	</xsl:template>

	<xsl:template name="patbill_esr_biller">
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:companyname" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/@title, ' ')" />
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:familyname)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:companyname" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/@title, ' ')" />
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:familyname)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:postal/invoice:street" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:street" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:postal/invoice:street" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:street" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company) > 0">
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:postal/invoice:city)" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:city)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company) > 0">
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:postal/invoice:city)" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:city)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
	</xsl:template>

	<xsl:template name="patbill_esr_garant">
		<xsl:choose>
			<xsl:when
				test="string-length($guarantorPostal) > 1">
				<fo:block linefeed-treatment="preserve">
					<xsl:value-of select="$guarantorPostal" />			
				</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when
						test="(string-length($guarantorLine) > 1 and count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0) or 
						(string-length($insuranceLine) > 1 and count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant) > 0)">
						<xsl:choose>
							<xsl:when
							test="(string-length($guarantorLine) > 1 and count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0)">
								<fo:block linefeed-treatment="preserve">
									<xsl:value-of select="$guarantorLine" />			
								</fo:block>
							</xsl:when>
							<xsl:otherwise>
								<fo:block linefeed-treatment="preserve">
									<xsl:value-of select="$insuranceLine" />			
								</fo:block>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<fo:block>
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
									<xsl:choose>
										<xsl:when
											test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
											<xsl:value-of
												select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:companyname" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of
												select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/@salutation, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/@title)" />
											<xsl:value-of
												select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:familyname)" />
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when
											test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company) > 0">
											<xsl:value-of
												select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:companyname" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of
												select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:guarantor/invoice:person/@salutation, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/@title)" />
											<xsl:value-of
												select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:familyname)" />
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:choose>
					<xsl:when
						test="(string-length($guarantorLine) > 35 and count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0) or 
						(string-length($insuranceLine) > 35 and count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant) > 0)">
					</xsl:when>
					<xsl:otherwise>
						<fo:block>
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
									<xsl:choose>
										<xsl:when
											test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
											<xsl:value-of
												select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:street" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of
												select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:street" />
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when
											test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company) > 0">
											<xsl:value-of
												select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:postal/invoice:street" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of
												select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:street" />
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>					
					</xsl:otherwise>
				</xsl:choose>
				<fo:block>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:city)" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:city)" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company) > 0">
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:postal/invoice:city)" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:guarantor/invoice:person/invoice:postal/invoice:city)" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="qrpatbill_esr_paypart">
		<fo:block-container margin-left="0mm" margin-right="0mm" margin-top="0mm" margin-bottom="0mm">
			<fo:table table-layout="auto" border-collapse="collapse" font-size="10px">
				<fo:table-column column-width="52mm" text-align="left" />
				<fo:table-column text-align="left" />
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-weight="bold" font-size="11px">
								Zahlteil
							</fo:block>
 							<fo:block-container text-align="left" margin-left="-1mm" margin-top="0mm" margin-bottom="0mm" width="50mm" height="56mm">
								<fo:block>
									<xsl:choose>
										<xsl:when test="string-length($qrJpeg) > 0">
											<fo:external-graphic src="{$qrJpeg}" width="100%" content-width="scale-to-fit"/>
										</xsl:when>
										<xsl:otherwise>
											<fo:block font-weight="bold">
												Error no QR Code
											</fo:block>
										</xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:block-container>
							<fo:block>
								&#160;&#160;
							</fo:block>
							<fo:table table-layout="fixed" border-collapse="collapse">
								<fo:table-column text-align="left" />
								<fo:table-column text-align="left" />
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-size="8px" font-weight="bold">
												Währung
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="8px" font-weight="bold">
												Betrag
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<xsl:choose>
											<xsl:when
												test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
												<fo:table-cell>
													<fo:block>
														<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@currency" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:call-template name="FormatNumber">
															<xsl:with-param name="Number"
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_due" />
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
											</xsl:when>
											<xsl:otherwise>
												<fo:table-cell>
													<fo:block>
														<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@currency" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:call-template name="FormatNumber">
															<xsl:with-param name="Number"
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount_due" />
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
											</xsl:otherwise>
										</xsl:choose>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="8px" font-weight="bold">
								Konto / Zahlbar an
							</fo:block>
							<fo:block margin-bottom="2mm">
								<xsl:call-template name="FormatIban">
									<xsl:with-param name="Input"
										select="/invoice:request/invoice:payload/invoice:body/invoice:esrQR/@iban" />
								</xsl:call-template>
								<xsl:call-template name="patbill_esr_creditor">
								</xsl:call-template>
							</fo:block>
							<fo:block font-size="8px" font-weight="bold">
								Referenz
							</fo:block>
							<fo:block margin-bottom="2mm">
								<xsl:call-template name="FormatReference">
									<xsl:with-param name="Input"
										select="/invoice:request/invoice:payload/invoice:body/invoice:esrQR/@reference_number" />
								</xsl:call-template>
							</fo:block>
							<xsl:if test="count(/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:payment_reason) > 0">
								<fo:block font-size="8px" font-weight="bold">
									Zusätzliche Informationen
								</fo:block>
								<fo:block margin-bottom="2mm">
									<xsl:for-each select="/invoice:request/invoice:payload/invoice:body/invoice:esrQR/invoice:payment_reason">
										<fo:block>
											<xsl:value-of select="text()"/>
										</fo:block>
									</xsl:for-each>
								</fo:block>							
							</xsl:if>
							<fo:block font-size="8px" font-weight="bold">
								Zahlbar durch
							</fo:block>
							<fo:block margin-bottom="2mm">
								<xsl:call-template name="patbill_esr_garant">
								</xsl:call-template>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="15mm">
						<fo:table-cell>
							<fo:block>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block-container>
	</xsl:template>

	<xsl:template name="qrpatbill_esr_receivepart">
		<fo:block-container margin-left="0mm" margin-right="0mm" margin-top="0mm" margin-bottom="0mm" width="100%">
		<fo:table table-layout="fixed" width="100%" height="100%" border-collapse="collapse" font-size="10px">
			<fo:table-column width="100%" text-align="left"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
							<fo:block font-weight="bold" font-size="11px">
								Empfangsschein
							</fo:block>
							<fo:block-container margin-left="0mm" height="56mm">
								<fo:block font-size="8px" font-weight="bold">
									Konto / Zahlbar an
								</fo:block>
								<fo:block margin-bottom="2mm">
									<xsl:call-template name="FormatIban">
										<xsl:with-param name="Input"
											select="/invoice:request/invoice:payload/invoice:body/invoice:esrQR/@iban" />
									</xsl:call-template>
									<xsl:call-template name="patbill_esr_creditor">
									</xsl:call-template>		
								</fo:block>
								<fo:block font-size="8px" font-weight="bold">
									Referenz
								</fo:block>
								<fo:block margin-bottom="2mm">
									<xsl:call-template name="FormatReference">
										<xsl:with-param name="Input"
											select="/invoice:request/invoice:payload/invoice:body/invoice:esrQR/@reference_number" />
									</xsl:call-template>
								</fo:block>
								<fo:block font-size="8px" font-weight="bold">
									Zahlbar durch
								</fo:block>
								<fo:block margin-bottom="2mm">
									<xsl:call-template name="patbill_esr_garant">
									</xsl:call-template>	
								</fo:block>							
							</fo:block-container>
							<fo:block>
								&#160;&#160;
							</fo:block>
							<fo:table table-layout="fixed" width="100%" height="100%"
								border-collapse="collapse">
								<fo:table-column text-align="left" />
								<fo:table-column text-align="left" />
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-size="8px" font-weight="bold">
												Währung
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block font-size="8px" font-weight="bold">
												Betrag
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<xsl:choose>
											<xsl:when
												test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
												<fo:table-cell>
													<fo:block>
														<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@currency" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:call-template name="FormatNumber">
															<xsl:with-param name="Number"
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_due" />
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
											</xsl:when>
											<xsl:otherwise>
												<fo:table-cell>
													<fo:block>
														<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@currency" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:call-template name="FormatNumber">
															<xsl:with-param name="Number"
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount_due" />
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
											</xsl:otherwise>
										</xsl:choose>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
							<fo:block-container margin-left="0mm" margin-top="8mm">
								<fo:block font-size="8px" font-weight="bold" text-align="right" linefeed-treatment="preserve">
									Annahmestelle
									
									
									
									
								</fo:block>
							</fo:block-container>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		</fo:block-container>		
	</xsl:template>

	<xsl:template name="qrpatbill_esr">
		<!-- ignore margin of page -->
		<fo:block-container margin-left="-{$leftMargin} +7mm"
			margin-right="-{$rightMargin} +7mm">
			<fo:block-container margin-left="0mm" margin-right="0mm">
				<fo:block text-align="center" font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
					Vor  der  Einzahlung  abzutrennen
				</fo:block>
				<fo:table table-layout="fixed" width="100%" height="100%"
					border-collapse="collapse" border-top="thin dashed black">
					<fo:table-column column-width="62mm - 11mm"
						text-align="left"/>
					<fo:table-column column-width="5mm" border-right="thin dashed black"/>
					<fo:table-column column-width="5mm"/>
					<fo:table-column text-align="left" />
					<fo:table-body font-size="9px"
						font-family="tahoma,arial,helvetica,sans-serif">
						<fo:table-row>
							<fo:table-cell>
								<fo:block margin-top="5mm">
									<xsl:call-template
										name="qrpatbill_esr_receivepart">
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell />
							<fo:table-cell />
							<fo:table-cell>
								<fo:block margin-top="5mm">
									<xsl:call-template
										name="qrpatbill_esr_paypart">
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			</fo:block-container>
		</fo:block-container>
	</xsl:template>

	<xsl:template name="PersonSalutation">
		<xsl:param name="Person" />
		<xsl:choose>
			<xsl:when test="$Person/@salutation">
				<xsl:choose>
					<xsl:when test="$Person/@salutation='Herr'">
						Sehr geehrter Herr
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$Person/@salutation='Frau'">
								Sehr geehrte Frau
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$Person/@salutation" />
								<xsl:value-of select="' '" />
							</xsl:otherwise>
						</xsl:choose>						
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				Sehr geehrte/r
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="FormatNumber">
		<xsl:param name="Number" />
		<xsl:if test="string-length($Number) > 0">
			<xsl:value-of select="format-number($Number,'##0.00')" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="GetBeforeComma">
		<xsl:param name="Input" />
		<xsl:choose>
			<xsl:when test="contains($Input,'.')">
				<xsl:value-of select="substring-before($Input,'.')" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$Input" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="GetAfterComma">
		<xsl:param name="Input" />
		<xsl:choose>
			<xsl:when test="contains($Input,'.')">
				<xsl:value-of select="substring-after($Input,'.')" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$Input" />
			</xsl:otherwise>
		</xsl:choose>
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

		<xsl:value-of select="$day" />
		<xsl:value-of select="'.'" />
		<xsl:value-of select="$month" />
		<xsl:value-of select="'.'" />
		<xsl:value-of select="$year" />
	</xsl:template>

	<xsl:template name="FormatReference">
		<xsl:param name="Input" />
		
		<xsl:value-of select="substring($Input,1,2)" />
		<xsl:value-of select="' '" />
		<xsl:value-of select="substring($Input,3,5)" />
		<xsl:value-of select="' '" />
		<xsl:value-of select="substring($Input,8,5)" />
		<xsl:value-of select="' '" />
		<xsl:value-of select="substring($Input,13,5)" />
		<xsl:value-of select="' '" />
		<xsl:value-of select="substring($Input,18,5)" />
		<xsl:value-of select="' '" />
		<xsl:value-of select="substring($Input,23,5)" />
	</xsl:template>

	<xsl:template name="FormatIban">
		<xsl:param name="Input" />
		
		<xsl:value-of select="substring($Input,1,4)" />
		<xsl:value-of select="' '" />
		<xsl:value-of select="substring($Input,5,4)" />
		<xsl:value-of select="' '" />
		<xsl:value-of select="substring($Input,9,4)" />
		<xsl:value-of select="' '" />
		<xsl:value-of select="substring($Input,13,4)" />
		<xsl:value-of select="' '" />
		<xsl:value-of select="substring($Input,17,4)" />
		<xsl:value-of select="' '" />
		<xsl:value-of select="substring($Input,21,1)" />
	</xsl:template>
	
	<xsl:template name="FormatGender">
		<xsl:choose>
			<xsl:when
				test="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/@gender = 'male'">
				<xsl:value-of select="'Mann / M'" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'Frau / F'" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>