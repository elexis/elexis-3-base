# Covercard®  Feature (Public License)
## Name
* Label: _Covercard® Query Feature for Elexis_
* id: _ch.framsteg.elexis.administration.covercard.feature_
* Category: _Dataexchange_

## Subordinated Plugin(s)
* [ch.framsteg.elexis.administration.covercard](https://github.com/elexis/elexis-3-base/tree/master/bundles/ch.framsteg.elexis.administration.covercard)

## Description
Feature to query patient information from covercard.hin.ch. The according insurance number, social security number and the card number are being stored within Elexis DB. Once stored the patient's data can be queried, updated and exported to CSV at any time 

## Requirement
For security reasons the queries against covercard.hin.ch need a running HIN client (including HIN username/password) which must be reachable vie TCP/IP.

## How to install
The plugin can easily be installed via _Help/Install New Software_. The feature _Covercard®_ is listed within the _Dataexchange_ category. The feature is ready to use.

## How to use
The feature can be used for either registering tasks or inspecting purposes:

1. Enter the patient's card number manually or swipe the card through the card reader. If the patient does not exist wihtin the Elexis DB just register it. If the patient exists, the read card number can be associated afterwards

2. Within the patient table the columns can be sorted in both directions (ASC/DESC). This is useful to maintain/update the user data

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