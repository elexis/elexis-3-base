package ch.elexis.omnivore.model.util;

import static ch.elexis.omnivore.Constants.CATEGORY_MIMETYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.ICategory;
import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.omnivore.Constants;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.model.TransientCategory;
import ch.elexis.omnivore.model.service.OmnivoreModelServiceHolder;

public class CategoryUtil {
	
	public static void addCategory(String name){
		if (findCategoriesByName(name).isEmpty()) {
			IDocumentHandle docHandle =
				OmnivoreModelServiceHolder.get().create(IDocumentHandle.class);
			docHandle.setTitle(name);
			docHandle.setCategory(new TransientCategory(name));
			docHandle.setMimeType(CATEGORY_MIMETYPE);
			OmnivoreModelServiceHolder.get().save(docHandle);
		}
	}
	
	public static List<ICategory> findCategoriesByName(String name){
		List<IDocumentHandle> docs = getCategoriesByName(name);
		List<ICategory> iCategories = new ArrayList<>();
		for (IDocumentHandle docHandle : docs) {
			// categories are IDocumentHandles, use get method to get a TransientCategory
			iCategories.add(docHandle.getCategory());
		}
		return iCategories;
	}
	
	public static List<IDocumentHandle> getCategoriesByName(String name){
		IQuery<IDocumentHandle> query =
			OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
		query.and("category", COMPARATOR.EQUALS, name, true);
		query.and("mimetype", COMPARATOR.EQUALS, CATEGORY_MIMETYPE);
		return query.execute();
	}
	
	public static List<String> getCategoriesNames(){
		INamedQuery<String> findCategoriesQuery = OmnivoreModelServiceHolder.get().getNamedQueryByName(
			String.class, IDocumentHandle.class, "DocHandle.select.category.names");
		List<String> result = findCategoriesQuery.executeWithParameters(Collections.emptyMap());
		return result;
	}
	
	public static ICategory getDefaultCategory(){
		IDocumentHandle existing = findDefaultCategory();
		if (existing == null) {
			addCategory(Constants.DEFAULT_CATEGORY);
			existing = findDefaultCategory();
		}
		return existing.getCategory();
	}
	
	private static IDocumentHandle findDefaultCategory(){
		IQuery<IDocumentHandle> query =
			OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
		query.and("mimetype", COMPARATOR.EQUALS, CATEGORY_MIMETYPE);
		query.and("category", COMPARATOR.EQUALS, Constants.DEFAULT_CATEGORY);
		
		List<IDocumentHandle> existing = query.execute();
		return existing.isEmpty() ? null : existing.get(0);
	}
	
	public static List<IDocumentHandle> getCategories(){
		INamedQuery<IDocumentHandle> findCategoriesQuery =
			OmnivoreModelServiceHolder.get().getNamedQueryByName(IDocumentHandle.class,
				IDocumentHandle.class, "DocHandle.select.categories");
		// filter duplicates, ordered with TreeMap
		TreeMap<String, IDocumentHandle> uniqueMap = new TreeMap<>();
		findCategoriesQuery.executeWithParameters(Collections.emptyMap()).forEach(dh -> {
			if(uniqueMap.containsKey(dh.getTitle())) {
				if (StringUtils.isNotBlank(dh.getCategory().getName())) {
					uniqueMap.put(dh.getTitle(), dh);
				}
			} else {
				uniqueMap.put(dh.getTitle(), dh);
			}
		});
		return new ArrayList<>(uniqueMap.values());
	}
	
	public static List<IDocumentHandle> getDocumentsWithCategoryByName(String name){
		IQuery<IDocumentHandle> query =
			OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
		query.and("mimetype", COMPARATOR.NOT_EQUALS, CATEGORY_MIMETYPE);
		query.and("category", COMPARATOR.EQUALS, name, true);
		return query.execute();
	}
	
	public static void renameCategory(String oldName, String newName){
		String oldname = oldName.trim();
		String newname = newName.trim();
		
		if (findCategoriesByName(newname).isEmpty()) {
			OmnivoreModelServiceHolder.get()
				.executeNativeUpdate("UPDATE CH_ELEXIS_OMNIVORE_DATA SET category='" + newname
					+ "' WHERE category='" + oldname + "'");
			OmnivoreModelServiceHolder.get()
				.executeNativeUpdate("UPDATE CH_ELEXIS_OMNIVORE_DATA SET title='" + newname
					+ "' WHERE title='" + oldname + "' AND mimetype='" + CATEGORY_MIMETYPE + "'");
			LoggerFactory.getLogger(CategoryUtil.class).info("Renaming category [" + oldname
				+ "], moving entries to category [" + newname + "]");
		} else {
			throw new IllegalStateException("Category [" + newname + "] already exists");
		}
	}
	
	public static void removeCategory(String name, String destName){
		OmnivoreModelServiceHolder.get()
			.executeNativeUpdate("UPDATE CH_ELEXIS_OMNIVORE_DATA SET category='" + destName
				+ "' WHERE category='" + name + "'");
		OmnivoreModelServiceHolder.get()
			.executeNativeUpdate("UPDATE CH_ELEXIS_OMNIVORE_DATA SET deleted='1' WHERE title='"
				+ name + "' AND mimetype='" + CATEGORY_MIMETYPE + "'");
		LoggerFactory.getLogger(CategoryUtil.class).info(
			"Removing category [" + name + "], moving entries to category [" + destName + "]");
	}
	
	public static void ensureCategoryAvailability(String category){
		List<ICategory> existing = findCategoriesByName(category);
		if (existing.isEmpty()) {
			addCategory(category);
		}
	}
}
