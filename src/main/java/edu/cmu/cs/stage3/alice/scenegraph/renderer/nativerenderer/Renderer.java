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

public abstract class Renderer extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer {
	static {
		// java.awt.Frame f = new java.awt.Frame();
		// f.show();
		// f = null;
		try {
			System.loadLibrary("jawt");
		} catch (final UnsatisfiedLinkError ule) {
			// pass
		}
	}
	private final int m_nativeInstance = 0;

	protected abstract void createNativeInstance();

	protected abstract void releaseNativeInstance();

	protected abstract void pick(ComponentProxy componentProxy, double x, double y, double z, double planeMinX,
			double planeMinY, double planeMaxX, double planeMaxY, double nearClippingPlaneDistance,
			double farClippingPlaneDistance, boolean isSubElementRequired, boolean isOnlyFrontMostRequired,
			int[] atVisual, boolean[] atIsFrontFacing, int[] atSubElement, double[] atZ);

	protected abstract RenderTargetAdapter createRenderTargetAdapter(RenderTarget renderTarget);

	protected abstract RenderCanvas createRenderCanvas(OnscreenRenderTarget onscreenRenderTarget);

	@Override
	protected abstract boolean requiresHierarchyAndAbsoluteTransformationListening();

	@Override
	protected abstract boolean requiresBoundListening();

	public Renderer() {
		super();
		createNativeInstance();
	}

	protected abstract void internalSetIsSoftwareEmulationForced(boolean isSoftwareEmulationForced);

	@Override
	public void setIsSoftwareEmulationForced(final boolean isSoftwareEmulationForced) {
		super.setIsSoftwareEmulationForced(isSoftwareEmulationForced);
		internalSetIsSoftwareEmulationForced(isSoftwareEmulationForced);
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(
			final edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent, final javax.vecmath.Vector3d v,
			final double planeMinX, final double planeMinY, final double planeMaxX, final double planeMaxY,
			final double nearClippingPlaneDistance, final double farClippingPlaneDistance,
			final boolean isSubElementRequired, final boolean isOnlyFrontMostRequired) {
		commitAnyPendingChanges();
		final ComponentProxy componentProxy = (ComponentProxy) getProxyFor(sgComponent);
		final int[] atVisual = { 0 };
		final boolean[] atIsFrontFacing = { true };
		final int[] atSubElement = { -1 };
		final double[] atZ = { Double.NaN };

		pick(componentProxy, v.x, v.y, v.z, planeMinX, planeMinY, planeMaxX, planeMaxY, nearClippingPlaneDistance,
				farClippingPlaneDistance, isSubElementRequired, isOnlyFrontMostRequired, atVisual, atIsFrontFacing,
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
		// todo: compute projection matrix
		return new PickInfo(sgComponent, null, sgVisuals, isFrontFacings, sgGeometries, subElements, atZ);
	}

	@Override
	protected void dispatchAbsoluteTransformationChange(
			final edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent absoluteTransformationEvent) {
		final edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent = (edu.cmu.cs.stage3.alice.scenegraph.Component) absoluteTransformationEvent
				.getSource();
		if (sgComponent.isReleased()) {
			// pass
		} else {
			final ComponentProxy componentProxy = (ComponentProxy) getProxyFor(sgComponent);
			componentProxy.onAbsoluteTransformationChange();
		}
	}

	@Override
	protected void dispatchBoundChange(final edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent boundEvent) {
	}

	@Override
	public void dispatchChildAdd(final edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent) {
		final edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer = (edu.cmu.cs.stage3.alice.scenegraph.Container) childrenEvent
				.getSource();
		final edu.cmu.cs.stage3.alice.scenegraph.Component sgChild = childrenEvent.getChild();
		if (sgContainer.isReleased() || sgChild.isReleased()) {
			// pass
		} else {
			final ContainerProxy containerProxy = (ContainerProxy) getProxyFor(sgContainer);
			final ComponentProxy childProxy = (ComponentProxy) getProxyFor(sgChild);
			containerProxy.onChildAdded(childProxy);
		}
	}

	@Override
	public void dispatchChildRemove(final edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent) {
		final edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer = (edu.cmu.cs.stage3.alice.scenegraph.Container) childrenEvent
				.getSource();
		final edu.cmu.cs.stage3.alice.scenegraph.Component sgChild = childrenEvent.getChild();
		if (sgContainer.isReleased() || sgChild.isReleased()) {
			// pass
		} else {
			final ContainerProxy containerProxy = (ContainerProxy) getProxyFor(sgContainer);
			final ComponentProxy childProxy = (ComponentProxy) getProxyFor(sgChild);
			containerProxy.onChildRemoved(childProxy);
		}
	}

	@Override
	protected void dispatchHierarchyChange(
			final edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent hierarchyEvent) {
		final edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent = (edu.cmu.cs.stage3.alice.scenegraph.Component) hierarchyEvent
				.getSource();
		if (sgComponent.isReleased()) {
			// pass
		} else {
			final ComponentProxy componentProxy = (ComponentProxy) getProxyFor(sgComponent);
			componentProxy.onHierarchyChange();
		}
	}

	@Override
	public void commitAnyPendingChanges() {
		super.commitAnyPendingChanges();
		ComponentProxy.updateAbsoluteTransformationChanges();
		GeometryProxy.updateBoundChanges();
	}

	@Override
	protected abstract edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxy createProxyFor(
			edu.cmu.cs.stage3.alice.scenegraph.Element sgElement);

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget createOffscreenRenderTarget() {
		return new OffscreenRenderTarget(this);
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget createOnscreenRenderTarget() {
		return new OnscreenRenderTarget(this);
	}
}
