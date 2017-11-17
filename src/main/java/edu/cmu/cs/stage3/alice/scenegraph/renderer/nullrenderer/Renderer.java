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

public class Renderer extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractRenderer {

	@Override
	protected boolean requiresHierarchyAndAbsoluteTransformationListening() {
		return false;
	}

	@Override
	protected boolean requiresBoundListening() {
		return false;
	}

	@Override
	protected void dispatchPropertyChange(final edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent propertyEvent) {
	}

	@Override
	protected void dispatchRelease(final edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent releaseEvent) {
	}

	@Override
	protected void dispatchAbsoluteTransformationChange(
			final edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent absoluteTransformationEvent) {
	}

	@Override
	protected void dispatchBoundChange(final edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent boundEvent) {
	}

	@Override
	public void dispatchChildAdd(final edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent) {
	}

	@Override
	public void dispatchChildRemove(final edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent) {
	}

	@Override
	protected void dispatchHierarchyChange(
			final edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent hierarchyEvent) {
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget createOffscreenRenderTarget() {
		return new OffscreenRenderTarget(this);
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget createOnscreenRenderTarget() {
		return new OnscreenRenderTarget(this);
	}

	@Override
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(
			final edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent, final javax.vecmath.Vector3d v,
			final double planeMinX, final double planeMinY, final double planeMaxX, final double planeMaxY,
			final double nearClippingPlaneDistance, final double farClippingPlaneDistance,
			final boolean isSubElementRequired, final boolean isOnlyFrontMostRequired) {
		return null;
	}
}
