/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.Log;

/**
 * Der ICDImporter importiert den ICD-Code, wie er beispielsweise von der WHO bereitgestellt wird.
 * Die "EDV-Fassung" kann z.B. hier bezogen werden: http://www
 * .dimdi.de/dynamic/de/klassi/downloadcenter/icd-10-who/version2006/systematik Sie benötigen die
 * ICD-10-WHO 2006 Systematik EDV-Fassung ASCII (x1vea2006.zip) und die ICD-10-WHO 2006 Systematik
 * Metadaten ASCII (x1vma2006.zip) Entpacken Sie beide in dieselbe Ebene desselben Verzeichnisses.
 * (Die "liesmich.txt"-Datei wird dabei überschrieben, da sie in beiden Archiven vorhanden ist. Sie
 * können die entsprechende Warung ignorieren)
 * 
 * Dieser Importer liest aus den Metadaten "codes.txt" die einzelnen ICD-Ziffern zeilenweise ein und
 * baut die kompletten Codes mithilfe der KAPxx-.asc - Kapiteldateien aus der Systematik auf.
 * 
 * Aufbau von codes.txt: Feld 1 : Klassifikationsebene, 1 Zeichen 3 = Dreisteller 4 = Viersteller 5
 * = Fünfsteller Feld 2 : Ort der Schlüsselnummer im Klassifikationsbaum, 1 Zeichen T = terminale
 * Schlüsselnummer (kodierbarer Endpunkt) N = nichtterminale Schlüsselnummer (kein kodierbarer
 * Endpunkt) Feld 3 : Generiert? X: explizit in der Klassifikation aufgeführt S: über
 * Subklassifikation generiert Feld 4 : Art der Viersteller X = explizit aufgeführt (präkombiniert)
 * S = per Subklassifikation (postkomibiniert) Feld 5 : Kapitelnummer, 2 Zeichen Feld 6 : erster
 * Dreisteller der Gruppe, 3 Zeichen Feld 7 : Dreisteller zur Schlüsselnummer, 3 Zeichen Feld 8 :
 * Schlüsselnummer ohne eventuelles Kreuzchen, bis zu 7 Zeichen Feld 9 : Schlüsselnummer ohne
 * Strich, Sternchen und Ausrufezeichen, bis zu 6 Zeichen Feld 10: Schlüsselnummer ohne Punkt,
 * Strich, Sternchen und Ausrufezeichen, bis zu 5 Zeichen Feld 11: Klassentitel, bis zu 255 Zeichen
 * Feld 12: Bezug zur Mortalitätsliste 1 Feld 13: Bezug zur Mortalitätsliste 2 Feld 14: Bezug zur
 * Mortalitätsliste 3 Feld 15: Bezug zur Mortalitätsliste 4 Feld 16: Bezug zur Morbiditätsliste Feld
 * 17: Geschlechtsbezug der Schlüsselnummer 9 = kein Geschlechtsbezug M = maennlich W = weiblich
 * Feld 18: Art des Fehlers bei Geschlechtsbezug 9 = irrelevant M = Muß-Fehler K = Kann-Fehler Feld
 * 19: untere Altersgrenze für eine Schlüsselnummer 999 = irrelevant 000 = bis unter 1 Tag 001-006 =
 * 1 Tag bis unter 7 Tage 011-013 = 7 Tage bis unter 28 Tage 101-111 = 28 Tage bis unter 1 Jahr
 * 201-299 = 1 Jahr bis unter 100 Jahre 300-324 = 100 Jahre bis unter 124 Jahre Feld 20: obere
 * Altersgrenze für eine Schlüsselnummer wie bei Feld 19 Feld 21: Art des Fehlers bei Altersbezug 9
 * = irrelevant M = Muß-Fehler K = Kann-Fehler Feld 22: Krankheit in Mitteleuropa sehr selten? J =
 * Ja (--> Kann-Fehler auslösen!) N = Nein Feld 23: Schlüsselnummer als Grundleiden in der
 * Todesursachenkodierung zugelassen? J = Ja M = Nein
 * 
 * 
 * @author gerry
 * 
 */
public class ICDImporter extends ImporterPage {
	DirectoryBasedImporter dbi;
	private Pattern pat_group;
	// private final static String regex_chapter=".*Kapitel +([IV]+).*";
	private final static String regex_group = "([A-Z][0-9][0-9]-[A-Z][0-9][0-9])(.+?):"; //$NON-NLS-1$
	static Log log = Log.get("ICD Import"); //$NON-NLS-1$
	
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception{
		monitor.beginTask(Messages.ICDImporter_icdImport, 15000);
		monitor.subTask(Messages.ICDImporter_createTable);
		ICD10.initialize();
		monitor.worked(500);
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		monitor.subTask(Messages.ICDImporter_readCodes);
		pat_group = Pattern.compile(regex_group, Pattern.CASE_INSENSITIVE);
		File codes = new File(results[0] + File.separator + "codes.txt"); //$NON-NLS-1$
		InputStreamReader ir = new InputStreamReader(new FileInputStream(codes), "iso-8859-1"); //$NON-NLS-1$
		BufferedReader br = new BufferedReader(ir);
		String in;
		ICD10 node;
		Hashtable<String, ICD10> chapters = new Hashtable<String, ICD10>();
		Hashtable<String, ICD10> groups = new Hashtable<String, ICD10>();
		Hashtable<String, ICD10> supercodes = new Hashtable<String, ICD10>();
		while ((in = br.readLine()) != null) {
			String[] fields = in.split(";"); //$NON-NLS-1$
			String kap = fields[ICD10.CHAPTER];
			String group = fields[ICD10.GROUP];
			String supercode = fields[ICD10.SUPERCODE];
			ICD10 kapitel = chapters.get(kap);
			if (kapitel == null) { // Neues Kapitel anlegen
				kapitel =
					new ICD10(
						"NIL", kap, "1;N;X;X;" + kap + ";" + kap + ";" + kap + ";" + kap + ";" + kap + ";" + kap + ";Kapitel " + kap + ";;;;;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
				chapters.put(kap, kapitel);
				LineFeeder lf = new LineFeeder(results[0] + File.separator + "KAP" + kap + ".asc"); //$NON-NLS-1$ //$NON-NLS-2$
				while (lf.peek() == '0') {
					String t = lf.nextLine();
					switch (t.charAt(1)) {
					case 'T':
						kapitel.set("Text", t.substring(2).replaceAll("\n", " - "));break; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					case 'I':
						kapitel.setExt("Incl:", t.substring(2));break; //$NON-NLS-1$
					case 'E':
						kapitel.setExt("Excl:", t.substring(2));break; //$NON-NLS-1$
					case 'G':
						String gruppen = t.substring(2).replaceAll("\n", "::"); //$NON-NLS-1$ //$NON-NLS-2$
						kapitel.setExt("Gruppen", t.substring(2)); //$NON-NLS-1$
						Matcher match = pat_group.matcher(gruppen + "::"); //$NON-NLS-1$
						while (match.find() == true) {
							String code = match.group(1);
							String[] gs = code.split("-"); //$NON-NLS-1$
							if (groups.get(gs[0]) == null) {
								ICD10 Group =
									new ICD10(
										kapitel.getId(),
										code,
										"1;N;X;X;" + kap + ";" + code + ";" + gs[0] + ";" + gs[0] + ";" + code + ";" + code + ";" + match.group(2)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
								groups.put(gs[0], Group);
							}
						}
						break;
					default:
						kapitel.setExt("Zusatz", t.substring(2)); //$NON-NLS-1$
					}
				}
				
			}
			ICD10 mygroup = groups.get(group);
			if (mygroup == null) {
				node = new ICD10(kapitel.getId(), fields[ICD10.CODE], in);
			} else {
				if (fields[0].equals("3")) { //$NON-NLS-1$
					node = new ICD10(mygroup.getId(), fields[ICD10.CODE], in);
					supercodes.put(fields[ICD10.CODE], node);
				} else {
					ICD10 icdSuper = supercodes.get(supercode);
					if (icdSuper == null) {
						node = new ICD10(mygroup.getId(), fields[ICD10.CODE], in);
					} else {
						node = new ICD10(icdSuper.getId(), fields[ICD10.CODE], in);
					}
				}
			}
			
			monitor.worked(1);
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			node = null;
			kap = null;
		}
		ElexisEventDispatcher.reload(ICD10.class);
		return Status.OK_STATUS;
	}
	
	@Override
	public String getTitle(){
		return "ICD-10"; //$NON-NLS-1$
	}
	
	@Override
	public Composite createPage(Composite parent){
		dbi = new ImporterPage.DirectoryBasedImporter(parent, this);
		dbi.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return dbi;
	}
	
	@Override
	public String getDescription(){
		return Messages.ICDImporter_enterDirectory;
	}
	
	class LineFeeder {
		String prev;
		BufferedReader br;
		
		LineFeeder(String fname) throws Exception{
			br =
				new BufferedReader(new InputStreamReader(new FileInputStream(fname), "iso-8859-1")); //$NON-NLS-1$
			prev = br.readLine();
		}
		
		char peek(){
			return prev.charAt(0);
		}
		
		String nextLine() throws Exception{
			if (prev == null) {
				return null;
			}
			String ret = prev;
			prev = br.readLine();
			if (prev == null) {
				br.close();
				return ret;
			}
			while (prev.startsWith(" ") || (prev.startsWith("P"))) { //$NON-NLS-1$ //$NON-NLS-2$
				ret += "\n" + prev.trim(); //$NON-NLS-1$
				prev = br.readLine();
				if (prev == null) {
					br.close();
					return ret;
				}
			}
			while (prev.substring(0, 2).equals(ret.substring(0, 2))) {
				ret += "\n" + nextLine().substring(2); //$NON-NLS-1$
				return ret;
			}
			while (prev.startsWith("LZ")) { //$NON-NLS-1$
				prev = br.readLine();
				if (prev == null) {
					br.close();
					break;
				}
			}
			return ret;
		}
		
		boolean atEOF(){
			return prev == null;
		}
		
		public void close() throws Exception{
			br.close();
		}
	}
	
}
