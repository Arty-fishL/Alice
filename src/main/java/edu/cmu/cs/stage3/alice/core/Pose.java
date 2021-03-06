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

import java.util.Enumeration;

import edu.cmu.cs.stage3.math.Matrix44;

/**
 * @author Jason Pratt
 */
public class Pose extends Element {
	public final edu.cmu.cs.stage3.alice.core.property.DictionaryProperty poseMap = new edu.cmu.cs.stage3.alice.core.property.DictionaryProperty(
			this, "poseMap", null);

	public static Pose manufacturePose(final Transformable modelRoot, final Transformable poseRoot) {
		final Pose pose = new Pose();
		final java.util.Hashtable<String, Matrix44> map = new java.util.Hashtable<>();
		final Transformable[] descendants = (Transformable[]) poseRoot.getDescendants(Transformable.class);
		for (final Transformable descendant : descendants) {
			if (descendant != poseRoot) {
				map.put(descendant.getKey(modelRoot), descendant.getLocalTransformation());
			}
		}
		pose.poseMap.set(map);
		return pose;
	}

	private final java.util.Hashtable<Element, Object> HACK_hardMap = new java.util.Hashtable<Element, Object>();

	public void HACK_harden() {
		final Element parent = getParent();
		HACK_hardMap.clear();
		@SuppressWarnings("unchecked")
		final java.util.Enumeration<String> enum0 = (Enumeration<String>) poseMap.keys();
		while (enum0.hasMoreElements()) {
			final String key = enum0.nextElement().toString();
			final Object value = poseMap.get(key);
			final Element hardKey = parent.getDescendantKeyedIgnoreCase(key);
			if (hardKey != null) {
				HACK_hardMap.put(hardKey, value);
			} else {
				System.err.println("COULD NOT HARDEN KEY: " + key);
			}
		}
	}

	public void HACK_soften() {
		final Element parent = getParent();
		final java.util.Dictionary<String, Object> softMap = new java.util.Hashtable<String, Object>();
		final java.util.Enumeration<Element> enum0 = HACK_hardMap.keys();
		while (enum0.hasMoreElements()) {
			final Element hardKey = enum0.nextElement();
			final Object value = HACK_hardMap.get(hardKey);
			final String softKey = hardKey.getKey(parent);
			if (softKey != null) {
				softMap.put(softKey, value);
			} else {
				System.err.println("COULD NOT SOFTEN KEY: " + hardKey);
			}
		}
		poseMap.set(softMap);
	}

	public void scalePositionRightNow(final Element part, final Element modelRoot, final javax.vecmath.Vector3d scale,
			final ReferenceFrame asSeenBy) {
		final String key = part.getKey(modelRoot);
		final javax.vecmath.Matrix4d prev = (javax.vecmath.Matrix4d) poseMap.get(key);
		if (prev != null) {
			final javax.vecmath.Matrix4d curr = new edu.cmu.cs.stage3.math.Matrix44();
			curr.set(prev);
			curr.m30 *= scale.x;
			curr.m31 *= scale.y;
			curr.m32 *= scale.z;
			poseMap.put(key, curr);
		} else {
			// todo?
		}
	}

	/*
	 * private String getRelativeKey( edu.cmu.cs.stage3.alice.core.Element root,
	 * edu.cmu.cs.stage3.alice.core.Element child ) { return
	 * child.getKey().substring( 0, child.getKey().indexOf( root.getKey() ) + 1
	 * ); }
	 */

	public void resize(final Element part, final Element modelRoot, final double ratio) {
		@SuppressWarnings("unchecked")
		final java.util.Enumeration<String> keys = (Enumeration<String>) poseMap.keys();
		while (keys.hasMoreElements()) {
			final String key = keys.nextElement().toString();
			if (key.indexOf(part.getKey(modelRoot)) != -1) {
				final edu.cmu.cs.stage3.math.Matrix44 transform = (edu.cmu.cs.stage3.math.Matrix44) poseMap.get(key);
				final edu.cmu.cs.stage3.math.Vector3 pos = transform.getPosition();
				pos.scale(ratio);
				transform.setPosition(pos);
				poseMap.put(key, transform);
			}
		}
	}
}