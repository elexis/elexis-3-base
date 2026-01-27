/*******************************************************************************
 * Copyright (c) 2013-2021, 2026 J. Sigle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    J. Sigle - added a preference page to omnivore to control  a new automatic archiving functionality
 *    N. Giger - separated parts of PreferencePage.java into Preferences.java and PreferenceConstants.java
 *    J. Sigle - review re-including what was missed when adopting omnivore_js back into omnivore
 *    Someone  - changed safe 80 chars filename length limit back to unsafe 120 (again), replaced "" by StringUtils.EMPTY,
 *               hampered readability by using 80 chars length limit THERE & removed (c) header from file written by s.o. else
 *    J. Sigle - restored the 80 chars filename length default limit & added a few entertaining comments
 *               for the next person who might want to change that value back to the unsafe value again.
 *               I leave the StringUtils.EMPTY however; They're a prime example of fashion over function and our modern times. 
 *    
 *    20260102js: After I had to repair and restore this from Elexis 2.1.7js to Elexis 3.7js,
 *                re-repairing and re-restoring (sic!) things again from Elexis 3.7js to Elexis 3.13js 
 *                can only be done in little portions :-(
 *                
 *                Most important thing for Omnivore right now is the filename length limit.
 *                (Dragging files with names > 80 chars into Omnivore would really fail in MS Win.)
 *                 
 *                Re-Restoring the improved drag-out-support from omnivore into MS Outlook must wait -
 *                even if the re-crippled version still doesn't manage to reliably pass content
 *                e.g. to MS Outlook 2013 in 2025... (just tested and confirmed that).
 *
 *******************************************************************************/

package ch.elexis.omnivore;

import org.apache.commons.lang3.StringUtils;

public class PreferenceConstants {

	public static final String PREFBASE = Constants.PLUGIN_ID + "/"; //$NON-NLS-1$
	public static final String STOREFSGLOBAL = PREFBASE + "store_in_fs_global"; //$NON-NLS-1$
	public static final String STOREFS = PREFBASE + "store_in_fs"; //$NON-NLS-1$
	public static final String BASEPATH = PREFBASE + "basepath"; //$NON-NLS-1$
	public static final String CATEGORIES = PREFBASE + "categories"; //$NON-NLS-1$
	public static final String DATE_MODIFIABLE = PREFBASE + "date_modifiable"; //$NON-NLS-1$
	public static final String PREFERENCE_SRC_PATTERN = PREFBASE + "src_pattern"; //$NON-NLS-1$
	public static final String PREFERENCE_DEST_DIR = PREFBASE + "dest_dir"; //$NON-NLS-1$
	public static final String PREF_MAX_FILENAME_LENGTH = PREFBASE + "max_filename_length"; //$NON-NLS-1$
	public static final String TWAINACCESS_TYPE = PREFBASE + "twainaccess_type"; //$NON-NLS-1$
	public static final String AUTO_BILLING = PREFBASE + "automatic_billing"; //$NON-NLS-1$
	public static final String AUTO_BILLING_BLOCK = PREFBASE + "automatic_billing_block"; //$NON-NLS-1$

    //20130325js: The following setting is used in ch.elexis.omnivore.data/DocHandle.java.
    //Linux and MacOS may be able to handle longer filenames, but we observed that Windows 7 64-bit will not import files with names longer than 80 chars.
    //So I make this setting configurable. Including a safe default and limits that a user cannot exceed.

    // 20210326js: The previous default of 120 is NOT safe. <<------ PLEASE READ THIS BEFORE CHANGING IT AGAIN.
    // As just stated it won't work with MS Windows. Changed it back to 80 -          ...and AGAIN, in 2026...
    // the same value as in the last version of omnivore_js from Elexis 2.1.7js           ...AROUND 2014.....?
	public static final Integer OmnivoreMax_Filename_Length_Min = 12;
	public static final Integer OmnivoreMax_Filename_Length_Default = 80;    // 80 is max safe value for MS Win.
	public static final Integer OmnivoreMax_Filename_Length_Max = 255;

    // 20130325js: For automatic archiving of incoming files:
    // Here is a comfortable way to specify how many rules shall be available:
    // The individual Strings in the following arrays may be left empty - they will be automatically filled.
    // But the smaller number of entries for Src and Dest determines how many rule editing field pairs
	// are provided on the actual preferences page, and processed later on.
    // The actual content of all field labels, and all preference store keys, is computed from content of the messages.properties file.
    // I've tested the construction of the preferences dialog with fields for some 26 rules, worked like a charm :-)
	
	// 20260102js: Oh there's something new :-)
	// https://stackoverflow.com/questions/4095501/is-stringutils-empty-recommended ?
	//
	// Answer: Of course not.
	// Would it be portable? - Of course not. Definitely no discussion here.
	// Wasn't "" clear enough? - Nope.
	// Does it have a storage advantage? - Needs 8.5 x more, or + 15 bytes per occurrence. Plus one import to begin with.
	// Does it save compile time? Or run time? - Through an import, and a field of an object? NOT EVEN if the compiler was optimized to replace it by "" ... 
	//
	// Why is it there? Any guesses?
	// -> 50 new ways to pass time & generate tons of diffs without any meaning or advantage.
	// -> Looks like super important code - even when there's just nothing.
	// -> Combine it with auto line breaks after 80 chars and *really* give your readers something to chew on.
	// -> Because it's possible. And someone started it. And others do it. Like... Dark mode. Flat GUI items with Teraflop GPUs.
	//    Incomprehensible, oversimplified icons. Marketing-"MB" = 1000 KB. You name it.
	//
	// -> Does it improve readability? - Well. Just compare the current code with the original lines now added to this comment.
	//    Then ask yourself: What is more clear? What can you understand more easily? What is more efficient? What is more error prone?
	//
	// public static final String[] PREF_SRC_PATTERN = { "", "", "", "", "" };
	// public static final String[] PREF_DEST_DIR =    { "", "", "", "", "" };
	
	public static final String[] PREF_SRC_PATTERN = { StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
			StringUtils.EMPTY, StringUtils.EMPTY };
	public static final String[] PREF_DEST_DIR = { StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
			StringUtils.EMPTY, StringUtils.EMPTY };
	public static final int nPREF_SRC_PATTERN = PREF_SRC_PATTERN.length;
	public static final int nPREF_DEST_DIR = PREF_DEST_DIR.length;
}

