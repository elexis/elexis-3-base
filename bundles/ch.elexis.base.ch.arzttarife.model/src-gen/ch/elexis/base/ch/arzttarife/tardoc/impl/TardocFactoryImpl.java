/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tardoc.impl;

import ch.elexis.base.ch.arzttarife.tardoc.*;

import ch.elexis.base.ch.arzttarife.tardoc.model.TardocExclusion;
import ch.elexis.base.ch.arzttarife.tardoc.model.TardocLimitation;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TardocFactoryImpl extends EFactoryImpl implements TardocFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static TardocFactory init() {
		try {
			TardocFactory theTardocFactory = (TardocFactory)EPackage.Registry.INSTANCE.getEFactory(TardocPackage.eNS_URI);
			if (theTardocFactory != null) {
				return theTardocFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new TardocFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TardocFactoryImpl() {
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
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case TardocPackage.MANDANT_TYPE:
				return createMandantTypeFromString(eDataType, initialValue);
			case TardocPackage.TARDOC_LIMITATION:
				return createTardocLimitationFromString(eDataType, initialValue);
			case TardocPackage.TARDOC_EXCLUSION:
				return createTardocExclusionFromString(eDataType, initialValue);
			case TardocPackage.TARDOC_KUMULATION_ART:
				return createTardocKumulationArtFromString(eDataType, initialValue);
			case TardocPackage.TARDOC_KUMULATION_TYP:
				return createTardocKumulationTypFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case TardocPackage.MANDANT_TYPE:
				return convertMandantTypeToString(eDataType, instanceValue);
			case TardocPackage.TARDOC_LIMITATION:
				return convertTardocLimitationToString(eDataType, instanceValue);
			case TardocPackage.TARDOC_EXCLUSION:
				return convertTardocExclusionToString(eDataType, instanceValue);
			case TardocPackage.TARDOC_KUMULATION_ART:
				return convertTardocKumulationArtToString(eDataType, instanceValue);
			case TardocPackage.TARDOC_KUMULATION_TYP:
				return convertTardocKumulationTypToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MandantType createMandantTypeFromString(EDataType eDataType, String initialValue) {
		MandantType result = MandantType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertMandantTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TardocLimitation createTardocLimitationFromString(EDataType eDataType, String initialValue) {
		return (TardocLimitation)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTardocLimitationToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TardocExclusion createTardocExclusionFromString(EDataType eDataType, String initialValue) {
		return (TardocExclusion)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTardocExclusionToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TardocKumulationArt createTardocKumulationArtFromString(EDataType eDataType, String initialValue) {
		return (TardocKumulationArt)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTardocKumulationArtToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TardocKumulationTyp createTardocKumulationTypFromString(EDataType eDataType, String initialValue) {
		return (TardocKumulationTyp)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTardocKumulationTypToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TardocPackage getTardocPackage() {
		return (TardocPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static TardocPackage getPackage() {
		return TardocPackage.eINSTANCE;
	}

} //TardocFactoryImpl
