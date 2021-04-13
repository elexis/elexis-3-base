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
					master-name="ContactAddressLabel" page-width="{$pageWidth}"
					page-height="{$pageHeight}" margin-top="{$marginTop}"
					margin-bottom="{$marginBottom}" margin-left="{$marginLeft}"
					margin-right="{$marginRight}">
					<fo:region-body />
					<fo:region-after />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence
				master-reference="ContactAddressLabel">
				<fo:flow flow-name="xsl-region-body"
					reference-orientation="{$textOrientation}">
					<fo:block-container font="12pt Helvetica"
						font-weight="normal">
						<xsl:apply-templates />
					</fo:block-container>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="Contact">
		<xsl:for-each select="Address/Part">
			<fo:block>
				<xsl:value-of select="." />
			</fo:block>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>