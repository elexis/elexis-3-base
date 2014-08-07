package at.medevit.elexis.ehc.core.internal.document;

import org.openhealthtools.mdht.uml.cda.ch.CDACH;

import ehealthconnector.cda.documents.ch.CdaCh;

public class CdaChImpl extends CdaCh {
	public CdaChImpl(CDACH doc){
		this.doc = doc;
		docRoot.setClinicalDocument(doc);
	}
}
