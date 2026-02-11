<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:invoice="http://www.forum-datenaustausch.ch/invoice">
	<xsl:param name="printerType" select="''" />
	<xsl:param name="leftMargin" select="''" />
	<xsl:param name="rightMargin" select="''" />
	<xsl:param name="topMargin" select="''" />
	<xsl:param name="bottomMargin" select="''" />
	<xsl:param name="eanList" select="''" />
	<xsl:param name="headerLine1" select="''" />
	<xsl:param name="headerLine2" select="''" />
	<xsl:param name="messageText" select="''" />
	<xsl:param name="qrJpeg" select="''" />
	<xsl:param name="guarantorPostal" select="''" />
	<xsl:param name="couvertLeft" select="''" />
	<xsl:param name="billerLine" select="''" />
	<xsl:param name="guarantorLine" select="''" />
	<xsl:param name="insuranceLine" select="''" />
	<xsl:param name="creditorLine" select="''" />
	<xsl:param name="amountPrepaid" select="''" />
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
					margin-bottom="0mm" margin-left="{$leftMargin}"
					margin-right="{$rightMargin}">
					<fo:region-body margin-top="0.5cm"
						margin-bottom="10.8cm" />
					<fo:region-before extent="3cm" />
					<fo:region-after extent="10.8cm" />
				</fo:simple-page-master>
				<fo:simple-page-master master-name="Overview"
					page-height="29.7cm" page-width="21cm" margin-top="{$topMargin}"
					margin-bottom="0mm" margin-left="{$leftMargin}"
					margin-right="{$rightMargin}">
					<fo:region-body margin-top="1.5cm"
						margin-bottom="2cm" />
					<fo:region-before extent="3cm" />
					<fo:region-after region-name="last-footer" extent="2cm"
						display-align="after" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="LD2-S">
				<fo:static-content flow-name="xsl-region-before">
					<!-- main header on every page -->
					<xsl:choose>
						<xsl:when
							test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
							<xsl:call-template name="billheader2D">
								<xsl:with-param name="Type" select="'GSR'">
								</xsl:with-param>
								<xsl:with-param name="Title" select="'Patientenrechnung mit QR-Code'">
								</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="billheader2D">
								<xsl:with-param name="Type" select="'PSR'">
								</xsl:with-param>
								<xsl:with-param name="Title" select="'Debitorrechnung mit QR-Code'">
								</xsl:with-param>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</fo:static-content>

				<fo:static-content flow-name="xsl-region-after">
					<xsl:call-template name="qrpatbill_esr">
					</xsl:call-template>
				</fo:static-content>

				<fo:flow flow-name="xsl-region-body">
					<fo:block-container height="20mm">
						<fo:table table-layout="fixed" width="100%"
							border-collapse="collapse" margin-top="10mm">
							<fo:table-body
								font-family="tahoma,arial,helvetica,sans-serif">
								<fo:table-row background-color="#dddddd">
									<fo:table-cell>
										<fo:block font-size="18px" font-weight="bold"
											text-align="center">
											<xsl:value-of select="$headerLine1" />
										</fo:block>
										<fo:block font-size="10px" text-align="center">
											<xsl:value-of select="$headerLine2" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block-container>
					<!-- default leftMargin 10mm, rightMargin 7mm, topMargin 10mm -->
					<fo:block-container margin-left="0mm"
						margin-right="0mm" margin-top="5mm">
						<fo:block-container margin-left="0mm"
							margin-right="0mm" margin-top="0mm">
							<fo:table table-layout="fixed" width="100%"
								border-collapse="collapse">
								<fo:table-column column-width="10.5cm"
									text-align="left" />
								<fo:table-column />
								<fo:table-body font-size="8px"
									font-family="tahoma,arial,helvetica,sans-serif">
									<xsl:choose>
										<xsl:when test="string-length($couvertLeft) > 1">
											<fo:table-row>
												<fo:table-cell>
													<fo:block margin-top="1cm" font-size="10px">
														<fo:block font-size="7px" margin-bottom="3mm">
															<xsl:call-template
																name="patbill_header_line">
															</xsl:call-template>
														</fo:block>
														<xsl:call-template
															name="patbill_garant_address">
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block margin-top="1cm">
														<fo:block font-size="7px">
															RechnungsstellerIn
														</fo:block>
														<xsl:call-template
															name="patbill_biller_address_50">
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row>
												<fo:table-cell display-align="after">
													<fo:block margin-top="15mm">
														<fo:block font-size="7px">
															PatientIn
														</fo:block>
														<xsl:call-template
															name="patbill_patient_address_50">
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block margin-top="5mm">
														<fo:block font-size="7px">
															LeistungserbringerIn
														</fo:block>
														<xsl:call-template
															name="patbill_provider_address_50">
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</xsl:when>
										<xsl:otherwise>
											<fo:table-row>
												<fo:table-cell>
													<fo:block margin-top="1cm">
														<fo:block font-size="7px">
															RechnungsstellerIn
														</fo:block>
														<xsl:call-template
															name="patbill_biller_address_50">
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block margin-top="1cm" font-size="10px">
														<fo:block font-size="7px" margin-bottom="3mm">
															<xsl:call-template
																name="patbill_header_line">
															</xsl:call-template>
														</fo:block>
														<xsl:call-template
															name="patbill_garant_address">
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row>
												<fo:table-cell>
													<fo:block margin-top="5mm">
														<fo:block font-size="7px">
															LeistungserbringerIn
														</fo:block>
														<xsl:call-template
															name="patbill_provider_address_50">
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell display-align="after">
													<fo:block margin-top="15mm">
														<fo:block font-size="7px">
															PatientIn
														</fo:block>
														<xsl:call-template
															name="patbill_patient_address_50">
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</xsl:otherwise>
									</xsl:choose>
								</fo:table-body>
							</fo:table>
							<fo:table table-layout="fixed" width="100%"
								border-collapse="collapse">
								<fo:table-column column-width="100%" />
								<fo:table-body font-size="10px"
									font-family="tahoma,arial,helvetica,sans-serif">
									<fo:table-row>
										<fo:table-cell>
											<fo:block margin-top="5mm" font-weight="bold">
												Dritte Mahnung
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block margin-top="2mm" margin-bottom="2mm">
												<xsl:call-template
													name="patbill_garant_salutation">
												</xsl:call-template>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block margin-bottom="5mm">
												<xsl:value-of select="$messageText" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block>
												<fo:inline-container
													inline-progression-dimension="40%">
													<fo:block display-align="before">
														<xsl:call-template
															name="patbill_bill_info_50">
														</xsl:call-template>
													</fo:block>
												</fo:inline-container>
												<fo:inline-container
													inline-progression-dimension="30%">
													<fo:block display-align="before">
														<xsl:call-template
															name="patbill_services_overview_50">
														</xsl:call-template>
													</fo:block>
												</fo:inline-container>
												<fo:inline-container
													inline-progression-dimension="30%">
													<fo:block display-align="before">
														<xsl:call-template
															name="patbill_vat_overview_50">
														</xsl:call-template>
													</fo:block>
												</fo:inline-container>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block-container>
					</fo:block-container>
				</fo:flow>
			</fo:page-sequence>
			<xsl:if
				test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
				<fo:page-sequence master-reference="Overview">
					<fo:static-content flow-name="xsl-region-before">
						<xsl:call-template name="billheader2D">
							<xsl:with-param name="Type" select="'GSR'">
							</xsl:with-param>
							<xsl:with-param name="Title" select="'Leistungsübersicht zur Behandlung'">
							</xsl:with-param>
						</xsl:call-template>
					</fo:static-content>
					<fo:static-content flow-name="last-footer">
						<fo:block-container height="20mm">
							<fo:block display-align="after">
								<xsl:call-template name="overview_footer">
								</xsl:call-template>
							</fo:block>
						</fo:block-container>
					</fo:static-content>
					<fo:flow flow-name="xsl-region-body">
						<fo:block-container height="20mm">
							<fo:block>
							<xsl:call-template name="overview_body">
							</xsl:call-template>
						</fo:block>
						</fo:block-container>
					</fo:flow>
				</fo:page-sequence>	
			</xsl:if>
		</fo:root>
	</xsl:template>

	<xsl:include href="/rsc/templates_50.xsl" />
	<xsl:include href="/rsc/qrpatbilltemplates.xsl" />
	<xsl:include href="/rsc/qrpatbilltemplates_50.xsl" />
</xsl:stylesheet>
