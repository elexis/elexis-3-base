package ch.elexis.connect.sysmex.packages;

import ch.rgw.tools.TimeTool;

public abstract class AbstractUrinData implements IProbe {

	public static class ResultInfo {
		private boolean isAnalyzed;
		private String commentMark;

		private String qualitativValue;
		private String semiQualitativValue;

		private String reflective1Value;
		private boolean isReflective;

		public static ResultInfo parse(int start, int end, String content) {
			ResultInfo ret = new ResultInfo();
			String part = content.substring(start, end);
			ret.isAnalyzed = isAnalyzed(part);
			if (ret.isAnalyzed) {
				if (part.length() == 32) {
					// parse with possible reflective value
					ret.commentMark = getCommentMark(part);
					ret.qualitativValue = getQualitativValue(part);
					ret.semiQualitativValue = getSemiQualitativValue(part);

					String reflective1 = getReflective1(part);
					if (reflective1 != null && !reflective1.isEmpty()) {
						ret.isReflective = true;
						ret.reflective1Value = reflective1;
					} else {
						ret.isReflective = false;
					}
				} else if (part.length() == 15) {
					// parse without reflective vlaue
					ret.commentMark = getCommentMark(part);
					ret.qualitativValue = getQualitativValue(part);
					ret.semiQualitativValue = getSemiQualitativValue(part);
				} else if (part.length() == 20) {
					// parse color

				} else {
					throw new IllegalStateException("Unknown part size " + part.length());
				}
			}
			return ret;
		}

		private static String getReflective1(String part) {
			return part.substring(14, 19).trim();
		}

		private static String getSemiQualitativValue(String part) {
			return part.substring(8, 14).trim();
		}

		private static String getQualitativValue(String part) {
			return part.substring(2, 8).trim();
		}

		private static String getCommentMark(String part) {
			return part.substring(1, 2);
		}

		private static boolean isAnalyzed(String part) {
			return part.substring(0, 1).equals("0");
		}

		public boolean isAnalyzed() {
			return isAnalyzed;
		}

		public String getCommentMark() {
			return commentMark;
		}

		public String getQualitativValue() {
			return qualitativValue;
		}

		public String getSemiQualitativValue() {
			return semiQualitativValue;
		}

		public String getReflective1Value() {
			return reflective1Value;
		}

		public boolean isReflective() {
			return isReflective;
		}
	}

	public abstract int getSize();

	protected abstract int getDataIndex();

	protected abstract TimeTool getDate(final String content);

	protected abstract Value getValue(final String paramName) throws PackageException;

	protected abstract String getPatientId(final String content);
}
