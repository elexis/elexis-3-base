# Analytics/Reporting Feature
## Name
* Label: _Analytics/Reporting Plugin for Elexis_
* id: _ch.framsteg.elexis.finance.analytics.feature_
* Category: _Statistics_

## Subordinated Plugini(s)
* [ch.framsteg.elexis.finance.analytics[(https://github.com/elexis/elexis-3-base/tree/master/bundles/ch.framsteg.elexis.finance.analytics)
## Description
The Analytics/Reporting Plugin (ch.framsteg.elexis.finance.analytics) answers question like *How much money have I earned within a certain period?* or *Which was the most successful year/month?* and *How does the daily report of specific day look like?* and so forth. The information is retrieved by eight different queries:

* Sales per clearable medical services and devices, pharmaceuticals and laboratory analysis **(Umsatz/Leistung)**
* Sales per clearable medical services and devices, pharmaceuticals and laboratory analysis per year **(Umsatz/Leistung/Jahr)**
* Sales per clearable medical services and devices, pharmaceuticals and laboratory analysis per year/month **(Umsatz/Leistung/Jahr/Monat)**
* Sales per year **(Umsatz/Jahr)**
* Sales per year/month **(Umsatz/Jahr/Monat)**
* Sales Tarmed per year/month **(Umsatz Tarmed/Jahr/Monat)**
* Sales Pharmaceuticals per year/month **(Umsatz Medikamente/Jahr/Monat)**
* Daily Report **(Tagesrapport)**

All queries can optionally be constrained by upper and lower time limits (from/to). Without limits the queries deliver key data over the whole time periode of a medical practice. The resulting data refers to the mandant the logged on user belongs to and can be exported as either PDF or CSV.
## Technical note
The Analytics/Reporting Plugin (ch.framsteg.elexis.finance.analytics) acts as a lightweight frontend to inspect denormalized data. Due to performance considerations and due to structure of the data being requested the plugin makes heavily use of SQL specific functions and materialized views. Therefore the queries evade the Elexis O/R mapping being implemented as native SQL queries. Nevertheless the plugin consumes the JDBC connection which is provied by Elexis.

**NOTE:**
**The plugin runs only against PostgreSQL**

## How to install
The plugin can easily be installed via *Help/Install New Software*. The feature _Analytics/Reporting_ is listed within the _Statistik_ category. During start it checks if the required database system (PostgreSQL exclusively) is used. If so the necessary materialized view is created automatically. The plugin is ready to use.
## How to use
1. Choose query
2. Choose lower/upper time limit (optional)
3. Export as PDF/CSV

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
