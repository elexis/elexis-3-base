package at.medevit.elexis.documents.converter;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import ch.elexis.core.documents.DocumentStore;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IDocumentConverter;
import io.swagger.client.ApiException;
import io.swagger.client.api.ConverterControllerApi;

@Component
public class JodRestDocumentConverter implements IDocumentConverter {
	
	@Reference
	private DocumentStore documentStore;
	
	@Reference
	private IConfigService configService;
	
	@Override
	public Optional<File> convertToPdf(IDocument document){
		ConverterControllerApi apiInstance = new ConverterControllerApi();
		apiInstance.getApiClient().setBasePath(getAppBasePath());
		try {
			String tempFilePath = documentStore.saveContentToTempFile(document, getPrefix(document),
				getExtension(document), true);
			if (tempFilePath != null) {
				File tempFile = new File(tempFilePath);
				File converted = apiInstance.convertToUsingParamUsingPOST(tempFile, "pdf", null);
				tempFile.delete();
				Path moved = Files.move(converted.toPath(),
					new File(tempFile.getParentFile(), getPrefix(document) + ".pdf").toPath(),
					StandardCopyOption.REPLACE_EXISTING);
				File ret = moved.toFile();
				ret.deleteOnExit();
				return Optional.of(ret);
			}
		} catch (ElexisException | ApiException | IOException e) {
			if (e instanceof ApiException) {
				LoggerFactory.getLogger(getClass())
					.error("Error rest api response code [" + ((ApiException) e).getCode() + "]");
			}
			LoggerFactory.getLogger(getClass())
				.error("Error converting document [" + document + "]", e);
		}
		return Optional.empty();
	}
	
	private static String getExtension(IDocument document){
		String extension = document.getExtension();
		if (extension.indexOf('.') != -1) {
			extension = extension.substring(extension.lastIndexOf('.') + 1);
		}
		return extension;
	}
	
	private static String getPrefix(IDocument iDocument){
		StringBuilder ret = new StringBuilder();
		ret.append(iDocument.getPatient().getCode()).append("_");
		
		ret.append(iDocument.getPatient().getLastName()).append(" ");
		ret.append(iDocument.getPatient().getFirstName()).append("_");
		String title = iDocument.getTitle();
		if (iDocument.getExtension() != null && title.endsWith(iDocument.getExtension())) {
			title = title.substring(0, title.lastIndexOf('.'));
		}
		ret.append(title).append("_");
		ret.append(new SimpleDateFormat("ddMMyyyy_HHmmss").format(iDocument.getLastchanged()));
		
		return ret.toString().replaceAll("[^a-züäöA-ZÜÄÖ0-9 _\\.\\-]", "");
	}
	
	private String getAppBasePath(){
		return configService.get("jodrestconverter/basepath", "");
	}
	
	@Override
	public boolean isAvailable(){
		String basePath = getAppBasePath();
		if(StringUtils.isNotEmpty(basePath)) {
			try {
				URI uri = new URI(basePath);
				int port = uri.getPort();
				if (port == -1) {
					if (basePath.toLowerCase().contains("https")) {
						port = 443;
					} else {
						port = 80;
					}
				}
				return isServiceAvailable(uri.getHost(), port, 500);
			} catch (URISyntaxException e) {
				LoggerFactory.getLogger(getClass()).warn("Invalid basePath URI syntax [" + basePath + "]");
			}
		}
		return false;
	}
	
	private boolean isServiceAvailable(String serverHost, int serverPort, Integer timeoutms){
		if (serverHost != null) {
			try {
				SocketAddress endpoint = new InetSocketAddress(serverHost, serverPort);
				Socket socket = new Socket();
				socket.connect(endpoint, timeoutms);
				socket.close();
				return true;
			} catch (NumberFormatException | IOException e) {
				return false;
			}
		}
		return false;
	}
}
