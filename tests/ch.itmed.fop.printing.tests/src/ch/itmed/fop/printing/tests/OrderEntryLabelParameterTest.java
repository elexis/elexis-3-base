package ch.itmed.fop.printing.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import ch.itmed.fop.printing.print.OrderEntryLabelParameter;

public class OrderEntryLabelParameterTest {

	@Test
	public void parseValid() {
		Map<String, Integer> entries = OrderEntryLabelParameter.parse("a1b2:2;c3d4:1");
		assertEquals(2, entries.size());
		assertEquals(Integer.valueOf(2), entries.get("a1b2"));
		assertEquals(Integer.valueOf(1), entries.get("c3d4"));
		assertEquals(Arrays.asList("a1b2", "c3d4"), new ArrayList<>(entries.keySet()));
	}

	@Test
	public void parseEmpty() {
		assertTrue(OrderEntryLabelParameter.parse(null).isEmpty());
		assertTrue(OrderEntryLabelParameter.parse("").isEmpty());
		assertTrue(OrderEntryLabelParameter.parse(" ; ").isEmpty());
	}

	@Test
	public void parseIgnoresInvalid() {
		Map<String, Integer> entries = OrderEntryLabelParameter
				.parse("valid:3;noAmount;:2;nonNumeric:x;zero:0;negative:-1;too:many:parts;");
		assertEquals(1, entries.size());
		assertEquals(Integer.valueOf(3), entries.get("valid"));
	}

	@Test
	public void parseSumsDuplicates() {
		Map<String, Integer> entries = OrderEntryLabelParameter.parse("a:1;a:2");
		assertEquals(Integer.valueOf(3), entries.get("a"));
	}
}
