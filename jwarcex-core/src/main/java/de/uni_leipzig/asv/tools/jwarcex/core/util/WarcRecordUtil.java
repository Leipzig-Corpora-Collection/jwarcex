package de.uni_leipzig.asv.tools.jwarcex.core.util;

import org.jwat.common.HeaderLine;
import org.jwat.warc.WarcRecord;

import de.uni_leipzig.asv.tools.jwarcex.core.constant.WarcConstants;

/**
 * Provides helper function for working with WarcRecords.
 */
public class WarcRecordUtil {

	public WarcRecordUtil() {

		// nothing here
	}


	/**
	 * Returns true, if the given warcRecord is a response record. This is the case when the header
	 * WARC-Type equals "response".
	 *
	 * @param warcRecord
	 *            a warcRecord
	 * @return true is the give record is a response record, false otherwise.
	 */
	public boolean isResponseRecord(WarcRecord warcRecord) {

		HeaderLine warcTypeHeaderLine = warcRecord.getHeader(WarcConstants.WARC_HEADER_TYPE_KEY);

		return warcTypeHeaderLine != null && warcTypeHeaderLine.value.equals("response");
	}


	/**
	 * Returns the String value of the headerField with the given headerFieldName from the given
	 * warcRecord. If there is no headerField with the given name <code>null</code> will be returned.
	 *
	 * @param warcRecord
	 *            a warc record
	 * @param headerFieldName
	 *            name (key) of the headerField
	 * @return header field value as string if the header field is present, otherwise null.
	 */
	public String getWarcRecordHeaderFieldValue(WarcRecord warcRecord, String headerFieldName) {

		HeaderLine headerLine = warcRecord.getHeader(headerFieldName);

		if (headerLine != null) {

			return headerLine.value;
		}

		return null;
	}

}
