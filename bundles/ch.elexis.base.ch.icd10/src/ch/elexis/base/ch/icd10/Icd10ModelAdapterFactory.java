package ch.elexis.base.ch.icd10;

import ch.elexis.core.jpa.entities.ICD10;
import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;
import ch.elexis.core.jpa.model.adapter.MappingEntry;
import ch.elexis.core.model.IDiagnosisTree;

public class Icd10ModelAdapterFactory extends AbstractModelAdapterFactory {
	
	private static Icd10ModelAdapterFactory INSTANCE;
	
	public static synchronized Icd10ModelAdapterFactory getInstance(){
		if (INSTANCE == null) {
			INSTANCE = new Icd10ModelAdapterFactory();
		}
		return INSTANCE;
	}
	
	private Icd10ModelAdapterFactory(){
		super();
	}
	
	@Override
	protected void initializeMappings(){
		addMapping(new MappingEntry(IDiagnosisTree.class,
			Icd10Diagnosis.class, ICD10.class));
	}
}
