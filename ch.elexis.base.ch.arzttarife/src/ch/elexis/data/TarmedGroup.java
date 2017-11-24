package ch.elexis.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.elexis.data.TarmedKumulation.TarmedKumulationType;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class TarmedGroup extends PersistentObject {
	
	public static final String TABLENAME = "TARMED_GROUP";
	
	private static final String FLD_GROUPNAME = "GroupName";
	private static final String FLD_SERVICES = "Services";
	private static final String FLD_LAW = "Law";
	private static final String FLD_VALIDFROM = "ValidFrom";
	private static final String FLD_VALIDTO = "ValidTo";
	
	public static final String ROW_VERSION = "Version";
	private static final String VERSION = "1.0.0";
	
	public static final Object SERVICES_SEPARATOR = "|";
	
	public static TimeTool curTimeHelper = new TimeTool();
	private TarmedExtension extension;
	
	// @formatter:off
	public static final String createDB = "CREATE TABLE " + TABLENAME + " (" 
		+"ID 					VARCHAR(32) primary key,"  
		+"lastupdate 			BIGINT," 
		+"deleted 				CHAR(1) default '0',"
		
		+ "GroupName			VARCHAR(32),"
		+ "Services				TEXT,"
		+ "Law					VARCHAR(3),"
		+ "ValidFrom			CHAR(8),"
		+ "ValidTo				CHAR(8)" 
		+ ");"
		+ "CREATE INDEX tarmedgroup_idx1 on " + TABLENAME + " (" + FLD_GROUPNAME + ");"		
		+ "CREATE INDEX tarmedgroup_idx2 on " + TABLENAME + " (" + FLD_LAW + ");"		
		+ "INSERT INTO " + TABLENAME + " (" + FLD_ID + ", " + FLD_GROUPNAME + ") VALUES (" + JdbcLink.wrap(ROW_VERSION) + ", " + JdbcLink.wrap(VERSION) + ");";
	//@formatter:on
	
	static {
		addMapping(TABLENAME, FLD_GROUPNAME, FLD_SERVICES, FLD_LAW, FLD_VALIDFROM, FLD_VALIDTO);
		
		if (!tableExists(TABLENAME)) {
			createOrModifyTable(createDB);
		} else {
			TarmedGroup version = load(TarmedGroup.ROW_VERSION);
			VersionInfo vi = new VersionInfo(version.get(FLD_GROUPNAME));
			if (vi.isOlder(VERSION)) {
				// put update code here ...
				version.set(FLD_GROUPNAME, VERSION);
			}
		}
	}
	
	protected TarmedGroup(){
		// leer
	}
	
	public static TarmedGroup load(String id){
		return new TarmedGroup(id);
	}
	
	public static Optional<TarmedGroup> find(String groupName, String law, TimeTool validFrom){
		Query<TarmedGroup> query = new Query<>(TarmedGroup.class);
		query.add(TarmedGroup.FLD_GROUPNAME, Query.EQUALS, groupName);
		query.add(TarmedGroup.FLD_LAW, Query.EQUALS, law);
		List<TarmedGroup> groups = query.execute();
		groups = groups.stream().filter(g -> g.validAt(validFrom)).collect(Collectors.toList());
		if (!groups.isEmpty()) {
			return Optional.of(groups.get(0));
		}
		return Optional.empty();
	}
	
	public boolean validAt(TimeTool validTime){
		TimeTool validFrom = new TimeTool(get(FLD_VALIDFROM));
		TimeTool validTo = new TimeTool(get(FLD_VALIDTO));
		return validTime.isAfterOrEqual(validFrom) && validTime.isBeforeOrEqual(validTo);
	}
	
	protected TarmedGroup(String id){
		super(id);
	}
	
	/**
	 * Used to create a new {@link TarmedGroup} from a TransientTarmedGroup by the GroupImporter.
	 * 
	 * @param id
	 * @param groupname
	 * @param law
	 * @param validFrom
	 * @param validTo
	 */
	public TarmedGroup(final String id, String groupname, String law,
		String validFrom, String validTo, String services){
		create(id, new String[] {
			FLD_GROUPNAME, FLD_LAW, FLD_VALIDFROM, FLD_VALIDTO, FLD_SERVICES
		}, new String[] {
			groupname, law, validFrom, validTo, services
		});
		extension = new TarmedExtension(this);
	}
	
	@Override
	public String getLabel(){
		return get(FLD_ID) + " - " + get(FLD_SERVICES);
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public void addService(String serviceCode){
		StringBuilder sb = new StringBuilder(get(FLD_SERVICES));
		if (sb.length() > 0) {
			sb.append(SERVICES_SEPARATOR);
		}
		sb.append(serviceCode);
		set(FLD_SERVICES, sb.toString());
	}
	
	public List<String> getServices(){
		String value = get(FLD_SERVICES);
		if (value != null && !value.isEmpty()) {
			String[] parts = value.split("\\" + SERVICES_SEPARATOR);
			return Arrays.asList(parts);
		}
		return Collections.emptyList();
	}
	
	/**
	 * Get the {@link TarmedExtension} object for this {@link TarmedLeistung}.
	 * 
	 * @return
	 */
	public TarmedExtension getExtension(){
		if (extension == null) {
			extension = TarmedExtension.getExtension(this);
		}
		return extension;
	}
	
	@SuppressWarnings("unchecked")
	public Hashtable<String, String> loadExtension(){
		if (getExtension() != null) {
			return (Hashtable<String, String>) extension.getMap(TarmedExtension.FLD_LIMITS);
		}
		return new Hashtable<>();
	}
	
	@SuppressWarnings("unchecked")
	public void setExtension(Hashtable<? extends Object, ? extends Object> map){
		if (getExtension() != null) {
			extension.setMap(TarmedExtension.FLD_LIMITS, (Map<Object, Object>) map);
		} else {
			throw new IllegalStateException("No Extension available for tarmed [" + getId() + "]");
		}
	}
	
	public String getCode(){
		return get(TarmedGroup.FLD_GROUPNAME);
	}
	
	/**
	 * Get the exclusions valid now as String, containing the service and chapter codes. Group
	 * exclusions are NOT part of the String.
	 * 
	 * @param kons
	 * 
	 * @return
	 */
	public List<TarmedExclusion> getExclusions(Konsultation kons){
		if (kons == null) {
			curTimeHelper.setTime(new Date());
		} else {
			curTimeHelper.set(kons.getDatum());
		}
		return getExclusions(curTimeHelper);
	}
	
	/**
	 * Get {@link TarmedExclusion} objects with this {@link TarmedLeistung} as master.
	 * 
	 * @param date
	 * @return
	 */
	public List<TarmedExclusion> getExclusions(TimeTool date){
		return TarmedKumulation.getExclusions(getCode(), TarmedKumulationType.GROUP, date,
			get(TarmedLeistung.FLD_LAW));
	}
	
	public List<TarmedLimitation> getLimitations(){
		String lim = (String) loadExtension().get("limits"); //$NON-NLS-1$
		if (lim != null && !lim.isEmpty()) {
			List<TarmedLimitation> ret = new ArrayList<>();
			String[] lines = lim.split("#"); //$NON-NLS-1$
			for (String line : lines) {
				ret.add(TarmedLimitation.of(line).setTarmedGroup(this));
			}
			return ret;
		}
		return Collections.emptyList();
	}
}
