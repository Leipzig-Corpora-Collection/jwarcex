package de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures;

/**
 * A processed warc document.
 */
public class ProcessedWarcDocument {

	/**
	 * The document's url.
	 */
	private String location;

	/**
	 * A formatted date string with the request date.
	 */
	private String date;

	/**
	 * The document's content.
	 */
	private String content;

	/**
	 * The detected encoding for this document.
	 */
	private String encoding;


	public ProcessedWarcDocument(String location, String date, String content, String encoding) {

		this.location = location;
		this.date = date;
		this.content = content;
		this.encoding = encoding;
	}


	public String getLocation() {

		return this.location;
	}


	public void setLocation(String location) {

		this.location = location;
	}


	public String getDate() {

		return this.date;
	}


	public void setDate(String date) {

		this.date = date;
	}


	public String getContent() {

		return this.content;
	}


	public void setContent(String content) {

		this.content = content;
	}


	public String getEncoding() {

		return encoding;
	}


	public void setEncoding(String encoding) {

		this.encoding = encoding;
	}

}
