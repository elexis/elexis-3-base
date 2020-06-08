/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import at.medevit.medelexis.text.msword.Activator;
import at.medevit.medelexis.text.msword.plugin.util.CommunicationFile.FileType;
import at.medevit.medelexis.text.msword.ui.MissingConversionDialog;

public class DocumentConversion {

	private static OfficeManager officeManager;
	public static final String PREFERENCE_OFFICE_HOME = "at.medevit.msword.officehome"; //$NON-NLS-1$
	
	public static OfficeManager getOfficeManager(){
		if (officeManager == null) {
			DefaultOfficeManagerConfiguration config = new DefaultOfficeManagerConfiguration();
			try {
				officeManager = config.buildOfficeManager();
			} catch (IllegalStateException e) {
				// try to create the office manager with the office home path from the preferences
				String officeHome =
					Activator.getDefault().getPreferenceStore().getString(PREFERENCE_OFFICE_HOME);
				try {
					config.setOfficeHome(officeHome);
				} catch (IllegalArgumentException iae) {
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run(){
							MissingConversionDialog dialog =
								new MissingConversionDialog(PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell());
							if (dialog.open() == Window.OK) {
								Activator.getDefault().getPreferenceStore()
									.setValue(PREFERENCE_OFFICE_HOME, dialog.getOfficeHomePath());
							}
						}
					});
					officeHome =
						Activator.getDefault().getPreferenceStore()
							.getString(PREFERENCE_OFFICE_HOME);
					config.setOfficeHome(officeHome);
				}
				officeManager = config.buildOfficeManager();
			}
			officeManager.start();
		}
		return officeManager;
	}
	
	public static void convertToWordCompatibleType(final CommunicationFile file){
		if (file.fileType == FileType.SXW || file.fileType == FileType.ODT) {
			try {
				OfficeManager officeManager = getOfficeManager();
				OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);

				converter.convert(file.getFile(), file.getConversionFile(), converter
					.getFormatRegistry().getFormatByExtension("doc")); //$NON-NLS-1$
				file.write(new FileInputStream(file.getConversionFile()));
			} catch (FileNotFoundException e) {
				throw new IllegalStateException(e);
			} catch (OfficeException e) {
				officeManager = null;
				throw e;
			}
		}
	}

	public static void dispose(){
		if (officeManager != null) {
			officeManager.stop();
			officeManager = null;
		}
	}
}
