<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:invoice="http://www.forum-datenaustausch.ch/invoice">

	<xsl:template name="patbill_biller_address_50">
		<fo:block>
			<xsl:choose>
				<xsl:when test="string-length($billerLine)>
					1">
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
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company) > 0">
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company/invoice:companyname" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/@title, ' ')" />
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:familyname)" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company) > 0">
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company/invoice:companyname" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/@title, ' ')" />
									<xsl:value-of
										select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:familyname)" />
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
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company/invoice:postal/invoice:street" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/invoice:postal/invoice:street" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company/invoice:postal/invoice:street" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/invoice:postal/invoice:street" />
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
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company) > 0">
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company/invoice:postal/invoice:city)" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/invoice:postal/invoice:city)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company) > 0">
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company/invoice:postal/invoice:city)" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/invoice:postal/invoice:city)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>

			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company) > 0">
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company/invoice:telecom/invoice:phone) > 0">
								<fo:block>
									Tel.
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company/invoice:telecom/invoice:phone" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company/invoice:telecom/invoice:fax) > 0">
								<fo:block>
									Fax.
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company/invoice:telecom/invoice:fax" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company/invoice:online/invoice:email) > 0">
								<fo:block>
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:company/invoice:online/invoice:email" />
								</fo:block>									
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/invoice:telecom/invoice:phone) > 0">
								<fo:block>
								Tel.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/invoice:telecom/invoice:phone" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/invoice:telecom/invoice:fax) > 0">
								Fax.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/invoice:telecom/invoice:fax" />
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/invoice:online/invoice:email) > 0">
								<fo:block>
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/invoice:online/invoice:email" />
								</fo:block>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company) > 0">
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company/invoice:telecom/invoice:phone) > 0">
								<fo:block>
								Tel.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company/invoice:telecom/invoice:phone" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company/invoice:telecom/invoice:fax) > 0">
								<fo:block>
								Fax.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company/invoice:telecom/invoice:fax" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company/invoice:online/invoice:email) > 0">
								<fo:block>
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:company/invoice:online/invoice:email" />
								</fo:block>
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/invoice:telecom/invoice:phone) > 0">
								<fo:block>
								Tel.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/invoice:telecom/invoice:phone" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/invoice:telecom/invoice:fax) > 0">
								<fo:block>
								Fax.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/invoice:telecom/invoice:fax" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/invoice:online/invoice:email) > 0">
								<fo:block>
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/invoice:online/invoice:email" />
								</fo:block>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
	</xsl:template>

	<xsl:template name="patbill_provider_address_50">
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:companyname" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/@title, ' ')" />
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:familyname)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:companyname" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/@title, ' ')" />
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:familyname)" />
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
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:postal/invoice:street" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:postal/invoice:street" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:postal/invoice:street" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:postal/invoice:street" />
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
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company) > 0">
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:postal/invoice:city)" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:postal/invoice:city)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company) > 0">
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:postal/invoice:city)" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:postal/invoice:city)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company) > 0">
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:telecom/invoice:phone) > 0">
								<fo:block>
								Tel.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:telecom/invoice:phone" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:telecom/invoice:fax) > 0">
								<fo:block>
								Fax.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:telecom/invoice:fax" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:online/invoice:email) > 0">
								<fo:block>
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:online/invoice:email" />
								</fo:block>
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:telecom/invoice:phone) > 0">
								<fo:block>
								Tel.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:telecom/invoice:phone" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:telecom/invoice:fax) > 0">
								<fo:block>
								Fax.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:telecom/invoice:fax" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:online/invoice:email) > 0">
								<fo:block>
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:online/invoice:email" />
								</fo:block>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company) > 0">
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:telecom/invoice:phone) > 0">
								<fo:block>
								Tel.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:telecom/invoice:phone" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:telecom/invoice:fax) > 0">
								<fo:block>
								Fax.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:telecom/invoice:fax" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:online/invoice:email) > 0">
								<fo:block>
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:online/invoice:email" />
								</fo:block>
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:telecom/invoice:phone) > 0">
								<fo:block>
								Tel.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:telecom/invoice:phone" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:telecom/invoice:fax) > 0">
								<fo:block>
								Fax.
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:telecom/invoice:fax" />
								</fo:block>
							</xsl:if>
							<xsl:if
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:online/invoice:email) > 0">
								<fo:block>
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:online/invoice:email" />
								</fo:block>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
	</xsl:template>

	<xsl:template name="patbill_patient_address_50">
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:value-of
						select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/@title, ' ')" />
					<xsl:value-of
						select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:familyname)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of
						select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/@title, ' ')" />
					<xsl:value-of
						select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:givenname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:familyname)" />
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:value-of
						select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:postal/invoice:street" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of
						select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:postal/invoice:street" />
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:value-of
						select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:postal/invoice:city)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of
						select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:postal/invoice:city)" />
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<fo:block>
			<fo:inline> &#160;</fo:inline>
<!-- 			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					<xsl:if
						test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:telecom/invoice:phone) > 0">
						Tel.
						<xsl:value-of
							select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:telecom/invoice:phone" />
					</xsl:if>
					<xsl:if
						test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:telecom/invoice:fax) > 0">
						Fax.
						<xsl:value-of
							select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:telecom/invoice:fax" />
					</xsl:if>
					<xsl:if
						test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:online/invoice:email) > 0">
						<xsl:value-of
							select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:online/invoice:email" />
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if
						test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:telecom/invoice:phone) > 0">
						Tel.
						<xsl:value-of
							select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:telecom/invoice:phone" />
						<fo:inline> &#160;</fo:inline>
					</xsl:if>
					<xsl:if
						test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:telecom/invoice:fax) > 0">
						Fax.
						<xsl:value-of
							select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:telecom/invoice:fax" />
					</xsl:if>
					<xsl:if
						test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:online/invoice:email) > 0">
						<xsl:value-of
							select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:online/invoice:email" />
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose> -->
		</fo:block>
		<fo:block>
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
					Geburtsdatum
					<xsl:call-template name="FormatDate">
						<xsl:with-param name="DateTime"
							select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/@birthdate" />
					</xsl:call-template>						
					 · 
					<xsl:call-template name="FormatGender">
						<xsl:with-param name="Gender"
							select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/@gender" />
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</fo:block>
	</xsl:template>

	<xsl:template name="patbill_services_overview_50">
		<fo:table table-layout="auto" width="5cm"
			border-collapse="collapse" font-size="8px"
			font-family="tahoma,arial,helvetica,sans-serif">
			<fo:table-column width="60%" />
			<fo:table-column width="40%" />
			<fo:table-body>
				<fo:table-row border-bottom-width="0.5pt"
					border-bottom-color="black" border-bottom-style="solid">
					<fo:table-cell>
						<fo:block>
							Bereich
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">
							Total/CHF
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="right">
							Medizinisch:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service_ex[@tariff_type='001']/@amount)
									+ sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service_ex[@tariff_type='007']/@amount)" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="right">
							Medikamente:
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
						<fo:block text-align="right">
							Labor:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service[@tariff_type='317']/@amount)" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="right">
							Migel:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service[@tariff_type='452']/@amount)" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="right">
							Übrige:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service[@tariff_type!='311' and @tariff_type!='317' and @tariff_type!='452' and @tariff_type!='400' and @tariff_type!='402' and @tariff_type!='406']/@amount)
									+ sum(/invoice:request/invoice:payload/invoice:body/invoice:services/invoice:service_ex[@tariff_type!='001' and @tariff_type!='007']/@amount)" />
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
									<fo:block text-align="right">
										Gebühren:
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
								<fo:block font-weight="bold" text-align="right">
									Gesamttotal:
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
								<fo:block font-weight="bold" text-align="right">
									Anzahlung:
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
								<fo:block font-weight="bold" text-align="right">
									Fälliger
									Betrag:
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
									<fo:block text-align="right">
										Gebühren:
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
								<fo:block font-weight="bold" text-align="right">
									Gesamttotal:
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
								<fo:block font-weight="bold" text-align="right">
									Anzahlung:
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-weight="bold">
									<xsl:call-template name="FormatNumber">
										<xsl:with-param name="Number"
											select="$amountPrepaid" />
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-weight="bold" text-align="right">
									Fälliger
									Betrag:
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

	<xsl:template name="patbill_bill_info_50">
		<fo:table table-layout="auto" width="6.5cm"
			border-collapse="collapse" font-size="8px"
			font-family="tahoma,arial,helvetica,sans-serif">
			<fo:table-column width="60%" />
			<fo:table-column width="40%" />
			<fo:table-body>

				<fo:table-row border-bottom-width="0.5pt"
					border-bottom-color="black" border-bottom-style="solid">
					<fo:table-cell>
						<fo:block>
							Rechnungs-Daten
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>

				<fo:table-row>
					<fo:table-cell margin-right="2mm">
						<fo:block text-align="right">
							MwSt.-Nummer:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
									<xsl:value-of select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/invoice:vat/@vat_number" />	
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/invoice:vat/@vat_number" />								
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>

				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="right" margin-right="2mm">
							Rechnungs-Datum:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>
							<xsl:call-template name="FormatDate">
								<xsl:with-param name="DateTime"
									select="/invoice:request/invoice:payload/invoice:invoice/@request_date" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>

				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="right" margin-right="2mm">
							Rechnungs-Nummer:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>
							<xsl:value-of
								select="number(substring(/invoice:request/invoice:payload/invoice:invoice/@request_id,string-length(/invoice:request/invoice:payload/invoice:invoice/@request_id) - 5))" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>

				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="right" margin-right="2mm">
							Behandlung:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>
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
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>

	</xsl:template>

	<xsl:template name="patbill_vat_overview_50">
		<fo:table table-layout="auto" width="5cm"
			border-collapse="collapse" font-size="8px"
			font-family="tahoma,arial,helvetica,sans-serif">
			<fo:table-column width="60%" />
			<fo:table-column width="40%" />
			<fo:table-body>
				<fo:table-row border-bottom-width="0.5pt"
					border-bottom-color="black" border-bottom-style="solid">
					<fo:table-cell>
						<fo:block>
							MwSt-Satz/% </fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">
							MwSt/CHF
						</fo:block>
					</fo:table-cell>
				</fo:table-row>


				<xsl:choose>
					<xsl:when
						test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">

						<xsl:for-each
							select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/invoice:vat/invoice:vat_rate">
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<xsl:value-of select="@vat_rate" />
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right">
										<xsl:value-of select="@vat" />
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</xsl:for-each>

						<fo:table-row border-top-width="0.5pt"
							border-top-color="black" border-top-style="solid">
							<fo:table-cell>
								<fo:block font-weight="bold">
									MwSt-Total:
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-weight="bold">
									<xsl:call-template name="FormatNumber">
										<xsl:with-param name="Number"
											select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/invoice:vat/@vat" />
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:when>
					<xsl:otherwise>
						<xsl:for-each
							select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/invoice:vat/invoice:vat_rate">
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<xsl:value-of select="@vat_rate" />
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right">
										<xsl:value-of select="@vat" />
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</xsl:for-each>

						<fo:table-row border-top-width="0.5pt"
							border-top-color="black" border-top-style="solid">
							<fo:table-cell>
								<fo:block font-weight="bold">
									MwSt-Total:
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" font-weight="bold">
									<xsl:call-template name="FormatNumber">
										<xsl:with-param name="Number"
											select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/vat/@vat" />
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:otherwise>
				</xsl:choose>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template name="overview_body">
	
		<fo:table table-layout="fixed" width="100%" font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell width="2cm">
						<fo:block font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Rolle/Ort:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block white-space-treatment="preserve" text-align="left">
							<xsl:if
								test="/invoice:request/invoice:payload/invoice:body/@role = 'physician'">
								<xsl:value-of select="'Arzt/Ärztin'" />
							</xsl:if>
							<xsl:value-of select="' · '" />
							<xsl:if
								test="/invoice:request/invoice:payload/invoice:body/@place = 'practice'">
								<xsl:value-of select="'Praxis'" />
							</xsl:if>
							<xsl:value-of select="' '" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell width="2cm">
						<fo:block font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Behandlung:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>
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
					</fo:table-cell>
				</fo:table-row>				
			</fo:table-body>
		</fo:table>
	
		<fo:table table-layout="fixed" width="100%" font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
			<fo:table-header>
				<fo:table-row border-top-style="solid" border-top-width="1pt"
					border-bottom-style="solid" border-bottom-width="1pt" font-weight="bold">
					<fo:table-cell width="1.8cm">
						<fo:block>
							Datum
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="1cm">
						<fo:block text-align="center">
							Tarif
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>
							Leistung
						</fo:block>
					</fo:table-cell>					
					<fo:table-cell width="3cm">
						<fo:block text-align="right">
							Betrag
						</fo:block>
					</fo:table-cell>					
				</fo:table-row>
			</fo:table-header>
			<fo:table-body>
				<xsl:for-each select="/invoice:request/invoice:payload/invoice:body/invoice:services/*">
					<xsl:call-template name="overview_records">
					</xsl:call-template>
				</xsl:for-each>
			</fo:table-body>			
		</fo:table>
	</xsl:template>

	<xsl:template name="overview_records">
		<fo:table-row keep-with-next.within-page="always">
			<fo:table-cell>
				<fo:block font-size="9px" font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:call-template name="FormatDate">
						<xsl:with-param name="DateTime" select="@date_begin" />
					</xsl:call-template>
				</fo:block>				
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="center" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:value-of select="@tariff_type" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block font-size="9px">
					<xsl:value-of select="@name" />
				</fo:block>
				<fo:block font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:value-of select="@remark" />
				</fo:block>
			</fo:table-cell>			
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:call-template name="FormatNumber">
						<xsl:with-param name="Number" select="@amount" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>			
		</fo:table-row>
	</xsl:template>

	<xsl:template name="overview_footer">
		<fo:table table-layout="fixed" width="100%" font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell width="14cm">
						<fo:block>
							<fo:inline> &#160;</fo:inline>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-top-style="solid" border-top-width="1pt" width="3cm">
						<fo:block font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Rechnungsbetrag:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-top-style="solid" border-top-width="1pt">
						<fo:block text-align="right" font-size="8px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
</xsl:stylesheet>