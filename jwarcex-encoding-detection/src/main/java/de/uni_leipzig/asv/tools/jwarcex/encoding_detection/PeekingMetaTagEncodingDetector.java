package de.uni_leipzig.asv.tools.jwarcex.encoding_detection;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Tries to guess the encoding using information from html's meta tags.
 * <p>
 * Only looks at the first N bytes from the content, reads it assuming it is UTF-8 and tries to read
 * the meta tags. This is a fallback strategy, when no encoding could be detected from the byte
 * array.
 */
public class PeekingMetaTagEncodingDetector implements EncodingDetector {

	private static final Logger LOGGER = LogManager.getLogger(PeekingMetaTagEncodingDetector.class);

	private static final Pattern META_WITH_CONTENT_PATTERN = Pattern.compile("[a-zA-Z/]+;\\s*charset=(.*)");

	private static final String HTML_META_TAG = "meta";

	private static final String HTML_META_ATTRIBUTE_CHARSET = "charset";

	private static final String HTML_META_ATTRIBUTE_CONTENT = "content";

	private Set<String> availableCharsets = new HashSet<>();

	private int numBytes;


	public PeekingMetaTagEncodingDetector(int numBytes) {

		this.numBytes = numBytes;

		for (String charsetName : Charset.availableCharsets().keySet()) {

			availableCharsets.add(charsetName);
		}
	}


	@Override
	public String getEncoding(byte[] data) {

		byte[] firstBytes = getFirstFewBytes(data);

		String html = new String(firstBytes);
		Document document = Jsoup.parse(html);
		Elements elements = document.select(HTML_META_TAG);

		String encoding = this.getEncoding(elements);
		if (availableCharsets.contains(encoding)) {

			return encoding;
		}

		return null;
	}


	protected byte[] getFirstFewBytes(byte[] data) {

		int minLength = Math.min(data.length, this.numBytes);

		byte[] firstBytes = new byte[this.numBytes];
		System.arraycopy(data, 0, firstBytes, 0, minLength);
		return firstBytes;
	}


	protected String getEncoding(Elements elements) {

		Iterator<Element> iterator = elements.iterator();

		String encoding = null;

		while (iterator.hasNext()) {

			Element element = iterator.next();

			if (element.hasAttr(HTML_META_ATTRIBUTE_CHARSET)) {

				encoding = element.attr(HTML_META_ATTRIBUTE_CHARSET);
				LOGGER.trace("PeekingMetaTagEncodingDetector: read encoding from charset: {}",
						encoding);
				return encoding;

			} else if (element.hasAttr(HTML_META_ATTRIBUTE_CONTENT)) {

				String contentAttribute = element.attr(HTML_META_ATTRIBUTE_CONTENT);
				Matcher matcher = META_WITH_CONTENT_PATTERN.matcher(contentAttribute);

				if (matcher.matches()) {

					encoding = matcher.group(1);
					LOGGER.trace("PeekingMetaTagEncodingDetector: read encoding from content: {}",
							encoding);
					return encoding;
				}
			}
		}

		return encoding;
	}

}
