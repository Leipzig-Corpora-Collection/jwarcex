package de.uni_leipzig.asv.tools.jwarcex.encoding_detection;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * An encoding detector which simply uses the encoding detector from the juniversalchardet library.
 */
public class UniversalEncodingDetector implements EncodingDetector {

	@Override
	public String getEncoding(byte[] data) {

		UniversalDetector detector = new UniversalDetector(null);

		detector.handleData(data, 0, data.length);
		detector.dataEnd();
		String detectedCharset = detector.getDetectedCharset();
		detector.reset();

		return detectedCharset;
	}

}
