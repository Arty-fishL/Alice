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

package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Transformable;

/**
 * @author Jason Pratt
 */
public class DefaultMouseInteractionBehavior extends InternalResponseBehavior {
	public final edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty renderTargets = new edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty(
			this, "renderTargets", null, edu.cmu.cs.stage3.alice.core.RenderTarget[].class);
	public final edu.cmu.cs.stage3.alice.core.property.ListProperty objects = new edu.cmu.cs.stage3.alice.core.property.ListProperty(
			this, "objects", null);

	protected java.util.Vector manipulators = new java.util.Vector();

	protected void scheduled(final double t) {
	}

	protected void objectsValueChanged(final edu.cmu.cs.stage3.alice.core.List value) {
		final java.util.Enumeration enum0 = DefaultMouseInteractionBehavior.this.manipulators.elements();
		while (enum0.hasMoreElements()) {
			final edu.cmu.cs.stage3.alice.core.manipulator.RenderTargetModelManipulator rtmm = (edu.cmu.cs.stage3.alice.core.manipulator.RenderTargetModelManipulator) enum0
					.nextElement();
			rtmm.clearObjectsOfInterestList();
			if (value != null) {
				for (int i = 0; i < value.values.getArrayValue().length; i++) {
					rtmm.addObjectOfInterest((Transformable) value.values.getArrayValue()[i]);
				}
			}
		}
	}

	private void setIsEnabled(final boolean value) {
		if (manipulators != null) {
			final java.util.Enumeration enum0 = manipulators.elements();
			while (enum0.hasMoreElements()) {
				((edu.cmu.cs.stage3.alice.core.manipulator.RenderTargetModelManipulator) enum0.nextElement())
						.setEnabled(isEnabled.booleanValue());
			}
		}
	}

	@Override
	protected void enabled() {
		setIsEnabled(true);
	}

	@Override
	protected void disabled() {
		setIsEnabled(false);
	}

	@Override
	protected void propertyChanged(final edu.cmu.cs.stage3.alice.core.Property property, final Object value) {
		if (property == objects) {
			objectsValueChanged(objects.getListValue());
		} else {
			super.propertyChanged(property, value);
		}
	}

	@Override
	protected void started(final edu.cmu.cs.stage3.alice.core.World world, final double time) {
		super.started(world, time);
		manipulators.clear();
		edu.cmu.cs.stage3.alice.core.RenderTarget[] renderTargetsValue = (edu.cmu.cs.stage3.alice.core.RenderTarget[]) renderTargets
				.get();
		if (renderTargetsValue == null) {
			renderTargetsValue = (edu.cmu.cs.stage3.alice.core.RenderTarget[]) world
					.getDescendants(edu.cmu.cs.stage3.alice.core.RenderTarget.class);
		}
		for (final RenderTarget element : renderTargetsValue) {
			final edu.cmu.cs.stage3.alice.core.manipulator.RenderTargetModelManipulator rtmm = new edu.cmu.cs.stage3.alice.core.manipulator.RenderTargetModelManipulator(
					element.getInternal());
			// rtmm.setPopupEnabled( false );
			rtmm.setPickAllForOneObjectOfInterestEnabled(false);
			manipulators.addElement(rtmm);
		}
		objectsValueChanged(objects.getListValue());
		setIsEnabled(isEnabled.booleanValue());
	}

	@Override
	protected void stopped(final edu.cmu.cs.stage3.alice.core.World world, final double time) {
		super.stopped(world, time);
		final java.util.Enumeration enum0 = manipulators.elements();
		while (enum0.hasMoreElements()) {
			((edu.cmu.cs.stage3.alice.core.manipulator.RenderTargetModelManipulator) enum0.nextElement())
					.setRenderTarget(null);
		}
		manipulators.clear();
	}
}
