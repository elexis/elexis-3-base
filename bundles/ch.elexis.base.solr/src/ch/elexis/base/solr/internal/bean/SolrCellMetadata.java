package ch.elexis.base.solr.internal.bean;

import java.util.HashMap;
import java.util.Map;

public class SolrCellMetadata {

	private Map<String, String> metadataMap;

	public SolrCellMetadata(String metadata) {
		metadataMap = new HashMap<String, String>();
		String _metadata = metadata.substring(1, metadata.length() - 1);
		String[] split = _metadata.split(","); //$NON-NLS-1$
		for (String kv : split) {
			String[] kvEntry = kv.split("="); //$NON-NLS-1$
			if (kvEntry.length == 2) {
				String key = kvEntry[0].toLowerCase();
				String value = kvEntry[1];
				if (value.length() > 1) {
					String _value = value.substring(1, value.length() - 1);
					metadataMap.put(key, _value);
				}

			}
		}
	}

	public String get(String key) {
		return metadataMap.get(key);
	}

}
