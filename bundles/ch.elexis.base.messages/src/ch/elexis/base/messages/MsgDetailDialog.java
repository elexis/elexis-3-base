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
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.issue.Visibility;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
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
	private Button bOK;
	private Button bAnswer;

	MsgDetailDialog(final Shell shell, final Message msg) {
		super(shell);
		setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
		this.incomingMsg = msg;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout(4, false));

		Label lblMessageInfo = new Label(ret, SWT.NONE);
		lblMessageInfo.setLayoutData(SWTHelper.getFillGridData(4, true, 1, false));
		String msgLabel = (incomingMsg == null) ? new TimeTool().toString(TimeTool.FULL_GER)
				: new TimeTool(incomingMsg.get(Message.FLD_TIME)).toString(TimeTool.FULL_GER);
		lblMessageInfo.setText(Messages.MsgDetailDialog_messageDated + msgLabel);

		new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_from);
		lblFrom = new Label(ret, SWT.NONE);

		new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_to);
		cbTo = new ComboViewer(ret, SWT.SINGLE | SWT.READ_ONLY);
		cbTo.setContentProvider(ArrayContentProvider.getInstance());
		cbTo.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				Anwender anw1 = (Anwender) e1;
				Anwender anw2 = (Anwender) e2;
				return String.CASE_INSENSITIVE_ORDER.compare(anw1.getLabel(), anw2.getLabel());
			}
		});
		cbTo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Anwender anw = (Anwender) element;
				return anw.getLabel();
			}
		});
	    List<Anwender> users = getUsers();
	    cbTo.setInput(users);
		String preferenceKey = getPreferenceKeyForUser();
	    String savedRecipientId = ConfigServiceHolder.getUser(preferenceKey, null);
	    if (savedRecipientId != null && !savedRecipientId.isEmpty()) {
	        for (Anwender user : users) {
	            if (user.getId().equals(savedRecipientId)) {
	                cbTo.setSelection(new StructuredSelection(user));
	                break;
	            }
	        }
	    } else {
			cbTo.setSelection(new StructuredSelection(users.get(0)));
	    }

		new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(SWTHelper.getFillGridData(4, true, 1, false));

		if (incomingMsg != null) {
			String senderString = (incomingMsg.getSender() != null) ? incomingMsg.getSender().getLabel()
					: incomingMsg.getSenderString();
			lblFrom.setText(senderString);
			Anwender sender = null;
			for (Anwender anwender : users) {
				if (incomingMsg.getDest().getId().equals(anwender.getId())) {
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
			lblIncomingMsg.setText(incomingMsg.get(Message.FLD_TEXT));

			new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_answer);
		} else {
			lblFrom.setText(CoreHub.getLoggedInContact().getLabel());
			new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_message);
		}

		txtMessage = SWTHelper.createText(ret, 1, SWT.BORDER);
		txtMessage.setLayoutData(SWTHelper.getFillGridData(3, true, 1, true));
		txtMessage.addModifyListener(e -> {
			if (txtMessage.getText() != null && txtMessage.getText().length() > 0) {
				getShell().setDefaultButton(bAnswer);
			} else {
				getShell().setDefaultButton(bOK);
			}
		});

		return ret;
	}

	private List<Anwender> getUsers() {
		IQuery<IUser> userQuery = CoreModelServiceHolder.get().getQuery(IUser.class);
		userQuery.and(ModelPackage.Literals.IUSER__ASSIGNED_CONTACT, COMPARATOR.NOT_EQUALS, null);
		List<IUser> users = userQuery.execute();
		return users.stream().filter(u -> isActive(u)).map(u -> Anwender.load(u.getAssignedContact().getId()))
				.collect(Collectors.toList());
	}

	private boolean isActive(IUser user) {
		if (user == null || user.getAssignedContact() == null) {
			return false;
		}
		if (!user.isActive()) {
			return false;
		}
		if (user.getAssignedContact() != null && user.getAssignedContact().isMandator()) {
			IMandator mandator = CoreModelServiceHolder.get().load(user.getAssignedContact().getId(), IMandator.class)
					.orElse(null);
			if (mandator != null && !mandator.isActive()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void create() {
		super.create();
		if (incomingMsg == null) {
			getShell().setText(Messages.MsgDetailDialog_createMessage);
		} else {
			getShell().setText(Messages.MsgDetailDialog_readMessage);
		}
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		String sOK;
		if (incomingMsg == null) {
			sOK = Messages.MsgDetailDialog_send;
		} else {
			sOK = Messages.MsgDetailDialog_delete;
		}
		bOK = createButton(parent, IDialogConstants.OK_ID, sOK, false);
		parent.getShell().setDefaultButton(bOK);
		bAnswer = createButton(parent, IDialogConstants.CLIENT_ID + 1, Messages.MsgDetailDialog_reply, false);
		if (incomingMsg == null) {
			bAnswer.setEnabled(false);
		}
		createButton(parent, IDialogConstants.CLIENT_ID + 2, Messages.MsgDetailDialog_asReminder, false);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.MsgDetailDialog_cancel, false);
	}

	@Override
	protected void buttonPressed(int buttonId) {
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
				Reminder rem = new Reminder(null, new TimeTool().toString(TimeTool.DATE_GER), Visibility.ALWAYS,
						StringUtils.EMPTY, incomingMsg.get(Message.FLD_TEXT));
				ElexisEventDispatcher.getInstance()
						.fire(new ElexisEvent(rem, Reminder.class, ElexisEvent.EVENT_CREATE));
				rem.addResponsible(anw);
			}
			okPressed();
		default:
			break;
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected void okPressed() {
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

	private String getPreferenceKeyForUser() {
		String userId = CoreHub.getLoggedInContact().getId();
		return Preferences.USR_DEFAULT_MESSAGE_RECIPIENT + "_" + userId;
	}
}
