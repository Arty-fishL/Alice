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

class ProjectionCameraProxy extends CameraProxy {
	private final double[] m_projection = new double[16];
	private final java.nio.DoubleBuffer m_projectionBuffer = java.nio.DoubleBuffer.wrap(m_projection);

	@Override
	protected java.awt.Rectangle getActualLetterboxedViewport(final int width, final int height) {
		// don't know
		return new java.awt.Rectangle(0, 0, width, height);
	}

	@Override
	protected double[] getActualNearPlane(final double[] ret, final int width, final int height, final double near) {
		// don't know
		ret[0] = Double.NaN;
		ret[1] = Double.NaN;
		ret[2] = Double.NaN;
		ret[3] = Double.NaN;
		return ret;
	}

	@Override
	protected void projection(final Context context, final int width, final int height, final float near,
			final float far) {
		context.gl.getGL2().glLoadMatrixd(m_projectionBuffer);
	}

	@Override
	protected void changed(final edu.cmu.cs.stage3.alice.scenegraph.Property property, final Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.ProjectionCamera.PROJECTION_PROPERTY) {
			copy(m_projection, (javax.vecmath.Matrix4d) value);
		} else {
			super.changed(property, value);
		}
	}
}
