package ch.elexis.regiomed.order.config;

/**
 * Configuration constants for HTTP-related settings used in Regiomed API
 * requests.
 */
public class RegiomedHttpConstants {

	public static final String METHOD_POST = "POST"; //$NON-NLS-1$
	public static final String METHOD_GET = "GET"; //$NON-NLS-1$

	public static final String HEADER_CONTENT_TYPE = "Content-Type"; //$NON-NLS-1$
	public static final String HEADER_ACCEPT = "Accept"; //$NON-NLS-1$
	public static final String HEADER_AUTHORIZATION = "Authorization"; //$NON-NLS-1$

	public static final String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=UTF-8"; //$NON-NLS-1$
	public static final String ACCEPT_JSON = "application/json"; //$NON-NLS-1$

	public static final String AUTH_BEARER_PREFIX = "Bearer "; //$NON-NLS-1$

	public static final int HTTP_OK = 200;
	public static final int HTTP_MULTIPLE_CHOICES = 300;
	public static final int HTTP_BAD_REQUEST = 400;
	public static final int HTTP_INTERNAL_ERROR = 500;

}