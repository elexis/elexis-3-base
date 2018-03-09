package ch.elexis.data;

import java.util.List;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IFall;
import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.data.importer.ComplementaryReferenceDataImporter;
import ch.elexis.tarmedprefs.PreferenceConstants;
import ch.rgw.tools.Result;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class ComplementaryLeistung extends VerrechenbarAdapter {
	public static final String FLD_CHAPTER = "chapter";
	public static final String FLD_CODE = "code";
	public static final String FLD_CODE_TEXT = "codetext";
	public static final String FLD_DESCRIPTION = "description";
	public static final String FLD_FIXEDVALUE = "fixedvalue";
	public static final String FLD_VALID_FROM = "validfrom";
	public static final String FLD_VALID_TO = "validto";
	
	public static final String VERSION = "0.0.1";
	
	public static final String TABLENAME = "CH_ELEXIS_ARZTTARIFE_CH_COMPLEMENTARY";
	
	public static final String XIDDOMAIN = "www.xid.ch/id/complementary";
	private static final String CODESYSTEMCODE = "590";
	public static final String CODESYSTEMNAME = "Komplementärmedizin";
	
	//@formatter:off
	private static final String createDB = "CREATE TABLE " + TABLENAME + " (" +
	"ID VARCHAR(25) primary key," + 
	"lastupdate BIGINT," +
	"deleted CHAR(1) default '0',"	+
	"chapter VARCHAR(255)," +
	"code VARCHAR(16)," +
	"codetext VARCHAR(255)," +
	"description TEXT," + 
	"fixedvalue VARCHAR(16)," +
	"validfrom CHAR(8)," +
	"validto CHAR(8));" +
	"CREATE INDEX complementaryindex on " + TABLENAME + " (" + FLD_CODE + ");";
	//@formatter:on
	
	static {
		addMapping(TABLENAME, FLD_CHAPTER, FLD_CODE, FLD_CODE_TEXT, FLD_DESCRIPTION, FLD_FIXEDVALUE,
			FLD_VALID_FROM,
			FLD_VALID_TO);
		Xid.localRegisterXIDDomainIfNotExists(XIDDOMAIN, "complementary",
			Xid.ASSIGNMENT_LOCAL);
		ComplementaryLeistung version = ComplementaryLeistung.load("VERSION");
		if (!version.exists()) {
			createOrModifyTable(createDB);
			version.create("VERSION");
			version.set(FLD_CODE, VERSION);
		}
	}
	
	private static IOptifier OPTIFIER;
	
	/**
	 * For factory use only
	 */
	protected ComplementaryLeistung(){
	}
	
	/**
	 * For {@link ComplementaryLeistung#load(String)} only
	 * 
	 * @param id
	 */
	protected ComplementaryLeistung(String id){
		super(id);
	}
	
	/**
	 * For use with {@link ComplementaryReferenceDataImporter}.
	 * 
	 * @param id
	 */
	public ComplementaryLeistung(String id, String chapter, String code, String codeText,
		String description, String validFromString, String validToString){
		create(id);
		set(new String[] {
			ComplementaryLeistung.FLD_CHAPTER, ComplementaryLeistung.FLD_CODE,
			ComplementaryLeistung.FLD_CODE_TEXT, ComplementaryLeistung.FLD_DESCRIPTION,
			ComplementaryLeistung.FLD_VALID_FROM, ComplementaryLeistung.FLD_VALID_TO
		}, chapter, code, codeText, description, validFromString, validToString);
	}
	

	public static ComplementaryLeistung load(String id){
		return new ComplementaryLeistung(id);
	}
	
	@Override
	public int getTP(TimeTool date, IFall fall){
		// configured hourly wage, or fixed value, in cents
		if (isFixedValueSet()) {
			return getFixedValue() * 100;
		} else {
			return getHourlyWage() / 12;
		}
	}
	
	/**
	 * The hourly wage in cents.
	 * 
	 * @return
	 */
	public int getHourlyWage(){
		String wageString =
			CoreHub.mandantCfg.get(PreferenceConstants.COMPLEMENTARY_HOURLY_WAGE, "0");
		return Integer.valueOf(wageString);
	}
	
	/**
	 * Get fixed value of the service in cents.
	 * 
	 * @return
	 */
	public int getFixedValue(){
		try {
			return Integer.valueOf(get(FLD_FIXEDVALUE));
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	/**
	 * Set fixed value of the service in cents. Setting a negative number clears the field.
	 * 
	 * @return
	 */
	public void setFixedValue(int value){
		if (value < 0) {
			set(FLD_FIXEDVALUE, "");
		} else {
			set(FLD_FIXEDVALUE, Integer.toString(value));
		}
	}
	
	/**
	 * Test if the fixed value field is set.
	 * 
	 * @return
	 */
	public boolean isFixedValueSet(){
		String fixedValue = get(FLD_FIXEDVALUE);
		return fixedValue != null && !fixedValue.isEmpty();
	}
	
	@Override
	public boolean isDragOK(){
		return true;
	}
	
	@Override
	public double getFactor(TimeTool date, IFall fall){
		return 1;
	}
	
	@Override
	public String getXidDomain(){
		return XIDDOMAIN;
	}
	
	@Override
	public List<Object> getActions(Object context){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	@Override
	public String getCodeSystemCode(){
		return CODESYSTEMCODE;
	}
	
	@Override
	public String getCodeSystemName(){
		return CODESYSTEMNAME;
	}
	
	@Override
	public String getCode(){
		return get(FLD_CODE);
	}
	
	@Override
	public String getText(){
		return get(FLD_CODE_TEXT);
	}
	
	@Override
	public String getLabel(){
		return "(" + getCode() + ") " + getText();
	}
	
	private TimeTool getValidTo(){
		String value = get(FLD_VALID_TO);
		if (!StringTool.isNothing(value)) {
			TimeTool res = new TimeTool(value);
			res.set(TimeTool.HOUR_OF_DAY, 23);
			res.set(TimeTool.MINUTE, 59);
			res.set(TimeTool.SECOND, 59);
			return res;
		} else {
			return null;
		}
	}
	
	private TimeTool getValidFrom(){
		String value = get(FLD_VALID_FROM);
		if (!StringTool.isNothing(value)) {
			return new TimeTool(value);
		} else {
			return null;
		}
	}
	
	@Override
	public synchronized IOptifier getOptifier(){
		if(OPTIFIER == null) {
			OPTIFIER = new DefaultOptifier() {
				@Override
				public Result<IVerrechenbar> add(IVerrechenbar code, Konsultation kons){
					boolean valid = true;
					// test VVG if necessary
					if (CoreHub.mandantCfg.get(PreferenceConstants.COMPLEMENTARY_FIXTOVVG, false)) {
						String gesetz = kons.getFall().getRequiredString("Gesetz");
						String system = kons.getFall().getAbrechnungsSystem();
						if(gesetz.isEmpty()) {
							if (!"vvg".equalsIgnoreCase(system)) {
								valid = false; 
							}
						} else {
							if (!"vvg".equalsIgnoreCase(gesetz)) {
								valid = false;
							}
						}
					}
					return valid ? super.add(code, kons) : new Result<IVerrechenbar>(Result.SEVERITY.WARNING, 0,
							"Komplementärmedizinische Leistungen können nur auf eine Fall mit Gesetz oder Name VVG verrechnet werden.",
								null, false);
				}
			};
		}
		return OPTIFIER;
	}
	
	/**
	 * Load a {@link ComplementaryLeistung} from code that is valid at the date.
	 * 
	 * @param code
	 * @param date
	 * @return
	 */
	public static IVerrechenbar getFromCode(final String code, TimeTool date){
		Query<ComplementaryLeistung> query =
			new Query<ComplementaryLeistung>(ComplementaryLeistung.class, FLD_CODE, code, TABLENAME,
				new String[] {
					FLD_VALID_FROM, FLD_VALID_TO
				});
		List<ComplementaryLeistung> leistungen = query.execute();
		for (ComplementaryLeistung leistung : leistungen) {
			TimeTool validFrom = leistung.getValidFrom();
			TimeTool validTo = leistung.getValidTo();
			if (date.isAfterOrEqual(validFrom) && date.isBeforeOrEqual(validTo))
				return leistung;
		}
		return null;
	}
	
	public boolean isValid(TimeTool now){
		if (now.isAfter(getValidFrom()) && now.isBeforeOrEqual(getValidTo())) {
			return true;
		}
		return false;
	}
}
