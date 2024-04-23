package ch.elexis.fire.core;

import org.hl7.fhir.r4.model.Bundle;

public interface IFIREService {

	public Bundle initialExport();

	public Bundle incrementalExport();
}
