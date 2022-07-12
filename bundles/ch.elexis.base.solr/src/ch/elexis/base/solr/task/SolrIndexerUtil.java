package ch.elexis.base.solr.task;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.util.ContentStreamBase;
import org.apache.solr.common.util.ContentStreamBase.ByteArrayStream;
import org.apache.solr.common.util.NamedList;

public class SolrIndexerUtil {

	public String[] performSolrCellRequest(HttpSolrClient solr, String collection, byte[] input)
			throws SolrServerException, IOException {

		// extract content and metadata using SolrCell
		ContentStreamUpdateRequest request = new ContentStreamUpdateRequest("/update/extract"); //$NON-NLS-1$

		final ByteArrayStream stream = new ContentStreamBase.ByteArrayStream(input, null);
		request.addContentStream(stream);
		request.setParam("extractOnly", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		request.setParam("extractFormat", "text"); //$NON-NLS-1$ //$NON-NLS-2$

		String content = StringUtils.EMPTY;
		String metadata = StringUtils.EMPTY;

		NamedList<Object> result = solr.request(request, collection);
		for (int i = 0; i < result.size(); i++) {
			String name = result.getName(i);
			if (name == null) {
				content = String.valueOf(result.getVal(i));
			} else if ("null_metadata".equals(name)) { //$NON-NLS-1$
				metadata = String.valueOf(result.getVal(i));
			}
		}

		return new String[] { content, metadata };
	}

}
