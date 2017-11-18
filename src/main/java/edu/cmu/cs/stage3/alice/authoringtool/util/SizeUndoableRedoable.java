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

package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;

/**
 * @author Jason Pratt
 */
public class SizeUndoableRedoable extends OneShotUndoableRedoable {
	public SizeUndoableRedoable(final edu.cmu.cs.stage3.alice.core.Transformable transformable,
			final javax.vecmath.Vector3d oldSize, final javax.vecmath.Vector3d newSize,
			final edu.cmu.cs.stage3.alice.core.Scheduler scheduler) {
		super(new edu.cmu.cs.stage3.alice.core.response.SizeAnimation(),
				new edu.cmu.cs.stage3.alice.core.response.SizeAnimation(),
				new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior(), scheduler);

		final edu.cmu.cs.stage3.alice.core.response.SizeAnimation redoResponse = (edu.cmu.cs.stage3.alice.core.response.SizeAnimation) getRedoResponse();
		final edu.cmu.cs.stage3.alice.core.response.SizeAnimation undoResponse = (edu.cmu.cs.stage3.alice.core.response.SizeAnimation) getUndoResponse();
		final edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior oneShotBehavior = getOneShotBehavior();

		redoResponse.subject.set(transformable);
		redoResponse.size.set(newSize);
		undoResponse.subject.set(transformable);
		undoResponse.size.set(oldSize);

		final java.util.ArrayList<ObjectProperty> affectedProperties = new java.util.ArrayList<ObjectProperty>();
		final edu.cmu.cs.stage3.alice.core.Transformable[] transformables = (edu.cmu.cs.stage3.alice.core.Transformable[]) transformable
				.getDescendants(edu.cmu.cs.stage3.alice.core.Transformable.class,
						edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
		for (final Transformable transformable2 : transformables) {
			affectedProperties.add(transformable2.localTransformation);
			if (transformable2 instanceof edu.cmu.cs.stage3.alice.core.Model) {
				affectedProperties.add(((edu.cmu.cs.stage3.alice.core.Model) transformable2).visualScale);
			}
		}

		oneShotBehavior.setAffectedProperties(affectedProperties
				.toArray(new edu.cmu.cs.stage3.alice.core.Property[0]));
	}
}