<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:invoice="http://www.forum-datenaustausch.ch/invoice">
	<xsl:param name="eanTable" select="''" />
	<xsl:param name="eanList" select="''" />
	<xsl:param name="vatList" select="''" />
	<xsl:param name="amountPrepaid" select="''" />
	<xsl:template match="invoice:*">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="firstPage"
					page-height="29.7cm" page-width="21cm" margin-top="1cm"
					margin-bottom="1.3cm" margin-left="1cm" margin-right="1cm">
					<fo:region-body margin-top="14.5cm"
						margin-bottom="1.2cm" />
					<fo:region-before extent="14.5cm" />
					<fo:region-after extent="1.2cm"
						display-align="after" />
				</fo:simple-page-master>
				<fo:simple-page-master
					master-name="middlePage" page-height="29.7cm" page-width="21cm"
					margin-top="1cm" margin-bottom="1.3cm" margin-left="1cm"
					margin-right="1cm">
					<fo:region-body margin-top="3cm"
						margin-bottom="1.2cm" />
					<fo:region-before extent="3cm" />
					<fo:region-after extent="1.2cm"
						display-align="after" />
				</fo:simple-page-master>
				<fo:simple-page-master master-name="lastPage"
					page-height="29.7cm" page-width="21cm" margin-top="1cm"
					margin-bottom="1.3cm" margin-left="1cm" margin-right="1cm">
					<fo:region-body margin-top="3cm" margin-bottom="2cm" />
					<fo:region-before extent="3cm" />
					<fo:region-after region-name="last-footer"
						extent="2cm" display-align="after" />
				</fo:simple-page-master>
				<fo:simple-page-master
					master-name="singlePage" page-height="29.7cm" page-width="21cm"
					margin-top="1cm" margin-bottom="1.3cm" margin-left="1cm"
					margin-right="1cm">
					<fo:region-body margin-top="14.5cm"
						margin-bottom="2cm" />
					<fo:region-before extent="14.5cm" />
					<fo:region-after region-name="last-footer"
						extent="2cm" display-align="after" />
				</fo:simple-page-master>
				<fo:page-sequence-master
					master-name="allPages">
					<fo:repeatable-page-master-alternatives>
						<fo:conditional-page-master-reference
							page-position="only" master-reference="singlePage" />
						<fo:conditional-page-master-reference
							page-position="first" master-reference="firstPage" />
						<fo:conditional-page-master-reference
							page-position="rest" master-reference="middlePage" />
						<fo:conditional-page-master-reference
							page-position="last" master-reference="lastPage" />
					</fo:repeatable-page-master-alternatives>
				</fo:page-sequence-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="allPages">
				<!-- header -->
				<fo:static-content flow-name="xsl-region-before">
					<!-- main header on every page -->
					<fo:block margin-bottom="0.4cm">
						<xsl:choose>
							<xsl:when
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
								<xsl:call-template name="billheader2D">
									<xsl:with-param name="Type" select="'GGR'">
									</xsl:with-param>
									<xsl:with-param name="Title"
										select="'Rückforderungsbeleg'">
									</xsl:with-param>
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="billheader2D">
									<xsl:with-param name="Type" select="'PGR'">
									</xsl:with-param>
									<xsl:with-param name="Title"
										select="'Tiers Payant Rechnung'">
									</xsl:with-param>
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</fo:block>

					<!-- sub header -->
					<fo:retrieve-marker
						retrieve-class-name="subHeader"
						retrieve-position="first-starting-within-page" />
				</fo:static-content>
				<!-- footer -->
				<fo:static-content flow-name="xsl-region-after">
					<fo:block text-align="right" font-size="7px">
						Zwischentotal
						<xsl:value-of select="' '" />
						<fo:inline font-weight="bold">
							<fo:retrieve-marker
								retrieve-class-name="recordsSum" retrieve-boundary="page"
								retrieve-position="last-starting-within-page" />
						</fo:inline>
					</fo:block>
					<fo:block margin-top="2mm">
						<xsl:call-template name="reclaim_esrline">
						</xsl:call-template>
					</fo:block>
				</fo:static-content>
				<fo:static-content flow-name="last-footer">
					<fo:block text-align="right" font-size="7px">
						<fo:block>
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
									<xsl:call-template
										name="reclaim_tg_summary_total">
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template
										name="reclaim_tp_summary_total">
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</fo:block>
					<fo:block margin-top="2mm">
						<xsl:call-template name="reclaim_esrline">
						</xsl:call-template>
					</fo:block>
				</fo:static-content>
				<!-- document body -->
				<fo:flow flow-name="xsl-region-body">
					<!-- empty blocks with markers for the header -->
					<fo:block>
						<!-- sub header for the first page -->
						<fo:marker marker-class-name="subHeader">
							<xsl:call-template name="reclaim_header">
							</xsl:call-template>
							<xsl:call-template name="reclaim_header_info">
							</xsl:call-template>
						</fo:marker>
					</fo:block>
					<fo:block>
						<!-- sub header for the not-first pages -->
						<fo:marker marker-class-name="subHeader">
							<xsl:call-template name="reclaim_header">
							</xsl:call-template>						
							<xsl:call-template
								name="reclaim_header_patient">
							</xsl:call-template>
						</fo:marker>
					</fo:block>
					<!-- normal content -->
					<fo:block>
						<xsl:call-template name="reclaim_body">
						</xsl:call-template>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<xsl:template name="reclaim_header">
		<fo:table table-layout="fixed" width="100%">
			<fo:table-column column-width="100%" />
			<fo:table-body font-size="9px"
				font-family="tahoma,arial,helvetica,sans-serif">
				<fo:table-row>
					<fo:table-cell border="0.5pt solid black">
						<fo:table table-layout="fixed" width="100%">
							<fo:table-column column-width="10%" />
							<fo:table-column column-width="15%" />
							<fo:table-column column-width="15%" />
							<fo:table-column column-width="45%" />
							<fo:table-column column-width="15%" />
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											Dokument
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Identifikation
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2">
										<fo:block>
											<xsl:value-of
												select="/invoice:request/invoice:payload/invoice:invoice/@request_timestamp" />
											/
											<xsl:call-template name="FormatDateTime">
												<xsl:with-param name="DateTime"
													select="/invoice:request/invoice:payload/invoice:invoice/@request_date" />
											</xsl:call-template>
											/
											<xsl:value-of
												select="/invoice:request/@guid" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif"
											text-align="left">
											Seite
											<fo:page-number />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											Rechnungs-
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											GLN-Nr.(B)
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/@gln" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/@gln" />
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
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
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/@salutation, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/@title)" />
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_gln/invoice:person/invoice:givenname)" />
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
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/@salutation, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/@title)" />
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_gln/invoice:person/invoice:givenname)" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif"
											white-space-treatment="preserve" text-align="right">

										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											steller
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											ZSR-Nr.(B)
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_zsr/@zsr" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_zsr/@zsr" />
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_zsr/invoice:company) > 0">
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_zsr/invoice:company/invoice:postal/invoice:street, '        ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_zsr/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_zsr/invoice:company/invoice:postal/invoice:city)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_zsr/invoice:person/invoice:postal/invoice:street, '        ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_zsr/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:billers/invoice:biller_zsr/invoice:person/invoice:postal/invoice:city)" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_zsr/invoice:company) > 0">
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_zsr/invoice:company/invoice:postal/invoice:street, '        ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_zsr/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_zsr/invoice:company/invoice:postal/invoice:city)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_zsr/invoice:person/invoice:postal/invoice:street, '        ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_zsr/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:billers/invoice:biller_zsr/invoice:person/invoice:postal/invoice:city)" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif"
											white-space-treatment="preserve" text-align="right">

										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template name="reclaim_header_info">
		<fo:table table-layout="fixed" width="100%" height="100%">
			<fo:table-column column-width="100%" />
			<fo:table-body font-size="9px"
				font-family="tahoma,arial,helvetica,sans-serif">
				<fo:table-row>
					<fo:table-cell border-top-width="0.5pt"
						border-top-color="black" border-top-style="solid"
						border-left-width="0.5pt" border-left-color="black"
						border-left-style="solid" border-bottom-width="0.5pt"
						border-bottom-color="black" border-bottom-style="solid">
						<fo:table table-layout="fixed" width="100%">
							<fo:table-column column-width="10%" />
							<fo:table-column column-width="15%" />
							<fo:table-column column-width="20%" />
							<fo:table-column column-width="15%" />
							<fo:table-column column-width="15%" />
							<fo:table-column column-width="25%" />
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											Patient
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Name
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:familyname" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:familyname" />
												</xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											GLN-Nr.
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:insurance/@gln" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/@gln" />
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Vorname
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:givenname" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:givenname" />
												</xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Strasse
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
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
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											PLZ
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:postal/invoice:zip" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:postal/invoice:zip" />
												</xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Ort
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:postal/invoice:city" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:postal/invoice:city" />
												</xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2">
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
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:givenname" />
															<xsl:value-of select="' '" />
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:familyname" />
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
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:person/invoice:givenname" />
															<xsl:value-of select="' '" />
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:person/invoice:familyname" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Geburtsdatum
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:call-template name="FormatDate">
														<xsl:with-param name="DateTime"
															select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/@birthdate" />
													</xsl:call-template>
												</xsl:when>
												<xsl:otherwise>
													<xsl:call-template name="FormatDate">
														<xsl:with-param name="DateTime"
															select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/@birthdate" />
													</xsl:call-template>
												</xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>

									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2">
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
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:person/invoice:postal/invoice:street" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Geschlecht
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:call-template name="FormatGender">
														<xsl:with-param name="Gender"
															select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/@gender" />
													</xsl:call-template>
												</xsl:when>
												<xsl:otherwise>
													<xsl:call-template name="FormatGender">
														<xsl:with-param name="Gender"
															select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/@gender" />
													</xsl:call-template>
												</xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2">
										<fo:block>
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company) > 0">
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:zip" />
															<xsl:value-of select="' '" />
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:company/invoice:postal/invoice:city" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:zip" />
															<xsl:value-of select="' '" />
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/invoice:person/invoice:postal/invoice:city" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company) > 0">
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:postal/invoice:zip" />
															<xsl:value-of select="' '" />
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:company/invoice:postal/invoice:city" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:person/invoice:postal/invoice:zip" />
															<xsl:value-of select="' '" />
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/invoice:person/invoice:postal/invoice:city" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:law[@type='UVG']) > 0">
													<xsl:value-of select="'Unfalldatum'" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:law[@type='IVG']) > 0">
													<xsl:value-of select="'Verfügungsdatum'" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="'Falldatum'" />
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:call-template name="FormatDate">
												<xsl:with-param name="DateTime"
															select="/invoice:request/invoice:payload/invoice:body/invoice:law/@case_date" />
											</xsl:call-template>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2">
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											<!-- add additional receiver address line -->
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:law[@type='UVG']) > 0">
													<xsl:value-of select="'Unfall-Nr.'" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:law[@type='IVG']) > 0">
													<xsl:value-of select="'Verfügungs-Nr.'" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="'Fall-Nr.'" />
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:value-of
												select="/invoice:request/invoice:payload/invoice:body/invoice:law/@case_id" />
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											AHV-Nr.
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/@ssn" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/@ssn" />
												</xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											VEKA-Nr.
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2">
										<fo:block white-space-treatment="preserve">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:card/@card_id" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:card/@card_id" />
												</xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Versicherten-Nr.
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:value-of
												select="/invoice:request/invoice:payload/invoice:body/invoice:law/@insured_id" />
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Kanton
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:value-of
												select="/invoice:request/invoice:payload/invoice:body/invoice:treatment/@canton" />
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Kopie
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:choose>
												<xsl:when
													test="/invoice:request/invoice:payload/@copy = 'true' or /invoice:request/invoice:payload/@copy = '1'">
													<xsl:value-of select="'Ja'" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="'Nein'" />
												</xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Vergütungsart
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of select="'TG'" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="'TP'" />
												</xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											KoGu-Datum/-Nr.
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:if
												test="count(/invoice:request/invoice:payload/invoice:credit) > 0">
												<xsl:call-template name="FormatDate">
													<xsl:with-param name="DateTime"
														select="/invoice:request/invoice:payload/invoice:credit/@request_date" />
												</xsl:call-template>
												/
												<xsl:value-of
													select="/invoice:request/invoice:payload/invoice:credit/@request_id" />
											</xsl:if>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Gesetz
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:value-of
													select="/invoice:request/invoice:payload/invoice:body/invoice:law/@type" />
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Rech.-Datum/-Nr.
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:call-template name="FormatDate">
												<xsl:with-param name="DateTime"
													select="/invoice:request/invoice:payload/invoice:invoice/@request_date" />
											</xsl:call-template>
											/
											<xsl:value-of
												select="number(substring(/invoice:request/invoice:payload/invoice:invoice/@request_id,string-length(/invoice:request/invoice:payload/invoice:invoice/@request_id) - 5))" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Behandlung
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:call-template name="FormatDate">
												<xsl:with-param name="DateTime"
													select="/invoice:request/invoice:payload/invoice:body/invoice:treatment/@date_begin" />
											</xsl:call-template>
											-
											<xsl:call-template name="FormatDate">
												<xsl:with-param name="DateTime"
													select="/invoice:request/invoice:payload/invoice:body/invoice:treatment/@date_end" />
											</xsl:call-template>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Mahn.-Datum/-Nr.
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:reminder) > 0">
													<xsl:call-template name="FormatDate">
														<xsl:with-param name="DateTime"
															select="/invoice:request/invoice:payload/invoice:reminder/@request_date" />
													</xsl:call-template>
													/
												</xsl:when>
												<xsl:otherwise>
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Behandlungsart
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											ambulant
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Behandlungsgrund
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:if
												test="/invoice:request/invoice:payload/invoice:body/invoice:treatment/@reason = 'disease'">
												Krankheit
											</xsl:if>
											<xsl:if
												test="/invoice:request/invoice:payload/invoice:body/invoice:treatment/@reason = 'accident'">
												Unfall
											</xsl:if>
											<xsl:if
												test="/invoice:request/invoice:payload/invoice:body/invoice:treatment/@reason = 'maternity'">
												Schwangerschaft
											</xsl:if>
											<xsl:if
												test="/invoice:request/invoice:payload/invoice:body/invoice:treatment/@reason = 'prevention'">
												Prävention
											</xsl:if>
											<xsl:if
												test="/invoice:request/invoice:payload/invoice:body/invoice:treatment/@reason = 'birthdefect'">
												Geburtsgebrechen
											</xsl:if>
											<xsl:if
												test="/invoice:request/invoice:payload/invoice:body/invoice:treatment/@reason = 'unknown'">
												Anderes
											</xsl:if>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Rolle/Ort
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:if
												test="/invoice:request/invoice:payload/invoice:body/@role = 'physician'">
												<xsl:value-of select="'Arzt/Ärztin'" />
											</xsl:if>
											<xsl:value-of select="' '" />
											<xsl:if
												test="/invoice:request/invoice:payload/invoice:body/@place = 'practice'">
												<xsl:value-of select="'Praxis'" />
											</xsl:if>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="3">
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											<!-- add name here -->
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row border-width="0.5pt"
									border-color="black" border-style="solid">
									<fo:table-cell>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											Leistungs-
										</fo:block>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											erbringer
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											GLN-Nr.(P)
										</fo:block>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											GLN-Nr.(L)
										</fo:block>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											ZSR-Nr.(P)
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/@gln" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/@gln" />
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/@gln_location" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/@gln_location" />
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_zsr/@zsr" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_zsr/@zsr" />
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2">
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
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
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/@salutation, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/@title)" />
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:givenname)" />
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
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/@salutation, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/@title)" />
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:givenname)" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company) > 0">
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:postal/invoice:street, ' · ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:postal/invoice:city)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:postal/invoice:street, ' · ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:postal/invoice:city)" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company) > 0">
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:postal/invoice:street, ' · ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:postal/invoice:city)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:postal/invoice:street, ' · ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:postal/invoice:city)" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company) > 0">
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:telecom/invoice:phone, ' · ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:company/invoice:telecom/invoice:email)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_gln/invoice:person/invoice:telecom/invoice:phone, ' · ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:providers/invoice:provider_glnn/invoice:person/invoice:telecom/invoice:email)" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company) > 0">
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:telecom/invoice:phone, ' · ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:company/invoice:telecom/invoice:email)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:telecom/invoice:phone, ' · ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:providers/invoice:provider_gln/invoice:person/invoice:telecom/invoice:email)" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row border-width="0.5pt"
									border-color="black" border-style="solid">
									<fo:table-cell>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											Diagnose
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="3">
										<fo:block white-space-treatment="preserve">
											<xsl:for-each
												select="/invoice:request/invoice:payload/invoice:body/invoice:treatment/invoice:diagnosis"
												group-by="@type">
												<xsl:choose>
													<xsl:when test="@type = 'by_contract'">
														<xsl:value-of select="'TI='" />
													</xsl:when>
													<xsl:otherwise>
														<xsl:choose>
															<xsl:when test="@type = 'freetext'">
																<xsl:value-of select="'Freitext='" />
															</xsl:when>
															<xsl:otherwise>
																<xsl:value-of select="concat(@type, '=')" />
															</xsl:otherwise>
														</xsl:choose>
													</xsl:otherwise>
												</xsl:choose>
												<xsl:choose>
													<xsl:when test="string-length(.) > 0">
														<xsl:value-of select="." />
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="@code" />
													</xsl:otherwise>
												</xsl:choose>
												;
											</xsl:for-each>
											<xsl:value-of select="'	'" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row border-width="0.5pt"
									border-color="black" border-style="solid">
									<fo:table-cell>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											Bemerkung
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="5">
										<fo:block-container height="1.1cm">
											<fo:block font-size="7px"
												font-family="tahoma,arial,helvetica,sans-serif">
												<xsl:value-of
													select="/invoice:request/invoice:payload/invoice:body/invoice:remark" />
											</fo:block>
										</fo:block-container>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:table table-layout="fixed" width="100%">
							<fo:table-column column-width="10%" />
							<fo:table-column column-width="15%" />
							<fo:table-column column-width="20%" />
							<fo:table-column column-width="15%" />
							<fo:table-column column-width="15%" />
							<fo:table-column column-width="25%" />
							<fo:table-body>



								<fo:table-row>
									<fo:table-cell number-columns-spanned="2">
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Partner
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="1">
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											GLN-/ZSR-/Sektion-Nr
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="3">
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											Adresse
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<xsl:call-template name="EanTable">
									<xsl:with-param name="str" select="$eanTable" />
									<xsl:with-param name="seqno" select="1" />
								</xsl:call-template>


							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template name="reclaim_header_patient">
		<fo:table table-layout="fixed" width="100%">
			<fo:table-column column-width="100%" />
			<fo:table-body font-size="9px"
				font-family="tahoma,arial,helvetica,sans-serif">
				<fo:table-row>
					<fo:table-cell border="0.5pt solid black">
						<fo:table table-layout="fixed" width="100%">
							<fo:table-column column-width="15%" />
							<fo:table-column column-width="10%" />
							<fo:table-column column-width="15%" />
							<fo:table-column column-width="25%" />
							<fo:table-column column-width="35%" />
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											Patient
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2">
										<fo:block>
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/invoice:person/invoice:givenname)" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/invoice:person/invoice:givenname)" />
												</xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="' '" />
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:call-template name="FormatDate">
														<xsl:with-param name="DateTime"
															select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/@birthdate" />
													</xsl:call-template>
												</xsl:when>
												<xsl:otherwise>
													<xsl:call-template name="FormatDate">
														<xsl:with-param name="DateTime"
															select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/@birthdate" />
													</xsl:call-template>
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif"
											text-align="center">
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template name="reclaim_body">
		<fo:table table-layout="fixed" width="100%">
			<fo:table-header>
				<fo:table-row>
					<fo:table-cell width="1.8cm">
						<fo:block font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Datum
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="0.8cm">
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Tarif
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="3cm">
						<fo:block text-align="left" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Tarifziffer
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="1.5cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Bezugsziffer
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="0.4cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Gr
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="0.8cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							St
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="1cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Anzahl
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="1.5cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							TP AL/Preis
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="1cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							fAL
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="1cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							TPW AL
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="1.5cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							TP TL
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="1cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							fTL
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="1cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							TPW TL
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="0.4cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							A
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="0.2cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							V
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="0.2cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							M
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="1.9cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Betrag
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-header>
			<fo:table-body>
				<xsl:for-each
					select="/invoice:request/invoice:payload/invoice:body/invoice:services/*">
					<xsl:call-template name="records">
					</xsl:call-template>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template name="records">
		<fo:table-row keep-with-next.within-page="always">
			<fo:table-cell>
				<fo:block>
					<fo:marker marker-class-name="recordsSum">
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="Number"
								select="@amount + sum(preceding-sibling::*/@amount)" />
						</xsl:call-template>
					</fo:marker>
				</fo:block>
				<fo:block font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:call-template name="FormatDate">
						<xsl:with-param name="DateTime"
							select="@date_begin" />
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
				<fo:block text-align="left" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:value-of select="@code" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:value-of select="@ref_code" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:value-of select="@session" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:if test="local-name() = 'service_ex'">
						<xsl:choose>
							<xsl:when test="@body_location = 'none'">

							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="@body_location = 'left'">
										L
									</xsl:when>
									<xsl:otherwise>
										R
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:call-template name="FormatNumber">
						<xsl:with-param name="Number" select="@quantity" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:choose>
						<xsl:when test="local-name() = 'service_ex'">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="@unit_mt" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="@unit" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:call-template name="FormatNumber">
						<xsl:with-param name="Number"
							select="@scale_factor_mt" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:choose>
						<xsl:when test="local-name() = 'service_ex'">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="@unit_factor_mt" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="@unit_factor" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:call-template name="FormatNumber">
						<xsl:with-param name="Number" select="@unit_tt" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:call-template name="FormatNumber">
						<xsl:with-param name="Number"
							select="@scale_factor_tt" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:call-template name="FormatNumber">
						<xsl:with-param name="Number"
							select="@unit_factor_tt" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:call-template name="EanIndex">
						<xsl:with-param name="Ean" select="@provider_id" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:call-template name="EanIndex">
						<xsl:with-param name="Ean" select="@responsible_id" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:call-template name="VatIndex">
						<xsl:with-param name="Vat" select="@vat_rate" />
					</xsl:call-template>
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
		<fo:table-row>
			<fo:table-cell>
				<fo:block text-align="right" font-size="7px"
					font-family="tahoma,arial,helvetica,sans-serif">
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="7px"
					font-family="tahoma,arial,helvetica,sans-serif">
				</fo:block>
			</fo:table-cell>
			<fo:table-cell number-columns-spanned="16">
				<fo:block font-weight="bold" font-size="7px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:value-of select="@name" />
				</fo:block>
				<fo:block font-size="7px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:value-of select="@remark" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="reclaim_tp_summary_total">
		<fo:table table-layout="fixed" width="100%">
			<fo:table-column column-width="7.5%" />
			<fo:table-column column-width="7.5%" />
			<fo:table-column column-width="7.5%" />
			<fo:table-column column-width="7.5%" />
			<fo:table-column column-width="7.5%" />
			<fo:table-column column-width="17.5%" />
			<fo:table-column column-width="7.5%" />
			<fo:table-column column-width="15%" />
			<fo:table-column column-width="15%" />
			<fo:table-column column-width="7.5%" />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Code
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Satz
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Betrag
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							MWSt
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block text-align="right" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							MwSt.Nr.:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block text-align="left" font-size="7px"
							white-space-treatment="preserve"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of select="' '" />
							<xsl:value-of select="' '" />
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/invoice:vat/@vat_number" />
							<xsl:value-of select="' '" />
							MWST
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Gesamtbetrag:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>

				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							0
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="0" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="0" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="VatAmount">
								<xsl:with-param name="Rate" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="0" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="VatVat">
								<xsl:with-param name="Rate" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block text-align="right" font-size="7px"
							font-weight="bold" padding-right="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Währung:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="left" font-size="7px"
							white-space-treatment="preserve"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of select="' '" />
							<xsl:value-of select="' '" />
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@currency" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
						</fo:block>
					</fo:table-cell>
				</fo:table-row>

				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							1
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="1" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="1" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="VatAmount">
								<xsl:with-param name="Rate" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="1" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="VatVat">
								<xsl:with-param name="Rate" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block text-align="right" font-size="7px"
							font-weight="bold" padding-right="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							IBAN:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block white-space-treatment="preserve" text-align="left" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of select="' '" />
							<xsl:value-of select="' '" />							
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:esrQR/@iban" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							2
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="2" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="2" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="VatAmount">
								<xsl:with-param name="Rate" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="2" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="VatVat">
								<xsl:with-param name="Rate" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block text-align="right" font-size="7px"
							font-weight="bold" padding-right="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Referenz-Nr.:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block white-space-treatment="preserve" text-align="left" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of select="' '" />
							<xsl:value-of select="' '" />							
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:esrQR/@reference_number" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Fälliger Betrag:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="8px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount_due" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template name="reclaim_tg_summary_total">
		<fo:table table-layout="fixed" width="100%">
			<fo:table-column column-width="7.5%" />
			<fo:table-column column-width="7.5%" />
			<fo:table-column column-width="7.5%" />
			<fo:table-column column-width="7.5%" />
			<fo:table-column column-width="7.5%" />
			<fo:table-column column-width="17.5%" />
			<fo:table-column column-width="7.5%" />
			<fo:table-column column-width="15%" />
			<fo:table-column column-width="15%" />
			<fo:table-column column-width="7.5%" />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Code
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Satz
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Betrag
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							MWSt
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							MwSt.Nr.:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="left" font-size="7px"
							white-space-treatment="preserve"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of select="' '" />
							<xsl:value-of select="' '" />
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/invoice:vat/@vat_number" />
							<xsl:value-of select="' '" />
							MWST
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Anzahlung:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							white-space-treatment="preserve"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of select="' '" />
							<xsl:value-of select="' '" />
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_prepaid) > 0">
									<xsl:value-of
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_prepaid" />
								</xsl:when>
								<xsl:otherwise>
									0.00
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Gesamtbetrag:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>

				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							0
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="0" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="0" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="VatAmount">
								<xsl:with-param name="Rate" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="0" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="VatVat">
								<xsl:with-param name="Rate" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-weight="bold" padding-right="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Währung:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="left" font-size="7px"
							white-space-treatment="preserve"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of select="' '" />
							<xsl:value-of select="' '" />
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@currency" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="left" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:if
								test="string-length(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_reminder) > 0">
								Mahngebühr:
							</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:if
								test="string-length(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_reminder) > 0">
								<xsl:value-of
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_reminder" />
							</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
						</fo:block>
					</fo:table-cell>
				</fo:table-row>

				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							1
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="1" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="1" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="VatAmount">
								<xsl:with-param name="Rate" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="1" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="VatVat">
								<xsl:with-param name="Rate" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="5">
						<fo:block text-align="left" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">

						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							2
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="2" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="2" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="VatAmount">
								<xsl:with-param name="Rate" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:variable name="vatRate">
								<xsl:call-template name="VatRate">
									<xsl:with-param name="Index" select="2" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="VatVat">
								<xsl:with-param name="Rate" select="$vatRate" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="3">
						<fo:block text-align="left" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">

						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Fälliger Betrag:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="8px"
							font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_due" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template name='reclaim_esrline'>
		<fo:block font-family="OCRB" font-size="10px"
			text-align="right" width="11.1cm">
			<xsl:choose>
				<xsl:when
					test="count(/invoice:request/invoice:payload/invoice:body/invoice:esr9) > 0">
					<xsl:value-of
						select="/invoice:request/invoice:payload/invoice:body/invoice:esr9/@coding_line" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of
						select="/invoice:request/invoice:payload/invoice:body/invoice:esr6/@coding_line" />
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>

	</xsl:template>

	<xsl:template name="FormatNumber">
		<xsl:param name="Number" />
		<xsl:if test="string-length($Number) > 0">
			<xsl:value-of select="format-number($Number,'##0.00')" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="BooleanToNumber">
		<xsl:param name="Boolean" />
		<xsl:choose>
			<xsl:when test="string-length($Boolean) > 1">
				<xsl:choose>
					<xsl:when test="$Boolean = 'true'">
						1
					</xsl:when>
					<xsl:otherwise>
						0
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$Boolean" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="EanTable">
		<xsl:param name="str" />
		<xsl:param name="seqno" />

		<xsl:choose>
			<xsl:when test="contains($str, '&#x7C;')">
				<fo:table-row>
					<xsl:call-template name="EanLine">
						<xsl:with-param name="str"
							select="substring-before($str, '&#x7C;')" />
						<xsl:with-param name="seqno" select="1" />
					</xsl:call-template>
				</fo:table-row>
				<xsl:call-template name="EanTable">
					<xsl:with-param name="str"
						select="substring-after($str, '&#x7C;')" />
					<xsl:with-param name="seqno" select="$seqno + 1" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<fo:table-row>
					<xsl:call-template name="EanLine">
						<xsl:with-param name="str" select="$str" />
						<xsl:with-param name="seqno" select="1" />
					</xsl:call-template>
				</fo:table-row>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="EanLine">
		<xsl:param name="str" />
		<xsl:param name="seqno" />

		<xsl:if test="$seqno = 1">
			<xsl:choose>
				<xsl:when test="contains($str, '&#xA;')">
					<fo:table-cell number-columns-spanned="2">
						<fo:block font-size="7px">
							<xsl:value-of
								select="substring-before($str, '&#xA;')" />
						</fo:block>
					</fo:table-cell>
					<xsl:call-template name="EanLine">
						<xsl:with-param name="str"
							select="substring-after($str, '&#xA;')" />
						<xsl:with-param name="seqno" select="$seqno + 1" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<fo:table-cell number-columns-spanned="2">
						<fo:block font-size="7px">
							<xsl:value-of select="$str" />
						</fo:block>
					</fo:table-cell>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="$seqno = 2">
			<xsl:choose>
				<xsl:when test="contains($str, '&#xA;')">
					<fo:table-cell number-columns-spanned="1">
						<fo:block font-size="7px">
							<xsl:value-of
								select="substring-before($str, '&#xA;')" />
						</fo:block>
					</fo:table-cell>
					<xsl:call-template name="EanLine">
						<xsl:with-param name="str"
							select="substring-after($str, '&#xA;')" />
						<xsl:with-param name="seqno" select="$seqno + 1" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<fo:table-cell number-columns-spanned="1">
						<fo:block font-size="7px">
							<xsl:value-of select="$str" />
						</fo:block>
					</fo:table-cell>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="$seqno = 3">
			<xsl:choose>
				<xsl:when test="contains($str, '&#xA;')">
					<fo:table-cell number-columns-spanned="3">
						<fo:block font-size="7px">
							<xsl:value-of
								select="substring-before($str, '&#xA;')" />
						</fo:block>
					</fo:table-cell>
					<xsl:call-template name="EanLine">
						<xsl:with-param name="str"
							select="substring-after($str, '&#xA;')" />
						<xsl:with-param name="seqno" select="$seqno + 1" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<fo:table-cell number-columns-spanned="3">
						<fo:block font-size="7px">
							<xsl:value-of select="$str" />
						</fo:block>
					</fo:table-cell>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>


	<xsl:template name="EanIndex">
		<xsl:param name="Ean" />
		<xsl:choose>
			<xsl:when test="contains($eanList,$Ean)">
				<xsl:variable name="matchingBeforePart">
					<xsl:value-of select="substring-before($eanList,$Ean)" />
				</xsl:variable>
				<xsl:value-of
					select="substring($matchingBeforePart, string-length($matchingBeforePart)-2, 2)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'?'" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="VatIndex">
		<xsl:param name="Vat" />
		<xsl:choose>
			<xsl:when test="string-length($Vat) > 0 and $Vat != '0'">
				<xsl:variable name="matchingBeforePart">
					<xsl:value-of select="substring-before($vatList,$Vat)" />
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="string-length($matchingBeforePart) > 0">
						<xsl:value-of
							select="substring($matchingBeforePart, string-length($matchingBeforePart)-2, 2)" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'0'" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'0'" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="VatRate">
		<xsl:param name="Index" />
		<xsl:choose>
			<xsl:when test="contains($vatList, concat($Index, '/'))">
				<xsl:variable name="matchingAfterPart">
					<xsl:value-of
						select="substring-after($vatList,concat($Index, '/'))" />
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="contains($matchingAfterPart, ' ')">
						<xsl:value-of
							select="substring-before($matchingAfterPart,' ')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$matchingAfterPart" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'0'" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="VatAmount">
		<xsl:param name="Rate" />
		<xsl:choose>
			<xsl:when
				test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
				<xsl:choose>
					<xsl:when
						test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$Rate]) = 0">
						<xsl:variable name="formattedRate">
							<xsl:value-of select="format-number($Rate,'##0.0')" />
						</xsl:variable>
						<xsl:choose>
							<xsl:when
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$formattedRate]) > 0">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="Number"
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$formattedRate]/@amount" />
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="'0.00'" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="Number"
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$Rate]/@amount" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when
						test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$Rate]) = 0">
						<xsl:variable name="formattedRate">
							<xsl:value-of select="format-number($Rate,'##0.0')" />
						</xsl:variable>
						<xsl:choose>
							<xsl:when
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$formattedRate]) > 0">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="Number"
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$formattedRate]/@amount" />
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="'0.00'" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="Number"
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$Rate]/@amount" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="VatVat">
		<xsl:param name="Rate" />
		<xsl:choose>
			<xsl:when
				test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
				<xsl:choose>
					<xsl:when
						test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$Rate]) = 0">
						<xsl:variable name="formattedRate">
							<xsl:value-of select="format-number($Rate,'##0.0')" />
						</xsl:variable>
						<xsl:choose>
							<xsl:when
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$formattedRate]) > 0">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="Number"
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$formattedRate]/@vat" />
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="'0.00'" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="Number"
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$Rate]/@vat" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when
						test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$Rate]) = 0">
						<xsl:variable name="formattedRate">
							<xsl:value-of select="format-number($Rate,'##0.0')" />
						</xsl:variable>
						<xsl:choose>
							<xsl:when
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$formattedRate]) > 0">
								<xsl:call-template name="FormatNumber">
									<xsl:with-param name="Number"
										select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$formattedRate]/@vat" />
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="'0.00'" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="FormatNumber">
							<xsl:with-param name="Number"
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/invoice:vat/invoice:vat_rate[@vat_rate=$Rate]/@vat" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
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

	<xsl:template name="FormatDateTime">
		<xsl:param name="DateTime" />

		<!-- new date format 2006-01-14T08:55:22 -->
		<!-- new date format 2024-11-30T00:00:00 -->
		<!-- new date format 2025-11-04T00:00:00.000+01:00 -->
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
		<xsl:variable name="time">
			<xsl:value-of select="substring(substring-after($DateTime,'T'),1,8)" />
		</xsl:variable>

		<xsl:value-of select="$day" />
		<xsl:value-of select="'.'" />
		<xsl:value-of select="$month" />
		<xsl:value-of select="'.'" />
		<xsl:value-of select="$year" />
		<xsl:value-of select="' '" />
		<xsl:value-of select="$time" />
	</xsl:template>

	<xsl:include href="/rsc/templates_50.xsl" />
</xsl:stylesheet>