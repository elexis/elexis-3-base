package ch.elexis.connect.afinion.packages;

public abstract class AbstractPart {
	
	public abstract int length();
	
	private int getNextVal(byte b){
		if (b < 0) {
			return 256 + b;
		}
		return b;
	}
	
	protected int getInteger(final byte[] bytes, int pos){
		if (pos > bytes.length) {
			throw new ArrayIndexOutOfBoundsException("Pos > byte.length");
		}
		if (pos + 4 > bytes.length) {
			throw new ArrayIndexOutOfBoundsException("Pos + 4 > byte.length");
		}
		int index = pos + 3;
		int value = getNextVal(bytes[index--]);
		value <<= 8;
		value += getNextVal(bytes[index--]);
		value <<= 8;
		value += getNextVal(bytes[index--]);
		value <<= 8;
		value += getNextVal(bytes[index]);
		return value;
	}
	
	protected float getFloat(final byte[] bytes, int pos){
		float value = Float.intBitsToFloat(getInteger(bytes, pos));
		float v = Math.round(value * 100);
		return v / 100;
	}
	
	protected String getString(final byte[] bytes, int pos, int length){
		if (pos > bytes.length) {
			throw new ArrayIndexOutOfBoundsException("Pos > byte.length");
		}
		if (pos + length > bytes.length) {
			throw new ArrayIndexOutOfBoundsException("Pos + length > byte.length");
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = pos; i < pos + length; i++) {
			if (bytes[i] != 0) {
				buffer.append((char) bytes[i]);
			}
		}
		return buffer.toString();
	}
}
