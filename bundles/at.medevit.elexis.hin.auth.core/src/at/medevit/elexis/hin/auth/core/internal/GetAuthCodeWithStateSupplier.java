package at.medevit.elexis.hin.auth.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class GetAuthCodeWithStateSupplier implements Supplier<String> {

	private String state;

	public GetAuthCodeWithStateSupplier(String state) {
		this.state = state;
	}

	@Override
	public String get() {
		// https://tools.medelexis.ch/hin/ac-obtain/state
		String ret = null;
		try (InputStream input = new URL(
				"https://tools.medelexis.ch/hin/ac-obtain/" + URLEncoder.encode(state, "UTF-8")).openStream()) {
			String token = IOUtils.toString(input, "UTF-8");
			if (StringUtils.isNotBlank(token)) {
				ret = token;
			}
		} catch (IOException e) {
			// ignore failed request
		}
		return ret;
	}
}
