package de.uni_leipzig.asv.tools.jwarcex;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;

/**
 * Some test RawWarcDocument objects.
 */
public class TestRawWarcDocuments {

	private TestRawWarcDocuments() {

		// prevent initialization
	}


	public static RawWarcDocument getFirstRawWarcDocument() {

		return new RawWarcDocument("<urn:uuid:00000000-0000-0000-0000-000000000001>",
				"location1",
				"2016-09-13T17:46:38Z",
				new String("<html><body>hallo das ist ein test, hallo das ist ein test, "
						+ "hallo das ist ein test, hallo das ist ein test, "
						+ "hallo das ist ein test</body></html>").getBytes());
	}


	public static RawWarcDocument getSecondRawWarcDocument() {

		return new RawWarcDocument("<urn:uuid:00000000-0000-0000-0000-000000000002>",
				"location2",
				"2013-06-21T10:26:38Z",
				new String("<html><body>und nochmal mehr content und nochmal mehr content"
						+ "und nochmal mehr content und nochmal mehr content</body></html>").getBytes());
	}


	public static RawWarcDocument getThirdRawWarcDocument() {

		return new RawWarcDocument("<urn:uuid:00000000-0000-0000-0000-000000000003>",
				"location3",
				"2013-06-21T12:55:38Z",
				new String("<html><body>this one is too short</body></html>").getBytes());
	}
}
