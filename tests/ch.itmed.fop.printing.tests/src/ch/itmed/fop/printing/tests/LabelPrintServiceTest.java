package ch.itmed.fop.printing.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;
import ch.itmed.fop.printing.print.LabelPrintService;

public class LabelPrintServiceTest {

	private final IArticle pantozol = proxy(IArticle.class, Map.of());
	private final IArticle dafalgan = proxy(IArticle.class, Map.of());

	@Test
	public void findUniquePrescriptionNone() {
		assertFalse(LabelPrintService.findUniquePrescription(pantozol, Collections.emptyList()).isPresent());
		assertFalse(LabelPrintService
				.findUniquePrescription(pantozol,
						Arrays.asList(prescription(dafalgan, EntryType.FIXED_MEDICATION, "1-0-0-0", null)))
				.isPresent());
	}

	@Test
	public void findUniquePrescriptionExactlyOne() {
		IPrescription fixed = prescription(pantozol, EntryType.FIXED_MEDICATION, "1-0-0-0", null);
		IPrescription other = prescription(dafalgan, EntryType.RESERVE_MEDICATION, "1", null);
		assertSame(fixed, LabelPrintService.findUniquePrescription(pantozol, Arrays.asList(other, fixed)).get());

		IPrescription symptomatic = prescription(pantozol, EntryType.SYMPTOMATIC_MEDICATION, "1", null);
		assertSame(symptomatic,
				LabelPrintService.findUniquePrescription(pantozol, Arrays.asList(symptomatic)).get());
	}

	@Test
	public void findUniquePrescriptionMultiple() {
		IPrescription fixed = prescription(dafalgan, EntryType.FIXED_MEDICATION, "1-0-1-0", null);
		IPrescription reserve = prescription(dafalgan, EntryType.RESERVE_MEDICATION, "max 4", null);
		assertFalse(LabelPrintService.findUniquePrescription(dafalgan, Arrays.asList(fixed, reserve)).isPresent());
	}

	@Test
	public void findUniquePrescriptionIgnoresOtherEntryTypes() {
		IPrescription fixed = prescription(pantozol, EntryType.FIXED_MEDICATION, "1-0-0-0", null);
		IPrescription recipe = prescription(pantozol, EntryType.RECIPE, "1-0-0-0", null);
		IPrescription selfDispensed = prescription(pantozol, EntryType.SELF_DISPENSED, "1-0-0-0", null);
		assertSame(fixed, LabelPrintService
				.findUniquePrescription(pantozol, Arrays.asList(recipe, fixed, selfDispensed)).get());
	}

	@Test
	public void findUniquePrescriptionNullSafe() {
		assertFalse(LabelPrintService.findUniquePrescription(null, Collections.emptyList()).isPresent());
		assertFalse(LabelPrintService.findUniquePrescription(pantozol, null).isPresent());
	}

	@Test
	public void hasDoseOrRemark() {
		assertTrue(LabelPrintService.hasDoseOrRemark(prescription(pantozol, EntryType.FIXED_MEDICATION, "1-0-0-0", null)));
		assertTrue(LabelPrintService
				.hasDoseOrRemark(prescription(pantozol, EntryType.FIXED_MEDICATION, null, "vor dem Essen")));
		assertFalse(LabelPrintService.hasDoseOrRemark(prescription(pantozol, EntryType.FIXED_MEDICATION, " ", "")));
		assertFalse(LabelPrintService.hasDoseOrRemark(prescription(pantozol, EntryType.FIXED_MEDICATION, null, null)));
		assertFalse(LabelPrintService.hasDoseOrRemark(null));
	}

	@Test
	public void isPrintAllowed() {
		assertTrue(LabelPrintService.isPrintAllowed("Etiketten", true));
		assertFalse(LabelPrintService.isPrintAllowed("", true));
		assertFalse(LabelPrintService.isPrintAllowed(null, true));
		// without required printer the default printer is used
		assertTrue(LabelPrintService.isPrintAllowed("", false));
		assertTrue(LabelPrintService.isPrintAllowed(null, false));
		assertEquals(true, LabelPrintService.isPrintAllowed("Etiketten", false));
	}

	private static IPrescription prescription(IArticle article, EntryType entryType, String dose, String remark) {
		Map<String, Object> values = new HashMap<>();
		values.put("getArticle", article);
		values.put("getEntryType", entryType);
		values.put("getDosageInstruction", dose);
		values.put("getRemark", remark);
		return proxy(IPrescription.class, values);
	}

	@SuppressWarnings("unchecked")
	static <T> T proxy(Class<T> clazz, Map<String, Object> returnValues) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz }, (p, method, args) -> {
			switch (method.getName()) {
			case "hashCode":
				return System.identityHashCode(p);
			case "equals":
				return p == args[0];
			case "toString":
				return clazz.getSimpleName() + returnValues;
			default:
				if (returnValues.containsKey(method.getName())) {
					return returnValues.get(method.getName());
				}
				return method.getReturnType() == boolean.class ? false : null;
			}
		});
	}
}
