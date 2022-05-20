package de.uni_leipzig.asv.tools.jwarcex;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jwat.common.ByteCountingPushBackInputStream;
import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderUncompressed;
import org.jwat.warc.WarcRecord;

/**
 * Some test WarcRecord objects.
 */
public class TestWarcRecords {

	private static Path warcPath = Paths.get("src/test/resources/warc/cz_web_2013_all.00003.5records.test.warc");


	private TestWarcRecords() {

		// prevent initialization
	}


	public static WarcRecord getFirstWarcRecord() throws IOException {

		ByteCountingPushBackInputStream pbin = getByteCountingPushbackInputStream();

		try (WarcReader warcReader = new WarcReaderUncompressed(pbin)) {

			return warcReader.getNextRecord();
		}
	}


	public static WarcRecord getSecondWarcRecord() throws IOException {

		ByteCountingPushBackInputStream pbin = getByteCountingPushbackInputStream();

		try (WarcReader warcReader = new WarcReaderUncompressed(pbin)) {

			warcReader.getNextRecord();

			return warcReader.getNextRecord();
		}
	}


	public static WarcRecord getThirdWarcRecord() throws IOException {

		ByteCountingPushBackInputStream pbin = getByteCountingPushbackInputStream();

		try (WarcReader warcReader = new WarcReaderUncompressed(pbin)) {

			warcReader.getNextRecord();
			warcReader.getNextRecord();

			return warcReader.getNextRecord();
		}
	}


	private static ByteCountingPushBackInputStream getByteCountingPushbackInputStream()
			throws FileNotFoundException {
		InputStream is = new BufferedInputStream(new FileInputStream(warcPath.toFile()));
		ByteCountingPushBackInputStream pbin = new ByteCountingPushBackInputStream(is, 32);
		return pbin;
	}
}
