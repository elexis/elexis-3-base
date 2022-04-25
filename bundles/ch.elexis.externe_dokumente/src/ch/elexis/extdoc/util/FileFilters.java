package ch.elexis.extdoc.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileFilters implements FilenameFilter {
	public Pattern pattern;

	public FileFilters(String lastname, String firstname) {
		FileFiltersConvention convention = new FileFiltersConvention(lastname, firstname);
		String regex = "^" + convention.getShortName() + " .*$"; //$NON-NLS-1$ //$NON-NLS-2$
		pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}

	public boolean accept(File dir, String name) {
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}
}
