package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;
import java.util.Optional;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import com.equo.chromium.swt.Browser;

import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import at.medevit.elexis.agenda.ui.rcprap.SingleSourceUtil;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.utils.CoreUtil;

public class ContextMenuFunction extends AbstractBrowserFunction {

	private ISelectionProvider selectionProvider;
	private MPart part;

	/*
	 * on right click from javascript if an event is hit: -
	 * contextMenuFunction(event.id) -> event selection -
	 * contextMenuFunction(jsEvent.pageX, jsEvent.pageY, date.format(),
	 * jsEvent.resource) -> resource selection if no event is hit -
	 * contextMenuFunction(jsEvent.pageX, jsEvent.pageY, date.format(),
	 * jsEvent.resource) -> resource selection as there is no other way to deselect
	 * if no event is hit on rightclick, remember timestamp of selection and
	 * deselect if older than 1 sec.
	 */
	private long selectionTimestamp;

	public ContextMenuFunction(MPart part, Browser browser, String name) {
		super(browser, name);
		this.part = part;
	}

	@Override
	public Object function(Object[] arguments) {
		if (arguments.length == 4) {
			if (selectionTimestamp > 0 && (System.currentTimeMillis() - selectionTimestamp > 1500)) {
				if (selectionProvider != null) {
					selectionProvider.setSelection(StructuredSelection.EMPTY);
					LoggerFactory.getLogger(getClass()).info("Clear selection resource click");
				}
			}

			LocalDateTime date = getDateTimeArg(arguments[2]);
			String resource = (String) arguments[3];

			Optional<SideBarComposite> activeSideBar = getActiveSideBar(part);
			activeSideBar.ifPresent(sideBar -> {
				sideBar.setMoveInformation(date, resource);

				if (SingleSourceUtil.isRap()) {
					// in Rap Browser Mouse Location is not connected to SWT
					// hence the menu does not open at the correct location (but would on the last
					// entry
					// point to the SWT browser widget)
					// see
					// https://www.eclipse.org/forums/index.php?t=msg&th=1100113&goto=1810544&#msg_1810544
					// relative if sidebar is opened
					Double argX = (Double) arguments[0];
					Double argY = (Double) arguments[1];
					getBrowser().getMenu().setLocation(argX.intValue() + 100, argY.intValue());
				}

				Display.getDefault().timerExec(100, () -> {
					if (!getBrowser().getMenu().isVisible()) {
						if (CoreUtil.isMac()) {
							getBrowser().setFocus();
						}
						getBrowser().getMenu().setVisible(true);
					}
				});
			});
		} else if (arguments.length == 1) {
			IAppointment termin = CoreModelServiceHolder.get().load((String) arguments[0], IAppointment.class)
					.orElse(null);
			if (selectionProvider != null) {
				IStructuredSelection selection = termin != null ? new StructuredSelection(termin) : null;
				selectionProvider.setSelection(selection);
				// set selection of right click
				selectionTimestamp = System.currentTimeMillis();
				LoggerFactory.getLogger(getClass()).info("Set selection [" + selection + "]");
			}

			Display.getDefault().timerExec(100, () -> {
				if (!getBrowser().getMenu().isVisible()) {
					if (CoreUtil.isMac()) {
						getBrowser().setFocus();
					}
					getBrowser().getMenu().setVisible(true);
				}
			});
		} else if (arguments.length == 0) {
			if (selectionProvider != null) {
				selectionProvider.setSelection(StructuredSelection.EMPTY);
				LoggerFactory.getLogger(getClass()).info("Clear selection no arguments");
			}
		}
		return null;
	}

	public void setSelectionProvider(ISelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;
	}
}
