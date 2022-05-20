package de.uni_leipzig.asv.tools.jwarcex;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jwat.common.ByteCountingPushBackInputStream;
import org.jwat.gzip.GzipReader;
import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderCompressed;
import org.jwat.warc.WarcReaderUncompressed;
import org.jwat.warc.WarcRecord;
import org.jwat.warc.WarcWriter;
import org.jwat.warc.WarcWriterFactory;

import com.google.common.io.ByteStreams;

/**
 * A simple tool to extract a given number of warcRecords (not only response records!) from a huge
 * warc file.
 */
public class TestExtractWarcEntriesTool {

	private static final int MAX_RECORDS = 10;

	private static final Path INPUT_FILE_PATH = Paths.get("");

	private static final Path OUPUT_FILE_PATH = Paths.get("");

	private static final boolean INPUT_COMPRESSED = true;


	public static void main(String[] args) throws IOException {

		InputStream is = new BufferedInputStream(new FileInputStream(INPUT_FILE_PATH.toFile()));
		ByteCountingPushBackInputStream pbin = new ByteCountingPushBackInputStream(is, 2048);

		OutputStream os = new BufferedOutputStream(new FileOutputStream(OUPUT_FILE_PATH.toFile()));

		WarcReader warcReader = getWarcReader(pbin);

		try (WarcWriter warcWriter = WarcWriterFactory.getWriter(os, false)) {

			writeRecords(warcReader, warcWriter);
		}
	}


	private static void writeRecords(WarcReader warcReader, WarcWriter warcWriter)
			throws IOException {

		WarcRecord warcRecord;
		int numRecords = 0;

		while ((warcRecord = warcReader.getNextRecord()) != null && numRecords < MAX_RECORDS) {

			warcWriter.writeHeader(warcRecord);

			if (warcRecord.hasPayload()) {

				byte[] bytes = ByteStreams.toByteArray(warcRecord.getPayload().getInputStreamComplete());
				warcWriter.writePayload(bytes, 0, bytes.length);
			}

			numRecords++;

			System.out.println(numRecords);
		}

		warcWriter.close();
	}


	private static WarcReader getWarcReader(ByteCountingPushBackInputStream pbin) {

		if (INPUT_COMPRESSED) {

			return new WarcReaderCompressed(new GzipReader(pbin));
		}

		return new WarcReaderUncompressed(pbin);
	}
}
