package de.uni_leipzig.asv.tools.jwarcex.encoding_detection;

import java.util.Arrays;
import java.util.List;

/**
 * A composite encoding detector which has a group of child encoding detectors.
 */
public class EncodingDetectorImpl implements EncodingDetector {

	/**
	 * Will be used if no child encoding detector could detect the encoding.
	 */
	public static final String CHARSET_NAME_FALLBACK = "UTF-8";

	private List<EncodingDetector> detectors;


	/**
	 * Convenience constructor with the default list of detectors.
	 */
	public EncodingDetectorImpl() {

		this(Arrays.asList(new UniversalEncodingDetector(),
				new PeekingMetaTagEncodingDetector(3000)));
	}


	public EncodingDetectorImpl(List<EncodingDetector> detectors) {

		this.detectors = detectors;
	}


	/**
	 * {@inheritDoc}
	 *
	 * This implementation simply iterates over all child encoding detectors. The composite encoding
	 * detector returns the first non-null answer that is given by a child encoding detector as the
	 * detected encoding.
	 */
	@Override
	public String getEncoding(byte[] data) {

		String encoding = null;

		for (EncodingDetector encodingDetector : this.detectors) {

			encoding = encodingDetector.getEncoding(data);

			if (encoding != null) {

				return encoding;
			}
		}

		return CHARSET_NAME_FALLBACK;
	}

}
