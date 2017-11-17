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

public class EulerAngles implements Interpolable, Cloneable {
	public double pitch = 0;
	public double yaw = 0;
	public double roll = 0;

	public EulerAngles() {
	}

	public EulerAngles(final double pitch, final double yaw, final double roll) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	public EulerAngles(final double[] a) {
		this(a[0], a[1], a[2]);
	}

	public EulerAngles(final Matrix33 m) {
		setMatrix33(m);
	}

	public EulerAngles(final AxisAngle aa) {
		setAxisAngle(aa);
	}

	public EulerAngles(final Quaternion q) {
		setQuaternion(q);
	}

	@Override
	public synchronized Object clone() {
		try {
			return super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (o != null && o instanceof EulerAngles) {
			final EulerAngles ea = (EulerAngles) o;
			return yaw == ea.yaw && pitch == ea.pitch && roll == ea.roll;
		} else {
			return false;
		}
	}

	public Matrix33 getMatrix33() {
		return new Matrix33(this);
	}

	public void setMatrix33(final Matrix33 m) {
		final javax.vecmath.Vector3d row0 = MathUtilities.getRow(m, 0);
		javax.vecmath.Vector3d row1 = MathUtilities.getRow(m, 1);
		javax.vecmath.Vector3d row2 = MathUtilities.getRow(m, 2);
		final javax.vecmath.Vector3d scale = new javax.vecmath.Vector3d();
		final Shear shear = new Shear();

		scale.x = row0.length();
		row0.normalize();
		shear.xy = MathUtilities.dotProduct(row0, row1);
		row1 = MathUtilities.combine(row1, row0, 1, -shear.xy);

		scale.y = row1.length();
		row1.normalize();
		shear.xy /= scale.y;
		shear.xz = MathUtilities.dotProduct(row0, row2);
		row2 = MathUtilities.combine(row2, row0, 1, -shear.xz);

		shear.yz = MathUtilities.dotProduct(row1, row2);
		row2 = MathUtilities.combine(row2, row1, 1, -shear.yz);

		scale.z = row2.length();
		row2.normalize();
		shear.xz /= scale.z;
		shear.yz /= scale.z;

		final double determinate = MathUtilities.dotProduct(row0, MathUtilities.crossProduct(row1, row2));
		;
		if (determinate < 0) {
			row0.negate();
			row1.negate();
			row2.negate();
			scale.scale(-1);
		}
		yaw = Math.asin(-row0.z);
		if (Math.cos(yaw) != 0) {
			pitch = Math.atan2(row1.z, row2.z);
			roll = Math.atan2(row0.y, row0.x);
		} else {
			pitch = Math.atan2(row1.x, row1.y);
			roll = 0;
		}
	}

	public AxisAngle getAxisAngle() {
		return new AxisAngle(this);
	}

	public void setAxisAngle(final AxisAngle aa) {
		// todo: optimize?
		setMatrix33(aa.getMatrix33());
	}

	public Quaternion getQuaternion() {
		return new Quaternion(this);
	}

	public void setQuaternion(final Quaternion q) {
		// todo: optimize?
		setMatrix33(q.getMatrix33());
	}

	public static EulerAngles interpolate(final EulerAngles a, final EulerAngles b, final double portion) {
		final Quaternion q = Quaternion.interpolate(a.getQuaternion(), b.getQuaternion(), portion);
		return new EulerAngles(q);
	}

	@Override
	public Interpolable interpolate(final Interpolable b, final double portion) {
		return interpolate(this, (EulerAngles) b, portion);
	}

	@Override
	public String toString() {
		return "edu.cmu.cs.stage3.math.EulerAngles[pitch=" + pitch + ",yaw=" + yaw + ",roll=" + roll + "]";
	}

	public static EulerAngles revolutionsToRadians(final EulerAngles ea) {
		return new EulerAngles(ea.pitch / Angle.RADIANS, ea.yaw / Angle.RADIANS, ea.roll / Angle.RADIANS);
	}

	public static EulerAngles radiansToRevolutions(final EulerAngles ea) {
		return new EulerAngles(ea.pitch * Angle.RADIANS, ea.yaw * Angle.RADIANS, ea.roll * Angle.RADIANS);
	}

	public static EulerAngles valueOf(final String s) {
		final String[] markers = { "edu.cmu.cs.stage3.math.EulerAngles[pitch=", ",yaw=", ",roll=", "]" };
		final double[] values = new double[markers.length - 1];
		for (int i = 0; i < values.length; i++) {
			final int begin = s.indexOf(markers[i]) + markers[i].length();
			final int end = s.indexOf(markers[i + 1]);
			values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
		}
		return new EulerAngles(values);
	}
}
