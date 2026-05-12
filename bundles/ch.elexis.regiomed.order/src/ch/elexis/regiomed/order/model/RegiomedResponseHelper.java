package ch.elexis.regiomed.order.model;

import java.util.stream.Collectors;

import ch.elexis.regiomed.order.messages.Messages;

public class RegiomedResponseHelper {

	public static boolean isOverallSuccess(RegiomedOrderResponse response) {
		if (!response.isCheckSuccess()) {
			return false;
		}
		if (response.getArticlesNOK() > 0) {
			return false;
		}
		if (response.getArticles() != null) {
			return response.getArticles().stream().allMatch(RegiomedOrderResponse.ArticleResult::isSuccess);
		}
		return true;
	}

	public static String buildErrorMessage(RegiomedOrderResponse response) {
		StringBuilder sb = new StringBuilder();

		if (response.getMessage() != null && !response.getMessage().isBlank()) {
			sb.append(response.getMessage());
		} else {
			sb.append(Messages.RegiomedOrderResponse_GenericErrorMessage);
		}

		if (response.getArticles() != null) {
			var bad = response.getArticles().stream().filter(a -> !a.isSuccess()).collect(Collectors.toList());

			if (!bad.isEmpty()) {
				sb.append("\n").append(Messages.RegiomedOrderResponse_ProblematicArticles);
				for (RegiomedOrderResponse.ArticleResult ar : bad) {
					sb.append("\n- ").append(ar.getDescription()).append(" (Pharma: ").append(ar.getPharmaCode())
							.append(")");

					if (ar.getAvailMsg() != null && !ar.getAvailMsg().isBlank()) {
						sb.append(": ").append(ar.getAvailMsg());
					}

					if (ar.getInfo() != null && !ar.getInfo().isBlank()) {
						sb.append(" [").append(ar.getInfo()).append("]");
					}
				}
			}
		}
		return sb.toString();
	}
}