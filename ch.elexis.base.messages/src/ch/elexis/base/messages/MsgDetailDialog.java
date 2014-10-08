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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Anwender;
import ch.elexis.data.Reminder;
import ch.elexis.messages.Message;
import ch.rgw.tools.TimeTool;

public class MsgDetailDialog extends Dialog {
	
	Label lbFrom;
	Combo cbTo;
	Text text;
	Message msg;
	Anwender[] users;
	Button bOK, bRecall, bAsReminder, bAnswer, bCancel;
	ClickListener clickListener = new ClickListener();
	
	MsgDetailDialog(final Shell shell, final Message msg){
		super(shell);
		this.msg = msg;
	}
	
	@Override
	protected Control createDialogArea(final Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout(4, false));
		Label l1 = new Label(ret, SWT.NONE);
		String l =
			(msg == null) ? new TimeTool().toString(TimeTool.FULL_GER) : new TimeTool(
				msg.get("time")).toString(TimeTool.FULL_GER); //$NON-NLS-1$
		
		l1.setText(Messages.MsgDetailDialog_messageDated + l);
		
		l1.setLayoutData(SWTHelper.getFillGridData(4, true, 1, false));
		
		new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_from);
		lbFrom = new Label(ret, SWT.NONE);
		
		new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_to);
		cbTo = new Combo(ret, SWT.SINGLE | SWT.READ_ONLY);
		new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(SWTHelper.getFillGridData(4,
			true, 1, false));
		new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_message);
		text = SWTHelper.createText(ret, 1, SWT.BORDER);
		text.setLayoutData(SWTHelper.getFillGridData(3, true, 1, true));
		users = Hub.getUserList().toArray(new Anwender[0]);
		for (Anwender a : users) {
			cbTo.add(a.getLabel());
		}
		if (msg == null) {
			lbFrom.setText(CoreHub.actUser.getLabel());
		} else {
			lbFrom.setText(msg.getSender().getLabel());
			cbTo.setText(msg.getDest().getLabel());
			cbTo.setEnabled(false);
			text.setText(msg.get("Text")); //$NON-NLS-1$
		}
		
		return ret;
	}
	
	@Override
	public void create(){
		super.create();
		if (msg == null) {
			getShell().setText(Messages.MsgDetailDialog_createMessage);
		} else {
			getShell().setText(Messages.MsgDetailDialog_readMessage);
		}
	}
	
	@Override
	protected void createButtonsForButtonBar(final Composite parent){
		String sOK;
		if (msg == null) {
			sOK = Messages.MsgDetailDialog_send;
		} else {
			sOK = Messages.MsgDetailDialog_delete;
		}
		bOK = createButton(parent, IDialogConstants.OK_ID, sOK, false);
		parent.getShell().setDefaultButton(bOK);
		bAnswer =
			createButton(parent, IDialogConstants.CLIENT_ID + 1, Messages.MsgDetailDialog_reply,
				false);
		bAnswer.addSelectionListener(clickListener);
		bAsReminder =
			createButton(parent, IDialogConstants.CLIENT_ID + 2,
				Messages.MsgDetailDialog_asReminder, false);
		bAsReminder.addSelectionListener(clickListener);
		bCancel =
			createButton(parent, IDialogConstants.CANCEL_ID, Messages.MsgDetailDialog_cancel, false);
		if (msg == null) {
			bAnswer.setEnabled(false);
		}
	}
	
	@Override
	protected void okPressed(){
		if (msg == null) {
			int idx = cbTo.getSelectionIndex();
			if (idx != -1) {
				msg = new Message(users[idx], text.getText());
			}
		} else {
			msg.delete();
		}
		super.okPressed();
	}
	
	class ClickListener extends SelectionAdapter {
		
		@Override
		public void widgetSelected(final SelectionEvent e){
			Button bOrigin = ((Button) e.getSource());
			if (bOrigin != null) {
				if (bOrigin.equals(bAnswer)) {
					lbFrom.setText(CoreHub.actUser.getLabel());
					if (msg != null) {
						Anwender an = msg.getSender();
						if (an != null) {
							cbTo.setText(an.getLabel());
						}
						msg.delete();
						msg = null;
					}
					// check if text should be cleared for answer
					if (CoreHub.userCfg.get(Preferences.USR_MESSAGES_ANSWER_AUTOCLEAR, false)) {
						text.setText("");
					}
					
					bOK.setText(Messages.MsgDetailDialog_send);
					// *** make sure we can see <from> when name is longer than before
					lbFrom.getParent().layout();
				} else {
					int idx = cbTo.getSelectionIndex();
					if (idx != -1) {
						Reminder rem =
							new Reminder(users[idx], new TimeTool().toString(TimeTool.DATE_GER),
								Reminder.Typ.anzeigeTodoAll, "", text.getText()); //$NON-NLS-1$
						rem.addResponsible(users[idx]);
					}
					okPressed();
				}
			}
		}
	}
}
