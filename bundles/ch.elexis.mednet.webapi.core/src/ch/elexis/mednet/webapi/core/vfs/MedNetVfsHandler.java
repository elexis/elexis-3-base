package ch.elexis.mednet.webapi.core.vfs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;
import ch.elexis.mednet.webapi.core.config.MedNetConfig;

/**
 * Central handler for all file operations in MedNet using the Elexis Virtual
 * Filesystem. Supports local files, SMB (Windows Shares), and WebDAV
 * seamlessly.
 */
public class MedNetVfsHandler {

	private static final Logger log = LoggerFactory.getLogger(MedNetVfsHandler.class);

	/**
	 * Retrieves the download directory handle. Creates the directory if it does not
	 * exist.
	 *
	 * @return the virtual filesystem handle for the configured download directory,
	 *         or null if not configured.
	 * @throws IOException if the path is invalid or cannot be created.
	 */
	public static IVirtualFilesystemHandle getDownloadDirectory() throws IOException {
		String path = MedNetConfig.load().getDownloadPath();
		if (StringUtils.isBlank(path)) {
			log.warn("Download path is not configured.");
			return null;
		}

		IVirtualFilesystemService vfs = VirtualFilesystemServiceHolder.get();
		IVirtualFilesystemHandle dirHandle = vfs.of(path, true);

		if (!dirHandle.exists()) {
			dirHandle.mkdirs();
			log.info("Created missing download directory at: [{}]", path);
		} else if (!dirHandle.isDirectory()) {
			throw new IOException("Configured path is a file, not a directory: " + path);
		}

		return dirHandle;
	}

	/**
	 * Recursively lists all files in the given directory handle.
	 *
	 * @param dir the directory handle to search in
	 * @return a list of file handles
	 */
	public static List<IVirtualFilesystemHandle> listFilesRecursively(IVirtualFilesystemHandle dir) {
		List<IVirtualFilesystemHandle> fileList = new ArrayList<>();
		try {
			if (dir == null || !dir.isDirectory()) {
				return fileList;
			}

			IVirtualFilesystemHandle[] children = dir.listHandles();
			if (children != null) {
				for (IVirtualFilesystemHandle child : children) {
					if (child.getName().startsWith(".")) {
						continue; // Skip hidden/system files
					}
					if (child.isDirectory()) {
						fileList.addAll(listFilesRecursively(child));
					} else {
						fileList.add(child);
					}
				}
			}
		} catch (IOException exception) {
			log.error("Failed to list files in directory: [{}]", dir.getName(), exception);
		}
		return fileList;
	}

	/**
	 * Checks if a file is currently locked or opened by another process. Falls back
	 * to standard write-checks for network shares if direct locking is unavailable.
	 *
	 * @param handle the virtual file handle
	 * @return true if the file is locked, false if it is free
	 */
	public static boolean isFileLocked(IVirtualFilesystemHandle handle) {
		Optional<File> localFile = handle.toFile();
		if (localFile.isPresent()) {
			File file = localFile.get();
			try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
					FileChannel channel = randomAccessFile.getChannel();
					FileLock lock = channel.lock()) {
				return false;
			} catch (OverlappingFileLockException exception) {
				log.debug("File [{}] is locked by an overlapping file lock.", file.getName(), exception);
				return true;
			} catch (IOException exception) {
				log.debug("I/O error while checking lock for file [{}]. Assuming it is opened.", file.getName(),
						exception);
				return true;
			}
		} else {
			// For pure network shares (SMB/DAV), if we can't write, we assume it's
			// locked/in use
			return !handle.canWrite();
		}
	}
}