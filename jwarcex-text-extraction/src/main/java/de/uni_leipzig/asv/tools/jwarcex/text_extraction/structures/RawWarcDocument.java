package de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures;

/**
 * Represents a crawled web document.
 */
public class RawWarcDocument {

	private String warcRecordId;

	private String url;

	private String date;

	private byte[] content;


	public RawWarcDocument(String warcRecordId, String url, String date, byte[] content) {

		this.warcRecordId = warcRecordId;
		this.url = url;
		this.date = date;
		this.content = content;
	}


	public String getWarcRecordId() {

		return this.warcRecordId;
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


	public String getDate() {

		return this.date;
	}


	public void setDate(String date) {

		this.date = date;
	}


	public byte[] getContent() {

		return this.content;
	}


	public void setContent(byte[] content) {

		this.content = content;
	}

}
