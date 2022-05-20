package de.uni_leipzig.asv.tools.jwarcex.core.extract;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import de.uni_leipzig.asv.tools.jwarcex.core.reader.WarcReader;
import de.uni_leipzig.asv.tools.jwarcex.core.reader.WarcReaderImpl;

/**
 * Provides support for handling gzip compressed warc files.
 */
public abstract class AbstractGzipCapableWarcExtractor extends AbstractWarcExtractor {

	/**
	 * Parameter that indicates whether the input is gzip compressed.
	 */
	public static final String PARAMETER_IS_COMPRESSED = "isCompressed";

	private boolean compressed = false;


	@Override
	protected WarcReader createWarcReader(InputStream inputStream) {

		return new WarcReaderImpl(inputStream, this.isCompressed());
	}


	@Override
	public void extract(Path warcPath, Path extractedFilePath) {

		super.extract(warcPath, extractedFilePath);
	}


	@Override
	public void extract(InputStream inputStream, OutputStream outputStream) {

		super.extract(inputStream, outputStream);
	}


	public boolean isCompressed() {

		return this.compressed;
	}


	public void setCompressed(boolean compressed) {

		this.compressed = compressed;
	}

}
