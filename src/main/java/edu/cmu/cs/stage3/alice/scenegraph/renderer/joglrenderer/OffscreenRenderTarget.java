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

public class OffscreenRenderTarget extends RenderTarget
		implements edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget {
	private javax.media.opengl.GLJPanel m_glJPanel;

	OffscreenRenderTarget(final Renderer renderer) {
		super(renderer);
		// javax.media.opengl.GLCapabilities glCaps = new
		// javax.media.opengl.GLCapabilities();
		// glCaps.setHardwareAccelerated( true );
		// glCaps.setRedBits( 8 );
		// glCaps.setBlueBits( 8 );
		// glCaps.setGreenBits( 8 );
		// glCaps.setAlphaBits( 8 );
		// m_glJPanel =
		// javax.media.opengl.GLDrawableFactory.getFactory().createGLJPanel(
		// glCaps );
	}

	@Override
	public java.awt.Graphics getOffscreenGraphics() {
		return null;
	}

	@Override
	public java.awt.Dimension getSize(final java.awt.Dimension rv) {
		// todo
		return rv;
	}

	@Override
	public void setSize(final int width, final int height) {
		// todo
	}

	@Override
	public void setSize(final java.awt.Dimension size) {
		setSize(size.width, size.height);
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(final int x, final int y,
			final boolean isSubElementRequired, final boolean isOnlyFrontMostRequired) {
		return null;
	}
}
