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

class TransformableProxy extends ReferenceFrameProxy {
	private final double[] m_local = new double[16];
	private final java.nio.DoubleBuffer m_localBuffer = java.nio.DoubleBuffer.wrap(m_local);

	@Override
	protected void changed(final edu.cmu.cs.stage3.alice.scenegraph.Property property, final Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.Transformable.LOCAL_TRANSFORMATION_PROPERTY) {
			copy(m_local, (javax.vecmath.Matrix4d) value);
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Transformable.IS_FIRST_CLASS_PROPERTY) {
			// pass
		} else {
			super.changed(property, value);
		}
	}

	@Override
	public void render(final RenderContext context) {
		context.gl.getGL2().glPushMatrix();
		context.gl.getGL2().glMultMatrixd(m_localBuffer);
		super.render(context);
		context.gl.getGL2().glPopMatrix();
	}

	@Override
	public void pick(final PickContext context, final PickParameters pickParameters) {
		context.gl.getGL2().glPushMatrix();
		context.gl.getGL2().glMultMatrixd(m_localBuffer);
		super.pick(context, pickParameters);
		context.gl.getGL2().glPopMatrix();
	}

}
