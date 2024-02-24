package de.uni_leipzig.asv.tools.jwarcex.concurrency;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;
import org.mockito.Mockito;

import de.uni_leipzig.asv.tools.jwarcex.core.concurrency.OutputWriterRunnable;
import de.uni_leipzig.asv.tools.jwarcex.core.writer.WarcWriter;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;

public class OutputWriterRunnableTest {

	@Test
	public void testOutputWriterRunnable() throws InterruptedException, IOException {

		ProcessedWarcDocument processedWarcDocument1 = new ProcessedWarcDocument(
				"<urn:uuid:00000000-0000-0000-0000-000000000000>",
				"url1",
				"canonicalUrl1",
				"2016-01-01",
				"content1",
				"UTF-8");
		ProcessedWarcDocument processedWarcDocument2 = new ProcessedWarcDocument(
				"<urn:uuid:00000000-0000-0000-0000-000000000000>",
				"url2",
				"canonicalUrl2",
				"2016-02-02",
				"content2",
				"UTF-8");

		BlockingQueue<ProcessedWarcDocument> queue = new ArrayBlockingQueue<>(3);
		queue.add(processedWarcDocument1);
		queue.add(processedWarcDocument2);

		WarcWriter warcWriterMock = Mockito.mock(WarcWriter.class);

		OutputWriterRunnable outputWriterRunnable = new OutputWriterRunnable(queue, warcWriterMock);
		outputWriterRunnable.setFinished(true);

		Mockito.doNothing().when(warcWriterMock).write(processedWarcDocument1);
		Mockito.doNothing().when(warcWriterMock).write(processedWarcDocument2);

		outputWriterRunnable.run();

		Mockito.verify(warcWriterMock).write(processedWarcDocument1);
		Mockito.verify(warcWriterMock).write(processedWarcDocument2);
	}
}
