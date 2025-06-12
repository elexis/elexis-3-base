/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmed.impl;

import ch.elexis.base.ch.arzttarife.tarmed.*;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import ch.elexis.base.ch.arzttarife.tarmed.MandantType;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedFactory;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationTyp;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExclusion;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLimitation;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TarmedFactoryImpl extends EFactoryImpl implements TarmedFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static TarmedFactory init() {
		try {
			TarmedFactory theTarmedFactory = (TarmedFactory)EPackage.Registry.INSTANCE.getEFactory(TarmedPackage.eNS_URI);
			if (theTarmedFactory != null) {
				return theTarmedFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new TarmedFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TarmedFactoryImpl() {
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
			case TarmedPackage.MANDANT_TYPE:
				return createMandantTypeFromString(eDataType, initialValue);
			case TarmedPackage.TARMED_LIMITATION:
				return createTarmedLimitationFromString(eDataType, initialValue);
			case TarmedPackage.TARMED_EXCLUSION:
				return createTarmedExclusionFromString(eDataType, initialValue);
			case TarmedPackage.TARMED_KUMULATION_ART:
				return createTarmedKumulationArtFromString(eDataType, initialValue);
			case TarmedPackage.TARMED_KUMULATION_TYP:
				return createTarmedKumulationTypFromString(eDataType, initialValue);
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
			case TarmedPackage.MANDANT_TYPE:
				return convertMandantTypeToString(eDataType, instanceValue);
			case TarmedPackage.TARMED_LIMITATION:
				return convertTarmedLimitationToString(eDataType, instanceValue);
			case TarmedPackage.TARMED_EXCLUSION:
				return convertTarmedExclusionToString(eDataType, instanceValue);
			case TarmedPackage.TARMED_KUMULATION_ART:
				return convertTarmedKumulationArtToString(eDataType, instanceValue);
			case TarmedPackage.TARMED_KUMULATION_TYP:
				return convertTarmedKumulationTypToString(eDataType, instanceValue);
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
	public TarmedLimitation createTarmedLimitationFromString(EDataType eDataType, String initialValue) {
		return (TarmedLimitation)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTarmedLimitationToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TarmedExclusion createTarmedExclusionFromString(EDataType eDataType, String initialValue) {
		return (TarmedExclusion)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTarmedExclusionToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TarmedKumulationArt createTarmedKumulationArtFromString(EDataType eDataType, String initialValue) {
		return (TarmedKumulationArt)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTarmedKumulationArtToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TarmedKumulationTyp createTarmedKumulationTypFromString(EDataType eDataType, String initialValue) {
		return (TarmedKumulationTyp)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTarmedKumulationTypToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TarmedPackage getTarmedPackage() {
		return (TarmedPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static TarmedPackage getPackage() {
		return TarmedPackage.eINSTANCE;
	}

} //TarmedFactoryImpl
