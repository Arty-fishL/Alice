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

import com.jogamp.opengl.GL2;

public class PickInfo implements edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo {
	private edu.cmu.cs.stage3.alice.scenegraph.Component m_source;
	// Unused ?? private javax.vecmath.Matrix4d m_projection;
	private edu.cmu.cs.stage3.alice.scenegraph.Visual[] m_visuals;
	private boolean[] m_isFrontFacings;
	private edu.cmu.cs.stage3.alice.scenegraph.Geometry[] m_geometries;
	private int[] m_subElements;
	private double[] m_zs;

	public PickInfo(final PickContext context, final java.nio.IntBuffer pickBuffer,
			final edu.cmu.cs.stage3.alice.scenegraph.Component source) {
		m_source = source;
		int length = context.gl.getGL2().glRenderMode(GL2.GL_RENDER);
		if (length < 0) {
			// todo: throw exception?
			length = 0;
		}
		pickBuffer.rewind();

		class PickItem {
			edu.cmu.cs.stage3.alice.scenegraph.Visual visual;
			boolean isFrontFacing;
			edu.cmu.cs.stage3.alice.scenegraph.Geometry geometry;
			int subElement;
			int zFront;

			PickItem(final PickContext context, final java.nio.IntBuffer pickBuffer, final int offset) {
				final int nameCount = pickBuffer.get(offset + 0);
				zFront = pickBuffer.get(offset + 1);
				/* Unused ?? final int zBack = */ pickBuffer.get(offset + 2);
				if (nameCount == 3) {
					final VisualProxy visualProxy = context.getPickVisualProxyForName(pickBuffer.get(offset + 3));
					if (visualProxy != null) {
						visual = visualProxy.getSceneGraphVisual();
						isFrontFacing = pickBuffer.get(offset + 4) == 1;
						geometry = visual.getGeometry();
						subElement = pickBuffer.get(offset + 5);
					}
				}
			}
		}

		final PickItem[] pickItems = new PickItem[length];
		int offset = 0;
		for (int i = 0; i < length; i++) {
			pickItems[i] = new PickItem(context, pickBuffer, offset);
			offset += 6;
		}

		java.util.Arrays.sort(pickItems, new java.util.Comparator<PickItem>() {
			@Override
			public int compare(final PickItem pi1, final PickItem pi2) {
				return pi1.zFront - pi2.zFront;
			}

			@Override
			public boolean equals(final Object obj) {
				return super.equals(obj);
			}
		});

		m_visuals = new edu.cmu.cs.stage3.alice.scenegraph.Visual[length];
		m_isFrontFacings = new boolean[length];
		m_geometries = new edu.cmu.cs.stage3.alice.scenegraph.Geometry[length];
		m_subElements = new int[length];
		m_zs = new double[length];
		for (int i = 0; i < length; i++) {
			m_visuals[i] = pickItems[i].visual;
			m_isFrontFacings[i] = pickItems[i].isFrontFacing;
			m_geometries[i] = pickItems[i].geometry;
			m_subElements[i] = pickItems[i].subElement;
			m_zs[i] = (float) pickItems[i].zFront / 0x7fffffff;
		}
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.Component getSource() {
		return m_source;
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.Visual[] getVisuals() {
		return m_visuals;
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.Geometry[] getGeometries() {
		return m_geometries;
	}

	@Override
	public boolean[] isFrontFacings() {
		return m_isFrontFacings;
	}

	@Override
	public int[] getSubElements() {
		return m_subElements;
	}

	@Override
	public double[] getZs() {
		return m_zs;
	}

	@Override
	public int getCount() {
		if (m_visuals != null) {
			return m_visuals.length;
		} else {
			return 0;
		}
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.Visual getVisualAt(final int index) {
		return m_visuals[index];
	}

	@Override
	public boolean isFrontFacingAt(final int index) {
		return m_isFrontFacings[index];
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.Geometry getGeometryAt(final int index) {
		return m_geometries[index];
	}

	@Override
	public int getSubElementAt(final int index) {
		return m_subElements[index];
	}

	@Override
	public double getZAt(final int index) {
		return m_zs[index];
	}

	@Override
	public javax.vecmath.Vector3d getLocalPositionAt(final int index) {
		return null;
	}
}
