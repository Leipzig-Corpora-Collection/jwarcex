package de.uni_leipzig.asv.tools.jwarcex.core.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;

/**
 * A warc writer implementation, which writes WARCs into a pseudo-XML source format.
 */
public class SourceFormatWarcWriterImpl implements WarcWriter, AutoCloseable {

	private static final Logger LOGGER = LogManager.getLogger(SourceFormatWarcWriterImpl.class);

	private OutputStreamWriter outputStreamWriter;


	public SourceFormatWarcWriterImpl(OutputStream outputStream) {

		this.outputStreamWriter = new OutputStreamWriter(outputStream, Charsets.UTF_8);
	}


	@Override
	public void write(ProcessedWarcDocument processedWarcDocument) throws IOException {

		this.outputStreamWriter.write(this.getStringFromSourceFileEntry(processedWarcDocument));
	}


	protected String getStringFromSourceFileEntry(ProcessedWarcDocument processedWarcDocument) {

		StringBuilder sb = new StringBuilder();

		sb.append("<source><location>");
		sb.append(Objects.toString(processedWarcDocument.getLocation()));
		sb.append("</location><date>");
		sb.append(processedWarcDocument.getDate());
		sb.append("</date>");

		sb.append("<encoding>");
		sb.append(processedWarcDocument.getEncoding());
		sb.append("</encoding>");

		sb.append("</source>");
		sb.append("\n\n");

		sb.append(Objects.toString(processedWarcDocument.getContent()));
		sb.append("\n\n");

		return sb.toString();
	}


	@Override
	public void flush() {

		try {
			this.outputStreamWriter.flush();
		} catch (IOException e) {

			LOGGER.error(e);
		}
	}


	@Override
	public void close() throws Exception {

		outputStreamWriter.close();
	}
}
