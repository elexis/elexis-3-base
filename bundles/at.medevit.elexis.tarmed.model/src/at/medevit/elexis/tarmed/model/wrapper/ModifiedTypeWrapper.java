package at.medevit.elexis.tarmed.model.wrapper;

/**
 * Wrapper Object providing transparent access for different
 * (http://www.forum-datenaustausch.ch/invoice) versions.
 *
 * @author thomas
 *
 */
public class ModifiedTypeWrapper {

	private ch.fd.invoice400.response.ModifiedType modified;

	public ModifiedTypeWrapper(ch.fd.invoice400.response.ModifiedType modified) {
		this.modified = modified;
	}

}
