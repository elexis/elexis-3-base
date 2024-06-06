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
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
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
					<fo:table font-size="8pt" table-layout="fixed" width="100%">
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
										eRezept by Elexis
									</fo:block>
								</fo:table-cell>
								<fo:table-cell display-align="right">
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
										<fo:block padding-top="5mm" font-size="32pt"
											font-weight="bold">
											eRezept
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
									<fo:block linefeed-treatment="preserve" padding-top="5mm" font-size="9pt">
											Das ist ein elektronisches Rezept und ist mit der HIN Signatur unterschrieben worden. Die verbindlichen Daten sind im QR-Code enthalten. 
											Um das Rezept auf Gültigkeit zu prüfen und einlösen zu können, muss der QR-Code gescannt werden. 
										</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>

					<xsl:if test="count(fix/medicament) > 0 or count(symptomatic/medicament) > 0 ">
						<fo:block padding-top="1mm" padding-bottom="1mm"
							font-size="8pt">
							Erstellt am:
							<xsl:value-of select="date" />
						</fo:block>
	
						<fo:table font-size="8pt" table-layout="fixed" width="100%">
							<fo:table-column column-width="23%" />
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
								<fo:table-row font-weight="bold">
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm">
											Medikament
										</fo:block>
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
											Art der Medikation
										</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="simpleBorder">
										<fo:block margin="1mm">
											Von bis und mit
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
							font-size="8pt" font-weight="bold">
							Reserve Medikation
						</fo:block>
						<fo:table font-size="8pt" table-layout="fixed" width="100%">
							<fo:table-column column-width="23%" />
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
					<fo:block id="last-page" />
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<!-- ========================= -->
	<!-- child element: mandant -->
	<!-- ========================= -->
	<xsl:template match="mandant">
		<fo:block-container font-size="8pt" white-space="pre">
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
					<fo:character character="&#x20;" />
					<fo:character character="&#x2F;" />
					<fo:character character="&#x20;" />
					<xsl:value-of select="tel" />
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
							<xsl:value-of select="unit" />
						</fo:block>
					</fo:table-cell>
				</xsl:otherwise>
			</xsl:choose>
			<fo:table-cell xsl:use-attribute-sets="simpleBorder">
				<fo:block margin="1mm">
					<xsl:value-of select="type" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="simpleBorder">
				<fo:block margin="1mm">
					<xsl:value-of select="startDate" />
				</fo:block>
				<xsl:if test="count(endDate) > 0 and string-length(endDate) > 0">
					<fo:block margin="1mm">
						<xsl:value-of select="endDate" />
					</fo:block>
				</xsl:if>
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
			<xsl:when test="$SignaturePart = '0'">
				<xsl:value-of select="'-'" />
			</xsl:when>
			<xsl:when test="$SignaturePart = '1/2' or $SignaturePart = '0.5'">
				<xsl:value-of select="'½'" />
			</xsl:when>
			<xsl:when test="$SignaturePart = '1/4' or $SignaturePart = '0.25'">
				<xsl:value-of select="'¼'" />
			</xsl:when>
			<xsl:when test="$SignaturePart = '3/4' or $SignaturePart = '0.75'">
				<xsl:value-of select="'¾'" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$SignaturePart" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
