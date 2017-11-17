/*
 * Created on Jun 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.bubble;

import java.awt.Graphics;

/**
 * @author caitlin
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TitleBubble extends Bubble {

	private java.awt.Rectangle actualViewport = null;

	/*
	 * (non-Javadoc)
	 *
	 * @see edu.cmu.cs.stage3.alice.core.bubble.Bubble#paintBackground(java.awt.
	 * Graphics )
	 */

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
			final java.awt.FontMetrics fontMetrics = java.awt.Toolkit.getDefaultToolkit().getFontMetrics(font);

			final double charCnt = actualViewport.getWidth() / fontMetrics.charWidth('W');
			setCharactersPerLine((int) charCnt * 2);
		}

		super.calculateBounds(rt);

		final java.awt.geom.Rectangle2D totalBound = getTotalBound();

		setPixelOffset(new java.awt.Point(
				(int) actualViewport.getX() + (int) (actualViewport.getWidth() / 2 - totalBound.getWidth() / 2),
				(int) actualViewport.getY() + (int) (actualViewport.getHeight() / 2 - totalBound.getHeight() / 2)));
	}

	@Override
	protected void paintBackground(final Graphics g) {
		final java.awt.Point origin = getOrigin();

		final int x = actualViewport.x;
		final int y = actualViewport.y;

		final int width = actualViewport.width;
		final int height = actualViewport.height;

		g.setColor(getBackgroundColor());
		g.fillRoundRect(x, y, width, height, 5, 5);
	}

	@Override
	protected void paint(final java.awt.Graphics g) {
		if (isShowing()) {
			paintBackground(g);
			if (getText() != null) {
				if (getFont() != null) {
					g.setFont(getFont());
				}
				g.setColor(getForegroundColor());
				if (m_subTexts.size() > 0) {
					final SubText subText0 = (SubText) m_subTexts.elementAt(0);
					final int offsetX = m_pixelOffset.x;
					final int offsetY = m_pixelOffset.y - (int) subText0.getBound().getY();
					for (int i = 0; i < m_subTexts.size(); i++) {
						final SubText subTextI = (SubText) m_subTexts.elementAt(i);
						final java.awt.geom.Rectangle2D boundI = subTextI.getBound();
						final int x = actualViewport.x + actualViewport.width / 2
								- (int) (boundI.getWidth() * 0.85 / 2);
						final int y = (int) (actualViewport.y + offsetY + boundI.getY());
						g.drawString(subTextI.getText(), x, y);
					}

				}

				// g.drawString( m_text, m_rect.x + PAD, m_rect.y + PAD + 16 );
			}
		}
	}

}
