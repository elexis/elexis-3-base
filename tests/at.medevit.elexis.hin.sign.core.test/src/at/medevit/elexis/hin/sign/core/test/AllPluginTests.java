package at.medevit.elexis.hin.sign.core.test;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.google.gson.Gson;

import at.medevit.elexis.emediplan.core.EMediplanUtil;
import at.medevit.elexis.hin.sign.core.internal.CliProcessTest;
import at.medevit.elexis.hin.sign.core.internal.HinSignServiceTest;

/**
 * User interaction and running HIN client required for tests. Automatically
 * running on build is not possible.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ CliProcessTest.class, HinSignServiceTest.class })
public class AllPluginTests {

	private static Gson gson = new Gson();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> getChmedWithNewId(String chmed) {
		if (StringUtils.isNotBlank(chmed)) {
			String decodedChmed = EMediplanUtil.getDecodedJsonString(chmed);
			Map chmedMap = gson.fromJson(decodedChmed, Map.class);
			String newId = UUID.randomUUID().toString();
			chmedMap.put("Id", newId);
			String encodeChmed = EMediplanUtil.getEncodedJson(gson.toJson(chmedMap));
			return Map.of("id", newId, "chmed", encodeChmed);
		}
		return Collections.emptyMap();
	}
}
