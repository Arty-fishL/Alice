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

class OrthographicCameraProxy extends CameraProxy {
	private final double[] m_plane = new double[4];

	@Override
	protected java.awt.Rectangle getActualLetterboxedViewport(final int width, final int height) {
		// todo: handle NaN
		return new java.awt.Rectangle(0, 0, width, height);
	}

	@Override
	protected double[] getActualNearPlane(final double[] ret, final int width, final int height, final double near) {
		double minX = m_plane[0];
		double maxX = m_plane[2];
		double minY = m_plane[1];
		double maxY = m_plane[3];
		if (Double.isNaN(minX) || Double.isNaN(maxX)) {
			if (Double.isNaN(minY) || Double.isNaN(maxY)) {
				minY = -1;
				maxY = 1;
			}
			final double factor = width / (double) height;
			minX = factor * minY;
			maxX = factor * maxY;
		} else {
			if (Double.isNaN(minY) || Double.isNaN(maxY)) {
				final double factor = height / (double) width;
				minY = factor * minX;
				maxY = factor * maxY;
			}
		}
		ret[0] = minX;
		ret[1] = minY;
		ret[2] = maxX;
		ret[3] = maxY;
		return ret;
	}

	private final double[] reuse_actualNearPlane = new double[4];

	@Override
	protected void projection(final Context context, final int width, final int height, final float near,
			final float far) {
		getActualNearPlane(reuse_actualNearPlane, width, height);
		// System.err.println( reuse_actualNearPlane[ 0 ] + " " +
		// reuse_actualNearPlane[ 2 ] + " " + reuse_actualNearPlane[ 1 ] + " " +
		// reuse_actualNearPlane[ 3 ] );
		context.gl.getGL2().glOrtho(reuse_actualNearPlane[0], reuse_actualNearPlane[2], reuse_actualNearPlane[1],
				reuse_actualNearPlane[3], near, far);
	}

	@Override
	protected void changed(final edu.cmu.cs.stage3.alice.scenegraph.Property property, final Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera.PLANE_PROPERTY) {
			final double[] plane = (double[]) value;
			System.arraycopy(plane, 0, m_plane, 0, m_plane.length);
		} else {
			super.changed(property, value);
		}
	}
}
