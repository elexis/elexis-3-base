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
	<xsl:param name="couvertLeft" select="''" />
	<xsl:param name="vatList" select="''" />
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
					<fo:region-body margin-top="3cm"
						margin-bottom="1.2cm" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="LD2-S">
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
									<xsl:choose>
										<xsl:when
											test="string-length($couvertLeft) > 1">
											<fo:table-row>
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
												<fo:table-cell>
													<fo:block margin-top="1cm">
														<fo:block font-size="7px">
															Rechnungssteller
														</fo:block>
														<xsl:call-template name="patbill_biller_address">
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row>
												<fo:table-cell display-align="after">
													<fo:block>
														<fo:block font-size="7px">
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block margin-top="5mm">
														<fo:block font-size="7px">
															Rechnungsinformationen
														</fo:block>
														<xsl:call-template name="patbill_bill_info">
														</xsl:call-template>
														Patient: <xsl:call-template name="patbill_patient_info">
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
														Patient: <xsl:call-template name="patbill_patient_info">
														</xsl:call-template>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell display-align="after">
													<fo:block>
														<fo:block font-size="7px">
														</fo:block>
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
												Honorar-Rechnung
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block-container>
					</fo:block-container>
					<!-- normal content -->
					<fo:block>
						<xsl:call-template name="reclaim_body">
						</xsl:call-template>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
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
					<fo:table-cell width="1.5cm">
						<fo:block text-align="left" font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Tarifziffer
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
					<xsl:call-template name="FormatNumber">
						<xsl:with-param name="Number" select="@quantity" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" font-size="9px"
					font-family="tahoma,arial,helvetica,sans-serif">
					<xsl:choose>
						<xsl:when test="local-name() = 'record_tarmed'">
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
						<xsl:when test="local-name() = 'record_tarmed'">
							<xsl:call-template name="FormatNumber">
								<xsl:with-param name="Number"
									select="@unit_factor_mt*@external_factor_mt" />
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
					<xsl:value-of select="@unit_tt" />
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
					<xsl:value-of select="@amount" />
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
	
	<xsl:include href="/rsc/patbilltemplates.xsl" />
</xsl:stylesheet>
