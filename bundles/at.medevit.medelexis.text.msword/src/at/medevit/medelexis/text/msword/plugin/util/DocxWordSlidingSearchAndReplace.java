package at.medevit.medelexis.text.msword.plugin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.elexis.core.data.interfaces.text.ReplaceCallback;

public class DocxWordSlidingSearchAndReplace {
	ArrayList<DocxWordRun> reverseOrderedRuns = new ArrayList<DocxWordRun>();

	Pattern pattern;
	String contains;

	ReplaceCallback callback;
	String replaceText;

	DocxWordParagraph paragraph;

	public DocxWordSlidingSearchAndReplace(DocxWordParagraph para, String regex, ReplaceCallback cb) {
		paragraph = para;
		pattern = Pattern.compile(regex);
		callback = cb;
	}

	public DocxWordSlidingSearchAndReplace(DocxWordParagraph para, String regex, String replace) {
		paragraph = para;
		pattern = Pattern.compile(regex);
		replaceText = replace;
	}

	public DocxWordSlidingSearchAndReplace(DocxWordParagraph para, String search) {
		paragraph = para;
		contains = search;
	}

	public void addRun(DocxWordRun run) {
		reverseOrderedRuns.add(0, run);
	}

	protected void reset() {
		reverseOrderedRuns.clear();
	}

	public boolean findAndReplaceAll() {
		boolean found = false;
		List<DocxWordRun> runs = paragraph.getDirectChildRuns();
		for (DocxWordRun run : runs) {
			addRun(run);
			if (doSearch())
				found = true;
		}
		return found;
	}

	protected boolean doSearch() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < reverseOrderedRuns.size(); i++) {
			DocxWordRun run = reverseOrderedRuns.get(i);
			if (run.isContainingText()) {
				sb.insert(0, run.getText());
				if (pattern != null) {
					String newText = findAndReplace(sb.toString());
					if (newText != null) {
						run.setText(getWindowsString(newText));
						while (--i >= 0) {
							paragraph.removeRun(reverseOrderedRuns.get(i));
						}
						reset();
						return true;
					}
				} else {
					if (sb.toString().contains(contains)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private String getWindowsString(String string) {
		String ret = string;
		// try to fix line endings
		String lines[] = string.split("\\r?\\n");//$NON-NLS-1$
		if (lines.length > 1) {
			StringBuilder sb = new StringBuilder();

			for (String line : lines) {
				if (!line.isEmpty()) {
					sb.append(line);
					sb.append("\r\n");//$NON-NLS-1$
				}
			}
			// remove final linebreak
			sb.replace(sb.length() - 2, sb.length(), "");//$NON-NLS-1$
			ret = sb.toString();
		}
		return ret;
	}

	protected String findAndReplace(String text) {
		StringBuilder sb = new StringBuilder();

		Matcher matcher = pattern.matcher(text);
		int lastEnd = 0;
		int found = 0;
		while (matcher.find()) {
			found++;
			if (lastEnd == 0)
				sb.append(text.substring(0, matcher.start()));
			else
				sb.append(text.substring(lastEnd, matcher.start()));

			String replace = ""; //$NON-NLS-1$
			if (callback != null) {
				Object obj = callback.replace(text.substring(matcher.start(), matcher.end()));
				if (obj instanceof String) {
					replace = (String) obj;
				} else if (obj instanceof String[][]) {
					String[][] contents = (String[][]) obj;
					// insert a table instead of the paragraph
					DocxWordRunProperties rProp = null;
					DocxWordParagraphProperties pProp = paragraph.getProperties();
					if (pProp != null) {
						rProp = pProp.getRunProperties();
					}

					DocxWordTable table = paragraph.replaceWithTable();
					DocxWordTableProperties tProp = table.createProperties();
					tProp.setWidth(100);
					// create the content
					for (int rowIdx = 0; rowIdx < contents.length; rowIdx++) {
						DocxWordTableRow row = table.createRow();
						String[] columns = contents[rowIdx];
						for (int columnIdx = 0; columnIdx < columns.length; columnIdx++) {
							// create column with parameters
							DocxWordTableColumn column = row.createTableColumn();
							// create run for the text
							DocxWordRun cRun = column.createParagraph().createRun();
							// user properties of the paragraph
							if (rProp != null && rProp.properties != null)
								cRun.setProperties(rProp.getClone(true));
							cRun.createText().setText(columns[columnIdx]);
						}
					}
					replace = "";
				}
			} else {
				replace = replaceText;
			}

			// append resolved text or the text that looked like a placeholder
			if (replace != null)
				sb.append(replace);
			else
				sb.append(text.substring(matcher.start(), matcher.end()));
			lastEnd = matcher.end();
		}
		sb.append(text.substring(lastEnd, text.length()));
		if (found > 0)
			return sb.toString();
		else
			return null;
	}

	public boolean contains() {
		boolean found = false;
		List<DocxWordRun> runs = paragraph.getDirectChildRuns();
		for (DocxWordRun run : runs) {
			addRun(run);
			if (doSearch())
				found = true;
		}
		return found;
	}
}
