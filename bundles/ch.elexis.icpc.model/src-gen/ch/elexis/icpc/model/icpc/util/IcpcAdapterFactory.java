/**
 */
package ch.elexis.icpc.model.icpc.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import ch.elexis.core.model.Deleteable;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IDiagnosis;
import ch.elexis.core.model.IDiagnosisTree;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.WithExtInfo;
import ch.elexis.icpc.model.icpc.IcpcCode;
import ch.elexis.icpc.model.icpc.IcpcEncounter;
import ch.elexis.icpc.model.icpc.IcpcEpisode;
import ch.elexis.icpc.model.icpc.IcpcPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see ch.elexis.icpc.model.icpc.IcpcPackage
 * @generated
 */
public class IcpcAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static IcpcPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IcpcAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = IcpcPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IcpcSwitch<Adapter> modelSwitch =
		new IcpcSwitch<Adapter>() {
			@Override
			public Adapter caseIcpcEncounter(IcpcEncounter object) {
				return createIcpcEncounterAdapter();
			}
			@Override
			public Adapter caseIcpcEpisode(IcpcEpisode object) {
				return createIcpcEpisodeAdapter();
			}
			@Override
			public Adapter caseIcpcCode(IcpcCode object) {
				return createIcpcCodeAdapter();
			}
			@Override
			public Adapter caseIdentifiable(Identifiable object) {
				return createIdentifiableAdapter();
			}
			@Override
			public Adapter caseDeleteable(Deleteable object) {
				return createDeleteableAdapter();
			}
			@Override
			public Adapter caseWithExtInfo(WithExtInfo object) {
				return createWithExtInfoAdapter();
			}
			@Override
			public Adapter caseICodeElement(ICodeElement object) {
				return createICodeElementAdapter();
			}
			@Override
			public Adapter caseIDiagnosis(IDiagnosis object) {
				return createIDiagnosisAdapter();
			}
			@Override
			public Adapter caseIDiagnosisTree(IDiagnosisTree object) {
				return createIDiagnosisTreeAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link ch.elexis.icpc.model.icpc.IcpcEncounter <em>Encounter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see ch.elexis.icpc.model.icpc.IcpcEncounter
	 * @generated
	 */
	public Adapter createIcpcEncounterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link ch.elexis.icpc.model.icpc.IcpcEpisode <em>Episode</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see ch.elexis.icpc.model.icpc.IcpcEpisode
	 * @generated
	 */
	public Adapter createIcpcEpisodeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link ch.elexis.icpc.model.icpc.IcpcCode <em>Code</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see ch.elexis.icpc.model.icpc.IcpcCode
	 * @generated
	 */
	public Adapter createIcpcCodeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link ch.elexis.core.model.Identifiable <em>Identifiable</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see ch.elexis.core.model.Identifiable
	 * @generated
	 */
	public Adapter createIdentifiableAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link ch.elexis.core.model.Deleteable <em>Deleteable</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see ch.elexis.core.model.Deleteable
	 * @generated
	 */
	public Adapter createDeleteableAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link ch.elexis.core.model.WithExtInfo <em>With Ext Info</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see ch.elexis.core.model.WithExtInfo
	 * @generated
	 */
	public Adapter createWithExtInfoAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link ch.elexis.core.model.ICodeElement <em>ICode Element</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see ch.elexis.core.model.ICodeElement
	 * @generated
	 */
	public Adapter createICodeElementAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link ch.elexis.core.model.IDiagnosis <em>IDiagnosis</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see ch.elexis.core.model.IDiagnosis
	 * @generated
	 */
	public Adapter createIDiagnosisAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link ch.elexis.core.model.IDiagnosisTree <em>IDiagnosis Tree</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see ch.elexis.core.model.IDiagnosisTree
	 * @generated
	 */
	public Adapter createIDiagnosisTreeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //IcpcAdapterFactory
