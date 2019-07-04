<?xml version="1.0" encoding="utf-8"?>
<!-- (c) IT-Med AG 2019; All rights reserved -->
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
					master-name="AppointmentCard" page-width="{$pageWidth}"
					page-height="{$pageHeight}" margin-top="{$marginTop}"
					margin-bottom="{$marginBottom}" margin-left="{$marginLeft}"
					margin-right="{$marginRight}">
					<fo:region-body />
					<fo:region-after />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="AppointmentCard">
				<fo:flow flow-name="xsl-region-body">
					<fo:block-container font="10pt Helvetica"
						font-weight="bold">
						<fo:block>Praxis Name</fo:block>
					</fo:block-container>
					<fo:block-container font="8pt Helvetica"
						font-weight="normal">
						<fo:block>Zusatzinformation Praxis</fo:block>
						<fo:block>Strasse 00</fo:block>
						<fo:block>0000 Ort</fo:block>
					</fo:block-container>
					<fo:block-container font="9pt Helvetica"
						font-weight="bold">
						<fo:block>041 000 00 00 t</fo:block>
						<fo:block>041 000 00 00 f</fo:block>
					</fo:block-container>
					<fo:block-container font="8pt Helvetica"
						font-weight="normal">
						<fo:block>email@test.ch</fo:block>
						<fo:block>
							<fo:leader />
						</fo:block>
					</fo:block-container>
					<xsl:apply-templates select="/Page/Patient" />
					<xsl:apply-templates
						select="/Page/AppointmentsInformation" />
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="Patient">
		<fo:block-container font="7.5pt Helvetica"
			font-weight="normal">
			<fo:block>
				<xsl:value-of select="LastName" />
			</fo:block>
			<fo:block>
				<xsl:value-of select="FirstName" />
			</fo:block>
			<fo:block>
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
			<xsl:if test="/Page/Patient/Phone1/text()">
				<fo:block>
					p:&#160;&#160;
					<xsl:value-of select="Phone1" />
				</fo:block>
			</xsl:if>
			<xsl:if test="/Page/Patient/Phone2/text()">
				<fo:block>
					g:&#160;&#160;
					<xsl:value-of select="Phone2" />
				</fo:block>
			</xsl:if>
			<xsl:if test="/Page/Patient/MobilePhone/text()">
				<fo:block>
					m:&#160;&#160;
					<xsl:value-of select="MobilePhone" />
				</fo:block>
			</xsl:if>
		</fo:block-container>
	</xsl:template>
	<xsl:template match="Contact">
		<fo:block-container font="8pt Helvetica"
			font-weight="normal">
			<fo:block>
				<fo:leader />
			</fo:block>
			<fo:block>
				Termin bei:
				<xsl:value-of select="Salutation" />
				<xsl:value-of select="FirstName" />
				<xsl:value-of select="LastName" />
			</fo:block>
		</fo:block-container>
	</xsl:template>
	<xsl:template match="AppointmentsInformation">
		<fo:block-container>
			<fo:block>
				<fo:leader />
			</fo:block>
			<fo:block font="7.5pt Helvetica" font-weight="normal"
				text-decoration="underline">
				Ihr nächster
				Termin:
			</fo:block>
		</fo:block-container>
		<fo:block-container font="8pt Helvetica"
			font-weight="normal">
			<xsl:apply-templates select="Appointments" />
		</fo:block-container>
		<fo:block>
			<fo:leader />
		</fo:block>
		<fo:block font="7pt Helvetica" font-style="italic">
			Absagen in weniger als 24 Stunden vor dem Termin werden verrechnet
		</fo:block>
	</xsl:template>
	<xsl:template match="Appointments">
		<fo:block font-weight="bold">
			<xsl:value-of select="Appointment" />
		</fo:block>
	</xsl:template>
	<!-- <xsl:template match="Disclaimer"> <fo:block-container font="7pt Helvetica" 
		font-style="italic"> </fo:block-container> </xsl:template> -->
</xsl:stylesheet>