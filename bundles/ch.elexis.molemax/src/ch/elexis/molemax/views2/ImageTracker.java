
package ch.elexis.molemax.views2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.molemax.data.MolemaxACL;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class ImageTracker extends PersistentObject {
	private static final String TABLENAME = "CH_ELEXIS_MOLEMAX";
	private static final String VERSION = "0.2.0";
	private static final String createTable = "CREATE TABLE " + TABLENAME + " ("
			+ "ID           VARCHAR(25) primary key, " + "deleted CHAR(1) default '0', " + "patientID    VARCHAR(25), "
			+ "parentID VARCHAR(25), " + "date CHAR(8)," + "slot CHAR(3), " + "koord VARCHAR(40), " + // x-y-w-h-num-ext
			" ExtInfo	BLOB, " + "lastupdate BIGINT);";
	private static final String createIndex = " CREATE INDEX MLMX1 ON " + TABLENAME + " (patientID);";
	private static final String insertVersion = "INSERT INTO " + TABLENAME + " (ID,koord) VALUES ('VERSION','" + VERSION
			+ "');";
	private static final String createDB = createTable + createIndex + insertVersion;
	private static final String updateDB011 = "ALTER  TABLE " + TABLENAME + " ADD parentID VARCHAR(25);";

	private static final String updateDB020 = "ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;";
	static Log log = Log.get("Molemax");
	private static int[] map = { 0, 4, 8, 1, 5, 9, 2, 6, 10, 3, 7, 11 };
	Image image = null;

	static {
		addMapping(TABLENAME, "PatientID=patientID", "ParentID=parentID", "Datum=S:D:date", "slot", "koord", "ExtInfo");
		ImageTracker version = load("VERSION");
		if (!version.exists()) {
			if (PersistentObject.tableExists(TABLENAME)) {
				createOrModifyTable("DROP TABLE " + TABLENAME);
			}
			createOrModifyTable(createDB);
			new MolemaxACL().initializeDefaults(CoreHub.acl);
		}

		VersionInfo vi = new VersionInfo(version.get("koord"));
		if ((vi.isOlder(VERSION))) {
			if (vi.isEqual(new VersionInfo("0.1.0"))) {
				createOrModifyTable(updateDB011);
				version.set("koord", VERSION);
				log.log("Update auf " + VERSION, Log.TRACE);
			} else if (vi.isEqual(new VersionInfo("0.1.1"))) {
				createOrModifyTable(updateDB020);
				version.set("koord", VERSION);
				log.log("Update auf " + VERSION, Log.TRACE);

			} else {
				SWTHelper.alert("Kann Molemax nicht starten", "Zu alte Version der Datenbank");
			}
		}
	}

	/**
	 * A Child Tracker: A detail within a region image
	 *
	 * @param p      patient
	 * @param parent parent tracker (that denotes the region)
	 * @param date   date of sequence to add this detail. If null: today
	 * @param slot   whicht region this detail belongs
	 * @param pos    position of this detail within the region
	 */
	public ImageTracker(final Patient p, final ImageTracker parent, String date, final int slot, final Rectangle pos) {
		create(null);
		if (date == null) {
			date = new TimeTool().toString(TimeTool.DATE_GER);
		}
		set(new String[] { "PatientID", "ParentID", "Datum", "slot", "koord" },
				new String[] { p.getId(), parent.getId(), date, Integer.toString(slot), makeFilename(pos, null) });

	}

	/**
	 * a parent tracker is one of the 12 regions to store. If in the directory of
	 * the originating file are exactly 12 images with subseqeuntly ascending
	 * sequence numbers in their filename, and this image is first of sequence, and
	 * slot 0 is selected, then all 12 images can be loaded
	 *
	 * @param p    patient this image belongs to
	 * @param date date of the sequence this image belongs to
	 * @param slot which of the 12 basic regions this image belongs to
	 * @param file file in which the image resides
	 */
	public ImageTracker(final Patient p, final String date, final int slot, final File file) {

		StringBuilder sb = new StringBuilder();
		sb.append(makeDescriptor(p));
		File dir = new File(sb.toString());
		if ((!dir.exists()) && (!dir.mkdirs())) {
			SWTHelper.showError("Schreibfehler", "Konnte Verzeichnis " + dir.getAbsolutePath()
					+ " nicht erstellen. Speicherverzeichnis korrekt angegeben?");
			return;
		}
		sb.append(File.separator);
		String ext = FileTool.getExtension(file.getName());
		String fname = "base." + ext;
		sb.append(fname);
		File out = new File(sb.toString());
		if (!FileTool.copyFile(file, out, FileTool.REPLACE_IF_EXISTS)) {
			SWTHelper.showError("I/O Fehler", "Kann das Bild nicht übertragen");
		} else {
			create(null);
			set(new String[] { "PatientID", "ParentID", "Datum", "slot", "koord" },
					new String[] { p.getId(), "NIL", date, Integer.toString(slot), fname });
		}
		if (slot == 0) {
			Pattern pattern = Pattern.compile("([a-z_\\-]+)([0-9]+)(\\.[a-z0-9]+)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(file.getName());
			if (matcher.matches()) {
				File path = file.getParentFile();
				String prefix = matcher.group(1);
				String val = matcher.group(2);
				int numlen = val.length();
				String postfix = matcher.group(3);
				int v = Integer.parseInt(val);
				boolean bSerie = true;
				for (int i = v; i < v + 11; i++) {
					String tryname = prefix + StringTool.pad(StringTool.LEFT, '0', Integer.toString(i), numlen)
							+ postfix;
					if (!new File(path, tryname).exists()) {
						bSerie = false;
						break;
					}
				}
				if (bSerie) {
					if (SWTHelper.askYesNo("Ganze Serie einlesen?",
							"Dieses Bild scheint das erste Bild einer Serie zu sein. Ganze Serie einlesen?")) {
						for (int i = v + 1; i < v + 12; i++) {
							String tryname = prefix + StringTool.pad(StringTool.LEFT, '0', Integer.toString(i), numlen)
									+ postfix;
							int slotnr = map[i - v];
							ImageTracker trytracker = new ImageTracker(p, date, slotnr, new File(path, tryname));
						}
						ElexisEventDispatcher.fireSelectionEvent(p);
						// GlobalEvents.getInstance().fireSelectionEvent(p);
					}
				}
			}

		}
	}

	/**
	 * Copy the file containing an image to the directory location it belongs
	 * (according to the database information of this tracker). If a file for the
	 * same tracker exists, the index portion of the filename is increased
	 *
	 * @param file the file to import. Must contain a valid image
	 * @return true on success
	 */
	public boolean setFile(final File file) {
		int idx = 0;
		File out;
		do {
			StringBuilder sb = new StringBuilder();
			sb.append(makeFilename()).append("-").append(idx);
			String ext = FileTool.getExtension(file.getName());
			sb.append(".").append(ext);
			out = new File(sb.toString());
			idx++;
		} while (out.exists());
		if (!FileTool.copyFile(file, out, FileTool.REPLACE_IF_EXISTS)) {
			SWTHelper.showError("I/O Fehler", "Kann das Bild nicht übertragen");
			return false;
		} else {
			set("koord", out.getName());
			return true;
		}
	}

	public void dispose() {
		if (image != null) {
			image.dispose();
			image = null;
		}
	}

	private File findSequenceFile() {
		String path = makeFilename();
		File file = new File(path);
		if (!file.exists()) {
			String fname = get("koord");
			String[] flds = FileTool.getNakedFilename(fname).split("-");
			String ext = FileTool.getExtension(fname);
			if (flds.length > 4) {
				String seq = flds[4];
				if (seq.matches("[0-9]+")) {
					int is = Integer.parseInt(seq);
					while (is-- > 0) {
						try {
							fname = makeFilename(Integer.parseInt(flds[0]), Integer.parseInt(flds[1]),
									Integer.parseInt(flds[2]), Integer.parseInt(flds[3]), is, ext);
							set("koord", fname);
							path = makeFilename();
							file = new File(path);
							if (file.exists() && file.canRead()) {
								return file;
							}
						} catch (Exception ex) {
							ExHandler.handle(ex);
						}

					}
				}
			}
			return null;
		}
		return file;
	}

	/**
	 * Image des Bildes erzeugen. Achtung: dieses muss nach Gebrauch mit dispose()
	 * wieder entsorgt werden.
	 *
	 * @return ein SWT-Image
	 */
	public Image createImage() {
		if (image != null) {
			return image;
		}
		File file = findSequenceFile();
		if (file == null) {
			return null;
		}
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			image = new Image(UiDesk.getDisplay(), fis);
			return image;

		} catch (FileNotFoundException e) {
			ExHandler.handle(e);
			return null;
		} catch (Exception ex) {
			ExHandler.handle(ex);
			return image;
		}

	}

	public Image createImageScaled(final Point size) {
		Image orig = createImage();
		Image scaled = new Image(UiDesk.getDisplay(), orig.getImageData().scaledTo(size.x, size.y));
		return scaled;
	}

	public int getSlot() {
		return checkZero(get("slot"));
	}

	public Rectangle getBounds() {
		String koord = get("koord");
		String[] k = koord.split("[\\.-]");
		if (k.length > 3) {
			try {
				return new Rectangle(Integer.parseInt(k[0]), Integer.parseInt(k[1]), Integer.parseInt(k[2]),
						Integer.parseInt(k[3]));
			} catch (NumberFormatException nx) {
				ExHandler.handle(nx);
			}
		} else {
			Image img = createImage();
			if (img != null) {
				ImageData imd = img.getImageData();
				return new Rectangle(imd.x, imd.y, imd.width, imd.height);
			}
		}
		return null;
	}

	@Override
	public String getLabel() {
		TimeTool tt = new TimeTool(get("Datum"));
		return tt.toString(TimeTool.DATE_GER);
	}

	public static ImageTracker load(final String id) {
		return new ImageTracker(id);
	}

	protected ImageTracker(final String id) {
		super(id);
	}

	protected ImageTracker() {
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	public Patient getPatient() {
		return Patient.load(get("PatientID"));
	}

	public ImageTracker getParent() {
		String parentID = checkNull(get("ParentID"));
		if (parentID.equals("NIL")) {
			return null;
		}
		ImageTracker ret = load(parentID);
		if (ret.isValid()) {
			return ret;
		}
		return null;
	}

	public String getDate() {
		return get("Datum");
	}

	/**
	 * compose a filename to the image file that belongs to this tracker
	 *
	 * @return
	 */
	String makeFilename() {
		StringBuilder ret = new StringBuilder();
		ImageTracker parent = getParent();
		String date = getDate();
		if (parent != null) {
			date = parent.getDate();
		}
		ret.append(makeDescriptor(getPatient())).append(File.separator).append(get("koord"));
		return ret.toString();
	}

	/**
	 * Load all children and return as stack of images
	 *
	 * @param parent the parent tracker whose children to load
	 * @return a stack that contains the parent at its bottom and all the children
	 *         above ordered by date
	 */
	public static ImageTracker[] getImageStack(final ImageTracker parent) {
		if (parent == null) {
			return new ImageTracker[0];
		}
		Query<ImageTracker> qbe = new Query<ImageTracker>(ImageTracker.class);
		qbe.add("PatientID", "=", parent.get("PatientID"));
		qbe.add("ParentID", "=", parent.getId());
		List<ImageTracker> list = qbe.execute();
		Collections.sort(list, new Comparator<ImageTracker>() {

			public int compare(final ImageTracker arg0, final ImageTracker arg1) {
				if ((arg0 != null) && (arg1 != null)) {
					TimeTool tt0 = new TimeTool(arg0.get("Datum"));
					TimeTool tt1 = new TimeTool(arg1.get("Datum"));
					int i = tt0.compareTo(tt1);
					if (i == 0) {
						Rectangle r0 = arg0.getBounds();
						Rectangle r1 = arg1.getBounds();
						if ((r0 != null) && (r1 != null)) {
							return SWTHelper.size(r0) - SWTHelper.size(r1);
						}
					}
					return i;
				}
				return 0;
			}
		});

		ImageTracker[] ret = new ImageTracker[list.size() + 1];
		for (int i = 1; i < ret.length; i++) {
			ret[i] = list.get(i - 1);
		}
		ret[0] = parent;
		return ret;
	}

	public static String getLastSequenceDate(final Patient pat) {
		Query<ImageTracker> qbe = new Query<ImageTracker>(ImageTracker.class);
		qbe.add("PatientID", "=", pat.getId());
		qbe.add("ParentID", "=", "NIL");
		List<ImageTracker> list = qbe.execute();
		if (list.size() > 0) {
			ImageTracker ret = list.get(0);
			if (list.size() > 1) {
				TimeTool lastDate = new TimeTool(TimeTool.BEGINNING_OF_UNIX_EPOCH);
				TimeTool cmp = new TimeTool();
				for (ImageTracker tracker : list) {
					cmp.set(tracker.get("Datum"));
					// System.out.println(cmp.dump());
					// System.out.println(lastDate.dump());
					if (cmp.isAfter(lastDate)) {
						lastDate.set(cmp);
						ret = tracker;
					}
				}
			}
			return ret.getDate();
		}
		return null;
	}

	/**
	 * Load the base Tracker for a given slot, patient and date
	 *
	 * @param patient patient
	 * @param date    date of sequence. If null: Any sequence
	 * @param slot    image region
	 * @return a Tracker for the requested patient and region. Might be null id no
	 *         such tracker exists
	 */
	public static ImageTracker loadBase(final Patient patient, final String date, final int slot) {
		Query<ImageTracker> qbe = new Query<ImageTracker>(ImageTracker.class);
		qbe.add("PatientID", "=", patient.getId());
		qbe.add("slot", "=", Integer.toString(slot));
		qbe.add("ParentID", "=", "NIL");
		if (date != null) {
			qbe.add("Datum", "=", date);
		}
		List<ImageTracker> list = qbe.execute();
		if (list.size() > 0) {
			ImageTracker ret = list.get(0);
			if ((date == null) && (list.size() > 1)) {
				TimeTool lastDate = new TimeTool(TimeTool.BEGINNING_OF_UNIX_EPOCH);
				TimeTool cmp = new TimeTool();
				for (ImageTracker tracker : list) {
					cmp.set(tracker.get("Datum"));
					if (cmp.isAfter(lastDate)) {
						lastDate = cmp;
						ret = tracker;
					}
				}
			}
			return ret;
		}
		return null;
	}

	/**
	 * Find the Tracker that is topmost at a given point
	 *
	 * @param slot Array of all trackers to match
	 * @param x    koordinate
	 * @param y    koordinate
	 * @return index of the last Tracker that contains the given point
	 */
	public static int getTrackerAtPoint(final ImageTracker[] slot, final int x, final int y) {
		for (int i = slot.length - 1; i >= 0; i--) {
			Rectangle rec = slot[i].getBounds();
			if (rec.contains(x, y)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Find all Trackers that contain a given point
	 *
	 * @param slot
	 * @param x
	 * @param y
	 * @return
	 */
	public static List<ImageTracker> getTrackersAtPoint(final ImageTracker[] slot, final int x, final int y) {
		ArrayList<ImageTracker> ret = new ArrayList<ImageTracker>(slot.length);
		for (int i = 1; i < slot.length; i++) {
			Rectangle rec = slot[i].getBounds();
			if (rec == null) {
				continue;
			}
			if (rec.contains(x, y)) {
				ret.add(slot[i]);
			}
		}
		return ret;
	}

	/**
	 * Create the full path used for images of the given slot
	 *
	 * @param p    Patient
	 * @param date date of sequence (if null: today)
	 * @param slot image slot
	 * @return the full path of the directory where images of this slot are stored
	 */
	public static String makeDescriptor(final Patient p) {
		String input = CoreHub.localCfg.get(MolemaxPrefs.CUSTOM_BASEDIR, StringUtils.EMPTY);

		StringBuilder ret = new StringBuilder();
		ret.append(CoreHub.localCfg.get(MolemaxPrefs.BASEDIR, StringUtils.EMPTY)).append(File.separator);

		// Teilen Sie den Eingabestring in Pfadsegmente
		String[] pathSegments = input.split("/");

		Pattern pattern = Pattern.compile("(Name|Vorname|PatNum|Datum(-[yMd.]+)?|Uhrzeit(-[Hhmsa:]+)?|Slot)(-\\d+)?");

		for (int i = 0; i < pathSegments.length; i++) {
			String segment = pathSegments[i];
			Matcher matcher = pattern.matcher(segment);

			while (matcher.find()) {
				String match = matcher.group();
				String keyword = match.split("-")[0];
				int length = -1; // -1 bedeutet, dass der gesamte Name/Vorname genommen wird

				if (match.contains("-") && !keyword.equals("Datum") && !keyword.equals("Uhrzeit")) {
					length = Integer.parseInt(match.split("-")[1]);
				}

				if (keyword.equals("PatNum")) {
					String patCode = p.getPatCode();
					ret.append(patCode);
				} else if (keyword.equals("Name")) {
					String namCode = p.getName();
					if (length != -1 && namCode.length() > length) {
						namCode = namCode.substring(0, length);
					}
					ret.append(namCode);
				} else if (keyword.equals("Vorname")) {
					String vorCode = p.getVorname();
					if (length != -1 && vorCode.length() > length) {
						vorCode = vorCode.substring(0, length);
					}
					ret.append(vorCode);
				} else if (keyword.equals("Datum")) {
					String dateFormat = match.split("-")[1];
					SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
					String formattedDate = sdf.format(new Date());

					ret.append(formattedDate);

				} else if (keyword.equals("Uhrzeit")) {
					String timeFormat = match.split("-")[1];
					SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
					String formattedTime = sdf.format(new Date());
					ret.append(formattedTime);
				}
			}

			// Nur ein Trennzeichen hinzufügen, wenn es nicht das letzte Segment ist
			if (i < pathSegments.length - 1) {
				ret.append(File.separator);
			}
		}

//		System.out.println("Test 2 " + ret.toString());
		return ret.toString();
	}

	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static int extractLengthAfterKeyword(String path, String keyword) {
		int keywordIndex = path.indexOf(keyword);
		if (keywordIndex == -1) {
			return -1;
		}

		int start = keywordIndex + keyword.length() + 1; // +1 für das Zeichen "-"
		int end = start;

		while (end < path.length() && Character.isDigit(path.charAt(end))) {
			end++;
		}

		String numberStr = path.substring(start, end);
		if (isNumeric(numberStr)) {
			return Integer.parseInt(numberStr);
		}

		return -1;
	}

	/**
	 * create a filename from koordinates and an extension
	 *
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param ext
	 * @return
	 */
	private static String makeFilename(final int x, final int y, final int w, final int h, final int seq,
			final String ext) {
		StringBuilder sb = new StringBuilder();
		sb.append(x).append("-").append(y).append("-").append(w).append("-").append(h).append("-").append(seq);
		if (ext != null) {
			sb.append(".").append(ext);
		}

		return sb.toString();
	}

	private static String makeFilename(final Rectangle rec, final String ext) {
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString(rec.x)).append("-").append(Integer.toString(rec.y)).append("-")
				.append(Integer.toString(rec.width)).append("-").append(Integer.toString(rec.height));
		if (ext != null) {
			sb.append(".").append(ext);
		}
		return sb.toString();
	}

	public static void delete(ImageTracker[] myTracker) {
		if (myTracker != null) {
			for (ImageTracker t : myTracker) {
				if (t != null) {
					t.delete();
				}
			}
			myTracker = new ImageTracker[0];
		}
	}

	public static void dispose(ImageTracker[] myTracker) {
		if (myTracker != null) {
			for (ImageTracker t : myTracker) {
				if (t != null) {
					t.dispose();
				}
			}
			myTracker = new ImageTracker[0];
		}
	}

	@Override
	public boolean delete() {
		if (image != null) {
			image.dispose();
			image = null;
		}
		if (isValid()) {
			String fname = makeFilename();
			File file = new File(fname);
			if (file.exists()) {
				file.delete();
			}
		}
		return super.delete();
	}

	@Override
	public boolean isValid() {
		if (getPatient().isValid()) {
			return super.isValid();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void setInfoString(final String name, final String text) {
		Map extinfo = getMap("ExtInfo");
		extinfo.put(name, text);
		setMap("ExtInfo", extinfo);
	}

	public String getInfoString(final String name) {
		Map extinfo = getMap("ExtInfo");
		return checkNull((String) extinfo.get(name));
	}

//	public static String makeDescriptor(Patient p) {
//		{
//
//			StringBuilder ret = new StringBuilder();
//			ret.append(CoreHub.localCfg.get(MolemaxPrefs.BASEDIR, StringUtils.EMPTY)).append(File.separator);
//			String name = p.getName();
//			ret.append(name.length() > 2 ? name.substring(0, 2) : name);
//			String vname = p.getVorname();
//			ret.append(vname.length() > 2 ? vname.substring(0, 2) : vname);
//			ret.append(p.getPatCode()).append(File.separator);
//			System.out.println("ret.toString(); " + ret.toString());
//			return ret.toString();
//
//		}

//	}
}
