package ch.elexis.omnivore.data.service.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.exceptions.PersistenceException;
import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IPersistentObject;
import ch.elexis.core.model.ITag;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.dto.CategoryDocumentDTO;
import ch.elexis.data.dto.TagDocumentDTO;
import ch.elexis.omnivore.Constants;
import ch.elexis.omnivore.data.DocHandle;
import ch.elexis.omnivore.data.dto.DocHandleDocumentDTO;
import ch.rgw.tools.JdbcLink.Stm;

@Component
public class OmnivoreDocumentStore implements IDocumentStore {
	
	private static final String STORE_ID = "ch.elexis.data.store.omnivore";
	private static Logger log = LoggerFactory.getLogger(OmnivoreDocumentStore.class);
	
	@Override
	public String getId(){
		return STORE_ID;
	}
	
	@Override
	public String getName(){
		return "Omnivore";
	}
	
	@Override
	public List<IDocument> getDocuments(String patientId, String authorId, ICategory category,
		List<ITag> tag){
		Query<DocHandle> query = new Query<>(DocHandle.class);
		query.add(DocHandle.FLD_PATID, Query.EQUALS, patientId);
		
		if (category != null) {
			query.add(DocHandle.FLD_CAT, Query.EQUALS, category.getName(), true);
		}
		if (tag != null) {
			query.startGroup();
			for (ITag t : tag) {
				query.add(DocHandle.FLD_KEYWORDS, Query.EQUALS, t.getName());
				query.or();
			}
			query.endGroup();
		}
		List<DocHandle> docs = query.execute();
		List<IDocument> results = new ArrayList<>();
		for (DocHandle doc : docs) {
			results.add(new DocHandleDocumentDTO(doc, STORE_ID));
		}
		return results;
	}
	
	@Override
	public List<ICategory> getCategories(){
		Stm stm = PersistentObject.getDefaultConnection().getStatement();
		ResultSet rs = stm
			.query("select distinct category from " + DocHandle.TABLENAME + " order by category");
		List<ICategory> categories = new ArrayList<>();
		try {
			while (rs.next()) {
				String typ = rs.getString("Category");
				if (typ != null) {
					categories.add(new CategoryDocumentDTO(typ));
				}
			}
		} catch (SQLException e) {
			log.error("Error executing distinct docHandle category selection", e);
		}
		PersistentObject.getDefaultConnection().releaseStatement(stm);
		
		return categories;
	}
	
	@Override
	public ICategory createCategory(String name){
		return new CategoryDocumentDTO(name);
	}
	
	@Override
	public void removeCategory(ICategory category) throws IllegalStateException{
		
	}
	
	@Override
	public List<ITag> getTags(){
		Stm stm = PersistentObject.getDefaultConnection().getStatement();
		ResultSet rs = stm.query("select distinct " + DocHandle.FLD_KEYWORDS + " from  "
			+ DocHandle.TABLENAME + " order by " + DocHandle.FLD_KEYWORDS);
		List<ITag> tags = new ArrayList<>();
		try {
			while (rs.next()) {
				String tag = rs.getString(DocHandle.FLD_KEYWORDS);
				if (tag != null) {
					tags.add(new TagDocumentDTO(tag));
				}
			}
		} catch (SQLException e) {
			log.error("Error executing distinct docHandle category selection", e);
		}
		PersistentObject.getDefaultConnection().releaseStatement(stm);
		return tags;
	}
	
	@Override
	public ITag addTag(String name){
		return null;
	}
	
	@Override
	public void removeTag(ITag tag){
		
	}
	
	@Override
	public Optional<IDocument> loadDocument(String id){
		DocHandle doc = DocHandle.load(id);
		if (doc.exists()) {
			return Optional.of(new DocHandleDocumentDTO(doc, STORE_ID));
		}
		return Optional.empty();
	}
	
	@Override
	public void removeDocument(IDocument document){
		DocHandle doc = DocHandle.load(document.getId());
		if (doc.exists()) {
			doc.delete();
		}
	}
	
	@Override
	public IDocument saveDocument(IDocument document) throws ElexisException{
		return save(document, null);
	}
	
	@Override
	public IDocument saveDocument(IDocument document, InputStream content) throws ElexisException{
		return save(document, content);
	}
	
	private IDocument save(IDocument document, InputStream content) throws ElexisException{
		try {
			DocHandle doc = DocHandle.load(document.getId());
			String category =
				document.getCategory() != null ? document.getCategory().getName() : null;
			if (doc.exists()) {
				// update an existing document
				String[] fetch = new String[] {
					DocHandle.FLD_PATID, DocHandle.FLD_TITLE, DocHandle.FLD_MIMETYPE,
					DocHandle.FLD_CAT
				};
				String[] data = new String[] {
					document.getPatientId(), document.getTitle(), document.getMimeType(), category
				};
				doc.set(fetch, data);
			} else {
				// persist a new document
				doc = new DocHandle(category, new byte[1], Patient.load(document.getPatientId()),
					document.getCreated(), document.getTitle(), document.getMimeType(),
					document.getTags().isEmpty() ? null : document.getTags().get(0).getName());
			}
			
			if (content != null) {
				doc.storeContent(IOUtils.toByteArray(content));
			}
			return new DocHandleDocumentDTO(doc, STORE_ID);
		} catch (PersistenceException | IOException e) {
			throw new ElexisException("cannot save", e);
		} finally {
			if (content != null) {
				IOUtils.closeQuietly(content);
			}
		}
	}
	
	@Override
	public Optional<InputStream> loadContent(IDocument document){
		DocHandle doc = DocHandle.load(document.getId());
		if (doc.exists()) {
			try {
				byte[] buf = doc.getContentsAsBytes();
				if (buf != null) {
					return Optional.of(new ByteArrayInputStream(buf));
				}
			} catch (ElexisException e) {
				log.error("Cannot load contents of document id: " + document.getId(), e);
			}
		}
		return Optional.empty();
	}
	
	@Override
	public IDocument createDocument(String patientId, String title, String categoryName){
		DocHandleDocumentDTO docHandleDocumentDTO = new DocHandleDocumentDTO(STORE_ID);
		ICategory iCategory =
			categoryName != null ? new CategoryDocumentDTO(categoryName) : getCategoryDefault();
		docHandleDocumentDTO.setCategory(iCategory);
		docHandleDocumentDTO.setPatientId(patientId);
		docHandleDocumentDTO.setTitle(title);
		return docHandleDocumentDTO;
	}
	
	@Override
	public ICategory getCategoryDefault(){
		return new CategoryDocumentDTO(Constants.DEFAULT_CATEGORY);
	}
	
	@Override
	public Optional<IPersistentObject> getPersistenceObject(IDocument iDocument){
		return Optional.of(DocHandle.load(iDocument.getId()));
	}
	
}
