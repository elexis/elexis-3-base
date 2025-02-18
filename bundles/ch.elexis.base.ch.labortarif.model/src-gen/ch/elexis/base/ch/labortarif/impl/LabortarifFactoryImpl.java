/**
 */
package ch.elexis.base.ch.labortarif.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import ch.elexis.base.ch.labortarif.LabortarifFactory;
import ch.elexis.base.ch.labortarif.LabortarifPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class LabortarifFactoryImpl extends EFactoryImpl implements LabortarifFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static LabortarifFactory init() {
		try {
			LabortarifFactory theLabortarifFactory = (LabortarifFactory)EPackage.Registry.INSTANCE.getEFactory(LabortarifPackage.eNS_URI);
			if (theLabortarifFactory != null) {
				return theLabortarifFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new LabortarifFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LabortarifFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LabortarifPackage getLabortarifPackage() {
		return (LabortarifPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static LabortarifPackage getPackage() {
		return LabortarifPackage.eINSTANCE;
	}

} //LabortarifFactoryImpl
