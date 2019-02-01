//package at.medevit.ch.artikelstamm.elexis.common.ui.dbcheck;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IConfigurationElement;
//import org.eclipse.core.runtime.IProgressMonitor;
//
//import ch.artikelstamm.elexis.common.ArtikelstammItem;
//import ch.elexis.core.data.constants.ExtensionPointConstantsData;
//import ch.elexis.core.data.interfaces.IVerrechenbar;
//import ch.elexis.core.data.interfaces.IVerrechnetAdjuster;
//import ch.elexis.core.data.util.Extensions;
//import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
//import ch.elexis.data.Query;
//import ch.elexis.data.Verrechnet;
//import ch.rgw.tools.ExHandler;
//
//public class AddMissingVATToVerrechenbarArticles extends ExternalMaintenance {
//	
//	private static ArrayList<IVerrechnetAdjuster> adjusters = new ArrayList<IVerrechnetAdjuster>();
//	
//	static {
//		List<IConfigurationElement> adjustersConfigurations =
//			Extensions.getExtensions(ExtensionPointConstantsData.VERRECHNUNGSCODE_ADJUSTER);
//		for (IConfigurationElement elem : adjustersConfigurations) {
//			Object o;
//			try {
//				o = elem.createExecutableExtension("class");
//				if (o instanceof IVerrechnetAdjuster) {
//					adjusters.add((IVerrechnetAdjuster) o);
//				}
//			} catch (CoreException e) {
//				// just log the failed instantiation
//				ExHandler.handle(e);
//			}
//		}
//	}
//	
//	@Override
//	public String executeMaintenance(IProgressMonitor pm, String DBVersion){
//		StringBuilder output = new StringBuilder();
//		
//		Query<Verrechnet> qbe = new Query<Verrechnet>(Verrechnet.class);
//		qbe.add(Verrechnet.CLASS, Query.LIKE, "ch.artikelstamm.elexis.common.ArtikelstammItem");
//		List<Verrechnet> qre = qbe.execute();
//		
//		output.append("Folgende Adjuster werden angewendet:\n");
//		for (IVerrechnetAdjuster adjuster : adjusters) {
//			output.append("\t"+adjuster.getClass().getName() + "\n");
//		}
//		
//		output.append("--------------------------------\n");
//		
//		pm.beginTask("Verifiziere Artikelstamm-Leistungen ....", qre.size());
//		
//		for (Verrechnet vr : qre) {
//			String verrechnet_vatscale = vr.getDetail(Verrechnet.VATSCALE);
//			if (verrechnet_vatscale == null) {
//				IVerrechenbar verrechenbar = vr.getVerrechenbar();
//				if (verrechenbar instanceof ArtikelstammItem) {
//					for (IVerrechnetAdjuster adjuster : adjusters) {
//						output.append("Ergänze fehlende Info bei Verrechnet " + vr.getId()
//							+ " (Kons " + vr.getKons().getLabel() + ")\n");
//						adjuster.adjust(vr);
//					}
//				}
//			}
//			pm.worked(1);
//		}
//		
//		output.append("DONE\n");
//		pm.done();
//		return output.toString();
//	}
//	
//	@Override
//	public String getMaintenanceDescription(){
//		return "[4947] Fehlende MWSt Information für RH eingende Artikel korrigieren";
//	}
//	
//}
