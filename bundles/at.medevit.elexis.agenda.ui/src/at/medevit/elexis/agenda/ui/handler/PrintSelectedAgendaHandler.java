package at.medevit.elexis.agenda.ui.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.function.LoadEventsFunction;
import at.medevit.elexis.agenda.ui.view.AgendaView;
import at.medevit.elexis.agenda.ui.xml.AreaPeriodsLetter;
import ch.elexis.agenda.data.Termin;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.services.IFormattedOutput;
import ch.elexis.core.services.IFormattedOutputFactory;
import ch.elexis.core.services.IFormattedOutputFactory.ObjectType;
import ch.elexis.core.services.IFormattedOutputFactory.OutputType;

public class PrintSelectedAgendaHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof AgendaView) {
			AgendaView agendaView = (AgendaView) activePart;
			LoadEventsFunction loadEventsFunction = agendaView.getLoadEventsFunction();
			
			List<IPeriod> periods = loadEventsFunction.getCurrentPeriods();
			Map<String, List<IPeriod>> areaPeriodMap = getAreaPeriodMap(periods);
			
			for (String area : areaPeriodMap.keySet()) {
				AreaPeriodsLetter letter = AreaPeriodsLetter.of(area, areaPeriodMap.get(area));
				if (letter != null) {
					BundleContext bundleContext =
						FrameworkUtil.getBundle(getClass()).getBundleContext();
					ServiceReference<IFormattedOutputFactory> serviceRef =
						bundleContext.getServiceReference(IFormattedOutputFactory.class);
					if (serviceRef != null) {
						IFormattedOutputFactory service = bundleContext.getService(serviceRef);
						IFormattedOutput outputter = service
							.getFormattedOutputImplementation(ObjectType.JAXB, OutputType.PDF);
						ByteArrayOutputStream pdf = new ByteArrayOutputStream();
						Map<String, String> parameters = new HashMap<>();
						parameters.put("current-date",
							LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
						
						outputter.transform(letter,
							getClass().getResourceAsStream("/rsc/xslt/areaperiods2fo.xslt"), pdf,
							parameters);
						bundleContext.ungetService(serviceRef);
						// save and open the file ...
						File file = null;
						FileOutputStream fout = null;
							try {
							file = File.createTempFile(letter.getArea() + "_", ".pdf");
							fout = new FileOutputStream(file);
							fout.write(pdf.toByteArray());
							} catch (IOException e) {
							Display.getDefault().syncExec(() -> {
								MessageDialog.openError(HandlerUtil.getActiveShell(event), "Fehler",
									"Fehler beim PDF anlegen.\n" + e.getMessage());
							});
							LoggerFactory.getLogger(getClass()).error("Error creating PDF", e);
						} finally {
							if (fout != null) {
								try {
									fout.close();
								} catch (IOException e) {
									// ignore
								}
							}
						}
						if (file != null) {
							Program.launch(file.getAbsolutePath());
							}
					}
				}
			}
		}
		return null;
	}
	
	private Map<String, List<IPeriod>> getAreaPeriodMap(List<IPeriod> periods){
		if (periods != null && !periods.isEmpty()) {
			if (periods.get(0) instanceof Termin) {
				Map<String, List<IPeriod>> ret = new HashMap<>();
				for (IPeriod iPeriod : periods) {
					String area = ((Termin) iPeriod).getBereich();
					List<IPeriod> list = ret.get(area);
					if (list == null) {
						list = new ArrayList<>();
					}
					list.add(iPeriod);
					ret.put(area, list);
				}
				return ret;
			} else {
				throw new IllegalStateException(
					"Can not determine area of period " + periods.get(0));
			}
		}
		return Collections.emptyMap();
	}
}
