package at.medevit.elexis.ehc.ui.extension;

import java.io.InputStream;

public interface IImportWizard {
	/**
	 * {@link InputStream} from which the document can be read. <b>ATTENTION</b>
	 * dont forget to reset the {@link InputStream} before reading. Parameter
	 * document can be null.
	 *
	 * @param document
	 */
	public void setDocument(InputStream document);
}
