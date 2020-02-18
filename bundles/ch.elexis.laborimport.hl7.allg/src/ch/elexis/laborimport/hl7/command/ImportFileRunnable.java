package ch.elexis.laborimport.hl7.command;

import ch.elexis.core.importer.div.importers.DefaultPersistenceHandler;
import ch.elexis.core.importer.div.importers.HL7Parser;
import ch.elexis.core.importer.div.importers.multifile.MultiFileParser;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.ui.importer.div.importers.DefaultHL7Parser;
import ch.elexis.core.ui.importer.div.importers.multifile.strategy.DefaultImportStrategyFactory;
import ch.elexis.laborimport.hl7.universal.LinkLabContactResolver;
import ch.rgw.tools.Result;

public class ImportFileRunnable implements Runnable {
	
	private IVirtualFilesystemHandle vfsFile;
	private Result<?> result;
	private MultiFileParser mfParser;
	private HL7Parser hl7parser;
	
	public ImportFileRunnable(IVirtualFilesystemHandle vfsFile, final String myLab){
		this.vfsFile = vfsFile;
		this.mfParser = new MultiFileParser(myLab);
		this.hl7parser = new DefaultHL7Parser(myLab);
	}
	
	public Result<?> getResult(){
		return result;
	}
	
	@Override
	public void run(){
		result = mfParser.importFromHandle(
			vfsFile, new DefaultImportStrategyFactory().setMoveAfterImport(true)
				.setLabContactResolver(new LinkLabContactResolver()),
			hl7parser, new DefaultPersistenceHandler());
	}
	
}
