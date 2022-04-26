/*******************************************************************************
 * Copyright (c) 2010, G. Weirich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *
 *
 *******************************************************************************/
package elexis_db_shaker.actions;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.data.Brief;
import ch.elexis.data.Query;

public class DocumentRemover {

	void run(IProgressMonitor monitor, int i) {
		monitor.subTask("Lösche Dokumente");
		Query<Brief> qbe = new Query<Brief>(Brief.class);
		for (Brief brief : qbe.execute()) {
			brief.delete();
		}
		monitor.worked(i / 2);
		Object os = Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
		if (os != null) {
			IDocumentManager dm = (IDocumentManager) os;
			try {
				List<IOpaqueDocument> documents = dm.listDocuments(null, null, null, null, null, null);
				for (IOpaqueDocument doc : documents) {
					dm.removeDocument(doc.getGUID());
				}
			} catch (ElexisException e) {

				e.printStackTrace();
			}
		}
		monitor.worked(i / 2);
	}

}
