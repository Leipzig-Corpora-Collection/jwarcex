package de.uni_leipzig.asv.tools.jwarcex.core.extract;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_leipzig.asv.tools.jwarcex.core.reader.WarcReader;
import de.uni_leipzig.asv.tools.jwarcex.core.writer.WarcWriter;
import de.uni_leipzig.asv.tools.jwarcex.encoding_detection.EncodingDetector;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractor;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;

/**
 * Abstract base class for a warc extractor.
 */
public abstract class AbstractWarcExtractor implements WarcExtractor {

	private static final Logger LOGGER = LogManager.getLogger(AbstractWarcExtractor.class);

	/**
	 * Buffer size for the output stream.
	 */
	private static final int OUTPUT_BUFFER_SIZE = 8 * 1024 * 1024;

	/**
	 * The target stream which will be written to.
	 */
	private OutputStream targetOutputStream;

	/**
	 * An encoding detector to use.
	 */
	private EncodingDetector encodingDetector;

	/**
	 * A text extractor to use.
	 */
	private TextExtractor textExtractor;


	@Override
	public void extract(Path warcPath, Path ouputFilePath) {

		InputStream inputStream;
		try {

			inputStream = new FileInputStream(warcPath.toFile());
			this.setOutputStream(new BufferedOutputStream(
					new FileOutputStream(ouputFilePath.toFile()), OUTPUT_BUFFER_SIZE));
			this.extract(inputStream);

		} catch (FileNotFoundException e) {

			LOGGER.error("Unexpected exception during extraction", e);
		}
	}


	@Override
	public void extract(InputStream inputStream, OutputStream outputStream) {

		this.setOutputStream(outputStream);
		this.extract(inputStream);
	}


	private void extract(InputStream inputStream) {

		try (WarcReader warcReader = this.createWarcReader(inputStream)) {

			this.prepare();

			while (warcReader.hasNext()) {

				this.processWarc(warcReader.next());
			}

		} catch (Exception e) {

			LOGGER.error("Unexpected exception during extraction", e);
		} finally {

			this.cleanup();
		}
	}


	protected OutputStream getOutputStream() {

		return this.targetOutputStream;
	}


	protected void setOutputStream(OutputStream outputStream) {

		this.targetOutputStream = outputStream;
	}


	public TextExtractor getTextExtractor() {

		return this.textExtractor;
	}


	public void setTextExtractor(TextExtractor textExtractor) {

		this.textExtractor = textExtractor;
	}


	public EncodingDetector getEncodingDetector() {

		return this.encodingDetector;
	}


	public void setEncodingDetector(EncodingDetector encodingDetector) {

		this.encodingDetector = encodingDetector;
	}


	protected abstract WarcReader createWarcReader(InputStream inputStream);


	protected abstract WarcWriter createWarcWriter(OutputStream outputStream) throws IOException;


	/**
	 * This method is called just before the warc extractions. Any necessary preparations should be made
	 * here.
	 *
	 * @throws IOException
	 */
	protected abstract void prepare() throws IOException;


	protected abstract void processWarc(RawWarcDocument rawWarcDocument);


	protected abstract void cleanup();

}
