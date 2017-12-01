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

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.gl2.GLUgl2;
import com.jogamp.opengl.util.gl2.GLUT;

abstract class Context implements GLEventListener {
	public GL2 gl;
	public GL gl2;
	public GLUgl2 glu;
	public GLUT glut;

	protected int m_width;
	protected int m_height;

	@Override
	public void init(final GLAutoDrawable drawable) {
		// drawable.setGL( new DebugGL( drawable.getGL() ) );
	}

	@Override
	public void display(final GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		glu = new GLUgl2();
		glut = new GLUT();
	}

	@Override
	public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width,
			final int height) {
		// System.err.println( "reshape: " + drawable );
		m_width = width;
		m_height = height;
	}

	/*@Override
	public void displayChanged(final GLAutoDrawable drawable, final boolean modeChanged,
			final boolean deviceChanged) {
		// System.err.println( "displayChanged: " + drawable + " " + modeChanged
		// + " " + deviceChanged );
	}*/

	public int getWidth() {
		return m_width;
	}

	public int getHeight() {
		return m_height;
	}
}
