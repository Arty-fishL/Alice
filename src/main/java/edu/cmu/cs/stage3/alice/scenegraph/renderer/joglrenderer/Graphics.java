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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import java.awt.Canvas;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

public class Graphics extends java.awt.Graphics {
	private RenderContext m_renderContext;
	private java.awt.Color m_color = java.awt.Color.black;
	private static int SINE_AND_COSINE_CACHE_LENGTH = 8;
	private static double[] s_cosines = null;
	private static double[] s_sines = null;

	private static void cacheSinesAndCosinesIfNecessary() {
		if (s_cosines == null) {
			s_cosines = new double[SINE_AND_COSINE_CACHE_LENGTH];
			s_sines = new double[SINE_AND_COSINE_CACHE_LENGTH];
			double theta = 0;
			final double dtheta = Math.PI / 2.0 / s_cosines.length;
			for (int i = 0; i < s_cosines.length; i++) {
				s_cosines[i] = Math.cos(theta);
				s_sines[i] = Math.sin(theta);
				theta += dtheta;
			}
		}
	}

	protected Graphics(final RenderContext renderContext) {
		m_renderContext = renderContext;
		setColor(m_color);

		final int width = m_renderContext.getWidth();
		final int height = m_renderContext.getHeight();
		m_renderContext.gl.glMatrixMode( GL2.GL_PROJECTION );
		m_renderContext.gl.glLoadIdentity();
		m_renderContext.gl.glOrtho( 0, width-1, height-1, 0, -1, 1 );
		// m_renderContext.gl.glViewport( 0, 0, width, height );
		m_renderContext.gl.glMatrixMode( GL2.GL_MODELVIEW );
		m_renderContext.gl.glLoadIdentity();

		m_renderContext.gl.glDisable( GL2.GL_DEPTH_TEST );
		m_renderContext.gl.glDisable( GL2.GL_LIGHTING );
 		m_renderContext.gl.glDisable( GL2.GL_CULL_FACE );

		m_renderContext.setTextureMapProxy(null);
	}

	@Override
	public void dispose() {
		m_renderContext.gl.glFlush();
		m_renderContext = null;
	}

	@Override
	public java.awt.Graphics create() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void translate(final int x, final int y) {
		m_renderContext.gl.glTranslatef( x, y, 0 );
	}

	@Override
	public java.awt.Color getColor() {
		return m_color;
	}

	@Override
	public void setColor(final java.awt.Color c) {
		m_color = c;
		m_renderContext.gl.glColor3ub( (byte)m_color.getRed(), (byte)m_color.getGreen(), (byte)m_color.getBlue() );
	}

	@Override
	public void setPaintMode() {
		// todo
	}

	@Override
	public void setXORMode(final java.awt.Color c1) {
		// todo
	}

	private java.awt.Font m_font = new java.awt.Font(null, java.awt.Font.PLAIN, 12);

	@Override
	public java.awt.Font getFont() {
		return m_font;
	}

	@Override
	public void setFont(final java.awt.Font font) {
		m_font = font;
	}

	@Override
	public java.awt.FontMetrics getFontMetrics(final java.awt.Font f) {
		Canvas c = new Canvas();
		return c.getFontMetrics(f);
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
	public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		m_renderContext.gl.glBegin( GL2.GL_LINES );
		m_renderContext.gl.glVertex2i( x1, y1 );
		m_renderContext.gl.glVertex2i( x2, y2 );
		m_renderContext.gl.glEnd();
	}

	@Override
	public void fillRect(final int x, final int y, final int width, final int height) {
		m_renderContext.gl.glBegin( GL2.GL_POLYGON );
		m_renderContext.gl.glVertex2i( x, y );
		m_renderContext.gl.glVertex2i( x+width, y );
		m_renderContext.gl.glVertex2i( x+width, y+height );
		m_renderContext.gl.glVertex2i( x, y+height );
		m_renderContext.gl.glEnd();
	}

	@Override
	public void clearRect(final int x, final int y, final int width, final int height) {
		throw new RuntimeException("not implemented");
	}

	private void glQuarterOval(final double centerX, final double centerY, final double radiusX, final double radiusY,
			final int whichQuarter) {
		final int n = s_cosines.length;
		final int max = n - 1;
		for (int i = 0; i < n; i++) {
			double cos;
			double sin;
			switch (whichQuarter) {
			case 0:
				cos = s_cosines[i];
				sin = s_sines[i];
				break;
			case 1:
				cos = -s_cosines[max - i];
				sin = s_sines[max - i];
				break;
			case 2:
				cos = -s_cosines[i];
				sin = -s_sines[i];
				break;
			case 3:
				cos = s_cosines[max - i];
				sin = -s_sines[max - i];
				break;
			default:
				throw new IllegalArgumentException();
			}
			m_renderContext.gl.glVertex2d( centerX + cos * radiusX, centerY + sin * radiusY );
		}
	}

	private void glRoundRect(final int x, final int y, final int width, final int height, final int arcWidth,
			final int arcHeight) {
		cacheSinesAndCosinesIfNecessary();

		// int x0 = x;
		final int x1 = x + arcWidth;
		final int x2 = x + width - arcWidth;
		// int x3 = x+width;

		// int y0 = y;
		final int y1 = y + arcHeight;
		final int y2 = y + height - arcHeight;
		// int y3 = y+height;

		glQuarterOval(x1, y1, arcWidth, arcHeight, 2);
		// m_renderContext.gl.glVertex2d( x1, y0 );
		glQuarterOval(x2, y1, arcWidth, arcHeight, 3);
		// m_renderContext.gl.glVertex2d( x3, y1 );
		glQuarterOval(x2, y2, arcWidth, arcHeight, 0);
		// m_renderContext.gl.glVertex2d( x2, y3 );
		glQuarterOval(x1, y2, arcWidth, arcHeight, 1);
		// m_renderContext.gl.glVertex2d( x0, y2 );
	}

	@Override
	public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth,
			final int arcHeight) {
		m_renderContext.gl.glBegin( GL2.GL_LINE_LOOP );
		glRoundRect(x, y, width, height, arcWidth, arcHeight);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth,
			final int arcHeight) {
		m_renderContext.gl.glBegin( GL2.GL_TRIANGLE_FAN );
		glRoundRect(x, y, width, height, arcWidth, arcHeight);
		m_renderContext.gl.glEnd();
	}

	private void glOval(final int x, final int y, final int width, final int height) {
		final double radiusX = width * 0.5;
		final double radiusY = height * 0.5;
		final double centerX = x + radiusX;
		final double centerY = y + radiusY;
		cacheSinesAndCosinesIfNecessary();
		glQuarterOval(centerX, centerY, radiusX, radiusY, 0);
		glQuarterOval(centerX, centerY, radiusX, radiusY, 1);
		glQuarterOval(centerX, centerY, radiusX, radiusY, 2);
		glQuarterOval(centerX, centerY, radiusX, radiusY, 3);
	}

	@Override
	public void drawOval(final int x, final int y, final int width, final int height) {
		m_renderContext.gl.glBegin( GL2.GL_LINE_LOOP );
		glOval(x, y, width, height);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void fillOval(final int x, final int y, final int width, final int height) {
		m_renderContext.gl.glBegin(GL2.GL_TRIANGLE_FAN);
		glOval(x, y, width, height);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void drawArc(final int x, final int y, final int width, final int height, final int startAngle,
			final int arcAngle) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
			final int arcAngle) {
		throw new RuntimeException("not implemented");
	}

	private void glPoly(final int xPoints[], final int yPoints[], final int nPoints) {
		for (int i = 0; i < nPoints; i++) {
			m_renderContext.gl.glVertex2i(xPoints[i], yPoints[i]);
		}
	}

	@Override
	public void drawPolyline(final int xPoints[], final int yPoints[], final int nPoints) {
		m_renderContext.gl.glBegin(GL2.GL_LINE_STRIP);
		glPoly(xPoints, yPoints, nPoints);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void drawPolygon(final int xPoints[], final int yPoints[], final int nPoints) {
		m_renderContext.gl.glBegin(GL2.GL_LINE_LOOP);
		glPoly(xPoints, yPoints, nPoints);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void fillPolygon(final int xPoints[], final int yPoints[], final int nPoints) {
		m_renderContext.gl.glBegin(GL2.GL_POLYGON);
		glPoly(xPoints, yPoints, nPoints);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void drawString(final String str, final int x, final int y) {
		final float scale = m_font.getSize() / 170.0f;
		m_renderContext.gl.glPushMatrix();
		m_renderContext.gl.glTranslatef(x, y, 0);
		m_renderContext.gl.glScalef(scale, -scale, 1.0f);
		m_renderContext.glut.glutStrokeString(GLUT.STROKE_ROMAN, str);
		m_renderContext.gl.glPopMatrix();
	}

	@Override
	public void drawString(final java.text.AttributedCharacterIterator iterator, final int x, final int y) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void drawChars(final char[] data, final int offset, final int length, final int x, final int y) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void drawBytes(final byte[] data, final int offset, final int length, final int x, final int y) {
		throw new RuntimeException("not implemented");
	}

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
