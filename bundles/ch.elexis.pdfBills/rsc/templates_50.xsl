<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:invoice="http://www.forum-datenaustausch.ch/invoice">
	
		<xsl:template name="billheader2D">
		<xsl:param name="Type" />
		<xsl:param name="Title" />
		<xsl:variable name="messageVar">
			<xsl:value-of
				select="concat(concat('FD50', concat(/invoice:request/@guid, /invoice:request/@language)), concat($Type ,'#page-number:00#'))" />
		</xsl:variable>

		<fo:table table-layout="fixed" border-collapse="collapse">
			<fo:table-column column-width="50%"
				text-align="right" />
			<fo:table-column column-width="50%"
				text-align="right" />
			<fo:table-body
				font-family="tahoma,arial,helvetica,sans-serif">
				<fo:table-row>
					<fo:table-cell>
						<fo:block padding-top="2mm" font-weight="bold"
							font-size="14">
							<xsl:value-of select="$Title" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-left="5cm"
						display-align="after">

						<fo:table width="6.5cm">
							<fo:table-body font-size="7px">
								<fo:table-row>
									<fo:table-cell padding-right="2mm">
										<fo:block padding-bottom="1mm" padding-top="1mm"
											border-bottom-style="solid" border-bottom-width="1pt"
											text-align="right">
											<xsl:if test="contains($Type, 'SR')">
												Release 5.0/QR/de
											</xsl:if>
											<xsl:if test="contains($Type, 'GR')">
												Release 5.0/General/de
											</xsl:if>
											<xsl:if test="contains($Type, 'AX')">
												Release 5.0/Annex/de
											</xsl:if>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell padding-left="2mm"
										border-left-style="solid" border-left-width="1pt"
										number-rows-spanned="2">
										<fo:block>
											<fo:instream-foreign-object>
												<barcode:barcode
													xmlns:barcode="http://barcode4j.krysalis.org/ns"
													message="{$messageVar}">
													<barcode:datamatrix>
														<barcode:min-symbol-size>24x24</barcode:min-symbol-size>
														<barcode:max-symbol-size>24x24</barcode:max-symbol-size>
													</barcode:datamatrix>
												</barcode:barcode>
											</fo:instream-foreign-object>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-right="2mm">
										<fo:block padding-bottom="1mm" text-align="right">
											<xsl:if test="contains($Type, 'SR')">
												Für Ihre Unterlagen
											</xsl:if>
											<xsl:if test="contains($Type, 'GR')">
												Der Versicherung zustellen
											</xsl:if>
											<xsl:if test="contains($Type, 'AX')">
												Der Versicherung zustellen
											</xsl:if>
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