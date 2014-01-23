package at.medevit.ch.artikelstamm.ui.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import at.medevit.atc_codes.ATCCodeService;

public class ATCCodeServiceTracker implements ServiceTrackerCustomizer {
	
	private final BundleContext ctx;
	
	public ATCCodeServiceTracker(BundleContext ctx){
		this.ctx = ctx;
	}
	
	@Override
	public Object addingService(ServiceReference reference){
		ATCCodeService service = (ATCCodeService) ctx.getService(reference);
		new ATCCodeServiceConsumer().bind(service);
		return service;
	}
	
	@Override
	public void modifiedService(ServiceReference reference, Object service){}
	
	@Override
	public void removedService(ServiceReference reference, Object service){
		new ATCCodeServiceConsumer().unbind((ATCCodeService) service);
		ctx.ungetService(reference);
	}
	
}
