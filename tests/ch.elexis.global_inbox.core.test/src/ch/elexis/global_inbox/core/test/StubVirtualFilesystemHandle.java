package ch.elexis.global_inbox.core.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemhandleFilter;

public class StubVirtualFilesystemHandle implements IVirtualFilesystemHandle {

	private String name;
	private IVirtualFilesystemHandle parent;
	private boolean isDir;

	public StubVirtualFilesystemHandle(String name, IVirtualFilesystemHandle parent) {
		this(name, parent, false);
	}

	public StubVirtualFilesystemHandle(String name, IVirtualFilesystemHandle parent, boolean isDir) {
		this.name = name;
		this.parent = parent;
		this.isDir = isDir;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IVirtualFilesystemHandle getParent() {
		return parent;
	}

	@Override
	public boolean isDirectory() {
		return isDir;
	}

	@Override
	public String getAbsolutePath() {
		if (parent != null) {
			return parent.getAbsolutePath() + "/" + name;
		}
		return "/" + name;
	}

	@Override
	public boolean exists() throws IOException {
		return true;
	}

	@Override
	public void delete() throws IOException {
	}

	@Override
	public IVirtualFilesystemHandle mkdirs() throws IOException {
		this.isDir = true;
		return this;
	}

	@Override
	public IVirtualFilesystemHandle mkdir() throws IOException {
		this.isDir = true;
		return this;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return null;
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return null;
	}

	@Override
	public byte[] readAllBytes() throws IOException {
		return new byte[0];
	}

	@Override
	public void writeAllBytes(byte[] content) throws IOException {
	}

	@Override
	public long getContentLenght() throws IOException {
		return 0;
	}

	@Override
	public IVirtualFilesystemHandle copyTo(IVirtualFilesystemHandle destination) throws IOException {
		return destination;
	}

	@Override
	public IVirtualFilesystemHandle[] listHandles(IVirtualFilesystemhandleFilter ff) throws IOException {
		return null;
	}

	@Override
	public IVirtualFilesystemHandle[] listHandles() throws IOException {
		return null;
	}

	@Override
	public boolean isDirectoryUrl() throws IOException {
		return isDir;
	}

	@Override
	public URL toURL() {
		return null;
	}

	@Override
	public URI getURI() {
		return null;
	}

	@Override
	public Optional<File> toFile() {
		return Optional.empty();
	}

	@Override
	public String getExtension() {
		return StringUtils.EMPTY;
	}

	@Override
	public boolean canRead() {
		return true;
	}

	@Override
	public boolean canWrite() {
		return true;
	}

	@Override
	public IVirtualFilesystemHandle moveTo(IVirtualFilesystemHandle target) throws IOException {
		return target;
	}

	@Override
	public IVirtualFilesystemHandle subDir(String string) throws IOException {
		return null;
	}

	@Override
	public IVirtualFilesystemHandle subFile(String name) throws IOException {
		return null;
	}
}