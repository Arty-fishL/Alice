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

package edu.cmu.cs.stage3.math;

public class Vector3 extends javax.vecmath.Vector3d implements Interpolable {
	/**
	 *
	 */
	private static final long serialVersionUID = -7461419106924698901L;
	public static final Vector3 ZERO = new Vector3(0, 0, 0);
	public static final Vector3 X_AXIS = new Vector3(1, 0, 0);
	public static final Vector3 X_AXIS_NEGATIVE = new Vector3(-1, 0, 0);
	public static final Vector3 Y_AXIS = new Vector3(0, 1, 0);
	public static final Vector3 Y_AXIS_NEGATIVE = new Vector3(0, -1, 0);
	public static final Vector3 Z_AXIS = new Vector3(0, 0, 1);
	public static final Vector3 Z_AXIS_NEGATIVE = new Vector3(0, 0, -1);

	public Vector3() {
	}

	public Vector3(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(final double[] v) {
		this(v[0], v[1], v[2]);
	}

	public Vector3(final javax.vecmath.Tuple3d t) {
		this(t.x, t.y, t.z);
	}

	public Vector3(final javax.vecmath.Tuple4d t) {
		this(t.x / t.w, t.y / t.w, t.z / t.w);
	}

	public double getItem(final int i) {
		switch (i) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		}
		throw new IllegalArgumentException();
	}

	public void setItem(final int i, final double v) {
		switch (i) {
		case 0:
			x = v;
			return;
		case 1:
			y = v;
			return;
		case 2:
			z = v;
			return;
		}
		throw new IllegalArgumentException();
	}

	public double[] getArray() {
		final double[] a = { x, y, z };
		return a;
	}

	public void setArray(final double[] a) {
		x = a[0];
		y = a[1];
		z = a[2];
	}

	public void add(final Vector3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	public static Vector3 add(final javax.vecmath.Vector3d a, final javax.vecmath.Vector3d b) {
		return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
	}

	public void subtract(final Vector3 v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}

	public static Vector3 subtract(final javax.vecmath.Vector3d a, final javax.vecmath.Vector3d b) {
		return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
	}

	public static Vector3 negate(final javax.vecmath.Vector3d v) {
		return new Vector3(-v.x, -v.y, -v.z);
	}

	public void multiply(final double scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}

	public void multiply(final javax.vecmath.Vector3d scalar) {
		x *= scalar.x;
		y *= scalar.y;
		z *= scalar.z;
	}

	public static Vector3 multiply(final javax.vecmath.Vector3d v, final double scalar) {
		return new Vector3(v.x * scalar, v.y * scalar, v.z * scalar);
	}

	public static Vector3 multiply(final javax.vecmath.Vector3d v, final Vector3 scalar) {
		return new Vector3(v.x * scalar.x, v.y * scalar.y, v.z * scalar.z);
	}

	public void divide(final double divisor) {
		multiply(1 / divisor);
	}

	public void divide(final javax.vecmath.Vector3d divisor) {
		x /= divisor.x;
		y /= divisor.y;
		z /= divisor.z;
	}

	public static Vector3 divide(final javax.vecmath.Vector3d v, final double divisor) {
		return multiply(v, 1 / divisor);
	}

	public static Vector3 divide(final javax.vecmath.Vector3d numerator, final javax.vecmath.Vector3d divisor) {
		return new Vector3(numerator.x / divisor.x, numerator.y / divisor.y, numerator.z / divisor.z);
	}

	public void invert() {
		x = 1 / x;
		y = 1 / y;
		z = 1 / z;
	}

	public static Vector3 invert(final javax.vecmath.Vector3d v) {
		return new Vector3(1 / v.x, 1 / v.y, 1 / v.z);
	}

	public static Vector3 normalizeV(final javax.vecmath.Vector3d v) {
		final Vector3 nv = new Vector3(v.x, v.y, v.z);
		nv.normalize();
		return nv;
	}

	public static double getLengthSquared(final double x, final double y, final double z) {
		return x * x + y * y + z * z;
	}

	public static double getLength(final double x, final double y, final double z) {
		final double lengthSquared = Vector3.getLengthSquared(x, y, z);
		if (lengthSquared == 1) {
			return 1;
		} else {
			return Math.sqrt(lengthSquared);
		}
	}

	public double getLengthSquared() {
		return Vector3.getLengthSquared(x, y, z);
	}

	public double getLength() {
		return Vector3.getLength(x, y, z);
	}

	public static double dotProduct(final javax.vecmath.Vector3d a, final javax.vecmath.Vector3d b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	public static Vector3 crossProduct(final javax.vecmath.Vector3d a, final javax.vecmath.Vector3d b) {
		return new Vector3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
	}

	public static Vector3 interpolate(final javax.vecmath.Vector3d a, final javax.vecmath.Vector3d b,
			final double portion) {
		return new Vector3(a.x + (b.x - a.x) * portion, a.y + (b.y - a.y) * portion, a.z + (b.z - a.z) * portion);
	}

	@Override
	public Interpolable interpolate(final Interpolable b, final double portion) {
		return interpolate(this, (Vector3) b, portion);
	}

	public Vector3 projectOnto(final javax.vecmath.Vector3d b) {
		return multiply(b, dotProduct(b, this) / dotProduct(b, b));
	}

	public static Vector3 projectOnto(final javax.vecmath.Vector3d a, final javax.vecmath.Vector3d b) {
		return multiply(b, dotProduct(b, a) / dotProduct(b, b));
	}

	public static Vector3 multiply(final javax.vecmath.Matrix3d a, final javax.vecmath.Vector3d b) {
		final double x = a.m00 * b.x + a.m01 * b.y + a.m02 * b.z;
		final double y = a.m10 * b.x + a.m11 * b.y + a.m12 * b.z;
		final double z = a.m20 * b.x + a.m21 * b.y + a.m22 * b.z;
		return new Vector3(x, y, z);
	}

	public static Vector3 multiply(final javax.vecmath.Vector3d a, final javax.vecmath.Matrix4d b) {
		final Vector3 ab = new Vector3();
		ab.x = a.x * b.m00 + a.y * b.m10 + a.z * b.m20;
		ab.y = a.x * b.m01 + a.y * b.m11 + a.z * b.m21;
		ab.z = a.x * b.m02 + a.y * b.m12 + a.z * b.m22;
		return ab;
	}

	public static Vector3 combine(final javax.vecmath.Vector3d a, final javax.vecmath.Vector3d b, final double asc1,
			final double bsc1) {
		final Vector3 ab = new Vector3();
		ab.x = asc1 * a.x + bsc1 * b.x;
		ab.y = asc1 * a.y + bsc1 * b.y;
		ab.z = asc1 * a.z + bsc1 * b.z;
		return ab;
	}

	@Override
	public String toString() {
		return "edu.cmu.cs.stage3.math.Vector3[x=" + x + ",y=" + y + ",z=" + z + "]";
	}

	public static Vector3 valueOf(final String s) {
		final String[] markers = { "edu.cmu.cs.stage3.math.Vector3[x=", ",y=", ",z=", "]" };
		final double[] values = new double[markers.length - 1];
		for (int i = 0; i < values.length; i++) {
			final int begin = s.indexOf(markers[i]) + markers[i].length();
			final int end = s.indexOf(markers[i + 1]);
			values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
		}
		return new Vector3(values);
	}
}
