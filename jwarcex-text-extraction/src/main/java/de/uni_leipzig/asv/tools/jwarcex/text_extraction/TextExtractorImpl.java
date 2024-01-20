package de.uni_leipzig.asv.tools.jwarcex.text_extraction;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.github.chschroeder.dom.DomContentExtrator;
import com.github.chschroeder.dom.TextDensityContentExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import com.google.common.collect.Sets;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.util.DateUtil;

/**
 * Implements a {@link TextExtractor} using JSoup.
 * <p>
 * Additional text cleaning features are as follows:
 * <ul>
 * <li>Misplaced noscript tags (which is invalid html but encountered on some sites) will be
 * filtered.
 * <li>Multiple sequential line breaks (&gt;2) will be compressed.
 * </ul>
 * <p>
 * Some custom behaviour was included to match the previous JWarcEx application:
 * <ul>
 * <li>Preserves whitespace for div and p tags.
 * <li>Only considers text with a length greater or equal than 20.
 * </ul>
 */
public class TextExtractorImpl implements TextExtractor {

	/**
	 * Only elements with more characters than this length will be added to the document's total text.
	 * Elements with less than this number of characters will be ignored.
	 */
	public static final int PARAMETER_MIN_LINE_LENGTH_DEFAULT = 20;

	/**
	 * Default value for minDocumentLength.
	 */
	public static final int PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT = 80;

	/**
	 * Default value for extractTitle.
	 */
	public static final boolean PARAMETER_EXTRACT_TITLE_DEFAULT = false;

	/**
	 * Default value for performContentExtraction.
	 */
	public static final boolean PARAMETER_PERFORM_CONTENT_EXTRACTION_DEFAULT = false;

	/**
	 * Table cell tags.
	 */
	private static final Set<Tag> TABLE_CELL_ELEMENTS = Sets.newHashSet(Tag.valueOf("td"), Tag.valueOf("th"));

	/**
	 * Do not add a line break for these tags.
	 */
	private static final Set<Tag> FORCE_NONBREAK_ELEMENTS = Sets.newHashSet(Tag.valueOf("thead"), Tag.valueOf("tbody"));

	/**
	 * Anchor tag.
	 */
	private static final Tag ANCHOR_TAG = Tag.valueOf("a");

	/**
	 * Line break tag.
	 */
	private static final Tag BR_ELEMENT = Tag.valueOf("br");

	/**
	 * Maximum number of subsequent linebreaks caused by block elements.
	 */
	private static final int MAX_BLOCK_LEVEL_LINE_BREAKS = 2;

	/**
	 * Date Util
	 */
	private final DateUtil dateUtil = new DateUtil();

	/**
	 * A content extractor, which preprocesses a document before text extraction (optional).
	 */
	private DomContentExtrator domContentExtrator = null;

	/**
	 * Only elements with more characters than this length will be added to the document's total text.
	 * Elements with less than this number of characters will be ignored
	 */
	private final int minLineLength;

	/**
	 * Documents with less characters will be ignored. Is preceded by minLineLength.
	 */
	private final int minDocumentLength;

	/**
	 * Extracts the html document's title and outputs it on the first line if true.
	 */
	private final boolean extractTitle;

	/**
	 * For testing purposes only.
	 */
	protected TextExtractorImpl() {

		this(PARAMETER_MIN_LINE_LENGTH_DEFAULT, PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT, PARAMETER_EXTRACT_TITLE_DEFAULT,
				PARAMETER_PERFORM_CONTENT_EXTRACTION_DEFAULT);
	}


	public TextExtractorImpl(int minLineLength, int minDocumentLength, boolean extractTitle,
							 boolean performContentExtraction) {

		this.minLineLength = minLineLength;
		this.validateMinLineLength();

		this.minDocumentLength = minDocumentLength;
		this.validateMinDocumentLength();

		this.extractTitle = extractTitle;

		if (performContentExtraction) {
			this.domContentExtrator = new TextDensityContentExtractor();
		}

	}


	private void validateMinLineLength() {

		if (this.minLineLength < -1) {

			throw new IllegalArgumentException(
					"Invalid paramater value for minLineLength + (" + String.valueOf(this.minLineLength) + ")");
		}
	}


	private void validateMinDocumentLength() {
		if (this.minDocumentLength < -1) {

			throw new IllegalArgumentException(
					"Invalid paramater value for minDocumentLength + (" + String.valueOf(this.minDocumentLength) + ")");
		}
	}


	@Override
	public ProcessedWarcDocument getText(RawWarcDocument rawWarcDocument, Charset charset) {

		String text = new String(rawWarcDocument.getContent(), charset);
		Document document = Jsoup.parse(text);
		document = this.handleNoscriptTags(document);

		if (this.domContentExtrator != null) {
			Element content = domContentExtrator.extractContent(document);
			return this.extractFromHtml(rawWarcDocument, charset, content);
		}

		return this.extractFromHtml(rawWarcDocument, charset, document);

	}


	protected ProcessedWarcDocument extractFromHtml(RawWarcDocument rawWarcDocument, Charset charset,
			Element element) {

		String extractedText = extractText(element);
		if (extractedText == null) {

			return null;
		}

		return new ProcessedWarcDocument(rawWarcDocument.getWarcRecordId(), rawWarcDocument.getLocation(),
				this.dateUtil.getFormattedDate(rawWarcDocument.getDate()), extractedText.toString(), charset.name());

	}


	protected String extractText(Element element) {

		StringBuilder extractedText = new StringBuilder();

		if (this.extractTitle) {
			extractedText.append(this.getText(element.selectFirst("head title")));
			extractedText.append('\n');
		}

		Element startElement = element.selectFirst("body");
		if (startElement == null) {

			startElement = element;
		}

		String bodyText = this.getText(startElement).strip();
		extractedText.append(bodyText);

		if (extractedText.length() < this.minDocumentLength) {

			return null;
		}

		return extractedText.toString();
	}


	/**
	 * Remove any <noscript> tags that reside within the <head> of the document. They can per definition
	 * only contain style, link and meta elements (where we do not extract text from).
	 *
	 * This is to avoid an incorrect behavior for invalid html pages where the creators wrongly use
	 * arbitrary elements inside a head's noscript tag.
	 *
	 * @see https://developer.mozilla.org/en/docs/Web/HTML/Element/noscript
	 */
	private Document handleNoscriptTags(Document document) {

		Elements elements = document.select("head noscript");

		for (Element element : elements) {

			element.remove();
		}

		return document;
	}


	/**
	 * Extracts the text from the given element.
	 *
	 * @param element
	 *            Element to start from
	 * @return the text extracted from the html structure
	 */
	private String getText(Element element) {

		Stack<Node> stack = new Stack<>();
		Set<Node> visited = new HashSet<>(1000);

		StringBuilder sb = new StringBuilder();

		stack.add(element);

		while (!stack.isEmpty()) {

			Node node = stack.pop();
			if (!visited.contains(node)) {

				visit(stack, visited, sb, node);
				for (int i = node.childNodeSize() - 1; i >= 0; i--) {

					Node childNode = node.childNode(i);
					stack.add(childNode);
				}

			}

		}

		return sb.toString();
	}


	protected void visit(Stack<Node> stack, Set<Node> visited, StringBuilder sb, Node node) {
		if (node instanceof TextNode) {

			TextNode textNode = (TextNode) node;
			String text = textNode.text();

			boolean nonEmptyTextNode = text.trim().length() > 0;
			if (nonEmptyTextNode && text.length() >= this.minLineLength) {

				sb.append(text);
			}

		} else if (node instanceof Element) {

			this.processNonTextNode(sb, node);
		}
		visited.add(node);
	}


	private void processNonTextNode(StringBuilder sb, Node node) {

		Element nodeElement = (Element) node;
		Tag tag = nodeElement.tag();

		if (!BR_ELEMENT.equals(tag)) {

			if (TABLE_CELL_ELEMENTS.contains(tag) && this.needsSpace(sb)) {

				sb.append(" ");
			} else if (ANCHOR_TAG.equals(tag)) {

				fixAnchorTagIfNecessary(sb, node);
			}

			boolean lineBreakPossible = sb.length() < MAX_BLOCK_LEVEL_LINE_BREAKS
					|| sb.charAt(sb.length() - MAX_BLOCK_LEVEL_LINE_BREAKS) != '\n';

			boolean lineBreakDesired = tag.isBlock() && !TABLE_CELL_ELEMENTS.contains(tag)
					&& !FORCE_NONBREAK_ELEMENTS.contains(tag);
			if (lineBreakPossible && lineBreakDesired) {

				this.addNewLine(sb);
			}

		} else {

			this.addNewLine(sb);
		}
	}


	protected void fixAnchorTagIfNecessary(StringBuilder sb, Node node) {
		Node prevSibling = node.previousSibling();

		if (prevSibling != null && prevSibling instanceof Element) {

			Element siblingNode = (Element) node.previousSibling();
			Tag siblingTag = siblingNode.tag();

			if (ANCHOR_TAG.equals(siblingTag)) {
				sb.append(" ");
			}
		}
	}


	private boolean needsSpace(StringBuilder sb) {

		return sb.length() != 0 && sb.charAt(sb.length() - 1) != '\n' && sb.charAt(sb.length() - 1) != ' ';
	}


	private void addNewLine(StringBuilder sb) {

		sb.append("\n");
	}
	
	
	public int getMineLineLength() {
		
		return this.minLineLength;
	}
	
	
	public int getMinDocumentLength() {
		
		return this.minDocumentLength;
	}
	
	
	public boolean extractTitle() {
		
		return this.extractTitle;
	}


	public DomContentExtrator getDomContentExtrator() {

		return this.domContentExtrator;
	}


	public TextExtractor clone() {

		boolean performContentExtraction = this.domContentExtrator != null;
		return new TextExtractorImpl(this.minLineLength, this.minDocumentLength, this.extractTitle,
				performContentExtraction);
	}
	
}
