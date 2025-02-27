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
					master-name="MedicationLabel" page-width="{$pageWidth}"
					page-height="{$pageHeight}" margin-top="{$marginTop}"
					margin-bottom="{$marginBottom}" margin-left="{$marginLeft}"
					margin-right="{$marginRight}">
					<fo:region-body />
					<fo:region-after />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="MedicationLabel">
				<fo:flow flow-name="xsl-region-body"
					reference-orientation="{$textOrientation}">
					<fo:block-container font="8pt Helvetica"
						font-weight="normal" text-align="center">
						<xsl:apply-templates select="/page/info" />
						<xsl:apply-templates select="/Page/Patient" />
						<xsl:apply-templates select="/Page/Medication" />
					</fo:block-container>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="Patient">
		<fo:block>
			<!-- <xsl:value-of select="/page/patient/salutation" /> &#160; -->
			<xsl:value-of select="/Page/Patient/FirstName" />
			&#160;
			<xsl:value-of select="/Page/Patient/LastName" />
			&#160;(
			<xsl:value-of select="/Page/Patient/Sex" />
			)
			,&#160;
			<xsl:value-of select="/Page/Patient/Birthdate" />
		</fo:block>
	</xsl:template>
	<xsl:template match="Medication">
		<fo:block font-size="8pt" font-style="italic">
			<xsl:value-of select="ArticleName" />
		</fo:block>
		<fo:block>
			Abgabedatum:&#160;
			<xsl:value-of select="DeliveryDate" />
		</fo:block>
		
        <xsl:choose>
            <xsl:when test="Dose/@Freetext = 'true'">
                <fo:table>
                    <fo:table-header border-width="1pt" border-style="solid">
                        <fo:table-row>
                            <fo:table-cell border-width="1pt" border-style="solid">
                                <fo:block>
                                    Dosierung
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </fo:table-header>
                    <fo:table-body border-width="1pt" border-style="solid">
                        <fo:table-row>
                            <fo:table-cell border-width="1pt" border-style="solid">
                                <fo:block>
                                    <xsl:value-of select="Dose" />
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </fo:table-body>
                </fo:table>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="DoseTableHeader/HeaderItem and DoseTableBody/DoseItem">
                    <fo:table>
                        <fo:table-header border-width="1pt" border-style="solid">
                            <fo:table-row>
                                <xsl:for-each select="DoseTableHeader/HeaderItem">
                                    <fo:table-cell border-width="1pt" border-style="solid">
                                        <fo:block>
                                            <xsl:value-of select="." />
                                        </fo:block>
                                    </fo:table-cell>
                                </xsl:for-each>
                            </fo:table-row>
                        </fo:table-header>
                        <fo:table-body border-width="1pt" border-style="solid">
                            <fo:table-row>
                                <xsl:for-each select="DoseTableBody/DoseItem">
                                    <fo:table-cell border-width="1pt" border-style="solid">
                                        <fo:block>
                                            <xsl:value-of select="." />
                                        </fo:block>
                                    </fo:table-cell>
                                </xsl:for-each>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </xsl:if>
                  <xsl:if test="DosageInstructions">
                    <fo:block space-before="5pt">
                        Einnahmeinstruktionen:&#160;
                        <xsl:value-of select="DosageInstructions" />
                    </fo:block>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
		<fo:block font-weight="bold">
			Preis:&#160;CHF&#160;
			<xsl:value-of select="ArticlePrice" />
		</fo:block>
		<fo:block font-size="6pt" text-decoration="underline">
			<xsl:value-of select="ResponsiblePharamcist" />
		</fo:block>
		<!-- testing -->
	</xsl:template>
</xsl:stylesheet>