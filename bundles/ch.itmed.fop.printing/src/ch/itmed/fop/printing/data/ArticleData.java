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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import ch.elexis.core.model.IArticle;

public final class ArticleData {
	private IArticle article;

	public ArticleData(IArticle v){
		article = v;
	}

	public String getName() {
		return article.getName();
	}

	public String getPrice() {
		return article.getSellingPrice().toString();
	}

	public String getDeliveryDate() {
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY").withZone(ZoneId.systemDefault());
		String currentDate = formatter.format(localDate);
		return currentDate;
	}
	
	public IArticle getArticle(){
		return article;
	}
}
