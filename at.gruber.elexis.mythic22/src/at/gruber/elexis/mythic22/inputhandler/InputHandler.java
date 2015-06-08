/*******************************************************************************
 * Copyright (c) 2011, Christian Gruber and MEDEVIT OG
 * All rights reserved.
 *******************************************************************************/
package at.gruber.elexis.mythic22.inputhandler;

import java.util.HashMap;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gruber.elexis.mythic22.model.HaematologicalValue;
import at.gruber.elexis.mythic22.model.Mythic22Result;

public class InputHandler {
	private static Logger logger = LoggerFactory.getLogger(InputHandler.class);
	
	// Map to hold input data
	private HashMap<String, String> m_inputMap = new HashMap<String, String>();
	
	// singleton instance
	private static InputHandler m_instance = null;
	
	/**
	 * 
	 * @return the single instance of this class
	 */
	public static InputHandler getInstance(){
		if (m_instance == null) {
			m_instance = new InputHandler();
		}
		return m_instance;
	}
	
	/**
	 * constructor prepares m_inputMap with all predefined fields
	 */
	private InputHandler(){
		super();
		for (String s : Mythic22Result.FIELDS) {
			m_inputMap.put(s, "");
		}
		for (String s : Mythic22Result.HAEMATOLOGICALFIELDS) {
			m_inputMap.put(s, "");
		}
	}
	
	/**
	 * Main entry point of regular Mythic22 Output parsing
	 * 
	 * @param input
	 *            - the input String sent by Mythic22
	 * @return Mythic22Result Object
	 */
	public Mythic22Result processInput(String input){
		fillInputMap(input);
		return genereateMythic22Result();
	}
	
	/**
	 * parses the input string and puts all defined values into m_inputMap for further processing
	 * 
	 * @param input
	 */
	private void fillInputMap(String input){
		
		String processedInput = processMatrices(input);
		
		String[] lines = processedInput.split("\n");
		// prepare the header line of the mythic22 input
		lines[0] = lines[0].replaceFirst(" ", ";");
		
		for (String line : lines) {
			String key = line.substring(0, line.indexOf(";"));
			String value = line.substring(line.indexOf(";") + 1);
			m_inputMap.put(key, value);
		}
	}
	
	/**
	 * puts the matrices into the m_inputMap and returns a string, without the matrices
	 * 
	 * @param input
	 * @return a string, without the matrices
	 */
	private String processMatrices(String input){
		try {
			// first two matrices end with ";T\n"
			String lmneMatrix =
				input.substring(input.indexOf(Mythic22Result.LMNEMATRIX),
					input.indexOf(";T\n", input.indexOf(Mythic22Result.LMNEMATRIX)));
			String lmneShadeMatrix =
				input.substring(input.indexOf(Mythic22Result.LMNESHADEMATRIX),
					input.indexOf(";T\n", input.indexOf(Mythic22Result.LMNESHADEMATRIX)));
			
			// third matrix takes up exactly 3 lines
			int endIndex =
				getIndexof(input, "\n", input.indexOf(Mythic22Result.THRES5DLMNEMATRIX), 3);
			String thres5dLmneMatrix =
				input.substring(input.indexOf(Mythic22Result.THRES5DLMNEMATRIX), endIndex);
			
			// remove matrices from initial input
			input = input.replaceFirst(lmneMatrix + ";T\n", "");
			input = input.replaceFirst(lmneShadeMatrix + ";T\n", "");
			input = input.replaceFirst(thres5dLmneMatrix + '\n', "");
			
			// place processed matrices in m_inputMap
			m_inputMap.put(Mythic22Result.LMNEMATRIX,
				lmneMatrix.substring(lmneMatrix.indexOf('\n') + 1));
			m_inputMap.put(Mythic22Result.LMNESHADEMATRIX,
				lmneMatrix.substring(lmneMatrix.indexOf('\n') + 1));
			m_inputMap.put(Mythic22Result.THRES5DLMNEMATRIX,
				lmneMatrix.substring(thres5dLmneMatrix.indexOf('\n') + 1));
		} catch (IndexOutOfBoundsException e) {
			logger.error("index error while processing matrix", e);
		}
		
		return input;
	}
	
	/**
	 * 
	 * @param str
	 *            - the string to look for occurrences in
	 * @param regex
	 *            - the expression to be searched
	 * @param fromIndex
	 *            - the index to start searching from
	 * @param occurrences
	 *            - the index of the occurrence which should be searched (example: 3 looks for the
	 *            third occurrence)
	 * @return the searched index
	 */
	private int getIndexof(String str, String regex, int fromIndex, int occurrences){
		
		int returnIndex = fromIndex;
		
		for (int i = 0; i < 3; i++) {
			returnIndex = str.indexOf(regex, returnIndex + 1);
		}
		return returnIndex;
	}
	
	/**
	 * generates a Mythic22Result object from m_inputMap
	 * 
	 * @return Mythic22Result Object
	 */
	private Mythic22Result genereateMythic22Result(){
		
		LinkedList<HaematologicalValue> haemaValuesList = new LinkedList<HaematologicalValue>();
		HashMap<String, LinkedList<String>> defaultValuesList =
			new HashMap<String, LinkedList<String>>();
		
		// process Haematological Values
		for (String field : Mythic22Result.HAEMATOLOGICALFIELDS) {
			haemaValuesList.add(new HaematologicalValue(field, m_inputMap.get(field)));
		}
		
		// process other values
		for (String field : Mythic22Result.FIELDS) {
			defaultValuesList.put(field, createValuesList(m_inputMap.get(field)));
		}
		
		return (new Mythic22Result(haemaValuesList, defaultValuesList));
	}
	
	private LinkedList<String> createValuesList(String csvString){
		
		String[] temp = csvString.split(";");
		LinkedList<String> returnList = new LinkedList<String>();
		
		for (String s : temp) {
			returnList.add(s.trim());
		}
		
		return returnList;
	}
	
	public HashMap<String, String> getInputMap(){
		return m_inputMap;
	}
	
}
