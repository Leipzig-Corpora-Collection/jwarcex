package de.uni_leipzig.asv.tools.jwarcex.core.extract;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;

import de.uni_leipzig.asv.tools.jwarcex.core.concurrency.OutputWriterRunnable;
import de.uni_leipzig.asv.tools.jwarcex.core.concurrency.ProcessWarcRunnable;
import de.uni_leipzig.asv.tools.jwarcex.core.constant.OutputFormat;
import de.uni_leipzig.asv.tools.jwarcex.core.writer.SourceFormatWarcWriterImpl;
import de.uni_leipzig.asv.tools.jwarcex.core.writer.WarcWriter;
import de.uni_leipzig.asv.tools.jwarcex.core.writer.XmlWarcWriterImpl;
import de.uni_leipzig.asv.tools.jwarcex.encoding_detection.EncodingDetector;
import de.uni_leipzig.asv.tools.jwarcex.encoding_detection.EncodingDetectorImpl;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractor;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractorImpl;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;

/**
 * A parallel {@link WarcExtractor} implementation with a configurable number of threads parameter.
 */
public class ParallelWarcExtractor extends AbstractGzipCapableWarcExtractor implements WarcExtractor {

	private static final Logger LOGGER = LogManager.getLogger(ParallelWarcExtractor.class);

	/**
	 * "Number of Threads"-Parameter.
	 */
	public static final String PARAMETER_NUMBER_OF_THREADS = "numberOfThreads";

	/**
	 * Default value for the Parameter PARAMETER_NUMBER_OF_THREADS.
	 */
	public static final int PARAMETER_NUMBER_OF_THREADS_DEFAULT = 2;

	/**
	 * Default value for the Parameter PARAMETER_OUTPUT_FORMAT_DEFAULT.
	 */
	public static final OutputFormat PARAMETER_OUTPUT_FORMAT_DEFAULT = OutputFormat.SOURCE;

	/**
	 * Number of documents in a batch.
	 */
	private static final int BATCH_SIZE = 10;

	/**
	 * Queue size for the ThreadPoolExecutor which holds warc batches to be processed. This executor
	 * handles the warcProcessing, so increasing this value increases the memory relative to the size of
	 * BATCH_SIZE times the size {@link RawWarcDocument}.
	 */
	private static final int WARC_QUEUE_SIZE = 100;

	/**
	 * ExecutorService that runs the threads for WARC processing.
	 */
	private ThreadPoolExecutor executorServiceForWarcProcessing;

	/**
	 * Executor service that runs the output writer thread
	 */
	private ExecutorService executorServiceForOutputWriter;

	private OutputWriterRunnable outputWriterRunnable;

	/**
	 * Queue that collects {@link RawWarcDocument}s until they are processed batch-wise.
	 */
	private List<RawWarcDocument> warcDocumentQueue = new ArrayList<>(BATCH_SIZE);

	private BlockingQueue<ProcessedWarcDocument> outgoingSourceFileEntryQueue;

	/**
	 * Output format.
	 */
	private OutputFormat outputFormat = PARAMETER_OUTPUT_FORMAT_DEFAULT;

	/**
	 * Number of additional warc extraction threads to use.
	 */
	private int numberOfThreads = PARAMETER_NUMBER_OF_THREADS_DEFAULT;

	/**
	 * Number of documents read (by the reader).
	 */
	private int readDocuments = 0;


	/**
	 * For testing purposes only.
	 */
	protected ParallelWarcExtractor() {

		this(new EncodingDetectorImpl(), new TextExtractorImpl(0, 0, false),
				PARAMETER_OUTPUT_FORMAT_DEFAULT, PARAMETER_NUMBER_OF_THREADS_DEFAULT);
	}


	public ParallelWarcExtractor(TextExtractor textExtractor) {

		this(new EncodingDetectorImpl(), textExtractor, PARAMETER_OUTPUT_FORMAT_DEFAULT,
				PARAMETER_NUMBER_OF_THREADS_DEFAULT);
	}


	public ParallelWarcExtractor(EncodingDetector encodingDetector, TextExtractor textExtractor,
			OutputFormat outputFormat, int numberOfThreads) {

		Preconditions.checkNotNull(encodingDetector, "Argument encodingDetector must not be null");
		Preconditions.checkNotNull(textExtractor, "Argument textExtractor must not be null");
		Preconditions.checkArgument(numberOfThreads >= 1,
				"Argument numberOfThreads must be greater than or equal to 1");

		this.setEncodingDetector(encodingDetector);
		this.setTextExtractor(textExtractor);
		this.setOutputFormat(outputFormat);
		this.setNumberOfThreads(numberOfThreads);
	}


	@Override
	public void extract(Path warcPath, Path outputFilePath) {

		super.extract(warcPath, outputFilePath);
	}


	@Override
	protected WarcWriter createWarcWriter(OutputStream outputStream) throws IOException {

		if (outputFormat == OutputFormat.XML) {

			return new XmlWarcWriterImpl(outputStream);
		}

		return new SourceFormatWarcWriterImpl(outputStream);
	}


	@Override
	protected void prepare() throws IOException {

		this.outgoingSourceFileEntryQueue = new ArrayBlockingQueue<>(500);

		this.executorServiceForWarcProcessing = new ThreadPoolExecutor(this.getNumberOfThreads(),
				this.getNumberOfThreads(), 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(WARC_QUEUE_SIZE));
		this.executorServiceForOutputWriter = Executors.newSingleThreadExecutor();

		this.outputWriterRunnable = new OutputWriterRunnable(this.outgoingSourceFileEntryQueue,
				this.createWarcWriter(this.getOutputStream()));
		this.executorServiceForOutputWriter.submit(this.outputWriterRunnable);
	}


	@Override
	protected void processWarc(RawWarcDocument rawWarcDocument) {

		if (rawWarcDocument.getLocation() != null && rawWarcDocument.getLocation().endsWith("robots.txt")) {

			return;
		}

		this.addToQueue(rawWarcDocument);

		if (this.warcDocumentQueue.size() % BATCH_SIZE == 0) {

			this.submitPendingWarcDocuments();
		}
	}


	protected void addToQueue(RawWarcDocument rawWarcDocument) {

		this.warcDocumentQueue.add(rawWarcDocument);
		this.readDocuments++;
	}


	private void submitPendingWarcDocuments() {

		ProcessWarcRunnable warcToSourceFileEntryRunnable = new ProcessWarcRunnable(this.warcDocumentQueue,
				this.outgoingSourceFileEntryQueue, this.getEncodingDetector(), this.getTextExtractor().clone());
		this.warcDocumentQueue = new ArrayList<>(BATCH_SIZE);

		while (this.executorServiceForWarcProcessing.getQueue().size() > WARC_QUEUE_SIZE) {

			// when the queue limit is reached wait until the queue accepts again
		}

		this.submitToThreadpool(warcToSourceFileEntryRunnable);
	}


	@SuppressWarnings("unchecked")
	private void submitToThreadpool(ProcessWarcRunnable warcToSourceFileEntryRunnable) {

		Future<ProcessWarcRunnable> future = null;
		while (future == null) {

			try {
				future = (Future<ProcessWarcRunnable>) this.executorServiceForWarcProcessing
						.submit(warcToSourceFileEntryRunnable);
			} catch (RejectedExecutionException e) {

				// future is still null, continue while loop
			}
		}
	}


	@Override
	protected void cleanup() {

		LOGGER.debug("{} documents read", Integer.valueOf(this.readDocuments));

		if (this.warcDocumentQueue.size() > 0) {

			this.submitPendingWarcDocuments();
		}

		this.shutdownWarcProcessingExecutor();
		this.stopOutputWriter();
		this.shutdownOutputWriterExecutor();
		this.closeOutputStream();
	}


	private void stopOutputWriter() {

		synchronized (this) {
			while (!this.outgoingSourceFileEntryQueue.isEmpty()) {

				try {
					this.wait(10);
				} catch (InterruptedException e) {

					LOGGER.error(e);
				}
			}
		}

		this.outputWriterRunnable.setFinished(true);
	}


	private void shutdownWarcProcessingExecutor() {

		try {

			LOGGER.debug("executorServiceForWarcProcessing shutdown");
			this.executorServiceForWarcProcessing.shutdown();

			synchronized (this) {
				// wait until all warcProcessing runnables have run
				while (this.executorServiceForWarcProcessing.getQueue().size() > 0) {

					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("Waiting for warc processing to finish, queue size: "
								+ String.valueOf(this.executorServiceForWarcProcessing.getQueue().size()));
					}

					this.wait(10);

				}
			}

			if (!this.executorServiceForWarcProcessing.awaitTermination(10, TimeUnit.SECONDS)) {

				LOGGER.warn("Executor Service did not terminate successfully");
			}

		} catch (InterruptedException e) {

			LOGGER.error(e);

		}
	}


	private void shutdownOutputWriterExecutor() {

		LOGGER.trace("Shutdown for executorServiceForOutputWriter will be issued now");
		try {
			this.executorServiceForOutputWriter.shutdown();

			LOGGER.trace("Awaiting termination for executorServiceForOutputWriter");
			this.executorServiceForOutputWriter.awaitTermination(10, TimeUnit.SECONDS);

		} catch (InterruptedException e) {

			LOGGER.error("Error during the shutdown of the output writer", e);
		}
	}


	private void closeOutputStream() {

		try {

			this.getOutputStream().close();
		} catch (IOException e) {

			LOGGER.error("Error while closing outputStream", e);
		}
	}


	public int getNumberOfThreads() {

		return this.numberOfThreads;
	}


	public void setNumberOfThreads(int numberOfThreads) {

		this.numberOfThreads = numberOfThreads;
	}


	public OutputFormat getOutputFormat() {

		return outputFormat;
	}


	public void setOutputFormat(OutputFormat outputFormat) {

		this.outputFormat = outputFormat;
	}


	/**
	 * For testing purposes only.
	 */
	protected ThreadPoolExecutor getThreadPoolExecutor() {

		return this.executorServiceForWarcProcessing;
	}

}
