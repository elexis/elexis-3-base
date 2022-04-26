/*******************************************************************************
 * Portions copyright (c) 2010, 2012 Jörg Sigle www.jsigle.com and portions copyright (c) 2007, medshare and Elexis
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    M. Imhof - initial implementation
 *    J. Sigle - 20101213-20101217, 20120712-20120713 www.jsigle.com
 *    			 Change of search request to request results in (probably more efficient) (almost-)text mode.
 *               Some comments added with regard to program functionality.
 *               Some debug/monitoring output added in internal version only;
 *                 commented out again for published version: System.out.print("jsdebug: ...
 *
 *******************************************************************************/

package ch.medshare.elexis.directories;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection; //js20120712 to support setting a user agent for http://tel.local.ch request
import java.text.MessageFormat;
import java.util.Locale;

public class DirectoriesHelper {

	private static String cleanupText(String text) {
		// 20120712 some additions that remove definitely unwanted portions of the
		// returned html
		// code:
		// text.replace uses search strings literally
		// text.replaceFirst, text.replaceAll use regex search strings

		// Remove extra boxes, maps etc. at the beginning - everything between </title>
		// and <div
		// class='summary'>
		// ?s enables "single line" mode, so that the search extends over multiple
		// lines:
		// Please note that this may break the HTML tree hierarchy;
		// I add a comment to warn you if you sould desire to use XPath or something
		// like that in
		// the future.
		text = text.replaceFirst("(?s)</title>.*<div class='summary'>",
				"</title>\n\n<!-- Elexis/js: Unwanted multiline content removed during preprocessing - this may have broken the HTML tree structure -->\n\n<div class='summary'>");

		// Remove the complete footer of the page. (outdated on the same day - I need to
		// remove even
		// more - see coment below)
		// ?s enables "single line" mode, so that the search extends over multiple
		// lines:
		// Please note that this may break the HTML tree hierarchy;
		// I add a comment to warn you if you sould desire to use XPath or something
		// like that in
		// the future.
		// text = text.replaceAll("(?s)<div id=\"footer\".*",
		// "\n<!-- Elexis/js: Unwanted multiline content removed during preprocessing -
		// this may have broken the HTML tree structure -->\n");

		// N.B.: If you search for "meier" in "bern", this will render a larger list.
		// Interestingly, Meier Angela does NOT have category entries, and she is last
		// on the first
		// page -
		// so when catIndex is computed, it finds another <div class='categories'> from
		// much further
		// below the interesting result table - and the evaluation jumps there, thereby
		// skipping all
		// remaining content for Meier Angela fields, and returning some unwanted stuff
		// in her
		// Zusatz
		// field instead. -> Ergo: We REALLY MUST STRIP not only the footer, but
		// actually everything
		// that comes after the list data and (at max) selection of additional list
		// pages
		// just to make sure this problem does not happen.
		text = text.replaceAll("(?s)<div class='content-box' id='ad-rectangle'>.*",
				"\n<!-- Elexis/js: Unwanted multiline content removed during preprocessing - this may have broken the HTML tree structure -->\n<!-- Removal of this html portion is, however, needed. Otherwise, parsing might search/skip into that part and last entries could be incompletely returned. -->\n");

		// Remove long lines that are a bit annoying during debugging:
		// single line links to adds:
		text = text.replaceAll("<div class='ad'.*></div>\n", "");
		// single line google maps coordinates
		text = text.replaceAll("<div data-east='.*></div>\n", "");

		text = text.replace("</nobr>", "").replace("<nobr>", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		text = text.replace("&amp;", "&"); //$NON-NLS-1$ //$NON-NLS-2$

		// This may also break the tree structure, but as of 20120712, it might not even
		// exist in
		// the incoming html code any more. js
		text = text.replace("<b class=\"searchWords\">", ""); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replace("</b>", ""); //$NON-NLS-1$ //$NON-NLS-2$

		text = text.replace((char) 160, ' '); // Spezielles Blank Zeichen wird ersetzt

		return text;
	}

	private static String cleanupUmlaute(String text) {
		/*
		 * text = text.replace("&#xE4;", "ae");//$NON-NLS-1$ //$NON-NLS-2$ text =
		 * text.replace("&#xC4;", "Ae");//$NON-NLS-1$ //$NON-NLS-2$ text =
		 * text.replace("&#xF6;", "oe");//$NON-NLS-1$ //$NON-NLS-2$ text =
		 * text.replace("&#xD6;", "Oe");//$NON-NLS-1$ //$NON-NLS-2$ text =
		 * text.replace("&#xFC;", "ue");//$NON-NLS-1$ //$NON-NLS-2$ text =
		 * text.replace("&#xDC;", "Ue");//$NON-NLS-1$ //$NON-NLS-2$
		 *
		 * text = text.replace("&#xE8;", "è");//$NON-NLS-1$ //$NON-NLS-2$ text =
		 * text.replace("&#xE9;", "é");//$NON-NLS-1$ //$NON-NLS-2$ text =
		 * text.replace("&#xEA;", "ê");//$NON-NLS-1$ //$NON-NLS-2$
		 *
		 * text = text.replace("&#xE0;", "à");//$NON-NLS-1$ //$NON-NLS-2$
		 *
		 * text = text.replace("&#xA0;", " ");//$NON-NLS-1$ //$NON-NLS-2$
		 */

		text = text.replace("&#xE4;", "ä");//$NON-NLS-1$ //$NON-NLS-2$
		text = text.replace("&#xC4;", "Ä");//$NON-NLS-1$ //$NON-NLS-2$
		text = text.replace("&#xF6;", "ö");//$NON-NLS-1$ //$NON-NLS-2$
		text = text.replace("&#xD6;", "Ö");//$NON-NLS-1$ //$NON-NLS-2$
		text = text.replace("&#xFC;", "ü");//$NON-NLS-1$ //$NON-NLS-2$
		text = text.replace("&#xDC;", "Ü");//$NON-NLS-1$ //$NON-NLS-2$

		text = text.replace("&#xE8;", "è");//$NON-NLS-1$ //$NON-NLS-2$
		text = text.replace("&#xE9;", "é");//$NON-NLS-1$ //$NON-NLS-2$
		text = text.replace("&#xEA;", "ê");//$NON-NLS-1$ //$NON-NLS-2$

		text = text.replace("&#xE0;", "à");//$NON-NLS-1$ //$NON-NLS-2$

		text = text.replace("&#xA0;", " ");//$NON-NLS-1$ //$NON-NLS-2$

		return text;
	}

	/**
	 * 20101213js added comments:
	 *
	 * @parameter: name, geo - name and location of the person/institution/... to be
	 *             searched for
	 * @return: a URL with a search request to tel.local.ch constructed from name
	 *          and geo
	 *
	 *          Die hier enthaltene URL ist auch am 2010-12-12 noch funktional, i.e.
	 *          die Eingabe von http://tel.local.ch/de/q/?what=meier&where=bern im
	 *          WWW-Browser liefert die gewünschte Antwort.
	 *
	 *          Ich ergänze aber: &mode=text Das blendet die Karte und hoffentlich
	 *          noch einigen anderen krimskrams aus; somit muss man weniger wirren
	 *          HTML Code verarbeiten, ausserdem spart das Bandbreite.
	 *
	 *          Derzeit werden wohl nur bis 10 results pro Seite zurückgeliefert und
	 *          ausgewertet, falls jemand Ergebnisse auswerten möchte, die sich über
	 *          mehrere Seiten erstrecken: ein &start=n würde die Anzeige bei
	 *          Eintrag n beginnen lassen, damit könnte man (theoretisch) ein
	 *          Schleife programmieren, die alle Ergebnisse in mehreren Schritten
	 *          abruft.
	 *
	 *          20120713js:
	 *
	 *          Currently, requests to both tel.local.ch return a page that appears
	 *          to be formatted for WAP, and includes references to
	 *          http://mobile.local.c/... when obtained through the elexis plugin.
	 *          The same url returns a proper www page when obtained through mozilla
	 *          firefox. The problematic part of this is that I cannot see the
	 *          desired results in the wap version :-( So I try to set a user agent
	 *          as well...
	 *
	 *          Ein &range=all könnte eventuell alle Ergebnisse zurückliefern -
	 *          nein, das tut es nicht (oder: derzeit nicht *mehr*), genauso scheint
	 *          auch &mode=text ignoriert zu werden.
	 */
	private static URL getURL(String name, String geo) throws MalformedURLException {
		name = name.replace(' ', '+');
		geo = geo.replace(' ', '+');

		String urlPattern = "http://tel.local.ch/{0}/q/?what={1}&where={2}&mode=text"; //$NON-NLS-1$

		System.out.print("jsdebug: DirectoriesHelper.java: " + urlPattern + "\n");
		System.out.print("jsdebug: DirectoriesHelper.java: language: " + Locale.getDefault().getLanguage() + "  name: "
				+ name + "  geo: " + geo + "\n");

		System.out.print("jsToDo:  DirectoriesHelper.java: ToDo: maybe add &range=all to the search request.\n");

		return new URL(MessageFormat.format(urlPattern, new Object[] { Locale.getDefault().getLanguage(), name, geo }));
	}

	/**
	 * Schreibt binäre Datei 20101213js: Ich bin nicht sicher, ob das überhaupt
	 * verwendet wird? Ggf. allenfalls ein Hilfsmittel für's Debugging?
	 */
	public static void writeFile(String filenamePath, final String text) throws IOException {

		System.out.print("jsdebug: DirectoriesHelper.java writeFile(" + filenamePath + ",text) running...\n");

		FileOutputStream output = null;
		try {
			output = new FileOutputStream(filenamePath);
			output.write(text.getBytes());
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	/**
	 * Liest Inhalt einer Web-Abfrage auf www.directories.ch/weisseseiten
	 *
	 * 20101213js added comments:
	 *
	 * @parameter: name, geo - name and location of the person/institution/... to be
	 *             searched for
	 * @return: One string containing the preprocessed response (i.e. the
	 *          interesting portion of the complete html page returned) for a search
	 *          request to tel.local.ch constructed from name and geo.
	 *
	 *          20120713js:
	 *
	 *          Apparently, requests to both tel.local.ch would now return a page
	 *          formatted for WAP, and including references to
	 *          http://mobile.local.c/... when obtained through the elexis plugin.
	 *
	 *          The same url would, however, return a proper www page when obtained
	 *          through mozilla firefox or wget. The problematic point is: I cannot
	 *          see the desired results in the wap version :-( (Even though they
	 *          appear when the download is displayed in a browser again, maybe
	 *          through some javascript construct, or I just missed something.)
	 *          Anyway, the ContentParser of this plugin also does not see anything
	 *          of interest in the WAP page it receives. So I want to set user agent
	 *          for the request to tel.local.ch
	 *
	 *          In order to set a user agent, I need to use a different approach to
	 *          obtain http content, which uses a connection. And yes - as long as I
	 *          set any userAgent string - even if it's empty - that works and I get
	 *          a real WWW page including the desired content.
	 *
	 *          Before, processing of Umlauts and removals of several unwanted tags
	 *          was already done before the page was returned (either added by
	 *          myself or by the original authors).
	 *
	 *          Since 20120713js, I also delete a large bunch of extra information
	 *          immediately, namely everything between ...</title> and
	 *          <div class='summary'>..., before returning the result. This has been
	 *          added to cleanupText() above.
	 *
	 *          It's not really beautiful code that does reading and preprocessing
	 *          in the same method. But after all, obfuscating some 10 addresses in
	 *          45KB of javascript/html is not beautiful either.
	 */
	public static String readContent(final String name, final String geo) throws IOException, MalformedURLException {

		/*
		 * Original code:
		 *
		 * This code did not set a user agent string. And as it did not explicitly use a
		 * connection, I think we couldn't make it do either.
		 *
		 *
		 * System.out.print("jsdebug: DirectoriesHelper.java readContent() running...\n"
		 * );
		 *
		 * URL content = getURL(name, geo);
		 *
		 * System.out.print("jsdebug: DirectoriesHelper.java URL content=");
		 * System.out.print(content.toString()); System.out.print("\n");
		 *
		 * InputStream input = content.openStream();
		 *
		 * StringBuffer sb = new StringBuffer(); int count = 0; char[] c = new
		 * char[10000]; InputStreamReader isr = new InputStreamReader(input); try {
		 * while ((count = isr.read(c)) > 0) { sb.append(c, 0, count); } } finally { if
		 * (input != null) { input.close(); } }
		 *
		 * System.out.print(
		 * "jsdebug: DirectoriesHelper.java readContent().sb.toString():\n --------(html text begin)--------\n"
		 * +sb.toString()+"\njsdebug: --------(html text end)--------\n");
		 * System.out.print(
		 * "jsdebug: DirectoriesHelper.java cleanup...(readContent().sb.toString()):\n --------(html text begin)--------\n"
		 * + cleanupUmlaute(cleanupText(sb.toString()))
		 * +"\njsdebug: --------(html text end)--------\n" );
		 */

		/*
		 * New code by js 20120712 based upon:
		 * http://jawe.net/2006/07/23/setting-the-http-user-agent-in-java/
		 */

		System.out.print("jsdebug: DirectoriesHelper.java readContent() running...\n");

		// It is apparently NOT necessary to change our user agent to Mozilla to avoid
		// getting a wap
		// page.
		// Switching over to the connection based URL reading approach AND setting ANY
		// User-Agent AT
		// ALL (!)
		// does suffice to achieve that.
		//
		// WARNING: userAgent="" works just as well as userAgent="Mozilla/5.0" etc.
		// But: Removing the line "connection.addRequestProperty("User-Agent"...) below
		// returns the unwanted result where we only receive a WAP page! 20120712js

		// String userAgent = "Mozilla/5.0";
		// String userAgent = ""; //receives: <div class='container'>
		String userAgent = "Elexis/js www.jsigle.com/prog/elexis"; // receives: <div
																	// class='container'>

		URL URLcontent = getURL(name, geo);
		URLConnection connection = null;
		InputStream input = null;

		System.out.print("jsdebug: DirectoriesHelper.java userAgent=" + userAgent + "\n");
		System.out.print("jsdebug: DirectoriesHelper.java URLcontent=" + URLcontent.toString() + "\n");

		StringBuffer sb = new StringBuffer();

		try {
			connection = URLcontent.openConnection();
			connection.addRequestProperty("User-Agent", userAgent);
			input = connection.getInputStream();
			// 201207140200js:
			// InputStreamReader isr = new InputStreamReader(input);
			// We need to specify the input stream encoding. The server sends the page using
			// UTF-8 (well, we can see that only after the transmission has begung, but I
			// have indeed checked it). If the plugin runs in eclipse, apparently, the
			// InputStreamReader treats it as such; whereas if the plugin runs in an
			// exported
			// setting (elexis.exe), the InputStreamReader treats incoming data as something
			// else, thereby receiving garbled Umlauts. Which are not fixable later on any
			// more,
			// apparently. In either case, I can read in the debug output that the server
			// sent <meta charset='utf-8'>; and from my experience with other Java
			// applications
			// (maybe even with AnyQuest for Windows / AnyQuest for Java, or around
			// Elexis?),
			// I remember vaguely, that some transcoding occurs actually during the file
			// reading
			// process, wich may be non-reversible, because multiple source characters are
			// transcoded to the same destination character. And the only way to get around
			// that
			// is to ensure that Java knows right what character set the incoming code has.
			// Now, let's put that knowledge to the test right here :-)
			// YEP, that's it. Now it works both inside and outside of Eclipse :-) :-) :-)
			// I also made sure that the encoding of "Stück" labels in various
			// Artikel/Medikament
			// plugins is correct outside Eclipse; after having reset
			// ch.elexis build.properties
			// from javacDefaultEncoding = UTF-8
			// (which achieved correct Umlauts in exported directory search and medikamente
			// plugin before, which I remember, but why, I don't know)
			// to javacDefaultEncoding.. = UTF-8
			InputStreamReader isr = new InputStreamReader(input, "UTF-8"); // 201207140208js
			int count = 0;
			char[] c = new char[10000];

			while ((count = isr.read(c)) > 0) {
				sb.append(c, 0, count);
			}

		} catch (IOException e) {
			// LOG.error("Error reading " + URLcontent, e);
			System.out.print("jsErrorMessage: Error reading: " + URLcontent.toString() + " " + e.toString());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// if (LOG.isWarnEnabled()) {
					// LOG.warn("Error closing input stream: " + url, e);
					// }
					System.out.print("jsErrorMessage: Error closing input stream: " + URLcontent.toString() + " "
							+ e.toString());
				} // catch
			} // if
		} // finally

		System.out.print(
				"jsToDo: DirectoriesHelper.java Change the above error messages so that they appear in the Elexis logs.");

		System.out.print(
				"\n\n\njsdebug: DirectoriesHelper.java readContent().sb.toString():\n --------(html text begin)--------\n"
						+ sb.toString() + "\njsdebug: --------(html text end)--------\n\n\n\n");

		System.out.print(
				"\n\n\njsdebug: DirectoriesHelper.java cleanup...(readContent().sb.toString()):\n --------(html text begin)--------\n"
						+ cleanupUmlaute(cleanupText(sb.toString()))
						+ "\njsdebug: --------(html text end)--------\n\n\n\n");

		return cleanupUmlaute(cleanupText(sb.toString()));
	}
}
