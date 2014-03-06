import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import waelti.statistics.export.CSVWriter;
import waelti.statistics.queries.ResultMatrix;

public class WriterTest {
	
	private ResultMatrix matrix;
	
	@Before
	public void setUp(){
		List<Object[]> list = new ArrayList<Object[]>(5);
		list.add(new Object[] {
			"1,1", "1,2", "1,3"
		});
		list.add(new Object[] {
			"2,1", "2,2", "2,3"
		});
		list.add(new Object[] {
			"3,1", "3,2", "3,3"
		});
		list.add(new Object[] {
			"4,1", "4,2", "4,3"
		});
		list.add(new Object[] {
			"5,1", "5,2", "5,3"
		});
		
		List<String> headings = new ArrayList<String>();
		headings.add("1col");
		headings.add("2col");
		headings.add("3col");
		
		matrix = new ResultMatrix(list, headings);
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testWriteAbstractQueryFile() throws FileNotFoundException{
		File file;
		try {
			file = CSVWriter.writer(matrix, new File("test"));
			
			Scanner scn = new Scanner(file);
			assertTrue(scn.next().equals("1col;2col;3col"));
			assertTrue(scn.next().equals("1,1;1,2;1,3"));
			assertTrue(scn.next().equals("2,1;2,2;2,3"));
			
			scn.next();// 3
			scn.next();// 4
			scn.next();// 5
			scn.next();// should not exist
			
		} catch (IOException e) {
			assertTrue(false);
		}
		
	}
	
}
