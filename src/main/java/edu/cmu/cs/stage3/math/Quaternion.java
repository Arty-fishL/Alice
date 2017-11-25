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

public class Quaternion implements Cloneable, Interpolable {
	public double x = 0;
	public double y = 0;
	public double z = 0;
	public double w = 1;

	public Quaternion() {
	}

	public Quaternion(final double x, final double y, final double z, final double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Quaternion(final double[] v) {
		this(v[0], v[1], v[2], v[3]);
	}

	public Quaternion(final Matrix33 m) {
		setMatrix33(m);
	}

	public Quaternion(final AxisAngle aa) {
		setAxisAngle(aa);
	}

	public Quaternion(final EulerAngles ea) {
		setEulerAngles(ea);
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
		if (o != null && o instanceof Quaternion) {
			final Quaternion q = (Quaternion) o;
			return x == q.x && y == q.y && z == q.z && w == q.w;
		} else {
			return false;
		}
	}

	public double[] getArray() {
		final double[] a = { x, y, z, w };
		return a;
	}

	public void setArray(final double[] a) {
		x = a[0];
		y = a[1];
		z = a[2];
		w = a[3];
	}

	public boolean equals(final Quaternion q) {
		return x == q.x && y == q.y && z == q.z && w == q.w;
	}

	public AxisAngle getAxisAngle() {
		return new AxisAngle(this);
	}

	public void setAxisAngle(final AxisAngle aa) {
		final double halfAngle = aa.getAngle() * 0.5;
		final double cosHalfAngle = Math.cos(halfAngle);
		final double sinHalfAngle = Math.sin(halfAngle);
		/* Unused ?? final javax.vecmath.Vector3d normalizedAxis = */ Vector3.normalizeV(aa.getAxis());
		w = cosHalfAngle;
		x = sinHalfAngle * aa.m_axis.x;
		y = sinHalfAngle * aa.m_axis.y;
		z = sinHalfAngle * aa.m_axis.z;
	}

	public EulerAngles getEulerAngles() {
		return new EulerAngles(this);
	}

	public void setEulerAngles(final EulerAngles ea) {
		final Matrix33 m = new Matrix33();
		m.rotateX(ea.pitch);
		m.rotateY(ea.yaw);
		m.rotateZ(ea.roll);
		setMatrix33(m);
		/*
		 * //todo: why doesn't this work? double c1 = Math.cos( ea.pitch/2 );
		 * double s1 = Math.sin( ea.pitch/2 ); double c2 = Math.cos( ea.yaw/2 );
		 * double s2 = Math.sin( ea.yaw/2 ); double c3 = Math.cos( ea.roll/2 );
		 * double s3 = Math.sin( ea.roll/2 );
		 *
		 * x = c2*s1*c3 - s2*c1*s3; y = c2*s1*s3 + s2*c1*c3; z = c2*c1*s3 -
		 * s2*s1*c3; w = c2*c1*c3 + s2*s1*s3;
		 */
	}

	public Matrix33 getMatrix33() {
		final Matrix33 m = new Matrix33();
		m.setQuaternion(this);
		return m;
	}

	public void setMatrix33(final Matrix33 m) {
		// based on
		// http://www.gamasutra.com/features/19980703/quaternions_01.htm
		// the rows and columns have been reversed
		final double tr = m.m00 + m.m11 + m.m22;
		if (tr > 0.0) {
			double s = Math.sqrt(tr + 1.0);
			w = s * 0.5;
			s = 0.5 / s;
			x = (m.m21 - m.m12) * s;
			y = (m.m02 - m.m20) * s;
			z = (m.m10 - m.m01) * s;
		} else {
			final int[] nxt = { 1, 2, 0 };
			final double[][] a = m.getMatrix();
			int i = 0;
			if (a[1][1] > a[0][0]) {
				i = 1;
			}
			if (a[2][2] > a[i][i]) {
				i = 2;
			}
			final int j = nxt[i];
			final int k = nxt[j];

			double s = Math.sqrt(a[i][i] - (a[j][j] + a[k][k]) + 1.0);

			final double[] q = new double[4];
			q[i] = s * 0.5;

			if (s != 0.0) {
				s = 0.5 / s;
			}

			q[3] = (a[k][j] - a[j][k]) * s;
			q[j] = (a[j][i] + a[i][j]) * s;
			q[k] = (a[k][i] + a[i][k]) * s;
			setArray(q);
		}
	}

	public void normalize() {
		final double lengthSquared = x * x + y * y + z * z + w * w;
		if (lengthSquared != 1) {
			final double length = Math.sqrt(lengthSquared);
			x /= length;
			y /= length;
			z /= length;
			w /= length;
		}
	}

	public static Quaternion multiply(final Quaternion a, final Quaternion b) {
		final double A = (a.w + a.x) * (b.w + b.x);
		final double B = (a.z - a.y) * (b.y - b.z);
		final double C = (a.x - a.w) * (b.y - b.z);
		final double D = (a.y + a.z) * (b.x - b.w);
		final double E = (a.x + a.z) * (b.x + b.y);
		final double F = (a.x - a.z) * (b.x - b.y);
		final double G = (a.w + a.y) * (b.w - b.z);
		final double H = (a.w - a.y) * (b.w + b.z);

		final Quaternion q = new Quaternion();
		q.w = B + (-E - F + G + H) / 2;
		q.x = A - (E + F + G + H) / 2;
		q.y = -C + (E - F + G - H) / 2;
		q.z = -D + (E - F - G + H) / 2;
		return q;
	}

	public static Quaternion interpolate(final Quaternion a, final Quaternion b, final double portion) {
		if (portion <= 0) {
			return (Quaternion) a.clone();
		}
		if (portion >= 1) {
			return (Quaternion) b.clone();
		}

		// calc cosine
		double cosom = a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;

		Quaternion b1;
		// adjust signs (if necessary)
		if (cosom < 0.0) {
			cosom = -cosom;
			b1 = new Quaternion(-b.x, -b.y, -b.z, -b.w);
		} else {
			b1 = b;
		}

		double scale0;
		double scale1;
		// calculate coefficients
		if (1.0 - cosom > Double.MIN_VALUE) {
			// standard case (slerp)
			final double omega = Math.acos(cosom);
			final double sinom = Math.sin(omega);
			scale0 = Math.sin((1.0 - portion) * omega) / sinom;
			scale1 = Math.sin(portion * omega) / sinom;
		} else {
			// "from" and "to" quaternions are very close
			// ... so we can do a linear interpolation
			scale0 = 1.0 - portion;
			scale1 = portion;
		}
		// calculate final values
		final Quaternion q = new Quaternion();
		q.x = scale0 * a.x + scale1 * b1.x;
		q.y = scale0 * a.y + scale1 * b1.y;
		q.z = scale0 * a.z + scale1 * b1.z;
		q.w = scale0 * a.w + scale1 * b1.w;
		return q;
	}

	@Override
	public Interpolable interpolate(final Interpolable b, final double portion) {
		return interpolate(this, (Quaternion) b, portion);
	}

	@Override
	public String toString() {
		return "edu.cmu.cs.stage3.math.Quaternion[x=" + x + ",y=" + y + ",z=" + z + ",w=" + w + "]";
	}

	public static Quaternion valueOf(final String s) {
		final String[] markers = { "edu.cmu.cs.stage3.math.Quaternion[x=", ",y=", ",z=", ",w=", "]" };
		final double[] values = new double[markers.length - 1];
		for (int i = 0; i < values.length; i++) {
			final int begin = s.indexOf(markers[i]) + markers[i].length();
			final int end = s.indexOf(markers[i + 1]);
			values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
		}
		return new Quaternion(values);
	}
}
