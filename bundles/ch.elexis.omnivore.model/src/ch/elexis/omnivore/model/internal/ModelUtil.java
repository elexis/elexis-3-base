package ch.elexis.omnivore.model.internal;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import ch.rgw.tools.MimeTool;

public class ModelUtil {

	/**
	 * Get the file extension part of the input String.
	 *
	 * @param input
	 * @return
	 */
	public static String evaluateFileExtension(String input) {
		String ext = MimeTool.getExtension(input);
		if (StringUtils.isEmpty(ext)) {
			ext = FilenameUtils.getExtension(input);
			if (StringUtils.isEmpty(ext)) {
				ext = input;
			}
		}
		return ext;
	}

}
