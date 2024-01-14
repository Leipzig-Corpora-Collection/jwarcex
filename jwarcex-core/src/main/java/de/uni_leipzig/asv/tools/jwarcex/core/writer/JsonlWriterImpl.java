package de.uni_leipzig.asv.tools.jwarcex.core.writer;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;
import com.google.common.base.Charsets;
import de.uni_leipzig.asv.tools.jwarcex.core.constant.WarcConstants;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jwat.warc.WarcRecord;
import org.jwat.warc.WarcWriterFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;


/**
 * A warc writer implementation, which writes extracted text into a JSONL format.
 */
public class JsonlWriterImpl implements WarcWriter, AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger(JsonlWriterImpl.class);

    private static final byte[] CRLF = "\r\n".getBytes(StandardCharsets.UTF_8);

    private OutputStream outputStream;

    private JsonWriter jsonWriter;

    public JsonlWriterImpl(OutputStream outputStream) throws IOException {


        this.outputStream = new BufferedOutputStream(outputStream, 8192);
    }


    @Override
    public void write(ProcessedWarcDocument processedWarcDocument) throws IOException {

        if (this.jsonWriter == null) {
            DslJson<Object> dslJson = new DslJson<>(Settings.withRuntime().allowArrayFormat(true).includeServiceLoader());
            this.jsonWriter = dslJson.newWriter();
        } else {
            this.outputStream.write(this.CRLF);
        }

        this.jsonWriter.reset();

        jsonWriter.writeByte(JsonWriter.OBJECT_START);

        jsonWriter.writeString("url");
        jsonWriter.writeByte(JsonWriter.SEMI);
        jsonWriter.writeString(processedWarcDocument.getLocation());
        jsonWriter.writeByte(JsonWriter.COMMA);

        jsonWriter.writeString("text");
        jsonWriter.writeByte(JsonWriter.SEMI);
        jsonWriter.writeString(processedWarcDocument.getContent());

        jsonWriter.writeByte(JsonWriter.OBJECT_END);

        this.outputStream.write(Arrays.copyOf(this.jsonWriter.getByteBuffer(), this.jsonWriter.size()));
    }


    @Override
    public void flush() {

        this.jsonWriter.flush();
        try {
            this.outputStream.flush();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }


    @Override
    public void close() throws Exception {

        this.outputStream.close();
    }
}
