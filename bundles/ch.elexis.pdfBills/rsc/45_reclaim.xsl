<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:invoice="http://www.forum-datenaustausch.ch/invoice">
	<xsl:param name="eanList" select="''" />
	<xsl:param name="vatList" select="''" />
	<xsl:param name="amountPrepaid" select="''" />
	<xsl:template match="invoice:*">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="firstPage"
					page-height="29.7cm" page-width="21cm" margin-top="1cm"
					margin-bottom="1.3cm" margin-left="1.5cm" margin-right="1.4cm">
					<fo:region-body margin-top="13.4cm" margin-bottom="1.2cm" />
					<fo:region-before extent="13.4cm" />
					<fo:region-after extent="1.2cm" display-align="after" />
				</fo:simple-page-master>
				<fo:simple-page-master master-name="middlePage"
					page-height="29.7cm" page-width="21cm" margin-top="1cm"
					margin-bottom="1.3cm" margin-left="1.5cm" margin-right="1.4cm">
					<fo:region-body margin-top="3cm" margin-bottom="1.2cm" />
					<fo:region-before extent="3cm" />
					<fo:region-after extent="1.2cm" display-align="after" />
				</fo:simple-page-master>
				<fo:simple-page-master master-name="lastPage"
					page-height="29.7cm" page-width="21cm" margin-top="1cm"
					margin-bottom="1.3cm" margin-left="1.5cm" margin-right="1.4cm">
					<fo:region-body margin-top="3cm" margin-bottom="2cm" />
					<fo:region-before extent="3cm" />
					<fo:region-after region-name="last-footer" extent="2cm"
						display-align="after" />
				</fo:simple-page-master>
				<fo:simple-page-master master-name="singlePage"
					page-height="29.7cm" page-width="21cm" margin-top="1cm"
					margin-bottom="1.3cm" margin-left="1.5cm" margin-right="1.4cm">
					<fo:region-body margin-top="13.4cm" margin-bottom="2cm" />
					<fo:region-before extent="13.4cm" />
					<fo:region-after region-name="last-footer" extent="2cm"
						display-align="after" />
				</fo:simple-page-master>
				<fo:page-sequence-master master-name="allPages">
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
					<xsl:call-template name="reclaim_header">
					</xsl:call-template>
					<!-- sub header -->
					<fo:retrieve-marker retrieve-class-name="subHeader"
						retrieve-position="first-starting-within-page" />
				</fo:static-content>
				<!-- footer -->
				<fo:static-content flow-name="xsl-region-after">
					<fo:block text-align="right" font-size="7px">
						Zwischentotal
						<xsl:value-of select="' '" />
						<fo:inline font-weight="bold">
							<fo:retrieve-marker retrieve-class-name="recordsSum"
								retrieve-boundary="page" retrieve-position="last-starting-within-page" />
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
									<xsl:call-template name="reclaim_tg_summary_total">
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="reclaim_tp_summary_total">
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
							<xsl:call-template name="reclaim_header_info">
							</xsl:call-template>
						</fo:marker>
					</fo:block>
					<fo:block>
						<!-- sub header for the not-first pages -->
						<fo:marker marker-class-name="subHeader">
							<xsl:call-template name="reclaim_header_patient">
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
		<fo:table table-layout="fixed" width="100%" border-collapse="collapse">
			<fo:table-column column-width="50%" text-align="left" />
			<fo:table-column column-width="50%" text-align="right" />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block font-weight="bold" font-size="15">
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
									Rückforderungsbeleg
								</xsl:when>
								<xsl:otherwise>
									TP-Rechnung
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="after">
						<fo:block text-align="right" font-size="7px">
							Release 4.5G/de
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
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
											-
											<xsl:call-template name="FormatDateTime">
												<xsl:with-param name="DateTime"
													select="/invoice:request/invoice:payload/invoice:invoice/@request_date" />
											</xsl:call-template>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif" text-align="left">
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
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/@ean_party" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/@ean_party" />
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
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company) > 0">
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:companyname" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/@salutation, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/@title)" />
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:givenname)" />
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
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/@salutation, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/@title)" />
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:givenname)" />
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
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													Tel:
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company) > 0">
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:telecom/invoice:phone" />

														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:telecom/invoice:phone" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													Tel:
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company) > 0">
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:telecom/invoice:phone" />

														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:telecom/invoice:phone" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:otherwise>
											</xsl:choose>
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
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/@zsr" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/@zsr" />
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
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company) > 0">
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:postal/invoice:street, '        ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:postal/invoice:city)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:street, '        ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:postal/invoice:city)" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company) > 0">
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:postal/invoice:street, '        ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:postal/invoice:city)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:street, '        ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:postal/invoice:city)" />
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
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													Fax:
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company) > 0">
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:company/invoice:telecom/invoice:fax" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:biller/invoice:person/invoice:telecom/invoice:fax" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													Fax:
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company) > 0">
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:company/invoice:telecom/invoice:fax" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:biller/invoice:person/invoice:telecom/invoice:fax" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											Leistungs-
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											GLN-Nr.(P)
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/@ean_party" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/@ean_party" />
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
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:company) > 0">
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:company/invoice:companyname" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:person/@salutation,
														' ',
														/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:person/@title,
														' ')" />
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:person/invoice:familyname, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:person/invoice:givenname)" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:company) > 0">
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:company/invoice:companyname" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:person/@salutation,
														 ' ',
														 /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:person/@title,
														 ' ')" />
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:person/invoice:familyname,
														 ' ',
														 /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:person/invoice:givenname)" />
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
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													Tel:
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:company) > 0">
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:company/invoice:telecom/invoice:phone" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:person/invoice:telecom/invoice:phone" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													Tel:
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:company) > 0">
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:company/invoice:telecom/invoice:phone" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:person/invoice:telecom/invoice:phone" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											erbringer
										</fo:block>
									</fo:table-cell>

									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											ZSR-Nr.(P)
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/@zsr" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/@zsr" />
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
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:company) > 0">
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:company/invoice:postal/invoice:street, '        ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:company/invoice:postal/invoice:city)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:person/invoice:postal/invoice:street, '        ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:person/invoice:postal/invoice:city)" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:company) > 0">
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:company/invoice:postal/invoice:street, '        ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:company/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:company/invoice:postal/invoice:city)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:person/invoice:postal/invoice:street, '        ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:person/invoice:postal/invoice:zip, ' ', /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:person/invoice:postal/invoice:city)" />
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
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													Fax:
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:company) > 0">
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:company/invoice:telecom/invoice:fax" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:provider/invoice:person/invoice:telecom/invoice:fax" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													Fax:
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:company) > 0">
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:company/invoice:telecom/invoice:fax" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:provider/invoice:person/invoice:telecom/invoice:fax" />
														</xsl:otherwise>
													</xsl:choose>
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
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:guarantor/@ean_party" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:insurance/@ean_party" />
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
													<xsl:choose>
														<xsl:when
															test="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/@gender = 'male'">
															<xsl:value-of select="'M'" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="'W'" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													<xsl:choose>
														<xsl:when
															test="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/@gender = 'male'">
															<xsl:value-of select="'M'" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="'W'" />
														</xsl:otherwise>
													</xsl:choose>
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
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:uvg) > 0">
													<xsl:value-of select="'Unfalldatum'" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:ivg) > 0">
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
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:kvg) > 0">
													<xsl:call-template name="FormatDate">
														<xsl:with-param name="DateTime"
															select="/invoice:request/invoice:payload/invoice:body/invoice:kvg/@case_date" />
													</xsl:call-template>
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:mvg) > 0">
													<xsl:call-template name="FormatDate">
														<xsl:with-param name="DateTime"
															select="/invoice:request/invoice:payload/invoice:body/invoice:mvg/@case_date" />
													</xsl:call-template>
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:ivg) > 0">
													<xsl:call-template name="FormatDate">
														<xsl:with-param name="DateTime"
															select="/invoice:request/invoice:payload/invoice:body/invoice:ivg/@case_date" />
													</xsl:call-template>
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:vvg) > 0">
													<xsl:call-template name="FormatDate">
														<xsl:with-param name="DateTime"
															select="/invoice:request/invoice:payload/invoice:body/invoice:vvg/@case_date" />
													</xsl:call-template>
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:org) > 0">
													<xsl:call-template name="FormatDate">
														<xsl:with-param name="DateTime"
															select="/invoice:request/invoice:payload/invoice:body/invoice:org/@case_date" />
													</xsl:call-template>
												</xsl:when>
												<xsl:otherwise>
													<xsl:call-template name="FormatDate">
														<xsl:with-param name="DateTime"
															select="/invoice:request/invoice:payload/invoice:body/invoice:uvg/@case_date" />
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
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:uvg) > 0">
													<xsl:value-of select="'Unfall-Nr.'" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:ivg) > 0">
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
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:kvg) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:kvg/@case_id" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:mvg) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:mvg/@case_id" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:ivg) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:ivg/@case_id" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:uvg/@case_id" />
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
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:kvg) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:kvg/@insured_id" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:mvg) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:mvg/@insured_id" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:ivg) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:ivg/@insured_id" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:vvg) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:vvg/@insured_id" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:org) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:org/@insured_id" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:uvg/@insured_id" />
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
												<xsl:when test="/invoice:request/invoice:payload/@copy = 'true' or /invoice:request/invoice:payload/@copy = '1'">
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
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:kvg) > 0">
													<xsl:value-of select="'KVG'" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:mvg) > 0">
													<xsl:value-of select="'MVG'" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:ivg) > 0">
													<xsl:value-of select="'IVG'" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:vvg) > 0">
													<xsl:value-of select="'VVG'" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:org) > 0">
													<xsl:value-of select="'ORG'" />
												</xsl:when>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:uvg) > 0">
													<xsl:value-of select="'UVG'" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="'privat'" />
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
											Betriebs-Nr./Name
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block white-space-treatment="preserve">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:employer/@reg_number" />
													/
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:employer/@reg_number" />
													/
												</xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="3">
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:employer/invoice:company) > 0">
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:employer/invoice:company/invoice:companyname,
																 ' ',
																 /invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:employer/invoice:company/invoice:postal/invoice:street)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:employer/invoice:person/@salutation,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:employer/invoice:person/invoice:familyname,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:employer/invoice:person/invoice:givenname,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:employer/invoice:person/invoice:postal/invoice:street)" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													<xsl:choose>
														<xsl:when
															test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:employer/invoice:company) > 0">
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:employer/invoice:company/invoice:companyname,
																 ' ',
																 /invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:employer/invoice:company/invoice:postal/invoice:street)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of
																select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:employer/invoice:person/@salutation,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:employer/invoice:person/invoice:familyname,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:employer/invoice:person/invoice:givenname,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:employer/invoice:person/invoice:postal/invoice:street)" />
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

								<fo:table-row border-width="0.5pt" border-color="black"
									border-style="solid">
									<fo:table-cell>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											Zuweiser
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											GLN-Nr. / ZSR-Nr.
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2">
										<fo:block white-space-treatment="preserve">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:referrer/@ean_party" />
													/
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:referrer/@zsr" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:referrer/@ean_party" />
													/
													<xsl:value-of
														select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:referrer/@zsr" />
												</xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="' '" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2">
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											<xsl:choose>
												<xsl:when
													test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
													<xsl:value-of
														select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:referrer/invoice:person/@title,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:referrer/invoice:person/invoice:familyname,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:referrer/invoice:person/invoice:givenname,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:referrer/invoice:person/invoice:postal/invoice:street,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:referrer/invoice:person/invoice:postal/invoice:zip,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:referrer/invoice:person/invoice:postal/invoice:city)" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of
														select="concat(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:referrer/invoice:person/@title,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:referrer/invoice:person/invoice:familyname,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:referrer/invoice:person/invoice:givenname,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:referrer/invoice:person/invoice:postal/invoice:street,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:referrer/invoice:person/invoice:postal/invoice:zip,
																' ',
																/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:referrer/invoice:person/invoice:postal/invoice:city)" />
												</xsl:otherwise>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row border-width="0.5pt" border-color="black"
									border-style="solid">
									<fo:table-cell>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											Diagnose
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">
											<xsl:if
												test="/invoice:request/invoice:payload/invoice:body/invoice:treatment/invoice:diagnosis/@type = 'by_contract'">
												Contract
											</xsl:if>
											<xsl:if
												test="/invoice:request/invoice:payload/invoice:body/invoice:treatment/invoice:diagnosis/@type = 'freetext'">
												Freitext
											</xsl:if>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="3">
										<fo:block white-space-treatment="preserve">
											<xsl:for-each
												select="/invoice:request/invoice:payload/invoice:body/invoice:treatment/invoice:diagnosis" group-by="@type">
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

								<fo:table-row border-width="0.5pt" border-color="black"
									border-style="solid">
									<fo:table-cell>
										<fo:block font-size="7px" font-weight="bold"
											font-family="tahoma,arial,helvetica,sans-serif">
											GLN-Liste
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="7px"
											font-family="tahoma,arial,helvetica,sans-serif">

										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="4">
										<fo:block-container height="0.8cm">
											<fo:block>
												<xsl:value-of select="$eanList" />
											</fo:block>
										</fo:block-container>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row border-width="0.5pt" border-color="black"
									border-style="solid">
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
											font-family="tahoma,arial,helvetica,sans-serif" text-align="center">
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
						<fo:block font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
							Datum
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="0.8cm">
						<fo:block text-align="center" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Tarif
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="1.5cm">
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
							Si
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
					<fo:table-cell width="0.4cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							V
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="0.4cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							P
						</fo:block>
					</fo:table-cell>
					<fo:table-cell width="0.4cm">
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							M
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
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
					<xsl:if test="local-name() = 'record_tarmed'">
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
						<xsl:with-param name="Number" select="@scale_factor_mt" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:choose>
						<xsl:when test="local-name() = 'service_ex'">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="@unit_factor_mt" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="@unit_factor" />
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
						<xsl:with-param name="Number" select="@scale_factor_tt" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:value-of select="@unit_factor_tt" />
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
					<xsl:call-template name="BooleanToNumber">
						<xsl:with-param name="Boolean" select="@obligation" />
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
				<fo:block font-size="7px" font-family="tahoma,arial,helvetica,sans-serif">
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
							font-weight="bold" font-family="tahoma,arial,helvetica,sans-serif">
							Code
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-weight="bold" font-family="tahoma,arial,helvetica,sans-serif">
							Satz
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Betrag
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							MWSt
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							MwSt.Nr.:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="left" font-size="7px"
							white-space-treatment="preserve" font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of select="' '" />
							<xsl:value-of select="' '" />
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/invoice:vat/@vat_number" />
							<xsl:value-of select="' '" />
							MWST
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Anzahlung:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							white-space-treatment="preserve" font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of select="' '" />
							<xsl:value-of select="' '" />
							<xsl:value-of select="$amountPrepaid" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px" font-weight="bold"
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
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							padding-right="7px" font-family="tahoma,arial,helvetica,sans-serif">
							Währung:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="left" font-size="7px"
							white-space-treatment="preserve" font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of select="' '" />
							<xsl:value-of select="' '" />
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@currency" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="left" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:if test="string-length(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount_reminder) > 0">
							Mahngebühr:
							</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:if test="string-length(/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount_reminder) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount_reminder" />
							</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							davon PFL:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:balance/@amount_obligations" />
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
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Fälliger Betrag:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="8px" font-weight="bold"
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
							font-weight="bold" font-family="tahoma,arial,helvetica,sans-serif">
							Code
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" font-size="7px"
							font-weight="bold" font-family="tahoma,arial,helvetica,sans-serif">
							Satz
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Betrag
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							MWSt
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							MwSt.Nr.:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="left" font-size="7px"
							white-space-treatment="preserve" font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of select="' '" />
							<xsl:value-of select="' '" />
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/invoice:vat/@vat_number" />
							<xsl:value-of select="' '" />
							MWST
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Anzahlung:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							white-space-treatment="preserve" font-family="tahoma,arial,helvetica,sans-serif">
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
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Gesamtbetrag:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount" />
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
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							padding-right="7px" font-family="tahoma,arial,helvetica,sans-serif">
							Währung:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="left" font-size="7px"
							white-space-treatment="preserve" font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:value-of select="' '" />
							<xsl:value-of select="' '" />
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@currency" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="left" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:if test="string-length(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_reminder) > 0">
							Mahngebühr:
							</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:if test="string-length(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_reminder) > 0">
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_reminder" />
							</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							davon PFL:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_obligations" />
							</xsl:call-template>
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
						<fo:block text-align="right" font-size="7px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							Fälliger Betrag:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right" font-size="8px" font-weight="bold"
							font-family="tahoma,arial,helvetica,sans-serif">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number" select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:balance/@amount_due" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template name='reclaim_esrline'>
		<fo:block font-family="OCRB" font-size="10px" text-align="right"
			width="11.1cm">
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
					<xsl:value-of select="substring-after($vatList,concat($Index, '/'))" />
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="contains($matchingAfterPart, ' ')">
						<xsl:value-of select="substring-before($matchingAfterPart,' ')" />
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
			<xsl:value-of select="substring-after($DateTime,'T')" />
		</xsl:variable>

		<xsl:value-of select="$day" />
		<xsl:value-of select="'.'" />
		<xsl:value-of select="$month" />
		<xsl:value-of select="'.'" />
		<xsl:value-of select="$year" />
		<xsl:value-of select="' '" />
		<xsl:value-of select="$time" />
	</xsl:template>
</xsl:stylesheet>