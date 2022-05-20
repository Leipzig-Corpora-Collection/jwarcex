package de.uni_leipzig.asv.tools.jwarcex.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Assert;
import org.junit.Test;

import de.uni_leipzig.asv.tools.jwarcex.TestRawWarcDocuments;
import de.uni_leipzig.asv.tools.jwarcex.core.concurrency.ProcessWarcRunnable;
import de.uni_leipzig.asv.tools.jwarcex.encoding_detection.EncodingDetectorImpl;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractor;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractorImpl;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;

public class ProcessWarcRunnableTest {

	@Test
	public void testProcessWarcRunnable() throws InterruptedException {

		RawWarcDocument rawWarcDocument1 = TestRawWarcDocuments.getFirstRawWarcDocument();
		RawWarcDocument rawWarcDocument2 = TestRawWarcDocuments.getSecondRawWarcDocument();
		// this one gets dropped due too containing too few characters
		RawWarcDocument rawWarcDocument3 = TestRawWarcDocuments.getThirdRawWarcDocument();

		List<RawWarcDocument> rawWarcDocuments = new ArrayList<>();
		rawWarcDocuments.add(rawWarcDocument1);
		rawWarcDocuments.add(rawWarcDocument2);
		rawWarcDocuments.add(rawWarcDocument3);

		BlockingQueue<ProcessedWarcDocument> queue = new ArrayBlockingQueue<>(3);

		TextExtractor textExtractor = new TextExtractorImpl(
				TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				false);
		ProcessWarcRunnable processWarcRunnable = new ProcessWarcRunnable(rawWarcDocuments, queue,
				new EncodingDetectorImpl(), textExtractor);

		processWarcRunnable.run();

		Assert.assertEquals(2, queue.size());

		ProcessedWarcDocument processedRawWarcDocument1 = queue.take();
		Assert.assertEquals("location1", processedRawWarcDocument1.getLocation());
		Assert.assertEquals("2016-09-13", processedRawWarcDocument1.getDate());
		Assert.assertNotNull(processedRawWarcDocument1.getContent());

		ProcessedWarcDocument processedRawWarcDocument2 = queue.take();
		Assert.assertEquals("location2", processedRawWarcDocument2.getLocation());
		Assert.assertEquals("2013-06-21", processedRawWarcDocument2.getDate());
	}
}
