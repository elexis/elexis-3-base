# Labor TeamW Feature (Public License)
## Name
* Label: _Labor TeamW Feature (Public License)_
* id: _ch.framsteg.elexis.labor.teamw.feature_
* Category: _Dataexchange_

## Subordinated Plugin(s)
* [ch.framsteg.elexis.labor.teamw](https://github.com/elexis/elexis-3-base/tree/master/bundles/ch.framsteg.elexis.labor.teamw)

## Description
The Labor TeamW feature allows automatic creation und transmission of lab orders from Elexis using the patients metadata. The communication between the Labor TeamW feature and the Labor TeamW endpoint is unidirectional. The requested lab order is managed within the Labor TeamW's application that can be accessed via [https://reports.team-w.ch](https://reports.team-w.ch). 

**NOTE:**
**The plugin runs only against PostgreSQL**

## How to install
The plugin can easily be installed via _Help/Install New Software_. The feature _Labor TeamW_ is listed within the _Dataexchange_ category. Within the features preference page, a few configurations need to be done:

 - Benutzername (provicec by TeamW)
 - Passwort (provided by TeamW)
 - Pfad zum Schlüssel / Durchsuchen (Choose file system path to the locally stored private key)
 
 **NOTE:**
**The requested private key to sign the lab order is not stored within the public Github repositories. To get the key contact olivier.debenath@framsteg.ch **
 
## How to use
1. Open the feature's view
2. Choose patient from the patient view
3. Choose case from the case view.
4. In the feature's view check if there are missing properties
5. Hit the send button. The lab order can be continued within https://reports.team-w.ch](https://reports.team-w.ch)

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