/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "ch.gpb.elexis.cst.preferences.messages";

    public static String CstLaborPrefs_name;
    public static String CstLaborPrefs_firstname;
    public static String CstLaborPrefs_id;
    public static String CstLaborPrefs_short;
    public static String CstLaborPrefs_type;
    public static String CstLaborPrefs_unit;
    public static String CstLaborPrefs_refM;
    public static String CstLaborPrefs_refF;
    public static String CstLaborPrefs_sortmode;
    public static String CstLaborPrefs_LOINC;

    public static String CstCategory_name;
    public static String CstCategory_description;
    public static String CstCategory_icon;
    public static String CstCategory_nopatientselected;
    public static String MediVerlaufView_reading;
    public static String CstProfileEditor_title;
    public static String CstProfileEditor_enterProfileDetails;
    public static String SampleDataType_hasBoreFactor;
    public static String SampleDataType_hasFunFactor;
    public static String CstProfileEditor_Anzeige;
    public static String CstProfileEditor_Auswahlbefunde;
    public static String CstProfileEditor_Therapievorschlag;
    public static String CstProfileEditor_Proimmun;
    public static String CstProfileEditor_GastroColo;
    public static String CstProfileEditor_Darstellungsoptionen;
    public static String CstProfileEditor_Effektiv;
    public static String CstProfileEditor_MinimalMaximal;
    public static String CstProfileEditor_Zeitraster;
    public static String CstProfileEditor_Crawlback;
    public static String CstProfileEditor_CrawlbackTooltip;
    public static String CstProfileEditor_AuswahlBefundparameter;
    public static String CstProfileEditor_Diagnose;
    public static String CstProfileEditor_Datum;
    public static String CstProfileEditor_AnzahlGetesterLebensmittel;
    public static String CstProfileEditor_Reaktionsstaerke1;
    public static String CstProfileEditor_Reaktionsstaerke2;
    public static String CstProfileEditor_Reaktionsstaerke3;
    public static String CstProfileEditor_Reaktionsstaerke4;
    public static String CstProfileEditor_GastroMakroBefund;
    public static String CstProfileEditor_GastroHistoBefund;
    public static String CstProfileEditor_ColoMakroBefund;
    public static String CstProfileEditor_ColoHistoBefund;
    public static String CstProfileEditor_Normal;
    public static String CstProfileEditor_KeinBefund;
    public static String CstProfileEditor_Pathologisch;
    public static String CstProfileEditor_SaveProfile;
    public static String CstProfileEditor_AuswertungAnzeigen;
    public static String CstProfileEditor_CstProfilesFor;
    public static String CstProfileEditor_ProfileItems;
    public static String CstThemenblockEditor_CstGroups;
    public static String CstThemenblockEditor_LaborItems;
    public static String CstThemenblockEditor_SaveAbstract;
    public static String Button_MoveUp;
    public static String Button_MoveDown;
    public static String CstResultEffektiv_Titel;
    public static String CstResultMiniMax_Titel;
    public static String CstProfile_name;
    public static String CstProfile_description;
    public static String CstProfile_ValidFrom;
    public static String CstProfile_ValidTo;
    public static String CstProfile_Ranking;
    public static String CstProfile_Zeitspanne;
    public static String Cst_Text_bis;
    public static String Cst_Text_Profile_fuer;
    public static String Cst_Text_ungueltiger_Wert;
    //public static String Cst_Text_Themenblock_existiert;
    public static String Cst_Text_create_cstgroup;
    public static String Cst_Text_create_cstgroup_tooltip;
    public static String Cst_Text_create_cstprofile;
    public static String Cst_Text_create_cstprofile_tooltip;
    public static String Cst_Text_delete_cstgroup;
    public static String Cst_Text_cstgroup_exists;
    public static String Cst_Text_delete_cstgroup_tooltip;
    public static String Cst_Text_delete_profile;
    public static String Cst_Text_copy_profile;
    public static String Cst_Text_copy_profile_tooltip;
    public static String Cst_Text_delete_profile_tooltip;
    public static String Cst_Text_delete_from_cstgroup;
    public static String Cst_Text_cstgroup_exists_in_profile;
    public static String Cst_Text_delete_cstgroup_from_profile;
    public static String Cst_Text_add_cstgroup_to_profile;
    public static String Cst_Text_delete_cstgroup_from_profile_tooltip;
    public static String Cst_Text_delete_from_cstgroup_tooltip;
    public static String Cst_Text_Labitem_existiert_in_group;
    public static String Cst_Text_add_to_cstgroup;
    public static String Cst_Text_add_to_cstgroup_tooltip;
    public static String Cst_Text_new_cstgroup;
    public static String Cst_Text_cstgroup_name;
    public static String Cst_Text_Enter_name_for_cstgroup;
    public static String Cst_Text_Enter_name_for_cstprofile;
    public static String Cst_Text_Patientenauswahl;
    public static String Cst_Text_Patientenauswahl_kopieren;
    public static String Cst_Text_Gruppenauswahl;
    public static String Cst_Text_Gruppenauswahl_label;
    public static String Cst_Text_Gruppen_details_editieren;
    public static String Cst_Text_Profil_details_editieren;
    public static String Cst_Text_Laboritemauswahl;
    public static String Cst_Text_Laboritemauswahl_label;
    public static String Cst_Text_Bitte_Profil_auswaehlen;
    public static String Cst_Text_Interpretation_Mitochondrienlabor;
    public static String Cst_Text_fuer;
    public static String Cst_Text_Blutdruck;
    public static String Cst_Text_Proimmun;
    public static String Cst_Text_IggAntikoerper;
    public static String Cst_Text_von;
    public static String Cst_Text_tooltip_befund_separator;
    public static String Cst_Text_tooltip_befundauswahl;
    public static String Cst_Text_am;
    public static String Cst_Text_getesteten;
    public static String Cst_Text_Lebensmittel;
    public static String Cst_Text_Fixmedikation;
    public static String Cst_Text_Darmuntersuchungen;
    public static String Cst_Text_Therapievorschlag;
    public static String Cst_Text_Gastroduodenoskopie;
    public static String Cst_Text_Magenspiegelung;
    public static String Cst_Text_Save_as_png;
    public static String Cst_Text_Save_as_pdf;
    public static String Cst_Text_Makrobefund;
    public static String Cst_Text_normal_pathologisch;
    public static String Cst_Text_Histologie;
    public static String Cst_Text_Coloskopie_Dickdarmspiegelung;
    public static String Cst_Text_Copy_of;
    public static String Cst_Text_Auswertungstyp_effektiv;
    public static String Cst_Text_Auswertungstyp_minimax;
    public static String Cst_Text_erste_Periode;
    public static String Cst_Text_zweite_Periode;
    public static String Cst_Text_dritte_Periode;
    public static String Cst_Text_error_startdate_enddate;
    public static String Cst_Text_error_enddate_startdate;
    public static String Cst_Text_difference;
    public static String Cst_Text_confirm_delete_profile;
    public static String Cst_Text_confirm_delete_group;
    public static String Cst_Text_cstprofile_exists;
    public static String Cst_Text_plausibilty_check;
    public static String Cst_Text_no_abstract_available;
    public static String Cst_Text_anzeigen_ab;
    public static String Cst_Text_startdatum;
    public static String Cst_Text_ausrichtung;
    public static String Cst_Text_profil_hat_keine_gruppen;
    public static String Cst_Text_profil_unvollstaendig;
    public static String Cst_Text_a4hoch;
    public static String Cst_Text_a4quer;
    public static String Cst_Text_keine_vorwerte;
    public static String Cst_Text_cst_documents;
    public static String Cst_Text_cst_documents_tooltip;
    public static String Cst_Text_cst_documents_tooltip_omnivore;
    public static String ProImmunComposite_von_anzahl_lebensmitteln;
    public static String ProImmunComposite_anzahl_lebensmittel;
    public static String Cst_Preference_Einstellungen;
    public static String HilfeComposite_hilfe_text;
    public static String HilfeComposite_hilfe_text_cstgroups;
    public static String HilfeComposite_hilfe_text_plugin;
    public static String HilfeComposite_hilfe_text_cstprofiles;
    public static String HilfeComposite_hilfe_titel;
    public static String HilfeComposite_hilfe_titel_cstgroups;
    public static String HilfeComposite_hilfe_titel_cstprofiles;
    public static String HilfeComposite_hilfe_text_cstdocuments;
    public static String HilfeComposite_hilfe_titel_cstdocuments;
    public static String CstCategoryDialog_lblNewLabel_text;
    //public static String AnzeigeOptionsComposite_grpAusrichtung_text;
    //public static String AnzeigeOptionsComposite_btnAQuer_text;
    //public static String AnzeigeOptionsComposite_btnNewButton_text;
    //public static String AnzeigeOptionsComposite_btnRadioButton_text;
    //public static String AnzeigeOptionsComposite_btnRadioButton_1_text;
    public static String TemplateComposite_is_template;
    public static String TemplateComposite_template_settings;
    public static String TemplateComposite_template_title;
    public static String TemplateComposite_OutputHeader;
    public static String RemindersComposite_title_reminder;
    public static String RemindersComposite_btnNewButton_text;
    public static String RemindersComposite_btnNewButton_text_1;
    public static String RemindersComposite_lblHeart_text;
    public static String RemindersComposite_lblCheckingForActions_text;
    public static String AnzeigeOptionsComposite_lblWoSindMeine_text;
    public static String CstResultEffektiv_hinweis_einmal_im_leben;
    public static String CstResultEffektiv_hinweis_keine_werte;
    public static String CstResultEffektiv_resultat_nie_ermittelt;
    public static String CstResultEffktiv_hinweis_immer_anzeigen;
    //public static String TemplateComposite_text_text;

    static {
	// load message values from bundle file
	NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
