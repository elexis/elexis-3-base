package at.medevit.elexis.gdt.defaultfilecp.ui;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.elexis.gdt.constants.GDTConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class GDTPreferencePageFileTransfer extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	public static final String CFG_GDT_FILETRANSFER_DIRECTORY =
		"GDT/defaultfilecp/fileTransferDirectory";
	public static final String CFG_GDT_FILETRANSFER_USED_TYPE =
		"GDT/defaultfilecp/fileTransferUsedType";
	public static final String CFG_GDT_FILETRANSFER_LONG_ID_RECEIVER =
		"GDT/defaultfilecp/longIDReceiver";
	public static final String CFG_GDT_FILETRANSFER_SHORT_ID_RECEIVER =
		"GDT/defaultfilecp/longIDReceiver";
	public static final String CFG_GDT_FILETRANSFER_EXECUTABLE = "GDT/defaultfilecp/executable";
	
	public static String[][] comboCharsetSelektor = new String[][] {
		{
			"7Bit", GDTConstants.ZEICHENSATZ_7BIT_CHARSET_STRING
		},
		{
			"IBM (Standard) CP 437", GDTConstants.ZEICHENSATZ_IBM_CP_437_CHARSET_STRING
		},
		{
			"ISO8859-1 (ANSI) CP 1252",
			GDTConstants.ZEICHENSATZ_ISO8859_1_ANSI_CP_1252_CHARSET_STRING
		}
	};
	
	private IPreferenceStore prefStore;
	private DirectoryFieldEditor exchangeDir;
	private RadioGroupFieldEditor fileType;
	
	/**
	 * Create the preference page.
	 */
	public GDTPreferencePageFileTransfer(){
		super(FieldEditorPreferencePage.GRID);
		setTitle("Datei-Kommunikation");
	}
	
	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors(){
		addField(new StringFieldEditor(CFG_GDT_FILETRANSFER_LONG_ID_RECEIVER,
			"Lange GDT ID Receiver", getFieldEditorParent()));
		addField(new StringFieldEditor(CFG_GDT_FILETRANSFER_SHORT_ID_RECEIVER,
			"Kurze GDT ID Receiver", getFieldEditorParent()));
		
		exchangeDir =
			new DirectoryFieldEditor(CFG_GDT_FILETRANSFER_DIRECTORY,
				"Standard-Austausch-Verzeichnis", getFieldEditorParent());
		addField(exchangeDir);
		
		fileType =
			new RadioGroupFieldEditor(CFG_GDT_FILETRANSFER_USED_TYPE, "Zu verwendender Dateityp",
				1, new String[][] {
					{
						"fest", GDTConstants.GDT_FILETRANSFER_TYP_FEST
					}, {
						"hochzählend", GDTConstants.GDT_FILETRANSFER_TYPE_HOCHZAEHLEND
					}
				}, getFieldEditorParent(), false);
		addField(fileType);
		addField(new FileFieldEditor(CFG_GDT_FILETRANSFER_EXECUTABLE, "Verarbeitungsprogramm",
			getFieldEditorParent()));
	}
	
	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench){
		prefStore = new SettingsPreferenceStore(CoreHub.localCfg);
		setPreferenceStore(prefStore);
	}
	
}
