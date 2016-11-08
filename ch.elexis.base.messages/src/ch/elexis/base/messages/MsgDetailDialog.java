/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.base.messages;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.issue.Visibility;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Anwender;
import ch.elexis.data.Reminder;
import ch.elexis.messages.Message;
import ch.rgw.tools.TimeTool;

public class MsgDetailDialog extends Dialog {
	
	private Label lblFrom;
	private ComboViewer cbTo;
	private Text txtMessage;
	private Message incomingMsg;
	private List<Anwender> users = CoreHub.getUserList();
	private Button bOK, bAnswer;
	
	MsgDetailDialog(final Shell shell, final Message msg){
		super(shell);
		this.incomingMsg = msg;
	}
	
	@Override
	protected Control createDialogArea(final Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout(4, false));
		
		Label lblMessageInfo = new Label(ret, SWT.NONE);
		lblMessageInfo.setLayoutData(SWTHelper.getFillGridData(4, true, 1, false));
		String msgLabel = (incomingMsg == null) ? new TimeTool().toString(TimeTool.FULL_GER)
				: new TimeTool(incomingMsg.get("time")).toString(TimeTool.FULL_GER); //$NON-NLS-1$
		lblMessageInfo.setText(Messages.MsgDetailDialog_messageDated + msgLabel);
		
		new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_from);
		lblFrom = new Label(ret, SWT.NONE);
		
		new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_to);
		cbTo = new ComboViewer(ret, SWT.SINGLE | SWT.READ_ONLY);
		cbTo.setContentProvider(ArrayContentProvider.getInstance());
		cbTo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element){
				Anwender anw = (Anwender) element;
				return anw.getLabel();
			}
		});
		cbTo.setInput(users);
		
		new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL)
			.setLayoutData(SWTHelper.getFillGridData(4, true, 1, false));
		
		if (incomingMsg != null) {
			lblFrom.setText(incomingMsg.getSender().getLabel());
			Anwender sender = null;
			for (Anwender anwender : users) {
				if (incomingMsg.getSender().getId().equals(anwender.getId())) {
					sender = anwender;
					break;
				}
			}
			if (sender != null) {
				cbTo.setSelection(new StructuredSelection(sender));
			}
			
			cbTo.getCombo().setEnabled(false);
			
			new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_message);
			Label lblIncomingMsg = new Label(ret, SWT.None);
			lblIncomingMsg.setLayoutData(SWTHelper.getFillGridData(3, true, 1, true));
			lblIncomingMsg.setText(incomingMsg.get("Text"));
			
			new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_answer);
		} else {
			lblFrom.setText(CoreHub.actUser.getLabel());
			new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_message);
		}
		
		txtMessage = SWTHelper.createText(ret, 1, SWT.BORDER);
		txtMessage.setLayoutData(SWTHelper.getFillGridData(3, true, 1, true));
		txtMessage.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e){
				if (txtMessage.getText() != null && txtMessage.getText().length() > 0) {
					getShell().setDefaultButton(bAnswer);
				} else {
					getShell().setDefaultButton(bOK);
				}
			}
		});
		
		return ret;
	}
	
	@Override
	public void create(){
		super.create();
		if (incomingMsg == null) {
			getShell().setText(Messages.MsgDetailDialog_createMessage);
		} else {
			getShell().setText(Messages.MsgDetailDialog_readMessage);
		}
	}
	
	@Override
	protected void createButtonsForButtonBar(final Composite parent){
		String sOK;
		if (incomingMsg == null) {
			sOK = Messages.MsgDetailDialog_send;
		} else {
			sOK = Messages.MsgDetailDialog_delete;
		}
		bOK = createButton(parent, IDialogConstants.OK_ID, sOK, false);
		parent.getShell().setDefaultButton(bOK);
		bAnswer = createButton(parent, IDialogConstants.CLIENT_ID + 1,
			Messages.MsgDetailDialog_reply, false);
		if (incomingMsg == null) {
			bAnswer.setEnabled(false);
		}
		createButton(parent, IDialogConstants.CLIENT_ID + 2, Messages.MsgDetailDialog_asReminder,
			false);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.MsgDetailDialog_cancel, false);
	}
	
	@Override
	protected void buttonPressed(int buttonId){
		switch (buttonId) {
		case IDialogConstants.OK_ID:
			okPressed();
			return;
		case IDialogConstants.CLIENT_ID + 1:
			if (incomingMsg != null) {
				Anwender an = incomingMsg.getSender();
				new Message(an, txtMessage.getText());
			}
			okPressed();
		case IDialogConstants.CLIENT_ID + 2:
			StructuredSelection ss = ((StructuredSelection) cbTo.getSelection());
			if (!ss.isEmpty()) {
				Anwender anw = (Anwender) ss.getFirstElement();
				Reminder rem = new Reminder(anw, new TimeTool().toString(TimeTool.DATE_GER),
					Visibility.ALWAYS, "", txtMessage.getText()); //$NON-NLS-1$
				rem.addResponsible(anw);
			}
			okPressed();
		default:
			break;
		}
		super.buttonPressed(buttonId);
	}
	
	@Override
	protected void okPressed(){
		if (incomingMsg == null) {
			StructuredSelection ss = ((StructuredSelection) cbTo.getSelection());
			if (!ss.isEmpty()) {
				incomingMsg = new Message((Anwender) ss.getFirstElement(), txtMessage.getText());
			}
		} else {
			incomingMsg.delete();
		}
		super.okPressed();
	}
}
