# Covercard® Feature

## Description
The Covercard plugin queries patient data starting from the cardnumber which acts as a kind of primary key. The resulting patient data is displayed to the user (transient) but only the identifiers are persisted in the database. The plugin allows to inspect the data to find out missing patient properties.

## Technical note

- The plugin sends the query to [http://covercard.hin.ch/covercard/servlet/ch.ofac.ca.covercard.CaValidationHorizontale?type=XXX&langue=1&carte=YYY&ReturnType=42a](http://covercard.hin.ch/covercard/servlet/ch.ofac.ca.covercard.CaValidationHorizontale?type=XXX&langue=1&carte=YYY&ReturnType=42a)

- The plugin uses a HIN proxy to secure the sensitive data

- The plugin receives a XML repsonse containing all the patient's data

- The plugin stores the following values in the Elexis database:
    - The insurance number using the key _www.xid.ch/covercard/insured-number_
    - The card number using the key _www.xid.ch/covercard/card-number_
    - The insured person number using the key _www.xid.ch/covercard/insured-person-number_

## How to install
The plugin is implicitly installed by installing the superordinated [feature](https://github.com/elexis/elexis-3-base/tree/master/features/ch.framsteg.elexis.administration.covercard.feature).

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