package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.Vector;

public class Menu implements StencilObject, StencilPanelMessageListener, MouseEventListener, StencilFocusListener,
		ReadWriteListener {
	protected Vector shapes = new Vector();
	protected Vector stencilObjectPositionListeners = new Vector();
	protected StencilManager stencilManager = null;
	protected boolean isShowing = false;
	protected int level = 0;
	protected Point centerPoint;
	protected boolean writeEnabled = true;
	protected boolean isModified = true;
	protected Rectangle previousRect = null;
	protected Rectangle rect = null;

	private Shape upOption = null;
	private Shape downOption = null;
	private Shape rightOption = null;
	private Shape leftOption = null;
	int fontSize = 30;
	Font font = new Font("Arial", 0, fontSize);

	public Menu(final StencilManager stencilManager) {
		this.stencilManager = stencilManager;
	}

	protected void updateShapes(final Point p) {
		centerPoint = p;
		previousRect = getRectangle();
		if (level == 1) {
			shapes.removeAllElements();

			final Point upPosition = new Point(p.x - 40, p.y - 50);
			upOption = new RoundRectangle2D.Double(upPosition.x, upPosition.y, 65, 30, 10, 10);
			shapes.addElement(new ScreenShape(Color.blue, upOption, true, 0));
			Shape s = createWordShape("new", new Point(upPosition.x + 5, upPosition.y + 25));
			shapes.addElement(new ScreenShape(Color.white, s, true, 1));

			final Point rightPosition = new Point(p.x - 90, p.y - 15);
			rightOption = new RoundRectangle2D.Double(rightPosition.x, rightPosition.y, 65, 30, 10, 10);
			shapes.addElement(new ScreenShape(Color.blue, rightOption, true, 2));
			s = createWordShape("load", new Point(rightPosition.x + 5, rightPosition.y + 25));
			shapes.addElement(new ScreenShape(Color.white, s, true, 3));

			final Point leftPosition = new Point(p.x + 20, p.y - 15);
			leftOption = new RoundRectangle2D.Double(leftPosition.x, leftPosition.y, 70, 30, 10, 10);
			shapes.addElement(new ScreenShape(Color.blue, leftOption, true, 4));
			s = createWordShape("save", new Point(leftPosition.x + 5, leftPosition.y + 25));
			shapes.addElement(new ScreenShape(Color.white, s, true, 5));

			final Point downPosition = new Point(p.x - 40, p.y + 20);
			downOption = new RoundRectangle2D.Double(downPosition.x, downPosition.y, 65, 30, 10, 10);
			shapes.addElement(new ScreenShape(Color.blue, downOption, true, 6));
			s = createWordShape("lock", new Point(downPosition.x + 5, downPosition.y + 25));
			shapes.addElement(new ScreenShape(Color.white, s, true, 7));

			previousRect = rect;
			rect = new Rectangle(p.x - 90, p.y - 50, p.x + 90, p.y + 50);

		} else if (level == 0) {
			shapes.removeAllElements();

			if (writeEnabled) {

				final Point upPosition = new Point(p.x - 40, p.y - 50);
				upOption = new RoundRectangle2D.Double(upPosition.x, upPosition.y, 75, 30, 10, 10);
				shapes.addElement(new ScreenShape(Color.blue, upOption, true, 0));
				Shape s = createWordShape("note", new Point(upPosition.x + 5, upPosition.y + 25));
				shapes.addElement(new ScreenShape(Color.white, s, true, 1));

				final Point rightPosition = new Point(p.x - 90, p.y - 15);
				rightOption = new RoundRectangle2D.Double(rightPosition.x, rightPosition.y, 80, 30, 10, 10);
				shapes.addElement(new ScreenShape(Color.blue, rightOption, true, 2));
				s = createWordShape("frame", new Point(rightPosition.x + 5, rightPosition.y + 25));
				shapes.addElement(new ScreenShape(Color.white, s, true, 3));

				final Point leftPosition = new Point(p.x + 20, p.y - 15);
				leftOption = new RoundRectangle2D.Double(leftPosition.x, leftPosition.y, 75, 30, 10, 10);
				shapes.addElement(new ScreenShape(Color.blue, leftOption, true, 4));
				s = createWordShape("clear", new Point(leftPosition.x + 5, leftPosition.y + 25));
				shapes.addElement(new ScreenShape(Color.white, s, true, 5));

				final Point downPosition = new Point(p.x - 40, p.y + 20);
				downOption = new RoundRectangle2D.Double(downPosition.x, downPosition.y, 75, 30, 10, 10);
				shapes.addElement(new ScreenShape(Color.blue, downOption, true, 6));
				s = createWordShape("other", new Point(downPosition.x + 5, downPosition.y + 25));
				shapes.addElement(new ScreenShape(Color.white, s, true, 7));

				previousRect = rect;
				rect = new Rectangle(p.x - 90, p.y - 50, p.x + 95, p.y + 50);
			} else {
				final Point rightPosition = new Point(p.x - 90, p.y - 15);
				rightOption = new RoundRectangle2D.Double(rightPosition.x, rightPosition.y, 65, 30, 10, 10);
				shapes.addElement(new ScreenShape(Color.blue, rightOption, true, 0));
				Shape s = createWordShape("load", new Point(rightPosition.x + 5, rightPosition.y + 25));
				shapes.addElement(new ScreenShape(Color.white, s, true, 1));

				// should I really do this?
				final Point downPosition = new Point(p.x - 40, p.y + 20);
				downOption = new RoundRectangle2D.Double(downPosition.x, downPosition.y, 65, 30, 10, 10);
				shapes.addElement(new ScreenShape(Color.blue, downOption, true, 2));
				s = createWordShape("lock", new Point(downPosition.x + 5, downPosition.y + 25));
				shapes.addElement(new ScreenShape(Color.white, s, true, 3));

				previousRect = rect;
				rect = new Rectangle(p.x - 90, p.y - 15, p.x + 25, p.y + 50);
			}
		}
		isModified = true;
	}

	protected Shape createWordShape(final String word, final Point startPos) {
		final TextLayout wordLayout = new TextLayout(word, font, new FontRenderContext(null, false, false));
		final AffineTransform textAt = new AffineTransform();
		textAt.translate(startPos.x, startPos.y);
		final Shape s = wordLayout.getOutline(textAt);

		return s;
	}

	/* Stencil Object stuff */
	@Override
	public Vector getShapes() {
		if (isShowing) {
			return shapes;
		} else {
			return null;
		}
	}

	@Override
	public Rectangle getRectangle() {
		if (isShowing) {
			return rect;
		}
		return null;
	}

	@Override
	public Rectangle getPreviousRectangle() {
		final Rectangle pRect = previousRect;
		// previousRect = null;
		return pRect;
	}

	@Override
	public boolean isModified() {
		if (isModified) {
			isModified = false;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean intersectsRectangle(final Rectangle rect) {
		final Rectangle currentRect = getRectangle();
		if (currentRect != null) {
			return rect.intersects(currentRect);
		} else {
			return false;
		}
	}

	@Override
	public void addStencilObjectPositionListener(final StencilObjectPositionListener posListener) {
		stencilObjectPositionListeners.addElement(posListener);
	}

	@Override
	public void removeStencilObjectPositionListener(final StencilObjectPositionListener posListener) {
		stencilObjectPositionListeners.remove(posListener);
	}

	@Override
	public String getComponentID() {
		return null;
	}

	/* StencilPanelMessageListener Stuff */
	@Override
	public void messageReceived(final int messageID, final Object data) {
		if (writeEnabled) {
			isShowing = true;
			level = 0;
			updateShapes((Point) data);
			stencilManager.requestFocus(this);
		}
	}

	/* MouseListener stuff */
	@Override
	public boolean contains(final Point point) {
		if (isShowing) {
			if (writeEnabled) {
				return true;
			} else {
				return false;
				/*
				 * if (upOption.contains(point)) return true; else if
				 * (downOption.contains(point)) return true; else if
				 * (rightOption.contains(point)) return true; else if
				 * (leftOption.contains(point)) return true; else return false;
				 * } else { if (downOption.contains(point)) return true; else if
				 * (rightOption.contains(point)) return true; else return false;
				 * }
				 */
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean mousePressed(final MouseEvent e) {
		return false;
	}

	@Override
	public boolean mouseReleased(final MouseEvent e) {
		return false;
	}

	@Override
	public boolean mouseClicked(final MouseEvent e) {
		isShowing = false;
		if (writeEnabled) {
			if (level == 0) {
				previousRect = rect;
				if (upOption.contains(e.getPoint())) {
					stencilManager.createNewNote(centerPoint);
				} else if (downOption.contains(e.getPoint())) {
					level = 1;
					isShowing = true;
					updateShapes(e.getPoint());
				} else if (rightOption.contains(e.getPoint())) {
					stencilManager.createNewFrame(centerPoint);
				} else if (leftOption.contains(e.getPoint())) {
					stencilManager.removeAllObjects();
				}
				isModified = true;
			} else {
				if (upOption.contains(e.getPoint())) {
					stencilManager.insertNewStencil();
				} else if (downOption.contains(e.getPoint())) {
					stencilManager.toggleLock();
				} else if (rightOption.contains(e.getPoint())) {
					stencilManager.loadStencilsFile();
				} else if (leftOption.contains(e.getPoint())) {
					stencilManager.saveStencilsFile();
				}
				previousRect = rect;
				isModified = true;
			}
		} else {
			if (downOption.contains(e.getPoint())) {
				stencilManager.toggleLock();
			} else if (rightOption.contains(e.getPoint())) {
				stencilManager.loadStencilsFile();
			}
			previousRect = rect;
			isModified = true;
		}
		return true;
	}

	@Override
	public boolean mouseEntered(final MouseEvent e) {
		return false;
	}

	@Override
	public boolean mouseExited(final MouseEvent e) {
		return false;
	}

	@Override
	public boolean mouseMoved(final MouseEvent e) {
		return false;
	}

	@Override
	public boolean mouseDragged(final MouseEvent e) {
		return false;
	}

	/* stencilfocus stuff */
	@Override
	public void focusGained() {
		isShowing = true;
	}

	@Override
	public void focusLost() {
		isShowing = false;
		previousRect = rect;
		isModified = true;
	}

	/* readwrite listener */
	@Override
	public void setWriteEnabled(final boolean enabled) {
		writeEnabled = enabled;
	}
}