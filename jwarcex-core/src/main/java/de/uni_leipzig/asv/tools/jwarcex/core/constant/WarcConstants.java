package de.uni_leipzig.asv.tools.jwarcex.core.constant;

/**
 * Constants related to the WARC file format.
 */
public final class WarcConstants {

	/**
	 * Field name for the WarcHeader "WARC-Record-ID".
	 */
	public static final String WARC_HEADER_RECORD_ID_KEY = "WARC-Record-ID";

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

	/**
	 * Field name for the WarcHeader "WARC-Refers-To".
	 */
	public static final String WARC_HEADER_REFERS_TO = "WARC-Refers-To";


	private WarcConstants() {

		// prevent initialization
	}

}
