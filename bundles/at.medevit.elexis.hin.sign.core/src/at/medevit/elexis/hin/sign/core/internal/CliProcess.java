package at.medevit.elexis.hin.sign.core.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.medevit.elexis.hin.sign.core.IHinSignService.Mode;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.core.utils.CoreUtil.OS;

public class CliProcess {

	private static Logger logger = LoggerFactory.getLogger(CliProcess.class);

	private List<String> command;

	private List<String> output;

	private Mode mode;

	private CliProcess() {
		command = new ArrayList<>();
	}

	public boolean execute() {
		try {
			logger.info("Executing cli command [" + command + "]");
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.environment().put("ENABLE_EPRESCRIPTION", "true");

			Process process = processBuilder.start();

			if (process.waitFor( 30, TimeUnit.SECONDS)) {
				output = readOutput(process.getInputStream());
				return process.exitValue() == 0;
			} else {
				logger.error("Error executing print command [" + command + "] process terminated.");
				process.destroy();
				return false;
			}
		} catch (IOException | InterruptedException e) {
			logger.error("Error executing print command", e);
			return false;
		}
	}

	public List<String> getOutput() {
		return output;
	}

	public Map<?, ?> getOutputAsMap() {
		if (output != null && !output.isEmpty() && output.get(0).startsWith("{")) {
			Gson gson = new GsonBuilder().create();
			return gson.fromJson(output.stream().collect(Collectors.joining("\n")), Map.class);
		}
		return null;
	}

	private List<String> readOutput(InputStream inputStream) {
		List<String> ret = new ArrayList<>();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = in.readLine()) != null) {
				ret.add(line);
			}
		} catch (IOException e) {
			logger.error("Error reading process output", e);
		}
		return ret;
	}

	private String getApiParamter() {
		return "https://api.testnet.certifaction.io";
	}

	private String getHinApiParamter() {
		return "https://oauth2.sign-test.hin.ch/api";
	}

	private static Optional<File> getCliLocation() {
		Bundle fragment = null;
		if (CoreUtil.getOperatingSystemType() == OS.WINDOWS) {
			fragment = Platform.getBundle("at.medevit.elexis.hin.sign.cli.win");
		}
		if (fragment != null) {
			Optional<File> bundleLocation = FileLocator.getBundleFileLocation(fragment);
			if (bundleLocation.isPresent()) {
				File cliDirectory = new File(bundleLocation.get(), "cli");
				if (cliDirectory.exists() && cliDirectory.isDirectory()) {
					return Optional.of(cliDirectory);
				}
			}
		}
		return Optional.empty();
	}

	private static String getExecutableName() {
		File cliDirectory = getCliLocation().get();
		File[] certifactionFiles = cliDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("certifaction");
			}
		});
		return getFilePath(certifactionFiles[0]);
	}

	private static String getFilePath(File file) {
		String path = file.getAbsolutePath();
		if (CoreUtil.isWindows()) {
			path = path.replace("\\", "\\\\");
		}
		return path;
	}

	public static boolean isCliAvailable() {
		return getCliLocation().isPresent();
	}

	public static CliProcess createPrescription(String epdHandle, String chmed, Mode mode) {
		if (isCliAvailable()) {
			try {
				Path tmpFile = Files.createTempFile("eprescription", ".tmp");
				Files.writeString(tmpFile, chmed);
				CliProcess ret = new CliProcess();
				ret.command.add(getExecutableName());
				ret.command.add("eprescription");
				ret.command.add("create");
				ret.command.add("--api");
				ret.command.add(ret.getApiParamter());
				ret.command.add("--hin-api");
				ret.command.add(ret.getHinApiParamter());
				ret.command.add("--token");
				ret.command.add(epdHandle);
				ret.command.add("-o");
				ret.command.add("'-'");
				ret.command.add("-f");
				ret.command.add("data");
				ret.command.add(getFilePath(tmpFile.toFile()));
				ret.setMode(mode);
				return ret;
			} catch (Exception e) {
				logger.error("Error creating chmed temp file", e);
				throw new IllegalStateException("Error creating chmed temp file");
			}
		}
		throw new IllegalStateException("No CLI available");
	}

	public static CliProcess verifyPrescription(String epdHandle, String chmed, Mode mode) {
		if (isCliAvailable()) {
			try {
				Path tmpFile = Files.createTempFile("eprescription", ".tmp");
				Files.writeString(tmpFile, chmed);
				CliProcess ret = new CliProcess();
				ret.command.add(getExecutableName());
				ret.command.add("eprescription");
				ret.command.add("verify");
				ret.command.add("--api");
				ret.command.add(ret.getApiParamter());
				ret.command.add("--hin-api");
				ret.command.add(ret.getHinApiParamter());
				ret.command.add("--token");
				ret.command.add(epdHandle);
				ret.command.add(getFilePath(tmpFile.toFile()));
				ret.setMode(mode);
				return ret;
			} catch (Exception e) {
				logger.error("Error creating chmed temp file", e);
				throw new IllegalStateException("Error creating chmed temp file");
			}
		}
		throw new IllegalStateException("No CLI available");
	}

	public static CliProcess revokePrescription(String epdHandle, String chmed, Mode mode) {
		if (isCliAvailable()) {
			try {
				Path tmpFile = Files.createTempFile("eprescription", ".tmp");
				Files.writeString(tmpFile, chmed);
				CliProcess ret = new CliProcess();
				ret.command.add(getExecutableName());
				ret.command.add("eprescription");
				ret.command.add("revoke");
				ret.command.add("--api");
				ret.command.add(ret.getApiParamter());
				ret.command.add("--hin-api");
				ret.command.add(ret.getHinApiParamter());
				ret.command.add("--token");
				ret.command.add(epdHandle);
				ret.command.add(getFilePath(tmpFile.toFile()));
				ret.setMode(mode);
				return ret;
			} catch (Exception e) {
				logger.error("Error creating chmed temp file", e);
				throw new IllegalStateException("Error creating chmed temp file");
			}
		}
		throw new IllegalStateException("No CLI available");
	}

	private void setMode(Mode mode) {
		this.mode = mode;
	}
}
