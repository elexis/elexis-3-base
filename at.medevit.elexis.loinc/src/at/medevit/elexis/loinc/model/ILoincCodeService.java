package at.medevit.elexis.loinc.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ILoincCodeService {
	
	/**
	 * Return all available LOINC codes.
	 * 
	 * @return
	 */
	public List<LoincCode> getAllCodes();

	/**
	 * Search for the LOINC code with the matching code attribute and return it. Returns null if not
	 * found.
	 * 
	 * @param code
	 * @return
	 */
	public LoincCode getByCode(String code);
	
	/**
	 * Import LOINC code from an InputStream in csv format. The field mapping specifies how the
	 * values from the csv will be mapped.
	 * 
	 * <pre>
	 * 0, LoincCode.FLD_CODE
	 * 1, LoincCode.FLD_LONGNAME
	 * 2, LoincCode.FLD_SHORTNAME
	 * </pre>
	 * 
	 * @param csv
	 * @param fieldMapping
	 * @throws IOException
	 */
	public void importFromCsv(InputStream csv, Map<Integer, String> fieldMapping)
		throws IOException;

	/**
	 * Import the data (top 2000 SI LOINC) codes from included csv files, and update if the file has
	 * been updated.
	 */
	public void updateData();
}
