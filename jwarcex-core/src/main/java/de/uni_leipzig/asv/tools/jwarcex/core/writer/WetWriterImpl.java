package de.uni_leipzig.asv.tools.jwarcex.core.writer;

import com.google.common.io.BaseEncoding;
import de.uni_leipzig.asv.tools.jwarcex.core.constant.WarcConstants;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jwat.warc.WarcRecord;
import warc.WarcWriterExtendedUncompressed;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
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

    private final BaseEncoding encoder = BaseEncoding.base32();

    private MessageDigest digest;

    private OutputStream outputStream;

    private org.jwat.warc.WarcWriter jwatWarcWriter;

    public WetWriterImpl(OutputStream outputStream) throws IOException {

        this.outputStream = outputStream;
        this.jwatWarcWriter = new WarcWriterExtendedUncompressed(this.outputStream);

        try {
            this.digest = MessageDigest.getInstance("sha1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US)
                .withZone(ZoneId.of("GMT"));
        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        String warcInfoContentBlock = "Software-Info : jwarcex" + this.CRLF;
        warcInfoContentBlock += "Extracted-Date : " + zonedDateTime.format(formatter) + this.CRLF;
        this.writeWarcInfo(warcInfoContentBlock);
    }


    @Override
    public void write(ProcessedWarcDocument processedWarcDocument) throws IOException {

        WarcRecord warcRecord = this.getWarcWetRecord(processedWarcDocument, this.jwatWarcWriter);
        if (processedWarcDocument.getTitle() != null) {
            warcRecord.header.addHeader(WarcConstants.WET_FIELD_TITLE, processedWarcDocument.getTitle());
        }
        if (processedWarcDocument.getCanonicalUrl() != null) {
            warcRecord.header.addHeader(WarcConstants.WET_FIELD_CANONICAL_URL, processedWarcDocument.getCanonicalUrl());
        }

        String contentBlock = processedWarcDocument.getContent();
        byte[] contentBlockBytes = contentBlock.getBytes(StandardCharsets.UTF_8);
        warcRecord.header.addHeader(WarcConstants.CONTENT_TYPE, "text/plain");
        warcRecord.header.addHeader(WarcConstants.CONTENT_LENGTH,
                String.valueOf(Long.valueOf(contentBlockBytes.length)));
        this.addBlockDigest(warcRecord, contentBlockBytes);
        this.jwatWarcWriter.writeHeader(warcRecord);

        this.jwatWarcWriter.writePayload(contentBlockBytes);
        this.jwatWarcWriter.closeRecord();
    }


    protected void writeWarcInfo(String contentBlock) throws IOException {
        byte[] contentBlockBytes = contentBlock.getBytes(StandardCharsets.UTF_8);

        WarcRecord warcRecord = this.getWarcInfoRecord(this.jwatWarcWriter);
        warcRecord.header.addHeader(WarcConstants.CONTENT_TYPE, "application/warc-fields");
        warcRecord.header.addHeader(WarcConstants.CONTENT_LENGTH,
                String.valueOf(Long.valueOf(contentBlockBytes.length)));
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


    protected void addBlockDigest(WarcRecord warcRecord, byte[] contentBlockBytes) {

        byte[] sha1Checksum = this.digest.digest(contentBlockBytes);
        String blockDigestBase32 = "sha1:" + this.encoder.encode(sha1Checksum);
        warcRecord.header.addHeader(WarcConstants.WARC_HEADER_BLOCK_DIGEST, blockDigestBase32);
    }


    protected String getCurrentDate() {
        return this.dateFormatGmt.format(new Date());
    }


    protected WarcRecord getWarcWetRecord(ProcessedWarcDocument processedWarcDocument,
                                       org.jwat.warc.WarcWriter jwatWarcWriter) {

        WarcRecord warcRecord = WarcRecord.createRecord(jwatWarcWriter);

        warcRecord.header.addHeader(WarcConstants.WARC_HEADER_TYPE_KEY, "conversion");
        warcRecord.header.addHeader(WarcConstants.WARC_HEADER_LOCATION_KEY,
                Objects.toString(processedWarcDocument.getUrl()));
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
