package ch.elexis.pdfBills;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Mode;
import com.google.zxing.qrcode.decoder.Version;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;

/**
 * Encodes one ISO/IEC 18004 Structured Append QR Code symbol as specified for the Swiss
 * e-invoice "XML auf Papier" annex (GeneralInvoiceRequest 5.0, printTemplates_GIReq500_Annex):
 * version 29 (133x133 modules), error correction level M. The symbol carries its payload in a
 * byte mode segment preceded by the 20 bit Structured Append header: mode indicator 0011,
 * 4 bit symbol position (0-based), 4 bit symbol count minus one, 8 bit parity (XOR of all
 * payload bytes of the whole sequence).
 * <p>
 * ZXing 3.5.x can decode Structured Append sequences but not encode them, and its matrix
 * construction classes (MatrixUtil, MaskUtil) are package private. This class therefore builds
 * the symbol matrix itself on top of public ZXing APIs (BitArray, Version, ReedSolomonEncoder).
 * The matrix construction, mask patterns and penalty calculation are ported from ZXing's
 * MatrixUtil and MaskUtil, the bit stream termination, block interleaving and error correction
 * code generation from ZXing's Encoder (https://github.com/zxing/zxing, Apache License 2.0).
 */
final class StructuredAppendQrEncoder {

	private static final int QR_VERSION = 29;
	private static final ErrorCorrectionLevel EC_LEVEL = ErrorCorrectionLevel.M;

	private StructuredAppendQrEncoder() {
	}

	/**
	 * Encode one symbol of a Structured Append sequence.
	 *
	 * @param data     payload bytes of this symbol (a base64 chunk, space padded to the full
	 *                 capacity by the caller)
	 * @param position 0-based position of this symbol in the sequence (0..15)
	 * @param total    number of symbols in the sequence (1..16)
	 * @param parity   XOR of all payload bytes of the whole sequence (0..255)
	 * @return the symbol matrix (133x133 for version 29), 1 = dark module, 0 = light module
	 * @throws WriterException if the sequence parameters are out of range or the data does not fit
	 *                         into the symbol
	 */
	static ByteMatrix encode(byte[] data, int position, int total, int parity) throws WriterException {
		if (position < 0 || position > 15 || total < 1 || total > 16 || position >= total) {
			throw new WriterException("Invalid structured append sequence parameters"); //$NON-NLS-1$
		}
		Version version = Version.getVersionForNumber(QR_VERSION);
		// data bit stream: 20 bit structured append header, then a byte mode segment
		BitArray bits = new BitArray();
		bits.appendBits(Mode.STRUCTURED_APPEND.getBits(), 4);
		bits.appendBits(position, 4);
		bits.appendBits(total - 1, 4);
		bits.appendBits(parity & 0xFF, 8);
		bits.appendBits(Mode.BYTE.getBits(), 4);
		bits.appendBits(data.length, Mode.BYTE.getCharacterCountBits(version));
		for (byte b : data) {
			bits.appendBits(b & 0xFF, 8);
		}
		// terminate and pad to the data capacity
		Version.ECBlocks ecBlocks = version.getECBlocksForLevel(EC_LEVEL);
		int numDataBytes = version.getTotalCodewords() - ecBlocks.getTotalECCodewords();
		terminateBits(numDataBytes, bits);
		// interleave data and error correction codewords
		BitArray finalBits = interleaveWithECBytes(bits, version.getTotalCodewords(), numDataBytes, ecBlocks);
		// choose the mask pattern with the lowest penalty, then build the final matrix
		ByteMatrix matrix = new ByteMatrix(version.getDimensionForVersion(), version.getDimensionForVersion());
		int bestMask = -1;
		int minPenalty = Integer.MAX_VALUE;
		for (int mask = 0; mask < QRCode.NUM_MASK_PATTERNS; mask++) {
			buildMatrix(finalBits, EC_LEVEL, version, mask, matrix);
			int penalty = calculateMaskPenalty(matrix);
			if (penalty < minPenalty) {
				minPenalty = penalty;
				bestMask = mask;
			}
		}
		buildMatrix(finalBits, EC_LEVEL, version, bestMask, matrix);
		return matrix;
	}

	private static void terminateBits(int numDataBytes, BitArray bits) throws WriterException {
		int capacity = numDataBytes * 8;
		if (bits.getSize() > capacity) {
			throw new WriterException("data bits cannot fit in the QR Code"); //$NON-NLS-1$
		}
		for (int i = 0; i < 4 && bits.getSize() < capacity; ++i) {
			bits.appendBit(false);
		}
		int numBitsInLastByte = bits.getSize() & 0x07;
		if (numBitsInLastByte > 0) {
			for (int i = numBitsInLastByte; i < 8; i++) {
				bits.appendBit(false);
			}
		}
		int numPaddingBytes = numDataBytes - bits.getSizeInBytes();
		for (int i = 0; i < numPaddingBytes; ++i) {
			bits.appendBits((i & 0x01) == 0 ? 0xEC : 0x11, 8);
		}
		if (bits.getSize() != capacity) {
			throw new WriterException("Bits size does not equal capacity"); //$NON-NLS-1$
		}
	}

	private static BitArray interleaveWithECBytes(BitArray bits, int numTotalBytes, int numDataBytes,
			Version.ECBlocks ecBlocks) throws WriterException {
		if (bits.getSizeInBytes() != numDataBytes) {
			throw new WriterException("Number of bits and data bytes does not match"); //$NON-NLS-1$
		}
		int numBlocks = ecBlocks.getNumBlocks();
		int ecCodewordsPerBlock = ecBlocks.getECCodewordsPerBlock();
		int[] dataSizes = new int[numBlocks];
		int idx = 0;
		for (Version.ECB ecb : ecBlocks.getECBlocks()) {
			for (int i = 0; i < ecb.getCount(); i++) {
				dataSizes[idx++] = ecb.getDataCodewords();
			}
		}
		int dataBytesOffset = 0;
		int maxNumDataBytes = 0;
		int maxNumEcBytes = 0;
		byte[][] dataBlocks = new byte[numBlocks][];
		byte[][] ecBlocksArr = new byte[numBlocks][];
		for (int i = 0; i < numBlocks; i++) {
			int size = dataSizes[i];
			byte[] dataBytes = new byte[size];
			bits.toBytes(8 * dataBytesOffset, dataBytes, 0, size);
			byte[] ecBytes = generateECBytes(dataBytes, ecCodewordsPerBlock);
			dataBlocks[i] = dataBytes;
			ecBlocksArr[i] = ecBytes;
			maxNumDataBytes = Math.max(maxNumDataBytes, size);
			maxNumEcBytes = Math.max(maxNumEcBytes, ecBytes.length);
			dataBytesOffset += size;
		}
		if (numDataBytes != dataBytesOffset) {
			throw new WriterException("Data bytes does not match offset"); //$NON-NLS-1$
		}
		BitArray result = new BitArray();
		for (int i = 0; i < maxNumDataBytes; i++) {
			for (int b = 0; b < numBlocks; b++) {
				if (i < dataBlocks[b].length) {
					result.appendBits(dataBlocks[b][i], 8);
				}
			}
		}
		for (int i = 0; i < maxNumEcBytes; i++) {
			for (int b = 0; b < numBlocks; b++) {
				if (i < ecBlocksArr[b].length) {
					result.appendBits(ecBlocksArr[b][i], 8);
				}
			}
		}
		if (numTotalBytes != result.getSizeInBytes()) {
			throw new WriterException("Interleaving error"); //$NON-NLS-1$
		}
		return result;
	}

	private static byte[] generateECBytes(byte[] dataBytes, int numEcBytesInBlock) {
		int numDataBytes = dataBytes.length;
		int[] toEncode = new int[numDataBytes + numEcBytesInBlock];
		for (int i = 0; i < numDataBytes; i++) {
			toEncode[i] = dataBytes[i] & 0xFF;
		}
		new ReedSolomonEncoder(GenericGF.QR_CODE_FIELD_256).encode(toEncode, numEcBytesInBlock);
		byte[] ecBytes = new byte[numEcBytesInBlock];
		for (int i = 0; i < numEcBytesInBlock; i++) {
			ecBytes[i] = (byte) toEncode[numDataBytes + i];
		}
		return ecBytes;
	}

	// ==================== matrix construction (ported from ZXing MatrixUtil) ====================

	private static final int[][] POSITION_DETECTION_PATTERN = {
			{1, 1, 1, 1, 1, 1, 1},
			{1, 0, 0, 0, 0, 0, 1},
			{1, 0, 1, 1, 1, 0, 1},
			{1, 0, 1, 1, 1, 0, 1},
			{1, 0, 1, 1, 1, 0, 1},
			{1, 0, 0, 0, 0, 0, 1},
			{1, 1, 1, 1, 1, 1, 1},
	};
	private static final int[][] POSITION_ADJUSTMENT_PATTERN = {
			{1, 1, 1, 1, 1},
			{1, 0, 0, 0, 1},
			{1, 0, 1, 0, 1},
			{1, 0, 0, 0, 1},
			{1, 1, 1, 1, 1},
	};
	private static final int[][] TYPE_INFO_COORDINATES = {
			{8, 0}, {8, 1}, {8, 2}, {8, 3}, {8, 4}, {8, 5}, {8, 7}, {8, 8},
			{7, 8}, {5, 8}, {4, 8}, {3, 8}, {2, 8}, {1, 8}, {0, 8},
	};
	private static final int VERSION_INFO_POLY = 0x1f25;
	private static final int TYPE_INFO_POLY = 0x537;
	private static final int TYPE_INFO_MASK_PATTERN = 0x5412;

	private static void buildMatrix(BitArray dataBits, ErrorCorrectionLevel ecLevel, Version version,
			int maskPattern, ByteMatrix matrix) throws WriterException {
		clearMatrix(matrix);
		embedBasicPatterns(version, matrix);
		embedTypeInfo(ecLevel, maskPattern, matrix);
		maybeEmbedVersionInfo(version, matrix);
		embedDataBits(dataBits, maskPattern, matrix);
	}

	private static void clearMatrix(ByteMatrix matrix) {
		matrix.clear((byte) -1);
	}

	private static boolean isEmpty(int value) {
		return value == -1;
	}

	private static void embedBasicPatterns(Version version, ByteMatrix matrix) {
		int w = matrix.getWidth();
		embedPositionDetectionPattern(matrix, 0, 0);
		embedPositionDetectionPattern(matrix, w - 7, 0);
		embedPositionDetectionPattern(matrix, 0, w - 7);
		for (int x = 0; x < 8; x++) {
			matrix.set(x, 7, 0);            // horizontal separator, top left
			matrix.set(w - 8 + x, 7, 0);    // horizontal separator, top right
			matrix.set(x, w - 8, 0);        // horizontal separator, bottom left
		}
		for (int y = 0; y < 7; y++) {
			matrix.set(7, y, 0);            // vertical separator, top left
			matrix.set(w - 8, y, 0);        // vertical separator, top right
			matrix.set(7, w - 7 + y, 0);    // vertical separator, bottom left
		}
		matrix.set(8, matrix.getHeight() - 8, 1); // dark dot
		int[] centers = version.getAlignmentPatternCenters();
		for (int cy : centers) {
			for (int cx : centers) {
				if (isEmpty(matrix.get(cx, cy))) {
					embedPositionAdjustmentPattern(matrix, cx - 2, cy - 2);
				}
			}
		}
		for (int i = 8; i < w - 8; ++i) {
			int bit = (i + 1) % 2;
			if (isEmpty(matrix.get(i, 6))) {
				matrix.set(i, 6, bit);
			}
			if (isEmpty(matrix.get(6, i))) {
				matrix.set(6, i, bit);
			}
		}
	}

	private static void embedPositionDetectionPattern(ByteMatrix matrix, int xStart, int yStart) {
		for (int y = 0; y < 7; ++y) {
			for (int x = 0; x < 7; ++x) {
				matrix.set(xStart + x, yStart + y, POSITION_DETECTION_PATTERN[y][x]);
			}
		}
	}

	private static void embedPositionAdjustmentPattern(ByteMatrix matrix, int xStart, int yStart) {
		for (int y = 0; y < 5; ++y) {
			for (int x = 0; x < 5; ++x) {
				matrix.set(xStart + x, yStart + y, POSITION_ADJUSTMENT_PATTERN[y][x]);
			}
		}
	}

	private static void embedTypeInfo(ErrorCorrectionLevel ecLevel, int maskPattern, ByteMatrix matrix)
			throws WriterException {
		BitArray typeInfoBits = new BitArray();
		makeTypeInfoBits(ecLevel, maskPattern, typeInfoBits);
		for (int i = 0; i < typeInfoBits.getSize(); ++i) {
			boolean bit = typeInfoBits.get(typeInfoBits.getSize() - 1 - i);
			int[] coordinates = TYPE_INFO_COORDINATES[i];
			matrix.set(coordinates[0], coordinates[1], bit);
			if (i < 8) {
				matrix.set(matrix.getWidth() - i - 1, 8, bit);
			} else {
				matrix.set(8, matrix.getHeight() - 7 + (i - 8), bit);
			}
		}
	}

	private static void maybeEmbedVersionInfo(Version version, ByteMatrix matrix) throws WriterException {
		if (version.getVersionNumber() < 7) {
			return;
		}
		BitArray versionInfoBits = new BitArray();
		makeVersionInfoBits(version, versionInfoBits);
		int bitIndex = 6 * 3 - 1;
		for (int i = 0; i < 6; ++i) {
			for (int j = 0; j < 3; ++j) {
				boolean bit = versionInfoBits.get(bitIndex);
				bitIndex--;
				matrix.set(i, matrix.getHeight() - 11 + j, bit);
				matrix.set(matrix.getHeight() - 11 + j, i, bit);
			}
		}
	}

	private static void embedDataBits(BitArray dataBits, int maskPattern, ByteMatrix matrix) throws WriterException {
		int bitIndex = 0;
		int direction = -1;
		int x = matrix.getWidth() - 1;
		int y = matrix.getHeight() - 1;
		while (x > 0) {
			if (x == 6) {
				x -= 1;
			}
			while (y >= 0 && y < matrix.getHeight()) {
				for (int i = 0; i < 2; ++i) {
					int xx = x - i;
					if (!isEmpty(matrix.get(xx, y))) {
						continue;
					}
					boolean bit;
					if (bitIndex < dataBits.getSize()) {
						bit = dataBits.get(bitIndex);
						++bitIndex;
					} else {
						bit = false;
					}
					if (getDataMaskBit(maskPattern, xx, y)) {
						bit = !bit;
					}
					matrix.set(xx, y, bit);
				}
				y += direction;
			}
			direction = -direction;
			y += direction;
			x -= 2;
		}
		if (bitIndex != dataBits.getSize()) {
			throw new WriterException("Not all bits consumed: " + bitIndex + '/' + dataBits.getSize()); //$NON-NLS-1$
		}
	}

	private static int findMSBSet(int value) {
		return 32 - Integer.numberOfLeadingZeros(value);
	}

	private static int calculateBCHCode(int value, int poly) {
		if (poly == 0) {
			throw new IllegalArgumentException("0 polynomial"); //$NON-NLS-1$
		}
		int msbSetInPoly = findMSBSet(poly);
		value <<= msbSetInPoly - 1;
		while (findMSBSet(value) >= msbSetInPoly) {
			value ^= poly << (findMSBSet(value) - msbSetInPoly);
		}
		return value;
	}

	private static void makeTypeInfoBits(ErrorCorrectionLevel ecLevel, int maskPattern, BitArray bits)
			throws WriterException {
		if (!QRCode.isValidMaskPattern(maskPattern)) {
			throw new WriterException("Invalid mask pattern"); //$NON-NLS-1$
		}
		int typeInfo = (ecLevel.getBits() << 3) | maskPattern;
		bits.appendBits(typeInfo, 5);
		int bchCode = calculateBCHCode(typeInfo, TYPE_INFO_POLY);
		bits.appendBits(bchCode, 10);
		BitArray maskBits = new BitArray();
		maskBits.appendBits(TYPE_INFO_MASK_PATTERN, 15);
		bits.xor(maskBits);
		if (bits.getSize() != 15) {
			throw new WriterException("should not happen but we got: " + bits.getSize()); //$NON-NLS-1$
		}
	}

	private static void makeVersionInfoBits(Version version, BitArray bits) throws WriterException {
		bits.appendBits(version.getVersionNumber(), 6);
		int bchCode = calculateBCHCode(version.getVersionNumber(), VERSION_INFO_POLY);
		bits.appendBits(bchCode, 12);
		if (bits.getSize() != 18) {
			throw new WriterException("should not happen but we got: " + bits.getSize()); //$NON-NLS-1$
		}
	}

	// ==================== mask patterns and penalty (ported from ZXing MaskUtil) ====================

	private static boolean getDataMaskBit(int maskPattern, int x, int y) {
		int intermediate;
		int temp;
		switch (maskPattern) {
		case 0:
			intermediate = (y + x) & 0x1;
			break;
		case 1:
			intermediate = y & 0x1;
			break;
		case 2:
			intermediate = x % 3;
			break;
		case 3:
			intermediate = (y + x) % 3;
			break;
		case 4:
			intermediate = ((y / 2) + (x / 3)) & 0x1;
			break;
		case 5:
			temp = y * x;
			intermediate = (temp & 0x1) + (temp % 3);
			break;
		case 6:
			temp = y * x;
			intermediate = ((temp & 0x1) + (temp % 3)) & 0x1;
			break;
		case 7:
			temp = y * x;
			intermediate = ((temp % 3) + ((y + x) & 0x1)) & 0x1;
			break;
		default:
			throw new IllegalArgumentException("Invalid mask pattern: " + maskPattern); //$NON-NLS-1$
		}
		return intermediate == 0;
	}

	private static int calculateMaskPenalty(ByteMatrix matrix) {
		return applyMaskPenaltyRule1(matrix) + applyMaskPenaltyRule2(matrix)
				+ applyMaskPenaltyRule3(matrix) + applyMaskPenaltyRule4(matrix);
	}

	private static int applyMaskPenaltyRule1(ByteMatrix matrix) {
		return applyMaskPenaltyRule1Internal(matrix, true) + applyMaskPenaltyRule1Internal(matrix, false);
	}

	private static int applyMaskPenaltyRule1Internal(ByteMatrix matrix, boolean isHorizontal) {
		int penalty = 0;
		int iLimit = isHorizontal ? matrix.getHeight() : matrix.getWidth();
		int jLimit = isHorizontal ? matrix.getWidth() : matrix.getHeight();
		byte[][] array = matrix.getArray();
		for (int i = 0; i < iLimit; i++) {
			int numSameBitCells = 0;
			int prevBit = -1;
			for (int j = 0; j < jLimit; j++) {
				int bit = isHorizontal ? array[i][j] : array[j][i];
				if (bit == prevBit) {
					numSameBitCells++;
				} else {
					if (numSameBitCells >= 5) {
						penalty += 3 + (numSameBitCells - 5);
					}
					numSameBitCells = 1;
					prevBit = bit;
				}
			}
			if (numSameBitCells >= 5) {
				penalty += 3 + (numSameBitCells - 5);
			}
		}
		return penalty;
	}

	private static int applyMaskPenaltyRule2(ByteMatrix matrix) {
		int penalty = 0;
		byte[][] array = matrix.getArray();
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		for (int y = 0; y < height - 1; y++) {
			byte[] arrayY = array[y];
			for (int x = 0; x < width - 1; x++) {
				int value = arrayY[x];
				if (value == arrayY[x + 1] && value == array[y + 1][x] && value == array[y + 1][x + 1]) {
					penalty++;
				}
			}
		}
		return 3 * penalty;
	}

	private static int applyMaskPenaltyRule3(ByteMatrix matrix) {
		int numPenalties = 0;
		byte[][] array = matrix.getArray();
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				byte[] arrayY = array[y];
				if (x + 6 < width && arrayY[x] == 1 && arrayY[x + 1] == 0 && arrayY[x + 2] == 1
						&& arrayY[x + 3] == 1 && arrayY[x + 4] == 1 && arrayY[x + 5] == 0 && arrayY[x + 6] == 1
						&& (isWhiteHorizontal(arrayY, x - 4, x) || isWhiteHorizontal(arrayY, x + 7, x + 11))) {
					numPenalties++;
				}
				if (y + 6 < height && array[y][x] == 1 && array[y + 1][x] == 0 && array[y + 2][x] == 1
						&& array[y + 3][x] == 1 && array[y + 4][x] == 1 && array[y + 5][x] == 0
						&& array[y + 6][x] == 1
						&& (isWhiteVertical(array, x, y - 4, y) || isWhiteVertical(array, x, y + 7, y + 11))) {
					numPenalties++;
				}
			}
		}
		return numPenalties * 40;
	}

	private static boolean isWhiteHorizontal(byte[] rowArray, int from, int to) {
		if (from < 0 || rowArray.length < to) {
			return false;
		}
		for (int i = from; i < to; i++) {
			if (rowArray[i] == 1) {
				return false;
			}
		}
		return true;
	}

	private static boolean isWhiteVertical(byte[][] array, int col, int from, int to) {
		if (from < 0 || array.length < to) {
			return false;
		}
		for (int i = from; i < to; i++) {
			if (array[i][col] == 1) {
				return false;
			}
		}
		return true;
	}

	private static int applyMaskPenaltyRule4(ByteMatrix matrix) {
		int numDarkCells = 0;
		byte[][] array = matrix.getArray();
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		for (int y = 0; y < height; y++) {
			byte[] arrayY = array[y];
			for (int x = 0; x < width; x++) {
				if (arrayY[x] == 1) {
					numDarkCells++;
				}
			}
		}
		int numTotalCells = matrix.getHeight() * matrix.getWidth();
		int fivePercentVariances = Math.abs(numDarkCells * 2 - numTotalCells) * 10 / numTotalCells;
		return fivePercentVariances * 10;
	}
}
