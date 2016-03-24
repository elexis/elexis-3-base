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
package ag.ion.noa4e.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.jsigle.noa.PreferenceConstants;

import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.noa4e.internal.ui.preferences.LocalOfficeApplicationPreferencesPage;
import ag.ion.noa4e.internal.ui.preferences.LocalOfficeApplicationPreferencesPage;
import ag.ion.noa4e.ui.operations.ActivateOfficeApplicationOperation;
import ag.ion.noa4e.ui.operations.FindApplicationInfosOperation;
import ag.ion.noa4e.ui.wizards.application.LocalApplicationWizard;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Joerg Sigle
 * @date 24.06.2012
 * @date 20.02.2012
 * 
 * @author Andreas Br�ker
 * @version $Revision: 11685 $
 * @date 28.06.2006
 */
public class NOAUIPlugin extends AbstractUIPlugin {

  /** ID of the plugin. */
  public static final String  PLUGIN_ID                      = "ag.ion.noa4e.ui";

  /**
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
 
  /**
   * Preferences key of the office home path. 
   * This is not used in the adoption for Elexis by js reproducing adoptions by gw - 20120221js
   * We'll use entries in ...(Hub.localCfg) instead.
   * I comment out the definition so I can automatically see an error anywhere in the code
   * where it is still in use and probably needs to be replaced.
   * public static final String  PREFERENCE_OFFICE_HOME         = "localOfficeApplicationPreferences.applicationPath";
   */
  
  /** 
   * Preferences key of the prevent office termination information.
   * This is not used in the adoption for Elexis by js reproducing adoptions by gw - 20120221js
   * We'll use entries in ...(Hub.localCfg) instead.
   * I comment out the definition so I can automatically see an error anywhere in the code
   * where it is still in use and probably needs to be replaced.
   * public static final String  PREFERENCE_PREVENT_TERMINATION = "localOfficeApplicationPreferences.preventTermintation";
   */

  public static final String PREFS_PREVENT_TERMINATION	= "openoffice/preventTermination";

  private static final String ERROR_ACTIVATING_APPLICATION   = Messages.NOAUIPlugin_message_application_can_not_be_activated;

  //The shared instance.
  private static NOAUIPlugin  plugin;

  private static FormToolkit  formToolkit                    = null;

  //----------------------------------------------------------------------------
  /**
   * The constructor.
   * 
   * @author Andreas Br�ker
   * @date 28.06.2006
   */
  public NOAUIPlugin() {
    plugin = this;
  }

  //----------------------------------------------------------------------------
  /**
   * This method is called upon plug-in activation.
   * 
   * @param context bundle context
   * 
   * @throws Exception if the bundle can not be started
   * 
   * @author Andreas Br�ker
   * @date 28.06.2006
   */
  public void start(BundleContext context) throws Exception {
    super.start(context);
  }

  //----------------------------------------------------------------------------
  /**
   * This method is called when the plug-in is stopped.
   * 
   * @param context bundle context
   * 
   * @throws Exception if the bundle can not be stopped
   * 
   * @author Andreas Br�ker
   * @date 28.06.2006
   */
  public void stop(BundleContext context) throws Exception {
    super.stop(context);
    plugin = null;
  }

  //----------------------------------------------------------------------------
  /**
   * Returns the shared instance.
   * 
   * @return shared instance
   * 
   * @author Andreas Br�ker
   * @date 28.06.2006
   */
  public static NOAUIPlugin getDefault() {
    return plugin;
  }

  //----------------------------------------------------------------------------
  /**
   * Returns an image descriptor for the image file at the given
   * plug-in relative path.
   *
   * @param path the path
   * 
   * @return the image descriptor
   * 
   * @author Andreas Br�ker
   * @date 28.06.2006
   */
  public static ImageDescriptor getImageDescriptor(String path) {
    return AbstractUIPlugin.imageDescriptorFromPlugin("ag.ion.noa4e.ui", path);
  }

  //----------------------------------------------------------------------------
  /**
   * Returns form toolkit.
   * 
   * @return form toolkit
   * 
   * @author Andreas Br�ker
   * @date 28.06.2006
   */
  public static FormToolkit getFormToolkit() {
    if (formToolkit == null) {
      formToolkit = new FormToolkit(Display.getCurrent());
      formToolkit.getColors().markShared();
    }
    return formToolkit;
  }

  //----------------------------------------------------------------------------
  /**
   * Starts local office application.
   * 
   * @param shell shell to be used
   * @param officeApplication office application to be started
   * 
   * @return information whether the office application was started or not - only 
   * if the status severity is <code>IStatus.OK</code> the application was started 
   * 
   * @author Joerg Sigle
   * @date 24.06.2012
   * @date 20.02.2012
   *
   * @author Andreas Br�ker
   * @date 28.06.2006
   */
  public static IStatus startLocalOfficeApplication(Shell shell,
      IOfficeApplication officeApplication) {

	System.out.println("NOAUIPlugin: startLocalOfficeApplication(Shell, officeApplication) begin");
	  
	while (true) {
	  System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): while (true) trying to start...");
		
	  IStatus status = internalStartApplication(shell, officeApplication);
	  
	  System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): returned from trying to start.");
	  if (status==null)	System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): status==null");
	  else				System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): status="+status.toString());
		
	  if (status.getSeverity() == IStatus.ERROR) {  
    	System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): WARNING: status.getSeverity()==IStatus.ERROR");
  		
        if (MessageDialog.openQuestion(shell,
            Messages.NOAUIPlugin_dialog_change_preferences_title,
            Messages.NOAUIPlugin_dialog_change_preferences_message)) {
          PreferenceDialog preferenceDialog = PreferencesUtil.createPreferenceDialogOn(shell,
              LocalOfficeApplicationPreferencesPage.PAGE_ID,
              null,
              null);
          if (preferenceDialog.open() == Window.CANCEL)
            return Status.CANCEL_STATUS;
          else
            continue;
        }
      }
	  else System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): SUCCESS: !status.getSeverity()==IStatus.ERROR"); 
	  
	  
      try {
     	//My warning in the following line referred to the original noa4e code:
    	//System.out.println("NOAUIPlugin: internalStartApplication(2): getting officeHome (WARNING: probably from the wrong source)...");
    	System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): getting officeHome...");
    	System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): Using js mod adopted for Elexis, reproducing prior GW adoptions, P_OOBASEDIR via ...(Hub.localCfg)");

    	//JS modified this:
    	//The original code tries to access a preference store which is not used in Elexis,
    	//according to GWs mods in (back then:) LocalOfficeApplicationPreferencesPage.java
    	//Unsuitable original line, removed:
    	//String officeHome = getDefault().getPreferenceStore().getString(PREFERENCE_OFFICE_HOME);
    	//Newly inserted lines:
    	IPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.localCfg);
    	String officeHome = preferenceStore.getString(PreferenceConstants.P_OOBASEDIR);
    	
    	if (officeHome==null)	System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): WARNING: officeHome==null");
        else					System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): officeHome="+officeHome);
    	  
    	System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): trying to get preventTermination setting...");

    	//My warning in the following line referred to the original noa4e code:
    	//System.out.println("NOAUIPlugin: WARNING: THIS PROBABLY REFERENCES THE WRONG PREFERENCE STORE. SEE LocalPreferences...GWeirich/JS mods");
    		
        //JS modified this:
	    //The original code tries to access a preference store which is not used in Elexis,
	    //according to GWs mods in (back then:) LocalOfficeApplicationPreferencesPage.java
	    //Unsuitable original line, removed:
    	//boolean preventTermination = getDefault().getPreferenceStore().getBoolean(PREFERENCE_PREVENT_TERMINATION);
        //Newly inserted lines:
	    //Already declared further above: IPreferenceStore preferenceStore = new SettingsPreferenceStore(Hub.localCfg);
    	boolean preventTermination=preferenceStore.getBoolean(PREFS_PREVENT_TERMINATION);
    	
        System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): got preventTermination setting="+preventTermination);
    	
        if (preventTermination) {
        	System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): trying officeApplication.getDesktopService().activateTerminationPrevention()...");
        	officeApplication.getDesktopService().activateTerminationPrevention();
        	System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): SUCCESS: officeApplication.getDesktopService().activateTerminationPrevention()");
        }
      }
      catch (OfficeApplicationException officeApplicationException) {
        //no prevention
    	  System.out.println("NOAUIPlugin: startLocalOfficeApplication(2): FAILED: preventTermination could NOT be set.");
      	
      }

      System.out.println("NOAUIPlugin: startLocalOfficeApplication(2) end, returning status");
      return status;
    }
  }

  //----------------------------------------------------------------------------
  /**
   * Internal method in order to start the office application.
   * 
   * @param shell shell to be used
   * @param officeApplication office application to be used
   * 
   * @return status information
   * 
   * @author Joerg Sigle
   * @date 24.06.2012
   * @date 20.02.2012 00:57
   *
   * @author Andreas Br�ker
   * @date 28.06.2006
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
  private static IStatus internalStartApplication(final Shell shell,
      IOfficeApplication officeApplication) {
	
	System.out.println("NOAUIPlugin: internalStartApplication() begin");
	
	if (officeApplication.isActive()) {
	  System.out.println("NOAUIPlugin: internalStartApplication(): officeApplication.isActive(), so returning immediately.");
      return Status.OK_STATUS;
	}
	
	System.out.println("NOAUIPlugin: internalStartApplication(): !officeApplication.isActive(), so starting it up...");
    
    boolean configurationChanged = false;
    boolean canStart = false;
    String home = null;

    HashMap configuration = new HashMap(1);
    
    
    //My warning in the following line referred to the original noa4e code:
    //System.out.println("NOAUIPlugin: internalStartApplication(): getting officeHome (WARNING: probably from the wrong source)...");
    System.out.println("NOAUIPlugin: internalStartApplication(): getting officeHome...");
    System.out.println("NOAUIPlugin: internalStartApplication(): Using js mod adopted for Elexis, reproducing prior GW adoptions, P_OOBASEDIR via ...(Hub.localCfg)");

    //JS modified this:
    //The original code tries to access a preference store which is not used in Elexis,
    //according to GWs mods in (back then:) LocalOfficeApplicationPreferencesPage.java
    //Unsuitable original line, removed:
    //String officeHome = getDefault().getPreferenceStore().getString(PREFERENCE_OFFICE_HOME);
    //Newly inserted lines:
    IPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.localCfg);
    String officeHome = preferenceStore.getString(PreferenceConstants.P_OOBASEDIR);
   
    if (officeHome==null)	System.out.println("NOAUIPlugin: internalStartApplication(): WARNING: officeHome==null");
    else					System.out.println("NOAUIPlugin: internalStartApplication(): officeHome="+officeHome);
    
    if (officeHome.length() != 0) {
      File file = new File(officeHome);
      if (file.canRead()) {
    
    	System.out.println("NOAUIPlugin: internalStartApplication(): Check: officeHome is a valid path. Setting canStart to true.");
        
    	configuration.put(IOfficeApplication.APPLICATION_HOME_KEY, officeHome);
        canStart = true;
      }
      else {
      	System.out.println("NOAUIPlugin: internalStartApplication(): WARNING: officeHome is NOT a valid path. Leaving canStart at false.");
        
      	MessageDialog.openWarning(shell,
            Messages.NOAUIPlugin_dialog_warning_invalid_path_title,
            Messages.NOAUIPlugin_dialog_warning_invalid_path_message);
      }
    }
    
    System.out.println("NOAUIPlugin: internalStartApplication(): canStart="+canStart);

    if (!canStart) {
      System.out.println("NOAUIPlugin: internalStartApplication(): canStart==false; trying to auto locate available office suite installations...");
      
      configurationChanged = true;
      ILazyApplicationInfo[] applicationInfos = null;
      boolean configurationCompleted = false;
      try {
        ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(shell);
        FindApplicationInfosOperation findApplicationInfosOperation = new FindApplicationInfosOperation();
        progressMonitorDialog.run(true, true, findApplicationInfosOperation);
        applicationInfos = findApplicationInfosOperation.getApplicationsInfos();
        if (applicationInfos.length == 1) {
          if (applicationInfos[0].getMajorVersion() == 2 || (applicationInfos[0].getMajorVersion() == 1 && applicationInfos[0].getMinorVersion() == 9)) {
            configuration.put(IOfficeApplication.APPLICATION_HOME_KEY,
                applicationInfos[0].getHome());
            configurationCompleted = true;
          }
        }
      }
      catch (Throwable throwable) {
        System.out.println("NOAUIPlugin: internalStartApplication(): canStart==false; cannot auto locate an office suite installation. So we must search manually...");
        //we must search manually
      }

      System.out.println("NOAUIPlugin: internalStartApplication(): configurationCompleted="+configurationCompleted);

      if (!configurationCompleted) {
        LocalApplicationWizard localApplicationWizard = new LocalApplicationWizard(applicationInfos);
        if (home != null && home.length() != 0)
          localApplicationWizard.setHomePath(home);
        WizardDialog wizardDialog = new WizardDialog(shell, localApplicationWizard);
        if (wizardDialog.open() == Window.CANCEL)
          return Status.CANCEL_STATUS;

        configuration.put(IOfficeApplication.APPLICATION_HOME_KEY,
            localApplicationWizard.getSelectedHomePath());
      }
    }

    System.out.println("NOAUIPlugin: internalStartApplication(): the office suite configuration should now be valid:");
    if (officeApplication==null)	System.out.println("NOAUIPlugin: internalStartApplication(): officeApplication==null");
    else							System.out.println("NOAUIPlugin: internalStartApplication(): officeApplication="+officeApplication.toString());
    if (configuration==null)		System.out.println("NOAUIPlugin: internalStartApplication(): configuration==null");
    else							System.out.println("NOAUIPlugin: internalStartApplication(): configuration="+configuration.toString());
    if (shell==null)				System.out.println("NOAUIPlugin: internalStartApplication(): shell==null");
    else							System.out.println("NOAUIPlugin: internalStartApplication(): shell="+shell.toString());
    System.out.println("NOAUIPlugin: internalStartApplication(): Finally trying activateOfficeApplication(officeApplication, configuration, shell):");
    
    IStatus status = activateOfficeApplication(officeApplication, configuration, shell);
    if (configurationChanged) {
        System.out.println("NOAUIPlugin: internalStartApplication(): Configuration of PREFERENCE_OFFICE_HOME changed.");
        System.out.println("NOAUIPlugin: internalStartApplication(): Storing the new configuration.");
        System.out.println("NOAUIPlugin: internalStartApplication(): Using js mod adopted for Elexis, reproducing prior GW adoptions, P_OOBASEDIR via ...(Hub.localCfg)");

    	//JS modified this:
        //The original code tries to access a preference store which is not used in Elexis,
        //according to GWs mods in (back then:) LocalOfficeApplicationPreferencesPage.java
        //Unsuitable original line, removed:
    	//getDefault().getPluginPreferences().setValue(PREFERENCE_OFFICE_HOME,
    	//                                             configuration.get(IOfficeApplication.APPLICATION_HOME_KEY).toString());
    	//Newly inserted line:
        preferenceStore.setValue(PreferenceConstants.P_OOBASEDIR, configuration.get(IOfficeApplication.APPLICATION_HOME_KEY).toString());
    }
      
    System.out.println("NOAUIPlugin: internalStartApplication() end, returning status");
    return status;
  }

  //----------------------------------------------------------------------------
  /**
   * Activates office application.
   * 
   * @param officeApplication office application to be activated
   * @param configuration configuration to be used
   * @param shell shell to be used
   * 
   * @return status information of the activation
   *  
   * @author Andreas Br�ker
   * @date 28.08.2006
   */
  private static IStatus activateOfficeApplication(IOfficeApplication officeApplication,
      Map configuration, Shell shell) {
    IStatus status = Status.OK_STATUS;
    
    System.out.println("NOAUIPlugin: activateOfficeApplication() begin");
    System.out.println("NOAUIPlugin: activateOfficeApplication(): trying to getActiveWorkbenchWindow()");

    try {
      officeApplication.setConfiguration(configuration);
      boolean useProgressMonitor = true;
      IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      if (workbenchWindow == null)
        useProgressMonitor = false;
      else {
        IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
        if (workbenchPage == null)
          useProgressMonitor = false;

        if (workbenchPage==null)		System.out.println("NOAUIPlugin: activateOfficeApplication(): workbenchPage==null");
        else							System.out.println("NOAUIPlugin: activateOfficeApplication(): workbenchPage="+workbenchPage.toString());

      }

      if (workbenchWindow==null)	System.out.println("NOAUIPlugin: activateOfficeApplication(): workbenchWindow==null");
      else							System.out.println("NOAUIPlugin: activateOfficeApplication(): workbenchWindow="+workbenchWindow.toString());
      
      System.out.println("NOAUIPlugin: activateOfficeApplication(): trying to get new ActivateOfficeApplicationOperation()");
  
      ActivateOfficeApplicationOperation activateOfficeApplicationOperation = new ActivateOfficeApplicationOperation(officeApplication);
      
      if (activateOfficeApplicationOperation==null)	System.out.println("NOAUIPlugin: activateOfficeApplication(): activateOfficeApplicationOperation==null");
      else											System.out.println("NOAUIPlugin: activateOfficeApplication(): activateOfficeApplicationOperation="+activateOfficeApplicationOperation.toString());

      System.out.println("NOAUIPlugin: activateOfficeApplication(): useProgressMonitor="+useProgressMonitor);
            
      if (useProgressMonitor) {
        System.out.println("NOAUIPlugin: activateOfficeApplication(): trying to get new ProgressMonitorDialog()");

        ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(shell);

        if (progressMonitorDialog ==null)	System.out.println("NOAUIPlugin: activateOfficeApplication(): progressMonitorDialog==null");
        else								System.out.println("NOAUIPlugin: activateOfficeApplication(): progressMonitorDialog="+progressMonitorDialog.toString());
        if (activateOfficeApplicationOperation ==null)	System.out.println("NOAUIPlugin: activateOfficeApplication(): activateOfficeApplicationOperation==null");
        else								System.out.println("NOAUIPlugin: activateOfficeApplication(): activateOfficeApplicationOperation="+activateOfficeApplicationOperation.toString());

        System.out.println("NOAUIPlugin: activateOfficeApplication(): trying to run ProgressMonitorDialog");
        
        //in progressMonitorDialog.run() selbst kann ich offenbar kein Monitoring hineinschreiben,
        //das gehört zu den eclipse Klassen. Aber vielleicht nach activateOfficeApplicationOperation.

        progressMonitorDialog.run(true, true, activateOfficeApplicationOperation);

        System.out.println("NOAUIPlugin: activateOfficeApplication(): ProgressMonitorDialog should now be running...");
      }
      else
        activateOfficeApplicationOperation.run(new NullProgressMonitor());

      System.out.println("NOAUIPlugin: activateOfficeApplication(): check whether exception occured:");
      
      if (activateOfficeApplicationOperation.getOfficeApplicationException() != null) {

    	System.out.println("NOAUIPlugin: activateOfficeApplication(): check whether exception occured: YES: so show status in error dialog");
              	  
    	status = new Status(IStatus.ERROR,
            PLUGIN_ID,
            IStatus.ERROR,
            activateOfficeApplicationOperation.getOfficeApplicationException().getMessage(),
            activateOfficeApplicationOperation.getOfficeApplicationException());
        ErrorDialog.openError(shell,
            Messages.NOAUIPlugin_title_error,
            ERROR_ACTIVATING_APPLICATION,
            status);
      }
    

    }
    catch (InvocationTargetException invocationTargetException) {
      status = new Status(IStatus.ERROR,
          PLUGIN_ID,
          IStatus.ERROR,
          invocationTargetException.getMessage(),
          invocationTargetException);
      ErrorDialog.openError(shell,
          Messages.NOAUIPlugin_title_error,
          ERROR_ACTIVATING_APPLICATION,
          status);
    }
    catch (OfficeApplicationException officeApplicationException) {
      status = new Status(IStatus.ERROR,
          PLUGIN_ID,
          IStatus.ERROR,
          officeApplicationException.getMessage(),
          officeApplicationException);
      ErrorDialog.openError(shell,
          Messages.NOAUIPlugin_title_error,
          ERROR_ACTIVATING_APPLICATION,
          status);
    }
    catch (InterruptedException interruptedException) {
      return Status.CANCEL_STATUS;
    }

    System.out.println("NOAUIPlugin: activateOfficeApplication() end, returning status");
    return status;
  }
  //----------------------------------------------------------------------------

}