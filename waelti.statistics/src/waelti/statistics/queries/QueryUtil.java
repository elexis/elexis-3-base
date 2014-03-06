package waelti.statistics.queries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import ch.elexis.data.Patient;
import ch.rgw.tools.Money;

public class QueryUtil {
	
	/**
	 * Extracts the year from a string formatted as given by Patient.getGeburtsdatum().<br>
	 * If the string is empty, '0' is returned.
	 * 
	 * @return year of birth as an integer.
	 * @param date
	 *            a string in the format "DD.MM.YYYY"
	 * @see Patient
	 */
	public static int extractYear(String date){
		if (date.length() != 0) {
			return new Integer(date.substring(6, 10));
		} else {
			return 0;
		}
	}
	
	/**
	 * This method generates a list containing as many arrays as there are years between the given
	 * firstYear integer and the actual year. Every array contains in the first column the year as
	 * an integer. All other array elements are filled with integer values of 0.<br>
	 * This is useful if you want to create some time series.
	 * 
	 * @param firstYear
	 *            the first year of this time series.
	 * @param arraySize
	 *            the array size of each year, inclusive the date.
	 * @return list containing an array for each year in the time series and its value as an integer
	 *         in the first column of the array.
	 * 
	 */
	public static List<Object[]> initiateYears(int firstYear, int arraySize){
		List<Object[]> result = new ArrayList<Object[]>();
		
		// getting the actual Date.
		Calendar currentDate = Calendar.getInstance();
		
		for (int i = firstYear; i <= currentDate.get(Calendar.YEAR); i++) {
			
			Object[] row = new Object[arraySize];
			row[0] = i;
			
			for (int j = 1; j < arraySize; j++) {
				row[j] = 0;
			}
			
			result.add(row);
		}
		
		return result;
	}
	
	/**
	 * Returns a new list with the cohorts given the size given. The array of the list has to
	 * contain all numerical values. The result list will contain strings in the first element of
	 * each array.
	 * 
	 * @param intervall
	 *            the size of the cohorts.
	 * @return a new list.
	 */
	public static List<Object[]> createCohorts(List<Object[]> list, int intervall){
		
		List<Object[]> newList = new ArrayList<Object[]>(list.size() / intervall + 1);
		
		Object[] row = new Object[list.get(0).length];
		
		int i = 0;
		for (Object[] objects : list) {
			
			if (i % intervall == 0) {
				if (i != 0) { // index == 0 --> nothing to add to list.
					newList.add(row);
				}
				row = new Object[list.get(0).length];
				
				row[0] = QueryUtil.getCohortName(objects[0], intervall);
				
				for (int j = 1; j < row.length; j++) {
					row[j] = new Double(0);
				}
			}
			
			for (int j = 1; j < objects.length; j++) {
				Double num = Double.valueOf(objects[j].toString());
				row[j] = (Double) row[j] + num;
			}
			
			i++;
		}
		
		newList.add(row); // add the last cohort
		return newList;
	}
	
	/** Returns a string in the format: "1990 - 1995" */
	private static Object getCohortName(Object object, int intervall){
		String name = object.toString();
		name += " - ";
		
		Integer lastYear = Integer.valueOf(object.toString());
		lastYear += intervall - 1; // start date is the first
		
		name += lastYear;
		return name;
	}
	
	/**
	 * This methods converts any List containing arrays of objects into a list containing arrays of
	 * strings. This is for convenience, since handling strings from the beginning of the
	 * computation can be complicated and clutter the code.
	 */
	public static List<String[]> convertToString(List<Object[]> list){
		assert (list != null);
		
		List<String[]> result = new ArrayList<String[]>();
		
		for (Object[] row : list) {
			String[] newRow = new String[row.length];
			
			for (int i = 0; i < newRow.length; i++) {
				newRow[i] = row[i].toString();
			}
			
			result.add(newRow);
		}
		
		return result;
	}
	
	/** Converts a double value of Rappen to a string in CHF. */
	public static void convertToCurrency(List<Object[]> list, int column){
		for (Object[] objects : list) {
			Double value = Double.valueOf(objects[column].toString());
			value /= 100;
			objects[column] = new Money(value);
		}
	}
	
	/**
	 * Rounds a given amount of money in Rappen to the next 5 Rappen.
	 * 
	 * @param Amount
	 *            of Rappen
	 * @return Amounf of CHF, in steps of 5 Rappen
	 */
	public static double convertAndRoundMoney(int num){
		
		int temp = num;
		
		if (temp % 5 != 0) {
			int add = 5 - (temp % 5);
			temp += add;
		}
		return (double) temp / 100;
		
	}
	
	/** InColumn1 / InColumn2 = resultColumn. ResultColumn will be a double. */
	public static List<Object[]> addAverage(List<Object[]> list, int InColumn1, int InColumn2,
		int resultColumn){
		for (Object[] objects : list) {
			// TODO check math rounding
			double div = Double.valueOf(objects[InColumn1].toString());
			double div2 = Double.valueOf(objects[InColumn2].toString());
			
			if (div2 != 0) {
				objects[resultColumn] = div / div2;
			}
		}
		
		return list;
	}
	
	/**
	 * Converts a Patient's birth date to a calendar object. Format of the given String has to be of
	 * 'dd.mm.yyyy'
	 */
	public static Calendar convertToCalendar(String dateString) throws NumberFormatException{
		Calendar cal = Calendar.getInstance();
		cal.setLenient(false); // throws exception if wrong date is set.
		
		Scanner scn = new Scanner(dateString).useDelimiter("\\.");
		
		int day = new Integer(scn.next());
		int month = new Integer(scn.next()) - 1; // month 0 = january
		int year = new Integer(scn.next());
		
		cal.set(year, month, day);
		
		return cal;
	}
	
	/**
	 * Throws an IllegalArgumentException if one the the calendar values are wrong.
	 */
	public static String convertFromCalendar(Calendar cal){
		StringBuffer str = new StringBuffer(10);
		
		str.append(cal.get(Calendar.DAY_OF_MONTH));
		str.append(".");
		str.append(cal.get(Calendar.MONTH) + 1); // month 0 is january
		str.append(".");
		str.append(cal.get(Calendar.YEAR));
		
		return str.toString();
	}
	
	public static void convertDoubleToInteger(List<Object[]> list, int index){
		for (Object[] objects : list) {
			Double d = (Double) objects[index];
			int i = d.intValue();
			objects[index] = i;
		}
	}
}
