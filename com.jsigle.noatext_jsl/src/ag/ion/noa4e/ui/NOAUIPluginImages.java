/****************************************************************************
 *                                                                          *
 * NOA (Nice Office Access)                                     						*
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU Lesser General Public License Version 2.1.              *
 *                                                                          * 
 * GNU Lesser General Public License Version 2.1                            *
 * ======================================================================== *
 * Copyright 2003-2006 by IOn AG                                            *
 *                                                                          *
 * This library is free software; you can redistribute it and/or            *
 * modify it under the terms of the GNU Lesser General Public               *
 * License version 2.1, as published by the Free Software Foundation.       *
 *                                                                          *
 * This library is distributed in the hope that it will be useful,          *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
 * Lesser General Public License for more details.                          *
 *                                                                          *
 * You should have received a copy of the GNU Lesser General Public         *
 * License along with this library; if not, write to the Free Software      *
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,                    *
 * MA  02111-1307  USA                                                      *
 *                                                                          *
 * Contact us:                                                              *
 *  http://www.ion.ag																												*
 *  http://ubion.ion.ag                                                     *
 *  info@ion.ag                                                             *
 *                                                                          *
 ****************************************************************************/
 
/*
 * Last changes made by $Author: andreas $, $Date: 2006-08-07 13:09:58 +0200 (Mo, 07 Aug 2006) $
 */
package ag.ion.noa4e.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Image registry of the plugin.
 * 
 * @author Andreas Bröker
 * @version $Revision: 9195 $
 */
public class NOAUIPluginImages {
  
  /** Banner for an office application wizard. */
  public static final String IMG_WIZBAN_APPLICATION             = "IMG_WIZBAN_APPLICATION";   //$NON-NLS-1$
    
  private static URL ICON_BASE_URL = null; 
  
  //private final static String ETOOL   = "etool16/"; //basic colors - size 16x16 //$NON-NLS-1$
  //private final static String DLCL    = "dlcl16/"; //disabled - size 16x16 //$NON-NLS-1$
  //private final static String ELCL    = "elcl16/"; //enabled - size 16x16 //$NON-NLS-1$
  //private final static String OBJECT  = "obj16/"; //basic colors - size 16x16 //$NON-NLS-1$
  private final static String WIZBAN  = "wizban/"; //basic colors - size 75x66 //$NON-NLS-1$
  //private final static String OVR     = "ovr16/"; //basic colors - size 7x8 //$NON-NLS-1$
  //private final static String VIEW    = "eview16/"; // views //$NON-NLS-1$

  private static ImageRegistry imageRegistry = null;
  
  static {
    String pathSuffix = "icons/full/"; //$NON-NLS-1$
    ICON_BASE_URL= NOAUIPlugin.getDefault().getBundle().getEntry(pathSuffix);
  }
  
  //----------------------------------------------------------------------------
  /**
   * Initializes the image registry and declare all images.
   * 
   * @return initialized image registry.
   * 
   * @author Andreas Bröker
   */
  public static ImageRegistry initializeImageRegistry() {
    Display display= Display.getCurrent();
    if (display == null) {
      display= Display.getDefault();
    }
    imageRegistry= new ImageRegistry(display);    
    declareImages();
    return imageRegistry;
  }
  //----------------------------------------------------------------------------
  /**
   * Returns the image registry.
   * 
   * @return image registry
   * 
   * @author Andreas Bröker
   */
  public static ImageRegistry getImageRegistry() {
    if (imageRegistry == null) {
      initializeImageRegistry();
    }
    return imageRegistry;
  }
  //----------------------------------------------------------------------------
  /**
   * Returns the <code>Image</code> identified by the given key,
   * or <code>null</code> if it does not exist.
   * 
   * @param key key to be used
   * 
   * @return image
   * 
   * @author Andreas Bröker
   */
  public static Image getImage(String key) {
    return getImageRegistry().get(key);
  }
  //----------------------------------------------------------------------------
  /**
   * Returns image descriptor of the image with the submitted key.
   * 
   * @param key key to be used
   * 
   * @return image descriptor of the image with the submitted key
   * 
   * @author Andreas Bröker
   */
  public static ImageDescriptor getImageDescriptor(String key) {
    return getImageRegistry().getDescriptor(key);
  }
  //----------------------------------------------------------------------------
  /**
   * Declares all images.
   * 
   * @author Andreas Bröker
   */
  private static void declareImages() {    
    declareRegistryImage(IMG_WIZBAN_APPLICATION, WIZBAN + "application_wiz.gif"); //$NON-NLS-1$
  }  
  //----------------------------------------------------------------------------
  /**
   * Declares an image.
   * 
   * @param key key of the image
   * @param path path the image
   * 
   * @author Andreas Bröker
   */
  private final static void declareRegistryImage(String key, String path) {
    ImageDescriptor desc= ImageDescriptor.getMissingImageDescriptor();
    try {
      desc = ImageDescriptor.createFromURL(makeImageFileURL(path));
    } 
    catch (MalformedURLException me) {
      //do nothing
    }
    imageRegistry.put(key, desc);    
  }
  //----------------------------------------------------------------------------
  /**
   * Constructs URL of a image.
   * 
   * @param imagePath path of the image.
   * 
   * @return URL of the image
   * 
   * @throws MalformedURLException if the path is not valid
   * 
   * @author Andreas Bröker
   */
  private static URL makeImageFileURL(String imagePath) throws MalformedURLException {
    if (ICON_BASE_URL == null) {
      throw new MalformedURLException();
    }      
    return new URL(ICON_BASE_URL, imagePath);
  }
  //----------------------------------------------------------------------------
  
}