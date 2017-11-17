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

public class Ray implements Cloneable {
	protected javax.vecmath.Point3d m_origin = null;
	protected javax.vecmath.Vector3d m_direction = null;

	public Ray() {
		this(new javax.vecmath.Point3d(0, 0, 0), new javax.vecmath.Vector3d(0, 0, 1));
	}

	public Ray(final javax.vecmath.Point3d origin, final javax.vecmath.Vector3d direction) {
		setOrigin(origin);
		setDirection(direction);
	}

	@Override
	public synchronized Object clone() {
		try {
			final Ray ray = (Ray) super.clone();
			ray.setOrigin(new javax.vecmath.Point3d(m_origin));
			ray.setDirection(new javax.vecmath.Vector3d(m_direction));
			return ray;
		} catch (final CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (o != null && o instanceof Ray) {
			final Ray ray = (Ray) o;
			return m_origin.equals(ray.m_origin) && m_direction.equals(ray.m_direction);
		} else {
			return false;
		}
	}

	public javax.vecmath.Point3d getOrigin() {
		if (m_origin != null) {
			return new javax.vecmath.Point3d(m_origin);
		} else {
			return null;
		}
	}

	public void setOrigin(final javax.vecmath.Point3d origin) {
		if (origin != null) {
			m_origin = new javax.vecmath.Point3d(origin);
		} else {
			m_origin = null;
		}
	}

	public javax.vecmath.Vector3d getDirection() {
		if (m_direction != null) {
			return new javax.vecmath.Vector3d(m_direction);
		} else {
			return null;
		}
	}

	public void setDirection(final javax.vecmath.Vector3d direction) {
		if (direction != null) {
			m_direction = new javax.vecmath.Vector3d(direction);
		} else {
			m_direction = null;
		}
	}

	public javax.vecmath.Vector3d getPoint(final double t) {
		final javax.vecmath.Vector3d p = new javax.vecmath.Vector3d(m_direction);
		p.scale(t);
		p.add(m_origin);
		return p;
	}

	public void transform(final javax.vecmath.Matrix4d m) {
		final javax.vecmath.Vector4d transformedOrigin = MathUtilities.multiply(m_origin.x, m_origin.y, m_origin.z, 1,
				m);
		m_origin = MathUtilities.createPoint3d(transformedOrigin);
		final javax.vecmath.Vector4d transformedDirection = MathUtilities.multiply(m_direction, 0, m);
		transformedDirection.w = 1;
		m_direction = MathUtilities.createVector3d(transformedDirection);
	}

	@Override
	public String toString() {
		return "edu.cmu.cs.stage3.math.Ray[origin=" + m_origin + ",direction=" + m_direction + "]";
	}
	// public static Ray valueOf( String s ) {
	// }
}
