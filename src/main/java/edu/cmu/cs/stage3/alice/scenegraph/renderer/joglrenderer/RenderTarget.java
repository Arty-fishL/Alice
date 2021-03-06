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

import edu.cmu.cs.stage3.alice.scenegraph.Camera;

public abstract class RenderTarget extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderTarget {
	protected RenderTarget(final Renderer renderer) {
		super(renderer);
	}

	private RenderContext m_renderContextForGetOffscreenGraphics = null;

	protected void performClearAndRenderOffscreen(final RenderContext context) {
		commitAnyPendingChanges();

		// todo:
		// note: clear hasn't really happened
		onClear();

		final edu.cmu.cs.stage3.alice.scenegraph.Camera[] cameras = getCameras();
		for (final Camera camera : cameras) {
			final CameraProxy cameraProxyI = (CameraProxy) getProxyFor(camera);
			cameraProxyI.performClearAndRenderOffscreen(context);
		}
		try {
			m_renderContextForGetOffscreenGraphics = context;
			onRender();
		} finally {
			m_renderContextForGetOffscreenGraphics = null;
		}
		context.gl.glFlush();
	}

	private java.nio.IntBuffer m_pickBuffer;
	private java.nio.IntBuffer m_viewportBuffer;

	public PickInfo performPick(final PickContext context, final PickParameters pickParameters) {
		final int x = pickParameters.getX();
		final int y = pickParameters.getY();
		final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = getCameraAtPixel(x, y);
		// System.err.println( sgCamera );
		if (sgCamera != null) {

			final int CAPACITY = 256;
			if (m_pickBuffer == null) {
				m_pickBuffer = java.nio.ByteBuffer.allocateDirect(CAPACITY * 4).order(java.nio.ByteOrder.nativeOrder())
						.asIntBuffer();
			} else {
				m_pickBuffer.rewind();
			}
			context.gl.glSelectBuffer(CAPACITY, m_pickBuffer);

			context.gl.glRenderMode(GL2.GL_SELECT);
			context.gl.glInitNames();

			final int width = context.getWidth();
			final int height = context.getHeight();
			// todo: use actual viewport
			final CameraProxy cameraProxy = (CameraProxy) getProxyFor(sgCamera);
			final java.awt.Rectangle viewport = cameraProxy.getActualViewport(width, height);
			// unused ?? final int[] vp = { viewport.x, viewport.y, viewport.width, viewport.height };
			context.gl.glViewport(viewport.x, viewport.y, viewport.width, viewport.height);

			context.gl.glMatrixMode(GL2.GL_PROJECTION);
			context.gl.glLoadIdentity();

			if (m_viewportBuffer == null) {
				m_viewportBuffer = java.nio.IntBuffer.allocate(4);
			} else {
				m_viewportBuffer.rewind();
			}
			m_viewportBuffer.put(viewport.x);
			m_viewportBuffer.put(viewport.y);
			m_viewportBuffer.put(viewport.width);
			m_viewportBuffer.put(viewport.height);
			m_viewportBuffer.rewind();
			context.gl.glMatrixMode(GL2.GL_PROJECTION);
			context.gl.glLoadIdentity();
			context.glu.gluPickMatrix(x, height - y, 1, 1, m_viewportBuffer);

			cameraProxy.performPick(context, pickParameters);

			context.gl.glFlush();
		}
		return new PickInfo(context, m_pickBuffer, sgCamera);
	}

	public void commitAnyPendingChanges() {
		((Renderer) getRenderer()).commitAnyPendingChanges();
	}

	@Override
	public javax.vecmath.Matrix4d getProjectionMatrix(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera) {
		// todo
		return sgCamera.getProjection();
	}

	@Override
	public double[] getActualPlane(final edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera sgOrthographicCamera) {
		// todo
		return sgOrthographicCamera.getPlane();
	}

	@Override
	public double[] getActualPlane(final edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera sgPerspectiveCamera) {
		// todo
		return sgPerspectiveCamera.getPlane();
	}

	@Override
	public double getActualHorizontalViewingAngle(
			final edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera) {
		final java.awt.Dimension size = getSize();
		final SymmetricPerspectiveCameraProxy symmetricPerspectiveCameraProxy = (SymmetricPerspectiveCameraProxy) getProxyFor(
				sgSymmetricPerspectiveCamera);
		return symmetricPerspectiveCameraProxy.getActualHorizontalViewingAngle(size.width, size.height);
	}

	@Override
	public double getActualVerticalViewingAngle(
			final edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera) {
		final java.awt.Dimension size = getSize();
		final SymmetricPerspectiveCameraProxy symmetricPerspectiveCameraProxy = (SymmetricPerspectiveCameraProxy) getProxyFor(
				sgSymmetricPerspectiveCamera);
		return symmetricPerspectiveCameraProxy.getActualVerticalViewingAngle(size.width, size.height);
	}

	@Override
	public java.awt.Rectangle getActualViewport(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera) {
		final java.awt.Dimension size = getSize();
		final CameraProxy cameraProxy = (CameraProxy) getProxyFor(sgCamera);
		return cameraProxy.getActualViewport(size.width, size.height);
	}

	@Override
	public java.awt.Rectangle getViewport(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera) {
		final CameraProxy cameraProxy = (CameraProxy) getProxyFor(sgCamera);
		return cameraProxy.getViewport();
	}

	@Override
	public void setViewport(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera,
			final java.awt.Rectangle viewport) {
		final CameraProxy cameraProxy = (CameraProxy) getProxyFor(sgCamera);
		cameraProxy.setViewport(viewport);
	}

	@Override
	public boolean isLetterboxedAsOpposedToDistorted(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera) {
		final CameraProxy cameraProxy = (CameraProxy) getProxyFor(sgCamera);
		return cameraProxy.isLetterboxedAsOpposedToDistorted();
	}

	@Override
	public void setIsLetterboxedAsOpposedToDistorted(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera,
			final boolean isLetterboxedAsOpposedToDistorted) {
		final CameraProxy cameraProxy = (CameraProxy) getProxyFor(sgCamera);
		cameraProxy.setIsLetterboxedAsOpposedToDistorted(isLetterboxedAsOpposedToDistorted);
	}

	@Override
	public void clearAndRenderOffscreen() {
	}

	@Override
	public boolean rendersOnEdgeTrianglesAsLines(
			final edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthographicCamera) {
		// todo
		return false;
	}

	@Override
	public void setRendersOnEdgeTrianglesAsLines(
			final edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthographicCamera,
			final boolean rendersOnEdgeTrianglesAsLines) {
		// todo
		// if( rendersOnEdgeTrianglesAsLines ) {
		// throw new RuntimeException( "not supported" );
		// }
	}

	@Override
	public java.awt.Image getOffscreenImage() {
		return null;
	}

	@Override
	public java.awt.Graphics getOffscreenGraphics() {
		return new Graphics(m_renderContextForGetOffscreenGraphics);
	}

	@Override
	public java.awt.Graphics getGraphics(final edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap) {
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
	public void copyOffscreenImageToTextureMap(final edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap) {
		// todo
	}

	public void setSilhouetteThickness(final double silhouetteThickness) {
		// todo
	}

	public double getSilhouetteThickness() {
		// todo
		return 0;
	}

	/* Unused ??
	private double[] getActualNearPlane(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera, final int width,
			final int height) {
		final CameraProxy cameraProxy = (CameraProxy) getProxyFor(sgCamera);
		final double[] ret = new double[4];
		return cameraProxy.getActualNearPlane(ret, width, height);
	}
	*/

	private edu.cmu.cs.stage3.alice.scenegraph.Camera getCameraAtPixel(final int x, final int y) {
		final edu.cmu.cs.stage3.alice.scenegraph.Camera[] sgCameras = getCameras();
		for (int i = sgCameras.length - 1; i >= 0; i--) {
			final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCameraI = sgCameras[i];
			final java.awt.Rectangle viewportI = getActualViewport(sgCameraI);
			if (viewportI.contains(x, y)) {
				return sgCameraI;
			}
		}
		return null;
	}

	/* Unused ??
	private static boolean isNaN(final double[] array) {
		for (final double element : array) {
			if (Double.isNaN(element)) {
				return true;
			}
		}
		return false;
	}
	*/
	
	// public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick( int x,
	// int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired ) {
	// edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = getCameraAtPixel( x,
	// y );
	// if( sgCamera != null ) {
	// java.awt.Rectangle actualViewport = getActualViewport( sgCamera );
	// x -= actualViewport.getX();
	// y -= actualViewport.getY();
	// edu.cmu.cs.stage3.math.Ray ray = getRayAtPixel( sgCamera, x, y );
	// double nearClippingPlaneDistance =
	// sgCamera.getNearClippingPlaneDistance();
	// double farClippingPlaneDistance = sgCamera.getFarClippingPlaneDistance();
	// javax.vecmath.Vector3d position = ray.getPoint( nearClippingPlaneDistance
	// );
	// double[] nearPlane = getActualNearPlane( sgCamera, actualViewport.width,
	// actualViewport.height );
	// if( isNaN( nearPlane ) ) {
	// return null;
	// } else {
	// double planeWidth = nearPlane[ 2 ] - nearPlane[ 0 ];
	// double planeHeight = nearPlane[ 3 ] - nearPlane[ 1 ];
	// double pixelHalfWidth = ( planeWidth / actualViewport.getWidth() ) / 2;
	// double pixelHalfHeight = ( planeHeight / actualViewport.getHeight() ) /
	// 2;
	// double planeMinX = position.x - pixelHalfWidth;
	// double planeMinY = position.y - pixelHalfHeight;
	// double planeMaxX = position.x + pixelHalfWidth;
	// double planeMaxY = position.y + pixelHalfHeight;
	// return getRenderer().pick( sgCamera, ray.getDirection(), planeMinX,
	// planeMinY, planeMaxX, planeMaxY, nearClippingPlaneDistance,
	// farClippingPlaneDistance, isSubElementRequired, isOnlyFrontMostRequired
	// );
	// }
	// } else {
	// return null;
	// }
	// }
}