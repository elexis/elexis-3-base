 /****************************************************************************
 *                                                                          *
 * ubion.ORS - Office Editor Core - adopted for NOAText_jsl                 *
 *                                                                          *
 * NOAText_jsl based upon NOA (Nice Office Access) / noa-libre              *
 *                                                                          *
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * ubion.ORS - The Open Report Suite                                        *
 *                                                                          *
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * Subproject: Office Editor Core                                           *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU General Public License Version 2.1                      *
 *                                                                          * 
 * GNU General Public License Version 2.1                                   *
 * ======================================================================== *
 * Portions Copyright 2011-2012 by Joerg Sigle                              *
 * Copyright 2003-2005 by IOn AG                                            *
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
 * Review the things added below producing "WARNING:" printlns and possibly use the "Intelligent" approach shown there.
 ****************************************************************************/

/*
 * Last changes made by $Author: jsigle $, $Date: 2012-06-26 23:14:00 +0200 (Di, 26 Jun 2012) $
 */
package ag.ion.bion.workbench.office.editor.core;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.runtime.IOfficeProgressMonitor;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.BundleContext;

import java.io.File;

import java.net.URL;

import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import java.awt.Frame;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Andreas Br�ker
 * @version $Revision: 11647 $
 */
public class EditorCorePlugin extends Plugin {
  
  /** ID of the plugin. */
  //201206262355js changed the plugin id
  //Original PLUGIN_ID in NOA for Eclipse application or examples
  //public static final String PLUGIN_ID = "ag.ion.bion.workbench.office.editor.core";
  //public static final String PLUGIN_ID = "com.jsigle.noatext_jsl";

  //Also see src/ag/ion/bion/workbench/office/editor/core/EditorCorePlugin.java where this string is also hardcoded (with/without _jsl)
  //Also see noa4e/internal/ui/preferences/LocalOfficeApplicationPreferencesPage.java (with/without _jsl) 
  //Also see com/jsigle/noa/NOAText.java where this string is also hardcoded (with//without _jsl)
		
  //201302210135 corrected the plugin ID for 1.4.7 from com.jsigle.noatext to noatext_jsl
  //public static final String PLUGIN_ID = "com.jsigle.noatext";
  public static final String PLUGIN_ID = "com.jsigle.noatext_jsl";
  
  //The shared instance.
  private static EditorCorePlugin plugin;
  //Resource bundle.
  private ResourceBundle resourceBundle;
  
  private IOfficeApplication localOfficeApplication = null;
  
  private String librariesLocation = null;
	
  //----------------------------------------------------------------------------
  /**
   * The constructor.
   * 
   * @author Andreas Br�ker
   */
  public EditorCorePlugin() {
    super();
		plugin = this;
		try {
		  resourceBundle = ResourceBundle.getBundle("ag.ion.bion.workbench.office.editor.core.CorePluginResources");
		} 
    catch (MissingResourceException missingResourceException) {
      resourceBundle = null;
    }
	}
  //----------------------------------------------------------------------------
  /**
   * This method is called upon plug-in activation.
   * 
   * @param context context to be used
   * 
   * @throws Exception if the bundle can not be started
   * 
   * @author Joerg Sigle  - workaround for noa being supplied below noatext_jsl package, and not as a separate library.
   * date 26.06.2012
   * 
   * @author Andreas Br�ker
   */
  public void start(BundleContext context) throws Exception {
    super.start(context);
/*  
 * js 1.4.4 20120626js
 * All of this is missing from the original noatext plugin, it only appears in version noa4e 2.0.14 and noa-libre.
 * As I don't know a correct setting - anything like "", "." "blub" apparently avoids error messages and displays the preferences,
 * but we cannot see details for any office package selected in the Einstellungen page, and briefe panel remains empty -, I just try removing it all.
 * Doesn't make it better either.
 */  
    System.out.println();
    System.out.println("EditorCorePlugin: start(BundleContext context): About to start the "+this.toString()+" plugin.");
    System.out.println("EditorCorePlugin: start(BundleContext context): Obtaining IOfficeApplication.NOA_NATIVE_LIB_PATH...");
    System.out.println();
    System.out.println("EditorCorePlugin: ToDo: 201206262304js, 201207262255js - NOAText_jsl - containing noa-libre: ");
    System.out.println("EditorCorePlugin: ToDo:");
    System.out.println("EditorCorePlugin: ToDo: In start(BundleContext context),");
    System.out.println("EditorCorePlugin: ToDo: in System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,getLibrariesLocation():");
    System.out.println("EditorCorePlugin: ToDo: getLibrariesLocation() would return null, when no separate noa library was available.");
    System.out.println("EditorCorePlugin: ToDo:");
    System.out.println("EditorCorePlugin: ToDo: As the noa libraries are currently located below the NOAText_jsl plugin,");
    System.out.println("EditorCorePlugin: ToDo: and have specific adoptions for Elexis, I replaced that call by an empty string.");
    System.out.println("EditorCorePlugin: ToDo:");
    System.out.println("EditorCorePlugin: ToDo: (Even) when the noa libraries are not separately available, that cures these errors:");
    System.out.println("EditorCorePlugin: ToDo: 'An error occured while automatically activating bundle: ch.elexis.noatext_jsl or com.jsigle.noatext_jsl'");
    System.out.println("EditorCorePlugin: ToDo: 'Unable to create the selected preference page. ag.ion.noa4e.internal.ui.preferences.LocalOfficeApplicationPreferencesPage'");
    System.out.println("EditorCorePlugin: ToDo:");
    System.out.println("EditorCorePlugin: ToDo: Also see src/ag/ion/bion/workbench/office/editor/core/EditorCorePlugin.java where this string is also hardcoded (with/without _jsl)");
    System.out.println("EditorCorePlugin: ToDo: Also see noa4e/internal/ui/preferences/LocalOfficeApplicationPreferencesPage.java where this string is also hardcoded (with/without _jsl)");
    System.out.println("EditorCorePlugin: ToDo: Also see com/jsigle/noa/NOAText.java where this string is also hardcoded (with/without _jsl)");
    System.out.println("EditorCorePlugin: ToDo:");
    System.out.println("EditorCorePlugin: ToDo: A 'semi intelligent' alternative aproach would be:");
    System.out.println("EditorCorePlugin: ToDo: [if getLibrariesLocation()==null then use empty string, else use getLibrariesLocation()]");
    System.out.println("EditorCorePlugin: ToDo:");
    System.out.println("EditorCorePlugin: ToDo: This might automatically use portions of an externally provided noa library file -");
    System.out.println("EditorCorePlugin: ToDo: elegant for easy updates by alternative future versions of noa, but on the other hand,");
    System.out.println("EditorCorePlugin: ToDo: that might introduce unwanted variability.");
    System.out.println("EditorCorePlugin: ToDo:");
    System.out.println("EditorCorePlugin: ToDo: I may reconsider using that, once I have reviewed again the spread and implications");
    System.out.println("EditorCorePlugin: ToDo: of each specific adoption in noa for Elexis. I need to see whether a completely");
    System.out.println("EditorCorePlugin: ToDo: externally provided noa would supply everything needed for [stable] operation?.");
    System.out.println("EditorCorePlugin: ToDo:");
    System.out.println("EditorCorePlugin: ToDo: If yes, I would remove everything Elexis specific adoption from the");
    System.out.println("EditorCorePlugin: ToDo: core noa (or rather: noa-libre) library that might hinder such re-separation.");
    System.out.println("EditorCorePlugin: ToDo:");
    System.out.println("EditorCorePlugin: ToDo: Several code alternatives are already included below (and commented out).");
    
    System.out.println();
    if (this==null)	System.out.println("EditorCorePlugin: start(BundleContext context): WARNING: this==null");
    else								System.out.println("EditorCorePlugin: start(BundleContext context): this="+this.toString());

    if (getLibrariesLocation()==null)	System.out.println("EditorCorePlugin: start(BundleContext context): WARNING: getLibrariesLocation()==null");
    else								System.out.println("EditorCorePlugin: start(BundleContext context): getLibrariesLocation()="+getLibrariesLocation());

    if (IOfficeApplication.NOA_NATIVE_LIB_PATH==null)	System.out.println("EditorCorePlugin: start(BundleContext context): WARNING: IOfficeApplication.NOA_NATIVE_LIB_PATH==null");
    else												System.out.println("EditorCorePlugin: start(BundleContext context): IOfficeApplication.NOA_NATIVE_LIB_PATH="+IOfficeApplication.NOA_NATIVE_LIB_PATH);

    System.out.println("EditorCorePlugin:");
    System.out.println("EditorCorePlugin: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    System.out.println("EditorCorePlugin: !!!  Please note: If you see an error in console output below this line,    !!!");
    System.out.println("EditorCorePlugin: !!!               and the noatext_jsl plugin should not load correctly,     !!!");
    System.out.println("EditorCorePlugin: !!!               then please review the output above and the code          !!!");
    System.out.println("EditorCorePlugin: !!!               just below in EditorCorePlugin.java, which should set     !!!");
    System.out.println("EditorCorePlugin: !!!               IOfficeApplication.NOA_NATIVE_LIB_PATH to something       !!!");
    System.out.println("EditorCorePlugin: !!!               reasonable either automatically or manually.              !!!");
    System.out.println("EditorCorePlugin: !!!                                                                         !!!");
    System.out.println("EditorCorePlugin: !!!  A problem may also appear when opening a document in the plugin:       !!!");
    System.out.println("EditorCorePlugin: !!!               The document frame would remain empty, and the console    !!!");
    System.out.println("EditorCorePlugin: !!!               would show the error message: ag.ion.noa4e.ui code=4      !!!");
    System.out.println("EditorCorePlugin: !!!               Can't load library: ... nativeview.dll                    !!!");
    System.out.println("EditorCorePlugin: !!!                                                                         !!!");
    System.out.println("EditorCorePlugin: !!!  Most probably, you'll have to set a constant in getLibrariesLocation() !!!");
    System.out.println("EditorCorePlugin: !!!               to the name of your bundle, instead of ag.ion.noa         !!!");
    System.out.println("EditorCorePlugin: !!!               or com.jsigle.noatext_jsl or something alike. js          !!!");
    System.out.println("EditorCorePlugin: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
    			    
    //201206262240js:
    //Replaced call to getLibrariesLocation() by "", because noa-libre / noa / noa4e etc. libraries are currently all locally arranged
    //below the plugin. Calling getLibrariesLocation() would have worked, when these libraries were also available inside Eclipse IDE
    //as part of an installed NOA for Eclipse, or as exported packages parallel to NOAText_jsl. It would return null, when all that was not the case -
    //and, effectively. when the plugin was run on systems in Elexis without separate ag.ion.noa-2.2.3.jar files.
    //And that REALLY fixed the problems of errormessages as listed in the verbose output above.
    
    //201207262320js:
    //If NO System.setProperty(...) statement is active here at all, this will run through the plugin startup,
    //and contents of the Einstellungen dialog for the plugin will be available and work.
    //But displaying a document through the plugin will complain via my console output about missing nativeview.dll:
    /*
    OfficePanel: activateNewFrame: resulting status=Status OK: unknown code=0 OK null
    OfficePanel: getOfficeApplication
    OfficePanel: activateNewFrame: CATCHING - SORRY...
    OfficePanel: hideProgressIndicator, default: nop
    OfficePanel: showOfficeFrame: begin
    OfficePanel: showOfficeFrame: baseComposite.isDisposed()=false
    OfficePanel: showOfficeFrame: officeComposite=Composite {}
    OfficePanel: showOfficeFrame: end
    OfficePanel: loadDocument: Status after doing the work:
    OfficePanel: documentPath=C:\Users\jsigle\AppData\Local\Temp\noa2231906313026136093.odt
    OfficePanel: currentDocumentPath=C:\Users\jsigle\AppData\Local\Temp\noa2231906313026136093.odt
    OfficePanel: document==null
    OfficePanel: officeFrame==null
    OfficePanel: lastLoadingStatus=Status ERROR: ag.ion.noa4e.ui code=4 Can't load library: L:\home\oeffentlich\software-archiv\java-elexis-development\eclipse-rcp-helios-SR2-win32\eclipse/lib/nativeview.dll org.eclipse.core.runtime.CoreException: Can't load library: L:\home\oeffentlich\software-archiv\java-elexis-development\eclipse-rcp-helios-SR2-win32\eclipse/lib/nativeview.dll
    NOAText: createMe
    NOAText: createMe: office=ag.ion.bion.officelayer.internal.application.LocalOfficeApplication@bdb859
    NOAText: createMe: panel=OfficePanel {}
    OfficePanel: getDocument
    OfficePanel: WARNING: Please note: will return document==null
    NOAText: createMe: WARNING: doc==null
    NOAText: createMe ends
    NOAText: storeToByteArray
    NOAText: getMimeType
    */

    //Original version. This would NOT work without separately available noa libraries:
    //As of 20120726, with noa-libre content below com.jsigle.noatext_jsl (or ch.elexis.noatext_jsl)
    //this really makes the plugin startup fail already, and causes error messages in the Einstellungen Panel of the plugin.
    //Maybe even when projects noa and noa-libre are available within the workspace,
    //but probably with at least some of their content either moved away or rearranged. 
    ////System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,getLibrariesLocation());

    //(Fast forward, as of 201207262358, been through the attempts below and more research:
    // I'm re-using exactly this setting above - coded further below - after correcting a constant in getLibrariesLocation() below.) 
    
    
    //An "Intelligent" version I suggested above, might be used later on with more knowledge, given need, and time for testing:
    ///if (getLibrariesLocation()==null)	System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,"");
    ///else	   							System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,getLibrariesLocation());
    
    //Another "Dumb" version, curently (as of 2012-06) works with noa libraries below com.jsigle.noatext_jsl:
    //System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,"L:\\home\\jsigle\\workspace\\elexis-2.1.6-20120105\\elexis-base\\com.jsigle.noatext_jsl");

    
    //This simplified version I have reported further above to work,
    //with noa (or rather: noa-libre) library content directly below ch.elexis.noatext_jsl (or com.jsigle.noatext_jsl).
    //However, while this works through startup of the plugin, when displaying a document it returns errors similar to those
    //listed above; with the following central line:
    /*
    OfficePanel: lastLoadingStatus=Status ERROR: ag.ion.noa4e.ui code=4 Expecting an absolute path of the library: /nativeview.dll org.eclipse.core.runtime.CoreException: Expecting an absolute path of the library: /nativeview.dll
    */
    //System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,"");

    //And same thing here:
    //System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,this.toString());
       
    //Others apparently have had the same problem:
    //http://ubion.ion.ag/mainForumFolder/noa_forum/0195
    //Hmmm.
    //Well, apparently, getLibrariesLocation should *not* return null but just provide that intelligent service,...
    //it should be further below in this file,... yes, and it uses a constant instructing it to look below ag.ion.noa.
    //That may explain quite a bit. Changed that to com.jsigle.noatext_jsl and added a ToDoRemark. Now let me try:
    System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,getLibrariesLocation());
    
    //201207270002js: Et voila, the plugin can be started, and display documents, inside Eclipse (at least) again.
    //Even with its new name: jsigle.com.noatext_jsl. Now, let me try it after an export of the whole package...
    
  /*
   *   
   */
    /**
     * Workaround in order to integrate the OpenOffice.org window into a AWT frame
     * on Linux based systems. 
     */
    
    System.out.println("EditorCorePlugin: *********************************************************************************************");
    System.out.println("EditorCorePlugin: Workaround to integrate OpenOffice.org window into an AWT frame on Linux: try new Frame();");
    System.out.println("EditorCorePlugin: Workaround: Apparently, this does not work any more. Now, tried it without that...");
    System.out.println("EditorCorePlugin: Workaround: ...which produces nothing different at all. No luck getting content into a frame.");
    System.out.println("EditorCorePlugin: Workaround: TO DO: WE CAN POSSIBLY DISABLE THIS PERMANENTLY. Temp reenabled it again for now.");
    System.out.println("EditorCorePlugin: Workaround: TO DO: NO, ONCE AGAIN DISABLED HERE AND IN NOAText.java as well. What happens?");
    System.out.println("EditorCorePlugin: Workaround: TO DO: Under Linux: No change, today, if both are disabled.");
    System.out.println("EditorCorePlugin: *********************************************************************************************");
    
    /*201302210124js tried disabling this:
    try {
    	System.out.println("EditorCorePlugin: Workaround: about to try: new Frame();");
        new Frame();
        System.out.println("EditorCorePlugin: Workaround: returned from new Frame();");
    }
    catch(Throwable throwable) {
        System.out.println("EditorCorePlugin: Workaround: new Frame() threw an error that should only occur in headless mode...");
      //only occurs in headless mode, where it doesn't matter
    }
    /* */    
    
  }
  //----------------------------------------------------------------------------
  /**
   * This method is called when the plug-in is stopped.
   * 
   * @param context context to be used
   * 
   * @throws Exception if the bundle can not be stopped
   * 
   * @author Andreas Br�ker
   */
  public void stop(BundleContext context) throws Exception {
    super.stop(context);
  }
  //----------------------------------------------------------------------------
  /**
   * Returns the shared instance.
   * 
   * @return shared instance
   * 
   * @author Andreas Br�ker
   */
  public static EditorCorePlugin getDefault() {
    return plugin;
  }
  //----------------------------------------------------------------------------
  /**
   * Returns the string from the plugin's resource bundle,
   * or 'key' if not found.
   * 
   * @param key key to be used
   * 
   * @return string from the plugin's resource bundle,
   * or 'key' if not found
   * 
   * @author Andreas Br�ker
   */
  public static String getResourceString(String key) {
    ResourceBundle bundle = EditorCorePlugin.getDefault().getResourceBundle();
		try {
		  return (bundle != null) ? bundle.getString(key) : key;
		} 
    catch (MissingResourceException missingResourceException) {
      return key;
    }
  }
  //----------------------------------------------------------------------------
  /**
   *  Returns the plugin's resource bundle.
   * 
   * @return plugin's resource bundle
   * 
   * @author Andreas Br�ker
   */
  public ResourceBundle getResourceBundle() {
    return resourceBundle;
  }
  //----------------------------------------------------------------------------
  /**
   * Returns local office application. The instance of the application
   * will be managed by this plugin.
   * 
   * @return local office application
   * 
   * @author Andreas Br�ker
   */
  public synchronized IOfficeApplication getManagedLocalOfficeApplication() {
    if(localOfficeApplication == null) {
      HashMap configuration = new HashMap(1);
      configuration.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
      try {
        localOfficeApplication = OfficeApplicationRuntime.getApplication(configuration);
      }
      catch(Throwable throwable) {
        //can not be - this code must work
        Platform.getLog(getBundle()).log(new Status(IStatus.ERROR, EditorCorePlugin.PLUGIN_ID,
            IStatus.ERROR, throwable.getMessage(), throwable));
      }
    }
    return localOfficeApplication;
  }
  //----------------------------------------------------------------------------
  /**
   * Returns location of the libraries of the plugin. Returns null if the location
   * can not be provided.
   * 
   * @return location of the libraries of the plugin or null if the location
   * can not be provided
   * 
   * @author Andreas Br�ker
   */
  public String getLibrariesLocation() {
    if(librariesLocation == null) {
      try {
    	//20120726js:
    	//This probably caused the requirement of a separate package to run the noatext plugin,
    	//and/or the trouble when I tried to use getLibrariesLocation() further above, instead of
    	//a static setting. 
        //URL url = Platform.getBundle("ag.ion.noa").getEntry("/");
    	  
    	System.out.println("EditorCorePlugin: getLibrariesLocation(): ToDo: This method uses a constant string to identify the library.");
    	System.out.println("EditorCorePlugin: getLibrariesLocation(): ToDo: It is set to ag.ion.noa in the original code,");
    	System.out.println("EditorCorePlugin: getLibrariesLocation(): ToDo: and I have currently set this to com.jsigle.noatext_jsl");
    	System.out.println("EditorCorePlugin: getLibrariesLocation(): ToDo: Instead, it should be passed as a variable, determined");
    	System.out.println("EditorCorePlugin: getLibrariesLocation(): ToDo: on compile time, run-time, or configurable at run-time.");
    	  
        URL url = Platform.getBundle("com.jsigle.noatext_jsl").getEntry("/");  
        url  = FileLocator.toFileURL(url);
        String bundleLocation = url.getPath();
        File file = new File(bundleLocation);
        bundleLocation = file.getAbsolutePath();
        bundleLocation = bundleLocation.replace('/', File.separatorChar) + File.separator + "lib";
        librariesLocation = bundleLocation;
        
        System.out.println("EditorCorePlugin: getLibrariesLocation(): librariesLocation="+bundleLocation);
      }
      catch(Throwable throwable) {
        return null;
      }
    }
    return librariesLocation;
  }  
  //----------------------------------------------------------------------------
  
}