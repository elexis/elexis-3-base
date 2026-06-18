package at.medevit.ch.artikelstamm.extinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.medevit.ch.artikelstamm.ARTIKELSTAMM.ITEMS.ITEM.ARTSL.ARTLIMS;
import at.medevit.ch.artikelstamm.ARTIKELSTAMM.LIMITATIONS.LIMITATION;

public class ArticleIndicationInfo {

	private List<ArticleIndication> indications;

	public static ArticleIndicationInfo of(ARTLIMS artlims, Map<String, LIMITATION> limitations) {
		ArticleIndicationInfo ret = new ArticleIndicationInfo();
		ret.indications = new ArrayList<ArticleIndication>();
		artlims.getARTLIM().forEach(al -> ret.indications.add(ArticleIndication.of(al, limitations)));
		return ret;
	}

	public List<ArticleIndication> getIndications() {
		return indications;
	}
}
