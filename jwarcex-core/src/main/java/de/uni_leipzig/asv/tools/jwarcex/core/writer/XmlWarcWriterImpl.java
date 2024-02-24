package de.uni_leipzig.asv.tools.jwarcex.core.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;

/**
 * A warc writer implementation, which output location, date, encoding, and context for every 
 * WARC entry as XML.
 */
public class XmlWarcWriterImpl implements WarcWriter, AutoCloseable {

	private static final Logger LOGGER = LogManager.getLogger(XmlWarcWriterImpl.class);

	private static final String XML_START = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<documents>\n";

	private static final String XML_END = "</documents>\n";

	private Encoder encoder = Base64.getEncoder();

	private OutputStreamWriter outputStreamWriter;


	public XmlWarcWriterImpl(OutputStream outputStream) throws IOException {

		this.outputStreamWriter = new OutputStreamWriter(outputStream, Charsets.UTF_8);

		this.outputStreamWriter.write(XML_START);
	}


	@Override
	public void write(ProcessedWarcDocument processedWarcDocument) throws IOException {

		this.outputStreamWriter.write(this.getStringFromSourceFileEntry(processedWarcDocument));
	}


	protected String getStringFromSourceFileEntry(ProcessedWarcDocument processedWarcDocument) {

		StringBuilder sb = new StringBuilder();

		sb.append("    <document>\n");

		sb.append("        <meta>\n");
		sb.append("            <entry name=\"location\">");
		sb.append(Objects.toString(processedWarcDocument.getUrl()));
		sb.append("</entry>\n            <entry name=\"date\">");
		sb.append(processedWarcDocument.getDate());
		sb.append("</entry>\n            <entry name=\"encoding\">");
		sb.append(processedWarcDocument.getEncoding());
		sb.append("</entry>\n");
		sb.append("        </meta>\n");

		sb.append("        <content>");
		sb.append(encoder.encodeToString(processedWarcDocument.getContent().getBytes()));
		sb.append("</content>\n");

		sb.append("    </document>\n");

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

		this.outputStreamWriter.write(XML_END);
		outputStreamWriter.close();
	}
}
