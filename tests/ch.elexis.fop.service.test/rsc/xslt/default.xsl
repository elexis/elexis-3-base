<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:template match="/contacts">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="simpleA4"
					page-height="21cm" page-width="29.7cm" margin-top="2cm"
					margin-bottom="2cm" margin-left="2cm" margin-right="2cm">
					<fo:region-body/>
					<fo:region-after/>
				</fo:simple-page-master>
			</fo:layout-master-set>

			<fo:page-sequence master-reference="simpleA4">
				<!-- document body -->
				<fo:flow flow-name="xsl-region-body">

					<xsl:for-each select="contact">
						<xsl:call-template name="contact">
						</xsl:call-template>
					</xsl:for-each>

				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<xsl:template name="contact">
		<fo:block>
			<xsl:value-of select="firstname" />
			<xsl:value-of select="' '" />
			<xsl:value-of select="lastname" />
		</fo:block>
	</xsl:template>
</xsl:stylesheet>