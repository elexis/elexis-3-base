/**
 */
package ch.elexis.icpc.model.icpc;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import ch.elexis.core.model.ModelPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see ch.elexis.icpc.model.icpc.IcpcFactory
 * @model kind="package"
 * @generated
 */
public interface IcpcPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "icpc";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/icpc";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ch.elexis.icpc.model";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	IcpcPackage eINSTANCE = ch.elexis.icpc.model.icpc.impl.IcpcPackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.icpc.model.icpc.IcpcEncounter <em>Encounter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.icpc.model.icpc.IcpcEncounter
	 * @see ch.elexis.icpc.model.icpc.impl.IcpcPackageImpl#getIcpcEncounter()
	 * @generated
	 */
	int ICPC_ENCOUNTER = 0;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_ENCOUNTER__LASTUPDATE = ModelPackage.IDENTIFIABLE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_ENCOUNTER__DELETED = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Encounter</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_ENCOUNTER__ENCOUNTER = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Episode</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_ENCOUNTER__EPISODE = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Proc</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_ENCOUNTER__PROC = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Diag</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_ENCOUNTER__DIAG = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Rfe</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_ENCOUNTER__RFE = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Encounter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_ENCOUNTER_FEATURE_COUNT = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link ch.elexis.icpc.model.icpc.IcpcEpisode <em>Episode</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.icpc.model.icpc.IcpcEpisode
	 * @see ch.elexis.icpc.model.icpc.impl.IcpcPackageImpl#getIcpcEpisode()
	 * @generated
	 */
	int ICPC_EPISODE = 1;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_EPISODE__DELETED = ModelPackage.WITH_EXT_INFO_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_EPISODE__LASTUPDATE = ModelPackage.WITH_EXT_INFO_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_EPISODE__TITLE = ModelPackage.WITH_EXT_INFO_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_EPISODE__NUMBER = ModelPackage.WITH_EXT_INFO_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Start Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_EPISODE__START_DATE = ModelPackage.WITH_EXT_INFO_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_EPISODE__STATUS = ModelPackage.WITH_EXT_INFO_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Patient</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_EPISODE__PATIENT = ModelPackage.WITH_EXT_INFO_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Diagnosis</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_EPISODE__DIAGNOSIS = ModelPackage.WITH_EXT_INFO_FEATURE_COUNT + 7;

	/**
	 * The number of structural features of the '<em>Episode</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_EPISODE_FEATURE_COUNT = ModelPackage.WITH_EXT_INFO_FEATURE_COUNT + 8;

	/**
	 * The meta object id for the '{@link ch.elexis.icpc.model.icpc.IcpcCode <em>Code</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.icpc.model.icpc.IcpcCode
	 * @see ch.elexis.icpc.model.icpc.impl.IcpcPackageImpl#getIcpcCode()
	 * @generated
	 */
	int ICPC_CODE = 2;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_CODE__CODE = ModelPackage.IDIAGNOSIS_TREE__CODE;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_CODE__TEXT = ModelPackage.IDIAGNOSIS_TREE__TEXT;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_CODE__LASTUPDATE = ModelPackage.IDIAGNOSIS_TREE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_CODE__DESCRIPTION = ModelPackage.IDIAGNOSIS_TREE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_CODE__PARENT = ModelPackage.IDIAGNOSIS_TREE__PARENT;

	/**
	 * The feature id for the '<em><b>Children</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_CODE__CHILDREN = ModelPackage.IDIAGNOSIS_TREE__CHILDREN;

	/**
	 * The feature id for the '<em><b>Icd10</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_CODE__ICD10 = ModelPackage.IDIAGNOSIS_TREE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Criteria</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_CODE__CRITERIA = ModelPackage.IDIAGNOSIS_TREE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Inclusion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_CODE__INCLUSION = ModelPackage.IDIAGNOSIS_TREE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Exclusion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_CODE__EXCLUSION = ModelPackage.IDIAGNOSIS_TREE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Note</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_CODE__NOTE = ModelPackage.IDIAGNOSIS_TREE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Consider</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_CODE__CONSIDER = ModelPackage.IDIAGNOSIS_TREE_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Code</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICPC_CODE_FEATURE_COUNT = ModelPackage.IDIAGNOSIS_TREE_FEATURE_COUNT + 6;

	/**
	 * Returns the meta object for class '{@link ch.elexis.icpc.model.icpc.IcpcEncounter <em>Encounter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Encounter</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcEncounter
	 * @generated
	 */
	EClass getIcpcEncounter();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getEncounter <em>Encounter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Encounter</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcEncounter#getEncounter()
	 * @see #getIcpcEncounter()
	 * @generated
	 */
	EReference getIcpcEncounter_Encounter();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getEpisode <em>Episode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Episode</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcEncounter#getEpisode()
	 * @see #getIcpcEncounter()
	 * @generated
	 */
	EReference getIcpcEncounter_Episode();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getProc <em>Proc</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Proc</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcEncounter#getProc()
	 * @see #getIcpcEncounter()
	 * @generated
	 */
	EReference getIcpcEncounter_Proc();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getDiag <em>Diag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Diag</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcEncounter#getDiag()
	 * @see #getIcpcEncounter()
	 * @generated
	 */
	EReference getIcpcEncounter_Diag();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getRfe <em>Rfe</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Rfe</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcEncounter#getRfe()
	 * @see #getIcpcEncounter()
	 * @generated
	 */
	EReference getIcpcEncounter_Rfe();

	/**
	 * Returns the meta object for class '{@link ch.elexis.icpc.model.icpc.IcpcEpisode <em>Episode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Episode</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcEpisode
	 * @generated
	 */
	EClass getIcpcEpisode();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getTitle <em>Title</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Title</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcEpisode#getTitle()
	 * @see #getIcpcEpisode()
	 * @generated
	 */
	EAttribute getIcpcEpisode_Title();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getNumber <em>Number</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Number</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcEpisode#getNumber()
	 * @see #getIcpcEpisode()
	 * @generated
	 */
	EAttribute getIcpcEpisode_Number();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getStartDate <em>Start Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Start Date</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcEpisode#getStartDate()
	 * @see #getIcpcEpisode()
	 * @generated
	 */
	EAttribute getIcpcEpisode_StartDate();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getStatus <em>Status</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Status</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcEpisode#getStatus()
	 * @see #getIcpcEpisode()
	 * @generated
	 */
	EAttribute getIcpcEpisode_Status();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getPatient <em>Patient</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Patient</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcEpisode#getPatient()
	 * @see #getIcpcEpisode()
	 * @generated
	 */
	EReference getIcpcEpisode_Patient();

	/**
	 * Returns the meta object for the reference list '{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getDiagnosis <em>Diagnosis</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Diagnosis</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcEpisode#getDiagnosis()
	 * @see #getIcpcEpisode()
	 * @generated
	 */
	EReference getIcpcEpisode_Diagnosis();

	/**
	 * Returns the meta object for class '{@link ch.elexis.icpc.model.icpc.IcpcCode <em>Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Code</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcCode
	 * @generated
	 */
	EClass getIcpcCode();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.icpc.model.icpc.IcpcCode#getIcd10 <em>Icd10</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Icd10</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcCode#getIcd10()
	 * @see #getIcpcCode()
	 * @generated
	 */
	EAttribute getIcpcCode_Icd10();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.icpc.model.icpc.IcpcCode#getCriteria <em>Criteria</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Criteria</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcCode#getCriteria()
	 * @see #getIcpcCode()
	 * @generated
	 */
	EAttribute getIcpcCode_Criteria();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.icpc.model.icpc.IcpcCode#getInclusion <em>Inclusion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Inclusion</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcCode#getInclusion()
	 * @see #getIcpcCode()
	 * @generated
	 */
	EAttribute getIcpcCode_Inclusion();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.icpc.model.icpc.IcpcCode#getExclusion <em>Exclusion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclusion</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcCode#getExclusion()
	 * @see #getIcpcCode()
	 * @generated
	 */
	EAttribute getIcpcCode_Exclusion();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.icpc.model.icpc.IcpcCode#getNote <em>Note</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Note</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcCode#getNote()
	 * @see #getIcpcCode()
	 * @generated
	 */
	EAttribute getIcpcCode_Note();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.icpc.model.icpc.IcpcCode#getConsider <em>Consider</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Consider</em>'.
	 * @see ch.elexis.icpc.model.icpc.IcpcCode#getConsider()
	 * @see #getIcpcCode()
	 * @generated
	 */
	EAttribute getIcpcCode_Consider();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	IcpcFactory getIcpcFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link ch.elexis.icpc.model.icpc.IcpcEncounter <em>Encounter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.icpc.model.icpc.IcpcEncounter
		 * @see ch.elexis.icpc.model.icpc.impl.IcpcPackageImpl#getIcpcEncounter()
		 * @generated
		 */
		EClass ICPC_ENCOUNTER = eINSTANCE.getIcpcEncounter();

		/**
		 * The meta object literal for the '<em><b>Encounter</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ICPC_ENCOUNTER__ENCOUNTER = eINSTANCE.getIcpcEncounter_Encounter();

		/**
		 * The meta object literal for the '<em><b>Episode</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ICPC_ENCOUNTER__EPISODE = eINSTANCE.getIcpcEncounter_Episode();

		/**
		 * The meta object literal for the '<em><b>Proc</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ICPC_ENCOUNTER__PROC = eINSTANCE.getIcpcEncounter_Proc();

		/**
		 * The meta object literal for the '<em><b>Diag</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ICPC_ENCOUNTER__DIAG = eINSTANCE.getIcpcEncounter_Diag();

		/**
		 * The meta object literal for the '<em><b>Rfe</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ICPC_ENCOUNTER__RFE = eINSTANCE.getIcpcEncounter_Rfe();

		/**
		 * The meta object literal for the '{@link ch.elexis.icpc.model.icpc.IcpcEpisode <em>Episode</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.icpc.model.icpc.IcpcEpisode
		 * @see ch.elexis.icpc.model.icpc.impl.IcpcPackageImpl#getIcpcEpisode()
		 * @generated
		 */
		EClass ICPC_EPISODE = eINSTANCE.getIcpcEpisode();

		/**
		 * The meta object literal for the '<em><b>Title</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICPC_EPISODE__TITLE = eINSTANCE.getIcpcEpisode_Title();

		/**
		 * The meta object literal for the '<em><b>Number</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICPC_EPISODE__NUMBER = eINSTANCE.getIcpcEpisode_Number();

		/**
		 * The meta object literal for the '<em><b>Start Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICPC_EPISODE__START_DATE = eINSTANCE.getIcpcEpisode_StartDate();

		/**
		 * The meta object literal for the '<em><b>Status</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICPC_EPISODE__STATUS = eINSTANCE.getIcpcEpisode_Status();

		/**
		 * The meta object literal for the '<em><b>Patient</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ICPC_EPISODE__PATIENT = eINSTANCE.getIcpcEpisode_Patient();

		/**
		 * The meta object literal for the '<em><b>Diagnosis</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ICPC_EPISODE__DIAGNOSIS = eINSTANCE.getIcpcEpisode_Diagnosis();

		/**
		 * The meta object literal for the '{@link ch.elexis.icpc.model.icpc.IcpcCode <em>Code</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.icpc.model.icpc.IcpcCode
		 * @see ch.elexis.icpc.model.icpc.impl.IcpcPackageImpl#getIcpcCode()
		 * @generated
		 */
		EClass ICPC_CODE = eINSTANCE.getIcpcCode();

		/**
		 * The meta object literal for the '<em><b>Icd10</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICPC_CODE__ICD10 = eINSTANCE.getIcpcCode_Icd10();

		/**
		 * The meta object literal for the '<em><b>Criteria</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICPC_CODE__CRITERIA = eINSTANCE.getIcpcCode_Criteria();

		/**
		 * The meta object literal for the '<em><b>Inclusion</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICPC_CODE__INCLUSION = eINSTANCE.getIcpcCode_Inclusion();

		/**
		 * The meta object literal for the '<em><b>Exclusion</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICPC_CODE__EXCLUSION = eINSTANCE.getIcpcCode_Exclusion();

		/**
		 * The meta object literal for the '<em><b>Note</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICPC_CODE__NOTE = eINSTANCE.getIcpcCode_Note();

		/**
		 * The meta object literal for the '<em><b>Consider</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICPC_CODE__CONSIDER = eINSTANCE.getIcpcCode_Consider();

	}

} //IcpcPackage
