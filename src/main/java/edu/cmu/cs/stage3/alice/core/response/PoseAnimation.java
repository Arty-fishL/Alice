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

package edu.cmu.cs.stage3.alice.core.response;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Vector3;

/**
 * @author Jason Pratt
 */
public class PoseAnimation extends Animation {
	public final edu.cmu.cs.stage3.alice.core.property.TransformableProperty subject = new edu.cmu.cs.stage3.alice.core.property.TransformableProperty(
			this, "subject", null);
	public final PoseProperty pose = new PoseProperty(this, "pose", null);

	public PoseAnimation() {
		// subject.setIsAcceptingOfNull( true );
		// pose.setIsAcceptingOfNull( true );
	}

	public class PoseProperty extends edu.cmu.cs.stage3.alice.core.property.ElementProperty {
		public PoseProperty(final edu.cmu.cs.stage3.alice.core.Element owner, final String name,
				final edu.cmu.cs.stage3.alice.core.Pose defaultValue) {
			super(owner, name, defaultValue, edu.cmu.cs.stage3.alice.core.Pose.class);
		}

		public edu.cmu.cs.stage3.alice.core.Pose getPoseValue() {
			return (edu.cmu.cs.stage3.alice.core.Pose) getElementValue();
		}
		// public edu.cmu.cs.stage3.alice.core.Pose getPoseValue(
		// RuntimePoseAnimation runtimePoseAnimation ) {
		// return (edu.cmu.cs.stage3.alice.core.Pose)super.getElementValue(
		// runtimePoseAnimation );
		// }
	}

	public class RuntimePoseAnimation extends RuntimeAnimation {
		protected edu.cmu.cs.stage3.alice.core.Transformable subject;
		protected edu.cmu.cs.stage3.alice.core.Pose pose;
		protected Dictionary<edu.cmu.cs.stage3.alice.core.Pose, String> poseStringMap;
		protected Vector<Transformable> transformableKeys = new Vector<Transformable>();
		protected Dictionary<edu.cmu.cs.stage3.alice.core.Pose, Transformable> poseTransformableMap = new Hashtable<>();
		protected Dictionary<Transformable, Vector3> sourcePositionMap = new Hashtable<Transformable, Vector3>();
		protected Dictionary<Transformable, Vector3> targetPositionMap = new Hashtable<Transformable, Vector3>();
		protected Dictionary<Transformable, Quaternion> sourceQuaternionMap = new Hashtable<Transformable, Quaternion>();
		protected Dictionary<Transformable, Quaternion> targetQuaternionMap = new Hashtable<Transformable, Quaternion>();
		protected Dictionary<?, ?> sourceScaleMap = new Hashtable<Object, Object>(); // Unused ??
		protected Dictionary<?, ?> targetScaleMap = new Hashtable<Object, Object>(); // Unused ??

		@Override
		public void prologue(final double t) {
			super.prologue(t);
			subject = PoseAnimation.this.subject.getTransformableValue();
			pose = PoseAnimation.this.pose.getPoseValue();
			final Dictionary<?, ?> poseStringMap = pose.poseMap.getDictionaryValue();
			for (final Enumeration<?> enum0 = poseStringMap.keys(); enum0.hasMoreElements();) {
				final String stringKey = (String) enum0.nextElement();
				final edu.cmu.cs.stage3.alice.core.Transformable key = (edu.cmu.cs.stage3.alice.core.Transformable) subject
						.getDescendantKeyed(stringKey);
				if (key != null) {
					transformableKeys.add(key);
					final edu.cmu.cs.stage3.math.Matrix44 m = (edu.cmu.cs.stage3.math.Matrix44) poseStringMap
							.get(stringKey);
					sourcePositionMap.put(key, key.getPosition());
					sourceQuaternionMap.put(key, key.getOrientationAsQuaternion());
					targetPositionMap.put(key, m.getPosition());
					targetQuaternionMap.put(key, m.getAxes().getQuaternion());
				} else {
					System.err.println("Can't find " + stringKey + " in " + subject);
				}
			}
		}

		@Override
		public void update(final double t) {
			super.update(t);
			final double portion = getPortion(t);
			for (final Enumeration<Transformable> enum0 = transformableKeys.elements(); enum0.hasMoreElements();) {
				final edu.cmu.cs.stage3.alice.core.Transformable key = enum0
						.nextElement();

				final edu.cmu.cs.stage3.math.Vector3 sourcePosition = sourcePositionMap
						.get(key);
				final edu.cmu.cs.stage3.math.Vector3 targetPosition = targetPositionMap
						.get(key);
				final edu.cmu.cs.stage3.math.Quaternion sourceQuaternion = sourceQuaternionMap
						.get(key);
				final edu.cmu.cs.stage3.math.Quaternion targetQuaternion = targetQuaternionMap
						.get(key);

				final edu.cmu.cs.stage3.math.Vector3 currentPosition = edu.cmu.cs.stage3.math.Vector3
						.interpolate(sourcePosition, targetPosition, portion);
				final edu.cmu.cs.stage3.math.Quaternion currentQuaternion = edu.cmu.cs.stage3.math.Quaternion
						.interpolate(sourceQuaternion, targetQuaternion, portion);

				key.setPositionRightNow(currentPosition);
				key.setOrientationRightNow(currentQuaternion);
			}
		}

		/*
		 * private edu.cmu.cs.stage3.alice.core.Transformable
		 * getDescendantFromKey( edu.cmu.cs.stage3.alice.core.Transformable
		 * subject, String relativeKey ) { StringTokenizer tokenizer =
		 * new StringTokenizer( relativeKey, ".", false ); while(
		 * tokenizer.hasMoreTokens() ) { subject =
		 * (edu.cmu.cs.stage3.alice.core.Transformable)subject.getChildNamed(
		 * tokenizer.nextToken() ); } return subject; }
		 */
	}
}