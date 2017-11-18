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

package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Decorator;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;

public class BoundingSphereDecorator extends Decorator {
	private static final int RESOLUTION_THETA = 25;
	private static double[] sines = new double[RESOLUTION_THETA];
	private static double[] cosines = new double[RESOLUTION_THETA];
	static {
		final double dtheta = 2.0 * Math.PI / RESOLUTION_THETA;
		double theta = 0;
		for (int i = 0; i < RESOLUTION_THETA; i++) {
			sines[i] = Math.sin(theta);
			cosines[i] = Math.cos(theta);
			theta += dtheta;
		}
	}
	private edu.cmu.cs.stage3.alice.scenegraph.LineStrip m_sgLineStrip = null;
	private ReferenceFrame m_referenceFrame = null;

	@Override
	public ReferenceFrame getReferenceFrame() {
		return m_referenceFrame;
	}

	public void setReferenceFrame(final ReferenceFrame referenceFrame) {
		if (referenceFrame != m_referenceFrame) {
			m_referenceFrame = referenceFrame;
			markDirty();
			updateIfShowing();
		}
	}

	@Override
	public void internalRelease(final int pass) {
		switch (pass) {
		case 2:
			if (m_sgLineStrip != null) {
				m_sgLineStrip.release();
				m_sgLineStrip = null;
			}
			break;
		}
		super.internalRelease(pass);
	}

	@Override
	protected void update() {
		super.update();
		final edu.cmu.cs.stage3.math.Sphere sphere = m_referenceFrame.getBoundingSphere();
		if (sphere == null || sphere.getCenter() == null || Double.isNaN(sphere.getRadius())) {
			return;
		}
		boolean requiresVerticesToBeUpdated = isDirty();
		if (m_sgLineStrip == null) {
			m_sgLineStrip = new edu.cmu.cs.stage3.alice.scenegraph.LineStrip();
			m_sgVisual.setGeometry(m_sgLineStrip);
			m_sgLineStrip.setBonus(getReferenceFrame());
			requiresVerticesToBeUpdated = true;
		}
		if (requiresVerticesToBeUpdated) {
			final double r = sphere.getRadius();
			final double x = sphere.getCenter().x;
			final double y = sphere.getCenter().y;
			final double z = sphere.getCenter().z;
			final edu.cmu.cs.stage3.alice.scenegraph.Color xColor = edu.cmu.cs.stage3.alice.scenegraph.Color.RED;
			final edu.cmu.cs.stage3.alice.scenegraph.Color yColor = edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN;
			final edu.cmu.cs.stage3.alice.scenegraph.Color zColor = edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE;
			final edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] xVertices = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[RESOLUTION_THETA
					+ 1];
			final edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] yVertices = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[RESOLUTION_THETA
					+ 1];
			final edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] zVertices = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[RESOLUTION_THETA
					+ 1];
			for (int i = 0; i < RESOLUTION_THETA; i++) {
				xVertices[i] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d(
						new javax.vecmath.Point3d(x, y + cosines[i] * r, z + sines[i] * r), null, xColor, null, null);
				yVertices[i] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d(
						new javax.vecmath.Point3d(x + cosines[i] * r, y, z + sines[i] * r), null, yColor, null, null);
				zVertices[i] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d(
						new javax.vecmath.Point3d(x + cosines[i] * r, y + sines[i] * r, z), null, zColor, null, null);
			}
			xVertices[RESOLUTION_THETA] = xVertices[0];
			yVertices[RESOLUTION_THETA] = yVertices[0];
			zVertices[RESOLUTION_THETA] = zVertices[0];

			final edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[xVertices.length
					+ yVertices.length + zVertices.length];
			System.arraycopy(xVertices, 0, vertices, 0, xVertices.length);
			System.arraycopy(yVertices, 0, vertices, xVertices.length, yVertices.length);
			System.arraycopy(zVertices, 0, vertices, xVertices.length + yVertices.length, zVertices.length);
			m_sgLineStrip.setVertices(vertices);
		}
		setIsDirty(false);
	}
}