<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">
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
	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master
					master-name="PatientLabel" page-width="{$pageWidth}"
					page-height="{$pageHeight}" margin-top="{$marginTop}"
					margin-bottom="{$marginBottom}" margin-left="{$marginLeft}"
					margin-right="{$marginRight}">
					<fo:region-body />
					<fo:region-after />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="PatientLabel">
				<fo:flow flow-name="xsl-region-body"
					reference-orientation="{$textOrientation}">
					<fo:block-container font="8pt Helvetica"
						font-weight="normal">
						<xsl:apply-templates />
					</fo:block-container>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="Patient">
		<fo:block>
			Patienten-Nr:&#160;
			<xsl:value-of select="PID" />
		</fo:block>
		<fo:block>
			Auftrags-Nr.:&#160;
			<xsl:value-of select="OrderNumber" />
		</fo:block>
		<fo:block>
			<xsl:value-of select="FirstName" />
			&#160;
			<xsl:value-of select="LastName" />
			&#160;(
			<xsl:value-of select="Sex" />
			)
			,&#160;
			<xsl:value-of select="Birthdate" />
		</fo:block>
		<fo:block>
			<xsl:value-of select="Street" />
		</fo:block>
		<fo:block>
			<xsl:value-of select="PostalCode" />
			&#160;
			<xsl:value-of select="City" />
		</fo:block>
		<fo:block>
			Tel
			<xsl:if test="/Page/Patient/Phone1/text()">
				&#160;P:&#160;
				<xsl:value-of select="Phone1" />
			</xsl:if>
			<xsl:if test="/Page/Patient/MobilePhone/text()">
				&#160;Mobil:&#160;
				<xsl:value-of select="MobilePhone" />
			</xsl:if>
		</fo:block>
	</xsl:template>
	<xsl:template match="Case">
			<xsl:if test="/Page/Case/CostBearer/text()">
			<fo:block>
			Versicherung:&#160;
				<xsl:value-of select="CostBearer" />
			</fo:block>
		</xsl:if>
		<xsl:if test="/Page/Case/InsurancePolicyNumber/text()">
			<fo:block>
			Nr:&#160;
				<xsl:value-of select="InsurancePolicyNumber" />
			</fo:block>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>