/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.interfaces;

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.constants.SystemConstants;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht;

public interface IGDTCommunicationPartner {

	/**
	 * The connection type of this communication partner. See
	 * {@link SystemConstants}
	 * 
	 * @return int connectionType
	 */
	int getConnectionType();

	/**
	 * The connection string in case of serial communication
	 * {@link SystemConstants#SERIAL_COMMUNICATION} e.g. /dev/ttyS0,9600,n,8,1
	 * 
	 * @return String the connection String
	 */
	String getConnectionString();

	/**
	 * The incoming directory for GDT files, only used if
	 * {@link IGDTCommunicationPartner#getConnectionType} is of type
	 * {@link SystemConstants#FILE_COMMUNICATION}
	 * 
	 * @return String containing the respective directory location
	 */
	String getIncomingDirectory();

	/**
	 * The outgoing directory for GDT files, only used if
	 * {@link IGDTCommunicationPartner#getConnectionType} is of type
	 * {@link SystemConstants#FILE_COMMUNICATION}
	 * 
	 * @return String containing the respective directory location
	 */
	String getOutgoingDirectory();

	/**
	 * The required file communication type, i.e. fixed or counting See
	 * {@link GDTConstants#GDT_FILETRANSFER_TYP_FEST} and
	 * {@link GDTConstants#GDT_FILETRANSFER_TYPE_HOCHZAEHLEND}
	 * 
	 * @return either {@link GDTConstants#GDT_FILETRANSFER_TYP_FEST} or
	 *         {@link GDTConstants#GDT_FILETRANSFER_TYPE_HOCHZAEHLEND}
	 */
	String getRequiredFileType();

	/**
	 * If the connection type is set to
	 * {@link SystemConstants#FILE_COMMUNICATION} in
	 * {@link #getConnectionType()} and the required file type is
	 * {@link GDTConstants#GDT_FILETRANSFER_TYP_FEST} in
	 * {@link #getRequiredFileType()}, a fixed file name for communication has
	 * to be returned here.
	 * 
	 * Otherwise this has to be <code>null</code>
	 * 
	 * @return the file name to be used (including the prefix <code>.gdt</code>), if applicable, else <code>null</code>
	 */
	String getFixedCommmunicationFileName();

	/**
	 * An array containing the values supported within field
	 * {@link GDTConstants#FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD}
	 * 
	 * @return String[] with the supported values, or <code>null</code> if not
	 *         applicable or required
	 * @see If no values are given, the 8402 will not be required for
	 *      successfull call of "Neue Untersuchung anfordern"
	 */
	String[] getSupported8402values();

	/**
	 * An array containing the description of the values supported within field
	 * {@link GDTConstants#FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD}
	 * 
	 * @return String[] the description of the values returned by
	 *         {@link IGDTCommunicationPartner#getSupported8402values}, or
	 *         <code>null</code> if not applicable or required
	 */
	String[] getSupported8402valuesDescription();

	/**
	 * An array containing a detail description of the respective field. Will be
	 * shown during the Auto-Complete selection in a separate field. Use this to
	 * further describe the happenings on sending a message.
	 * 
	 * @return String[] detailed description of the values returned by
	 *         {@link IGDTCommunicationPartner#getSupported8402values}, or
	 *         <code>null</code> if not applicableor required
	 */
	String[] getSupported8402valuesDetailDescription();

	/**
	 * Label denoting the name of the communication partner, has to be < 60
	 * chars will be used to show to the user and to store as remote
	 * identification in the logfile
	 * 
	 * @return
	 */
	String getLabel();

	/**
	 * The default incoming charset as used by the specific communication
	 * partner. See {@link GDTConstants#ZEICHENSATZ_*}
	 * 
	 * If not set {@link GDTConstants#ZEICHENSATZ_IBM_CP_437_CHARSET} is
	 * assumed.
	 * 
	 * @return int denoting the respective charset, 0 to use system default
	 */
	int getIncomingDefaultCharset();

	/**
	 * The default outgoing charset as used by the specific communication
	 * partner. See {@link GDTConstants#ZEICHENSATZ_*}
	 * 
	 * If not set, the default charset of the (GDT) system will be used, which
	 * is {@code cp437}. See {@link GDTConstants#ZEICHENSATZ_IBM_CP_437_CHARSET}
	 * 
	 * @return int denoting the respective charset, 0 to use system default
	 */
	int getOutgoingDefaultCharset();

	/**
	 * Call a program after creating a GDT package to a specific communication partner. This hook
	 * can be used, if a certain program needs to be called to handle the generated GDT
	 * Satznachricht.
	 * 
	 * If not null this program will be executed any time a message has been written to this
	 * communication partner.
	 * 
	 * This is only applicable for communication of type {@link SystemConstants#FILE_COMMUNICATION}.
	 * 
	 * @param handlerType
	 * 
	 * @return String containing the program and arguments to be called
	 */
	String getExternalHandlerProgram(HandlerProgramType handlerType);
	
	/**
	 * The "long" ID of the receiver which will be embedded into the GDT
	 * Satznachricht at {@link GDTConstants#FELDKENNUNG_GDT_ID_EMPFAENGER} when
	 * an outgoing message is created.
	 * 
	 * @return String
	 */
	String getIDReceiver();

	/**
	 * The "short" ID used to create outgoing filename sender, receiver
	 * combinations.
	 * 
	 * This is only applicable for communication of type
	 * {@link SystemConstants#FILE_COMMUNICATION}.
	 * 
	 * @return String with max 4 characters
	 */
	String getShortIDReceiver();

	/**
	 * Method called before actual output is performed, allowing {@link GDTSatzNachricht}
	 * modification. If values are added {@link GDTSatzNachricht#setAddAllNotAddedIfSet(boolean)}
	 * should be called to include additional fields in the message.
	 * 
	 * @param gdtSatzNachricht
	 */
	default void handleOutput(GDTSatzNachricht gdtSatzNachricht){}
	
}
