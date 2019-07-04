package ch.itmed.fop.printing.data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import ch.elexis.data.Artikel;

public final class ArticleData {
	private Artikel article;

	public ArticleData(Artikel artikel) {
		article = artikel;
	}

	public String getName() {
		return article.getName();
	}

	public String getPrice() {
		return article.getVKPreis().toString();
	}

	public String getDeliveryDate() {
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY").withZone(ZoneId.systemDefault());
		String currentDate = formatter.format(localDate);
		return currentDate;
	}
}
