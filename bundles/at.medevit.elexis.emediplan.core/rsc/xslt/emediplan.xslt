<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	exclude-result-prefixes="fo">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="no"
		indent="yes" encoding="UTF-8" />
	<xsl:param name="versionParam" select="'1.0'" />
	<xsl:param name="logoJpeg" select="''" />
	<xsl:param name="qrJpeg" select="''" />
	<xsl:param name="commentText" select="''" />
		
	<xsl:attribute-set name="simpleBorder">
		<xsl:attribute name="border">solid 0.2mm black</xsl:attribute>
	</xsl:attribute-set>

	<!-- ========================= -->
	<!-- root element: letter -->
	<!-- ========================= -->
	<xsl:template match="medication">
		<!-- PDF/A does not allow the base 14 fonts, the font used has to be embedded -->
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format"
			font-family="Open Sans">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="simpleA4"
					page-height="21cm" page-width="29.7cm" margin-top="0.8cm"
					margin-bottom="0.8cm" margin-left="0.8cm" margin-right="0.8cm">
					<fo:region-body />
					<fo:region-after />
				</fo:simple-page-master>
			</fo:layout-master-set>

			<fo:page-sequence master-reference="simpleA4">
				<fo:static-content flow-name="xsl-region-after">
					<fo:table font-size="8.5pt" table-layout="fixed" width="100%">
						<fo:table-column />
						<fo:table-column />
						<fo:table-column />
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<xsl:value-of select="/medication/patient/title" />
										<fo:character character="&#x20;" />
										<xsl:value-of select="/medication/patient/lastname" />
										<fo:character character="&#x20;" />
										<xsl:value-of select="/medication/patient/firstname" />
										<fo:character character="&#x2C;" />
										<fo:character character="&#x20;" />
										<xsl:value-of select="/medication/patient/birthdate" />
									</fo:block>
								</fo:table-cell>
								<fo:table-cell display-align="center">
									<fo:block text-align="center">
										eMediplan by Elexis
										<fo:character character="&#x20;" />
										<fo:character character="&#x28;" />
										<xsl:value-of select="$versionParam" />
										<fo:character character="&#x29;" />
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right">
										Seite
										<fo:page-number />
										von
										<fo:page-number-citation ref-id="last-page" />
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:static-content>

				<fo:flow flow-name="xsl-region-body">
					<fo:table table-layout="fixed" width="100%">
						<fo:table-column column-width="25%" />
						<fo:table-column column-width="40%" />
						<fo:table-column column-width="17.5%" />
						<fo:table-column column-width="17.5%" />
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell display-align="before">
									<fo:block>
										<!-- eMediplan Logo -->
										<xsl:if test="string-length($logoJpeg) > 0">
											<fo:external-graphic src="{$logoJpeg}" />
										</xsl:if>
										<fo:block padding-top="5mm" font-size="11pt"
											font-weight="bold">
											Der Schweizer
											Medikationsplan
										</fo:block>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding-left="10mm">
									<fo:table>
										<fo:table-column />
										<fo:table-column />
										<fo:table-body>
											<xsl:apply-templates select="patient" />
										</fo:table-body>
									</fo:table>
								</fo:table-cell>
								<fo:table-cell padding-left="1mm">
									<xsl:apply-templates select="mandant" />
								</fo:table-cell>
								<fo:table-cell number-rows-spanned="2">
									<fo:block>
										<!-- QR Code -->
										<xsl:if test="string-length($qrJpeg) > 0">
											<fo:external-graphic src="{$qrJpeg}"
												content-width="40mm" content-height="40mm" scaling="non-uniform" />
										</xsl:if>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell number-columns-spanned="3">
									<fo:block linefeed-treatment="preserve" padding-top="5mm"
										font-size="8.5pt">
										<xsl:value-of select="$commentText" />
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>

					<xsl:if test="count(fix/medicament) > 0 or count(symptomatic/medicament) > 0 ">
						<fo:block padding-top="1mm" padding-bottom="1mm"
							font-size="8.5pt">
							Ausstellungsdatum:
							<xsl:value-of select="date" />
						</fo:block>
	
						<fo:table font-size="8.5pt" table-layout="fixed" width="100%">
							<fo:table-column column-width="17%" />
							<fo:table-column column-width="6%" />
							<fo:table-column column-width="5%" />
							<fo:table-column column-width="5%" />
							<fo:table-column column-width="5%" />
							<fo:table-column column-width="5%" />
							<fo:table-column column-width="5%" />
							<fo:table-column column-width="6%" />
							<fo:table-column column-width="6%" />
							<fo:table-column column-width="20%" />
							<fo:table-column column-width="10%" />
							<fo:table-column column-width="10%" />
							<fo:table-header>
								<fo:table-row font-weight="bold"
									background-color="#DDDDDD">
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm">
											Medikament
										</fo:block>
									</fo:table-cell>
									<!-- picture of the dose form -->
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm" />
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm">
											Morgen
										</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm">
											Mittag
										</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm">
											Abend
										</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm">
											Nacht
										</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm">
											Einheit
										</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm">
											Von
										</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm">
											Bis u. mit
										</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm">
											Anwendungsinstruktion
										</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm">
											Anwendungsgrund
										</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm">
											Verordnet durch
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-header>
							<fo:table-body>
								<xsl:apply-templates select="fix/medicament" />
								<xsl:apply-templates select="symptomatic/medicament" />
							</fo:table-body>
						</fo:table>
					</xsl:if>
					<xsl:if test="count(reserve/medicament) > 0">
						<fo:block padding-top="3mm" padding-bottom="1mm"
							font-size="8.5pt" font-weight="bold">
							Reserve Medikation
						</fo:block>
						<fo:table font-size="8.5pt" table-layout="fixed" width="100%">
							<fo:table-column column-width="17%" />
							<fo:table-column column-width="6%" />
							<fo:table-column column-width="5%" />
							<fo:table-column column-width="5%" />
							<fo:table-column column-width="5%" />
							<fo:table-column column-width="5%" />
							<fo:table-column column-width="5%" />
							<fo:table-column column-width="6%" />
							<fo:table-column column-width="6%" />
							<fo:table-column column-width="20%" />
							<fo:table-column column-width="10%" />
							<fo:table-column column-width="10%" />
							<fo:table-body>
								<xsl:apply-templates select="reserve/medicament" />
							</fo:table-body>
						</fo:table>
					</xsl:if>
					<!-- remark the author wrote for this plan, shown only if there is one -->
					<xsl:if test="count(remark) > 0 and string-length(remark) > 0">
						<fo:block padding-top="3mm" padding-bottom="1mm"
							font-size="8.5pt" font-weight="bold">
							Bemerkung:
						</fo:block>
						<fo:block linefeed-treatment="preserve" font-size="8.5pt"
							background-color="#EEEEEE" padding="2mm">
							<xsl:value-of select="remark" />
						</fo:block>
					</xsl:if>
					<fo:block id="last-page" />
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<!-- ========================= -->
	<!-- child element: mandant -->
	<!-- ========================= -->
	<xsl:template match="mandant">
		<fo:block-container font-size="8.5pt" white-space="pre">
			<fo:block>erstellt von:</fo:block>
			<fo:block>
				<fo:character character="&#x20;" />
			</fo:block>
			<fo:block white-space="pre">
				<xsl:if test="string-length(title) > 0">
					<xsl:value-of select="title" />
					<fo:character character="&#x20;" />
				</xsl:if>
				<xsl:value-of select="lastname" />
				<fo:character character="&#x20;" />
				<xsl:value-of select="firstname" />
			</fo:block>
			<fo:block>
				<xsl:value-of select="street1" />
			</fo:block>
			<fo:block>
				<xsl:value-of select="zip" />
				<fo:character character="&#x20;" />
				<xsl:value-of select="city" />
			</fo:block>
		</fo:block-container>
	</xsl:template>

	<!-- ========================= -->
	<!-- child element: patient -->
	<!-- ========================= -->
	<xsl:template match="patient">
		<fo:table-row>
			<fo:table-cell number-columns-spanned="2">
				<fo:block font-size="16pt" font-weight="bold" white-space="pre">
					<xsl:if test="string-length(title) > 0">
						<xsl:value-of select="title" />
						<fo:character character="&#x20;" />
					</xsl:if>
					<xsl:value-of select="firstname" />
					<fo:character character="&#x20;" />
					<xsl:value-of select="lastname" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>

		<fo:table-row>
			<fo:table-cell number-columns-spanned="2">
				<fo:block font-size="8pt" white-space="pre">
					<xsl:value-of select="birthdate" />
					<fo:character character="&#x20;" />
					<fo:character character="&#x28;" />
					<xsl:value-of select="gender" />
					<fo:character character="&#x29;" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>

		<fo:table-row>
			<fo:table-cell number-columns-spanned="2">
				<fo:block font-size="8pt" white-space="pre">
					<xsl:value-of select="street1" />
					<fo:character character="&#x2C;" />
					<fo:character character="&#x20;" />
					<xsl:value-of select="zip" />
					<fo:character character="&#x20;" />
					<xsl:value-of select="city" />
					<xsl:if test="count(tel) > 0 and string-length(tel) > 0">
						<fo:character character="&#x20;" />
						<fo:character character="&#x2F;" />
						<fo:character character="&#x20;" />
						<xsl:value-of select="tel" />
					</xsl:if>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<!-- ========================= -->
	<!-- child element: medicament -->
	<!-- ========================= -->
	<xsl:template match="medicament">
		<fo:table-row>
			<fo:table-cell xsl:use-attribute-sets="simpleBorder">
				<fo:block margin="1mm">
					<xsl:value-of select="name" />
				</fo:block>
				<xsl:if test="count(substances) > 0 and string-length(substances) > 0">
					<fo:block margin-left="1mm" margin-right="1mm" margin-bottom="1mm"
						font-size="7pt" color="#555555">
						<xsl:value-of select="substances" />
					</fo:block>
				</xsl:if>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="simpleBorder"
				display-align="center">
				<fo:block margin="1mm" text-align="center">
					<xsl:if test="count(image) > 0 and string-length(image) > 0">
						<!-- scaled uniformly into a box of 14mm x 10mm -->
						<fo:external-graphic src="{image}" content-width="14mm"
							content-height="10mm" scaling="uniform" />
					</xsl:if>
				</fo:block>
			</fo:table-cell>
			<xsl:choose>
				<xsl:when test="count(dosageText) > 0 and string-length(dosageText) > 0">
					<fo:table-cell xsl:use-attribute-sets="simpleBorder"
						number-columns-spanned="5">
						<fo:block margin="1mm">
							siehe Anwendungsinstruktion
						</fo:block>
					</fo:table-cell>
				</xsl:when>
				<xsl:otherwise>
					<fo:table-cell xsl:use-attribute-sets="simpleBorder"
						display-align="center">
						<fo:block margin="1mm" text-align="center">
							<xsl:call-template name="FormatSignaturePart">
								<xsl:with-param name="SignaturePart" select="dosageMorning" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="simpleBorder"
						display-align="center">
						<fo:block margin="1mm" text-align="center">
							<xsl:call-template name="FormatSignaturePart">
								<xsl:with-param name="SignaturePart" select="dosageNoon" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="simpleBorder"
						display-align="center">
						<fo:block margin="1mm" text-align="center">
							<xsl:call-template name="FormatSignaturePart">
								<xsl:with-param name="SignaturePart" select="dosageEvening" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="simpleBorder"
						display-align="center">
						<fo:block margin="1mm" text-align="center">
							<xsl:call-template name="FormatSignaturePart">
								<xsl:with-param name="SignaturePart" select="dosageNight" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="simpleBorder"
						display-align="center">
						<fo:block margin="1mm" text-align="center">
							<xsl:call-template name="FormatSignaturePart">
								<xsl:with-param name="SignaturePart" select="unit" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</xsl:otherwise>
			</xsl:choose>
			<fo:table-cell xsl:use-attribute-sets="simpleBorder">
				<fo:block margin="1mm">
					<xsl:value-of select="startDate" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="simpleBorder">
				<fo:block margin="1mm">
					<xsl:value-of select="endDate" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="simpleBorder">
				<xsl:if
					test="count(dosageText) > 0 and string-length(dosageText) 
						> 0">
					<fo:block margin="1mm">
						<xsl:value-of select="dosageText" />
					</fo:block>
				</xsl:if>
				<fo:block margin="1mm">
					<xsl:value-of select="remarks" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="simpleBorder">
				<fo:block margin="1mm">
					<xsl:value-of select="reason" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="simpleBorder">
				<fo:block margin="1mm">
					<xsl:value-of select="prescriptor" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="FormatSignaturePart">
		<xsl:param name="SignaturePart" />
		<xsl:choose>
			<xsl:when
				test="$SignaturePart = '0' or string-length(normalize-space($SignaturePart)) = 0">
				<xsl:value-of select="'-'" />
			</xsl:when>
			<xsl:when test="contains($SignaturePart, '/') and not(contains($SignaturePart, ' '))">
				<xsl:call-template name="FormatFraction">
					<xsl:with-param name="Numerator" select="substring-before($SignaturePart, '/')" />
					<xsl:with-param name="Denominator" select="substring-after($SignaturePart, '/')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$SignaturePart = '0.5'">
				<xsl:call-template name="FormatFraction">
					<xsl:with-param name="Numerator" select="'1'" />
					<xsl:with-param name="Denominator" select="'2'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$SignaturePart = '0.25'">
				<xsl:call-template name="FormatFraction">
					<xsl:with-param name="Numerator" select="'1'" />
					<xsl:with-param name="Denominator" select="'4'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$SignaturePart = '0.75'">
				<xsl:call-template name="FormatFraction">
					<xsl:with-param name="Numerator" select="'3'" />
					<xsl:with-param name="Denominator" select="'4'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$SignaturePart = '0.125'">
				<xsl:call-template name="FormatFraction">
					<xsl:with-param name="Numerator" select="'1'" />
					<xsl:with-param name="Denominator" select="'8'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$SignaturePart = '0.33' or $SignaturePart = '0.333'">
				<xsl:call-template name="FormatFraction">
					<xsl:with-param name="Numerator" select="'1'" />
					<xsl:with-param name="Denominator" select="'3'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$SignaturePart = '0.66' or $SignaturePart = '0.667'">
				<xsl:call-template name="FormatFraction">
					<xsl:with-param name="Numerator" select="'2'" />
					<xsl:with-param name="Denominator" select="'3'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$SignaturePart" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		Numerator raised, denominator lowered, separated by a fraction slash. The
		slash is written as text on purpose, that makes the content of the cell mixed
		and keeps the serializer from indenting the parts, which would end up as
		spaces between them.
	-->
	<xsl:template name="FormatFraction">
		<xsl:param name="Numerator" />
		<xsl:param name="Denominator" />
		<fo:inline font-size="70%" baseline-shift="super"><xsl:value-of select="$Numerator" /></fo:inline><xsl:text>&#x2044;</xsl:text><fo:inline font-size="70%" baseline-shift="sub"><xsl:value-of select="$Denominator" /></fo:inline>
	</xsl:template>
</xsl:stylesheet>
