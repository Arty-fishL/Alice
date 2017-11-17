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

package edu.cmu.cs.stage3.pratt.maxkeyframing;

/**
 * @author Jason Pratt
 */
public class QuaternionTCBKey extends TCBKey {
	public QuaternionTCBKey(final double time, final edu.cmu.cs.stage3.math.Quaternion value, final double tension,
			final double continuity, final double bias) {
		super(time, new double[] { value.x, value.y, value.z, value.w }, tension, continuity, bias); // fix
																										// this
																										// when
																										// Quaternion
																										// constructor
																										// changes
	}

	private final edu.cmu.cs.stage3.math.Quaternion qSample = new edu.cmu.cs.stage3.math.Quaternion();

	@Override
	public Object createSample(final double[] components) {
		final double lengthSquared = components[0] * components[0] + components[1] * components[1]
				+ components[2] * components[2] + components[3] * components[3];
		if (lengthSquared == 1.0) {
			qSample.x = components[0];
			qSample.y = components[1];
			qSample.z = components[2];
			qSample.w = components[3];
			return qSample;
			// return new edu.cmu.cs.stage3.math.Quaternion( components[0],
			// components[1], components[2], components[3] );
		} else {
			final double length = Math.sqrt(lengthSquared);
			qSample.x = components[0] / length;
			qSample.y = components[1] / length;
			qSample.z = components[2] / length;
			qSample.w = components[3] / length;
			return qSample;
			// return new edu.cmu.cs.stage3.math.Quaternion(
			// components[0]/length, components[1]/length, components[2]/length,
			// components[3]/length );
		}
	}

	public static QuaternionTCBKey valueOf(final String s) {
		final java.util.StringTokenizer st = new java.util.StringTokenizer(s, " \t,[]");

		final String className = st.nextToken(); // unused
		final double time = Double.parseDouble(st.nextToken());
		final edu.cmu.cs.stage3.math.Quaternion value = new edu.cmu.cs.stage3.math.Quaternion(
				Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()),
				Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
		final double tension = Double.parseDouble(st.nextToken());
		final double continuity = Double.parseDouble(st.nextToken());
		final double bias = Double.parseDouble(st.nextToken());

		return new QuaternionTCBKey(time, value, tension, continuity, bias);
	}
}