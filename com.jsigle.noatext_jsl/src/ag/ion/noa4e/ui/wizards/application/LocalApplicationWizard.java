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
 * Why does commenting setDefaultPageImageDescriptor() below make Einstellungen work in Elexis?
 ****************************************************************************/

/*
 * Last changes made by $Author: jsigle $, $Date: 2012-06-23 14:38:00 +0100 (Su, 23 Jun 2012) $
 */
package ag.ion.noa4e.ui.wizards.application;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.noa4e.ui.NOAUIPluginImages;

/**
 * Wizard in order to define the path of a local OpenOffice.org application.
 * 
 * @author Andreas Br�ker
 * @version $Revision: 11685 $
 */
public class LocalApplicationWizard extends Wizard implements IWizard {

  private LocalApplicationWizardDefinePage localApplicationWizardDefinePage = null;

  private ILazyApplicationInfo[]           applicationInfos                 = null;

  private String                           homePath                         = null;

  //----------------------------------------------------------------------------
  /**
   * Constructs new LocalApplicationWizard.
   * 
   * @author Joerg Sigle - added progress monitoring code
   * @date 22.05.2012
   *
   * @author Andreas Br�ker
   */
  public LocalApplicationWizard() {
	this(null);
	System.out.println("LOAW: LocalApplicationWizard() - just constructed new LocalApplicationWizard");
  	setNeedsProgressMonitor(true);
  }

  //----------------------------------------------------------------------------
  /**
   * Constructs new LocalApplicationWizard.
   * 
   * @param applicationInfos application infos to be used (can be null)
   * 
   * @author Joerg Sigle - added monitoring code and disabled something,
   *                       apparently to make things work, given my older comments.
   * @date 22.05.2012
   * 
   * @author Andreas Br�ker
   */
  public LocalApplicationWizard(ILazyApplicationInfo[] applicationInfos) {
	System.out.println("LOAW: LocalApplicationWizard(applicationInfos) - constructs new LocalApplicationWizard");
	if (applicationInfos==null) System.out.println("LOAW: Please note: applicationInfos==null");
	else System.out.println("LOAW: Please note: applicationInfos: "+applicationInfos.toString());
	
	this.applicationInfos = applicationInfos;

    setWindowTitle(Messages.LocalApplicationWizard_title);

    System.out.println("LOAW: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	System.out.println("LOAW: LocalApplicationWizard(applicationInfos) WARNING: SKIPPED FOR DEBUGGING: setDefaultPageImageDescriptor()");
	System.out.println("LOAW: This actually makes the Einstellungen - NOAText_jsl - Define - code work without throwing an error.");
	System.out.println("LOAW: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	//js setDefaultPageImageDescriptor(NOAUIPluginImages.getImageDescriptor(NOAUIPluginImages.IMG_WIZBAN_APPLICATION));
  }

  //----------------------------------------------------------------------------  
  /**
   * Sets home path to be edited.
   * 
   * @param homePath home path to be edited
   * 
   * @author Joerg Sigle - added progress monitoring code
   * @date 22.05.2012
   *
   * @author Andreas Br�ker
   */
  public void setHomePath(String homePath) {
	  System.out.println("LOAW: setHomePath to "+homePath);
		this.homePath = homePath;
  }

  //----------------------------------------------------------------------------
  /**
   * Returns selected home path of an local office application. Returns null
   * if a home path is not available.
   * 
   * @return selected home path of an local office application or null
   * if a home path is not available
   * 
   * @author Joerg Sigle - added progress monitoring code
   * @date 22.05.2012
   *
   * @author Andreas Br�ker
   */
  public String getSelectedHomePath() {
	  System.out.println("LOAW: getSelectedHomePath");
	  if (localApplicationWizardDefinePage != null)
      return localApplicationWizardDefinePage.getSelectedHomePath();
    return null;
  }

  //----------------------------------------------------------------------------
  /**
   * Performs any actions appropriate in response to the user 
   * having pressed the Finish button, or refuse if finishing
   * now is not permitted.
   *
   * @return <code>true</code> to indicate the finish request
   *   was accepted, and <code>false</code> to indicate
   *   that the finish request was refused
   * 
   * @author Joerg Sigle - added progress monitoring code
   * @date 22.05.2012
   *
   * @author Andreas Br�ker
   */
  public boolean performFinish() {
	System.out.println("LOAW: performFinish");
	if (localApplicationWizardDefinePage.getSelectedHomePath() != null)
      return true;
    return false;
  }

  //----------------------------------------------------------------------------
  /**
   * Adds any last-minute pages to this wizard.
   * 
   * @author Joerg Sigle - added progress monitoring code
   * @date 22.05.2012
   *
   * @author Andreas Br�ker
   */
  public void addPages() {
	System.out.println("LOAW: addPages");
	localApplicationWizardDefinePage = new LocalApplicationWizardDefinePage(homePath,
        applicationInfos);
    addPage(localApplicationWizardDefinePage);
  }
  //----------------------------------------------------------------------------

}