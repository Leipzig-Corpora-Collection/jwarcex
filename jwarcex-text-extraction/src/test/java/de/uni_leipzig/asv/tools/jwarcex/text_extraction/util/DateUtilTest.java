package de.uni_leipzig.asv.tools.jwarcex.text_extraction.util;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilTest {

	private DateUtil dateUtil = new DateUtil();


	@Test
	public void testGetFormattedDate() throws ParseException {

		String dateString = this.dateUtil.getFormattedDate("2016-09-13T23:59:38Z");
		Assert.assertEquals("2016-09-13", dateString);
		System.out.println(this.dateUtil.getFormattedDate("2021-02-01T23:16:09Z"));
	}


	@Test
	public void testGetFormattedDateWithInvalidDate() {

		Assert.assertNull(this.dateUtil.getFormattedDate("invalid date"));
	}

}
