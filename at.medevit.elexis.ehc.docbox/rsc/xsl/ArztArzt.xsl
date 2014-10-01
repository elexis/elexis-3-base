<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:n1="urn:hl7-org:v3"
	xmlns:n2="urn:hl7-org:v3/meta/voc" xmlns:voc="urn:hl7-org:v3/voc"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output method="xml" omit-xml-declaration="yes" indent="yes"
		version="1.0" encoding="ISO-8859-1" />
	<xsl:variable name="language">
		<xsl:choose>
			<xsl:when test="/n1:ClinicalDocument/n1:languageCode/@code">
				<xsl:value-of select="/n1:ClinicalDocument/n1:languageCode/@code" />
			</xsl:when>
			<xsl:otherwise>
				de-CH
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:template match="/">
		<xsl:apply-templates select="n1:ClinicalDocument" />
	</xsl:template>
	<xsl:template match="n1:ClinicalDocument">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="simpleA4"
					page-height="29.7cm" page-width="21cm" margin-top="2cm"
					margin-bottom="2cm" margin-left="1.5cm" margin-right="1.5cm">
					<fo:region-body />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="simpleA4">
				<fo:flow flow-name="xsl-region-body">
					<!-- Datum -->
					<fo:block font-size="11pt">
						<xsl:value-of
							select="/n1:ClinicalDocument/n1:custodian/n1:assignedCustodian/n1:representedCustodianOrganization/n1:addr/n1:city" />
						<fo:inline>,&#160;</fo:inline>
						<xsl:call-template name="formatDate">
							<xsl:with-param name="date"
								select="/n1:ClinicalDocument/n1:effectiveTime/@value" />
						</xsl:call-template>
						<fo:inline>&#160;</fo:inline>
						<xsl:call-template name="formatTime">
							<xsl:with-param name="date"
								select="/n1:ClinicalDocument/n1:effectiveTime/@value" />
						</xsl:call-template>
					</fo:block>

					<!--  Absender -->
					<fo:block font-size="11pt" space-before="36pt">
						<fo:block>
							<xsl:variable name="prefix2">
								<xsl:value-of
									select="/n1:ClinicalDocument/n1:author/n1:assignedAuthor/n1:assignedPerson/n1:name/n1:prefix" />
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$prefix2 != ''">
									<xsl:value-of select="$prefix2" />
									<fo:inline>&#160;</fo:inline>
								</xsl:when>
							</xsl:choose>
							<xsl:value-of
								select="/n1:ClinicalDocument/n1:author/n1:assignedAuthor/n1:assignedPerson/n1:name/n1:given" />
							<fo:inline>&#160;</fo:inline>
							<xsl:value-of
								select="/n1:ClinicalDocument/n1:author/n1:assignedAuthor/n1:assignedPerson/n1:name/n1:family" />
						</fo:block>
						<fo:block>
							<xsl:value-of
								select="/n1:ClinicalDocument/n1:custodian/n1:assignedCustodian/n1:representedCustodianOrganization/n1:name" />
						</fo:block>
						<fo:block>
							<xsl:value-of
								select="/n1:ClinicalDocument/n1:custodian/n1:assignedCustodian/n1:representedCustodianOrganization/n1:addr/n1:streetAddressLine" />
						</fo:block>
						<fo:block>
							<xsl:value-of
								select="/n1:ClinicalDocument/n1:custodian/n1:assignedCustodian/n1:representedCustodianOrganization/n1:addr/n1:postalCode" />
							<fo:inline>&#160;</fo:inline>
							<xsl:value-of
								select="/n1:ClinicalDocument/n1:custodian/n1:assignedCustodian/n1:representedCustodianOrganization/n1:addr/n1:city" />
						</fo:block>
					</fo:block>

					<!-- Empfänger -->
					<fo:table table-layout="fixed" width="100%">
						<fo:table-column column-number="1" column-width="12cm" />
						<fo:table-column column-number="2" column-width="6cm" />
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block />
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<xsl:variable name="prefix1">
											<xsl:value-of
												select="/n1:ClinicalDocument/n1:informationRecipient/n1:intendedRecipient/n1:informationRecipient/n1:name/n1:prefix" />
										</xsl:variable>
										<xsl:choose>
											<xsl:when test="$prefix1 != ''">
												<xsl:value-of select="$prefix1" />
												<fo:inline>&#160;</fo:inline>
											</xsl:when>
										</xsl:choose>
										<xsl:value-of
											select="/n1:ClinicalDocument/n1:informationRecipient/n1:intendedRecipient/n1:informationRecipient/n1:name/n1:given" />
										<fo:inline>&#160;</fo:inline>
										<xsl:value-of
											select="/n1:ClinicalDocument/n1:informationRecipient/n1:intendedRecipient/n1:informationRecipient/n1:name/n1:family" />
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block />
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<xsl:value-of
											select="/n1:ClinicalDocument/n1:informationRecipient/n1:intendedRecipient/n1:receivedOrganization/n1:name" />
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block />
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<xsl:value-of
											select="/n1:ClinicalDocument/n1:informationRecipient/n1:intendedRecipient/n1:receivedOrganization/n1:addr/n1:streetAddressLine" />
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block />
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<xsl:value-of
											select="/n1:ClinicalDocument/n1:informationRecipient/n1:intendedRecipient/n1:receivedOrganization/n1:addr/n1:postalCode" />
										<fo:inline>&#160;</fo:inline>
										<xsl:value-of
											select="/n1:ClinicalDocument/n1:informationRecipient/n1:intendedRecipient/n1:receivedOrganization/n1:addr/n1:city" />
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>

					<fo:block font-size="16pt"  font-weight="bold" space-before="36pt">
						<xsl:value-of select="/n1:ClinicalDocument/n1:title" />
					</fo:block>
					<fo:block font-size="11pt" space-before="36pt">
						<xsl:value-of select="/n1:ClinicalDocument/n1:component/n1:structuredBody/n1:component/n1:section[n1:code[@nullFlavor='NA']/n1:translation[@code='NOTIZ']]/n1:text" />
					</fo:block>
				
					<fo:block font-size="11pt" font-weight="bold" space-after="6pt"
						space-before="36pt">
						Patient
	                	</fo:block>
	                	
					<fo:block font-size="11pt">

					<fo:block>
						<xsl:value-of
							select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:name/n1:given" />
						<fo:inline>&#160;</fo:inline>
						<xsl:value-of
							select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:name/n1:family" />
					</fo:block>
					<fo:block>
						<xsl:value-of
							select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:addr/n1:streetAddressLine" />
					</fo:block>
					<fo:block>
						<xsl:value-of
							select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:addr/n1:postalCode" />
						<fo:inline>&#160;</fo:inline>
						<xsl:value-of
							select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:addr/n1:city" />
					</fo:block>

					<fo:block>
						Tel:
						<xsl:value-of
							select="substring-after(/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:telecom/@value, ':')" />
					</fo:block>

					<fo:block>
						Geboren am:
						<xsl:call-template name="formatDate">
							<xsl:with-param name="date"
								select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:birthTime/@value" />
						</xsl:call-template>
					</fo:block>

					<fo:block>
						Geschlecht:
						<xsl:call-template name="formatSex">
							<xsl:with-param name="sex"
								select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:administrativeGenderCode/@code" />
						</xsl:call-template>
					</fo:block>
					</fo:block>

				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template name="formatPhoneName">
		<xsl:param name="telecom" />
		<xsl:choose>
			<xsl:when test="$telecom='HP'">
				home_phone:
			</xsl:when>
			<xsl:when test="$telecom='WP'">
				work_phone:
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="formatPhoneValue">
		<xsl:param name="telecom" />
		<xsl:choose>
			<xsl:when test="substring ($telecom, 1, 4)='tel:'">
				<xsl:value-of select="substring ($telecom, 5, 256)" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="formatTelecomName">
		<xsl:param name="telecom" />
		<xsl:choose>
			<xsl:when test="substring ($telecom, 1, 4)='tel:'">
				mobile:
			</xsl:when>
			<xsl:when test="substring ($telecom, 1, 4)='fax:'">
				fax:
			</xsl:when>
			<xsl:when test="substring ($telecom, 1, 7)='mailto:'">
				email:
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="formatTelecomValue">
		<xsl:param name="telecom" />
		<xsl:choose>
			<xsl:when test="substring ($telecom, 1, 4)='tel:'">
				<xsl:value-of select="substring ($telecom, 5, 256)" />
			</xsl:when>
			<xsl:when test="substring ($telecom, 1, 4)='fax:'">
				<xsl:value-of select="substring ($telecom, 5, 256)" />
			</xsl:when>
			<xsl:when test="substring ($telecom, 1, 7)='mailto:'">
				<xsl:value-of select="substring ($telecom, 8, 256)" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="formatSex">
		<xsl:param name="sex" />
		<xsl:choose>
			<xsl:when test="$sex='M'">
				m
			</xsl:when>
			<xsl:when test="$sex='F'">
				w
			</xsl:when>
			<xsl:otherwise>
				-
			</xsl:otherwise>
		</xsl:choose>
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
	<xsl:template name="formatTime">
		<xsl:param name="date" />
		<xsl:choose>
			<xsl:when test="substring ($date, 9, 1)='0'">
				<xsl:value-of select="substring ($date, 10, 1)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="substring ($date, 9, 2)" />
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="$date != ''">
				<xsl:text>:</xsl:text>
			</xsl:when>
		</xsl:choose>
		<xsl:value-of select="substring ($date, 11, 2)" />
	</xsl:template>
</xsl:stylesheet>