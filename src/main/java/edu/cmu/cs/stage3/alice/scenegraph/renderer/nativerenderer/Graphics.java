/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

public abstract class Graphics extends java.awt.Graphics {
	private final int m_nativeInstance = 0;

	protected abstract void createNativeInstance(RenderTargetAdapter renderTargetAdapter);

	protected abstract void createNativeInstance(RenderTargetAdapter renderTargetAdapter,
			TextureMapProxy textureMapProxy);

	protected abstract void releaseNativeInstance();

	private final RenderTarget m_renderTarget;
	private final TextureMapProxy m_textureMapProxy;

	protected Graphics(final RenderTarget renderTarget) {
		createNativeInstance(renderTarget.getAdapter());
		m_renderTarget = renderTarget;
		m_textureMapProxy = null;
	}

	protected Graphics(final RenderTarget renderTarget, final TextureMapProxy textureMapProxy) {
		createNativeInstance(renderTarget.getAdapter(), textureMapProxy);
		m_renderTarget = renderTarget;
		m_textureMapProxy = textureMapProxy;
	}

	@Override
	public void dispose() {
		releaseNativeInstance();
		if (m_textureMapProxy != null) {
			if (m_renderTarget instanceof OnscreenRenderTarget) {
				((OnscreenRenderTarget) m_renderTarget).getAWTComponent().repaint();
			}
			m_textureMapProxy.setRenderTargetWithLatestImage(m_renderTarget);
		}
	}

	@Override
	public java.awt.Graphics create() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public abstract void translate(int x, int y);

	@Override
	public abstract java.awt.Color getColor();

	@Override
	public abstract void setColor(java.awt.Color c);

	@Override
	public abstract void setPaintMode();

	@Override
	public abstract void setXORMode(java.awt.Color c1);

	private java.awt.Font m_font = null;

	protected abstract void setFont(String family, String name, boolean isBold, boolean isItalic, int size);

	@Override
	public java.awt.Font getFont() {
		return m_font;
	}

	@Override
	public void setFont(final java.awt.Font font) {
		m_font = font;
		final int style = m_font.getStyle();
		final boolean isBold = (style & java.awt.Font.BOLD) != 0;
		final boolean isItalic = (style & java.awt.Font.ITALIC) != 0;
		setFont(m_font.getFamily(), m_font.getName(), isBold, isItalic, m_font.getSize());
	}

	@Override
	public java.awt.FontMetrics getFontMetrics(final java.awt.Font f) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public java.awt.Rectangle getClipBounds() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void clipRect(final int x, final int y, final int width, final int height) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void setClip(final int x, final int y, final int width, final int height) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public java.awt.Shape getClip() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void setClip(final java.awt.Shape clip) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public abstract void copyArea(int x, int y, int width, int height, int dx, int dy);

	@Override
	public abstract void drawLine(int x1, int y1, int x2, int y2);

	@Override
	public abstract void fillRect(int x, int y, int width, int height);

	@Override
	public abstract void clearRect(int x, int y, int width, int height);

	@Override
	public abstract void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

	@Override
	public abstract void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

	@Override
	public abstract void drawOval(int x, int y, int width, int height);

	@Override
	public abstract void fillOval(int x, int y, int width, int height);

	@Override
	public abstract void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	@Override
	public abstract void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	@Override
	public abstract void drawPolyline(int xPoints[], int yPoints[], int nPoints);

	@Override
	public abstract void drawPolygon(int xPoints[], int yPoints[], int nPoints);

	@Override
	public abstract void fillPolygon(int xPoints[], int yPoints[], int nPoints);

	@Override
	public abstract void drawString(String str, int x, int y);

	@Override
	public void drawString(final java.text.AttributedCharacterIterator iterator, final int x, final int y) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public abstract void drawChars(char[] data, int offset, int length, int x, int y);

	@Override
	public abstract void drawBytes(byte[] data, int offset, int length, int x, int y);

	@Override
	public boolean drawImage(final java.awt.Image img, final int x, final int y,
			final java.awt.image.ImageObserver observer) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean drawImage(final java.awt.Image img, final int x, final int y, final int width, final int height,
			final java.awt.image.ImageObserver observer) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean drawImage(final java.awt.Image img, final int x, final int y, final java.awt.Color bgcolor,
			final java.awt.image.ImageObserver observer) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean drawImage(final java.awt.Image img, final int x, final int y, final int width, final int height,
			final java.awt.Color bgcolor, final java.awt.image.ImageObserver observer) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean drawImage(final java.awt.Image img, final int dx1, final int dy1, final int dx2, final int dy2,
			final int sx1, final int sy1, final int sx2, final int sy2, final java.awt.image.ImageObserver observer) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean drawImage(final java.awt.Image img, final int dx1, final int dy1, final int dx2, final int dy2,
			final int sx1, final int sy1, final int sx2, final int sy2, final java.awt.Color bgcolor,
			final java.awt.image.ImageObserver observer) {
		throw new RuntimeException("not implemented");
	}
}
