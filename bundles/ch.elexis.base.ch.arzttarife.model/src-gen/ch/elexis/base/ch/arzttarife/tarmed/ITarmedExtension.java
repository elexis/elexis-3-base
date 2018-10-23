/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmed;

import ch.elexis.core.model.Deleteable;
import ch.elexis.core.model.Identifiable;
import java.util.Map;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ITarmed Extension</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension#getLimits <em>Limits</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedExtension()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ITarmedExtension extends Identifiable, Deleteable {

	/**
	 * Returns the value of the '<em><b>Limits</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Limits</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Limits</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedExtension_Limits()
	 * @model transient="true" changeable="false"
	 * @generated
	 */
	Map<String, String> getLimits();
} // ITarmedExtension
