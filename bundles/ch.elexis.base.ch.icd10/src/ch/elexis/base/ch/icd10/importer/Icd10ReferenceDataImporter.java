package ch.elexis.base.ch.icd10.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jpa.entities.ICD10;
import ch.elexis.core.jpa.model.util.JpaModelUtil;
import ch.rgw.io.FileTool;

/**
 * Der ICDImporter importiert den ICD-Code, wie er beispielsweise von der WHO
 * bereitgestellt wird. Die "EDV-Fassung" kann z.B. hier bezogen werden:
 * http://www
 * .dimdi.de/dynamic/de/klassi/downloadcenter/icd-10-who/version2006/systematik
 * Sie benötigen die ICD-10-WHO 2006 Systematik EDV-Fassung ASCII
 * (x1vea2006.zip) und die ICD-10-WHO 2006 Systematik Metadaten ASCII
 * (x1vma2006.zip) Entpacken Sie beide in dieselbe Ebene desselben
 * Verzeichnisses. (Die "liesmich.txt"-Datei wird dabei überschrieben, da sie in
 * beiden Archiven vorhanden ist. Sie können die entsprechende Warung
 * ignorieren)
 *
 * Dieser Importer liest aus den Metadaten "codes.txt" die einzelnen ICD-Ziffern
 * zeilenweise ein und baut die kompletten Codes mithilfe der KAPxx-.asc -
 * Kapiteldateien aus der Systematik auf.
 *
 * Aufbau von codes.txt: Feld 1 : Klassifikationsebene, 1 Zeichen 3 =
 * Dreisteller 4 = Viersteller 5 = Fünfsteller Feld 2 : Ort der Schlüsselnummer
 * im Klassifikationsbaum, 1 Zeichen T = terminale Schlüsselnummer (kodierbarer
 * Endpunkt) N = nichtterminale Schlüsselnummer (kein kodierbarer Endpunkt) Feld
 * 3 : Generiert? X: explizit in der Klassifikation aufgeführt S: über
 * Subklassifikation generiert Feld 4 : Art der Viersteller X = explizit
 * aufgeführt (präkombiniert) S = per Subklassifikation (postkomibiniert) Feld 5
 * : Kapitelnummer, 2 Zeichen Feld 6 : erster Dreisteller der Gruppe, 3 Zeichen
 * Feld 7 : Dreisteller zur Schlüsselnummer, 3 Zeichen Feld 8 : Schlüsselnummer
 * ohne eventuelles Kreuzchen, bis zu 7 Zeichen Feld 9 : Schlüsselnummer ohne
 * Strich, Sternchen und Ausrufezeichen, bis zu 6 Zeichen Feld 10:
 * Schlüsselnummer ohne Punkt, Strich, Sternchen und Ausrufezeichen, bis zu 5
 * Zeichen Feld 11: Klassentitel, bis zu 255 Zeichen Feld 12: Bezug zur
 * Mortalitätsliste 1 Feld 13: Bezug zur Mortalitätsliste 2 Feld 14: Bezug zur
 * Mortalitätsliste 3 Feld 15: Bezug zur Mortalitätsliste 4 Feld 16: Bezug zur
 * Morbiditätsliste Feld 17: Geschlechtsbezug der Schlüsselnummer 9 = kein
 * Geschlechtsbezug M = maennlich W = weiblich Feld 18: Art des Fehlers bei
 * Geschlechtsbezug 9 = irrelevant M = Muß-Fehler K = Kann-Fehler Feld 19:
 * untere Altersgrenze für eine Schlüsselnummer 999 = irrelevant 000 = bis unter
 * 1 Tag 001-006 = 1 Tag bis unter 7 Tage 011-013 = 7 Tage bis unter 28 Tage
 * 101-111 = 28 Tage bis unter 1 Jahr 201-299 = 1 Jahr bis unter 100 Jahre
 * 300-324 = 100 Jahre bis unter 124 Jahre Feld 20: obere Altersgrenze für eine
 * Schlüsselnummer wie bei Feld 19 Feld 21: Art des Fehlers bei Altersbezug 9 =
 * irrelevant M = Muß-Fehler K = Kann-Fehler Feld 22: Krankheit in Mitteleuropa
 * sehr selten? J = Ja (--> Kann-Fehler auslösen!) N = Nein Feld 23:
 * Schlüsselnummer als Grundleiden in der Todesursachenkodierung zugelassen? J =
 * Ja M = Nein
 *
 *
 * @author gerry
 *
 */
@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=icd10")
public class Icd10ReferenceDataImporter extends AbstractReferenceDataImporter implements IReferenceDataImporter {

	private Pattern pat_group;

	private final static String regex_group = "([A-Z][0-9][0-9]-[A-Z][0-9][0-9])(.+?):"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(Icd10ReferenceDataImporter.class);

	static final int LEVEL = 0;
	static final int TERMINAL = 1;
	static final int GENERATED = 2;
	static final int KIND = 3;
	static final int CHAPTER = 4;
	static final int GROUP = 5;
	static final int SUPERCODE = 6;
	static final int CODE = 7;
	static final int CODE_SHORT = 8;
	static final int CODE_COMPACT = 9;
	static final int TEXT = 10;

	@SuppressWarnings("unchecked")
	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer newVersion) {
		List<ICD10> existing = EntityUtil.loadAll(ICD10.class);
		if (existing.size() > 1) {
			EntityUtil.removeAll((List<Object>) (List<?>) existing);
		}
		monitor.beginTask("ICD-10 Import", 15000);
		try {
			// input is expected to be a zip file, containing the icd-10 who 2006 files as
			// described
			File zipFile = Files.createTempFile("icd10_", ".zip").toFile(); //$NON-NLS-1$ //$NON-NLS-2$
			FileUtils.copyInputStreamToFile(input, zipFile);
			File unzipDirectory = Files.createTempDirectory("icd10_").toFile(); //$NON-NLS-1$
			FileTool.unzip(zipFile, unzipDirectory);
			monitor.worked(500);
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			monitor.subTask("Lese Codes");
			pat_group = Pattern.compile(regex_group, Pattern.CASE_INSENSITIVE);
			File codes = new File(unzipDirectory, "codes.txt"); //$NON-NLS-1$
			InputStreamReader ir = new InputStreamReader(new FileInputStream(codes), "iso-8859-1"); //$NON-NLS-1$
			try (BufferedReader br = new BufferedReader(ir)) {
				String in;
				ICD10 node;
				Hashtable<String, ICD10> chapters = new Hashtable<String, ICD10>();
				Hashtable<String, ICD10> groups = new Hashtable<String, ICD10>();
				Hashtable<String, ICD10> supercodes = new Hashtable<String, ICD10>();
				while ((in = br.readLine()) != null) {
					String[] fields = in.split(";"); //$NON-NLS-1$
					String kap = fields[CHAPTER];
					String group = fields[GROUP];
					String supercode = fields[SUPERCODE];
					ICD10 kapitel = chapters.get(kap);
					if (kapitel == null) { // Neues Kapitel anlegen
						kapitel = new ICD10();
						kapitel.setParent("NIL"); //$NON-NLS-1$
						kapitel.setCode(kap);
						kapitel.setEncoded("1;N;X;X;" + kap + ";" + kap + ";" + kap + ";" + kap + ";" + kap + ";" + kap //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
								+ ";Kapitel " + kap + ";;;;;"); //$NON-NLS-2$
						kapitel.setText(kapitel.getEnoded().split(";")[TEXT]); //$NON-NLS-1$
						chapters.put(kap, kapitel);
						LineFeeder lf = new LineFeeder(new File(unzipDirectory, "KAP" + kap + ".asc")); //$NON-NLS-1$ //$NON-NLS-2$
						while (lf.peek() == '0') {
							String t = lf.nextLine();
							switch (t.charAt(1)) {
							case 'T':
								kapitel.setText(t.substring(2).replaceAll(StringUtils.LF, " - ")); //$NON-NLS-1$
								// //$NON-NLS-3$
								break;
							case 'I':
								setExtInfo("Incl:", t.substring(2), kapitel); //$NON-NLS-1$
								break;
							case 'E':
								setExtInfo("Excl:", t.substring(2), kapitel); //$NON-NLS-1$
								break;
							case 'G':
								String gruppen = t.substring(2).replaceAll(StringUtils.LF, "::"); //$NON-NLS-1$
								setExtInfo("Gruppen", t.substring(2), kapitel); //$NON-NLS-1$
								Matcher match = pat_group.matcher(gruppen + "::"); //$NON-NLS-1$
								while (match.find() == true) {
									String code = match.group(1);
									String[] gs = code.split("-"); //$NON-NLS-1$
									if (groups.get(gs[0]) == null) {
										ICD10 icdgroup = new ICD10();
										icdgroup.setParent(kapitel.getId());
										icdgroup.setCode(code);
										icdgroup.setEncoded("1;N;X;X;" + kap + ";" + code + ";" + gs[0] + ";" + gs[0] //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
												+ ";" + code + ";" + code + ";" + match.group(2)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
										icdgroup.setText(icdgroup.getEnoded().split(";")[TEXT]); //$NON-NLS-1$
										groups.put(gs[0], icdgroup);
									}
								}
								break;
							default:
								setExtInfo("Zusatz", t.substring(2), kapitel); //$NON-NLS-1$
							}
						}
						// save chapters and groups
						EntityUtil.save(new ArrayList(chapters.values()));
						EntityUtil.save(new ArrayList(groups.values()));
					}

					ICD10 mygroup = groups.get(group);
					if (mygroup == null) {
						node = new ICD10();
						node.setParent(kapitel.getId());
						node.setCode(fields[CODE]);
						node.setEncoded(in);
						node.setText(node.getEnoded().split(";")[TEXT]); //$NON-NLS-1$
					} else {
						if (fields[0].equals("3")) { //$NON-NLS-1$
							node = new ICD10();
							node.setParent(mygroup.getId());
							node.setCode(fields[CODE]);
							node.setEncoded(in);
							node.setText(node.getEnoded().split(";")[TEXT]); //$NON-NLS-1$
							supercodes.put(fields[CODE], node);
						} else {
							ICD10 icdSuper = supercodes.get(supercode);
							if (icdSuper == null) {
								node = new ICD10();
								node.setParent(mygroup.getId());
								node.setCode(fields[CODE]);
								node.setEncoded(in);
								node.setText(node.getEnoded().split(";")[TEXT]); //$NON-NLS-1$
							} else {
								node = new ICD10();
								node.setParent(icdSuper.getId());
								node.setCode(fields[CODE]);
								node.setEncoded(in);
								node.setText(node.getEnoded().split(";")[TEXT]); //$NON-NLS-1$
							}
						}
					}
					EntityUtil.save(Collections.singletonList(node));

					monitor.worked(1);
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					node = null;
					kap = null;
				}
			}
		} catch (Exception e) {
			logger.error("Error importing icd10 zip file", e); //$NON-NLS-1$
			return new Status(Status.ERROR, "ch.elexis.base.ch.icd10", //$NON-NLS-1$
					"Error importing icd10 zip file [" + e.getMessage() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return Status.OK_STATUS;
	}

	@Override
	public int getCurrentVersion() {
		return -1;
	}

	private static void setExtInfo(Object key, Object value, ICD10 icd) {
		Map<Object, Object> extInfo = new Hashtable<>();
		byte[] bytes = icd.getExtInfo();
		if (bytes != null) {
			extInfo = JpaModelUtil.extInfoFromBytes(bytes);
		}
		if (value == null) {
			extInfo.remove(key);
		} else {
			extInfo.put(key, value);
		}
		icd.setExtInfo(JpaModelUtil.extInfoToBytes(extInfo));
	}

	private class LineFeeder {
		String prev;
		BufferedReader br;

		LineFeeder(File file) throws Exception {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "iso-8859-1")); //$NON-NLS-1$
			prev = br.readLine();
		}

		char peek() {
			return prev.charAt(0);
		}

		String nextLine() throws Exception {
			if (prev == null) {
				return null;
			}
			String ret = prev;
			prev = br.readLine();
			if (prev == null) {
				br.close();
				return ret;
			}
			while (prev.startsWith(StringUtils.SPACE) || (prev.startsWith("P"))) { //$NON-NLS-1$
				ret += StringUtils.LF + prev.trim();
				prev = br.readLine();
				if (prev == null) {
					br.close();
					return ret;
				}
			}
			while (prev.substring(0, 2).equals(ret.substring(0, 2))) {
				ret += StringUtils.LF + nextLine().substring(2);
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

		boolean atEOF() {
			return prev == null;
		}

		public void close() throws Exception {
			br.close();
		}
	}
}
