package ch.elexis.omnivore.model.service.internal;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
import ch.elexis.omnivore.Constants;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.model.TransientCategory;
import ch.elexis.omnivore.model.util.CategoryUtil;

@Component(immediate = true, property = "storeid=ch.elexis.data.store.omnivore")
public class OmnivoreDocumentStore implements IDocumentStore {
	
	private static final String STORE_ID = "ch.elexis.data.store.omnivore";
	
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
			"select distinct category from CH_ELEXIS_OMNIVORE_DATA where mimetype='text/category' and deleted = '0' order by category");
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
		return handle;
	}
	
	@Override
	public ICategory getCategoryDefault(){
		return new TransientCategory(Constants.DEFAULT_CATEGORY);
	}
	
	@Override
	public Optional<Object> getPersistenceObject(IDocument iDocument){
		return Optional.empty();
	}
	
	@Override
	public ICategory createCategory(String name){
		if (name != null) {
			if (CategoryUtil.findCategoriesByName(name).isEmpty()) {
				CategoryUtil.addCategory(name);
			}
		}
		return new TransientCategory(name);
		
	}
	
	@Override
	public void removeCategory(IDocument iDocument, String newCategory)
		throws IllegalStateException{
		if (iDocument.getId() != null && iDocument.getCategory() != null) {
			// check if document to category references exists and ignore current iDocument
			ICategory oldCategory = iDocument.getCategory();
			
			List<IDocumentHandle> existing =
				CategoryUtil.getDocumentsWithCategoryByName(oldCategory.getName());
			if (existing.isEmpty()
				|| (existing.size() == 1 && existing.get(0).getId().equals(iDocument.getId()))) {
				CategoryUtil.removeCategory(oldCategory.getName(), newCategory);
			} else {
				throw new IllegalStateException(
					"at least one document to category reference exists with id: "
						+ existing.get(0).getId());
			}
		}
	}
	
	@Override
	public void renameCategory(ICategory category, String newCategory)
		throws IllegalStateException{
		CategoryUtil.renameCategory(category.getName(), newCategory);
	}
}
