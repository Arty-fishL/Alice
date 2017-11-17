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

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;

public class RenderTarget extends Element {
	/** @deprecated */
	@Deprecated
	public final ObjectProperty requiredCapabilities = new ObjectProperty(this, "requiredCapabilities", null,
			Long.class);
	private edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget m_onscreenRenderTarget = null;
	private java.awt.Component m_awtComponent;
	private final java.util.Vector m_cameras = new java.util.Vector();
	private Camera[] m_cameraArray = null;
	private edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory m_renderTargetFactory;
	private static java.util.Dictionary s_componentMap = new java.util.Hashtable();
	private static java.util.Dictionary s_eventMap = new java.util.Hashtable();

	public RenderTarget() {
		requiredCapabilities.deprecate();
	}

	@Override
	protected void finalize() throws java.lang.Throwable {
		if (m_awtComponent != null) {
			s_componentMap.remove(m_awtComponent);
		}
		super.finalize();
	}

	@Override
	protected void internalRelease(final int pass) {
		switch (pass) {
		case 1:
			final java.util.Enumeration enum0 = m_cameras.elements();
			while (enum0.hasMoreElements()) {
				final Camera camera = (Camera) enum0.nextElement();
				if (m_onscreenRenderTarget != null) {
					m_onscreenRenderTarget.removeCamera(camera.getSceneGraphCamera());
				}
			}
			break;
		case 2:
			if (m_onscreenRenderTarget != null) {
				if (m_renderTargetFactory != null) {
					m_renderTargetFactory.releaseOnscreenRenderTarget(m_onscreenRenderTarget);
				}
				m_onscreenRenderTarget = null;
			}
			break;
		}
		super.internalRelease(pass);
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(final int x, final int y,
			final boolean isSubElementRequired, final boolean isOnlyFrontMostRequired) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.pick(x, y, isSubElementRequired, isOnlyFrontMostRequired);
		} else {
			throw new NullPointerException("internal m_onscreenRenderTarget is null");
		}
	}

	public static edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(
			final java.awt.event.MouseEvent mouseEvent) {
		edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo = (edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo) s_eventMap
				.get(mouseEvent);
		if (pickInfo == null) {
			final RenderTarget renderTarget = (RenderTarget) s_componentMap.get(mouseEvent.getComponent());
			pickInfo = renderTarget.pick(mouseEvent.getX(), mouseEvent.getY(), false, true);
			if (pickInfo != null) {
				s_eventMap.put(mouseEvent, pickInfo);
			}
		}
		return pickInfo;
	}

	public void commit(final edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory renderTargetFactory) {
		m_renderTargetFactory = renderTargetFactory;
		m_onscreenRenderTarget = renderTargetFactory.createOnscreenRenderTarget();
		m_awtComponent = m_onscreenRenderTarget.getAWTComponent();
		s_componentMap.put(m_awtComponent, this);
		final java.util.Enumeration enum0 = m_cameras.elements();
		while (enum0.hasMoreElements()) {
			final Camera camera = (Camera) enum0.nextElement();
			m_onscreenRenderTarget.addCamera(camera.getSceneGraphCamera());
			m_onscreenRenderTarget.setIsLetterboxedAsOpposedToDistorted(camera.getSceneGraphCamera(),
					camera.isLetterboxedAsOpposedToDistorted.booleanValue());
		}
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.Renderer getRenderer() {
		return m_onscreenRenderTarget.getRenderer();
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget getInternal() {
		return m_onscreenRenderTarget;
	}

	public void addCamera(final Camera camera) {
		if (m_cameras.contains(camera)) {
		} else {
			m_cameras.addElement(camera);
			m_cameraArray = null;
			if (m_onscreenRenderTarget != null) {
				m_onscreenRenderTarget.addCamera(camera.getSceneGraphCamera());
			}
		}
	}

	public void removeCamera(final Camera camera) {
		m_cameras.removeElement(camera);
		m_cameraArray = null;
		if (m_onscreenRenderTarget != null) {
			m_onscreenRenderTarget.removeCamera(camera.getSceneGraphCamera());
		}
	}

	public Camera[] getCameras() {
		if (m_cameraArray == null) {
			m_cameraArray = new Camera[m_cameras.size()];
			m_cameras.copyInto(m_cameraArray);
		}
		return m_cameraArray;
	}

	public double[] getActualPlane(final edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera orthographicCamera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getActualPlane(orthographicCamera.getSceneGraphOrthographicCamera());
		} else {
			return null;
		}
	}

	public double[] getActualPlane(final edu.cmu.cs.stage3.alice.core.camera.PerspectiveCamera perspectiveCamera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getActualPlane(perspectiveCamera.getSceneGraphPerspectiveCamera());
		} else {
			return null;
		}
	}

	public double getActualHorizontalViewingAngle(
			final edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera symmetricPerspectiveCamera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getActualVerticalViewingAngle(
					symmetricPerspectiveCamera.getSceneGraphSymmetricPerspectiveCamera());
		} else {
			return Double.NaN;
		}
	}

	public double getActualVerticalViewingAngle(
			final edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera symmetricPerspectiveCamera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getActualVerticalViewingAngle(
					symmetricPerspectiveCamera.getSceneGraphSymmetricPerspectiveCamera());
		} else {
			return Double.NaN;
		}
	}

	public javax.vecmath.Matrix4d getProjectionMatrix(final Camera camera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getProjectionMatrix(camera.getSceneGraphCamera());
		} else {
			return null;
		}
	}

	public java.awt.Rectangle getActualViewport(final Camera camera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getActualViewport(camera.getSceneGraphCamera());
		} else {
			return null;
		}
	}

	public java.awt.Rectangle getViewport(final Camera camera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getViewport(camera.getSceneGraphCamera());
		} else {
			return null;
		}
	}

	public void setViewport(final Camera camera, final java.awt.Rectangle rectangle) {
		if (m_onscreenRenderTarget != null) {
			m_onscreenRenderTarget.setViewport(camera.getSceneGraphCamera(), rectangle);
		}
	}

	public edu.cmu.cs.stage3.math.Vector4 project(final edu.cmu.cs.stage3.math.Vector3 point, final Camera camera) {
		if (m_onscreenRenderTarget != null) {
			final javax.vecmath.Matrix4d projection = m_onscreenRenderTarget
					.getProjectionMatrix(camera.getSceneGraphCamera());
			final edu.cmu.cs.stage3.math.Vector4 xyzw = new edu.cmu.cs.stage3.math.Vector4(point.x, point.y, point.z,
					1);
			return edu.cmu.cs.stage3.math.Vector4.multiply(xyzw, projection);
		} else {
			return null;
		}
	}

	public java.awt.Component getAWTComponent() {
		return m_awtComponent;
	}

	public java.awt.Image getOffscreenImage() {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getOffscreenImage();
		} else {
			return null;
		}
	}

	public java.awt.Graphics getOffscreenGraphics() {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getOffscreenGraphics();
		} else {
			return null;
		}
	}

	public void addRenderTargetListener(
			final edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener renderTargetListener) {
		if (m_onscreenRenderTarget != null) {
			m_onscreenRenderTarget.addRenderTargetListener(renderTargetListener);
		} else {
			throw new NullPointerException("internal m_onscreenRenderTarget is null");
		}
	}

	public void removeRenderTargetListener(
			final edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener renderTargetListener) {
		if (m_onscreenRenderTarget != null) {
			m_onscreenRenderTarget.removeRenderTargetListener(renderTargetListener);
		} else {
			throw new NullPointerException("internal m_onscreenRenderTarget is null");
		}
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener[] getRenderTargetListeners() {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getRenderTargetListeners();
		} else {
			throw new NullPointerException("internal m_onscreenRenderTarget is null");
		}
	}

	public void addKeyListener(final java.awt.event.KeyListener keyListener) {
		if (m_awtComponent != null) {
			m_awtComponent.addKeyListener(keyListener);
		} else {
			throw new NullPointerException("internal m_awtComponent is null");
		}
	}

	public void removeKeyListener(final java.awt.event.KeyListener keyListener) {
		if (m_awtComponent != null) {
			m_awtComponent.removeKeyListener(keyListener);
		} else {
			throw new NullPointerException("internal m_awtComponent is null");
		}
	}

	public void addMouseListener(final java.awt.event.MouseListener mouseListener) {
		if (m_awtComponent != null) {
			m_awtComponent.addMouseListener(mouseListener);
		} else {
			throw new NullPointerException("internal m_awtComponent is null");
		}
	}

	public void removeMouseListener(final java.awt.event.MouseListener mouseListener) {
		if (m_awtComponent != null) {
			m_awtComponent.removeMouseListener(mouseListener);
		} else {
			throw new NullPointerException("internal m_awtComponent is null");
		}
	}

	public void addMouseMotionListener(final java.awt.event.MouseMotionListener mouseMotionListener) {
		if (m_awtComponent != null) {
			m_awtComponent.addMouseMotionListener(mouseMotionListener);
		} else {
			throw new NullPointerException("internal m_awtComponent is null");
		}
	}

	public void removeMouseMotionListener(final java.awt.event.MouseMotionListener mouseMotionListener) {
		if (m_awtComponent != null) {
			m_awtComponent.removeMouseMotionListener(mouseMotionListener);
		} else {
			throw new NullPointerException("internal m_awtComponent is null");
		}
	}

	@Override
	protected void started(final World world, final double time) {
		super.started(world, time);
		m_onscreenRenderTarget.addRenderTargetListener(world.getBubbleManager());
		final java.awt.Component awtComponent = m_onscreenRenderTarget.getAWTComponent();
		if (awtComponent != null) {
			awtComponent.requestFocus();
		}
	}

	@Override
	protected void stopped(final World world, final double time) {
		super.stopped(world, time);
		m_onscreenRenderTarget.removeRenderTargetListener(world.getBubbleManager());
	}
}
