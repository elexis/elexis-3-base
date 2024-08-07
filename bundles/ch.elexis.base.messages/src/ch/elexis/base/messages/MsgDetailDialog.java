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
import java.util.Optional;
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
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IMessage;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.IUserGroup;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.builder.IMessageBuilder;
import ch.elexis.core.model.builder.IReminderBuilder;
import ch.elexis.core.model.format.UserFormatUtil;
import ch.elexis.core.model.issue.ProcessStatus;
import ch.elexis.core.model.issue.Visibility;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.TimeTool;

public class MsgDetailDialog extends Dialog {

	private Label lblFrom;
	private ComboViewer cbTo;
	private Button groupBtn;
	private Text txtMessage;
	private IMessage incomingMsg;
	private Button bOK;
	private Button bAnswer;

	MsgDetailDialog(final Shell shell, final IMessage msg) {
		super(shell);
		setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
		this.incomingMsg = msg;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout(5, false));

		Label lblMessageInfo = new Label(ret, SWT.NONE);
		lblMessageInfo.setLayoutData(SWTHelper.getFillGridData(5, true, 1, false));
		String msgLabel = (incomingMsg == null) ? new TimeTool().toString(TimeTool.FULL_GER)
				: new TimeTool(incomingMsg.getCreateDateTime()).toString(TimeTool.FULL_GER);
		lblMessageInfo.setText(Messages.MsgDetailDialog_messageDated + msgLabel);

		new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_from);
		lblFrom = new Label(ret, SWT.NONE);

		new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_to);
		cbTo = new ComboViewer(ret, SWT.SINGLE | SWT.READ_ONLY);
		cbTo.setContentProvider(ArrayContentProvider.getInstance());
		cbTo.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof IContact && e2 instanceof IContact) {
					IContact anw1 = (IContact) e1;
					IContact anw2 = (IContact) e2;
					return String.CASE_INSENSITIVE_ORDER.compare(UserFormatUtil.getUserLabel(anw1),
							UserFormatUtil.getUserLabel(anw2));
				} else if (e1 instanceof IUserGroup && e2 instanceof IUserGroup) {
					IUserGroup ug1 = (IUserGroup) e1;
					IUserGroup ug2 = (IUserGroup) e2;
					return String.CASE_INSENSITIVE_ORDER.compare(ug1.getGroupname(), ug2.getGroupname());
				}
				return 0;
			}
		});
		cbTo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IContact) {
					IContact anw = (IContact) element;
					return UserFormatUtil.getUserLabel(anw);
				} else if (element instanceof IUserGroup) {
					return ((IUserGroup) element).getGroupname();
				}
				return super.getText(element);
			}
		});
		List<IContact> users = setComboUserInput();

		groupBtn = new Button(ret, SWT.CHECK);
		groupBtn.setText(" Gruppe");
		groupBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (groupBtn.getSelection()) {
					setComboGroupInput();
				} else {
					setComboUserInput();
				}
			}
		});

		new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(SWTHelper.getFillGridData(5, true, 1, false));

		if (incomingMsg != null) {
			String senderString = incomingMsg.getSender();
			Optional<IContact> sender = getSender(incomingMsg);
			if (sender.isPresent()) {
				senderString = UserFormatUtil.getUserLabel(sender.get());
			}
			lblFrom.setText(senderString);
			Optional<IContact> destination = getDestination(incomingMsg);
			if (destination.isPresent() && sender.isPresent()) {
				for (IContact anwender : users) {
					if (destination.get().getId().equals(anwender.getId())) {
						sender = Optional.of(anwender);
						break;
					}
				}
			}
			if (sender.isPresent()) {
				cbTo.setSelection(new StructuredSelection(sender.get()));
			}
			cbTo.getCombo().setEnabled(false);
			groupBtn.setEnabled(false);
			new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_message);
			Text txtIncomingMsg = new Text(ret, SWT.READ_ONLY | SWT.BORDER);
			GridData gd_txtIncomingMsg = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
			txtIncomingMsg.setLayoutData(gd_txtIncomingMsg);
			txtIncomingMsg.setText(incomingMsg.getMessageText());
			Button copyButton = new Button(ret, SWT.PUSH);
			copyButton.setText(Messages.MsgDetailDialog_Copy);
			GridData gd_copyButton = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
			copyButton.setLayoutData(gd_copyButton);
			copyButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Clipboard clipboard = new Clipboard(Display.getCurrent());
					String textToCopy = txtIncomingMsg.getText();
					clipboard.setContents(new Object[] { textToCopy },
							new org.eclipse.swt.dnd.Transfer[] { org.eclipse.swt.dnd.TextTransfer.getInstance() });
					clipboard.dispose();
				}
			});
			new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_answer);
		} else {
			lblFrom.setText(UserFormatUtil.getUserLabel(ContextServiceHolder.get().getActiveUserContact().get()));
			new Label(ret, SWT.NONE).setText(Messages.MsgDetailDialog_message);
		}
		txtMessage = SWTHelper.createText(ret, 1, SWT.BORDER);
		txtMessage.setLayoutData(SWTHelper.getFillGridData(4, true, 1, true));
		txtMessage.addModifyListener(e -> {
			if (txtMessage.getText() != null && txtMessage.getText().length() > 0) {
				getShell().setDefaultButton(bAnswer);
			} else {
				getShell().setDefaultButton(bOK);
			}
		});
		return ret;
	}

	private void setComboGroupInput() {
		List<IUserGroup> groups = getUserGroups();
		cbTo.setInput(groups);
		cbTo.refresh();
	}

	private List<IUserGroup> getUserGroups() {
		List<IUserGroup> userGroups = CoreModelServiceHolder.get().getQuery(IUserGroup.class).execute();
		userGroups.sort((u1, u2) -> u1.getLabel().compareTo(u2.getLabel()));
		return userGroups;
	}

	private List<IContact> setComboUserInput() {
		List<IContact> users = getUsers();
		cbTo.setInput(users);

		String preferenceKey = getPreferenceKeyForUser();
		String savedRecipientId = ConfigServiceHolder.getUser(preferenceKey, null);
		if (savedRecipientId != null && !savedRecipientId.isEmpty()) {
			for (IContact user : users) {
				if (user.getId().equals(savedRecipientId)) {
					cbTo.setSelection(new StructuredSelection(user));
					break;
				}
			}
		} else {
			cbTo.setSelection(new StructuredSelection(users.get(0)));
		}
		return users;
	}

	private Optional<IContact> getSender(IMessage message) {
		if (StringUtils.isNotBlank(message.getSender())) {
			return CoreModelServiceHolder.get().load(message.getSender(), IContact.class, true);
		}
		return Optional.empty();
	}

	private Optional<IContact> getDestination(IMessage message) {
		if (message.getReceiver() != null && !message.getReceiver().isEmpty()) {
			return CoreModelServiceHolder.get().load(message.getReceiver().get(0), IContact.class, true);
		}
		return Optional.empty();
	}

	private List<IContact> getUsers() {
		IQuery<IUser> userQuery = CoreModelServiceHolder.get().getQuery(IUser.class);
		userQuery.and(ModelPackage.Literals.IUSER__ASSIGNED_CONTACT, COMPARATOR.NOT_EQUALS, null);
		List<IUser> users = userQuery.execute();
		return users.stream().filter(u -> isActive(u)).map(u -> u.getAssignedContact())
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
		Button bReminder = createButton(parent, IDialogConstants.CLIENT_ID + 2, Messages.MsgDetailDialog_asReminder,
				false);
		if (incomingMsg == null) {
			bReminder.setEnabled(false);
		}
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
				ContextServiceHolder.get().getActiveUserContact().ifPresent(uc -> {
					getSender(incomingMsg).ifPresent(sc -> {
						IMessage message = new IMessageBuilder(CoreModelServiceHolder.get(), uc, sc).build();
						message.setMessageText(txtMessage.getText());
						CoreModelServiceHolder.get().save(message);
					});
				});
			}
			okPressed();
		case IDialogConstants.CLIENT_ID + 2:
			StructuredSelection ss = ((StructuredSelection) cbTo.getSelection());
			if (!ss.isEmpty()) {
				IContact destination = (IContact) ss.getFirstElement();
				new IReminderBuilder(CoreModelServiceHolder.get(), ContextServiceHolder.get(), Visibility.ALWAYS,
						ProcessStatus.OPEN, incomingMsg.getMessageText()).addResponsible(destination).buildAndSave();
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
				ContextServiceHolder.get().getActiveUserContact().ifPresent(uc -> {
					if (ss.getFirstElement() instanceof IContact) {
						createMessage(uc, (IContact) ss.getFirstElement());
					} else if (ss.getFirstElement() instanceof IUserGroup) {
						IUserGroup userGoup = (IUserGroup) ss.getFirstElement();
						for (IUser user : userGoup.getUsers()) {
							if (user.getAssignedContact() != null) {
								createMessage(uc, user.getAssignedContact());
							}
						}
					}
				});
			}
		} else {
			CoreModelServiceHolder.get().delete(incomingMsg);
		}
		super.okPressed();
	}

	private void createMessage(IContact sender, IContact receiver) {
		IMessage message = new IMessageBuilder(CoreModelServiceHolder.get(), sender, receiver).build();
		message.setMessageText(txtMessage.getText());
		CoreModelServiceHolder.get().save(message);
	}

	private String getPreferenceKeyForUser() {
		String userId = ContextServiceHolder.get().getActiveUserContact().get().getId();
		return Preferences.USR_DEFAULT_MESSAGE_RECIPIENT + "_" + userId;
	}
}
