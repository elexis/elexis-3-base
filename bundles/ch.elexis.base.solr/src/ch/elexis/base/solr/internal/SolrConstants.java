package ch.elexis.base.solr.internal;

public class SolrConstants {

	public static final String CORE_ENCOUNTERS = "el-encounters";
	public static final String CORE_DOCUMENTS = "el-documents";
	public static final String CORE_LETTERS = "el-letters";

	/**
	 * The last index run for encounters included with lastUpdate <= the given
	 * value. On initial run, all entities with possibly <code>null</code> as
	 * lastUpdate will be included
	 */
	public static final String CONFIG_KEY_LASTINDEXRUN_ENCOUNTER = "solrIndexer/LU_lastIndexRun_encounter";

}
