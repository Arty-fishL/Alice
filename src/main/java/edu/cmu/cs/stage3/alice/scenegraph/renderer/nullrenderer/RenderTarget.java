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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera;
import edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera;
import edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.scenegraph.TextureMap;

public abstract class RenderTarget extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractRenderTarget {
	RenderTarget(final Renderer renderer) {
		super(renderer);
	}

	@Override
	public void markDirty() {
	}

	public boolean updateIsRequired() {
		return false;
	}

	@Override
	public javax.vecmath.Matrix4d getProjectionMatrix(final Camera camera) {
		return null;
	}

	@Override
	public double[] getActualPlane(final OrthographicCamera sgOrthographicCamera) {
		return null;
	}

	@Override
	public double[] getActualPlane(final PerspectiveCamera sgPerspectiveCamera) {
		return null;
	}

	@Override
	public double getActualHorizontalViewingAngle(final SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera) {
		return Double.NaN;
	}

	@Override
	public double getActualVerticalViewingAngle(final SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera) {
		return Double.NaN;
	}

	@Override
	public java.awt.Rectangle getActualViewport(final Camera sgCamera) {
		return new java.awt.Rectangle(getSize());
	}

	@Override
	public java.awt.Rectangle getViewport(final Camera sgCamera) {
		return null;
	}

	@Override
	public void setViewport(final Camera sgCamera, final java.awt.Rectangle viewport) {
	}

	@Override
	public boolean isLetterboxedAsOpposedToDistorted(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera) {
		return true;
	}

	@Override
	public void setIsLetterboxedAsOpposedToDistorted(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera,
			final boolean isLetterboxedAsOpposedToDistorted) {
	}

	@Override
	public java.awt.Image getOffscreenImage() {
		return null;
	}

	@Override
	public java.awt.Image getZBufferImage() {
		return null;
	}

	@Override
	public java.awt.Image getImage(final edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap) {
		return null;
	}

	@Override
	public java.awt.Graphics getGraphics(final TextureMap textureMap) {
		return null;
	}

	@Override
	public void copyOffscreenImageToTextureMap(final TextureMap textureMap) {
	}

	@Override
	public void clearAndRenderOffscreen() {
	}

	@Override
	public boolean rendersOnEdgeTrianglesAsLines(
			final edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthographicCamera) {
		return false;
	}

	@Override
	public void setRendersOnEdgeTrianglesAsLines(
			final edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthographicCamera,
			final boolean rendersOnEdgeTrianglesAsLines) {
	}

	@Override
	public java.awt.Graphics getOffscreenGraphics() {
		return null;
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(final int x, final int y,
			final boolean isSubElementRequired, final boolean isOnlyFrontMostRequired) {
		return null;
	}

	private double m_silhouetteThickness = 0.0;

	public void setSilhouetteThickness(final double silhouetteThickness) {
		m_silhouetteThickness = silhouetteThickness;
	}

	public double getSilhouetteThickness() {
		return m_silhouetteThickness;
	}
}
