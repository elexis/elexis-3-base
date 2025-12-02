<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:invoice="http://www.forum-datenaustausch.ch/invoice">
	<xsl:param name="xmlQr0" select="''" />
	<xsl:param name="xmlQr1" select="''" />
	<xsl:param name="xmlQr2" select="''" />
	<xsl:param name="xmlQr3" select="''" />
	<xsl:param name="xmlQr4" select="''" />
	<xsl:param name="xmlQr5" select="''" />
	<xsl:param name="xmlQr6" select="''" />
	<xsl:param name="xmlQr7" select="''" />
	<xsl:param name="xmlQr8" select="''" />
	<xsl:param name="xmlQr9" select="''" />
	<xsl:param name="xmlQr10" select="''" />
	<xsl:param name="xmlQr11" select="''" />
	<xsl:template match="invoice:*">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="Page"
					page-height="29.7cm" page-width="21cm" margin-top="1cm"
					margin-bottom="1.3cm" margin-left="1cm" margin-right="1cm">
					<fo:region-body margin-top="3cm" margin-bottom="2cm" />
					<fo:region-before extent="3cm" />
					<fo:region-after region-name="footer"
						extent="2cm" display-align="after" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="Page">
				<!-- header -->
				<fo:static-content flow-name="xsl-region-before">
					<!-- main header on every page -->
					<fo:block margin-bottom="0.4cm">
						<xsl:choose>
							<xsl:when
								test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
								<xsl:call-template name="billheader2D">
									<xsl:with-param name="Type" select="'GAX'">
									</xsl:with-param>
									<xsl:with-param name="Title"
										select="'Rückforderungsbeleg QR-Code Blatt'">
									</xsl:with-param>
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="billheader2D">
									<xsl:with-param name="Type" select="'PAX'">
									</xsl:with-param>
									<xsl:with-param name="Title"
										select="'Tiers Payant QR-Code Blatt'">
									</xsl:with-param>
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</fo:block>
				</fo:static-content>
				<fo:static-content flow-name="footer">
					
				</fo:static-content>
				<!-- document body -->
				<fo:flow flow-name="xsl-region-body">
					<!-- normal content -->
					<fo:block>
						<xsl:call-template name="qr_header">
						</xsl:call-template>
						<xsl:call-template name="qr_body">
						</xsl:call-template>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<xsl:template name="qr_header">
		<fo:table table-layout="fixed" width="100%" font-size="8px" font-family="tahoma,arial,helvetica,sans-serif">
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell width="2cm">
						<fo:block font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							Identifikation:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>
							<xsl:value-of
								select="/invoice:request/invoice:payload/invoice:invoice/@request_timestamp" />
							/
							<xsl:call-template name="FormatDateTime">
								<xsl:with-param name="DateTime"
									select="/invoice:request/invoice:payload/invoice:invoice/@request_date" />
							</xsl:call-template>
							/
							<xsl:value-of
								select="/invoice:request/@guid" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell width="2cm">
						<fo:block font-size="7px"
							font-family="tahoma,arial,helvetica,sans-serif">
							PatientIn:
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block white-space-treatment="preserve">
							<xsl:choose>
								<xsl:when
									test="count(/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant) > 0">
									<xsl:call-template name="FormatGender">
										<xsl:with-param name="Gender"
											select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_garant/invoice:patient/@gender" />
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="FormatGender">
										<xsl:with-param name="Gender"
											select="/invoice:request/invoice:payload/invoice:body/invoice:tiers_payant/invoice:patient/@gender" />
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:value-of select="' '" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>				
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template name="qr_body">
		<fo:table table-layout="fixed" width="100%">
			<fo:table-body>
				<xsl:if test="string-length($xmlQr0) > 0">
					<fo:table-row>
						<fo:table-cell>
							<fo:block-container text-align="left" margin-left="-1mm" margin-top="0mm" margin-bottom="0mm" width="50mm" height="50mm">
								<fo:block>
									<xsl:choose>
										<xsl:when test="string-length($xmlQr0) > 0">
											<fo:external-graphic src="{$xmlQr0}" width="100%" content-width="scale-to-fit"/>
										</xsl:when>
										<xsl:otherwise>
											<fo:block font-weight="bold">
												Error no QR Code
											</fo:block>
										</xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:block-container>						
						</fo:table-cell>
						<xsl:if test="string-length($xmlQr1) > 0">
						<fo:table-cell>
							<fo:block-container text-align="left" margin-left="-1mm" margin-top="0mm" margin-bottom="0mm" width="50mm" height="50mm">
								<fo:block>
									<xsl:choose>
										<xsl:when test="string-length($xmlQr1) > 0">
											<fo:external-graphic src="{$xmlQr1}" width="100%" content-width="scale-to-fit"/>
										</xsl:when>
										<xsl:otherwise>
											<fo:block font-weight="bold">
												Error no QR Code
											</fo:block>
										</xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:block-container>						
						</fo:table-cell>
						</xsl:if>
					</fo:table-row>
				</xsl:if>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template name="FormatNumber">
		<xsl:param name="Number" />
		<xsl:if test="string-length($Number) > 0">
			<xsl:value-of select="format-number($Number,'##0.00')" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="BooleanToNumber">
		<xsl:param name="Boolean" />
		<xsl:choose>
			<xsl:when test="string-length($Boolean) > 1">
				<xsl:choose>
					<xsl:when test="$Boolean = 'true'">
						1
					</xsl:when>
					<xsl:otherwise>
						0
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$Boolean" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="FormatDate">
		<xsl:param name="DateTime" />

		<!-- new date format 2006-01-14T08:55:22 -->
		<xsl:variable name="year">
			<xsl:value-of select="substring($DateTime,1,4)" />
		</xsl:variable>
		<xsl:variable name="month-temp">
			<xsl:value-of select="substring-after($DateTime,'-')" />
		</xsl:variable>
		<xsl:variable name="month">
			<xsl:value-of select="substring-before($month-temp,'-')" />
		</xsl:variable>
		<xsl:variable name="day-temp">
			<xsl:value-of select="substring-after($month-temp,'-')" />
		</xsl:variable>
		<xsl:variable name="day">
			<xsl:value-of select="substring($day-temp,1,2)" />
		</xsl:variable>

		<xsl:value-of select="$day" />
		<xsl:value-of select="'.'" />
		<xsl:value-of select="$month" />
		<xsl:value-of select="'.'" />
		<xsl:value-of select="$year" />
	</xsl:template>

	<xsl:template name="FormatDateTime">
		<xsl:param name="DateTime" />

		<!-- new date format 2006-01-14T08:55:22 -->
		<xsl:variable name="year">
			<xsl:value-of select="substring($DateTime,1,4)" />
		</xsl:variable>
		<xsl:variable name="month-temp">
			<xsl:value-of select="substring-after($DateTime,'-')" />
		</xsl:variable>
		<xsl:variable name="month">
			<xsl:value-of select="substring-before($month-temp,'-')" />
		</xsl:variable>
		<xsl:variable name="day-temp">
			<xsl:value-of select="substring-after($month-temp,'-')" />
		</xsl:variable>
		<xsl:variable name="day">
			<xsl:value-of select="substring($day-temp,1,2)" />
		</xsl:variable>
		<xsl:variable name="time">
			<xsl:value-of select="substring-after($DateTime,'T')" />
		</xsl:variable>

		<xsl:value-of select="$day" />
		<xsl:value-of select="'.'" />
		<xsl:value-of select="$month" />
		<xsl:value-of select="'.'" />
		<xsl:value-of select="$year" />
		<xsl:value-of select="' '" />
		<xsl:value-of select="$time" />
	</xsl:template>

	<xsl:include href="/rsc/templates_50.xsl" />
</xsl:stylesheet>