package ch.elexis.omnivore.data.dto;

import java.util.Date;

import ch.elexis.data.dto.AbstractDocumentDTO;
import ch.elexis.data.dto.CategoryDocumentDTO;
import ch.elexis.omnivore.data.DocHandle;
import ch.rgw.tools.TimeTool;

public class DocHandleDocumentDTO extends AbstractDocumentDTO {
	
	public DocHandleDocumentDTO(String storeId){
		setStoreId(storeId);
	}
	
	public DocHandleDocumentDTO(DocHandle docHandle, String storeId){
		this(storeId);
		String[] fetch = new String[]
		{
			DocHandle.FLD_PATID, DocHandle.FLD_TITLE, DocHandle.FLD_MIMETYPE,
			DocHandle.FLD_KEYWORDS, DocHandle.FLD_CAT
		};
		String[] data = new String[fetch.length];
		docHandle.get(fetch, data);
		
		setId(docHandle.getId());
		setLabel(docHandle.getLabel());
		setPatientId(data[0]);
		setTitle(data[1]);
		setMimeType(data[2]);
		setExtension(evaluateExtension(data[2]));
		setKeywords(data[3]);
		setCategory(new CategoryDocumentDTO(data[4]));
		
		setLastchanged(new Date(Long.valueOf(docHandle.get(DocHandle.FLD_LASTUPDATE))));
		setCreated(new TimeTool(docHandle.get(DocHandle.FLD_DATE)).getTime());
	}
}
