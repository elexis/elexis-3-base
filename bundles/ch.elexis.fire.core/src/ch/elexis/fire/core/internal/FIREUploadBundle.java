package ch.elexis.fire.core.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;

public class FIREUploadBundle implements Supplier<Boolean> {

	private static final String UPLOAD_URL = "https://fire.ihamz.uzh.ch";

	private File file;

	public FIREUploadBundle(File file) {
		this.file = file;
	}

	@Override
	public Boolean get() {
		File tempBundleFile = null;
		try {
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(UPLOAD_URL + "/fire/index.php");

			httppost.setHeader(HttpHeaders.AUTHORIZATION, getAuth());

			FileBody bundleBody = new FileBody(file, ContentType.APPLICATION_JSON);

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setCharset(StandardCharsets.UTF_8);
			builder.addPart("files[]", bundleBody);

			HttpEntity uploadEntity = builder.build();

			httppost.setEntity(uploadEntity);
			// Execute and get the response.
			HttpResponse response = httpclient.execute(httppost);
			LoggerFactory.getLogger(getClass()).info("Got response code [" + response.getStatusLine().getStatusCode()
					+ "] from [" + httppost.getURI().toString() + "]");
			if (response.getStatusLine().getStatusCode() == 200) {
				LoggerFactory.getLogger(getClass())
						.info("Bundle [" + file.getName() + "] uploaded successful");
				return Boolean.TRUE;
			} else {
				LoggerFactory.getLogger(getClass()).warn("Uploading bundle [" + file.getName() + "] failed");
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Exception uploading bundle", e);
		} finally {
			if (tempBundleFile != null) {
				tempBundleFile.delete();
			}
		}
		return Boolean.FALSE;
	}

	private String getAuth() {
		InputStream rsc = getClass().getResourceAsStream("/rsc/upload");
		if (rsc != null) {
			try {
				String upload = IOUtils.toString(rsc, "UTF-8");
				String[] parts = upload.split(",");
				if (parts.length == 2) {
					String credentials = parts[0] + ":" + parts[1];
					return "Basic " + new String(Base64.encodeBase64(credentials.getBytes()));
				}
			} catch (IOException e) {
				LoggerFactory.getLogger(getClass()).error("Exception reading upload", e);
			}
		}
		LoggerFactory.getLogger(getClass()).warn("No auth found");
		return StringUtils.EMPTY;
	}
}
