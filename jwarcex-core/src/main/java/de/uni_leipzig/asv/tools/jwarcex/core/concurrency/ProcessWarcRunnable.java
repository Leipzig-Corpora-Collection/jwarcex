package de.uni_leipzig.asv.tools.jwarcex.core.concurrency;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_leipzig.asv.tools.jwarcex.core.util.WarcRecordUtil;
import de.uni_leipzig.asv.tools.jwarcex.encoding_detection.EncodingDetector;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractor;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;

/**
 * Performs all transformation necessary to build a {@link ProcessedWarcDocument} from a
 * {@link RawWarcDocument}.
 */
public class ProcessWarcRunnable implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger(ProcessWarcRunnable.class);

	private final EncodingDetector encodingDetector;

	private final List<RawWarcDocument> rawWarcDocuments;

	private final BlockingQueue<ProcessedWarcDocument> queue;

	private final TextExtractor textExtractor;


	public ProcessWarcRunnable(List<RawWarcDocument> rawWarcDocuments, BlockingQueue<ProcessedWarcDocument> queue,
			EncodingDetector encodingDetector, TextExtractor textExtractor) {

		this.rawWarcDocuments = rawWarcDocuments;
		this.queue = queue;
		this.encodingDetector = encodingDetector;
		this.textExtractor = textExtractor;
	}


	@Override
	public void run() {

		for (RawWarcDocument rawWarcDocument : this.rawWarcDocuments) {

			String encoding = this.getEncoding(rawWarcDocument);

			if (encoding != null) {

				this.process(rawWarcDocument, encoding);
			} else {

				LOGGER.warn("No encoding could be detected for document with location: {}",
						String.valueOf(rawWarcDocument.getLocation()));
			}

		}
	}


	private String getEncoding(RawWarcDocument rawWarcDocument) {

		return this.encodingDetector.getEncoding(rawWarcDocument.getContent());
	}


	private void process(RawWarcDocument rawWarcDocument, String encoding) {

		Charset charset;

		try {
			charset = getCharsetFromString(encoding);

			ProcessedWarcDocument processedWarcDocument = this.textExtractor.getText(rawWarcDocument, charset);

			if (processedWarcDocument != null) {

				this.addProcessedEntryToQueue(processedWarcDocument);
			} else {
				LOGGER.debug("No text extracted from document with location={}", rawWarcDocument.getLocation());
			}

		} catch (IllegalArgumentException e) {
			LOGGER.debug("IllegalArgumentException due to invalid encoding: {}", String.valueOf(encoding));
		} catch (RuntimeException e) {
			LOGGER.error(e);
		}


	}


	protected Charset getCharsetFromString(String encoding) {

		return Charset.forName(encoding);

	}


	protected void addProcessedEntryToQueue(ProcessedWarcDocument sourceFileEntry) {

		boolean success = false;

		while (!success) {
			success = this.queue.offer(sourceFileEntry);
		}
	}
}
