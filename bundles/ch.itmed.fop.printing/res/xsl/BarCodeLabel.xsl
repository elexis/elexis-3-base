<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:str="http://exslt.org/strings">
	<xsl:output method="xml" indent="yes" />
		<xsl:variable name="pageWidth">
		<xsl:value-of select="Page/@pageWidth" />
	</xsl:variable>
	<xsl:variable name="pageHeight">
		<xsl:value-of select="Page/@pageHeight" />
	</xsl:variable>
	<xsl:variable name="marginTop">
		<xsl:value-of select="Page/@marginTop" />
	</xsl:variable>
	<xsl:variable name="marginBottom">
		<xsl:value-of select="Page/@marginBottom" />
	</xsl:variable>
	<xsl:variable name="marginLeft">
		<xsl:value-of select="Page/@marginLeft" />
	</xsl:variable>
	<xsl:variable name="marginRight">
		<xsl:value-of select="Page/@marginRight" />
	</xsl:variable>
	<xsl:variable name="textOrientation">
		<xsl:value-of select="Page/@textOrientation" />
	</xsl:variable>
	<xsl:variable name="BarcodeKennung">
		<xsl:value-of select="Page/Etikette/@barcodeLabel" />
	</xsl:variable>
	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="customEtikette"
					page-width="{$pageWidth}" page-height="{$pageHeight}" margin-top="{$marginTop}"
					margin-bottom="{$marginBottom}" margin-left="{$marginLeft}"
					margin-right="{$marginRight}">
					<fo:region-body />
					<fo:region-after />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<!-- XSL Stylesheet Patientenetikette mit variabler Länge und Breite -->
			<!-- (c) MEDEVIT OG 2011; All rights reserved -->
			<fo:page-sequence master-reference="customEtikette">
				<fo:flow flow-name="xsl-region-body"
				 font="8pt Helvetica"
						font-weight="normal">
					<fo:block-container reference-orientation="{$textOrientation}" text-align="center">
						<fo:block>
							<fo:instream-foreign-object>
								<barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns"
									message="{$BarcodeKennung}">
									<barcode:code128>
										<barcode:height>14</barcode:height>
										<barcode:human-readable>
											<barcode:placement>none</barcode:placement>
										</barcode:human-readable>
									</barcode:code128>
								</barcode:barcode>
							</fo:instream-foreign-object>
						</fo:block>
					<fo:block>
					<xsl:value-of select="/Page/Patient/FirstName" />
					&#160;
					<xsl:value-of select="/Page/Patient/LastName" />
					&#160;(
					<xsl:value-of select="/Page/Patient/Sex" />
					)
					,&#160;
					<xsl:value-of select="/Page/Patient/Birthdate" />
					</fo:block>
					<fo:block>
					<xsl:value-of select="/Page/Case/CostBearer" />
					,&#160;
					<xsl:value-of select="/Page/Case/InsurancePolicyNumber" />
					</fo:block>
					</fo:block-container>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
</xsl:stylesheet>
