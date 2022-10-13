/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.holder.ContextServiceHolder;

public final class ConsultationData {
	private IEncounter encounter;
	private static List<IBillable> verrechenbar;

	public List<ArticleData> load() throws NullPointerException {
		encounter = ContextServiceHolder.get().getTyped(IEncounter.class).orElse(null);
		if (encounter == null) {
			throw new NullPointerException("No consultation selected");
		}
		return getArticles();
	}

	private List<ArticleData> getArticles() {
		List<IBilled> verrechnet = encounter.getBilled();

		verrechenbar = new ArrayList<>();
		verrechnet.stream().forEach(new VerrechnetConsumer());

		List<ArticleData> articles = new ArrayList<>();
		verrechenbar.stream().filter(v -> v instanceof IArticle)
				.forEach(v -> articles.add(new ArticleData((IArticle) v)));

		return articles;
	}

	private static class VerrechnetConsumer implements Consumer<IBilled> {
		@Override
		public void accept(IBilled v) {
			// We need to count the quantity of the articles
			for (int i = 0; i < v.getAmount(); i++) {
				verrechenbar.add(v.getBillable());
			}
		}
	}

	public List<IPrescription> getMedication() {
		IPatient patient = encounter.getCoverage().getPatient();
		List<EntryType> filterList = Arrays.asList(new EntryType[] { EntryType.FIXED_MEDICATION,
				EntryType.RESERVE_MEDICATION, EntryType.SYMPTOMATIC_MEDICATION });
		return patient.getMedication(filterList);
	}
}
