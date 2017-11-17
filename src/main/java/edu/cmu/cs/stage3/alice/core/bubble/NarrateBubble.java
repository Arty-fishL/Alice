/*
 * Created on May 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.bubble;

import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * @author caitlin
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NarrateBubble extends Bubble {

	private final boolean m_displayTopOfScreen = false;
	private java.awt.Rectangle actualViewport = null;

	public NarrateBubble() {
		setCharactersPerLine(60);
	}

	@Override
	public void calculateOrigin(final edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget rt) {

	}

	// narrate doesn't really have an origin, this is more to calculate a
	// reasonable set of offsets

	@Override
	public void calculateBounds(final edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget rt) {

		final edu.cmu.cs.stage3.alice.scenegraph.Camera[] sgCameras = rt.getCameras();
		if (sgCameras.length > 0) {
			final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = sgCameras[0];
			actualViewport = rt.getActualViewport(sgCamera);

			final java.awt.Font font = getFont();
			final FontMetrics fontMetrics = java.awt.Toolkit.getDefaultToolkit().getFontMetrics(font);
			// System.out.println(fontMetrics.charWidth('W'));

			final double charCnt = actualViewport.getWidth() / fontMetrics.charWidth('W');
			// System.out.println("charCnt: " + charCnt + " " + font.getSize());
			setCharactersPerLine((int) charCnt * 2);
		}

		super.calculateBounds(rt);

		final java.awt.geom.Rectangle2D totalBound = getTotalBound();

		if (sgCameras.length > 0) {
			final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = sgCameras[0];
			actualViewport = rt.getActualViewport(sgCamera);

			final double x = actualViewport.getX() + actualViewport.getWidth() / 2.0 - totalBound.getWidth() / 2.0;
			double y = 0;
			if (m_displayTopOfScreen) {
				y = PAD_Y;
				if (totalBound.getY() < 0) {
					y = -1.0 * totalBound.getY() + PAD_Y;
				}
			} else {
				y = actualViewport.y + actualViewport.getHeight() - totalBound.getHeight() + PAD_Y;
			}

			setPixelOffset(new java.awt.Point((int) x, (int) y));
		}
	}

	@Override
	protected void paintBackground(final Graphics g) {
		final java.awt.geom.Rectangle2D totalBound = getTotalBound();
		final java.awt.Point origin = getOrigin();
		final java.awt.Point pixelOffset = getPixelOffset();

		final int x = actualViewport.x;
		final int y = (int) (totalBound.getY() + pixelOffset.y - PAD_Y);

		final int width = actualViewport.width;
		final int height = (int) totalBound.getHeight() + PAD_Y + PAD_Y;

		g.setColor(getBackgroundColor());
		g.fillRoundRect(x, y, width, height, 5, 5);
		g.setColor(java.awt.Color.black);
		g.drawRoundRect(x, y, width, height, 5, 5);
	}

}
