<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xmlns:invoice="http://www.xmlData.ch/xmlInvoice/XSD">
	<xsl:output method="html" indent="yes" version="4.01" encoding="ISO-8859-1" doctype-public="-//W3C//DTD HTML 4.01//EN"/>
	<!-- MD Invoice Response -->
	<xsl:variable name="thisStyleSheet">Elexis Plugin medshare-mediport (mediport_response.xsl)</xsl:variable>
	<xsl:variable name="thisStyleSheetVersion">1.0 / 12.06.2008</xsl:variable>
	<xsl:template match="/">
				<xsl:choose>
					<xsl:when test=".=/invoice:request"><xsl:apply-templates select="invoice:request"/></xsl:when>
					<xsl:when test=".=/invoice:response"><xsl:apply-templates select="invoice:response"/></xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="unsupportedXML" />
					</xsl:otherwise>
				</xsl:choose>
	</xsl:template>
	<xsl:template name="unsupportedXML">
	<html>
			<head>
				<!-- <meta name='Generator' content='&MediPort-Stylesheet;'/> -->
				<xsl:comment>
					Do NOT edit this HTML directly, it was generated via an XSL transformation from the original MD Invoice Response.
				</xsl:comment>
				<title>
					Elexis MediPort Stylesheet
					<!-- <xsl:value-of select="$title"/> -->
				</title>
				<style type="text/css">
					<xsl:text>
						body { color: #003366; font-size: 10pt; line-height: normal; font-family: Verdana, Arial, sans-serif;
						margin: 10px; scrollbar-3dlight-color: #EEEEEE; scrollbar-arrow-color: #003366; scrollbar-darkshadow-color: #EEEEEE;
						scrollbar-face-color: #EEEEEE; scrollbar-highlight-color: #003366; scrollbar-shadow-color: #003366;
						scrollbar-track-color: #EEEEEE } a { color: #003366; text-decoration: none } table { font-size: 10pt;
						background-repeat: no-repeat; border: 2px #bacd0c } .input { color: #003366; font-size: 10pt; font-family: Verdana,
						Arial, sans-serif; background-color: #ffffff; border: solid 1px } h1 { font-size: 12pt; } h1 { font-size: 11pt; }
						table { line-height: 10pt; border-width: 0; border-color: #eeeeee; font-size: 10pt; }
					</xsl:text>
				</style>
			</head>
			<xsl:comment>Created by Tony Schaller, medshare GmbH, for MediPort Communicator</xsl:comment>
			<body>
			Diese XML Datei kann mit diesem Stylesheet nicht angezeigt werden. Öffnen Sie die XML Datei in einem Editor.
			</body>
		</html>
	</xsl:template>
	<xsl:template match="invoice:request">
	<html>
			<head>
				<!-- <meta name='Generator' content='&MediPort-Stylesheet;'/> -->
				<xsl:comment>
					Do NOT edit this HTML directly, it was generated via an XSL transformation from the original MD Invoice Response.
				</xsl:comment>
				<title>
					Elexis MediPort Stylesheet
					<!-- <xsl:value-of select="$title"/> -->
				</title>
				<style type="text/css">
					<xsl:text>
						body { color: #003366; font-size: 10pt; line-height: normal; font-family: Verdana, Arial, sans-serif;
						margin: 10px; scrollbar-3dlight-color: #EEEEEE; scrollbar-arrow-color: #003366; scrollbar-darkshadow-color: #EEEEEE;
						scrollbar-face-color: #EEEEEE; scrollbar-highlight-color: #003366; scrollbar-shadow-color: #003366;
						scrollbar-track-color: #EEEEEE } a { color: #003366; text-decoration: none } table { font-size: 10pt;
						background-repeat: no-repeat; border: 2px #bacd0c } .input { color: #003366; font-size: 10pt; font-family: Verdana,
						Arial, sans-serif; background-color: #ffffff; border: solid 1px } h1 { font-size: 12pt; } h1 { font-size: 11pt; }
						table { line-height: 10pt; border-width: 0; border-color: #eeeeee; font-size: 10pt; }
					</xsl:text>
				</style>
			</head>
			<xsl:comment>Created by Tony Schaller, medshare GmbH, for MediPort Communicator</xsl:comment>
			<body>
			Tarmed Rechnungen können mit diesem Stylesheet nicht angezeigt werden. Öffnen Sie die XML Datei in einem Editor.
			</body>
		</html>
	</xsl:template>
	<xsl:template match="invoice:response">
	<html>
			<head>
				<!-- <meta name='Generator' content='&MediPort-Stylesheet;'/> -->
				<xsl:comment>
					Do NOT edit this HTML directly, it was generated via an XSL transformation from the original MD Invoice Response.
				</xsl:comment>
				<title>
					MediPort Rechnungsantwort
					<!-- <xsl:value-of select="$title"/> -->
				</title>
				<style type="text/css">
					<xsl:text>
						body { color: #003366; font-size: 10pt; line-height: normal; font-family: Verdana, Arial, sans-serif;
						margin: 10px; scrollbar-3dlight-color: #EEEEEE; scrollbar-arrow-color: #003366; scrollbar-darkshadow-color: #EEEEEE;
						scrollbar-face-color: #EEEEEE; scrollbar-highlight-color: #003366; scrollbar-shadow-color: #003366;
						scrollbar-track-color: #EEEEEE } a { color: #003366; text-decoration: none } table { font-size: 10pt;
						background-repeat: no-repeat; border: 2px #bacd0c } .input { color: #003366; font-size: 10pt; font-family: Verdana,
						Arial, sans-serif; background-color: #ffffff; border: solid 1px } h1 { font-size: 12pt; } h1 { font-size: 11pt; }
						table { line-height: 10pt; border-width: 0; border-color: #eeeeee; font-size: 10pt; }
					</xsl:text>
				</style>
			</head>
			<xsl:comment>Created by Tony Schaller, medshare GmbH, for MediPort Communicator</xsl:comment>
			<body>
				<!--
				*********************************************************************************************
					Header: Rechungsantwort und Modus
			    *********************************************************************************************
				-->
				<table width="100%" cellspacing="1" cellpadding="5">
					<tr bgcolor="#3399ff">
						<td colspan="2" valign="top">
							<span style="color:white;font-weight:bold;">
								<xsl:text>MediPort Rechnungsantwort</xsl:text>
							</span>
						</td>
						<td valign="top" align="right">
							<span style="color:white;">
								<xsl:text>Modus: </xsl:text>
							</span>
						</td>
						<td valign="top">
							<span style="color:white;font-weight:bold;">
								<xsl:variable name="role"
								  select="/invoice:response/@role"/>
								<xsl:choose>
									<xsl:when test="$role='test'">Test</xsl:when>
									<xsl:when test="$role='production'">Produktiv</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="/invoice:response/@role"/>
									</xsl:otherwise>
								</xsl:choose>
							</span>
						</td>
					</tr>
					<!--
					*********************************************************************************************
						Header: Sender, Intermediär, Empfänger
					*********************************************************************************************
					-->
					<tr bgcolor="#ccccff">
						<td width="20%" valign="top">
							<xsl:text>Sender:</xsl:text>
						</td>
						<td width="50%" valign="top">
							<xsl:value-of select="/invoice:response/invoice:header/invoice:sender/@ean_party"/>
						</td>
						<td width="15%" valign="top" align="right">
							<xsl:text>Timestamp:</xsl:text>
						</td>
						<td width="15%" valign="top">
							<xsl:value-of select="/invoice:response/invoice:invoice/@response_timestamp"/>
						</td>
					</tr>
					<tr bgcolor="#ccccff">
						<td>
							<xsl:text>Intermediär:</xsl:text>
						</td>
						<td>
							<xsl:value-of select="/invoice:response/invoice:header/invoice:intermediate/@ean_party"/>
						</td>
						<td align="right">
							<xsl:text>ID:</xsl:text>
						</td>
						<td>
							<xsl:value-of select="/invoice:response/invoice:invoice/@response_id"/>
						</td>
					</tr>
					<tr bgcolor="#ccccff">
						<td>
							<xsl:text>Empfänger:</xsl:text>
						</td>
						<td colspan="3">
							<xsl:value-of select="/invoice:response/invoice:header/invoice:recipient/@ean_party"/>
						</td>
					</tr>
					<!--
					*********************************************************************************************
						Header: Bezug auf Rechnung
					*********************************************************************************************
					-->
					<tr bgcolor="#3399ff">
						<td colspan="4" valign="top">
							<span style="color:white;font-weight:bold;">
								<xsl:text>Bezug auf folgende Rechnung:</xsl:text>
							</span>
						</td>
					</tr>
					<tr bgcolor="#ccccff">
						<td>
							<xsl:text>Datum:</xsl:text>
						</td>
						<td>
							<xsl:call-template name="formatShortDateTime">
								<xsl:with-param name="date"
								  select="/invoice:response/invoice:invoice/@invoice_date"/>
							</xsl:call-template>
						</td>
						<td valign="top" align="right">
							<xsl:text>Timestamp:</xsl:text>
						</td>
						<td valign="top">
							<xsl:value-of select="/invoice:response/invoice:invoice/@invoice_timestamp"/>
						</td>
					</tr>
					<tr bgcolor="#ccccff">
						<td valign="top">
							<xsl:text>Fallnr:</xsl:text>
						</td>
						<td valign="top">
							<xsl:value-of select="/invoice:response/invoice:invoice/@case_id"/>
						</td>
						<td align="right">
							<xsl:text>ID:</xsl:text>
						</td>
						<td>
							<xsl:value-of select="/invoice:response/invoice:invoice/@invoice_id"/>
						</td>
					</tr>
					<tr bgcolor="#ccccff">
						<td>
							<xsl:text>Rechnungssteller:</xsl:text>
						</td>
						<td colspan="3">
							<xsl:value-of select="/invoice:response/invoice:invoice/invoice:biller/@ean_party"/>
						</td>
					</tr>
					<tr bgcolor="#ccccff">
						<td>
							<xsl:text>Kostenträger:</xsl:text>
						</td>
						<td colspan="3">
							<xsl:value-of select="/invoice:response/invoice:invoice/invoice:insurance/@ean_party"/>
						</td>
					</tr>
				</table>
				<!--
				*********************************************************************************************
					Body
			    *********************************************************************************************
				-->
				<xsl:apply-templates select="/invoice:response/invoice:status"/>
				<!--
				*********************************************************************************************
					Footer
			    *********************************************************************************************
				-->
				<hr/>
				<table width="100%" cellspacing="1" cellpadding="5">
					<tr bgcolor="#3399ff">
						<td colspan="4" valign="top">
							<span style="color:white;font-weight:bold;">
								<xsl:text>Weitere Informationen</xsl:text>
							</span>
						</td>
					</tr>
					<xsl:apply-templates select="/invoice:response/invoice:invoice/invoice:reply"/>
					<tr bgcolor="#ccccff">
						<td valign="top">
							<xsl:text>Branchenapplikation:</xsl:text>
						</td>
						<td valign="top">
							<xsl:value-of select="/invoice:response/invoice:prolog/invoice:package"/>
						</td>
						<td valign="top" align="right">
							<xsl:text>Version:</xsl:text>
						</td>
						<td valign="top">
							<xsl:value-of select="/invoice:response/invoice:prolog/invoice:package/@version"/>
						</td>
					</tr>
					<tr bgcolor="#ccccff">
						<td width="20%" valign="top">
							<xsl:text>XML Generator:</xsl:text>
						</td>
						<td width="50%" valign="top">
							<xsl:value-of select="/invoice:response/invoice:prolog/invoice:generator/invoice:software"/>
						</td>
						<td width="15%" valign="top" align="right">
							<xsl:text>Version:</xsl:text>
						</td>
						<td width="15%" valign="top">
							<xsl:value-of select="/invoice:response/invoice:prolog/invoice:generator/invoice:software/@version"/>
						</td>
					</tr>
					<tr bgcolor="#ccccff">
						<td width="20%" valign="top">
							<xsl:text>Validator:</xsl:text>
						</td>
						<td width="50%" valign="top">
							<xsl:value-of select="/invoice:response/invoice:prolog/invoice:validator"/>
						</td>
						<td width="15%" valign="top" align="right">
							<xsl:text>Version:</xsl:text>
						</td>
						<td width="15%" valign="top">
							<xsl:value-of select="/invoice:response/invoice:prolog/invoice:validator/@version_software"/>
						</td>
					</tr>
					<tr bgcolor="#ccccff">
						<td width="20%" valign="top">
							<xsl:text>Validierungsfokus:</xsl:text>
						</td>
						<td width="50%" valign="top">
							<xsl:variable name="focus"
							  select="/invoice:response/invoice:prolog/invoice:validator/@focus"/>
							<xsl:choose>
								<xsl:when test="$focus='tarmed'">TarMed</xsl:when>
								<xsl:when test="$focus='cantonal'">Kantonale Tarife</xsl:when>
								<xsl:when test="$focus='lab'">Eidg. Analysenliste</xsl:when>
								<xsl:when test="$focus='unclassified'">Übrige Tarife</xsl:when>
								<xsl:when test="$focus='drug'">Medikamente</xsl:when>
								<xsl:when test="$focus='migel'">MiGel</xsl:when>
								<xsl:when test="$focus='physio'">Physiotherapie Tarif</xsl:when>
								<xsl:when test="$focus='other'">Andere Tarife</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="/invoice:response/invoice:prolog/invoice:validator/@focus"/>
								</xsl:otherwise>
							</xsl:choose>
						</td>
						<td width="15%" valign="top" align="right">
							<xsl:text>DB Version:</xsl:text>
						</td>
						<td width="15%" valign="top">
							<xsl:value-of select="/invoice:response/invoice:prolog/invoice:validator/@version_db"/>
						</td>
					</tr>
					<tr bgcolor="#ccccff">
						<td width="20%" valign="top">
							<xsl:text>Rendering:</xsl:text>
						</td>
						<td width="50%" valign="top">
							<xsl:value-of select="$thisStyleSheet"/>
						</td>
						<td width="15%" valign="top" align="right">
							<xsl:text>Version:</xsl:text>
						</td>
						<td width="15%" valign="top">
							<xsl:value-of select="$thisStyleSheetVersion"/>
						</td>
					</tr>
				</table>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="/invoice:response/invoice:invoice/invoice:reply">
		<tr bgcolor="#ccccff">
			<td width="20%" valign="top">
				<xsl:text>Korrespondenz:</xsl:text>
			</td>
		<td valign="top" align="left">
		<xsl:value-of select="./invoice:company/invoice:companyname"/>
		<xsl:choose>
			<xsl:when test="./@ean_party">
				<xsl:text> (</xsl:text>
				<xsl:value-of select="./@ean_party"/>
				<xsl:text>)</xsl:text>
			</xsl:when>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="./invoice:company/invoice:department">
				<br/>
				<xsl:value-of select="./invoice:company/invoice:department"/>
			</xsl:when>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="./invoice:company/invoice:postal/invoice:street">
				<br/>
				<xsl:value-of select="./invoice:company/invoice:postal/invoice:street"/>
			</xsl:when>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="./invoice:company/invoice:postal/invoice:pobox">
				<br/>
				<xsl:text>Postfach </xsl:text>
				<xsl:value-of select="./invoice:company/invoice:postal/invoice:pobox"/>
			</xsl:when>
		</xsl:choose>
		<br/>
		<xsl:choose>
			<xsl:when test="./invoice:company/invoice:postal/invoice:zip">
				<xsl:value-of select="./invoice:company/invoice:postal/invoice:zip"/>
				<xsl:text> </xsl:text>
			</xsl:when>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="./invoice:company/invoice:postal/invoice:city">
				<xsl:value-of select="./invoice:company/invoice:postal/invoice:city"/>
			</xsl:when>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="./invoice:company/invoice:postal/invoice:zip/@statecode">
				<xsl:text> </xsl:text>
				<xsl:value-of select="./invoice:company/invoice:postal/invoice:zip/@statecode"/>
			</xsl:when>
		</xsl:choose>
		</td>
		<td valign="top" align="right">
			<xsl:choose>
				<xsl:when test="./invoice:company/invoice:telecom/invoice:phone">
					<xsl:text>Tel: </xsl:text>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="./invoice:company/invoice:telecom/invoice:fax">
					<br/>
					<xsl:text>Fax: </xsl:text>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="./invoice:company/invoice:online/invoice:email">
					<br/>
					<xsl:text>eMail: </xsl:text>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="./invoice:company/invoice:online/invoice:url">
					<br/>
					<xsl:text>Web: </xsl:text>
				</xsl:when>
			</xsl:choose>
		</td>
		<td valign="top" align="left">
			<xsl:choose>
				<xsl:when test="./invoice:company/invoice:telecom/invoice:phone">
					<xsl:value-of select="./invoice:company/invoice:telecom/invoice:phone"/>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="./invoice:company/invoice:telecom/invoice:fax">
					<br/>
					<xsl:value-of select="./invoice:company/invoice:telecom/invoice:fax"/>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="./invoice:company/invoice:online/invoice:email">
					<br/>
					<xsl:value-of select="./invoice:company/invoice:online/invoice:email"/>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="./invoice:company/invoice:online/invoice:url">
					<br/>
					<xsl:value-of select="./invoice:company/invoice:online/invoice:url"/>
				</xsl:when>
			</xsl:choose>
		</td>
		</tr>
		<tr bgcolor="#ccccff">
			<td width="20%" valign="top">
				<xsl:text>Kontaktperson:</xsl:text>
			</td>
			<td valign="top" align="left">
				<xsl:choose>
					<xsl:when test="./invoice:contact/@salutation">
						<xsl:value-of select="./invoice:contact/@salutation"/>
						<xsl:text> </xsl:text>
					</xsl:when>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="./invoice:contact/@title">
						<xsl:value-of select="./invoice:contact/@title"/>
						<xsl:text> </xsl:text>
					</xsl:when>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="./invoice:contact/invoice:givenname">
						<xsl:value-of select="./invoice:contact/invoice:givenname"/>
						<xsl:text> </xsl:text>
					</xsl:when>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="./invoice:contact/invoice:familyname">
						<xsl:value-of select="./invoice:contact/invoice:familyname"/>
					</xsl:when>
				</xsl:choose>
			</td>
			<td valign="top" align="right">
				<xsl:choose>
					<xsl:when test="./invoice:contact/invoice:telecom/invoice:phone">
						<xsl:text>Tel: </xsl:text>
					</xsl:when>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="./invoice:contact/invoice:telecom/invoice:fax">
						<br/>
						<xsl:text>Fax: </xsl:text>
					</xsl:when>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="./invoice:contact/invoice:online/invoice:email">
						<br/>
						<xsl:text>eMail: </xsl:text>
					</xsl:when>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="./invoice:contact/invoice:online/invoice:url">
						<br/>
						<xsl:text>Web: </xsl:text>
					</xsl:when>
				</xsl:choose>
			</td>
			<td valign="top" align="left">
				<xsl:choose>
					<xsl:when test="./invoice:contact/invoice:telecom/invoice:phone">
						<xsl:value-of select="./invoice:contact/invoice:telecom/invoice:phone"/>
					</xsl:when>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="./invoice:contact/invoice:telecom/invoice:fax">
						<br/>
						<xsl:value-of select="./invoice:contact/invoice:telecom/invoice:fax"/>
					</xsl:when>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="./invoice:contact/invoice:online/invoice:email">
						<br/>
						<xsl:value-of select="./invoice:contact/invoice:online/invoice:email"/>
					</xsl:when>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="./invoice:contact/invoice:online/invoice:url">
						<br/>
						<xsl:value-of select="./invoice:contact/invoice:online/invoice:url"/>
					</xsl:when>
				</xsl:choose>
			</td>
		</tr>	</xsl:template>
	<!--
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		Payload: rejected
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	-->
	<xsl:template match="/invoice:response/invoice:status/invoice:rejected">
		<hr/>
		<h1>
			<xsl:text>Rückweisung der Rechnung (endgültig)</xsl:text>
		</h1>
		<p>
			<xsl:call-template name="formatReason">
				<xsl:with-param name="reason"
				  select="./invoice:explanation"/>
			</xsl:call-template>
			<table width="100%" cellspacing="1" cellpadding="5">
				<tr>
					<th valign="top" align="left">
						Hauptbereich
					</th>
					<th valign="top" align="left">
						Nebenbereich
					</th>
					<th valign="top" align="left">
						Fehlercode
					</th>
					<th valign="top" align="left">
						Fehlerart
					</th>
				</tr>
			<xsl:for-each select="./invoice:error">
				<tr>
					<td valign="top" align="left">
						<xsl:value-of select="./@major"/>
					</td>
					<td valign="top" align="left">
						<xsl:value-of select="./@minor"/>
					</td>
					<td valign="top" align="left">
						<xsl:value-of select="./@error"/>
					</td>
					<td valign="top" align="left">
						<xsl:apply-templates select="."/>
					</td>
				</tr>
			</xsl:for-each>
			</table>
		</p>
	</xsl:template>
	<xsl:template match="/invoice:response/invoice:status/invoice:rejected/invoice:error/invoice:error_business">
		<b>
			<xsl:text>Fachlicher Fehler: </xsl:text>
		</b>
		<xsl:value-of select="."/>
		<br/>
		<br/>
		<xsl:text>Pos: </xsl:text>
		<xsl:value-of select="./@record_id"/>
		<br/>
		<xsl:text>Falscher Wert: </xsl:text>
		<xsl:value-of select="./@error_value"/>
		<br/>
		<xsl:text>Gültiger Wert: </xsl:text>
		<xsl:value-of select="./@valid_value"/>
		<br/>
		<br/>
	</xsl:template>
	<xsl:template match="/invoice:response/invoice:status/invoice:rejected/invoice:error/invoice:error_schema">
		<b>
			<xsl:text>XML Fehler: </xsl:text>
		</b>
		<xsl:value-of select="."/>
		<br/>
		<br/>
		<xsl:text>Zeile: </xsl:text>
		<xsl:value-of select="./@line_number"/>
		<br/>
		<xsl:text>Pos: </xsl:text>
		<xsl:value-of select="./@line_pos"/>
		<br/>
		<xsl:text>Fehler-Code: </xsl:text>
		<xsl:value-of select="./@err_code"/>
		<br/>
		<xsl:text>Fehler-Text: </xsl:text>
		<xsl:value-of select="./@err_text"/>
	</xsl:template>
	<!--
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		Payload: calledIn
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	-->
	<xsl:template match="/invoice:response/invoice:status/invoice:calledIn">
		<hr/>
		<h1>
			<xsl:text>Einforderung von Unterlagen/Informationen (vorläufig)</xsl:text>
		</h1>
		<p>
			<xsl:call-template name="formatReason">
				<xsl:with-param name="reason"
				  select="./invoice:explanation"/>
			</xsl:call-template>
			Einforderungs-Code:
			<xsl:value-of select="./invoice:error/@major"/>
		</p>
	</xsl:template>
	<!--
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		Payload: pending
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	-->
	<xsl:template match="/invoice:response/invoice:status/invoice:pending">
		<hr/>
		<h1>
			<xsl:text>Vorbescheid (vorläufig)</xsl:text>
		</h1>
		<p>
			<xsl:value-of select="./invoice:explanation"/>
		</p>
	</xsl:template>
	<!--
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		Payload: resend
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	-->
	<xsl:template match="/invoice:response/invoice:status/invoice:resend">
		<hr/>
		<h1>
			<xsl:text>Anforderung einer elektronischen rechnungs-Kopie (vorläufig)</xsl:text>
		</h1>
		<p>
			<xsl:value-of select="./invoice:explanation"/>
		</p>
	</xsl:template>
	<!--
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		Payload: modified
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	-->
	<xsl:template match="/invoice:response/invoice:status/invoice:modified">
		<hr/>
		<h1>
			<xsl:text>Korrektur der Rechnung (endgültig)</xsl:text>
		</h1>
		<p>
			<xsl:call-template name="formatReason">
				<xsl:with-param name="reason"
				  select="./invoice:explanation"/>
			</xsl:call-template>
			<table width="100%" cellspacing="1" cellpadding="5">
				<tr>
					<th valign="top" align="left">
						Hauptbereich
					</th>
					<th valign="top" align="left">
						Nebenbereich
					</th>
					<th valign="top" align="left">
						Fehlercode
					</th>
					<th valign="top" align="left">
						Fehlerart
					</th>
				</tr>
				<xsl:for-each select="./invoice:error">
					<tr>
						<td valign="top" align="left">
							<xsl:value-of select="./@major"/>
						</td>
						<td valign="top" align="left">
							<xsl:value-of select="./@minor"/>
						</td>
						<td valign="top" align="left">
							<xsl:value-of select="./@error"/>
						</td>
						<td valign="top" align="left">
							<xsl:apply-templates select="./invoice:error_business"/>
						</td>
					</tr>
				</xsl:for-each>
			</table>
			<br/>
			<hr/>
			<table width="100%" cellspacing="1" cellpadding="5">
				<tr>
					<td valign="top" align="left">
						<br/>
						<b>Gesamtbeträge Rechnung:</b>
					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						Bruttobetrag: <xsl:value-of select="./invoice:balance/@amount"/>
					</td>
					<td valign="top" align="left">
						Anzahlung: <xsl:value-of select="./invoice:balance/@amount_prepaid"/>
					</td>
					<td valign="top" align="left">
						Nettobetrag: <xsl:value-of select="./invoice:balance/@amount_due"/>
					</td>
					<td valign="top" align="left">
						Währung: <xsl:value-of select="./invoice:balance/@currency"/>
					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						<br/>
						<i>Gesmtbeträge Pflichtleistungen:</i>
					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						 TarMed: <xsl:value-of select="./invoice:balance/@amount_tarmed"/>
					</td>
					<td valign="top" align="left">
						Kantonal: <xsl:value-of select="./invoice:balance/@amount_cantonal"/>
					</td>
					<td valign="top" align="left">
						Eidg. Analysenliste: <xsl:value-of select="./invoice:balance/@amount_lab"/>
					</td>
					<td valign="top" align="left">
						MiGel: <xsl:value-of select="./invoice:balance/@amount_migel"/>
					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						Physio: <xsl:value-of select="./invoice:balance/@amount_physio"/>
					</td>
					<td valign="top" align="left">
						Medikamente: <xsl:value-of select="./invoice:balance/@amount_drug"/>
					</td>
					<td valign="top" align="left">
						Übrige: <xsl:value-of select="./invoice:balance/@amount_unclassified"/>
					</td>
					<td valign="top" align="left">
						Total: <xsl:value-of select="./invoice:balance/@amount_obligations"/>
					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						<br/>
						<i>Zusammenfassung TarMed:</i>
					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						ärztl. Pflichtleistungen
					</td>
					<td valign="top" align="left">
						Betrag: <xsl:value-of select="./invoice:balance/@amount_tarmed.mt"/>
					</td>
					<td valign="top" align="left">
						Taxpunkte: <xsl:value-of select="./invoice:balance/@unit_tarmed.mt"/>
					</td>
					<td valign="top" align="left">

					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						techn. Pflichtleistungen
					</td>
					<td valign="top" align="left">
						Betrag: <xsl:value-of select="./invoice:balance/@amount_tarmed.tt"/>
					</td>
					<td valign="top" align="left">
						Taxpunkte: <xsl:value-of select="./invoice:balance/@unit_tarmed.tt"/>
					</td>
					<td valign="top" align="left">

					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						<br/>
						<b>Korrigierte Gesamtbeträge:</b>
					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						Bruttobetrag: <xsl:value-of select="./invoice:balance_corrected/@amount"/>
					</td>
					<td valign="top" align="left">
						Anzahlung: <xsl:value-of select="./invoice:balance_corrected/@amount_prepaid"/>
					</td>
					<td valign="top" align="left">
						Nettobetrag: <xsl:value-of select="./invoice:balance_corrected/@amount_due"/>
					</td>
					<td valign="top" align="left">
						Währung: <xsl:value-of select="./invoice:balance_corrected/@currency"/>
					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						<br/>
						<i>Gesmtbeträge Pflichtleistungen:</i>
					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						TarMed: <xsl:value-of select="./invoice:balance_corrected/@amount_tarmed"/>
					</td>
					<td valign="top" align="left">
						Kantonal: <xsl:value-of select="./invoice:balance_corrected/@amount_cantonal"/>
					</td>
					<td valign="top" align="left">
						Eidg. Analysenliste: <xsl:value-of select="./invoice:balance_corrected/@amount_lab"/>
					</td>
					<td valign="top" align="left">
						MiGel: <xsl:value-of select="./invoice:balance_corrected/@amount_migel"/>
					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						Physio: <xsl:value-of select="./invoice:balance_corrected/@amount_physio"/>
					</td>
					<td valign="top" align="left">
						Medikamente: <xsl:value-of select="./invoice:balance_corrected/@amount_drug"/>
					</td>
					<td valign="top" align="left">
						Übrige: <xsl:value-of select="./invoice:balance_corrected/@amount_unclassified"/>
					</td>
					<td valign="top" align="left">
						Total: <xsl:value-of select="./invoice:balance_corrected/@amount_obligations"/>
					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						<br/>
						<i>Zusammenfassung TarMed:</i>
					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						ärztl. Pflichtleistungen
					</td>
					<td valign="top" align="left">
						Betrag: <xsl:value-of select="./invoice:balance_corrected/@amount_tarmed.mt"/>
					</td>
					<td valign="top" align="left">
						Taxpunkte: <xsl:value-of select="./invoice:balance_corrected/@unit_tarmed.mt"/>
					</td>
					<td valign="top" align="left">

					</td>
				</tr>
				<tr>
					<td valign="top" align="left">
						techn. Pflichtleistungen
					</td>
					<td valign="top" align="left">
						Betrag: <xsl:value-of select="./invoice:balance_corrected/@amount_tarmed.tt"/>
					</td>
					<td valign="top" align="left">
						Taxpunkte: <xsl:value-of select="./invoice:balance_corrected/@unit_tarmed.tt"/>
					</td>
					<td valign="top" align="left">

					</td>
				</tr>
			</table>			
			<br/>
			<hr/>
			<table width="100%" cellspacing="1" cellpadding="5">
				<tr>
					<td valign="top" align="left">
						<br/>
						<b>Details zu den Leistungspositionen:</b>
					</td>
				</tr>
			</table>
			<table width="100%" cellspacing="1" cellpadding="5">
				<tr>
					<th valign="top" align="left">
						Pos
					</th>
					<th valign="top" align="left">
						Text
					</th>
					<th valign="top" align="left">
						Tarif
					</th>
					<th valign="top" align="left">
						Aktion
					</th>
					<th valign="top" align="left">
						Begründung
					</th>
				</tr>
				<xsl:for-each select="./invoice:services/*">
					<xsl:call-template name="formatInvoiceRecord" />
				</xsl:for-each>
			</table>
		</p>
	</xsl:template>
	<xsl:template match="/invoice:response/invoice:status/invoice:modified/invoice:error/invoice:error_business">
		<b>
			<xsl:text>Fachlicher Fehler: </xsl:text>
		</b>
		<xsl:value-of select="."/>
		<br/>
		<br/>
		<xsl:text>Pos: </xsl:text>
		<xsl:value-of select="./@record_id"/>
		<br/>
		<xsl:text>Falscher Wert: </xsl:text>
		<xsl:value-of select="./@error_value"/>
		<br/>
		<xsl:text>Gültiger Wert: </xsl:text>
		<xsl:value-of select="./@valid_value"/>
		<br/>
		<br/>
	</xsl:template>
	<xsl:template name="formatInvoiceRecord">
		<tr>
			<td valign="top" align="left">
				<xsl:value-of select="./@record_id"/>
			</td>
			<td valign="top" align="left">
				<xsl:value-of select="."/>
			</td>
			<td valign="top" align="left">
				<xsl:choose>
					<xsl:when test=".=/invoice:response/invoice:status/invoice:modified/invoice:services/invoice:record_tarmed">TarMed</xsl:when>
					<xsl:when test=".=/invoice:response/invoice:status/invoice:modified/invoice:services/invoice:record_cantonal">Kantonal</xsl:when>
					<xsl:when test=".=/invoice:response/invoice:status/invoice:modified/invoice:services/invoice:record_unclassified">Übrige</xsl:when>
					<xsl:when test=".=/invoice:response/invoice:status/invoice:modified/invoice:services/invoice:record_lab">Eidg. Analysenliste</xsl:when>
					<xsl:when test=".=/invoice:response/invoice:status/invoice:modified/invoice:services/invoice:record_migel">MiGel</xsl:when>
					<xsl:when test=".=/invoice:response/invoice:status/invoice:modified/invoice:services/invoice:record_physio">Physio</xsl:when>
					<xsl:when test=".=/invoice:response/invoice:status/invoice:modified/invoice:services/invoice:record_drug">Medikamente</xsl:when>
					<xsl:otherwise>
						Unbekannt
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td valign="top" align="left">
				<xsl:variable name="correctCode"
				  select="./@status"/>
				<xsl:choose>
					<xsl:when test="$correctCode='added'">Hinzugefügt</xsl:when>
					<xsl:when test="$correctCode='corrected'">Korrigiert</xsl:when>
					<xsl:when test="$correctCode='rejected'">Zurückgewiesen</xsl:when>
					<xsl:otherwise>
						Unbekannt
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td valign="top" align="left">
				<xsl:value-of select="./@comment"/>
			</td>
		</tr>
	</xsl:template>

	<!--
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		Payload: annulment
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	-->
	<xsl:template match="/invoice:response/invoice:status/invoice:annulment">
		<hr/>
		<h1>
			<xsl:text>Antwort auf eine Rechnungs-Annullierung (endgültig)</xsl:text>
		</h1>
		<p>
			<b>Antwort:</b>
			<br/>
			<xsl:choose>
				<xsl:when test="./invoice:notAccepted">Die Annullierung wird nicht akzeptiert.</xsl:when>
				<xsl:when test="./invoice:accepted">Die Annullierung der noch nicht bezahlten Rechnung wird akzeptiert.</xsl:when>
				<xsl:when test="./invoice:invoiceNotFound">Die zu annullierende Rechnung wurde nicht gefunden.</xsl:when>
				<xsl:when test="./invoice:invoiceRejected">Die zu annullierende Rechnung wurde bereits zurückgewiesen.</xsl:when>
				<xsl:when test="./invoice:invoicePaid">Die Annullierung der bereits bezahlten Rechnung wird akzeptiert.</xsl:when>
				<xsl:otherwise>
					Unbekannt
				</xsl:otherwise>
			</xsl:choose>
			<br/>
			<br/>
			<xsl:call-template name="formatReason">
				<xsl:with-param name="reason"
				  select="./invoice:explanation"/>
			</xsl:call-template>
			<xsl:for-each select="./invoice:invoicePaid/*">
				<xsl:call-template name="formatESR" />
			</xsl:for-each>
		</p>
	</xsl:template>
	<!--
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		Payload: creditAdvice
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	-->
	<xsl:template match="/invoice:response/invoice:status/invoice:creditAdvice">
		<hr/>
		<h1>
			<xsl:text>Antwort auf eine Gutschrifts-Anzeige (endgültig)</xsl:text>
		</h1>
		<p>
			<b>Antwort:</b>
			<br/>
			<xsl:choose>
				<xsl:when test="./invoice:notAccepted">Die Gutschrifts-Anzeige wird nicht akzeptiert.</xsl:when>
				<xsl:when test="./invoice:accepted">Die Gutschrifts-Anzeige der noch nicht bezahlten Rechnung wird akzeptiert.</xsl:when>
				<xsl:otherwise>
					Unbekannt
				</xsl:otherwise>
			</xsl:choose>
			<br/>
			<br/>
			<xsl:call-template name="formatReason">
				<xsl:with-param name="reason"
				  select="./invoice:explanation"/>
			</xsl:call-template>
			<xsl:for-each select="./invoice:accepted/*">
				<xsl:call-template name="formatESR" />
			</xsl:for-each>
		</p>
	</xsl:template>
	<!--
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		Formatierungsfunktionen
	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	-->
	<xsl:template name="formatShortDateTime">
		<xsl:param name="date"/>
		<xsl:value-of select="substring ($date, 9, 2)"/>
		<xsl:text>.</xsl:text>
		<xsl:value-of select="substring ($date, 6, 2)"/>
		<xsl:text>.</xsl:text>
		<xsl:value-of select="substring ($date, 1, 4)"/>
		<xsl:choose>
			<xsl:when test="substring ($date, 11, 9)='T00:00:00'">
			</xsl:when>
			<xsl:otherwise>
				<xsl:text> </xsl:text>
				<xsl:value-of select="substring ($date, 12, 2)"/>
				<xsl:text>:</xsl:text>
				<xsl:value-of select="substring ($date, 15, 2)"/>
				<xsl:text>:</xsl:text>
				<xsl:value-of select="substring ($date, 18, 2)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="formatReason">
		<xsl:param name="reason"/>
		<b>Begründung:</b>
		<br/>
		<xsl:choose>
			<xsl:when test="$reason">
				<xsl:value-of select="$reason"/>
			</xsl:when>
			<xsl:otherwise>
				(Keine)
			</xsl:otherwise>
		</xsl:choose>
		<br/>
		<br/>
	</xsl:template>
	<xsl:template name="formatESR">
		<br/>
		<b>Zahlungsinformationen</b>
		<br/>
		<table width="100%" cellspacing="1" cellpadding="5">
			<tr>
				<th valign="top" align="left">
					Betrag
				</th>
				<th valign="top" align="left">
					Referenznummer
				</th>
				<th valign="top" align="left">
					Kontoverbindung
				</th>
			</tr>
			<tr>
				<td valign="top" align="left">
					<xsl:value-of select="./@amount_due"/>
				</td>
				<td valign="top" align="left">
					<xsl:value-of select="./@reference_number"/>
				</td>
				<td valign="top" align="left">
					<i>
						Diese Information kann hier noch nicht angezeigt werden!<br/>Bitte senden Sie diese XML Datei an info@medshare.net.
					</i>
				</td>
			</tr>
			<tr>
				<td colspan="3" valign="top" align="left">
					<b>ESR Zeile: </b><xsl:value-of select="./@coding_line"/>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="formatBank">
	</xsl:template>
</xsl:stylesheet>
