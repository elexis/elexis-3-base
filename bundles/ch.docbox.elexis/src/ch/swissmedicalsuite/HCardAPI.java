/**
 *
 * Copyright (c) 2010, Oliver Egger, visionary ag
 * java interface to http://jna.java.net/
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * hCardAPI: Copyright (C) 2011 Ing. Büro Kleiber, 8142 Uitikon
 * Distributed by H-Net AG, Zürich www.h-net.ch
 *
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The preferred method is to set the jna.library.path system property to the path to your target library.
 *  -Djna.library.path=c:/hCard
 */

package ch.swissmedicalsuite;

import org.apache.commons.lang3.StringUtils;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.ptr.PointerByReference;

public interface HCardAPI extends Library {
	HCardAPI INSTANCE = (HCardAPI) Native.loadLibrary((Platform.isWindows() ? "hCardAPI" : StringUtils.EMPTY),
			HCardAPI.class);

	int getVersion();

	int initApi(String oem_code, boolean api_call_trace, PointerByReference stat_text);

	int getStatus(String mandant, PointerByReference stat_text);

	int getUserProxyPort(String mandant);

	int startSmsBrowser(String mandant, String URL, int win_handle);
}