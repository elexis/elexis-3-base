package at.medevit.elexis.medicationlist.ui.composites;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "at.medevit.elexis.medicationlist.ui.composites.messages"; //$NON-NLS-1$
	public static String TherapieplanComposite_btnIsFixmedication_text;
	public static String TherapieplanComposite_tblclmnArticle_text;
	public static String TherapieplanComposite_txtArticle_message;
	public static String TherapieplanComposite_btnPRNMedication_toolTipText;
	public static String TherapieplanComposite_tblclmnDosage_text;
	public static String TherapieplanComposite_tblclmnDosage_toolTipText;
	public static String MedicationComposite_txtComment_message;
	public static String MedicationComposite_txtIntakeOrder_message;
	public static String MedicationComposite_lblNewLabel_text;
	public static String MedicationComposite_lblNewLabel_text_1;
	public static String MedicationComposite_btnNewButton_text;
	public static String MedicationComposite_btnPRNMedication_text;
	public static String MovePrescriptionPositionInTableUpAction_Label;
	public static String MovePrescriptionPositionInTableDownAction_Label;
	public static String MedicationComposite_btnCheckButton_text;
	public static String MedicationComposite_btnShowHistory_toolTipText;
	public static String MedicationComposite_btnNewButton_text_1;
	public static String MedicationComposite_btnIsFixmedication_toolTipText;
	////////////////////////////////////////////////////////////////////////////
	//
	// Constructor
	//
	////////////////////////////////////////////////////////////////////////////
	private Messages() {
		// do not instantiate
	}
	////////////////////////////////////////////////////////////////////////////
	//
	// Class initialization
	//
	////////////////////////////////////////////////////////////////////////////
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
