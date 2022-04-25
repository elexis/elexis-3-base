/*******************************************************************************
 * Copyright (c) 2010, Niklaus Giger and Medelexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Niklaus Giger - initial implementation
 *
 *******************************************************************************/

package ch.ngiger.comm.ftp;

public class FtpSemaException extends Exception {
	private static final long serialVersionUID = -2150109019599639291L;

	public FtpSemaException(String arg0) {
		super(arg0, null);
	}

	public FtpSemaException(Throwable cause) {
		super(cause);
	}

}
