package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Vector;

import javax.swing.JPanel;

public class StencilPanel extends JPanel implements MouseEventListener, ReadWriteListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -1506713176778750356L;
	protected StencilManager stencilManager = null;
	protected boolean isDrawing = false;
	protected Vector holes = new Vector();
	protected Vector filledShapes = new Vector();
	protected Vector unfilledShapes = new Vector();
	protected Vector stencilPanelMessageListeners = new Vector();
	protected boolean writeEnabled = true;
	protected Vector holeRegions = new Vector();
	protected Vector clearRegions = new Vector();
	protected boolean nothingToDraw = true;
	protected Color bgColor = new Color(13, 99, 161, 150);// (0, 150,255,100);

	// texture paint stuff
	protected java.awt.Image img = null;
	protected java.awt.image.BufferedImage bImg = null;
	protected java.awt.image.BufferedImage screenBuffer = null;
	protected java.awt.MediaTracker mTracker = null;
	protected java.awt.TexturePaint tPaint = null;

	public StencilPanel(final StencilManager stencilManager) {
		this.stencilManager = stencilManager;
		final java.awt.Toolkit toolKit = getToolkit();
		img = toolKit.getImage("blue2.png");
		mTracker = new java.awt.MediaTracker(this);
		mTracker.addImage(img, 1);
	}

	public void setIsDrawing(final boolean isDrawing) {
		this.isDrawing = isDrawing;
		setVisible(isDrawing);
	}

	public boolean getIsDrawing() {
		return isDrawing;
	}

	public void redraw() {
		// categorizeShapes(shapes, clearRegions);
		this.repaint();
	}

	protected void categorizeShapes(final Vector shapes, final Vector allRegions) {
		// if (nothingToDraw) {
		holes = new Vector();
		filledShapes = new Vector();
		unfilledShapes = new Vector();

		holeRegions = new Vector();
		clearRegions = new Vector();
		// }

		for (int i = 0; i < shapes.size(); i++) {
			final ScreenShape currentShape = (ScreenShape) shapes.elementAt(i);
			if (currentShape.getColor() == null) { // this is a hole
				holes.addElement(currentShape);
				if (currentShape.getIndex() == 0) {
					holeRegions.addElement(currentShape.getShape().getBounds());
				}
			} else {
				if (currentShape.getIndex() == 0 && allRegions.size() > 0) {
					final java.awt.Rectangle r = (java.awt.Rectangle) allRegions.remove(0);
					clearRegions.addElement(r);
				}
				if (currentShape.getIsFilled() == true) {
					filledShapes.addElement(currentShape);
				} else {
					unfilledShapes.addElement(currentShape);
				}
			}

			// this.nothingToDraw = false;
		}

		if (shapes.size() == 0 && allRegions.size() > 0) {
			clearRegions = allRegions;
		}

	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);

		if (screenBuffer == null || screenBuffer.getWidth() != getWidth() || screenBuffer.getHeight() != getHeight()) {
			screenBuffer = new java.awt.image.BufferedImage(getWidth(), getHeight(),
					java.awt.image.BufferedImage.TYPE_INT_ARGB);

			final Graphics graphics = screenBuffer.getGraphics();
			graphics.setColor(bgColor);
			graphics.fillRect(0, 0, getWidth(), getHeight());
		}

		final Vector allShapes = stencilManager.getUpdateShapes();
		final Vector allRegions = stencilManager.getClearRegions();
		categorizeShapes(allShapes, allRegions);

		final Graphics2D bg2 = (Graphics2D) screenBuffer.getGraphics();
		bg2.setBackground(new Color(255, 255, 255, 0));
		final Graphics2D g2 = (Graphics2D) g;

		/*
		 * if ((mTracker.checkID(1, true)) && (bImg == null) ){ bImg = new
		 * java.awt.image.BufferedImage(img.getHeight(null), img.getWidth(null),
		 * java.awt.image.BufferedImage.TYPE_4BYTE_ABGR);
		 * bImg.getGraphics().drawImage(this.img, 0,0, null); tPaint = new
		 * java.awt.TexturePaint(bImg, new
		 * java.awt.geom.Rectangle2D.Double(0,0,img.getWidth(null),
		 * img.getHeight(null))); }
		 */

		if (isDrawing) {
			Object oldAntialiasing = null;
			if (g instanceof java.awt.Graphics2D) {
				oldAntialiasing = ((java.awt.Graphics2D) g).getRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING);
				((java.awt.Graphics2D) g).addRenderingHints(new java.awt.RenderingHints(
						java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON));
				bg2.addRenderingHints(new java.awt.RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING,
						java.awt.RenderingHints.VALUE_ANTIALIAS_ON));
			}

			Area outside = null;
			final Color outsideColor = bgColor;
			if (tPaint != null) {
				bg2.setPaint(tPaint);
			} else {
				bg2.setColor(outsideColor);
			}

			// clear out all the appropriate regions and refill them
			for (int i = 0; i < clearRegions.size(); i++) {
				final java.awt.Rectangle r = (java.awt.Rectangle) clearRegions.elementAt(i);
				bg2.clearRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
				bg2.fillRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
			}

			// now make the holes
			for (int i = 0; i < holeRegions.size(); i++) {
				final java.awt.Rectangle r = (java.awt.Rectangle) holeRegions.elementAt(i);
				bg2.clearRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
			}

			// subtract out the holes
			for (int i = 0; i < holes.size(); i++) {
				outside = new Area((java.awt.Rectangle) holeRegions.elementAt(i));
				final ScreenShape scrShape = (ScreenShape) holes.elementAt(i);

				final RoundRectangle2D.Double r = (RoundRectangle2D.Double) scrShape.getShape();
				final RoundRectangle2D.Double upperShadow = new RoundRectangle2D.Double(r.x - 2, r.y - 2, r.width + 2,
						r.height + 2, r.arcwidth, r.archeight);
				final RoundRectangle2D.Double lowerShadow = new RoundRectangle2D.Double(r.x, r.y, r.width + 2,
						r.height + 2, r.arcwidth, r.archeight);

				final Area upperBorder = new Area(upperShadow);
				final Area currentArea = new Area(r);
				upperBorder.subtract(currentArea);
				bg2.setColor(Color.darkGray);
				bg2.fill(upperBorder);

				final Area lowerBorder = new Area(lowerShadow);
				lowerBorder.subtract(currentArea);
				bg2.setColor(Color.lightGray);
				bg2.fill(lowerBorder);

				outside.subtract(currentArea);

				bg2.fill(outside);
			}

			bg2.setColor(outsideColor);

			for (int i = 0; i < filledShapes.size(); i++) {
				final ScreenShape currentShape = (ScreenShape) filledShapes.elementAt(i);
				if (currentShape.shape instanceof Line2D) {
					bg2.setColor(currentShape.color);
					bg2.draw(currentShape.shape);
				} else {
					bg2.setColor(currentShape.color);
					bg2.fill(currentShape.shape);
				}
			}

			for (int i = 0; i < unfilledShapes.size(); i++) {
				final ScreenShape currentShape = (ScreenShape) unfilledShapes.elementAt(i);
				bg2.setColor(currentShape.color);
				bg2.draw(currentShape.shape);

			}

			g2.drawImage(screenBuffer, null, null);

			if (g instanceof java.awt.Graphics2D) {
				((java.awt.Graphics2D) g).addRenderingHints(
						new java.awt.RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, oldAntialiasing));
			}
		}

		// this.nothingToDraw = true;
	}

	// StencilPanelMessageListener stuff
	public void addMessageListener(final StencilPanelMessageListener spmListener) {
		stencilPanelMessageListeners.addElement(spmListener);
	}

	public void removeMessageListener(final StencilPanelMessageListener spmListener) {
		stencilPanelMessageListeners.remove(spmListener);
	}

	public void removeAllMessageListeners() {
		stencilPanelMessageListeners.removeAllElements();
	}

	protected void broadCastMessage(final int messageID, final Object data) {
		for (int i = 0; i < stencilPanelMessageListeners.size(); i++) {
			final StencilPanelMessageListener spmListener = (StencilPanelMessageListener) stencilPanelMessageListeners
					.elementAt(i);
			spmListener.messageReceived(messageID, data);
		}
	}

	// MouseEventListener interface

	@Override
	public boolean contains(final Point point) {
		return true;
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
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			broadCastMessage(StencilPanelMessageListener.SHOW_MENU, e.getPoint());
			// Vector shapes = stencilManager.getShapesToDraw();
			// this.redraw(shapes);
			return true;
		} else if (e.getClickCount() == 2) {
			if (writeEnabled) {
				stencilManager.createNewHole(e.getPoint());
				return true;
			}
		}
		return false;
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

	/* readwrite listener */
	@Override
	public void setWriteEnabled(final boolean enabled) {
		writeEnabled = enabled;
	}

	protected class HoleData {
		public Hole hole;
		public java.awt.Rectangle clearRect;
	}
}