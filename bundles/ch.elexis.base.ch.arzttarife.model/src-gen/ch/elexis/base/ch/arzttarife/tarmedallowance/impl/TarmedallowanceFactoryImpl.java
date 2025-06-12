/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmedallowance.impl;

import ch.elexis.base.ch.arzttarife.tarmedallowance.*;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import ch.elexis.base.ch.arzttarife.tarmedallowance.TarmedallowanceFactory;
import ch.elexis.base.ch.arzttarife.tarmedallowance.TarmedallowancePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TarmedallowanceFactoryImpl extends EFactoryImpl implements TarmedallowanceFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static TarmedallowanceFactory init() {
		try {
			TarmedallowanceFactory theTarmedallowanceFactory = (TarmedallowanceFactory)EPackage.Registry.INSTANCE.getEFactory(TarmedallowancePackage.eNS_URI);
			if (theTarmedallowanceFactory != null) {
				return theTarmedallowanceFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new TarmedallowanceFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TarmedallowanceFactoryImpl() {
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
	public TarmedallowancePackage getTarmedallowancePackage() {
		return (TarmedallowancePackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static TarmedallowancePackage getPackage() {
		return TarmedallowancePackage.eINSTANCE;
	}

} //TarmedallowanceFactoryImpl
