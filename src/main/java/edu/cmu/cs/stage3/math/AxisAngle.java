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

public class AxisAngle implements Cloneable {
	protected javax.vecmath.Vector3d m_axis = MathUtilities.createZAxis();
	protected double m_angle = 0;

	public AxisAngle() {
	}

	public AxisAngle(final double x, final double y, final double z, final double angle) {
		this(new javax.vecmath.Vector3d(x, y, z), angle);
	}

	public AxisAngle(final double[] axis, final double angle) {
		this(new javax.vecmath.Vector3d(axis), angle);
	}

	public AxisAngle(final double[] array) {
		this(array[0], array[1], array[2], array[3]);
	}

	public AxisAngle(final javax.vecmath.Vector3d axis, final double angle) {
		m_axis = axis;
		m_angle = angle;
	}

	public AxisAngle(final Matrix33 m) {
		setMatrix33(m);
	}

	public AxisAngle(final Quaternion q) {
		setQuaternion(q);
	}

	public AxisAngle(final EulerAngles ea) {
		setEulerAngles(ea);
	}

	public double getAngle() {
		return m_angle;
	}

	public void setAngle(final double angle) {
		m_angle = angle;
	}

	public javax.vecmath.Vector3d getAxis() {
		if (m_axis != null) {
			return (javax.vecmath.Vector3d) m_axis.clone();
		} else {
			return null;
		}
	}

	public void setAxis(final javax.vecmath.Vector3d axis) {
		m_axis = axis;
	}

	@Override
	public synchronized Object clone() {
		try {
			final AxisAngle axisAngle = (AxisAngle) super.clone();
			axisAngle.setAxis((javax.vecmath.Vector3d) m_axis.clone());
			return axisAngle;
		} catch (final CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (o != null && o instanceof AxisAngle) {
			final AxisAngle aa = (AxisAngle) o;
			// todo handle null
			return m_axis.equals(aa.m_axis) && m_angle == aa.m_angle;
		} else {
			return false;
		}
	}

	public double[] getArray() {
		final double[] a = { m_axis.x, m_axis.y, m_axis.z, m_angle };
		return a;
	}

	public void setArray(final double[] a) {
		m_axis.x = a[0];
		m_axis.y = a[1];
		m_axis.z = a[2];
		m_angle = a[3];
	}

	public Quaternion getQuaternion() {
		return new Quaternion(this);
	}

	public void setQuaternion(final Quaternion q) {
		m_angle = 2 * Math.acos(q.w);
		m_axis.x = 2 * Math.asin(q.x);
		m_axis.y = 2 * Math.asin(q.y);
		m_axis.z = 2 * Math.asin(q.z);
	}

	public EulerAngles getEulerAngles() {
		return new EulerAngles(this);
	}

	public void setEulerAngles(final EulerAngles ea) {
		// todo: optimize?
		setQuaternion(ea.getQuaternion());
	}

	public Matrix33 getMatrix33() {
		return new Matrix33(this);
	}

	public void setMatrix33(final Matrix33 m) {
		// todo: optimize?
		setQuaternion(m.getQuaternion());
	}

	@Override
	public String toString() {
		return "edu.cmu.cs.stage3.math.AxisAngle[axis.x=" + m_axis.x + ",axis.y=" + m_axis.y + ",axis.z=" + m_axis.z
				+ ",angle=" + m_angle + "]";
	}

	public static AxisAngle valueOf(final String s) {
		final String[] markers = { "edu.cmu.cs.stage3.math.AxisAngle[axis.x=", ",axis.y=", ",axis.z=", ",angle=", "]" };
		final double[] values = new double[markers.length - 1];
		for (int i = 0; i < values.length; i++) {
			final int begin = s.indexOf(markers[i]) + markers[i].length();
			final int end = s.indexOf(markers[i + 1]);
			values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
		}
		return new AxisAngle(values);
	}
}
