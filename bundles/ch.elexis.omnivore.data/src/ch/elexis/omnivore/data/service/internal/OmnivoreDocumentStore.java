package ch.elexis.omnivore.data.service.internal;

import static ch.elexis.omnivore.Constants.CATEGORY_MIMETYPE;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.exceptions.PersistenceException;
import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ITag;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.data.Query;
import ch.elexis.data.dto.CategoryDocumentDTO;
import ch.elexis.omnivore.Constants;
import ch.elexis.omnivore.data.DocHandle;
import ch.elexis.omnivore.data.model.IDocumentHandle;
import ch.elexis.omnivore.data.model.TransientCategory;

@Component(property = "storeid=ch.elexis.data.store.omnivore")
public class OmnivoreDocumentStore implements IDocumentStore {
	
	private static final String STORE_ID = "ch.elexis.data.store.omnivore";
	private static Logger log = LoggerFactory.getLogger(OmnivoreDocumentStore.class);
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	private IModelService coreModelService;
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.omnivore.data.model)")
	private IModelService modelService;
	
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
		
		Optional<IPatient> patient = coreModelService.load(patientId, IPatient.class);
		if (patient.isPresent()) {
			IQuery<IDocumentHandle> query = modelService.getQuery(IDocumentHandle.class);
			query.and(ModelPackage.Literals.IDOCUMENT__PATIENT, COMPARATOR.EQUALS, patient.get());
			
			if (authorId != null) {
				Optional<IContact> author = coreModelService.load(authorId, IContact.class);
				author.ifPresent(a -> {
					query.and(ModelPackage.Literals.IDOCUMENT__AUTHOR, COMPARATOR.EQUALS, a);
				});
			}
			if (category != null && category.getName() != null) {
				query.and(ModelPackage.Literals.IDOCUMENT__CATEGORY, COMPARATOR.EQUALS,
					category.getName());
			}
			
			@SuppressWarnings("unchecked")
			List<IDocument> results = (List<IDocument>) ((List<?>) query.execute());
			results.parallelStream().forEach(d -> d.setStoreId(STORE_ID));
			return results;
		}
		return Collections.emptyList();
	}
	
	@Override
	public List<ICategory> getCategories(){
		Stream<?> resultStream = modelService.executeNativeQuery(
			"select distinct category from CH_ELEXIS_OMNIVORE_DATA where deleted = '0' order by category");
		return resultStream.filter(o -> o instanceof String)
			.map(o -> new TransientCategory((String) o)).collect(Collectors.toList());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Optional<IDocument> loadDocument(String id){
		return (Optional<IDocument>) (Optional<?>) modelService.load(id, IDocumentHandle.class);
	}
	
	@Override
	public void removeDocument(IDocument document){
		Optional<IDocumentHandle> existing =
			modelService.load(document.getId(), IDocumentHandle.class);
		existing.ifPresent(d -> {
			modelService.delete(d);
		});
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
			if (content != null) {
				document.setContent(content);
			}
			modelService.save(document);
			return document;
		} catch (PersistenceException e) {
			throw new ElexisException("cannot save", e);
		}
	}
	
	@Override
	public Optional<InputStream> loadContent(IDocument document){
		return Optional.ofNullable(document.getContent());
	}
	
	@Override
	public IDocument createDocument(String patientId, String title, String categoryName){
		IDocumentHandle handle = modelService.create(IDocumentHandle.class);
		handle.setStoreId(STORE_ID);
		handle.setTitle(title);
		handle.setPatient(coreModelService.load(patientId, IPatient.class).orElse(null));
		ICategory iCategory =
			categoryName != null ? new TransientCategory(categoryName) : getCategoryDefault();
		handle.setCategory(iCategory);
		modelService.save(handle);
		return handle;
	}
	
	@Override
	public ICategory getCategoryDefault(){
		return new CategoryDocumentDTO(Constants.DEFAULT_CATEGORY);
	}
	
	@Override
	public Optional<Object> getPersistenceObject(IDocument iDocument){
		return Optional.of(DocHandle.load(iDocument.getId()));
	}
	
	@Override
	public ICategory createCategory(String name){
		if (name != null) {
			if (findCategoriesByName(name).isEmpty()) {
				DocHandle.addMainCategory(name);
			}
		}
		return new CategoryDocumentDTO(name);
		
	}

	private List<ICategory> findCategoriesByName(String name){
		Query<DocHandle> query = new Query<>(DocHandle.class);
		query.add(DocHandle.FLD_CAT, Query.EQUALS, name, true);
		query.add(DocHandle.FLD_MIMETYPE, Query.EQUALS, CATEGORY_MIMETYPE);
		List<DocHandle> docs = query.execute();
		List<ICategory> iCategories = new ArrayList<>();
		for (DocHandle docHandle : docs) {
			iCategories.add(new CategoryDocumentDTO(docHandle.getCategoryName()));
		}
		return iCategories;
	}
	
	@Override
	public void removeCategory(IDocument iDocument, String newCategory)
		throws IllegalStateException{
		if (iDocument.getId() != null && iDocument.getCategory() != null) {
			// check if document to category references exists and ignore current iDocument
			ICategory oldCategory = iDocument.getCategory();
			Query<DocHandle> query = new Query<>(DocHandle.class);
			query.add(DocHandle.FLD_CAT, Query.EQUALS, oldCategory.getName(), true);
			query.add(DocHandle.FLD_MIMETYPE, Query.NOT_EQUAL, CATEGORY_MIMETYPE);
			query.add(DocHandle.FLD_ID, Query.NOT_EQUAL, iDocument.getId());
			List<DocHandle> docs = query.execute();
			if (!docs.isEmpty()) {
				throw new IllegalStateException(
					"at least one document to category reference exists with id: "
						+ docs.get(0).getId());
			}
			DocHandle.removeCategory(oldCategory.getName(), newCategory);
		}
	}
	
	@Override
	public void renameCategory(ICategory category, String newCategory)
		throws IllegalStateException{
		DocHandle.renameCategory(category.getName(), newCategory);
	}
	
}
