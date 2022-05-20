package de.uni_leipzig.asv.tools.jwarcex.encoding_detection;

/**
 * Detects the used encoding (charset) for byte arrays which represent text data.
 */
public interface EncodingDetector {

	/**
	 * Tries to detect the correct encoding for the given byte array. Returns <code>null</code> if no
	 * encoding could be detected.
	 *
	 * @param data
	 *            a byte array
	 * @return the charSet name if detected, otherwise null.
	 */
	String getEncoding(byte[] data);
}
