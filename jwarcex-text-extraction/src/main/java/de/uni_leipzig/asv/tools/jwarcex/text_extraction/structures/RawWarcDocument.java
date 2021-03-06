package de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures;

/**
 * Represents a crawled web document.
 */
public class RawWarcDocument {

	private String location;

	private String date;

	private byte[] content;


	public RawWarcDocument(String location, String date, byte[] content) {

		this.location = location;
		this.date = date;
		this.content = content;
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


	public byte[] getContent() {

		return this.content;
	}


	public void setContent(byte[] content) {

		this.content = content;
	}

}
