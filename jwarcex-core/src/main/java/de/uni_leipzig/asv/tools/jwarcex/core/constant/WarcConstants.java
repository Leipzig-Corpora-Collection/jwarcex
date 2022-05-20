package de.uni_leipzig.asv.tools.jwarcex.core.constant;

/**
 * Constants related to the WARC file format.
 */
public final class WarcConstants {

	/**
	 * Field name for the WarcHeader "WARC-Type".
	 */
	public static final String WARC_HEADER_TYPE_KEY = "WARC-Type";

	/**
	 * Field name for the WarcHeader "WARC-Target-URI".
	 */
	public static final String WARC_HEADER_LOCATION_KEY = "WARC-Target-URI";

	/**
	 * Field name for the WarcHeader "WARC-Date".
	 */
	public static final String WARC_HEADER_DATE_KEY = "WARC-Date";


	private WarcConstants() {

		// prevent initialization
	}

}
