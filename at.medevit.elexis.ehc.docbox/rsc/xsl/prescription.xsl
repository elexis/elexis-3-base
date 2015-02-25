<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cda="urn:hl7-org:v3"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	exclude-result-prefixes="fo">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="no"
		indent="yes" encoding="UTF-8" />
	<xsl:param name="versionParam" select="'1.0'" />

	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="simpleA4"
					page-height="29.7cm" page-width="21cm" margin-top="2cm"
					margin-bottom="2cm" margin-left="1.5cm" margin-right="1.5cm">
					<fo:region-body />
				</fo:simple-page-master>
			</fo:layout-master-set>

			<fo:page-sequence master-reference="simpleA4">
				<fo:flow flow-name="xsl-region-body">
					<fo:block font-size="10pt" border-top-style="solid"
						border-top-width="thin" border-bottom-width="thin"
						border-bottom-style="solid" padding-top="2mm" padding-bottom="2mm">
						<fo:table width="100%">
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="12pt" font-weight="bold">
											<xsl:value-of
												select="cda:ClinicalDocument/cda:author/cda:assignedAuthor/cda:assignedPerson/cda:name/cda:prefix" />
											<fo:inline>&#160;</fo:inline>
											<xsl:value-of
												select="cda:ClinicalDocument/cda:author/cda:assignedAuthor/cda:assignedPerson/cda:name/cda:given" />
											<fo:inline>&#160;</fo:inline>
											<xsl:value-of
												select="cda:ClinicalDocument/cda:author/cda:assignedAuthor/cda:assignedPerson/cda:name/cda:family" />
										</fo:block>
										<fo:block>
											<xsl:value-of
												select="cda:ClinicalDocument/cda:custodian/cda:assignedCustodian/cda:representedCustodianOrganization/cda:name" />
										</fo:block>
										<fo:block>
											<xsl:value-of
												select="cda:ClinicalDocument/cda:custodian/cda:assignedCustodian/cda:representedCustodianOrganization/cda:addr/cda:streetAddressLine" />
										</fo:block>
										<fo:block>
											<xsl:value-of
												select="cda:ClinicalDocument/cda:custodian/cda:assignedCustodian/cda:representedCustodianOrganization/cda:addr/cda:postalCode" />
											<fo:inline>&#160;</fo:inline>
											<xsl:value-of
												select="cda:ClinicalDocument/cda:custodian/cda:assignedCustodian/cda:representedCustodianOrganization/cda:addr/cda:city" />
										</fo:block>

										<fo:block>
											Tel:
											<xsl:value-of
												select="substring-after(cda:ClinicalDocument/cda:custodian/cda:assignedCustodian/cda:representedCustodianOrganization/cda:telecom/@value, ':')" />
										</fo:block>
										<fo:block>
											EAN:
											<xsl:value-of
												select="cda:ClinicalDocument/cda:author/cda:assignedAuthor/cda:id[@root='1.3.88']/@extension" />
										</fo:block>
										<fo:block>
											ZSR:
											<xsl:value-of
												select="cda:ClinicalDocument/cda:custodian/cda:assignedCustodian/cda:representedCustodianOrganization/cda:id[@root='2.16.756.5.30.1.105.1.1.2']/@extension" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right">
											<xsl:variable name="BarcodeKennung">
												<xsl:value-of
													select="cda:ClinicalDocument/cda:id[@root='2.16.756.5.30.1.105.1.6']/@extension" />
											</xsl:variable>
											<fo:instream-foreign-object>
												<barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns"
													message="{$BarcodeKennung}">
													<barcode:code128>
														<barcode:height>10mm</barcode:height>
													</barcode:code128>
												</barcode:barcode>
											</fo:instream-foreign-object>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>

					<fo:block space-before="10mm" font-size="12pt"
						font-weight="bold">
						Rezept
					</fo:block>

					<fo:block font-size="10pt" space-before="10mm">
						<xsl:value-of
							select="cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:name/cda:given" />
						<fo:inline>&#160;</fo:inline>
						<xsl:value-of
							select="cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:name/cda:family" />
						<fo:inline>&#44;</fo:inline>
						geboren am
						<xsl:call-template name="formatDate">
							<xsl:with-param name="date"
								select="cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:birthTime/@value" />
						</xsl:call-template>
						<fo:inline>&#44;&#160;</fo:inline>
						<xsl:value-of
							select="cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:addr/cda:streetAddressLine" />
						<fo:inline>&#160;</fo:inline>
						<xsl:value-of
							select="cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:addr/cda:postalCode" />
						<fo:inline>&#160;</fo:inline>
						<xsl:value-of
							select="cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:addr/cda:city" />
						<fo:inline>&#160;</fo:inline>
					</fo:block>

					<fo:block space-before="10mm" font-size="10pt">
						<fo:table width="100%">
							<fo:table-body>
								<xsl:apply-templates
									select="cda:ClinicalDocument/cda:component/cda:structuredBody/cda:component/cda:section" />
							</fo:table-body>
						</fo:table>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<xsl:template match="cda:entry/cda:substanceAdministration">
		<fo:table-row>
			<fo:table-cell>
				<fo:block>
					<xsl:value-of select="cda:text" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
				<fo:block>
					<fo:inline>&#160;</fo:inline>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="formatDate">
		<xsl:param name="date" />
		<xsl:choose>
			<xsl:when test="substring ($date, 7, 1)='0'">
				<xsl:value-of select="substring ($date, 8, 1)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="substring ($date, 7, 2)" />
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="$date != ''">
				<xsl:text>. </xsl:text>
			</xsl:when>
		</xsl:choose>
		<xsl:variable name="month" select="substring ($date, 5, 2)" />
		<xsl:choose>
			<xsl:when test="$month='01'">
				Januar
			</xsl:when>
			<xsl:when test="$month='02'">
				Februar
			</xsl:when>
			<xsl:when test="$month='03'">
				März
			</xsl:when>
			<xsl:when test="$month='04'">
				April
			</xsl:when>
			<xsl:when test="$month='05'">
				Mai
			</xsl:when>
			<xsl:when test="$month='06'">
				Juni
			</xsl:when>
			<xsl:when test="$month='07'">
				Juli
			</xsl:when>
			<xsl:when test="$month='08'">
				August
			</xsl:when>
			<xsl:when test="$month='09'">
				September
			</xsl:when>
			<xsl:when test="$month='10'">
				Oktober
			</xsl:when>
			<xsl:when test="$month='11'">
				November
			</xsl:when>
			<xsl:when test="$month='12'">
				Dezember
			</xsl:when>
		</xsl:choose>
		<xsl:text> </xsl:text>
		<xsl:value-of select="substring ($date, 1, 4)" />
	</xsl:template>
</xsl:stylesheet>