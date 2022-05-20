package de.uni_leipzig.asv.tools.jwarcex.text_extraction.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides helper function for working with WarcRecords.
 */
public class DateUtil {

	private static final String ISO_8601_DATE_STRING = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	private static final String OUTPUT_DATE_FORMAT = "yyyy-MM-dd";

	private DateFormat dateFormatGmt = new SimpleDateFormat(ISO_8601_DATE_STRING);

	private DateFormat dateFormatOutputGmt = new SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.US);


	public DateUtil() {

		this.dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.dateFormatOutputGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	}


	/**
	 * Parses the date from the dateString of the warcRecord's "Warc-Date" header field and returns a
	 * formatted date string (yyyy-MM-dd).
	 *
	 * <pre>
	 * https://webarchive.jira.com/wiki/display/Heritrix/Glossary:
	 * "All times in Heritrix are GMT, assuming the clock and timezone on the local system
	 * are correct."
	 * </pre>
	 *
	 * @param dateString
	 *            a valid iso8601 string (GMT assumed)
	 * @return String a formatted string in the output date format yyyy-MM-dd (GMT) or null if the date
	 *         cannot be parsed
	 */
	public String getFormattedDate(String dateString) {

		Date date;
		try {
			date = this.dateFormatGmt.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
		return this.dateFormatOutputGmt.format(date);
	}

}
