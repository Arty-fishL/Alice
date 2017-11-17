package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.util.Vector;

public class Paragraph {
	protected int textWidth = 100;
	protected Point textOrigin = new Point(0, 0);

	protected int cursorIndex = 0;
	// protected StringBuffer text = new StringBuffer("");
	protected Shape caretShape = null;
	protected float lineHeight = 0;
	protected double lineAscent = 0;

	protected FontRenderContext frc = null;
	// protected LineBreakMeasurer measurer;
	// protected AttributedCharacterIterator iterator;

	protected StringBuffer currentText = new StringBuffer("");
	protected Vector buffers = new Vector();
	protected Vector colors = new Vector();
	protected Vector iterators = new Vector();
	protected Vector measurers = new Vector();

	private final int fontSize = 14;
	// private java.awt.Font font = new Font("Arial",

	public Paragraph() {
		frc = new FontRenderContext(null, false, false);
		buffers.addElement(currentText);
		colors.addElement(Color.blue);
		regenerateLineBreakMeasurer();
	}

	public Paragraph(final int textWidth, final Point textOrigin) {
		this();
		this.textWidth = textWidth;
		this.textOrigin = textOrigin;
	}

	// set config values
	public void setTextWidth(final int textWidth) {
		this.textWidth = textWidth;
	}

	public void setTextOrigin(final Point textOrigin) {
		this.textOrigin = textOrigin;
	}

	// setText would probably take a vector of textStrings
	/*
	 * public void setText(String textString) { this.text = new
	 * StringBuffer(textString); regenerateLineBreakMeasurer(); }
	 */

	public void addText(final String textString, final Color textColor) {
		buffers.addElement(new StringBuffer(textString));
		colors.addElement(textColor);
		currentText = (StringBuffer) buffers.elementAt(0);
		cursorIndex = 0;
		regenerateLineBreakMeasurer();
	}

	public void clearText() {
		buffers.removeAllElements();
		colors.removeAllElements();
		regenerateLineBreakMeasurer();
	}

	public void createNewLine() {
		StringBuffer lastBuffer = new StringBuffer();
		StringBuffer newBuffer = new StringBuffer();

		if (cursorIndex == currentText.length()) {
			lastBuffer = currentText;
			newBuffer = new StringBuffer(" ");
		} else {
			lastBuffer = new StringBuffer(currentText.substring(0, cursorIndex));
			newBuffer = new StringBuffer(currentText.substring(cursorIndex));
		}
		// add the buffers in
		final int index = buffers.indexOf(currentText);
		if (index != -1) {
			buffers.setElementAt(lastBuffer, index);
			currentText = newBuffer;
			cursorIndex = 0;
			buffers.insertElementAt(newBuffer, index + 1);

			final Color textColor = (Color) colors.elementAt(index);
			colors.insertElementAt(textColor, index + 1);
		}
		regenerateLineBreakMeasurer();
	}

	public void insertChar(final char c) {
		if (cursorIndex == currentText.length()) {
			currentText.append(c);
		} else {
			currentText.insert(cursorIndex, c);
		}
		cursorIndex += 1;
		regenerateLineBreakMeasurer();
	}

	// same - need a current textString
	public void deleteChar() {
		final int lineIndex = buffers.indexOf(currentText);
		if (cursorIndex > 0) {
			currentText.deleteCharAt(cursorIndex - 1);
			cursorIndex -= 1;
			if (cursorIndex >= currentText.length()) {
				cursorIndex = currentText.length();
			} else if (cursorIndex < 0) {
				cursorIndex = 0;
			}
			regenerateLineBreakMeasurer();
		} else if (cursorIndex == 0 && lineIndex > 0) {
			final StringBuffer previousLine = (StringBuffer) buffers.elementAt(lineIndex - 1);
			cursorIndex = previousLine.length();
			previousLine.append(currentText.toString());
			final int index = buffers.indexOf(currentText);
			buffers.remove(currentText);
			colors.removeElementAt(index);
			currentText = previousLine;
			regenerateLineBreakMeasurer();
		}
	}

	// vector of text strings
	// vector of iterators
	// vector of measurers
	protected void regenerateLineBreakMeasurer() {
		iterators = new Vector();
		measurers = new Vector();
		for (int i = 0; i < buffers.size(); i++) {
			final StringBuffer currBuffer = (StringBuffer) buffers.elementAt(i);
			final java.text.AttributedString attrString = new java.text.AttributedString(currBuffer.toString());
			if (currBuffer.toString().length() > 0) {
				attrString.addAttribute(java.awt.font.TextAttribute.SIZE, new Float(fontSize));
				final java.awt.Font font = new java.awt.Font("Comic Sans MS", 1, fontSize);
				attrString.addAttribute(java.awt.font.TextAttribute.FONT, font);
			}
			// iterator = attrString.getIterator();
			final AttributedCharacterIterator itr = attrString.getIterator();
			iterators.addElement(itr);

			if (itr.getEndIndex() != 0) {
				// measurer = new LineBreakMeasurer(iterator, frc);
				measurers.addElement(new LineBreakMeasurer(itr, frc));
			} else {
				measurers.addElement(null);
			}
		}
	}

	public Shape getCaretShape() {
		return caretShape;
	}

	public int getStartY() {
		return (int) textOrigin.getY() + (int) lineAscent;
	}

	public int getNextY(final int currentY) {
		return currentY + (int) lineHeight;
	}

	// this has to loop over the iterators, measurers
	public Vector getShapes() {
		final Vector shapes = new Vector();
		double line = 0;
		for (int i = 0; i < iterators.size(); i++) {
			final AttributedCharacterIterator itr = (AttributedCharacterIterator) iterators.elementAt(i);
			final StringBuffer bfr = (StringBuffer) buffers.elementAt(i); // PROBLEM
			final Color color = (Color) colors.elementAt(i);
			final int paragraphStart = itr.getBeginIndex();
			final int paragraphEnd = itr.getEndIndex();

			final LineBreakMeasurer msr = (LineBreakMeasurer) measurers.elementAt(i);
			if (msr != null) {
				msr.setPosition(paragraphStart);
				while (msr.getPosition() < paragraphEnd) {
					final int begin = msr.getPosition();
					final TextLayout layout = msr.nextLayout(textWidth);
					final int end = begin + layout.getCharacterCount();

					// update the lineheight if that's necessary
					lineHeight = layout.getAscent() + layout.getDescent() + layout.getLeading();
					lineAscent = layout.getAscent();

					// check to see if this is where the caret should be drawn
					// and draw it
					if (currentText == bfr && cursorIndex >= begin && cursorIndex <= end) {
						final Shape[] carets = layout.getCaretShapes(cursorIndex - begin);
						final AffineTransform at = new AffineTransform();
						at.translate(textOrigin.getX(), getStartY() + line * lineHeight);

						caretShape = at.createTransformedShape(carets[0]);
					}

					final AffineTransform at = new AffineTransform();
					at.translate(textOrigin.getX(), getStartY() + line * lineHeight);
					final Shape s = layout.getOutline(at);
					final ScreenShape sShape = new ScreenShape(color, s, true, 5);
					shapes.addElement(sShape);
					line += 1;
				}
				line = line + 0.5;
			}
		}
		return shapes;
	}

	public Vector getText() {
		final Vector strings = new Vector();
		for (int i = 0; i < buffers.size(); i++) {
			final String st = ((StringBuffer) buffers.elementAt(i)).toString();
			if (st.length() > 0) {
				strings.addElement(st);
			}
		}
		return strings;
	}

	public Vector getColors() {
		return colors;
	}

	public void updateCaretPosition(final Point clickPos) {
		final float clickX = (float) (clickPos.getX() - textOrigin.getX());
		final float clickY = (float) (clickPos.getY() - textOrigin.getY());

		float bottomBoundary = 0;
		float topBoundary = 0;

		for (int i = 0; i < iterators.size(); i++) {
			final AttributedCharacterIterator iterator = (AttributedCharacterIterator) iterators.elementAt(i);
			final LineBreakMeasurer measurer = (LineBreakMeasurer) measurers.elementAt(i);

			final int paragraphStart = iterator.getBeginIndex();
			final int paragraphEnd = iterator.getEndIndex();

			if (measurer != null) {
				measurer.setPosition(paragraphStart);
				// float bottomBoundary = 0;
				// float topBoundary = 0;
			}

			cursorIndex = 0;
			currentText = null;

			// Get lines from lineMeasurer until the entire
			// paragraph has been displayed.
			if (measurer != null) {
				while (measurer.getPosition() < paragraphEnd) {

					// Retrieve next layout.
					final TextLayout layout = measurer.nextLayout(textWidth);
					bottomBoundary = topBoundary + lineHeight;

					if (clickY > topBoundary && clickY < bottomBoundary) {
						// Get the character position of the mouse click.
						final TextHitInfo currentHit = layout.hitTestChar(clickX, clickY);
						if (currentHit != null) {
							currentText = (StringBuffer) buffers.elementAt(i);
							cursorIndex += currentHit.getInsertionIndex();
							return;
						}
					} else {
						cursorIndex = measurer.getPosition();
					}
					topBoundary = bottomBoundary;
				}
				topBoundary = topBoundary + lineHeight / 2;
			}
		}
		if (currentText == null) {
			currentText = (StringBuffer) buffers.elementAt(0);
			cursorIndex = 0;
		}
	}
}