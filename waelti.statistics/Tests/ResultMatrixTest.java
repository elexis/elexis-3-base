import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import waelti.statistics.queries.ResultMatrix;
import ch.rgw.tools.Money;

public class ResultMatrixTest {
	
	private ResultMatrix matrix;
	
	// layout:
	// Names Money1 Money2 Count [HEADING]
	// Name1 100 10 12
	// Name2 200 20 25
	// Name3 300 30 36
	// Name4 400 40 48
	
	@Before
	public void setUp(){
		List<Object[]> list = new ArrayList<Object[]>();
		List<String> head = new ArrayList<String>();
		
		list.add(new Object[] {
			"Name1", new Money(100), new Money(10), new Integer(12)
		});
		list.add(new Object[] {
			"Name2", new Money(200), new Money(20), new Integer(25)
		});
		list.add(new Object[] {
			"Name3", new Money(300), new Money(30), new Integer(36)
		});
		list.add(new Object[] {
			"Name4", new Money(400), new Money(40), new Integer(48)
		});
		
		head.add("Names");
		head.add("Money1");
		head.add("Money2");
		head.add("Count");
		
		matrix = new ResultMatrix(list, head);
	}
	
	@Test
	public void testGet(){
		assertTrue(matrix.get(0, 0).equals("Name1"));
		// no changes when calling:
		assertTrue(matrix.get(0, 0).equals("Name1"));
		
		assertTrue(matrix.get(2, 3).equals(new Money(40)));
		
	}
	
	@Test
	public void testGetRow(){
		Object[] row3 = matrix.getRow(3);
		assertTrue(row3[0].equals("Name4"));
		assertTrue(row3.length == 4);
		assertTrue(row3[3].equals(48));
		
	}
	
	@Test
	public void testGetColumn(){
		Object[] col = matrix.getColumn(2);
		assertTrue(col[0].equals(new Money(10)));
		assertTrue(col[1].equals(new Money(20)));
		assertTrue(col[3].equals(new Money(40)));
		assertTrue(col.length == 4);
	}
	
	@Test
	public void testAddRow(){
		Object[] col = matrix.getColumn(0);
		assertTrue(col.length == 4);
		
		matrix.addRow();
		
		col = matrix.getColumn(0);
		assertTrue(col.length == 5);
		assertTrue(col[4] == null); // added row
		
	}
	
	@Test
	public void testAddRowObjectArray(){
		Object[] newRow = new Object[] {
			"Name5", new Money(500), new Money(50), new Integer(59)
		};
		matrix.addRow(newRow);
		
		assertTrue(matrix.getRow(4)[0].equals("Name5"));
		assertTrue(matrix.getRow(3)[0].equals("Name4"));
		
	}
	
	@Test
	public void testHeadings(){
		assertTrue(matrix.getHeadings().get(0).equals("Names"));
		
		List<String> newHeading = new ArrayList<String>();
		
		newHeading.add("New Name");
		newHeading.add("New Money1");
		newHeading.add("New Money2");
		newHeading.add("New Count");
		
		matrix.setHeadings(newHeading);
		assertTrue(matrix.getHeadings().get(0).equals("New Name"));
		
	}
}
