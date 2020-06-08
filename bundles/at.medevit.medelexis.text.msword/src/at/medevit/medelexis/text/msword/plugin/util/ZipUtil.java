/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	static final int BUFFER_SIZE = 1000 * 1024;

	@SuppressWarnings("unchecked")
	public static void unzipToDirectory(File sourceFile, File directory){
		try {
			BufferedOutputStream dest = null;
			BufferedInputStream is = null;
			ZipEntry entry;
			ZipFile zipfile = new ZipFile(sourceFile);
			
			// create files
			Enumeration<ZipEntry> zipEntryEnum = (Enumeration<ZipEntry>) zipfile.entries();
			while (zipEntryEnum.hasMoreElements()) {
				entry = (ZipEntry) zipEntryEnum.nextElement();
				
				// create an absolute path for the entry
				String entryName = directory.getAbsolutePath() + File.separator + entry.getName();
				entryName = entryName.replace('/', File.separatorChar);
				entryName = entryName.replace('\\', File.separatorChar);
				
				// check if we need to create directories first
				String entryDir = entryName.substring(0, entryName.lastIndexOf(File.separatorChar));
				File dir = new File(entryDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}

				// write unzipped file
				int count;
				byte data[] = new byte[BUFFER_SIZE];
				is = new BufferedInputStream(zipfile.getInputStream(entry));
				FileOutputStream fos = new FileOutputStream(entryName);
				dest = new BufferedOutputStream(fos, BUFFER_SIZE);
				while ((count = is.read(data, 0, BUFFER_SIZE)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
				is.close();

			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private static void zipAddDirectory(ZipOutputStream zout, File fileSource,
		String directoryName){
		// get sub-folder/files list
		File[] files = fileSource.listFiles();
		
		for (int i = 0; i < files.length; i++) {
			// add sub directories
			if (files[i].isDirectory()) {
				String subDirectoryName = directoryName.isEmpty() ? files[i].getName() : directoryName + File.separator + files[i].getName();
				zipAddDirectory(zout, files[i], subDirectoryName);
				continue;
			}
			
			// add files
			try {
				String entryName =
					directoryName.isEmpty() ? files[i].getName() : directoryName + File.separator
						+ files[i].getName();
				
				byte[] buffer = new byte[BUFFER_SIZE];
				FileInputStream fin = new FileInputStream(files[i]);
				// write the file to the zip
				zout.putNextEntry(new ZipEntry(entryName));
				int length;
				while ((length = fin.read(buffer)) > 0) {
					zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
				
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public static void zipDirectory(File sourceDirectory, FileOutputStream fout){
		ZipOutputStream zout = null;
		try {
			zout = new ZipOutputStream(fout);
			
			// start adding the directory to the zip
			zipAddDirectory(zout, sourceDirectory, ""); //$NON-NLS-1$
		} finally {
			if (zout != null) {
				try {
					zout.close();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}
	
	public static void copyFile(File srcFile, File destFile) throws IOException{
		if (destFile.exists() && destFile.isDirectory()) {
			throw new IOException("Destination '" + destFile + "' exists but is a directory"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		try (FileInputStream fis = new FileInputStream(srcFile);
				FileOutputStream fos = new FileOutputStream(destFile);
				FileChannel input = fis.getChannel();
				FileChannel output = fos.getChannel()) {
			long size = input.size();
			long pos = 0;
			long count = 0;
			while (pos < size) {
				count = (size - pos) > BUFFER_SIZE ? BUFFER_SIZE : (size - pos);
				pos += output.transferFrom(input, pos, count);
			}
		}
		
		if (srcFile.length() != destFile.length()) {
			throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" //$NON-NLS-1$ //$NON-NLS-2$
				+ destFile + "'"); //$NON-NLS-1$
		}
	}

	public static boolean deleteRecursive(File path){
		if (!path.exists())
			throw new IllegalArgumentException("Path [" + path.getAbsolutePath() //$NON-NLS-1$
				+ "] does not exist"); //$NON-NLS-1$
		boolean ret = true;
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				ret = ret && ZipUtil.deleteRecursive(f);
			}
		}
		return ret && path.delete();
	}
}
