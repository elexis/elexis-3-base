package ch.elexis.omnivore.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.ole.win32.FORMATETC;
import org.eclipse.swt.internal.ole.win32.IDataObject;
import org.eclipse.swt.internal.ole.win32.IStream;
import org.eclipse.swt.internal.ole.win32.STGMEDIUM;
import org.eclipse.swt.internal.win32.OS;

public class OutlookTransfer extends ByteArrayTransfer {
	private static int BYTES_COUNT = 592;
	private static int SKIP_BYTES = 72;
	
	private final String[] typeNames = new String[] {
		"FileGroupDescriptorW", "FileContents"
	};
	private final int[] typeIds = new int[] {
		registerType(typeNames[0]), registerType(typeNames[1])
	};
	
	private static OutlookTransfer instance = new OutlookTransfer();
	
	private OutlookTransfer(){}
	
	public static OutlookTransfer getInstance(){
		return instance;
	}
	
	@Override
	protected int[] getTypeIds(){
		return typeIds;
	}
	
	@Override
	protected String[] getTypeNames(){
		return typeNames;
	}
	
	@SuppressWarnings("restriction")
	@Override
	protected Object nativeToJava(TransferData transferData){
		String[] result = null;
		
		if (!isSupportedType(transferData) || transferData.pIDataObject == 0)
			return null;
		
		IDataObject data = new IDataObject(transferData.pIDataObject);
		data.AddRef();
		// Check for descriptor format type
		try {
			FORMATETC formatetcFD = transferData.formatetc;
			STGMEDIUM stgmediumFD = new STGMEDIUM();
			stgmediumFD.tymed = COM.TYMED_HGLOBAL;
			transferData.result = data.GetData(formatetcFD, stgmediumFD);
			
			if (transferData.result == COM.S_OK) {
				// Check for contents format type
				int hMem = stgmediumFD.unionField;
				int fileDiscriptorPtr = OS.GlobalLock(hMem);
				int[] fileCount = new int[1];
				try {
					OS.MoveMemory(fileCount, fileDiscriptorPtr, 4);
					fileDiscriptorPtr += 4;
					result = new String[fileCount[0]];
					for (int i = 0; i < fileCount[0]; i++) {
						String fileName = handleFile(fileDiscriptorPtr, data);
						System.out.println("FileName : = " + fileName);
						result[i] = fileName;
						fileDiscriptorPtr += BYTES_COUNT;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					OS.GlobalFree(hMem);
				}
			}
		} finally {
			data.Release();
		}
		return result;
	}
	
	@SuppressWarnings("restriction")
	private String handleFile(long fileDiscriptorPtr, IDataObject data) throws Exception{
		
		// GetFileName
		char[] fileNameChars = new char[OS.MAX_PATH];
		byte[] fileNameBytes = new byte[OS.MAX_PATH];
		COM.MoveMemory(fileNameBytes, (int) fileDiscriptorPtr, BYTES_COUNT);
		
		// Skip some bytes.
		fileNameBytes = Arrays.copyOfRange(fileNameBytes, SKIP_BYTES, fileNameBytes.length);
		String fileNameIncludingTrailingNulls = new String(fileNameBytes, "UTF-16LE");
		fileNameChars = fileNameIncludingTrailingNulls.toCharArray();
		StringBuilder builder = new StringBuilder(OS.MAX_PATH);
		for (int i = 0; fileNameChars[i] != 0 && i < fileNameChars.length; i++) {
			builder.append(fileNameChars[i]);
		}
		String name = builder.toString();
		
		try {
			File file = saveFileContent(name, data);
			if (file != null) {
				System.out.println("File Saved @ " + file.getAbsolutePath());
				;
			}
		} catch (IOException e) {
			System.out.println("Count not save file content");
			;
		}
		
		return name;
	}
	
	@SuppressWarnings("restriction")
	private File saveFileContent(String fileName, IDataObject data) throws IOException{
		File file = null;
		FORMATETC formatetc = new FORMATETC();
		formatetc.cfFormat = typeIds[1];
		formatetc.dwAspect = COM.DVASPECT_CONTENT;
		formatetc.lindex = 0;
		formatetc.tymed = 4; // content.
		
		STGMEDIUM stgmedium = new STGMEDIUM();
		stgmedium.tymed = 4;
		
		if (data.GetData(formatetc, stgmedium) == COM.S_OK) {
			file = new File(fileName);
			IStream iStream = new IStream(stgmedium.unionField);
			iStream.AddRef();
			
			try (FileOutputStream outputStream = new FileOutputStream(file)) {
				
				int increment = 1024 * 4;
				int pv = COM.CoTaskMemAlloc(increment);
				int[] pcbWritten = new int[1];
				while (iStream.Read(pv, increment, pcbWritten) == COM.S_OK && pcbWritten[0] > 0) {
					byte[] buffer = new byte[pcbWritten[0]];
					OS.MoveMemory(buffer, pv, pcbWritten[0]);
					outputStream.write(buffer);
				}
				COM.CoTaskMemFree(pv);
				
			} finally {
				iStream.Release();
			}
			return file;
		} else {
			return null;
		}
	}
	
}
