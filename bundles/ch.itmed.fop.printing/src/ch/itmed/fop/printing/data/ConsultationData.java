package ch.itmed.fop.printing.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Artikel;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Verrechnet;

import ch.itmed.fop.printing.data.ArticleData;
import ch.itmed.fop.printing.resources.Messages;

public final class ConsultationData {
	private Konsultation consultation;
	private static List<IVerrechenbar> verrechenbar;

	public List<ArticleData> load() throws NullPointerException {
		consultation = (Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
		if (consultation == null) {
			SWTHelper.showInfo(Messages.Info_NoConsultation_Title, Messages.Info_NoConsultation_Message);
			throw new NullPointerException("No consultation selected");
		}
		return getArticles();
	}

	private List<ArticleData> getArticles() {
		List<Verrechnet> verrechnet = consultation.getLeistungen();

		verrechenbar = new ArrayList<>();
		verrechnet.stream().forEach(new VerrechnetConsumer());

		List<ArticleData> articles = new ArrayList<>();
		verrechenbar.stream().filter(v -> v instanceof Artikel)
				.forEach(v -> articles.add(new ArticleData((Artikel) v)));

		return articles;
	}

	private static class VerrechnetConsumer implements Consumer<Verrechnet> {
		@Override
		public void accept(Verrechnet v) {
			// We need to count the quantity of the articles
			for (int i = 0; i < v.getZahl(); i++) {
				verrechenbar.add(v.getVerrechenbar());
			}
		}
	}
}
