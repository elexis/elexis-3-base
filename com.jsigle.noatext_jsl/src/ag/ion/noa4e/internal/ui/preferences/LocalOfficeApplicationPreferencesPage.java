/****************************************************************************
 *                                                                          *
 * NOAText_jsl based upon NOA (Nice Office Access) / noa-libre              *
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU General Public License Version 2.1                      *
 *                                                                          * 
 * GNU General Public License Version 2.1                                   *
 * ======================================================================== *
 * Portions Copyright 2012 by Joerg Sigle                                   *
 * Copyright 2003-2006 by IOn AG                                            *
 *                                                                          *
 * Portions Copyright 2007 by Gerry Weirich (Only for a different branch    *
 *  producing his NOAText 1.4.1, not directly used to generate this file.)  *
 *                                                                          *
 * This program is free software: you can redistribute it and/or modify     *
 * it under the terms of the GNU General Public License as published by     *
 * the Free Software Foundation, either version 2.1 of the License.         *
 *                                                                          *
 *  This program is distributed in the hope that it will be useful,         *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 *  GNU General Public License for more details.                            *
 *                                                                          *
 *  You should have received a copy of the GNU General Public License       *
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.   *
 *                                                                          *
 * Contact us:                                                              *
 *  http://www.jsigle.com                                                   *
 *  http://www.ql-recorder.com                                              *
 *  http://code.google.com/p/noa-libre                                      *
 *  http://www.elexis.ch                                                    *
 *  http://www.ion.ag                                                       *
 *  http://ubion.ion.ag                                                     *
 *  info@ion.ag                                                             *
 *                                                                          *
 * Please note: Previously, versions of the NOA library provided by         *
 * www.ion.ag and the noa-libre project carried a licensing remark          *
 * that made them available under the LGPL. However, they include portions  *
 * obtained from the YaBS project, licensed under GPL. Consequently, NOA    *
 * should have been licensed under the GPL, not LGPL, given that no special *
 * permission of the authors of YaBS for LGPL licensing had been obtained.  *
 * To point out the possible problem, I'm providing the files where I added *
 * contributions under the GPL for now. This move is always allowed for     *
 * LPGL licensed material. 20120623js                                       * 
 *                                                                          *
 ****************************************************************************/
 
/****************************************************************************
 * To Do:
 * Possibly, this preference page should get a new and unique PAGE_ID string.
 * Possibly, this version of the library should get a new revision number, currently used is 11685.
 * Clarificytion of LGPL vs. GPL vs. Eclipse licensing issues.
 ****************************************************************************/

/*
 * Last changes made by $Author: jsigle $, $Date: 2012-06-23 14:38:00 +0100 (Su, 23 Jun 2012) $
 */
package ag.ion.noa4e.internal.ui.preferences;

import java.util.Arrays;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.jsigle.noa.PreferenceConstants;

import ag.ion.bion.officelayer.application.IApplicationAssistant;
import ag.ion.bion.officelayer.application.IApplicationProperties;
import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.workbench.office.editor.core.EditorCorePlugin;
import ag.ion.bion.workbench.office.editor.core.EditorCorePlugin;
import ag.ion.noa4e.ui.NOAUIPlugin;
import ag.ion.noa4e.ui.wizards.application.LocalApplicationWizard;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

/**
 * Preferences page for local OpenOffice.org application - adopted for Elexis and NOAText_jsl.
 * 
 * @author Joerg Sigle
 * @author Andreas Br�ker
 * @author Markus Kr�ger
 * @version $Revision: 11685 $
 */
public class LocalOfficeApplicationPreferencesPage extends PreferencePage implements
    IWorkbenchPreferencePage {

  /** ID of the page. */
  //20120626js
  //original setting: may require ag.ion.noa4e either in eclipse, or exported as package ag.ion.noa4e (noa or noa-libre exported as such)
  //will apparently not run, when all of these are missing and ag.ion.noa4e is only below NOAText_jsl
  //Cave - an dieser Stelle steht auch im "Original": PreferencePage ohne s!
  //Aber ich vermute, das ist nicht sooo wichtig. Da könnte auch jeder andere Unique String stehen.
  public static final String PAGE_ID = "ag.ion.noa4e.ui.preferences.LocalOfficeApplicationPreferencePage"; //$NON-NLS-1$
  //public static final String PAGE_ID = "ag.ion.noa4e.ui.preferences.LocalOfficeApplicationPreferencesPage_jsl"; //$NON-NLS-1$
  //public static final String PAGE_ID = "com.jsigle.noatext_jsl.ag.ion.noa4e.ui.preferences.LocalOfficeApplicationPreferencesPage"; //$NON-NLS-1$
  
  /**
   * @author Joerg Sigle
   * 
   * Adopted for Elexis by Joerg Sigle 02/2012, adding the following line.
   * Changes required because of different preference store layout in Elexis.
   * There are corresponding changes in:
   * LocalOfficeApplicationsPreferencesPage.java
   *   PREFS_PREVENT_TERMINATION
   *   initPreferenceValues()
   *   performOk()
   * NOAUIPlugin.java							
   *   PREFERENCE_OFFICE_HOME
   *   PREFERENCE_PREVENT_TERMINATION				
   *   internalStartApplication().
   */
  
  //FIXME: 20140421js: Da erscheint oft beim Klick auf Apply oder OK der Hinweis: "Ihre Änderungen werden erst nach einem Neustart der Workbench wirksam. Möchten Sie die Workbench neu starten?" - Erstens ist das wohl Quatsch, es erscheint z.B. auch nach Änderungen der configurable Filename settings, jedenfalls gerade beim Testen mit offenem Dokument in der Briefe view. In Omnivore_js passiert das nicht. Ausserdem passiert dann nichts weiter, und die Änderungen werden trotzdem wirklsam
  
  //FIXME: 20130420js: Warning: Introduced this variable to be used in the code adopted from omnivore_js. For now, it's only used in newly introduced code adopted from omnivore_js.
  //FIXME: 20130420js: Warning: IF YOU WANT TO REFACTOR the following constants also containing openoffice/, replacing that portion by PREFERENCE_BRANCH, then definitely make sure you do the same wherever these constants are used in any other places throughout the plugin or elexis.
  public static final String PREFERENCE_BRANCH="openoffice/";
  
  public static final String PREFS_PREVENT_TERMINATION	= "openoffice/preventTermination";

  public static final String PREFS_TIMEOUT_BOOTSTRAP_CONNECT  = "openoffice/timeoutBootstrapConnect";  //20130310js timeout made configurable for the loop found in bootstrap*.jar that would originally stop a connection attempt after 500 tries
  public static final String PREFS_TIMEOUT_THREADED_WATCHDOG  = "openoffice/timeoutThreadedWatchdog";  //20130310js timeout made configurable for the threaded watchdog timer added in 1.4.x by js
  
  private Text               textHome                   = null;
  private Button          buttonPreventTermination   = null;
  
  private Text               textTimeoutBootstrapConnect = null;  //20130310js timeout made configurable for the loop found in bootstrap*.jar that would originally stop a connection attempt after 500 tries
  																  //The setting is used in BootstrapConnector.java; the effective local variable name is: timeoutBootstrapConnect
  private Text               textTimeoutThreadedWatchdog = null;  //20130310js timeout made configurable for the threaded watchdog timer added in 1.4.x by js
  																  //The setting is used in LoadDocumentOperation.java; the effective local variable name is: cyclesWatchdogMaximumAllowedHalfSecs

  private static final Integer	 timeoutBootstrapConnectMin = 1;		//I'm unsure but a setting of 0 might actually result in no connection attempt being made at all, and variables left undefined (for loop never executed).
  private static final Integer	 timeoutBootstrapConnectDefault = 80;	//The original setting in bootstrapconnector would be 600 attempts, or 300 sec = 5 min, but apparently noone would wait that long. Setting this to 0 has no meaning different from 1.
  private static final Integer	 timeoutBootstrapConnectMax = 600;
  
  private static final Integer	 timeoutThreadedWatchdogMin = 0;		//0 means the watchdog will be disabled. Extra code is there to handle this.
  private static final Integer	 timeoutThreadedWatchdogDefault = 60;	//The original setting in NOAText_jsl was 60 cycles, or 30 sec. It is reasonable to have this below the bootstrap connecteor timeout, or disabled, i.e. = 0.
  private static final Integer	 timeoutThreadedWatchdogMax = 300;  																  

    //20130420js: noatext_jsl 1.4.9 -> 1.4.10: Adopt configurability of meaningful temporary filename from omnivore_js 1.4.4:
    //Offer Less elements, but basically follow the same approach. So a lot of code and comments copied over and adopted.
    //Note that especially some preference store keys look the same - however, they are used in a different branch of the preference store.
    //WARNING: Note that because the constants are public, identical names might lead to confusion.
    //However, the two preference units are - hopefully - not used much outside the respective plugin code.
    //We could always specify the complete path to the file in case of ambiguity. So I keep the constant names mostly unchanged for now.
    //Some filename elements offered in omnivore_js are NOT easily available in NOAText_jsl,
    //because only the module *calling* NOAText_jsl might have reliable and directly accessible knowledge of the respective data.
  
	//20130411js: Make the temporary filename configurable
	//which is generated to extract the document from the database for viewing.
	//Thereby, simplify tasks like adding a document to an e-mail.
	//For most elements noted below, we can set the maximum number of digits
	//to be used (taken from the source from left); which character to add thereafter;
	//and whether to fill leading digits by a given character.
	//This makes a large number of options, so I construct the required preference store keys from arrays.
	//Note: The DocHandle.getTitle() javadoc says that a document title in omnivore may contain 80 chars.
	//To enable users to copy that in full, I allow for a max of 80 chars to be specified as num_digits for *any* element.
	//Using all elements to that extent will return filename that's vastly too long, but that will probably be handled elsewhere.
  
   //FIXME: having nNOAText_jslPREF_cotf_element_digits_max in addition to nOmnivore_jsPREF_cotf_element_digits_max may be seen as redundant.
  //Please note: content of the following constants is different from content of constants with the same name in Omnivore_js!
  //FIXME: We might switch  to an approach for temp filename generation more like the one in Omnivore_js, including GUID and random portions.
	public static final Integer nNOAText_jslPREF_cotf_element_digits_max=80;
	public static final String PREFERENCE_COTF="cotf_";
	
	//Please NOTE: the Strings "constant" and "num_digit" are hard coded in the code below.
	public static final String[] PREFERENCE_cotf_elements={"constant1","PID", "fn", "gn", "dob"};
	public static final String[] PREFERENCE_cotf_parameters={"fill_leading_char", "num_digits", "add_trailing_char"};
	//The following unwanted characters, and all below codePoint=32  will be cleaned in advance.
	//Please see the getNOAText_jslTemp_Filename_Element for details.
	//FIXME: having cotf_unwanted_chars here and in omnivore_js preferences may be seen as redundant.
	//NOTE: here, it's public, because temp filename creation happens more over there in NOAText.java, than over here. In contrast to how omnivore_js is layed out. 
	public static final String cotf_unwanted_chars="\\/:*?()+,;\"'´`"; 
	//Dank Eclipse's mglw. etwas übermässiger "Optimierung" werden externalisierte Strings nun als Felder von Messges angesprochen -
	//und nicht mehr wie zuvor über einen als String übergebenen key. Insofern muss ich wohl zu den obigen Arrays korrespondierende Arrays
	//vorab erstellen, welche die jeweils zugehörigen Strings aus omnivore_js.Messages dann in eine definierte Reihenfolge bringen,
	//in der ich sie unten auch wieder gerne erhalten würde. Einfach per Programm at runtime die keys generieren scheint nicht so leicht zu gehen.
	public static final String[] PREFERENCE_cotf_elements_messages={
		Messages.LocalOfficeApplicationPrefrencesPage_cotf_constant1, 
		Messages.LocalOfficeApplicationPrefrencesPage_cotf_pid, 
		Messages.LocalOfficeApplicationPrefrencesPage_cotf_fn, 
		Messages.LocalOfficeApplicationPrefrencesPage_cotf_gn, 
		Messages.LocalOfficeApplicationPrefrencesPage_cotf_dob};
	public static final String[] PREFERENCE_cotf_parameters_messages={
		Messages.LocalOfficeApplicationPrefrencesPage_cotf_fill_lead_char, 
		Messages.LocalOfficeApplicationPrefrencesPage_cotf_num_digits, 
		Messages.LocalOfficeApplicationPrefrencesPage_cotf_add_trail_char};
	//In contrast to omnivore_js...PreferencePage.java, over here in NOAText_jsl...LocalOfficeApplicationPreferencesPage.java, some place to hold editable text elements must be provided.
	//TODO: Instead of initializing this to the required field size manually, we might also do the same automatically in the for loop below, observing the String array sizes from above.
	private Text[][] textCotfOption = { {null,null,null},{null,null,null},{null,null,null},{null,null,null},{null,null,null} };
    
  
  private Table              tableApplicationProperties = null;

  //----------------------------------------------------------------------------
  /**
   * Initializes this preference page for the given workbench.
   * 
   * @param workbench workbnech to be used
   * 
   * @author Andreas Br�ker
   */
  public void init(IWorkbench workbench) {
	  System.out.println("LocalOfficeApplicationPreferencesPage: init");
		  setDescription(Messages.LocalOfficeApplicationPreferencesPage_description_configure_application);
  }

  //----------------------------------------------------------------------------
  /**
   * Creates and returns the SWT control for the customized body of this preference 
   * page under the given parent composite. 
   * 
   * @param parent the parent composite
   * 
   * @return constructed control
   * 
   * @author Andreas Br�ker
   * @author Markus Kr�ger
   */
  protected Control createContents(Composite parent) {
	System.out.println("LocalOfficeApplicationPreferencesPage: createContents");
	FormToolkit formToolkit = NOAUIPlugin.getFormToolkit();
    Composite composite = new Composite(parent, SWT.NULL);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    composite.setLayout(gridLayout);
  
    Label labelHome = formToolkit.createLabel(composite,
        Messages.LocalOfficeApplicationPreferencesPage_label_application_home);
    labelHome.setBackground(composite.getBackground());

    textHome = formToolkit.createText(composite, ""); //$NON-NLS-1$
    textHome.setEditable(false);
    textHome.setFont(composite.getFont());
    GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
    textHome.setLayoutData(gridData);

    final Link linkDefine = new Link(composite, SWT.NONE);
    linkDefine.setText("<a>" + Messages.LocalOfficeApplicationPreferencesPage_link_define_text + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    linkDefine.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent selectionEvent) {
    	System.out.println("LocalOfficeApplicationPreferencesPage: createContents: widgetSelected: 1 start");
    	LocalApplicationWizard localApplicationWizard = new LocalApplicationWizard();
    	System.out.println("LocalOfficeApplicationPreferencesPage: createContents: widgetSelected: 2");
    	String oldHome = textHome.getText();
    	System.out.println("LocalOfficeApplicationPreferencesPage: createContents: widgetSelected: 3");
    	if (oldHome.length() != 0)
          localApplicationWizard.setHomePath(oldHome);
    	System.out.println("LocalOfficeApplicationPreferencesPage: createContents: widgetSelected: 4");
    	WizardDialog wizardDialog = new WizardDialog(linkDefine.getShell(), localApplicationWizard);
    	System.out.println("LocalOfficeApplicationPreferencesPage: createContents: widgetSelected: 5");
    	if (wizardDialog.open() == Window.OK) {
    		System.out.println("LocalOfficeApplicationPreferencesPage: createContents: widgetSelected: 6");
        	String home = localApplicationWizard.getSelectedHomePath();
        	System.out.println("LocalOfficeApplicationPreferencesPage: createContents: widgetSelected: 7");
        	if (home != null)
        		textHome.setText(home);
          initApplicationProperties(tableApplicationProperties);
          System.out.println("LocalOfficeApplicationPreferencesPage: createContents: widgetSelected: 8");
      	}
    	System.out.println("LocalOfficeApplicationPreferencesPage: createContents: widgetSelected: 9 end");
      }
    });

    Label labelNull = formToolkit.createLabel(composite, ""); //$NON-NLS-1$
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    labelNull.setLayoutData(gridData);

    Label labelProperties = formToolkit.createLabel(composite,
        Messages.LocalOfficeApplicationPreferencesPage_label_application_properties_text);
    labelProperties.setBackground(composite.getBackground());
    labelProperties.setFont(JFaceResources.getFontRegistry().getBold(labelProperties.getFont().toString()));
    gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
    gridData.horizontalSpan = 3;
    labelProperties.setLayoutData(gridData);

    tableApplicationProperties = formToolkit.createTable(composite, SWT.READ_ONLY);
    gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    gridData.horizontalSpan = 3;
    int tableWidth = (int) (tableApplicationProperties.getDisplay().getClientArea().width * 0.3);
    gridData.widthHint = tableWidth;
    tableApplicationProperties.setLayoutData(gridData);

    TableLayout tableLayout = new TableLayout();
    tableApplicationProperties.setLayout(tableLayout);

    TableColumn columnProduct = new TableColumn(tableApplicationProperties, SWT.NONE);
    columnProduct.setText(Messages.LocalOfficeApplicationPreferencesPage_column_name_text);
    int columnProductWidth = (int) (tableWidth * 0.4);
    columnProduct.setWidth(columnProductWidth);

    TableColumn columnHome = new TableColumn(tableApplicationProperties, SWT.NONE);
    columnHome.setText(Messages.LocalOfficeApplicationPreferencesPage_column_value_text);
    columnHome.setWidth(tableWidth - columnProductWidth);

    tableApplicationProperties.setLinesVisible(true);
    tableApplicationProperties.setHeaderVisible(true);

    buttonPreventTermination = formToolkit.createButton(composite,
        Messages.LocalOfficeApplicationPreferencesPage_prevent_termination_lable,
        SWT.CHECK);
    buttonPreventTermination.setBackground(composite.getBackground());
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    buttonPreventTermination.setLayoutData(gridData);
    
    //20130310js: Adding new text elements to specify timeout values begin
    //I tried to use an initial value for the text to get a certain minimum width of the input field.
    //but that hasn't worked out at all.
    //The text that can actually appear is clamped to numeric in the range of 0..500, or 0..60, respectively, by other code.
    
    Label labelTimeoutBootstrapConnect = formToolkit.createLabel(composite,
            Messages.LocalOfficeApplicationPreferencesPage_label_timeout_bootstrap_connect);
    labelTimeoutBootstrapConnect.setBackground(composite.getBackground());

    textTimeoutBootstrapConnect = formToolkit.createText(composite, "     "); //$NON-NLS-1$
    textTimeoutBootstrapConnect.setEditable(true);
    textTimeoutBootstrapConnect.setFont(composite.getFont());
    //GridData gridData = new GridData(SWT.LEFT, SWT.NONE, false, false);
    gridData = new GridData(SWT.LEFT, SWT.NONE, false, false);
    gridData.horizontalSpan = 2;
    textTimeoutBootstrapConnect.setLayoutData(gridData);
    
    Label labelTimeoutThreadedWatchdog = formToolkit.createLabel(composite,
            Messages.LocalOfficeApplicationPreferencesPage_label_timeout_threaded_watchdog);
    labelTimeoutThreadedWatchdog.setBackground(composite.getBackground());

    textTimeoutThreadedWatchdog = formToolkit.createText(composite, "     "); //$NON-NLS-1$
    textTimeoutThreadedWatchdog.setEditable(true);
    textTimeoutThreadedWatchdog.setFont(composite.getFont());
    gridData = new GridData(SWT.LEFT, SWT.NONE, false, false);
    gridData.horizontalSpan = 2;
    textTimeoutThreadedWatchdog.setLayoutData(gridData);
    //20130310js: Adding new text elements to specify timeout values end  
    
    
    
    
    //20130420js: noatext_jsl 1.4.9 -> 1.4.10: Adopt configurability of meaningful temporary filename from omnivore_js 1.4.4 begin
    //The following code was adopted in a simplified way from Omnivore_js...PreferencePage.java
    //Detailed coments and temporarily tried out code was not copied over here.
    //Note that elements of the preferences dialog are apparently constructed at a different level of abstraction here.
    
	Integer nCotfRules=PREFERENCE_cotf_elements.length;

	Group gCotfRules = new Group(parent, SWT.NONE);
		
	GridLayout gCotfRulesGridLayout = new GridLayout();
	gCotfRulesGridLayout.numColumns=nCotfRules;		//at least this one is finally honoured...
	gCotfRules.setLayout(gCotfRulesGridLayout);
	
	GridData gCotfRulesGridLayoutData = new GridData();
	gCotfRulesGridLayoutData.grabExcessHorizontalSpace = true;
	gCotfRulesGridLayoutData.horizontalAlignment=GridData.FILL;
	gCotfRules.setLayoutData(gCotfRulesGridLayoutData);
	
	gCotfRules.setText(Messages.LocalOfficeApplicationPrefrencesPage_construction_of_temporary_filename);
				
	for (int i=0;i<nCotfRules;i++) {

		Group gCotfRule = new Group(gCotfRules, SWT.NONE);
		
		//gCotfRule.setLayoutData(SWTHelper.getFillGridData(2,false,2,false));	//This would probably make groups-within-group completely disappear.
		
		gCotfRule.setLayout(new FillLayout());
		GridLayout gCotfRuleGridLayout = new GridLayout();
		gCotfRuleGridLayout.numColumns=nCotfRules;					
		gCotfRule.setLayout(gCotfRuleGridLayout);
		
		GridData gCotfRuleGridLayoutData = new GridData();
		gCotfRuleGridLayoutData.grabExcessHorizontalSpace = true;	
		gCotfRuleGridLayoutData.horizontalAlignment=GridData.FILL;
		//Sadly, even if I get the constant element to fill 3 lines - its heading will be at the top, and only its input field in the middle of the vertical space. So I don't do that.
		//gCotfRuleGridLayoutData.grabExcessVerticalSpace = true;			//so that the constant element with only one line occupies the same space as a fully configurable element
		//gCotfRuleGridLayoutData.verticalAlignment=GridData.FILL;			//so that the constant element with only one line occupies the same space as a fully configurable element
		gCotfRule.setLayoutData(gCotfRuleGridLayoutData);
		
		//System.out.println("Messages.LocalOfficeApplicationPrefrencesPage_cotf_"+PREFERENCE_cotf_elements[i]);
		
		gCotfRule.setText(PREFERENCE_cotf_elements_messages[i]);	
		GridLayout gridDataLayout = new GridLayout();
		gridDataLayout.numColumns=1;
		gCotfRule.setLayout(gridDataLayout);
		
		if (PREFERENCE_cotf_elements[i].contains("constant")) {
			//That's the way elements are added in omnivore_js....PreferencePage.java:
			//addField(new StringFieldEditor("","",10,gCotfRule));
			//addField(new StringFieldEditor(PREFERENCE_BRANCH+PREFERENCE_COTF+PREFERENCE_cotf_elements[i]+"_"+PREFERENCE_cotf_parameters[1],PREFERENCE_cotf_elements_messages[i],10,gCotfRule));
			//addField(new StringFieldEditor("","",10,gCotfRule));
			//But addField() is not known here. Instead, that's the way elements are added in LocalOfficeApplicationPreferencesPage.java - Sorry for the highly redundant ad-hoc code:
			//After all, this would enable me to avoid the two non-used text input fields for the constant element, by placing 2x2 labels containing nothing or just one space :-)
			//TODO: Moreover, I reviewed the code to obtain the desired output with fewer lines. Should be done above as well, and in omnivore_js as well.

			/*
			Label labelCotfRule = formToolkit.createLabel(gCotfRule," ");
		    labelCotfRule.setBackground(gCotfRule.getBackground());
			
		    textCotfOption[i][0] = formToolkit.createText(gCotfRule, ""); //$NON-NLS-1$
		    
		    textCotfOption[i][0].setEditable(true);
		    textCotfOption[i][0].setFont(gCotfRule.getFont());
		    
		    gridData = new GridData(SWT.LEFT, SWT.NONE, false, false);
		    gridData.horizontalSpan = 1;
		    textCotfOption[i][0].setLayoutData(gridData);
		    */
		    
		    Label labelCotfRule = formToolkit.createLabel(gCotfRule,PREFERENCE_cotf_parameters_messages[1]);
		    labelCotfRule.setBackground(gCotfRule.getBackground());

		    textCotfOption[i][1] = formToolkit.createText(gCotfRule, ""); //$NON-NLS-1$
		    textCotfOption[i][1].setEditable(true);
		    
		    gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
		    gridData.horizontalSpan = 1;
		    textCotfOption[i][1].setLayoutData(gridData);
		    
		    /*
		    labelCotfRule = formToolkit.createLabel(gCotfRule,"n.a.");
		    labelCotfRule.setBackground(gCotfRule.getBackground());

		    textCotfOption[i][2] = formToolkit.createText(gCotfRule, ""); //$NON-NLS-1$
		    textCotfOption[i][2].setEditable(true);
		    textCotfOption[i][2].setFont(gCotfRule.getFont());
		    
		    textCotfOption[i][2].setLayoutData(gridData);
		    */
		}
		else {
			//addField(new StringFieldEditor(PREFERENCE_BRANCH+PREFERENCE_COTF+PREFERENCE_cotf_elements[i]+"_"+PREFERENCE_cotf_parameters[0],PREFERENCE_cotf_parameters_messages[0],10,gCotfRule));
			//addField(new StringFieldEditor(PREFERENCE_BRANCH+PREFERENCE_COTF+PREFERENCE_cotf_elements[i]+"_"+PREFERENCE_cotf_parameters[1],PREFERENCE_cotf_parameters_messages[1],10,gCotfRule));
			//addField(new StringFieldEditor(PREFERENCE_BRANCH+PREFERENCE_COTF+PREFERENCE_cotf_elements[i]+"_"+PREFERENCE_cotf_parameters[2],PREFERENCE_cotf_parameters_messages[2],10,gCotfRule));

			Label labelCotfRule = formToolkit.createLabel(gCotfRule,PREFERENCE_cotf_parameters_messages[0]);
		    labelCotfRule.setBackground(gCotfRule.getBackground());

		    textCotfOption[i][0] = formToolkit.createText(gCotfRule, ""); //$NON-NLS-1$
		    textCotfOption[i][0].setEditable(true);
		    
		    gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
		    gridData.horizontalSpan = 1;
		    textCotfOption[i][0].setLayoutData(gridData);
			
		    
		    labelCotfRule = formToolkit.createLabel(gCotfRule,PREFERENCE_cotf_parameters_messages[1]);
		    labelCotfRule.setBackground(gCotfRule.getBackground());

		    textCotfOption[i][1] = formToolkit.createText(gCotfRule, ""); //$NON-NLS-1$
		    textCotfOption[i][1].setEditable(true);
		    
		    textCotfOption[i][1].setLayoutData(gridData);
		    
		    
		    labelCotfRule = formToolkit.createLabel(gCotfRule,PREFERENCE_cotf_parameters_messages[2]);
		    labelCotfRule.setBackground(gCotfRule.getBackground());

		    textCotfOption[i][2] = formToolkit.createText(gCotfRule, ""); //$NON-NLS-1$
		    textCotfOption[i][2].setEditable(true);
		    
		    textCotfOption[i][2].setLayoutData(gridData);
		}
	}

    
    //20130420js: noatext_jsl 1.4.9 -> 1.4.10: Adopt configurability of meaningful temporary filename from omnivore_js 1.4.4 end

    formToolkit.paintBordersFor(composite);
    initPreferenceValues();
    initApplicationProperties(tableApplicationProperties);
    return composite;
  }

  //----------------------------------------------------------------------------
  /**
   * Notifies that the OK button of this page's container has been pressed. 
   * 
   * @return false to abort the container's OK processing and true to allow 
   * the OK to happen
   * 
   * @author Joerg Sigle
   * @author Gerry Weirich
   * @author Andreas Br�ker
   * @author Markus Kr�ger
   *
   * Adopted for Elexis by Joerg Sigle 02/2012, adding comments and monitoring output,
   * and reproducing the functionality of changes made by Gerry Weirich in 06/2007
   * for his NOAText plugin 1.4.1 to a file obtained from an older version of the ag.ion noa library.
   * 
   * Changes required because of different preference store layout in Elexis:
   * There are corresponding changes in:
   * LocalOfficeApplicationsPreferencesPage.java
   *   PREFS_PREVENT_TERMINATION
   *   initPreferenceValues()
   *   performOk()
   * NOAUIPlugin.java							
   *   PREFERENCE_OFFICE_HOME
   *   PREFERENCE_PREVENT_TERMINATION				
   *   internalStartApplication().
   */
  public boolean performOk() {
	System.out.println("LocalOfficeApplicationPreferencesPage: performOK() begin - Adopted to Elexis by GW/JS");
	System.out.println("LocalOfficeApplicationPreferencesPage: performOk(): allocating preferenceStore = new SettingsPreferenceStore(Hub.localCfg)");
	System.out.println("LocalOfficeApplicationPreferencesPage: performOk(): instead of using = NOAUIPlugin.getDefault().getPreferenceStore()");
	
	System.out.println("LocalOfficeApplicationPreferencesPage: performOk(): Transferring settings from configuration dialog into internal storage...");
	
	
	IPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.localCfg);
    preferenceStore.setValue(PREFS_PREVENT_TERMINATION, buttonPreventTermination.getSelection());


    //When we read the two timeout settings, use try/catch so that data that can't be interpreted as integer numbers doesn't cause any harm.
    //The trim() is needed before parseInt(), otherwise an leading space will cause failure. 
    //See corresponding code in initPreferenceValues() and performOk().
    System.out.println("LocalOfficeApplicationPreferencesPage: performOk(): ToDo: refactor to move repeatedly used constants into single places or even out of the code."); 
    
    Integer timeoutBootstrapConnect=timeoutBootstrapConnectDefault;     //Start by establishing a valid default setting
    try {
        timeoutBootstrapConnect=Integer.parseInt(textTimeoutBootstrapConnect.getText().trim()); //20130310js timeout made configurable for the loop found in bootstrap*.jar that would originally stop a connection attempt after 500 tries
    } catch (Throwable throwable) {
        //do not consume
    }
    if (timeoutBootstrapConnect<timeoutBootstrapConnectMin) {timeoutBootstrapConnect=timeoutBootstrapConnectMin;};
    if (timeoutBootstrapConnect>timeoutBootstrapConnectMax) {timeoutBootstrapConnect=timeoutBootstrapConnectMax;};
    preferenceStore.setValue(PREFS_TIMEOUT_BOOTSTRAP_CONNECT, timeoutBootstrapConnect.toString());
    //I also write back the possibly clamped value into the dialog immediately, cause this method might have been called by Apply rather than by OK:
    textTimeoutBootstrapConnect.setText(timeoutBootstrapConnect.toString());
                
    Integer timeoutThreadedWatchdog=timeoutThreadedWatchdogDefault;         //Start by establishing a valid default setting
    try {
        timeoutThreadedWatchdog=Integer.parseInt(textTimeoutThreadedWatchdog.getText().trim()); //20130310js timeout made configurable for the threaded watchdog timer added in 1.4.x by js
    } catch (Throwable throwable) {
        //do not consume
    }
    if (timeoutThreadedWatchdog<timeoutThreadedWatchdogMin) {timeoutThreadedWatchdog=timeoutThreadedWatchdogMin;};
    if (timeoutThreadedWatchdog>timeoutThreadedWatchdogMax) {timeoutThreadedWatchdog=timeoutThreadedWatchdogMax;};
    preferenceStore.setValue(PREFS_TIMEOUT_THREADED_WATCHDOG, timeoutThreadedWatchdog.toString());
    //I also write back the possibly clamped value into the dialog immediately, cause this method might have been called by Apply rather than by OK:
    textTimeoutThreadedWatchdog.setText(timeoutThreadedWatchdog.toString());
	
    
    ///20130420js: noatext_jsl 1.4.9 -> 1.4.10: Adopt configurability of meaningful temporary filename from omnivore_js 1.4.4 begin
	for (int i=0;i<PREFERENCE_cotf_elements.length;i++) {
		for (int j=0;j<PREFERENCE_cotf_parameters.length;j++) {
			//Specifically, textCotfOption[0][0] and [0][2] will NOT have been created for theconstant1 element. Therefore, don't try to set anything to its content!
			if (textCotfOption[i][j] != null) {
				//Intermediate string variable:
				//check whether it's a valid integer.
				//	if yes: clamp it to the range [1..nNOAText_jslPREF_cotf_element_digits_max] 
				//	If no: re-use what's already in the preferenceStore. Should that not be an integer either, then remove the content alltogether.
				//FIXME: Sadly, any auto produced changes are only visible when the dialog is re-opened the next time.
				//FIXME: The same checking and limiting mechanism could be used upon the initialization of the dialog content after it is created. 
				String s=textCotfOption[i][j].getText().trim();
				if (!PREFERENCE_cotf_elements[i].contains("constant") && (PREFERENCE_cotf_parameters[j].contains("num_digits"))) {
					try {
						Integer v1=Integer.parseInt(s);
						Integer v2=v1;
						if (v1>nNOAText_jslPREF_cotf_element_digits_max) {
							v2=nNOAText_jslPREF_cotf_element_digits_max;
						} else {
							if (v1<1) {
								v2=1;
							}
						if (v2!=v1) {
							s=v2.toString().trim();
						}
						}
					} catch (Throwable throwable) {
						s=getCotfOption(i,j);
						try {
							Integer v3=Integer.parseInt(s);
						} catch (Throwable throwable2) {
							s="";
						}
					}
					
				}
				System.out.println("LocalOfficeApplicationPreferencesPage: performOk(): About to setValue("
							+PREFERENCE_BRANCH+PREFERENCE_COTF+PREFERENCE_cotf_elements[i]+"_"+PREFERENCE_cotf_parameters[j]
							+", textCotfOption["+i+"]["+j+"].toString().trim()  ); which is <"+s+">");
				preferenceStore.setValue(PREFERENCE_BRANCH+PREFERENCE_COTF+PREFERENCE_cotf_elements[i]+"_"+PREFERENCE_cotf_parameters[j],s);
			}
		}
	}
    
	//IPreferenceStore preferenceStore = NOAUIPlugin.getDefault().getPreferenceStore();
    //preferenceStore.setValue(NOAUIPlugin.PREFERENCE_PREVENT_TERMINATION,
    //    buttonPreventTermination.getSelection());

    String oldPath = preferenceStore.getString(PreferenceConstants.P_OOBASEDIR);
    preferenceStore.setValue(PreferenceConstants.P_OOBASEDIR, textHome.getText());

    //String oldPath = preferenceStore.getString(NOAUIPlugin.PREFERENCE_OFFICE_HOME);
    //preferenceStore.setValue(NOAUIPlugin.PREFERENCE_OFFICE_HOME, textHome.getText());

    System.out.println("LocalOfficeApplicationPreferencesPage: performOk(): Please note: There is a reference to NOAUIPlugin.getDefault()...");
    System.out.println("LocalOfficeApplicationPreferencesPage: performOk(): still left in this code; I (js) don't know whether this might be null and hence not work.");
        
    super.performOk();
    if (oldPath.length() != 0 || !oldPath.equals(textHome.getText())) {
      if (EditorCorePlugin.getDefault().getManagedLocalOfficeApplication().isActive()) {
        if (MessageDialog.openQuestion(getShell(),
            Messages.LocalOfficeApplicationPreferencesPage_dialog_restart_workbench_title,
            Messages.LocalOfficeApplicationPreferencesPage_dialog_restart_workbench_message))
          NOAUIPlugin.getDefault().getWorkbench().restart();
      }
    }
    
    System.out.println("LocalOfficeApplicationPreferencesPage: performOk() return true");
    return true;
  }

  //----------------------------------------------------------------------------
  /**
   * Inits application properties.
   * 
   * @param table table to be used
   * 
   * @author Andreas Br�ker
   */
  private void initApplicationProperties(Table table) {
	  System.out.println("LocalOfficeApplicationPreferencesPage: initApplicationProperties begin");
	  try {
      TableItem[] tableItems = table.getItems();
      for (int i = 0, n = tableItems.length; i < n; i++) {
        tableItems[i].dispose();
      }

      IApplicationAssistant applicationAssistant = OfficeApplicationRuntime.getApplicationAssistant(EditorCorePlugin.getDefault().getLibrariesLocation());
      ILazyApplicationInfo applicationInfo = applicationAssistant.findLocalApplicationInfo(textHome.getText());
      if (applicationInfo != null) {
        IApplicationProperties applicationProperties = applicationInfo.getProperties();
        if (applicationProperties != null) {
          String[] names = applicationProperties.getPropertyNames();
          TreeSet treeSet = new TreeSet(Arrays.asList(names));
          names = (String[]) treeSet.toArray(new String[treeSet.size()]);
          for (int i = 0, n = names.length; i < n; i++) {
            String name = names[i];
            String value = applicationProperties.getPropertyValue(name);
            if (value != null && value.length() != 0) {
              TableItem tableItem = new TableItem(table, SWT.NONE);
              tableItem.setText(0, name);
              tableItem.setText(1, value);
            }
          }
        }
      }
    }
    catch (Throwable throwable) {
      //do not consume
    }
	System.out.println("LocalOfficeApplicationPreferencesPage: initApplicationProperties end");  
  }

  //----------------------------------------------------------------------------
  /**
   * Inits all preference values.
   * 
   * @author Joerg Sigle
   * @author Gerry Weirich
   * @author Andreas Br�ker
   * @author Markus Kr�ger
   *
   * Adopted for Elexis by Joerg Sigle 02/2012, adding comments and monitoring output,
   * and reproducing the functionality of changes made by Gerry Weirich in 06/2007
   * for his NOAText plugin 1.4.1 to a file obtained from an older version of the ag.ion noa library.
   * 
   * Changes required because of different preference store layout in Elexis.
   * There are corresponding changes in:
   * LocalOfficeApplicationsPreferencesPage.java
   *   PREFS_PREVENT_TERMINATION
   *   initPreferenceValues()
   *   performOk()
   * NOAUIPlugin.java			
   *   PREFERENCE_OFFICE_HOME
   *   PREFERENCE_PREVENT_TERMINATION				
   *   internalStartApplication().
   */
  private void initPreferenceValues() {
	System.out.println("LocalOfficeApplicationPreferencesPage: initPreferenceValues() begin - adopted for Elexis and NOAText_jsl by GW/JS");
	System.out.println("LocalOfficeApplicationPreferencesPage: initPreferenceValues(): allocating preferenceStore = new SettingsPreferenceStore(Hub.localCfg)");
	System.out.println("LocalOfficeApplicationPreferencesPage: initPreferenceValues(): instead of using = NOAUIPlugin.getDefault().getPreferenceStore()");
	
    System.out.println("LocalOfficeApplicationPreferencesPage: initPreferenceValues(): initializing dialog fields from internal storage or default values");

    IPreferenceStore preferenceStore=new SettingsPreferenceStore(CoreHub.localCfg);
	String officeHomePath=preferenceStore.getString(PreferenceConstants.P_OOBASEDIR);
	boolean preventTermination=preferenceStore.getBoolean(PREFS_PREVENT_TERMINATION);

	//IPreferenceStore preferenceStore = NOAUIPlugin.getDefault().getPreferenceStore();
    //String officeHomePath = preferenceStore.getString(NOAUIPlugin.PREFERENCE_OFFICE_HOME);
    //boolean preventTermination = preferenceStore.getBoolean(NOAUIPlugin.PREFERENCE_PREVENT_TERMINATION);

    textHome.setText(officeHomePath);
    buttonPreventTermination.setSelection(preventTermination);

    
    //When we read the two timeout settings, use try/catch so that data that can't be interpreted as integer numbers doesn't cause any harm.
    //The trim() is needed before parseInt(), otherwise an leading space will cause failure (though that is less probable in a string coming from internal storage).
    //See corresponding code in initPreferenceValues() and performOk().
    System.out.println("LocalOfficeApplicationPreferencesPage: initPreferenceValues(): ToDo: refactor to move repeatedly used constants into single places or even out of the code.");

    Integer timeoutBootstrapConnect=getTimeoutBootstrapConnect(preferenceStore);	//This also observes defaults and min/max values	
    textTimeoutBootstrapConnect.setText(timeoutBootstrapConnect.toString());
		
    Integer timeoutThreadedWatchdog=getTimeoutThreadedWatchdog(preferenceStore);	//This also observes defaults and min/max values
    textTimeoutThreadedWatchdog.setText(timeoutThreadedWatchdog.toString());
    	
    //20130420js: noatext_jsl 1.4.9 -> 1.4.10: Adopt configurability of meaningful temporary filename from omnivore_js 1.4.4 begin
	for (int i=0;i<PREFERENCE_cotf_elements.length;i++) {
		for (int j=0;j<PREFERENCE_cotf_parameters.length;j++) {
			//Intermediate string variable just to supply debugging output.
			String s=getCotfOption(preferenceStore,i,j);
			//Specifically, textCotfOption[0][0] and [0][2] will NOT have been created for theconstant1 element. Therefore, don't try to set them to anything!
			if (textCotfOption[i][j] != null) {
				System.out.println("LocalOfficeApplicationPreferencesPage: initPreferenceValues(): About to set textCotfOption["+i+"]["+j+"] to <"+s+">...");
				textCotfOption[i][j].setText(s);
			}
		}
	}
	
	System.out.println("LocalOfficeApplicationPreferencesPage: initPreferenceValues() end");  
  }

  //----------------------------------------------------------------------------
  /**
   * Returns information whether this preferences page is valid.
   * 
   * @return information whether this preferences page is valid
   * 
   * @author Andreas Br�ker
   */
  public boolean isValid() {
	  //FIXME: If I press "Restore Defaults", only this method is apparently run. I don't see any default content being put in the input fields.
	  System.out.println("LocalOfficeApplicationPreferencesPage: isValid - always just returns true");
	  return true;
  }
  
  //----------------------------------------------------------------------------
  /**
   * Returns currently configured timeout value from the preference store, observing default settings and min/max settings for that parameter
   * 
   * @param  Can be called with an already available preferenceStore. If none is passed, one will be temporarily instantiated on the fly.
   *
   * @return Number of cycles allowable for bootstrap connect
   * 
   * @author Joerg Sigle
   */
  public static Integer getTimeoutBootstrapConnect() {
	IPreferenceStore preferenceStore=new SettingsPreferenceStore(CoreHub.localCfg);
	return getTimeoutBootstrapConnect(preferenceStore);
  }	
  
  public static Integer getTimeoutBootstrapConnect(IPreferenceStore preferenceStore) {
	System.out.println("LocalOfficeApplicationPreferencesPage: getTimeoutBootstrapConnect begin");
		
	Integer timeoutBootstrapConnect=timeoutBootstrapConnectDefault;	//Start by establishing a valid default setting
	try {
		timeoutBootstrapConnect=Integer.parseInt(preferenceStore.getString(PREFS_TIMEOUT_BOOTSTRAP_CONNECT).trim());  //20130310js timeout made configurable for the loop found in bootstrap*.jar that would originally stop a connection attempt after 500 tries
	} catch (Throwable throwable) {
	    //do not consume
	}
	if (timeoutBootstrapConnect<timeoutBootstrapConnectMin) {timeoutBootstrapConnect=timeoutBootstrapConnectMin;};
	if (timeoutBootstrapConnect>timeoutBootstrapConnectMax) {timeoutBootstrapConnect=timeoutBootstrapConnectMax;};
	
	System.out.println("LocalOfficeApplicationPreferencesPage: getTimeoutBootstrapConnect returning "+timeoutBootstrapConnect);
	return timeoutBootstrapConnect;
  }

  //----------------------------------------------------------------------------
  /**
   * Returns currently configured timeout value from the preference store, observing default settings and min/max settings for that parameter
   * 
   * @param  Can be called with an already available preferenceStore. If none is passed, one will be temporarily instantiated on the fly.
   * 
   * @return Number of cycles allowable for threaded watchdog, or 0 if the threaded watchdog should be disabled.
   * 
   * @author Joerg Sigle
   */
  public static Integer getTimeoutThreadedWatchdog() {
	IPreferenceStore preferenceStore=new SettingsPreferenceStore(CoreHub.localCfg);
	return getTimeoutThreadedWatchdog(preferenceStore);
  }	
  
  public static Integer getTimeoutThreadedWatchdog(IPreferenceStore preferenceStore) {
	System.out.println("LocalOfficeApplicationPreferencesPage: getTimeoutThreadedWatchdog begin");
		
	Integer timeoutThreadedWatchdog=timeoutThreadedWatchdogDefault;	//Start by establishing a valid default setting
	try {
		timeoutThreadedWatchdog=Integer.parseInt(preferenceStore.getString(PREFS_TIMEOUT_THREADED_WATCHDOG).trim());  //20130310js timeout made configurable for the threaded watchdog timer added in 1.4.x by js
	} catch (Throwable throwable) {
	    //do not consume
	}
	if (timeoutThreadedWatchdog<timeoutThreadedWatchdogMin) {timeoutThreadedWatchdog=timeoutThreadedWatchdogMin;};
	if (timeoutThreadedWatchdog>timeoutThreadedWatchdogMax) {timeoutThreadedWatchdog=timeoutThreadedWatchdogMax;};
	
	System.out.println("LocalOfficeApplicationPreferencesPage: getTimeoutThreadedWatchdog returning "+timeoutThreadedWatchdog);
	return timeoutThreadedWatchdog;
  }
  
  //----------------------------------------------------------------------------
  /**
   * Returns currently configured Text CotfOption[i][j] configured content from the preference store
   * 
   * @param  Can be called with an already available preferenceStore. If none is passed, one will be temporarily instantiated on the fly.
   * 
   * @return The indicated Text from CotfOption[i][j] obtained from the preferecneStore.
   * 
   * @author Joerg Sigle
   */
  public static String getCotfOption(int i,int j) {
	IPreferenceStore preferenceStore=new SettingsPreferenceStore(CoreHub.localCfg);
	return getCotfOption(preferenceStore,i,j);
  }	
  
  public static String getCotfOption(IPreferenceStore preferenceStore,int i,int j) {
	System.out.println("LocalOfficeApplicationPreferencesPage: getCotfOption begin");
		
	//Start by establishing a valid default setting.
	//We do NOT need to hard code anything like the pre noatext_jsl 1.4.9 used noa_1234567890123456.odt here,
	//because we can better recognize and handle a fully unconfigured setup further down where we actually create the temp file. 
	String getCotfOption;
	getCotfOption="";
	
	try {
		getCotfOption=preferenceStore.getString(PREFERENCE_BRANCH+PREFERENCE_COTF+PREFERENCE_cotf_elements[i]+"_"+PREFERENCE_cotf_parameters[j]).trim();
	} catch (Throwable throwable) {
	    //do not consume
	}
	
	System.out.println("LocalOfficeApplicationPreferencesPage: getCotfOption returning "+getCotfOption);
	return getCotfOption;
  }
  
	//----------------------------------------------------------------------------
	
	// TO DO: Refactoring: Similar code is used around the omnivore and noatext preferences. Maybe put it all into some jsigle.utils code file. Be careful not to break plugin independence by that.

	 /**
	   * 201305271847js:
	   * Clear unwanted_chars from s_incoming and return a String with all remaining chars whose codePoint is >= 32.
	   * Even if s_incoming is null or empty, an allocated but empty String is returned.
	   * Warning: This method relies on Java properly handling memory allocation by itself.
	   *  
	   * minus all characters that occur in 
	   * @param s_incoming		- The string that shall be cleaned from unwanted characters.
	   * @param unwanted_chars	- A string of characters >= 32 that must not appear in the returned StringBuffer. 
	   * @return				- A StringBuffer containing s_incoming cleaned from all chars whose codePoint is <32 or who are contained in unwanted_chars.
	   */
		
	//Ich verwende kein replaceAll, weil dessen Implementation diverse erforderliche Escapes offenbar nicht erlaubt.
	//Especially, \. is not available to specify a plain dot. (Na ja: \0x2e ginge dann doch - oder sollte gehen.
	//Findet aber nichts. Im interaktiven Suchen/Ersetzen in Eclipse ist \0x2e illegal; \x2e geht eher.
	//In Java Code geht ggf. \056 (octal) . Siehe unten beim automatischen Entfernen von Dateinamen-Resten besonders aus dem docTitle.))

	//A final trim() is NOT included, as the function may also be used on content that might consist of a single space -
	//namely, the configurable separation charactes between various elements of an auto-generated filename in Omnivore or TextView. 
	
	public static String cleanStringFromUnwantedCharsAndTrim(String s_incoming, String unwanted_chars) {
		StringBuffer s_clean=new StringBuffer();
		if (s_incoming != null) { 
			for (int n=0;n<s_incoming.length();n++) {
				String c=s_incoming.substring(n,n+1);
				if ((c.codePointAt(0)>=32) && (!unwanted_chars.contains(c))) {
					s_clean.append(c);
				}			
			}
		}
		return s_clean.toString();
	}												

//----------------------------------------------------------------------------

// TO DO: Refactoring: Similar code is used around the omnivore and noatext preferences. Maybe put it all into some jsigle.utils code file. Be careful not to break plugin independence by that.
	
  /**
   * Accepts some data to turn into a temporary filename element, and returns a formatted temporary filename element, observing current settings from the preference store, also observing default settings and min/max settings for that parameter
   * 
   * @param  Can be called with an already available preferenceStore. If none is passed, one will be temporarily instantiated on the fly.
   * 					Also accepts <code>String element_key</code> to identify the requested filename element, and the <code>String element_data</data> to be processed into a string constituting that filename element. 
   *
   * @return The requested filename element as a string.
   * 
   * @author Joerg Sigle
   */

  //FIXME: The same code (with slightly different names) is contained in Omnivore_js.PreferencePage.java; from where this was adopted. Maybe it should be refactored to one common js toolkit unit?. 
  
  public static String getNOAText_jslTemp_Filename_Element(String element_key,String element_data) {
	IPreferenceStore preferenceStore=new SettingsPreferenceStore(CoreHub.localCfg);
	return getNOAText_jslTemp_Filename_Element(preferenceStore, element_key, element_data);
  }	
  
  public static String getNOAText_jslTemp_Filename_Element(IPreferenceStore preferenceStore,String element_key,String element_data) {
	    
	  System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: element_key=<"+element_key+">");
	  
	  StringBuffer element_data_processed=new StringBuffer();
	  Integer nCotfRules=PREFERENCE_cotf_elements.length;
	   for (int i=0;i<nCotfRules;i++) {
			
		   System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: PREFERENCE_cotf_elements["+i+"]=<"+PREFERENCE_cotf_elements[i]+">");

		   if (PREFERENCE_cotf_elements[i].equals(element_key))	 {
			
			   System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: Match!");
			   
			   if (element_key.contains("constant")) {
				   //Since omnivore_js 1.4.5 / noatext_jsl 1.4.12:
				   //Mask all characters that shall not appear in the generated filename from add_trail_char.
				   //The masking is implemented here, and not merely after the input dialog, so that unwanted characters are caught
				   //even if they were introduced through manipulated configuration files or outdated settings.
				   //Do NOT trim leadig and trailing space however - some user might want a single space as separation character.
					
				   String constant=cleanStringFromUnwantedCharsAndTrim(
							preferenceStore.getString(PREFERENCE_BRANCH+PREFERENCE_COTF+PREFERENCE_cotf_elements[i]+"_"+PREFERENCE_cotf_parameters[1]),
							cotf_unwanted_chars);

				   System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: returning constant=<"+constant+">");

				   return constant;
			   }
			   else {
					//Shall we return ANY digits at all for this element, and later on: shall we cut down or extend the processed string to some defined number of digits?
					String snum_digits=preferenceStore.getString(PREFERENCE_BRANCH+PREFERENCE_COTF+PREFERENCE_cotf_elements[i]+"_"+PREFERENCE_cotf_parameters[1]).trim();
					System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: snum_digits=<"+snum_digits+">");

					//If the num_digits for this element is empty, then return an empty result - the element is disabled.
					if (snum_digits.isEmpty()) {
						return "";
					}
					
					Integer num_digits=-1;
					if (snum_digits != null)	{
						try {
							num_digits=Integer.parseInt(snum_digits);
						} catch (Throwable throwable) {
							//do not consume
						}
					}
					
					//if num_digits for this element is <= 0, then return an empty result - the element is disabled.
					if (num_digits<=0) {
						return "";
					}
					
					if (num_digits>nNOAText_jslPREF_cotf_element_digits_max) {
						num_digits=nNOAText_jslPREF_cotf_element_digits_max;
					}
					System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: num_digits=<"+num_digits+">");
					
					//Start with the passed element_data string
					String element_data_incoming=element_data.trim();
					System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: element_data_incoming=<"+element_data_incoming+">");
					
					//Remove all characters that shall not appear in the generated filename and trim leading and trailing whitespace
					String element_data_processed5=cleanStringFromUnwantedCharsAndTrim(element_data_incoming, cotf_unwanted_chars).trim();
					
					System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: element_data_processed5=<"+element_data_processed5+">");
					
					//filter out some special unwanted strings from the title that may have entered while importing and partially renaming files
					String element_data_processed4=element_data_processed5.replaceAll("_noa[0-9]+\056[a-zA-Z0-9]{0,3}","");					//remove filename remainders like _noa635253160443574060.doc 
					String element_data_processed3=element_data_processed4.replaceAll("noa[0-9]+\056[a-zA-Z0-9]{0,3}","");					//remove filename remainders like noa635253160443574060.doc 
					String element_data_processed2=element_data_processed3.replaceAll("_omni_[0-9]+_vore\056[a-zA-Z0-9]{0,3}","");	//remove filename remainders like _omni_635253160443574060_vore.pdf
					String element_data_processed1=element_data_processed2.replaceAll("omni_[0-9]+_vore\056[a-zA-Z0-9]{0,3}","");		//remove filename remainders like omni_635253160443574060_vore.pdf
					 
					System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: element_data_processed1=<"+element_data_processed1+">");
					
					//Limit the length of the result if it exceeds the specified or predefined max number of digits
					if (element_data_processed1.length()>num_digits) {
						element_data_processed1=element_data_processed1.substring(0,num_digits);
					}
					
					System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: num_digits=<"+num_digits+">");
					
					//If a leading fill character is given, and the length of the result is below the specified max_number of digits, then fill it up.
					//Note: We could also check whether the num_digits has been given. Instead, I use the default max num of digits if not.

				    //Since omnivore_js 1.4.5 / noatext_jsl 1.4.12:
					//Mask all characters that shall not appear in the generated filename from add_trail_char.
					//The masking is implemented here, and not merely after the input dialog, so that unwanted characters are caught
					//even if they were introduced through manipulated configuration files or outdated settings.
					//Do NOT trim leadig and trailing space however - some user might want a single space as separation character.
					
					String lead_fill_char=cleanStringFromUnwantedCharsAndTrim(
							preferenceStore.getString(PREFERENCE_BRANCH+PREFERENCE_COTF+PREFERENCE_cotf_elements[i]+"_"+PREFERENCE_cotf_parameters[0]),
							cotf_unwanted_chars);

					System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: lead_fill_char=<"+lead_fill_char+">");
					
					if ((lead_fill_char != null) && (lead_fill_char.length()>0) && (element_data_processed1.length()<num_digits)) {
						lead_fill_char=lead_fill_char.substring(0,1);
						
						System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: lead_fill_char=<"+lead_fill_char+">");
						System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: num_digits=<"+num_digits+">");
						System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: element_data_processed1.length()=<"+element_data_processed1.length()+">");
						System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: element_data_processed1=<"+element_data_processed1+">");
						
						for (int n=element_data_processed1.length();n<=num_digits;n++) {
							element_data_processed.append(lead_fill_char);
							System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: n, element_data_processed="+n+", <"+element_data_processed+">");
						}				
					}
					element_data_processed.append(element_data_processed1);
					
					System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: element_data_processed=<"+element_data_processed+">");
					
					
					//If an add trailing character is given, add one (typically, this would be a space or an underscore).
					//Even if a string is entered in the configuration dialog, only the first valid character is used.
					
				    //Since omnivore_js 1.4.5 / noatext_jsl 1.4.12:
					//Mask all characters that shall not appear in the generated filename from add_trail_char.
					//The masking is implemented here, and not merely after the input dialog, so that unwanted characters are caught
					//even if they were introduced through manipulated configuration files or outdated settings.
					//Do NOT trim leadig and trailing space however - some user might want a single space as separation character.
					
					String add_trail_char=cleanStringFromUnwantedCharsAndTrim(
							preferenceStore.getString(PREFERENCE_BRANCH+PREFERENCE_COTF+PREFERENCE_cotf_elements[i]+"_"+PREFERENCE_cotf_parameters[2]),
							cotf_unwanted_chars);

					System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: add_trail_char=<"+add_trail_char+">");
					
					if ((add_trail_char != null) && (add_trail_char.length()>0)) {
						add_trail_char=add_trail_char.substring(0,1);
						System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: add_trail_char=<"+add_trail_char+">");
						element_data_processed.append(add_trail_char);			
						System.out.println("LocalOfficeApplicationPreferencePage: getNOAText_jslTemp_Filename_Element: element_data_processed=<"+element_data_processed+">");
					} 			
				}
			   
			   return element_data_processed.toString();	//This also breaks the for loop
		   } // if ... equals(element_key)
		} //for int i...
	   return "";		//default return value, if nothing is defined.
  }  
  
}