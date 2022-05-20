package de.uni_leipzig.asv.tools.jwarcex.core.extract;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Extracts information from a warc file.
 */
public interface WarcExtractor {

	/**
	 * Reads the warcFile from the given <code>warcPath</code> and writes an arbitrary file format to
	 * the location of the given <code>outputFilePath</code>.
	 *
	 * @param warcPath
	 *            path to the warc file to be read
	 * @param outputFilePath
	 *            path to the output file
	 */
	void extract(Path warcPath, Path outputFilePath);


	/**
	 * Reads the given <code>inputStream</code> as a warcFile and writes an arbitrary file format to the
	 * given <code>outputStream</code>
	 *
	 * @param inputStream
	 *            inputStream to read from
	 * @param outputStream
	 *            outputStream to write to
	 */
	void extract(InputStream inputStream, OutputStream outputStream);
}
