# Labor TeamW Plugin
## Description
The _Labor TeamW_ Plugin (ch.framsteg.elexis.labor.teamw) creates a lab order from the choosen patient's context and sends it to Labor TeamW Endpoint: [https://reports.team-w.ch/onlineRequestV2/oRWS.asmx](https://reports.team-w.ch/onlineRequestV2/oRWS.asmx). The following metadata are encoded as GDT:

	- PatientenNr
	- Geschlecht
	- Title
	- Name
	- Vorname
	- Geburtstag
	- Adresse
	- PLZ
	- Ort
	- Land
	- AHV Nummer
	- Kartennummer (Covercard)
	- Mobiltelefon
	- E-Mail
	- Fall
	- Versicherungsnummer
	- Versicherungs EAN
	- Fall Grund

The metadata is put into a XMl structure (message body), signed with the plugin specific private key and send as SOAP request

## Technical note
The _Labor TeamW_ plugin (ch.framsteg.elexis.labor.teamw) creates a message header containing two timestamps (UTC / local) which must be synchronized otherwise the Labor TeamW Endpoint would reject the request. The plugin takes into account the daylight saving time (DST). Afterward the message (header including body) is signed with a plugin specific private key. This key is not part of the Github repository and need therefore be requested by olivier.debenath@framsteg.ch.

## How to install
The plugin is implicitly installed by installing the superordinated [feature](https://github.com/elexis/elexis-3-base/tree/master/features/ch.framsteg.elexis.finance.analytics.feature).

## License
Copyright 2024 Framsteg GmbH / olivier.debenath@framsteg.ch
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
