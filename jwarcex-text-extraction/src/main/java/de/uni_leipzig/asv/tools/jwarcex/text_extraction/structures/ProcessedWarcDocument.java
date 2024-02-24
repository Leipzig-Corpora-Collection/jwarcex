package de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures;

/**
 * A processed warc document.
 */
public class ProcessedWarcDocument {

	/**
	 * WARC record ID.
	 */
	protected String warcRecordId;

	/**
	 * The document's url.
	 */
	private String url;

	/**
	 * The document's canonical url.
	 */
	private String canonicalUrl;

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

	public ProcessedWarcDocument(String warcRecordId, String url, String canonicalUrl, String date,
								 String content, String encoding) {

		this.warcRecordId = warcRecordId;
		this.url = url;
		this.canonicalUrl = canonicalUrl;
		this.date = date;
		this.content = content;
		this.encoding = encoding;
	}

	public String getWarcRecordId() {

		return warcRecordId;
	}

	public void setWarcRecordId(String warcRecordId) {

		this.warcRecordId = warcRecordId;
	}

	public String getUrl() {

		return this.url;
	}


	public void setUrl(String url) {

		this.url = url;
	}


	public String getCanonicalUrl() {

		return this.canonicalUrl;
	}


	public void setCanonicalUrl(String url) {

		this.url = canonicalUrl;
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
