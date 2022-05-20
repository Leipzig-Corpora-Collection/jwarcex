package de.uni_leipzig.asv.tools.jwarcex.core.reader;

import java.util.Iterator;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;

public interface WarcReader extends Iterator<RawWarcDocument>, AutoCloseable {

	// nothing here
}
