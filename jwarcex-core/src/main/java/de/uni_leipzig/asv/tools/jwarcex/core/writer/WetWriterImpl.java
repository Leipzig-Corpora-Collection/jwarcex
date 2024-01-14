package de.uni_leipzig.asv.tools.jwarcex.core.writer;

import de.uni_leipzig.asv.tools.jwarcex.core.constant.WarcConstants;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jwat.warc.WarcRecord;
import org.jwat.warc.WarcWriterFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;


/**
 * A warc writer implementation, which writes extracted text into a WET format.
 */
public class WetWriterImpl implements WarcWriter, AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger(WetWriterImpl.class);

    private static final String ISO_8601_DATE_STRING = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final String CRLF = "\r\n";

    private final DateFormat dateFormatGmt = new SimpleDateFormat(ISO_8601_DATE_STRING);

    private OutputStream outputStream;

    private org.jwat.warc.WarcWriter jwatWarcWriter;

    public WetWriterImpl(OutputStream outputStream) throws IOException {

        this.outputStream = outputStream;
        this.jwatWarcWriter = WarcWriterFactory.getWriterUncompressed(this.outputStream);

        String warcInfoContentBlock = "software : jwarcex" + this.CRLF;
        this.writeWarcInfo(warcInfoContentBlock);
    }


    @Override
    public void write(ProcessedWarcDocument processedWarcDocument) throws IOException {

        String contentBlock = processedWarcDocument.getContent();
        byte[] contentBlockBytes = contentBlock.getBytes(StandardCharsets.UTF_8);

        WarcRecord warcRecord = this.getWarcWetRecord(processedWarcDocument, this.jwatWarcWriter);
        warcRecord.header.addHeader("Content-Type", "text/plain");
        warcRecord.header.addHeader("Content-Length", String.valueOf(Long.valueOf(contentBlockBytes.length)));
        this.jwatWarcWriter.writeHeader(warcRecord);

        this.jwatWarcWriter.writePayload(contentBlockBytes);
        this.jwatWarcWriter.closeRecord();
    }


    protected void writeWarcInfo(String contentBlock) throws IOException {
        byte[] contentBlockBytes = contentBlock.getBytes(StandardCharsets.UTF_8);

        WarcRecord warcRecord = this.getWarcInfoRecord(this.jwatWarcWriter);
        warcRecord.header.addHeader("Content-Type", "application/warc-fields");
        warcRecord.header.addHeader("Content-Length", String.valueOf(Long.valueOf(contentBlockBytes.length)));
        this.jwatWarcWriter.writeHeader(warcRecord);

        this.jwatWarcWriter.writePayload(contentBlockBytes);
        this.jwatWarcWriter.closeRecord();
    }

    protected WarcRecord getWarcInfoRecord(org.jwat.warc.WarcWriter jwatWarcWriter) {

        WarcRecord warcRecord = WarcRecord.createRecord(jwatWarcWriter);

        warcRecord.header.addHeader(WarcConstants.WARC_HEADER_TYPE_KEY, "warcinfo");
        warcRecord.header.addHeader(WarcConstants.WARC_HEADER_DATE_KEY, this.getCurrentDate());
        warcRecord.header.addHeader(WarcConstants.WARC_HEADER_RECORD_ID_KEY, this.getRecordIdKey());

        return warcRecord;
    }


    protected String getCurrentDate() {
        return this.dateFormatGmt.format(new Date());
    }


    protected WarcRecord getWarcWetRecord(ProcessedWarcDocument processedWarcDocument,
                                       org.jwat.warc.WarcWriter jwatWarcWriter) {

        WarcRecord warcRecord = WarcRecord.createRecord(jwatWarcWriter);

        warcRecord.header.addHeader(WarcConstants.WARC_HEADER_TYPE_KEY, "conversion");
        warcRecord.header.addHeader(WarcConstants.WARC_HEADER_LOCATION_KEY,
                Objects.toString(processedWarcDocument.getLocation()));
        warcRecord.header.addHeader(WarcConstants.WARC_HEADER_DATE_KEY, processedWarcDocument.getDate());
        warcRecord.header.addHeader(WarcConstants.WARC_HEADER_RECORD_ID_KEY, this.getRecordIdKey());
        warcRecord.header.addHeader(WarcConstants.WARC_HEADER_REFERS_TO, processedWarcDocument.getWarcRecordId());

        return warcRecord;
    }


    protected String getRecordIdKey() {

        StringBuilder sb = new StringBuilder();
        sb.append("<urn:uuid:");
        sb.append(UUID.randomUUID());
        sb.append(">");
        return sb.toString();
    }


    @Override
    public void flush() {

        try {
            this.outputStream.flush();
        } catch (IOException e) {

            LOGGER.error(e);
        }
    }


    @Override
    public void close() throws Exception {

        jwatWarcWriter.close();
    }
}
