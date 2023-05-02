package ch.elexis.omnivore.ui.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.program.Program;
import org.slf4j.LoggerFactory;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.omnivore.data.Messages;
import ch.elexis.omnivore.data.Preferences;
import ch.elexis.omnivore.data.Utils;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.model.util.CategoryUtil;
import ch.elexis.omnivore.ui.service.OmnivoreModelServiceHolder;
import ch.elexis.omnivore.ui.views.FileImportDialog;
import ch.rgw.tools.ExHandler;

public class UiUtils {

	public static void open(IDocumentHandle handle) {
		try {
			String ext = StringConstants.SPACE; // StringUtils.EMPTY;
			File temp = Utils.createTemporaryFile(handle, handle.getTitle());

			Program proggie = Program.findProgram(ext);
			if (proggie != null) {
				proggie.execute(temp.getAbsolutePath());
			} else {
				if (Program.launch(temp.getAbsolutePath()) == false) {
					Runtime.getRuntime().exec(temp.getAbsolutePath());
				}

			}

		} catch (Exception ex) {
			LoggerFactory.getLogger(UiUtils.class).error("Error on omnivore open", ex); //$NON-NLS-1$
			SWTHelper.showError(ch.elexis.omnivore.ui.Messages.DocHandle_runErrorHeading, ex.getMessage());
		}
	}

	public static IDocumentHandle assimilate(String f, String selectedCategory) {
		IPatient act = ContextServiceHolder.get().getActivePatient().orElse(null);
		if (act == null) {
			SWTHelper.showError(Messages.DocHandle_noPatientSelected, Messages.DocHandle_pleaseSelectPatient);
			return null;
		}
		IVirtualFilesystemHandle file;
		try {
			file = VirtualFilesystemServiceHolder.get().of(f);
			if (!file.canRead()) {
				SWTHelper.showError(Messages.DocHandle_cantReadCaption,
						String.format(Messages.DocHandle_cantReadMessage, f));
				return null;
			}
			// can't import complete directory
			if (file.isDirectory()) {
				SWTHelper.showError(Messages.DocHandle_importErrorDirectory,
						Messages.DocHandle_importErrorDirectoryText);
				return null;
			}
		} catch (IOException e) {
			SWTHelper.showError(Messages.DocHandle_importErrorDirectory, e.getMessage());
			return null;
		}

		Integer maxOmnivoreFilenameLength = Preferences.getOmnivoreMax_Filename_Length();

		String nam = file.getName();
		if (nam.length() > maxOmnivoreFilenameLength) {
			SWTHelper.showError(Messages.DocHandle_importErrorCaption,
					MessageFormat.format(Messages.DocHandle_importErrorMessage, maxOmnivoreFilenameLength));
			return null;
		}

		FileImportDialog fid;
		if (selectedCategory == null) {
			fid = new FileImportDialog(file.getName());
		} else {
			fid = new FileImportDialog(file.getName(), selectedCategory);
		}

		IDocumentHandle docHandle = null;
		if (fid.open() == Dialog.OK) {
			try (InputStream bis = file.openInputStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				IOUtils.copy(bis, baos);

				String fileName = file.getName();
				if (fileName.length() > 255) {
					SWTHelper.showError(Messages.DocHandle_readErrorCaption, Messages.DocHandle_fileNameTooLong);
					return null;
				}
				String category = fid.category;
				if (category == null || category.length() == 0) {
					category = CategoryUtil.getDefaultCategory().getName();
				}
				docHandle = createDocHandle(category, baos.toByteArray(), act, fid.title.trim(), file.getName(),
						fid.keywords.trim());
				docHandle.setLastchanged(fid.saveDate);
				if (Preferences.getDateModifiable()) {
					docHandle.setCreated(fid.originDate);
				}
				OmnivoreModelServiceHolder.get().save(docHandle);

			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.showError(Messages.DocHandle_importErrorCaption, Messages.DocHandle_importErrorMessage2);
				return null;
			}
			Utils.archiveFile(file, docHandle);
		}
		return docHandle;
	}

	public static IDocumentHandle assimilate(String f) {
		IPatient act = ContextServiceHolder.get().getActivePatient().orElse(null);
		if (act == null) {
			SWTHelper.showError(Messages.DocHandle_noPatientSelected, Messages.DocHandle_pleaseSelectPatient);
			return null;
		}
		File file = new File(f);
		if (!file.canRead()) {
			SWTHelper.showError(Messages.DocHandle_cantReadCaption,
					MessageFormat.format(Messages.DocHandle_cantReadText, f));
			return null;
		}

		// can't import complete directory
		if (file.isDirectory()) {
			SWTHelper.showError(Messages.DocHandle_importErrorDirectory, Messages.DocHandle_importErrorDirectoryText);
			return null;
		}

		FileImportDialog fid = new FileImportDialog(file.getName());
		if (fid.open() == Dialog.OK) {
			try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
				IOUtils.copy(bis, baos);
				String nam = file.getName();
				if (nam.length() > 255) {
					SWTHelper.showError(Messages.DocHandle_readErrorCaption3, Messages.DocHandle_fileNameTooLong);
					return null;
				}
				String category = fid.category;
				if (category == null || category.length() == 0) {
					category = CategoryUtil.getDefaultCategory().getName();
				}
				IDocumentHandle docHandle = createDocHandle(category, baos.toByteArray(), act, fid.originDate,
						fid.title, file.getName(), fid.keywords);
				docHandle.setLastchanged(fid.saveDate);
				if (Preferences.getDateModifiable()) {
					docHandle.setCreated(fid.originDate);
				}
				OmnivoreModelServiceHolder.get().save(docHandle);
				return docHandle;
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.showError(Messages.DocHandle_readErrorCaption3, Messages.DocHandle_readErrorText2);
			}
		}
		return null;
	}

	private static IDocumentHandle createDocHandle(String category, byte[] doc, IPatient pat, Date creationDate,
			String title, String mime, String keyw) {
		IDocumentHandle ret = OmnivoreModelServiceHolder.get().create(IDocumentHandle.class);
		OmnivoreModelServiceHolder.get().setEntityProperty("category", category, ret); //$NON-NLS-1$
		ret.setPatient(pat);
		ret.setCreated(creationDate);
		ret.setTitle(title);
		ret.setMimeType(mime);
		ret.setKeywords(keyw);
		ret.setContent(new ByteArrayInputStream(doc));
		OmnivoreModelServiceHolder.get().save(ret);
		return ret;
	}

	private static IDocumentHandle createDocHandle(String category, byte[] doc, IPatient pat, String title, String mime,
			String keyw) {
		IDocumentHandle ret = OmnivoreModelServiceHolder.get().create(IDocumentHandle.class);
		OmnivoreModelServiceHolder.get().setEntityProperty("category", category, ret); //$NON-NLS-1$
		ret.setPatient(pat);
		ret.setTitle(title);
		ret.setMimeType(mime);
		ret.setKeywords(keyw);
		ret.setContent(new ByteArrayInputStream(doc));
		OmnivoreModelServiceHolder.get().save(ret);
		return ret;
	}

}
