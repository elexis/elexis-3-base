package ch.elexis.labororder.lg1_medicalvalues.order.model.exceptions;

public class NoEncounterSelectedException extends Exception {
    public NoEncounterSelectedException() {
        super("No encounter selected");
    }
}
