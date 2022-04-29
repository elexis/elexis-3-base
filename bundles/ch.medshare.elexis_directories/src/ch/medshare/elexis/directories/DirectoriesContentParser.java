/*******************************************************************************
 * Portions copyright (c) 2010, 2012 Jörg Sigle www.jsigle.com and portions copyright (c) 2007, medshare and Elexis
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    J. Sigle - 20120712-20120713 www.jsigle.com
 *    			 On about 2012-07-11 (or 2012-07-04?), tel.local.ch vastly changed their delivery formats again.
 *    			   This rendered this plugin completely dysfunctional.
 *    		     Changed http get implementation to use a connection and userAgent setting,
 *     			   otherwise, WAP content would be served that did not directly reveal the desired content.
 *     			 Reviewed, made functional again, and improved pre-processing and parsing steps.
 *               Added a few features regarding filling of the Zusatz field from the list format,
 *                 among others: interpretation of the Categories field unnumbered list entries
 *                 into the field Zusatz, as long as no better information is obtained from a Details entry.
 *               When a single entry is double clicked now in a result list,
 *                 additional / overwriting content is now truly filled in from the resulting served Details entry.
 *               URIencoded e-mail addresses are now decoded and stripped from excess wrapping characters.
 *
 *
 *    J. Sigle - 20101213-20101217 www.jsigle.com
 *    			 Hopefully, all of my changes are marked by a comment that has "js" in it.
 *    			 Adoption of processing of results in ADR_LIST format to changed html content,
 *                 as recently introduced by http://tel.local.ch.
 *               Filling of poBox (and some other fields) made conditional:
 *                 The target is now initialized as an empty string, and only filled from the
 *                 processed html when moveTo("SomeRespectiveTag") was successful.
 *                 Otherwise, some garbage could be read (or persist) in(to) the target
 *                 variable - and, in the case of poBox, if zusatz was empty, that garbage
 *                 would appear up there. Effectively, the user had to clean zusatz
 *                 more often than not e.g. when the address of a health insurance company
 *                 was obtained.
 *               Enhanced removeDirt() to also remove leading and trailing blanks/spaces.
 *                 I found addresses with trailing blanks in the street address field quite often.
 *               Some comments added with regard to program functionality.
 *               Some debug/monitoring output added in internal version only;
 *                 commented out again for published version: //logger.debug("...
 *
 *    			 Suggestion regarding getVornameNachname():
 *                 Maybe the first-name/last-name split should be changed,
 *    			   or an interactive selection of the split point be provided.
 *                 See new comment before the function header.
 *
 *               Suggestion regarding zusatz:
 *                 If a title like Dr. med., PD Dr. med., Prof. is encountered,
 *                 split it off and put it directly into the title field.
 *                 (We use zusatz for a Facharztbezeichnung like Innere Medizin FMH,
 *                  and title for a Titel like Dr. med. etc.)
 *
 *               Suggestion regarding second level search request:
 *                 Problem: When I first search for anne müller in bern,
 *                 and thereafter double click on the first entry, I get data
 *                 in the add-contact-dialog that does NOT include her title (role).
 *                 (with java code revised by myself, which works for other
 *                  müllers returned in the first result list, including their titles).
 *
 *                 Similar problem: Looking for Henzi in Bern finds a whole list of results.
 *                 Double clicking the last (Stefan Henzi) returns another list.
 *                 Some of the entries have the title Dr. med. and some don't -
 *                 Elexis does NOT show the second list for further selection,
 *                 and it apparently evaluates one that doesn't have the title.
 *
 *                 Not similar - but related problem: Looking for Hamacher in Bern
 *                 returns two entries. Double click on Jürg Hamacher in the tel.local.ch
 *                 page in the WWW browser returns a single entry. This detail entry
 *                 (but not the previously shown list entry) includes the title and
 *                 the e-mail address. Neither is transferred to Elexis when double
 *                 clicking on the second list entry.
 *                 But when I search for Jürg Hamacher immediately, the single detailed
 *                 entry is found immediately, and all information from there transferred.
 *
 *
 *                 I'm somewhat unsure whether my assumption regarding what happens
 *                 are correct here (would need to further review the program).
 *                 I currently guess that:
 *
 *                 If a user clicks on one entry from the list returned by an initial search,
 *                 then feed the second level search (which will feed the new kontakt entry dialog)
 *                 with both the name AND the address returned from the first level search.
 *
 *                 (a) The results from the second level search, may be processed, but are
 *                     NOT really used by Elexis. I.e. I see debug output from inside
 *                     extractKontakt(), but I don't see any effects (i.e. changed variable
 *                     content) in the Elexis Kontakt dialog.
 *                     (The Jürg Hamacher example)
 *
 *                 (b) Supplying ONLY the name, will not suffice to get a single-entry result
 *                     e.g. for Anne Müller in Bern, so her title which is available only in the
 *                     detailed result output will be missed.
 *                     (The Anna Müller Stefan Henzi examples)
 *
 *                 Moreover, it is (maybe) shere luck that in this case,
 *                 we have only ONE Anne Müller in Bern in the result list -
 *                 and all others have some additional names. Otherwise, I'm unsure
 *                 whether a result list containing e.g. TWO entries for Max Muster,
 *                 would return the correct one for either case in the second level search...
 *                 (The Stefan Henzi examples, I guess, *might* illustrate just that.)
 *
 *                 I have followed this through to WeiseSeitenSearchForm.java
 *                 openKontaktDialog() where the information from the list_format entry
 *                 (i.e. with empty zusatz, exactly the entry at which we double clicked) is supplied.
 *
 *                 I've also reviewed open.dialog() one step further - but then it becomes too
 *                 much for me for today. From my debugging output, I can clearly see that
 *                 (for the Jürg Hamacher example):
 *
 *                 *after* openKontaktDialog() is called,
 *                 there occurs another call of extractKontakte() (from where?!)
 *                 which calls extractKontakt(),
 *                 which returns all information from the detailed information (),
 *                 which is added as part of a new kontakte.entry at the end of the extractKontakt() loop,
 *                 and apparently ignored thereafter (why that?)
 *
 *                 I'm unsure whether this lasts search/search result processing should not
 *                 better occur *before* the dialog is opened, and its information used to
 *                 feed the dialog. But take care; that multiple dialogs may be opened if
 *                 multiple contacts are selected on the first list, so they all must be
 *                 fed with individual new searches, and the original contact list may not
 *                 be forgotten until the last dialog window so generated has been closed...
 *
 *                 Please look at my extensive Anne Müller related comments below;
 *                 and please note, that Anne Müller's "Zusatz" is not lost because
 *                 I changed some zusatz related lines, but because the second level
 *                 search request apparently returns a list_format result (again),
 *                 which does NOT have a title entry for her and/or because when a
 *                 detailed_format result is returned in a second level search,
 *                 it is processed, but its results are not honoured.
 *
 *                 Please review the output of tel.local.ch for all entries on the
 *                 first anne müller bern search result, and what happens with the
 *                 various titles. Some work, some don't.
 *                 Please also review the Stefan Henzi example case.
 *
 *                 You can easily switch on my debug output:
 *                 look for: //Anne Müller case debug output
 *                 in DirectoriesContentParser.java (this file)
 *                 and WeisseSeitenSearchView.java
 *                 Or just uncomment all occurences of "//System.out.print("jsdebug:"
 *                 in these files (except the one in the line above) with find/replace.
 *
 *                 You can also set the variable zusatz to a fixed value in either
 *                 extractKontakt() or extractListKontakt() functions and see what is used,
 *                 and what is ignored.
 *
 *                 Sorry - for myself, I just don't have the time to review that
 *                 problem in more detail by now today; I also think it's of secondary
 *                 importance compared to the restoration of the first level search
 *                 function in general.
 *
 *    M. Imhof - initial implementation
 *
 * $Id: DirectoriesContentParser.java 5277 2009-05-05 19:00:19Z tschaller $
 *******************************************************************************/

package ch.medshare.elexis.directories;

import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException; //20120713js
import java.net.MalformedURLException;
import java.net.URLDecoder; //20120713js
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.util.SWTHelper;

/**
 *
 * @author jsigle (comment and 20101213, 20120712 update only)
 *
 *         The service http://tel.local.ch provides a user-interface for WWW
 *         browsers with a lot of additional content around the desired
 *         address/contact information. The address/contact information is
 *         extracted from this material and returned in variable fields suitable
 *         for further usage by Elexis.
 *
 *         If a search request returns multiple results, these appear in (what
 *         we call) "ADR_LIST" format. If a search request returns exactly one
 *         result (or one entry from the list is clicked at in the WWW browser),
 *         the result appears in (what we call) "ADR_DETAIL" format.
 *
 *         By 12/2010, a change in the output format of tel.local.ch required
 *         new marker strings for processing. The processing of a result in
 *         ADR_DETAIL format continued to work, But the processing of a result
 *         in ADR_LIST format would deliver an empty result.
 *
 *         By 2012-07-11, the plugin stopped working again.
 *
 *         Please note that there is relaunch related info on
 *         www.directoriesdata.ch www.local.ch shall apparently be split into a
 *         free consumer site, and a commercial counterpart.
 *
 *         Also note that neither &mode=text nor &range=all do currently change
 *         the result which I observe.
 *
 *
 */
public class DirectoriesContentParser extends HtmlParser {

	private static final String ADR_LISTENTRY_TAG = "<div class='row local-listing'"; //$NON-NLS-1$
	private static final String ADR_SINGLEDETAILENTRY_TAG = "<div class='eight columns details'";; //$NON-NLS-1$
	private static String metaPLZTrunc = StringUtils.EMPTY;
	private static String metaOrtTrunc = StringUtils.EMPTY;
	private static String metaStrasseTrunc = StringUtils.EMPTY;
	private final Logger logger = LoggerFactory.getLogger("ch.medshare.elexis_directories");

	public DirectoriesContentParser(String htmlText) {
		super(htmlText);
	}

	/**
	 * Retourniert String in umgekehrter Reihenfolge
	 */
	private String reverseString(String text) {
		if (text == null) {
			return StringUtils.EMPTY;
		}
		String reversed = StringUtils.EMPTY;
		for (char c : text.toCharArray()) {
			reversed = c + reversed;
		}
		return reversed;
	}

	/**
	 * This splits the provided string at the first contained space. This is not
	 * optimal for all cases: Persons may have multiple given names / christian
	 * names, and they will very often be separated just by spaces. I actually
	 * observed in real life usage that a second given name went to the
	 * "Name"="Nachname"="Family name" field together with the true family name. It
	 * might be better to split the name at the *last* contained space, because
	 * multiple family names are usually linked by a dash (-), rather than separated
	 * by a space (this is my personal impression). However, I haven't changed the
	 * code so far.
	 */
	private String[] getVornameNachname(String text) {
		String vorname = StringUtils.EMPTY;
		String nachname = text;
		int nameEndIndex = text.trim().indexOf(StringUtils.SPACE);
		if (nameEndIndex > 0) {
			vorname = text.trim().substring(nameEndIndex).trim();
			nachname = text.trim().substring(0, nameEndIndex).trim();
		}
		return new String[] { vorname, nachname };
	}

	/**
	 * remove leading and trailing whitespace characters
	 *
	 * @param text
	 * @return
	 */
	private String removeDirt(String text) {
		text = text.replaceAll("^+\\s", StringUtils.EMPTY);
		text = text.replaceAll("\\s+$", StringUtils.EMPTY);

		return text.replace("<span class=\"highlight\">", StringUtils.EMPTY).replace("</span>", StringUtils.EMPTY);
	}

	/**
	 * Informationen zur Suche werden extrahiert.
	 *
	 */
	public String getSearchInfo() {
		reset();

		logger.debug("DirectoriesContentParser.java: getSearchInfo() running...\n");
		logger.debug("Beginning of substrate: <" + extract("<", ">") + "...\n");

		String searchInfoText = extract("<title>", "</title>");

		if (searchInfoText == null) {
			return StringUtils.EMPTY;
		}

		logger.debug("DirectoriesContentParser.java: getSearchInfo(): searchInfoText != null\n");
		logger.debug("DirectoriesContentParser.java: getSearchInfo(): \"" + searchInfoText + "\"\n\n");

		return searchInfoText.replace("<strong class=\"what\">", StringUtils.EMPTY)
				.replace("<strong class=\"where\">", StringUtils.EMPTY) // $NON-NLS-3$
				.replace("<strong>", StringUtils.EMPTY).replace("</strong>", StringUtils.EMPTY).trim();
	}

	/**
	 *
	 * Extrahiert Informationen aus dem retournierten Html. Anhand der
	 * <div class="xxx"> kann entschieden werden, ob es sich um eine Liste oder
	 * einen Detaileintrag (mit Telefon handelt).
	 *
	 * Detaileinträge: "adrNameDetLev0", "adrNameDetLev1", "adrNameDetLev3" Nur
	 * Detaileintrag "adrNameDetLev2" darf nicht extrahiert werden
	 *
	 * Listeinträge: "adrListLev0", "adrListLev1", "adrListLev3" Nur Listeintrag
	 * "adrListLev0Cat" darf nicht extrahiert werden
	 *
	 */
	public List<KontaktEntry> extractKontakte() throws IOException {
		reset();

		logger.debug("DirectoriesContentParser.java: extractKontakte() running...\n");
		logger.debug("Beginning of substrate: <" + extract("<", ">") + "...\n");

		if (getNextPos("<meta content='Adresse von ") > 0) {
			logger.debug(
					"Processing a <meta> field to help processing the 'details' field later on which is very unstructured after 20131124...\n");
			moveTo("<meta content='Adresse von ");
			metaStrasseTrunc = removeDirt(extract("Strasse: ", ",")).replaceAll("[^A-Za-z0-9]", StringUtils.EMPTY); //$NON-NLS-1$
			// //20131127js
			metaPLZTrunc = removeDirt(extract("PLZ: ", ",")).replaceAll("[^A-Za-z0-9]", StringUtils.EMPTY); //$NON-NLS-1$
			// //20131127js
			metaOrtTrunc = removeDirt(extract("Ort: ", ",")).replaceAll("[^A-Za-z0-9]", StringUtils.EMPTY); //$NON-NLS-1$
			// //20131127js
			if (metaStrasseTrunc == null)
				logger.debug("WARNING: metaStrasseTrunc == null\n");
			else
				logger.debug("metaStrasseTrunc == " + metaStrasseTrunc + StringUtils.LF);
			if (metaPLZTrunc == null)
				logger.debug("WARNING: metaPLZTrunc == null\n");
			else
				logger.debug("metaPLZTrunc == " + metaPLZTrunc + StringUtils.LF);
			if (metaOrtTrunc == null)
				logger.debug("WARNING: metaOrtTrunc == null\n");
			else
				logger.debug("metaOrtTrunc == " + metaOrtTrunc + StringUtils.LF);
		}
		;

		List<KontaktEntry> kontakte = new Vector<KontaktEntry>();

		int listIndex = getNextPos(ADR_LISTENTRY_TAG);
		int detailIndex = getNextPos(ADR_SINGLEDETAILENTRY_TAG);

		logger.debug("DirectoriesContentParser.java: extractKontakte() initial values of...\n");
		logger.debug("DirectoriesContentParser.java: extractKontakte().listIndex: " + listIndex + StringUtils.LF);
		logger.debug("DirectoriesContentParser.java: extractKontakte().detailIndex: " + detailIndex + StringUtils.LF);

		while (listIndex > 0 || detailIndex > 0) {
			KontaktEntry entry = null;

			logger.debug("DirectoriesContentParser.java: extractKontakte() intraloop values of...\n");
			logger.debug("DirectoriesContentParser.java: extractKontakte().listIndex: " + listIndex + StringUtils.LF);
			logger.debug(
					"DirectoriesContentParser.java: extractKontakte().detailIndex: " + detailIndex + StringUtils.LF);

			if (detailIndex < 0 || (listIndex >= 0 && listIndex < detailIndex)) {
				// Parsing Liste
				logger.debug("DirectoriesContentParser.java: Parsing Liste:\n");
				entry = extractListKontakt();
			} else if (listIndex < 0 || (detailIndex >= 0 && detailIndex < listIndex)) {
				// Parsing Einzeladresse
				logger.debug("DirectoriesContentParser.java: Parsing Einzeladresse:\n");
				entry = extractKontakt();
			}

			if (entry != null) {
				logger.debug("DirectoriesContentParser.java: entry: " + entry.toString() + StringUtils.LF);
			} else {
				logger.debug("DirectoriesContentParser.java: entry: NULL\n");
			}

			if (entry != null) {
				kontakte.add(entry);
			}
			listIndex = getNextPos(ADR_LISTENTRY_TAG);
			detailIndex = getNextPos(ADR_SINGLEDETAILENTRY_TAG);
		}

		return kontakte;
	}

	/**
	 * Extrahiert einen Kontakt aus einem Listeintrag
	 *
	 * Please note (!) that the
	 * <li class="detail">tag is a part of the ADR_LIST display type now, which
	 * could be confusing to other people reviewing this code... And please note
	 * that one entry does NOT begin with the <li class "detail"> tag, but (quite
	 * probably, I've not perfectly reviewed it) with the <div data-slot="... tag.
	 *
	 * Please also note that the address/contact details seem to be included in
	 * either of the data carrying lines.
	 *
	 *
	 */
	private KontaktEntry extractListKontakt() throws IOException, MalformedURLException {

		logger.debug("DirectoriesContentParser.java: extractListKontakt() running...\n");
		logger.debug("Beginning of substrate: <" + extract("<", ">") + "...\n");

		if (!moveTo(ADR_LISTENTRY_TAG)) { // Kein neuer Eintrag
			return null;
		}

		logger.debug("DirectoriesContentParser.java: extractListKontakt() extracting next entry...\n");

		int nextEntryPoxIndex = getNextPos(ADR_LISTENTRY_TAG); // 20120712js

		logger.debug("DirectoriesContentParser.java: extractListKontakt() nextEntryPoxIndex: " + nextEntryPoxIndex
				+ StringUtils.LF);

		logger.debug(
				"DirectoriesContentParser.java: Shouldn't the \\\" in the following line and similar ones throughout this file be changed to a simple ' ???\n");
		moveTo("<h2><a href=\"http://tel.local.ch/"); // 20131127js
		String nameVornameText = extract("\">", "</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //20120712js

		nameVornameText = removeDirt(nameVornameText);

		if (nameVornameText == null || nameVornameText.length() == 0) { // Keine
																		// leeren
																		// Inhalte
			return null;
		}
		String[] vornameNachname = getVornameNachname(nameVornameText);
		String vorname = vornameNachname[0];
		String nachname = vornameNachname[1];

		// Anne Müller case debug output:
		logger.debug("DirectoriesContentParser.java: extractListKontakt() nameVornameText: " + nameVornameText
				+ StringUtils.LF);
		logger.debug(
				"DirectoriesContentParser.java: extractListKontakt() Possibly add better processing of a successor to role/categories/profession fields here as well, see comments above.\n");

		String zusatz = StringUtils.EMPTY; // 20120712js

		// This is obviously much worse structured XML in a technical sense.
		// It's all direct layout control, rather than providing logically structured
		// content and letting the browser do the formatting etc.
		// Anyway - we would extract the single or multiple entries from a single line
		// (!) into a single line for Elexis field "Zusatz" like this:
		int catIndex = getNextPos("<span class='categories'>"); // 20131127js:
		if (catIndex > 0 && ((catIndex < nextEntryPoxIndex) || nextEntryPoxIndex == -1)) { // 20120712js
			moveTo("<span class='categories'>"); // 20131127js:
			zusatz = extractTo("</span>"); // 20131127js:
			zusatz = zusatz.replaceAll("&nbsp;&bull;&nbsp;", ", "); // 20131127js:
		} // 20120712js
			// I don't want to use a bullet instead of the comma, because I this may be much
			// more error prone, as it depends on suitable character sets/encodings etc.
			// One drawback (but this was there before) is that the "Gemeinschaftspraxis,
			// ..." from the address field returned for anne müller and separated further
			// below
			// will be separated by a dash (done by code below), so this is an
			// inconsistency. On the other hand, this was there before, and it's also
			// information retrieved from another source.
			// So: may that remain like that for now.
			// This update gets us the categories information into the Zusatz field in the
			// single result that appears after dblclick on one entry from the tabulated
			// results.

		// Anne Müller case debug output:
		logger.debug("DirectoriesContentParser.java: extractListKontakt() catIndex: " + catIndex + StringUtils.LF);
		logger.debug("DirectoriesContentParser.java: extractListKontakt() zusatz: \"" + zusatz + "\"\n\n");

		String adressTxt = extract("<span class='address'>", "</span>"); // 20131127js
		// This update gets us address (street, number, zip, city) into both the
		// tabulated results, and the single result that appears after dblclick on one
		// entry from the tabulated results.
		// (But still not the Fax number, that should be extracted from what appears if
		// we click on "Details" in the local.ch tabulated results page.
		// The Fax number is *not* contained in the tabulated result for multiple hits
		// on local.ch, so that will be extracted later on.)

		logger.debug("DirectoriesContentParser.java: extractListKontakt().addressTxt:\n" + adressTxt + "\n\n");

		// As of 20120712js, the format of adressTxt is now simply like:
		// "Musterstrasse 12, 3047 Bremgarten b. Bern" (test case: search for: hamacher,
		// bern)
		// "Gemeinschaftspraxis, Monbijoustrasse 124, 3007 Bern" (test case: search for:
		// anne
		// müller, bern)
		String strasse = StringUtils.EMPTY;
		String plz = StringUtils.EMPTY;
		String ort = StringUtils.EMPTY;
		if (adressTxt.contains(", ")) {
			// Use lastIndexOf() to separate only PLZ Ort, no matter how many comma separted
			// entries
			// precede it.
			int CommaPos = adressTxt.lastIndexOf(", ");
			if (CommaPos > -1) {
				strasse = removeDirt(adressTxt.substring(0, CommaPos));
				int SpacePos = adressTxt.indexOf(StringUtils.SPACE, CommaPos + 2);
				if (SpacePos > -1) {
					plz = removeDirt(adressTxt.substring(CommaPos + 2, SpacePos));
					ort = removeDirt(adressTxt.substring(SpacePos + 1));
				} else {
					ort = removeDirt(adressTxt.substring(CommaPos + 2));
				}
			} else {
				ort = removeDirt(adressTxt);
			}
		}
		if (strasse != StringUtils.EMPTY) {
			int CommaPos = strasse.lastIndexOf(", ");
			if (CommaPos > -1) {
				if (zusatz == StringUtils.EMPTY) {
					zusatz = removeDirt(strasse.substring(0, CommaPos));
				} else {
					// Puts the new one to the end of the old one:
					// zusatz = zusatz.concat(" - "+removeDirt(strasse.substring(0,CommaPos)));
					// Puts the new one to the beginning of the old one:
					zusatz = removeDirt(strasse.substring(0, CommaPos)) + " - " + zusatz;
				}
				strasse = removeDirt(strasse.substring(CommaPos + 2));
			}
		}
		// 20120712js We want to parse the phone number also for the last entry in the
		// list,
		// where nextEntryPoxIndex will already be -1 (!).
		// You can test that with meier, bern, or hamacher, bern.
		String telNr = StringUtils.EMPTY; // 20120712js
		int phonePos = (getNextPos("<span class='phone'")); // 20120712js
		if (phonePos >= 0 && ((phonePos < nextEntryPoxIndex) || nextEntryPoxIndex == -1)) { // 20120712js
			moveTo("<span class='phone'"); // 20120712js
			moveTo("<label>Telefon"); // 20120712js
			// 20120713js Don't use "refuse number" but only "number" - the "refuse " is
			// probably
			// only there
			// for people who don't want to get called for advertising or a similar thing;
			// there is
			// a matching
			// note on the individual Details entries; and probably an asterisk displayed
			// left of
			// the phone number
			// in the List format output.
			moveTo("number\""); // 20120712js
			telNr = extract(">", "</").replace("&nbsp;", StringUtils.EMPTY).replace("*", StringUtils.EMPTY).trim(); // 20120712js
		} // 20120712js

		// 20120713js: Please note: Fax and E-mail are NOT available in the List format
		// result
		// 20131127js: And this is still the case in the next revision after
		// 20131124js...
		return new KontaktEntry(vorname, nachname, zusatz, // $NON-NLS-1$
				strasse, plz, ort, telNr, StringUtils.EMPTY, StringUtils.EMPTY, false);
	}

	/**
	 * Decodes the passed UTF-8 String using an algorithm that's compatible with
	 * JavaScript's <code>decodeURIComponent</code> function. Returns
	 * <code>null</code> if the String is <code>null</code>.
	 *
	 * From: Utility class for JavaScript compatible UTF-8 encoding and decoding.
	 *
	 * @see http
	 *      ://stackoverflow.com/questions/607176/java-equivalent-to-javascripts-encodeuricomponent
	 *      -that-produces-identical-output
	 * @author John Topley
	 *
	 * @param s The UTF-8 encoded String to be decoded
	 * @return the decoded String
	 */
	public static String decodeURIComponent(String s) {
		if (s == null) {
			return null;
		}
		String result = null;
		try {
			result = URLDecoder.decode(s, "UTF-8");
		}
		// This exception should never occur.
		catch (UnsupportedEncodingException e) {
			result = s;
		}
		return result;
	}

	/**
	 * Extrahiert einen Kontakt aus einem Detaileintrag
	 *
	 * A result of this type should be obtainable by searching for: Wer, Was:
	 * eggimann meier Wo: bern
	 *
	 *
	 */
	private KontaktEntry extractKontakt() {

		logger.debug("DirectoriesContentParser.java: extractKontakt() running...\n");
		logger.debug("Beginning of substrate: <" + extract("<", ">") + "...\n");

		if (!moveTo(ADR_SINGLEDETAILENTRY_TAG)) { // Kein neuer Eintrag
			return null;
		}

		logger.debug("DirectoriesContentParser.java: extractKontakt() extracting next entry...\n");

		// 20120712js: Title: This field appears before fn; it is not being
		// processed so far.
		logger.debug(
				"DirectoriesContentParser.java: extractKontakt() Add processing of the class='title', class='urls', and optionally class='region'. \n");

		// Wegen des hinzugefügten loops für ggf. mehrere Adressen auch im
		// Detailergebnis: Variablen hier vorab definiert,
		// damit sie später bei return ausserhalb des loops noch sichtbar sind.
		String vorname = StringUtils.EMPTY;
		String nachname = StringUtils.EMPTY;

		String streetAddress = StringUtils.EMPTY;
		String poBox = StringUtils.EMPTY;
		String plzCode = StringUtils.EMPTY; // 20120712js
		String ort = StringUtils.EMPTY; // 20120712js

		String zusatz = StringUtils.EMPTY;
		String tel = StringUtils.EMPTY; // 20120712js
		String fax = StringUtils.EMPTY; // 20120712js
		String email = StringUtils.EMPTY; // 20120712js

		Boolean doItOnceMore = true;
		while (doItOnceMore) { // 20120712js
			moveTo("<h4 class='name fn'");
			String nameVornameText = extract(">", "</h4>"); // 20120712js

			logger.debug(
					"DirectoriesContentParser.java: extractKontakt().nameVornameText: \"" + nameVornameText + "\"\n");

			if (nameVornameText == null || nameVornameText.length() == 0) { // Keine leeren Inhalte
				return null;
			}
			String[] vornameNachname = getVornameNachname(nameVornameText);

			if (vorname.equals(StringUtils.EMPTY)) {
				vorname = vornameNachname[0];
				nachname = vornameNachname[1];
			} else {

				// Das hier ist vielleicht besser, wenn's geht:
				nachname = nachname + StringUtils.SPACE + vorname;
				vorname = vornameNachname[1] + StringUtils.SPACE + vornameNachname[0];
			}

			// Anne Müller case debug output:
			logger.debug("DirectoriesContentParser.java: extractKontakt() nameVornameText: " + nameVornameText
					+ StringUtils.LF);

			if (moveTo("<div class='profession'>")) { // 20120712js
				zusatz = extractTo("</div>"); // 20120712js
			}

			// 20131127js: Replace something like "Dr. med. PD" by "PD Dr. med."
			if (zusatz != null) {
				zusatz = zusatz.replace("Dr. med. PD", "PD Dr. med.");
				zusatz = zusatz.replace("Dr. med. Prof.", "Prof. Dr. med.");
			}

			// Anne Müller case debug output:
			logger.debug("DirectoriesContentParser.java: extractKontakt() zusatz: \"" + zusatz + "\"\n\n");

			String adressTxt = extract("<p class='address'>", "</p>").trim(); // 20120712js

			// Anne Müller, Bern or Eggimann Meier, Bern case debug output:
			logger.debug("DirectoriesContentParser.java: adressTxt: " + adressTxt + StringUtils.LF);

			// HtmlParser parser = new HtmlParser(adressTxt);

			String[] addressLines = adressTxt.split("<br/>");

			// String streetAddress = removeDirt(parser.extract("<span
			// class=\"street-address\">",
			// "</span>")); // 20120712js:
			logger.debug("Trying to use Meta-Info collected above to parse the address content...\n");
			if (metaStrasseTrunc == null)
				logger.debug("WARNING: metaStrasseTrunc == null\n");
			else
				logger.debug("metaStrasseTrunc == " + metaStrasseTrunc + StringUtils.LF);
			if (metaPLZTrunc == null)
				logger.debug("WARNING: metaPLZTrunc == null\n");
			else
				logger.debug("metaPLZTrunc == " + metaPLZTrunc + StringUtils.LF);
			if (metaOrtTrunc == null)
				logger.debug("WARNING: metaOrtTrunc == null\n");
			else
				logger.debug("metaOrtTrunc == " + metaOrtTrunc + StringUtils.LF);
			for (String thisLine : addressLines) {
				if (thisLine != null) {
					thisLine = thisLine.trim();
				}
				; // especially remove leading and trailing newlines.
				if (thisLine == null)
					logger.debug("WARNING: thisLine == null\n");
				else {
					logger.debug("thisLine == " + thisLine + StringUtils.LF);
					if (thisLine.startsWith(metaStrasseTrunc)) {
						streetAddress = removeDirt(thisLine);
					}
					if (thisLine.startsWith(metaPLZTrunc)) {
						int i = thisLine.indexOf(StringUtils.SPACE);
						plzCode = removeDirt(thisLine.substring(0, i));
						ort = removeDirt(thisLine.substring(i + 1));
					}
				}
			}

			// 20131127js:
			// Jetzt ggf. noch die Zeilen auf poBox auswerten - dazu gibt's keinen Hint aus
			// der MetaInfo:
			// Falls eine Zeile "Postfach" oder "Postfach..." gefunden wird, diese nach
			// poBoxA tun.
			String poBoxA = StringUtils.EMPTY;
			String poBoxB = StringUtils.EMPTY;
			for (String thisLine : addressLines) {
				if (thisLine != null) {
					thisLine = thisLine.trim();
				}
				; // especially remove leading and trailing newlines.
				if (thisLine == null)
					logger.debug("WARNING: thisLine == null\n");
				else {
					logger.debug("thisLine == " + thisLine + StringUtils.LF);
					if (thisLine.startsWith("Postfach")) {
						poBoxA = removeDirt(thisLine);
					}
				}
			}
			// dürfte das wohl der (vom schon verarbeiteten PLZ Ort der Strassenadresse
			// abweichende) PLZ Ort von PoBox sein.
			// Diesen dann bitte mit Komma Leerzeichen getrennt an den Eintrag der poBox
			// anhängen.
			if (poBoxA != StringUtils.EMPTY) {
				for (String thisLine : addressLines) {
					if (thisLine != null) {
						thisLine = thisLine.trim();
					}
					; // especially remove leading and trailing newlines.
					if (thisLine == null)
						logger.debug("WARNING: thisLine == null\n");
					else {
						logger.debug("thisLine == " + thisLine + StringUtils.LF);
						if (thisLine.contains(metaOrtTrunc) && (!thisLine.startsWith(metaPLZTrunc))) {
							poBoxB = thisLine;
						}
					}
				}
			}
			if (poBoxB.equals(StringUtils.EMPTY)) {
				poBox = poBoxA;
			} else {
				poBox = poBoxA + ", " + poBoxB;
			}
			;

			// 20131127js:
			// Debug output zeigt, was herausgekommen ist:
			if (streetAddress == null)
				logger.debug("WARNING: streetAddress == null\n");
			else
				logger.debug("streetAddress == " + streetAddress + StringUtils.LF);
			if (poBox == null)
				logger.debug("WARNING: poBox == null\n");
			else
				logger.debug("poBox == " + poBox + StringUtils.LF);
			if (plzCode == null)
				logger.debug("WARNING: plzCode == null\n");
			else
				logger.debug("plzCode == " + plzCode + StringUtils.LF);
			if (ort == null)
				logger.debug("WARNING: ort == null\n");
			else
				logger.debug("ort == " + ort + StringUtils.LF);

			// If zusatz is empty, then we copy the content of poBox into zusatz.
			if (zusatz == null || zusatz.length() == 0) {
				zusatz = poBox;
			}

			// Tel/Fax & Email

			if (moveTo("<tr class='phone'>")) { // 20120712js
				if (moveTo("<span>\nTelefon:")) { // 20131127js
					if (moveTo("href=\"tel:")) { // 20131127js
						// 20120713js Don't use "refuse number" but only "number" - the "refuse " is
						// probably only there
						// for people who don't want to get called for advertising or a similar thing;
						// there
						// is a matching
						// note on the individual Details entries; and probably an asterisk displayed
						// left
						// of the phone number
						// in the List format output.
						moveTo("number\""); // 20120712js
						tel = extract(">", "</").replace("&nbsp;", StringUtils.EMPTY).replace("*", StringUtils.EMPTY)
								.trim(); // 20120712js
					}
				}
			}
			if (moveTo("<tr class='fax'>")) { // 20120712js

				// if (moveTo("<th class='label'>\nFax:")) { // 20120712js
				if (moveTo("<span>\nFax:")) { // 20131127js

					moveTo("number'"); // 20120712js
					fax = extract(">", "</").replace("&nbsp;", StringUtils.EMPTY).replace("*", StringUtils.EMPTY)
							.trim(); // 20120712js
				}
			}
			logger.debug("jsdebug: Trying to parse e-mail...\n");
			if (moveTo("<div class='email'")) { // 20131127js
				// Here we also accumulate results from multiple address entries per single
				// result, if available; This time, separated by ;
				// If desired (or a user who does not know better uses that), it can be entered
				// directly into several mail clients and will cause a message to be sent to
				// each of the contained addresses.
				if (email.equals(StringUtils.EMPTY)) {
					email = extract("href=\"mailto:", "\">").trim();
				} else {
					email = email + "; " + extract("href=\"mailto:", "\">").trim();
				}
			}
			// }

			doItOnceMore = (getNextPos("<h4 class='name fn'") > 0);
			if (doItOnceMore) {
				SWTHelper.showInfo("Warnung",
						"Dieser eine Eintrag liefert gleich mehrere Adressen.\n\nBitte führen Sie selbst eine Suche im WWW auf tel.local.ch durch,\num alle Angaben zu sehen.\n\nIch versuche, für die Namen die Informationen sinnvoll zusammenzufügen;\nfür die Adressdaten bleibt von mehreren Einträgen der letzte bestehen.\n\nFalls Sie eine Verbesserung benötigen, fragen Sie bitte\njoerg.sigle@jsigle.com - Danke!");
			}
		}

		return new KontaktEntry(vorname, nachname, zusatz, streetAddress, plzCode, ort, tel, fax, email, true);
	}
}
