/**
 */
package ch.elexis.icpc.model.icpc;

import ch.elexis.core.model.IDiagnosisTree;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Code</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcCode#getIcd10 <em>Icd10</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcCode#getCriteria <em>Criteria</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcCode#getInclusion <em>Inclusion</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcCode#getExclusion <em>Exclusion</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcCode#getNote <em>Note</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcCode#getConsider <em>Consider</em>}</li>
 * </ul>
 *
 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcCode()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface IcpcCode extends IDiagnosisTree {

	/**
	 * Returns the value of the '<em><b>Icd10</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Icd10</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Icd10</em>' attribute.
	 * @see #setIcd10(String)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcCode_Icd10()
	 * @model
	 * @generated
	 */
	String getIcd10();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcCode#getIcd10 <em>Icd10</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Icd10</em>' attribute.
	 * @see #getIcd10()
	 * @generated
	 */
	void setIcd10(String value);

	/**
	 * Returns the value of the '<em><b>Criteria</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Criteria</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Criteria</em>' attribute.
	 * @see #setCriteria(String)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcCode_Criteria()
	 * @model
	 * @generated
	 */
	String getCriteria();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcCode#getCriteria <em>Criteria</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Criteria</em>' attribute.
	 * @see #getCriteria()
	 * @generated
	 */
	void setCriteria(String value);

	/**
	 * Returns the value of the '<em><b>Inclusion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Inclusion</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Inclusion</em>' attribute.
	 * @see #setInclusion(String)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcCode_Inclusion()
	 * @model
	 * @generated
	 */
	String getInclusion();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcCode#getInclusion <em>Inclusion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Inclusion</em>' attribute.
	 * @see #getInclusion()
	 * @generated
	 */
	void setInclusion(String value);

	/**
	 * Returns the value of the '<em><b>Exclusion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Exclusion</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclusion</em>' attribute.
	 * @see #setExclusion(String)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcCode_Exclusion()
	 * @model
	 * @generated
	 */
	String getExclusion();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcCode#getExclusion <em>Exclusion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclusion</em>' attribute.
	 * @see #getExclusion()
	 * @generated
	 */
	void setExclusion(String value);

	/**
	 * Returns the value of the '<em><b>Note</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Note</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Note</em>' attribute.
	 * @see #setNote(String)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcCode_Note()
	 * @model
	 * @generated
	 */
	String getNote();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcCode#getNote <em>Note</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Note</em>' attribute.
	 * @see #getNote()
	 * @generated
	 */
	void setNote(String value);

	/**
	 * Returns the value of the '<em><b>Consider</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Consider</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Consider</em>' attribute.
	 * @see #setConsider(String)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcCode_Consider()
	 * @model
	 * @generated
	 */
	String getConsider();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcCode#getConsider <em>Consider</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Consider</em>' attribute.
	 * @see #getConsider()
	 * @generated
	 */
	void setConsider(String value);
} // IcpcCode
