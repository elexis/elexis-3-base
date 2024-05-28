package at.medevit.elexis.emediplan.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.LoggerFactory;

import ch.elexis.core.jdt.NonNull;

public class EMediplanUtil {

	/**
	 * Get the encoded (Header with zipped and Base64 encoded content) String. The
	 * header of the current CHMED Version is added to the resulting String.
	 *
	 * @param json
	 * @return
	 */
	public static final String getEncodedJson(@NonNull String json) {
		StringBuilder sb = new StringBuilder();
		// header for compresses json
		sb.append("CHMED16A1"); //$NON-NLS-1$

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
			gzip.write(json.getBytes());
		} catch (IOException e) {
			LoggerFactory.getLogger(EMediplanUtil.class).error("Error encoding json", e); //$NON-NLS-1$
			throw new IllegalStateException("Error encoding json", e); //$NON-NLS-1$
		}
		sb.append(Base64.getEncoder().encodeToString(out.toByteArray()));
		return sb.toString();
	}

	/**
	 * Get the decoded String, from the zipped and Base64 encoded String. The first
	 * 9 characters (CHMED header) are ignored.
	 *
	 * @param encodedJson
	 * @return
	 */
	public static String getDecodedJsonString(@NonNull String encodedJson) {
		String content = encodedJson.substring(9);
		byte[] zipped = Base64.getMimeDecoder().decode(content);
		StringBuilder sb = new StringBuilder();
		try {
			GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(zipped));
			InputStreamReader reader = new InputStreamReader(gzip);
			BufferedReader in = new BufferedReader(reader);
			// Probably only single json line, but just to be sure ...
			String read;
			while ((read = in.readLine()) != null) {
				sb.append(read);
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(EMediplanUtil.class).error("Error decoding json", e); //$NON-NLS-1$
			throw new IllegalStateException("Error decoding json", e); //$NON-NLS-1$
		}
		return sb.toString();
	}

}
