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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import java.awt.Rectangle;

import edu.cmu.cs.stage3.alice.scenegraph.Camera;

public abstract class RenderTarget extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderTarget {
	private final RenderTargetAdapter m_adapter;

	protected RenderTargetAdapter getAdapter() {
		return m_adapter;
	}

	private final java.util.Dictionary<Camera, Rectangle> m_cameraViewportMap = new java.util.Hashtable<Camera, Rectangle>();

	RenderTarget(final Renderer renderer) {
		super(renderer);
		m_adapter = renderer.createRenderTargetAdapter(this);
	}

	@Override
	public void release() {
		super.release();
		m_adapter.release();
	}

	@Override
	public java.awt.Graphics getOffscreenGraphics() {
		return m_adapter.getOffscreenGraphics();
	}

	@Override
	public java.awt.Graphics getGraphics(final edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap) {
		return m_adapter.getGraphics((TextureMapProxy) getProxyFor(textureMap));
	}

	@Override
	public javax.vecmath.Matrix4d getProjectionMatrix(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera) {
		return m_adapter.getProjectionMatrix((CameraProxy) getProxyFor(sgCamera));
	}

	@Override
	public double[] getActualPlane(final edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera sgOrthographicCamera) {
		return m_adapter.getActualPlane((OrthographicCameraProxy) getProxyFor(sgOrthographicCamera));
	}

	@Override
	public double[] getActualPlane(final edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera sgPerspectiveCamera) {
		return m_adapter.getActualPlane((PerspectiveCameraProxy) getProxyFor(sgPerspectiveCamera));
	}

	@Override
	public double getActualHorizontalViewingAngle(
			final edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera) {
		return m_adapter.getActualHorizontalViewingAngle(
				(SymmetricPerspectiveCameraProxy) getProxyFor(sgSymmetricPerspectiveCamera));
	}

	@Override
	public double getActualVerticalViewingAngle(
			final edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera) {
		return m_adapter.getActualVerticalViewingAngle(
				(SymmetricPerspectiveCameraProxy) getProxyFor(sgSymmetricPerspectiveCamera));
	}

	@Override
	public java.awt.Rectangle getActualViewport(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera) {
		return m_adapter.getActualViewport((CameraProxy) getProxyFor(sgCamera));
	}

	@Override
	public java.awt.Rectangle getViewport(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera) {
		return m_cameraViewportMap.get(sgCamera);
	}

	@Override
	public void setViewport(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera,
			final java.awt.Rectangle viewport) {
		final CameraProxy cameraProxy = (CameraProxy) getProxyFor(sgCamera);
		if (viewport == null) {
			m_adapter.onViewportChange(cameraProxy, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
			m_cameraViewportMap.remove(sgCamera);
		} else {
			m_adapter.onViewportChange(cameraProxy, viewport.x, viewport.y, viewport.width, viewport.height);
			m_cameraViewportMap.put(sgCamera, viewport);
		}
	}

	@Override
	public boolean rendersOnEdgeTrianglesAsLines(
			final edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera sgOrthographicCamera) {
		return m_adapter.rendersOnEdgeTrianglesAsLines((OrthographicCameraProxy) getProxyFor(sgOrthographicCamera));
	}

	@Override
	public void setRendersOnEdgeTrianglesAsLines(
			final edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera sgOrthographicCamera,
			final boolean rendersOnEdgeTrianglesAsLines) {
		m_adapter.setRendersOnEdgeTrianglesAsLines((OrthographicCameraProxy) getProxyFor(sgOrthographicCamera),
				rendersOnEdgeTrianglesAsLines);
	}

	@Override
	public boolean isLetterboxedAsOpposedToDistorted(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera) {
		return m_adapter.isLetterboxedAsOpposedToDistorted((CameraProxy) getProxyFor(sgCamera));
	}

	@Override
	public void setIsLetterboxedAsOpposedToDistorted(final edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera,
			final boolean isLetterboxedAsOpposedToDistorted) {
		m_adapter.setIsLetterboxedAsOpposedToDistorted((CameraProxy) getProxyFor(sgCamera),
				isLetterboxedAsOpposedToDistorted);
	}

	public void commitAnyPendingChanges() {
		((Renderer) getRenderer()).commitAnyPendingChanges();
	}

	@Override
	public void clearAndRenderOffscreen() {
		commitAnyPendingChanges();
		final edu.cmu.cs.stage3.alice.scenegraph.Camera[] sgCameras = getCameras();
		if (sgCameras.length > 0) {
			for (int i = 0; i < sgCameras.length; i++) {
				final CameraProxy cameraProxy = (CameraProxy) getProxyFor(sgCameras[i]);
				m_adapter.clear(cameraProxy, i == 0);
				onClear();
				ComponentProxy.updateAbsoluteTransformationChanges();
				GeometryProxy.updateBoundChanges();
				m_adapter.render(cameraProxy);
				onRender();
			}
		}
	}

	private CameraProxy getCameraProxy(final int x, final int y) {
		if (x >= 0 && y >= 0 && x < m_adapter.getWidth() && y < m_adapter.getHeight()) {
			final edu.cmu.cs.stage3.alice.scenegraph.Camera[] sgCameras = getCameras();
			if (sgCameras != null) {
				for (final Camera sgCamera : sgCameras) {
					final CameraProxy cameraProxy = (CameraProxy) getProxyFor(sgCamera);
					final java.awt.Rectangle viewport = m_adapter.getActualViewport(cameraProxy);
					if (viewport != null) {
						if (viewport.contains(x, y)) {
							return cameraProxy;
						}
					} else {
						return cameraProxy;
					}
				}
			}
		}
		return null;
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(final int x, final int y,
			final boolean isSubElementRequired, final boolean isOnlyFrontMostRequired) {
		commitAnyPendingChanges();
		final CameraProxy cameraProxy = getCameraProxy(x, y);
		if (cameraProxy != null) {
			final int[] atVisual = { 0 };
			final boolean[] atIsFrontFacing = { true };
			final int[] atSubElement = { -1 };
			final double[] atZ = { Double.NaN };

			m_adapter.pick(cameraProxy, x, y, isSubElementRequired, isOnlyFrontMostRequired, atVisual, atIsFrontFacing,
					atSubElement, atZ);

			edu.cmu.cs.stage3.alice.scenegraph.Visual[] sgVisuals = null;
			edu.cmu.cs.stage3.alice.scenegraph.Geometry[] sgGeometries = null;
			int[] subElements = null;
			boolean[] isFrontFacings = null;
			final VisualProxy visualProxy = VisualProxy.map(atVisual[0]);
			if (visualProxy != null) {
				sgVisuals = new edu.cmu.cs.stage3.alice.scenegraph.Visual[1];
				sgVisuals[0] = (edu.cmu.cs.stage3.alice.scenegraph.Visual) visualProxy.getSceneGraphElement();
				sgGeometries = new edu.cmu.cs.stage3.alice.scenegraph.Geometry[1];
				sgGeometries[0] = sgVisuals[0].getGeometry();
				subElements = new int[1];
				subElements[0] = atSubElement[0];
				isFrontFacings = new boolean[1];
				isFrontFacings[0] = atIsFrontFacing[0];
			}
			return new PickInfo(this, (edu.cmu.cs.stage3.alice.scenegraph.Camera) cameraProxy.getSceneGraphElement(), x,
					y, sgVisuals, isFrontFacings, sgGeometries, subElements);
		} else {
			return null;
		}
	}

	/*
	 * private int fixEndian( int i ) { return ((i & 0x000000ff) << 24) | ((i &
	 * 0x0000ff00) << 8) | ((i & 0x00ff0000) >>> 8) | ((i & 0xff000000) >>> 24);
	 * }
	 */
	@Override
	public java.awt.Image getOffscreenImage() {
		final int width = m_adapter.getWidth();
		final int height = m_adapter.getHeight();
		final int pitch = m_adapter.getPitch();
		final int bitCount = m_adapter.getBitCount();
		final int redBitMask = m_adapter.getRedBitMask();
		final int greenBitMask = m_adapter.getGreenBitMask();
		final int blueBitMask = m_adapter.getBlueBitMask();
		final int alphaBitMask = m_adapter.getAlphaBitMask();

		final int[] pixels = new int[width * height];
		m_adapter.getPixels(0, 0, width, height, pitch, bitCount, redBitMask, greenBitMask, blueBitMask, alphaBitMask,
				pixels);
		final java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(width, height,
				java.awt.image.BufferedImage.TYPE_INT_RGB);
		bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
		return bufferedImage;
		/*
		 * java.awt.image.BufferedImage bufferedImage; switch( rgbBitCount ) {
		 * case 32: case 24: int[] pixels4 = new int[ width * height ];
		 * m_adapter.getPixels( 0, 0, width, height, pitch, rgbBitCount,
		 * redBitMask, greenBitMask, blueBitMask, alphaBitMask, pixels4 );
		 * bufferedImage = new java.awt.image.BufferedImage( width, height,
		 * java.awt.image.BufferedImage.TYPE_INT_RGB ); bufferedImage.setRGB( 0,
		 * 0, width, height, pixels4, 0, width ); break; case 16: short[]
		 * pixels2 = new short[ width * height ];
		 *
		 * m_adapter.getPixels( 0, 0, width, height, pitch, rgbBitCount,
		 * redBitMask, greenBitMask, blueBitMask, alphaBitMask, pixels2 );
		 *
		 * System.err.println( "red " + Integer.toBinaryString( redBitMask ) );
		 * System.err.println( "green " + Integer.toBinaryString( greenBitMask )
		 * ); System.err.println( "blue " + Integer.toBinaryString( blueBitMask
		 * ) ); System.err.println( "alpha " + Integer.toBinaryString(
		 * alphaBitMask ) );
		 *
		 * bufferedImage = new java.awt.image.BufferedImage( width, height,
		 * java.awt.image.BufferedImage.TYPE_USHORT_565_RGB );
		 * bufferedImage.setRGB( 0, 0, width, height, pixels2, 0, width );
		 *
		 * break; } return bufferedImage;
		 */

		// pitch = width * 4;
		// java.awt.image.DirectColorModel colorModel = new
		// java.awt.image.DirectColorModel( rgbBitCount, redBitMask,
		// greenBitMask, blueBitMask, alphaBitMask );
		// java.awt.image.ImageProducer imageProducer = new
		// java.awt.image.MemoryImageSource( width, height, colorModel, pixels,
		// 0, pitch );
		// return java.awt.Toolkit.getDefaultToolkit().createImage(
		// imageProducer );
	}

	@Override
	public java.awt.Image getZBufferImage() {
		final int width = m_adapter.getWidth();
		final int height = m_adapter.getHeight();
		final int zPitch = m_adapter.getZBufferPitch();
		final int zBitCount = m_adapter.getZBufferBitCount();
		final int[] zPixels = new int[width * height];
		m_adapter.getZBufferPixels(0, 0, width, height, zPitch, zBitCount, zPixels);
		final java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(width, height,
				java.awt.image.BufferedImage.TYPE_INT_RGB);
		bufferedImage.setRGB(0, 0, width, height, zPixels, 0, width);
		return bufferedImage;
	}

	@Override
	public java.awt.Image getImage(final edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap) {
		final TextureMapProxy textureMapProxy = (TextureMapProxy) getProxyFor(textureMap);
		final int width = m_adapter.getWidth(textureMapProxy);
		final int height = m_adapter.getHeight(textureMapProxy);
		final int pitch = m_adapter.getPitch(textureMapProxy);
		final int bitCount = m_adapter.getBitCount(textureMapProxy);
		final int redBitMask = m_adapter.getRedBitMask(textureMapProxy);
		final int greenBitMask = m_adapter.getGreenBitMask(textureMapProxy);
		final int blueBitMask = m_adapter.getBlueBitMask(textureMapProxy);
		final int alphaBitMask = m_adapter.getAlphaBitMask(textureMapProxy);

		final int[] pixels = new int[width * height];
		m_adapter.getPixels(textureMapProxy, 0, 0, width, height, pitch, bitCount, redBitMask, greenBitMask,
				blueBitMask, alphaBitMask, pixels);

		final java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(width, height,
				java.awt.image.BufferedImage.TYPE_INT_RGB);
		bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
		return bufferedImage;
	}

	@Override
	public void copyOffscreenImageToTextureMap(final edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap) {
		final TextureMapProxy textureMapProxy = (TextureMapProxy) getProxyFor(textureMap);
		m_adapter.blt(textureMapProxy);
		markDirty();
	}

	public void setSilhouetteThickness(final double silhouetteThickness) {
		m_adapter.setSilhouetteThickness(silhouetteThickness);
	}

	public double getSilhouetteThickness() {
		return m_adapter.getSilhouetteThickness();
	}
}