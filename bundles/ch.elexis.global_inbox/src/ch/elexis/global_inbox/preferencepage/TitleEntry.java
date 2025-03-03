package ch.elexis.global_inbox.preferencepage;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class TitleEntry {

	private String title;
	private String categoryName;

	public TitleEntry(String value) {
		String[] split = value.split(Pattern.quote(TitleCompletionPreferencePage.STORE_SEPARATOR));
		title = split[0];
		categoryName = (split.length > 1) ? split[1] : StringUtils.EMPTY;
	}

	public TitleEntry() {
		title = "Titel";
		categoryName = null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
}
