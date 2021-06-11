package at.medevit.elexis.outbox.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.outbox.model.IOutboxElement;
import at.medevit.elexis.outbox.model.IOutboxElementService.State;
import at.medevit.elexis.outbox.model.OutboxElementType;
import at.medevit.elexis.outbox.ui.filter.NotSentOutboxFilter;
import at.medevit.elexis.outbox.ui.part.provider.IOutboxElementUiProvider;
import ch.elexis.core.mail.MailMessage;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.tasks.model.ITask;
import ch.elexis.core.tasks.model.ITaskDescriptor;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.CoreUiUtil;

public class CoreOutboxElementLabelProvider implements IOutboxElementUiProvider {
	
	private CoreLabelProvider labelProvider;
	
	private CoreColorProvider colorProvider;
	
	private Map<String, IIdentifiedRunnable> identifiedRunnablesMap;
	
	public CoreOutboxElementLabelProvider(){
		labelProvider = new CoreLabelProvider();
		colorProvider = new CoreColorProvider();
		
		identifiedRunnablesMap = buildIdentifiedRunnablesMap();
	}
	
	private Map<String, IIdentifiedRunnable> buildIdentifiedRunnablesMap(){
		List<IIdentifiedRunnable> available = TaskServiceComponent.get().getIdentifiedRunnables();
		if (available != null && !available.isEmpty()) {
			Map<String, IIdentifiedRunnable> ret = new HashMap<>();
			available.stream().forEach(ir -> ret.put(ir.getId(), ir));
			return ret;
		}
		return Collections.emptyMap();
	}
	
	@Override
	public ImageDescriptor getFilterImage(){
		return Images.IMG_MAIL_SEND.getImageDescriptor();
	}
	
	@Override
	public ViewerFilter getFilter(){
		return new NotSentOutboxFilter();
	}
	
	@Override
	public LabelProvider getLabelProvider(){
		return labelProvider;
	}
	
	@Override
	public IColorProvider getColorProvider(){
		return colorProvider;
	}
	
	@Override
	public boolean isProviderFor(IOutboxElement element){
		OutboxElementType elementType = OutboxElementType.parseType(element.getUri());
		if (OutboxElementType.DOC.equals(elementType)) {
			return true;
		} else if (OutboxElementType.DB.equals(elementType)) {
			return element.getObject() instanceof ITaskDescriptor;
		}
		return false;
	}
	
	@Override
	public void doubleClicked(IOutboxElement element){
		OutboxElementType elementType =
			OutboxElementType.parseType(((IOutboxElement) element).getUri());
		if (OutboxElementType.DB.equals(elementType)) {
			if (((IOutboxElement) element).getObject() instanceof ITaskDescriptor) {
				ITaskDescriptor taskDescriptor =
					(ITaskDescriptor) ((IOutboxElement) element).getObject();
				if ("sendMailFromContext".equals(taskDescriptor.getIdentifiedRunnableId())) {
					// now try to call the send mail task command
					try {
						ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
							.getService(ICommandService.class);
						Command sendMailTaskCommand =
							commandService.getCommand("ch.elexis.core.mail.ui.sendMailTask");
						
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("ch.elexis.core.mail.ui.sendMailTaskDescriptorId",
							taskDescriptor.getId());
						ParameterizedCommand parametrizedCommmand =
							ParameterizedCommand.generateCommand(sendMailTaskCommand, params);
						Boolean success =
							(Boolean) PlatformUI.getWorkbench().getService(IHandlerService.class)
								.executeCommand(parametrizedCommmand, null);
						if (success) {
							OutboxServiceComponent.get().changeOutboxElementState(element,
								State.SENT);
						}
					} catch (Exception ex) {
						LoggerFactory.getLogger(getClass())
							.warn("Send mail Task command not available", ex);
					}
				}
			}
		} else if (OutboxElementType.DOC.equals(elementType)) {
			IDocument document = (IDocument) element.getObject();
			if (document != null) {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().showView("ch.elexis.core.ui.documents.views.DocumentsView");
				} catch (PartInitException e) {
					LoggerFactory.getLogger(getClass()).error("Could not open documents view", e);
				}
			}
		}
	}
	
	class CoreColorProvider implements IColorProvider {
		
		@Override
		public Color getForeground(Object element){
			return null;
		}
		
		@Override
		public Color getBackground(Object element){
			if (((IOutboxElement) element).getState() == State.SENT) {
				return CoreUiUtil.getColorForString("d3d3d3");
			}
			return null;
		}
		
	}
	
	class CoreLabelProvider extends LabelProvider {
		private Image taskImage;
		
		@Override
		public String getText(Object element){
			OutboxElementType elementType =
				OutboxElementType.parseType(((IOutboxElement) element).getUri());
			if (OutboxElementType.DB.equals(elementType)) {
				if (((IOutboxElement) element).getObject() instanceof ITaskDescriptor) {
					return getTaskDescriptorText(
						(ITaskDescriptor) ((IOutboxElement) element).getObject());
				}
			}
			return ((IOutboxElement) element).getLabel();
		}
		
		private String getTaskDescriptorText(ITaskDescriptor taskDescriptor){
			StringBuilder sb = new StringBuilder();
			IIdentifiedRunnable ir =
				identifiedRunnablesMap.get(taskDescriptor.getIdentifiedRunnableId());
			Optional<ITask> execution =
				TaskServiceComponent.get().findLatestExecution(taskDescriptor);
			if (execution.isPresent()) {
				sb.append("versendet am " + execution.get().getFinishedAt()
					.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
			} else {
				sb.append(ir.getLocalizedDescription());
			}
			if ("sendMailFromContext".equals(taskDescriptor.getIdentifiedRunnableId())) {
				MailMessage msg =
					MailMessage.fromJson(taskDescriptor.getRunContext().get("message"));
				if (msg != null) {
					sb.append(" an ").append(msg.getTo());
					if (StringUtils.isNotBlank(msg.getCc())) {
						sb.append(", cc ").append(msg.getCc());
					}
					return sb.toString();
				}
			}
			
			return sb.toString();
		}
		
		@Override
		public Image getImage(Object element){
			OutboxElementType elementType =
				OutboxElementType.parseType(((IOutboxElement) element).getUri());
			if (OutboxElementType.DOC.equals(elementType)) {
				return Images.IMG_DOCUMENT_TEXT.getImage();
			} else if (OutboxElementType.DB.equals(elementType)) {
				if (((IOutboxElement) element).getObject() instanceof ITaskDescriptor) {
					return getTaskImage();
				}
			}
			return null;
		}
		
		private Image getTaskImage(){
			if (taskImage == null) {
				try {
					taskImage = ImageDescriptor
						.createFromURL(new URL(
							"platform:/plugin/ch.elexis.core.ui.tasks/rsc/icons/screwdriver.png"))
						.createImage();
				} catch (MalformedURLException e) {
					LoggerFactory.getLogger(getClass()).warn("Error loading task image ", e);
				}
			}
			return taskImage;
		}
	}
	
	@Override
	public void delete(IOutboxElement element){
		OutboxElementType elementType =
			OutboxElementType.parseType(((IOutboxElement) element).getUri());
		if (OutboxElementType.DB.equals(elementType)) {
			if (((IOutboxElement) element).getObject() instanceof ITaskDescriptor) {
				ITaskDescriptor taskDescriptor =
					(ITaskDescriptor) ((IOutboxElement) element).getObject();
				try {
					TaskServiceComponent.get().removeTaskDescriptor(taskDescriptor);
				} catch (TaskException e) {
					LoggerFactory.getLogger(getClass()).warn("Error removing mail Task", e);
				}
			}
		}
	}
}
