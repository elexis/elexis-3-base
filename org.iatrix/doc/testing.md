# Test cases

* Opening KG-Iatrix a new kons for the current day should be created (eventually also creating a new case)
* When leaving the KG Iatrix an empty kons should be deleted
* KG Iatrix and Iatrix Konsultation should update the current patient in the following cases (and be in sync with the patient displayed at the top of Elexis 
** Selecting a new patient in the Patientenübersicht
** Selecting a labor item of different patient
** Selecting an agenda item which switches patient
** Close the view, change active patient, open it again
* The list of konsultations must be refeshed in the following cases
* A kons created in (Gerry's) Kons View
* A kons deleted in (Gerry's) Kons View
* A new patient is created
* After saving a Kons Entry in KG Iatrix the changed content should be visible in the list below


* Open a patient which has different konsultations
** Change the konstext in oned
** Switch in the lower part to a different konsultation
** Verify that the changes got correctly applied and that the list is correctly updated.
** Verify that the konsText, verrechnungen, problem, etc adapt when changing konsultations

* Open a kons
** Drop a verrechenbar item into the verrechnbar
** The total must be updated

Testing changing assigned user
* Select a kons not being from you with an empty content
** Change author to you
** Verify that the labels change
** Change to another konsultation
** Verify that the labels change
** Change back to your konsultation
** Show the attributes you als author?
* Repeat the same with a  konsultation which has text in it

Testcase from ticket 10686
* KG Iatrix displays patient with kons
** Leave JournalView e.g. changing to Agenda Gross
** Select a different patient
** Select JournalView again
** The new patient must be selected

Testcase from 10713

* Open kons not from you
** Open another Elexis which user who created konst
** Change Konsext
** Close other Elexis
* add Leistung
* select other patient
* Konstext may not change and display after reselecting the same patient the changed content
