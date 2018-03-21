package ch.medshare.util;

public interface SystemProperties {
	/**
	 * The platform-dependent file separator (e.g., "/" on UNIX, "\" for Windows)
	 */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator"); //$NON-NLS-1$
	
	/**
	 * The value of the CLASSPATH environment variable
	 */
	public static final String JAVA_CLASS_PATH = System.getProperty("java.class.path"); //$NON-NLS-1$
	
	/**
	 * The version of the Java API
	 */
	public static final String JAVA_CLASS_VERSION = System.getProperty(" java.class.version"); //$NON-NLS-1$
	
	/**
	 * The directory in which Java is installed
	 */
	public static final String JAVA_HOME = System.getProperty("java.home"); //$NON-NLS-1$
	
	/**
	 * The version of the Java interpreter
	 */
	public static final String JAVA_VERSION = System.getProperty("java.version"); //$NON-NLS-1$
	
	/**
	 * The platform-dependent line separator (e.g., "\n" on UNIX, "\r\n" for Windows)
	 */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$
	
	/**
	 * The name of the operating system
	 */
	public static final String OS_NAME = System.getProperty("os.name"); //$NON-NLS-1$
	
	/**
	 * The operating system version
	 */
	public static final String OS_VERSION = System.getProperty("os.version"); //$NON-NLS-1$
	
	/**
	 * The platform-dependent path separator (e.g., ":" on UNIX, "," for Windows)
	 */
	public static final String PATH_SEPARATOR = System.getProperty("path.separator"); //$NON-NLS-1$
	
	/**
	 * The current working directory when the properties were initialized
	 */
	public static final String USER_DIR = System.getProperty("user.dir"); //$NON-NLS-1$
	
	/**
	 * The home directory of the current user
	 */
	public static final String USER_HOME = System.getProperty("user.home"); //$NON-NLS-1$
	
	/**
	 * The two-letter language code of the default locale
	 */
	public static final String USER_LANGUAGE = System.getProperty("user.language"); //$NON-NLS-1$
	
	/**
	 * The username of the current user
	 */
	public static final String USER_NAME = System.getProperty("user.name"); //$NON-NLS-1$
}
