/**
 */
package ch.elexis.icpc.model.icpc;

import ch.elexis.core.model.Deleteable;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.Identifiable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Encounter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getEncounter <em>Encounter</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getEpisode <em>Episode</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getProc <em>Proc</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getDiag <em>Diag</em>}</li>
 *   <li>{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getRfe <em>Rfe</em>}</li>
 * </ul>
 *
 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcEncounter()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface IcpcEncounter extends Identifiable, Deleteable {
	/**
	 * Returns the value of the '<em><b>Encounter</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Encounter</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Encounter</em>' reference.
	 * @see #setEncounter(IEncounter)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcEncounter_Encounter()
	 * @model
	 * @generated
	 */
	IEncounter getEncounter();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getEncounter <em>Encounter</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Encounter</em>' reference.
	 * @see #getEncounter()
	 * @generated
	 */
	void setEncounter(IEncounter value);

	/**
	 * Returns the value of the '<em><b>Episode</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Episode</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Episode</em>' reference.
	 * @see #setEpisode(IcpcEpisode)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcEncounter_Episode()
	 * @model
	 * @generated
	 */
	IcpcEpisode getEpisode();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getEpisode <em>Episode</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Episode</em>' reference.
	 * @see #getEpisode()
	 * @generated
	 */
	void setEpisode(IcpcEpisode value);

	/**
	 * Returns the value of the '<em><b>Proc</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Proc</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Proc</em>' reference.
	 * @see #setProc(IcpcCode)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcEncounter_Proc()
	 * @model
	 * @generated
	 */
	IcpcCode getProc();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getProc <em>Proc</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Proc</em>' reference.
	 * @see #getProc()
	 * @generated
	 */
	void setProc(IcpcCode value);

	/**
	 * Returns the value of the '<em><b>Diag</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Diag</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Diag</em>' reference.
	 * @see #setDiag(IcpcCode)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcEncounter_Diag()
	 * @model
	 * @generated
	 */
	IcpcCode getDiag();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getDiag <em>Diag</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Diag</em>' reference.
	 * @see #getDiag()
	 * @generated
	 */
	void setDiag(IcpcCode value);

	/**
	 * Returns the value of the '<em><b>Rfe</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rfe</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rfe</em>' reference.
	 * @see #setRfe(IcpcCode)
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#getIcpcEncounter_Rfe()
	 * @model
	 * @generated
	 */
	IcpcCode getRfe();

	/**
	 * Sets the value of the '{@link ch.elexis.icpc.model.icpc.IcpcEncounter#getRfe <em>Rfe</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rfe</em>' reference.
	 * @see #getRfe()
	 * @generated
	 */
	void setRfe(IcpcCode value);

} // IcpcEncounter
