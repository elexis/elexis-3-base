# Analytics/Reporting Plugin
## Description
The _Analytics/Reporting_ Plugin (ch.framsteg.elexis.finance.analytics) answers question like *How much money have I earned within a certain period?* or *Which was the most successful year/month?* and *How does the daily report of specific day look like?* and so forth. The information is retrieved by eight different queries:

## Technical note
The _Analytics/Reporting_ Plugin (ch.framsteg.elexis.finance.analytics) acts as a lightweight frontend to inspect denormalized data. Due to performance considerations and due to structure of the data being requested the plugin makes heavily use of SQL specific functions and materialized views. Therefore the queries evade the Elexis O/R mapping being implemented as native SQL queries. Nevertheless the plugin consumes the JDBC connection which is provied by Elexis.

**NOTE:**
**The plugin runs only against PostgreSQL**

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
