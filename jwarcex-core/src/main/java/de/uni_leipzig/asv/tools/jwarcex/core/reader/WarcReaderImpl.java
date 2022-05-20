package de.uni_leipzig.asv.tools.jwarcex.core.reader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jwat.common.ByteCountingPushBackInputStream;
import org.jwat.gzip.GzipReader;
import org.jwat.warc.WarcReaderCompressed;
import org.jwat.warc.WarcReaderUncompressed;
import org.jwat.warc.WarcRecord;

import com.google.common.io.ByteStreams;

import de.uni_leipzig.asv.tools.jwarcex.core.constant.WarcConstants;
import de.uni_leipzig.asv.tools.jwarcex.core.util.WarcRecordUtil;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;

public class WarcReaderImpl implements WarcReader {

	private static final Logger LOGGER = LogManager.getLogger(WarcReaderImpl.class);

	private static final int INPUT_BUFFER_SIZE = 32 * 1024;

	private static final int PUSHBACK_BUFFER_SIZE = 32 * 1024;

	private WarcRecordUtil warcRecordUtil = new WarcRecordUtil();

	private org.jwat.warc.WarcReader warcReader;
	private Iterator<WarcRecord> warcIterator;

	private RawWarcDocument rawWarcDocument;


	public WarcReaderImpl(Path warcPath) {

		this(warcPath, false);
	}


	public WarcReaderImpl(InputStream inputStream) {

		this(inputStream, false);
	}


	public WarcReaderImpl(Path warcPath, boolean compressed) {

		InputStream is;
		try {

			is = new FileInputStream(warcPath.toFile());
		} catch (FileNotFoundException e) {

			throw new IllegalArgumentException(
					"Given warcPath is invalid (file not found): " + warcPath.toString());
		}

		this.initialize(is, compressed);
	}


	public WarcReaderImpl(InputStream inputStream, boolean compressed) {

		this.initialize(inputStream, compressed);
	}


	private void initialize(InputStream inputStream, boolean compressed) {

		this.initializeJwatWarcReader(inputStream, compressed);
		this.warcIterator = this.warcReader.iterator();
	}


	private void initializeJwatWarcReader(InputStream is, boolean compressed) {

		if (compressed) {

			this.warcReader = new WarcReaderCompressed(new GzipReader(is), INPUT_BUFFER_SIZE);
		} else {

			InputStream bis = new BufferedInputStream(is, INPUT_BUFFER_SIZE);
			ByteCountingPushBackInputStream pbin = new ByteCountingPushBackInputStream(bis, PUSHBACK_BUFFER_SIZE);
			this.warcReader = new WarcReaderUncompressed(pbin);
		}
	}


	@Override
	public void close() throws Exception {

		if (this.warcReader != null) {

			this.warcReader.close();
		}

	}


	@Override
	public boolean hasNext() {

		if (this.warcIterator == null) {

			return false;
		}

		while (this.rawWarcDocument == null && this.warcIterator.hasNext()) {

			WarcRecord warcRecord = this.warcIterator.next();

			if (this.warcRecordUtil.isResponseRecord(warcRecord)) {

				this.rawWarcDocument = this.convertToRawWarcDocument(warcRecord);
			}
		}

		return this.rawWarcDocument != null;
	}


	protected RawWarcDocument convertToRawWarcDocument(WarcRecord warcRecord) {

		try {
			String location = this.getWarcRecordHeaderFieldValue(warcRecord,
					WarcConstants.WARC_HEADER_LOCATION_KEY);
			String date = this.getWarcRecordHeaderFieldValue(warcRecord,
					WarcConstants.WARC_HEADER_DATE_KEY);

			byte[] bytes = this.toByteArray(warcRecord.getPayload().getInputStream());

			return new RawWarcDocument(location, date, bytes);

		} catch (IOException e) {

			LOGGER.warn("Could not read warc record: {}", e);
			return null;
		}
	}


	protected String getWarcRecordHeaderFieldValue(WarcRecord warcRecord, String headerFieldName) {

		return this.warcRecordUtil.getWarcRecordHeaderFieldValue(warcRecord, headerFieldName);
	}


	/**
	 * Proxy method for {@link ByteStreams#toByteArray(InputStream)} that can be mocked in unit tests.
	 * 
	 * @throws IOException
	 */
	protected byte[] toByteArray(InputStream inputStream) throws IOException {

		return ByteStreams.toByteArray(inputStream);
	}


	@Override
	public RawWarcDocument next() {

		RawWarcDocument next = this.rawWarcDocument;

		if (next != null) {

			this.rawWarcDocument = null;
		}

		return next;
	}
}
