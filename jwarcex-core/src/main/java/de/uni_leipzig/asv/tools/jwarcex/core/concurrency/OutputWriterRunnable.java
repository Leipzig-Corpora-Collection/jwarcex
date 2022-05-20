package de.uni_leipzig.asv.tools.jwarcex.core.concurrency;

import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_leipzig.asv.tools.jwarcex.core.writer.WarcWriter;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;

public class OutputWriterRunnable implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger(OutputWriterRunnable.class);

	private BlockingQueue<ProcessedWarcDocument> queue;

	private WarcWriter warcWriter;

	private volatile boolean finished = false;

	private int writtenDocuments = 0;


	public OutputWriterRunnable(BlockingQueue<ProcessedWarcDocument> queue, WarcWriter warcWriter) {

		this.queue = queue;
		this.warcWriter = warcWriter;
	}


	public void setFinished(boolean finished) {

		this.finished = finished;
	}


	@Override
	public void run() {

		while (!this.finished || !this.queue.isEmpty()) {
			try {

				ProcessedWarcDocument processedWarcDocument = this.queue.poll();

				if (processedWarcDocument != null) {

					this.warcWriter.write(processedWarcDocument);
					this.writtenDocuments++;
				}

			} catch (Throwable e) {

				LOGGER.error(e);
			}

		}

		this.warcWriter.flush();

		LOGGER.debug("Writer thread finished. {} documents written",
				Integer.valueOf(this.writtenDocuments));
	}
}
