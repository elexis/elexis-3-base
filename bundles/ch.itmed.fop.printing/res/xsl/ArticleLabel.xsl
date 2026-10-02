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
	<!-- region of the barcode of a delivered mediorder article: end if the text is
		wider than high, otherwise after -->
	<xsl:variable name="barcodeRegion">
		<xsl:choose>
			<xsl:when test="not(/Page/Mediorder)">none</xsl:when>
			<xsl:when
				test="($textOrientation = '90') = (number(substring-before($pageHeight, 'mm')) &gt; number(substring-before($pageWidth, 'mm')))">end</xsl:when>
			<xsl:otherwise>after</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master
					master-name="ArticleLabel" page-width="{$pageWidth}"
					page-height="{$pageHeight}" margin-top="{$marginTop}"
					margin-bottom="{$marginBottom}" margin-left="{$marginLeft}"
					margin-right="{$marginRight}" reference-orientation="{$textOrientation}">
					<fo:region-body>
						<xsl:call-template name="barcodeRegionBodyMargin" />
					</fo:region-body>
					<fo:region-after>
						<xsl:call-template name="barcodeRegionAfterExtent" />
					</fo:region-after>
					<xsl:call-template name="barcodeRegionEnd" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="ArticleLabel">
				<xsl:apply-templates select="/Page/Mediorder" mode="barcode" />
				<fo:flow flow-name="xsl-region-body">
					<fo:block-container font="8pt Helvetica"
						font-weight="normal" text-align="center">
						<xsl:apply-templates />
					</fo:block-container>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="Articles">
		<xsl:for-each select="Article">
			<fo:block page-break-before="always">
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
				<fo:block font-size="6pt" text-decoration="underline">
					<xsl:value-of select="/Page/Info/responsibleMedicalPerson" />
				</fo:block>				
				<fo:block>
					<fo:leader />
				</fo:block>
				<fo:block font-style="italic">
					<xsl:value-of select="Name" />
				</fo:block>
				<fo:block>
					Abgabedatum:&#160;
					<xsl:value-of select="DeliveryDate" />
				</fo:block>
				<fo:block font-weight="bold">
					Preis:&#160;CHF&#160;
					<xsl:value-of select="Price" />
				</fo:block>
			</fo:block>
		</xsl:for-each>
	</xsl:template>
	<!-- barcode of a delivered mediorder article, scanning it hands the article
		out. Placed in its own region, so it never covers the text. -->
	<xsl:template name="barcodeRegionBodyMargin">
		<xsl:if test="$barcodeRegion = 'end'">
			<xsl:attribute name="margin-right">13mm</xsl:attribute>
		</xsl:if>
		<xsl:if test="$barcodeRegion = 'after'">
			<xsl:attribute name="margin-bottom">13mm</xsl:attribute>
		</xsl:if>
	</xsl:template>
	<xsl:template name="barcodeRegionAfterExtent">
		<xsl:if test="$barcodeRegion = 'after'">
			<xsl:attribute name="extent">13mm</xsl:attribute>
			<xsl:attribute name="display-align">after</xsl:attribute>
		</xsl:if>
	</xsl:template>
	<xsl:template name="barcodeRegionEnd">
		<xsl:if test="$barcodeRegion = 'end'">
			<fo:region-end extent="13mm" display-align="after" />
		</xsl:if>
	</xsl:template>
	<xsl:template match="Mediorder" mode="barcode">
		<fo:static-content flow-name="xsl-region-{$barcodeRegion}">
			<fo:block text-align="end" line-height="0">
				<fo:instream-foreign-object>
					<barcode:barcode
						xmlns:barcode="http://barcode4j.krysalis.org/ns"
						message="{@barcodeLabel}">
						<barcode:datamatrix>
							<barcode:module-width>0.35mm</barcode:module-width>
							<barcode:shape>force-square</barcode:shape>
						</barcode:datamatrix>
					</barcode:barcode>
				</fo:instream-foreign-object>
			</fo:block>
		</fo:static-content>
	</xsl:template>
</xsl:stylesheet>


