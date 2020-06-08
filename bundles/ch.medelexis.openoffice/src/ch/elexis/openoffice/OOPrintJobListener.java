package ch.elexis.openoffice;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.lang.EventObject;
import com.sun.star.view.PrintJobEvent;
import com.sun.star.view.PrintableState;
import com.sun.star.view.XPrintJobListener;

public class OOPrintJobListener implements XPrintJobListener {
	private static Logger log = LoggerFactory.getLogger(OOPrintJobListener.class);
	
	private PrintableState status = null;
	
	public PrintableState getStatus(){
		return status;
	}
	
	public void setStatus(final PrintableState status){
		this.status = status;
	}
	
	/**
	 * The print job event: has to be called when the action is triggered.
	 */
	public void printJobEvent(final PrintJobEvent printJobEvent){
		if (printJobEvent.State == PrintableState.JOB_COMPLETED) {
			log.info("JOB_COMPLETED");
			this.setStatus(PrintableState.JOB_COMPLETED);
		} else if (printJobEvent.State == PrintableState.JOB_ABORTED) {
			log.info("JOB_ABORTED");
			this.setStatus(PrintableState.JOB_ABORTED);
		} else if (printJobEvent.State == PrintableState.JOB_FAILED) {
			log.info("JOB_FAILED");
			this.setStatus(PrintableState.JOB_FAILED);
		} else if (printJobEvent.State == PrintableState.JOB_SPOOLED) {
			log.info("JOB_SPOOLED");
			this.setStatus(PrintableState.JOB_SPOOLED);
		} else if (printJobEvent.State == PrintableState.JOB_SPOOLING_FAILED) {
			log.info("JOB_SPOOLING_FAILED");
			this.setStatus(PrintableState.JOB_SPOOLING_FAILED);
		} else if (printJobEvent.State == PrintableState.JOB_STARTED) {
			log.info("JOB_STARTED");
			this.setStatus(PrintableState.JOB_STARTED);
		}
	}
	
	public String toString(){
		if (status == PrintableState.JOB_COMPLETED) {
			return "JOB_COMPLETED";
		} else if (status == PrintableState.JOB_ABORTED) {
			return "JOB_ABORTED";
		} else if (status == PrintableState.JOB_FAILED) {
			return "JOB_FAILED";
		} else if (status == PrintableState.JOB_SPOOLED) {
			return "JOB_SPOOLED";
		} else if (status == PrintableState.JOB_SPOOLING_FAILED) {
			return "JOB_SPOOLING_FAILED";
		} else if (status == PrintableState.JOB_STARTED) {
			return "JOB_STARTED";
		}
		return "UNDEFINED: " + status;
	}
	
	/**
	 * Disposing event: ignore.
	 */
	public void disposing(EventObject eventObject){
	// Nothing to dispose
	}
}
