/*******************************************************************************
 * Copyright (c) 2014, Pharmed Solutions GmbH
 * All rights reserved.
 *******************************************************************************/

package ch.pharmed.phmprescriber;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;

import ch.elexis.core.data.interfaces.IOutputter;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IRecipe;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.data.Rezept;

public class PhMRpActions implements IAction, IOutputter {

	private static final String opID = "ch.pharmed.phmprescriber"; //$NON-NLS-1$
	private String opDescription;
	private String optooltiptext;;
	private String opText;
	public static final String PLUGIN_ID = "ch.pharmed.phmprescriber"; //$NON-NLS-1$

	private ImageDescriptor imgDescr;
	private String toolTipText;
	private int style;
	private String text;
	private int keycode;
	private ResourceBundle messages;

	// Constructor
	public PhMRpActions() {

		this.messages = ResourceBundle.getBundle("ch.pharmed.phmprescriber.MessagesBundle", new Locale("de", "CH"));

		this.style = AS_PUSH_BUTTON;
		this.setImageDescriptor(createImageDescriptor());
		this.setToolTipText(messages.getString("PhMRpActions_2"));
		this.setText(messages.getString("PhMRpActions_3"));
		this.setAccelerator(SWT.CTRL | 'N');

	}

	private ImageDescriptor createImageDescriptor() {

		Bundle bundle = Platform.getBundle("ch.pharmed.phmprescriber"); //$NON-NLS-1$
		URL fullPathString = BundleUtility.find(bundle, "icons/logo_elexis.png"); //$NON-NLS-1$

		return ImageDescriptor.createFromURL(fullPathString);

	}

	@Override
	public String getOutputterID() {
		// TODO Auto-generated method stub
		return opID;
	}

	@Override
	public String getOutputterDescription() {
		// TODO Auto-generated method stub
		return messages.getString("PhMRpActions_1");
	}

	@Override
	public Object getSymbol() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getAccelerator() {
		// TODO Auto-generated method stub
		return this.keycode;
	}

	@Override
	public String getActionDefinitionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageDescriptor getDisabledImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HelpListener getHelpListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageDescriptor getHoverImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {

		return this.imgDescr;

	}

	@Override
	public IMenuCreator getMenuCreator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStyle() {

		// TODO Auto-generated method stub
		return this.style;
	}

	@Override
	public String getText() {

		// TODO Auto-generated method stub
		return this.text;
	}

	@Override
	public String getToolTipText() {
		// TODO Auto-generated method stub
		return this.toolTipText;
	}

	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		System.out.println("Test"); //$NON-NLS-1$

	}

	@Override
	public void runWithEvent(Event event) {
		Rezept rp = getSelectedRezept();
		Physician ph = new Physician();
		Sender sender = new Sender(rp, ph);
		sender.sendnprint();

	}

	private Rezept getSelectedRezept() {
		return ContextServiceHolder.get().getTyped(IRecipe.class)
				.map(ir -> ((Rezept) NoPoUtil.loadAsPersistentObject(ir))).orElse(null);
	}

	@Override
	public void setActionDefinitionId(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setChecked(boolean checked) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDescription(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDisabledImageDescriptor(ImageDescriptor newImage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHelpListener(HelpListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHoverImageDescriptor(ImageDescriptor newImage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setId(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setImageDescriptor(ImageDescriptor newImage) {

		this.imgDescr = newImage;

	}

	@Override
	public void setMenuCreator(IMenuCreator creator) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub
		this.text = text;

	}

	@Override
	public void setToolTipText(String text) {

		this.toolTipText = text;

	}

	@Override
	public void setAccelerator(int keycode) {
		// TODO Auto-generated method stub
		this.keycode = keycode;

	}

}
