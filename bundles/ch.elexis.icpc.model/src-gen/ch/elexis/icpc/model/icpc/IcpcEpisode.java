/**
 */
package ch.elexis.icpc.model.icpc;

import java.util.List;

import ch.elexis.core.model.Deleteable;
import ch.elexis.core.model.IDiagnosis;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.WithExtInfo;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Episode</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getTitle <em>Title</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getNumber <em>Number</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getStartDate <em>Start Date</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getStatus <em>Status</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getPatient <em>Patient</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getDiagnosis <em>Diagnosis</em>}</li>
 * </ul>
 *
 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcEpisode()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface IcpcEpisode extends WithExtInfo, Deleteable, Identifiable {
	/**
	 * Returns the value of the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Title</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Title</em>' attribute.
	 * @see #setTitle(String)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcEpisode_Title()
	 * @model
	 * @generated
	 */
	String getTitle();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getTitle <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Title</em>' attribute.
	 * @see #getTitle()
	 * @generated
	 */
	void setTitle(String value);

	/**
	 * Returns the value of the '<em><b>Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Number</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Number</em>' attribute.
	 * @see #setNumber(String)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcEpisode_Number()
	 * @model
	 * @generated
	 */
	String getNumber();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getNumber <em>Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Number</em>' attribute.
	 * @see #getNumber()
	 * @generated
	 */
	void setNumber(String value);

	/**
	 * Returns the value of the '<em><b>Start Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Start Date</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Start Date</em>' attribute.
	 * @see #setStartDate(String)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcEpisode_StartDate()
	 * @model
	 * @generated
	 */
	String getStartDate();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getStartDate <em>Start Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Start Date</em>' attribute.
	 * @see #getStartDate()
	 * @generated
	 */
	void setStartDate(String value);

	/**
	 * Returns the value of the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Status</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Status</em>' attribute.
	 * @see #setStatus(int)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcEpisode_Status()
	 * @model
	 * @generated
	 */
	int getStatus();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getStatus <em>Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Status</em>' attribute.
	 * @see #getStatus()
	 * @generated
	 */
	void setStatus(int value);

	/**
	 * Returns the value of the '<em><b>Patient</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Patient</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Patient</em>' reference.
	 * @see #setPatient(IPatient)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcEpisode_Patient()
	 * @model annotation="http://elexis.info/jpa/entity/attribute/mapping attributeName='patientKontakt'"
	 * @generated
	 */
	IPatient getPatient();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcEpisode#getPatient <em>Patient</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Patient</em>' reference.
	 * @see #getPatient()
	 * @generated
	 */
	void setPatient(IPatient value);

	/**
	 * Returns the value of the '<em><b>Diagnosis</b></em>' reference list.
	 * The list contents are of type {@link ch.elexis.core.model.IDiagnosis}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Diagnosis</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Diagnosis</em>' reference list.
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcEpisode_Diagnosis()
	 * @model changeable="false"
	 * @generated
	 */
	List<IDiagnosis> getDiagnosis();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosisRequired="true"
	 * @generated
	 */
	void addDiagnosis(IDiagnosis diagnosis);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosisRequired="true"
	 * @generated
	 */
	void removeDiagnosis(IDiagnosis diagnosis);

} // IcpcEpisode
