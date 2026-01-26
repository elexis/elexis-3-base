package ch.elexis.regiomed.order.ui.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.elexis.regiomed.order.model.RegiomedOrderResponse.AlternativeResult;

public record RenderingContext(boolean isSearchAvailable, Set<String> removed, Map<String, String> replacements,
		Map<String, String> replacementNames, Set<String> forcedItems,
		Map<String, List<AlternativeResult>> alternativesMap, String imgLogo, String imgWarning, String imgEdit) {
}