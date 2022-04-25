/*******************************************************************************
 * Copyright (c) 2007, medshare and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    M. Imhof - initial implementation
 *
 *******************************************************************************/

package ch.medshare.elexis.directories;

public class HtmlParser {
	private final StringBuffer htmlText;
	private int currentPos = 0;

	public HtmlParser(String htmlText) {
		super();
		this.htmlText = new StringBuffer(htmlText);
	}

	public void reset() {
		this.currentPos = 0;
	}

	public boolean startsWith(String prefix) {
		if (prefix == null) {
			return false;
		}
		return htmlText.substring(currentPos, currentPos + prefix.length()).startsWith(prefix);
	}

	/**
	 * Verschiebt Cursor bis zur Position nach dem gefundenen String
	 */
	public boolean moveTo(String keyString) {
		int newPos = getNextPos(keyString);
		if (newPos >= 0) {
			currentPos = newPos + keyString.length();
			display(currentPos);
			return true;
		}
		return false;
	}

	public String getTail() {
		return htmlText.substring(currentPos, htmlText.length());
	}

	public String extractTo(String endKeyString) {
		int newPos = getNextPos(endKeyString);
		String text = "";
		if (newPos >= 0) {
			text = htmlText.substring(currentPos, newPos);
			currentPos = newPos + endKeyString.length();
			display(currentPos);
		}

		return text;
	}

	private void display(int pos) {
		if (true) {
			return;
		}
		int theEnd = pos + 1000;
		if (theEnd >= htmlText.length()) {
			theEnd = htmlText.length() - 1;
		}
		if (pos < theEnd) {
			System.out.println("Current: " + htmlText.substring(pos, theEnd));
		}
	}

	public String extract(String startKeyString, String endKeyString) {
		if (moveTo(startKeyString)) {
			return extractTo(endKeyString);
		}
		return "";
	}

	public int getNextPos(String keyString, int pos) {
		return htmlText.indexOf(keyString, pos);
	}

	public int getNextPos(String keyString) {
		return getNextPos(keyString, currentPos);
	}
}
