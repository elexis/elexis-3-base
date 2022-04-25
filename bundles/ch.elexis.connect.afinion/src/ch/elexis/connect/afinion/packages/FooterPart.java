package ch.elexis.connect.afinion.packages;

public class FooterPart extends AbstractPart {

	public FooterPart(final String content) {
		parse(content);
	}

	public void parse(final String content) {
		// Nothing to parse
	}

	@Override
	public int length() {
		return 28;
	}
}
