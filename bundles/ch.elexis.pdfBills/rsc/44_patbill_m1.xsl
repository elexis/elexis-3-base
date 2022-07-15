<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:invoice="http://www.forum-datenaustausch.ch/invoice">
	<xsl:param name="printerType" select="''" />
	<xsl:param name="leftMargin" select="''" />
	<xsl:param name="rightMargin" select="''" />
	<xsl:param name="topMargin" select="''" />
	<xsl:param name="bottomMargin" select="''" />
	<xsl:param name="besrMarginVertical" select="''" />
	<xsl:param name="besrMarginHorizontal" select="''" />
	<xsl:param name="eanList" select="''" />
	<xsl:param name="headerLine1" select="''" />
	<xsl:param name="headerLine2" select="''" />
	<xsl:param name="guarantorPostal" select="''" />
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
					<fo:region-body margin-bottom="10.5cm" />
					<fo:region-after extent="10.5cm" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="LD2-S">
				<fo:static-content flow-name="xsl-region-after">
					<xsl:call-template name="patbill_esr">
					</xsl:call-template>
				</fo:static-content>

				<fo:flow flow-name="xsl-region-body">
					<fo:block-container height="20mm">
						<fo:table table-layout="fixed" width="100%"
									border-collapse="collapse" margin-top="10mm">
							<fo:table-body font-family="tahoma,arial,helvetica,sans-serif">
								<fo:table-row background-color="#dddddd">
									<fo:table-cell>
										<fo:block font-size="18px" font-weight="bold" text-align="center">
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
								<fo:table-body font-size="10px"
									font-family="tahoma,arial,helvetica,sans-serif">
									<fo:table-row>
										<fo:table-cell>
											<fo:block margin-top="1cm">
												<fo:block font-size="7px">
													Rechnungssteller
												</fo:block>
												<xsl:call-template name="patbill_biller_address">
												</xsl:call-template>
											</fo:block>

										</fo:table-cell>
										<fo:table-cell>
											<fo:block margin-top="1cm">
												<fo:block font-size="7px" margin-bottom="3mm">
													<xsl:call-template name="patbill_header_line">
													</xsl:call-template>
												</fo:block>
												<xsl:call-template name="patbill_garant_address">
												</xsl:call-template>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block margin-top="5mm">
												<fo:block font-size="7px">
													Rechnungsinformationen
												</fo:block>
												<xsl:call-template name="patbill_bill_info">
												</xsl:call-template>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell display-align="after">
											<fo:block>
												<fo:block font-size="7px">
													Patient
												</fo:block>
												<xsl:call-template name="patbill_patient_info">
												</xsl:call-template>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
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
												Zahlungserinnerung
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block margin-top="2mm" margin-bottom="2mm">
												<xsl:call-template name="patbill_garant_salutation">
												</xsl:call-template>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block>
Bei der Durchsicht unserer Buchhaltung haben wir festgestellt, dass oben genannte Rechnung noch nicht bezahlt ist. Wir bitten Sie höflich, den Betrag baldmöglichst zu überweisen. Sollte sich die Zahlung mit dieser Zahlungserinnerung kreuzen, betrachten Sie dieses Schreiben als gegenstandslos.
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block margin-top="2mm">
												<xsl:call-template name="patbill_services_overview">
												</xsl:call-template>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block-container>
					</fo:block-container>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<xsl:include href="/rsc/patbilltemplates.xsl" />
</xsl:stylesheet>
